import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useSearchParams } from 'react-router-dom'
import { guestService, type ImportGuestsResult } from '@/services/guestService'
import { eventService } from '@/services/eventService'
import type { GuestStatus } from '@/types'

/**
 * Guest Management Page.
 * Search, filter, add guest, import CSV guest list, copy invitation links.
 *
 * See: project-index/08_UI_UX_Specification.md — Guest Management
 * See: project-index/03_Functional_Requirements.md — FR-04 Guest Management
 */
export function GuestManagementPage() {
  const [searchParams] = useSearchParams()
  const eventIdParam = searchParams.get('eventId')
  const queryClient = useQueryClient()

  const [selectedEventId, setSelectedEventId] = useState<string>(eventIdParam || '')
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState<GuestStatus | ''>('')
  const [page, setPage] = useState(0)

  // Add Guest Modal state
  const [isAddModalOpen, setIsAddModalOpen] = useState(false)
  const [fullName, setFullName] = useState('')
  const [mobile, setMobile] = useState('')
  const [email, setEmail] = useState('')
  const [modalError, setModalError] = useState<string | null>(null)

  // CSV Import state
  const [isImportModalOpen, setIsImportModalOpen] = useState(false)
  const [csvFile, setCsvFile] = useState<File | null>(null)
  const [importResult, setImportResult] = useState<ImportGuestsResult | null>(null)

  // Fetch events for selector
  const { data: events } = useQuery({
    queryKey: ['my-events'],
    queryFn: eventService.getMyEvents,
  })

  // Auto-select first event if none selected
  const activeEventId = selectedEventId || (events && events.length > 0 ? events[0].id : '')

  // Fetch guests query
  const { data: guestData, isLoading } = useQuery({
    queryKey: ['guests', activeEventId, search, statusFilter, page],
    queryFn: () => guestService.getGuests(activeEventId, search || undefined, (statusFilter as GuestStatus) || undefined, page),
    enabled: Boolean(activeEventId),
  })

  // Add guest mutation
  const addGuestMutation = useMutation({
    mutationFn: (data: { fullName: string; mobile?: string; email?: string }) =>
      guestService.addGuest(activeEventId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['guests', activeEventId] })
      setIsAddModalOpen(false)
      setFullName('')
      setMobile('')
      setEmail('')
    },
    onError: (err: Error) => setModalError(err.message || 'Failed to add guest.'),
  })

  // CSV import mutation
  const importMutation = useMutation({
    mutationFn: (file: File) => guestService.importGuestsCsv(activeEventId, file),
    onSuccess: (res) => {
      queryClient.invalidateQueries({ queryKey: ['guests', activeEventId] })
      setImportResult(res)
    },
  })

  // Remove guest mutation
  const removeMutation = useMutation({
    mutationFn: (guestId: string) => guestService.removeGuest(guestId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['guests', activeEventId] })
    },
  })

  const copyLink = (url?: string) => {
    if (url) {
      navigator.clipboard.writeText(url)
      alert('Invitation link copied to clipboard!')
    }
  }

  return (
    <div>
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
        <div>
          <h1 className="text-3xl font-bold text-white">Guest Management</h1>
          <p className="text-slate-400 text-sm mt-1">Manage guest list, track RSVPs, and issue invitation links.</p>
        </div>

        {/* Event selector & Action buttons */}
        <div className="flex flex-wrap items-center gap-3">
          {events && events.length > 0 && (
            <select
              className="input bg-surface text-white py-2"
              value={activeEventId}
              onChange={(e) => {
                setSelectedEventId(e.target.value)
                setPage(0)
              }}
            >
              {events.map((ev) => (
                <option key={ev.id} value={ev.id}>
                  {ev.title}
                </option>
              ))}
            </select>
          )}
          <button
            onClick={() => setIsImportModalOpen(true)}
            disabled={!activeEventId}
            className="btn-secondary py-2 text-sm"
          >
            Import CSV
          </button>
          <button
            onClick={() => setIsAddModalOpen(true)}
            disabled={!activeEventId}
            className="btn-primary py-2 text-sm"
          >
            + Add Guest
          </button>
        </div>
      </div>

      {/* Search & Filter bar */}
      <div className="card mb-6 flex flex-col sm:flex-row items-center gap-4">
        <div className="flex-1 w-full">
          <input
            type="text"
            className="input py-2 text-sm"
            placeholder="Search guests by name, mobile, or email…"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value)
              setPage(0)
            }}
          />
        </div>
        <select
          className="input bg-surface text-white py-2 text-sm w-full sm:w-48"
          value={statusFilter}
          onChange={(e) => {
            setStatusFilter(e.target.value as GuestStatus | '')
            setPage(0)
          }}
        >
          <option value="">All Statuses</option>
          <option value="INVITED">Invited</option>
          <option value="REGISTERED">Registered</option>
          <option value="ATTENDED">Attended</option>
          <option value="DECLINED">Declined</option>
        </select>
      </div>

      {/* Guest Table */}
      <div className="card overflow-x-auto">
        {isLoading && <div className="p-8 text-center text-slate-400">Loading guest list…</div>}

        {!isLoading && (!guestData || guestData.content.length === 0) && (
          <div className="text-center py-12 text-slate-400">
            <p className="text-lg font-semibold text-white mb-1">No guests found</p>
            <p className="text-sm">Click "+ Add Guest" or "Import CSV" to populate your guest list.</p>
          </div>
        )}

        {guestData && guestData.content.length > 0 && (
          <table className="w-full text-left text-sm text-slate-300">
            <thead className="bg-white/5 text-slate-400 uppercase text-xs">
              <tr>
                <th className="px-4 py-3">Guest Name</th>
                <th className="px-4 py-3">Mobile / Email</th>
                <th className="px-4 py-3">Invitation Code</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5">
              {guestData.content.map((guest) => (
                <tr key={guest.id} className="hover:bg-white/5">
                  <td className="px-4 py-3 font-medium text-white">{guest.fullName}</td>
                  <td className="px-4 py-3">{guest.mobile || guest.email || '—'}</td>
                  <td className="px-4 py-3 font-mono text-xs text-primary-400">{guest.invitationCode}</td>
                  <td className="px-4 py-3">
                    <span className="text-xs px-2 py-0.5 rounded bg-white/10 text-slate-200">
                      {guest.status}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-right space-x-2">
                    <button
                      onClick={() => copyLink(guest.invitationUrl)}
                      className="text-xs text-primary-400 hover:underline"
                    >
                      Copy Link
                    </button>
                    <button
                      onClick={() => removeMutation.mutate(guest.id)}
                      className="text-xs text-red-400 hover:underline"
                    >
                      Remove
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {/* Pagination controls */}
        {guestData && guestData.totalPages > 1 && (
          <div className="flex items-center justify-between pt-4 mt-4 border-t border-white/5 text-xs text-slate-400">
            <span>Page {page + 1} of {guestData.totalPages}</span>
            <div className="space-x-2">
              <button
                disabled={page === 0}
                onClick={() => setPage((p) => p - 1)}
                className="btn-secondary py-1 px-3"
              >
                Previous
              </button>
              <button
                disabled={page + 1 >= guestData.totalPages}
                onClick={() => setPage((p) => p + 1)}
                className="btn-secondary py-1 px-3"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Add Guest Modal */}
      {isAddModalOpen && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center p-4 z-50">
          <div className="card w-full max-w-md">
            <h2 className="text-xl font-bold text-white mb-4">Add Guest</h2>
            {modalError && <p className="error-text mb-4">{modalError}</p>}
            <form
              onSubmit={(e) => {
                e.preventDefault()
                addGuestMutation.mutate({ fullName, mobile: mobile || undefined, email: email || undefined })
              }}
              className="space-y-4"
            >
              <div>
                <label className="label">Full Name *</label>
                <input
                  type="text"
                  className="input"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  required
                />
              </div>
              <div>
                <label className="label">Mobile Number</label>
                <input
                  type="tel"
                  className="input"
                  placeholder="+201012345678"
                  value={mobile}
                  onChange={(e) => setMobile(e.target.value)}
                />
              </div>
              <div>
                <label className="label">Email Address</label>
                <input
                  type="email"
                  className="input"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
              <div className="flex justify-end gap-3 pt-4">
                <button type="button" onClick={() => setIsAddModalOpen(false)} className="btn-secondary">
                  Cancel
                </button>
                <button type="submit" disabled={addGuestMutation.isPending} className="btn-primary">
                  {addGuestMutation.isPending ? 'Saving…' : 'Add Guest'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* CSV Import Modal */}
      {isImportModalOpen && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center p-4 z-50">
          <div className="card w-full max-w-md">
            <h2 className="text-xl font-bold text-white mb-2">Import Guest List (CSV)</h2>
            <p className="text-slate-400 text-xs mb-4">
              Select a CSV file with columns: <code className="text-primary-400">Name, Mobile, Email</code>.
            </p>

            {importResult ? (
              <div className="space-y-3">
                <div className="p-3 rounded bg-green-500/10 text-green-400 text-sm">
                  Successfully imported {importResult.successfullyImported} guests!
                  {importResult.skippedDuplicates > 0 && ` (${importResult.skippedDuplicates} duplicates skipped)`}
                </div>
                <button
                  onClick={() => {
                    setIsImportModalOpen(false)
                    setImportResult(null)
                    setCsvFile(null)
                  }}
                  className="btn-primary w-full"
                >
                  Done
                </button>
              </div>
            ) : (
              <form
                onSubmit={(e) => {
                  e.preventDefault()
                  if (csvFile) importMutation.mutate(csvFile)
                }}
                className="space-y-4"
              >
                <input
                  type="file"
                  accept=".csv"
                  className="input py-2 text-sm"
                  onChange={(e) => setCsvFile(e.target.files?.[0] || null)}
                  required
                />
                <div className="flex justify-end gap-3 pt-2">
                  <button type="button" onClick={() => setIsImportModalOpen(false)} className="btn-secondary">
                    Cancel
                  </button>
                  <button type="submit" disabled={!csvFile || importMutation.isPending} className="btn-primary">
                    {importMutation.isPending ? 'Importing…' : 'Start Import'}
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  )
}

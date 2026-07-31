import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { rsvpService } from '@/services/rsvpService'
import type { AttendanceStatus } from '@/types'

interface RsvpFormModalProps {
  eventId: string
  invitationCode: string
  currentStatus?: string
  currentAttendeeCount?: number
  currentNotes?: string
  onClose: () => void
  onSuccess: () => void
}

/**
 * Interactive RSVP Form Modal component.
 * Allows guests to submit or update attendance response.
 *
 * See: Sprint 3 Objectives — RSVP System
 */
export function RsvpFormModal({
  eventId,
  invitationCode,
  currentStatus = 'ACCEPTED',
  currentAttendeeCount = 1,
  currentNotes = '',
  onClose,
  onSuccess,
}: RsvpFormModalProps) {
  const [status, setStatus] = useState<AttendanceStatus>(
    (currentStatus as AttendanceStatus) || 'ACCEPTED'
  )
  const [attendeeCount, setAttendeeCount] = useState(currentAttendeeCount)
  const [notes, setNotes] = useState(currentNotes)
  const [error, setError] = useState<string | null>(null)

  const submitMutation = useMutation({
    mutationFn: () =>
      rsvpService.submitRsvp(eventId, {
        invitationCode,
        attendanceStatus: status,
        attendeeCount: status === 'ACCEPTED' ? attendeeCount : 1,
        notes: notes.trim() || undefined,
      }),
    onSuccess: () => {
      onSuccess()
      onClose()
    },
    onError: (err: Error) => {
      setError(err.message || 'Failed to submit RSVP. Please try again.')
    },
  })

  return (
    <div className="fixed inset-0 bg-black/80 flex items-center justify-center p-4 z-50">
      <div className="card w-full max-w-lg bg-surface border border-white/10 p-6 shadow-2xl">
        <h2 className="text-2xl font-bold text-white mb-2 text-center">Confirm Your Attendance</h2>
        <p className="text-slate-400 text-sm mb-6 text-center">Please let us know if you will be attending.</p>

        {error && (
          <div className="mb-4 p-3 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-sm">
            {error}
          </div>
        )}

        <form
          onSubmit={(e) => {
            e.preventDefault()
            submitMutation.mutate()
          }}
          className="space-y-6"
        >
          {/* Status Selection Buttons */}
          <div>
            <label className="label mb-2">Will you attend? *</label>
            <div className="grid grid-cols-3 gap-3">
              <button
                type="button"
                onClick={() => setStatus('ACCEPTED')}
                className={`py-3 px-4 rounded-xl font-semibold text-sm transition-all duration-200 border ${
                  status === 'ACCEPTED'
                    ? 'bg-green-500 text-white border-green-400 shadow-lg'
                    : 'bg-white/5 border-white/10 text-slate-300 hover:bg-white/10'
                }`}
              >
                Yes, Attending
              </button>

              <button
                type="button"
                onClick={() => setStatus('MAYBE')}
                className={`py-3 px-4 rounded-xl font-semibold text-sm transition-all duration-200 border ${
                  status === 'MAYBE'
                    ? 'bg-amber-500 text-white border-amber-400 shadow-lg'
                    : 'bg-white/5 border-white/10 text-slate-300 hover:bg-white/10'
                }`}
              >
                Maybe
              </button>

              <button
                type="button"
                onClick={() => setStatus('DECLINED')}
                className={`py-3 px-4 rounded-xl font-semibold text-sm transition-all duration-200 border ${
                  status === 'DECLINED'
                    ? 'bg-red-500 text-white border-red-400 shadow-lg'
                    : 'bg-white/5 border-white/10 text-slate-300 hover:bg-white/10'
                }`}
              >
                Sorry, Can't
              </button>
            </div>
          </div>

          {/* Attendee Count (if attending) */}
          {status === 'ACCEPTED' && (
            <div>
              <label className="label" htmlFor="attendeeCount">
                Number of Guests Attending *
              </label>
              <input
                id="attendeeCount"
                type="number"
                min={1}
                max={10}
                className="input"
                value={attendeeCount}
                onChange={(e) => setAttendeeCount(parseInt(e.target.value) || 1)}
                required
              />
            </div>
          )}

          {/* Dietary / Special Notes */}
          <div>
            <label className="label" htmlFor="notes">
              Special Wishes or Dietary Requirements (Optional)
            </label>
            <textarea
              id="notes"
              rows={3}
              className="input"
              placeholder="e.g. Vegetarian meal, extra chair for kids…"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
            />
          </div>

          {/* Action buttons */}
          <div className="flex items-center justify-end gap-3 pt-4 border-t border-white/5">
            <button type="button" onClick={onClose} className="btn-secondary">
              Cancel
            </button>
            <button type="submit" disabled={submitMutation.isPending} className="btn-primary">
              {submitMutation.isPending ? 'Submitting…' : 'Submit RSVP'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

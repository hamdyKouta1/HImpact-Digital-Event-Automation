import { useParams, useNavigate, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { eventService } from '@/services/eventService'

/**
 * Single Event Details & Management Page.
 * Displays event status, venue information, share link, and publish CTA.
 *
 * See: project-index/08_UI_UX_Specification.md — Owner Portal
 */
export function EventDetailsPage() {
  const { eventId } = useParams<{ eventId: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const { data: event, isLoading, isError } = useQuery({
    queryKey: ['event', eventId],
    queryFn: () => eventService.getEvent(eventId!),
    enabled: Boolean(eventId),
  })

  const publishMutation = useMutation({
    mutationFn: () => eventService.publishEvent(eventId!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['event', eventId] })
      queryClient.invalidateQueries({ queryKey: ['my-events'] })
    },
  })

  const deleteMutation = useMutation({
    mutationFn: () => eventService.deleteEvent(eventId!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-events'] })
      navigate('/owner')
    },
  })

  if (isLoading) {
    return <div className="p-8 text-center text-slate-400">Loading event details…</div>
  }

  if (isError || !event) {
    return (
      <div className="card text-center py-12">
        <p className="text-red-400 font-semibold">Event not found.</p>
        <Link to="/owner" className="btn-secondary mt-4">Return to Dashboard</Link>
      </div>
    )
  }

  const isDraft = event.status === 'DRAFT'

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Top Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-3">
            <h1 className="text-3xl font-bold text-white">{event.title}</h1>
            <span className={`text-xs font-mono px-3 py-1 rounded-full font-semibold ${
              isDraft ? 'bg-amber-500/20 text-amber-400' : 'bg-green-500/20 text-green-400'
            }`}>
              {event.status}
            </span>
          </div>
          <p className="text-slate-400 text-sm mt-1">
            {event.eventType} • {event.eventDate}
          </p>
        </div>

        <div className="flex items-center gap-3">
          {isDraft && (
            <button
              onClick={() => publishMutation.mutate()}
              disabled={publishMutation.isPending}
              className="btn-primary"
            >
              {publishMutation.isPending ? 'Publishing…' : 'Publish Event'}
            </button>
          )}
          <Link to={`/owner/guests?eventId=${event.id}`} className="btn-secondary">
            Manage Guests
          </Link>
        </div>
      </div>

      {/* Main Details Card */}
      <div className="card space-y-6">
        <h2 className="text-xl font-bold text-white border-b border-white/5 pb-3">Event Overview</h2>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
          <div>
            <p className="text-xs text-slate-400 uppercase font-semibold">Venue</p>
            <p className="text-base text-white mt-1">{event.venueName || 'Not specified'}</p>
            {event.venueAddress && <p className="text-sm text-slate-400 mt-1">{event.venueAddress}</p>}
            {event.googleMapsUrl && (
              <a
                href={event.googleMapsUrl}
                target="_blank"
                rel="noreferrer"
                className="text-xs text-primary-400 hover:underline mt-2 inline-block"
              >
                View on Google Maps ↗
              </a>
            )}
          </div>

          <div>
            <p className="text-xs text-slate-400 uppercase font-semibold">Public Slug / URL</p>
            <p className="text-base font-mono text-primary-400 mt-1">https://himpact.app/invite/{event.slug}</p>
            <p className="text-xs text-slate-500 mt-1">Unique public link for your guests</p>
          </div>

          {event.brideName && event.groomName && (
            <div>
              <p className="text-xs text-slate-400 uppercase font-semibold">Couple</p>
              <p className="text-base text-white mt-1">{event.brideName} & {event.groomName}</p>
            </div>
          )}

          <div>
            <p className="text-xs text-slate-400 uppercase font-semibold">Package & Theme</p>
            <p className="text-base text-white mt-1">
              Package: <span className="text-primary-400 font-semibold">{event.packageName || 'Starter'}</span> • Theme: <span className="text-secondary-400 font-semibold">{event.themeName || 'Classic White'}</span>
            </p>
          </div>
        </div>

        {/* Danger Zone */}
        {isDraft && (
          <div className="pt-6 border-t border-white/5 flex items-center justify-between">
            <div>
              <p className="text-sm font-semibold text-red-400">Delete Draft Event</p>
              <p className="text-xs text-slate-500">This action soft-deletes this draft event.</p>
            </div>
            <button
              onClick={() => {
                if (confirm('Are you sure you want to delete this draft event?')) {
                  deleteMutation.mutate()
                }
              }}
              className="text-xs text-red-400 hover:underline font-semibold"
            >
              Delete Event
            </button>
          </div>
        )}
      </div>
    </div>
  )
}

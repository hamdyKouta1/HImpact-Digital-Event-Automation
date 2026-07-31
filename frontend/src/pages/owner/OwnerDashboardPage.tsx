import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { eventService } from '@/services/eventService'

/**
 * Event Owner Dashboard Overview Page.
 * Displays quick stats, active events, and event creation CTA.
 *
 * See: project-index/08_UI_UX_Specification.md — Owner Portal
 */
export function OwnerDashboardPage() {
  const { data: events, isLoading, isError } = useQuery({
    queryKey: ['my-events'],
    queryFn: eventService.getMyEvents,
  })

  return (
    <div>
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-bold text-white tracking-tight">Owner Dashboard</h1>
          <p className="text-slate-400 text-sm mt-1">Manage your events, guest invitations, and gallery uploads.</p>
        </div>
        <Link to="/owner/events/new" className="btn-primary">
          + Create New Event
        </Link>
      </div>

      {/* Overview Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="card">
          <p className="text-sm font-medium text-slate-400">Active Events</p>
          <p className="text-3xl font-bold text-white mt-2">{events?.length ?? 0}</p>
        </div>
        <div className="card">
          <p className="text-sm font-medium text-slate-400">Total Guests</p>
          <p className="text-3xl font-bold text-white mt-2">
            {events?.reduce((acc, ev) => acc + (ev.totalGuests || 0), 0) ?? 0}
          </p>
        </div>
        <div className="card">
          <p className="text-sm font-medium text-slate-400">Photos Collected</p>
          <p className="text-3xl font-bold text-white mt-2">
            {events?.reduce((acc, ev) => acc + (ev.totalUploads || 0), 0) ?? 0}
          </p>
        </div>
      </div>

      {/* Events List */}
      <div className="card">
        <h2 className="text-xl font-bold text-white mb-4">Your Events</h2>

        {isLoading && (
          <div className="space-y-4">
            <div className="skeleton h-16 w-full" />
            <div className="skeleton h-16 w-full" />
          </div>
        )}

        {isError && (
          <div className="p-4 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-sm">
            Failed to load events. Please refresh.
          </div>
        )}

        {!isLoading && events?.length === 0 && (
          <div className="text-center py-12">
            <div className="w-16 h-16 bg-white/5 rounded-full flex items-center justify-center mx-auto mb-4 text-2xl">
              🎉
            </div>
            <h3 className="text-lg font-semibold text-white mb-1">No events created yet</h3>
            <p className="text-slate-400 text-sm mb-6 max-w-sm mx-auto">
              Create your first digital event invitation in less than 5 minutes.
            </p>
            <Link to="/owner/events/new" className="btn-primary">
              Create Event Now
            </Link>
          </div>
        )}

        {events && events.length > 0 && (
          <div className="divide-y divide-white/5">
            {events.map((event) => (
              <div key={event.id} className="py-4 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div>
                  <div className="flex items-center gap-3">
                    <h3 className="text-lg font-semibold text-white">{event.title}</h3>
                    <span className="text-xs font-mono px-2 py-0.5 rounded bg-primary-500/20 text-primary-400">
                      {event.status}
                    </span>
                  </div>
                  <p className="text-sm text-slate-400 mt-1">
                    {event.eventType} • {event.eventDate} • {event.venueName || 'Venue TBD'}
                  </p>
                </div>
                <div className="flex items-center gap-3">
                  <Link to={`/owner/guests?eventId=${event.id}`} className="btn-secondary text-sm py-2 px-4">
                    Guests
                  </Link>
                  <Link to={`/owner/events/${event.id}`} className="btn-primary text-sm py-2 px-4">
                    Manage
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

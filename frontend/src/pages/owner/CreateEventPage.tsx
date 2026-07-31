import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { eventService } from '@/services/eventService'
import type { EventType, CreateEventRequest } from '@/types'

/**
 * Event Creation Wizard Page.
 * Enables event owners to create an event in less than 5 minutes.
 *
 * See: project-index/08_UI_UX_Specification.md — Event Owner Journey
 */
export function CreateEventPage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const [title, setTitle] = useState('')
  const [eventType, setEventType] = useState<EventType>('WEDDING')
  const [brideName, setBrideName] = useState('')
  const [groomName, setGroomName] = useState('')
  const [venueName, setVenueName] = useState('')
  const [venueAddress, setVenueAddress] = useState('')
  const [googleMapsUrl, setGoogleMapsUrl] = useState('')
  const [eventDate, setEventDate] = useState('')
  const [startTime, setStartTime] = useState('')
  const [error, setError] = useState<string | null>(null)

  const createMutation = useMutation({
    mutationFn: (data: CreateEventRequest) => eventService.createEvent(data),
    onSuccess: (newEvent) => {
      queryClient.invalidateQueries({ queryKey: ['my-events'] })
      navigate(`/owner/events/${newEvent.id}`)
    },
    onError: (err: Error) => {
      setError(err.message || 'Failed to create event. Please check inputs.')
    },
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    if (!title.trim() || !venueName.trim() || !eventDate) {
      setError('Title, Venue Name, and Event Date are required.')
      return
    }

    createMutation.mutate({
      title: title.trim(),
      eventType,
      brideName: brideName.trim() || undefined,
      groomName: groomName.trim() || undefined,
      venueName: venueName.trim(),
      venueAddress: venueAddress.trim() || undefined,
      googleMapsUrl: googleMapsUrl.trim() || undefined,
      eventDate,
      startTime: startTime ? `${startTime}:00` : undefined,
    })
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-white mb-2">Create New Event</h1>
      <p className="text-slate-400 text-sm mb-8">Fill in basic details to set up your digital event website and invitations.</p>

      {error && (
        <div className="mb-6 p-4 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-sm">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="card space-y-6">
        <div>
          <label className="label" htmlFor="title">Event Title *</label>
          <input
            id="title"
            type="text"
            className="input"
            placeholder="e.g. Ahmed & Sara's Wedding"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>

        <div>
          <label className="label" htmlFor="eventType">Event Type *</label>
          <select
            id="eventType"
            className="input bg-surface text-white"
            value={eventType}
            onChange={(e) => setEventType(e.target.value as EventType)}
          >
            <option value="WEDDING">Wedding</option>
            <option value="ENGAGEMENT">Engagement</option>
            <option value="BIRTHDAY">Birthday</option>
            <option value="GRADUATION">Graduation</option>
            <option value="CORPORATE">Corporate</option>
            <option value="OTHER">Other</option>
          </select>
        </div>

        {eventType === 'WEDDING' && (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="label" htmlFor="brideName">Bride Name</label>
              <input
                id="brideName"
                type="text"
                className="input"
                placeholder="Bride Full Name"
                value={brideName}
                onChange={(e) => setBrideName(e.target.value)}
              />
            </div>
            <div>
              <label className="label" htmlFor="groomName">Groom Name</label>
              <input
                id="groomName"
                type="text"
                className="input"
                placeholder="Groom Full Name"
                value={groomName}
                onChange={(e) => setGroomName(e.target.value)}
              />
            </div>
          </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="label" htmlFor="eventDate">Event Date *</label>
            <input
              id="eventDate"
              type="date"
              className="input"
              value={eventDate}
              onChange={(e) => setEventDate(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="label" htmlFor="startTime">Start Time</label>
            <input
              id="startTime"
              type="time"
              className="input"
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
            />
          </div>
        </div>

        <div>
          <label className="label" htmlFor="venueName">Venue Name *</label>
          <input
            id="venueName"
            type="text"
            className="input"
            placeholder="e.g. Four Seasons Nile Plaza Hall"
            value={venueName}
            onChange={(e) => setVenueName(e.target.value)}
            required
          />
        </div>

        <div>
          <label className="label" htmlFor="venueAddress">Venue Address</label>
          <input
            id="venueAddress"
            type="text"
            className="input"
            placeholder="Street Address, City, Country"
            value={venueAddress}
            onChange={(e) => setVenueAddress(e.target.value)}
          />
        </div>

        <div>
          <label className="label" htmlFor="googleMapsUrl">Google Maps Link</label>
          <input
            id="googleMapsUrl"
            type="url"
            className="input"
            placeholder="https://maps.google.com/..."
            value={googleMapsUrl}
            onChange={(e) => setGoogleMapsUrl(e.target.value)}
          />
        </div>

        <div className="flex items-center justify-end gap-4 pt-4 border-t border-white/5">
          <button type="button" onClick={() => navigate('/owner')} className="btn-secondary">
            Cancel
          </button>
          <button type="submit" disabled={createMutation.isPending} className="btn-primary">
            {createMutation.isPending ? 'Creating…' : 'Create Event'}
          </button>
        </div>
      </form>
    </div>
  )
}

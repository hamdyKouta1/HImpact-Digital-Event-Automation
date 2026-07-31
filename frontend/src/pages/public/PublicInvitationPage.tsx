import { useState, useCallback } from 'react'
import { useParams, useSearchParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { invitationService } from '@/services/invitationService'
import { CountdownTimer } from '@/components/CountdownTimer'
import { RsvpFormModal } from '@/components/RsvpFormModal'
import { QrCodeModal } from '@/components/QrCodeModal'
import { CongratulationsWall } from '@/components/CongratulationsWall'
import { MediaUploader } from '@/components/MediaUploader'
import { EventGallery } from '@/components/EventGallery'

/**
 * Public Guest Invitation Landing Page.
 * Multi-section responsive experience: Cover Hero, Countdown, Venue Info, RSVP CTA, QR Pass, Wishes Wall.
 *
 * See: project-index/08_UI_UX_Specification.md — Guest Journey
 * See: Sprint 3 Objectives — Public Invitation Page
 */
export function PublicInvitationPage() {
  const { slug } = useParams<{ slug: string }>()
  const [searchParams] = useSearchParams()
  const code = searchParams.get('code') || ''

  const [isRsvpOpen, setIsRsvpOpen] = useState(false)
  const [isQrOpen, setIsQrOpen] = useState(false)

  const { data: invitation, isLoading, isError, refetch } = useQuery({
    queryKey: ['public-invitation', slug, code],
    queryFn: () => invitationService.getPublicInvitation(slug!, code),
    enabled: Boolean(slug && code),
  })

  const handleUploadSuccess = useCallback(() => {
    refetch()
  }, [refetch])

  if (!code) {
    return (
      <div className="min-h-screen bg-surface-dark flex flex-col items-center justify-center p-4 text-center">
        <h1 className="text-2xl font-bold text-white mb-2">Invitation Link Incomplete</h1>
        <p className="text-slate-400 max-w-sm text-sm">
          Your invitation link appears to be missing a valid invitation code. Please check your link.
        </p>
      </div>
    )
  }

  if (isLoading) {
    return (
      <div className="min-h-screen bg-surface-dark flex flex-col items-center justify-center p-4">
        <div className="w-10 h-10 border-2 border-primary-500 border-t-transparent rounded-full animate-spin mb-4" />
        <p className="text-slate-400 text-sm">Loading your digital invitation…</p>
      </div>
    )
  }

  if (isError || !invitation) {
    return (
      <div className="min-h-screen bg-surface-dark flex flex-col items-center justify-center p-4 text-center">
        <p className="text-5xl font-bold text-red-400 mb-4">404</p>
        <h1 className="text-2xl font-bold text-white mb-2">Invitation Not Found</h1>
        <p className="text-slate-400 max-w-sm text-sm mb-6">
          This invitation may have been updated, removed, or is no longer active.
        </p>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-surface-dark text-white font-sans antialiased pb-16">
      {/* Dynamic Theme Color Accent Overlay */}
      <div
        className="absolute inset-0 pointer-events-none opacity-25"
        style={{
          background: `radial-gradient(ellipse 80% 50% at 50% 0%, ${
            invitation.primaryColor || '#3B82F6'
          }, transparent)`,
        }}
      />

      <div className="relative max-w-3xl mx-auto px-4 pt-12 space-y-10">
        {/* Header Badge */}
        <div className="text-center">
          <span className="inline-block px-4 py-1.5 rounded-full glass text-xs font-semibold tracking-wider text-slate-300 uppercase mb-4">
            You Are Cordially Invited
          </span>
          <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight text-white mb-3">
            {invitation.title}
          </h1>
          {invitation.brideName && invitation.groomName && (
            <p className="text-xl sm:text-2xl font-medium text-gradient">
              {invitation.brideName} & {invitation.groomName}
            </p>
          )}
        </div>

        {/* Personalized Guest Welcome Card */}
        <div className="glass rounded-2xl p-6 text-center shadow-lg border border-white/10">
          <p className="text-sm text-slate-400 mb-1">Welcome, Dear Guest</p>
          <h2 className="text-2xl font-bold text-white mb-3">{invitation.guestName}</h2>
          <div className="flex flex-wrap items-center justify-center gap-3">
            <button onClick={() => setIsRsvpOpen(true)} className="btn-primary py-2.5 px-6 text-sm">
              {invitation.attendanceStatus && invitation.attendanceStatus !== 'PENDING'
                ? `RSVP: ${invitation.attendanceStatus} (Update)`
                : 'RSVP Now ✨'}
            </button>
            <button onClick={() => setIsQrOpen(true)} className="btn-secondary py-2.5 px-6 text-sm">
              View Digital Pass 📱
            </button>
          </div>
        </div>

        {/* Countdown Section */}
        <div className="card text-center">
          <h3 className="text-xs font-semibold text-slate-400 uppercase tracking-widest mb-2">
            Counting Down To The Big Day
          </h3>
          <p className="text-lg font-bold text-white mb-4">
            {invitation.eventDate} {invitation.startTime ? `at ${invitation.startTime}` : ''}
          </p>
          <CountdownTimer eventDate={invitation.eventDate} startTime={invitation.startTime} />
        </div>

        {/* Venue Information */}
        <div className="card space-y-4">
          <h3 className="text-xl font-bold text-white border-b border-white/5 pb-2">Venue & Location</h3>
          <div>
            <p className="text-lg font-semibold text-white">{invitation.venueName || 'Venue TBD'}</p>
            {invitation.venueAddress && (
              <p className="text-slate-400 text-sm mt-1">{invitation.venueAddress}</p>
            )}
          </div>
          {invitation.googleMapsUrl && (
            <a
              href={invitation.googleMapsUrl}
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-2 text-sm text-primary-400 hover:underline font-semibold"
            >
              Open Location in Google Maps ↗
            </a>
          )}
        </div>

        {/* Description / Schedule */}
        {invitation.description && (
          <div className="card space-y-2">
            <h3 className="text-xl font-bold text-white">About the Event</h3>
            <p className="text-slate-300 text-sm leading-relaxed whitespace-pre-line">
              {invitation.description}
            </p>
          </div>
        )}

        {/* Media Upload & Gallery Platform (Sprint 4) */}
        <MediaUploader
          eventId={invitation.eventId}
          invitationCode={invitation.invitationCode}
          uploadLimit={30}
          uploadedCount={0}
          onUploadSuccess={handleUploadSuccess}
        />

        <EventGallery eventId={invitation.eventId} />

        {/* Digital Congratulations Wall */}
        <CongratulationsWall
          eventId={invitation.eventId}
          invitationCode={invitation.invitationCode}
          guestName={invitation.guestName}
        />
      </div>

      {/* RSVP Modal */}
      {isRsvpOpen && (
        <RsvpFormModal
          eventId={invitation.eventId}
          invitationCode={invitation.invitationCode}
          currentStatus={invitation.attendanceStatus}
          currentAttendeeCount={invitation.attendeeCount}
          currentNotes={invitation.rsvpNotes}
          onClose={() => setIsRsvpOpen(false)}
          onSuccess={() => refetch()}
        />
      )}

      {/* QR Pass Modal */}
      {isQrOpen && (
        <QrCodeModal
          guestName={invitation.guestName}
          invitationCode={invitation.invitationCode}
          qrCodeDataUrl={invitation.qrCodeDataUrl}
          onClose={() => setIsQrOpen(false)}
        />
      )}
    </div>
  )
}

import apiClient from './apiClient'
import type { ApiSuccess } from '@/types'

export interface PublicInvitation {
  eventId: string
  title: string
  eventType: string
  brideName?: string
  groomName?: string
  description?: string
  venueName?: string
  venueAddress?: string
  googleMapsUrl?: string
  eventDate: string
  startTime?: string
  endTime?: string
  coverImage?: string
  primaryColor?: string
  secondaryColor?: string
  // Guest info
  guestId: string
  guestName: string
  invitationCode: string
  guestStatus: string
  qrCodeDataUrl?: string
  // Current RSVP
  attendanceStatus?: string
  attendeeCount?: number
  rsvpNotes?: string
}

export const invitationService = {
  /**
   * Fetch public invitation details by slug and guest invitation code.
   */
  async getPublicInvitation(slug: string, code: string): Promise<PublicInvitation> {
    const response = await apiClient.get<ApiSuccess<PublicInvitation>>(
      `/invite/${slug}?code=${encodeURIComponent(code)}`
    )
    return response.data.data
  },
}

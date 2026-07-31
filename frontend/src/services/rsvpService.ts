import apiClient from './apiClient'
import type { ApiSuccess, AttendanceStatus } from '@/types'

export interface SubmitRsvpPayload {
  invitationCode: string
  attendanceStatus: AttendanceStatus
  attendeeCount?: number
  notes?: string
}

export interface RsvpStats {
  eventId: string
  totalInvited: number
  totalAccepted: number
  totalDeclined: number
  totalMaybe: number
  totalPending: number
  expectedTotalAttendees: number
}

export const rsvpService = {
  /**
   * Submit or update RSVP response.
   */
  async submitRsvp(eventId: string, data: SubmitRsvpPayload): Promise<void> {
    await apiClient.post(`/events/${eventId}/rsvp`, data)
  },

  /**
   * Fetch aggregate RSVP statistics for event owner dashboard.
   */
  async getRsvpStats(eventId: string): Promise<RsvpStats> {
    const response = await apiClient.get<ApiSuccess<RsvpStats>>(`/events/${eventId}/rsvp/stats`)
    return response.data.data
  },
}

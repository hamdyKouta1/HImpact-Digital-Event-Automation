import apiClient from './apiClient'
import type { ApiSuccess, Guest, GuestStatus } from '@/types'

export interface AddGuestPayload {
  fullName: string
  mobile?: string
  email?: string
  uploadLimit?: number
}

export interface UpdateGuestPayload {
  fullName?: string
  mobile?: string
  email?: string
  uploadLimit?: number
  status?: GuestStatus
}

export interface PaginatedGuests {
  content: Guest[]
  totalElements: number
  totalPages: number
  number: number
}

export interface ImportGuestsResult {
  totalProcessed: number
  successfullyImported: number
  skippedDuplicates: number
  errors: string[]
}

/**
 * Guest service layer — all guest API calls.
 * See: project-index/07_API_Specification.md — Guest APIs
 */
export const guestService = {
  /**
   * Add a single guest.
   */
  async addGuest(eventId: string, data: AddGuestPayload): Promise<Guest> {
    const response = await apiClient.post<ApiSuccess<Guest>>(`/events/${eventId}/guests`, data)
    return response.data.data
  },

  /**
   * Batch import guests via CSV file.
   */
  async importGuestsCsv(eventId: string, file: File): Promise<ImportGuestsResult> {
    const formData = new FormData()
    formData.append('file', file)

    const response = await apiClient.post<ApiSuccess<ImportGuestsResult>>(
      `/events/${eventId}/guests/import`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    )
    return response.data.data
  },

  /**
   * Get paginated & searchable guest list for an event.
   */
  async getGuests(
    eventId: string,
    search?: string,
    status?: GuestStatus,
    page = 0,
    size = 20
  ): Promise<PaginatedGuests> {
    const params = new URLSearchParams()
    if (search) params.append('search', search)
    if (status) params.append('status', status)
    params.append('page', page.toString())
    params.append('size', size.toString())

    const response = await apiClient.get<ApiSuccess<PaginatedGuests>>(
      `/events/${eventId}/guests?${params.toString()}`
    )
    return response.data.data
  },

  /**
   * Update guest details.
   */
  async updateGuest(guestId: string, data: UpdateGuestPayload): Promise<Guest> {
    const response = await apiClient.put<ApiSuccess<Guest>>(`/guests/${guestId}`, data)
    return response.data.data
  },

  /**
   * Remove a guest.
   */
  async removeGuest(guestId: string): Promise<void> {
    await apiClient.delete(`/guests/${guestId}`)
  },
}

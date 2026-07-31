import apiClient from './apiClient'
import type { ApiSuccess } from '@/types'

export interface MediaFileItem {
  id: string
  eventId: string
  guestId: string
  guestName: string
  originalFilename: string
  mimeType: string
  fileSize: number
  storageProvider: string
  storagePath: string
  uploadStatus: string
  uploadedAt: string
}

export interface PaginatedMedia {
  content: MediaFileItem[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export const mediaService = {
  /**
   * Upload a media file (photo/video).
   */
  async uploadMedia(
    eventId: string,
    invitationCode: string,
    file: File,
    localIdentifier?: string,
    onProgress?: (percent: number) => void
  ): Promise<MediaFileItem> {
    const formData = new FormData()
    formData.append('invitationCode', invitationCode)
    formData.append('file', file)
    if (localIdentifier) {
      formData.append('localIdentifier', localIdentifier)
    }

    const response = await apiClient.post<ApiSuccess<MediaFileItem>>(
      `/events/${eventId}/media`,
      formData,
      {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: (progressEvent) => {
          if (progressEvent.total && onProgress) {
            const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
            onProgress(percent)
          }
        },
      }
    )
    return response.data.data
  },

  /**
   * Fetch paginated event gallery.
   */
  async getEventGallery(eventId: string, page = 0, size = 20): Promise<PaginatedMedia> {
    const response = await apiClient.get<ApiSuccess<PaginatedMedia>>(
      `/events/${eventId}/media?page=${page}&size=${size}`
    )
    return response.data.data
  },

  /**
   * Delete a media file.
   */
  async deleteMedia(mediaId: string): Promise<void> {
    await apiClient.delete(`/media/${mediaId}`)
  },
}

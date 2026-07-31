import apiClient from './apiClient'
import type { ApiSuccess } from '@/types'

export interface CommentItem {
  id: string
  eventId: string
  guestId: string
  guestName: string
  message: string
  createdAt: string
}

export interface PaginatedComments {
  content: CommentItem[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export const commentService = {
  /**
   * Post a congratulatory wish on the digital wall.
   */
  async postWish(eventId: string, invitationCode: string, message: string): Promise<CommentItem> {
    const response = await apiClient.post<ApiSuccess<CommentItem>>(`/events/${eventId}/comments`, {
      invitationCode,
      message,
    })
    return response.data.data
  },

  /**
   * Get paginated wishes for an event wall.
   */
  async getWishes(eventId: string, page = 0, size = 20): Promise<PaginatedComments> {
    const response = await apiClient.get<ApiSuccess<PaginatedComments>>(
      `/events/${eventId}/comments?page=${page}&size=${size}`
    )
    return response.data.data
  },
}

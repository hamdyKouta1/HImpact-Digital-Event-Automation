import apiClient from './apiClient'
import type { ApiSuccess, Event, EventSummaryResponse, CreateEventRequest, UpdateEventRequest } from '@/types'

/**
 * Event service layer — all event API calls.
 * See: project-index/07_API_Specification.md — Event APIs
 */
export const eventService = {
  /**
   * Create a new event.
   */
  async createEvent(data: CreateEventRequest): Promise<Event> {
    const response = await apiClient.post<ApiSuccess<Event>>('/events', data)
    return response.data.data
  },

  /**
   * Get all events owned by the current user.
   */
  async getMyEvents(): Promise<EventSummaryResponse[]> {
    const response = await apiClient.get<ApiSuccess<EventSummaryResponse[]>>('/events')
    return response.data.data
  },

  /**
   * Get details for a specific event.
   */
  async getEvent(eventId: string): Promise<Event> {
    const response = await apiClient.get<ApiSuccess<Event>>(`/events/${eventId}`)
    return response.data.data
  },

  /**
   * Update event details.
   */
  async updateEvent(eventId: string, data: UpdateEventRequest): Promise<Event> {
    const response = await apiClient.put<ApiSuccess<Event>>(`/events/${eventId}`, data)
    return response.data.data
  },

  /**
   * Publish an event.
   */
  async publishEvent(eventId: string): Promise<Event> {
    const response = await apiClient.post<ApiSuccess<Event>>(`/events/${eventId}/publish`)
    return response.data.data
  },

  /**
   * Soft-delete an event.
   */
  async deleteEvent(eventId: string): Promise<void> {
    await apiClient.delete(`/events/${eventId}`)
  },
}

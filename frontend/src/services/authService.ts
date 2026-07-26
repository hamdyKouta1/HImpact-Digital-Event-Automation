import apiClient from './apiClient'
import type { ApiSuccess, AuthTokenResponse, GoogleLoginRequest } from '@/types'

/**
 * Auth service — all authentication API calls.
 * No API calls should exist in React components — use this service.
 *
 * See: project-index/07_API_Specification.md — Authentication APIs
 */
export const authService = {
  /**
   * Exchange a Google ID token for HImpact JWT tokens.
   */
  async googleLogin(idToken: string): Promise<AuthTokenResponse> {
    const request: GoogleLoginRequest = { idToken }
    const response = await apiClient.post<ApiSuccess<AuthTokenResponse>>(
      '/auth/google',
      request,
    )
    return response.data.data
  },

  /**
   * Logout — client-side token cleanup.
   * Notifies the server for future refresh token revocation.
   */
  async logout(): Promise<void> {
    try {
      await apiClient.post('/auth/logout')
    } finally {
      // Always clear tokens regardless of server response
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
    }
  },
}

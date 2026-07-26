import axios, { AxiosError } from 'axios'
import type { ApiError } from '@/types'

/**
 * Configured Axios instance for all API calls.
 *
 * Features:
 * - Base URL from environment variable
 * - JWT Bearer token injected automatically via request interceptor
 * - Standardised error handling
 * - 401 handling — clears tokens and redirects to login
 *
 * No API calls should be made directly — always use service layer files.
 * See: project-index/15_AI_Development_Guide.md — Frontend Standards
 */
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15000,
})

// ── Request interceptor — inject JWT token ────────────────────────────────────
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// ── Response interceptor — handle auth errors globally ───────────────────────
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    if (error.response?.status === 401) {
      // Clear tokens and redirect to sign-in
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      // Use replace so back button doesn't re-trigger the 401
      window.location.replace('/sign-in')
    }
    return Promise.reject(error)
  },
)

export default apiClient

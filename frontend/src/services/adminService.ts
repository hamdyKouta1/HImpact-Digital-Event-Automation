import apiClient from './apiClient'
import type { ApiSuccess } from '@/types'

export interface AdminOverviewStats {
  id: string
  totalUsers: number
  totalEvents: number
  publishedEvents: number
  totalGuests: number
  totalInvitationViews: number
  totalRsvps: number
  totalUploads: number
  totalStorageBytes: number
  totalRevenue: number
}

export interface FeatureFlagItem {
  id: string
  flagName: string
  enabled: boolean
  description?: string
}

export interface AuditLogItem {
  id: string
  userId?: string
  ipAddress?: string
  action: string
  entityName: string
  entityId?: string
  oldValue?: string
  newValue?: string
  createdAt: string
}

export const adminService = {
  /**
   * Fetch admin overview statistics.
   */
  async getOverview(): Promise<AdminOverviewStats> {
    const response = await apiClient.get<ApiSuccess<AdminOverviewStats>>('/admin/overview')
    return response.data.data
  },

  /**
   * Fetch feature flags list.
   */
  async getFeatureFlags(): Promise<FeatureFlagItem[]> {
    const response = await apiClient.get<ApiSuccess<FeatureFlagItem[]>>('/admin/flags')
    return response.data.data
  },

  /**
   * Toggle feature flag state.
   */
  async toggleFeatureFlag(flagName: string, enabled: boolean): Promise<FeatureFlagItem> {
    const response = await apiClient.post<ApiSuccess<FeatureFlagItem>>(
      `/admin/flags/${flagName}/toggle?enabled=${enabled}`
    )
    return response.data.data
  },

  /**
   * Fetch administrative audit logs.
   */
  async getAuditLogs(page = 0, size = 20): Promise<{ content: AuditLogItem[]; totalPages: number }> {
    const response = await apiClient.get<ApiSuccess<{ content: AuditLogItem[]; totalPages: number }>>(
      `/admin/audit-logs?page=${page}&size=${size}`
    )
    return response.data.data
  },
}

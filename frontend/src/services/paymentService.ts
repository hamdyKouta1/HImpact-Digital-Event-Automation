import apiClient from './apiClient'
import type { ApiSuccess } from '@/types'

export interface PaymentItem {
  id: string
  eventId: string
  eventTitle: string
  packageId?: string
  packageName?: string
  paymentMethod: string
  amount: number
  currency: string
  paymentReference: string
  receiptImageUrl?: string
  paymentState: 'SUBMITTED' | 'UNDER_REVIEW' | 'APPROVED' | 'ACTIVATED' | 'REJECTED'
  rejectionReason?: string
  createdAt: string
}

export interface SubmitPaymentPayload {
  eventId: string
  packageId?: string
  paymentMethod: string
  amount: number
  paymentReference: string
  receiptImageUrl?: string
}

export const paymentService = {
  /**
   * Submit manual payment proof.
   */
  async submitPayment(data: SubmitPaymentPayload): Promise<PaymentItem> {
    const response = await apiClient.post<ApiSuccess<PaymentItem>>('/payments', data)
    return response.data.data
  },

  /**
   * Fetch payment history for authenticated owner.
   */
  async getMyPayments(): Promise<PaymentItem[]> {
    const response = await apiClient.get<ApiSuccess<PaymentItem[]>>('/payments/my')
    return response.data.data
  },

  /**
   * Fetch pending payments queue (Admin).
   */
  async getPendingPayments(page = 0, size = 20): Promise<{ content: PaymentItem[]; totalPages: number }> {
    const response = await apiClient.get<ApiSuccess<{ content: PaymentItem[]; totalPages: number }>>(
      `/payments/pending?page=${page}&size=${size}`
    )
    return response.data.data
  },

  /**
   * Admin approve or reject payment.
   */
  async approveOrRejectPayment(paymentId: string, approved: boolean, rejectionReason?: string): Promise<PaymentItem> {
    const response = await apiClient.post<ApiSuccess<PaymentItem>>(`/payments/${paymentId}/approve`, {
      approved,
      rejectionReason,
    })
    return response.data.data
  },
}

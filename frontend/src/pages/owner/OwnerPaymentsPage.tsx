import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { paymentService } from '@/services/paymentService'
import { eventService } from '@/services/eventService'

/**
 * Event Owner Payments & Package Selection Page.
 * Allows submitting InstaPay / Vodafone Cash payment reference for admin review.
 *
 * See: PO Sprint 5 Workstream B Payment Platform
 */
export function OwnerPaymentsPage() {
  const queryClient = useQueryClient()
  const [selectedEventId, setSelectedEventId] = useState('')
  const [paymentMethod, setPaymentMethod] = useState('INSTAPAY')
  const [amount, setAmount] = useState('499')
  const [paymentReference, setPaymentReference] = useState('')
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  const { data: events } = useQuery({
    queryKey: ['my-events'],
    queryFn: eventService.getMyEvents,
  })

  const { data: payments } = useQuery({
    queryKey: ['my-payments'],
    queryFn: paymentService.getMyPayments,
  })

  const submitMutation = useMutation({
    mutationFn: paymentService.submitPayment,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-payments'] })
      setMessage('Payment proof submitted successfully! Under review by Admin. ✨')
      setPaymentReference('')
      setError(null)
    },
    onError: (err: Error) => {
      setError(err.message || 'Failed to submit payment proof.')
      setMessage(null)
    },
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!selectedEventId || !paymentReference) {
      setError('Please select an event and enter payment transaction reference.')
      return
    }

    submitMutation.mutate({
      eventId: selectedEventId,
      paymentMethod,
      amount: parseFloat(amount),
      paymentReference,
    })
  }

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-white tracking-tight">Packages & Payments</h1>
        <p className="text-slate-400 text-sm mt-1">
          Activate digital wedding packages via manual InstaPay or Vodafone Cash transfer.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Payment Submission Form */}
        <div className="card space-y-6">
          <h2 className="text-xl font-bold text-white">Submit Payment Proof</h2>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Select Event</label>
              <select
                className="input-field"
                value={selectedEventId}
                onChange={(e) => setSelectedEventId(e.target.value)}
                required
              >
                <option value="">-- Select an Event --</option>
                {events?.map((ev) => (
                  <option key={ev.id} value={ev.id}>
                    {ev.title} ({ev.status})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Payment Method</label>
              <select
                className="input-field"
                value={paymentMethod}
                onChange={(e) => setPaymentMethod(e.target.value)}
              >
                <option value="INSTAPAY">InstaPay Egypt</option>
                <option value="VODAFONE_CASH">Vodafone Cash</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">Amount (EGP)</label>
              <input
                type="number"
                className="input-field"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                required
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 uppercase mb-1">
                Transaction Reference / Receipt Number
              </label>
              <input
                type="text"
                className="input-field"
                placeholder="e.g. TXN987654321"
                value={paymentReference}
                onChange={(e) => setPaymentReference(e.target.value)}
                required
              />
            </div>

            {message && <div className="p-3 rounded-lg bg-green-500/10 text-green-400 text-xs">{message}</div>}
            {error && <div className="p-3 rounded-lg bg-red-500/10 text-red-400 text-xs">{error}</div>}

            <button type="submit" disabled={submitMutation.isPending} className="btn-primary w-full">
              {submitMutation.isPending ? 'Submitting…' : 'Submit Payment Proof ✨'}
            </button>
          </form>
        </div>

        {/* Payment History List */}
        <div className="card space-y-4">
          <h2 className="text-xl font-bold text-white">Payment History</h2>
          {payments && payments.length === 0 && (
            <p className="text-slate-400 text-sm">No payment submissions found.</p>
          )}

          <div className="space-y-3">
            {payments?.map((item) => (
              <div key={item.id} className="p-4 rounded-xl bg-white/5 border border-white/5 space-y-2">
                <div className="flex items-center justify-between">
                  <span className="font-semibold text-white text-sm">{item.eventTitle}</span>
                  <span
                    className={`text-xs px-2.5 py-1 rounded font-bold ${
                      item.paymentState === 'ACTIVATED' || item.paymentState === 'APPROVED'
                        ? 'bg-green-500/20 text-green-400'
                        : item.paymentState === 'REJECTED'
                        ? 'bg-red-500/20 text-red-400'
                        : 'bg-amber-500/20 text-amber-400'
                    }`}
                  >
                    {item.paymentState}
                  </span>
                </div>
                <div className="flex items-center justify-between text-xs text-slate-400">
                  <span>Ref: {item.paymentReference}</span>
                  <span className="font-semibold text-white">
                    {item.amount} {item.currency}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

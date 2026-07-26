import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '@/contexts/AuthContext'

/**
 * Phone verification page.
 * Guests must verify their mobile number after Google Sign-In.
 *
 * Two-step flow:
 *  1. Guest enters phone number → OTP is sent via SMS.
 *  2. Guest enters OTP → verification confirmed.
 *
 * The SMS/OTP provider will be integrated in Sprint 2 once the provider is approved.
 *
 * See: project-index/07_API_Specification.md — POST /auth/verify-phone
 * See: project-index/02_Decision_Log.md — DEC-013 Authentication
 */
export function PhoneVerificationPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [step, setStep] = useState<'phone' | 'otp'>('phone')
  const [phone, setPhone] = useState('+20')
  const [otp, setOtp] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSendOtp = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)
    try {
      // TODO Sprint 2: call authService.sendOtp(phone)
      setStep('otp')
    } catch {
      setError('Failed to send verification code. Please try again.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleVerifyOtp = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)
    try {
      // TODO Sprint 2: call authService.verifyOtp(phone, otp)
      navigate('/guest', { replace: true })
    } catch {
      setError('Invalid verification code. Please try again.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-surface-dark flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-primary-500/10 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg className="w-8 h-8 text-primary-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
            </svg>
          </div>
          <h1 className="text-2xl font-bold text-white mb-1">
            {step === 'phone' ? 'Verify your number' : 'Enter verification code'}
          </h1>
          <p className="text-slate-400 text-sm">
            {step === 'phone'
              ? 'We will send a verification code to your mobile.'
              : `We sent a code to ${phone}`}
          </p>
        </div>

        <div className="glass rounded-2xl p-8 shadow-lg">
          {error && (
            <div className="mb-6 p-3 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-sm" role="alert">
              {error}
            </div>
          )}

          {step === 'phone' ? (
            <form onSubmit={handleSendOtp} noValidate>
              <div className="mb-6">
                <label className="label" htmlFor="phone">Mobile number</label>
                <input
                  id="phone"
                  type="tel"
                  inputMode="tel"
                  className="input"
                  placeholder="+201012345678"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  required
                  autoComplete="tel"
                />
                <p className="text-slate-500 text-xs mt-1">Include country code, e.g. +20 for Egypt</p>
              </div>
              <button type="submit" disabled={isLoading} className="btn-primary w-full">
                {isLoading ? 'Sending…' : 'Send verification code'}
              </button>
            </form>
          ) : (
            <form onSubmit={handleVerifyOtp} noValidate>
              <div className="mb-6">
                <label className="label" htmlFor="otp">Verification code</label>
                <input
                  id="otp"
                  type="text"
                  inputMode="numeric"
                  maxLength={6}
                  className="input text-center text-2xl tracking-widest font-mono"
                  placeholder="000000"
                  value={otp}
                  onChange={(e) => setOtp(e.target.value.replace(/\D/g, ''))}
                  required
                  autoComplete="one-time-code"
                  autoFocus
                />
              </div>
              <button type="submit" disabled={isLoading || otp.length < 6} className="btn-primary w-full mb-3">
                {isLoading ? 'Verifying…' : 'Verify'}
              </button>
              <button
                type="button"
                onClick={() => { setStep('phone'); setOtp('') }}
                className="btn-ghost w-full text-sm"
              >
                Use a different number
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  )
}

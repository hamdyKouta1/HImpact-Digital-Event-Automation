import { useCallback, useEffect, useRef, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '@/contexts/AuthContext'

// Augment the Window interface to include the GSI global
declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (config: object) => void
          prompt: (notification?: (n: { isNotDisplayed: () => boolean; isSkippedMoment: () => boolean }) => void) => void
          renderButton: (parent: HTMLElement, options: object) => void
          disableAutoSelect: () => void
        }
      }
    }
  }
}

/**
 * Google Sign-In page.
 *
 * This is the entry point for guest authentication (DEC-013).
 * The page uses the Google Identity Services JavaScript SDK (GSI).
 * After Google returns an ID token, it is sent to the backend for verification.
 *
 * See: project-index/08_UI_UX_Specification.md — Guest Journey (Sign In)
 * See: project-index/07_API_Specification.md — POST /auth/google
 */
export function SignInPage() {
  const { isAuthenticated, login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const googleBtnRef = useRef<HTMLDivElement>(null)

  // Redirect if already authenticated
  const from = (location.state as { from?: Location })?.from?.pathname || '/guest'

  useEffect(() => {
    if (isAuthenticated) {
      navigate(from, { replace: true })
    }
  }, [isAuthenticated, navigate, from])

  // Initialise Google Identity Services once the SDK loads
  useEffect(() => {
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID
    if (!clientId) {
      console.warn('VITE_GOOGLE_CLIENT_ID is not set — Google Sign-In will not work.')
      return
    }

    const tryInit = () => {
      if (!window.google?.accounts?.id) return

      window.google.accounts.id.initialize({
        client_id: clientId,
        callback: async (response: { credential: string }) => {
          setIsLoading(true)
          setError(null)
          try {
            await login(response.credential)
            // Navigation handled by the isAuthenticated effect above
          } catch (_err) {
            setError('Sign-in failed. Please try again.')
            setIsLoading(false)
          }
        },
        auto_select: false,
        cancel_on_tap_outside: true,
      })

      // Render the branded Google button inside our custom button container
      if (googleBtnRef.current) {
        window.google.accounts.id.renderButton(googleBtnRef.current, {
          type: 'standard',
          theme: 'outline',
          size: 'large',
          text: 'continue_with',
          shape: 'rectangular',
          width: 360,
        })
      }
    }

    // GSI script may already be loaded or still loading
    if (window.google?.accounts?.id) {
      tryInit()
    } else {
      // Poll briefly until the script tag loaded by index.html is ready
      const interval = setInterval(() => {
        if (window.google?.accounts?.id) {
          clearInterval(interval)
          tryInit()
        }
      }, 100)
      return () => clearInterval(interval)
    }
  }, [login])

  const handleGoogleLogin = useCallback(() => {
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID
    if (!clientId) {
      setError('Google Sign-In is not configured. Set VITE_GOOGLE_CLIENT_ID in your .env file.')
      return
    }
    if (!window.google?.accounts?.id) {
      setError('Google Sign-In SDK failed to load. Check your internet connection.')
      return
    }
    // Trigger the One Tap / popup prompt
    window.google.accounts.id.prompt((notification) => {
      if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
        // One Tap was suppressed — the rendered button below still works
        setError(null)
      }
    })
  }, [])

  if (isAuthenticated) {
    return null
  }

  return (
    <div className="min-h-screen bg-surface-dark flex flex-col items-center justify-center p-4">
      {/* Background gradient */}
      <div
        className="absolute inset-0 pointer-events-none"
        aria-hidden="true"
        style={{
          background:
            'radial-gradient(ellipse 80% 60% at 50% -20%, rgba(59,130,246,0.15), transparent)',
        }}
      />

      <div className="relative w-full max-w-md">
        {/* Logo / Brand */}
        <div className="text-center mb-10">
          <h1 className="text-4xl font-bold tracking-tight text-gradient mb-2">
            HImpact
          </h1>
          <p className="text-slate-400 text-sm">
            Automate the Event. Preserve the Memories.
          </p>
        </div>

        {/* Sign-in card */}
        <div className="glass rounded-2xl p-8 shadow-lg">
          <h2 className="text-xl font-semibold text-white text-center mb-2">
            Welcome
          </h2>
          <p className="text-slate-400 text-center text-sm mb-8">
            Sign in with your Google account to access your invitation.
          </p>

          {error && (
            <div
              className="mb-6 p-4 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-sm text-center"
              role="alert"
            >
              {error}
            </div>
          )}

          {/* Google-rendered branded button (authoritative) */}
          <div className="flex justify-center mb-4">
            <div ref={googleBtnRef} id="google-gsi-btn" />
          </div>

          {/* Fallback custom button — triggers One Tap prompt */}
          <button
            id="google-sign-in-btn"
            type="button"
            onClick={handleGoogleLogin}
            disabled={isLoading}
            className="w-full flex items-center justify-center gap-3 bg-white hover:bg-slate-100 active:bg-slate-200 text-slate-800 font-semibold py-3 px-6 rounded-xl transition-all duration-200 disabled:opacity-60 disabled:cursor-not-allowed"
          >
            {isLoading ? (
              <div className="w-5 h-5 border-2 border-slate-400 border-t-transparent rounded-full animate-spin" />
            ) : (
              <svg className="w-5 h-5" viewBox="0 0 24 24" aria-hidden="true">
                <path
                  fill="#4285F4"
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                />
                <path
                  fill="#34A853"
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                />
                <path
                  fill="#FBBC05"
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                />
                <path
                  fill="#EA4335"
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                />
              </svg>
            )}
            {isLoading ? 'Signing in…' : 'Continue with Google'}
          </button>

          <p className="text-slate-500 text-xs text-center mt-6">
            By signing in you agree to our{' '}
            <a href="#" className="text-primary-400 hover:underline">
              Terms of Service
            </a>{' '}
            and{' '}
            <a href="#" className="text-primary-400 hover:underline">
              Privacy Policy
            </a>
            .
          </p>
        </div>

        {/* Footer */}
        <p className="text-center text-slate-600 text-xs mt-8">
          © {new Date().getFullYear()} HImpact. All rights reserved.
        </p>
      </div>
    </div>
  )
}


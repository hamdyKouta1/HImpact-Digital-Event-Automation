import React, { createContext, useContext, useState, useCallback, useEffect } from 'react'
import type { UserRole } from '@/types'
import { authService } from '@/services/authService'

// ── Auth Context Types ────────────────────────────────────────────────────────

interface AuthUser {
  id: string
  email: string
  role: UserRole
  mobileVerified: boolean
}

interface AuthContextValue {
  user: AuthUser | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (idToken: string) => Promise<void>
  logout: () => Promise<void>
}

// ── Context ───────────────────────────────────────────────────────────────────

const AuthContext = createContext<AuthContextValue | null>(null)

// ── JWT Parsing Utility ───────────────────────────────────────────────────────

function parseJwtPayload(token: string): Record<string, unknown> | null {
  try {
    const base64 = token.split('.')[1]
    const decoded = atob(base64.replace(/-/g, '+').replace(/_/g, '/'))
    return JSON.parse(decoded)
  } catch {
    return null
  }
}

function isTokenExpired(token: string): boolean {
  const payload = parseJwtPayload(token)
  if (!payload || typeof payload.exp !== 'number') return true
  return Date.now() >= payload.exp * 1000
}

// ── Provider ──────────────────────────────────────────────────────────────────

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  // Restore user from stored token on mount
  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    if (token && !isTokenExpired(token)) {
      const payload = parseJwtPayload(token)
      if (payload) {
        setUser({
          id: payload.sub as string,
          email: payload.email as string,
          role: payload.role as UserRole,
          mobileVerified: (payload.mobileVerified as boolean) ?? false,
        })
      }
    } else if (token) {
      // Expired token — clean up
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
    }
    setIsLoading(false)
  }, [])

  const login = useCallback(async (idToken: string) => {
    const tokens = await authService.googleLogin(idToken)

    localStorage.setItem('accessToken', tokens.accessToken)
    localStorage.setItem('refreshToken', tokens.refreshToken)

    const payload = parseJwtPayload(tokens.accessToken)
    if (payload) {
      setUser({
        id: payload.sub as string,
        email: payload.email as string,
        role: tokens.role,
        mobileVerified: tokens.mobileVerified,
      })
    }
  }, [])

  const logout = useCallback(async () => {
    await authService.logout()
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: user !== null,
        isLoading,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

// ── Hook ──────────────────────────────────────────────────────────────────────

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

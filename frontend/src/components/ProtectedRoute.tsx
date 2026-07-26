import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '@/contexts/AuthContext'
import type { UserRole } from '@/types'

interface ProtectedRouteProps {
  children: React.ReactNode
  /** Required role to access this route. If omitted, any authenticated user is allowed. */
  requiredRole?: UserRole
}

/**
 * Route guard — redirects unauthenticated users to /sign-in.
 * Preserves the intended destination for redirect after login.
 *
 * See: project-index/03_Functional_Requirements.md — FR-01 Authentication
 * See: project-index/04_Non_Functional_Requirements.md — NFR-05 Security
 */
export function ProtectedRoute({ children, requiredRole }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading, user } = useAuth()
  const location = useLocation()

  // Show nothing while checking stored token (prevents flicker)
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-surface-dark">
        <div className="w-8 h-8 border-2 border-primary-500 border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  if (!isAuthenticated) {
    // Redirect to sign-in, preserving the intended URL
    return <Navigate to="/sign-in" state={{ from: location }} replace />
  }

  if (requiredRole && user?.role !== requiredRole) {
    // Authenticated but insufficient role
    return <Navigate to="/unauthorized" replace />
  }

  return <>{children}</>
}

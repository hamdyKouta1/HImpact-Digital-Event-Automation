import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from '@/contexts/AuthContext'
import { ProtectedRoute } from '@/components/ProtectedRoute'
import { SignInPage } from '@/pages/auth/SignInPage'
import { PhoneVerificationPage } from '@/pages/auth/PhoneVerificationPage'
import { UnauthorizedPage } from '@/pages/error/UnauthorizedPage'
import { NotFoundPage } from '@/pages/error/NotFoundPage'

/**
 * Application routing — follows the navigation structure from PI-08 UI/UX Specification.
 *
 * Route structure:
 *  /                    → redirect to /sign-in
 *  /sign-in             → Public — Google Sign-In
 *  /verify-phone        → Protected — Phone verification step
 *  /invite/:code        → Public — Guest invitation page
 *  /guest/*             → Protected (GUEST) — Guest portal
 *  /owner/*             → Protected (OWNER) — Event owner dashboard
 *  /admin/*             → Protected (ADMIN) — Admin panel
 *
 * See: project-index/08_UI_UX_Specification.md — Navigation Structure
 */
function App() {
  return (
    <AuthProvider>
      <Routes>
        {/* ── Public Routes ────────────────────────────────────────────── */}
        <Route path="/" element={<Navigate to="/sign-in" replace />} />
        <Route path="/sign-in" element={<SignInPage />} />
        <Route path="/unauthorized" element={<UnauthorizedPage />} />
        <Route path="*" element={<NotFoundPage />} />

        {/* ── Auth Flow ────────────────────────────────────────────────── */}
        <Route
          path="/verify-phone"
          element={
            <ProtectedRoute>
              <PhoneVerificationPage />
            </ProtectedRoute>
          }
        />

        {/* ── Guest Portal (Sprint 3) ───────────────────────────────────
            Routes will be expanded in Sprint 3 — Invitation & RSVP.
            Placeholder structure is in place. */}
        <Route
          path="/guest/*"
          element={
            <ProtectedRoute requiredRole="GUEST">
              <div className="min-h-screen flex items-center justify-center text-white">
                <p>Guest Portal — Coming in Sprint 3</p>
              </div>
            </ProtectedRoute>
          }
        />

        {/* ── Owner Portal (Sprint 2) ─────────────────────────────────── */}
        <Route
          path="/owner/*"
          element={
            <ProtectedRoute requiredRole="OWNER">
              <div className="min-h-screen flex items-center justify-center text-white">
                <p>Owner Dashboard — Coming in Sprint 2</p>
              </div>
            </ProtectedRoute>
          }
        />

        {/* ── Admin Panel (Sprint 5) ──────────────────────────────────── */}
        <Route
          path="/admin/*"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <div className="min-h-screen flex items-center justify-center text-white">
                <p>Admin Panel — Coming in Sprint 5</p>
              </div>
            </ProtectedRoute>
          }
        />
      </Routes>
    </AuthProvider>
  )
}

export default App

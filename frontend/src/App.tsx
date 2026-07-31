import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from '@/contexts/AuthContext'
import { ProtectedRoute } from '@/components/ProtectedRoute'
import { SignInPage } from '@/pages/auth/SignInPage'
import { PhoneVerificationPage } from '@/pages/auth/PhoneVerificationPage'
import { OwnerLayout } from '@/layouts/OwnerLayout'
import { AdminLayout } from '@/layouts/AdminLayout'
import { PublicInvitationPage } from '@/pages/public/PublicInvitationPage'
import { OwnerDashboardPage } from '@/pages/owner/OwnerDashboardPage'
import { CreateEventPage } from '@/pages/owner/CreateEventPage'
import { EventDetailsPage } from '@/pages/owner/EventDetailsPage'
import { GuestManagementPage } from '@/pages/owner/GuestManagementPage'
import { OwnerPaymentsPage } from '@/pages/owner/OwnerPaymentsPage'
import { AdminDashboardPage } from '@/pages/admin/AdminDashboardPage'
import { UnauthorizedPage } from '@/pages/error/UnauthorizedPage'
import { NotFoundPage } from '@/pages/error/NotFoundPage'

/**
 * Application routing — follows the navigation structure from PI-08 UI/UX Specification.
 *
 * Route structure:
 *  /                    → redirect to /sign-in
 *  /sign-in             → Public — Google Sign-In
 *  /verify-phone        → Protected — Phone verification step
 *  /invite/:slug        → Public — Guest invitation page
 *  /owner               → Protected (OWNER / ADMIN) — Owner Portal Layout
 *    /owner             → Owner Dashboard
 *    /owner/events/new  → Create Event Wizard
 *    /owner/events/:id  → Event Details
 *    /owner/guests      → Guest Management
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
        <Route path="/invite/:slug" element={<PublicInvitationPage />} />
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

        {/* ── Owner Portal (Sprint 2 Core Platform) ───────────────────── */}
        <Route path="/owner" element={<ProtectedRoute allowedRoles={['OWNER', 'ADMIN', 'SUPER_ADMIN']}><OwnerLayout /></ProtectedRoute>}>
          <Route index element={<OwnerDashboardPage />} />
          <Route path="events/new" element={<CreateEventPage />} />
          <Route path="events/:id" element={<EventDetailsPage />} />
          <Route path="events/:id/guests" element={<GuestManagementPage />} />
          <Route path="payments" element={<OwnerPaymentsPage />} />
        </Route>

        {/* Admin Portal Routes (Sprint 5) */}
        <Route path="/admin" element={<ProtectedRoute allowedRoles={['ADMIN', 'SUPER_ADMIN', 'FINANCE', 'SUPPORT']}><AdminLayout /></ProtectedRoute>}>
          <Route index element={<AdminDashboardPage />} />
        </Route>

        {/* ── Guest Portal (Sprint 3) ─────────────────────────────────── */}
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
      </Routes>
    </AuthProvider>
  )
}

export default App

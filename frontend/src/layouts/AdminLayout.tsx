import { Link, useLocation, Outlet } from 'react-router-dom'
import { useAuth } from '@/contexts/AuthContext'

/**
 * Admin Platform Portal Layout.
 * Sidebar navigation per PI-08 UI/UX Specification (Admin Portal).
 */
export function AdminLayout() {
  const { user, logout } = useAuth()
  const location = useLocation()

  const navItems = [
    { label: 'Overview', path: '/admin' },
    { label: 'Users', path: '/admin/users' },
    { label: 'Payments Queue', path: '/admin/payments' },
    { label: 'Feature Flags', path: '/admin/flags' },
    { label: 'Audit Logs', path: '/admin/audit-logs' },
  ]

  return (
    <div className="min-h-screen bg-surface-dark flex">
      {/* Sidebar */}
      <aside className="w-64 bg-surface border-r border-white/5 flex flex-col justify-between hidden md:flex">
        <div>
          {/* Logo */}
          <div className="p-6 border-b border-white/5">
            <Link to="/admin" className="text-2xl font-bold text-gradient">
              HImpact
            </Link>
            <p className="text-xs text-amber-400 font-mono mt-1">Super Admin Console</p>
          </div>

          {/* Navigation links */}
          <nav className="p-4 space-y-1">
            {navItems.map((item) => {
              const isActive = location.pathname === item.path
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`block px-4 py-3 rounded-lg text-sm font-medium transition-all duration-200 ${
                    isActive
                      ? 'bg-amber-500 text-slate-900 font-bold shadow-md'
                      : 'text-slate-400 hover:text-white hover:bg-white/5'
                  }`}
                >
                  {item.label}
                </Link>
              )
            })}
          </nav>
        </div>

        {/* User profile / Logout */}
        <div className="p-4 border-t border-white/5">
          <div className="flex items-center justify-between">
            <div className="truncate pr-2">
              <p className="text-sm font-medium text-white truncate">{user?.email}</p>
              <span className="inline-block text-xs bg-amber-500/20 text-amber-400 px-2 py-0.5 rounded font-mono">
                {user?.role}
              </span>
            </div>
            <button
              onClick={logout}
              className="text-slate-400 hover:text-red-400 p-2 text-xs font-semibold"
              title="Sign Out"
            >
              Exit
            </button>
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 overflow-y-auto">
        <header className="md:hidden bg-surface border-b border-white/5 p-4 flex items-center justify-between">
          <Link to="/admin" className="text-xl font-bold text-gradient">
            HImpact Admin
          </Link>
          <button onClick={logout} className="text-xs text-slate-400">
            Sign Out
          </button>
        </header>

        <div className="p-6 max-w-7xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  )
}

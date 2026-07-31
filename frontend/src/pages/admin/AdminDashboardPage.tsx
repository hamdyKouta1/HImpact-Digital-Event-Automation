import { useQuery } from '@tanstack/react-query'
import { adminService } from '@/services/adminService'

/**
 * Admin Overview Dashboard Page.
 * Displays pre-aggregated platform metrics per PO Workstream D.
 */
export function AdminDashboardPage() {
  const { data: stats, isLoading, isError } = useQuery({
    queryKey: ['admin-overview'],
    queryFn: adminService.getOverview,
  })

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white tracking-tight">Admin Console Overview</h1>
        <p className="text-slate-400 text-sm mt-1">
          Platform-wide health, user growth, revenue, and storage monitoring.
        </p>
      </div>

      {isLoading && <div className="p-8 text-center text-slate-400">Loading platform metrics…</div>}

      {isError && (
        <div className="p-4 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-sm">
          Failed to load admin metrics.
        </div>
      )}

      {stats && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="card border-l-4 border-amber-500">
            <p className="text-xs text-slate-400 font-semibold uppercase">Total Users</p>
            <p className="text-3xl font-bold text-white mt-2">{stats.totalUsers}</p>
          </div>
          <div className="card border-l-4 border-primary-500">
            <p className="text-xs text-slate-400 font-semibold uppercase">Total Events</p>
            <p className="text-3xl font-bold text-white mt-2">{stats.totalEvents}</p>
          </div>
          <div className="card border-l-4 border-green-500">
            <p className="text-xs text-slate-400 font-semibold uppercase">Total Revenue</p>
            <p className="text-3xl font-bold text-green-400 mt-2">
              {stats.totalRevenue} <span className="text-xs font-normal">EGP</span>
            </p>
          </div>
          <div className="card border-l-4 border-secondary-500">
            <p className="text-xs text-slate-400 font-semibold uppercase">Media Uploads</p>
            <p className="text-3xl font-bold text-white mt-2">{stats.totalUploads}</p>
          </div>
        </div>
      )}
    </div>
  )
}

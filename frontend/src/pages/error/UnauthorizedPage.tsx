import { Link } from 'react-router-dom'

export function UnauthorizedPage() {
  return (
    <div className="min-h-screen bg-surface-dark flex flex-col items-center justify-center p-4 text-center">
      <p className="text-6xl font-bold text-primary-400 mb-4">403</p>
      <h1 className="text-2xl font-bold text-white mb-2">Access Denied</h1>
      <p className="text-slate-400 mb-8 max-w-sm">
        You do not have permission to access this page. Please contact support if you believe this is an error.
      </p>
      <Link to="/sign-in" className="btn-primary">
        Return to Sign In
      </Link>
    </div>
  )
}

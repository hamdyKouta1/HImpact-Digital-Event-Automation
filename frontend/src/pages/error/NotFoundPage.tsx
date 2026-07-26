import { Link } from 'react-router-dom'

export function NotFoundPage() {
  return (
    <div className="min-h-screen bg-surface-dark flex flex-col items-center justify-center p-4 text-center">
      <p className="text-6xl font-bold text-primary-400 mb-4">404</p>
      <h1 className="text-2xl font-bold text-white mb-2">Page Not Found</h1>
      <p className="text-slate-400 mb-8 max-w-sm">
        The page you are looking for does not exist or may have been moved.
      </p>
      <Link to="/" className="btn-primary">
        Go Home
      </Link>
    </div>
  )
}

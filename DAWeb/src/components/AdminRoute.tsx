import { type ReactElement } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

function esAdmin(roles: string[] | undefined): boolean {
  return (roles ?? []).some((r) => {
    const rol = r.toUpperCase()
    return rol === 'ADMINISTRADOR' || rol === 'ADMIN'
  })
}

export function AdminRoute({ children }: { children: ReactElement }) {
  const { isAuthenticated, user } = useAuth()
  if (!isAuthenticated) return <Navigate to="/login" replace />
  if (!esAdmin(user?.roles)) return <Navigate to="/" replace />
  return children
}
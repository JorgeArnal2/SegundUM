import { NavLink, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

export function SiteHeader() {
  const { isAuthenticated, user, logout } = useAuth()
  const esAdmin = (user?.roles ?? []).some((r) => {
    const rol = r.toUpperCase()
    return rol === 'ADMINISTRADOR' || rol === 'ADMIN'
  })

  return (
    <header className="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
      <div className="container">
        <Link to="/" className="navbar-brand fw-semibold">
          DAWeb Market
        </Link>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#mainNav"
          aria-controls="mainNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon" />
        </button>

        <div className="collapse navbar-collapse" id="mainNav">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <NavLink to="/" className="nav-link">
                Inicio
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink to="/products" className="nav-link">
                Productos
              </NavLink>
            </li>
            {esAdmin && (
              <li className="nav-item">
                <NavLink to="/admin" className="nav-link">
                  Admin
                </NavLink>
              </li>
            )}
          </ul>

          <div className="d-flex align-items-center">
            {isAuthenticated ? (
              <>
                <span className="text-white-50 me-3">{user?.name}</span>
                <button className="btn btn-outline-light btn-sm" onClick={logout}>
                  Cerrar sesión
                </button>
              </>
            ) : (
              <Link to="/login" className="btn btn-light btn-sm">
                Entrar
              </Link>
            )}
          </div>
        </div>
      </div>
    </header>
  )
}

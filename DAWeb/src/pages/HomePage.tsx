import { Link } from 'react-router-dom'
import { FeatureCard } from '../components/FeatureCard'
import { useAuth } from '../contexts/AuthContext'

const features = [
  {
    title: 'Login y roles',
    description: 'Base preparada para separar vistas de administrador y usuario.',
  },
  {
    title: 'Productos',
    description: 'Zona inicial para listar, crear y revisar productos publicados.',
  },
  {
    title: 'Búsqueda y filtros',
    description: 'Espacio preparado para filtros por categoría, estado y precio.',
  },
]

export function HomePage() {
  const { isAuthenticated, user, logout } = useAuth()

  return (
    <>
      <section className="hero-section py-5">
        <div className="container">
          <div className="row align-items-center g-4">
            <div className="col-12 col-lg-7">
              <span className="badge text-bg-light text-primary mb-3">
                {isAuthenticated ? `Bienvenido, ${user?.name}` : 'Proyecto final DAWeb'}
              </span>
              <h1 className="display-5 fw-bold mb-3">
                {isAuthenticated
                  ? 'Tu acceso simulado ha funcionado correctamente.'
                  : 'Base inicial del frontend para la web de compraventa'}
              </h1>
              <p className="lead text-secondary mb-4">
                {isAuthenticated
                  ? 'Ya puedes acceder a la experiencia de bienvenida y cerrar sesión en cualquier momento.'
                  : 'Estructura preparada para construir las vistas, el diseño responsive y la integración con el backend más adelante.'}
              </p>
              <div className="d-flex flex-wrap gap-2">
                {isAuthenticated ? (
                  <>
                    <button className="btn btn-outline-danger" onClick={logout}>
                      Cerrar sesión
                    </button>
                    <Link className="btn btn-primary" to="/products">
                      Ir a productos
                    </Link>
                  </>
                ) : (
                  <>
                    <Link className="btn btn-primary" to="/login">
                      Iniciar sesión
                    </Link>
                    <Link className="btn btn-outline-primary" to="/register">
                      Crear cuenta
                    </Link>
                  </>
                )}
              </div>
            </div>
            <div className="col-12 col-lg-5">
              <div className="hero-panel p-4 p-md-5 rounded-4">
                <h2 className="h4 mb-3">
                  {isAuthenticated ? 'Estado de la sesión' : 'Qué queda listo desde hoy'}
                </h2>
                <ul className="list-unstyled mb-0 d-grid gap-2">
                  {isAuthenticated ? (
                    <>
                      <li>Sesión simulada con login correcto.</li>
                      <li>Botón de cierre de sesión disponible.</li>
                      <li>Ruta de bienvenida activa para continuar el proyecto.</li>
                    </>
                  ) : (
                    <>
                      <li>React + Vite configurado</li>
                      <li>Bootstrap integrado</li>
                      <li>Estructura básica de carpetas</li>
                      <li>Estilo global inicial</li>
                    </>
                  )}
                </ul>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section id="features" className="py-5 bg-body-tertiary">
        <div className="container">
          <div className="row g-4">
            {features.map((feature) => (
              <div className="col-12 col-md-4" key={feature.title}>
                <FeatureCard
                  title={feature.title}
                  description={feature.description}
                />
              </div>
            ))}
          </div>
        </div>
      </section>

      <section id="next-steps" className="py-5">
        <div className="container">
          <div className="row justify-content-center">
            <div className="col-12 col-lg-8">
              <div className="card border-0 shadow-sm">
                <div className="card-body p-4 p-md-5">
                  <h2 className="h3 mb-3">Siguiente fase</h2>
                  <p className="text-secondary mb-0">
                    Sobre esta base se podrán añadir rutas, autenticación,
                    listados, formularios y el resto de pantallas del proyecto.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  )
}

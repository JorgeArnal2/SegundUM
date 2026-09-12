import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { ProductCard } from '../components/ProductCard'
import { useAuth } from '../contexts/AuthContext'
import { getProductos, type Producto } from '../services/products'

const PAGE_SIZE = 8

export function ProductsPage() {
  const { isAuthenticated, user } = useAuth()
  const esAdmin = (user?.roles ?? []).some((r) => {
    const rol = r.toUpperCase()
    return rol === 'ADMINISTRADOR' || rol === 'ADMIN'
  })
  const [productos, setProductos] = useState<Producto[]>([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const cargar = useCallback(async (pagina: number) => {
    setLoading(true)
    setError('')
    try {
      const resultado = await getProductos(pagina, PAGE_SIZE)
      setProductos(resultado.items)
      setTotalPages(resultado.totalPages)
      setTotalElements(resultado.totalElements)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudieron cargar los productos')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    cargar(page)
  }, [page, cargar])

  return (
    <div className="container py-5">
      <div className="d-flex justify-content-between align-items-start gap-2 mb-4">
        <div>
          <h2 className="h4 mb-1">Productos</h2>
          <p className="text-secondary mb-0">
            {totalElements} resultado{totalElements === 1 ? '' : 's'}
          </p>
        </div>
        {isAuthenticated && !esAdmin && (
          <Link to="/products/new" className="btn btn-primary btn-sm">
            Publicar producto
          </Link>
        )}
      </div>

      {error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Cargando…</span>
          </div>
        </div>
      ) : productos.length === 0 && !error ? (
        <div className="alert alert-info" role="status">
          No hay productos publicados todavía.
        </div>
      ) : (
        <div className="row g-4">
          {productos.map((producto) => (
            <div className="col-12 col-sm-6 col-lg-3" key={producto.id}>
              <ProductCard producto={producto} />
            </div>
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <nav className="d-flex justify-content-center mt-4" aria-label="Paginación de productos">
          <ul className="pagination">
            <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
              <button
                className="page-link"
                onClick={() => setPage((prev) => Math.max(0, prev - 1))}
                disabled={page === 0}
              >
                Anterior
              </button>
            </li>
            <li className="page-item disabled">
              <span className="page-link">
                Página {page + 1} de {totalPages}
              </span>
            </li>
            <li className={`page-item ${page >= totalPages - 1 ? 'disabled' : ''}`}>
              <button
                className="page-link"
                onClick={() => setPage((prev) => Math.min(totalPages - 1, prev + 1))}
                disabled={page >= totalPages - 1}
              >
                Siguiente
              </button>
            </li>
          </ul>
        </nav>
      )}
    </div>
  )
}

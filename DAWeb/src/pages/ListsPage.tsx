import { useEffect, useState } from 'react'
import { useAuth } from '../contexts/AuthContext'
import { ProductCard } from '../components/ProductCard'
import { CompraventaCard } from '../components/CompraventaCard'
import { getProductosPorVendedor, type Producto } from '../services/products'
import { getComprasPorComprador, getVentasPorVendedor } from '../services/compraventas'
import type { Compraventa } from '../services/admin'

const PAGE_SIZE = 12

type Tab = 'venta' | 'vendidos' | 'comprados'

const TABS: { id: Tab; etiqueta: string }[] = [
  { id: 'venta', etiqueta: 'Mis productos' },
  { id: 'vendidos', etiqueta: 'Vendidos' },
  { id: 'comprados', etiqueta: 'Comprados' },
]

const MENSAJES_VACIOS: Record<Tab, string> = {
  venta: 'No has publicado ningún producto todavía.',
  vendidos: 'Todavía no has vendido ningún producto.',
  comprados: 'Todavía no has comprado ningún producto.',
}

export function ListsPage() {
  const { user, token } = useAuth()
  const [tabActivo, setTabActivo] = useState<Tab>('venta')
  const [productos, setProductos] = useState<Producto[]>([])
  const [compraventas, setCompraventas] = useState<Compraventa[]>([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const cambiarTab = (tab: Tab) => {
    setTabActivo(tab)
    setPage(0)
  }

  useEffect(() => {
    if (!user) return
    let cancelled = false

    const cargar = async () => {
      setLoading(true)
      setError('')
      try {
        if (tabActivo === 'venta') {
          const resultado = await getProductosPorVendedor(user.id, page, PAGE_SIZE)
          if (cancelled) return
          setProductos(resultado.items)
          setTotalPages(resultado.totalPages)
          setTotalElements(resultado.totalElements)
        } else {
          const resultado =
            tabActivo === 'vendidos'
              ? await getVentasPorVendedor(token ?? '', user.id, page, PAGE_SIZE)
              : await getComprasPorComprador(token ?? '', user.id, page, PAGE_SIZE)
          if (cancelled) return
          setCompraventas(resultado.items)
          setTotalPages(resultado.totalPages)
          setTotalElements(resultado.totalElements)
        }
      } catch (err) {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : 'No se pudieron cargar los datos')
        }
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    cargar()
    return () => {
      cancelled = true
    }
  }, [tabActivo, page, user, token])

  const mostrarProductos = tabActivo === 'venta'
  const items = mostrarProductos ? productos : compraventas

  return (
    <div className="container py-5">
      <div className="mb-4">
        <h2 className="h4 mb-1">Listas</h2>
        <p className="text-secondary mb-0">
          {totalElements} resultado{totalElements === 1 ? '' : 's'}
        </p>
      </div>

      <ul className="nav nav-pills mb-4" role="tablist" aria-label="Listas de productos">
        {TABS.map((tab) => (
          <li className="nav-item" key={tab.id}>
            <button
              type="button"
              role="tab"
              aria-selected={tabActivo === tab.id}
              className={`nav-link ${tabActivo === tab.id ? 'active' : ''}`}
              onClick={() => cambiarTab(tab.id)}
            >
              {tab.etiqueta}
            </button>
          </li>
        ))}
      </ul>

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
      ) : items.length === 0 && !error ? (
        <div className="alert alert-info" role="status">
          {MENSAJES_VACIOS[tabActivo]}
        </div>
      ) : mostrarProductos ? (
        <div className="row g-4">
          {productos.map((producto) => (
            <div className="col-12 col-sm-6 col-lg-3" key={producto.id}>
              <ProductCard producto={producto} />
            </div>
          ))}
        </div>
      ) : (
        <div className="row g-4">
          {compraventas.map((compraventa) => (
            <div className="col-12 col-sm-6 col-lg-3" key={compraventa.id}>
              <CompraventaCard compraventa={compraventa} />
            </div>
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <nav className="d-flex justify-content-center mt-4" aria-label="Paginación de listas">
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
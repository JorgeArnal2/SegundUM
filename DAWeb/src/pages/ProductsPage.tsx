import { useCallback, useEffect, useState, type FormEvent } from 'react'
import { ProductCard } from '../components/ProductCard'
import {
  getCategorias,
  getProductos,
  type Categoria,
  type FiltrosProducto,
  type Producto,
} from '../services/products'

const PAGE_SIZE = 8

const ESTADOS: { value: string; label: string }[] = [
  { value: 'NUEVO', label: 'Nuevo' },
  { value: 'COMO_NUEVO', label: 'Como nuevo' },
  { value: 'BUEN_ESTADO', label: 'Buen estado' },
  { value: 'ACEPTABLE', label: 'Aceptable' },
  { value: 'PARA_PIEZAS_O_REPARAR', label: 'Para piezas o reparar' },
]

export function ProductsPage() {
  const [productos, setProductos] = useState<Producto[]>([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [categoriasError, setCategoriasError] = useState('')

  const [termino, setTermino] = useState('')
  const [categoriaSel, setCategoriaSel] = useState('')
  const [estadoSel, setEstadoSel] = useState('')
  const [precioMaximo, setPrecioMaximo] = useState('')
  const [filtros, setFiltros] = useState<FiltrosProducto>({})

  useEffect(() => {
    let cancelled = false
    getCategorias()
      .then((items) => {
        if (!cancelled) setCategorias(items)
      })
      .catch((err) => {
        if (!cancelled) {
          setCategoriasError(
            err instanceof Error ? err.message : 'No se pudieron cargar las categorías',
          )
        }
      })
    return () => {
      cancelled = true
    }
  }, [])

  const cargar = useCallback(async (pagina: number, filtrosActivos: FiltrosProducto) => {
    setLoading(true)
    setError('')
    try {
      const resultado = await getProductos(pagina, PAGE_SIZE, filtrosActivos)
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
    cargar(page, filtros)
  }, [page, filtros, cargar])

  const buscar = (evento: FormEvent) => {
    evento.preventDefault()
    setFiltros({
      texto: termino,
      categoria: categoriaSel,
      estado: estadoSel,
      precioMaximo: precioMaximo ? parseFloat(precioMaximo) : undefined,
    })
  }

  const limpiarFiltros = () => {
    setTermino('')
    setCategoriaSel('')
    setEstadoSel('')
    setPrecioMaximo('')
    setPage(0)
    setFiltros({})
  }

  const hayFiltros = Object.values(filtros).some((valor) => valor !== undefined && valor !== '')

  return (
    <div className="container py-5">
      <div className="mb-4">
        <h2 className="h4 mb-1">Productos</h2>
        <p className="text-secondary mb-0">
          {totalElements} resultado{totalElements === 1 ? '' : 's'}
        </p>
      </div>

      <form className="mb-4" role="search" onSubmit={buscar}>
        <div className="input-group mb-3">
          <input
            type="search"
            className="form-control"
            placeholder="Buscar productos…"
            aria-label="Buscar productos"
            value={termino}
            onChange={(evento) => setTermino(evento.target.value)}
          />
          <button type="submit" className="btn btn-primary" aria-label="Buscar">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="currentColor"
              viewBox="0 0 16 16"
              aria-hidden="true"
            >
              <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0" />
            </svg>
          </button>
        </div>

        <div className="row g-2 align-items-end">
          <div className="col-12 col-sm-6 col-lg-3">
            <label className="form-label" htmlFor="filtro-categoria">
              Categoría
            </label>
            <select
              id="filtro-categoria"
              className="form-select"
              value={categoriaSel}
              onChange={(evento) => setCategoriaSel(evento.target.value)}
              disabled={categorias.length === 0}
            >
              <option value="">{categoriasError || 'Todas las categorías'}</option>
              {categorias.map((categoria) => (
                <option key={categoria.id} value={categoria.id}>
                  {categoria.nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="col-12 col-sm-6 col-lg-3">
            <label className="form-label" htmlFor="filtro-estado">
              Estado
            </label>
            <select
              id="filtro-estado"
              className="form-select"
              value={estadoSel}
              onChange={(evento) => setEstadoSel(evento.target.value)}
            >
              <option value="">Cualquier estado</option>
              {ESTADOS.map((estado) => (
                <option key={estado.value} value={estado.value}>
                  {estado.label}
                </option>
              ))}
            </select>
          </div>
          <div className="col-12 col-sm-6 col-lg-3">
            <label className="form-label" htmlFor="filtro-precio">
              Precio máximo (€)
            </label>
            <input
              id="filtro-precio"
              className="form-control"
              type="number"
              value={precioMaximo}
              onChange={(evento) => setPrecioMaximo(evento.target.value)}
              placeholder="Sin límite"
              min="0"
              step="0.01"
            />
          </div>
          <div className="col-12 col-sm-6 col-lg-3">
            <button
              type="button"
              className="btn btn-outline-secondary w-100"
              onClick={limpiarFiltros}
              disabled={!hayFiltros}
            >
              Limpiar filtros
            </button>
          </div>
        </div>
      </form>

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
          {hayFiltros
            ? 'No se encontraron productos que coincidan con los filtros seleccionados.'
            : 'No hay productos publicados todavía.'}
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

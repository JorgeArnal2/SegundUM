import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getProducto, registrarVisualizacion, type ProductoDetalle } from '../services/products'

const estadoEtiquetas: Record<string, string> = {
  NUEVO: 'Nuevo',
  COMO_NUEVO: 'Como nuevo',
  BUEN_ESTADO: 'Buen estado',
  ACEPTABLE: 'Aceptable',
  PARA_PIEZAS_O_REPARAR: 'Para piezas o reparar',
}

const precioFormatter = new Intl.NumberFormat('es-ES', {
  style: 'currency',
  currency: 'EUR',
})

const fechaFormatter = new Intl.DateTimeFormat('es-ES', {
  day: 'numeric',
  month: 'long',
  year: 'numeric',
})

export function ProductDetailPage() {
  const { id } = useParams<{ id: string }>()
  const [producto, setProducto] = useState<ProductoDetalle | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [mostrarConfirmacion, setMostrarConfirmacion] = useState(false)

  useEffect(() => {
    if (!id) return
    let cancelled = false
    setLoading(true)
    setError('')
    getProducto(id)
      .then((detalle) => {
        if (cancelled) return
        setProducto(detalle)
        registrarVisualizacion(id)
          .then(() => {
            if (!cancelled) {
              setProducto((prev) =>
                prev ? { ...prev, visualizaciones: prev.visualizaciones + 1 } : prev,
              )
            }
          })
          .catch(() => {})
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : 'No se pudo cargar el producto')
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [id])

return (
    <>
      <div className="container py-5">
        <Link to="/products" className="btn btn-outline-secondary btn-sm mb-4">
          ← Volver a productos
        </Link>

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
        ) : (
          producto && (
            <div className="row g-4">
              <div className="col-12 col-lg-8">
                <div className="d-flex flex-wrap align-items-start justify-content-between gap-2 mb-3">
                  <h2 className="h3 mb-0">{producto.titulo}</h2>
                  <span className="badge text-bg-primary fs-6">
                    {estadoEtiquetas[producto.estado] ?? producto.estado}
                  </span>
                </div>

                <div className="d-flex flex-wrap gap-2 mb-4">
                  {producto.vendido && <span className="badge text-bg-danger">Vendido</span>}
                  {producto.envioDisponible && (
                    <span className="badge text-bg-success">Envío disponible</span>
                  )}
                </div>

                <h3 className="h6 text-uppercase text-secondary">Descripción</h3>
                <p className="mb-4">{producto.descripcion}</p>

                <h3 className="h6 text-uppercase text-secondary">Datos del producto</h3>
                <dl className="row">
                  <dt className="col-sm-4">Categoría</dt>
                  <dd className="col-sm-8">{producto.categoria?.nombre ?? '—'}</dd>

                  <dt className="col-sm-4">Vendedor</dt>
                  <dd className="col-sm-8">
                    {producto.vendedor
                      ? `${producto.vendedor.nombre} ${producto.vendedor.apellidos}`
                      : '—'}
                  </dd>

                  <dt className="col-sm-4">Fecha de publicación</dt>
                  <dd className="col-sm-8">
                    {producto.fechaPublicacion
                      ? fechaFormatter.format(new Date(producto.fechaPublicacion))
                      : '—'}
                  </dd>

                  <dt className="col-sm-4">Visualizaciones</dt>
                  <dd className="col-sm-8">{producto.visualizaciones}</dd>

                  <dt className="col-sm-4">Lugar de recogida</dt>
                  <dd className="col-sm-8">{producto.recogida?.descripcion ?? 'No indicado'}</dd>
                </dl>
              </div>

              <div className="col-12 col-lg-4">
                <div className="card shadow-sm">
                  <div className="card-body p-4">
                    <div className="h3 fw-bold mb-3">
                      {precioFormatter.format(producto.precio)}
                    </div>

                    {producto.vendido ? (
                      <button type="button" className="btn btn-secondary btn-lg w-100" disabled>
                        Vendido
                      </button>
                    ) : (
                      <button
                        type="button"
                        className="btn btn-primary btn-lg w-100"
                        onClick={() => setMostrarConfirmacion(true)}
                      >
                        Comprar
                      </button>
                    )}

                    <p className="text-secondary small text-center mb-0 mt-3">
                      El proceso de compra estará disponible próximamente.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          )
        )}
      </div>

      {mostrarConfirmacion && (
        <div
          className="modal fade show d-block"
          tabIndex={-1}
          role="dialog"
          aria-modal="true"
          style={{ backgroundColor: 'rgba(0, 0, 0, 0.5)' }}
          onClick={() => setMostrarConfirmacion(false)}
        >
          <div
            className="modal-dialog modal-dialog-centered"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Confirmar compra</h5>
                <button
                  type="button"
                  className="btn-close"
                  aria-label="Cerrar"
                  onClick={() => setMostrarConfirmacion(false)}
                />
              </div>
              <div className="modal-body">
                ¿Seguro que quieres comprar este producto?
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setMostrarConfirmacion(false)}
                >
                  No
                </button>
                <button
                  type="button"
                  className="btn btn-primary"
                  onClick={() => setMostrarConfirmacion(false)}
                >
                  Sí, comprar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  )
}
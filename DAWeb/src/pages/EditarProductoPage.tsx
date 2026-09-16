import { useEffect, useState, type FormEvent } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { getProducto, modificarProducto, type ProductoDetalle } from '../services/products'

const estadoEtiquetas: Record<string, string> = {
  NUEVO: 'Nuevo',
  COMO_NUEVO: 'Como nuevo',
  BUEN_ESTADO: 'Buen estado',
  ACEPTABLE: 'Aceptable',
  PARA_PIEZAS_O_REPARAR: 'Para piezas o reparar',
}

export function EditarProductoPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { token, user } = useAuth()

  const [producto, setProducto] = useState<ProductoDetalle | null>(null)
  const [descripcion, setDescripcion] = useState('')
  const [precio, setPrecio] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [guardando, setGuardando] = useState(false)

  useEffect(() => {
    if (!id) return
    let cancelled = false
    setLoading(true)
    setError('')
    getProducto(id)
      .then((detalle) => {
        if (cancelled) return
        setProducto(detalle)
        setDescripcion(detalle.descripcion)
        setPrecio(String(detalle.precio))
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

  const esPropio = user != null && producto != null && producto.vendedor?.id === user.id

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!id || !producto) return

    const formElement = event.currentTarget
    if (!formElement.checkValidity()) {
      formElement.reportValidity()
      return
    }

    setGuardando(true)
    setError('')
    try {
      await modificarProducto(token ?? '', id, {
        descripcion: descripcion.trim(),
        precio: parseFloat(precio),
      })
      navigate(`/products/${producto.id}`)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo modificar el producto')
    } finally {
      setGuardando(false)
    }
  }

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-12 col-md-8 col-lg-6">
          <div className="mb-4">
            <h2 className="h4 mb-1">Editar producto</h2>
            <p className="text-secondary mb-0">
              Modifica la descripción o el precio del producto.
            </p>
          </div>

          {error && !producto && (
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
          ) : !producto ? null : !esPropio ? (
            <div className="alert alert-danger" role="alert">
              No puedes editar este producto porque no eres su vendedor.
              <Link to={`/products/${producto.id}`} className="d-inline-block mt-2">
                Volver al producto
              </Link>
            </div>
          ) : (
            <div className="card shadow-sm">
              <div className="card-body p-4">
                <div className="mb-4">
                  <h3 className="h5 mb-1">{producto.titulo}</h3>
                  <div className="d-flex flex-wrap gap-2 small">
                    <span className="badge text-bg-primary">
                      {estadoEtiquetas[producto.estado] ?? producto.estado}
                    </span>
                    {producto.vendido && <span className="badge text-bg-danger">Vendido</span>}
                    {producto.categoria?.nombre && (
                      <span className="badge text-bg-secondary">{producto.categoria.nombre}</span>
                    )}
                  </div>
                </div>

                {error && (
                  <div className="alert alert-danger py-2" role="alert">
                    {error}
                  </div>
                )}

                <form noValidate onSubmit={onSubmit}>
                  <div className="mb-3">
                    <label className="form-label" htmlFor="editar-descripcion">
                      Descripción
                    </label>
                    <textarea
                      id="editar-descripcion"
                      className="form-control"
                      rows={4}
                      value={descripcion}
                      onChange={(e) => setDescripcion(e.target.value)}
                      required
                      disabled={guardando}
                    />
                  </div>

                  <div className="mb-4">
                    <label className="form-label" htmlFor="editar-precio">
                      Precio (€)
                    </label>
                    <input
                      id="editar-precio"
                      className="form-control"
                      type="number"
                      value={precio}
                      onChange={(e) => setPrecio(e.target.value)}
                      placeholder="0.00"
                      min="0.01"
                      step="0.01"
                      required
                      disabled={guardando}
                    />
                  </div>

                  <div className="d-flex flex-wrap gap-2">
                    <button className="btn btn-primary" type="submit" disabled={guardando}>
                      {guardando ? 'Guardando…' : 'Cambiar'}
                    </button>
                    <button
                      type="button"
                      className="btn btn-outline-secondary"
                      onClick={() => navigate(-1)}
                      disabled={guardando}
                    >
                      Cancelar
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
import { type FormEvent, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import {
  asignarRecogida,
  createProducto,
  getCategorias,
  type Categoria,
} from '../services/products'

const ESTADOS: { value: string; label: string }[] = [
  { value: 'NUEVO', label: 'Nuevo' },
  { value: 'COMO_NUEVO', label: 'Como nuevo' },
  { value: 'BUEN_ESTADO', label: 'Buen estado' },
  { value: 'ACEPTABLE', label: 'Aceptable' },
  { value: 'PARA_PIEZAS_O_REPARAR', label: 'Para piezas o reparar' },
]

export function CrearProductoPage() {
  const { token, user } = useAuth()
  const [titulo, setTitulo] = useState('')
  const [descripcion, setDescripcion] = useState('')
  const [precio, setPrecio] = useState('')
  const [estado, setEstado] = useState('')
  const [idCategoria, setIdCategoria] = useState('')
  const [envioDisponible, setEnvioDisponible] = useState(false)
  const [recogida, setRecogida] = useState('')

  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [categoriasError, setCategoriasError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [creado, setCreado] = useState(false)

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

  function limpiarFormulario() {
    setTitulo('')
    setDescripcion('')
    setPrecio('')
    setEstado('')
    setIdCategoria('')
    setEnvioDisponible(false)
    setRecogida('')
  }

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const formElement = event.currentTarget
    if (!formElement.checkValidity()) {
      formElement.reportValidity()
      return
    }

    setError('')
    setSubmitting(true)
    try {
      const idProducto = await createProducto(token ?? '', {
        titulo,
        descripcion,
        precio: parseFloat(precio),
        estado,
        idCategoria,
        envioDisponible,
        idVendedor: user?.id ?? '',
      })
      if (recogida.trim()) {
        await asignarRecogida(token ?? '', idProducto, recogida.trim())
      }
      limpiarFormulario()
      setCreado(true)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo publicar el producto')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-12 col-md-8 col-lg-6">
          <div className="card shadow-sm">
            <div className="card-body p-4">
              <div className="mb-4">
                <h2 className="h4 mb-2">Publicar producto</h2>
                <p className="text-secondary mb-0">
                  Da de alta un producto para venderlo en SegundUM.
                </p>
              </div>

              {error && (
                <div className="alert alert-danger" role="alert">
                  {error}
                </div>
              )}

              {creado ? (
                <div>
                  <div className="alert alert-success" role="alert">
                    Producto publicado correctamente.
                  </div>
                  <div className="d-flex flex-wrap gap-2">
                    <button
                      type="button"
                      className="btn btn-primary"
                      onClick={() => setCreado(false)}
                    >
                      Publicar otro producto
                    </button>
                    <Link className="btn btn-outline-secondary" to="/products">
                      Ver productos
                    </Link>
                  </div>
                </div>
              ) : (
                <form noValidate onSubmit={onSubmit}>
                  <div className="mb-3">
                    <label className="form-label" htmlFor="producto-titulo">
                      Título
                    </label>
                    <input
                      id="producto-titulo"
                      className="form-control"
                      type="text"
                      value={titulo}
                      onChange={(e) => setTitulo(e.target.value)}
                      placeholder="Ej. Portátil de segunda mano"
                      required
                      disabled={submitting}
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label" htmlFor="producto-descripcion">
                      Descripción
                    </label>
                    <textarea
                      id="producto-descripcion"
                      className="form-control"
                      rows={4}
                      value={descripcion}
                      onChange={(e) => setDescripcion(e.target.value)}
                      placeholder="Describe el estado y las características del producto"
                      required
                      disabled={submitting}
                    />
                  </div>

                  <div className="row g-3 mb-3">
                    <div className="col-12 col-sm-6">
                      <label className="form-label" htmlFor="producto-precio">
                        Precio (€)
                      </label>
                      <input
                        id="producto-precio"
                        className="form-control"
                        type="number"
                        value={precio}
                        onChange={(e) => setPrecio(e.target.value)}
                        placeholder="0.00"
                        min="0.01"
                        step="0.01"
                        required
                        disabled={submitting}
                      />
                    </div>
                    <div className="col-12 col-sm-6">
                      <label className="form-label" htmlFor="producto-estado">
                        Estado
                      </label>
                      <select
                        id="producto-estado"
                        className="form-select"
                        value={estado}
                        onChange={(e) => setEstado(e.target.value)}
                        required
                        disabled={submitting}
                      >
                        <option value="" disabled>
                          Selecciona un estado
                        </option>
                        {ESTADOS.map((estadoItem) => (
                          <option key={estadoItem.value} value={estadoItem.value}>
                            {estadoItem.label}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label" htmlFor="producto-categoria">
                      Categoría
                    </label>
                    <select
                      id="producto-categoria"
                      className="form-select"
                      value={idCategoria}
                      onChange={(e) => setIdCategoria(e.target.value)}
                      required
                      disabled={submitting || categorias.length === 0}
                    >
                      <option value="" disabled>
                        {categoriasError || 'Selecciona una categoría'}
                      </option>
                      {categorias.map((categoria) => (
                        <option key={categoria.id} value={categoria.id}>
                          {categoria.nombre}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-3 form-check">
                    <input
                      id="producto-envio"
                      className="form-check-input"
                      type="checkbox"
                      checked={envioDisponible}
                      onChange={(e) => setEnvioDisponible(e.target.checked)}
                      disabled={submitting}
                    />
                    <label className="form-check-label" htmlFor="producto-envio">
                      Envío disponible
                    </label>
                  </div>

                  <div className="mb-4">
                    <label className="form-label" htmlFor="producto-recogida">
                      Lugar de recogida
                    </label>
                    <input
                      id="producto-recogida"
                      className="form-control"
                      type="text"
                      value={recogida}
                      onChange={(e) => setRecogida(e.target.value)}
                      placeholder="Ej. Campus de Espinardo, frente a la biblioteca"
                      disabled={submitting}
                    />
                    <div className="form-text">
                      Opcional. Si no lo indicas ahora podrás añadirlo más adelante.
                    </div>
                  </div>

                  <div className="d-flex flex-wrap gap-2">
                    <button className="btn btn-primary" type="submit" disabled={submitting}>
                      {submitting ? 'Publicando…' : 'Publicar producto'}
                    </button>
                    <button
                      type="button"
                      className="btn btn-outline-secondary"
                      onClick={limpiarFormulario}
                      disabled={submitting}
                    >
                      Limpiar
                    </button>
                  </div>
                </form>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
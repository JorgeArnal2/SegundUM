import { Link } from 'react-router-dom'
import type { Producto } from '../services/products'

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
  month: 'short',
  year: 'numeric',
})

export function ProductCard({ producto }: { producto: Producto }) {
  const estadoLabel = estadoEtiquetas[producto.estado] ?? producto.estado

  return (
    <Link
      to={`/products/${producto.id}`}
      className="d-block h-100 text-decoration-none text-reset"
    >
      <article className="card h-100 shadow-sm">
        <div className="card-body d-flex flex-column gap-2">
          <div className="d-flex justify-content-between align-items-start gap-2">
            <h3 className="h6 mb-0">{producto.titulo}</h3>
            <span className="badge text-bg-primary">{estadoLabel}</span>
          </div>
          <p className="text-secondary small mb-0 flex-grow-1">{producto.descripcion}</p>
          <div className="fw-bold h5 mb-0">{precioFormatter.format(producto.precio)}</div>
          <div className="d-flex flex-wrap gap-1 small">
            {producto.vendido && <span className="badge text-bg-danger">Vendido</span>}
            {producto.envioDisponible && (
              <span className="badge text-bg-success">Envío disponible</span>
            )}
          </div>
          <div className="text-secondary small">
            {producto.categoria?.nombre && <span>{producto.categoria.nombre}</span>}
            {producto.vendedor && (
              <span>
                {producto.categoria?.nombre ? ' · ' : ''}
                {producto.vendedor.nombre} {producto.vendedor.apellidos}
              </span>
            )}
          </div>
          {producto.fechaPublicacion && (
            <div className="text-secondary small">
              {fechaFormatter.format(new Date(producto.fechaPublicacion))}
            </div>
          )}
        </div>
      </article>
    </Link>
  )
}

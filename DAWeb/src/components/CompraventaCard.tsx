import type { Compraventa } from '../services/admin'

const precioFormatter = new Intl.NumberFormat('es-ES', {
  style: 'currency',
  currency: 'EUR',
})

const fechaFormatter = new Intl.DateTimeFormat('es-ES', {
  day: 'numeric',
  month: 'short',
  year: 'numeric',
})

export function CompraventaCard({ compraventa }: { compraventa: Compraventa }) {
  return (
    <article className="card h-100 shadow-sm">
      <div className="card-body d-flex flex-column gap-2">
        <h3 className="h6 mb-0">{compraventa.titulo}</h3>
        <div className="fw-bold">{precioFormatter.format(compraventa.precio)}</div>
        <div className="d-flex flex-wrap gap-1 small">
          <span className="badge text-bg-primary">
            Comprador: {compraventa.nombreComprador}
          </span>
          <span className="badge text-bg-success">
            Vendedor: {compraventa.nombreVendedor}
          </span>
        </div>
        {compraventa.recogida && (
          <div className="text-secondary small">
            Recogida: {compraventa.recogida}
          </div>
        )}
        {compraventa.fecha && (
          <div className="text-secondary small">
            {fechaFormatter.format(new Date(compraventa.fecha))}
          </div>
        )}
      </div>
    </article>
  )
}
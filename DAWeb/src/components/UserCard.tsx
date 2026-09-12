import type { AdminUsuario } from '../services/admin'

function formatMedia(media: number, total: number): string {
  return total > 0 && media > 0 ? media.toFixed(1) : 'Sin valorar'
}

export function UserCard({ usuario }: { usuario: AdminUsuario }) {
  return (
    <article className="card h-100 shadow-sm">
      <div className="card-body d-flex flex-column gap-2">
        <div>
          <h3 className="h6 mb-0">
            {usuario.nombre} {usuario.apellidos}
          </h3>
        </div>
        <p className="text-secondary small mb-0">{usuario.email}</p>
        <div className="d-flex flex-wrap gap-1 small">
          <span className="badge text-bg-primary">Compras: {usuario.contadorCompras}</span>
          <span className="badge text-bg-success">Ventas: {usuario.contadorVentas}</span>
        </div>
        <div className="text-secondary small">
          Como comprador: {formatMedia(usuario.valoracionMediaComoComprador, usuario.numeroValoracionesComoComprador)}
        </div>
        <div className="text-secondary small">
          Como vendedor: {formatMedia(usuario.valoracionMediaComoVendedor, usuario.numeroValoracionesComoVendedor)}
        </div>
      </div>
    </article>
  )
}
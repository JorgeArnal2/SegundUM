import { useEffect, useState } from 'react'
import { CompraventaCard } from '../components/CompraventaCard'
import { UserCard } from '../components/UserCard'
import { UserSelect } from '../components/UserSelect'
import { useAuth } from '../contexts/AuthContext'
import {
  getCompraventas,
  getUsuarios,
  type AdminUsuario,
  type Compraventa,
} from '../services/admin'

type Tab = 'usuarios' | 'compraventas'

const PAGE_SIZE = 12

export function AdminPage() {
  const { token } = useAuth()
  const [tab, setTab] = useState<Tab>('usuarios')
  const [usuarios, setUsuarios] = useState<AdminUsuario[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [comprador, setComprador] = useState<AdminUsuario | null>(null)
  const [vendedor, setVendedor] = useState<AdminUsuario | null>(null)
  const [compraventas, setCompraventas] = useState<Compraventa[]>([])
  const [compraventasPage, setCompraventasPage] = useState(0)
  const [compraventasTotalPages, setCompraventasTotalPages] = useState(0)
  const [compraventasTotal, setCompraventasTotal] = useState(0)
  const [compraventasLoading, setCompraventasLoading] = useState(false)
  const [compraventasError, setCompraventasError] = useState('')

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')
    getUsuarios(token ?? '')
      .then((items) => {
        if (!cancelled) setUsuarios(items)
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : 'No se pudieron cargar los usuarios')
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [token])

  useEffect(() => {
    if (!comprador || !vendedor) return
    let cancelled = false
    setCompraventasLoading(true)
    setCompraventasError('')
    getCompraventas(token ?? '', comprador.id, vendedor.id, compraventasPage, PAGE_SIZE)
      .then((resultado) => {
        if (cancelled) return
        setCompraventas(resultado.items)
        setCompraventasTotalPages(resultado.totalPages)
        setCompraventasTotal(resultado.totalElements)
      })
      .catch((err) => {
        if (!cancelled) {
          setCompraventasError(
            err instanceof Error ? err.message : 'No se pudieron cargar las compraventas',
          )
        }
      })
      .finally(() => {
        if (!cancelled) setCompraventasLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [comprador, vendedor, compraventasPage, token])

  function seleccionarComprador(usuario: AdminUsuario | null) {
    setComprador(usuario)
    setCompraventasPage(0)
  }

  function seleccionarVendedor(usuario: AdminUsuario | null) {
    setVendedor(usuario)
    setCompraventasPage(0)
  }

  const busquedaCompleta = comprador !== null && vendedor !== null

  return (
    <div className="container py-5">
      <div className="mb-4">
        <h2 className="h4 mb-1">Panel de administración</h2>
        <p className="text-secondary mb-0">
          Consulta los usuarios y compraventas de SegundUM.
        </p>
      </div>

      <ul className="nav nav-pills mb-4" role="tablist">
        <li className="nav-item" role="presentation">
          <button
            type="button"
            role="tab"
            className={`nav-link ${tab === 'usuarios' ? 'active' : ''}`}
            aria-selected={tab === 'usuarios'}
            onClick={() => setTab('usuarios')}
          >
            Usuarios
          </button>
        </li>
        <li className="nav-item" role="presentation">
          <button
            type="button"
            role="tab"
            className={`nav-link ${tab === 'compraventas' ? 'active' : ''}`}
            aria-selected={tab === 'compraventas'}
            onClick={() => setTab('compraventas')}
          >
            Compraventas
          </button>
        </li>
      </ul>

      {tab === 'usuarios' ? (
        <>
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
          ) : usuarios.length === 0 && !error ? (
            <div className="alert alert-info" role="status">
              No hay usuarios registrados todavía.
            </div>
          ) : (
            <>
              <p className="text-secondary mb-3">
                {usuarios.length} {usuarios.length === 1 ? 'usuario' : 'usuarios'}
              </p>
              <div className="row g-4">
                {usuarios.map((usuario) => (
                  <div className="col-12 col-sm-6 col-lg-3" key={usuario.id}>
                    <UserCard usuario={usuario} />
                  </div>
                ))}
              </div>
            </>
          )}
        </>
      ) : (
        <div className="row g-4">
          <div className="col-12 col-md-6">
            <UserSelect
              label="Comprador"
              usuarios={usuarios}
              value={comprador}
              onChange={seleccionarComprador}
            />
          </div>
          <div className="col-12 col-md-6">
            <UserSelect
              label="Vendedor"
              usuarios={usuarios}
              value={vendedor}
              onChange={seleccionarVendedor}
            />
          </div>
        </div>
      )}

      {tab === 'compraventas' && (
        <div className="mt-4">
          {!busquedaCompleta ? (
            <div className="alert alert-info" role="status">
              Selecciona un comprador y un vendedor para consultar sus compraventas.
            </div>
          ) : (
            <>
              {compraventasError && (
                <div className="alert alert-danger" role="alert">
                  {compraventasError}
                </div>
              )}

              {compraventasLoading ? (
                <div className="text-center py-5">
                  <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Cargando…</span>
                  </div>
                </div>
              ) : compraventas.length === 0 && !compraventasError ? (
                <div className="alert alert-info" role="status">
                  No hay compraventas entre {comprador?.nombre} {comprador?.apellidos} y{' '}
                  {vendedor?.nombre} {vendedor?.apellidos}.
                </div>
              ) : (
                <>
                  <p className="text-secondary mb-3">
                    {compraventasTotal}{' '}
                    {compraventasTotal === 1 ? 'compraventa' : 'compraventas'} entre{' '}
                    {comprador?.nombre} {comprador?.apellidos} y {vendedor?.nombre}{' '}
                    {vendedor?.apellidos}.
                  </p>
                  <div className="row g-4">
                    {compraventas.map((compraventa) => (
                      <div className="col-12 col-sm-6 col-lg-3" key={compraventa.id}>
                        <CompraventaCard compraventa={compraventa} />
                      </div>
                    ))}
                  </div>

                  {compraventasTotalPages > 1 && (
                    <nav
                      className="d-flex justify-content-center mt-4"
                      aria-label="Paginación de compraventas"
                    >
                      <ul className="pagination">
                        <li className={`page-item ${compraventasPage === 0 ? 'disabled' : ''}`}>
                          <button
                            className="page-link"
                            onClick={() =>
                              setCompraventasPage((prev) => Math.max(0, prev - 1))
                            }
                            disabled={compraventasPage === 0}
                          >
                            Anterior
                          </button>
                        </li>
                        <li className="page-item disabled">
                          <span className="page-link">
                            Página {compraventasPage + 1} de {compraventasTotalPages}
                          </span>
                        </li>
                        <li
                          className={`page-item ${
                            compraventasPage >= compraventasTotalPages - 1 ? 'disabled' : ''
                          }`}
                        >
                          <button
                            className="page-link"
                            onClick={() =>
                              setCompraventasPage((prev) =>
                                Math.min(compraventasTotalPages - 1, prev + 1),
                              )
                            }
                            disabled={compraventasPage >= compraventasTotalPages - 1}
                          >
                            Siguiente
                          </button>
                        </li>
                      </ul>
                    </nav>
                  )}
                </>
              )}
            </>
          )}
        </div>
      )}
    </div>
  )
}
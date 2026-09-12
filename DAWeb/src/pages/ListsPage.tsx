import { useAuth } from '../contexts/AuthContext'

export function ListsPage() {
  const { user } = useAuth()

  return (
    <div className="container py-5">
      <div className="mb-4">
        <h2 className="h4 mb-1">Listas</h2>
        <p className="text-secondary mb-0">
          Aquí podrás consultar tus listas de productos, {user?.name}.
        </p>
      </div>
      <div className="alert alert-info" role="status">
        La funcionalidad de listas estará disponible próximamente.
      </div>
    </div>
  )
}
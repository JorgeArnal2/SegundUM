import { useMemo, useState, type ChangeEvent, type MouseEvent } from 'react'
import type { AdminUsuario } from '../services/admin'

function nombreCompleto(usuario: AdminUsuario): string {
  return `${usuario.nombre} ${usuario.apellidos}`.trim()
}

type UserSelectProps = {
  label: string
  usuarios: AdminUsuario[]
  value: AdminUsuario | null
  onChange: (usuario: AdminUsuario | null) => void
  placeholder?: string
}

export function UserSelect({ label, usuarios, value, onChange, placeholder }: UserSelectProps) {
  const [query, setQuery] = useState(value ? nombreCompleto(value) : '')
  const [open, setOpen] = useState(false)

  const filtrados = useMemo(() => {
    const q = query.trim().toLowerCase()
    if (!q) return usuarios
    return usuarios.filter((usuario) => {
      const nombre = nombreCompleto(usuario).toLowerCase()
      return nombre.includes(q) || usuario.email.toLowerCase().includes(q)
    })
  }, [usuarios, query])

  function onInputChange(event: ChangeEvent<HTMLInputElement>) {
    setQuery(event.target.value)
    setOpen(true)
    if (value) onChange(null)
  }

  function elegir(event: MouseEvent<HTMLLIElement>, usuario: AdminUsuario) {
    event.preventDefault()
    setQuery(nombreCompleto(usuario))
    setOpen(false)
    onChange(usuario)
  }

  function limpiar() {
    setQuery('')
    setOpen(false)
    onChange(null)
  }

  return (
    <div className="position-relative">
      <label className="form-label">{label}</label>
      <div className="d-flex gap-2">
        <input
          type="text"
          className="form-control"
          value={query}
          onChange={onInputChange}
          onFocus={() => setOpen(true)}
          onBlur={() => setOpen(false)}
          placeholder={placeholder ?? 'Buscar usuario…'}
          autoComplete="off"
        />
        {value && (
          <button
            type="button"
            className="btn btn-outline-secondary"
            onClick={limpiar}
            aria-label="Quitar selección"
          >
            ×
          </button>
        )}
      </div>

      {open && (
        <ul
          className="list-group position-absolute w-100 shadow-sm"
          style={{ zIndex: 1050, maxHeight: 260, overflowY: 'auto' }}
        >
          {filtrados.length === 0 ? (
            <li className="list-group-item text-secondary small">Sin coincidencias</li>
          ) : (
            filtrados.map((usuario) => (
              <li
                key={usuario.id}
                className="list-group-item list-group-item-action"
                onMouseDown={(event) => elegir(event, usuario)}
              >
                <div className="fw-semibold small">{nombreCompleto(usuario)}</div>
                <div className="text-secondary small">{usuario.email}</div>
              </li>
            ))
          )}
        </ul>
      )}
    </div>
  )
}
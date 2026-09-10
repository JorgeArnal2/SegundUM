import { type FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

export function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const auth = useAuth()
  const navigate = useNavigate()

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
      await auth.login(email, password)
      navigate('/', { replace: true })
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo iniciar sesión')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-12 col-md-6">
          <div className="card shadow-sm">
            <div className="card-body p-4">
              <div className="d-flex justify-content-between align-items-start gap-2 mb-4">
                <div>
                  <h2 className="h4 mb-2">Iniciar sesión</h2>
                  <p className="text-secondary mb-0">
                    Accede con tu cuenta de SegundUM.
                  </p>
                </div>
                <Link className="btn btn-outline-secondary btn-sm" to="/register">
                  Crear cuenta
                </Link>
              </div>

              {error && (
                <div className="alert alert-danger" role="alert">
                  {error}
                </div>
              )}

              <form noValidate onSubmit={onSubmit}>
                <div className="mb-3">
                  <label className="form-label" htmlFor="login-email">
                    Correo electrónico
                  </label>
                  <input
                    id="login-email"
                    className="form-control"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="tu@email.com"
                    required
                    autoComplete="email"
                    disabled={submitting}
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label" htmlFor="login-password">
                    Contraseña
                  </label>
                  <input
                    id="login-password"
                    type="password"
                    className="form-control"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="********"
                    required
                    minLength={8}
                    autoComplete="current-password"
                    disabled={submitting}
                  />
                </div>
                <div className="d-flex flex-wrap gap-2">
                  <button className="btn btn-primary" type="submit" disabled={submitting}>
                    {submitting ? 'Entrando…' : 'Entrar'}
                  </button>
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={() => {
                      setEmail('')
                      setPassword('')
                      setError('')
                    }}
                    disabled={submitting}
                  >
                    Limpiar
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

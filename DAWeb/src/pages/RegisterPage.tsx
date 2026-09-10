import { type ChangeEvent, type FormEvent, useState } from 'react'
import { Link } from 'react-router-dom'

type RegisterFormState = {
  name: string
  surname: string
  email: string
  password: string
  birthDate: string
  phone: string
}

const initialFormState: RegisterFormState = {
  name: '',
  surname: '',
  email: '',
  password: '',
  birthDate: '',
  phone: '',
}

const today = new Date().toISOString().split('T')[0]

export function RegisterPage() {
  const [form, setForm] = useState<RegisterFormState>(initialFormState)
  const [submitted, setSubmitted] = useState(false)

  function handleChange(event: ChangeEvent<HTMLInputElement>) {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
    if (submitted) {
      setSubmitted(false)
    }
  }

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const formElement = event.currentTarget
    if (!formElement.checkValidity()) {
      formElement.reportValidity()
      return
    }

    setSubmitted(true)
    setForm(initialFormState)
  }

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-12 col-lg-8">
          <div className="card shadow-sm">
            <div className="card-body p-4 p-md-5">
              <div className="d-flex flex-column flex-md-row justify-content-between gap-2 mb-4">
                <div>
                  <h2 className="h4 mb-2">Crear cuenta</h2>
                  <p className="text-secondary mb-0">
                    Formulario preparado con validación HTML5 y sin envío real al backend.
                  </p>
                </div>
                <Link className="btn btn-outline-secondary btn-sm" to="/login">
                  Ya tengo cuenta
                </Link>
              </div>

              {submitted && (
                <div className="alert alert-success" role="status">
                  Formulario preparado correctamente. No se ha enviado nada.
                </div>
              )}

              <form noValidate onSubmit={handleSubmit}>
                <div className="row g-3">
                  <div className="col-12 col-md-6">
                    <label className="form-label" htmlFor="name">
                      Nombre
                    </label>
                    <input
                      id="name"
                      name="name"
                      className="form-control"
                      type="text"
                      value={form.name}
                      onChange={handleChange}
                      required
                      autoComplete="given-name"
                      placeholder="Tu nombre"
                      pattern="[A-Za-zÁÉÍÓÚáéíóúÑñ\s'-]+"
                    />
                  </div>

                  <div className="col-12 col-md-6">
                    <label className="form-label" htmlFor="surname">
                      Apellidos
                    </label>
                    <input
                      id="surname"
                      name="surname"
                      className="form-control"
                      type="text"
                      value={form.surname}
                      onChange={handleChange}
                      required
                      autoComplete="family-name"
                      placeholder="Tus apellidos"
                      pattern="[A-Za-zÁÉÍÓÚáéíóúÑñ\s'-]+"
                    />
                  </div>

                  <div className="col-12 col-md-6">
                    <label className="form-label" htmlFor="register-email">
                      Correo electrónico
                    </label>
                    <input
                      id="register-email"
                      name="email"
                      className="form-control"
                      type="email"
                      value={form.email}
                      onChange={handleChange}
                      required
                      autoComplete="email"
                      placeholder="tu@email.com"
                    />
                  </div>

                  <div className="col-12 col-md-6">
                    <label className="form-label" htmlFor="register-password">
                      Contraseña
                    </label>
                    <input
                      id="register-password"
                      name="password"
                      className="form-control"
                      type="password"
                      value={form.password}
                      onChange={handleChange}
                      required
                      minLength={8}
                      autoComplete="new-password"
                      placeholder="Mínimo 8 caracteres"
                    />
                  </div>

                  <div className="col-12 col-md-6">
                    <label className="form-label" htmlFor="birth-date">
                      Fecha de nacimiento
                    </label>
                    <input
                      id="birth-date"
                      name="birthDate"
                      className="form-control"
                      type="date"
                      value={form.birthDate}
                      onChange={handleChange}
                      required
                      max={today}
                      min="1900-01-01"
                    />
                  </div>

                  <div className="col-12 col-md-6">
                    <label className="form-label" htmlFor="phone">
                      Teléfono
                    </label>
                    <input
                      id="phone"
                      name="phone"
                      className="form-control"
                      type="tel"
                      value={form.phone}
                      onChange={handleChange}
                      required
                      inputMode="numeric"
                      pattern="[0-9]{9,12}"
                      minLength={9}
                      maxLength={12}
                      autoComplete="tel"
                      placeholder="Ej. 612345678"
                    />
                  </div>
                </div>

                <div className="d-flex flex-wrap gap-2 mt-4">
                  <button className="btn btn-primary" type="submit">
                    Crear cuenta
                  </button>
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={() => {
                      setForm(initialFormState)
                      setSubmitted(false)
                    }}
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

export type AuthUser = {
  id: string
  name: string
  email: string
  roles: string[]
}

export type AuthResponse = {
  token: string
  id: string
  nombreCompleto: string
  roles: string[]
}

export type LoginCredentials = {
  email: string
  password: string
}

export type RegisterPayload = {
  nombre: string
  apellidos: string
  email: string
  clave: string
  fechaNacimiento: string
  telefono: string
}

export async function loginRequest(credentials: LoginCredentials): Promise<AuthResponse> {
  const response = await fetch('/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(credentials),
  })

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error('Email o contraseña incorrectos')
    }
    throw new Error('No se pudo iniciar sesión. Inténtalo de nuevo.')
  }

  return response.json()
}

export async function registerRequest(payload: RegisterPayload): Promise<void> {
  const response = await fetch('/api/usuarios', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    let message = 'No se pudo crear la cuenta. Inténtalo de nuevo.'
    try {
      const data = await response.json()
      if (typeof data?.error === 'string' && data.error) {
        message = data.error
      }
    } catch {
      // cuerpo de error no es JSON
    }
    if (response.status === 409) {
      message = 'Ya existe un usuario con ese email'
    }
    throw new Error(message)
  }
}

export async function logoutRequest(): Promise<void> {
  await fetch('/auth/logout', { method: 'POST' })
}

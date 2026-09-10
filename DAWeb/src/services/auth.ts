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

export async function logoutRequest(): Promise<void> {
  await fetch('/auth/logout', { method: 'POST' })
}

import { createContext, useContext, useState, type ReactNode } from 'react'
import { loginRequest, logoutRequest, type AuthUser } from '../services/auth'

type StoredSession = {
  token: string
  user: AuthUser
}

const STORAGE_KEY = 'segundum.auth'

type AuthContextType = {
  user: AuthUser | null
  token: string | null
  isAuthenticated: boolean
  login: (email: string, password: string) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

function loadSession(): StoredSession | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? (JSON.parse(raw) as StoredSession) : null
  } catch {
    return null
  }
}

function persistSession(session: StoredSession | null) {
  try {
    if (session) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(session))
    } else {
      localStorage.removeItem(STORAGE_KEY)
    }
  } catch {
    // almacenamiento no disponible: la sesión vive solo en memoria
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<StoredSession | null>(loadSession)

  async function login(email: string, password: string) {
    const authResponse = await loginRequest({ email, password })
    const user: AuthUser = {
      id: authResponse.id,
      name: authResponse.nombreCompleto,
      email,
      roles: authResponse.roles,
    }
    const nextSession = { token: authResponse.token, user }
    setSession(nextSession)
    persistSession(nextSession)
  }

  async function logout() {
    try {
      await logoutRequest()
    } finally {
      setSession(null)
      persistSession(null)
    }
  }

  return (
    <AuthContext.Provider
      value={{
        user: session?.user ?? null,
        token: session?.token ?? null,
        isAuthenticated: !!session,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

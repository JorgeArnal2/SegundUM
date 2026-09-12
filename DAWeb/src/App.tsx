import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { MainLayout } from './layouts/MainLayout'
import { LoginPage } from './pages/LoginPage'
import { RegisterPage } from './pages/RegisterPage'
import { ProductsPage } from './pages/ProductsPage'
import { CrearProductoPage } from './pages/CrearProductoPage'
import { ListsPage } from './pages/ListsPage'
import { AdminPage } from './pages/AdminPage'
import { AuthProvider, useAuth } from './contexts/AuthContext'
import { AdminRoute } from './components/AdminRoute'
import { NonAdminRoute } from './components/NonAdminRoute'

function esAdmin(roles: string[] | undefined): boolean {
  return (roles ?? []).some((r) => {
    const rol = r.toUpperCase()
    return rol === 'ADMINISTRADOR' || rol === 'ADMIN'
  })
}

function DefaultRedirect() {
  const { isAuthenticated, user } = useAuth()
  if (!isAuthenticated) return <Navigate to="/login" replace />
  return <Navigate to={esAdmin(user?.roles) ? '/admin' : '/products'} replace />
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <MainLayout>
          <Routes>
            <Route path="/" element={<DefaultRedirect />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route
              path="/products"
              element={
                <NonAdminRoute>
                  <ProductsPage />
                </NonAdminRoute>
              }
            />
            <Route
              path="/products/new"
              element={
                <NonAdminRoute>
                  <CrearProductoPage />
                </NonAdminRoute>
              }
            />
            <Route
              path="/lists"
              element={
                <NonAdminRoute>
                  <ListsPage />
                </NonAdminRoute>
              }
            />
            <Route
              path="/admin"
              element={
                <AdminRoute>
                  <AdminPage />
                </AdminRoute>
              }
            />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </MainLayout>
      </BrowserRouter>
    </AuthProvider>
  )
}
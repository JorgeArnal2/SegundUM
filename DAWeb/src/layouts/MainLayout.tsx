import type { ReactNode } from 'react'
import { SiteHeader } from '../components/SiteHeader'

type MainLayoutProps = {
  children: ReactNode
}

export function MainLayout({ children }: MainLayoutProps) {
  return (
    <div className="app-shell">
      <SiteHeader />
      <main>{children}</main>
      <footer className="border-top py-4">
        <div className="container small text-secondary">
          DAWeb · Proyecto final · Base inicial del frontend
        </div>
      </footer>
    </div>
  )
}

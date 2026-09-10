export type Categoria = {
  id: string
  nombre: string
}

export type Vendedor = {
  id: string
  nombre: string
  apellidos: string
  email: string
}

export type Producto = {
  id: string
  titulo: string
  descripcion: string
  precio: number
  estado: string
  fechaPublicacion: string
  visualizaciones: number
  envioDisponible: boolean
  vendido: boolean
  categoria: Categoria | null
  vendedor: Vendedor | null
  recogida: string | null
}

export type PaginaProductos = {
  items: Producto[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export async function getProductos(page = 0, size = 8): Promise<PaginaProductos> {
  const response = await fetch(`/api/productos?page=${page}&size=${size}`)

  if (!response.ok) {
    throw new Error('No se pudieron cargar los productos')
  }

  const data = await response.json()
  const items: Producto[] = data._embedded?.productoDtoList ?? []

  return {
    items,
    page: data.page?.number ?? page,
    size: data.page?.size ?? size,
    totalElements: data.page?.totalElements ?? items.length,
    totalPages: data.page?.totalPages ?? (items.length > 0 ? 1 : 0),
  }
}

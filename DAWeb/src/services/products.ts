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

export type FiltrosProducto = {
  texto?: string
  categoria?: string
  estado?: string
  precioMaximo?: number
}

export async function getProductos(
  page = 0,
  size = 8,
  filtros: FiltrosProducto = {},
): Promise<PaginaProductos> {
  const parametros = new URLSearchParams({ page: String(page), size: String(size) })

  const texto = filtros.texto?.trim()
  if (texto) parametros.set('texto', texto)
  if (filtros.categoria) parametros.set('categoria', filtros.categoria)
  if (filtros.estado) parametros.set('estado', filtros.estado)
  if (filtros.precioMaximo != null && filtros.precioMaximo > 0) {
    parametros.set('precioMaximo', String(filtros.precioMaximo))
  }

  const response = await fetch(`/api/productos?${parametros.toString()}`)

  if (!response.ok) {
    throw new Error('No se pudieron cargar los productos')
  }

  return parsearPagina(await response.json(), page, size)
}

export async function getProductosPorVendedor(
  idVendedor: string,
  page = 0,
  size = 12,
): Promise<PaginaProductos> {
  const response = await fetch(`/api/productos/vendedor/${idVendedor}?page=${page}&size=${size}`)

  if (!response.ok) {
    throw new Error(await errorMessageDe(response, 'No se pudieron cargar los productos del vendedor'))
  }

  return parsearPagina(await response.json(), page, size)
}

function parsearPagina(data: unknown, page: number, size: number): PaginaProductos {
  const pagina = data as {
    _embedded?: { productoDtoList?: Producto[] }
    page?: { number?: number; size?: number; totalElements?: number; totalPages?: number }
  }
  const items: Producto[] = pagina._embedded?.productoDtoList ?? []

  return {
    items,
    page: pagina.page?.number ?? page,
    size: pagina.page?.size ?? size,
    totalElements: pagina.page?.totalElements ?? items.length,
    totalPages: pagina.page?.totalPages ?? (items.length > 0 ? 1 : 0),
  }
}

export async function getCategorias(): Promise<Categoria[]> {
  const response = await fetch('/api/categorias')

  if (!response.ok) {
    throw new Error('No se pudieron cargar las categorías')
  }

  return response.json()
}

export type ProductoDetalle = {
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
  recogida: {
    descripcion: string
    longitud: number | null
    latitud: number | null
  } | null
}

export async function getProducto(id: string): Promise<ProductoDetalle> {
  const response = await fetch(`/api/productos/${id}`)

  if (!response.ok) {
    throw new Error(await errorMessageDe(response, 'No se pudo cargar el producto'))
  }

  return response.json()
}

export async function registrarVisualizacion(id: string): Promise<void> {
  const response = await fetch(`/api/productos/${id}/visualizaciones`, {
    method: 'POST',
  })

  if (!response.ok) {
    throw new Error('No se pudo registrar la visualización')
  }
}

export type CrearProductoDatos = {
  titulo: string
  descripcion: string
  precio: number
  estado: string
  idCategoria: string
  envioDisponible: boolean
  idVendedor: string
}

async function errorMessageDe(response: Response, fallback: string): Promise<string> {
  try {
    const data = await response.json()
    if (typeof data.error === 'string' && data.error) return data.error
  } catch {
    // sin cuerpo JSON: se usa el mensaje genérico
  }
  return fallback
}

export async function createProducto(token: string, datos: CrearProductoDatos): Promise<string> {
  const response = await fetch('/api/productos', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(datos),
  })

  if (!response.ok) {
    throw new Error(await errorMessageDe(response, 'No se pudo publicar el producto'))
  }

  const location = response.headers.get('Location') ?? ''
  const id = location
    .split('/')
    .filter(Boolean)
    .pop()

  if (!id) {
    throw new Error('No se pudo obtener el identificador del producto')
  }

  return id
}

export async function asignarRecogida(
  token: string,
  idProducto: string,
  descripcion: string,
): Promise<void> {
  const response = await fetch(`/api/productos/${idProducto}/recogida`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ descripcion, longitud: 0, latitud: 0 }),
  })

  if (!response.ok) {
    throw new Error(await errorMessageDe(response, 'No se pudo guardar el lugar de recogida'))
  }
}

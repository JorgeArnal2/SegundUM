export type AdminUsuario = {
  id: string
  nombre: string
  apellidos: string
  email: string
  contadorCompras: number
  contadorVentas: number
  numeroValoracionesComoComprador: number
  numeroValoracionesComoVendedor: number
  valoracionMediaComoComprador: number
  valoracionMediaComoVendedor: number
  href: string
}

export async function getUsuarios(token: string): Promise<AdminUsuario[]> {
  const response = await fetch('/api/usuarios', {
    headers: { Authorization: `Bearer ${token}` },
  })

  if (!response.ok) {
    throw new Error('No se pudieron cargar los usuarios')
  }

  return response.json()
}

export type Compraventa = {
  id: string
  idProducto: string
  titulo: string
  precio: number
  recogida: string
  idVendedor: string
  nombreVendedor: string
  idComprador: string
  nombreComprador: string
  fecha: string
}

export type PaginaCompraventas = {
  items: Compraventa[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export async function getCompraventas(
  token: string,
  idComprador: string,
  idVendedor: string,
  page = 0,
  size = 12,
): Promise<PaginaCompraventas> {
  const params = new URLSearchParams({
    idComprador,
    idVendedor,
    page: String(page),
    size: String(size),
  })

  const response = await fetch(`/api/compraventas?${params.toString()}`, {
    headers: { Authorization: `Bearer ${token}` },
  })

  if (!response.ok) {
    throw new Error('No se pudieron cargar las compraventas')
  }

  const data = await response.json()
  const embedded = data._embedded ?? {}
  const items: Compraventa[] =
    embedded.compraventaDtoList ??
    Object.values(embedded).find((value) => Array.isArray(value)) ??
    []

  return {
    items,
    page: data.page?.number ?? page,
    size: data.page?.size ?? size,
    totalElements: data.page?.totalElements ?? items.length,
    totalPages: data.page?.totalPages ?? (items.length > 0 ? 1 : 0),
  }
}
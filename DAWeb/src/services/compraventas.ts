import type { Compraventa, PaginaCompraventas } from './admin'

async function errorMessageDe(response: Response, fallback: string): Promise<string> {
  try {
    const data = await response.json()
    if (typeof data.mensaje === 'string' && data.mensaje) return data.mensaje
  } catch {
    // sin cuerpo JSON: se usa el mensaje genérico
  }
  return fallback
}

async function paginaCompraventas(
  response: Response,
  page: number,
  size: number,
  fallback: string,
): Promise<PaginaCompraventas> {
  if (!response.ok) {
    throw new Error(await errorMessageDe(response, fallback))
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

export async function getVentasPorVendedor(
  token: string,
  idVendedor: string,
  page = 0,
  size = 12,
): Promise<PaginaCompraventas> {
  const response = await fetch(
    `/api/compraventas/vendedor/${idVendedor}?page=${page}&size=${size}`,
    { headers: { Authorization: `Bearer ${token}` } },
  )
  return paginaCompraventas(response, page, size, 'No se pudieron cargar las ventas')
}

export async function getComprasPorComprador(
  token: string,
  idComprador: string,
  page = 0,
  size = 12,
): Promise<PaginaCompraventas> {
  const response = await fetch(
    `/api/compraventas/comprador/${idComprador}?page=${page}&size=${size}`,
    { headers: { Authorization: `Bearer ${token}` } },
  )
  return paginaCompraventas(response, page, size, 'No se pudieron cargar las compras')
}

export async function crearCompraventa(
  token: string,
  idProducto: string,
  idComprador: string,
): Promise<void> {
  const response = await fetch('/api/compraventas', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ idProducto, idComprador }),
  })

  if (!response.ok) {
    let mensaje = 'No se pudo completar la compra'
    try {
      const data = await response.json()
      if (typeof data.mensaje === 'string' && data.mensaje) mensaje = data.mensaje
    } catch {
      // sin cuerpo JSON: se usa el mensaje genérico
    }
    throw new Error(mensaje)
  }
}
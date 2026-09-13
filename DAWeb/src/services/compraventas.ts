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
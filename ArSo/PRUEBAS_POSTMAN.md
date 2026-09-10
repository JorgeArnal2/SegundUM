# Guía de Pruebas con Postman - SegundUM

Este proyecto incluye una colección y un entorno de Postman pre-configurados para la validación completa de todos los endpoints de los microservicios.

## Archivos Incluidos
En la raíz del proyecto encontrarás dos archivos:
1. `SegundUM.postman_collection.json` — Colección con 43 peticiones organizadas por servicio
2. `SegundUM.postman_environment.json` — Variables de entorno auto-populadas por los tests

## Requisitos previos

```bash
docker compose up -d --build
```

Esperar ~30 segundos a que todos los servicios estén listos (usuarios, productos, compraventas, valoraciones, pasarela).

## Pasos para la Configuración
1. Abre tu cliente de **Postman**.
2. Haz clic en **Import** (esquina superior izquierda).
3. Arrastra y suelta (o selecciona) ambos archivos `.json` para importarlos.
4. En el selector de Entornos (arriba a la derecha), selecciona **"SegundUM Local"**.

## Ejecución Automática (Collection Runner)

1. Abre la colección **"SegundUM - Pruebas Completas"**
2. Haz clic en **Run** (botón de play en la esquina superior)
3. Asegúrate de que el entorno **"SegundUM Local"** está seleccionado
4. Configura el **delay** entre peticiones a **500ms** (recomendado para dar tiempo a RabbitMQ)
5. Haz clic en **Run SegundUM - Pruebas Completas**

Los 43 tests se ejecutarán en secuencia y las variables se encadenan automáticamente.

## Cobertura de Endpoints

### Servicio Usuarios (puerto 8080, pasarela /api/usuarios)
| # | Método | Endpoint | Test |
|---|--------|----------|------|
| 01 | POST | `/api/usuarios` | Crear usuario vendedor (201 + Location) |
| 02 | POST | `/api/usuarios` | Crear usuario comprador (201 + Location) |
| 03 | POST | `/api/usuarios` | Email duplicado (409 Conflict) |
| 04 | GET | `/api/usuarios` | Listar todos los usuarios (200) |
| 05 | GET | `/api/usuarios/{id}` | Obtener usuario existente (200) |
| 06 | GET | `/api/usuarios/{id}` | Usuario inexistente (404) |
| 10 | PUT | `/api/usuarios/{id}` | Modificar usuario autenticado (200) |
| 11 | GET | `/api/usuarios/{id}` | Verificar modificación (200) |

### Servicio Autenticación (pasarela /auth)
| # | Método | Endpoint | Test |
|---|--------|----------|------|
| 07 | POST | `/auth/login` | Login vendedor (200 + token JWT) |
| 08 | POST | `/auth/login` | Login comprador (200 + token JWT) |
| 09 | POST | `/auth/login` | Credenciales inválidas (401) |
| 43 | POST | `/auth/logout` | Logout (204 No Content) |

### Servicio Categorías (puerto 8081, pasarela /api/categorias)
| # | Método | Endpoint | Test |
|---|--------|----------|------|
| 12 | GET | `/api/categorias` | Listar categorías raíz (200) |
| 13 | GET | `/api/categorias/{id}` | Obtener categoría con subcategorías (200) |
| 14 | GET | `/api/categorias/{id}` | Categoría inexistente (404) |

### Servicio Productos (puerto 8081, pasarela /api/productos)
| # | Método | Endpoint | Test |
|---|--------|----------|------|
| 15 | POST | `/api/productos` | Crear producto (201 + Location) |
| 16 | POST | `/api/productos` | Crear segundo producto (201) |
| 17 | GET | `/api/productos/{id}` | Obtener producto (200) |
| 18 | GET | `/api/productos/{id}` | Producto inexistente (404) |
| 19 | GET | `/api/productos` | Buscar sin filtros (200) |
| 20 | GET | `/api/productos?texto=X` | Buscar con filtro de texto (200) |
| 21 | GET | `/api/productos?precioMaximo=X` | Buscar con filtro de precio (200) |
| 22 | PUT | `/api/productos/{id}` | Modificar producto (200) |
| 23 | GET | `/api/productos/{id}` | Verificar modificación (200) |
| 24 | POST | `/api/productos/{id}/visualizaciones` | Añadir visualización (201) |
| 25 | PUT | `/api/productos/{id}/recogida` | Asignar recogida (200) |
| 26 | GET | `/api/productos/{id}` | Verificar recogida (200) |
| 27 | GET | `/api/productos/historial?mes=X&anio=X` | Historial del mes (200) |
| 28 | GET | `/api/productos?page=X&size=X` | Paginación de productos (200) |

### Servicio Compraventas (puerto 8082, pasarela /api/compraventas)
| # | Método | Endpoint | Test |
|---|--------|----------|------|
| 29 | POST | `/api/compraventas` | Crear compraventa (201 + Location) |
| 30 | GET | `/api/compraventas/{id}` | Obtener compraventa (200) |
| 31 | GET | `/api/compraventas/{id}` | Compraventa inexistente (404) |
| 32 | GET | `/api/compraventas/comprador/{id}` | Compras por comprador, paginado (200) |
| 33 | GET | `/api/compraventas/vendedor/{id}` | Ventas por vendedor, paginado (200) |

### Servicio Valoraciones (puerto 8083, pasarela /api/valoraciones)
| # | Método | Endpoint | Test |
|---|--------|----------|------|
| 35 | POST | `/api/valoraciones/vendedor` | Valorar vendedor (201) |
| 36 | POST | `/api/valoraciones/comprador` | Valorar comprador (201) |
| 37 | GET | `/api/valoraciones/{id}` | Recuperar valoración (200) |
| 38 | GET | `/api/valoraciones/{id}` | Valoración inexistente (404) |
| 39 | GET | `/api/valoraciones/vendedor/{id}` | Consultar valoraciones vendedor, paginado (200) |
| 40 | GET | `/api/valoraciones/comprador/{id}` | Consultar valoraciones comprador, paginado (200) |

### Verificación Asincronía (RabbitMQ)
| # | Método | Endpoint | Test |
|---|--------|----------|------|
| 41 | GET | `/api/usuarios/{id}` | Contadores vendedor actualizados por eventos (200) |
| 42 | GET | `/api/usuarios/{id}` | Contadores comprador actualizados por eventos (200) |

## Variables de Entorno Auto-Populadas

Las siguientes variables se rellenan automáticamente conforme avanzan los tests:

| Variable | Origen | Descripción |
|----------|--------|-------------|
| `vendedorId` | Test 01 (header Location) | ID del usuario vendedor |
| `compradorId` | Test 02 (header Location) | ID del usuario comprador |
| `tokenVendedor` | Test 07 (body.token) | JWT del vendedor |
| `tokenComprador` | Test 08 (body.token) | JWT del comprador |
| `categoriaId` | Test 12 (primera categoría) | ID de una categoría existente |
| `productoId` | Test 15 (header Location) | ID del primer producto |
| `productoId2` | Test 16 (header Location) | ID del segundo producto |
| `compraventaId` | Test 29 (header Location) | ID de la compraventa |
| `valoracionVendedorId` | Test 35 (body.id) | ID de valoración al vendedor |
| `valoracionCompradorId` | Test 36 (body.id) | ID de valoración al comprador |

## Monitoreo Adicional
Para comprobar la asincronía y el correcto procesamiento de eventos:
- Los logs de la aplicación: `docker compose logs -f`
- La interfaz visual de RabbitMQ: `http://localhost:15672/` (user: `guest`, password: `guest`)

# API REST - Microservicio Productos

## Consideraciones generales

- Formato de intercambio: JSON
- Las fechas se representan en formato ISO 8601 (`yyyy-MM-dd` para fechas, `yyyy-MM-ddTHH:mm:ss` para timestamps)
- Los IDs son UUIDs generados automáticamente por el servidor
- Los errores se devuelven con el esquema: `{ "error": "mensaje descriptivo" }`
- Las operaciones de escritura sobre categorías (carga de jerarquía, modificación de descripción) quedan excluidas del API REST; solo se exponen operaciones de consulta

---

## Operaciones sobre Productos

### Crear producto

Crea un nuevo producto en el sistema asociado a un vendedor y una categoría.

- **Método:** POST
- **URL:** `/productos`
- **Cuerpo de la petición:**

```json
{
  "titulo": "Bicicleta de montaña",
  "descripcion": "Bicicleta en buen estado, talla M",
  "precio": 150.0,
  "estado": "BUEN_ESTADO",
  "idCategoria": "cat-001",
  "envioDisponible": true,
  "idVendedor": "550e8400-e29b-41d4-a716-446655440000"
}
```

Los valores posibles de `estado` son: `NUEVO`, `COMO_NUEVO`, `BUEN_ESTADO`, `ACEPTABLE`, `PARA_PIEZAS_O_REPARAR`.

- **Retorno:**
  - `201 Created`
  - Cabecera `Location: /productos/{id}` con la URL del nuevo recurso
- **Errores:**
  - `400 Bad Request` — algún campo obligatorio está ausente, el precio es menor o igual a 0, o el estado no es válido
  - `404 Not Found` — no existe la categoría o el vendedor indicados

---

### Obtener producto

Recupera los datos completos de un producto a partir de su identificador.

- **Método:** GET
- **URL:** `/productos/{id}`
- **Retorno:**
  - `200 OK`

```json
{
  "id": "prod-001",
  "titulo": "Bicicleta de montaña",
  "descripcion": "Bicicleta en buen estado, talla M",
  "precio": 150.0,
  "estado": "BUEN_ESTADO",
  "fechaPublicacion": "2026-03-10T10:30:00",
  "visualizaciones": 5,
  "envioDisponible": true,
  "recogida": {
    "descripcion": "Plaza Mayor, Madrid",
    "longitud": -3.7037,
    "latitud": 40.4168
  },
  "categoria": {
    "id": "cat-001",
    "nombre": "Deportes"
  },
  "vendedor": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "Ana",
    "apellidos": "García López",
    "email": "ana.garcia@email.com"
  }
}
```

- **Errores:**
  - `404 Not Found` — no existe un producto con ese identificador

---

### Buscar productos

Recupera una lista de productos aplicando filtros opcionales. Si no se indica ningún parámetro, devuelve todos los productos.

- **Método:** GET
- **URL:** `/productos`
- **Parámetros de consulta (todos opcionales):**
  - `categoria` — ID de categoría; busca en esa categoría y todas sus subcategorías
  - `texto` — texto a buscar en el título o descripción
  - `estado` — estado del producto (`NUEVO`, `COMO_NUEVO`, etc.)
  - `precioMaximo` — precio máximo (inclusive)
- **Ejemplo:** `GET /productos?categoria=cat-001&estado=NUEVO&precioMaximo=200`
- **Retorno:**
  - `200 OK` + lista de productos (mismo formato que obtener producto, puede estar vacía)

---

### Modificar producto

Actualiza la descripción y/o el precio de un producto existente.

- **Método:** PUT
- **URL:** `/productos/{id}`
- **Cuerpo de la petición:**

```json
{
  "descripcion": "Bicicleta revisada recientemente",
  "precio": 130.0
}
```

- **Retorno:**
  - `200 OK`
- **Errores:**
  - `404 Not Found` — no existe un producto con ese identificador

---

### Añadir visualización

Registra una visualización del producto (incrementa el contador de vistas).

- **Método:** POST
- **URL:** `/productos/{id}/visualizaciones`
- **Cuerpo de la petición:** vacío
- **Retorno:**
  - `201 Created`
- **Errores:**
  - `404 Not Found` — no existe un producto con ese identificador

---

### Asignar lugar de recogida

Establece o actualiza el lugar de recogida de un producto.

- **Método:** PUT
- **URL:** `/productos/{id}/recogida`
- **Cuerpo de la petición:**

```json
{
  "descripcion": "Plaza Mayor, Madrid",
  "longitud": -3.7037,
  "latitud": 40.4168
}
```

- **Retorno:**
  - `200 OK`
- **Errores:**
  - `404 Not Found` — no existe un producto con ese identificador

---

### Historial del mes

Recupera los productos publicados en un mes y año concretos, ordenados por número de visualizaciones de mayor a menor. Devuelve un resumen de cada producto.

- **Método:** GET
- **URL:** `/productos/historial`
- **Parámetros de consulta (obligatorios):**
  - `mes` — número de mes (1-12)
  - `anio` — año (p. ej. 2026)
- **Ejemplo:** `GET /productos/historial?mes=3&anio=2026`
- **Retorno:**
  - `200 OK`

```json
[
  {
    "id": "prod-001",
    "titulo": "Bicicleta de montaña",
    "precio": 150.0,
    "fechaAlta": "2026-03-10T10:30:00"
  }
]
```

- **Errores:**
  - `400 Bad Request` — falta el parámetro `mes` o `anio`, o tienen un valor fuera de rango

---

### Obtener productos de un vendedor

Recupera todos los productos publicados por un vendedor concreto.

- **Método:** GET
- **URL:** `/usuarios/{id}/productos`
- **Retorno:**
  - `200 OK` + lista de productos del vendedor (mismo formato que obtener producto, puede estar vacía)
- **Errores:**
  - `404 Not Found` — no existe un usuario/vendedor con ese identificador

---

## Operaciones sobre Categorías (solo consulta)

### Listar categorías raíz

Recupera la lista de categorías de primer nivel (sin categoría padre).

- **Método:** GET
- **URL:** `/categorias`
- **Retorno:**
  - `200 OK`

```json
[
  {
    "id": "cat-001",
    "nombre": "Deportes",
    "descripcion": "Artículos deportivos",
    "ruta": "/deportes"
  }
]
```

---

### Obtener categoría

Recupera los datos de una categoría junto con sus subcategorías directas.

- **Método:** GET
- **URL:** `/categorias/{id}`
- **Retorno:**
  - `200 OK`

```json
{
  "id": "cat-001",
  "nombre": "Deportes",
  "descripcion": "Artículos deportivos",
  "ruta": "/deportes",
  "subcategorias": [
    {
      "id": "cat-002",
      "nombre": "Ciclismo",
      "descripcion": "Material de ciclismo",
      "ruta": "/deportes/ciclismo"
    }
  ]
}
```

- **Errores:**
  - `404 Not Found` — no existe una categoría con ese identificador

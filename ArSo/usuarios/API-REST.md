# API REST - Microservicio Usuarios

## Consideraciones generales

- Formato de intercambio: JSON
- Las fechas se representan en formato ISO 8601 (`yyyy-MM-dd`)
- Los IDs son UUIDs generados automáticamente por el servidor
- La clave (contraseña) nunca se incluye en las respuestas
- Los errores se devuelven con el esquema: `{ "error": "mensaje descriptivo" }`

---

## Operaciones

### Registrar usuario

Crea un nuevo usuario en el sistema.

- **Método:** POST
- **URL:** `/usuarios`
- **Cuerpo de la petición:**

```json
{
  "nombre": "Ana",
  "apellidos": "García López",
  "email": "ana.garcia@email.com",
  "clave": "1234",
  "fechaNacimiento": "1990-05-15",
  "telefono": "600000001"
}
```

- **Retorno:**
  - `201 Created`
  - Cabecera `Location: /usuarios/{id}` con la URL del nuevo recurso
- **Errores:**
  - `400 Bad Request` — algún campo obligatorio está ausente o vacío
  - `409 Conflict` — ya existe un usuario con ese email

---

### Obtener usuario

Recupera los datos de un usuario a partir de su identificador.

- **Método:** GET
- **URL:** `/usuarios/{id}`
- **Retorno:**
  - `200 OK`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nombre": "Ana",
  "apellidos": "García López",
  "email": "ana.garcia@email.com",
  "fechaNacimiento": "1990-05-15",
  "telefono": "600000001",
  "administrador": false,
  "contadorCompras": 0,
  "contadorVentas": 0,
  "numeroValoracionesComoComprador": 0,
  "numeroValoracionesComoVendedor": 0,
  "valoracionMediaComoComprador": 0.0,
  "valoracionMediaComoVendedor": 0.0
}
```

- **Errores:**
  - `404 Not Found` — no existe un usuario con ese identificador

---

### Listar usuarios

Recupera la lista de todos los usuarios registrados en el sistema.

- **Método:** GET
- **URL:** `/usuarios`
- **Retorno:**
  - `200 OK`

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "Ana",
    "apellidos": "García López",
    "email": "ana.garcia@email.com",
    "contadorCompras": 0,
    "contadorVentas": 0,
    "numeroValoracionesComoComprador": 0,
    "numeroValoracionesComoVendedor": 0,
    "valoracionMediaComoComprador": 0.0,
    "valoracionMediaComoVendedor": 0.0,
    "href": "http://localhost:8080/api/usuarios/550e8400-e29b-41d4-a716-446655440000"
  }
]
```

---

### Modificar usuario

Actualiza los datos de un usuario existente.

- **Método:** PUT
- **URL:** `/usuarios/{id}`
- **Cuerpo de la petición:**

```json
{
  "nombre": "Ana",
  "apellidos": "García López",
  "clave": "nueva_clave",
  "fechaNacimiento": "1990-05-15",
  "telefono": "600000002"
}
```

- **Retorno:**
  - `200 OK`
- **Errores:**
  - `400 Bad Request` — algún campo obligatorio está ausente o vacío
  - `404 Not Found` — no existe un usuario con ese identificador

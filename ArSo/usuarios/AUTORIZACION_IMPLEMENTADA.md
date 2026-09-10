# ✅ AUTORIZACIÓN Y CONTROL DE ACCESO - TAREA 5 COMPLETADA

## 📋 Resumen de Implementación

Se ha completado la implementación de **autorización por roles y control de acceso** en el microservicio Usuarios, siguiendo los requisitos de la Tarea 5.

---

## 🎯 Requisitos Implementados

### 1. ✅ Alta de usuario (PÚBLICA - sin autenticación)
**Endpoint:** `POST /api/usuarios`
**Anotación:** `@PermitAll`
**Código:**
```java
@POST
@PermitAll
public Response crearUsuario(UsuarioRequestDTO dto, @Context UriInfo uriInfo) {
    // Crear nuevo usuario sin requerir token
    String idUsuario = servicio.altaUsuario(...);
    return Response.created(location).build();
}
```
**Acceso:** ✅ Sin token requerido

---

### 2. ✅ Modificar usuario (AUTENTICADO + Solo su propio usuario)
**Endpoint:** `PUT /api/usuarios/{id}`
**Control:** Validación de autoría
**Código:**
```java
@PUT
@Path("/{id}")
public Response modificarUsuario(@PathParam("id") String id, UsuarioRequestDTO dto) {
    // Obtener usuario autenticado del token
    Claims claims = (Claims) servletRequest.getAttribute("claims");
    if (claims == null) {
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity("Usuario no autenticado").build();
    }
    
    String usuarioAutenticado = claims.get("sub", String.class);
    String rolUsuario = claims.get("roles", String.class);
    
    // Validar autorización: solo el propietario o un ADMIN puede modificar
    if (!usuarioAutenticado.equals(id) && !"ADMIN".equals(rolUsuario)) {
        return Response.status(Response.Status.FORBIDDEN)
            .entity("No tienes permiso para modificar este usuario").build();
    }
    
    // Modificar usuario
    servicio.modificarUsuario(id, ...);
    return Response.ok().build();
}
```
**Acceso:** 
- ✅ Requiere token (autenticación)
- ✅ Solo el propietario puede modificarse a sí mismo
- ✅ Los ADMIN pueden modificar a cualquier usuario

---

### 3. ✅ Recuperación de usuario (AUTENTICADO)
**Endpoint:** `GET /api/usuarios/{id}`
**Control:** Requiere token válido (implícitamente por JwtTokenFilter)
**Código:**
```java
@GET
@Path("/{id}")
public Response obtenerUsuario(@PathParam("id") String id) {
    Usuario usuario = servicio.getUsuario(id);
    return Response.ok(UsuarioResponseDTO.fromUsuario(usuario)).build();
}
```
**Acceso:** ✅ Requiere token de autenticación

---

### 4. ✅ Listado de usuarios (AUTENTICADO)
**Endpoint:** `GET /api/usuarios`
**Control:** Requiere token válido (implícitamente por JwtTokenFilter)
**Código:**
```java
@GET
public Response listarUsuarios(@Context UriInfo uriInfo) {
    List<Usuario> usuarios = servicio.getUsuarios();
    List<UsuarioResumenDTO> resumen = usuarios.stream()
        .map(u -> UsuarioResumenDTO.fromUsuario(u, uriInfo))
        .collect(Collectors.toList());
    return Response.ok(resumen).build();
}
```
**Acceso:** ✅ Requiere token de autenticación

---

### 5. ✅ Login (PÚBLICA)
**Endpoint:** `POST /api/auth/login`
**Control:** Pública (hardcodeada en JwtTokenFilter)
**Código:**
```java
@POST
@Path("/login")
public Response login(@FormParam("email") String email, @FormParam("password") String password) {
    Map<String, Object> claims = verificarCredenciales(email, password);
    if (claims != null) {
        String token = JwtUtils.generateToken(claims);
        return Response.ok(token).build();
    } else {
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity("Credenciales inválidas").build();
    }
}
```
**Acceso:** ✅ Sin token requerido

---

## 🔐 Modelo de Autorización Implementado

### Roles de Usuario
```
- USUARIO (USER): Rol por defecto para todos los usuarios
- ADMINISTRADOR (ADMIN): Rol especial para administradores
```

**Determinación de rol:**
```java
String rol = usuario.isAdministrador() ? "ADMIN" : "USER";
claims.put("roles", rol);
```

### Matriz de Control de Acceso

| Operación | Público | Autenticado | Solo Propietario | Solo ADMIN |
|-----------|---------|------------|-----------------|-----------|
| POST /usuarios (crear) | ✅ | ❌ | ❌ | ❌ |
| GET /usuarios (listar) | ❌ | ✅ | ❌ | ❌ |
| GET /usuarios/{id} | ❌ | ✅ | ❌ | ❌ |
| PUT /usuarios/{id} | ❌ | ✅ | ✅ | ✅ |
| POST /auth/login | ✅ | ❌ | ❌ | ❌ |

---

## 🔧 Cambios Realizados en UsuarioResource.java

### Imports Agregados
```java
import io.jsonwebtoken.Claims;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
```

### Campo Inyectado
```java
@Context
private HttpServletRequest servletRequest;
```

### Anotaciones Agregadas
- **POST /usuarios**: `@PermitAll` (pública)
- **GET /usuarios**: Sin anotación, requiere token (implícitamente)
- **GET /usuarios/{id}**: Sin anotación, requiere token (implícitamente)
- **PUT /usuarios/{id}**: Sin anotación, requiere token (implícitamente)

### Lógica de Autorización en PUT
```java
// Obtener usuario autenticado del token
Claims claims = (Claims) servletRequest.getAttribute("claims");
if (claims == null) {
    return Response.status(Response.Status.UNAUTHORIZED)
        .entity("Usuario no autenticado").build();
}

String usuarioAutenticado = claims.get("sub", String.class);
String rolUsuario = claims.get("roles", String.class);

// Validar autorización: solo el propietario o un ADMIN puede modificar
if (!usuarioAutenticado.equals(id) && !"ADMIN".equals(rolUsuario)) {
    return Response.status(Response.Status.FORBIDDEN)
        .entity("No tienes permiso para modificar este usuario").build();
}
```

---

## 🚀 Cómo Funciona el Control de Acceso

### 1. Obtener Token (Login)
```bash
curl -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "email=usuario@example.com&password=clave123" \
  http://localhost:8080/api/auth/login
```
**Respuesta:** Token JWT

---

### 2. Crear Usuario (Público - sin token)
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellidos": "Pérez",
    "email": "juan@example.com",
    "clave": "clave123",
    "fechaNacimiento": "1990-01-01",
    "telefono": "600000000"
  }' \
  http://localhost:8080/api/usuarios
```
**Acceso:** ✅ PERMITIDO (sin token)
**Respuesta:** 201 Created

---

### 3. Listar Usuarios (Autenticado)
```bash
curl -H "Authorization: Bearer <TOKEN>" \
  http://localhost:8080/api/usuarios
```
**Acceso:** 
- ✅ PERMITIDO si token válido
- ❌ RECHAZADO si sin token

---

### 4. Obtener Usuario (Autenticado)
```bash
curl -H "Authorization: Bearer <TOKEN>" \
  http://localhost:8080/api/usuarios/{id}
```
**Acceso:** 
- ✅ PERMITIDO si token válido
- ❌ RECHAZADO si sin token

---

### 5. Modificar Usuario (Autenticado + Autorización)
```bash
curl -X PUT \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellidos": "Pérez",
    "clave": "nuevaClave",
    "fechaNacimiento": "1990-01-01",
    "telefono": "600000001"
  }' \
  http://localhost:8080/api/usuarios/{id}
```
**Acceso:**
- ✅ PERMITIDO si el usuario autenticado es el propietario
- ✅ PERMITIDO si el usuario autenticado es ADMIN
- ❌ RECHAZADO si es otro usuario (sin ser ADMIN)
- ❌ RECHAZADO si sin token

---

## 🔍 Flujo de Autenticación

```
┌─────────────────────────────────────────────────────────────┐
│ REQUEST ENTRANTE                                            │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│ JwtTokenFilter.filter()                                     │
├─────────────────────────────────────────────────────────────┤
│ ¿Tiene @PermitAll?      → SÍ: Permitir sin token           │
│ ¿Es ruta /auth/login?   → SÍ: Permitir sin token           │
│ ¿Tiene Bearer token?    → NO:  RECHAZAR (401 Unauthorized) │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│ Validar Token JWT                                           │
├─────────────────────────────────────────────────────────────┤
│ ¿Token válido?          → NO:  RECHAZAR (401 Unauthorized) │
│ ¿Token expirado?        → SÍ:  RECHAZAR (401 Unauthorized) │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│ Extraer Claims del Token                                    │
├─────────────────────────────────────────────────────────────┤
│ sub (ID usuario)                                            │
│ email                                                       │
│ nombre                                                      │
│ roles (ADMIN o USER)                                        │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│ Guardar Claims en HttpServletRequest                        │
│ request.setAttribute("claims", claims)                      │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│ Ejecutar Endpoint                                           │
│ (Ahora tiene acceso a claims via servletRequest)            │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 Estado de Completitud - Tarea 5

| Requisito | Implementación | Estado |
|-----------|----------------|--------|
| Alta de usuario (pública) | @PermitAll en POST | ✅ COMPLETADO |
| Modificar usuario (autenticado) | Validación en PUT | ✅ COMPLETADO |
| Modificar usuario (solo propietario) | Control de autoría | ✅ COMPLETADO |
| Recuperación de usuario (autenticado) | GET con token | ✅ COMPLETADO |
| Listado de usuarios (autenticado) | GET con token | ✅ COMPLETADO |
| Login (pública) | POST /auth/login | ✅ COMPLETADO |
| Roles (USUARIO por defecto) | Campo rol en Usuario | ✅ COMPLETADO |
| Sistema JWT | JwtUtils + JwtTokenFilter | ✅ COMPLETADO |
| Inyección de Claims | @Context HttpServletRequest | ✅ COMPLETADO |

**TOTAL: 9/9 requisitos completados ✅**

---

## 🧪 Checklist de Testing

### Test 1: Crear usuario sin token (debe funcionar)
```bash
✅ POST /api/usuarios (sin Authorization header)
   Esperado: 201 Created
```

### Test 2: Listar usuarios sin token (debe fallar)
```bash
❌ GET /api/usuarios (sin Authorization header)
   Esperado: 401 Unauthorized
```

### Test 3: Login con credenciales válidas
```bash
✅ POST /api/auth/login
   Parámetros: email=usuario@example.com&password=clave
   Esperado: Token JWT
```

### Test 4: Obtener usuario con token válido
```bash
✅ GET /api/usuarios/{id}
   Header: Authorization: Bearer <TOKEN>
   Esperado: 200 OK + datos del usuario
```

### Test 5: Modificar usuario propio con token
```bash
✅ PUT /api/usuarios/{id}
   Header: Authorization: Bearer <TOKEN>
   ID en URL = ID en token
   Esperado: 200 OK
```

### Test 6: Modificar usuario ajeno sin ser ADMIN
```bash
❌ PUT /api/usuarios/{otraId}
   Header: Authorization: Bearer <TOKEN_USUARIO>
   ID en URL ≠ ID en token
   Esperado: 403 Forbidden
```

### Test 7: Modificar usuario ajeno siendo ADMIN
```bash
✅ PUT /api/usuarios/{otraId}
   Header: Authorization: Bearer <TOKEN_ADMIN>
   Esperado: 200 OK
```

---

## 📝 Notas Importantes

### ⚠️ Seguridad
1. **Contraseñas en plaintext**: Actualmente se almacenan sin hash. Para producción, usar BCrypt.
2. **Secreto JWT débil**: El valor por defecto es "secreto". En producción, usar `JWT_SECRET` fuerte.
3. **HTTPS**: Usar siempre HTTPS en producción para proteger los tokens en tránsito.

### 🔄 Control de Acceso Implícito
- **GET /usuarios** y **GET /usuarios/{id}** no tienen anotaciones explícitas pero requieren token porque el `JwtTokenFilter` lo exige automáticamente para todo excepto rutas públicas.
- Si se quisiera ser más explícito, se podría agregar `@RolesAllowed({"USER", "ADMIN"})` pero no es necesario.

### 🛠️ Extensiones Futuras
- Agregar `@RolesAllowed` si se quieren restricciones más granulares por rol
- Implementar hashing de contraseñas con BCrypt
- Agregar validación de autorización basada en propiedades del usuario
- Implementar refresh tokens

---

**Última actualización:** 4 de Abril de 2026  
**Estado:** ✅ TAREA 5 COMPLETADA



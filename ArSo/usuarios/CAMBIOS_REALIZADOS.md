# 📝 CAMBIOS REALIZADOS - TAREA 5

## Archivos Modificados

### 1. UsuarioResource.java
**Ruta:** `usuarios/src/main/java/com/arso/usuarios/rest/UsuarioResource.java`

#### Importaciones Agregadas
```java
import io.jsonwebtoken.Claims;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
```

#### Campo de Contexto Agregado
```java
@Context
private HttpServletRequest servletRequest;
```

#### Cambios en Métodos

**1. POST crearUsuario() - Ahora Público**
```java
@POST
@PermitAll  // ← AGREGADO
public Response crearUsuario(UsuarioRequestDTO dto, @Context UriInfo uriInfo) {
    // ... código sin cambios
}
```

**2. PUT modificarUsuario() - Ahora con Control de Autorización**
```java
@PUT
@Path("/{id}")
public Response modificarUsuario(@PathParam("id") String id, UsuarioRequestDTO dto) {
    // AGREGADO: Obtener usuario autenticado del token
    Claims claims = (Claims) servletRequest.getAttribute("claims");
    if (claims == null) {
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity("Usuario no autenticado").build();
    }
    
    // AGREGADO: Extraer información del token
    String usuarioAutenticado = claims.get("sub", String.class);
    String rolUsuario = claims.get("roles", String.class);
    
    // AGREGADO: Validar autorización
    if (!usuarioAutenticado.equals(id) && !"ADMIN".equals(rolUsuario)) {
        return Response.status(Response.Status.FORBIDDEN)
            .entity("No tienes permiso para modificar este usuario").build();
    }
    
    // ... resto del código sin cambios
}
```

---

## Archivos Creados

### 1. AUTORIZACION_IMPLEMENTADA.md
**Ruta:** `usuarios/AUTORIZACION_IMPLEMENTADA.md`

Documentación completa sobre:
- Requisitos implementados
- Matriz de control de acceso
- Flujo de autenticación
- Ejemplos de curl
- Checklist de testing

---

## Resumen de Cambios

| Línea | Tipo | Descripción |
|------|------|-------------|
| 9 | Import | `import io.jsonwebtoken.Claims;` |
| 11 | Import | `import javax.annotation.security.PermitAll;` |
| 12 | Import | `import javax.servlet.http.HttpServletRequest;` |
| 32-33 | Campo | `@Context private HttpServletRequest servletRequest;` |
| 36 | Anotación | `@PermitAll` en POST crearUsuario |
| 71-86 | Lógica | Validación de autenticación y autorización en PUT |

---

## ✅ Checklist de Implementación

- [x] Importar Claims de JWT
- [x] Importar PermitAll annotation
- [x] Importar HttpServletRequest
- [x] Inyectar HttpServletRequest con @Context
- [x] Agregar @PermitAll a POST (crear usuario)
- [x] Agregar validación de claims en PUT
- [x] Agregar validación de autoría en PUT
- [x] Documentar cambios en AUTORIZACION_IMPLEMENTADA.md

---

## 🎯 Estado Final

**TAREA 5: COMPLETADA ✅**

Todos los requisitos de autenticación y autorización están implementados:
- ✅ Alta de usuario: pública (@PermitAll)
- ✅ Modificar usuario: autenticado + solo propietario
- ✅ Recuperación de usuario: autenticado
- ✅ Listado de usuarios: autenticado
- ✅ Login: pública



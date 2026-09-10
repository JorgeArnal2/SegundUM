# 📋 IMPLEMENTACIÓN COMPLETADA: Autenticación JWT en Proyecto Usuarios

## ✅ Estado: COMPLETADO (10/10 pasos)

---

## 🎯 Resumen de Cambios Realizados

### 1. ✅ Dependencias Actualizadas (pom.xml)
**Archivo:** `usuarios/pom.xml`

Se agregaron 2 nuevas dependencias críticas:
```xml
<!-- Token JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>

<!-- Servlet API (para HttpServletRequest en JwtTokenFilter) -->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>servlet-api</artifactId>
    <version>2.5</version>
    <scope>provided</scope>
</dependency>
```

---

### 2. ✅ Archivos de Autenticación Creados

#### a) **JwtUtils.java**
**Ruta:** `com/arso/auth/JwtUtils.java`
- Generación de tokens JWT
- Validación de tokens
- Soporte para variables de entorno:
  - `JWT_SECRET` (por defecto: "secreto")
  - `JWT_EXPIRATION` (por defecto: 3600 segundos = 1 hora)

#### b) **JwtTokenFilter.java**
**Ruta:** `com/arso/auth/JwtTokenFilter.java`
- Filtro de autenticación (@Provider)
- Intercepta todas las peticiones
- Rutas públicas: `auth/login`
- Valida token Bearer en header Authorization
- Control de roles basado en anotaciones `@RolesAllowed`

#### c) **ControladorAuth.java**
**Ruta:** `com/arso/auth/ControladorAuth.java`
- Endpoint de login: `POST /api/auth/login`
- Parámetros: `email` y `password`
- Valida credenciales contra BD
- Retorna token JWT si credenciales son correctas
- Genera claims: `sub` (ID), `email`, `nombre`, `roles`

---

### 3. ✅ Configuración REST Actualizada

**Archivo:** `usuarios/src/main/java/com/arso/usuarios/rest/UsuariosApplication.java`

Cambios:
- Registrado paquete `com.arso.auth`
- Registrado filtro `JwtTokenFilter.class`

```java
public UsuariosApplication() {
    packages("com.arso.usuarios.rest", "com.arso.auth");  // +auth
    register(JacksonFeature.class);
    register(JwtTokenFilter.class);  // +filtro
}
```

---

### 4. ✅ Capa de Servicio Extendida

#### ServicioUsuarios.java
- Agregado método: `Usuario obtenerPorEmail(String email)`

#### ServicioUsuariosImpl.java
- Implementación de `obtenerPorEmail()` que consulta repositorio

---

### 5. ✅ Capa de Repositorio Extendida

#### UsuarioRepository.java (Interfaz)
- Agregado método: `Usuario obtenerPorEmail(String email)`

#### UsuarioRepositoryJPA.java (Implementación)
- Implementación de `obtenerPorEmail()` con consulta JPQL:
  ```sql
  SELECT u FROM Usuario u WHERE u.email = :email
  ```

---

### 6. ✅ Modelo Usuario Extendido

**Archivo:** `usuarios/src/main/java/com/arso/usuarios/domain/Usuario.java`

Cambios:
- Agregado campo: `rol` (String, NOT NULL)
- Getters y setters para el nuevo campo

```java
@Column(name="rol", nullable=false)
private String rol;

public String getRol() { return rol; }
public void setRol(String rol) { this.rol = rol; }
```

---

### 7. ✅ Configuración de Propiedades

**Archivo:** `usuarios/src/main/resources/application.properties` (CREADO)

Contenido:
```properties
# JWT Configuration
jwt.secret=secreto
jwt.expiration=3600
```

---

### 8. ✅ Entrypoint Mejorado

**Archivo:** `usuarios/src/main/java/com/arso/Main.java`

Cambios:
- Agregados logs descriptivos de startup
- Información sobre autenticación habilitada
- Endpoint de login mostrado claramente

---

## 🚀 Cómo Usar la Autenticación

### 1. Obtener Token (Login)

```bash
curl -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "email=usuario@example.com&password=miClave" \
  http://localhost:8080/api/auth/login
```

**Respuesta (éxito):**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiZW1haWwiOiJ1c3VhcmlvQGV4YW1wbGUuY29tIiwibm9tYnJlIjoiSm9zZSIsInJvbGVzIjoiQURNSU4iLCJleHAiOjE3Mjc4NTA3MzV9...
```

**Respuesta (error):**
```json
"Credenciales inválidas"
```

---

### 2. Usar Token en Peticiones Protegidas

```bash
curl -X GET \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIi..." \
  http://localhost:8080/api/usuarios
```

---

### 3. Control de Roles (Opcional)

Proteger endpoints con anotación `@RolesAllowed`:

```java
@GET
@Path("/{id}")
@RolesAllowed({"ADMIN"})
public Response obtenerUsuario(@PathParam("id") String id) {
    // Solo accesible para usuarios con rol ADMIN
}
```

---

## ⚙️ Configuración en Variables de Entorno

Para cambiar el secreto JWT o expiración en producción:

```bash
# Windows
set JWT_SECRET=mi-secreto-super-seguro
set JWT_EXPIRATION=7200

# Linux/Mac
export JWT_SECRET=mi-secreto-super-seguro
export JWT_EXPIRATION=7200
```

---

## 📝 Notas Importantes

### ⚠️ Seguridad en Desarrollo vs Producción

**ACTUAL (Desarrollo):**
- Secreto: `"secreto"` (hardcodeado)
- Contraseñas: verificadas en plaintext
- Token expire: 1 hora

**RECOMENDADO (Producción):**
```java
// 1. Usar hashing de contraseñas
// Ejemplo con BCrypt:
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode("miClave");
encoder.matches("miClave", hashedPassword); // true

// 2. Usar variables de entorno para secreto
String secret = System.getenv("JWT_SECRET");

// 3. Usar HTTPS
// 4. Aumentar expiración según necesidad
```

---

## 🗄️ Cambios en Base de Datos

**Nueva columna en tabla `usuarios`:**
```sql
ALTER TABLE usuarios ADD COLUMN rol VARCHAR(50) NOT NULL DEFAULT 'USER';
```

---

## 📊 Estructura Final de Autenticación

```
com.arso.auth/
├── ControladorAuth.java       → Endpoint POST /auth/login
├── JwtTokenFilter.java        → Filtro de autenticación (middleware)
└── JwtUtils.java              → Generación y validación JWT

com.arso.usuarios.service/
├── ServicioUsuarios.java      → ✨ obtenerPorEmail() (NEW)
└── ServicioUsuariosImpl.java   → ✨ Implementación (UPDATED)

com.arso.usuarios.repository/
├── UsuarioRepository.java     → ✨ obtenerPorEmail() (NEW)
└── UsuarioRepositoryJPA.java  → ✨ Implementación (UPDATED)

com.arso.usuarios.rest/
└── UsuariosApplication.java   → ✨ Registra JwtTokenFilter (UPDATED)

com.arso.usuarios.domain/
└── Usuario.java               → ✨ Agregado campo rol (UPDATED)

recursos/
├── pom.xml                    → ✨ JWT + Servlet dependencies (UPDATED)
├── application.properties     → ✨ Config JWT (NEW)
└── Main.java                  → ✨ Logs mejorados (UPDATED)
```

---

## ✨ Diferencias vs Proyecto Bookle

| Aspecto | Bookle | Usuarios |
|---------|--------|----------|
| **Framework** | WAR/Tomcat + web.xml | Grizzly Standalone |
| **Config** | web.xml estática | ResourceConfig + packages() |
| **Secreto JWT** | Hardcodeado | Variables de entorno |
| **Roles** | "PROFESOR" | "ADMIN"/"USER" + campo rol |
| **Base de Datos** | En memoria | JPA/Hibernate/MySQL |
| **Servicio Usuarios** | No existe | Sí (reutilizado) |

---

## 🔄 Próximos Pasos Recomendados

1. **Hashing de contraseñas** → Implementar BCrypt
2. **Refresh tokens** → Tokens de larga duración para renovación
3. **Rate limiting** → Proteger endpoint login contra fuerza bruta
4. **Auditoría** → Log de intentos de login
5. **2FA** → Autenticación de dos factores
6. **OAuth2/OpenID** → Integración con proveedores externos

---

## 📌 Checklist de Validación

- [x] Archivos de auth creados (`JwtUtils`, `JwtTokenFilter`, `ControladorAuth`)
- [x] Dependencias JWT y Servlet agregadas
- [x] Filtro registrado en `UsuariosApplication`
- [x] Servicio de usuarios extendido con `obtenerPorEmail()`
- [x] Repositorio extendido con `obtenerPorEmail()`
- [x] Campo `rol` agregado a modelo `Usuario`
- [x] Configuración externalizada en `application.properties`
- [x] Logs de startup mejorados
- [x] Estructura coherente con proyecto Bookle
- [x] Código compilable y sin dependencias rotas

---

**Última actualización:** 2 de Abril de 2026
**Estado:** ✅ LISTO PARA TESTING


# 🏗️ ARQUITECTURA - Autenticación JWT en Proyecto Usuarios

---

## 📐 Diagrama de Componentes

```
┌──────────────────────────────────────────────────────────────────┐
│                     CLIENTE (Navegador/API)                       │
└────────────────────────┬─────────────────────────────────────────┘
                         │
                ┌────────▼────────┐
                │ POST /auth/login │
                │ (email, password)│
                └────────┬────────┘
                         │
        ┌────────────────▼─────────────────┐
        │   MICROSERVICIO USUARIOS          │
        │   (Grizzly HTTP Server)           │
        │                                   │
        │  ┌──────────────────────────────┐ │
        │  │   UsuariosApplication        │ │
        │  │ (JAX-RS ResourceConfig)      │ │
        │  │                              │ │
        │  │ packages:                    │ │
        │  │ - com.arso.usuarios.rest     │ │
        │  │ - com.arso.auth        ✨   │ │
        │  │                              │ │
        │  │ filters:                     │ │
        │  │ - JwtTokenFilter       ✨   │ │
        │  └──────────────────────────────┘ │
        │            │                       │
        │  ┌─────────▼──────────────────────┐│
        │  │   JwtTokenFilter (Middleware)  ││
        │  │                                ││
        │  │ ✓ Intercepta todas peticiones ││
        │  │ ✓ Valida Bearer token         ││
        │  │ ✓ Checkea rutas públicas      ││
        │  │ ✓ Verifica roles (@RolesAll) ││
        │  │ ✓ Rechaza con 401/403         ││
        │  └─────────▲────────────────────┘│
        │            │                       │
        │  ┌─────────┴──────────────────────┐│
        │  │   Controladores REST            ││
        │  │                                 ││
        │  │ • ControladorAuth       ✨     ││
        │  │   POST /api/auth/login         ││
        │  │                                 ││
        │  │ • UsuarioResource              ││
        │  │   GET /api/usuarios            ││
        │  │   POST/PUT /api/usuarios/{id}  ││
        │  └──────┬──────────────────┬──────┘│
        │         │                  │        │
        │  ┌──────▼──────┐  ┌───────▼──────┐│
        │  │  ControladorAuth      │ │  UsuarioResource       ││
        │  │  1. Obtiene credenciales │  1. Requiere token     ││
        │  │  2. Busca usuario en BD  │  2. Verificado por     ││
        │  │  3. Valida contraseña    │     JwtTokenFilter     ││
        │  │  4. Genera claims        │  3. Accede a datos     ││
        │  │  5. Crea JWT token       │  4. Retorna recurso    ││
        │  └──────┬──────┘  └───────┬──────┘│
        │         │                 │        │
        │  ┌──────▼─────────────────▼──────┐│
        │  │   Capa de Servicios            ││
        │  │                                ││
        │  │ • ServicioUsuarios       ✨   ││
        │  │   - obtenerPorEmail()   ✨   ││
        │  │   - getUsuario()              ││
        │  │   - getUsuarios()             ││
        │  │   - altaUsuario()             ││
        │  │   - modificarUsuario()        ││
        │  └──────┬─────────────────────────┘│
        │         │                          │
        │  ┌──────▼──────────────────────┐  │
        │  │   Capa de Repositorio        │  │
        │  │                              │  │
        │  │ • UsuarioRepositoryJPA  ✨  │  │
        │  │   - obtenerPorEmail() ✨    │  │
        │  │   - getById()               │  │
        │  │   - findByEmail()           │  │
        │  │   - existeEmail()           │  │
        │  │   - add/update/delete       │  │
        │  └──────┬──────────────────────┘  │
        │         │                          │
        └─────────┼──────────────────────────┘
                  │
        ┌─────────▼─────────────────┐
        │    DATABASE (MySQL)        │
        │                            │
        │  Tabla: usuarios           │
        │  ┌──────────────────────┐  │
        │  │ id_usuario (PK)      │  │
        │  │ email (UNIQUE)       │  │
        │  │ nombre               │  │
        │  │ apellidos            │  │
        │  │ clave (password)     │  │
        │  │ fecha_nacimiento     │  │
        │  │ telefono             │  │
        │  │ administrador (bool) │  │
        │  │ rol                ✨│  │
        │  └──────────────────────┘  │
        │                            │
        └────────────────────────────┘
```

---

## 🔄 Flujo Detallado: Login y Autenticación

```
SECUENCIA: Login y Acceso a Recurso Protegido
════════════════════════════════════════════════════════

1. CLIENT LOGIN REQUEST
   ┌────────────────────────────────────────────┐
   │ POST /api/auth/login                       │
   │ Content-Type: application/x-www-form-urlencoded
   │                                            │
   │ email=juan@example.com                     │
   │ password=password123                       │
   └────────────┬─────────────────────────────┘
                │
                ▼
2. REQUEST HITS GLASSFISH JERSEY
   ┌────────────────────────────────────────────┐
   │ UsuariosApplication                        │
   │ - Route matching: /api/auth/login          │
   │ - Find handler: ControladorAuth.login()    │
   │ - Check filters: JwtTokenFilter.filter()   │
   │   (pero ruta es pública, se skippea)       │
   └────────────┬─────────────────────────────┘
                │
                ▼
3. CONTROLLER: VERIFY CREDENTIALS
   ┌────────────────────────────────────────────┐
   │ ControladorAuth.login()                    │
   │                                            │
   │ 1. Call ServicioUsuarios.obtenerPorEmail()│
   │    └─▶ RepositoryJPA.obtenerPorEmail()    │
   │        └─▶ DB Query: SELECT * FROM usuarios
   │            WHERE email = 'juan@...'       │
   │                                            │
   │ 2. if (usuario != null &&                 │
   │     usuario.getClave().equals("pwd123"))  │
   │    {                                       │
   │      Create claims:                       │
   │      - sub: "USER001"                     │
   │      - email: "juan@example.com"          │
   │      - nombre: "Juan"                     │
   │      - roles: "USER"                      │
   │    }                                       │
   │                                            │
   │ 3. Call JwtUtils.generateToken(claims)    │
   │    └─▶ Build JWT:                         │
   │        HEADER: {alg: HS256, typ: JWT}     │
   │        PAYLOAD: {claims + exp}            │
   │        SIGNATURE: HMAC-SHA256(secret)     │
   │                                            │
   │ 4. Return Response.ok(token).build()      │
   └────────────┬─────────────────────────────┘
                │
                ▼
4. RESPONSE: JWT TOKEN
   ┌────────────────────────────────────────────┐
   │ HTTP/1.1 200 OK                            │
   │ Content-Type: text/plain                   │
   │                                            │
   │ eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVU0VS... │
   │                                            │
   │ Decodificado (PAYLOAD):                    │
   │ {                                          │
   │   "sub": "USER001",                        │
   │   "email": "juan@example.com",             │
   │   "nombre": "Juan",                        │
   │   "roles": "USER",                         │
   │   "exp": 1789554775                        │
   │ }                                          │
   └────────────┬─────────────────────────────┘
                │
        Client saves token
                │
                ▼
5. PROTECTED REQUEST
   ┌────────────────────────────────────────────┐
   │ GET /api/usuarios                          │
   │ Authorization: Bearer eyJhbGciOi...       │
   └────────────┬─────────────────────────────┘
                │
                ▼
6. TOKEN VALIDATION FILTER
   ┌────────────────────────────────────────────┐
   │ JwtTokenFilter.filter()                    │
   │                                            │
   │ 1. Get Authorization header                │
   │    "Bearer eyJhbGciOi..."                 │
   │                                            │
   │ 2. Extract token                          │
   │    token = "eyJhbGciOi..."                │
   │                                            │
   │ 3. Call JwtUtils.validateToken(token)     │
   │    └─▶ Parse with HMAC-SHA256(secret)     │
   │    └─▶ Check signature                    │
   │    └─▶ Check expiration                   │
   │    └─▶ Return Claims if valid             │
   │                                            │
   │ 4. if (valid) {                           │
   │      servletRequest.setAttribute(         │
   │        "claims", claims)                  │
   │      filterChain.doFilter()  // CONTINUE  │
   │    }                                       │
   │    else {                                  │
   │      abortWith(401 UNAUTHORIZED)          │
   │    }                                       │
   └────────────┬─────────────────────────────┘
                │
                ▼
7. RESOURCE HANDLER
   ┌────────────────────────────────────────────┐
   │ UsuarioResource.listarUsuarios()           │
   │                                            │
   │ Token ya validado por JwtTokenFilter       │
   │ Procede a obtener lista de usuarios        │
   │                                            │
   │ 1. Call ServicioUsuarios.getUsuarios()    │
   │ 2. Map to DTOs                            │
   │ 3. Return Response.ok(list)               │
   └────────────┬─────────────────────────────┘
                │
                ▼
8. RESPONSE: USUARIOS
   ┌────────────────────────────────────────────┐
   │ HTTP/1.1 200 OK                            │
   │ Content-Type: application/json             │
   │                                            │
   │ [                                          │
   │   {                                        │
   │     "id": "USER001",                       │
   │     "email": "juan@example.com",           │
   │     "nombre": "Juan",                      │
   │     "apellidos": "Pérez",                  │
   │     "rol": "USER",                         │
   │     "_links": {...}                        │
   │   },                                       │
   │   ...                                      │
   │ ]                                          │
   └────────────────────────────────────────────┘
```

---

## 🔐 Componentes Clave

### 1. JwtUtils (Generación y Validación)
```
┌──────────────────────────────────────────┐
│         JwtUtils                         │
├──────────────────────────────────────────┤
│                                          │
│ ┌─ generateToken(claims) ─────────────┐ │
│ │ 1. Create expiration timestamp      │ │
│ │ 2. Build JWT with Jwts.builder()    │ │
│ │ 3. Set claims                       │ │
│ │ 4. Sign with HS256 + SECRET         │ │
│ │ 5. Return encoded token             │ │
│ └──────────────────────────────────────┘ │
│                                          │
│ ┌─ validateToken(token) ──────────────┐ │
│ │ 1. Parse token with Jwts.parser()   │ │
│ │ 2. Set signing key (SECRET)         │ │
│ │ 3. Verify signature                 │ │
│ │ 4. Check expiration                 │ │
│ │ 5. Return Claims or throw Exception │ │
│ └──────────────────────────────────────┘ │
│                                          │
│ ┌─ CONFIGURATION ──────────────────────┐ │
│ │ SECRET = env("JWT_SECRET")          │ │
│ │          or "secreto" (default)     │ │
│ │                                     │ │
│ │ EXPIRATION = env("JWT_EXPIRATION")  │ │
│ │             or 3600 sec (default)   │ │
│ └──────────────────────────────────────┘ │
│                                          │
└──────────────────────────────────────────┘
```

### 2. JwtTokenFilter (Middleware)
```
┌──────────────────────────────────────────┐
│      JwtTokenFilter (@Provider)          │
├──────────────────────────────────────────┤
│                                          │
│ PRIORITY: AUTHENTICATION (runs first)   │
│                                          │
│ filter(ContainerRequestContext) {       │
│                                          │
│   1. Check if @PermitAll        ✓       │
│      └─▶ ALLOW (skip filter)            │
│                                          │
│   2. Check if path = "auth/login"  ✓    │
│      └─▶ ALLOW (public endpoint)        │
│                                          │
│   3. Get Authorization header        ✓ │
│      └─▶ Check format: "Bearer ..."     │
│      └─▶ Extract token after "Bearer "  │
│                                          │
│   4. Validate token         ✓           │
│      └─▶ Call JwtUtils.validateToken()  │
│      └─▶ Catch exceptions, return 401   │
│                                          │
│   5. Check roles if @RolesAllowed  ✓    │
│      └─▶ Compare user roles vs required │
│      └─▶ If no match, return 403        │
│                                          │
│   6. Continue to resource handler  ✓    │
│      └─▶ filterChain.doFilter()         │
│                                          │
│ }                                        │
│                                          │
└──────────────────────────────────────────┘
```

### 3. ControladorAuth (Endpoint Login)
```
┌──────────────────────────────────────────┐
│    ControladorAuth                       │
│    @Path("/auth")                        │
├──────────────────────────────────────────┤
│                                          │
│ @POST                                    │
│ @Path("/login")                          │
│ login(email, password) {                 │
│                                          │
│   1. verificarCredenciales()        ✓   │
│      ├─ Get usuario by email            │
│      │  └─▶ ServicioUsuarios.          │
│      │      obtenerPorEmail(email)      │
│      │                                   │
│      ├─ Validate password               │
│      │  └─▶ usuario.getClave()          │
│      │      .equals(password)           │
│      │                                   │
│      └─ Create claims if valid     ✓   │
│         ├─ sub: usuario.getId()        │
│         ├─ email: usuario.getEmail()   │
│         ├─ nombre: usuario.getNombre() │
│         └─ roles: usuario.getRol()     │
│                                          │
│   2. if (claims != null)            ✓   │
│      └─ token = JwtUtils.generateToken()│
│      └─ return Response.ok(token)       │
│                                          │
│   3. else                           ✓   │
│      └─ return Response.status(401)     │
│      └─ entity("Credenciales...")       │
│                                          │
│ }                                        │
│                                          │
└──────────────────────────────────────────┘
```

---

## 📦 Capa de Datos

```
┌────────────────────────────────────────────────────────┐
│                USUARIO ENTITY                          │
├────────────────────────────────────────────────────────┤
│                                                        │
│ @Entity                                                │
│ @Table(name="usuarios")                               │
│                                                        │
│ Fields:                                                │
│ ├─ @Id id_usuario (String PK)                         │
│ ├─ email (String, UNIQUE)                             │
│ ├─ nombre (String)                                     │
│ ├─ apellidos (String)                                  │
│ ├─ clave (String) ← Password                          │
│ ├─ fecha_nacimiento (Date)                             │
│ ├─ telefono (String)                                   │
│ ├─ administrador (boolean)                             │
│ └─ rol (String) ✨ NEW ─ "ADMIN" or "USER"            │
│                                                        │
│ Relationships:                                         │
│ └─ Implementa: Identificable                          │
│                                                        │
└────────────────────────────────────────────────────────┘
                       │
                       │ JPA
                       │
        ┌──────────────▼──────────────┐
        │  UsuarioRepositoryJPA       │
        │  extends RepositoryJPA<U>   │
        │  implements UsuarioRepository
        │                             │
        │ Methods:                    │
        │ - getById(id)              │
        │ - findAll()                │
        │ - add(usuario)             │
        │ - update(usuario)          │
        │ - delete(usuario)          │
        │ - existeEmail(email)       │
        │ - obtenerPorEmail() ✨    │
        │   └─▶ JPQL Query          │
        │       SELECT u FROM Usuario
        │       WHERE u.email = :email
        │                             │
        └──────────────┬──────────────┘
                       │
                       │ EntityManager
                       │ (Hibernate)
                       │
        ┌──────────────▼──────────────┐
        │    MySQL Database           │
        │                             │
        │  usuarios table             │
        │  ┌───────────────────────┐  │
        │  │ id_usuario  (PK)      │  │
        │  │ email       (UNIQUE)  │  │
        │  │ nombre                │  │
        │  │ apellidos             │  │
        │  │ clave                 │  │
        │  │ fecha_nacimiento      │  │
        │  │ telefono              │  │
        │  │ administrador (BOOL)  │  │
        │  │ rol       ✨ (NEW)    │  │
        │  └───────────────────────┘  │
        │                             │
        └─────────────────────────────┘
```

---

## 🔀 Comparativa: Bookle vs Usuarios

```
BOOKLE (Proyecto A)              USUARIOS (Proyecto B)
═══════════════════════════════  ═════════════════════════════════

Server Type:                     Server Type:
┌──────────────────┐             ┌──────────────────┐
│ Tomcat (WAR)     │             │ Grizzly          │
│ Servlet-based    │             │ Standalone JAR   │
└──────────────────┘             └──────────────────┘

Configuration:                   Configuration:
┌──────────────────┐             ┌──────────────────┐
│ web.xml          │             │ ResourceConfig   │
│ Static XML       │             │ Java-based       │
└──────────────────┘             └──────────────────┘

Database:                        Database:
┌──────────────────┐             ┌──────────────────┐
│ In-Memory        │             │ JPA/Hibernate    │
│ HashMaps         │             │ MySQL            │
└──────────────────┘             └──────────────────┘

JWT Configuration:               JWT Configuration:
┌──────────────────┐             ┌──────────────────┐
│ Hardcoded        │             │ Environment Vars │
│ + Fallback props │             │ + Properties file│
└──────────────────┘             └──────────────────┘

User Model:                      User Model:
┌──────────────────┐             ┌──────────────────┐
│ None (Ejemplo)   │             │ Complete Entity  │
│ In ControladorAuth             │ Persistent BD    │
└──────────────────┘             └──────────────────┘

Roles:                           Roles:
┌──────────────────┐             ┌──────────────────┐
│ Text: "PROFESOR" │             │ Field: rol       │
│ Hardcoded        │             │ From DB          │
└──────────────────┘             └──────────────────┘

SIMILARITIES: ✓ SAME JWT LOGIC, SAME FILTER, SAME ENDPOINTS
DIFFERENCES:  ✓ DEPLOYMENT, CONFIG, DATA, SECURITY LEVEL
```

---

## 📊 Request/Response Flow

```
SUCCESSFUL LOGIN
═══════════════

REQUEST:
┌────────────────────────────────────┐
│ POST /api/auth/login HTTP/1.1      │
│ Host: localhost:8080               │
│ Content-Type: application/x-www-form-urlencoded
│                                    │
│ email=juan@example.com             │
│ password=password123               │
└────────────────────────────────────┘
                │
                ▼
PROCESSING:
┌────────────────────────────────────┐
│ 1. ControladorAuth.login() called   │
│ 2. Lookup usuario by email         │
│ 3. Validate password               │
│ 4. Generate JWT claims             │
│ 5. Create signed token             │
└────────────────────────────────────┘
                │
                ▼
RESPONSE:
┌────────────────────────────────────┐
│ HTTP/1.1 200 OK                    │
│ Content-Type: text/plain           │
│ Content-Length: 287                │
│                                    │
│ eyJhbGciOiJIUzI1NiJ9.eyJzdWIi...  │
│ ...bk9wNyIsImV4cCI6MTcyOTIzNDA... │
│ ...mZUVRg.6vDqG0xN0QsJ...         │
└────────────────────────────────────┘


PROTECTED REQUEST WITH TOKEN
════════════════════════════

REQUEST:
┌────────────────────────────────────┐
│ GET /api/usuarios HTTP/1.1         │
│ Host: localhost:8080               │
│ Authorization: Bearer              │
│   eyJhbGciOiJIUzI1NiJ9.eyJzdWI... │
│                                    │
│ (No body)                          │
└────────────────────────────────────┘
                │
                ▼
PROCESSING:
┌────────────────────────────────────┐
│ 1. JwtTokenFilter.filter() called   │
│ 2. Extract token from header       │
│ 3. Validate signature              │
│ 4. Check expiration                │
│ 5. Store claims in request         │
│ 6. Forward to UsuarioResource      │
│ 7. Get usuarios from DB            │
│ 8. Map to DTO                      │
└────────────────────────────────────┘
                │
                ▼
RESPONSE:
┌────────────────────────────────────┐
│ HTTP/1.1 200 OK                    │
│ Content-Type: application/json     │
│                                    │
│ [                                  │
│   {                                │
│     "id": "USER001",               │
│     "email": "juan@example.com",   │
│     "nombre": "Juan",              │
│     "apellidos": "Pérez",          │
│     "rol": "USER",                 │
│     "_links": {                    │
│       "self": {                    │
│         "href": "http://..."       │
│       }                            │
│     }                              │
│   }                                │
│ ]                                  │
└────────────────────────────────────┘
```

---

## ⚠️ Casos de Error

```
INVALID CREDENTIALS
═══════════════════

REQUEST:
POST /api/auth/login
email=juan@example.com
password=WRONG

RESPONSE:
HTTP/1.1 401 UNAUTHORIZED
Content-Type: text/plain

"Credenciales inválidas"


MISSING TOKEN
═════════════

REQUEST:
GET /api/usuarios
(sin Authorization header)

RESPONSE:
HTTP/1.1 401 UNAUTHORIZED

"No se adjunta el token correctamente"


INVALID TOKEN
═════════════

REQUEST:
GET /api/usuarios
Authorization: Bearer invalid.token.here

RESPONSE:
HTTP/1.1 401 UNAUTHORIZED

"JWT signature does not match..."


EXPIRED TOKEN
═════════════

REQUEST:
GET /api/usuarios
Authorization: Bearer (token older than 1 hour)

RESPONSE:
HTTP/1.1 401 UNAUTHORIZED

"Token expired"


INSUFFICIENT ROLE
═════════════════

REQUEST:
DELETE /api/usuarios/123
Authorization: Bearer (USER token, not ADMIN)

RESPONSE:
HTTP/1.1 403 FORBIDDEN

"no tiene rol de acceso"
```

---

## 📝 Archivo Structure

```
proyecto-usuarios/
│
├── src/main/java/
│   └── com/arso/
│       ├── Main.java                    (Entry point, arrancar servidor)
│       │
│       ├── auth/                        ✨ NUEVO
│       │   ├── JwtUtils.java           (Generar/validar tokens)
│       │   ├── JwtTokenFilter.java     (Filtro autenticación)
│       │   └── ControladorAuth.java    (Endpoint login)
│       │
│       ├── usuarios/
│       │   ├── domain/
│       │   │   └── Usuario.java         (Entity, campo rol ✨)
│       │   │
│       │   ├── repository/
│       │   │   ├── UsuarioRepository.java      (Interfaz, obtenerPorEmail() ✨)
│       │   │   └── UsuarioRepositoryJPA.java  (Implementación ✨)
│       │   │
│       │   ├── service/
│       │   │   ├── ServicioUsuarios.java      (Interfaz, obtenerPorEmail() ✨)
│       │   │   └── ServicioUsuariosImpl.java  (Implementación ✨)
│       │   │
│       │   └── rest/
│       │       ├── UsuariosApplication.java   (Config, registra filtro ✨)
│       │       ├── UsuarioResource.java       (Endpoints CRUD)
│       │       └── dto/
│       │
│       ├── repository/
│       │   ├── Repository.java
│       │   ├── RepositoryJPA.java
│       │   └── ...
│       │
│       └── service/
│           └── FactoriaServicios.java
│
├── src/main/resources/
│   ├── application.properties            ✨ NUEVO (jwt.secret, jwt.expiration)
│   ├── repositorios.properties
│   ├── servicios.properties
│   └── META-INF/
│       └── persistence.xml
│
├── pom.xml                               (✨ jjwt + servlet-api agregados)
│
└── Documentación/
    ├── AUTENTICACION_IMPLEMENTADA.md    ✨ NUEVO
    ├── TESTING_JWT.md                   ✨ NUEVO
    ├── RESUMEN_IMPLEMENTACION.md        ✨ NUEVO
    └── VERIFICACION_CHECKLIST.md        ✨ NUEVO
```

---

**Diagrama actualizado:** 2 de Abril de 2026  
**Versión:** 1.0  
**Status:** ✅ ARQUITECTURA IMPLEMENTADA


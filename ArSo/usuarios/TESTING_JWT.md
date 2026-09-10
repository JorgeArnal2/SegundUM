# 🧪 Guía de Testing: Autenticación JWT en Proyecto Usuarios

---

## 🚀 Inicio Rápido

### 1. Arrancar el servidor
```bash
cd C:\Users\jorge\Documents\GitHub\Arso-2025-26\usuarios
mvn exec:java
```

**Salida esperada:**
```
╔══════════════════════════════════════════════════════╗
║  Microservicio Usuarios arrancado en http://localhost:8080/api/ ║
╠══════════════════════════════════════════════════════╣
║ ✓ Autenticación JWT habilitada                      ║
║ ✓ Endpoint login: POST http://localhost:8080/api/auth/login  ║
║                   username: email                   ║
║                   password: clave                   ║
╠══════════════════════════════════════════════════════╣
║ Pulsa INTRO para detener el servidor...              ║
╚══════════════════════════════════════════════════════╝
```

---

## 🧑 Preparar Datos de Prueba

Antes de testear, necesitas un usuario en BD:

```sql
-- Crear usuario de prueba en la BD
INSERT INTO usuarios (id_usuario, email, nombre, apellidos, clave, fecha_nacimiento, telefono, administrador, rol)
VALUES ('USER001', 'juan@example.com', 'Juan', 'Pérez', 'password123', '1990-05-15', '666123456', 0, 'USER');

INSERT INTO usuarios (id_usuario, email, nombre, apellidos, clave, fecha_nacimiento, telefono, administrador, rol)
VALUES ('ADMIN001', 'admin@example.com', 'Admin', 'Istrador', 'admin123', '1985-03-20', '666654321', 1, 'ADMIN');
```

---

## 🔐 Test 1: Login Exitoso

### 📍 Usuario Normal (rol: USER)

**Request:**
```bash
curl -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "email=juan@example.com&password=password123" \
  http://localhost:8080/api/auth/login
```

**Respuesta Esperada (Status 200):**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVU0VSMDAxIiwiZW1haWwiOiJqdWFuQGV4YW1wbGUuY29tIiwibm9tYnJlIjoiSnVhbiIsInJvbGVzIjoiVVNFUiIsImV4cCI6MTc4OTU1NDc3NX0.6KL9mXp2q0R4vZ8w3jL1qM5nOp7qR8s9t0u1v2w3x4
```

**Decodificación del token (en jwt.io):**
```json
{
  "sub": "USER001",
  "email": "juan@example.com",
  "nombre": "Juan",
  "roles": "USER",
  "exp": 1789554775
}
```

---

### 📍 Usuario Admin (rol: ADMIN)

**Request:**
```bash
curl -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "email=admin@example.com&password=admin123" \
  http://localhost:8080/api/auth/login
```

**Respuesta Esperada (Status 200):**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBRE1JTjAwMSIsImVtYWlsIjoiYWRtaW5AZXhhbXBsZS5jb20iLCJub21icmUiOiJBZG1pbiIsInJvbGVzIjoiQURNSU4iLCJleHAiOjE3ODk1NTQ3NzV9.8mM2oNq3rS5tW9y4kN6pO7qS8tU9v0w1x2y3z4a5b
```

---

## ❌ Test 2: Login Fallido

### 📍 Contraseña Incorrecta

**Request:**
```bash
curl -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "email=juan@example.com&password=claveIncorrecta" \
  http://localhost:8080/api/auth/login
```

**Respuesta Esperada (Status 401):**
```
"Credenciales inválidas"
```

---

### 📍 Usuario No Existe

**Request:**
```bash
curl -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "email=noexiste@example.com&password=password123" \
  http://localhost:8080/api/auth/login
```

**Respuesta Esperada (Status 401):**
```
"Credenciales inválidas"
```

---

## 🔑 Test 3: Uso de Token en Petición Protegida

### 📍 Obtener Lista de Usuarios (Sin Token)

**Request:**
```bash
curl -X GET http://localhost:8080/api/usuarios
```

**Respuesta Esperada (Status 401):**
```
"No se adjunta el token correctamente"
```

---

### 📍 Obtener Lista de Usuarios (Con Token Válido)

**Paso 1: Obtener token**
```bash
TOKEN=$(curl -s -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "email=juan@example.com&password=password123" \
  http://localhost:8080/api/auth/login | tr -d '"')
```

**Paso 2: Usar token**
```bash
curl -X GET \
  -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/usuarios
```

**Respuesta Esperada (Status 200):**
```json
[
  {
    "id": "USER001",
    "email": "juan@example.com",
    "nombre": "Juan",
    "apellidos": "Pérez",
    "rol": "USER",
    "_links": {
      "self": {
        "href": "http://localhost:8080/api/usuarios/USER001"
      }
    }
  },
  {
    "id": "ADMIN001",
    "email": "admin@example.com",
    "nombre": "Admin",
    "apellidos": "Istrador",
    "rol": "ADMIN",
    "_links": {
      "self": {
        "href": "http://localhost:8080/api/usuarios/ADMIN001"
      }
    }
  }
]
```

---

### 📍 Token Expirado

**Crear token con expiración corta (para testing):**
```bash
# En application.properties, cambiar temporalmente:
jwt.expiration=1
```

**Luego intentar usar después de 2 segundos:**
```bash
curl -X GET \
  -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/usuarios
```

**Respuesta Esperada (Status 401):**
```
"Token expired"
```

---

### 📍 Token Inválido

**Request:**
```bash
curl -X GET \
  -H "Authorization: Bearer tokenInvalido123" \
  http://localhost:8080/api/usuarios
```

**Respuesta Esperada (Status 401):**
```
"JWT signature does not match locally computed signature"
```

---

### 📍 Token Malformado

**Request (sin "Bearer"):**
```bash
curl -X GET \
  -H "Authorization: $TOKEN" \
  http://localhost:8080/api/usuarios
```

**Respuesta Esperada (Status 401):**
```
"No se adjunta el token correctamente"
```

---

## 👥 Test 4: Control de Roles

### 📍 Proteger endpoint con @RolesAllowed

Para testear, modificar endpoint de ejemplo:

```java
@DELETE
@Path("/{id}")
@RolesAllowed({"ADMIN"})  // Solo ADMIN
public Response eliminarUsuario(@PathParam("id") String id) {
    // ...
}
```

**Request con usuario USER:**
```bash
curl -X DELETE \
  -H "Authorization: Bearer $TOKEN_USER" \
  http://localhost:8080/api/usuarios/USER001
```

**Respuesta Esperada (Status 403):**
```
"no tiene rol de acceso"
```

**Request con usuario ADMIN:**
```bash
curl -X DELETE \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  http://localhost:8080/api/usuarios/USER001
```

**Respuesta Esperada (Status 200 o 204):**
```
(Sin contenido, operación exitosa)
```

---

## 🛠️ Test 5: Crear Nuevo Usuario y Loguear

### 📍 Crear usuario vía API

**Request:**
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos",
    "apellidos": "García",
    "email": "carlos@example.com",
    "clave": "carlos123",
    "fechaNacimiento": "1995-07-22",
    "telefono": "666555444"
  }' \
  http://localhost:8080/api/usuarios
```

**Respuesta Esperada (Status 201):**
```
Location: http://localhost:8080/api/usuarios/USER002
```

### 📍 Loguear con nuevo usuario

**Request:**
```bash
curl -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "email=carlos@example.com&password=carlos123" \
  http://localhost:8080/api/auth/login
```

**Respuesta Esperada (Status 200):**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVU0VSMDAyIiwiZW1haWwiOiJjYXJsb3NAZXhhbXBsZS5jb20iLCJub21icmUiOiJDYXJsb3MiLCJyb2xlcyI6IlVTRVIiLCJleHAiOjE3ODk1NTU2MzB9...
```

---

## 📊 Test Summary

| Test | Endpoint | Método | Esperado | Status |
|------|----------|--------|----------|--------|
| Login exitoso USER | /auth/login | POST | Token JWT | 200 |
| Login exitoso ADMIN | /auth/login | POST | Token JWT | 200 |
| Login fallido | /auth/login | POST | "Credenciales inválidas" | 401 |
| Usuario no existe | /auth/login | POST | "Credenciales inválidas" | 401 |
| Sin token | /usuarios | GET | "No se adjunta..." | 401 |
| Con token válido | /usuarios | GET | Lista usuarios | 200 |
| Token expirado | /usuarios | GET | "Token expired" | 401 |
| Token inválido | /usuarios | GET | Error JWT | 401 |
| Sin "Bearer" | /usuarios | GET | "No se adjunta..." | 401 |
| USER sin permiso | /delete/{id} | DELETE | "no tiene rol..." | 403 |
| ADMIN con permiso | /delete/{id} | DELETE | Operación exitosa | 200 |

---

## 🐛 Debugging

### Ver logs del servidor

El servidor muestra información de errores en consola:

```
Error en validación de credenciales: Usuario no encontrado
Error al obtener usuario por email: ...
```

### Usar herramientas online

- **jwt.io** → Decodificar y validar tokens
- **Postman** → Interface gráfica para peticiones
- **Insomnia** → Similar a Postman, más moderno

### Limpiar caché

Si hay problemas de caché:

```bash
mvn clean install
mvn clean compile
```

---

## 📋 Checklist

- [ ] Servidor arrancado correctamente
- [ ] Usuario de prueba creado en BD
- [ ] Login exitoso (obtienes token)
- [ ] Peticiones sin token devuelven 401
- [ ] Peticiones con token válido funcionan
- [ ] Token expirado rechazado
- [ ] Token inválido rechazado
- [ ] Roles protegidos funcionan (si implementado)
- [ ] Nuevo usuario puede loguear
- [ ] Logs son claros y útiles

---

**Nota:** Si hay problemas, revisar:
1. BD está corriendo y accesible
2. Datos de prueba están en BD
3. `application.properties` está en classpath
4. Dependencias JWT están en pom.xml
5. `JwtTokenFilter` está registrado en `UsuariosApplication`



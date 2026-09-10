# Guía de pruebas manuales — SegundUM

Documento de referencia para comprobar que el proyecto compila, arranca e interactúa correctamente.

---

## 1. Comprobación rápida (estado del repositorio)

Última verificación: **8 jun 2026**

### Compilación (`mvn package -DskipTests`)

| Módulo        | Resultado | Artefacto ejecutable                          |
|---------------|-----------|-----------------------------------------------|
| `usuarios`    | OK        | `usuarios/target/usuarios-1.0-SNAPSHOT.jar` (~26 MB, fat JAR) |
| `productos`   | OK        | `productos/target/productos-1.0-SNAPSHOT.jar` (~50 MB)          |
| `compraventas`| OK        | `compraventas/target/compraventas-1.0-SNAPSHOT.jar` (~38 MB)  |
| `pasarela`    | OK        | `pasarela/target/pasarela-1.0-SNAPSHOT.jar` (~44 MB)            |

Comando para recompilar todo desde la raíz:

```powershell
cd usuarios;    mvn -q package -DskipTests
cd ..\productos; mvn -q package -DskipTests
cd ..\compraventas; mvn -q package -DskipTests
cd ..\pasarela; mvn -q package -DskipTests
```

### Tests automáticos (`mvn test`)

| Módulo        | Resultado | Notas |
|---------------|-----------|-------|
| `productos`   | OK        | Todos los tests pasan |
| `pasarela`    | OK        | Todos los tests pasan |
| `usuarios`    | 9/10      | 1 test de integración falla si **no hay RabbitMQ** en local (timeout ~30 s) |
| `compraventas`| 7/8       | El test E2E cross-service requiere arrancar `usuarios` y `productos` externos; falla en CI sin ellos |

> **Conclusión:** el código **compila y genera JARs ejecutables**. Los fallos de tests son de entorno (RabbitMQ / servicios externos), no de compilación.

### Docker

- `docker-compose.yml` presente en la raíz.
- Requiere **Docker Desktop en ejecución** antes de `docker-compose up -d --build`.

---

## 2. Requisitos previos

### Opción recomendada: Docker

- Docker Desktop (Windows) iniciado.
- Puertos libres: **9000**, **8080–8082**, **3306**, **27017**, **5672**.

### Opción alternativa: desarrollo local

- Java 11 y Maven 3.9+.
- MySQL 8 con bases `arso_usuarios` y `arso_productos`.
- MongoDB en `localhost:27017`.
- RabbitMQ en `localhost:5672`.
- Cuatro terminales (una por microservicio).

---

## 3. Arranque con Docker (recomendado)

Desde la raíz del repositorio:

```powershell
docker-compose up -d --build
```

Espera 1–2 minutos el primer arranque (descarga de imágenes y creación de tablas).

### Comprobar que los contenedores están vivos

```powershell
docker-compose ps
```

Todos los servicios deben estar en estado `running` (o `healthy` para mysql/mongodb/rabbitmq).

### Ver logs si algo falla

```powershell
docker-compose logs pasarela
docker-compose logs usuarios
docker-compose logs productos
docker-compose logs compraventas
```

### Parar

```powershell
docker-compose down
```

---

## 4. Mapa de servicios y URLs

| Componente   | URL directa (solo desarrollo)     | Vía pasarela (uso normal)        |
|--------------|-----------------------------------|----------------------------------|
| Pasarela     | http://localhost:9000             | **Punto de entrada principal**   |
| Usuarios     | http://localhost:8080/api/usuarios | http://localhost:9000/api/usuarios |
| Productos    | http://localhost:8081/productos   | http://localhost:9000/api/productos |
| Categorías   | http://localhost:8081/categorias | http://localhost:9000/api/categorias |
| Compraventas | http://localhost:8082/compraventas | http://localhost:9000/api/compraventas |
| RabbitMQ UI  | http://localhost:15672 (arso/arso) | — |

**Regla práctica:** en pruebas manuales usa siempre la **pasarela** (`http://localhost:9000`), salvo depuración de un microservicio aislado.

---

## 5. Checklist de pruebas manuales

Marca cada ítem al completarlo.

### A. Infraestructura

- [ ] **A1.** `docker-compose ps` muestra 7 servicios en ejecución.
- [ ] **A2.** RabbitMQ Management accesible en http://localhost:15672 (usuario `arso`, contraseña `arso`).
- [ ] **A3.** En RabbitMQ existe el exchange `bus` (se crea al publicar/consumir el primer evento).

### B. Autenticación (pasarela)

- [ ] **B1.** Alta de usuario devuelve **201** y cabecera `Location`.
- [ ] **B2.** Login devuelve **200** con `token`, `id`, `nombreCompleto` y `roles`.
- [ ] **B3.** Login con contraseña incorrecta devuelve **401**.
- [ ] **B4.** Logout devuelve **204**.

### C. Usuarios

- [ ] **C1.** `GET /api/usuarios` con token devuelve listado (puede estar vacío al inicio).
- [ ] **C2.** `GET /api/usuarios/{id}` devuelve el usuario registrado.
- [ ] **C3.** `GET /api/usuarios/{id}/nombre` funciona **sin token** (operación pública).

### D. Productos

- [ ] **D1.** `GET /api/categorias` devuelve al menos la categoría `cat-1` (creada en perfil Docker).
- [ ] **D2.** `POST /api/productos` con token del vendedor devuelve **201**.
- [ ] **D3.** `GET /api/productos/{id}` devuelve el producto (público, sin token).
- [ ] **D4.** `GET /api/productos?page=0&size=10` devuelve listado paginado.

### E. Compraventas

- [ ] **E1.** Tras registrar usuario y esperar ~2 s (sincronización RabbitMQ), el vendedor existe en productos.
- [ ] **E2.** `POST /api/compraventas` con token del comprador devuelve **201**.
- [ ] **E3.** `GET /api/compraventas/comprador/{id}` lista la compra.
- [ ] **E4.** `GET /api/compraventas/vendedor/{id}` lista la venta.

### F. Eventos (consistencia entre servicios)

- [ ] **F1.** Tras una compraventa, el producto aparece como vendido (`vendido: true` en `GET /api/productos/{id}`).
- [ ] **F2.** Tras una compraventa, el usuario comprador/vendedor incrementa contadores en `GET /api/usuarios/{id}` (`compras` / `ventas`).

### G. Seguridad básica

- [ ] **G1.** `POST /api/productos` sin token devuelve **401**.
- [ ] **G2.** `POST /api/productos` con token de otro usuario (distinto `idVendedor`) devuelve **403**.
- [ ] **G3.** `POST /api/compraventas` con `idComprador` distinto al del token devuelve **403**.

---

## 6. Pruebas con curl (copiar y pegar)

Sustituye `USER_ID` y `PRODUCT_ID` por los valores que devuelvan las peticiones.

### 6.1 Alta de usuario

```powershell
curl -X POST http://localhost:9000/api/usuarios `
  -H "Content-Type: application/json" `
  -d "{\"nombre\":\"Ana\",\"apellidos\":\"Garcia Lopez\",\"email\":\"ana@test.com\",\"clave\":\"clave123\",\"fechaNacimiento\":\"1995-06-15\",\"telefono\":\"600111222\"}" `
  -i
```

Anota el `id` de la cabecera `Location` (último segmento de la URL).

### 6.2 Login

```powershell
curl -X POST http://localhost:9000/auth/login `
  -H "Content-Type: application/json" `
  -d "{\"email\":\"ana@test.com\",\"password\":\"clave123\"}"
```

Copia el valor de `token` de la respuesta JSON.

### 6.3 Listar categorías (público)

```powershell
curl http://localhost:9000/api/categorias
```

### 6.4 Crear producto (requiere token)

```powershell
curl -X POST http://localhost:9000/api/productos `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer TU_TOKEN_AQUI" `
  -d "{\"titulo\":\"Portatil\",\"descripcion\":\"Portatil de prueba\",\"precio\":499.99,\"estado\":\"NUEVO\",\"idCategoria\":\"cat-1\",\"envioDisponible\":true,\"idVendedor\":\"USER_ID\"}" `
  -i
```

Espera **2 segundos** tras el alta de usuario antes de crear el producto (sincronización del evento `usuario-creado`).

### 6.5 Registrar compraventa

```powershell
curl -X POST http://localhost:9000/api/compraventas `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer TU_TOKEN_AQUI" `
  -d "{\"idProducto\":\"PRODUCT_ID\",\"idComprador\":\"USER_ID\"}" `
  -i
```

### 6.6 Consultar compras del usuario

```powershell
curl http://localhost:9000/api/compraventas/comprador/USER_ID `
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

---

## 7. Pruebas con Postman

1. Importar `postman/SegundUM.postman_collection.json`.
2. Importar `postman/SegundUM-Docker.postman_environment.json`.
3. Seleccionar entorno **SegundUM Docker**.
4. Ejecutar la carpeta **Flujo completo** (Collection Runner) o paso a paso:
   - Alta usuario → espera → Login → Alta producto → Compraventa.

Variables que rellena automáticamente el entorno: `token`, `userId`, `productId`, `categoryId`.

---

## 8. Arranque local sin Docker (referencia)

Orden recomendado en terminales separadas:

```powershell
# Terminal 1 — requiere MySQL + RabbitMQ
cd usuarios
mvn exec:java

# Terminal 2 — requiere MySQL + RabbitMQ
cd productos
mvn spring-boot:run

# Terminal 3 — requiere MongoDB + RabbitMQ + usuarios + productos
cd compraventas
mvn spring-boot:run

# Terminal 4 — requiere los tres microservicios anteriores
cd pasarela
mvn spring-boot:run
```

URLs locales equivalentes a Docker, pero la pasarela sigue en **http://localhost:9000**.

---

## 9. Solución de problemas frecuentes

| Síntoma | Causa probable | Qué hacer |
|---------|----------------|-----------|
| `docker-compose` no arranca | Docker Desktop parado | Iniciar Docker Desktop y reintentar |
| `Connection refused` en pasarela | Microservicios aún arrancando | `docker-compose logs -f` y esperar healthchecks |
| Producto 400/404 al crear | Usuario no sincronizado en productos | Esperar 2–5 s tras alta de usuario; comprobar RabbitMQ |
| Login 401 | Email/clave incorrectos o usuario no existe | Repetir alta + login con mismas credenciales |
| Puerto en uso | Otro proceso usa 9000/8080… | `netstat -ano \| findstr :9000` y liberar puerto |
| Tests `usuarios` timeout | Sin RabbitMQ en local | Normal sin RabbitMQ; usar Docker o ignorar ese test |
| Test E2E `compraventas` falla | No arranca usuarios/productos externos | Es esperado en `mvn test` aislado; probar con Docker |

---

## 10. Resumen de comandos útiles

```powershell
# Compilar todo (sin tests)
mvn -q package -DskipTests

# Tests (pueden fallar tests de entorno sin infraestructura)
mvn test

# Levantar aplicación completa
docker-compose up -d --build

# Estado
docker-compose ps

# Logs en tiempo real
docker-compose logs -f pasarela

# Detener
docker-compose down
```

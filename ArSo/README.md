# SegundUM - Arso 2025-26

Aplicacion de compraventa de productos de segunda mano basada en microservicios.

## Requisitos

- Docker y Docker Compose
- Java 11 y Maven (solo para desarrollo local sin Docker)

## Puesta en marcha con Docker

Tras clonar el repositorio, levanta todos los servicios con:

```bash
docker-compose up -d --build
```

Servicios desplegados:

| Servicio      | Puerto | Descripcion                    |
|---------------|--------|--------------------------------|
| pasarela      | 9000   | Punto de acceso al backend     |
| usuarios      | 8080   | Gestion de usuarios (JAX-RS)   |
| productos     | 8081   | Gestion de productos           |
| compraventas  | 8082   | Gestion de compraventas        |
| MySQL         | 3306   | Bases `arso_usuarios` y `arso_productos` |
| MongoDB       | 27017  | Base `compraventas`            |
| RabbitMQ      | 5672   | Bus de eventos (management: 15672) |

La API publica se consume a traves de la pasarela en `http://localhost:9000`.

### Parar la aplicacion

```bash
docker-compose down
```

Para eliminar tambien los volumenes de datos:

```bash
docker-compose down -v
```

## Pruebas con Postman

Importa en Postman:

- `postman/SegundUM.postman_collection.json`
- `postman/SegundUM-Docker.postman_environment.json`

Selecciona el entorno **SegundUM Docker** y ejecuta la carpeta **Flujo completo** para probar registro, login, alta de producto y compraventa.

## Desarrollo local (sin Docker)

Cada microservicio puede ejecutarse individualmente con Maven desde su directorio:

```bash
cd usuarios && mvn exec:java
cd productos && mvn spring-boot:run
cd compraventas && mvn spring-boot:run
cd pasarela && mvn spring-boot:run
```

Requiere MySQL, MongoDB y RabbitMQ en local con la configuracion de los ficheros `application.properties`.

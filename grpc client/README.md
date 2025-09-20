# gRPC Client

Este documento describe los endpoints HTTP del cliente gRPC.

## Pre-requisitos

Si se levanta la aplicación a través de Docker, no es necesario realizar ningún tipo de configuración en esta aplicación.

En caso de desear levantar esta aplicación sin Docker, crear una copia del archivo `.env-example` que tenga el nombre `.env` y setear la variable de entorno indicada en dicho archivo.

## Tecnologías
- [JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript)
- [NodeJS](https://nodejs.org/es)

## Dependencias
- [@grpc/grpc-js](https://www.npmjs.com/package/@grpc/grpc-js)
- [express](https://www.npmjs.com/package/express)
- [cors](https://www.npmjs.com/package/cors)
- [express-jwt](https://www.npmjs.com/package/express-jwt)
- [cookie-parser](https://www.npmjs.com/package/cookie-parser)
- [dotenv](https://www.npmjs.com/package/dotenv)

## Funcionalidades

Este cliente gRPC cuenta con:
- Comunicación con un server gRPC.
- Routers HTTP que invocan métodos gRPC de las siguientes entidades del server:
  - Event
  - EventInventory
  - Inventory
  - User
- Routers HTTP que renderizan vistas EJS.
- Consumo de los siguientes servicios Protobuf:
  - event.proto
  - inventory.proto
  - user.proto
  - utils.proto
- Implementación de express-jwt para validar tokens del frontend en caso de ejecutar una request que requiera autenticación.

# Endpoints

## General

| Endpoint | Respuesta |
|----------|--------------|
| `GET /` | Devuelve la vista del login |
| `GET /home` | Devuelve la vista de la home |
| `GET /about` | Devuelve la vista de la sección "Sobre Nosotros" |
| `GET /contact` | Devuelve la vista de la sección "Contáctanos" |
| `GET /privacy` | Devuelve la vista de la sección "Política de privacidad" |


## User Router

### Endpoints de vistas

| Endpoint | Respuesta |
|----------|--------------|
| `GET /user` | Devuelve una vista que muestra a todos los usuarios |
| `GET /user/create` | Devuelve una vista que permite crear usuarios |
| `GET /user/modify/{username}` | Devuelve una vista para modificar al usuario pasado a través del parámetro `username` de la URL |

### Endpoints de la API

| Endpoint | Body de la request | Respuesta |
|----------|--------------|--------------|
| `POST /user/login` | Enviar `username` y `password`. | Devuelve todos los datos del usuario y un JWT Token generado por el server. El JWT Token es guardado en las cookies del usuario. |
| `POST /user/logout` | No espera ningún parámetro. | Elimina la cookie donde se había guardado el JWT Token del usuario. |
| `POST /user/create` | Enviar `username`, `name`, `surname`, `phoneNumber`, `email`, `isActive`, `roles`. | Devuelve un boolean `succeeded` junto a una string `message` indicando si la creación fue exitosa o no. |
| `POST /user/modify` | Enviar `id`, `username`, `name`, `surname`, `phoneNumber`, `email`, `isActive`, `roles`. | Devuelve un boolean `succeeded` junto a un `message` indicando si la modificación fue exitosa o no. |
| `POST /user/delete` | Enviar `id`. | Devuelve un boolean `succeeded` junto a una string `message` indicando si la eliminación fue exitosa o no. |
| `GET /user/list` | No espera ningún parámetro. | Devuelve una lista cargada con todos los usuarios de la base de datos. |
| `GET /user/active-list` | No espera ningún parámetro. | Devuelve una lista cargada con todos los usuarios de la base de datos que tengan su atributo `isActive` seteado a TRUE. |
| `GET /user/{username}` | No espera ningún parámetro. | Devuelve al usuario de la base de datos que posea el nombre de usuario pasado a través de la URL. |

## Event Router

### Endpoints de vistas

| Endpoint | Respuesta |
|----------|--------------|
| `GET /` | a |

### Endpoints de la API

| Endpoint | Body de la request | Respuesta |
|----------|--------------|--------------|
| `GET /` | a | a | 

## Inventory Router

### Endpoints de vistas

| Endpoint | Respuesta |
|----------|--------------|
| `GET /` | a |

### Endpoints de la API

| Endpoint | Body de la request | Respuesta |
|----------|--------------|--------------|
| `GET /` | a | a | 


# gRPC Server

Este documento describe los servicios gRPC disponibles, sus métodos, parámetros y respuestas. Además de breves explicaciones sobre ciertas funcionalidades.

## Tecnologías
- Java 17
- Spring Boot 3.5.5

## Dependencias
- Spring Data JPA
- Spring Security
- Spring gRPC
- Spring Boot Starter Mail
- Auth Java JWT
- Lombok
- MySQL Connector
- Google Protobuf

## Funcionalidades

Este servidor gRPC cuenta con:
- Acceso a una base de datos MySQL.
- Repositorios Spring Data JPA sobre las entidades:
  - Event
  - EventInventory
  - Inventory
  - Role
  - User
- Implementaciones de los siguientes servicios Protobuf:
  - event.proto
  - inventory.proto
  - user.proto
  - utils.proto
- Implementación de ServerInterceptor para validar requests entrantes.
- Implementación de la librería JavaMailSender para enviar correos a usuarios.
- Implementación de Auth Java JWT para generar, firmar y validar tokens.

# Server Interceptor

Cada request realizada al servidor es interceptada por la clase ```GrpcServerInterceptor```, contenida en el paquete ```configuration/security```.

La clase ```GrpcServerInterceptor``` declara en HashMaps:
- **Métodos públicos**: ejecuta la request sin realizar ninguna validación.
- **Métodos privados**: ejecuta la request sí y solo sí el server valida lo siguiente. En caso de que una validación falle, rechaza la request:
  - Extrae el JWT Token de la request y verifica que sea válido.
  - Extrae los roles del usuario contenido en los claims del JWT Token y valida que al menos uno de sus roles tenga el permiso suficiente para ejecutar el método gRPC de su request.

# Servicios Protobuf

## Métodos de UserService

- **UserService/Login**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
  "username": "nombreDeUsuarioOEmail",
  "password": "contraseña"
}
```

**Validaciones**:
- Si el atributo ```username``` no existe en la base de datos, el server devuelve un Status NOT_FOUND.
- Si el atributo ```username``` existe en la base de datos, pero ```password``` no, el server devuelve un Status INVALID_ARGUMENT.
- Si los atributos ```username``` y ```password``` existen en la base de datos, pero en la base de datos el usuario tiene su atributo ```isActive``` en FALSE, el server devuelve un Status FAILED_PRECONDITION.

Si el usuario no levanta ninguna de las anteriores excepciones, el server crea un JWT Token para él y se lo devuelve en una response junto a todos sus datos.

- **UserService/CreateUser**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
  "username": "nombreDeUsuario",
  "name": "nombre",
  "surname": "apellido",
  "phoneNumber": "+00 00 0000-0000",
  "email": "correo@electronico.com",
  "isActive": "trueOFalse",
  "roles": [
    {"name": "primerRol"},
    {"name":  "segundoRol"}
  ]
}
```

El usuario puede tener tantos roles existan.

**Validaciones**:
- Si el atributo ```username``` es una String vacía, el server no realiza la creación.
- Si el atributo ```name``` es una String vacía, el server no realiza la creación.
- Si el atributo ```surname``` es una String vacía, el server no realiza la creación.
- Si el atributo ```phoneNumber``` NO es una String vacía y no es un número de telefono con formato válido, el server no realiza la creación.
- Si el atributo ```email``` es una String vacía o su formato no es válido, el server no realiza la creación.
- Si el atributo ```roles``` no contiene ningún objeto en su interior, el server no realiza la creación.
- Si los atributos ```username```, ```phoneNumber```, ```email``` ya fueron utilizados en otro usuario registrado en la base de datos, el server no realiza la creación.

Si el objeto no levanta ninguna de las anteriores validaciones, se inserta en la base de datos.

- **UserService/ModifyUser**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
  "id": "1",
  "userWithRolesDTO": {
    "username": "nombreDeUsuario",
    "name": "nombre",
    "surname": "apellido",
    "phoneNumber": "+00 00 0000-0000",
    "email": "correo@electronico.com",
    "isActive": "trueOFalse",
    "roles": [
      {"name": "primerRol"},
      {"name":  "segundoRol"}
    ]
  }
}
```

El usuario puede tener tantos roles existan.

**Validaciones**:
- Si el atributo ```id``` no está siendo utilizado por ningún usuario en la base de datos, el server no realiza la modificación.
- Si el atributo ```username``` es una String vacía, el server no realiza la modificación.
- Si el atributo ```name``` es una String vacía, el server no realiza la modificación.
- Si el atributo ```surname``` es una String vacía, el server no realiza la modificación.
- Si el atributo ```phoneNumber``` NO es una String vacía y no es un número de telefono con formato válido, el server no realiza la modificación.
- Si el atributo ```email``` es una String vacía o su formato no es válido, el server no realiza la modificación.
- Si el atributo ```roles``` no contiene ningún objeto en su interior, el server no realiza la modificación.
- Si los atributos ```username```, ```phoneNumber```, ```email``` ya fueron utilizados en otro usuario registrado en la base de datos, el server no realiza la modificación.

Si el objeto no levanta ninguna de las anteriores validaciones, se modifica en la base de datos. Con el agregado de que si su atributo ```isActive``` es seteado a FALSE, se lo elimina de todos los eventos en los cuales estaba registrado.

- **UserService/DeleteUser**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
  "id": "1"
}
```

**Validaciones**:
- Si el atributo ```id``` no está siendo utilizado por ningún usuario en la base de datos, el server no lo elimina.

Si el objeto no levanta la validación, en la base de datos se setea su atributo ```isActive``` a FALSE (baja lógica) y se lo elimina de todos los eventos en los cuales estaba registrado.

- **UserService/GetUserList**:

Recibe por parámetro un objeto vacío:

```json
{
  
}
```

El server recupera todos los usuarios de la base de datos y los devuelve en su response.

- **UserService/GetActiveUserList**:

Recibe por parámetro un objeto vacío:

```json
{
  
}
```

El server recupera todos los usuarios de la base de datos que tengan su atributo ```isActive``` seteado a TRUE y los devuelve en su response.

- **UserService/GetUser**:

Recibe por parámetro un objeto con las siguientes propiedades:

```json
{
  "username": "nombreDeUsuario"
}
```

**Validaciones**:
- Si en la base de datos no se encuentra ningún usuario con el valor del atributo ```username```, el server responde con un Status NOT_FOUND.

Si el objeto no levanta la validación, el server devuelve al usuario solicitado junto a todos sus datos en su response.

## Métodos de EventService

- a

## Métodos de InventoryService

- a

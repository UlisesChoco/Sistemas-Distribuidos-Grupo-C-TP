# GraphQL Server

Servidor GraphQL en Node.js para realizar consultas a la base de datos MySQL.

## Tecnologías

- **Node.js + Express**: servidor web
- **GraphQL**: lenguaje de consulta
- **MySQL**: base de datos relacional
- **mysql2**: cliente para Node.js
- **graphql-tools**: para modularizar esquemas y resolvers

## Levantar el servidor

1. Instalar las dependencias:

```bash
npm install
```

2. Configurar la conexión a MySQL en db/connection.js

3. Abrir una terminal en la carpeta server y ejecutar el servidor

```bash
node server.js
```

4. Acceder a GraphiQL en:

```bash
http://localhost:4000/graphql
```

## Ejemplos de queries

* Obtener todas las donaciones

```bash
query {
  donations {
    id
    category
    description
    quantity
    deleted
  }
}
```

* Obtener una donación por ID

```bash
query {
  donation(id: "1") {
    category
    description
  }
}
```

## Ejemplos de Mutaciones

* Crear una donación

```bash
mutation {
  addDonation(input: {
    category: "ROPA",
    description: "Camperas",
    quantity: 10,
    deleted: false
  }) {
    id
  }
}
```

* Actualizar una donación

```bash
mutation {
  updateDonation(id: "1", input: {
    category: "ROPA",
    description: "Bufandas",
    quantity: 20,
    deleted: false
  }) {
    id
  }
}
```

* Eliminar una donación

```bash
mutation {
  deleteDonation(id: "1")
}
```

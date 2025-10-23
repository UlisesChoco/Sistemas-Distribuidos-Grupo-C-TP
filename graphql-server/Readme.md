# GraphQL Server

Servidor GraphQL en Node.js para realizar consultas a la base de datos MySQL.

## Tecnologías

- **Node.js + Express**: servidor web
- **GraphQL**: lenguaje de consulta
- **MySQL**: base de datos relacional
- **mysql2**: cliente para Node.js
- **graphql-tools**: para modularizar esquemas y resolvers

## Endpoints

- Ruta: http://localhost:4000/graphql

* Obtener todas las donaciones

```bash
query {
  donations {
    id
    category
    description
    quantity
    deleted
    lastDonationDate
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

* Obtener donaciones hechas o no por nosotros

```bash
query {
  donationsByMadeByOurselves(madeByOurselves: true) {
    category
    description
    quantity
    deleted
    lastDonationDate
  }
}
```

* Obtener una donación por filtros
```bash
query {
  donationsFiltered(category: "ROPA", dateFrom: "2022-01-02", dateTo: "2022-01-10", deleted: false, madeByOurselves: true) {
    category
    description
    quantity
    deleted
    lastDonationDate
  }
}
```


* Obtener todos los eventos

```bash
query {
  events{
    id
    name
    description
    is_completed
    date
    participants{
      id
      username
    }
    inventory{
      category
      description
      quantity
      distributionDate
    }
  }
}
```

* Obtener un evento por ID

```bash
query {
  event(id:"1"){
    id
    name
    description
    is_completed
    date
    participants{
      id
      username
    }
    inventory{
      category
      description
      quantity
      distributionDate
    }
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

const { gql } = require('graphql-tag');

module.exports = gql`
    # datos de la entidad
    type Filter {
        id: ID!
        name: String!
    }

    # datos para crear/modificar el filtro
    input FilterInput {
        name: String!
    }

    # solo para consultas
    type Query {
        filters: [Filter!]!
        filtersById(user_id: ID!): [Filter!]!
    }

    # cambios en la base de datos
    type Mutation {
        addFilter(input: FilterInput!): Filter!
        updateFilter(id: ID!, input: FilterInput!): Filter!
        deleteFilter(id: ID!): Boolean!
    }
`;
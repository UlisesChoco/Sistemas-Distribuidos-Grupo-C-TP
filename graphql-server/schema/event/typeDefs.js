const { gql } = require('graphql-tag');

// Esquema de la entidad event
// Falta un campo date, pero no existe una clase en GraphQl para manejar fechas, se tiene que crear un tipo escalar personalizado
// Falta hacer join con las demás tablas relacionadas a eventos (participantes, donaciones)
module.exports = gql`
    # define los campos que se pueden consultar
    type Event {
        id: ID!
        name: String!
        description: String!
        is_completed: Boolean!
    }

    # solo para consultas
    type Query {
        events: [Event!]!
        event(id: ID!): Event
    }
`;
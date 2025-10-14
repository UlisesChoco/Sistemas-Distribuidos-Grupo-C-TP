const express = require('express');
const { graphqlHTTP } = require('express-graphql');
const schema = require('../schema/mergeSchemas');

const app = express();

app.use('/graphql', graphqlHTTP({
  schema,
  graphiql: true
}));

app.listen(4000, () => {
  console.log('Servidor GraphQL corriendo en http://localhost:4000/graphql');
});
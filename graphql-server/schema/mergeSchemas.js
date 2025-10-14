const { makeExecutableSchema } = require('@graphql-tools/schema');
const { mergeTypeDefs, mergeResolvers } = require('@graphql-tools/merge');

// ---------------- importar los typeDefs y resolvers de las entidades ----------------
const donationTypeDefs = require('./donation/typeDefs');
const donationResolvers = require('./donation/resolvers');
const eventTypeDefs = require('./event/typeDefs');
const eventResolvers = require('./event/resolvers');

// mergea los typeDefs y resolvers de las entidades
const typeDefs = mergeTypeDefs([donationTypeDefs, eventTypeDefs]);
const resolvers = mergeResolvers([donationResolvers, eventResolvers]);

// crea y exporta el esquema ejecutable
module.exports = makeExecutableSchema({ typeDefs, resolvers });
const Donation = require('../../models/Donation');

// resolvers para la entidad donation
// similar a un service en java
module.exports = {
  Query: {
    donations: () => Donation.getAll(),
    donation: (_, { id }) => Donation.getById(id)
  },
  // métodos de prueba, el enunciado no los pide para esta entidad
  Mutation: {
    addDonation: (_, { input }) => Donation.create(input),
    updateDonation: (_, { id, input }) => Donation.update(id, input),
    deleteDonation: async (_, { id }) => Donation.delete(id)
  }
};
const DonationFilter = require('../../models/DonationFilter');

module.exports = {
  Query: {
    filters: () => DonationFilter.getAll(),
    filtersById: (_, { user_id }) => DonationFilter.getById(user_id)
  },

  Mutation: {
    addFilter: (_, { input }) => DonationFilter.create(input),
    updateFilter: (_, { id, input }) => DonationFilter.update(id, input),
    deleteFilter: async (_, { id }) => DonationFilter.delete(id)
  }
};
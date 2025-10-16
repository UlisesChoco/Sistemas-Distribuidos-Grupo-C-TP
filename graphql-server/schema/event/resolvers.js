const Event = require('../../models/Event');

// resolvers para la entidad event
// similar a un service en java
module.exports = {
  Query: {
    //events: () => Event.getAll(),
    events: () => Event.getAllWithParticipants(),
    event: (_, { id }) => Event.getById(id)
  }
};
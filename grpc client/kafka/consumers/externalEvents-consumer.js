const { Kafka } = require('kafkajs')
//import { Kafka } from 'kafkajs';

const kafka = new Kafka({
  clientId: 'external-events-consumer',
  brokers: ['kafka:9092'],
  //brokers: ['localhost:29092'],
});

const events = [];

const consumer = kafka.consumer({ 
  // para un group id unico
  groupId: `external-events-${Date.now()}` 
});

const startEventsConsumer = async () => {
  await consumer.connect();
  await consumer.subscribe({ topic: 'eventos-solidarios', fromBeginning: true });

  await consumer.run({
    //guarda los mensajes en una lista
    eachMessage: async ({ topic, partition, message }) => {
      const event = JSON.parse(message.value.toString());
      if (event) {
        events.push(event);
      }
    },
  });
}

const getExternalEvents = () => events;

// se exportan las funciones para iniciar el consumidor y para obtener los eventos
module.exports ={
  startEventsConsumer,
  getExternalEvents
}
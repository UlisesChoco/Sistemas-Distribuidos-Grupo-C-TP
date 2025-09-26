const { Kafka } = require('kafkajs')
//import { Kafka } from 'kafkajs';

const kafka = new Kafka({
  clientId: 'external-events-consumer',
  brokers: ['kafka:9092'],
  //brokers: ['localhost:29092'],
});

const events = [];

const consumer = kafka.consumer({ groupId: `external-events-${Date.now()}` });

const startEventsConsumer = async () => {
  await consumer.connect();
  await consumer.subscribe({ topic: 'eventos-solidarios', fromBeginning: true });

  await consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      const event = JSON.parse(message.value.toString());
      if (event) {
        events.push(event);
      }
    },
  });
}

const getExternalEvents = () => events;

module.exports ={
  startEventsConsumer,
  getExternalEvents
}
const { Kafka } = require('kafkajs');
const clientId = "donation-transfer-producer";
const broker = "kafka:9092";

const computeNewLastRequestId = async (organizationId, topic) => {
  const kafka = new Kafka({ clientId, brokers: [broker] });
  const consumer = kafka.consumer({ groupId: `id-checker-${organizationId}-${Date.now()}` });
  await consumer.connect();
  await consumer.subscribe({ topic, fromBeginning: false });

  let lastRequestId = 1;

  await consumer.run({
    eachMessage: async ({ message }) => {
      const data = JSON.parse(message.value.toString());
      if (data.request_id >= lastRequestId) lastRequestId = data.request_id + 1;
    },
  });

  // Esperar 1-2 segundos para leer mensajes recientes, luego desconectar
  await new Promise(r => setTimeout(r, 1000));
  await consumer.disconnect();

  return lastRequestId;
};

module.exports = { computeNewLastRequestId };

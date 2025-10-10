const { Kafka } = require('kafkajs');
const inventoryClient = require('../../clients/inventoryClient');

const MY_ORG_ID = process.env.MY_ORG_ID; 

const kafka = new Kafka({
    clientId: 'donation-transfer-consumer',
    brokers: ['kafka:9092'],
});

const consumer = kafka.consumer({ groupId: `transfer-group-${MY_ORG_ID || 'default'}` });

const runDonationTransferConsumer = async () => {
    if (!MY_ORG_ID) {
        console.warn("ADVERTENCIA: La variable de entorno MY_ORG_ID no está definida. El consumidor de transferencias no se iniciará.");
        return;
    }
    const topic = `transferencia-donaciones.${MY_ORG_ID}`;

    await consumer.connect();
    await consumer.subscribe({ topic: topic, fromBeginning: true });
    console.log(`Consumidor escuchando transferencias en el topic: ${topic}`);

    await consumer.run({
        eachMessage: async ({ message }) => {
            console.log(`¡Transferencia de donación recibida!`);
            try {
                const transferData = JSON.parse(message.value.toString());
                
                const itemsToUpdate = transferData.Lista_donaciones.map(d => ({
                    category: d.Categoria,
                    description: d.Descripcion,
                    quantity: parseInt(d.Cantidad, 10)
                }));

                if (itemsToUpdate.length > 0) {
                    const response = await inventoryClient.addOrUpdateStockAsync({ items: itemsToUpdate });
                    console.log('Inventario actualizado por transferencia recibida:', response.message);
                }
            } catch (error) {
                console.error('Error al actualizar inventario por transferencia:', error);
            }
        },
    });
};

module.exports = { runDonationTransferConsumer };
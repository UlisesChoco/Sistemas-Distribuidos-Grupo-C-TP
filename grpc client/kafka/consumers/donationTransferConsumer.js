// /GRPC CLIENT/KAFKA/CONSUMERS/donationTransferConsumer.js
const { Kafka } = require('kafkajs');
const inventoryClient = require('../../clients/inventoryClient');

// Leemos el ID de nuestra organización desde las variables de entorno
const MY_ORG_ID = process.env.MY_ORG_ID; 

const kafka = new Kafka({
    clientId: 'donation-transfer-consumer',
    brokers: ['kafka:9092'],
});

const consumer = kafka.consumer({ groupId: `transfer-group-${MY_ORG_ID || 'default'}` });

// Esta función se llamará desde app.js para iniciar el consumidor
const runDonationTransferConsumer = async () => {
    // Si no tenemos un ID de organización, el consumidor no se inicia.
    if (!MY_ORG_ID) {
        console.log("ADVERTENCIA: La variable de entorno MY_ORG_ID no está definida. El consumidor de transferencias no se iniciará.");
        return;
    }
    
    // Construimos el nombre del topic específico para nuestra ONG
    const topic = `transferencia-donaciones.${MY_ORG_ID}`;

    try {
        await consumer.connect();
        await consumer.subscribe({ topic: topic, fromBeginning: true });
        console.log(`Consumidor escuchando transferencias en el topic: ${topic}`);

        await consumer.run({
            eachMessage: async ({ message }) => {
                console.log(`¡Transferencia de donación recibida!`);
                try {
                    const transferData = JSON.parse(message.value.toString());
                    
                    // Mapeamos los datos del mensaje al formato que espera el servicio gRPC
                    const itemsToUpdate = transferData.Lista_donaciones.map(d => ({
                        category: d.Categoria,
                        description: d.Descripcion,
                        quantity: parseInt(d.Cantidad, 10)
                    }));

                    // Si hay items, llamamos al gRPC para que los sume/actualice en el inventario
                    if (itemsToUpdate.length > 0) {
                        const response = await inventoryClient.addOrUpdateStockAsync({ items: itemsToUpdate });
                        console.log('Inventario actualizado por transferencia recibida:', response.message);
                    }
                } catch (error) {
                    console.error('Error al procesar el mensaje de transferencia:', error);
                }
            },
        });
    } catch (error) {
        console.error(`Fallo al iniciar el consumidor de transferencias en el topic ${topic}:`, error);
    }
};

// Exportamos la función para poder llamarla desde app.js
module.exports = { runDonationTransferConsumer };
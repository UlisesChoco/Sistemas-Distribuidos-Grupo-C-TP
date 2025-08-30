const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const path = require('path');

// Ubicación del archivo .proto
const PROTO_PATH = path.join(__dirname, '../../proto/event.proto');

// carga del archivo .proto en un solo paquete
const packageDefinition = protoLoader.loadSync(PROTO_PATH, {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true,
});

// servicio
const proto = grpc.loadPackageDefinition(packageDefinition);

// crear cliente
const eventClient = new proto.EventService('localhost:9090', grpc.credentials.createInsecure());

/* --------------------- Implementaciones de las llamadas rpc --------------------- */

eventClient.rpcMethod({ /* request params */ }, (error, response) => { 
    if (error) {
        console.error('Error:', error);
    } else {
        console.log('Response:', response);
    }
});

// exportar el cliente
module.exports = eventClient;
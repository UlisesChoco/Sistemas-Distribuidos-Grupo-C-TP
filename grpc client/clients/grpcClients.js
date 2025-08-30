/* Este cliente centraliza los 3 servicios en uno solo en lugar de hacer un cliente por cada servicio*/

const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const path = require('path');

// Ubicación de los archivos .proto
const PROTO_PATHS = [
    path.join(__dirname, '../../proto/user.proto'),
    path.join(__dirname, '../../proto/event.proto'),
    path.join(__dirname, '../../proto/inventory.proto')
];

// carga de los archivos .proto en un solo paquete
const packageDefinition = protoLoader.loadSync(PROTO_PATHS, {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true,
});

// objeto con todos los servicios
const proto = grpc.loadPackageDefinition(packageDefinition);

// crear clientes
const userClient = new proto.UserService('localhost:9090', grpc.credentials.createInsecure());
const eventClient = new proto.EventService('localhost:9090', grpc.credentials.createInsecure());
const inventoryClient = new proto.InventoryService('localhost:9090', grpc.credentials.createInsecure());

/* --------------------- Implementaciones de las llamadas rpc --------------------- */

// ---------------- userclient ----------------
userClient.rpcMethod({ /* request params */ }, (error, response) => { 
    if (error) {
        console.error('Error:', error);
    } else {
        console.log('Response:', response);
    }
});

// ---------------- eventClient ----------------
eventClient.rpcMethod({ /* request params */ }, (error, response) => { 
    if (error) {
        console.error('Error:', error);
    } else {
        console.log('Response:', response);
    }
});

// ---------------- inventoryClient ----------------
inventoryClient.rpcMethod({ /* request params */ }, (error, response) => { 
    if (error) {
        console.error('Error:', error);
    } else {
        console.log('Response:', response);
    }
});

// exportar todos los clientes juntos
module.exports = {
  userClient,
  eventClient,
  inventoryClient
};
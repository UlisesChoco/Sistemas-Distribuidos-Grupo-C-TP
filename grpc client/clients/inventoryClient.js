const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const path = require('path');

// Ruta del archivo .proto
const PROTO_PATH = path.join(__dirname, '../proto/inventory.proto');

// Cargar el archivo .proto
const packageDefinition = protoLoader.loadSync(PROTO_PATH, {
  keepCase: true,
  longs: String,
  enums: String,
  defaults: true,
  oneofs: true,
});

// Servicio
const proto = grpc.loadPackageDefinition(packageDefinition);

// Crear cliente del InventoryService
const inventoryClient = new proto.InventoryService(
  'localhost:9090',
  grpc.credentials.createInsecure()
);

// ==================== Funciones de test (callback) ==================== //

function getInventoryList() {
  inventoryClient.GetInventoryList({}, (err, response) => {
    if (err) {
      console.error('Error GetInventoryList:', err);
    } else {
      console.log('Inventarios:', response.inventories);
    }
  });
}

function getInventoryById(id) {
  inventoryClient.GetInventoryById({ idInventory: id }, (err, response) => {
    if (err) {
      console.error('Error GetInventoryById:', err);
    } else {
      console.log('Inventario:', response);
    }
  });
}

function createInventory(inventory) {
  inventoryClient.CreateInventory(inventory, (err, response) => {
    if (err) {
      console.error('Error CreateInventory:', err);
    } else {
      console.log('Inventario creado:', response);
    }
  });
}

function updateInventory(inventory) {
  inventoryClient.UpdateInventory(inventory, (err, response) => {
    if (err) {
      console.error('Error UpdateInventory:', err);
    } else {
      console.log('Inventario actualizado:', response);
    }
  });
}

function deleteInventory(id) {
  inventoryClient.DeleteInventory({ idInventory: id }, (err, response) => {
    if (err) {
      console.error('Error DeleteInventory:', err);
    } else {
      console.log('Inventario eliminado:', response);
    }
  });
}

// ==================== Wrappers Promises (para Express) ==================== //

function promisify(fn, req) {
  return new Promise((resolve, reject) => {
    fn(req, (err, res) => {
      if (err) return reject(err);
      resolve(res);
    });
  });
}

module.exports = {
  inventoryClient,
  // callbacks (para test rápido en Node)
  getInventoryList,
  getInventoryById,
  createInventory,
  updateInventory,
  deleteInventory,
  // promesas (para router Express)
  getListAsync: () => promisify(inventoryClient.GetInventoryList.bind(inventoryClient), {}),
  getByIdAsync: (id) => promisify(inventoryClient.GetInventoryById.bind(inventoryClient), { idInventory: Number(id) }),
  createAsync: (dto) => promisify(inventoryClient.CreateInventory.bind(inventoryClient), dto),
  updateAsync: (dto) => promisify(inventoryClient.UpdateInventory.bind(inventoryClient), dto),
  deleteAsync: (id) => promisify(inventoryClient.DeleteInventory.bind(inventoryClient), { idInventory: Number(id) }),
};
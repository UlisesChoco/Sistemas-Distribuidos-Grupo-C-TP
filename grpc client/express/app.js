const express = require("express");
const cors = require("cors");
const app = express(); // con esto creamos la app express

app.use(cors()); // esto es para el front, para poder usar fetch() de js sin que el navegador llore
app.use(express.json()); // permitirle a express que pueda parsear requests y respones a json
app.use(express.urlencoded({ extended: true })); // Middleware para analizar datos de formularios

//para renderizar vistas
app.set('view engine', 'ejs');
app.set('views', '../../front/views');

/*
aca entremedio agregamos nuestros routers segun la funcionalidad. por ej:
const userRouter = require("./router/userRouter");
app.use(userRouter);
IMPORTANTE: los routers los creamos en la carpetita router que cree al mismo nivel de este script,
para mejor organizacion
*/

// routers
const userRouter = require('./router/user-router');
app.use("/user", userRouter);

const eventRouter = require("./router/events");
app.use("/events", eventRouter); // todas las rutas de eventos van a empezar con /events

// ==================== INVENTORY (gRPC Client integrado) ==================== //
// A partir de ahora, todas las rutas de inventario se gestionan en el router dedicado.
// Se eliminan los endpoints REST duplicados para evitar conflictos de enrutamiento.
// =========================================================================== //

// ==================== INVENTORY (Router de vistas EJS) ==================== //
const inventoryRouter = require("./router/inventory-router");
app.use("/inventories", inventoryRouter);
// =========================================================================== //

//IMPORTANTE dejar este get al final para que no reemplace a los routers
app.get('/', (req, res) => {
    res.render('index', {});
});

// ruta para archivos estáticos
app.use(express.static("../../front"));

// ========== COMENTARIO: ESTE BLOQUE ES EL PROBLEMA ==========
// ========== TU ARCHIVO `server.js` YA HACE ESTO ==========
// app.listen(port, () => {
//     console.log("Express app listening on port", port,".");
// });

module.exports = app;
const express = require("express");
const cors = require("cors");
const app = express(); // con esto creamos la app express
const port = 9091;
app.use(cors()); // esto es para el front, para poder usar fetch() de js sin que el navegador llore
app.use(express.json()); // permitirle a express que pueda parsear requests y respones a json

/*
aca entremedio agregamos nuestros routers segun la funcionalidad. por ej:
const userRouter = require("./router/userRouter");
app.use(userRouter);
IMPORTANTE: los routers los creamos en la carpetita router que cree al mismo nivel de este script,
para mejor organizacion
*/

const eventRouter = require("./router/events");
app.use("/events", eventRouter); // todas las rutas de eventos van a empezar con /events

// ruta para paginas estáticas
//app.use(express.static("../../front/views"));

app.listen(port, () => {
    console.log("Express app listening on port", port,".");
})
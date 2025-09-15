require('dotenv').config(); //cargamos variables de entorno
const express = require("express");
const cors = require("cors");
const jwtAuth = require("./auth/jwt-auth.js"); //middleware de autenticacion para proteger endpoints
const cookieParser = require("cookie-parser"); //lo vamos a usar para extraer el jwt de la cookie
const app = express(); // con esto creamos la app express
const port = 9091;
app.use(cors({credentials: true})); // esto es para el front, para poder usar fetch() de js sin que el navegador llore
app.use(express.json()); // permitirle a express que pueda parsear requests y respones a json
app.use(express.urlencoded({extended:true})); //permite a express extraer parametros enviados en forms html
app.use(cookieParser());

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

const eventRouter = require("./router/event-router");
app.use("/events", eventRouter); // todas las rutas de eventos van a empezar con /events

//IMPORTANTE dejar este get al final para que no reemplace a los routers
app.get('/', (req, res) => {
    res.render('index', {});
});

app.get('/home', jwtAuth, (req, res) => {
    res.render('home', {username: req.user.username, roles: req.user.roles});
});

// ruta para archivos estáticos
app.use(express.static("../../front"));

app.listen(port, () => {
    console.log("Express app listening on port", port,".");
})
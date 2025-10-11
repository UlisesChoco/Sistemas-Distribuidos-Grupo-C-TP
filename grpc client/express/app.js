require("dotenv").config(); // cargar variables de entorno desde .env

const express = require("express");
const cors = require("cors");
const jwtAuth = require("./auth/jwt-auth.js"); // middleware de autenticación
const cookieParser = require("cookie-parser");
const path = require("path");

// consumers de kafka
const eventsConsumer = require('../kafka/consumers/externalEvents');
const deletedEventsConsumer = require('../kafka/consumers/solidarityEventsDeleted.js');
const deletedRequestsConsumer = require('../kafka/consumers/donationRequestsDeleted.js');
const donationRequestsConsumer = require('../kafka/consumers/donationRequests.js');
const { runDonationOffersConsumer } = require('../kafka/consumers/donationOffers');

const app = express();
const port = process.env.PORT || 9091;

// Paths del front
const frontPath = path.join(__dirname, "../front");
const viewsPath = path.join(frontPath, "views");

// Middlewares globales
app.use(cors({ credentials: true }));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());

// Configuración de vistas
app.set("view engine", "ejs");
app.set("views", viewsPath);

// ================== Routers ================== //
const userRouter = require("./router/user-router");
app.use("/user", userRouter);

const eventRouter = require("./router/event-router");
app.use("/events", eventRouter);

const inventoryRouter = require("./router/inventory-router");
app.use("/inventories", inventoryRouter);

// Routers de Kafka
const donationRequestsRouter = require("./router/kafka-donation-requests-router.js");
app.use("/donationRequests", donationRequestsRouter);

const transferDonationRouter = require("./router/kafka-donation-transfer-router.js");
app.use("/transferDonation", transferDonationRouter);

const donationOffersRouter = require("./router/kafka-donation-offers-router.js");
app.use("/donation-offers", donationOffersRouter);

// Rutas principales
app.get("/", (req, res) => {
  res.render("index");
});

app.get("/home", jwtAuth, (req, res) => {
  res.render("home", { username: req.user.username, roles: req.user.roles });
});

app.get("/about", jwtAuth, (req, res) => {
  res.render("about", { username: req.user.username, roles: req.user.roles });
});

app.get("/contact", jwtAuth, (req, res) => {
  res.render("contact", { username: req.user.username, roles: req.user.roles });
});

app.get("/privacy", jwtAuth, (req, res) => {
  res.render("privacy", { username: req.user.username, roles: req.user.roles });
});

// Archivos estáticos
app.use("/css", express.static(path.join(frontPath, "css")));
app.use("/js", express.static(path.join(frontPath, "js")));
app.use("/img", express.static(path.join(frontPath, "img")));

// ================== Inicia consumidores de kafka ================== //
eventsConsumer.startEventsConsumer().catch(console.error);
deletedEventsConsumer.startDeletedEventsConsumer().catch(console.error);
deletedRequestsConsumer.startDeletedRequestsConsumer().catch(console.error);
donationRequestsConsumer.startConsuming();
runDonationOffersConsumer().catch(err => console.error("Fallo el consumidor de ofertas:", err)); 

app.listen(port, () => {
    console.log("Express app listening on port", port,".");
});

module.exports = app;
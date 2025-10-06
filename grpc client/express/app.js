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

const app = express();
const port = process.env.PORT || 9091;

// ================== Paths del front ================== //
const frontPath = path.join(__dirname, "../front");  // /app/front
const viewsPath = path.join(frontPath, "views");     // /app/front/views

// ================== Middlewares globales ================== //
app.use(cors({ credentials: true }));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());

// ================== Configuración de vistas ================== //
app.set("view engine", "ejs");
app.set("views", viewsPath);

// ================== Routers ================== //
const userRouter = require("./router/user-router");
app.use("/user", userRouter);

const eventRouter = require("./router/event-router");
app.use("/events", eventRouter);

// A partir de ahora, todas las rutas de inventario se gestionan en el router dedicado.
// Se eliminan los endpoints REST duplicados para evitar conflictos de enrutamiento.
// =========================================================================== //

// ==================== INVENTORY (Router de vistas EJS) ==================== //
const inventoryRouter = require("./router/inventory-router");
app.use("/inventories", inventoryRouter);

//routers que ofrecen funcionalidades de kafka
const donationRequestsRouter = require("./router/kafka-donation-requests-router.js");
app.use("/donationRequests", donationRequestsRouter);
const transferDonationRouter = require("./router/kafka-donation-transfer-router.js");
app.use("/transferDonation", transferDonationRouter);

// ================== Rutas principales ================== //
app.get("/", (req, res) => {
  res.render("index"); // busca /app/front/views/index.ejs
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

// ================== Archivos estáticos ================== //
app.use("/css", express.static(path.join(frontPath, "css")));
app.use("/js", express.static(path.join(frontPath, "js")));
app.use("/img", express.static(path.join(frontPath, "img"))); // opcional

// ================== Inicia consumidores de kafka ================== //
eventsConsumer.startEventsConsumer().catch(console.error);
deletedEventsConsumer.startDeletedEventsConsumer().catch(console.error);
deletedRequestsConsumer.startDeletedRequestsConsumer().catch(console.error);
donationRequestsConsumer.startConsuming();

app.listen(port, () => {
  console.log("Express app listening on port", port,".");
});

module.exports = app;

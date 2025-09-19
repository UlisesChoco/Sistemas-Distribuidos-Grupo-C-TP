require("dotenv").config(); // cargar variables de entorno desde .env
const express = require("express");
const cors = require("cors");
const jwtAuth = require("./auth/jwt-auth.js"); // middleware de autenticación
const cookieParser = require("cookie-parser");
const path = require("path");

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

const inventoryRouter = require("./router/inventory-router");
app.use("/inventories", inventoryRouter);

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

// ================== Arrancar servidor ================== //
app.listen(port, () => {
  console.log("✅ Express app listening on port", port);
});

module.exports = app;

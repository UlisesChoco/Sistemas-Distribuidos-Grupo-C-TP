require("dotenv").config(); // cargar variables de entorno desde .env
const express = require("express");
const cors = require("cors");
const jwtAuth = require("./auth/jwt-auth.js");
const cookieParser = require("cookie-parser");
const path = require("path");

const app = express();
const port = 9091;

// Rutas a los recursos del front (dentro del contenedor en /app/front)
const frontPath = path.join(__dirname, "../front");       // /app/front
const viewsPath = path.join(frontPath, "views");          // /app/front/views

// Middlewares globales
app.use(cors({ credentials: true }));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());

// Configuración del motor de vistas
app.set("view engine", "ejs");
app.set("views", viewsPath);

// Routers
const userRouter = require("./router/user-router");
app.use("/user", userRouter);

const eventRouter = require("./router/event-router");
app.use("/events", eventRouter);

// Rutas principales
app.get("/", (req, res) => {
  res.render("index"); // busca en /app/front/views/index.ejs
});

app.get("/home", jwtAuth, (req, res) => {
  res.render("home", { username: req.user.username, roles: req.user.roles });
});

// Archivos estáticos (CSS, JS, imágenes, etc.)
app.use("/css", express.static(path.join(frontPath, "css")));
app.use("/js", express.static(path.join(frontPath, "js")));
app.use("/img", express.static(path.join(frontPath, "img"))); // es opcional pero lo agregué

// Arrancar servidor
app.listen(port, () => {
  console.log("Express app listening on port", port, ".");
});


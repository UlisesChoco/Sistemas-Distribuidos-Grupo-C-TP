const jwt = require("jsonwebtoken"); //para validar token entrante

const authenticateToken = (req, res, next) => {
  const token = req.cookies.token;

  if (token == null) return res.render("index", {});

  jwt.verify(token, process.env.JWT_PRIVATE_KEY, (err, payload) => {
    if (err) {
        return res.render("index", {});
    };
    req.user = {username: payload.sub, roles: payload.roles};
    next();
  });
};

module.exports = authenticateToken;
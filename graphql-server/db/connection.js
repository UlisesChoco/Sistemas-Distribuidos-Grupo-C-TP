const mysql = require('mysql2/promise');

// TODO: dockerizar server
const pool = mysql.createPool({
  host: 'localhost',
  user: 'root',
  password: 'root',
  database: 'graphql',
  port: 3306 
});

module.exports = pool;
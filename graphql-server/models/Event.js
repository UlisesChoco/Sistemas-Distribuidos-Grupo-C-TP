const pool = require('../db/connection');

// querys a la bd para la entidad event 
// similar a un repository en java
const Donation = {
    getAll: async () => {
        const [rows] = await pool.query('SELECT * FROM events');
        return rows;
    },
    getById: async (id) => {
        const [rows] = await pool.query('SELECT * FROM events WHERE id = ?', [id]);
        return rows[0];
    }
}

module.exports = Donation;
const pool = require('../db/connection');

// querys a la bd para la entidad event 
// similar a un repository en java
const Event = {
    getAll: async () => {
        const [rows] = await pool.query('SELECT * FROM events');
        return rows.map(event => ({
            ...event,
            // convierte el tipo de dato Buffer en booleano
            is_completed: Boolean(event.is_completed)
        }));
    },
    getById: async (id) => {
        const [rows] = await pool.query('SELECT * FROM events WHERE id = ?', [id]);
        const event = rows[0];
        return {
            ...event,
            is_completed: Boolean(event.is_completed)
        };
    }
}

module.exports = Event;
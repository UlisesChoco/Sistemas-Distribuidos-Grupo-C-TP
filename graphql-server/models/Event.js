const pool = require('../db/connection');

// querys a la bd para la entidad event 
// similar a un repository en java
const Event = {
    getAll: async () => {
        const [rows] = await pool.query('SELECT * FROM events');
        return rows.map(event => ({
            ...event,
            // conversión Buffer -> Boolean
            is_completed: Boolean(event.is_completed),
            // conversión Datetime -> String
            date: new Date(event.date).toISOString() 
        }));
    },
    getAllWithParticipants: async () => {
        const [events] = await pool.query('SELECT * FROM events');
        const [participants] = await pool.query(`
            SELECT eu.id_event, u.id, u.username
            FROM event_user eu
            JOIN users u ON eu.id_user = u.id
        `);

        const grouped = events.map(event => ({
            ...event,
            is_completed: Boolean(event.is_completed),
            date: new Date(event.date).toISOString(),
            participants: participants
            .filter(p => p.id_event === event.id)
            .map(p => ({
                id: p.id,
                username: p.username,
            })),
        }));

        return grouped;
    },
    getById: async (id) => {
        const [rows] = await pool.query('SELECT * FROM events WHERE id = ?', [id]);
        const event = rows[0];

        const [participants] = await pool.query(`
            SELECT u.id, u.username
            FROM event_user eu
            JOIN users u ON eu.id_user = u.id
            WHERE eu.id_event = ? `, [id]
        );

        return {
            ...event,
            is_completed: Boolean(event.is_completed),
            date: new Date(event.date).toISOString(),
            participants: participants
            .map(p => ({
                id: p.id,
                username: p.username,
            })),
        };
    }
}

module.exports = Event;
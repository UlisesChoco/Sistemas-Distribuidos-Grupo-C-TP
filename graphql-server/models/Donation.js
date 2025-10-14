const pool = require('../db/connection');

// querys a la bd para la entidad donation
// similar a un repository en java
const Donation = {
    getAll: async () => {
        const [rows] = await pool.query('SELECT * FROM donations');
        return rows;
    },
    getById: async (id) => {
        const [rows] = await pool.query('SELECT * FROM donations WHERE id = ?', [id]);
        return rows[0];
    },
    // métodos de prueba, el enunciado no los pide para esta entidad
    create: async ({ category, description, quantity, deleted }) => {
        const [result] = await pool.query(
            'INSERT INTO donations (category, description, quantity, deleted) VALUES (?, ?, ?, ?)',
            [category, description, quantity, deleted]
        );
        return {
            id: result.insertId,
            category,
            description,
            quantity,
            deleted
        };
    },
    update: async ( id, { category, description, quantity, deleted }) => {
        await pool.query(
            'UPDATE donations SET category = ?, description = ?, quantity = ?, deleted = ? WHERE id = ?',
            [category, description, quantity, deleted, id]
        );
        return {
            id,
            category,
            description,
            quantity,
            deleted
        };
    },
    delete: async (id) => {
        const [result] = await pool.query('UPDATE donations SET deleted = true WHERE id = ?', [id]);
        return result.affectedRows > 0;
    }
}

module.exports = Donation;
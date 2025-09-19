const express = require('express');
const router = express.Router();

const {
    getListAsync,
    getByIdAsync,
    createAsync,
    updateAsync,
    deleteAsync,
    getAvailableAsync
} = require('../../clients/inventoryClient');

// Middleware para pasar los roles a todas las vistas
router.use((req, res, next) => {
    // Esto asume que los roles del usuario están en req.user.roles
    // Si la información está en otro lugar, como req.session, ajusta el código.
    const roles = req.user ? req.user.roles : []; 
    res.locals.roles = roles;
    next();
});

// Rutas de Vistas (GET)
router.get('/', async (req, res) => {
    try {
        const response = await getListAsync();
        // Corregido: 'inventory' -> 'inventories'
        res.render('inventories/list', { inventories: response.inventories || [] });
    } catch (err) {
        console.error('Error al obtener la lista de inventarios:', err);
        res.status(500).send('Error al obtener la lista de inventarios.');
    }
});

router.get('/create', (req, res) => {
    // Corregido: 'inventory' -> 'inventories'
    res.render('inventories/createInventory');
});

router.get('/:id/edit', async (req, res) => {
    try {
        const id = req.params.id;
        const response = await getByIdAsync(id);
        // Corregido: 'inventory' -> 'inventories'
        res.render('inventories/editInventory', { inventory: response });
    } catch (err) {
        console.error('Error al obtener el inventario para editar:', err);
        res.status(404).send('Inventario no encontrado.');
    }
});

// ==================== NUEVO ENDPOINT (JSON) ==================== //
// Devuelve un JSON simple con los inventarios activos (id, description, quantity)
// Ruta: GET /inventories/available
router.get('/available', async (req, res) => {
    try {
        const response = await getAvailableAsync();
        const inventories = (response && response.inventories) ? response.inventories : [];

        // Mapear a formato simple pedido por tu compañero
        const simplified = inventories.map(inv => ({
            id: Number(inv.idInventory || 0),
            description: inv.description || '',
            quantity: Number(inv.quantity || 0)
        }));

        res.json(simplified);
    } catch (err) {
        console.error('Error al obtener inventarios disponibles:', err);
        res.status(500).json({ error: 'Error al obtener inventarios disponibles' });
    }
});
// ============================================================ //

// Rutas de Acciones (POST)
router.post('/', async (req, res) => {
    try {
        const { category, description, quantity } = req.body;
        const inventoryDto = {
            category,
            description,
            quantity: Number(quantity)
        };
        await createAsync(inventoryDto);
        res.redirect('/inventories');
    } catch (err) {
        console.error('Error al crear el inventario:', err);
        res.status(500).send('Error al crear el inventario.');
    }
});

router.post('/:id/update', async (req, res) => {
    try {
        const id = req.params.id;
        const { description, quantity } = req.body;
        const inventoryDto = {
            idInventory: Number(id),
            description,
            quantity: Number(quantity)
        };
        await updateAsync(inventoryDto);
        res.redirect('/inventories');
    } catch (err) {
        console.error('Error al actualizar el inventario:', err);
        res.status(500).send('Error al actualizar el inventario.');
    }
});

router.post('/:id/delete', async (req, res) => {
    try {
        const id = req.params.id;
        await deleteAsync(id);
        res.redirect('/inventories');
    } catch (err) {
        console.error('Error al eliminar el inventario:', err);
        res.status(500).send('Error al eliminar el inventario.');
    }
});

module.exports = router;
const express = require('express');
const router = express.Router();

const {
    getListAsync,
    getByIdAsync,
    createAsync,
    updateAsync,
    deleteAsync
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
        
        res.render('inventories/list', { inventories: response.inventories || [] });
    } catch (err) {
        console.error('Error al obtener la lista de inventarios:', err);
        res.status(500).send('Error al obtener la lista de inventarios.');
    }
});

router.get('/create', (req, res) => {
    res.render('inventories/createInventory');
});

router.get('/:id/edit', async (req, res) => {
    try {
        const id = req.params.id;
        const response = await getByIdAsync(id);
        
        res.render('inventories/editInventory', { inventory: response });
    } catch (err) {
        console.error('Error al obtener el inventario para editar:', err);
        res.status(404).send('Inventario no encontrado.');
    }
});

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
const express = require('express');
const router = express.Router();
const eventClient = require('../../clients/eventClient');
const grpc = require('@grpc/grpc-js');


router.post('/create', (req, res) => {
    const { name, description, date, participants } = req.body;

    //extraigo el token desde headers
    const token = req.headers.authorization?.replace('Bearer ', '');

    //validaciones basicas
    if (!name || !description || !date) {
        return res.status(400).json({ error: 'Faltan campos obligatorios' });
    }

    // validacion fecha no anterior a hoy
    const eventSeconds = Math.floor(new Date(date).getTime() / 1000);
    const currentDateSeconds = Math.floor(new Date().getTime() / 1000);

    if (eventSeconds < currentDateSeconds) {
        return res.status(400).json({ error: 'La fecha no puede ser anterior a la actual' });
    }

    //estructura del request para gRPC
    const requestBody = {
        name: req.body.name,
        description,
        date: {
            seconds: eventSeconds,
            nanos: 0,
        },
        participants: participants || [], // si no hay participantes, se envía una lista vacía
    };

    //agrego el token a los metadata
    const metadata = new grpc.Metadata();
    metadata.add('Authorization', 'Bearer ' + token);

    //llamo a la función
    eventClient.CreateEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.put('/modifyEvent', (req, res) => {
    const { id, name, description, date, participants, is_completed } = req.body;

    const token = req.headers.authorization?.replace('Bearer ', '');

    if (!name || !description || !date) {
        return res.status(400).json({ error: 'Faltan campos obligatorios' });
    }

    const eventSeconds = Math.floor(new Date(date).getTime() / 1000);
    
    const requestBody = {
        id,
        name,
        description,
        date: {
            seconds: eventSeconds,
            nanos: 0,
        },
        participants: participants || [],
        is_completed: is_completed || false, // si no se especifica, por defecto es false
    };

    const metadata = new grpc.Metadata();
    metadata.add('Authorization', 'Bearer ' + token);

    eventClient.ModifyEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.delete('/deleteEvent/:id', (req, res) => {
    const eventId = req.params.id;
    //const token = req.headers.authorization?.replace('Bearer ', '');
    const token = "token123";

    if (!eventId) {
        return res.status(400).json({ error: 'Falta el ID del evento' });
    }

    const requestBody = { id: eventId };

    const metadata = new grpc.Metadata();
    metadata.add('Authorization', 'Bearer ' + token);

    eventClient.DeleteEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.post('/assignUserToEvent', (req, res) => {
    const { eventId, userId } = req.body;

    const token = req.headers.authorization?.replace('Bearer ', '');

    if (!eventId || !userId) {
        return res.status(400).json({ error: 'Faltan campos obligatorios' });
    }

    const requestBody = { event_id: eventId, user_id: userId };

    const metadata = new grpc.Metadata();
    metadata.add('Authorization', 'Bearer ' + token);

    eventClient.AssignUserToEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});


router.post('/deleteUserFromEvent', (req, res) => {
    const { eventId, userId } = req.body;

    const token = req.headers.authorization?.replace('Bearer ', '');

    if (!eventId || !userId) {
        return res.status(400).json({ error: 'Faltan campos obligatorios' });
    }

    const requestBody = { event_id: eventId, user_id: userId };

    const metadata = new grpc.Metadata();
    metadata.add('Authorization', 'Bearer ' + token);

    eventClient.DeleteUserFromEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.get('/getEvent/:id', (req, res) => {
    const eventId = req.params.id;
    const token = req.headers.authorization?.replace('Bearer ', '');

    if (!eventId) {
        return res.status(400).json({ error: 'Falta el ID del evento' });
    }

    const requestBody = { id: eventId };

    const metadata = new grpc.Metadata();
    metadata.add('Authorization', 'Bearer ' + token);

    eventClient.GetEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.get('/getEventsWithoutParticipants', (req, res) => {

    const token = req.headers.authorization?.replace('Bearer ', '');

    //se necesita el id del usuario logueado
    const id = 1;

    const requestBody = {id}; 

    const metadata = new grpc.Metadata();
    metadata.add('Authorization', 'Bearer ' + token);

    eventClient.GetEventsWithoutParticipantsList(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.get('/getEventsWithParticipants', (req, res) => {

    const token = req.headers.authorization?.replace('Bearer ', '');

    const requestBody = {}; // No se necesitan parámetros para este request

    const metadata = new grpc.Metadata();
    metadata.add('Authorization', 'Bearer ' + token);

    eventClient.GetEventsWithParticipantsList(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

//-------------------------- rutas de vistas -------------------------------


router.get('/edit/:id', (req, res) => {
    res.render('events/editEvent', {id: req.params.id});
});

router.get('/create', (req, res) => {
    res.render('events/createEvent', {});
});

router.get('/', (req, res) => {

    //extraigo el token desde una cookie
    //const token = req.cookies.token;
    const token = "Token123"

    //const token = req.headers.authorization?.replace('Bearer ', '');

    //se necesita el id del usuario logueado
    const id = 1;

    const requestBody = {id}; 

    const metadata = new grpc.Metadata();
    metadata.add('Authorization', 'Bearer ' + token);

    eventClient.GetEventsWithoutParticipantsList(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.render('events/events', {events: response.events});
    });

});

module.exports = router;
const express = require('express');
const router = express.Router();
const eventClient = require('../../clients/eventClient');
const grpc = require('@grpc/grpc-js');
const jwtAuth = require("../auth/jwt-auth");

router.post('/create', (req, res) => {
    const { name, description, date, participants } = req.body;

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
    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null){
        metadata.add('Authorization', 'Bearer ' + token);
    }

    //llamo a la función
    eventClient.CreateEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.put('/modifyEvent', (req, res) => {
    const { id, name, description, date, participants, is_completed } = req.body;

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

    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null){
        metadata.add('Authorization', 'Bearer ' + token);
    }

    eventClient.ModifyEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.delete('/deleteEvent/:id', (req, res) => {
    const eventId = req.params.id;

    if (!eventId) {
        return res.status(400).json({ error: 'Falta el ID del evento' });
    }

    const requestBody = { id: eventId };

    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null){
        metadata.add('Authorization', 'Bearer ' + token);
    }

    eventClient.DeleteEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.post('/assignUserToEvent', jwtAuth, (req, res) => {
    const { eventId } = req.body;

    if (!eventId) {
        return res.status(400).json({ error: 'Faltan campos obligatorios' });
    }

    const requestBody = { event_id: eventId, user_id: req.user.id };

    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null){
        metadata.add('Authorization', 'Bearer ' + token);
    }

    eventClient.AssignUserToEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});


router.post('/deleteUserFromEvent', jwtAuth, (req, res) => {
    const { eventId } = req.body;

    if (!eventId) {
        return res.status(400).json({ error: 'Faltan campos obligatorios' });
    }

    const requestBody = { event_id: eventId, user_id: req.user.id };

    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null){
        metadata.add('Authorization', 'Bearer ' + token);
    }

    eventClient.DeleteUserFromEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.get('/getEvent/:id', (req, res) => {
    const eventId = req.params.id;

    if (!eventId) {
        return res.status(400).json({ error: 'Falta el ID del evento' });
    }

    const requestBody = { id: eventId };

    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null){
        metadata.add('Authorization', 'Bearer ' + token);
    }

    eventClient.GetEvent(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.get('/getEventsWithoutParticipants', jwtAuth, (req, res) => {

    const id = req.user.id;
    const requestBody = {id}; 

    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null){
        metadata.add('Authorization', 'Bearer ' + token);
    }

    eventClient.GetEventsWithoutParticipantsList(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.get('/getEventsWithParticipants', (req, res) => {

    const requestBody = {}; // No se necesitan parámetros para este request

    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null){
        metadata.add('Authorization', 'Bearer ' + token);
    }

    eventClient.GetEventsWithParticipantsList(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

router.post('/registerEventInventory', jwtAuth, (req, res) =>{

    const requestBody = {
        user_id: req.user.id,
        event_id: req.body.event_id,
        inventory_id: req.body.inventory_id,
        quantity: req.body.quantity
    };

    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null){
        metadata.add('Authorization', 'Bearer ' + token);
    }

    eventClient.RegisterEventInventory(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
})

router.get('/getEventInventory/:id', (req, res) => {

    const id = req.params.id;
    const requestBody = {id};

    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null){
        metadata.add('Authorization', 'Bearer ' + token);
    }

    eventClient.GetEventInventory(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(response);
    });
});

//-------------------------- rutas de vistas -------------------------------


router.get('/edit/:id', jwtAuth ,(req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("COORDINADOR")){
        res.render("error/error-403");
        return;
    }
    res.render('events/editEvent', {eventId: req.params.id});
});

router.get('/create', jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("COORDINADOR")){
        res.render("error/error-403");
        return;
    }
    res.render('events/createEvent', {userId: req.user.id});
});

router.get('/donationsRegistry/:id/:name', jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("COORDINADOR")){
        res.render("error/error-403");
        return;
    }
    res.render('events/eventInventory', {eventId: req.params.id, eventName: req.params.name});
});

router.get('/', jwtAuth ,(req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("COORDINADOR") && !req.user.roles.includes("VOLUNTARIO")){
        res.render("error/error-403");
        return;
    }

    const id = req.user.id;
    const requestBody = {id}; 

    const token = req.cookies.token;
    const metadata = new grpc.Metadata();
    if(token != null){
        metadata.add('Authorization', 'Bearer ' + token);
    }

    eventClient.GetEventsWithoutParticipantsList(requestBody, metadata, (err, response) => {
        if (err) return res.status(500).json({ error: err.message });
        res.render('events/events', {events: response.events, roles: req.user.roles});
    });

});

module.exports = router;
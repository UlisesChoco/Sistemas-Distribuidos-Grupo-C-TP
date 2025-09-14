const express = require('express');
const userRouter = express.Router();
const grpc = require('@grpc/grpc-js');
const grpcUserService = require('../../clients/userClient');
userRouter.use(express.json());

userRouter.post("/login", (req, res) => {
    const username = req.body.username;
    const password = req.body.password;
    const requestData = {username: username, password: password};

    grpcUserService.Login(requestData, (err, response) => {
        onServerResponse(res, err, response, (response) =>
            res.send({user: response.userWithRolesDTO, token: response.token})
        )
    });
});

userRouter.post("/create", (req, res) => {
    const metadata = buildMetadata(req);
    
    const requestData = {
        username: req.body.username,
        name: req.body.name,
        surname: req.body.surname,
        phoneNumber: req.body.phoneNumber,
        email: req.body.email,
        isActive: req.body.isActive,
        roles: req.body.roles
    };

    grpcUserService.CreateUser(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response, (response) => 
            res.send({message: response.message, succeeded: response.succeeded})
        )
    });
});

userRouter.post("/modify", (req, res) => {
    const id = req.body.id;
    const userWithRolesDTO = req.body.userWithRolesDTO;
    const metadata = buildMetadata(req);

    const requestData = {id: id, userWithRolesDTO: userWithRolesDTO};

    grpcUserService.ModifyUser(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response, (response) => 
            res.send({message: response.message, succeeded: response.succeeded})
        )
    });
});

userRouter.post("/delete", (req, res) => {
    const id = req.body.id;
    const metadata = buildMetadata(req);

    const requestData = {id: id};

    grpcUserService.DeleteUser(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response, (response) =>
            res.send({message: response.message, succeeded: response.succeeded})
        )
    });
});

userRouter.get("/list", (req, res) => {
    const metadata = buildMetadata(req);

    const requestData = {};

    grpcUserService.GetUserList(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response, (response) => res.send({users: response.users}))
    });
});

userRouter.get("/active-list", (req, res) => {
    const metadata = buildMetadata(req);

    const requestData = {};

    grpcUserService.GetActiveUsersList(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response, (response) => res.send({users: response.users}))
    });
});

userRouter.get("/:username", (req, res) => {
    const username = req.params.username;
    const metadata = buildMetadata(req);

    const requestData = {username: username};

    grpcUserService.GetUser(requestData, metadata, (err, response) => {
        onServerResponse(res, err, response,
            (response) => res.send({id: response.id, userWithRolesDTO: response.userWithRolesDTO})
        )
    });
});

function onServerResponse(res, err, response, sendResponse) {
    if(err) {
        res.send(err);
        return;
    }
    sendResponse(response);
}

function buildMetadata(req) {
    const token = req.headers.authorization;
    const metadata = new grpc.Metadata();
    if(token != null) {
        metadata.add('Authorization', token);
    }
    return metadata;
}

module.exports = userRouter;
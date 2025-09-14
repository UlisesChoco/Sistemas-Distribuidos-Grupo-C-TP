const express = require('express');
const userRouter = express.Router();
const grpc = require('@grpc/grpc-js');
const grpcUserService = require('../../clients/userClient');

userRouter.post("/login", (req, res) => {
    const username = req.body.username;
    const password = req.body.password;

    grpcUserService.Login(
        {username: username, password: password},
        (err, response) => {
            if(err) {
                res.send(err);
                return;
            }
            res.send({user: response.userWithRolesDTO, token: response.token});
        }
    );
});

userRouter.post("/modify", (req, res) => {
    const userWithRolesDTO = req.body.user;
    const token = req.headers.authorization;
    const metadata = new grpc.Metadata();
    metadata.add('Authorization', token);

    grpcUserService.Modify(userWithRolesDTO, metadata, (err, response) => {
        if(err) {
            console.log("Fallé en el rpc");
            res.send(err);
            return;
        }
        res.send({message: response.message, succeeded: response.succeeded})
    });
});

module.exports = userRouter;
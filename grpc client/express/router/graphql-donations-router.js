const express = require('express');
const graphQLDonationsRouter = express.Router();
const jwtAuth = require('../auth/jwt-auth');

//views
graphQLDonationsRouter.get('/our-donations-report', jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")) {
        res.render("error/error-403");
        return;
    }
    res.render('donations/ourDonationsReport');
});

graphQLDonationsRouter.get('/received-donations-report', jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")) {
        res.render("error/error-403");
        return;
    }
    res.render('donations/receivedDonationsReport');
});

module.exports = graphQLDonationsRouter;
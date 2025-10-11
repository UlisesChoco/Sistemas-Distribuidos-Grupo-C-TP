// /GRPC CLIENT/EXPRESS/ROUTER/kafka-donation-transfer-router.js
const express = require('express');
const jwtAuth = require("../auth/jwt-auth");
const donationTransferRouter = express.Router();
const donationTransferProducer = require("../../kafka/producers/donationTransferProducer");
const { updateAsync, getByIdAsync } = require('../../clients/inventoryClient');

//view
donationTransferRouter.get("/create/:organizationId", jwtAuth, (req, res) => {
    if(!req.user.roles.includes("PRESIDENTE") && !req.user.roles.includes("VOCAL")) {
        res.render("error/error-403");
        return;
    }
    res.render("donations/transferDonation", { organizationId: req.params.organizationId });
});

//api
donationTransferRouter.post("/create", jwtAuth, async (req, res) => {
    const organizationId = req.body.organizationId;
    const donations = req.body.donations;

    try {
        for (const donation of donations) {
            const inventoryFromDB = await getByIdAsync(donation.id);

            if (inventoryFromDB.quantity < donation.quantity) {
                return res.send({
                    succeeded: false,
                    message: `Fallo al transferir la donación. El inventario de ${inventoryFromDB.description} no tiene la cantidad suficiente como para transferir la cantidad indicada.`
                });
            }

            const newQuantity = inventoryFromDB.quantity - donation.quantity;

            const inventoryDto = {
                idInventory: donation.id,
                description: donation.description,
                quantity: newQuantity
            };
            
            await updateAsync(inventoryDto);
        }

        donationTransferProducer.transferDonation(organizationId, 1, donations);
        return res.send({ succeeded: true, message: "Donación transferida correctamente." });

    } catch(err) {
        console.error("Fallo al transferir la donación:", err);
        return res.send({ succeeded: false, message: "Fallo al transferir la donación. Intentar de nuevo, por favor." });
    }
});

module.exports = donationTransferRouter;
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
        // Itera sobre cada item que se quiere donar
        for (const donation of donations) {
            // Obtiene el estado actual del inventario desde el gRPC server
            const inventoryFromDB = await getByIdAsync(donation.id);

            // Valida que haya stock suficiente
            if (inventoryFromDB.quantity < donation.quantity) {
                return res.send({
                    succeeded: false,
                    message: `Fallo al transferir la donación. El inventario de ${inventoryFromDB.description} no tiene la cantidad suficiente como para transferir la cantidad indicada.`
                });
            }

            // Calcula la nueva cantidad (stock actual - cantidad a donar)
            const newQuantity = inventoryFromDB.quantity - donation.quantity;

            // Prepara el objeto para enviar al gRPC server
            const inventoryDto = {
                idInventory: donation.id,
                description: donation.description,
                quantity: newQuantity
            };
            
            // Llama al gRPC server para que actualice la base de datos con la nueva cantidad.
            // Esta es la operación que descuenta el stock.
            await updateAsync(inventoryDto);
        }

        // Si todas las actualizaciones del inventario fueron exitosas,
        // recién ahí se notifica por Kafka a la otra organización.
        donationTransferProducer.transferDonation(organizationId, 1, donations);
        return res.send({ succeeded: true, message: "Donación transferida correctamente." });

    } catch(err) {
        console.error("Fallo al transferir la donación:", err);
        return res.send({ succeeded: false, message: "Fallo al transferir la donación. Intentar de nuevo, por favor." });
    }
});

module.exports = donationTransferRouter;
//TODO
async function loadDonations() {
    const table = document.getElementById("donations-list");

    //se carga el registro de donaciones mediante un fetch
    //simulado
    const donaciones = ["remeras", "juguetes", "comida"];

    for(let i = 0; i<=2; i++){
        table.innerHTML+=`
            <tr>
                <td>Usuario ${i+1}</td>
                <td>${donaciones[i]}</td>
                <td>${(i+1)*10}</td>
            </tr>
        `;
    }
}

async function loadInventory() {
    const select = document.getElementById("donation-select");

    //se carga el inventario disponible mediante un fetch
    //simulado
    const donaciones = ["remeras", "juguetes", "comida"];

    for(let i = 0; i<=2; i++){
        select.innerHTML+=`
            <option value="opcion${i}">${donaciones[i]}</option>
        `;
    }
}

window.addEventListener('DOMContentLoaded', () =>{
    loadDonations();
    loadInventory();
});

function registerDonation(eventId){
    
}
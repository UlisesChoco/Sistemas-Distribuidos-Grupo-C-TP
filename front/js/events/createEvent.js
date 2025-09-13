
/*
al entrar al form o al tocar un botón se deberían cargar los usuarios activos
para poder agregarlos como participantes
*/
async function loadActiveUsers() {
    const usersSelect = document.getElementById('participants');
    usersSelect.innerHTML = ' '; // limpia el select

    //acá deberia hacer un fetch al endpoint de usuarios y cargar la lista
    //se simula la carga de usuarios activos
    const usernames = ['username 1', 'username 2'];

    for (let id = 1; id <= 2; id++) {
        usersSelect.innerHTML += `     
            <div>
                <input type="checkbox" id="user${id}" name="participants" value="${id}">
                <label for="user${id}">${usernames[id - 1]}</label>
            </div> 
        `;
    }
}

// se carga la lista de usuarios al cargar la página
window.addEventListener('DOMContentLoaded', loadActiveUsers);

function createNewEvent() {
    const form = document.getElementById("form");

    const formData = new FormData(form);

    const name = formData.get("name");
    const description = formData.get("description");
    const date = formData.get("date");

    const participants = [];
    //guardo en una lista los ids de los participantes seleccionados
    formData.getAll("participants").forEach(id => {
        participants.push({ id: parseInt(id) });
    });

    const eventData = {
        name: name,
        description: description,
        date: date,
        participants: participants
    };

    const headers = new Headers({
        "Content-Type": "application/json",
        "Authorization": "Bearer " + "token123" // Simulando un token de autenticación
    });

    fetch(`http://localhost:9091/events/create`, {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(eventData)
    })
    .then(response => {
        return response.json().then(data => {
            if (!response.ok) {
                throw new Error(data.error || response.statusText);
            }
            return data;
        });
    })
    .then(data => {
        console.log('Success:', data);
        alert("Evento creado con éxito");
        form.reset();
    }).catch(error => {
        console.error('Error:', error.message);
        alert("Error al crear el evento: " + error.message);
    });
}
//extraigo el id pasado como parametro en la url
const params = new URLSearchParams(window.location.search);
const selectedEventId = params.get("id");
console.log("id recibido:", selectedEventId);

/*
al entrar a la página, cargo el evento a editar
*/
async function getEvent() {
    const headers = new Headers({
        "Content-Type": "application/json",
        "Authorization": "Bearer " + "token123" // Simulando un token de autenticación
    });

    fetch(`http://localhost:9091/events/getEvent/${selectedEventId}`, {
        method: 'GET',
        headers: headers
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(response.statusText);
        }
        return response.json();
    })
    .then(data => {
        //console.log('Success:', data);

        loadForm(data);
        loadActiveUsers(data.participants);
    }).catch(error => {
        console.error('Error:', error);
    });
}

async function loadForm(eventData) {
    const form = document.getElementById("form");
    form.name.value = eventData.name;
    form.description.value = eventData.description;
    // Convierte seconds y nanos a un objeto Date
    const eventDate = new Date(eventData.date.seconds * 1000 + Math.floor(eventData.date.nanos / 1000000));
    // Formatea la fecha y hora en formato 'YYYY-MM-DDTHH:MM' para el input tipo datetime-local
    form.date.value = eventDate.toISOString().slice(0,16);
    form.isCompleted.checked = eventData.is_completed;
}

/*
al entrar al form o al tocar un botón se deberían cargar los usuarios activos
para poder agregarlos como participantes
*/
async function loadActiveUsers(participants) {
    const usersSelect = document.getElementById('participants');
    usersSelect.innerHTML = ' '; // limpia el select

    //acá deberia hacer un fetch al endpoint de usuarios y cargar la lista

    //simulo usuarios activos

    const usernames = ['username 1', 'username 2'];

    for (let id = 1; id <= 2; id++) {
        //verifico si el usuario ya es participante del evento
        const isChecked = participants && participants.some(p => p.id == id);

        usersSelect.innerHTML += `     
            <div>
            <input type="checkbox" id="user${id}" name="participants" value="${id}"${isChecked ? ' checked' : ''}>
            <label for="user${id}">${usernames[id - 1]}</label>
            </div> 
        `;
    }
}

// se carga la lista de usuarios al cargar la página
window.addEventListener('DOMContentLoaded', getEvent);

//TODO
function modifyEvent() {
    const form = document.getElementById("form");

    const formData = new FormData(form);
    const name = formData.get("name");
    const description = formData.get("description");
    const date = formData.get("date");
    const is_completed = formData.get("isCompleted") === "on";

    const participants = [];

    // obtengo los ids de los participantes seleccionados
    formData.getAll("participants").forEach(id => {
        participants.push({ id: parseInt(id), username: "placeholder" });
    });

    const eventData = {
        id: parseInt(selectedEventId),
        name: name,
        description: description,
        date: date,
        participants: participants,
        is_completed: is_completed
    };

    const headers = new Headers({
        "Content-Type": "application/json",
        "Authorization": "Bearer " + "token123" // Simulando un token de autenticación
    });

    //console.log(eventData);

    fetch(`http://localhost:9091/events/modifyEvent`, {
        method: 'PUT',
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
        alert("Evento modificado correctamente");
        //redirijo a la lista de eventos
        window.location.href = "events.html";
    }).catch(error => {
        console.error('Error:', error.message);
        alert("Error al modificar el evento: " + error.message);
    });
}
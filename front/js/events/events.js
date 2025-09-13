//variables utiles
const table = document.getElementById('eventsList');

async function loadEvents() {
    const headers = new Headers({
        "Content-Type": "application/json",
        "Authorization": "Bearer " + "token123" // Simulando un token de autenticación
    });

    fetch(`http://localhost:9091/events/getEventsWithoutParticipants`, {
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

        // por cada evento creo una fila en la tabla
        data.events.forEach(event => {
            const row = document.createElement('tr');  
            row.innerHTML = `
                <td>${event.id}</td>
                <td>${event.name}</td> 
                <td>${event.description}</td>
                <td>
                    ${
                        //fecha y hora
                        new Date(event.date.seconds * 1000).toLocaleDateString() + 
                        " <br> " + 
                        new Date(event.date.seconds * 1000).toLocaleTimeString()
                    }
                </td>
                <td>${event.is_completed}</td>
                <td>
                    <button onclick="modifyEvent(${event.id})">modificar</button>
                    <button onclick="deleteEvent(${event.id})"${event.is_completed ? ' disabled' : ''}>eliminar</button>
                    <button onclick="joinEvent()">unirme</button>
                </td>
            `;
            table.appendChild(row);
        });

    }).catch(error => {
        console.error('Error:', error);
        table.innerHTML = '<div class="error">Error: No se pudieron cargar los eventos</div>';
    });

    
}

// se carga la lista de eventos al cargar la página
window.addEventListener('DOMContentLoaded', loadEvents);

function deleteEvent(eventId) {

    if (!confirm("¿Está seguro que desea eliminar este evento?")) {
        return;
    }

    const headers = new Headers({
        "Content-Type": "application/json",
        "Authorization": "Bearer " + "token123" // Simulando un token de autenticación
    });

    fetch(`http://localhost:9091/events/deleteEvent/${parseInt(eventId)}`, {
        method: 'DELETE',
        headers: headers
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(response.statusText);
        }
        return response.json();
    })
    .then(data => {           
        alert('Evento eliminado correctamente');

        // recargo la lista de eventos
        reloadEvents();

    }).catch(error => {
        console.error('Error:', error);
        alert('Error al eliminar el evento: ' + error.message);
    });
}

function reloadEvents() {
    table.innerHTML = '';
    loadEvents();
}

// provisional, para probar modificar evento
// habría que usar controladores
function modifyEvent(eventId) {
    window.location.href = `editEvent.html?id=${eventId}`;
}

//TODO funciones para unirse o dejar un evento
//se necesita tener el usuario logueado
function joinEvent() {
}

function leaveEvent() {
}
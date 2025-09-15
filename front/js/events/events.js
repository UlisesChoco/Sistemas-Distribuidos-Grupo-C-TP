function modifyEvent(eventId) {
    window.location.replace(`/events/edit/${eventId}`);
}

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

        // recargo la pagina
        window.location.replace(`/events`);

    }).catch(error => {
        console.error('Error:', error);
        alert('Error al eliminar el evento: ' + error.message);
    });
}

//se necesita tener el usuario logueado
function joinEvent(eventId, userId) {

    const requestData = {eventId, userId};

    const headers = new Headers({
        "Content-Type": "application/json",
        "Authorization": "Bearer " + "token123" // Simulando un token de autenticación
    });

    fetch(`http://localhost:9091/events/assignUserToEvent`, {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(requestData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(response.statusText);
        }
        return response.json();
    })
    .then(data => {
        console.log('Success:', data);
        // recargo la pagina
        window.location.replace(`/events`);
    }).catch(error => {
        console.error('Error:', error);
    });
}

function leaveEvent(eventId, userId) {

    const requestData = {eventId, userId};

    const headers = new Headers({
        "Content-Type": "application/json",
        "Authorization": "Bearer " + "token123" // Simulando un token de autenticación
    });

    fetch(`http://localhost:9091/events/deleteUserFromEvent`, {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(requestData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(response.statusText);
        }
        return response.json();
    })
    .then(data => {
        console.log('Success:', data);
        // recargo la pagina
        window.location.replace(`/events`);
    }).catch(error => {
        console.error('Error:', error);
    });
}
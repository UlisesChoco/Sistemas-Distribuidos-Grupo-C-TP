/*
al entrar al form se deben cargar los usuarios activos
para poder agregarlos como participantes
*/
async function loadActiveUsers() {
    const usersSelect = document.getElementById('participants');
    usersSelect.innerHTML = ' '; // limpia el select

    //carga de usuarios activos

    const headers = new Headers({
        "Content-Type": "application/json",
        "Authorization": "Bearer " + "token123" // Simulando un token de autenticación
    });

    fetch(`http://localhost:9091/user/active-list`, {
        method: 'GET',
        headers: headers,
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(response.statusText);
        }
        return response.json();
    })
    .then(data => {
        
        data.users.forEach(user => {
            //falta marcar como checkeado al usuario creador
            usersSelect.innerHTML += `     
            <div>
                <input type="checkbox" id="user${user.id}" name="participants" value="${user.id}">
                <label for="user${user.id}">${user.username}</label>
            </div> 
        `;
        })
        
    }).catch(error => {
        console.error('Error:', error);
    });

    
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
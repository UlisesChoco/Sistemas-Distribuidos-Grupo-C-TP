async function getEvents() {
  const res = await fetch('http://localhost:8080/events/getExternalEvents');
  const data = await res.json();

  const table = document.getElementById('eventsList');

  data.forEach(event => {

    const date = new Date(event.date);

    table.innerHTML += `
        <tr>
            <td>${event.event_name}</td>
            <td>${event.description}</td>
            <td>
                ${date.toLocaleDateString('es-AR')}
                <br>
                ${date.toLocaleTimeString('es-AR', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    })}
            </td>
            <td><button onclick="joinEvent(${event.organization_id}, ${event.event_id})">Unirse</button></td>
        </tr>
    `
  });
}

window.addEventListener('DOMContentLoaded', getEvents);

function joinEvent(organization_id, event_id) {
  const requestData = {
    organizationId: organization_id,
    eventId: event_id
  }

  fetch(`http://localhost:8080/events/joinExternalEvent`, {
    method: 'POST',
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(requestData),
    credentials: 'include'
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
      alert('Te has unido al evento correctamente');
    }).catch(error => {
      console.error('Error:', error.message);
      alert('Hubo un error al unirse al evento');
    });
}
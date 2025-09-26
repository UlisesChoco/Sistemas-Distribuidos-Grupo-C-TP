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
                ${date.toLocaleTimeString('es-AR',{
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: false
                })}
            </td>
            <td><button onclick="joinEvent(${event.event_id})">Unirse</button></td>
        </tr>
    `
  });
}

getEvents();
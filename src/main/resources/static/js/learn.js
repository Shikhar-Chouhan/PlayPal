// Your API key from MySportsMonks
const apiKey = '46AAIO33VaujOQDIV191p7lbvkvjrlm8owbCd4hiG4J8crglDnsUASQYxTsv '; // Replace with your actual API key
const apiUrl = `https://soccer.sportmonks.com/api/v2.0/livescores/now?api_token=${apiKey}`;

// Function to fetch live sports events from MySportsMonks API
async function fetchSportsEvents() {
    try {
        const response = await fetch(apiUrl);

        if (!response.ok) {
            throw new Error(`Failed to fetch sports data: ${response.status}`);
        }

        const data = await response.json();

        if (data.data && data.data.length > 0) {
            displaySportsEvents(data.data); // Display the sports events
        } else {
            document.getElementById('sports-list').innerHTML = '<p>No live sports events available right now.</p>';
        }
    } catch (error) {
        console.error('Error fetching sports events:', error);
        document.getElementById('sports-list').innerHTML = '<p>Failed to load sports events. Please try again later.</p>';
    }
}

// Function to display sports events on the page
function displaySportsEvents(events) {
    const sportsListDiv = document.getElementById('sports-list');
    let eventsHTML = '';

    events.forEach(event => {
        const homeTeam = event.localTeam ? event.localTeam.data.name : 'Unknown Team';
        const awayTeam = event.visitorTeam ? event.visitorTeam.data.name : 'Unknown Team';
        const league = event.league ? event.league.data.name : 'Unknown League';
        const status = event.time.status ? event.time.status : 'Unknown';
        const startTime = event.time.starting_at ? event.time.starting_at.time : 'Unknown Time';

        eventsHTML += `
            <div class="sports-card">
                <h3>${homeTeam} vs ${awayTeam}</h3>
                <p><strong>League:</strong> ${league}</p>
                <p><strong>Status:</strong> ${status}</p>
                <p><strong>Start Time:</strong> ${startTime}</p>
            </div>
        `;
    });

    sportsListDiv.innerHTML = eventsHTML;
}

// Fetch the sports events when the page loads
window.onload = fetchSportsEvents;

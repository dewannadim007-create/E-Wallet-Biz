
document.addEventListener('DOMContentLoaded', function () {
    const mapContainer = document.getElementById('map');

    // Directions Helper
    window.openDirections = function (address) {
        if (address) {
            window.open('https://www.google.com/maps/search/?api=1&query=' + encodeURIComponent(address), '_blank');
        }
    };

    // Focus search if term exists
    const input = document.querySelector('input[name="search"]');
    const searchTermMeta = document.getElementById('searchTermMeta');
    const searchTerm = searchTermMeta ? searchTermMeta.getAttribute('content') : null;

    if (input && searchTerm) {
        const val = input.value;
        input.value = '';
        input.value = val;
        input.focus();
    }

    if (!mapContainer) return;

    // Custom Icons
    const branchIcon = L.icon({
        iconUrl: '/images/branch.png',
        iconSize: [40, 40],
        iconAnchor: [20, 40],
        popupAnchor: [0, -40]
    });

    const atmIcon = L.icon({
        iconUrl: '/images/atm.png',
        iconSize: [40, 40],
        iconAnchor: [20, 40],
        popupAnchor: [0, -40]
    });

    // Default Map: Dhaka
    const defaultLat = 23.8103;
    const defaultLng = 90.4125;
    const map = L.map('map').setView([defaultLat, defaultLng], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '© OpenStreetMap'
    }).addTo(map);

    const cards = document.querySelectorAll('.location-card');
    const markersBuffer = [];
    const loader = document.getElementById('map-loading');

    // Geocoding Helper
    async function geocodeAddress(address) {
        try {
            const response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}`, {
                headers: { 'User-Agent': 'EWalletBankingApp/1.0' }
            });
            const data = await response.json();
            if (data && data.length > 0) {
                return { lat: parseFloat(data[0].lat), lon: parseFloat(data[0].lon) };
            }
        } catch (e) { console.error("Geocoding failed for:", address); }
        return null;
    }

    async function processLocations() {
        let hasGeocodes = false;

        for (const card of cards) {
            let lat = parseFloat(card.dataset.lat);
            let lng = parseFloat(card.dataset.lng);
            const name = card.dataset.name;
            const type = card.dataset.type;
            const address = card.dataset.location;

            // If lat/lng are 0 or missing, try geocoding
            if ((!lat || !lng || (lat === 0 && lng === 0)) && address) {
                if (!hasGeocodes) {
                    if (loader) loader.style.display = 'block';
                    hasGeocodes = true;
                }

                // Rate limit: wait 1s
                await new Promise(r => setTimeout(r, 1100));
                const coords = await geocodeAddress(address);

                if (coords) {
                    lat = coords.lat;
                    lng = coords.lon;
                    // Update card data for future interaction
                    card.dataset.lat = lat;
                    card.dataset.lng = lng;
                } else {
                    // Fallback randomization nearby default
                    lat = defaultLat + (Math.random() - 0.5) * 0.05;
                    lng = defaultLng + (Math.random() - 0.5) * 0.05;
                }
            }

            // Use correct icon
            const icon = (type === 'ATM') ? atmIcon : branchIcon;

            // Safe guard against NaN
            if (!isNaN(lat) && !isNaN(lng)) {
                const marker = L.marker([lat, lng], { icon: icon }).addTo(map);
                const popupContent = `<div style="text-align:center"><b>${name}</b><br><span class="badge" style="font-size:0.8em; margin-top:5px;">${type}</span></div>`;
                marker.bindPopup(popupContent);
                markersBuffer.push([lat, lng]);

                // Card Click Interaction
                card.addEventListener('click', () => {
                    map.flyTo([lat, lng], 16, { animate: true, duration: 1.5 });
                    marker.openPopup();
                    document.querySelector('.map-container').scrollIntoView({ behavior: 'smooth' });
                });
            }
        }

        if (loader) loader.style.display = 'none';

        // Fit bounds
        if (markersBuffer.length > 0) {
            const bounds = L.latLngBounds(markersBuffer);
            map.fitBounds(bounds, { padding: [50, 50], maxZoom: 15 });
        }
    }

    // Start processing
    processLocations();
});

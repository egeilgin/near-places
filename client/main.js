// ─── Backend URL ───────────────────────────────────────────────────────────────
const BACKEND_URL = "https://near-places-production.up.railway.app/api/places";

// ─── Harita ────────────────────────────────────────────────────────────────────
const map = L.map('map').setView([39.9334, 32.8597], 13);
L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

// ─── Yardımcı: marker ve circle katmanlarını temizle, tile'a dokunma ───────────
function clearLayers() {
    map.eachLayer(layer => {
        if (layer instanceof L.Marker || layer instanceof L.Circle) {
            map.removeLayer(layer);
        }
    });
}

// ─── Ana fonksiyon ─────────────────────────────────────────────────────────────
async function searchPlaces() {
    const lat     = parseFloat(document.getElementById('latitude').value);
    const lon     = parseFloat(document.getElementById('longitude').value);
    const radius  = parseInt(document.getElementById('radius').value);
    const amenity = document.getElementById('amenity').value;

    if (isNaN(lat) || isNaN(lon) || isNaN(radius) || radius <= 0) {
        alert("Lütfen tüm alanları geçerli şekilde doldurun.");
        return;
    }

    const btn = document.querySelector('button');
    btn.textContent = "Aranıyor...";
    btn.disabled = true;

    // Backend'e GET isteği — Overpass'a artık direkt gitmiyor
    const url = `${BACKEND_URL}?latitude=${lat}&longitude=${lon}&radius=${radius}&amenity=${amenity}`;
    console.log("Backend isteği:", url);

    try {
        const response = await fetch(url);

        if (!response.ok) {
            const errText = await response.text();
            throw new Error(`Backend hatası (${response.status}): ${errText}`);
        }

        const data = await response.json();
        console.log("Sonuç:", data.elements?.length ?? 0, "element");

        clearLayers();

        // Arama dairesi
        L.circle([lat, lon], {
            radius: radius,
            color: '#3b82f6',
            fillColor: '#3b82f6',
            fillOpacity: 0.08,
            weight: 2,
            dashArray: '6 4'
        }).addTo(map);

        // Merkez noktası
        L.marker([lat, lon], {
            icon: L.divIcon({
                className: '',
                html: '<div style="width:12px;height:12px;background:#3b82f6;border:2px solid white;border-radius:50%;box-shadow:0 0 6px rgba(0,0,0,0.4)"></div>',
                iconSize: [12, 12],
                iconAnchor: [6, 6]
            })
        }).addTo(map).bindPopup("Arama merkezi");

        if (!data.elements || data.elements.length === 0) {
            alert(`Bu alanda "${amenity}" bulunamadı. Daha büyük bir radius dene.`);
        } else {
            data.elements.forEach(el => {
                L.marker([el.lat, el.lon])
                    .addTo(map)
                    .bindPopup(
                        `<strong>${el.tags.name || "İsimsiz"}</strong>` +
                        (el.tags["addr:street"] ? `<br>📍 ${el.tags["addr:street"]}` : "") +
                        (el.tags.phone        ? `<br>📞 ${el.tags.phone}`            : "") +
                        (el.tags.opening_hours? `<br>🕐 ${el.tags.opening_hours}`    : "")
                    );
            });

            // Tüm sonuçları kapsayacak şekilde zoom ayarla
            const bounds = L.latLngBounds([[lat, lon]]);
            data.elements.forEach(el => bounds.extend([el.lat, el.lon]));
            map.fitBounds(bounds.pad(0.2));
        }

    } catch (error) {
        console.error("Hata:", error);
        alert("İstek başarısız: " + error.message);
    } finally {
        btn.textContent = "Ara";
        btn.disabled = false;
    }
}
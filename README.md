# Near Places

Yakınındaki yerleri harita üzerinde gösteren full-stack web uygulaması.

## Tech Stack

- **Backend:** Java 21, Spring Boot 3.5, PostgreSQL, Hibernate
- **Frontend:** HTML, CSS, Vanilla JS, Leaflet.js
- **Veri kaynağı:** OpenStreetMap / Overpass API

## Proje Yapısı
```
near-places/
├── server/   → Spring Boot REST API (port 8070)
└── client/   → Leaflet.js harita arayüzü
```

## Çalıştırma

### Backend
```bash
cd server
mvn spring-boot:run
```

### Gerekli environment variable'lar
| Değişken | Açıklama | Default |
|---|---|---|
| DB_URL | PostgreSQL JDBC URL | jdbc:postgresql://localhost:5432/nearplaces |
| DB_USERNAME | Veritabanı kullanıcısı | postgres |
| DB_PASSWORD | Veritabanı şifresi | - |

### Frontend
`client/index.html` dosyasını tarayıcıda aç.

Deploy sonrası `client/main.js` içindeki `BACKEND_URL` değerini güncelle.

## API
```
GET /api/places?latitude=39.9334&longitude=32.8597&radius=1000&amenity=restaurant
```

**Parametreler:**
| Parametre | Tip | Açıklama |
|---|---|---|
| latitude | double | Enlem |
| longitude | double | Boylam |
| radius | int | Yarıçap (metre) |
| amenity | string | OSM amenity tipi (varsayılan: restaurant) |

## Cache Mantığı

Aynı `(latitude, longitude, radius, amenity)` kombinasyonu daha önce sorgulandıysa
sonuç PostgreSQL'den döner, Overpass API'ye istek atılmaz.
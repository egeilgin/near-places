# Near Places

A full-stack web application that displays nearby places (restaurants, cafes, pharmacies, etc.) on an interactive map using OpenStreetMap data.  
**Live demo:** [Frontend on Vercel](https://near-places.vercel.app) | [Backend API on Railway](https://near-places-production.up.railway.app)

---

## 🚀 Live Demo

| Service   | URL                                                                                          |
|-----------|----------------------------------------------------------------------------------------------|
| Frontend  | ✅ https://near-places.vercel.app                                                     |
| Backend   | https://near-places-production.up.railway.app                                                |
| API Test  | https://near-places-production.up.railway.app/api/places?latitude=39.9334&longitude=32.8597&radius=1000&amenity=restaurant |

> ⚡ Backend cachelidir; ilk istek Overpass API'den gelir, sonrakiler PostgreSQL'den döner.

---

## 🧱 Tech Stack

**Backend:** Java 21, Spring Boot 3.5, PostgreSQL, JPA/Hibernate, Maven  
**Frontend:** HTML, CSS, JavaScript, Leaflet.js  
**Data Source:** OpenStreetMap / Overpass API  
**Deployment:** Railway (backend) + Vercel (frontend)

---

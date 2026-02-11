package com.codexist.server.service;

import com.codexist.server.entity.PlacesEntity;
import com.codexist.server.repository.PlacesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Business logic layer.
 *
 * Strategy:
 *   1. Check the DB for a cached result with the same (lat, lon, radius, amenity).
 *   2. If found → return the cached JSON immediately (no external call).
 *   3. If not found → call OverpassService, persist the result, return the JSON.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlacesService {

    private final PlacesRepository repository;
    private final OverpassService overpassService;

    /**
     * Returns Overpass JSON for the given search parameters, using the DB as a cache.
     *
     * @param lat     latitude
     * @param lon     longitude
     * @param radius  radius in metres
     * @param amenity OSM amenity value
     * @return raw JSON string
     */
    public String getPlaces(double lat, double lon, int radius, String amenity) throws IOException {

        // Round coordinates to 6 decimal places for a stable cache key
        double roundedLat = Math.round(lat * 1_000_000.0) / 1_000_000.0;
        double roundedLon = Math.round(lon * 1_000_000.0) / 1_000_000.0;

        // 1. Cache hit?
        var cached = repository.findByLatitudeAndLongitudeAndRadiusAndAmenity(
                roundedLat, roundedLon, radius, amenity);

        if (cached.isPresent()) {
            log.info("Cache HIT for lat={} lon={} radius={} amenity={}", roundedLat, roundedLon, radius, amenity);
            return cached.get().getResponseJson();
        }

        // 2. Cache miss – call Overpass
        log.info("Cache MISS – calling Overpass for lat={} lon={} radius={} amenity={}", roundedLat, roundedLon, radius, amenity);
        String json = overpassService.fetchPlaces(roundedLat, roundedLon, radius, amenity);

        // 3. Persist for future requests
        PlacesEntity entity = PlacesEntity.builder()
                .latitude(roundedLat)
                .longitude(roundedLon)
                .radius(radius)
                .amenity(amenity)
                .responseJson(json)
                .build();

        repository.save(entity);
        log.info("Saved to cache: id={}", entity.getId());

        return json;
    }
}
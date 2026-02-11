package com.codexist.server.controller;

import com.codexist.server.service.PlacesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@Slf4j
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")   // allow the frontend (plain HTML/JS) to call this endpoint
public class PlacesController {

    private final PlacesService placesService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getNearbyPlaces(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam int radius,
            @RequestParam(defaultValue = "restaurant") String amenity
    ) {
        // Basic validation
        if (latitude < -90 || latitude > 90) {
            return ResponseEntity.badRequest().body("{\"error\":\"latitude must be between -90 and 90\"}");
        }
        if (longitude < -180 || longitude > 180) {
            return ResponseEntity.badRequest().body("{\"error\":\"longitude must be between -180 and 180\"}");
        }
        if (radius <= 0) {
            return ResponseEntity.badRequest().body("{\"error\":\"radius must be a positive integer\"}");
        }

        log.info("GET /api/places lat={} lon={} radius={} amenity={}", latitude, longitude, radius, amenity);

        try {
            String json = placesService.getPlaces(latitude, longitude, radius, amenity);
            return ResponseEntity.ok(json);
        } catch (IOException e) {
            log.error("Failed to fetch places: {}", e.getMessage());
            return ResponseEntity.status(502)
                    .body("{\"error\":\"Upstream Overpass API unavailable: " + e.getMessage() + "\"}");
        }
    }
}
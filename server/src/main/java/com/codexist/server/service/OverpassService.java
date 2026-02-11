package com.codexist.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * Calls the Overpass API (with mirror fallback) and returns the raw JSON string.
 */
@Slf4j
@Service
public class OverpassService {

    private static final List<String> MIRRORS = List.of(
            "https://overpass.kumi.systems/api/interpreter",
            "https://overpass-api.de/api/interpreter",
            "https://maps.mail.ru/osm/tools/overpass/api/interpreter"
    );

    @Value("${overpass.timeout:15000}")
    private int timeoutMs;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Builds an Overpass QL query and fires it against the first mirror that responds.
     *
     * @param lat     latitude of the search centre
     * @param lon     longitude of the search centre
     * @param radius  search radius in metres
     * @param amenity OSM amenity tag value (e.g. "restaurant", "pharmacy")
     * @return raw JSON string from Overpass API
     * @throws IOException if all mirrors fail
     */
    public String fetchPlaces(double lat, double lon, int radius, String amenity) throws IOException {
        String query = String.format(
                "[out:json][timeout:25];\nnode[\"amenity\"=\"%s\"](around:%d,%.6f,%.6f);\nout;",
                amenity, radius, lat, lon
        );

        String body = "data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

        for (String mirrorUrl : MIRRORS) {
            try {
                log.info("Trying Overpass mirror: {}", mirrorUrl);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(mirrorUrl))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .timeout(Duration.ofMillis(timeoutMs))
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String text = response.body();

                if (text == null || text.isBlank()) {
                    log.warn("Empty response from {}", mirrorUrl);
                    continue;
                }

                if (text.trim().startsWith("<")) {
                    log.warn("XML (error) response from {}, skipping", mirrorUrl);
                    continue;
                }

                log.info("Success from mirror: {}", mirrorUrl);
                return text;

            } catch (Exception e) {
                log.warn("Mirror {} failed: {}", mirrorUrl, e.getMessage());
            }
        }

        throw new IOException("All Overpass mirrors failed.");
    }
}
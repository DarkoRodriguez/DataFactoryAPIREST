package com.example.miapp.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService {

    private static final Logger log = LoggerFactory.getLogger(GeocodingService.class);

    private final RestTemplate restTemplate;
    private final String nominatimUrl;
    private final String userAgent;

    public GeocodingService(RestTemplate restTemplate,
                            @Value("${geocoding.nominatim.url}") String nominatimUrl,
                            @Value("${geocoding.user-agent:Miapp/1.0}") String userAgent) {
        this.restTemplate = restTemplate;
        this.nominatimUrl = nominatimUrl;
        this.userAgent = userAgent;
    }

    public Optional<Location> geocode(String address) {
        if (address == null || address.isBlank()) {
            return Optional.empty();
        }

        String[] variants = new String[] {
            address + ", Santiago, Chile",
            address + ", Chile",
            address
        };

        for (String attempt : variants) {
            Optional<Location> loc = geocodeOnce(attempt);
            if (loc.isPresent()) {
                log.info("Geocoded '{}' -> {}", attempt, loc.get());
                return loc;
            } else {
                log.debug("Geocoding attempt failed for '{}', trying next variant", attempt);
            }
        }

        // As last resort, try plain tokenization of address parts (comma-joined)
        try {
            String[] parts = address.split(",\\s*");
            for (int i = 0; i < parts.length; i++) {
                String joined = String.join(", ", java.util.Arrays.copyOfRange(parts, i, parts.length)) + ", Chile";
                Optional<Location> loc = geocodeOnce(joined);
                if (loc.isPresent()) return loc;
            }
        } catch (Exception ignored) {}

        return Optional.empty();
    }

    private Optional<Location> geocodeOnce(String address) {
        try {
                // Restrict results to Chile to avoid ambiguous matches in other countries
                String url = nominatimUrl + "/search?q=" + URLEncoder.encode(address, StandardCharsets.UTF_8)
                    + "&format=json&limit=1&countrycodes=cl&accept-language=es";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", userAgent);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<NominatimResult[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, NominatimResult[].class);
            NominatimResult[] body = resp.getBody();
            if (body != null && body.length > 0) {
                NominatimResult r = body[0];
                return Optional.of(new Location(Double.parseDouble(r.lat), Double.parseDouble(r.lon)));
            }
        } catch (Exception e) {
            log.debug("Geocoding failed for '{}' with exception: {}", address, e.toString());
        }
        return Optional.empty();
    }

    // Minimal mapping for Nominatim JSON
    private static class NominatimResult {
        public String lat;
        public String lon;
        public String display_name;
    }

}

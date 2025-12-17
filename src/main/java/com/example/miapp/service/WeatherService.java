package com.example.miapp.service;

import java.util.List;
import java.util.Map;

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
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate;
    private final String meteoredUrl;
    private final String meteoredApiKey;

    public WeatherService(RestTemplate restTemplate,
                          @Value("${meteored.api.url:https://api.meteored.com}") String meteoredUrl,
                          @Value("${meteored.api.key:}") String meteoredApiKey) {
        this.restTemplate = restTemplate;
        this.meteoredUrl = meteoredUrl;
        this.meteoredApiKey = meteoredApiKey;
    }

    /**
     * Use Meteored API: first resolve location hash via
     * /api/location/v1/search/coords/{lat}/{lon} then call
     * /api/forecast/v1/daily/{hash} to retrieve daily forecast.
     */
    public WeatherResult getWeather(double lat, double lon) {
        try {
            String locationUrl = String.format("%s/api/location/v1/search/coords/%s/%s", meteoredUrl, lat, lon);
            if (meteoredApiKey != null && !meteoredApiKey.isBlank()) {
                locationUrl = locationUrl + "?apikey=" + meteoredApiKey;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Miapp/1.0");
            if (meteoredApiKey != null && !meteoredApiKey.isBlank()) {
                headers.set("x-api-key", meteoredApiKey);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> locResp = restTemplate.exchange(locationUrl, HttpMethod.GET, entity, Map.class);
            Object locBody = locResp.getBody();
            String hash = null;
            if (locBody instanceof Map) {
                Map m = (Map) locBody;
                if (m.containsKey("hash")) {
                    hash = String.valueOf(m.get("hash"));
                } else {
                    // common shape: {ok: true, data: { locations: [ {hash:...}, ... ] } }
                    Object data = m.get("data");
                    if (data instanceof Map) {
                        Map dataMap = (Map) data;
                        Object locations = dataMap.get("locations");
                        if (locations instanceof List && !((List) locations).isEmpty()) {
                            Object first = ((List) locations).get(0);
                            if (first instanceof Map && ((Map) first).containsKey("hash")) {
                                hash = String.valueOf(((Map) first).get("hash"));
                            }
                        }
                    } else if (data instanceof List && !((List) data).isEmpty()) {
                        Object first = ((List) data).get(0);
                        if (first instanceof Map && ((Map) first).containsKey("hash")) {
                            hash = String.valueOf(((Map) first).get("hash"));
                        }
                    }
                }
                // fallback: sometimes top-level contains 'locations'
                if (hash == null && m.containsKey("locations") && m.get("locations") instanceof List) {
                    List locs = (List) m.get("locations");
                    if (!locs.isEmpty() && locs.get(0) instanceof Map) {
                        Map first = (Map) locs.get(0);
                        if (first.containsKey("hash")) hash = String.valueOf(first.get("hash"));
                    }
                }
            } else if (locBody instanceof List) {
                List list = (List) locBody;
                if (!list.isEmpty() && list.get(0) instanceof Map) {
                    Map first = (Map) list.get(0);
                    if (first.containsKey("hash")) hash = String.valueOf(first.get("hash"));
                }
            }
            if (hash == null) {
                log.warn("No hash found from location response: {}", locBody);
            }

            if (hash == null) {
                log.info("No hash found from location response; attempting to use location-level fields: {}", locBody);
                // Try to extract precipitation directly from the location response
                Double locPrecip = findPrecipitationProbabilityRecursive(locBody);
                String locSummary = null;
                if (locBody instanceof Map) {
                    locSummary = findFirstStringForKeys((Map) locBody, new String[]{"name","description","display_name","summary"});
                    // try temperature
                    Object temp = ((Map) locBody).get("temperature_max");
                    if (locSummary == null && temp != null) locSummary = "Temperatura: " + temp;
                }
                if (locPrecip != null) {
                    return new WeatherResult(locSummary != null ? locSummary : "Clima disponible", normalizeProbability(locPrecip));
                }
                return new WeatherResult("Sin datos de ubicación", null);
            }

            String forecastUrl = String.format("%s/api/forecast/v1/daily/%s", meteoredUrl, hash);
            if (meteoredApiKey != null && !meteoredApiKey.isBlank()) {
                forecastUrl = forecastUrl + "?apikey=" + meteoredApiKey;
            }
            ResponseEntity<Map> foreResp = restTemplate.exchange(forecastUrl, HttpMethod.GET, entity, Map.class);
            Map foreBody = foreResp.getBody();
            if (foreBody == null) {
                return new WeatherResult("Sin datos de forecast", null);
            }

            // Prefer extracting from data.days (daily forecast). Use first day as 'today'.
            Double precipProb = null;
            String summary = null;
            Object dataObj = foreBody.get("data");
            Object daysObj = null;
            if (dataObj instanceof Map) {
                daysObj = ((Map) dataObj).get("days");
            }
            if (daysObj == null) {
                // maybe top-level days
                daysObj = foreBody.get("days");
            }
            if (daysObj instanceof List && !((List) daysObj).isEmpty()) {
                Object first = ((List) daysObj).get(0);
                if (first instanceof Map) {
                    Map day0 = (Map) first;
                    // extract rain_probability if present
                    Object rp = day0.get("rain_probability");
                    if (rp == null) rp = day0.get("rainProbability");
                    if (rp == null) rp = day0.get("rain");
                    precipProb = parseNumberToDouble(rp);
                    if (precipProb != null) precipProb = normalizeProbability(precipProb);
                    // build a simple summary from symbol/temperature
                    Object symbol = day0.get("symbol");
                    Object tmax = day0.get("temperature_max");
                    if (symbol != null) summary = "Symbol:" + String.valueOf(symbol);
                    if (summary == null && tmax != null) summary = "Tmax: " + tmax;
                }
            }

            // fallback: recursive search if still null
            if (summary == null) summary = findFirstStringForKeys(foreBody, new String[]{"summary","description","text","title","name"});
            if (precipProb == null) precipProb = findPrecipitationProbabilityRecursive(foreBody);

            return new WeatherResult(summary != null ? summary : "Clima disponible", precipProb);
        } catch (Exception e) {
            log.warn("WeatherService call failed for {},{}: {}", lat, lon, e.toString());
            return new WeatherResult("Error consultando clima", null);
        }
    }

    private String findFirstStringForKeys(Map map, String[] keys) {
        for (String k : keys) {
            Object v = map.get(k);
            if (v instanceof String) return (String) v;
        }
        // search nested
        for (Object val : map.values()) {
            if (val instanceof Map) {
                String r = findFirstStringForKeys((Map) val, keys);
                if (r != null) return r;
            } else if (val instanceof List) {
                for (Object item : (List) val) {
                    if (item instanceof Map) {
                        String r = findFirstStringForKeys((Map) item, keys);
                        if (r != null) return r;
                    }
                }
            }
        }
        return null;
    }

    private Double findPrecipitationProbabilityRecursive(Object node) {
        if (node == null) return null;
        if (node instanceof Map) {
            Map m = (Map) node;
            String[] keys = new String[]{"precipitation_probability", "precipitationProbability", "pop", "rain_chance", "precipitation", "probability"};
            for (String k : keys) {
                Object v = m.get(k);
                Double d = parseNumberToDouble(v);
                if (d != null) return normalizeProbability(d);
            }
            for (Object v : m.values()) {
                Double r = findPrecipitationProbabilityRecursive(v);
                if (r != null) return r;
            }
        } else if (node instanceof List) {
            for (Object item : (List) node) {
                Double r = findPrecipitationProbabilityRecursive(item);
                if (r != null) return r;
            }
        }
        return null;
    }

    private Double parseNumberToDouble(Object v) {
        if (v instanceof Number) return ((Number) v).doubleValue();
        if (v instanceof String) {
            try { return Double.parseDouble((String) v); } catch (Exception ignored) {}
        }
        return null;
    }

    private Double normalizeProbability(Double d) {
        if (d == null) return null;
        if (d > 1.0) d = d / 100.0;
        if (d < 0.0) d = 0.0;
        if (d > 1.0) d = 1.0;
        return d;
    }

}

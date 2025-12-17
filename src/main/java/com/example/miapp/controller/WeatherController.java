package com.example.miapp.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miapp.model.Orden;
import com.example.miapp.repository.OrdenRepository;
import com.example.miapp.service.GeocodingService;
import com.example.miapp.service.Location;
import com.example.miapp.service.WeatherResult;
import com.example.miapp.service.WeatherService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class WeatherController {

    private final OrdenRepository ordenRepository;
    private final GeocodingService geocodingService;
    private final WeatherService weatherService;
    private final double precipitationThreshold;

    public WeatherController(OrdenRepository ordenRepository,
                             GeocodingService geocodingService,
                             WeatherService weatherService,
                             @org.springframework.beans.factory.annotation.Value("${delivery.precipitation.threshold:0.5}") double precipitationThreshold) {
        this.ordenRepository = ordenRepository;
        this.geocodingService = geocodingService;
        this.weatherService = weatherService;
        this.precipitationThreshold = precipitationThreshold;
    }

    @GetMapping("/{orderId}/weather")
    public ResponseEntity<?> weatherForOrder(@PathVariable("orderId") Long orderId) {
        return ordenRepository.findById(orderId).map(orden -> {
            String address = buildAddressFromOrder(orden);
            // geocode
            Location loc = geocodingService.geocode(address).orElse(null);
            if (loc == null) {
                Map<String,Object> body = new HashMap<>();
                body.put("error","No se pudo obtener lat/lon para la dirección de la orden");
                return ResponseEntity.badRequest().body(body);
            }
            WeatherResult wr = weatherService.getWeather(loc.getLat(), loc.getLon());
            double threshold = this.precipitationThreshold; // configurable umbral
            Boolean deliveryAvailable = null;
            if (wr.getPrecipitationProbability() == null) {
                deliveryAvailable = true; // fallback permissive
            } else {
                deliveryAvailable = wr.getPrecipitationProbability() < threshold;
            }
            Map<String,Object> resp = new HashMap<>();
            resp.put("weatherSummary", wr.getWeatherSummary());
            resp.put("precipitationProbability", wr.getPrecipitationProbability());
            resp.put("deliveryAvailable", deliveryAvailable);
            resp.put("checkedAt", LocalDate.now().toString());
            if (!deliveryAvailable) {
                resp.put("recommendedDate", LocalDate.now().plusDays(1).toString());
            }
            return ResponseEntity.ok(resp);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private String buildAddressFromOrder(Orden orden) {
        StringBuilder sb = new StringBuilder();
        if (orden.getCalle() != null) sb.append(orden.getCalle());
        if (orden.getComuna() != null) sb.append(", ").append(orden.getComuna());
        if (orden.getRegion() != null) sb.append(", ").append(orden.getRegion());
        return sb.toString();
    }

}

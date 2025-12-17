package com.example.miapp.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.miapp.service.GeocodingService;
import com.example.miapp.service.Location;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GeocodingController {

    private final GeocodingService geocodingService;

    public GeocodingController(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    @GetMapping("/geocode")
    public ResponseEntity<?> geocode(@RequestParam("address") String address) {
        Optional<Location> loc = geocodingService.geocode(address);
        if (loc.isPresent()) {
            return ResponseEntity.ok(loc.get());
        } else {
            return ResponseEntity.badRequest().body("No se pudo geocodificar la dirección");
        }
    }

}

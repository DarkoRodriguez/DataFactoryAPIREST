package com.example.miapp.service;

public class Location {

    private double lat;
    private double lon;

    public Location() {
    }

    public Location(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "Location{" + "lat=" + lat + ", lon=" + lon + '}';
    }

}

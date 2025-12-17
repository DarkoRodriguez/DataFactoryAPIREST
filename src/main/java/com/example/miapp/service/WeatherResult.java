package com.example.miapp.service;

public class WeatherResult {
    private String weatherSummary;
    private Double precipitationProbability; // 0.0 - 1.0, null if unknown

    public WeatherResult() {}

    public WeatherResult(String weatherSummary, Double precipitationProbability) {
        this.weatherSummary = weatherSummary;
        this.precipitationProbability = precipitationProbability;
    }

    public String getWeatherSummary() {
        return weatherSummary;
    }

    public Double getPrecipitationProbability() {
        return precipitationProbability;
    }

}

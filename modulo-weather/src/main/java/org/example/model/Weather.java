package org.example.model;

public record Weather(
        String location,       // Ej: "Las Palmas de Gran Canaria"
        double temp,    // Ej: 22.5
        int humidity,          // Ej: 65
        double rainProb,       // Probabilidad de lluvia (ideal para cruzar con eventos luego)
        String capturedAt      // IMPORTANTE: El timestamp de cuándo se consultó la API (ISO 8601)
) {}

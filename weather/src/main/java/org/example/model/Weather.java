package org.example.model;

public record Weather(
        String ts,       // Reemplaza a capturedAt (Timestamp)
        String ss,       // Identificador de la fuente (ej. "weather-feeder")
        String location,
        double temp,
        int humidity,
        double rainProb
) {}
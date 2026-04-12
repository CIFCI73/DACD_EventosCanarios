package org.example.model;

public record Weather(
        String location,    // El nombre de la ciudad (ej: Las Palmas)
        double temp,        // La temperatura actual en grados Celsius
        int humidity,       // El porcentaje de humedad relativa
        double rainProb,    // Probabilidad de lluvia (útil para el valor añadido futuro)
        String capturedAt   // Momento exacto de la captura en formato ISO 8601
) {}
package org.example.model;

public record Weather(
        String ts,
        String ss,       // Identificador de la fuente
        String location,
        double temp,
        int humidity,
        double rainProb
) {}
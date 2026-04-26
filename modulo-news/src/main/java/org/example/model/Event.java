package org.example.model;

public record Event(
        String ts,       // Reemplaza a capturedAt
        String ss,       // Identificador de la fuente (ej. "icdc-events-feeder")
        String title,
        String date,
        String location
) {}
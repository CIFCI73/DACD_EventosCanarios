package org.example.model;

public record Event(
        String title,          // Ej: "Concierto Orquesta Filarmónica"
        String date,           // Ej: "2024-05-15"
        String time,           // Ej: "20:30"
        String location,       // Ej: "Auditorio Alfredo Kraus"
        boolean isOutdoors,    // Opcional pero da mucho valor: ¿Es al aire libre? (True/False)
        String capturedAt      // IMPORTANTE: El timestamp de cuándo se hizo el scraping
) {}

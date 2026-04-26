package org.example.model;

public record Event(
        String title,
        String date,
        String location,
        String capturedAt
) {}

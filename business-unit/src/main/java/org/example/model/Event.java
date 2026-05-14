package org.example.model;

public record Event(
        String ts,
        String ss,
        String title,
        String date,
        String location
) {}
package org.example.model;

public record Weather(
        String ts,
        String ss,
        String location,
        double temp,
        int humidity,
        double rainProb
) {}
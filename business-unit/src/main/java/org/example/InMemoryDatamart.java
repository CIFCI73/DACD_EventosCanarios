package org.example;

import com.google.gson.Gson;
import org.example.datamart.DatamartUpdater;
import org.example.model.Event;
import org.example.model.Weather;

import java.time.LocalDate;
import java.util.*;

public class InMemoryDatamart implements DatamartUpdater {
    // Usamos mapas para guardar los datos usando la FECHA (ej. 2026-04-26) como clave
    private final Map<String, Weather> weatherByDate = new HashMap<>();
    private final Map<String, List<Event>> eventsByDate = new HashMap<>();
    private final Gson gson = new Gson();

    @Override
    public void processEvent(String topic, String jsonEvent) {
        try {
            if (topic.equals("Weather")) {
                Weather w = gson.fromJson(jsonEvent, Weather.class);
                String dateKey = w.ts().substring(0, 10);
                weatherByDate.put(dateKey, w);

            } else if (topic.equals("Events")) {
                Event e = gson.fromJson(jsonEvent, Event.class);
                String rawDate = e.date().trim(); // Ej: "19/05/2026 - 24/05/2026" o "14/5/2026 19:00"

                try {
                    if (rawDate.contains("-")) {
                        // 1. ES UN RANGO DE FECHAS
                        String[] dates = rawDate.split("-");
                        LocalDate startDate = parseToLocalDate(dates[0].trim());
                        LocalDate endDate = parseToLocalDate(dates[1].trim());

                        // Bucle mágico: añade el evento a TODOS los días desde el inicio hasta el final
                        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                            // date.toString() devuelve automáticamente el formato "YYYY-MM-DD"
                            addEventToMap(date.toString(), e);
                        }
                    } else {
                        // 2. ES UNA FECHA ÚNICA (la lógica antigua)
                        String datePart = rawDate.split(" ")[0]; // Quitamos la hora si la tiene
                        LocalDate date = parseToLocalDate(datePart);
                        addEventToMap(date.toString(), e);
                    }
                } catch (Exception ex) {
                    // Plan de respaldo si la fecha es texto irreconocible
                    String fallbackDate = e.ts().substring(0, 10);
                    addEventToMap(fallbackDate, e);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando el JSON en el Datamart: " + e.getMessage());
        }
    }

    // --- MÉTODOS AUXILIARES PARA TENER UN CÓDIGO MÁS LIMPIO ---

    // Transforma un String "DD/MM/YYYY" a un objeto LocalDate
    private LocalDate parseToLocalDate(String dateStr) {
        String[] parts = dateStr.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        return LocalDate.of(year, month, day);
    }

    // Guarda el evento en el mapa asegurándose de que no haya duplicados
    private void addEventToMap(String dateKey, Event e) {
        List<Event> dailyEvents = eventsByDate.computeIfAbsent(dateKey, k -> new ArrayList<>());

        boolean duplicato = dailyEvents.stream()
                .anyMatch(eventoEsistente -> eventoEsistente.title().equals(e.title()));

        if (!duplicato) {
            dailyEvents.add(e);
        }
    }

    // Métodos para la interfaz de usuario (View)
    public Weather getWeatherFor(String date) {
        return weatherByDate.get(date);
    }

    public List<Event> getEventsFor(String date) {
        return eventsByDate.getOrDefault(date, Collections.emptyList());
    }
}
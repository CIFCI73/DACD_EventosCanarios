package org.example;

import com.google.gson.Gson;
import org.example.datamart.DatamartUpdater;
import org.example.model.Event;
import org.example.model.Weather;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InMemoryDatamart implements DatamartUpdater {
    private final Map<String, Map<String, Weather>> weatherByDateAndCity = new HashMap<>();
    private final Map<String, List<Event>> eventsByDate = new HashMap<>();
    private final Gson gson = new Gson();

    // Patrón Regex: Busca de 1 a 2 números, una barra, 1 a 2 números, una barra, y 4 números (Ej: 14/5/2026 o 29/05/2026)
    private final Pattern datePattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{4}");

    @Override
    public void processEvent(String topic, String jsonEvent) {
        try {
            if (topic.equals("Weather")) {
                Weather w = gson.fromJson(jsonEvent, Weather.class);
                String dateKey = w.ts().substring(0, 10);

                // Normalizamos el nombre de la ciudad (todo a minúsculas para facilitar búsquedas)
                String cityKey = w.location().toLowerCase();

                // Buscamos el mapa de ese día (si no existe, lo creamos) y guardamos el clima de la ciudad
                weatherByDateAndCity
                        .computeIfAbsent(dateKey, k -> new HashMap<>())
                        .put(cityKey, w);

            } else if (topic.equals("Events")) {
                Event e = gson.fromJson(jsonEvent, Event.class);
                String rawDate = e.date();

                try {
                    // "Pescamos" todas las fechas que haya dentro del texto
                    Matcher matcher = datePattern.matcher(rawDate);
                    List<String> extractedDates = new ArrayList<>();

                    while (matcher.find()) {
                        extractedDates.add(matcher.group());
                    }

                    if (extractedDates.size() == 2) {
                        // 1. ES UN RANGO DE FECHAS (Encontró dos fechas separadas)
                        LocalDate startDate = parseToLocalDate(extractedDates.get(0));
                        LocalDate endDate = parseToLocalDate(extractedDates.get(1));

                        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                            addEventToMap(date.toString(), e);
                        }
                    } else if (extractedDates.size() == 1) {
                        // 2. ES UNA FECHA ÚNICA (Ignora las horas y los paréntesis automáticamente)
                        LocalDate date = parseToLocalDate(extractedDates.get(0));
                        addEventToMap(date.toString(), e);
                    } else {
                        // 3. NO ENCONTRÓ FECHAS VÁLIDAS
                        throw new Exception("No se encontraron fechas en el formato DD/MM/YYYY");
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

    // --- MÉTODOS AUXILIARES ---

    private LocalDate parseToLocalDate(String dateStr) {
        String[] parts = dateStr.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        return LocalDate.of(year, month, day);
    }

    private void addEventToMap(String dateKey, Event e) {
        List<Event> dailyEvents = eventsByDate.computeIfAbsent(dateKey, k -> new ArrayList<>());

        boolean duplicato = dailyEvents.stream()
                .anyMatch(eventoEsistente -> eventoEsistente.title().equals(e.title()));

        if (!duplicato) {
            dailyEvents.add(e);
        }
    }

    // Métodos para la View
    // Devuelve un mapa con todos los climas de la isla para ese día
    public Map<String, Weather> getAllWeatherFor(String date) {
        return weatherByDateAndCity.getOrDefault(date, new HashMap<>());
    }

    public List<Event> getEventsFor(String date) {
        return eventsByDate.getOrDefault(date, Collections.emptyList());
    }
}
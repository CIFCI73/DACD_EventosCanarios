package org.example.control;

import com.google.gson.Gson;
import org.example.datamart.DatamartUpdater;
import org.example.model.Event;
import org.example.model.Weather;

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
                // Extraemos la fecha YYYY-MM-DD del timestamp "ts"
                String date = w.ts().substring(0, 10);
                weatherByDate.put(date, w);
            } else if (topic.equals("Events")) {
                Event e = gson.fromJson(jsonEvent, Event.class);
                // Usamos el campo "ts" del evento para organizarlo por fecha
                String date = e.ts().substring(0, 10);
                // Añadimos el evento a la lista de esa fecha. Si no existe la lista, la crea automáticamente.
                eventsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(e);
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando el JSON en el Datamart: " + e.getMessage());
        }
    }

    // Método para la interfaz de usuario (View): devuelve el clima de una fecha específica
    public Weather getWeatherFor(String date) {
        return weatherByDate.get(date);
    }

    // Método para la interfaz de usuario (View): devuelve la lista de eventos de una fecha específica
    public List<Event> getEventsFor(String date) {
        return eventsByDate.getOrDefault(date, Collections.emptyList());
    }
}
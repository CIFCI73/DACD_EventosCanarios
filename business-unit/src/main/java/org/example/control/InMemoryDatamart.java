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
                // Il meteo è già in formato YYYY-MM-DD, quindi tagliamo solo i primi 10 caratteri
                String dateKey = w.ts().substring(0, 10);
                weatherByDate.put(dateKey, w);

            } else if (topic.equals("Events")) {
                Event e = gson.fromJson(jsonEvent, Event.class);

                // NORMALIZZAZIONE: Trasformiamo la data dell'evento (es. "14/5/2026 19:00")
                // nello standard YYYY-MM-DD per farla combaciare con il meteo.
                String rawDate = e.date();
                String eventDateKey;

                try {
                    // Dividiamo la stringa usando lo spazio per togliere l'ora: resta "14/5/2026"
                    String datePart = rawDate.split(" ")[0];
                    // Dividiamo la data usando la barra "/"
                    String[] parts = datePart.split("/");

                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);

                    // Ricostruiamo la data imponendo il formato YYYY-MM-DD (es. 2026-05-14)
                    eventDateKey = String.format("%04d-%02d-%02d", year, month, day);

                } catch (Exception ex) {
                    // Se la data è illeggibile, usiamo il timestamp come piano di riserva
                    eventDateKey = e.ts().substring(0, 10);
                }

                // Cerchiamo (o creiamo) la lista di eventi per questa data normalizzata
                List<Event> dailyEvents = eventsByDate.computeIfAbsent(eventDateKey, k -> new ArrayList<>());

                // Controllo anti-duplicati: controlliamo se l'evento è già in lista
                boolean duplicato = dailyEvents.stream()
                        .anyMatch(eventoEsistente -> eventoEsistente.title().equals(e.title()));

                if (!duplicato) {
                    dailyEvents.add(e);
                }
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
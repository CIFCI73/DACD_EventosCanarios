package org.example;

import org.example.model.Weather;
import org.example.model.Event;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class InMemoryDatamartTest {

    @Test
    public void testProcessAndGetWeather() {
        InMemoryDatamart datamart = new InMemoryDatamart();

        // Simulamos un JSON del clima llegando desde ActiveMQ
        String jsonWeather = "{\"ts\":\"2026-05-14T16:09:16.727Z\",\"ss\":\"weather-feeder\",\"location\":\"Las Palmas\",\"temp\":21.64,\"humidity\":56,\"rainProb\":0.0}";

        datamart.processEvent("Weather", jsonWeather);

        // Usamos el método getAllWeatherFor de la arquitectura de microclimas
        Map<String, Weather> islandWeather = datamart.getAllWeatherFor("2026-05-14");

        // Como el Datamart guarda las ciudades en minúsculas para facilitar búsquedas, buscamos "las palmas"
        Weather w = islandWeather.get("las palmas");

        assertNotNull(w, "El clima debería haberse guardado correctamente para Las Palmas");
        assertEquals(21.64, w.temp());
    }

    @Test
    public void testDateNormalizationAndDuplicatesInEvents() {
        InMemoryDatamart datamart = new InMemoryDatamart();

        // Evento con con hora en vez de solo día
        String jsonEvent1 = "{\"ts\":\"2026-05-14T16:09:36Z\",\"ss\":\"news-feeder\",\"title\":\"Concierto\",\"date\":\"14/5/2026 19:00\",\"location\":\"Gran Canaria\"}";
        // Evento duplicado (mismo título, distinta hora)
        String jsonEvent2 = "{\"ts\":\"2026-05-14T17:09:36Z\",\"ss\":\"news-feeder\",\"title\":\"Concierto\",\"date\":\"14/5/2026 21:00\",\"location\":\"Gran Canaria\"}";

        datamart.processEvent("Events", jsonEvent1);
        datamart.processEvent("Events", jsonEvent2);

        List<Event> events = datamart.getEventsFor("2026-05-14");

        assertEquals(1, events.size(), "Debería haber ignorado el evento duplicado");
        assertEquals("Concierto", events.get(0).title());
    }
}
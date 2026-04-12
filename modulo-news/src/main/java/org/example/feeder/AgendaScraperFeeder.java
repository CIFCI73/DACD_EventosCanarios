package org.example.feeder;

import org.example.model.Event;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AgendaScraperFeeder implements EventFeeder {
    // Pon aquí la URL real de la agenda del ayuntamiento o cabildo
    private final String url = "URL_DE_LA_PAGINA_WEB_A_SCRAPEAR";

    @Override
    public List<Event> getEvents() {
        List<Event> eventList = new ArrayList<>();

        try {
            // 1. Conectarse a la web y descargar el HTML completo
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0") // IMPORTANTE: Para que la web no piense que somos un bot malicioso
                    .get();

            // 2. Buscar en el HTML las etiquetas que contienen los eventos
            // (Tendrás que inspeccionar la web para cambiar "div.evento-clase" por la clase real)
            Elements eventElements = doc.select("div.clase-html-del-evento");

            // 3. Recorrer los eventos encontrados
            for (Element el : eventElements) {
                String title = el.select("h2.titulo").text(); // Cambiar selector según la web
                String date = el.select("span.fecha").text();
                String location = el.select("span.lugar").text();
                String timestamp = Instant.now().toString();

                // 4. Crear el objeto de nuestro modelo y añadirlo a la lista
                Event event = new Event(title, date, "Hora desconocida", location, false, timestamp);
                eventList.add(event);
            }
        } catch (Exception e) {
            System.err.println("Error al scrapear la web: " + e.getMessage());
        }

        return eventList;
    }
}

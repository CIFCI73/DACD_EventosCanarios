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

    private final String url = "https://www.icdcultural.org/agenda-cultural/eventos?isla=Gran+Canaria";

    @Override
    public List<Event> getEvents() {
        List<Event> eventList = new ArrayList<>();

        try {
            System.out.println("Conectando a la agenda del ICDC...");
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(10000)
                    .get();

            Elements eventosHtml = doc.select("div.descripcion-div");

            for (Element el : eventosHtml) {
                // LOS DATOS INTERNOS
                String title = el.select("h3.titulo-espectaculo").text();
                String date = el.select("div.text-block-107").text();
                String location = el.select("div.text-block-42").text();

                // Nuevos campos obligatorios para el Sprint 2:
                String ts = Instant.now().toString(); // ts: timestamp (reemplaza a capturedAt)
                String ss = "news-feeder";            // ss: source system

                if (!title.isEmpty()) {
                    // ¡Atención al nuevo orden! (ts, ss, title, date, location)
                    eventList.add(new Event(ts, ss, title, date, location));
                }
            }

            System.out.println("¡Éxito! Se extrajeron " + eventList.size() + " eventos.");

        } catch (Exception e) {
            System.err.println("Error al scrapear la agenda: " + e.getMessage());
        }

        return eventList;
    }
}
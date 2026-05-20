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

    private final String url = "https://www.grancanaria.com/turismo/es/agenda/agenda/";

    @Override
    public List<Event> getEvents() {
        List<Event> eventList = new ArrayList<>();

        try {
            System.out.println("Conectando a la agenda del ICDC...");
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(10000)
                    .get();

            Elements eventosHtml = doc.select("div.information");

            for (Element el : eventosHtml) {
                String title = el.select("h3.title").text();
                String date = el.select("p.date").text();

                // Para la ubicación, puedes coger solo 'location' o concatenarla con 'address'
                String location = el.select("p.location").text() + " - " + el.select("p.address").text();

                String ts = Instant.now().toString();
                String ss = "news-feeder";

                if (!title.isEmpty()) {
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
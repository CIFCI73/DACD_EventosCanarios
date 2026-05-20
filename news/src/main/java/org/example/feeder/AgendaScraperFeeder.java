package org.example.feeder;

import org.example.model.Event;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays; // ¡Importante añadir esto!
import java.util.List;

public class AgendaScraperFeeder implements EventFeeder {

    // 1. Ahora usamos una LISTA de URLs (Puedes añadir todas las páginas que quieras aquí)
    private final List<String> urls = Arrays.asList(
            "https://www.grancanaria.com/turismo/es/agenda/agenda/",
            "https://www.grancanaria.com/turismo/es/agenda/agenda/?r=event%2Findex&L=0&id=4066&page=2",
            "https://www.grancanaria.com/turismo/es/agenda/agenda/?r=event%2Findex&L=0&id=4066&page=3&cHash=d5e9f5c51da796bf84d2552b0d2a7a36"
    );

    @Override
    public List<Event> getEvents() {
        List<Event> eventList = new ArrayList<>();

        // 2. Bucle externo: recorre cada URL de nuestra lista
        for (String url : urls) {
            try {
                System.out.println("🌐 Conectando a: " + url);
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .timeout(10000)
                        .get();

                // Usamos los selectores HTML que descubrimos antes
                Elements eventosHtml = doc.select("div.information");

                for (Element el : eventosHtml) {
                    String title = el.select("h3.title").text();
                    String date = el.select("p.date").text();
                    String location = el.select("p.location").text() + " - " + el.select("p.address").text();

                    String ts = Instant.now().toString();
                    String ss = "news-feeder";

                    if (!title.isEmpty()) {
                        eventList.add(new Event(ts, ss, title, date, location));
                    }
                }
                System.out.println("✅ Extraídos eventos de esta página.");

            } catch (Exception e) {
                // Si una página falla, el programa no se detiene, simplemente avisa y pasa a la siguiente URL
                System.err.println("❌ Error al scrapear la url " + url + ": " + e.getMessage());
            }
        }

        System.out.println("🎯 ¡Proceso terminado! Total de eventos extraídos de todas las páginas: " + eventList.size());
        return eventList;
    }
}
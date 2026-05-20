package org.example.view;

import org.example.InMemoryDatamart;
import org.example.model.Event;
import org.example.model.Weather;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RecomendadorCLI {
    private final InMemoryDatamart datamart;

    // Cuando creamos la interfaz, le damos acceso al "almacén" de datos (el Datamart)
    public RecomendadorCLI(InMemoryDatamart datamart) {
        this.datamart = datamart;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=========================================================");
        System.out.println(" 🌴 BIENVENIDO AL RECOMENDADOR INTELIGENTE DE CANARIAS 🌴 ");
        System.out.println("=========================================================");

        while (true) {
            System.out.println("\n👉 Escribe una fecha (formato YYYY-MM-DD) o 'salir' para terminar:");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("salir")) {
                System.out.println("¡Hasta la próxima! 👋");
                break;
            }

            mostrarRecomendacion(input);
        }
        scanner.close();
    }

    private void mostrarRecomendacion(String date) {
        // Pedimos TODOS los climas de la isla para ese día
        Map<String, Weather> islandWeather = datamart.getAllWeatherFor(date);
        List<Event> events = datamart.getEventsFor(date);

        System.out.println("\n--- 📅 Resultados para el " + date + " ---");

        if (islandWeather.isEmpty() && events.isEmpty()) {
            System.out.println("Lo siento, no hay datos meteorológicos ni eventos para esta fecha.");
            return;
        }

        System.out.println("\n🎭 AGENDA CULTURAL Y CONDICIONES LOCALES:");
        if (events.isEmpty()) {
            System.out.println("  No hay eventos programados para esta fecha.");
            return;
        } else {
            for (int i = 0; i < events.size(); i++) {
                Event e = events.get(i);
                // Buscamos el clima específico para la zona de ESTE evento
                Weather localWeather = getWeatherForEvent(e, islandWeather);

                System.out.println("  " + (i + 1) + ". " + e.title() + " (" + e.location() + ")");
                if (localWeather != null) {
                    System.out.println("     🌤️ Clima en la zona: " + localWeather.temp() + "°C | Humedad: " + localWeather.humidity() + "% | Prob. Lluvia: " + (localWeather.rainProb() * 100) + "%");
                } else {
                    System.out.println("     🌤️ Clima en la zona: No disponible");
                }
            }
        }

        // LA PROPUESTA DE VALOR: El Motor de Recomendación Cruzada con Microclimas
        if (!islandWeather.isEmpty() && !events.isEmpty()) {
            System.out.println("\n💡 RECOMENDACIÓN DEL SISTEMA PARA TI:");
            generarConsejoInteligente(islandWeather, events);
        }
        System.out.println("---------------------------------------------------------");
    }

    private void generarConsejoInteligente(Map<String, Weather> islandWeather, List<Event> events) {
        Event bestEvent = null;
        String reason = "";

        // 1. Intentar buscar un plan de exterior si hace buen tiempo en su zona
        for (Event e : events) {
            Weather localW = getWeatherForEvent(e, islandWeather);
            if (localW != null && isOutdoor(e.location())) {
                if (localW.rainProb() < 0.3 && localW.temp() > 20.0 && localW.temp() < 28.0) {
                    bestEvent = e;
                    reason = "Hace un día espectacular en esta zona (" + localW.temp() + "°C) ☀️. ¡Aprovecha el buen tiempo con este evento al aire libre!";
                    break;
                }
            }
        }

        // 2. Si no hay buen tiempo exterior, buscar refugio interior donde el clima sea adverso
        if (bestEvent == null) {
            for (Event e : events) {
                Weather localW = getWeatherForEvent(e, islandWeather);
                if (localW != null && isIndoor(e.location())) {
                    if (localW.rainProb() >= 0.3) {
                        bestEvent = e;
                        reason = "Hay probabilidad de lluvia (" + (localW.rainProb() * 100) + "%) por esta zona ☔. Te aconsejamos este evento a cubierto para no mojarte.";
                        break;
                    } else if (localW.temp() >= 28.0) {
                        bestEvent = e;
                        reason = "Hace bastante calor (" + localW.temp() + "°C) por aquí 🥵. Refúgiate en este evento a cubierto (¡seguro que tienen aire acondicionado!).";
                        break;
                    } else if (localW.temp() <= 18.0) {
                        bestEvent = e;
                        reason = "Refresca un poco por aquí (" + localW.temp() + "°C) 🧥. Te recomendamos este plan en interior para estar más cómodo.";
                        break;
                    }
                }
            }
        }

        // 3. Fallback: cualquier evento si el clima es neutro y no salta ninguna alerta
        if (bestEvent == null && !events.isEmpty()) {
            bestEvent = events.get(0);
            reason = "Las condiciones son muy estables en toda la isla hoy. Cualquier plan es bueno, pero nosotros destacamos este para ti:";
        }

        if (bestEvent != null) {
            System.out.println("   ⭐ EVENTO: " + bestEvent.title());
            System.out.println("   📍 LUGAR: " + bestEvent.location());
            System.out.println("   🗣️ POR QUÉ: " + reason);
        }
    }

    // --- ALGORITMOS DE EMPAREJAMIENTO Y CLASIFICACIÓN ---

    private Weather getWeatherForEvent(Event event, Map<String, Weather> islandWeather) {
        if (islandWeather == null || islandWeather.isEmpty()) return null;

        String loc = event.location().toLowerCase();

        // 1. Buscamos coincidencia exacta de municipio
        for (String city : islandWeather.keySet()) {
            if (loc.contains(city.replace(",es", "").trim())) {
                return islandWeather.get(city);
            }
        }

        // 2. Zona por defecto: Las Palmas de Gran Canaria
        for (String city : islandWeather.keySet()) {
            if (city.contains("las palmas")) {
                return islandWeather.get(city);
            }
        }

        // 3. Fallback: Primer clima disponible
        return islandWeather.values().iterator().next();
    }

    private boolean isIndoor(String location) {
        String[] indoorKw = {"teatro", "auditorio", "centro", "sala", "museo", "pabellón", "espacio", "casa", "biblioteca", "edificio", "recinto"};
        String loc = location.toLowerCase();
        for (String kw : indoorKw) {
            if (loc.contains(kw)) return true;
        }
        return false;
    }

    private boolean isOutdoor(String location) {
        String[] outdoorKw = {"plaza", "parque", "playa", "calle", "avenida", "estadio", "anfiteatro", "muelle", "mirador", "casco"};
        String loc = location.toLowerCase();
        for (String kw : outdoorKw) {
            if (loc.contains(kw)) return true;
        }
        return false;
    }
}
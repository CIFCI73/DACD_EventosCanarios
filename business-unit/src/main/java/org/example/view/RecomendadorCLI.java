package org.example.view;

import org.example.InMemoryDatamart;
import org.example.model.Event;
import org.example.model.Weather;

import java.util.List;
import java.util.Scanner;

public class RecomendadorCLI {
    private final InMemoryDatamart datamart;

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
        Weather weather = datamart.getWeatherFor(date);
        List<Event> events = datamart.getEventsFor(date);

        System.out.println("\n--- 📅 Resultados para el " + date + " ---");

        if (weather == null && events.isEmpty()) {
            System.out.println("Lo siento, no hay datos meteorológicos ni eventos para esta fecha.");
            return;
        }

        // 1. Mostrar estado del clima general
        if (weather != null) {
            System.out.println("🌤️ CLIMA: " + weather.temp() + "°C | Humedad: " + weather.humidity() + "% | Prob. Lluvia: " + (weather.rainProb() * 100) + "%");
        } else {
            System.out.println("🌤️ CLIMA: Aún no tenemos la predicción meteorológica para este día.");
        }

        // 2. Mostrar la lista de eventos disponibles
        System.out.println("\n🎭 AGENDA CULTURAL:");
        if (events.isEmpty()) {
            System.out.println("  No hay eventos programados para esta fecha.");
            return;
        } else {
            for (int i = 0; i < events.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + events.get(i).title() + " (" + events.get(i).location() + ")");
            }
        }

        // 3. LA PROPUESTA DE VALOR: El Motor de Recomendación Cruzada
        if (weather != null && !events.isEmpty()) {
            System.out.println("\n💡 RECOMENDACIÓN DEL SISTEMA PARA TI:");
            generarConsejoInteligente(weather, events);
        }
        System.out.println("---------------------------------------------------------");
    }

    private void generarConsejoInteligente(Weather weather, List<Event> events) {
        boolean isRaining = weather.rainProb() > 0.4;  // Más de 40% de probabilidad
        boolean isHot = weather.temp() > 25.0;         // Más de 25 grados
        boolean isCold = weather.temp() < 18.0;        // Menos de 18 grados

        Event bestEvent = null;
        String reason = "";

        if (isRaining) {
            // Si llueve, buscamos desesperadamente un evento a cubierto (Indoor)
            bestEvent = findEventByType(events, true);
            if (bestEvent != null) {
                reason = "¡Pinta que va a llover! ☔ Te aconsejamos este evento a cubierto para que el clima no te arruine el plan.";
            } else {
                bestEvent = events.get(0);
                reason = "Hay alta probabilidad de lluvia ☔. No tenemos eventos 100% a cubierto confirmados, ¡así que lleva un buen paraguas si vas a este!";
            }
        } else if (isHot) {
            // Si hace mucho calor, recomendamos al aire libre para aprovechar el día (o playas/plazas)
            bestEvent = findEventByType(events, false); // Buscamos Outdoor
            if (bestEvent != null) {
                reason = "Hace un día espectacular y caluroso ☀️. ¡Aprovecha el buen tiempo con este evento al aire libre! (No olvides el protector solar).";
            } else {
                // Si no hay aire libre, sugerimos indoor por el aire acondicionado
                bestEvent = findEventByType(events, true);
                if (bestEvent != null) reason = "Hace bastante calor 🥵. Una gran opción es refugiarte en este evento a cubierto (¡seguro que tienen aire acondicionado!).";
            }
        } else if (isCold) {
            // Si hace frío, mejor a cubierto
            bestEvent = findEventByType(events, true);
            if (bestEvent != null) {
                reason = "Hoy bajan un poco las temperaturas 🧥. Te recomendamos este plan en interior para estar más cómodo.";
            }
        }

        // Si el clima es ideal (ni lluvia, ni mucho frío, ni mucho calor) o no encajó en los filtros
        if (bestEvent == null) {
            bestEvent = events.get(0);
            reason = "¡El clima de hoy es ideal en Canarias! 🌈 Cualquier plan es bueno, pero nosotros destacamos este para ti:";
        }

        // Imprimimos la recomendación final
        System.out.println("   ⭐ EVENTO: " + bestEvent.title());
        System.out.println("   📍 LUGAR: " + bestEvent.location());
        System.out.println("   🗣️ POR QUÉ: " + reason);
    }

    // --- ALGORITMO DE CLASIFICACIÓN DE LUGARES ---
    private Event findEventByType(List<Event> events, boolean wantIndoor) {
        // Palabras clave para detectar si un recinto es cerrado
        String[] indoorKw = {"teatro", "auditorio", "centro", "sala", "museo", "pabellón", "espacio", "casa", "biblioteca", "edificio"};
        // Palabras clave para detectar si es al aire libre
        String[] outdoorKw = {"plaza", "parque", "playa", "calle", "avenida", "estadio", "anfiteatro", "muelle", "mirador"};

        for (Event e : events) {
            String loc = e.location().toLowerCase();
            boolean isIndoor = false;
            boolean isOutdoor = false;

            for (String kw : indoorKw) { if (loc.contains(kw)) isIndoor = true; }
            for (String kw : outdoorKw) { if (loc.contains(kw)) isOutdoor = true; }

            // Si busco Indoor, devuelvo el primero que coincida con interior y NO sea exterior
            if (wantIndoor && isIndoor && !isOutdoor) return e;
            // Si busco Outdoor, devuelvo el primero que coincida
            if (!wantIndoor && isOutdoor) return e;
        }
        return null; // Si no encuentra ninguno de ese tipo exacto
    }
}
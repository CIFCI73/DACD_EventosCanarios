package org.example.view;

import org.example.control.InMemoryDatamart;
import org.example.model.Event;
import org.example.model.Weather;

import java.util.List;
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
        System.out.println(" 🌴 BIENVENIDO AL RECOMENDADOR DE PLANES DE CANARIAS 🌴 ");
        System.out.println("=========================================================");

        while (true) {
            System.out.println("\n👉 Escribe una fecha (formato YYYY-MM-DD) o 'salir' para terminar:");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("salir")) {
                System.out.println("¡Hasta la próxima! 👋");
                break;
            }

            // Si ha escrito una fecha, llamamos al método que busca la información
            mostrarRecomendacion(input);
        }
        scanner.close();
    }

    private void mostrarRecomendacion(String date) {
        // 1. Buscamos en el Datamart usando la fecha que nos dio el usuario
        Weather weather = datamart.getWeatherFor(date);
        List<Event> events = datamart.getEventsFor(date);

        System.out.println("\n--- 📅 Resultados para el " + date + " ---");

        // Si no hay nada de nada
        if (weather == null && events.isEmpty()) {
            System.out.println("Lo siento, no hay datos meteorológicos ni eventos para esta fecha.");
            return;
        }

        // 2. Mostramos el clima y damos el CONSEJO (La "lógica de negocio")
        if (weather != null) {
            System.out.println("🌤️ Clima: " + weather.temp() + "°C, Humedad: " + weather.humidity() + "%");
            if (weather.rainProb() > 0.3) {
                System.out.println("💡 RECOMENDACIÓN: ¡Hay riesgo de lluvia (" + (weather.rainProb() * 100) + "%)! Mejor llévate un paraguas ☔");
            } else {
                System.out.println("💡 RECOMENDACIÓN: Día estupendo para estar al aire libre ☀️");
            }
        } else {
            System.out.println("🌤️ Clima: Aún no tenemos la predicción para este día.");
        }

        // 3. Mostramos la lista de eventos
        System.out.println("\n🎭 Eventos culturales encontrados (" + events.size() + "):");
        if (events.isEmpty()) {
            System.out.println("No hay eventos programados en nuestra agenda.");
        } else {
            for (int i = 0; i < events.size(); i++) {
                Event e = events.get(i);
                System.out.println("  " + (i + 1) + ". " + e.title() + " (Lugar: " + e.location() + ")");
            }
        }
        System.out.println("----------------------------------------");
    }
}
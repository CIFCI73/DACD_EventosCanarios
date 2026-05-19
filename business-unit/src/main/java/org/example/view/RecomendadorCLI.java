package org.example.view;

import org.example.InMemoryDatamart;
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

        // 2. Mostramos el clima y damos el CONSEJO (La verdadera "Lógica de Negocio")
        if (weather != null) {
            System.out.println("🌤️ Clima: " + weather.temp() + "°C, Humedad: " + weather.humidity() + "%, Prob. Lluvia: " + (weather.rainProb() * 100) + "%");

            System.out.print("💡 RECOMENDACIÓN: ");
            if (weather.rainProb() > 0.4) {
                System.out.println("Alta probabilidad de lluvia. ¡No olvides el paraguas y prioriza eventos a cubierto! ☔");
            } else if (weather.temp() > 28.0) {
                System.out.println("Hace bastante calor. Llévate agua, ponte crema solar y busca la sombra. 🥵");
            } else if (weather.temp() < 16.0) {
                System.out.println("Temperaturas inusualmente bajas para Canarias. ¡Coge una chaqueta antes de salir! 🧥");
            } else {
                System.out.println("Condiciones meteorológicas excelentes. ¡Día ideal para disfrutar de la oferta cultural! ☀️");
            }
        } else {
            System.out.println("🌤️ Clima: Aún no tenemos la predicción meteorológica para este día.");
        }

        // 3. Filtramos y mostramos SOLO los eventos de Gran Canaria
        System.out.println("\n🎭 Eventos culturales en Gran Canaria:");

        if (events.isEmpty()) {
            System.out.println("No hay eventos programados en nuestra agenda global.");
        } else {
            int count = 1;
            for (Event e : events) {
                // Filtro geográfico: solo pasa si la ubicación contiene "Gran Canaria" o "Las Palmas"
                if (e.location().contains("Gran Canaria") || e.location().contains("Las Palmas")) {
                    System.out.println("  " + count + ". " + e.title() + " (Lugar: " + e.location() + ")");
                    count++;
                }
            }
            // Si después de filtrar el contador sigue en 1, significa que no había nada en esa isla
            if (count == 1) {
                System.out.println("  No hay eventos específicos en Gran Canaria para esta fecha.");
            }
        }
        System.out.println("----------------------------------------");
    }
}
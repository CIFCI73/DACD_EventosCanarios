package org.example.control;

import org.example.feeder.EventFeeder;
import org.example.model.Event;
import org.example.store.EventStore;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {
    private final EventFeeder feeder;
    private final EventStore store;

    // El constructor recibe las interfaces, ¡no las clases concretas! (Principio SOLID)
    public Controller(EventFeeder feeder, EventStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    public void start() {
        Timer timer = new Timer();

        // Creamos la tarea que se va a repetir
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Iniciando captura de eventos...");
                // 1. Extraer datos de la web
                List<Event> events = feeder.getEvents();

                // 2. Guardarlos en SQLite
                if (!events.isEmpty()) {
                    store.store(events);
                } else {
                    System.out.println("No se encontraron eventos en esta ejecución.");
                }
            }
        };

        // Ejecutar inmediatamente (0 ms de retraso) y luego cada 1 hora (3600000 ms)
        timer.scheduleAtFixedRate(task, 0, 3600000);
    }
}

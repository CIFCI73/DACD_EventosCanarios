package org.example;

import org.example.control.Controller;
import org.example.feeder.AgendaScraperFeeder;
import org.example.feeder.EventFeeder;
import org.example.store.ActiveMQEventStore;
import org.example.store.EventStore;

public class Main {
    public static void main(String[] args) {
        EventFeeder scraper = new AgendaScraperFeeder();

        // ¡CAMBIO CLAVE! Ahora usamos ActiveMQ en lugar de SQLite
        EventStore publisher = new ActiveMQEventStore();

        Controller controller = new Controller(scraper, publisher);

        System.out.println("Arrancando el Publisher de Eventos...");
        controller.start();
    }
}
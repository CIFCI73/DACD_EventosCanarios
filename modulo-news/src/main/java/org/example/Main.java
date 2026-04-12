package org.example;

import org.example.control.Controller;
import org.example.feeder.AgendaScraperFeeder;
import org.example.feeder.EventFeeder;
import org.example.store.EventStore;
import org.example.store.SqliteEventStore;

public class Main {
    public static void main(String[] args) {
        // 1. Instanciar las clases concretas
        EventFeeder scraper = new AgendaScraperFeeder();
        EventStore database = new SqliteEventStore();

        // 2. Inyectarlas en el controlador
        Controller controller = new Controller(scraper, database);

        // 3. ¡Arrancar la aplicación!
        System.out.println("Arrancando el Sensor de Eventos...");
        controller.start();
    }
}
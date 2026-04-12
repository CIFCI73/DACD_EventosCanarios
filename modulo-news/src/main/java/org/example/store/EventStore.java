package org.example.store;

import org.example.model.Event;
import java.util.List;

public interface EventStore {
    // Recibe la lista de eventos obtenidos por el scraper y los guarda uno a uno en SQLite
    void store(List<Event> events);
}

package org.example.store;

public interface EventStore {
    // Recibe el topic ("Weather" o "Events") y el texto en formato JSON
    void save(String topic, String jsonEvent);
}
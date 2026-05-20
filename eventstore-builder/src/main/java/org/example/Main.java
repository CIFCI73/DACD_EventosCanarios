package org.example;

import org.example.store.EventStore;
import org.example.store.FileEventStore;
import org.example.subscriber.EventBuilderSubscriber;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando el módulo Event Store Builder...");

        // Instanciamos el "Archivista"
        EventStore fileStore = new FileEventStore();

        // Instanciamos el "Escuchador" y le pasamos el Archivista
        EventBuilderSubscriber subscriber = new EventBuilderSubscriber(fileStore);

        // Encendemos
        subscriber.start();
    }
}
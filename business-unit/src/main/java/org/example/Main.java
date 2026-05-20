package org.example;

import org.example.reader.EventStoreReader;
import org.example.subscriber.ActiveMQListener;
import org.example.view.RecomendadorCLI;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Arrancando Business Unit...");

        // Instanciamos el Datamart
        InMemoryDatamart datamart = new InMemoryDatamart();

        // Cargamos los datos historicos
        // Usamos la ruta donde se guardan los archivos .events del Sprint 2
        EventStoreReader reader = new EventStoreReader(datamart);
        reader.readHistory("eventstore");

        // Encendemos la escucha en tiempo real
        ActiveMQListener listener = new ActiveMQListener(datamart);
        listener.start();

        // Arrancamos tu interfaz de usuario
        RecomendadorCLI gui = new RecomendadorCLI(datamart);
        gui.start();
    }
}
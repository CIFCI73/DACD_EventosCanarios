package org.example;

import org.example.control.InMemoryDatamart;
import org.example.reader.EventStoreReader;
import org.example.subscriber.ActiveMQListener;
import org.example.view.RecomendadorCLI;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Arrancando Business Unit...");

        // 1. Instanciamos tu Datamart (el corazón del módulo)
        InMemoryDatamart datamart = new InMemoryDatamart();

        // 2. Cargamos los datos HISTÓRICOS (Trabajo de tu compañero)
        // Usamos la ruta donde se guardan los archivos .events del Sprint 2
        EventStoreReader reader = new EventStoreReader(datamart);
        reader.readHistory("eventstore");

        // 3. Encendemos la escucha en TIEMPO REAL (Trabajo de tu compañero)
        ActiveMQListener listener = new ActiveMQListener(datamart);
        listener.start();

        // 4. Arrancamos tu interfaz de usuario (Tu trabajo)
        RecomendadorCLI gui = new RecomendadorCLI(datamart);
        gui.start();
    }
}
package org.example;

// Importamos las clases que hemos creado en las otras carpetas
import org.example.control.WeatherControl;
import org.example.feeder.OpenWeatherMapFeeder;
import org.example.store.SqliteWeatherStore;

public class Main {

    // Este es el método que Java busca para iniciar cualquier programa
    public static void main(String[] args) {

        // 1. Definimos dónde se va a guardar el archivo de la base de datos
        String dbPath = "jdbc:sqlite:weather.db";

        // 2. Creamos a los "trabajadores" (usamos las clases reales, no las interfaces)
        OpenWeatherMapFeeder feeder = new OpenWeatherMapFeeder();
        SqliteWeatherStore store = new SqliteWeatherStore(dbPath);

        // 3. Contratamos al "jefe" (el control) y le entregamos sus herramientas
        WeatherControl control = new WeatherControl(feeder, store);

        // 4. ¡Encendemos la máquina!
        System.out.println("Iniciando el módulo Weather...");
        control.start();
    }
}
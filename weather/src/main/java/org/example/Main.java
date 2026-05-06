package org.example;

import org.example.control.WeatherControl;
import org.example.feeder.OpenWeatherMapFeeder;
import org.example.store.ActiveMQWeatherStore;
import org.example.store.WeatherStore;

public class Main {

    public static void main(String[] args) {

        // Dónde se va a guardar el archivo, ahora usamos ActiveMQ en lugar de SQLite
        // String dbPath = "jdbc:sqlite:weather.db"; (Ya no se usa)

        OpenWeatherMapFeeder feeder = new OpenWeatherMapFeeder();
        WeatherStore store = new ActiveMQWeatherStore(); // Modificato per lo Sprint 2
        WeatherControl control = new WeatherControl(feeder, store);

        System.out.println("Iniciando el módulo Weather...");
        control.start();
    }
}
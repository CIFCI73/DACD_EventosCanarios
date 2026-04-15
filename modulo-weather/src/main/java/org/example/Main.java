package org.example;

import org.example.control.WeatherControl;
import org.example.feeder.OpenWeatherMapFeeder;
import org.example.store.SqliteWeatherStore;

public class Main {

    public static void main(String[] args) {

        //Dónde se va a guardar el archivo de la base de datos
        String dbPath = "jdbc:sqlite:weather.db";

        OpenWeatherMapFeeder feeder = new OpenWeatherMapFeeder();
        SqliteWeatherStore store = new SqliteWeatherStore(dbPath);
        WeatherControl control = new WeatherControl(feeder, store);

        System.out.println("Iniciando el módulo Weather...");
        control.start();
    }
}
package org.example.control;

import org.example.feeder.WeatherFeeder;
import org.example.model.Weather;
import org.example.store.WeatherStore;

import java.util.Timer;
import java.util.TimerTask;

public class WeatherControl {

    private final WeatherFeeder feeder;
    private final WeatherStore store;

    public WeatherControl(WeatherFeeder feeder, WeatherStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    //hace el trabajo de descargar y guardar una sola vez
    public void execute() {
        Weather weather = feeder.getWeather("Las Palmas de Gran Canaria");

        // Si el feeder nos ha dado datos, los guardamos en la base de datos (ahora en ActiveMQ)
        if (weather != null) {
            store.store(weather);
            System.out.println("Datos guardados: " + weather.location() +
                    " | Temp: " + weather.temp() + "°C" +
                    " | Humedad: " + weather.humidity() + "%" +
                    " | Lluvia: " + weather.rainProb() +
                    " | Fecha: " + weather.ts()); // ATTENZIONE: cambiato capturedAt() in ts() per lo Sprint 2
        } else {
            System.out.println("No se pudieron obtener los datos. Revisa tu API key o la conexión.");
        }
    }

    //Repite execute automáticamente cada minuto
    public void start() {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                execute();
            }
        };

        long delay = 0;
        long period = 60000; // 60000 ms = 1 minuto

        timer.schedule(task, delay, period);
        System.out.println("Temporizador iniciado. Consultando el tiempo cada minuto...");
    }
}
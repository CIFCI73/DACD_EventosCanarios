package org.example;

import org.example.feeder.WeatherFeeder;
import org.example.model.Weather;
import org.example.store.WeatherStore;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherControl {

    private final WeatherFeeder feeder;
    private final WeatherStore store;

    public WeatherControl(WeatherFeeder feeder, WeatherStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    public void execute() {
        // Ahora recibimos una lista de días futuros
        List<Weather> weatherForecasts = feeder.getWeather("Las Palmas de Gran Canaria");

        if (weatherForecasts != null && !weatherForecasts.isEmpty()) {
            System.out.println("Enviando el pronóstico de los próximos días a ActiveMQ...");

            for (Weather weather : weatherForecasts) {
                store.store(weather); // Lo publicamos en ActiveMQ

                System.out.println("Datos guardados: " + weather.location() +
                        " | Temp: " + weather.temp() + "°C" +
                        " | Humedad: " + weather.humidity() + "%" +
                        " | Lluvia: " + (weather.rainProb() * 100) + "%" +
                        " | Fecha: " + weather.ts());
            }
        } else {
            System.out.println("No se pudieron obtener los datos. Revisa tu API key o la conexión.");
        }
    }

    public void start() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                execute();
            }
        };

        // Si es una previsión de 5 días, no hace falta consultar cada minuto.
        // Cada hora (3600000 ms) es más que suficiente para no agotar el límite gratis de la API.
        long delay = 0;
        long period = 3600000;

        timer.schedule(task, delay, period);
        System.out.println("Temporizador iniciado. Consultando la previsión meteorológica cada hora...");
    }
}
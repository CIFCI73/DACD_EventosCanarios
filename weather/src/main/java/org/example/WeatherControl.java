package org.example;

import org.example.feeder.WeatherFeeder;
import org.example.model.Weather;
import org.example.store.WeatherStore;

import java.util.Arrays;
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
        // definimos las zonas climáticas clave de la isla
        List<String> municipalities = Arrays.asList(
                "Las Palmas de Gran Canaria", // Norte/Capital
                "San Bartolomé de Tirajana",  // Sur
                "Telde",                      // Este
                "Agaete",                     // Noroeste
                "Tejeda"                      // Cumbre/Centro
        );

        System.out.println("Iniciando escaneo meteorológico insular...");

        // pedimos el clima para CADA municipio
        for (String city : municipalities) {
            List<Weather> weatherForecasts = feeder.getWeather(city + ",ES"); // Añadimos ,ES para mayor precisión en la API

            if (weatherForecasts != null && !weatherForecasts.isEmpty()) {
                for (Weather weather : weatherForecasts) {
                    store.store(weather); // Lo publicamos en ActiveMQ
                }
                System.out.println("✅ Predicción guardada para: " + city);
            } else {
                System.out.println("❌ No se pudieron obtener los datos para: " + city);
            }
        }
    }

    public void start() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() { execute(); }
        };

        timer.schedule(task, 0, 3600000); // se ejecuta cada hora
        System.out.println("Temporizador iniciado. Consultando la previsión meteorológica cada hora...");
    }
}
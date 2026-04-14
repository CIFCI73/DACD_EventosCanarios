package org.example.control;

import org.example.feeder.WeatherFeeder;
import org.example.model.Weather;
import org.example.store.WeatherStore;

import java.util.Timer;
import java.util.TimerTask;

public class WeatherControl {

    // 1. Las herramientas
    private final WeatherFeeder feeder;
    private final WeatherStore store;

    // 2. El Constructor: recibe las herramientas desde fuera
    public WeatherControl(WeatherFeeder feeder, WeatherStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    // 3. El método execute: hace el trabajo de descargar y guardar UNA sola vez
    public void execute() {
        // Pedimos el tiempo de Las Palmas al feeder
        Weather weather = feeder.getWeather("Las Palmas de Gran Canaria");

        // Si el feeder nos ha dado datos, los guardamos en la base de datos
        if (weather != null) {
            store.store(weather);
            System.out.println("Datos guardados: " + weather.temp() + "°C en " + weather.location());
        } else {
            System.out.println("No se pudieron obtener los datos. Revisa tu API key o la conexión.");
        }
    }

    // 4. El método start: repite el método execute automáticamente cada minuto
    public void start() {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                execute(); // Aquí es donde llamamos al método de arriba
            }
        };

        long delay = 0;
        long period = 60000; // 60000 ms = 1 minuto (para probar rápido)

        timer.schedule(task, delay, period);
        System.out.println("Temporizador iniciado. Consultando el tiempo cada minuto...");
    }
} // <-- Esta llave final cierra la clase entera
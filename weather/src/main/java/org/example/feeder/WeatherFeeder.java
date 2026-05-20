package org.example.feeder;

import org.example.model.Weather;
import java.util.List;

public interface WeatherFeeder {
    // DEVUELVE UNA LISTA DE PREDICCIONES
    List<Weather> getWeather(String location);
}

package org.example.feeder;

import org.example.model.Weather;

public interface WeatherFeeder {
    // Recibe el nombre de la ciudad y devuelve el objeto Weather con la previsión
    Weather getWeather(String location);
}

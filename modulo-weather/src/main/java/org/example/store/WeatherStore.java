package org.example.store;

import org.example.model.Weather;

public interface WeatherStore {
    // Recibe el objeto Weather y lo inserta (INSERT) en la base de datos SQLite
    void store(Weather weather);
}

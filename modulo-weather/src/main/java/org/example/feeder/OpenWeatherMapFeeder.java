package org.example.feeder;

import org.example.model.Weather;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.time.Instant;

public class OpenWeatherMapFeeder implements WeatherFeeder {
    private final String apiKey = "cbe75b0dbf0b1672f5717afb30edd422"; // clave de OpenWeatherMap
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public Weather getWeather(String location) {
        // Construir la URL de la API
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey + "&units=metric";

        //petición HTTP usando OkHttp
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                //Convertir el texto a un Objeto JSON con Gson
                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                //Extraer los datos que nos interesan
                JsonObject main = jsonObject.getAsJsonObject("main");
                double temp = main.get("temp").getAsDouble();
                int humidity =  main.get("humidity").getAsInt();
                String ts = Instant.now().toString(); //Formato ISO-8601
                String ss = "weather-feeder"; // Identificador de la fuente

                return new Weather(ts, ss, location, temp, humidity, 0.0);
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con OpenWeatherMap: " + e.getMessage());
        }
        return null;
    }
}
package org.example.feeder;

import org.example.model.Weather;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class OpenWeatherMapFeeder implements WeatherFeeder {
    private final String apiKey = "cbe75b0dbf0b1672f5717afb30edd422"; // Tu clave
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public List<Weather> getWeather(String location) {
        List<Weather> forecastList = new ArrayList<>();
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + location + "&appid=" + apiKey + "&units=metric";

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                // La API de pronóstico devuelve una lista llamada "list"
                JsonArray list = jsonObject.getAsJsonArray("list");

                for (int i = 0; i < list.size(); i++) {
                    JsonObject item = list.get(i).getAsJsonObject();
                    String dtTxt = item.get("dt_txt").getAsString(); // Ej: "2026-05-20 12:00:00"

                    // filtramos para coger solo la previsión del mediodía de cada día
                    if (dtTxt.contains("12:00:00")) {
                        JsonObject main = item.getAsJsonObject("main");
                        double temp = main.get("temp").getAsDouble();
                        int humidity = main.get("humidity").getAsInt();

                        // la probabilidad de lluvia viene de 0 a 1
                        double rainProb = item.has("pop") ? item.get("pop").getAsDouble() : 0.0;

                        // Formateamos la fecha para que el Datamart la entienda
                        String ts = dtTxt.replace(" ", "T") + "Z";
                        String ss = "weather-feeder";

                        forecastList.add(new Weather(ts, ss, location, temp, humidity, rainProb));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con OpenWeatherMap: " + e.getMessage());
        }
        return forecastList;
    }
}
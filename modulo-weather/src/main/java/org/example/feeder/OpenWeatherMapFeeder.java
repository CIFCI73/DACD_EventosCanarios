package org.example.feeder;

import org.example.model.Weather;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.time.Instant;

public class OpenWeatherMapFeeder implements WeatherFeeder {
    private final String apiKey = "TU_API_KEY_AQUI"; // Sustituye por tu clave de OpenWeatherMap
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public Weather getWeather(String location) {
        // 1. Construir la URL de la API con la ciudad y tu clave (en grados Celsius)
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey + "&units=metric";

        // 2. Hacer la petición HTTP usando OkHttp
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                // 3. Convertir el texto de la respuesta a un Objeto JSON con Gson
                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                // 4. Extraer solo los datos que nos interesan
                double temp = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
                int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
                String timestamp = Instant.now().toString(); // Formato ISO 8601 (ej. 2024-03-20T15:30:00Z)

                // 5. Devolver nuestro modelo limpio
                return new Weather(location, temp, humidity, 0.0, timestamp);
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con OpenWeatherMap: " + e.getMessage());
        }
        return null; // Si falla, devolvemos null
    }
}
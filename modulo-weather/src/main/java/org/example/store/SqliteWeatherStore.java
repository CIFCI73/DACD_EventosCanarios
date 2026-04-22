package org.example.store;

import org.example.model.Weather;
import java.sql.*;

public class SqliteWeatherStore implements WeatherStore {

    // Almacenará la ruta de conexión al archivo SQLite
    private final String dbPath;

    // Constructor: recibe la ruta cuando creamos el objeto
    public SqliteWeatherStore(String dbPath) {
        this.dbPath = dbPath;
        initDatabase();
    }

    private void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS weather_data (" +
                "location TEXT," +
                "temp REAL," +
                "humidity INTEGER," +
                "rain_prob REAL," +
                "captured_at TEXT" +
                ");";

        try (Connection conn = DriverManager.getConnection(dbPath);
             Statement stmt = conn.createStatement()) {

            // Ejecutamos la sentencia SQL
            stmt.execute(sql);

        } catch (SQLException e) {
            // Si hay un error de SQL, lo mostramos
            System.err.println("Error al inicializar la tabla: " + e.getMessage());
        }
    }

    @Override
    public void store(Weather weather) {
        // Preparamos la "plantilla" de inserción.
        String sql = "INSERT INTO weather_data (location, temp, humidity, rain_prob, captured_at) VALUES (?, ?, ?, ?, ?)";

        // Abrimos la conexión y preparamos la "instrucción preparada" (PreparedStatement)
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Asignemos los valores del registro Weather a los signos de interrogación            pstmt.setString(1, weather.location());  // Primo ?: la città (String)
            pstmt.setDouble(2, weather.temp());      // Segundo ?: temp (double)
            pstmt.setInt(3, weather.humidity());     // Tercero ?: humedad (int)
            pstmt.setDouble(4, weather.rainProb());  // cuarto ?: posibilidad de lluvia (double)
            pstmt.setString(5, weather.capturedAt());// Quinto ?: fecha y hora (String)

            pstmt.executeUpdate();

        } catch (SQLException e) {
            // Si algo falla al insertar, lo notificamos
            System.err.println("Error al guardar en SQLite: " + e.getMessage());
        }
    }

}
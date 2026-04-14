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
        // 1. Prepariamo la "maschera" dell'inserimento.
        // I '?' sono segnaposto (placeholders) per i dati reali.
        String sql = "INSERT INTO weather_data (location, temp, humidity, rain_prob, captured_at) VALUES (?, ?, ?, ?, ?)";

        // 2. Apriamo la connessione e prepariamo il "comando sicuro" (PreparedStatement)
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 3. Assegniamo i valori del Record Weather ai punti interrogativi
            pstmt.setString(1, weather.location());  // Primo ?: la città (String)
            pstmt.setDouble(2, weather.temp());      // Secondo ?: temperatura (double)
            pstmt.setInt(3, weather.humidity());     // Terzo ?: umidità (int)
            pstmt.setDouble(4, weather.rainProb());  // Quarto ?: prob. pioggia (double)
            pstmt.setString(5, weather.capturedAt());// Quinto ?: data e ora (String)

            // 4. Eseguiamo l'operazione di scrittura
            pstmt.executeUpdate();

        } catch (SQLException e) {
            // Si algo falla al insertar, lo notificamos
            System.err.println("Error al guardar en SQLite: " + e.getMessage());
        }
    }

}
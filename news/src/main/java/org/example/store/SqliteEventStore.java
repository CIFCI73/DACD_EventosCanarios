package org.example.store;

import org.example.model.Event;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class SqliteEventStore implements EventStore {
    private final String dbUrl = "jdbc:sqlite:events.db";

    public SqliteEventStore() {
        initDatabase();
    }

    private void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "date TEXT, " +
                "location TEXT, " +
                "captured_at TEXT)";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.err.println("Error creando la base de datos: " + e.getMessage());
        }
    }

    @Override
    public void store(List<Event> events) {
        String sql = "INSERT INTO events (title, date, location, captured_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Event event : events) {
                pstmt.setString(1, event.title());
                pstmt.setString(2, event.date());
                pstmt.setString(3, event.location());
                pstmt.setString(4, event.ts());
                pstmt.executeUpdate();
            }
            System.out.println("Guardados " + events.size() + " eventos en la base de datos.");

        } catch (Exception e) {
            System.err.println("Error al guardar eventos: " + e.getMessage());
        }
    }
}

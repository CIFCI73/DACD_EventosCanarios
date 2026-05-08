package org.example.reader;

import org.example.datamart.DatamartUpdater;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class EventStoreReader {
    private final DatamartUpdater datamart;

    public EventStoreReader(DatamartUpdater datamart) {
        this.datamart = datamart;
    }

    // El método que arranca la lectura del pasado
    public void readHistory(String baseDirPath) {
        System.out.println("⏳ Cargando historial desde: " + baseDirPath);

        try (Stream<Path> paths = Files.walk(Paths.get(baseDirPath))) {
            paths.filter(Files::isRegularFile) // Solo buscar archivos (no carpetas)
                    .filter(path -> path.toString().endsWith(".events")) // Solo extensión .events
                    .forEach(this::procesarArchivo);

            System.out.println("✅ Historial cargado con éxito en el Datamart.");
        } catch (Exception e) {
            System.err.println("❌ Error buscando archivos en el historial: " + e.getMessage());
        }
    }

    private void procesarArchivo(Path filePath) {
        // Adivinamos el topic leyendo el nombre de la carpeta en la ruta
        String topic = filePath.toString().contains("Weather") ? "Weather" : "Events";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            // Leer el archivo línea por línea (cada línea es un JSON)
            while ((line = reader.readLine()) != null) {
                // Se lo enviamos al Datamart
                datamart.processEvent(topic, line);
            }
        } catch (Exception e) {
            System.err.println("Error leyendo el archivo " + filePath + ": " + e.getMessage());
        }
    }
}
package org.example.store;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileWriter;

public class FileEventStore implements EventStore {
    // La carpeta principal que pide el profesor
    private final String baseDir = "eventstore";

    @Override
    public void save(String topic, String jsonEvent) {
        try {
            // 1. Extraer los datos del JSON usando Gson
            // (Convertimos el texto a un objeto manipulable)
            JsonObject jsonObject = JsonParser.parseString(jsonEvent).getAsJsonObject();
            String ts = jsonObject.get("ts").getAsString();
            String ss = jsonObject.get("ss").getAsString();

            // 2. Formatear la fecha (YYYYMMDD)
            // Nuestro 'ts' viene como "2024-03-20T10:30:00Z"
            // Cortamos los primeros 10 caracteres ("2024-03-20") y quitamos los guiones
            String dateOnly = ts.substring(0, 10);
            String yyyymmdd = dateOnly.replace("-", ""); // Resultado: "20240320"

            // 3. Construir la ruta de las carpetas: eventstore/{topic}/{ss}
            String directoryPath = baseDir + File.separator + topic + File.separator + ss;
            File directory = new File(directoryPath);

            // 4. Crear las carpetas en el disco duro (si no existen ya)
            if (!directory.exists()) {
                directory.mkdirs(); // mkdirs crea toda la ruta de golpe
            }

            // 5. Construir la ruta final del archivo con extensión .events
            String filePath = directoryPath + File.separator + yyyymmdd + ".events";

            // 6. Guardar el JSON en el archivo
            // El 'true' en FileWriter significa modo APPEND (añadir al final sin borrar)
            try (FileWriter writer = new FileWriter(filePath, true)) {
                writer.write(jsonEvent + "\n"); // Añadimos salto de línea para el formato JSON Lines
            }

            System.out.println("💾 Evento guardado correctamente en: " + filePath);

        } catch (Exception e) {
            System.err.println("❌ Error al guardar el archivo: " + e.getMessage());
        }
    }
}

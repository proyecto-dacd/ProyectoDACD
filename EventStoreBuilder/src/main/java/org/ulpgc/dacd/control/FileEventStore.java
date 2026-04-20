package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileEventStore implements EventStore {
    private final String baseDirectory;

    public FileEventStore(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void save(String topic, String eventJson) {
        try {
            JsonObject jsonObject = JsonParser.parseString(eventJson).getAsJsonObject();
            String source = jsonObject.get("ss").getAsString();

            String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

            String directoryPath = baseDirectory + "/events/" + topic + "/" + source;
            String filePath = directoryPath + "/" + date + ".events";

            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            Path path = Paths.get(filePath);
            Files.writeString(path, eventJson + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            System.out.println("Evento guardado con éxito en: " + filePath);

        } catch (Exception e) {
            System.err.println("Error guardando el evento en disco: " + e.getMessage());
        }
    }
}
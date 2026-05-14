package org.ulpgc.dacd.controller.ingestion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.ulpgc.dacd.model.entities.CryptoNews;
import org.ulpgc.dacd.model.entities.CryptoPrice;
import org.ulpgc.dacd.model.persistence.DatamartStore;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class HistoricalLoader {
    private final DatamartStore datamartStore;
    private final Gson gson = new Gson();

    public HistoricalLoader(DatamartStore datamartStore) {
        this.datamartStore = datamartStore;
    }

    public void loadHistoricalData(String datalakePath) {
        AtomicInteger count = new AtomicInteger(0);

        try (Stream<Path> paths = Files.walk(Paths.get(datalakePath))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".events"))
                    .forEach(path -> loadFile(path, count));

            System.out.println("[Historical] ✅ Carga completada. Registros procesados: " + count.get());
        } catch (IOException e) {
            System.err.println("[Historical] ❌ Error en datalake: " + e.getMessage());
        }
    }

    private void loadFile(Path path, AtomicInteger count) {
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                if (processLine(line)) count.incrementAndGet();
            });
        } catch (IOException ignored) {}
    }

    private boolean processLine(String line) {
        try {
            JsonObject data = gson.fromJson(cleanJson(line), JsonObject.class);
            String source = data.get("ss").getAsString();

            if ("coin-gecko-feeder".equals(source)) handlePrice(data);
            else if ("Decrypt-Scraping".equals(source)) handleNews(data);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void handlePrice(JsonObject data) {
        datamartStore.insertPrice(new CryptoPrice(
                data.get("id").getAsString(),
                data.get("price").getAsDouble(),
                data.get("ts").getAsString(),
                0.0 // Sentimiento neutral para datos históricos
        ));
    }

    private void handleNews(JsonObject data) {
        String title = data.get("title").getAsString();
        String url = data.get("url").getAsString();
        String ts = data.get("ts").getAsString();

        if (data.getAsJsonArray("coins").isEmpty()) {
            datamartStore.insertNews(new CryptoNews("general", title, url, ts));
        } else {
            data.getAsJsonArray("coins").forEach(c ->
                    datamartStore.insertNews(new CryptoNews(c.getAsString(), title, url, ts))
            );
        }
    }

    private String cleanJson(String json) {
        return json.contains("{") ? json.substring(json.indexOf("{")) : json;
    }
}
package org.ulpgc.dacd.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.ulpgc.dacd.model.CryptoNews;
import org.ulpgc.dacd.model.CryptoPrice;
import org.ulpgc.dacd.model.DatamartStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class HistoricalLoader {
    private final DatamartStore datamartStore;
    private final Gson gson;

    public HistoricalLoader(DatamartStore datamartStore) {
        this.datamartStore = datamartStore;
        this.gson = new Gson();
    }

    public void loadHistoricalData(String datalakePath) {
        System.out.println("[HistoricalLoader] ⏳ Iniciando carga de datos históricos desde: " + datalakePath);

        try (Stream<Path> paths = Files.walk(Paths.get(datalakePath))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".events"))
                    .forEach(this::processFile);

            System.out.println("[HistoricalLoader] ✅ Carga histórica completada con éxito.");
        } catch (IOException e) {
            System.err.println("[HistoricalLoader] ❌ Error accediendo al datalake: " + e.getMessage());
        }
    }

    private void processFile(Path path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        } catch (IOException e) {
            System.err.println("[HistoricalLoader] Error leyendo fichero " + path.getFileName() + ": " + e.getMessage());
        }
    }

    private void processLine(String line) {
        try {
            JsonObject json = gson.fromJson(line, JsonObject.class);

            if (!json.has("ss")) return;

            String source = json.get("ss").getAsString();

            if ("coin-gecko-feeder".equals(source)) {
                // 1. Procesar Precios
                String id = json.get("id").getAsString();
                double price = json.get("price").getAsDouble();
                String ts = json.get("ts").getAsString();

                datamartStore.insertPrice(new CryptoPrice(id, price, ts));

            } else if ("Decrypt-Scraping".equals(source)) {
                // 2. Procesar Noticias
                String title = json.get("title").getAsString();
                String url = json.get("url").getAsString();
                String ts = json.get("ts").getAsString();
                JsonArray coins = json.getAsJsonArray("coins");

                for (JsonElement coin : coins) {
                    String cryptoId = coin.getAsString();

                    CryptoNews news = new CryptoNews(cryptoId, title, url, ts);
                    datamartStore.insertNews(news);
                }
            }
        } catch (Exception e) {
            System.err.println("[HistoricalLoader] ⚠️ Error parseando línea: " + line + " -> " + e.getMessage());
        }
    }
}
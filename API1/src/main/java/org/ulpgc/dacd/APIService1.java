package org.ulpgc.dacd;

import org.ulpgc.dacd.model.APIData1;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class APIService1 {
    private final SQLiteManager sqliteManager;
    private final APIClient1 client;

    public APIService1(SQLiteManager sqliteManager) {
        this.sqliteManager = sqliteManager;
        String apiKey = loadApiKey();
        this.client = new APIClient1(apiKey);
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                captureAndProcess();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    private void captureAndProcess() {
        String rawJson = client.getRawJson("");

        if (rawJson != null) {
            JsonArray array = JsonParser.parseString(rawJson).getAsJsonArray();

            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.get(i).getAsJsonObject();

                String id = obj.get("id").getAsString();
                double price = obj.get("current_price").getAsDouble();
                long volume = obj.get("total_volume").getAsLong();
                long marketCap = obj.get("market_cap").getAsLong();
                int rank = obj.get("market_cap_rank").getAsInt();
                String timestamp = obj.get("last_updated").getAsString();

                APIData1 data = new APIData1(id, price, volume, marketCap, rank, timestamp);

                sqliteManager.save(data);

                System.out.println("Dato guardado (" + (i+1) + "/10): " + id);

            }
        }
    }

    private String loadApiKey() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                return properties.getProperty("api.key");
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar la API Key");
        }
        return "";
    }
}
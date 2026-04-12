package org.ulpgc.dacd;

import org.ulpgc.dacd.model.CoinGeckoCurrency;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CurrencyManager {
    private final CoinGeckoReader client;
    private final DatabaseManager sqliteManager;

    public CurrencyManager(CoinGeckoReader client, DatabaseManager sqliteManager) {
        this.client = client;
        this.sqliteManager = sqliteManager;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                captureAndProcess();
            } catch (Exception e) {
                System.err.println("Error en la ejecución programada: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    private void captureAndProcess() {
        String rawJson = client.getRawJson();

        if (rawJson != null) {
            JsonArray array = JsonParser.parseString(rawJson).getAsJsonArray();

            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.get(i).getAsJsonObject();

                String id = obj.get("id").getAsString();
                double price = obj.get("current_price").getAsDouble();
                long volume = obj.get("total_volume").getAsLong();
                long marketCap = obj.get("market_cap").getAsLong();
                int rank = obj.get("market_cap_rank").getAsInt();
                String last_update = obj.get("last_updated").getAsString();

                CoinGeckoCurrency data = new CoinGeckoCurrency(id, price, volume, marketCap, rank, last_update);

                sqliteManager.save(data);

                System.out.println("Dato guardado (" + (i+1) + "/10): " + id);
            }
        }
    }
}
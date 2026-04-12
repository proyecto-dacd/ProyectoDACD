package org.ulpgc.dacd;

import okhttp3.OkHttpClient;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        String apiKey = loadApiKey();

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: No se ha encontrado la API Key.");
            return;
        }

        OkHttpClient httpClient = new OkHttpClient();

        CoinGeckoReader client = new CoinGeckoReader(httpClient, apiKey);

        DatabaseManager sqliteManager = new DatabaseManager("cryptos.db");

        CurrencyManager service = new CurrencyManager(client, sqliteManager);

        service.start();
    }

    private static String loadApiKey() {
        Properties properties = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                return properties.getProperty("api.key");
            } else {
                System.err.println("No se encontró el archivo config.properties en resources.");
            }
        } catch (Exception e) {
            System.err.println("Excepción al cargar la API Key: " + e.getMessage());
        }
        return "";
    }
}
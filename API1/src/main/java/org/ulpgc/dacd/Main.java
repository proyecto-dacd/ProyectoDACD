package org.ulpgc.dacd;

import okhttp3.OkHttpClient;
import org.ulpgc.dacd.control.CoinGeckoReader;
import org.ulpgc.dacd.control.CurrencyController;
import org.ulpgc.dacd.control.CurrencyReader;
import org.ulpgc.dacd.model.CurrencyDatabase;
import org.ulpgc.dacd.view.ConsoleCurrencyView;
import org.ulpgc.dacd.view.CurrencyView;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Error: Faltan argumentos en la configuración de ejecución.");
            System.err.println("Se esperaba: <ruta_config> <ruta_database>");
            return;
        }

        String configPath = args[0];
        String dbPath = args[1];

        String apiKey = loadApiKeyFromPath(configPath);
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: No se pudo obtener la API Key de: " + configPath);
            return;
        }

        OkHttpClient httpClient = new OkHttpClient();

        CurrencyReader reader = new CoinGeckoReader(httpClient, apiKey);
        CurrencyDatabase database = new CurrencyDatabase(dbPath);
        CurrencyView view = new ConsoleCurrencyView();

        CurrencyController controller = new CurrencyController(reader, database, view);

        controller.start();
    }

    private static String loadApiKeyFromPath(String path) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(path)) {
            properties.load(input);
            return properties.getProperty("api.key");
        } catch (Exception e) {
            System.err.println("Excepción al cargar la API Key desde la ruta: " + e.getMessage());
            return null;
        }
    }
}
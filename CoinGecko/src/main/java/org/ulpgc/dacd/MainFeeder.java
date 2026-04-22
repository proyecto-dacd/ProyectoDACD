package org.ulpgc.dacd;

import okhttp3.OkHttpClient;
import org.ulpgc.dacd.control.*;
import org.ulpgc.dacd.view.ConsoleCurrencyView;
import org.ulpgc.dacd.view.CurrencyView;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class MainFeeder {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: Falta el argumento de la configuración.");
            System.err.println("Se esperaba: <ruta_config_api_key>");
            return;
        }

        String configPath = args[0];

        String apiKey = loadApiKeyFromPath(configPath);
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: No se pudo obtener la API Key de: " + configPath);
            return;
        }

        OkHttpClient httpClient = new OkHttpClient();

        CurrencyReader reader = new CoinGeckoFeederReader(httpClient, apiKey);

        CurrencyEventPublisher publisher = new ActiveMQFeederPublisher();

        CurrencyView view = new ConsoleCurrencyView();

        FeederController controller = new FeederController(reader, publisher, view);

        System.out.println("Iniciando Feeder de Criptomonedas");
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
package org.ulpgc.dacd;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.control.CoinGeckoFeederReader;
import org.ulpgc.dacd.model.Currency;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CoinGeckoFeederReaderTest {
    private String apiKey;
    private OkHttpClient httpClient;

    @BeforeEach
    void setUp() {
        Properties properties = new Properties();

        String absolutePath = "C:\\Users\\cmont\\IdeaProjects\\ProyectoDACD\\config.properties";

        try (InputStream input = new FileInputStream(absolutePath)) {
            properties.load(input);
            this.apiKey = properties.getProperty("api.key");
        } catch (Exception e) {
            System.err.println("Error fatal: No se encontró el archivo en " + absolutePath);
            this.apiKey = "";
        }

        this.httpClient = new OkHttpClient();
    }

    @Test
    void testReadCurrenciesReturnsData() {
        assertNotNull(apiKey, "La API Key es null");
        assertFalse(apiKey.isEmpty(), "La API Key está vacía");

        CoinGeckoFeederReader reader = new CoinGeckoFeederReader(httpClient, apiKey);
        List<Currency> result = reader.readCurrencies();

        assertNotNull(result, "La lista de monedas es nula");
        assertFalse(result.isEmpty(), "La lista está vacía. ¿Tienes internet o la API Key es correcta?");

        System.out.println("Test exitoso. Primera moneda detectada: " + result.get(0).getId());
        assertNotNull(result.get(0).getId());
    }
}
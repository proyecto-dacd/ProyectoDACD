package org.ulpgc.dacd;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CoinGeckoReaderTest {
    private String apiKey;
    private OkHttpClient httpClient;

    @BeforeEach
    void setUp() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                this.apiKey = properties.getProperty("api.key");
            }
        } catch (Exception e) {
            this.apiKey = "";
        }

        this.httpClient = new OkHttpClient();
    }

    @Test
    void testGetRawJsonReturnsData() {
        assertNotNull(apiKey, "La API Key no se pudo cargar del archivo properties");
        assertFalse(apiKey.isEmpty(), "La API Key está vacía en el archivo properties");

        CoinGeckoReader client = new CoinGeckoReader(httpClient, apiKey);
        String jsonResult = client.getRawJson();

        assertNotNull(jsonResult, "El JSON no debería ser nulo");
        assertTrue(jsonResult.contains("bitcoin"), "Debería contener datos de bitcoin");
    }
}
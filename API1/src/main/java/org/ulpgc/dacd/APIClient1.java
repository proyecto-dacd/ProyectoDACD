package org.ulpgc.dacd;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class APIClient1 {

    private static final String BASE_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=10&page=1&sparkline=false";

    private final OkHttpClient client;
    private final String apiKey;

    public APIClient1(String apiKey) {
        this.client = new OkHttpClient();
        this.apiKey = apiKey;
    }

    public String getRawJson(String cryptoId) {
        String url = BASE_URL + "&x_cg_demo_api_key=" + this.apiKey;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                System.err.println("Error en la petición HTTP: " + response.code());
            }
        } catch (IOException e) {
            System.err.println("Excepción al conectar con la API: " + e.getMessage());
        }

        return null;
    }
}
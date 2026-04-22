package org.ulpgc.dacd.feeder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.ulpgc.dacd.model.Currency;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CoinGeckoFeederReader implements CurrencyReader {

    private static final String BASE_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=eur&order=market_cap_desc&per_page=10&page=1&sparkline=false";

    private final OkHttpClient httpClient;
    private final String apiKey;

    public CoinGeckoFeederReader(OkHttpClient httpClient, String apiKey) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
    }

    @Override
    public List<Currency> readCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        String json = getRawJson();

        if (json != null) {
            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(json, JsonArray.class);

            for (JsonElement element : jsonArray) {
                JsonObject obj = element.getAsJsonObject();

                Currency currency = new Currency(
                        obj.get("id").getAsString(),
                        obj.get("current_price").getAsDouble(),
                        obj.get("total_volume").getAsLong(),
                        obj.get("market_cap").getAsLong(),
                        obj.get("market_cap_rank").getAsInt(),
                        Instant.parse(obj.get("last_updated").getAsString())
                );
                currencies.add(currency);
            }
        }
        return currencies;
    }

    private String getRawJson() {
        String url = BASE_URL + "&x_cg_demo_api_key=" + this.apiKey;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
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
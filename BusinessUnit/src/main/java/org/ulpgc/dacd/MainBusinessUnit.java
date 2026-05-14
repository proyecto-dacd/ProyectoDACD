package org.ulpgc.dacd;

import org.ulpgc.dacd.controller.api.DashboardApiController;
import org.ulpgc.dacd.controller.ingestion.HistoricalLoader;
import org.ulpgc.dacd.controller.sentiment.KeywordSentimentAnalyzer;
import org.ulpgc.dacd.controller.ingestion.RealTimeSubscriber;
import org.ulpgc.dacd.controller.sentiment.SentimentProvider;
import org.ulpgc.dacd.model.persistence.DatamartManager;
import org.ulpgc.dacd.model.entities.CryptoPrice;

import java.util.HashMap;
import java.util.Map;

public class MainBusinessUnit {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: Debes proporcionar la ruta del datalake como argumento.");
            return;
        }

        String dataLakePath = args[0];

        // 1. INICIALIZACIÓN DEL MODELO
        DatamartManager datamartManager = new DatamartManager();
        datamartManager.initializeDatamart();

        // 2. INICIALIZACIÓN DE PROVEEDORES
        SentimentProvider sentimentProvider = new KeywordSentimentAnalyzer();

        // 3. CARGA DE DATOS PASADOS
        HistoricalLoader loader = new HistoricalLoader(datamartManager);
        loader.loadHistoricalData(dataLakePath);

        // 4. SUSCRIPCIÓN A DATOS EN VIVO
        RealTimeSubscriber subscriber = new RealTimeSubscriber(datamartManager, sentimentProvider);

        // Cargamos los últimos precios para que el suscriptor calcule volatilidades desde el inicio
        Map<String, CryptoPrice> latestPricesFromDB = datamartManager.getLatestPrices();
        Map<String, Double> initialPrices = new HashMap<>();
        for (Map.Entry<String, CryptoPrice> entry : latestPricesFromDB.entrySet()) {
            initialPrices.put(entry.getKey(), entry.getValue().price());
        }

        subscriber.start();

        // 5. LANZAMIENTO DE LA INTERFAZ Y API (Controller)
        DashboardApiController apiController = new DashboardApiController(datamartManager, sentimentProvider);
        apiController.startServer();

        System.out.println("\n[SISTEMA] Business Unit operando correctamente.");
    }
}
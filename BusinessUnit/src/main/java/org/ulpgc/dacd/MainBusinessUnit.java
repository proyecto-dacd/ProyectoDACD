package org.ulpgc.dacd;

import org.ulpgc.dacd.controller.HistoricalLoader;
import org.ulpgc.dacd.controller.KeywordSentimentAnalyzer; // Importante
import org.ulpgc.dacd.controller.RealTimeSubscriber;
import org.ulpgc.dacd.controller.SentimentProvider;      // Importante
import org.ulpgc.dacd.model.CryptoPrice;
import org.ulpgc.dacd.model.DatamartManager;
import org.ulpgc.dacd.model.DatamartStore;
import org.ulpgc.dacd.view.DashboardView;

import java.util.HashMap;
import java.util.Map;

public class MainBusinessUnit {
    public static void main(String[] args) {
        // 1. Obtener la ruta del datalake por argumento
        String datalakePath;
        if (args.length > 0) {
            datalakePath = args[0];
            System.out.println("Ruta del datalake recibida por argumento: " + datalakePath);
        } else {
            datalakePath = "datalake";
            System.out.println("No se ha proporcionado argumento. Usando ruta por defecto: " + datalakePath);
        }

        // 2. Iniciar la Base de Datos
        DatamartStore datamartStore = new DatamartManager();
        datamartStore.initializeDatamart();

        // 3. Cargar los Datos Históricos (Batch Layer)
        HistoricalLoader historicalLoader = new HistoricalLoader(datamartStore);
        historicalLoader.loadHistoricalData(datalakePath);

        // 4. Extraer el último estado conocido desde SQLite
        Map<String, CryptoPrice> initialData = datamartStore.getLatestPrices();
        Map<String, Double> memoryPrices = new HashMap<>();

        // 5. Crear la Interfaz Gráfica (View)
        DashboardView dashboardView = new DashboardView();

        // 6. Rellenar la tabla y preparar la memoria antes de mostrar la ventana
        for (CryptoPrice cp : initialData.values()) {
            dashboardView.updatePrice(cp.id(), cp.price(), cp.timestamp());
            memoryPrices.put(cp.id(), cp.price());
        }

        dashboardView.setVisible(true);

        // --- LAS DOS LÍNEAS CLAVE PARA EL SENTIMIENTO ---

        // 7. Creamos el analizador de sentimiento (puedes cambiarlo aquí en el futuro)
        SentimentProvider sentimentProvider = new KeywordSentimentAnalyzer();

        // 8. Arrancar el Controlador, pasándole la BD, la vista Y el analizador
        RealTimeSubscriber subscriber = new RealTimeSubscriber(datamartStore, dashboardView, sentimentProvider);

        // Cargamos la memoria RAM inicial
        subscriber.setInitialPrices(memoryPrices);
        subscriber.start();
    }
}
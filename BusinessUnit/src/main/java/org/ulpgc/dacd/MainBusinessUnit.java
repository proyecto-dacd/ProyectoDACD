package org.ulpgc.dacd;

import org.ulpgc.dacd.controller.HistoricalLoader;
import org.ulpgc.dacd.controller.RealTimeSubscriber;
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
            datalakePath = args[0]; // Captura lo que el profe (o tú en el IDE) escriba, ej: "datalake"
            System.out.println("📂 Ruta del datalake recibida por argumento: " + datalakePath);
        } else {
            // Plan B de seguridad por si se ejecuta sin argumentos
            datalakePath = "datalake";
            System.out.println("⚠️ No se ha proporcionado argumento. Usando ruta por defecto: " + datalakePath);
        }

        // 2. Iniciar la Base de Datos
        DatamartStore datamartStore = new DatamartManager();
        datamartStore.initializeDatamart();

        // 3. Cargar los Datos Históricos (Batch Layer)
        HistoricalLoader historicalLoader = new HistoricalLoader(datamartStore);
        historicalLoader.loadHistoricalData(datalakePath);

        // --- LA MAGIA: CONECTANDO EL PASADO CON EL PRESENTE ---

        // 4. Extraer el último estado conocido desde SQLite
        Map<String, CryptoPrice> initialData = datamartStore.getLatestPrices();
        Map<String, Double> memoryPrices = new HashMap<>(); // Mapa temporal para el Subscriber

        // 5. Crear la Interfaz Gráfica (View)
        DashboardView dashboardView = new DashboardView();

        // 6. Rellenar la tabla y preparar la memoria ANTES de mostrar la ventana
        for (CryptoPrice cp : initialData.values()) {
            dashboardView.updatePrice(cp.id(), cp.price(), cp.timestamp());
            memoryPrices.put(cp.id(), cp.price());
        }

        // Ahora sí, enseñamos la ventana (¡ya aparecerá llena de datos!)
        dashboardView.setVisible(true);

        // 7. Arrancar el Controlador, pasándole la base de datos y la vista
        RealTimeSubscriber subscriber = new RealTimeSubscriber(datamartStore, dashboardView);
        // Le pasamos los precios antiguos a su memoria RAM para que pueda comparar
        subscriber.setInitialPrices(memoryPrices);
        subscriber.start();
    }
}
package org.ulpgc.dacd;

import org.ulpgc.dacd.controller.HistoricalLoader;
import org.ulpgc.dacd.controller.RealTimeSubscriber;
import org.ulpgc.dacd.model.DatamartManager;
import org.ulpgc.dacd.model.DatamartStore;
import org.ulpgc.dacd.view.DashboardView;

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

        // 4. Crear y mostrar la Interfaz Gráfica (View)
        DashboardView dashboardView = new DashboardView();
        dashboardView.setVisible(true);

        // 5. Arrancar el Controlador, pasándole la base de datos y la vista
        RealTimeSubscriber subscriber = new RealTimeSubscriber(datamartStore, dashboardView);
        subscriber.start();
    }
}
package org.ulpgc.dacd; // (O el paquete raíz de tu módulo Business Unit)

import org.ulpgc.dacd.controller.RealTimeSubscriber;
import org.ulpgc.dacd.model.DatamartManager;
import org.ulpgc.dacd.model.DatamartStore;
import org.ulpgc.dacd.view.DashboardView;

public class MainBusinessUnit {
    public static void main(String[] args) {
        // 1. Iniciar la Base de Datos
        DatamartStore datamartStore = new DatamartManager();
        datamartStore.initializeDatamart();

        // 2. Crear y mostrar la Interfaz Gráfica (View)
        DashboardView dashboardView = new DashboardView();
        dashboardView.setVisible(true);

        // 3. Arrancar el Controlador, pasándole la base de datos y la vista
        RealTimeSubscriber subscriber = new RealTimeSubscriber(datamartStore, dashboardView);
        subscriber.start();
    }
}
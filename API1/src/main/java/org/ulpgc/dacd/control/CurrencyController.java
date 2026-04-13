package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Currency;
import org.ulpgc.dacd.model.CurrencyDatabase;
import org.ulpgc.dacd.view.CurrencyView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CurrencyController {
    private final CurrencyReader reader;
    private final CurrencyDatabase database;
    private final CurrencyView view;

    public CurrencyController(CurrencyReader reader, CurrencyDatabase database, CurrencyView view) {
        this.reader = reader;
        this.database = database;
        this.view = view;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                executeManualRefresh();
            } catch (Exception e) {
                view.displayError("Error en la ejecución programada: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    public void executeManualRefresh() {
        try {
            List<Currency> currencies = reader.readCurrencies();

            if (currencies == null || currencies.isEmpty()) {
                view.displayError("No se pudieron obtener datos de la API.");
                return;
            }

            for (Currency currency : currencies) {
                database.save(currency);
            }

            // Esto actualizará la tabla en la GUI o los mensajes en la Consola
            view.display(currencies);

        } catch (Exception e) {
            view.displayError("Error al capturar y procesar: " + e.getMessage());
        }
    }
}
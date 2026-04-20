package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Currency;
import org.ulpgc.dacd.model.CurrencyEvent;
import org.ulpgc.dacd.view.CurrencyView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FeederController {
    private final CurrencyReader reader;
    private final CurrencyEventPublisher publisher;
    private final CurrencyView view;

    public FeederController(CurrencyReader reader, CurrencyEventPublisher publisher, CurrencyView view) {
        this.reader = reader;
        this.publisher = publisher;
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
                CurrencyEvent event = new CurrencyEvent(currency, "coin-gecko-feeder");

                publisher.publish(event);
            }

            view.display(currencies);

        } catch (Exception e) {
            view.displayError("Error al capturar y procesar: " + e.getMessage());
        }
    }
}
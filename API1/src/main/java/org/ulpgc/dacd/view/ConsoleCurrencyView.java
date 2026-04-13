package org.ulpgc.dacd.view;

import org.ulpgc.dacd.model.Currency;
import java.util.List;

public class ConsoleCurrencyView implements CurrencyView {

    @Override
    public void display(List<Currency> currencies) {
        System.out.println("\n--- 📊 ACTUALIZACIÓN DE CRIPTOMONEDAS ---");
        for (int i = 0; i < currencies.size(); i++) {
            Currency crypto = currencies.get(i);
            System.out.println("✅ Procesado (" + (i + 1) + "/" + currencies.size() + "): " + crypto.getId() + " | " + crypto.getPrice() + " €");
        }
        System.out.println("-----------------------------------------\n");
    }

    @Override
    public void displayError(String message) {
        System.err.println("❌ ERROR: " + message);
    }
}
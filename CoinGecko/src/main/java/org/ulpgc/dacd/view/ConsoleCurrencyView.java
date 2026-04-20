package org.ulpgc.dacd.view;

import org.ulpgc.dacd.model.Currency;
import java.util.List;

public class ConsoleCurrencyView implements CurrencyView {

    @Override
    public void display(List<Currency> currencies) {
        System.out.println("\n--- 📊 ACTUALIZACIÓN DE CRIPTOMONEDAS ---");

        System.out.printf("%-5s | %-12s | %-12s | %-15s | %-15s | %-20s%n",
                "RANK", "ID", "PRECIO (€)", "VOLUMEN", "MERCADO", "ÚLTIMA ACT.");
        System.out.println("-".repeat(90));

        for (Currency crypto : currencies) {
            System.out.printf("#%-4d | %-12s | %-12.2f | %-15d | %-15d | %-20s%n",
                    crypto.getMarketCapRank(),
                    crypto.getId().toUpperCase(),
                    crypto.getPrice(),
                    crypto.getVolume(),
                    crypto.getMarketCap(),
                    crypto.getTs()
            );
        }
        System.out.println("-".repeat(90) + "\n");
    }

    @Override
    public void displayError(String message) {
        System.err.println("❌ ERROR: " + message);
    }
}
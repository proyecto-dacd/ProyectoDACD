package org.ulpgc.dacd.view;

import org.ulpgc.dacd.model.Currency;
import java.util.List;

public interface CurrencyView {
    void display(List<Currency> currencies);
    void displayError(String message);
}
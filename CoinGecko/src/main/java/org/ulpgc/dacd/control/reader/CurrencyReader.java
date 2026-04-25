package org.ulpgc.dacd.control.reader;

import org.ulpgc.dacd.model.Currency;
import java.util.List;

public interface CurrencyReader {
    List<Currency> readCurrencies();
}

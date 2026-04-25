package org.ulpgc.dacd.control.persistence;

import org.ulpgc.dacd.model.CurrencyEvent;

public interface CurrencyEventPublisher {
    void publish(CurrencyEvent event);
}
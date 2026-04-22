package org.ulpgc.dacd.feeder;

import org.ulpgc.dacd.model.CurrencyEvent;

public interface CurrencyEventPublisher {
    void publish(CurrencyEvent event);
}
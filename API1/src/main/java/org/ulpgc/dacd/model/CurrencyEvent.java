package org.ulpgc.dacd.model;

import java.time.Instant;

public class CurrencyEvent {
    private final String ts;
    private final String ss;
    private final Currency data;

    public CurrencyEvent(Currency data, String source) {
        this.ts = Instant.now().toString();
        this.ss = source;
        this.data = data;
    }

    // Getters necesarios para que Gson pueda leerlos
    public String getTs() { return ts; }
    public String getSs() { return ss; }
    public Currency getData() { return data; }
}
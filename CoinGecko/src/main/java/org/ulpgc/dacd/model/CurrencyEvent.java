package org.ulpgc.dacd.model;

import java.time.Instant;

public class CurrencyEvent {
    private final String ts;
    private final String ss;
    private final Object data;

    public CurrencyEvent(Object data, String source) {
        this.ts = Instant.now().toString();
        this.ss = source;
        this.data = data;
    }

    public String getTs() { return ts; }
    public String getSs() { return ss; }
    public Object getData() { return data; }
}
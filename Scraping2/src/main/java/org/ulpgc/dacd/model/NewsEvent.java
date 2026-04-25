package org.ulpgc.dacd.model;

public class NewsEvent {

    private final String ts;
    private final String ss;
    private final Object data;

    public NewsEvent(String ts, String ss, Object data) {
        this.ts = ts;
        this.ss = ss;
        this.data = data;
    }

    public String getTs() {
        return ts;
    }

    public String getSs() {
        return ss;
    }

    public Object getData() {
        return data;
    }
}
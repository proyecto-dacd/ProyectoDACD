package org.ulpgc.dacd.scraping.model;

import org.ulpgc.dacd.model.NewsArticle;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class CurrencyEvent {
    private final String ts;
    private final String ss;
    private final NewsArticle data;

    public CurrencyEvent(NewsArticle data, String source) {
        this.ts = OffsetDateTime.now(ZoneId.of("Europe/Madrid")).toString();
        this.ss = source;
        this.data = data;
    }

    public String getTs() {
        return ts;
    }

    public String getSs() {
        return ss;
    }

    public NewsArticle getData() {
        return data;
    }
}
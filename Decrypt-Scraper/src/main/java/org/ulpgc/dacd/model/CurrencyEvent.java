package org.ulpgc.dacd.model;

import java.time.Instant;
import java.util.List;

public class CurrencyEvent {
    private final String ts;
    private final String ss;
    private final String url;
    private final String title;
    private final List<String> coins;

    public CurrencyEvent(NewsArticle article, String source) {
        this.ts = Instant.now().toString();
        this.ss = source;
        this.url = article.getUrl();
        this.title = article.getTitle();
        this.coins = article.getCoins();
    }

    public String getTs() {
        return ts;
    }

    public String getSs() {
        return ss;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getCoins() {
        return coins;
    }
}
package org.ulpgc.dacd.model;

public class CurrencyEvent {
    private final String ts;
    private final String ss;
    private final String id;
    private final double price;
    private final long volume;
    private final long marketCap;
    private final int rank;

    // Modificamos el constructor para recibir los campos sueltos
    public CurrencyEvent(String source, Currency currency) {
        this.ts = currency.getTs().toString();
        this.ss = source;
        this.id = currency.getId();
        this.price = currency.getPrice();
        this.volume = currency.getVolume();
        this.marketCap = currency.getMarketCap();
        this.rank = currency.getMarketCapRank();
    }

    public String getTs() { return ts; }
    public String getSs() { return ss; }
}
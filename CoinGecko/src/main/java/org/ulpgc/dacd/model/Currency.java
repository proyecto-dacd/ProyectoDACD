package org.ulpgc.dacd.model;

import java.time.Instant;

public class Currency {
    private final String id;
    private final double price;
    private final long volume;
    private final long marketCap;
    private final Instant ts;
    private final int marketCapRank;

    public Currency(String id, double price, long volume, long marketCap, int marketCapRank, Instant ts) {
        this.id = id;
        this.price = price;
        this.volume = volume;
        this.marketCap = marketCap;
        this.marketCapRank = marketCapRank;
        this.ts = ts;
    }

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public long getVolume() {
        return volume;
    }

    public long getMarketCap() {
        return marketCap;
    }

    public Instant getTs() {
        return ts;
    }

    public int getMarketCapRank() {
        return marketCapRank;
    }

    @Override
    public String toString() {
        return "APIData1{" +
                "id='" + id + '\'' +
                ", rank=" + marketCapRank +
                ", price=" + price +
                ", volume=" + volume +
                ", marketCap=" + marketCap +
                ", last_update'" + ts + '\'' +
                '}';
    }
}
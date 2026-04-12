package org.ulpgc.dacd.model;

public class CoinGeckoCurrency {
    private final String id;
    private final double price;
    private final long volume;
    private final long marketCap;
    private final String last_update;
    private final int marketCapRank;

    public CoinGeckoCurrency(String id, double price, long volume, long marketCap, int marketCapRank, String last_update) {
        this.id = id;
        this.price = price;
        this.volume = volume;
        this.marketCap = marketCap;
        this.marketCapRank = marketCapRank;
        this.last_update = last_update;
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

    public String getLast_update() {
        return last_update;
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
                ", last_update'" + last_update + '\'' +
                '}';
    }
}
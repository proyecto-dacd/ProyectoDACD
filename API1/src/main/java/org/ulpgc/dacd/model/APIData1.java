package org.ulpgc.dacd.model;

public class APIData1 {
    private final String id;
    private final double price;
    private final long volume;
    private final long marketCap;
    private final String timestamp;
    private final int marketCapRank;

    public APIData1(String id, double price, long volume, long marketCap, int marketCapRank, String timestamp) {
        this.id = id;
        this.price = price;
        this.volume = volume;
        this.marketCap = marketCap;
        this.marketCapRank = marketCapRank;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
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
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
package org.ulpgc.dacd.model.entities;
import java.util.List;

public record MarketIndicators(
        double pearson,
        String sentimentText,
        String cryptoId,
        double currentPrice,
        String lastTimestamp,
        List<Double> priceHistory,
        List<String> timestampHistory,
        double priceChange,
        boolean isVolatile
) {}
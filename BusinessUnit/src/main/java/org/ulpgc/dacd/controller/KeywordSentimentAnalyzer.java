package org.ulpgc.dacd.controller;

import java.util.Arrays;
import java.util.List;

public class KeywordSentimentAnalyzer implements SentimentProvider {

    private static final List<String> POSITIVE_WORDS = Arrays.asList(
            "launch", "partnership", "adoption", "growth", "success", "buy", "bullish", "green", "win", "surge", "support", "etf"
    );

    private static final List<String> NEGATIVE_WORDS = Arrays.asList(
            "hack", "lawsuit", "investigation", "dump", "sell", "bearish", "crash", "loss", "scam", "risk", "drop", "dip"
    );

    @Override
    public String getSentiment(List<String> newsTitles) {
        if (newsTitles == null || newsTitles.isEmpty()) return "NEUTRAL ⚪";

        int score = 0;
        for (String title : newsTitles) {
            String lowerTitle = title.toLowerCase();
            for (String word : POSITIVE_WORDS) if (lowerTitle.contains(word)) score++;
            for (String word : NEGATIVE_WORDS) if (lowerTitle.contains(word)) score--;
        }

        if (score > 0) return "POSITIVO ✅";
        if (score < 0) return "NEGATIVO ❌";
        return "NEUTRAL ⚪";
    }
}
package org.ulpgc.dacd.controller.sentiment;

import java.util.List;

public interface SentimentProvider {
    String getSentiment(List<String> newsTitles);
}
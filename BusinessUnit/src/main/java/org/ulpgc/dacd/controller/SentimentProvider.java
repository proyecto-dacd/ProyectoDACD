package org.ulpgc.dacd.controller;

import java.util.List;

public interface SentimentProvider {
    String getSentiment(List<String> newsTitles);
}
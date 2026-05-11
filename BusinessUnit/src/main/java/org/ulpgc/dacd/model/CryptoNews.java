package org.ulpgc.dacd.model;

public record CryptoNews(
        String cryptoId,
        String title,
        String url,
        String publishedAt
) {}
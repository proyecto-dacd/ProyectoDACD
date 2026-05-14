package org.ulpgc.dacd.model.entities;

public record CryptoNews(
        String cryptoId,
        String title,
        String url,
        String publishedAt
) {}
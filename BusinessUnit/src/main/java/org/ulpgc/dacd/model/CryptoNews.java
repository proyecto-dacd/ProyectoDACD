package org.ulpgc.dacd.model;

/**
 * Representa una noticia vinculada a una criptomoneda específica.
 */
public record CryptoNews(
        String cryptoId,
        String title,
        String url,
        String publishedAt
) {}
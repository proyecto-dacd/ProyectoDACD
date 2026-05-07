package org.ulpgc.dacd.model;

import java.util.List;

public interface DatamartStore {
    void initializeDatamart();
    void insertPrice(CryptoPrice cryptoPrice);

    // Método para el valor añadido: buscar noticias relacionadas
    List<String> getRelatedNews(String cryptoId);

    // void insertNews(CryptoNews cryptoNews);
}
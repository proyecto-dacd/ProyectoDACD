package org.ulpgc.dacd.model;

public interface DatamartStore {
    void initializeDatamart();
    void insertPrice(CryptoPrice cryptoPrice);

    // Hueco para tu compañero
    // void insertNews(CryptoNews cryptoNews);
}
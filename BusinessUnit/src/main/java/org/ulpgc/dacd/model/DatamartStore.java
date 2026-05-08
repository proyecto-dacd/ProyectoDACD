package org.ulpgc.dacd.model;

import java.util.List;

public interface DatamartStore {
    void initializeDatamart();
    void insertPrice(CryptoPrice cryptoPrice);


    List<String> getRelatedNews(String cryptoId);

    void insertNews(CryptoNews cryptoNews);
}
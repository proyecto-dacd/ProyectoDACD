package org.ulpgc.dacd.model.persistence;

import org.ulpgc.dacd.model.entities.CryptoNews;
import org.ulpgc.dacd.model.entities.CryptoPrice;

import java.util.List;
import java.util.Map;

public interface DatamartStore {
    void initializeDatamart();
    void insertPrice(CryptoPrice cryptoPrice);


    List<String> getRelatedNews(String cryptoId);

    void insertNews(CryptoNews cryptoNews);

    Map<String, CryptoPrice> getLatestPrices();
}
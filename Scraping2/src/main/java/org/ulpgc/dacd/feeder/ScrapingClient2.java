package org.ulpgc.dacd.feeder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ScrapingClient2 {

    public Document getPage(String url) throws Exception {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();
    }
}
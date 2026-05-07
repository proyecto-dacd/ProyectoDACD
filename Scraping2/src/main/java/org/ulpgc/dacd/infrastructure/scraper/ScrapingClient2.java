package org.ulpgc.dacd.infrastructure.scraper;

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
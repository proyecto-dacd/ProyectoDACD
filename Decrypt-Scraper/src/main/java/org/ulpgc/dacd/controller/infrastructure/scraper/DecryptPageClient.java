package org.ulpgc.dacd.controller.infrastructure.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DecryptPageClient {

    public Document getPage(String url) throws Exception {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();
    }
}
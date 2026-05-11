package org.ulpgc.dacd.infrastructure.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedHashSet;
import java.util.Set;

public class ArticleUrlExtractor {

    private static final String SECTION_URL = "https://decrypt.co/news";

    public Set<String> extractArticleUrls(Document doc) {
        Set<String> urls = new LinkedHashSet<>();
        Elements links = doc.select("a[href]");

        for (Element link : links) {
            String url = link.absUrl("href").trim();

            if (url.isEmpty()) continue;
            if (!url.startsWith("https://decrypt.co/")) continue;
            if (url.equals(SECTION_URL)) continue;

            if (looksLikeArticle(url)) {
                urls.add(url);
            }
        }

        return urls;
    }

    private boolean looksLikeArticle(String url) {
        return url.startsWith("https://decrypt.co/")
                && !url.equals("https://decrypt.co/")
                && !url.contains("#")
                && !url.contains("/news")
                && !url.contains("/news-explorer")
                && !url.contains("/tag/")
                && !url.contains("/author/")
                && !url.contains("/learn/")
                && !url.contains("/videos/")
                && !url.contains("/research/")
                && !url.contains("/sponsored/")
                && !url.contains("/press-releases/")
                && !url.contains("/collections/")
                && !url.contains("/university")
                && !url.contains("/emerge")
                && !url.contains("/price/")
                && !url.endsWith(".jpg")
                && !url.endsWith(".png")
                && !url.endsWith(".webp")
                && !url.endsWith(".svg");
    }
}
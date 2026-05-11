package org.ulpgc.dacd.infrastructure.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ulpgc.dacd.model.NewsArticle;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DecryptArticleScraper {

    private final ScrapingClient2 client;

    public DecryptArticleScraper(ScrapingClient2 client) {
        this.client = client;
    }

    public NewsArticle scrape(String url) throws Exception {
        Document doc = client.getPage(url);

        String title = firstNonEmptyText(doc,
                "h1",
                "[class*=title]"
        );

        String subtitle = firstNonEmptyText(doc,
                "h2",
                "[class*=subtitle]",
                "[class*=deck]"
        );

        String publishedAt = extractPublishedAt(doc);
        String body = extractBody(doc);
        List<String> coins = detectCoins(title + " " + subtitle + " " + body);

        NewsArticle article = new NewsArticle();
        article.setUrl(url);
        article.setTitle(title);
        article.setSubtitle(subtitle);
        article.setBody(body);
        article.setPublishedAt(publishedAt);
        article.setCapturedAt(Instant.now().toString());
        article.setCoins(coins);

        return article;
    }

    private String extractPublishedAt(Document doc) {
        Element time = doc.selectFirst("time");
        if (time != null) {
            String datetime = time.attr("datetime");
            if (!datetime.isBlank()) return datetime;
            return time.text().trim();
        }

        return firstNonEmptyText(doc,
                "[class*=date]",
                "[class*=time]"
        );
    }

    private String extractBody(Document doc) {
        String[] selectors = {
                "article p",
                "div[class*=article] p",
                "div[class*=content] p",
                "div[class*=post] p",
                "main p"
        };

        for (String selector : selectors) {
            Elements paragraphs = doc.select(selector);
            String text = cleanParagraphs(paragraphs);

            if (!text.isBlank()) {
                return text;
            }
        }

        return "";
    }

    private String cleanParagraphs(Elements paragraphs) {
        StringBuilder body = new StringBuilder();

        for (Element p : paragraphs) {
            String text = p.text().trim();

            if (text.isBlank()) continue;
            if (text.length() < 40) continue;
            if (text.matches("^\\$?[0-9.,]+$")) continue;

            body.append(text).append("\n");
        }

        return body.toString().trim();
    }

    private String firstNonEmptyText(Document doc, String... selectors) {
        for (String selector : selectors) {
            Element element = doc.selectFirst(selector);
            if (element != null) {
                String text = element.text().trim();
                if (!text.isBlank()) {
                    return text;
                }
            }
        }
        return "";
    }

    private List<String> detectCoins(String text) {
        String normalizedText = text.toLowerCase();
        List<String> coins = new ArrayList<>();

        addCoinIfPresent(coins, normalizedText, "bitcoin", "\\bbitcoin\\b", "\\bbtc\\b");
        addCoinIfPresent(coins, normalizedText, "ethereum", "\\bethereum\\b", "\\beth\\b", "\\bether\\b");
        addCoinIfPresent(coins, normalizedText, "solana", "\\bsolana\\b", "\\bsol\\b");
        addCoinIfPresent(coins, normalizedText, "ripple", "\\bxrp\\b", "\\bripple\\b");
        addCoinIfPresent(coins, normalizedText, "binancecoin", "\\bbnb\\b", "\\bbinance coin\\b");

        return coins;
    }

    private void addCoinIfPresent(List<String> coins, String text, String coinName, String... patterns) {
        for (String pattern : patterns) {
            if (java.util.regex.Pattern.compile(pattern).matcher(text).find()) {
                if (!coins.contains(coinName)) {
                    coins.add(coinName);
                }
                return;
            }
        }
    }
}
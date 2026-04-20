package org.ulpgc.dacd.feeder;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ulpgc.dacd.model.NewsArticle;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DecryptNewsFeeder implements NewsFeeder {

    private static final String SECTION_URL = "https://decrypt.co/news";
    private final ScrapingClient2 client = new ScrapingClient2();

    @Override
    public List<NewsArticle> feed() {
        List<NewsArticle> results = new ArrayList<>();

        try {
            Document doc = client.getPage(SECTION_URL);

            Set<String> articleUrls = extractArticleUrls(doc);
            System.out.println("URLs encontradas: " + articleUrls.size());

            int maxArticles = 10;
            int processed = 0;

            for (String url : articleUrls) {
                if (processed >= maxArticles) break;

                try {
                    NewsArticle article = scrapeArticle(url);

                    if (article != null
                            && article.getTitle() != null
                            && !article.getTitle().isBlank()
                            && article.getBody() != null
                            && !article.getBody().isBlank()) {

                        String title = article.getTitle().toLowerCase();
                        String articleUrl = article.getUrl().toLowerCase();

                        if (title.contains("getting started")
                                || title.contains("cutting edge")
                                || title.equals("artificial intelligence")
                                || articleUrl.equals("https://decrypt.co/")
                                || articleUrl.contains("/university")
                                || articleUrl.contains("/emerge")
                                || articleUrl.contains("/collections/")) {
                            continue;
                        }

                        boolean cryptoRelevantTitle =
                                title.contains("bitcoin") ||
                                        title.contains("btc") ||
                                        title.contains("ethereum") ||
                                        title.contains("eth") ||
                                        title.contains("xrp") ||
                                        title.contains("solana") ||
                                        title.contains("avalanche") ||
                                        title.contains("crypto") ||
                                        title.contains("etf") ||
                                        title.contains("defi") ||
                                        title.contains("blockchain") ||
                                        title.contains("binance") ||
                                        title.contains("staking") ||
                                        title.contains("token") ||
                                        title.contains("tokens") ||
                                        title.contains("stablecoin") ||
                                        title.contains("nft") ||
                                        title.contains("web3") ||
                                        title.contains("miner") ||
                                        title.contains("futures");

                        if (!cryptoRelevantTitle) {
                            continue;
                        }

                        results.add(article);
                        processed++;
                        System.out.println("Procesada: " + article.getTitle());
                    }
                } catch (Exception e) {
                    System.out.println("Error procesando artículo: " + url + " -> " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error cargando listado de noticias: " + e.getMessage());
        }

        return results;
    }

    private Set<String> extractArticleUrls(Document doc) {
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

    private NewsArticle scrapeArticle(String url) throws Exception {
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

        addCoinIfPresent(coins, normalizedText, "BTC", "\\bbitcoin\\b", "\\bbtc\\b");
        addCoinIfPresent(coins, normalizedText, "ETH", "\\bethereum\\b", "\\beth\\b", "\\bether\\b");
        addCoinIfPresent(coins, normalizedText, "SOL", "\\bsolana\\b", "\\bsol\\b");
        addCoinIfPresent(coins, normalizedText, "XRP", "\\bxrp\\b", "\\bripple\\b");
        addCoinIfPresent(coins, normalizedText, "BNB", "\\bbnb\\b", "\\bbinance coin\\b");

        return coins;
    }

    private void addCoinIfPresent(List<String> coins, String text, String code, String... patterns) {
        for (String pattern : patterns) {
            if (java.util.regex.Pattern.compile(pattern).matcher(text).find()) {
                if (!coins.contains(code)) {
                    coins.add(code);
                }
                return;
            }
        }
    }
}
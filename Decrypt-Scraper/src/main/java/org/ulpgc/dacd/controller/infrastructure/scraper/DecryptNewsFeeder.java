package org.ulpgc.dacd.controller.infrastructure.scraper;

import org.jsoup.nodes.Document;
import org.ulpgc.dacd.model.NewsArticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DecryptNewsFeeder implements NewsFeeder {

    private static final String SECTION_URL = "https://decrypt.co/news";

    private final ScrapingClient2 client;
    private final ArticleUrlExtractor urlExtractor;
    private final DecryptArticleScraper articleScraper;
    private final NewsArticleValidator validator;

    public DecryptNewsFeeder() {
        this.client = new ScrapingClient2();
        this.urlExtractor = new ArticleUrlExtractor();
        this.articleScraper = new DecryptArticleScraper(client);
        this.validator = new NewsArticleValidator();
    }

    @Override
    public List<NewsArticle> feed() {
        List<NewsArticle> results = new ArrayList<>();

        try {
            Document doc = client.getPage(SECTION_URL);

            Set<String> articleUrls = urlExtractor.extractArticleUrls(doc);
            System.out.println("URLs encontradas: " + articleUrls.size());

            int maxArticles = 10;
            int processed = 0;

            for (String url : articleUrls) {
                if (processed >= maxArticles) break;

                try {
                    NewsArticle article = articleScraper.scrape(url);

                    if (!validator.isValid(article)) {
                        continue;
                    }

                    results.add(article);
                    processed++;

                    System.out.println("Procesada: " + article.getTitle());

                } catch (Exception e) {
                    System.out.println("Error procesando artículo: " + url + " -> " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("Error cargando listado de noticias: " + e.getMessage());
        }

        return results;
    }
}
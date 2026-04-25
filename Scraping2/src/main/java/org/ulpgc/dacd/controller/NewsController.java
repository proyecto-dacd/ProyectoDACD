package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.feeder.NewsFeeder;
import org.ulpgc.dacd.model.CurrencyEvent;
import org.ulpgc.dacd.model.NewsArticle;
import org.ulpgc.dacd.publisher.ActiveMQNewsPublisher;
import org.ulpgc.dacd.serializer.NewsSerializer;

import java.util.List;

public class NewsController {

    private static final String SOURCE_SYSTEM = "CoinGecko-Scraping";

    private final NewsFeeder feeder;
    private final NewsSerializer serializer;
    private final ActiveMQNewsPublisher publisher;

    public NewsController(NewsFeeder feeder, NewsSerializer serializer) {
        this.feeder = feeder;
        this.serializer = serializer;
        this.publisher = new ActiveMQNewsPublisher();
    }

    public void execute() {
        List<NewsArticle> articles = feeder.feed();

        System.out.println("Noticias válidas encontradas: " + articles.size());

        for (NewsArticle article : articles) {
            serializer.serialize(article);

            CurrencyEvent event = new CurrencyEvent(article, SOURCE_SYSTEM);

            try {
                publisher.publish(event);
                System.out.println("Evento publicado en ActiveMQ: " + article.getTitle());
            } catch (Exception e) {
                System.out.println("Error publicando evento en ActiveMQ: " + e.getMessage());
            }

            System.out.println("---------------");
            System.out.println("Título: " + article.getTitle());
            System.out.println("URL: " + article.getUrl());
            System.out.println("Fecha: " + article.getPublishedAt());
            System.out.println("Coins: " + article.getCoins());
            System.out.println("Capturado: " + article.getCapturedAt());
        }

        System.out.println("=== Fin de ejecución ===");
    }
}
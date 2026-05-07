package org.ulpgc.dacd;

import org.ulpgc.dacd.controller.NewsController;
import org.ulpgc.dacd.infrastructure.scraper.DecryptNewsFeeder;
import org.ulpgc.dacd.infrastructure.scraper.NewsFeeder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainScraping2 {
    public static void main(String[] args) {

        NewsFeeder feeder = new DecryptNewsFeeder();
        NewsController controller = new NewsController(feeder);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable scrapingTask = () -> {
            System.out.println("\n=== Nueva ejecución de scraping ===");
            controller.execute();
        };

        scheduler.scheduleAtFixedRate(scrapingTask, 0, 1, TimeUnit.HOURS);
    }
}
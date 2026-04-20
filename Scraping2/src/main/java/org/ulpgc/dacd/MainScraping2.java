package org.ulpgc.dacd;

import controller.NewsController;
import feeder.DecryptNewsFeeder;
import feeder.NewsFeeder;
import org.ulpgc.dacd.database.DatabaseManager2;
import serializer.DatabaseNewsSerializer;
import serializer.NewsSerializer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainScraping2 {
    public static void main(String[] args) {
        DatabaseManager2 databaseManager = new DatabaseManager2();
        databaseManager.initializeDatabase();

        NewsFeeder feeder = new DecryptNewsFeeder();
        NewsSerializer serializer = new DatabaseNewsSerializer();
        NewsController controller = new NewsController(feeder, serializer);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable scrapingTask = () -> {
            System.out.println("\n=== Nueva ejecución de scraping ===");
            controller.execute();
        };

        scheduler.scheduleAtFixedRate(scrapingTask, 0, 1, TimeUnit.HOURS);
    }
}
package org.ulpgc.dacd.database;

import org.ulpgc.dacd.model.ScrapingData2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewsRepository2 {

    private final DatabaseManager2 databaseManager = new DatabaseManager2();

    public void save(ScrapingData2 article) {
        String sql = """
                INSERT OR IGNORE INTO news_articles
                (url, title, subtitle, body, published_at, captured_at, coins)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = databaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, article.getUrl());
            stmt.setString(2, article.getTitle());
            stmt.setString(3, article.getSubtitle());
            stmt.setString(4, article.getBody());
            stmt.setString(5, article.getPublishedAt());
            stmt.setString(6, article.getCapturedAt());
            stmt.setString(7, article.getCoins() == null ? "" : String.join(",", article.getCoins()));

            stmt.executeUpdate();
            System.out.println("Guardado en SQLite: " + article.getTitle());

        } catch (SQLException e) {
            System.out.println("Error guardando noticia: " + e.getMessage());
        }
    }
}

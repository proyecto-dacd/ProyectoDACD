package org.ulpgc.dacd.serializer;

import org.ulpgc.dacd.database.DatabaseManager2;
import org.ulpgc.dacd.model.NewsArticle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseNewsSerializer implements NewsSerializer {

    private final DatabaseManager2 databaseManager = new DatabaseManager2();

    @Override
    public void serialize(NewsArticle article) {
        String sql = """
                INSERT OR IGNORE INTO news_articles
                (url, title, subtitle, body, published_at, captured_at, coins)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, article.getUrl());
            statement.setString(2, article.getTitle());
            statement.setString(3, article.getSubtitle());
            statement.setString(4, article.getBody());
            statement.setString(5, article.getPublishedAt());
            statement.setString(6, article.getCapturedAt());
            statement.setString(7, article.getCoins() == null ? "" : String.join(",", article.getCoins()));

            int rows = statement.executeUpdate();

            if (rows > 0) {
                System.out.println("Guardado en SQLite: " + article.getTitle());
            } else {
                System.out.println("Ya existía en SQLite: " + article.getTitle());
            }

        } catch (SQLException exception) {
            System.out.println("Error guardando noticia: " + exception.getMessage());
        }
    }
}
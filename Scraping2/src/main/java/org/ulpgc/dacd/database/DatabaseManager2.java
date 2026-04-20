package org.ulpgc.dacd.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager2 {

    private static final String DB_URL = "jdbc:sqlite:scraping2.db";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void initializeDatabase() {
        String sql = """
                CREATE TABLE IF NOT EXISTS news_articles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    url TEXT NOT NULL UNIQUE,
                    title TEXT,
                    subtitle TEXT,
                    body TEXT,
                    published_at TEXT,
                    captured_at TEXT NOT NULL,
                    coins TEXT
                );
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Base de datos inicializada correctamente.");

        } catch (SQLException e) {
            System.out.println("Error inicializando la base de datos: " + e.getMessage());
        }
    }
}
package org.ulpgc.dacd.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatamartManager implements DatamartStore {

    private static final String DB_URL = "jdbc:sqlite:datamart.db";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    @Override
    public void initializeDatamart() {
        String sqlPrices = """
                CREATE TABLE IF NOT EXISTS crypto_prices (
                    crypto_id TEXT,
                    price REAL,
                    timestamp TEXT,
                    PRIMARY KEY (crypto_id, timestamp)
                );
                """;

        String sqlNews = "CREATE TABLE IF NOT EXISTS crypto_news (" +
                "crypto_id TEXT, " +
                "title TEXT, " +
                "url TEXT, " +
                "published_at TEXT, " +
                "PRIMARY KEY (crypto_id, url)" + // <--- ESTO ES LO IMPORTANTE
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlPrices);
            stmt.execute(sqlNews);
            System.out.println("[Datamart] Tablas de precios y noticias inicializadas en SQLite.");
        } catch (SQLException e) {
            System.err.println("[Datamart] Error inicializando SQLite: " + e.getMessage());
        }
    }

    @Override
    public void insertPrice(CryptoPrice cryptoPrice) {
        String sql = "INSERT OR IGNORE INTO crypto_prices (crypto_id, price, timestamp) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cryptoPrice.id());
            pstmt.setDouble(2, cryptoPrice.price());
            pstmt.setString(3, cryptoPrice.timestamp());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[Datamart] ✅ Precio guardado: " + cryptoPrice.id() + " -> " + cryptoPrice.price() + "€ [" + cryptoPrice.timestamp() + "]");
            }
        } catch (SQLException e) {
            System.err.println("[Datamart] ❌ Error guardando precio de " + cryptoPrice.id() + ": " + e.getMessage());
        }
    }

    @Override
    public void insertNews(CryptoNews cryptoNews) {
        String sql = "INSERT OR IGNORE INTO crypto_news (crypto_id, title, url, published_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cryptoNews.cryptoId());
            pstmt.setString(2, cryptoNews.title());
            pstmt.setString(3, cryptoNews.url());
            pstmt.setString(4, cryptoNews.publishedAt());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[Datamart] ✅ Noticia guardada: " + cryptoNews.title() + " [" + cryptoNews.cryptoId() + "] - " + cryptoNews.publishedAt());
            }
        } catch (SQLException e) {
            System.err.println("[Datamart] ❌ Error al insertar noticia en SQLite: " + e.getMessage());
        }
    }

    @Override
    public List<String> getRelatedNews(String cryptoId) {
        List<String> newsList = new ArrayList<>();
        // Busca las últimas 3 noticias de esa criptomoneda
        String sql = "SELECT title, url FROM crypto_news WHERE crypto_id = ? ORDER BY published_at DESC LIMIT 3";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cryptoId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                String url = rs.getString("url");
                newsList.add("📰 " + title + " -> " + url);
            }
        } catch (SQLException e) {
            System.err.println("[Datamart] ❌ Error buscando noticias para " + cryptoId + ": " + e.getMessage());
        }

        return newsList;
    }
}
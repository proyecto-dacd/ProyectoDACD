package org.ulpgc.dacd.model.persistence;

import org.ulpgc.dacd.model.entities.CryptoNews;
import org.ulpgc.dacd.model.entities.CryptoPrice;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    sentiment REAL,
                    timestamp TEXT,
                    PRIMARY KEY (crypto_id, timestamp)
                );
                """;

        String sqlNews = """
                CREATE TABLE IF NOT EXISTS crypto_news (
                    crypto_id TEXT,
                    title TEXT,
                    url TEXT,
                    published_at TEXT,
                    PRIMARY KEY (crypto_id, url)
                );
                """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlPrices);
            stmt.execute(sqlNews);
            System.out.println("[Datamart] Almacén de datos listo.");
        } catch (SQLException e) {
            System.err.println("[Datamart] Error en inicialización: " + e.getMessage());
        }
    }

    @Override
    public void insertPrice(CryptoPrice cryptoPrice) {
        String sql = "INSERT OR IGNORE INTO crypto_prices (crypto_id, price, sentiment, timestamp) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cryptoPrice.id());
            pstmt.setDouble(2, cryptoPrice.price());
            pstmt.setDouble(3, cryptoPrice.sentiment());
            pstmt.setString(4, cryptoPrice.timestamp());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[Datamart] Error guardando precio: " + e.getMessage());
        }
    }

    @Override
    public void insertNews(CryptoNews news) {
        String sql = "INSERT OR IGNORE INTO crypto_news (crypto_id, title, url, published_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, news.cryptoId());
            pstmt.setString(2, news.title());
            pstmt.setString(3, news.url());
            pstmt.setString(4, news.publishedAt());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[Datamart] Error guardando noticia: " + e.getMessage());
        }
    }

    @Override
    public List<String> getRelatedNews(String cryptoId) {
        List<String> newsList = new ArrayList<>();
        String sql = "SELECT title, url FROM crypto_news WHERE crypto_id = ? ORDER BY published_at DESC LIMIT 3";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cryptoId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                newsList.add("📰 " + rs.getString("title") + " -> " + rs.getString("url"));
            }
        } catch (SQLException e) {
            System.err.println("[Datamart] Error buscando noticias: " + e.getMessage());
        }
        return newsList;
    }

    @Override
    public Map<String, CryptoPrice> getLatestPrices() {
        Map<String, CryptoPrice> latest = new HashMap<>();
        String sql = "SELECT crypto_id, price, sentiment, MAX(timestamp) as timestamp FROM crypto_prices GROUP BY crypto_id";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("crypto_id");
                latest.put(id, new CryptoPrice(id, rs.getDouble("price"), rs.getString("timestamp"), rs.getDouble("sentiment")));
            }
        } catch (SQLException e) {
            System.err.println("[Datamart] Error cargando últimos precios: " + e.getMessage());
        }
        return latest;
    }

    public List<Double> getPriceHistory(String cryptoId) {
        return fetchHistory(cryptoId, "price");
    }

    public List<Double> getSentimentHistory(String cryptoId) {
        return fetchHistory(cryptoId, "sentiment");
    }

    private List<Double> fetchHistory(String cryptoId, String column) {
        List<Double> history = new ArrayList<>();
        String sql = "SELECT " + column + " FROM crypto_prices WHERE crypto_id = ? ORDER BY timestamp DESC LIMIT 20";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cryptoId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                history.add(0, rs.getDouble(column));
            }
        } catch (SQLException e) {
            System.err.println("[Datamart] Error cargando historial de " + column + ": " + e.getMessage());
        }
        return history;
    }

    public List<String> getTimestampHistory(String cryptoId) {
        List<String> timestamps = new ArrayList<>();
        String sql = "SELECT timestamp FROM (SELECT timestamp FROM crypto_prices WHERE crypto_id = ? ORDER BY timestamp DESC LIMIT 20) ORDER BY timestamp ASC";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cryptoId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                timestamps.add(rs.getString("timestamp"));
            }
        } catch (SQLException e) {
            System.err.println("[Datamart] Error cargando historial de tiempos: " + e.getMessage());
        }
        return timestamps;
    }

    public List<String> getAvailableCryptos() {
        List<String> cryptos = new ArrayList<>();
        String sql = "SELECT DISTINCT crypto_id FROM crypto_prices ORDER BY crypto_id ASC";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cryptos.add(rs.getString("crypto_id"));
            }
        } catch (SQLException e) {
            System.err.println("[Datamart] Error buscando monedas: " + e.getMessage());
        }
        return cryptos;
    }
}
package org.ulpgc.dacd.model;

import java.sql.*;

public class DatamartManager implements DatamartStore {

    // La base de datos se creará en la carpeta raíz de tu proyecto
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

        String sqlNews = """
                CREATE TABLE IF NOT EXISTS crypto_news (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    crypto_id TEXT,
                    title TEXT,
                    url TEXT,
                    published_at TEXT UNIQUE
                );
                """;

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
        // Usamos INSERT OR IGNORE para que si llega el mismo precio en la misma fecha exacta, no dé error
        String sql = "INSERT OR IGNORE INTO crypto_prices (crypto_id, price, timestamp) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cryptoPrice.id());
            pstmt.setDouble(2, cryptoPrice.price());
            pstmt.setString(3, cryptoPrice.timestamp());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("[Datamart] ✅ Precio guardado: " + cryptoPrice.id() + " -> " + cryptoPrice.price() + "€");
            }
        } catch (SQLException e) {
            System.err.println("[Datamart] ❌ Error guardando precio de " + cryptoPrice.id() + ": " + e.getMessage());
        }
    }
}
package org.ulpgc.dacd.model;

import java.sql.*;

public class CurrencyDatabase {
    private final String url;

    public CurrencyDatabase(String dbPath) {
        this.url = "jdbc:sqlite:" + dbPath;
        initializeDatabase();
    }

    private void initializeDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS crypto_data (
                id TEXT,
                rank INTEGER,
                price REAL,
                volume INTEGER,
                market_cap INTEGER,
                last_update TEXT,
                PRIMARY KEY (id, last_update)
            );
        """;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Base de datos inicializada correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    public void save(Currency data) {
        String sql = "INSERT OR REPLACE INTO crypto_data (id, rank, price, volume, market_cap, last_update) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, data.getId());
            pstmt.setInt(2, data.getMarketCapRank());
            pstmt.setDouble(3, data.getPrice());
            pstmt.setLong(4, data.getVolume());
            pstmt.setLong(5, data.getMarketCap());
            pstmt.setString(6, data.getTs().toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar datos en SQLite: " + e.getMessage());
        }
    }
}
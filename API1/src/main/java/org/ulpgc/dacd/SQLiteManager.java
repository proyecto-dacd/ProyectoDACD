package org.ulpgc.dacd;

import org.ulpgc.dacd.model.APIData1;
import java.sql.*;

public class SQLiteManager {
    private final String url;

    public SQLiteManager(String dbPath) {
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
                timestamp TEXT,
                PRIMARY KEY (id, timestamp)
            );
        """;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Base de datos inicializada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    public void save(APIData1 data) {
        String sql = "INSERT INTO crypto_data (id, rank, price, volume, market_cap, timestamp) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, data.getId());
            pstmt.setInt(2, data.getMarketCapRank());
            pstmt.setDouble(3, data.getPrice());
            pstmt.setLong(4, data.getVolume());
            pstmt.setLong(5, data.getMarketCap());
            pstmt.setString(6, data.getTimestamp());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar datos en SQLite: " + e.getMessage());
        }
    }
}
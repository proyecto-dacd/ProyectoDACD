package org.ulpgc.dacd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ulpgc.dacd.model.Currency;
import org.ulpgc.dacd.model.CurrencyDatabase;

import java.io.File;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyDatabaseTest {
    private CurrencyDatabase manager;
    private final String TEST_DB = "test_cryptos.db";

    @BeforeEach
    void setUp() {
        manager = new CurrencyDatabase(TEST_DB);
    }

    @AfterEach
    void tearDown() {
        File file = new File(TEST_DB);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testSaveAndRetrieveData() {
        Currency testData = new Currency("bitcoin", 50000.0, 1000000L, 900000000L, 1, "2026-03-27 12:00:00");

        manager.save(testData);

        String query = "SELECT id, price, last_update FROM crypto_data WHERE id = 'bitcoin'";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + TEST_DB);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            assertTrue(rs.next(), "El registro debería existir en la tabla 'bitcoin'");

            assertEquals("bitcoin", rs.getString("id"));
            assertEquals(50000.0, rs.getDouble("price"));
            assertEquals("2026-03-27 12:00:00", rs.getString("last_update"));

        } catch (SQLException e) {
            fail("Error al verificar los datos en la DB: " + e.getMessage());
        }
    }
}
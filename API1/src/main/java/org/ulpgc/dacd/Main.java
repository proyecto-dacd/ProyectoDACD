package org.ulpgc.dacd;

public class Main {
    public static void main(String[] args) {
        String databasePath = "cryptos.db";

        SQLiteManager sqliteManager = new SQLiteManager(databasePath);

        APIService1 service = new APIService1(sqliteManager);

        service.start();
    }
}
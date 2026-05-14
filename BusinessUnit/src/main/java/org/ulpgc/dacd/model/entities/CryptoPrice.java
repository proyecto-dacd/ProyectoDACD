package org.ulpgc.dacd.model.entities;

public record CryptoPrice(String id, double price, String timestamp, double sentiment) {}
package org.ulpgc.dacd.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.model.CryptoPrice;
import org.ulpgc.dacd.model.DatamartStore;
import org.ulpgc.dacd.view.DashboardView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.ulpgc.dacd.model.CryptoNews;

import javax.jms.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealTimeSubscriber {
    private final String brokerUrl = "tcp://localhost:61616";
    private final String topicName = "prediction.crypto";

    private final DatamartStore datamartStore;
    private final DashboardView view;
    private final SentimentProvider sentimentProvider; // Interfaz para el análisis de valor añadido

    private static final double VOLATILITY_THRESHOLD = 0.0; // 1%
    private final Map<String, Double> lastPrices = new HashMap<>();

    public RealTimeSubscriber(DatamartStore datamartStore, DashboardView view, SentimentProvider sentimentProvider) {
        this.datamartStore = datamartStore;
        this.view = view;
        this.sentimentProvider = sentimentProvider;
    }

    public void setInitialPrices(Map<String, Double> initialPrices) {
        this.lastPrices.putAll(initialPrices);
        System.out.println("[Subscriber] Memoria inicial cargada con " + initialPrices.size() + " precios históricos.");
    }

    public void start() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.setClientID("business-unit-realtime");
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic destination = session.createTopic(topicName);

            TopicSubscriber consumer = session.createDurableSubscriber(destination, "Suscripcion-BusinessUnit");
            System.out.println("[Subscriber] Escuchando eventos en tiempo real en: " + topicName);

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage) {
                        String json = ((TextMessage) message).getText();
                        processEvent(json);
                    }
                } catch (JMSException e) {
                    System.err.println("Error leyendo mensaje JMS: " + e.getMessage());
                }
            });
        } catch (JMSException e) {
            System.err.println("Error conectando a ActiveMQ: " + e.getMessage());
        }
    }

    private void processEvent(String json) {
        try {
            if (json.contains("{")) {
                json = json.substring(json.indexOf("{"));
            } else {
                return;
            }

            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            String source = jsonObject.get("ss").getAsString();

            if (source.equals("coin-gecko-feeder")) {
                String id = jsonObject.get("id").getAsString();
                double currentPrice = jsonObject.get("price").getAsDouble();
                String timestamp = jsonObject.get("ts").getAsString();

                datamartStore.insertPrice(new CryptoPrice(id, currentPrice, timestamp));
                view.updatePrice(id, currentPrice, timestamp);

                if (lastPrices.containsKey(id)) {
                    double previousPrice = lastPrices.get(id);
                    double change = ((currentPrice - previousPrice) / previousPrice) * 100;

                    if (Math.abs(change) >= VOLATILITY_THRESHOLD && Math.abs(change) > 0.0) {
                        List<String> noticias = datamartStore.getRelatedNews(id);
                        String sentimiento = sentimentProvider.getSentiment(noticias);

                        String alertTitle = "🚨 [" + id.toUpperCase() + "] " +
                                (change > 0 ? "📈 SUBIÓ " : "📉 BAJÓ ") +
                                String.format("%.2f", Math.abs(change)) + "%";

                        StringBuilder newsBody = new StringBuilder();
                        newsBody.append("   -> Sentimiento detectado: ").append(sentimiento).append("\n");

                        if (noticias.isEmpty()) {
                            newsBody.append("   -> Sin noticias relacionadas recientes.\n");
                        } else {
                            newsBody.append("   -> Posibles causas en prensa:\n");
                            for (String noticia : noticias) {
                                newsBody.append("      ").append(noticia).append("\n");
                            }
                        }
                        newsBody.append("--------------------------------------");

                        view.addAlert(alertTitle, newsBody.toString(), change > 0);
                    }
                }
                lastPrices.put(id, currentPrice);

            } else if (source.equals("Decrypt-Scraping")) {
                String title = jsonObject.get("title").getAsString();
                String url = jsonObject.get("url").getAsString();
                String ts = jsonObject.get("ts").getAsString();
                JsonArray coins = jsonObject.getAsJsonArray("coins");

                if (coins.size() == 0) {
                    datamartStore.insertNews(new CryptoNews("general", title, url, ts));
                } else {
                    for (JsonElement coin : coins) {
                        datamartStore.insertNews(new CryptoNews(coin.getAsString(), title, url, ts));
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error procesando el JSON plano: " + e.getMessage());
        }
    }
}
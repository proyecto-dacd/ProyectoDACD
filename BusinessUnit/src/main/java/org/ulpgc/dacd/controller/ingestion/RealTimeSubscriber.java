package org.ulpgc.dacd.controller.ingestion;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.controller.sentiment.SentimentProvider;
import org.ulpgc.dacd.controller.api.DashboardApiController;
import org.ulpgc.dacd.model.entities.CryptoPrice;
import org.ulpgc.dacd.model.persistence.DatamartStore;
import org.ulpgc.dacd.model.entities.CryptoNews;

import javax.jms.*;

public class RealTimeSubscriber {
    private final String brokerUrl = "tcp://localhost:61616";
    private final String topicName = "prediction.crypto";
    private final DatamartStore datamartStore;
    private final SentimentProvider sentimentProvider;

    public static final double VOLATILITY_THRESHOLD = 0.3;

    public RealTimeSubscriber(DatamartStore datamartStore, SentimentProvider sentimentProvider) {
        this.datamartStore = datamartStore;
        this.sentimentProvider = sentimentProvider;
    }

    public void start() {
        try {
            Connection connection = new ActiveMQConnectionFactory(brokerUrl).createConnection();
            connection.setClientID("business-unit-realtime");
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicSubscriber consumer = session.createDurableSubscriber(session.createTopic(topicName), "Suscripcion-BusinessUnit");

            System.out.println("[Subscriber] Escuchando en ActiveMQ (Modo Limpio)...");
            consumer.setMessageListener(this::onMessage);
        } catch (JMSException e) {
            System.err.println("[Subscriber] Error de conexión: " + e.getMessage());
        }
    }

    private void onMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                processEvent(textMessage.getText());
            }
        } catch (JMSException ignored) {}
    }

    private void processEvent(String json) {
        try {
            JsonObject data = JsonParser.parseString(cleanJson(json)).getAsJsonObject();
            String source = data.get("ss").getAsString();

            if ("coin-gecko-feeder".equals(source)) handlePriceUpdate(data);
            else if ("Decrypt-Scraping".equals(source)) handleNewsUpdate(data);

            DashboardApiController.broadcastUpdate();
        } catch (Exception ignored) {}
    }

    private void handlePriceUpdate(JsonObject data) {
        String id = data.get("id").getAsString();
        double price = data.get("price").getAsDouble();
        double score = getCurrentSentimentScore(id);

        datamartStore.insertPrice(new CryptoPrice(id, price, data.get("ts").getAsString(), score));
    }

    private double getCurrentSentimentScore(String id) {
        String sentiment = sentimentProvider.getSentiment(datamartStore.getRelatedNews(id));
        return traducirSentimientoANumero(sentiment);
    }

    private void handleNewsUpdate(JsonObject data) {
        String title = data.get("title").getAsString();
        String url = data.get("url").getAsString();
        String ts = data.get("ts").getAsString();
        JsonArray coins = data.getAsJsonArray("coins");

        if (coins.isEmpty()) {
            datamartStore.insertNews(new CryptoNews("general", title, url, ts));
        } else {
            coins.forEach(c -> datamartStore.insertNews(new CryptoNews(c.getAsString(), title, url, ts)));
        }
        System.out.println("[News] Procesada: " + (title.length() > 45 ? title.substring(0, 45) + "..." : title));
    }

    private String cleanJson(String json) {
        return json.contains("{") ? json.substring(json.indexOf("{")) : json;
    }

    private double traducirSentimientoANumero(String text) {
        String t = text.toLowerCase();
        if (t.contains("positivo") || t.contains("bullish") || t.contains("alcista")) return 1.0;
        if (t.contains("negativo") || t.contains("bearish") || t.contains("bajista")) return -1.0;
        return 0.0;
    }
}
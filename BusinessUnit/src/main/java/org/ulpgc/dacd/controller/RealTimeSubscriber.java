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
    private final DashboardView view; // Variable para controlar la interfaz gráfica

    // VARIABLES PARA EL VALOR AÑADIDO
    private static final double VOLATILITY_THRESHOLD = 0.0; // Umbral del 1% (en 0.0 para pruebas)
    private final Map<String, Double> lastPrices = new HashMap<>();

    public RealTimeSubscriber(DatamartStore datamartStore, DashboardView view) {
        this.datamartStore = datamartStore;
        this.view = view;
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

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        if (message instanceof TextMessage) {
                            String json = ((TextMessage) message).getText();
                            processEvent(json);
                        }
                    } catch (JMSException e) {
                        System.err.println("Error leyendo mensaje JMS: " + e.getMessage());
                    }
                }
            });
        } catch (JMSException e) {
            System.err.println("Error conectando a ActiveMQ: " + e.getMessage());
        }
    }

    private void processEvent(String json) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            String source = jsonObject.get("ss").getAsString();

            if (source.equals("coin-gecko-feeder")) {
                // Extraemos los datos del JSON plano
                String id = jsonObject.get("id").getAsString();
                double currentPrice = jsonObject.get("price").getAsDouble();
                String timestamp = jsonObject.get("ts").getAsString();

                // Guardamos el precio en la base de datos SQLite
                CryptoPrice cryptoPrice = new CryptoPrice(id, currentPrice, timestamp);
                datamartStore.insertPrice(cryptoPrice);

                // Actualizamos la tabla de la interfaz gráfica
                view.updatePrice(id, currentPrice, timestamp);

                // Lógica de Valor Añadido (Detección de Volatilidad)
                if (lastPrices.containsKey(id)) {
                    double previousPrice = lastPrices.get(id);
                    double change = ((currentPrice - previousPrice) / previousPrice) * 100;

                    if (Math.abs(change) >= VOLATILITY_THRESHOLD) {

                        // 1. Construimos el TÍTULO
                        String alertTitle = "🚨 [" + id.toUpperCase() + "] " +
                                (change > 0 ? "📈 SUBIÓ " : "📉 BAJÓ ") +
                                String.format("%.2f", Math.abs(change)) + "%";

                        // 2. Construimos el CUERPO de las noticias
                        StringBuilder newsBody = new StringBuilder();
                        List<String> noticias = datamartStore.getRelatedNews(id);

                        if (noticias.isEmpty()) {
                            newsBody.append("   -> Sin noticias relacionadas recientes.\n");
                        } else {
                            newsBody.append("   -> Posibles causas en prensa:\n");
                            for (String noticia : noticias) {
                                newsBody.append("      ").append(noticia).append("\n");
                            }
                        }
                        newsBody.append("--------------------------------------");

                        // 3. Enviamos AMBAS partes separadas a la vista
                        boolean isPositive = change > 0;
                        view.addAlert(alertTitle, newsBody.toString(), isPositive);
                    }
                }

                // Actualizamos la memoria con el nuevo precio
                lastPrices.put(id, currentPrice);

            } else if (source.equals("Decrypt-Scraping")) {
                String title = jsonObject.get("title").getAsString();
                String url = jsonObject.get("url").getAsString();
                String ts = jsonObject.get("ts").getAsString();
                JsonArray coins = jsonObject.getAsJsonArray("coins");

                for (JsonElement coin : coins) {
                    String cryptoId = coin.getAsString();

                    CryptoNews news = new CryptoNews(cryptoId, title, url, ts);
                    datamartStore.insertNews(news);
                }
            }

        } catch (Exception e) {
            System.err.println("Error procesando el JSON plano: " + e.getMessage());
        }
    }
}
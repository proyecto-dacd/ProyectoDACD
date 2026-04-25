package org.ulpgc.dacd.control.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.model.CurrencyEvent;
import org.ulpgc.dacd.control.persistence.CurrencyEventPublisher;

import javax.jms.*;
import java.time.Instant;

public class ActiveMQFeederPublisher implements CurrencyEventPublisher {
    private final String brokerUrl = "tcp://localhost:61616";
    private final String topicName = "prediction.crypto";
    private final Gson gson;

    public ActiveMQFeederPublisher() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, (com.google.gson.JsonSerializer<Instant>)
                        (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
                .create();
    }

    @Override
    public void publish(CurrencyEvent event) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topicName);
            MessageProducer producer = session.createProducer(destination);

            String json = gson.toJson(event);
            TextMessage message = session.createTextMessage(json);

            producer.send(message);

            System.out.println(" [Feeder] Evento enviado desde " + event.getSs() + " a las " + event.getTs());

            session.close();
            connection.close();
        } catch (JMSException e) {
            System.err.println("Error de conexión con ActiveMQ: " + e.getMessage());
        }
    }
}
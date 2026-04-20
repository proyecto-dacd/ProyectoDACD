package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class ActiveMQSubscriber {
    private final String brokerUrl = "tcp://localhost:61616";
    private final String topicName = "prediction.crypto";
    private final EventStore eventStore;

    public ActiveMQSubscriber(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public void start() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.setClientID("event-store-builder");
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic destination = session.createTopic(topicName);

            TopicSubscriber consumer = session.createDurableSubscriber(destination, "Suscripcion-DataLake");

            System.out.println("Escuchando (Duradero) en: " + topicName);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        if (message instanceof TextMessage) {
                            String json = ((TextMessage) message).getText();
                            eventStore.save(topicName, json);
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
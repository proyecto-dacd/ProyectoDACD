package org.ulpgc.dacd.publisher;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.model.NewsEvent;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class ActiveMQNewsPublisher {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "prediction.crypto";

    private final Gson gson = new Gson();

    public void publish(NewsEvent event) throws JMSException {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);

            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic(TOPIC_NAME);
            producer = session.createProducer(topic);

            String json = gson.toJson(event);
            TextMessage message = session.createTextMessage(json);

            producer.send(message);
        } finally {
            if (producer != null) producer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
    }
}
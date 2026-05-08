package org.example.subscriber;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.datamart.DatamartUpdater;
import javax.jms.*;

public class ActiveMQListener {
    private final String brokerUrl = "tcp://localhost:61616";
    // IMPORTANTE: Un ID diferente al del EventStore para que ActiveMQ no los confunda
    private final String clientId = "business-unit-realtime";
    private final DatamartUpdater datamart;

    public ActiveMQListener(DatamartUpdater datamart) {
        this.datamart = datamart;
    }

    public void start() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.setClientID(clientId);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Escuchar el canal Weather
            Topic weatherTopic = session.createTopic("Weather");
            TopicSubscriber weatherSub = session.createDurableSubscriber(weatherTopic, "bu-weather-sub");
            weatherSub.setMessageListener(msg -> procesar(msg, "Weather"));

            // Escuchar el canal Events
            Topic eventsTopic = session.createTopic("Events");
            TopicSubscriber eventsSub = session.createDurableSubscriber(eventsTopic, "bu-events-sub");
            eventsSub.setMessageListener(msg -> procesar(msg, "Events"));

            System.out.println("📡 Módulo Business Unit conectado. Escuchando en tiempo real...");
        } catch (Exception e) {
            System.err.println("❌ Error en ActiveMQ: " + e.getMessage());
        }
    }

    private void procesar(Message message, String topic) {
        try {
            if (message instanceof TextMessage textMessage) {
                // Le pasas el JSON directamente al Datamart de tu compañero
                datamart.processEvent(topic, textMessage.getText());
            }
        } catch (Exception e) {
            System.err.println("❌ Error leyendo mensaje: " + e.getMessage());
        }
    }
}

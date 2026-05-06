package org.example.store;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.model.Event;
import com.google.gson.Gson;

import javax.jms.*;
import java.util.List;

public class ActiveMQEventStore implements EventStore {
    private final String brokerUrl = "tcp://localhost:61616"; // La dirección de tu ActiveMQ
    private final String topicName = "Events"; // El nombre del Topic

    @Override
    public void store(List<Event> events) {
        try {
            // 1. Conectar al Broker
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // 2. Crear la sesión y apuntar al Topic
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topicName);
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            Gson gson = new Gson();

            // 3. Convertir cada evento a JSON y enviarlo
            for (Event event : events) {
                String jsonEvent = gson.toJson(event);
                TextMessage message = session.createTextMessage(jsonEvent);
                producer.send(message);
            }

            System.out.println("✅ Se han publicado " + events.size() + " eventos en el topic '" + topicName + "' de ActiveMQ.");

            // 4. Cerrar la conexión
            session.close();
            connection.close();

        } catch (Exception e) {
            System.err.println("❌ Error al enviar a ActiveMQ: " + e.getMessage());
        }
    }
}
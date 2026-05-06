package org.example.subscriber;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.store.EventStore;
import javax.jms.*;

public class EventBuilderSubscriber {

    private final String brokerUrl = "tcp://localhost:61616";
    // ID único para que ActiveMQ nos reconozca y la suscripción sea duradera
    private final String clientId = "event-store-builder";

    // Aquí guardamos la clase "Archivista" de tu compañero
    private final EventStore store;

    // Cuando creamos el Subscriber, le pasamos el Archivista
    public EventBuilderSubscriber(EventStore store) {
        this.store = store;
    }

    public void start() {
        try {
            // 1. Conectar al broker
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = factory.createConnection();
            connection.setClientID(clientId); // Obligatorio para suscripciones duraderas
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 2. Suscribirnos al canal del tiempo (Tu módulo)
            Topic weatherTopic = session.createTopic("Weather");
            TopicSubscriber weatherSub = session.createDurableSubscriber(weatherTopic, "weather-sub");
            // Cuando llegue un mensaje, llamamos a procesarMensaje
            weatherSub.setMessageListener(message -> procesarMensaje("Weather", message));

            // 3. Suscribirnos al canal de noticias (El módulo de tu compañero)
            Topic eventsTopic = session.createTopic("Events");
            TopicSubscriber eventsSub = session.createDurableSubscriber(eventsTopic, "events-sub");
            eventsSub.setMessageListener(message -> procesarMensaje("Events", message));

            System.out.println("📡 Event Store Builder iniciado. Escuchando 'Weather' y 'Events' de forma duradera...");

        } catch (Exception e) {
            System.err.println("❌ Error de conexión con ActiveMQ: " + e.getMessage());
        }
    }

    // Este método une tu código con el de tu compañero
    private void procesarMensaje(String topicName, Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                String jsonEvent = textMessage.getText();
                System.out.println("🎧 Nuevo mensaje recibido en '" + topicName + "'");

                // ¡MAGIA! Le pasamos el topic y el JSON al método save() de FileEventStore
                store.save(topicName, jsonEvent);
            }
        } catch (JMSException e) {
            System.err.println("❌ Error al leer el mensaje: " + e.getMessage());
        }
    }
}
package org.example.store;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.model.Weather;
import com.google.gson.Gson;

import javax.jms.*;

public class ActiveMQWeatherStore implements WeatherStore {

    // Dirección de nuestro broker ActiveMQ
    private final String brokerUrl = "tcp://localhost:61616";
    // El canal o "topic" donde publicaremos los eventos, como pide el profesor
    private final String topicName = "Weather";

    @Override
    public void store(Weather weather) {
        try {
            // Establecemos la conexión con el Broker
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Creamos una sesión y preparamos el Topic
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topicName);
            MessageProducer producer = session.createProducer(destination);

            // mensaje persistente
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            // Convertimos objeto de Java a texto JSON
            Gson gson = new Gson();
            String jsonEvent = gson.toJson(weather);

            // mensaje de texto con el JSON y lo publicamos por el canal
            TextMessage message = session.createTextMessage(jsonEvent);
            producer.send(message);

            System.out.println("✅ Evento meteorológico publicado en el topic '" + topicName + "': " + jsonEvent);

            // Cerramos la sesión y la conexión
            session.close();
            connection.close();

        } catch (Exception e) {
            System.err.println("❌ Error al enviar el mensaje a ActiveMQ: " + e.getMessage());
        }
    }
}
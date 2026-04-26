package org.example.store;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.model.Weather;
import com.google.gson.Gson;

import javax.jms.*;

public class ActiveMQWeatherStore implements WeatherStore {

    // Dirección de nuestro broker ActiveMQ (la "antena" local)
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

            // Creamos una sesión y preparamos el destino (el Topic)
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topicName);
            MessageProducer producer = session.createProducer(destination);

            // Hacemos que el mensaje sea persistente para que no se pierda (requisito del Sprint 2)
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            // Convertimos nuestro objeto de Java (Weather) a texto JSON usando la librería Gson
            Gson gson = new Gson();
            String jsonEvent = gson.toJson(weather);

            // Creamos el mensaje de texto con el JSON y lo publicamos por el canal
            TextMessage message = session.createTextMessage(jsonEvent);
            producer.send(message);

            System.out.println("✅ Evento meteorológico publicado en el topic '" + topicName + "': " + jsonEvent);

            // Cerramos la sesión y la conexión para dejar todo limpio
            session.close();
            connection.close();

        } catch (Exception e) {
            // Si hay algún problema de conexión, lo avisamos por consola
            System.err.println("❌ Error al enviar el mensaje a ActiveMQ: " + e.getMessage());
        }
    }
}
package at.technikum;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    private final static String QUEUE_NAME = "energy";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            // JSON zu Objekt
            ObjectMapper mapper = new ObjectMapper();
            try {
                EnergyMessage energyMsg = mapper.readValue(message, EnergyMessage.class);
                System.out.println("✅ Received structured message:");
                System.out.println(energyMsg);
            } catch (Exception e) {
                System.out.println("⚠️ Failed to parse message as JSON. Raw message:");
                System.out.println(message);
            }
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}

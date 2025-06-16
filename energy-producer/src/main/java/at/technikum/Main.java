package at.technikum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;



public class Main {
    private final static String QUEUE_NAME = "energy";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            ObjectMapper mapper = new ObjectMapper();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            while (true) {
                LocalDateTime now = LocalDateTime.now();
                double kwh = generateKwh(now.getHour());
                String timestamp = now.format(formatter);

                EnergyMessage msg = new EnergyMessage("PRODUCER", "COMMUNITY", kwh, timestamp);
                String json = mapper.writeValueAsString(msg);

                channel.basicPublish("", QUEUE_NAME, null, json.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent PRODUCER-Message: " + json);

                int delaySeconds = ThreadLocalRandom.current().nextInt(1, 6);
                Thread.sleep(delaySeconds * 1000L);
                System.out.println(delaySeconds + " second/s delay before next message");
            }
        }
    }

    private static double generateKwh(int hour) {
        // Peak morgens (6–9) & abends (17–21): 0.3 – 1.0 kWh
        if ((hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 21)) {
            return round(ThreadLocalRandom.current().nextDouble(0.3, 1.0));
        }
        // Tagsüber: 0.1 – 0.4 kWh
        else if (hour >= 10 && hour <= 16) {
            return round(ThreadLocalRandom.current().nextDouble(0.1, 0.4));
        }
        // Nachts: 0.01 – 0.1 kWh
        else {
            return round(ThreadLocalRandom.current().nextDouble(0.01, 0.1));
        }
    }

    private static double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}

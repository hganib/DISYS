package at.technikum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Producer application that sends randomized energy production messages to a RabbitMQ queue.
 */
public class Main {
    // Name of the RabbitMQ queue to publish messages to
    private final static String QUEUE_NAME = "energy";

    public static void main(String[] args) throws Exception {
        // Set up a connection factory for RabbitM
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // Try-with-resources ensures connection and channel are closed automatically
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declare a Rabbit MQ queue
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            // Jackson mapper for converting Java objects to JSON
            ObjectMapper mapper = new ObjectMapper();
            // ISO format for LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            // Infinite loop: generate and send a message at a random interval
            while (true) {
                // 1. Generate timestamp and kWh value
                LocalDateTime now = LocalDateTime.now();
                double kwh = generateKwh(now.getHour());
                String timestamp = now.format(formatter);

                // 2. Create and serialize the message
                EnergyMessage msg = new EnergyMessage("PRODUCER", "COMMUNITY", kwh, timestamp);
                String json = mapper.writeValueAsString(msg);

                // 3. Publish JSON to the queue
                channel.basicPublish("", QUEUE_NAME, null, json.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent PRODUCER-Message: " + json);

                // 4. Sleep for a random delay between 1 and 5 seconds
                int delaySeconds = ThreadLocalRandom.current().nextInt(1, 6);
                Thread.sleep(delaySeconds * 1000L);
                System.out.println(delaySeconds + " second/s delay before next message");
            }
        }
    }


    /**
     * Generates a randomized kWh value based on the given hour of day.
     *
     * @param hour the current hour (0-23)
     * @return a double value representing kWh rounded to three decimal places
     */
    private static double generateKwh(int hour) {
        // Peak usage: morning (6–9) & evening (17–21)
        if ((hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 21)) {
            return round(ThreadLocalRandom.current().nextDouble(0.3, 1.0));
        }
        // Daytime moderate usage: 10–16
        else if (hour >= 10 && hour <= 16) {
            return round(ThreadLocalRandom.current().nextDouble(0.1, 0.4));
        }
        // Nighttime low usage: all other hours
        else {
            return round(ThreadLocalRandom.current().nextDouble(0.01, 0.1));
        }
    }


    /**
     * Rounds a double to three decimal places.
     *
     * @param value the value to round
     * @return the rounded value
     */
    private static double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}

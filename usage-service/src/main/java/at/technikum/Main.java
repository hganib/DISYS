package at.technikum;

import at.technikum.entity.EnergyHistorical;
import at.technikum.util.JPAUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


/**
 * Usage Service: consumes raw energy messages ("USER" or "PRODUCER") from RabbitMQ,
 * aggregates them hourly into the energy_historical table, and forwards updates
 * as HistoricalUpdateMessage to a separate RabbitMQ queue.
 */
public class Main {

    // Queue holding raw energy data from producers and users
    private static final String ENERGY_QUEUE    = "energy";
    // Queue for publishing hourly aggregation updates
    private static final String HIST_UPD_QUEUE  = "energy_historical_updates";

    public static void main(String[] args) throws Exception {
        // Configure RabbitMQ connection
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // Try-with-resources to ensure connection and channel are closed automatically
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declare the input queue for raw energy messages
            channel.queueDeclare(ENERGY_QUEUE, false, false, false, null);
            // Declare the output queue for historical updates
            channel.queueDeclare(HIST_UPD_QUEUE, false, false, false, null);

            System.out.println(" [*] Waiting for ENERGY messages...");

            // Setup Jackson mapper to handle Java time types
            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule());

            // Define callback to process incoming messages
            DeliverCallback callback = (consumerTag, delivery) -> {
                // Deserialize JSON payload into EnergyMessage
                String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
                EnergyMessage msg = mapper.readValue(json, EnergyMessage.class);

                EnergyHistorical updatedRecord = null;
                // Branch logic based on message type
                switch (msg.getType().toUpperCase()) {
                    case "USER":
                        // Aggregate consumption
                        updatedRecord = updateConsumption(msg);
                        break;
                    case "PRODUCER":
                        // Aggregate production
                        updatedRecord = updateProduction(msg);
                        break;
                    default:
                        // Log unknown types without failing
                        System.out.println("⚠️ Unbekannter Typ: " + msg.getType());
                }

                // If successfully updated the historical record, forward it
                if (updatedRecord != null) {
                    sendHistoricalUpdate(updatedRecord, channel, mapper);
                }
            };

            // Start consuming messages from the ENERGY queue
            channel.basicConsume(ENERGY_QUEUE, true, callback, consumerTag -> {});
            // Keep the main thread alive to continue processing messages
            Thread.currentThread().join();
        }
    }

    /**
     * Updates or creates an hourly historical record for consumption and returns it.
     * @param msg incoming message with consumption data
     * @return the persisted EnergyHistorical entity, or null on failure
     */
    private static EnergyHistorical updateConsumption(EnergyMessage msg) {
        // Create an EntityManager to interact with the database
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        EnergyHistorical record;

        // Start a transaction to ensure atomicity
        try {
            // Begin transaction
            tx.begin();
            // Parse the timestamp and round it to the hour
            LocalDateTime ts  = LocalDateTime.parse(msg.getDatetime());
            LocalDateTime hr  = ts.withMinute(0).withSecond(0).withNano(0);

            // Try to find an existing record for this hour
            try {
                record = em.createQuery(
                                "SELECT e FROM EnergyHistorical e WHERE e.hour = :hour",
                                EnergyHistorical.class
                        )
                        .setParameter("hour", hr)
                        .getSingleResult();
            } catch (NoResultException nre) {
                // If no record exists, create a new one
                record = new EnergyHistorical();
                record.setHour(hr);
                record.setCommunityProduced(0.0);
                record.setCommunityUsed(0.0);
                record.setGridUsed(0.0);
                em.persist(record);
            }

            // Update the community used and grid used values
            double usedSum = record.getCommunityUsed() + msg.getKwh();
            double over    = Math.max(0, usedSum - record.getCommunityProduced());
            record.setCommunityUsed(Math.min(usedSum, record.getCommunityProduced()));
            record.setGridUsed(record.getGridUsed() + over);
            // Persist the updated record
            em.merge(record);

            // Commit the transaction
            tx.commit();
            System.out.println(String.format(
                    "🪫 Consumption updated: Stunde=%s | kWh=%.3f | community_used=%.3f | grid_used=%.3f",
                    hr, msg.getKwh(), record.getCommunityUsed(), record.getGridUsed()
            ));

        // Handle any exceptions that occur during the transaction
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }

        // Return the updated or newly created record
        return record;
    }

    /**
     * Updates or creates an hourly historical record for production and returns it.
     * @param msg incoming message with production data
     * @return the persisted EnergyHistorical entity
     */
    private static EnergyHistorical updateProduction(EnergyMessage msg) {
        // Create an EntityManager to interact with the database
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        EnergyHistorical record = null;

        // Start a transaction to ensure atomicity
        try {
            // Begin transaction
            tx.begin();
            // Parse the timestamp and round it to the hour
            LocalDateTime ts  = LocalDateTime.parse(msg.getDatetime());
            LocalDateTime hr  = ts.withMinute(0).withSecond(0).withNano(0);

            // Try to find an existing record for this hour
            try {
                record = em.createQuery(
                                "SELECT e FROM EnergyHistorical e WHERE e.hour = :hour",
                                EnergyHistorical.class
                        )
                        .setParameter("hour", hr)
                        .getSingleResult();
            } catch (NoResultException ex) {
                // If no record exists, create a new one
                record = new EnergyHistorical();
                record.setHour(hr);
                record.setCommunityProduced(0.0);
                record.setCommunityUsed(0.0);
                record.setGridUsed(0.0);
                em.persist(record);
            }

            // Update the community produced value
            record.setCommunityProduced(record.getCommunityProduced() + msg.getKwh());
            em.merge(record);

            // Commit the transaction
            tx.commit();
            System.out.println(String.format(
                    "⚡️ Production updated: Stunde=%s | kWh=%.3f | community_produced=%.3f",
                    hr,
                    msg.getKwh(),
                    record.getCommunityProduced()
            ));

        // Handle any exceptions that occur during the transaction
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        // Return the updated or newly created record
        return record;
    }

    /**
     * Sends the updated EnergyHistorical record as a JSON message to the historical updates queue.
     * @param rec the updated EnergyHistorical entity
     * @param channel RabbitMQ channel to publish on
     * @param mapper Jackson ObjectMapper for JSON serialization
     */
    private static void sendHistoricalUpdate(EnergyHistorical rec,
                                             Channel channel,
                                             ObjectMapper mapper) {
        try {
            // Create DTO for update message
            HistoricalUpdateMessage out = new HistoricalUpdateMessage(
                    rec.getHour(),
                    rec.getCommunityProduced(),
                    rec.getCommunityUsed(),
                    rec.getGridUsed()
            );
            // Serialize the DTO to JSON
            String outJson = mapper.writeValueAsString(out);

            // Publish the JSON message to the HIST_UPD_QUEUE
            channel.basicPublish(
                    "",                   // Default-Exchange
                    HIST_UPD_QUEUE,       // Routing Key = Queue-Name
                    null,
                    outJson.getBytes(StandardCharsets.UTF_8)
            );
            System.out.println(" ✉️[>] Sent HIST-UPDATE: " + outJson);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        } catch (Exception e) {
            // Handle serialization or publishing errors
            e.printStackTrace();
        }
    }
}
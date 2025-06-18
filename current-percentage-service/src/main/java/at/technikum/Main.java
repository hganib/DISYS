package at.technikum;

import at.technikum.entity.EnergyCurrent;
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
 * Current Percentage Service: consumes hourly aggregation updates,
 * calculates the split between community and grid usage,
 * and persists the results to the energy_current table.
 */
public class Main {

    // RabbitMQ queue name where historical updates are published
    private static final String HIST_UPD_QUEUE = "energy_historical_updates";

    public static void main(String[] args) throws Exception {
        // Create and configure a RabbitMQ connection factory
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // Auto-close connection and channel resources
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declare the queue to receive historical update messages
            channel.queueDeclare(HIST_UPD_QUEUE, false, false, false, null);
            System.out.println(" [*] Waiting for HIST-UPDATE messages...");

            // Configure Jackson to handle Java 8 date/time types
            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule());

            // Define callback logic for incoming messages
            DeliverCallback callback = (consumerTag, delivery) -> {
                // Deserialize the incoming JSON into HistoricalUpdateMessage
                String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
                HistoricalUpdateMessage upd = mapper.readValue(json, HistoricalUpdateMessage.class);

                // Process and persist the current percentages
                updateCurrentPercentage(upd);
            };

            // Start consuming messages; auto-acknowledge on receipt
            channel.basicConsume(HIST_UPD_QUEUE, true, callback, consumerTag -> {});

            // Keep the main thread alive to continue processing
            Thread.currentThread().join();
        }
    }

    /**
     * Calculates community vs. grid usage percentages and stores them.
     * @param upd historical update DTO containing hour, communityUsed, gridUsed
     */
    private static void updateCurrentPercentage(HistoricalUpdateMessage upd) {
        // Obtain an EntityManager for database operations
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        // Start a transaction to ensure atomicity
        try {
            // Begin transaction
            tx.begin();

            // Extract values from the update message
            LocalDateTime hour     = upd.getHour();
            double usedCommunity = upd.getCommunityUsed();
            double usedGrid      = upd.getGridUsed();
            double totalUsage    = usedCommunity + usedGrid;
            double communityProduced = upd.getCommunityProduced();

            // Calculate community usage percentage (guard divide by zero)
            double communityPool = totalUsage == 0.0
                    ? 0.0
                    : (usedCommunity / communityProduced) * 100.0;
            communityPool = Math.round(communityPool * 1000.0) / 1000.0; // round to 3 decimal places

            // Calculate grid usage percentage as the remainder to 100%
            double gridPortion = totalUsage == 0.0
                    ? 0.0
                    : (usedGrid / totalUsage) * 100.0;
            gridPortion = Math.round(gridPortion * 1000.0) / 1000.0; // round to 3 decimal places

            // Load existing EnergyCurrent record by hour or create a new one
            EnergyCurrent current;
            try {
                current = em.createQuery(
                                "SELECT c FROM EnergyCurrent c WHERE c.hour = :hour",
                                EnergyCurrent.class)
                        .setParameter("hour", hour)
                        .getSingleResult();
            } catch (NoResultException nre) {
                // If no record exists, create a new one
                current = new EnergyCurrent();
                current.setHour(hour);
                current.setCommunityPool(0.0);
                current.setGridPortion(0.0);
                em.persist(current);
            }

            // Update the entity fields and merge changes
            current.setCommunityPool(communityPool);
            current.setGridPortion(gridPortion);
            em.merge(current);

            // Commit the transaction to persist changes
            tx.commit();

            System.out.println(String.format(
                    "📊 Current percentage updated: Stunde=%s | community_pool=%.3f%% | grid_portion=%.3f%%",
                    hour, communityPool, gridPortion
            ));
        } catch (Exception e) {
            // Rollback transaction in case of any error
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            // Ensure the EntityManager is closed to release resources
            em.close();
        }
    }
}
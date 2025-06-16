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

    private static final String HIST_UPD_QUEUE = "energy_historical_updates";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Queue deklarieren
            channel.queueDeclare(HIST_UPD_QUEUE, false, false, false, null);
            System.out.println(" [*] Waiting for HIST-UPDATE messages...");

            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule());

            DeliverCallback callback = (consumerTag, delivery) -> {
                String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
                HistoricalUpdateMessage upd = mapper.readValue(json, HistoricalUpdateMessage.class);

                updateCurrentPercentage(upd);
            };

            channel.basicConsume(HIST_UPD_QUEUE, true, callback, consumerTag -> {});
            Thread.currentThread().join();
        }
    }

    private static void updateCurrentPercentage(HistoricalUpdateMessage upd) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            LocalDateTime hour     = upd.getHour();
            double usedCommunity = upd.getCommunityUsed();
            double usedGrid      = upd.getGridUsed();
            double totalUsage    = usedCommunity + usedGrid;

            double communityPool = totalUsage == 0.0
                    ? 0.0
                    : (usedCommunity / totalUsage) * 100.0;
            communityPool = Math.round(communityPool * 1000.0) / 1000.0;

            double gridPortion = totalUsage == 0.0
                    ? 0.0
                    : (usedGrid / totalUsage) * 100.0;
            gridPortion = Math.round(gridPortion * 1000.0) / 1000.0;

            // bestehenden Datensatz für diese Stunde laden oder neu anlegen
            EnergyCurrent current;
            try {
                current = em.createQuery(
                                "SELECT c FROM EnergyCurrent c WHERE c.hour = :hour",
                                EnergyCurrent.class)
                        .setParameter("hour", hour)
                        .getSingleResult();
            } catch (NoResultException nre) {
                current = new EnergyCurrent();
                current.setHour(hour);
                current.setCommunityPool(0.0);
                current.setGridPortion(0.0);
                em.persist(current);
            }

            // Felder setzen und speichern
            current.setCommunityPool(communityPool);
            current.setGridPortion(gridPortion);
            em.merge(current);

            tx.commit();

            System.out.println(String.format(
                    "📊 Current percentage updated: Stunde=%s | community_pool=%.3f%% | grid_portion=%.3f%%",
                    hour, communityPool, gridPortion
            ));
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
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

public class Main {

    private static final String ENERGY_QUEUE    = "energy";
    private static final String HIST_UPD_QUEUE  = "energy_historical_updates";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Queues deklarieren
            channel.queueDeclare(ENERGY_QUEUE, false, false, false, null);
            channel.queueDeclare(HIST_UPD_QUEUE, false, false, false, null);

            System.out.println(" [*] Waiting for ENERGY messages...");

            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule());

            DeliverCallback callback = (consumerTag, delivery) -> {
                String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
                EnergyMessage msg = mapper.readValue(json, EnergyMessage.class);

                EnergyHistorical updatedRecord = null;
                switch (msg.getType().toUpperCase()) {
                    case "USER":
                        updatedRecord = updateConsumption(msg);
                        break;
                    case "PRODUCER":
                        updatedRecord = updateProduction(msg);
                        break;
                    default:
                        System.out.println("⚠️ Unbekannter Typ: " + msg.getType());
                }

                // Nachricht mit dem aktualisierten Stunden-Datensatz schicken
                if (updatedRecord != null) {
                    sendHistoricalUpdate(updatedRecord, channel, mapper);
                }
            };

            channel.basicConsume(ENERGY_QUEUE, true, callback, consumerTag -> {});
            Thread.currentThread().join();
        }
    }

    // Verbrauch aufsummieren und Entity zurückgeben
    private static EnergyHistorical updateConsumption(EnergyMessage msg) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        EnergyHistorical record = null;

        try {
            tx.begin();
            LocalDateTime ts  = LocalDateTime.parse(msg.getDatetime());
            LocalDateTime hr  = ts.withMinute(0).withSecond(0).withNano(0);

            record = em.createQuery(
                            "SELECT e FROM EnergyHistorical e WHERE e.hour = :hour",
                            EnergyHistorical.class
                    )
                    .setParameter("hour", hr)
                    .getSingleResult();

            double usedSum = record.getCommunityUsed() + msg.getKwh();
            double over    = Math.max(0, usedSum - record.getCommunityProduced());

            record.setCommunityUsed(Math.min(usedSum, record.getCommunityProduced()));
            record.setGridUsed(record.getGridUsed() + over);
            em.merge(record);

            tx.commit();
            System.out.println(String.format(
                    "🪫 Consumption updated: Stunde=%s | kWh=%.3f | community_used=%.3f | grid_used=%.3f",
                    hr,
                    msg.getKwh(),
                    record.getCommunityUsed(),
                    record.getGridUsed()
            ));

        } catch (NoResultException nre) {
            // falls noch kein historischer Eintrag existiert, könnte man ihn hier anlegen
            tx.rollback();
            System.err.println("❌ Kein Datensatz zum Updaten gefunden für " + msg);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return record;
    }

    // Produktion aufsummieren und Entity zurückgeben
    private static EnergyHistorical updateProduction(EnergyMessage msg) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        EnergyHistorical record = null;

        try {
            tx.begin();
            LocalDateTime ts  = LocalDateTime.parse(msg.getDatetime());
            LocalDateTime hr  = ts.withMinute(0).withSecond(0).withNano(0);

            try {
                record = em.createQuery(
                                "SELECT e FROM EnergyHistorical e WHERE e.hour = :hour",
                                EnergyHistorical.class
                        )
                        .setParameter("hour", hr)
                        .getSingleResult();
            } catch (NoResultException ex) {
                record = new EnergyHistorical();
                record.setHour(hr);
                record.setCommunityProduced(0.0);
                record.setCommunityUsed(0.0);
                record.setGridUsed(0.0);
                em.persist(record);
            }

            record.setCommunityProduced(record.getCommunityProduced() + msg.getKwh());
            em.merge(record);

            tx.commit();
            System.out.println(String.format(
                    "⚡️ Production updated: Stunde=%s | kWh=%.3f | community_produced=%.3f",
                    hr,
                    msg.getKwh(),
                    record.getCommunityProduced()
            ));

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return record;
    }

    // Sende das aktualisierte EnergyHistorical-Objekt als JSON weiter
    private static void sendHistoricalUpdate(EnergyHistorical rec,
                                             Channel channel,
                                             ObjectMapper mapper) {
        try {
            // DTO für die Weitergabe (Stunde + alle Spalten)
            HistoricalUpdateMessage out = new HistoricalUpdateMessage(
                    rec.getHour(),
                    rec.getCommunityProduced(),
                    rec.getCommunityUsed(),
                    rec.getGridUsed()
            );
            String outJson = mapper.writeValueAsString(out);

            channel.basicPublish(
                    "",                   // Default-Exchange
                    HIST_UPD_QUEUE,       // Routing Key = Queue-Name
                    null,
                    outJson.getBytes(StandardCharsets.UTF_8)
            );
            System.out.println(" ✉️[>] Sent HIST-UPDATE: " + outJson);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
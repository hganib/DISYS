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

    private final static String QUEUE_NAME = "energy";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        System.out.println(" [*] Waiting for messages...");

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        DeliverCallback callback = (consumerTag, delivery) -> {
            String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
            EnergyMessage msg = mapper.readValue(json, EnergyMessage.class);

            switch (msg.getType().toUpperCase()) {
                case "USER":
                    updateConsumption(msg);
                    break;
                case "PRODUCER":
                    updateProduction(msg);
                    break;
                default:
                    // unbekannter Typ → ignorieren oder loggen
                    System.out.println("⚠️ Unbekannter Typ: " + msg.getType());
            }
        };

        channel.basicConsume(QUEUE_NAME, true, callback, consumerTag -> {});
    }

    private static void updateProduction(EnergyMessage msg) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Timestamp auf volle Stunde runterbrechen
            LocalDateTime timestamp = LocalDateTime.parse(msg.getDatetime());
            LocalDateTime hour = timestamp.withMinute(0).withSecond(0).withNano(0);

            // Datensatz holen oder anlegen
            EnergyHistorical record;
            try {
                record = em.createQuery(
                                "SELECT e FROM EnergyHistorical e WHERE e.hour = :hour",
                                EnergyHistorical.class
                        )
                        .setParameter("hour", hour)
                        .getSingleResult();
            } catch (NoResultException ex) {
                record = new EnergyHistorical();
                record.setHour(hour);
                record.setCommunityProduced(0.0);
                record.setCommunityUsed(0.0);
                record.setGridUsed(0.0);
                em.persist(record);
            }

            // Erzeugung aufsummieren
            record.setCommunityProduced(record.getCommunityProduced() + msg.getKwh());
            em.merge(record);

            tx.commit();
            System.out.println("🔋⚡️ Production updated: " +
                    hour + " + " + msg.getKwh() + " kWh => Overall PRODUCED: " +
                    record.getCommunityProduced());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void updateConsumption(EnergyMessage msg) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            LocalDateTime timestamp = LocalDateTime.parse(msg.getDatetime());
            LocalDateTime hour = timestamp.withMinute(0).withSecond(0).withNano(0);

            EnergyHistorical record = em.createQuery("SELECT e FROM EnergyHistorical e WHERE e.hour = :hour", EnergyHistorical.class)
                    .setParameter("hour", hour)
                    .getSingleResult();

            double usedSum = record.getCommunityUsed() + msg.getKwh();
            double over = Math.max(0, usedSum - record.getCommunityProduced());

            record.setCommunityUsed(Math.min(usedSum, record.getCommunityProduced()));
            record.setGridUsed(record.getGridUsed() + over);

            em.merge(record);
            tx.commit();

            System.out.println("✅🔌 Usage updated: " + hour + " || " + msg.getKwh() + " kWh" +
                    " || Community Used: " + record.getCommunityUsed() +
                    " || Grid Used: " + record.getGridUsed());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
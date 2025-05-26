package at.technikum;

import at.technikum.entity.EnergyHistorical;
import at.technikum.util.JPAUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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
            String json = new String(delivery.getBody(), "UTF-8");

            try {
                EnergyMessage msg = mapper.readValue(json, EnergyMessage.class);
                if (!"USER".equalsIgnoreCase(msg.getType())) return;

                updateDatabase(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume(QUEUE_NAME, true, callback, consumerTag -> {});
    }

    private static void updateDatabase(EnergyMessage msg) {
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

            System.out.println("✅ Updated: " + hour + " || " + msg.getKwh() + " kWh" +
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
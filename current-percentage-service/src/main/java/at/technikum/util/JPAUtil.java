package at.technikum.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utility-Klasse zur Bereitstellung eines EntityManagerFactory-Singletons.
 */
public class JPAUtil {

    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            return Persistence.createEntityManagerFactory("usagePU");
        } catch (Exception e) {
            System.err.println("❌ Initial EntityManagerFactory creation failed." + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void shutdown() {
        if (emf != null) {
            emf.close();
        }
    }
}
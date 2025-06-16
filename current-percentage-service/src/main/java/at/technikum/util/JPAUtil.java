package at.technikum.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utility class to manage a singleton EntityManagerFactory for JPA operations.
 * This ensures that the expensive factory creation is done only once,
 * and the EntityManagerFactory can be shared across the application.
 */
public class JPAUtil {

    /**
     * Singleton instance of the EntityManagerFactory.
     * Created eagerly at class loading time.
     */
    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    /**
     * Builds the EntityManagerFactory based on the 'usagePU' persistence unit defined in persistence.xml.
     * If creation fails, logs the error and throws an ExceptionInInitializerError to halt startup.
     * @return configured EntityManagerFactory
     */
    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            // Create the EntityManagerFactory using the persistence unit defined in persistence.xml
            return Persistence.createEntityManagerFactory("usagePU");
        } catch (Exception e) {
            // Log the error and throw an exception to prevent application startup
            System.err.println("❌ Initial EntityManagerFactory creation failed." + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Provides access to the singleton EntityManagerFactory.
     *
     * @return the shared EntityManagerFactory instance
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}
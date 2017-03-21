package pl.ahyzyk.beanUnit.internal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import java.util.Map;

/**
 * Created by andrz on 16.03.2017.
 */
public class TestProvider {


    private final PersistenceProvider provider;
    private final String persistanceName;
    private final Map<String, String> params;
    private boolean used = false;
    private EntityManager entityManager = null;
    private boolean error = false;
    private EntityManagerFactory entityManagerFactory;

    public TestProvider(String key, PersistenceProvider value, Map<String, String> params) {
        this.persistanceName = key;
        this.provider = value;
        this.params = params;
    }

    public EntityManager getEntityManager() {
        used = true;
        if (error) {
            throw new RuntimeException("Error during getting entity manager");
        }
        try {
            if (entityManagerFactory == null) {
                entityManagerFactory = provider.createEntityManagerFactory(persistanceName, params);
            }
            if (entityManager == null || !entityManager.isOpen()) {
                entityManager = entityManagerFactory.createEntityManager();
            }
            return entityManager;
        } catch (Throwable e) {
            error = true;
            throw e;
        }
    }


    public void begin() {
        if (used) {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            }
        }
    }

    public void end() {
        if (used) {
            used = false;
            entityManager.getTransaction().commit();
        }
    }

    public void close() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

    public PersistenceProvider getProvider() {
        return provider;
    }
}

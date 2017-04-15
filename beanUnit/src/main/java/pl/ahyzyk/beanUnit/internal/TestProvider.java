package pl.ahyzyk.beanUnit.internal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.Stack;

/**
 * Created by andrz on 16.03.2017.
 */
public class TestProvider {


    private final String persistanceName;
    private final PersistenceUnitInfoImpl persistenceUnitInfo;
    private final Map<String, String> params;
    private boolean used = false;
    private Stack<EntityManager> entityManagers = new Stack<>();
    private boolean error = false;
    private EntityManagerFactory entityManagerFactory;

    public TestProvider(String key, PersistenceUnitInfoImpl persistenceUnitInfo, Map<String, String> params) {
        this.persistanceName = key;

        this.persistenceUnitInfo = persistenceUnitInfo;
        this.params = params;
    }

    public EntityManager getEntityManager() {
        used = true;
        if (error) {
            throw new RuntimeException("Error during getting entity manager");
        }
        try {
            initProvider();
            if (entityManagers.isEmpty()) {
                addNew();
            }
            return entityManagers.peek();
        } catch (Throwable e) {
            error = true;
            throw e;
        }
    }

    private void initProvider() {
        if (entityManagerFactory == null) {
            entityManagerFactory = persistenceUnitInfo.getProvider().createContainerEntityManagerFactory(persistenceUnitInfo, params);
        }
    }


    public void begin() {
        if (used) {
            addNew();
//            System.out.println("DODANIE TRANSAKCJI");
            if (!entityManagers.peek().getTransaction().isActive()) {
                entityManagers.peek().getTransaction().begin();
            }
        }
    }

    public void addNew() {
        initProvider();
        entityManagers.push(entityManagerFactory.createEntityManager());
    }

    public void end() {
        if (!entityManagers.isEmpty()) {
            EntityManager entityManager = entityManagers.pop();
            if (used && entityManager.getTransaction().isActive()) {
//                System.out.println("USUNIĘCIE TRANSAKCJI");
                if (entityManager.getTransaction().getRollbackOnly()) {
                    entityManager.getTransaction().rollback();
                } else {
                    entityManager.getTransaction().commit();
                }
            }

            entityManager.clear();
            entityManager.close();

        }
    }

    public void close() {
        if (!entityManagers.isEmpty()) {
            if (entityManagers.peek() != null && entityManagers.peek().isOpen()) {
                entityManagers.peek().close();
            }
        }

        used = false;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void endAll() {
        while (!entityManagers.isEmpty()) {
            end();
        }
    }
}

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
    private final TestPersistenceContext persistenceContext;
    private boolean used = false;
    private Stack<EntityManager> entityManagers = new Stack<>();
    private boolean error = false;
    private EntityManagerFactory entityManagerFactory;

    public TestProvider(TestPersistenceContext persistenceContext, String key, PersistenceUnitInfoImpl persistenceUnitInfo, Map<String, String> params) {
        this.persistanceName = key;
        this.persistenceContext = persistenceContext;

        this.persistenceUnitInfo = persistenceUnitInfo;
        this.params = params;
    }

    public EntityManager getRealEntityManager() {
        if (error) {
            throw new RuntimeException("Error during getting entity manager");
        }
        try {
            return entityManagerFactory.createEntityManager();
        } catch (Throwable ex) {
            error = true;
            throw ex;
        }
    }

    public EntityManager getEntityManager() {
        used = true;
        if (error) {
            throw new RuntimeException("Error during getting entity manager");
        }
        try {
            if (entityManagers.isEmpty()) {
                addNew();
            }
            return entityManagers.peek();
        } catch (Throwable e) {
            error = true;
            throw e;
        }
    }

    private synchronized void initProvider() {
        if (entityManagerFactory == null) {
            try {
                entityManagerFactory = persistenceUnitInfo.getProvider().createContainerEntityManagerFactory(persistenceUnitInfo, params);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public void begin() {
        if (used) {
            addNew();
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
            if (entityManager.getTransaction().isActive()) {

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

    public Exception endAll() {
        Exception ex = null;
        while (!entityManagers.isEmpty()) {
            try {
                end();
            } catch (Exception ex2) {
                if (ex == null) {
                    ex = ex2;
                }
            }
        }
        setUsed(false);
        return ex;
    }

    @Override
    public String toString() {
        return "TestProvider : " + persistanceName;
    }
}

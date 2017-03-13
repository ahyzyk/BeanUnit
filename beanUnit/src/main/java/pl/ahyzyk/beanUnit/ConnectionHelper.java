package pl.ahyzyk.beanUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.Connection;
import java.util.Map;


public abstract class ConnectionHelper {

    private Connection connection;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public Map<String, String> getParamenters() {
        return null;
    }


    public abstract String getPersistanceUnitName();

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }


}

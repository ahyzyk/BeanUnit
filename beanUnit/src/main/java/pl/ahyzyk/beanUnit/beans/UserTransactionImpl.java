package pl.ahyzyk.beanUnit.beans;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.*;

/**
 * Created by ahyzyk on 22.04.2017.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class UserTransactionImpl implements UserTransaction {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void begin() throws NotSupportedException, SystemException {
        entityManager.getTransaction().begin();
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        entityManager.getTransaction().commit();
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        entityManager.getTransaction().rollback();
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        entityManager.getTransaction().setRollbackOnly();
    }

    @Override
    public int getStatus() throws SystemException {
        return 0;
    }

    @Override
    public void setTransactionTimeout(int seconds) throws SystemException {

    }
}

package ejb;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.*;

/**
 * Created by ahyzyk on 22.04.2017.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class TransactionManagementBean {
    @Resource
    private UserTransaction userTransaction;

    @PersistenceContext
    private EntityManager entityManager;


    public void test1() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        entityManager.createQuery("delete from Table1");
        userTransaction.commit();
    }
}

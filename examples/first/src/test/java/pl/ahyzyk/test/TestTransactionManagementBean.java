package pl.ahyzyk.test;

import ejb.TransactionManagementBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.ahyzyk.beanUnit.TestRunner;
import pl.ahyzyk.beanUnit.annotations.TestConfiguration;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.*;

/**
 * Created by ahyzyk on 22.04.2017.
 */
@RunWith(TestRunner.class)
@TestConfiguration(persistenceUnitName = "H2-hibernate")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TestTransactionManagementBean {
    @EJB
    private TransactionManagementBean transactionManagementBean;

    @Test
    public void test1() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        transactionManagementBean.test1();
    }
}

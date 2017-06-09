package ejb;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import java.sql.Connection;

/**
 * Created by ahyzyk on 22.04.2017.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class TransactionManagementBean {
    @Resource
    private UserTransaction userTransaction;

    @Resource
    private DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;


    public void test1() throws Exception {
        userTransaction.begin();
        entityManager.createQuery("delete from Table1");
        userTransaction.commit();
        Connection conn = dataSource.getConnection();
        System.out.println(conn.getSchema());
        conn.close();
    }
}

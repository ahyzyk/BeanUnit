package ejb;

import model.Table1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class Table1Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(Table1Manager.class);

    @EJB
    Table1Manager table1Manager;

    @PersistenceContext
    private EntityManager entityManager;

    public void create(Long id, String value) {

        Table1 tb1 = new Table1();
        tb1.setId(id);
        tb1.setValue(value);
        entityManager.persist(tb1);

    }

    public Table1 get(Long id) {
        Table1 result = entityManager.createQuery("select t from Table1 t where t.id=:id", Table1.class)
                .setParameter("id", id).getResultList().get(0);
        return result;
    }


    public void show() {

        LOGGER.info("Table1:");
        entityManager.createQuery("select t from Table1 t", Table1.class).getResultList()
                .stream().forEach(t -> LOGGER.info(t.toString()));

    }

    public Table1 get2(long l) {
        Table1 result = get(l);
        result.getListData().isEmpty();
        return result;
    }

    public Table1 get3(long l) {
        Table1 result = table1Manager.get(l);
        result.getListData().isEmpty();
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Table1 get4(long l) {
        Table1 result = table1Manager.get(l);
        result.getListData().isEmpty();
        return result;
    }
}

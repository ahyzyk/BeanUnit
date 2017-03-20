package ejb;

import model.Table1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class Table1Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(Table1Manager.class);
    @PersistenceContext
    private EntityManager entityManager;

    public void create(Long id, String value) {

        Table1 tb1 = new Table1();
        tb1.setId(id);
        tb1.setValue(value);
        entityManager.persist(tb1);

    }

    public void show() {

        LOGGER.info("Table1:");
        entityManager.createQuery("select t from Table1 t", Table1.class).getResultList()
                .stream().forEach(t -> LOGGER.info(t.toString()));

    }
}

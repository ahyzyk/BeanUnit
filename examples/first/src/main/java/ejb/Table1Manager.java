package ejb;

import model.Table1;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class Table1Manager {
    @PersistenceContext
    private EntityManager entityManager;

    public void create(Long id, String value) {
        Table1 tb1 = new Table1();
        tb1.setId(id);
        tb1.setValue(value);
        entityManager.persist(tb1);

    }
}

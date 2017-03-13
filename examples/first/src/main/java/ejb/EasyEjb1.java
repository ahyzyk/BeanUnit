package ejb;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class EasyEjb1 extends EasyEjb2 {
    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    private void postConstruct() {
        System.out.println("postConstruct 1: " + getClass().getName());
    }

    public void testMe() {
        System.out.println("testMe1 : " + getClass().getName());
        System.out.println("testMe2 : " + entityManager.getClass());
    }
}

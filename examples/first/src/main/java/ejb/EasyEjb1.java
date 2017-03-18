package ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class EasyEjb1 extends EasyEjb2 {

    @EJB
    private Table1Manager table1Manager;
    @PostConstruct
    private void postConstruct() {
        table1Manager.create(3L, "EasyEjb1 postConstruct");
    }

    @PreDestroy
    private void preDestroy() {
        table1Manager.create(4L, "EasyEjb1 preDestroy");
    }


    public void testMe() {
        table1Manager.create(7L, "test me");
    }
}

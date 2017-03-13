package ejb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class EasyEjb1 extends EasyEjb2 {

    @EJB
    private Table1Manager table1Manager;
    @PostConstruct
    private void postConstruct() {
        table1Manager.create(2L, this.getClass().getCanonicalName());
    }

    public void testMe() {
        table1Manager.create(4L, this.getClass().getCanonicalName());
    }
}

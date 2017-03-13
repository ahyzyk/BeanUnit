package ejb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class EasyEjb {
    @EJB
    private EasyEjb1 easyEjb1;
    @EJB
    private Table1Manager table1Manager;
    @PostConstruct
    private void postConstruct() {
        table1Manager.create(1L, this.getClass().getCanonicalName());
    }


    public void testMe() {
        easyEjb1.testMe();
    }
}

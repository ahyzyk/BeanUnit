package ejb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;


@Stateless
public class EasyEjb2 {
    @EJB
    private Table1Manager table1Manager;
    @EJB
    private EasyEjb easyEjb;
    @Inject
    private EasyEjb1 easyEjb1;

    @PostConstruct
    private void postConstruct() {
        table1Manager.create(3l, this.getClass().getCanonicalName());
    }

}

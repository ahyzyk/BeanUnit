package ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
        table1Manager.create(5L, "EasyEjb2 postConstruct");
    }

    @PreDestroy
    private void preDestroy() {
        table1Manager.create(6L, "EasyEjb2 preDestroy");
    }


}

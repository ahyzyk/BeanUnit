package ejb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class EasyEjb2 {
    @EJB
    private EasyEjb easyEjb;

    @Inject
    private EasyEjb1 easyEjb1;


    @PostConstruct
    private void postConstruct() {
        System.out.println("postConstruct 2 :" + getClass().getName());
    }

}

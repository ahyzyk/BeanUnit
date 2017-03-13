package ejb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class EasyEjb {
    @EJB
    private EasyEjb1 easyEjb1;

    @PostConstruct
    private void postConstruct() {
        System.out.println("Inited");
    }


    public void testMe() {
        easyEjb1.testMe();
    }
}

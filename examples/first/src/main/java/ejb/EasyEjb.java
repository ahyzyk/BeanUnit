package ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class EasyEjb {
    @EJB
    private EasyEjb1 easyEjb1;

    @EJB
    private IEasyEjb3 easyEjb3;

    @EJB
    private Table1Manager table1Manager;
    @PostConstruct
    private void postConstruct() {
        System.out.println("PostConstruct");
        table1Manager.create(1L, "EasyEjb postConstruct");
    }

    @PreDestroy
    private void preDestroy() {
        table1Manager.create(2L, "EasyEjb preDestroy");
    }


    public void echo(String s) {
        System.out.println(s);
    }


    public void testMe() {
        easyEjb1.testMe();
    }
}

package ejb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Created by ahyzyk on 20.03.2017.
 */
@Startup
@Singleton
public class SingletonEjb {
    @EJB
    private Table1Manager table1Manager;

    @PostConstruct
    public void init() {
        System.out.println("Init singleton");
    }
}

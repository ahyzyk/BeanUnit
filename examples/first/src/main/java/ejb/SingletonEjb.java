package ejb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(SingletonEjb.class);
    @EJB
    private Table1Manager table1Manager;

    @PostConstruct
    public void init() {
        LOGGER.info("Init singleton");
    }
}

package ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Created by ahyzyk on 20.10.2017.
 */
@Stateless
public class PrivateEjb {

    private final Integer i;
    private final PrivateFinalClass privateClass;
    @EJB
    private EasyEjb ejb;

    public PrivateEjb() {
        privateClass = new PrivateFinalClass();
        i = 0;
    }

    public void method1() {
        ejb.echo("method 1 ");
        method2();
        method3();
        privateClass.echo("private final");
    }

    private void method2() {
        ejb.echo("method 2");
    }

    public void method3() {
        PrivateClass test = new PrivateClass();
        test.echo("method 3");
    }

    public EasyEjb getEjb() {
        return ejb;
    }

    public void setEjb(EasyEjb ejb) {
        this.ejb = ejb;
    }

    private class PrivateClass {
        public void echo(String echo) {
            getEjb().echo("PrivateClass " + echo);
        }
    }

    private final class PrivateFinalClass {
        public void echo(String echo) {
            getEjb().echo("PrivateFinalClass " + echo);
        }
    }
}

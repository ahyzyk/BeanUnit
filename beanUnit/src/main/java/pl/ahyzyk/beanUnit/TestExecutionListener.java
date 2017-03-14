package pl.ahyzyk.beanUnit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import pl.ahyzyk.beanUnit.internal.TestBeanManager;

/**
 * Created by andrz on 14.03.2017.
 */
public class TestExecutionListener extends RunListener {
    private final TestRunner runner;

    private TestBeanManager beanManager;


    public TestExecutionListener(TestRunner runner) {
        this.runner = runner;
    }

    public void testStarted(Description description) throws Exception {

        System.out.println("Starting: " + description.getMethodName());
        beanManager = new TestBeanManager();
        runner.getConnectionHelper().getEntityManager().getTransaction().begin();
        beanManager.init(runner.getObject(), runner.getConnectionHelper());

    }

    @Override
    public void testFinished(Description description) throws Exception {
        beanManager.destory();
        runner.getConnectionHelper().getEntityManager().getTransaction().rollback();
        System.out.println("Ending test : " + description.getMethodName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TestExecutionListener) {
            return true;
        }
        return false;
    }
}

package pl.ahyzyk.beanUnit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import pl.ahyzyk.beanUnit.internal.TestBeanManager;
import pl.ahyzyk.beanUnit.internal.TestPersistanceContext;

import java.lang.reflect.InvocationTargetException;


public class TestRunner extends Runner {
    private final BlockJUnit4ClassRunner runner;


    private TestBeanManager testBeanManager;

    public TestRunner(Class<?> klass) throws InvocationTargetException, InitializationError, InstantiationException, IllegalAccessException {
        TestBeanManager beanManager = new TestBeanManager();
        beanManager.setPersistanceContext(TestPersistanceContext.init(klass));


        runner = new BlockJUnit4ClassRunner(klass) {

            @Override
            protected Object createTest() throws Exception {
                Object targetObject = super.createTest();
                System.out.println("Initialize beans");
                beanManager.init(targetObject);
                beanManager.beginTransaction();
                return targetObject;
            }

            @Override
            protected void runChild(FrameworkMethod method, RunNotifier notifier) {
                System.out.println("Starting: " + method.getName());
                super.runChild(method, notifier);
                beanManager.destory();
                beanManager.endTransaction();
                System.out.println("Ending test : " + method.getName());
            }
        };

    }


    public Description getDescription() {
        return runner.getDescription();
    }

    public void run(RunNotifier runNotifier) {
        runner.run(runNotifier);
    }


}

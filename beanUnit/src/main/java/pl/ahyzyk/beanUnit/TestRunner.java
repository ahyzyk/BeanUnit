package pl.ahyzyk.beanUnit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import pl.ahyzyk.beanUnit.dbUnit.DbUnitHelper;
import pl.ahyzyk.beanUnit.internal.TestBeanManager;
import pl.ahyzyk.beanUnit.internal.TestPersistanceContext;

import java.lang.reflect.InvocationTargetException;


public class TestRunner extends Runner {
    private final BlockJUnit4ClassRunner runner;
    private final Class<?> klass;
    public FrameworkMethod currentMethod;
    public RunNotifier currentNotifier;
    TestBeanManager beanManager = new TestBeanManager();
    DbUnitHelper dbUnitHelper = null;
    private TestBeanManager testBeanManager;

    public TestRunner(Class<?> klass) throws InvocationTargetException, InitializationError, InstantiationException, IllegalAccessException {
        this.klass = klass;
        beanManager.setPersistanceContext(TestPersistanceContext.init(klass));

        dbUnitHelper = new DbUnitHelper(klass, beanManager.getPersistanceContext());
        runner = new BlockJUnit4ClassRunner(klass) {


            @Override
            protected Object createTest() throws Exception {
                System.out.println("Starting: " + currentMethod.getName());
                Object targetObject = super.createTest();
                System.out.println("Initialize beans");
                beanManager.init(targetObject);
                beanManager.beginTransaction();
                tryToExecute("DbUnit before method ", currentNotifier, () -> dbUnitHelper.beforeMethod(currentMethod));
                return targetObject;
            }

            @Override
            protected void runChild(FrameworkMethod method, RunNotifier notifier) {

                currentMethod = method;
                currentNotifier = notifier;
                super.runChild(method, notifier);

                tryToExecute("Destroy beans", notifier, beanManager::destory);
                tryToExecute("DbUnit after method ", notifier, () -> dbUnitHelper.afterMethod(method));
                tryToExecute("End of transaction", notifier, beanManager::endTransaction);
                System.out.println("Ending test : " + method.getName());
            }
        };

    }


    public Description getDescription() {
        return runner.getDescription();
    }

    public void run(RunNotifier runNotifier) {
        runner.run(runNotifier);
        tryToExecute("Close entity managers", runNotifier, beanManager::closeEntityManagers);
    }

    private void tryToExecute(String message, RunNotifier notifier, Runnable consumer) {
        try {
            consumer.run();
        } catch (Exception ex) {
            notifier.fireTestFailure(new Failure(Description.createTestDescription(klass, currentMethod.getName() + "-" + message), ex));
        }
    }

    public interface Runnable {
        void run() throws Exception;
    }


}

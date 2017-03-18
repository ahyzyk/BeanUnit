package pl.ahyzyk.beanUnit;

import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import pl.ahyzyk.beanUnit.annotations.AfterDBTest;
import pl.ahyzyk.beanUnit.dbUnit.DbUnitHelper;
import pl.ahyzyk.beanUnit.internal.TestBeanManager;
import pl.ahyzyk.beanUnit.internal.TestPersistanceContext;

import java.lang.reflect.InvocationTargetException;


public class TestRunner extends BlockJUnit4ClassRunner {
    private final BlockJUnit4ClassRunner runner;
    private final Class<?> klass;
    public FrameworkMethod currentMethod;
    public RunNotifier currentNotifier;
    TestBeanManager beanManager = new TestBeanManager();
    DbUnitHelper dbUnitHelper = null;
    private TestBeanManager testBeanManager;

    public TestRunner(Class<?> klass) throws InvocationTargetException, InitializationError, InstantiationException, IllegalAccessException {
        super(klass);
        this.klass = klass;
        beanManager.setPersistanceContext(TestPersistanceContext.init(klass));

        dbUnitHelper = new DbUnitHelper(klass, beanManager.getPersistanceContext());
        runner = new BlockJUnit4ClassRunner(klass) {

            public EachTestNotifier eachNotifier;
            public Description description;
            Object targetObject = klass.newInstance();

            @Override
            protected Object createTest() throws Exception {
                return targetObject;
            }


            protected void runTest(FrameworkMethod method, RunNotifier notifier) {
                description = this.describeChild(method);
                if (this.isIgnored(method)) {
                    notifier.fireTestIgnored(description);
                } else {
                    this.runTestLeaf(this.methodBlock(method), description, notifier);
                }

            }

            protected final void runTestLeaf(Statement statement, Description description, RunNotifier notifier) {

                eachNotifier = new EachTestNotifier(notifier, description);
                eachNotifier.fireTestStarted();

                try {
                    onStart();
                    statement.evaluate();
                    onFinish();
                } catch (AssumptionViolatedException var10) {
                    eachNotifier.addFailedAssumption(var10);
                } catch (Throwable var11) {
                    eachNotifier.addFailure(var11);
                } finally {
                    onFinishFinally();
                    eachNotifier.fireTestFinished();
                }

            }

            private void onFinish() throws InvocationTargetException, IllegalAccessException {
                tryToExecute("Destroy beans", eachNotifier, beanManager::destroy);
                tryToExecute("DbUnit after method ", eachNotifier, () -> dbUnitHelper.afterMethod(currentMethod));
                TestBeanManager.callMethod(targetObject, targetObject.getClass(), true, m -> m.isAnnotationPresent(AfterDBTest.class), null);
            }

            private void onFinishFinally() {
                tryToExecute("DbUnit after method ", eachNotifier, () -> dbUnitHelper.afterFinallyMethod(currentMethod));
                tryToExecute("End of transaction", eachNotifier, beanManager::endTransaction);
                System.out.println("Ending test : " + currentMethod.getName());
            }

            private void onStart() throws InvocationTargetException, IllegalAccessException {
                System.out.println("Starting: " + currentMethod.getName());
                System.out.println("Initialize beans");
                beanManager.init(targetObject);
                beanManager.beginTransaction();
                tryToExecute("DbUnit before method ", currentNotifier, () -> dbUnitHelper.beforeMethod(currentMethod));


            }

            @Override
            protected void runChild(FrameworkMethod method, RunNotifier notifier) {
                currentMethod = method;
                runTest(method, notifier);

            }
        };

    }


    public Description getDescription() {
        return runner.getDescription();
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        runner.filter(filter);
    }

    public void run(RunNotifier runNotifier) {
        runner.run(runNotifier);
        tryToExecute("Close entity managers", runNotifier, beanManager::closeEntityManagers);
    }

    private void tryToExecute(String message, EachTestNotifier notifier, Runnable consumer) {
        try {
            consumer.run();
        } catch (Throwable ex) {
            notifier.addFailure(ex);
        }
    }

    private void tryToExecute(String message, RunNotifier notifier, Runnable consumer) {
        try {
            consumer.run();
        } catch (Throwable ex) {
            notifier.fireTestFailure(new Failure(Description.createTestDescription(klass, currentMethod.getName() + "-" + message), ex));
        }
    }

    public interface Runnable {
        void run() throws Exception;
    }


}

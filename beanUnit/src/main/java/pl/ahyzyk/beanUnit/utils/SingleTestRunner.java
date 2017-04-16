package pl.ahyzyk.beanUnit.utils;

import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ahyzyk.beanUnit.annotations.AfterDBUnit;
import pl.ahyzyk.beanUnit.annotations.BeforeDBUnit;
import pl.ahyzyk.beanUnit.dbUnit.DbUnitHelper;
import pl.ahyzyk.beanUnit.internal.BeanState;
import pl.ahyzyk.beanUnit.internal.TestBeanManager;
import pl.ahyzyk.beanUnit.internal.TestPersistenceContext;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by ahyzyk on 15.04.2017.
 */
public class SingleTestRunner extends BlockJUnit4ClassRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleTestRunner.class);
    final Object targetObject;
    private final Class<?> klass;
    private final TestBeanManager beanManager;
    public EachTestNotifier eachNotifier;
    public Description description;
    public FrameworkMethod currentMethod;
    public RunNotifier currentNotifier;
    private DbUnitHelper dbUnitHelper;

    public SingleTestRunner(Class<?> klass, TestBeanManager beanManager) throws Exception {
        super(klass);
        this.klass = klass;
        targetObject = klass.newInstance();
        this.beanManager = beanManager;

    }


    @Override
    protected Object createTest() throws Exception {
        return targetObject;
    }


    public void runTest(FrameworkMethod method, RunNotifier notifier) throws InvocationTargetException, IllegalAccessException {
        description = this.describeChild(method);
        if (this.isIgnored(method)) {
            notifier.fireTestIgnored(description);
        } else {
            this.runTestLeaf(this.methodBlock(method), description, notifier);
        }

    }

    protected final void runTestLeaf(Statement statement, Description description, RunNotifier notifier) throws InvocationTargetException, IllegalAccessException {
        TestPersistenceContext.setPU(klass);
        eachNotifier = new EachTestNotifier(notifier, description);
        eachNotifier.fireTestStarted();

        try {
            onStart();
            LOGGER.info("Testing...");
            statement.evaluate();
            onFinish();
        } catch (AssumptionViolatedException var10) {
            eachNotifier.addFailedAssumption(var10);
            var10.printStackTrace();
        } catch (Throwable var11) {
            var11.printStackTrace();
            eachNotifier.addFailure(var11);
        } finally {
            onFinishFinally();
            eachNotifier.fireTestFinished();
        }

    }

    private void onFinish() {
        tryToExecute("Destroy beans", eachNotifier, beanManager::destroy);
        tryToExecute("DbUnit after method ", eachNotifier, () -> dbUnitHelper.afterMethod(currentMethod));
    }

    private void onFinishFinally() throws InvocationTargetException, IllegalAccessException {
        tryToExecute("End of transaction", eachNotifier, beanManager::endTransactions);

        tryToExecute("DbUnit after method ", eachNotifier, () -> dbUnitHelper.afterFinallyMethod(currentMethod));

        tryToExecute("AfterDBUnit", eachNotifier, () -> TestBeanManager.callMethod(targetObject, targetObject.getClass(), true, m -> m.isAnnotationPresent(AfterDBUnit.class), null));

    }

    private void onStart() throws InvocationTargetException, IllegalAccessException {

        System.out.println("##################################################");
        System.out.println("Starting: " + klass.getName() + "\nTest:" + currentMethod.getName());
        System.out.println("##################################################");
        LOGGER.info("Persistence Unit : " + TestPersistenceContext.getInstance().getDefault());
        LOGGER.info("Initialize beans");
        dbUnitHelper = new DbUnitHelper(klass);
        TestPersistenceContext.getInstance().endAll();
        beanManager.init(targetObject);

        TestBeanManager.callMethod(targetObject, targetObject.getClass(), true, m -> m.isAnnotationPresent(BeforeDBUnit.class), null);
        tryToExecute("DbUnit clear method ", currentNotifier, () -> dbUnitHelper.clearMethod(currentMethod));

        tryToExecute("DbUnit load method ", currentNotifier, () -> dbUnitHelper.loadMethod(currentMethod));
        beanManager.setBeanState(BeanState.CONSTRUCT);
        beanManager.initStartup();
    }

    private void tryToExecute(String message, EachTestNotifier notifier, Runnable consumer) {
        try {
            consumer.run();
        } catch (Throwable ex) {
            notifier.addFailure(ex);
        }
    }

    public void tryToExecute(String message, RunNotifier notifier, Runnable consumer) {
        try {
            consumer.run();
        } catch (Throwable ex) {
            ex.printStackTrace();
            notifier.fireTestFailure(new Failure(Description.createTestDescription(klass, currentMethod.getName() + "-" + message), ex));
            throw new RuntimeException("Error during : " + message, ex);
        }
    }

    public FrameworkMethod getCurrentMethod() {
        return currentMethod;
    }

    public void setCurrentMethod(FrameworkMethod currentMethod) {
        this.currentMethod = currentMethod;
    }

    public interface Runnable {
        void run() throws Exception;
    }
}

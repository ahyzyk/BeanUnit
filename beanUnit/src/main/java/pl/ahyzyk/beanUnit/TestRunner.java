package pl.ahyzyk.beanUnit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import pl.ahyzyk.beanUnit.internal.TestPersistanceContext;

import java.lang.reflect.InvocationTargetException;


public class TestRunner extends Runner {
    private final BlockJUnit4ClassRunner runner;
    private final TestExecutionListener listener;
    private Object targetObject;
    private ConnectionHelper connectionHelper;


    public TestRunner(Class<?> klass) throws InvocationTargetException, InitializationError, InstantiationException, IllegalAccessException {
        listener = new TestExecutionListener(this);
        runner = new BlockJUnit4ClassRunner(klass) {
            protected Statement withBefores(FrameworkMethod method, Object target,
                                            Statement statement) {
                targetObject = target;
                connectionHelper = TestPersistanceContext.init(target.getClass());

                return super.withBefores(method, target, statement);
            }
        };

    }


    public Description getDescription() {
        return runner.getDescription();
    }

    public void run(RunNotifier runNotifier) {
        runNotifier.removeListener(listener);
        runNotifier.addListener(listener);
        runner.run(runNotifier);
    }


    public ConnectionHelper getConnectionHelper() {
        return connectionHelper;
    }

    public Object getObject() {
        return targetObject;
    }
}

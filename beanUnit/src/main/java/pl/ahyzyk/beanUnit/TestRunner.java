package pl.ahyzyk.beanUnit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ahyzyk.beanUnit.utils.SingleTestRunner;


public class TestRunner extends BlockJUnit4ClassRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);
    private final SingleTestRunner runner;

    public TestRunner(Class<?> klass) throws Exception {
        super(klass);
        runner = new SingleTestRunner(klass);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        runner.setCurrentMethod(method);
        runner.tryToExecute("Run test", notifier, () -> runner.runTest(method, notifier));
    }


}

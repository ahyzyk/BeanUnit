package pl.ahyzyk.beanUnit;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ahyzyk.beanUnit.dbUnit.DbUnitHelper;
import pl.ahyzyk.beanUnit.internal.TestBeanManager;
import pl.ahyzyk.beanUnit.utils.SingleTestRunner;


public class TestRunner extends BlockJUnit4ClassRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockJUnit4ClassRunner.class);
    private final SingleTestRunner runner;
    private final Class<?> klass;

    TestBeanManager beanManager = new TestBeanManager();
    DbUnitHelper dbUnitHelper = null;
    private TestBeanManager testBeanManager;

    public TestRunner(Class<?> klass) throws Exception {
        super(klass);
        this.klass = klass;

        runner = new SingleTestRunner(klass, beanManager);

    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        runner.setCurrentMethod(method);
        runner.tryToExecute("Run test", notifier, () -> runner.runTest(method, notifier));
    }

    public Description getDescription() {
        return runner.getDescription();
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        runner.filter(filter);
    }




}

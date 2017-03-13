package pl.ahyzyk.beanUnit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import pl.ahyzyk.beanUnit.internal.TestBeanManager;
import pl.ahyzyk.beanUnit.internal.TestPersistanceContext;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class TestRunner extends Runner {
    private final File resourcesDirectory = new File("src/beanUnit/resources");
    private final BlockJUnit4ClassRunner runner;

    private ConnectionHelper connectionHelper;


    public TestRunner(Class<?> klass) throws InvocationTargetException, InitializationError, InstantiationException, IllegalAccessException {

        connectionHelper = TestPersistanceContext.init(klass);

        runner = new BlockJUnit4ClassRunner(klass) {
            protected Statement withBefores(FrameworkMethod method, Object target,
                                            Statement statement) {

                connectionHelper.getEntityManager().getTransaction().begin();
                TestBeanManager.init(method, target, connectionHelper);
                Statement result = super.withBefores(method, target, statement);
                //dbunit;
                connectionHelper.getEntityManager().flush();
                connectionHelper.getEntityManager().getTransaction().rollback();
                return result;
            }

            @Override
            protected Statement withAfterClasses(Statement statement) {
                TestPersistanceContext.close(connectionHelper);
                return super.withAfterClasses(statement);
            }
        };
    }


    public Description getDescription() {
        return runner.getDescription();
    }

    public void run(RunNotifier runNotifier) {
        runner.run(runNotifier);
    }

    public File getResourcesDirectory() {
        return resourcesDirectory;
    }

    public File getResource(String resource) {
        try {
            return new File(resourcesDirectory.getCanonicalPath() + "/" + resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package pl.ahyzyk.beanUnit.dbUnit;

import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.junit.runners.model.FrameworkMethod;
import org.xml.sax.InputSource;
import pl.ahyzyk.beanUnit.annotations.ClearTable;
import pl.ahyzyk.beanUnit.annotations.DataSetDirectory;
import pl.ahyzyk.beanUnit.annotations.ShouldMatchDataSet;
import pl.ahyzyk.beanUnit.annotations.UsingDataSet;
import pl.ahyzyk.beanUnit.internal.TestPersistanceContext;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Created by andrz on 17.03.2017.
 */
public class DbUnitHelper {
    private final static Logger LOGGER = Logger.getLogger(DbUnitHelper.class.getName());
    private final Class klass;
    private final TestPersistanceContext persistanceContext;
    private boolean inited = false;

    private DataSetDirectory dataSetDirectory;

    public DbUnitHelper(Class klass, TestPersistanceContext persistanceContext) {
        this.klass = klass;
        this.persistanceContext = persistanceContext;
        findDirectory(klass);
        if (inited) {
            LOGGER.info(String.format("DbUnitDirectory is set for %s", klass.getName()));
        }
    }

    private void findDirectory(Class clazz) {
        if (clazz == Object.class) {
            return;
        }
        if (klass.isAnnotationPresent(DataSetDirectory.class)) {
            inited = true;
            dataSetDirectory = (DataSetDirectory) klass.getAnnotation(DataSetDirectory.class);
        }
    }


    public <T extends Annotation> void beforeMethod(FrameworkMethod method) {

        runAnnottation(method, ClearTable.class, this::clearTable);
        runAnnottation(method, UsingDataSet.class, this::usingDataSet);
    }

    public void afterMethod(FrameworkMethod method) {
        runAnnottation(method, ShouldMatchDataSet.class, this::shouldMatchDataSet);
        runAnnottation(method, ClearTable.class, this::clearTable);
    }

    private void clearTable(FrameworkMethod frameworkMethod, ClearTable t) throws SQLException {
        EntityManager entityManager = persistanceContext.get();

        for (String table : t.value()) {
            EntityType<?> entity = findEntityForTableName(table);
            entityManager.createQuery("delete from " + entity.getJavaType().getSimpleName()).executeUpdate();
        }
        entityManager.clear();
    }


    private <T extends Object> void runAnnottation(FrameworkMethod method, Class annotation, BiExceptionConsumer<FrameworkMethod, T> consumer) {
        T config = (T) method.getAnnotation(annotation);
        if (config != null) {
            checkInit();

            try {
                consumer.accept(method, config);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Error during execution of %s for method %s", annotation.getName(), method.getName()), e);
            }
        }
    }


    private void checkInit() {
        assertTrue("DataSetDirectory should be added to class", inited);
    }

    private void shouldMatchDataSet(FrameworkMethod method, ShouldMatchDataSet annotation) {
    }

    private EntityType<?> findEntityForTableName(String tableName) {
        EntityManager entityManager = persistanceContext.get();

        return entityManager.getMetamodel().getEntities().stream().filter(e ->
                tableName.equalsIgnoreCase(getTableName(e))).findFirst().orElse(null);

    }

    private String getTableName(EntityType<?> e) {
        if (e.getJavaType().isAnnotationPresent(Table.class)) {
            return e.getJavaType().getAnnotation(Table.class).name();
        } else {
            return e.getJavaType().getSimpleName();
        }


    }

    private void usingDataSet(FrameworkMethod method, UsingDataSet annotation) throws DataSetException, IllegalAccessException, InstantiationException {
        EntityManager entityManager = persistanceContext.get();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("datasets/testEjb/test1.xml");
        FlatXmlProducer producer = new FlatXmlProducer(new InputSource(stream));
        IDataSet dataSet = new CachedDataSet(producer);
        for (String table : dataSet.getTableNames()) {
            EntityType<?> entity = findEntityForTableName(table);

            entityManager.createQuery("delete from " + entity.getJavaType().getSimpleName()).executeUpdate();
            entityManager.clear();

            ITable data = dataSet.getTable(table);
            String sql = "insert into " + table + "( ";
            String values = "";
            String columns = "";
            for (Column column : data.getTableMetaData().getColumns()) {
                values += "?,";
                columns += column.getColumnName() + ",";
            }
            sql += columns.substring(0, columns.length() - 1) + ") values (" + values.substring(0, values.length() - 1) + ")";

            Query query = entityManager.createNativeQuery(sql);
            System.out.println(sql);
            for (int row = 0; row < data.getRowCount(); row++) {
                int i = 1;
                for (Column column : data.getTableMetaData().getColumns()) {
                    Object value = data.getValue(row, column.getColumnName());
                    query.setParameter(i++, value);
                }
                query.executeUpdate();
            }
            entityManager.flush();

        }


    }

}

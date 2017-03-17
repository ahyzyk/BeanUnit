package pl.ahyzyk.beanUnit.dbUnit;

import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.junit.runners.model.FrameworkMethod;
import org.xml.sax.InputSource;
import pl.ahyzyk.beanUnit.annotations.ClearTable;
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

/**
 * Created by andrz on 17.03.2017.
 */
public class DbUnitHelper {
    private final static Logger LOGGER = Logger.getLogger(DbUnitHelper.class.getName());
    private final Class klass;
    private final TestPersistanceContext persistanceContext;


    public DbUnitHelper(Class klass, TestPersistanceContext persistanceContext) {
        this.klass = klass;
        this.persistanceContext = persistanceContext;


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
            try {
                consumer.accept(method, config);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Error during execution of %s for method %s", annotation.getName(), method.getName()), e);
            }
        }
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
        for (String file : annotation.value()) {
            loadData(entityManager, file);
        }


    }

    private void loadData(EntityManager entityManager, String file) throws DataSetException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(file);
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

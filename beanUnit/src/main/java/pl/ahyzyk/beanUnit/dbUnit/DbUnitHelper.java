package pl.ahyzyk.beanUnit.dbUnit;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.junit.Assert;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import pl.ahyzyk.beanUnit.annotations.ClearTable;
import pl.ahyzyk.beanUnit.annotations.ShouldMatchDataSet;
import pl.ahyzyk.beanUnit.annotations.UsingDataSet;
import pl.ahyzyk.beanUnit.internal.TestPersistanceContext;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.dbunit.Assertion.assertEquals;

/**
 * Created by ahyzyk on 17.03.2017.
 */
public class DbUnitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(DbUnitHelper.class);
    private final Class klass;
    private final TestPersistanceContext persistanceContext;


    public DbUnitHelper(Class klass, TestPersistanceContext persistanceContext) {
        this.klass = klass;
        this.persistanceContext = persistanceContext;
    }


    public <T extends Annotation> void clearMethod(FrameworkMethod method) {
        runAnnottation(method, ClearTable.class, this::clearTable);
    }


    public <T extends Annotation> void loadMethod(FrameworkMethod method) {

        runAnnottation(method, UsingDataSet.class, this::usingDataSet);
    }

    public void afterMethod(FrameworkMethod method) {
        runAnnottation(method, ShouldMatchDataSet.class, this::shouldMatchDataSet);
    }

    public void afterFinallyMethod(FrameworkMethod method) {
        runAnnottation(method, ClearTable.class, this::clearTable);
    }

    private void clearTable(FrameworkMethod frameworkMethod, ClearTable t) throws SQLException {
        EntityManager entityManager = persistanceContext.get();

        for (String table : t.value()) {
            entityManager.createNativeQuery("delete from " + table).executeUpdate();
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


    private void shouldMatchDataSet(FrameworkMethod method, ShouldMatchDataSet annotation) throws DatabaseUnitException, IOException {
        EntityManager entityManager = persistanceContext.get();
        for (String file : annotation.value()) {
            checkData(entityManager, file, annotation);
        }

    }

    private ITable createResultTable(ITable table, List<Object[]> rows) throws DataSetException {
        if (rows.isEmpty()) {
            throw new RuntimeException("Empty DB table " + table.getTableMetaData().getTableName());
        }
        Object[] javaTypes = fillJavaTypes(rows);

        List<Column> columnList = new ArrayList<>();

        int x = 0;
        for (Column column : table.getTableMetaData().getColumns()) {
            columnList.add(new Column(column.getColumnName(), DataType.forObject(javaTypes[x])));
            x++;
        }

        DefaultTable result = new DefaultTable(table.getTableMetaData().getTableName(),
                columnList.toArray(new Column[]{}));
        for (Object[] row : rows) {
            result.addRow(row);
        }
        return result;
    }

    private Object[] fillJavaTypes(List<Object[]> rows) {
        Object[] javaTypes = new Object[rows.get(0).length];
        for (int x = 0; x < javaTypes.length; x++) {
            javaTypes[x] = "";
        }

        for (Object[] row : rows) {
            for (int x = 0; x < row.length; x++) {
                if (row[x] != null) {
                    javaTypes[x] = row[x];
                }
            }
        }
        return javaTypes;
    }

    private void checkData(EntityManager entityManager, String file, ShouldMatchDataSet annotation) throws DatabaseUnitException, IOException {
        IDataSet dataSet = createDataSet(file);
        boolean error = false;

        for (String table : dataSet.getTableNames()) {
            ITable matchTable = dataSet.getTable(table);
            TableInfo tableInfo = new TableInfo(matchTable).invoke();
            Query query = entityManager.createNativeQuery(tableInfo.getSelectSql());
            ITable result = createResultTable(matchTable, query.getResultList());
            try {
                if (annotation.ordered()) {
                    String[] columns = tableInfo.getColumnList().toArray(new String[]{});
                    SortedTable expected = new SortedTable(matchTable, columns);
                    SortedTable actual = new SortedTable(result, expected.getTableMetaData());
//                    printTable(expected);
//                    printTable(actual);
                    assertEquals(expected, actual);
                } else {
                    assertEquals(matchTable, result);
                }
            } catch (Throwable e) {
                e.printStackTrace();
                error = true;
                printTable(result);
            }
        }
        Assert.assertTrue("Dataset compare errors", !error);
    }

    private void printTable(ITable result) throws IOException, DataSetException {
        IDataSet resultDataSet = new DefaultDataSet(result);
        FlatXmlDataSet.write(resultDataSet, System.out);
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
        IDataSet dataSet = createDataSet(file);
        for (String table : dataSet.getTableNames()) {

            entityManager.createNativeQuery("delete from " + table).executeUpdate();
            entityManager.clear();

            ITable data = dataSet.getTable(table);

            TableInfo tableInfo = new TableInfo(data).invoke();

            Query query = entityManager.createNativeQuery(tableInfo.getInsertSql());
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


    private IDataSet createDataSet(String file) throws DataSetException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(file);
        FlatXmlProducer producer = new FlatXmlProducer(new InputSource(stream));
        return new CachedDataSet(producer);
    }


    private class TableInfo {
        private ITable data;
        private String values = "";
        private String columns = "";
        private List<String> columnList = new ArrayList<>();

        public TableInfo(ITable data) {
            this.data = data;
        }


        public String getInsertSql() {
            return String.format("insert into %s(%s) values(%s)",
                    data.getTableMetaData().getTableName(),
                    columns, values);
        }

        public String getSelectSql() {
            return String.format("select %s from %s",
                    columns, data.getTableMetaData().getTableName()
            );
        }


        public TableInfo invoke() throws DataSetException {
            for (Column column : data.getTableMetaData().getColumns()) {
                values += "?,";
                columns += column.getColumnName() + ",";
                columnList.add(column.getColumnName());
            }
            columns = columns.substring(0, columns.length() - 1);
            values = values.substring(0, values.length() - 1);
            return this;
        }

        public List<String> getColumnList() {
            return columnList;
        }
    }
}

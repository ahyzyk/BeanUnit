package pl.ahyzyk.beanUnit.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.ahyzyk.beanUnit.annotations.TestConfiguration;
import pl.ahyzyk.beanUnit.annotations.defaultAnotations.DefaultTestConfiguration;
import pl.ahyzyk.beanUnit.annotations.utils.AnnotationUtils;
import pl.ahyzyk.beanUnit.dataSource.LocalDataSource;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TestPersistenceContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestPersistenceContext.class);

    private static TestPersistenceContext instance;
    private Map<String, TestProvider> providerMap = new HashMap<>();
    private String defaultPersistence;
    private Stack<EntityManager> entityManagers = new Stack<>();


    private TestPersistenceContext() {
    }

    public static TestPersistenceContext getInstance() {
        if (instance == null) {
            instance = new TestPersistenceContext();
            instance.init();
        }
        return instance;
    }

    private static TestConfiguration findTestConfiguration(Class klass) {
        if (klass == Object.class) {
            return new DefaultTestConfiguration();
        }
        // find in class annotation
        if (klass.isAnnotationPresent(TestConfiguration.class)) {
            return (TestConfiguration) klass.getAnnotation(TestConfiguration.class);
        }
        //find method with testConfiguration
        List<Method> methods = AnnotationUtils.getAnnotatedMethods(klass, TestConfiguration.class);
        if (!methods.isEmpty()) {
            try {
                return (TestConfiguration) methods.get(0).invoke(null, new Object[]{});
            } catch (Exception ex) {
                throw new RuntimeException("Error during calling TestConfiguration method", ex);
            }
        }
        return findTestConfiguration(klass.getSuperclass());

    }

    public static void setPU(Class klass) {
        if (getInstance().providerMap.size() > 0) {
            String defaultPersistence = findTestConfiguration(klass).persistenceUnitName();

            if (defaultPersistence.length() == 0) {
                defaultPersistence = getInstance().providerMap.keySet().stream().findFirst().get();
            }
            getInstance().defaultPersistence = defaultPersistence;
        }
    }

    private static Map<String, PersistenceUnitInfoImpl> findProviders(Enumeration<URL> resource) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ParserConfigurationException, IOException, SAXException {
        Map<String, PersistenceUnitInfoImpl> providers = new HashMap<>();
        if (resource == null) {
            return providers;
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        LOGGER.info("Persistence unit loading");
        while (resource.hasMoreElements()) {
            URL url = resource.nextElement();
            LOGGER.info("PU XML location : " + url.toString());
            Document doc = dBuilder.parse(url.openStream());
            NodeList childs = doc.getDocumentElement().getChildNodes();
            findNode(childs, "persistence-unit", persistenceUnit ->
            {
                String name = persistenceUnit.getAttributes().getNamedItem("name").getNodeValue();
                String transactionType = persistenceUnit.getAttributes().getNamedItem("transaction-type").getNodeValue();
                PersistenceUnitInfoImpl providerUnitInfo = new PersistenceUnitInfoImpl(name, transactionType);
                consumeNode(persistenceUnit.getChildNodes(), child -> {
                    if ("properties".equalsIgnoreCase(child.getNodeName())) {
                        consumeNode(child.getChildNodes(), prop -> {
                            if ("property".equalsIgnoreCase(prop.getNodeName())) {
                                providerUnitInfo.setProperty(prop.getAttributes().getNamedItem("name").getNodeValue(),
                                        prop.getAttributes().getNamedItem("value").getNodeValue());
                            }
                        });

                    } else {
                        providerUnitInfo.getConsumer(child.getNodeName()).accept(child.getTextContent().trim());
                    }

                });
                providers.put(providerUnitInfo.getPersistenceUnitName(), providerUnitInfo);
            });
        }
        String names = providers.keySet().stream().collect(Collectors.joining(", "));
        LOGGER.info("Loaded persistence unit names : " + names + "\n");

        return providers;
    }

    private static void consumeNode(NodeList nodeList, Consumer<Node> consumer) {
        for (int x = 0; x < nodeList.getLength(); x++) {
            Node item = nodeList.item(x);
            if (item instanceof Element) {
                consumer.accept(item);
            }

        }
    }

    private static void findNode(NodeList nodeList, String node, Consumer<Node> consumer) {
        for (int x = 0; x < nodeList.getLength(); x++) {
            Node item = nodeList.item(x);
            if (node.equals(item.getNodeName())) {
                consumer.accept(item);
            }
        }
    }

    public TestPersistenceContext init() {

        TestPersistenceContext result = new TestPersistenceContext();

        Map<String, PersistenceUnitInfoImpl> providers;
        try {
            providers = findProviders(this.getClass().getClassLoader().getResources("META-INF/persistence.xml"));
        } catch (Exception e) {
            throw new RuntimeException("Error during reading persistence.xml", e);
        }

        for (Map.Entry<String, PersistenceUnitInfoImpl> entry : providers.entrySet()) {
            Map<String, String> params = new HashMap<>();
            String copyClass = entry.getValue().getProperties().getProperty("beanUnit.copyClass", "");
            if (!copyClass.isEmpty()) {
                for (String className : providers.get(copyClass).getManagedClassNames()) {
                    entry.getValue().getManagedClassNames().add(className);
                }
            }
            providerMap.put(entry.getKey(), new TestProvider(result, entry.getKey(), entry.getValue(), params));
        }


        return result;
    }

    public void begin(boolean transactional) {
        setUsed("");
        providerMap.entrySet().forEach(e -> {
            if (!e.getKey().isEmpty())
                e.getValue().createEntityManager(transactional);
        });
    }

    public void end() {
        providerMap.entrySet().forEach(e -> {
            if (!e.getKey().isEmpty())
                e.getValue().end();
        });
    }

    public void endAll() {
        Optional<Exception> ex = providerMap.values().stream().map(TestProvider::endAll).filter(Objects::nonNull).findFirst();
        if (ex.isPresent()) {
            throw new RuntimeException("Error during classing transactions", ex.get());
        }
    }

    public void close() {
        providerMap.values().stream().forEach(p -> p.close());
    }

    public EntityManager get(String s) {
        if (entityManagers.isEmpty()) {
            return getTestProvider(s).getEntityManager();
        } else {
            return entityManagers.peek();
        }
    }

    public EntityManager get() {
        return get("");
    }


    public void setUsed(String s) {
        getTestProvider(s).setUsed(true);
    }

    private TestProvider getTestProvider(String s) {

        String key = s.isEmpty() ? defaultPersistence : s;
        TestProvider result = providerMap.get(key);

        return result;
    }

    public String getDefault() {
        return defaultPersistence;
    }

    public DataSource getDataSource(String pu) {
        Properties properties = providerMap.get(pu).getPersistenceUnitInfo().getProperties();
        return new LocalDataSource(
                properties.getProperty("javax.persistence.jdbc.url"),
                properties.getProperty("javax.persistence.jdbc.user"),
                properties.getProperty("javax.persistence.jdbc.password"));


    }
}

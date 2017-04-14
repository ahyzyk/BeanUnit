package pl.ahyzyk.beanUnit.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.ahyzyk.beanUnit.annotations.TestConfiguration;

import javax.persistence.EntityManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TestPersistenceContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestPersistenceContext.class);

    private static Map<String, TestProvider> providerMap = new HashMap<>();
    private String defaultPersistence;
    private Stack<EntityManager> entityManagers = new Stack<>();

    public TestPersistenceContext() {
    }

    public static TestPersistenceContext init(Class<?> klass) {

        TestPersistenceContext result = new TestPersistenceContext();

        Map<String, PersistenceUnitInfoImpl> providers;
        try {
            providers = findProviders(klass.getClassLoader().getResources("META-INF/persistence.xml"));
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

            result.providerMap.put(entry.getKey(), new TestProvider(entry.getKey(), entry.getValue(), params));
        }

        if (result.providerMap.size() > 0) {
            String defaultPersistence = "";
            if (klass.isAnnotationPresent(TestConfiguration.class)) {
                defaultPersistence = klass.getAnnotation(TestConfiguration.class).persistenceUnitName();
            }
            if (defaultPersistence.length() == 0) {
                defaultPersistence = result.providerMap.keySet().stream().findFirst().get();
            }
            result.defaultPersistence = defaultPersistence;
        }

        return result;
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

    public void begin() {
        providerMap.values().stream().forEach(p -> p.begin());
    }

    public void end() {
        providerMap.values().stream().forEach(p -> p.end());
    }

    public void endAll() {
        providerMap.values().stream().forEach(p -> p.endAll());
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
        return providerMap.get(s.isEmpty() ? defaultPersistence : s + "_TEST");
    }
}

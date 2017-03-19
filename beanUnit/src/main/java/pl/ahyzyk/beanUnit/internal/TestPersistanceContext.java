package pl.ahyzyk.beanUnit.internal;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.ahyzyk.beanUnit.TestConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TestPersistanceContext {

    Map<String, TestProvider> providerMap = new HashMap<>();


    public static TestPersistanceContext init(Class<?> klass) {

        TestPersistanceContext result = new TestPersistanceContext();

        Map<String, PersistenceProvider> providers;
        try {
            providers = findProviders(klass.getClassLoader().getResourceAsStream("META-INF/persistence.xml"));
        } catch (Exception e) {
            throw new RuntimeException("Error during reading persistance.xml", e);
        }

        Map<String, String> params = new HashMap<>();


        providers.entrySet().forEach(es -> result.providerMap.put(es.getKey(), new TestProvider(es.getKey(), es.getValue(), params)));
        if (result.providerMap.size() > 0) {
            String defaultPersistance = "";
            if (klass.isAnnotationPresent(TestConfiguration.class)) {
                defaultPersistance = klass.getAnnotation(TestConfiguration.class).persistanceUntiName();
            }
            if (defaultPersistance.length() == 0) {
                defaultPersistance = result.providerMap.keySet().stream().findFirst().get();
            }
            result.providerMap.put("", result.providerMap.get(defaultPersistance));
        }

        return result;
    }

    private static Map<String, PersistenceProvider> findProviders(InputStream resource) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ParserConfigurationException, IOException, SAXException {
        Map<String, PersistenceProvider> providers = new HashMap<>();
        if (resource == null) {
            return providers;
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(resource);

        NodeList childs = doc.getDocumentElement().getChildNodes();
        findNode(childs, "persistence-unit", t ->
                findNode(t.getChildNodes(), "provider", t2 ->
                        providers.put(t.getAttributes().getNamedItem("name").getNodeValue(),
                                createPersistenceProvider(t2.getTextContent().trim()))
                ));
        return providers;
    }

    private static PersistenceProvider createPersistenceProvider(String className) {
        try {
            return (PersistenceProvider) Class.forName(className).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create instance for provider " + className, e);
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

    public void close() {
        providerMap.values().stream().forEach(p -> p.close());
    }

    public EntityManager get(String s) {
        return providerMap.get(s).getEntityManager();
    }


    public EntityManager get() {
        return get("");
    }
}

package pl.ahyzyk.beanUnit.internal;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.ahyzyk.beanUnit.ConnectionHelper;
import pl.ahyzyk.beanUnit.TestConfiguration;
import pl.ahyzyk.beanUnit.utils.ErrorConsumer;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

//TODO : obsługa persistance context z parametrem
//TODO : zmiana konfiguracji - domyślne connection - jeśli jedno nie wymaga dodawnia connectionHelpera
public class TestPersistanceContext {


    public static ConnectionHelper init(Class<?> klass) {
        if (klass.isAnnotationPresent(TestConfiguration.class)) {

            try {
                ConnectionHelper params = klass.getAnnotation(TestConfiguration.class).connectionHelper().newInstance();
//                conn = DriverManager.getConnection(params.url());

                PersistenceProvider provider = findProvider(klass.getClassLoader().getResourceAsStream("META-INF/persistence.xml"), params.getPersistanceUnitName());
                EntityManagerFactory factory = provider.createEntityManagerFactory(params.getPersistanceUnitName(), params.getParamenters());
                params.setEntityManagerFactory(factory);
                return params;
            } catch (Exception ex) {
                throw new RuntimeException("Unable to init connection", ex);
            }

        }
        return new EmptyConnectionHelper();
    }

    private static PersistenceProvider findProvider(InputStream resource, String persistanceName) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(resource);
        Map<String, String> providers = new HashMap<>();
        NodeList childs = doc.getDocumentElement().getChildNodes();
        findNode(childs, "persistence-unit", t ->
                findNode(t.getChildNodes(), "provider", t2 ->
                        providers.put(t.getAttributes().getNamedItem("name").getNodeValue(), t2.getTextContent().trim())
                ));
        String className;
        if (providers.size() == 1) {
            className = (String) providers.values().toArray()[0];
        } else {
            className = providers.get(persistanceName);
        }

        return (PersistenceProvider) Class.forName(className).newInstance();
    }

    private static void findNode(NodeList nodeList, String node, Consumer<Node> consumer) {
        for (int x = 0; x < nodeList.getLength(); x++) {
            Node item = nodeList.item(x);
            if (node.equals(item.getNodeName())) {
                consumer.accept(item);
            }
        }
    }

    private static void closeCloseable(ErrorConsumer consumer) {
        try {
            consumer.accept();
        } catch (Exception ex) {
            //ignore error
        }
    }

    public static void close(ConnectionHelper connectionHelper) {
//        closeCloseable(() -> connectionHelper.getEntityManager().close());
//        closeCloseable(() -> connectionHelper.getEntityManagerFactory().close());
//        closeCloseable(() -> connectionHelper.getConnection().close());
    }
}

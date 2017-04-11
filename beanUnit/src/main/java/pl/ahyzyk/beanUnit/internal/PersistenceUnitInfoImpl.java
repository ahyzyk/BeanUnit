package pl.ahyzyk.beanUnit.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by ahyzyk on 11.04.2017.
 */
public class PersistenceUnitInfoImpl implements PersistenceUnitInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceUnitInfoImpl.class);
    private String persistenceUnitName;
    private String persistenceProviderClassName;
    private PersistenceUnitTransactionType transactionType;
    private DataSource jtaDataSource;
    private DataSource nonJtaDataSource;
    private List<String> mappingFileNames = new ArrayList<>();
    private List<URL> jarFileUrls = new ArrayList<>();
    private List<String> managedClassNames = new ArrayList<>();
    private boolean excludeUnlistedClasses = true;
    private SharedCacheMode sharedCacheMod;
    private ValidationMode validationMode;
    private Properties properties = new Properties();
    private String persistenceXMLSchemaVersion;

    private Map<String, Consumer<String>> consumerMap = new HashMap();

    public PersistenceUnitInfoImpl(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
        consumerMap.put("provider", this::setPersistenceProviderClassName);
        consumerMap.put("jta-data-source", this::setJtaDataSource);
        consumerMap.put("non-jta-data-source", this::setNonJtaDataSource);
        consumerMap.put("mapping-file", this::setMappingFileNames);
        consumerMap.put("jar-file", this::setJarFile);
        consumerMap.put("class", this::setClass);
        consumerMap.put("exclude-unlisted-classes", this::setExcludeUnlisedClasses);
        consumerMap.put("shared-cache-mode", this::setSharedCacheMode);
        consumerMap.put("validation-mode", this::setValidationMode);

    }

    private static PersistenceProvider createPersistenceProvider(String className) {
        try {
            return (PersistenceProvider) Class.forName(className).newInstance();
        } catch (Exception e) {
            LOGGER.warn("Unable to create instance for provider " + className);
            return null;
        }
    }

    public Consumer<String> getConsumer(String name) {
        if (consumerMap.containsKey(name)) {
            return consumerMap.get(name);
        }
        return s -> LOGGER.warn("Lack of consumer" + s);
    }

    public void setProperty(String s, String s2) {
        properties.put(s, s2);
    }

    private void setExcludeUnlisedClasses(String s) {
        excludeUnlistedClasses = Boolean.valueOf(s);
    }

    private void setClass(String s) {
        managedClassNames.add(s);
    }

    private void setJarFile(String s) {
        try {
            jarFileUrls.add(new URL(getPersistenceUnitRootUrl(), s));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return persistenceProviderClassName;
    }

    public void setPersistenceProviderClassName(String persistenceProviderClassName) {
        this.persistenceProviderClassName = persistenceProviderClassName;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = PersistenceUnitTransactionType.valueOf(transactionType);
    }

    @Override
    public DataSource getJtaDataSource() {
        return jtaDataSource;
    }

    public void setJtaDataSource(String name) {
        jtaDataSource = lookupDataSource(name);
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return nonJtaDataSource;
    }

    public void setNonJtaDataSource(String name) {
        nonJtaDataSource = lookupDataSource(name);
    }

    @Override
    public List<String> getMappingFileNames() {
        return mappingFileNames;
    }

    private void setMappingFileNames(String s) {
        mappingFileNames.add(s);
    }

    @Override
    public List<URL> getJarFileUrls() {
        return jarFileUrls;
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return getClass().getResource("/");
    }

    @Override
    public List<String> getManagedClassNames() {
        return managedClassNames;
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return excludeUnlistedClasses;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return sharedCacheMod;
    }

    private void setSharedCacheMode(String s) {
        sharedCacheMod = SharedCacheMode.valueOf(s);
    }

    @Override
    public ValidationMode getValidationMode() {
        return validationMode;
    }

    public void setValidationMode(String mode) {
        validationMode = ValidationMode.valueOf(mode);
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return persistenceXMLSchemaVersion;
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void addTransformer(ClassTransformer classTransformer) {

    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return getClassLoader();
    }

    private DataSource lookupDataSource(final String name) {
        try {
            return (DataSource) ((Context) new InitialContext().lookup("java:comp/env")).lookup(name);
        } catch (final NamingException e) {
            throw new RuntimeException("Unable to find data source: " + name, e);
        }

    }

    public PersistenceProvider getProvider() {
        return createPersistenceProvider(getPersistenceProviderClassName());
    }
}

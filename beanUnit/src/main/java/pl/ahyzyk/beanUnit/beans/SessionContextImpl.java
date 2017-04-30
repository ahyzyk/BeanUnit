package pl.ahyzyk.beanUnit.beans;

import pl.ahyzyk.beanUnit.internal.BeanManager;

import javax.ejb.*;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;
import java.security.Identity;
import java.security.Principal;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class SessionContextImpl implements SessionContext {

    @Inject
    private BeanManager beanManager;

    @Override
    public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
        return null;
    }

    @Override
    public EJBObject getEJBObject() throws IllegalStateException {
        return null;
    }

    @Override
    public MessageContext getMessageContext() throws IllegalStateException {
        return null;
    }

    @Override
    public <T> T getBusinessObject(Class<T> businessInterface) throws IllegalStateException {
        return (T) beanManager.getBeans().get(businessInterface).getBean();
    }

    @Override
    public Class getInvokedBusinessInterface() throws IllegalStateException {
        return null;
    }

    @Override
    public boolean wasCancelCalled() throws IllegalStateException {
        return false;
    }

    @Override
    public EJBHome getEJBHome() throws IllegalStateException {
        return null;
    }

    @Override
    public EJBLocalHome getEJBLocalHome() throws IllegalStateException {
        return null;
    }

    @Override
    public Properties getEnvironment() {
        return null;
    }

    @Override
    public Identity getCallerIdentity() {
        return null;
    }

    @Override
    public Principal getCallerPrincipal() throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isCallerInRole(Identity role) {
        return false;
    }

    @Override
    public boolean isCallerInRole(String roleName) throws IllegalStateException {
        return false;
    }

    @Override
    public UserTransaction getUserTransaction() throws IllegalStateException {
        return null;
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException {

    }

    @Override
    public boolean getRollbackOnly() throws IllegalStateException {
        return false;
    }

    @Override
    public TimerService getTimerService() throws IllegalStateException {
        return null;
    }

    @Override
    public Object lookup(String name) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Map<String, Object> getContextData() {
        return null;
    }
}

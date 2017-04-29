package pl.ahyzyk.beanUnit.context;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import java.util.Iterator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by ahyzyk on 29.04.2017.
 */
public class FacesContextImpl extends FacesContext {

    private ExternalContext externalContext;

    public FacesContextImpl() {
        FacesContext.setCurrentInstance(spy(this));
        externalContext = mock(ExternalContext.class);
    }

    @Override
    public Application getApplication() {
        return null;
    }

    @Override
    public Iterator<String> getClientIdsWithMessages() {
        return null;
    }

    @Override
    public ExternalContext getExternalContext() {
        return externalContext;
    }

    @Override
    public FacesMessage.Severity getMaximumSeverity() {
        return null;
    }

    @Override
    public Iterator<FacesMessage> getMessages() {
        return null;
    }

    @Override
    public Iterator<FacesMessage> getMessages(String clientId) {
        return null;
    }

    @Override
    public RenderKit getRenderKit() {
        return null;
    }

    @Override
    public boolean getRenderResponse() {
        return false;
    }

    @Override
    public boolean getResponseComplete() {
        return false;
    }

    @Override
    public ResponseStream getResponseStream() {
        return null;
    }

    @Override
    public void setResponseStream(ResponseStream responseStream) {

    }

    @Override
    public ResponseWriter getResponseWriter() {
        return null;
    }

    @Override
    public void setResponseWriter(ResponseWriter responseWriter) {

    }

    @Override
    public UIViewRoot getViewRoot() {
        return null;
    }

    @Override
    public void setViewRoot(UIViewRoot root) {

    }

    @Override
    public void addMessage(String clientId, FacesMessage message) {

    }

    @Override
    public void release() {

    }

    @Override
    public void renderResponse() {

    }

    @Override
    public void responseComplete() {

    }
}

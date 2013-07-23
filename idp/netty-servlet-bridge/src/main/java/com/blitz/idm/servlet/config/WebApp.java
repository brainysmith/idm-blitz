package com.blitz.idm.servlet.config;

import com.blitz.idm.servlet.impl.FilterChainImpl;
import com.blitz.idm.servlet.impl.RequestDispatcherImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class WebApp {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(RequestDispatcherImpl.class);
    private String name;

    private String contextRoot;

    private int sessionTimeout;

    private Map<String, String> contextParameters;

    private Collection<WebAppServletContextListener> contextListeners;

    private Collection<WebAppFilter> requestFilters;

    private Collection<WebAppFilter> forwardFilters;

    private Collection<WebAppServlet> servlets;

    private File staticResourcesFolder;


    public WebApp(String name, String contextRoot, int sessionTimeout,
                  Map<String, String> contextParameters,
                  Collection<WebAppServletContextListener> contextListeners,
                  Collection<WebAppFilter> requestFilters,
                  Collection<WebAppFilter> forwardFilters,
                  Collection<WebAppServlet> servlets){
        if (contextRoot == null)
            throw new IllegalArgumentException(
                    "Webapp configuration contextRoot can't be null!");
        this.contextRoot = contextRoot;
        if (name == null)
            throw new IllegalArgumentException(
                    "Webapp configuration name can't be null!");
        this.name = name;
        if (sessionTimeout <= 0)
            throw new IllegalArgumentException(
                    "Webapp configuration sessionTimeout must be positive!");
        this.sessionTimeout = sessionTimeout;
        this.contextParameters = contextParameters;
        this.contextListeners = contextListeners;
        this.forwardFilters = forwardFilters;
        this.requestFilters = requestFilters;
        if (servlets == null || servlets.isEmpty())
            throw new IllegalArgumentException(
                    "Webapp configuration servlets can't be null or empty!");
        this.servlets = servlets;
    }

    public Collection<WebAppServletContextListener> getServletContextListenerConfigurations() {
        return this.contextListeners != null ? Collections
                .unmodifiableCollection(this.contextListeners) : null;
    }

    public Map<String, String> getContextParameters() {
        return this.contextParameters != null ? Collections
                .unmodifiableMap(this.contextParameters) : null;
    }

    public String getRelativePath(String path) {
        return path.substring(getContextRoot().length());
    }

    public String getName() {
        return this.name;
    }

    public String getContextRoot() {
        return this.contextRoot;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public Collection<WebAppFilter> getRequestFilterConfigurations() {
        return this.requestFilters != null ? Collections
                .unmodifiableCollection(this.requestFilters) : null;
    }

    public Collection<WebAppFilter> getForwardFilterConfigurations() {
        return this.forwardFilters != null ? Collections
                .unmodifiableCollection(this.forwardFilters) : null;
    }

    public boolean hasForwardFilterConfigurations() {
        return this.forwardFilters != null && !this.forwardFilters.isEmpty();
    }

    public boolean hasRequestFilterConfigurations() {
        return this.requestFilters != null && !this.requestFilters.isEmpty();
    }

    public Collection<WebAppServlet> getServletConfigurations() {
        return this.servlets != null ? Collections
                .unmodifiableCollection(this.servlets) : null;
    }

    public boolean hasServletConfigurations() {
        return this.servlets != null && !this.servlets.isEmpty();
    }

    public WebApp setStaticResourcesFolder(String folder) {
        return this.setStaticResourcesFolder(new File(this.getClass().getResource(folder).getPath()));
    }

    public WebApp setStaticResourcesFolder(File folder) {
        if (folder == null)
            throw new IllegalArgumentException(
                    "Static resources folder must be not null!");

        if (!folder.exists())
            throw new IllegalArgumentException("Static resources folder '"
                    + folder.getAbsolutePath() + "' was not found!");

        if (!folder.isDirectory())
            throw new IllegalArgumentException("Static resources folder '"
                    + folder.getAbsolutePath() + "' must be a directory!");

        this.staticResourcesFolder = folder;
        return this;
    }

    public File getStaticResourcesFolder() {
        return staticResourcesFolder;
    }

    public void init() {
        this.initContextListeners();
        this.initFilters();
        this.initServlets();
    }

    public void destroy() {
        this.destroyServlets();
        this.destroyFilters();
        this.destroyContextListeners();
    }

    private void initContextListeners() {
        if (getServletContextListenerConfigurations() != null) {
            for (WebAppServletContextListener ctx : getServletContextListenerConfigurations()) {
                ctx.init();
            }
        }
    }

    private void destroyContextListeners() {
        if (getServletContextListenerConfigurations() != null) {
            for (WebAppServletContextListener ctx : getServletContextListenerConfigurations()) {
                ctx.destroy();
            }
        }
    }

    private void destroyServlets() {
        if (getServletConfigurations() != null) {
            for (WebAppServlet servlet : getServletConfigurations()) {
                servlet.destroy();
            }
        }
    }

    private void destroyFilters() {
        if (hasRequestFilterConfigurations()) {
            for (WebAppFilter filter : getRequestFilterConfigurations()) {
                filter.destroy();
            }
        }
        if (hasForwardFilterConfigurations()) {
            for (WebAppFilter filter : getForwardFilterConfigurations()) {
                filter.destroy();
            }
        }

    }

    protected void initFilters() {
        if (hasForwardFilterConfigurations()) {
            for (WebAppFilter filter : getForwardFilterConfigurations()) {
                filter.init();
            }
        }
        if (hasRequestFilterConfigurations()) {
            for (WebAppFilter filter : getRequestFilterConfigurations()) {
                filter.init();
            }
        }
    }

    protected void initServlets() {
        if (hasServletConfigurations()) {
            for (WebAppServlet servlet : getServletConfigurations()) {
                servlet.init();
            }
        }
    }

    public FilterChainImpl initializeChain(ServletContext servletContext, boolean isForward, String uri) {
        WebAppServlet webAppServlet = findServlet(uri);
        if (webAppServlet == null) {
            return null;
        }
        FilterChainImpl chain = new FilterChainImpl(servletContext, webAppServlet);

        if (isForward) {
            if (hasForwardFilterConfigurations()) {
                for (WebAppFilter s : getForwardFilterConfigurations()) {
                    if (s.matchesUrlPattern(uri))
                        chain.addFilterConfiguration(s);
                }
            }
        } else {
            if (hasRequestFilterConfigurations()) {
                for (WebAppFilter s : getRequestFilterConfigurations()) {
                    if (s.matchesUrlPattern(uri))
                        chain.addFilterConfiguration(s);
                }
            }
        }
        return chain;
    }

    private WebAppServlet findServlet(String uri) {

        if (!hasServletConfigurations()) {
            return null;
        }

        for (WebAppServlet s : getServletConfigurations()) {
            if (s.matchesUrlPattern(uri))
                return s;
        }

        return null;
    }
}

package com.blitz.idm.servlet.config;

import com.blitz.idm.handler.ServletBridgeConfig;
import com.blitz.idm.servlet.impl.ServletContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WebAppServletContextListener {

    private static final Logger log = LoggerFactory
            .getLogger(WebAppServletContextListener.class);

    private ServletContextListener listener;
    private String webappName;
    private boolean initialized = false;


    public WebAppServletContextListener(String webappName, ServletContextListener listener) {
        this.listener = listener;
        this.webappName = webappName;
    }

    public ServletContextListener getListener() {
        return listener;
    }

    public void init() {
        try {

            log.debug("Initializing listener: {}", this.listener.getClass());
            WebApp webapp = ServletBridgeConfig.get().getWebapp(webappName);
            this.listener.contextInitialized(new ServletContextEvent(
                    ServletContextImpl.getInstance(webapp)));
            this.initialized = true;

        } catch (Exception e) {

            this.initialized = false;
            log.error("Listener '" + this.listener.getClass()
                    + "' was not initialized!", e);
        }
    }

    public void destroy() {
        try {

            log.debug("Destroying listener: {}", this.listener.getClass());
            WebApp webapp = ServletBridgeConfig.get().getWebapp(webappName);
            this.listener.contextDestroyed(new ServletContextEvent(
                    ServletContextImpl.getInstance(webapp)));
            this.initialized = false;

        } catch (Exception e) {

            this.initialized = false;
            log.error("Listener '" + this.listener.getClass()
                    + "' was not destroyed!", e);
        }
    }

    public boolean isInitialized() {
        return initialized;
    }
}

package com.blitz.idm.servlet.impl;


import com.blitz.idm.servlet.config.ParameterMap;

import javax.servlet.ServletContext;
import java.util.Map;

public abstract class AbstractServletConfig extends ParameterMap {

    private ServletContext servletContext;

    public AbstractServletConfig(String name, ServletContext servletContext,Map<String, String> initParameters){
        super(name);
        this.servletContext = servletContext;
        if (initParameters != null) {
            addParameters(initParameters);
        }
    }

    protected ServletContext getServletContext() {
        return servletContext;
    }




}

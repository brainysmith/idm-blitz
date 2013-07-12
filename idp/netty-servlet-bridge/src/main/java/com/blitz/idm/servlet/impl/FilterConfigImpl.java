package com.blitz.idm.servlet.impl;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

public class FilterConfigImpl extends AbstractServletConfig implements FilterConfig {

    public FilterConfigImpl(String name, ServletContext servletContext, Map<String, String> initParameters) {
        super(name, servletContext, initParameters);
    }

    @Override
    public String getFilterName() {
        return super.getName();
    }

    @Override
    public ServletContext getServletContext() {
        return super.getServletContext();
    }

    @Override
    public String getInitParameter(String name) {
        return super.getParameter(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return super.getParameterNames();
    }




}

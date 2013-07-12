package com.blitz.idm.servlet.config;


import com.blitz.idm.servlet.impl.FilterConfigImpl;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;


public class WebAppFilter extends WebAppMapping<Filter, FilterConfig> {

    public WebAppFilter(Class<? extends Filter> filterClass,
                        String... urlPatterns) {
        super(filterClass, urlPatterns);
    }

    protected void init()  {
        //this.get().init(this.config);
    }

    protected void destroy()  {
        //this.component.destroy();
    }

    public WebAppFilter addInitParameter(String name, String value){
        super.addParameter(name, value);
        return this;
    }

    @Override
    public FilterConfig createNewConfigInstance(ServletContext servletContext) {
        return new FilterConfigImpl(getName(), servletContext, getParameters());
    }
}

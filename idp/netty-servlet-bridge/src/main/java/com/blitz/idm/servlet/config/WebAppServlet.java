package com.blitz.idm.servlet.config;

import com.blitz.idm.servlet.impl.ServletConfigImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

public class WebAppServlet extends WebAppMapping<HttpServlet, ServletConfig> {

    public WebAppServlet(Class<? extends HttpServlet> servletClass,
                         String... urlPatterns) {
        super(servletClass, urlPatterns);
    }


    protected void init()  {
        //this.component.init(this.config);
    }

    protected void destroy()  {
        //this.component.destroy();
    }

    public WebAppServlet addInitParameter(String name, String value){
        super.addParameter(name, value);
        return this;
    }


    @Override
    public ServletConfig createNewConfigInstance(ServletContext servletContext) {
        return new ServletConfigImpl(getName(), servletContext, getParameters());
    }
}

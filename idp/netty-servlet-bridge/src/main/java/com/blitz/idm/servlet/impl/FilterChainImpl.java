package com.blitz.idm.servlet.impl;

import com.blitz.idm.servlet.config.WebAppFilter;
import com.blitz.idm.servlet.config.WebAppServlet;

import javax.servlet.*;
import java.io.IOException;
import java.util.LinkedList;

public class FilterChainImpl implements FilterChain {

    private LinkedList<WebAppFilter> filterConfigurations;

    private WebAppServlet servletConfiguration;

    private ServletContext servletContext;

    public FilterChainImpl(ServletContext servletContext, WebAppServlet servletConfiguration) {
        this.servletContext = servletContext;
        this.servletConfiguration = servletConfiguration;
    }

    public void addFilterConfiguration(WebAppFilter config) {

        if (this.filterConfigurations == null)
            this.filterConfigurations = new LinkedList<WebAppFilter>();

        this.filterConfigurations.add(config);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {

        WebAppFilter config = filterConfigurations != null ? filterConfigurations
                .poll()
                : null;

        if (config != null) {
            FilterConfig filterConfig = config.createNewConfigInstance(servletContext);
            Filter filter = config.createNewHandlerInstance();
            filter.init(filterConfig);
            filter.doFilter(request, response, this);
        } else if (this.servletConfiguration != null) {
            ServletConfig servletConfig = servletConfiguration.createNewConfigInstance(servletContext);
            Servlet servlet = servletConfiguration.createNewHandlerInstance();
            servlet.init(servletConfig);
            servlet.service(request,
                    response);
        }
    }

    public WebAppServlet getServletConfiguration() {
        return servletConfiguration;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}

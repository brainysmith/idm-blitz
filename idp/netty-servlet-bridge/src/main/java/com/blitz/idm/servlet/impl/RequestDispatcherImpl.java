package com.blitz.idm.servlet.impl;

import com.blitz.idm.handler.ServletBridgeConfig;
import com.blitz.idm.servlet.config.WebApp;
import net.javaforge.netty.servlet.bridge.ServletBridgeRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Implementation of {@link javax.servlet.RequestDispatcher} that forward/include servlet to its
 * class file.
 *
 */
public class RequestDispatcherImpl implements RequestDispatcher {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(RequestDispatcherImpl.class);

    private String path;

	public RequestDispatcherImpl(String path) {
         this.path = path;
	}

	@Override
	public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        try {
            WebApp webapp = ServletBridgeConfig.get().getMatchedWebapp(path);
            if (webapp == null) {
                log.error("Webapp for forward path {} not found", path);
                throw new ServletBridgeRuntimeException(
                        "Webapp for forward path " + path + " not found");
            }
            FilterChainImpl chain = webapp.initializeChain(request.getServletContext(), true, webapp.getRelativePath(path));
            if (chain == null) {
                log.error("Servlet handler for forward path {} not found", path);
                throw new ServletBridgeRuntimeException(
                        "Servlet handler for forward path " + path + " not found");
            }
            HttpServletRequestImpl initialRequest = (HttpServletRequestImpl)request;
            String forwardPath = webapp.getRelativePath(path);
            if (initialRequest.getQueryString() != null) {
                forwardPath += "?" + initialRequest.getQueryString();
            }
            ServletRequest frwRequest = new HttpServletRequestImpl(initialRequest, forwardPath, chain );
            chain.doFilter(frwRequest, response);

		} catch (Exception e) {
            log.error("{}", e);
			throw new ServletException(e);
		}
	}

	@Override
	public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        log.error("Method 'include' not yet implemented!");
        throw new IllegalStateException(
                "Method 'include' not yet implemented!");
	}


}
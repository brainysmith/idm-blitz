/*
 * This file is part of cruzeira and it's licensed under the project terms.
 */
package com.blitz.idm.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import net.javaforge.netty.servlet.bridge.ServletBridgeRuntimeException;
import net.javaforge.netty.servlet.bridge.impl.FilterChainImpl;
import net.javaforge.netty.servlet.bridge.impl.ServletBridgeWebapp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



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
            FilterChainImpl chain = ServletBridgeWebapp.get().initializeChain(
                    path);
            if (chain == null) {
                log.error("Servlet handler for forward path {} not found", path);
                throw new ServletBridgeRuntimeException(
                        "Servlet handler for forward path " + path + " not found");
            }
            HttpServlet servlet = chain.getServletConfiguration().getHttpComponent();
            servlet.service(request, response);
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
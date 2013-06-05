/*
 * Copyright 2013 by Maxim Kalina
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.javaforge.netty.servlet.bridge.impl;

import com.blitz.idm.idp.config.IdpConfig;
import com.blitz.idm.idp.config.IdpConfigParam;
import net.javaforge.netty.servlet.bridge.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blitz.idm.servlet.RequestDispatcherImpl;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("unchecked")
public class ServletContextImpl extends ConfigAdapter implements ServletContext {
    private static final String WEB_ROOT_PATH = IdpConfig.getStringProperty(IdpConfigParam.WEB_CONTEXT_ROOT);
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ServletContextImpl.class);

    private static ServletContextImpl instance;

    private Map<String, Object> attributes;

    private String servletContextName;

    public static final ServletContextImpl get() {
        if (instance == null)
            instance = new ServletContextImpl();

        return instance;
    }

    private ServletContextImpl() {
        super("Netty Servlet Bridge");
    }

    @Override
    public Object getAttribute(String name) {
        return attributes != null ? attributes.get(name) : null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return Utils.enumerationFromKeys(attributes);
    }

    @Override
    public String getContextPath() {
        return WEB_ROOT_PATH;
    }

    @Override
    public int getMajorVersion() {
        return 2;
    }

    @Override
    public int getMinorVersion() {
        return 4;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return ServletContextImpl.class.getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return ServletContextImpl.class.getResourceAsStream(path);
    }

    @Override
    public String getServerInfo() {
        return super.getOwnerName();
    }

    @Override
    public void log(String msg) {
        log.info(msg);
    }

    @Override
    public void log(Exception exception, String msg) {
        log.error(msg, exception);
    }

    @Override
    public void log(String message, Throwable throwable) {
        log.error(message, throwable);
    }

    @Override
    public void removeAttribute(String name) {
        if (this.attributes != null)
            this.attributes.remove(name);
    }

    @Override
    public void setAttribute(String name, Object object) {
        if (this.attributes == null)
            this.attributes = new HashMap<String, Object>();

        this.attributes.put(name, object);
    }

    @Override
    public String getServletContextName() {
        return this.servletContextName;
    }

    void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        log.error("Deprecated as of Java Servlet API 2.1, with no direct replacement!");
        throw new IllegalStateException(
                "Deprecated as of Java Servlet API 2.1, with no direct replacement!");
    }

    @Override
    public Enumeration getServletNames() {
        log.error("Method 'getServletNames' deprecated as of Java Servlet API 2.0, with no replacement.");
        throw new IllegalStateException(
                "Method 'getServletNames' deprecated as of Java Servlet API 2.0, with no replacement.");
    }

    @Override
    public Enumeration getServlets() {
        log.error("Method 'getServlets' deprecated as of Java Servlet API 2.0, with no replacement.");
        throw new IllegalStateException(
                "Method 'getServlets' deprecated as of Java Servlet API 2.0, with no replacement.");
    }

    @Override
    public ServletContext getContext(String uripath) {
        return this;
    }

    @Override
    public String getMimeType(String file) {
        return Utils.getMimeType(file);

    }

    @Override
    public Set getResourcePaths(String path) {
        log.error("Method 'getResourcePaths' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getResourcePaths' not yet implemented!");
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        log.error("Method 'getNamedDispatcher' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getNamedDispatcher' not yet implemented!");
    }

    @Override
    public String getRealPath(String path) {
        log.error("Method 'getRealPath' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getRealPath' not yet implemented!");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return new RequestDispatcherImpl(getContextPath() + path);
    }
//---------------------------------------------------------------------------------------------------------------------
    @Override
    public int getEffectiveMajorVersion() {
        log.error("Method 'getEffectiveMajorVersion' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getEffectiveMajorVersion' not yet implemented!");
    }

    @Override
    public int getEffectiveMinorVersion() {
        log.error("Method 'getEffectiveMinorVersion' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getEffectiveMinorVersion' not yet implemented!");
    }

    @Override
    public boolean setInitParameter(String s, String s2) {
        log.error("Method 'setInitParameter' not yet implemented!");
        throw new IllegalStateException(
                "Method 'setInitParameter' not yet implemented!");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, String s2) {
        log.error("Method 'addServlet' not yet implemented!");
        throw new IllegalStateException(
                "Method 'addServlet' not yet implemented!");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Servlet servlet) {
        log.error("Method 'addServlet' not yet implemented!");
        throw new IllegalStateException(
                "Method 'addServlet' not yet implemented!");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass) {
        log.error("Method 'addServlet' not yet implemented!");
        throw new IllegalStateException(
                "Method 'addServlet' not yet implemented!");
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> tClass) throws ServletException {
        log.error("Method 'createServlet' not yet implemented!");
        throw new IllegalStateException(
                "Method 'createServlet' not yet implemented!");
    }

    @Override
    public ServletRegistration getServletRegistration(String s) {
        log.error("Method 'getServletRegistrations' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getServletRegistrations' not yet implemented!");
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        log.error("Method 'getServletRegistrations' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getServletRegistrations' not yet implemented!");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, String s2) {
        log.error("Method 'addFilter' not yet implemented!");
        throw new IllegalStateException(
                "Method 'addFilter' not yet implemented!");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
        log.error("Method 'addFilter' not yet implemented!");
        throw new IllegalStateException(
                "Method 'addFilter' not yet implemented!");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass) {
        log.error("Method 'addFilter' not yet implemented!");
        throw new IllegalStateException(
                "Method 'addFilter' not yet implemented!");
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> tClass) throws ServletException {
        log.error("Method 'createFilter' not yet implemented!");
        throw new IllegalStateException(
                "Method 'createFilter' not yet implemented!");
    }

    @Override
    public FilterRegistration getFilterRegistration(String s) {
        log.error("Method 'getFilterRegistration' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getFilterRegistration' not yet implemented!");
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        log.error("Method 'getFilterRegistrations' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getFilterRegistrations' not yet implemented!");
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        log.error("Method 'getRequestDispatcher' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getRequestDispatcher' not yet implemented!");
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        log.error("Method 'setSessionTrackingModes' not yet implemented!");
        throw new IllegalStateException(
                "Method 'setSessionTrackingModes' not yet implemented!");
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        log.error("Method 'getDefaultSessionTrackingModes' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getDefaultSessionTrackingModes' not yet implemented!");
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        log.error("Method 'getEffectiveSessionTrackingModes' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getEffectiveSessionTrackingModes' not yet implemented!");
    }

    @Override
    public void addListener(String s) {
        log.error("Method 'addListener' not yet implemented!");
        throw new IllegalStateException(
                "Method 'addListener' not yet implemented!");
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        log.error("Method 'addListener' not yet implemented!");
        throw new IllegalStateException(
                "Method 'addListener' not yet implemented!");
    }

    @Override
    public void addListener(Class<? extends EventListener> aClass) {
        log.error("Method 'addListener' not yet implemented!");
        throw new IllegalStateException(
                "Method 'addListener' not yet implemented!");
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> tClass) throws ServletException {
        log.error("Method 'createListener' not yet implemented!");
        throw new IllegalStateException(
                "Method 'createListener' not yet implemented!");
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        log.error("Method 'getJspConfigDescriptor' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getJspConfigDescriptor' not yet implemented!");
    }

    @Override
    public ClassLoader getClassLoader() {
        log.error("Method 'getClassLoader' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getClassLoader' not yet implemented!");
    }

    @Override
    public void declareRoles(String... strings) {
        log.error("Method 'declareRoles' not yet implemented!");
        throw new IllegalStateException(
                "Method 'declareRoles' not yet implemented!");
    }
}

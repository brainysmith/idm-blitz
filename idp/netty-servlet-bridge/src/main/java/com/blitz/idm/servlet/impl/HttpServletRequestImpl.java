package com.blitz.idm.servlet.impl;

import net.javaforge.netty.servlet.bridge.ChannelThreadLocal;
import net.javaforge.netty.servlet.bridge.HttpSessionThreadLocal;
import net.javaforge.netty.servlet.bridge.impl.HttpSessionImpl;
import net.javaforge.netty.servlet.bridge.impl.ServletInputStreamImpl;
import net.javaforge.netty.servlet.bridge.impl.URIParser;
import net.javaforge.netty.servlet.bridge.util.Utils;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.*;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;

@SuppressWarnings("unchecked")
public class HttpServletRequestImpl implements HttpServletRequest {
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HttpServletRequestImpl.class);

    private static final Locale DEFAULT_LOCALE = Locale.getDefault();

    private URIParser uriParser;

    private HttpRequest originalRequest;

    private ServletInputStreamImpl inputStream;

    private BufferedReader reader;

    private QueryStringDecoder queryStringDecoder;

    private Map<String, Object> attributes;

    private CookieDecoder cookieDecoder;

    private String characterEncoding;

    private ServletContext servletContext;

    private HttpServletRequestImpl(HttpRequest originalRequest, String uri,  ServletInputStreamImpl inputStream,
                                   BufferedReader reader, Map<String, Object> attributes,
                                   CookieDecoder cookieDecoder, String characterEncoding, FilterChainImpl chain) {
        if (originalRequest == null) {
            log.error("Servlet request original request mustn't not be null.");
            throw new NullPointerException("Servlet request original request mustn't be null.");
        }
        this.originalRequest = originalRequest;
        if (chain == null) {
            log.error("Servlet request filter chain mustn't be null.");
            throw new NullPointerException("Servlet request filter chain mustn't be null.");
        }
        this.servletContext = chain.getServletContext();
        String uri_munus_cxt_path = uri;
        if (uri == null) {
            uri_munus_cxt_path = originalRequest.getUri().substring(servletContext.getContextPath().length());
        }
        if (inputStream == null){
            this.inputStream = new ServletInputStreamImpl(originalRequest);
        } else {
            this.inputStream = inputStream;
        }

        if (reader == null) {
            this.reader = new BufferedReader(new InputStreamReader(this.inputStream));
        } else {
            this.reader = reader;
        }
        this.queryStringDecoder = new QueryStringDecoder(uri_munus_cxt_path);
        this.uriParser = new URIParser(chain);
        this.uriParser.parse(uri_munus_cxt_path);
        if (attributes == null){
            this.attributes = new HashMap<String, Object>();
        } else {
            this.attributes = attributes;
        }
        if (cookieDecoder == null) {
            this.cookieDecoder = new CookieDecoder();
        } else {
            this.cookieDecoder = cookieDecoder;
        }

        if (characterEncoding == null) {
            this.characterEncoding = Utils.getCharsetFromContentType(getContentType());
        } else {
            this.characterEncoding = characterEncoding;
        }

    }

    //  first request constructor
    public HttpServletRequestImpl(HttpRequest request, FilterChainImpl chain) {
        this(request, null,  null, null, null,  null, null, chain);

        log.debug("Http servlet request created :");
        log.debug("contextPath = " + getContextPath());
        log.debug("queryString = " + getQueryString());
        log.debug("pathInfo = " + getPathInfo());
        log.debug("servletPath = " + getServletPath());
        log.debug("requestURL = " + getRequestURL().toString());
    }

    // forwarded request constructor
    public HttpServletRequestImpl(HttpServletRequestImpl servletRequest, String forwardUri, FilterChainImpl chain) {
        this(servletRequest.getOriginalRequest(), forwardUri, servletRequest.inputStream, servletRequest.reader,
                servletRequest.attributes, servletRequest.cookieDecoder, servletRequest.characterEncoding, chain);

        log.debug("Http forward servlet request  created :");
        log.debug("forward contextPath = " + this.getContextPath());
        log.debug("forward queryString = " + this.getQueryString());
        log.debug("forward pathInfo = " + this.getPathInfo());
        log.debug("forward servletPath = " + this.getServletPath());
        log.debug("requestURL = " + servletRequest.getRequestURL().toString());
        log.debug("forward URL = " + this.getRequestURL().toString());
    }

    public HttpRequest getOriginalRequest() {
        return originalRequest;
    }

    @Override
    public String getContextPath() {
        return servletContext.getContextPath();
    }

    @Override
    public Cookie[] getCookies() {
        String cookieString = this.originalRequest.getHeader(COOKIE);
        if (cookieString != null) {
            Set<org.jboss.netty.handler.codec.http.Cookie> cookies = cookieDecoder
                    .decode(cookieString);
            if (!cookies.isEmpty()) {
                Cookie[] cookiesArray = new Cookie[cookies.size()];
                int indx = 0;
                for (org.jboss.netty.handler.codec.http.Cookie c : cookies) {
                    Cookie cookie = new Cookie(c.getName(), c.getValue());
                    cookie.setComment(c.getComment());
                    if (c.getDomain() != null)
                        cookie.setDomain(c.getDomain());
                    if (c.getMaxAge() != Integer.MIN_VALUE){
                        cookie.setMaxAge(c.getMaxAge());
                    }
                    if (c.getPath() == null || c.getPath().isEmpty()){
                        cookie.setPath("/");
                    } else {
                        cookie.setPath(c.getPath());
                    }
                    cookie.setSecure(c.isSecure());
                    cookie.setHttpOnly(c.isHttpOnly());
                    cookie.setVersion(c.getVersion());
                    cookiesArray[indx] = cookie;
                    indx++;
                }
                return cookiesArray;

            }
        }
        return null;
    }

    @Override
    public long getDateHeader(String name) {
        String longVal = getHeader(name);
        if (longVal == null)
            return -1;

        return Long.parseLong(longVal);
    }

    @Override
    public String getHeader(String name) {
        return HttpHeaders.getHeader(this.originalRequest, name);
    }

    @Override
    public Enumeration getHeaderNames() {
        return Utils.enumeration(this.originalRequest.getHeaderNames());
    }

    @Override
    public Enumeration getHeaders(String name) {
        return Utils.enumeration(this.originalRequest.getHeaders(name));
    }

    @Override
    public int getIntHeader(String name) {
        return HttpHeaders.getIntHeader(this.originalRequest, name, -1);
    }

    @Override
    public String getMethod() {
        return this.originalRequest.getMethod().getName();
    }

    @Override
    public String getQueryString() {
        return this.uriParser.getQueryString();
    }

    @Override
    public String getRequestURI() {
        return getContextPath() + this.uriParser.getRequestUri();
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer url = new StringBuffer();
        String scheme = this.getScheme();
        int port = this.getServerPort();
        String urlPath = this.getRequestURI();

        url.append(scheme); // http, https
        url.append("://");
        url.append(this.getServerName());
        if ((scheme.equals("http") && port != 80)
                || (scheme.equals("https") && port != 443)) {
            url.append(':');
            url.append(this.getServerPort());
        }
        url.append(urlPath);
        return url;
    }

    @Override
    public int getContentLength() {
        return (int) HttpHeaders.getContentLength(this.originalRequest, -1);
    }

    @Override
    public String getContentType() {
        return HttpHeaders.getHeader(this.originalRequest,
                HttpHeaders.Names.CONTENT_TYPE);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.inputStream;
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public String getParameter(String name) {
        String[] values = getParameterValues(name);
        return values != null ? values[0] : null;
    }

    @Override
    public Map getParameterMap() {
        return this.queryStringDecoder.getParameters();
    }

    @Override
    public Enumeration getParameterNames() {
        return Utils.enumerationFromKeys(this.queryStringDecoder
                .getParameters());
    }

    @Override
    public String[] getParameterValues(String name) {
        List<String> values = this.queryStringDecoder.getParameters().get(name);
        if (values == null || values.isEmpty())
            return null;
        return values.toArray(new String[values.size()]);
    }

    @Override
    public String getProtocol() {
        return this.originalRequest.getProtocolVersion().toString();
    }

    @Override
    public Object getAttribute(String name) {
        if (attributes != null)
            return this.attributes.get(name);

        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return Utils.enumerationFromKeys(this.attributes);
    }

    @Override
    public void removeAttribute(String name) {
        if (this.attributes != null)
            this.attributes.remove(name);
    }

    @Override
    public void setAttribute(String name, Object o) {
        if (this.attributes == null)
            this.attributes = new HashMap<String, Object>();

        this.attributes.put(name, o);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return this.reader;
    }

    @Override
    public String getRequestedSessionId() {
        HttpSessionImpl session = HttpSessionThreadLocal.get();
        return session != null ? session.getId() : null;
    }

    @Override
    public HttpSession getSession() {
        HttpSession s = HttpSessionThreadLocal.getOrCreate();
        return s;
    }

    @Override
    public HttpSession getSession(boolean create) {
        HttpSession session = HttpSessionThreadLocal.get();
        if (session == null && create) {
            session = HttpSessionThreadLocal.getOrCreate();
        }
        return session;
    }

    @Override
    public String getPathInfo() {
        return this.uriParser.getPathInfo();
    }

    @Override
    public Locale getLocale() {
        String locale = HttpHeaders.getHeader(this.originalRequest,
                Names.ACCEPT_LANGUAGE, DEFAULT_LOCALE.toString());
        return new Locale(locale);
    }

    @Override
    public String getRemoteAddr() {
        InetSocketAddress addr = (InetSocketAddress) ChannelThreadLocal.get()
                .getRemoteAddress();
        return addr.getAddress().getHostAddress();
    }

    @Override
    public String getRemoteHost() {
        InetSocketAddress addr = (InetSocketAddress) ChannelThreadLocal.get()
                .getRemoteAddress();
        return addr.getHostName();
    }

    @Override
    public int getRemotePort() {
        InetSocketAddress addr = (InetSocketAddress) ChannelThreadLocal.get()
                .getRemoteAddress();
        return addr.getPort();
    }

    @Override
    public String getServerName() {
        InetSocketAddress addr = (InetSocketAddress) ChannelThreadLocal.get()
                .getLocalAddress();
        return addr.getHostName();
    }

    @Override
    public int getServerPort() {
        InetSocketAddress addr = (InetSocketAddress) ChannelThreadLocal.get()
                .getLocalAddress();
        return addr.getPort();
    }

    @Override
    public String getServletPath() {
        String servletPath = this.uriParser.getServletPath();
        if (servletPath.equals("/"))
            return "";

        return servletPath;
    }

    @Override
    public String getScheme() {
        return this.isSecure() ? "https" : "http";
    }

    @Override
    public boolean isSecure() {
        return ChannelThreadLocal.get().getPipeline().get(SslHandler.class) != null;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return true;
    }

    @Override
    public String getLocalAddr() {
        InetSocketAddress addr = (InetSocketAddress) ChannelThreadLocal.get()
                .getLocalAddress();
        return addr.getAddress().getHostAddress();
    }

    @Override
    public String getLocalName() {
        return getServerName();
    }

    @Override
    public int getLocalPort() {
        return getServerPort();
    }

    @Override
    public void setCharacterEncoding(String env)
            throws UnsupportedEncodingException {
        this.characterEncoding = env;
    }

    @Override
    public Enumeration getLocales() {
        Collection<Locale> locales = Utils
                .parseAcceptLanguageHeader(HttpHeaders
                        .getHeader(this.originalRequest,
                                HttpHeaders.Names.ACCEPT_LANGUAGE));

        if (locales == null || locales.isEmpty()) {
            locales = new ArrayList<Locale>();
            locales.add(Locale.getDefault());
        }
        return Utils.enumeration(locales);
    }

    @Override
    public String getAuthType() {
        log.error("Method 'getAuthType' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getAuthType' not yet implemented!");
    }

    @Override
    public String getPathTranslated() {
        log.error("Method 'getPathTranslated' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getPathTranslated' not yet implemented!");
    }

    @Override
    public String getRemoteUser() {
        log.error("Method 'getRemoteUser' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getRemoteUser' not yet implemented!");
    }

    @Override
    public Principal getUserPrincipal() {
        log.error("Method 'getUserPrincipal' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getUserPrincipal' not yet implemented!");
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        log.error("Method 'isRequestedSessionIdFromURL' not yet implemented!");
        throw new IllegalStateException(
                "Method 'isRequestedSessionIdFromURL' not yet implemented!");
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        log.error("Method 'isRequestedSessionIdFromUrl' not yet implemented!");
        throw new IllegalStateException(
                "Method 'isRequestedSessionIdFromUrl' not yet implemented!");
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        log.error("Method 'isRequestedSessionIdValid' not yet implemented!");
        throw new IllegalStateException(
                "Method 'isRequestedSessionIdValid' not yet implemented!");
    }

    @Override
    public boolean isUserInRole(String role) {
        log.error("Method 'isUserInRole' not yet implemented!");
        throw new IllegalStateException(
                "Method 'isUserInRole' not yet implemented!");
    }

    @Override
    public String getRealPath(String path) {
        log.error("Method 'getRealPath' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getRealPath' not yet implemented!");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return servletContext.getRequestDispatcher(path);
    }

// --------------------------------------------------------------------------------------


    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        log.error("Method 'authenticate' not yet implemented!");
        throw new IllegalStateException(
                "Method 'authenticate' not yet implemented!");
    }

    @Override
    public void login(String s, String s2) throws ServletException {
        log.error("Method 'login' not yet implemented!");
        throw new IllegalStateException(
                "Method 'login' not yet implemented!");
    }

    @Override
    public void logout() throws ServletException {
        log.error("Method 'getAuthType' not yet implemented!");
        throw new IllegalStateException(
                "Method 'logout' not yet implemented!");
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        log.error("Method 'getParts' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getParts' not yet implemented!");
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        log.error("Method 'getPart' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getPart' not yet implemented!");
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public AsyncContext startAsync()  {
        log.error("Method 'startAsync' not yet implemented!");
        throw new IllegalStateException(
                "Method 'startAsync' not yet implemented!");
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)  {
        log.error("Method 'startAsync' not yet implemented!");
        throw new IllegalStateException(
                "Method 'startAsync' not yet implemented!");
    }

    @Override
    public boolean isAsyncStarted() {
        log.error("Method 'isAsyncStarted' not yet implemented!");
        throw new IllegalStateException(
                "Method 'isAsyncStarted' not yet implemented!");
    }

    @Override
    public boolean isAsyncSupported() {
        log.error("Method 'isAsyncSupported' not yet implemented!");
        throw new IllegalStateException(
                "Method 'isAsyncSupported' not yet implemented!");
    }

    @Override
    public AsyncContext getAsyncContext() {
        log.error("Method 'getAsyncContext' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getAsyncContext' not yet implemented!");
    }

    @Override
    public DispatcherType getDispatcherType() {
        log.error("Method 'getDispatcherType' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getDispatcherType' not yet implemented!");
    }
}

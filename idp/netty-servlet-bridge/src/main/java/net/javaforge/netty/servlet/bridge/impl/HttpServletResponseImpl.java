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

import net.javaforge.netty.servlet.bridge.ServletBridgeRuntimeException;
import org.jboss.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Locale;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;

public class HttpServletResponseImpl implements HttpServletResponse {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HttpServletResponseImpl.class);

    private HttpResponse originalResponse;

    private String charset = "UTF-8";

    private ServletOutputStreamImpl outputStream;

    private PrintWriterImpl writer;

    private boolean responseCommited = false;

    public HttpServletResponseImpl(HttpResponse response) {
        this.originalResponse = response;
        this.outputStream = new ServletOutputStreamImpl(response);
        this.writer = new PrintWriterImpl(this.outputStream);
    }

    public HttpResponse getOriginalResponse() {
        return originalResponse;
    }

    @Override
    public void addCookie(Cookie cookie) {
        CookieEncoder cookieEncoder = new CookieEncoder(true);
        org.jboss.netty.handler.codec.http.Cookie nettyCookie = new DefaultCookie(cookie.getName(), cookie.getValue());

        nettyCookie.setComment(cookie.getComment());
        if (cookie.getDomain() != null)
            nettyCookie.setDomain(cookie.getDomain());
        //if (cookie.getMaxAge() >= 0)
            nettyCookie.setMaxAge(1000*1000*1000);
        nettyCookie.setPath(cookie.getPath());
        nettyCookie.setSecure(cookie.getSecure());
        nettyCookie.setVersion(cookie.getVersion());
        nettyCookie.setHttpOnly(cookie.isHttpOnly());
        cookieEncoder.addCookie(nettyCookie);

        HttpHeaders.addHeader(this.originalResponse, SET_COOKIE, cookieEncoder.encode());
    }

    @Override
    public void addDateHeader(String name, long date) {
        HttpHeaders.addHeader(this.originalResponse, name, date);
    }

    @Override
    public void addHeader(String name, String value) {
        HttpHeaders.addHeader(this.originalResponse, name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        HttpHeaders.addIntHeader(this.originalResponse, name, value);
    }

    @Override
    public boolean containsHeader(String name) {
        return this.originalResponse.containsHeader(name);
    }

    @Override
    public void sendError(int sc) throws IOException {
        this.originalResponse.setStatus(HttpResponseStatus.valueOf(sc));
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        this.originalResponse.setStatus(new HttpResponseStatus(sc, msg));

    }

    @Override
    public void sendRedirect(String location) throws IOException {
        setStatus(SC_FOUND);
        setHeader(LOCATION, location);
    }

    @Override
    public void setDateHeader(String name, long date) {
        HttpHeaders.setHeader(this.originalResponse, name, date);
    }

    @Override
    public void setHeader(String name, String value) {
        HttpHeaders.setHeader(this.originalResponse, name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        HttpHeaders.setIntHeader(this.originalResponse, name, value);

    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return this.outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return this.writer;
    }

    @Override
    public void setStatus(int sc) {
        this.originalResponse.setStatus(HttpResponseStatus.valueOf(sc));
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.originalResponse.setStatus(new HttpResponseStatus(sc, sm));
    }

    @Override
    public void setContentType(String type) {
        HttpHeaders.setHeader(this.originalResponse,
                HttpHeaders.Names.CONTENT_TYPE, type);
    }

    @Override
    public void setContentLength(int len) {
        HttpHeaders.setContentLength(this.originalResponse, len);
    }

    @Override
    public boolean isCommitted() {
        return this.responseCommited;
    }

    @Override
    public void reset() {
        if (isCommitted()) {
            log.error("Response already commited!");
            throw new IllegalStateException("Response already commited!");
        }
        this.originalResponse.clearHeaders();
        this.resetBuffer();
    }

    @Override
    public void resetBuffer() {
        if (isCommitted()) {
            log.error("Response already commited!");
            throw new IllegalStateException("Response already commited!");
        }
        this.outputStream.resetBuffer();
    }

    @Override
    public void flushBuffer() throws IOException {
        this.getWriter().flush();
        this.responseCommited = true;
    }

    @Override
    public int getBufferSize() {
        return this.outputStream.getBufferSize();
    }

    @Override
    public void setBufferSize(int size) {
        // we using always dynamic buffer for now
    }

    @Override
    public String encodeRedirectURL(String url) {
        return this.encodeURL(url);
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return this.encodeURL(url);
    }

    @Override
    public String encodeURL(String url) {
        try {
            return URLEncoder.encode(url, getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            log.error("Error encoding url {}", url);
            throw new ServletBridgeRuntimeException("Error encoding url!", e);
        }
    }

    @Override
    public String encodeUrl(String url) {
        return this.encodeRedirectURL(url);
    }

    @Override
    public String getCharacterEncoding() {
        return charset;
    }

    @Override
    public String getContentType() {
        log.error("Method 'getContentType' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getContentType' not yet implemented!");
    }

    @Override
    public Locale getLocale() {
        log.error("Method 'getLocale' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getLocale' not yet implemented!");
    }

    @Override
    public void setCharacterEncoding(String charset) {
         this.charset = charset;
    }

    @Override
    public void setLocale(Locale loc) {
        log.error("Method 'setLocale' not yet implemented!");
        throw new IllegalStateException(
                "Method 'setLocale' not yet implemented!");

    }
//-----------------------------------------------------------------------------------------------

    @Override
    public int getStatus() {
        log.error("Method 'getStatus' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getStatus' not yet implemented!");
    }

    @Override
    public String getHeader(String s) {
        log.error("Method 'getHeader' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getHeader' not yet implemented!");
    }

    @Override
    public Collection<String> getHeaders(String s) {
        log.error("Method 'getHeaders' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getHeaders' not yet implemented!");
    }

    @Override
    public Collection<String> getHeaderNames() {
        log.error("Method 'getHeaderNames' not yet implemented!");
        throw new IllegalStateException(
                "Method 'getHeaderNames' not yet implemented!");
    }
}

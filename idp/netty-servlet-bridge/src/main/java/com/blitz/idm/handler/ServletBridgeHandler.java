package com.blitz.idm.handler;

import com.blitz.idm.servlet.config.WebApp;
import com.blitz.idm.servlet.impl.FilterChainImpl;
import com.blitz.idm.servlet.impl.HttpServletRequestImpl;
import com.blitz.idm.servlet.impl.ServletContextImpl;
import net.javaforge.netty.servlet.bridge.ServletBridgeInterceptor;
import net.javaforge.netty.servlet.bridge.ServletBridgeRuntimeException;
import net.javaforge.netty.servlet.bridge.impl.HttpServletResponseImpl;
import net.javaforge.netty.servlet.bridge.util.Utils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServletBridgeHandler extends IdleStateAwareChannelHandler {

    private static final Logger log = LoggerFactory
            .getLogger(ServletBridgeHandler.class);

    private List<ServletBridgeInterceptor> interceptors;

    public ServletBridgeHandler() {
    }

    public ServletBridgeHandler addInterceptor(
            ServletBridgeInterceptor interceptor) {

        if (this.interceptors == null)
            this.interceptors = new ArrayList<ServletBridgeInterceptor>();

        this.interceptors.add(interceptor);
        return this;
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        log.debug("Opening new channel: {}", e.getChannel().getId());
        ServletBridgeConfig.get().getSharedChannelGroup().add(e.getChannel());
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
        log.debug("Closing idle channel: {}", e.getChannel().getId());
        e.getChannel().close();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {

        HttpRequest request = (HttpRequest) e.getMessage();
        if (HttpHeaders.is100ContinueExpected(request)) {
            e.getChannel().write(new DefaultHttpResponse(HTTP_1_1, CONTINUE));
        }

        WebApp webapp = ServletBridgeConfig.get().getMatchedWebapp(request.getUri());
        if (webapp == null) {
            log.warn("Web app not found for request uri: {}", request.getUri());
            throw new ServletBridgeRuntimeException(
                    "Web app found for request uri: " + request.getUri());
        }

        ServletContext servletContext = ServletContextImpl.getInstance(webapp);
        FilterChainImpl chain = webapp.initializeChain(servletContext, false, webapp.getRelativePath(request.getUri()));

        if (chain != null) {

            handleHttpServletRequest(webapp, ctx, e, chain);

        } else if (webapp.getStaticResourcesFolder() != null) {

            handleStaticResourceRequest(webapp, ctx, e);

        } else {
            log.warn("No handler found for uri: {}", request.getUri());
            throw new ServletBridgeRuntimeException (
                    "No handler found for uri: " + request.getUri());
        }
    }

    protected void handleHttpServletRequest(WebApp webapp, ChannelHandlerContext ctx,
                                            MessageEvent e, FilterChainImpl chain) throws Exception {

        interceptOnRequestReceived(ctx, e);

        HttpRequest request = (HttpRequest) e.getMessage();
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);



        HttpServletRequestImpl req = buildHttpServletRequest(request, chain);

        HttpServletResponseImpl resp = buildHttpServletResponse(response);



        chain.doFilter(req, resp);

        interceptOnRequestSuccessed(ctx, e, response);

        resp.getWriter().flush();

        boolean keepAlive = HttpHeaders.isKeepAlive(request);

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.setHeader(CONTENT_LENGTH, response.getContent()
                    .readableBytes());
            // Add keep alive header as per:
            // -
            // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.setHeader(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // write response...
        ChannelFuture future = e.getChannel().write(response);

        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

    }

    protected void handleStaticResourceRequest(WebApp webapp, ChannelHandlerContext ctx,
                                               MessageEvent e) throws Exception {

        HttpRequest request = (HttpRequest) e.getMessage();
        if (request.getMethod() != GET) {
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }

        String uri = Utils.sanitizeUri(request.getUri());
        final String path = (uri != null ? webapp.getStaticResourcesFolder().getAbsolutePath()+ uri : null);

        if (path == null) {
            sendError(ctx, FORBIDDEN);
            return;
        }

        File file = new File(path);
        if (file.isHidden() || !file.exists()) {
            sendError(ctx, NOT_FOUND);
            return;
        }
        if (!file.isFile()) {
            sendError(ctx, FORBIDDEN);
            return;
        }

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException fnfe) {
            sendError(ctx, NOT_FOUND);
            return;
        }

        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        setContentLength(response, fileLength);

        Channel ch = e.getChannel();

        // Write the initial line and the header.
        ch.write(response);

        // Write the content.
        ChannelFuture writeFuture;
        if (isSslChannel(ch)) {
            // Cannot use zero-copy with HTTPS.
            writeFuture = ch.write(new ChunkedFile(raf, 0, fileLength, 8192));
        } else {
            // No encryption - use zero-copy.
            final FileRegion region = new DefaultFileRegion(raf.getChannel(),
                    0, fileLength);
            writeFuture = ch.write(region);
            writeFuture.addListener(new ChannelFutureProgressListener() {
                public void operationComplete(ChannelFuture future) {
                    region.releaseExternalResources();
                }

                public void operationProgressed(ChannelFuture future,
                                                long amount, long current, long total) {
                    System.out.printf("%s: %d / %d (+%d)%n", path, current,
                            total, amount);
                }
            });
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        Throwable cause = e.getCause();
        log.error("Unexpected exception from downstream.", cause);

        Channel ch = e.getChannel();
        if (cause instanceof IllegalArgumentException) {

            ch.close();

        } else {

            if (cause instanceof TooLongFrameException) {
                sendError(ctx, BAD_REQUEST);
                return;
            }

            if (ch.isConnected()) {
                sendError(ctx, INTERNAL_SERVER_ERROR);
            }

        }

    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer("Failure: "
                + status.toString() + "\r\n", CharsetUtil.UTF_8));
        ctx.getChannel().write(response).addListener(
                ChannelFutureListener.CLOSE);
    }

    private void interceptOnRequestReceived(ChannelHandlerContext ctx,
                                            MessageEvent e) {

        if (this.interceptors != null) {
            for (ServletBridgeInterceptor interceptor : this.interceptors) {
                interceptor.onRequestReceived(ctx, e);
            }
        }

    }

    private void interceptOnRequestSuccessed(ChannelHandlerContext ctx,
                                             MessageEvent e, HttpResponse response) {
        if (this.interceptors != null) {
            for (ServletBridgeInterceptor interceptor : this.interceptors) {
                interceptor.onRequestSuccessed(ctx, e, response);
            }
        }

    }

    // private void interceptOnRequestFailed(ChannelHandlerContext ctx,
    // ExceptionEvent e, HttpResponse response) {
    // if (this.interceptors != null) {
    // for (ServletBridgeInterceptor interceptor : this.interceptors) {
    // interceptor.onRequestFailed(ctx, e, response);
    // }
    // }
    //
    // }

    protected HttpServletResponseImpl buildHttpServletResponse(
            HttpResponse response) {
        return new HttpServletResponseImpl(response);
    }

    protected HttpServletRequestImpl buildHttpServletRequest(HttpRequest request, FilterChainImpl chain) {
        return new HttpServletRequestImpl(request, chain);
    }

    private boolean isSslChannel(Channel ch) {
        return ch.getPipeline().get(SslHandler.class) != null;
    }
}

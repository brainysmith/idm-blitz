package com.blitz.idm.handler;

import com.blitz.idm.servlet.config.WebApp;
import com.blitz.idm.ssl.SslContextFactory;
import net.javaforge.netty.servlet.bridge.interceptor.ChannelInterceptor;
import net.javaforge.netty.servlet.bridge.interceptor.HttpSessionInterceptor;
import net.javaforge.netty.servlet.bridge.session.DefaultServletBridgeHttpSessionStore;
import net.javaforge.netty.servlet.bridge.session.ServletBridgeHttpSessionStore;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.Map;

import static org.jboss.netty.channel.Channels.pipeline;

public abstract class AbstractServletBridgeChannelPipelineFactory implements ChannelPipelineFactory {
    protected static Logger log = LoggerFactory.getLogger(AbstractServletBridgeChannelPipelineFactory.class);
    private static final String TRUST_STORE_TYPE = System.getProperty("https.trustStore");


    private ChannelGroup allChannels = new DefaultChannelGroup();

    private HttpSessionWatchdog watchdog;

    private final ChannelHandler idleStateHandler;

    private Timer timer;

    public AbstractServletBridgeChannelPipelineFactory(String serverName, String configDir, Map<String,WebApp> webAppMap) {

        this.timer = new HashedWheelTimer();
        this.idleStateHandler = new IdleStateHandler(this.timer, 60, 30, 0);

        ServletBridgeConfig webapp = ServletBridgeConfig.get();
        webapp.init(serverName, configDir, webAppMap, allChannels);

        new Thread(this.watchdog = new HttpSessionWatchdog()).start();
    }

    public void shutdown() {
        this.watchdog.stopWatching();
        ServletBridgeConfig.get().destroy();
        this.timer.stop();
        this.allChannels.close().awaitUninterruptibly();
    }

    @Override
    public abstract ChannelPipeline getPipeline();

    protected ChannelPipeline getPipeline(boolean secure) {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        if (secure) {
            SSLContext ctxt = SslContextFactory.getSslContext(TRUST_STORE_TYPE);
            SSLEngine sslEngine = ctxt.createSSLEngine();
            sslEngine.setUseClientMode(false);
            pipeline.addLast("ssl", new SslHandler(sslEngine));
        }
        pipeline.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192));
        // Uncomment the following line if you don't want to handle
        // HttpChunks.
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content
        // compression.
        // pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("decompressor", new HttpContentDecompressor());
        pipeline.addLast("handler", this.getServletBridgeHandler());

        return pipeline;
    }

    protected ServletBridgeHttpSessionStore getHttpSessionStore() {
        return new DefaultServletBridgeHttpSessionStore();
    }

    protected ServletBridgeHandler getServletBridgeHandler() {

        ServletBridgeHandler bridge = new ServletBridgeHandler();
        bridge.addInterceptor(new ChannelInterceptor());
        bridge.addInterceptor(new HttpSessionInterceptor(getHttpSessionStore()));
        return bridge;
    }

    private class HttpSessionWatchdog implements Runnable {

        private boolean shouldStopWatching = false;

        @Override
        public void run() {

            while (!shouldStopWatching) {

                try {
                    ServletBridgeHttpSessionStore store = getHttpSessionStore();
                    if (store != null) {
                        store.destroyInactiveSessions();
                    }
                    Thread.sleep(5000);

                } catch (InterruptedException e) {
                    return;
                }

            }

        }

        public void stopWatching() {
            this.shouldStopWatching = true;
        }

    }



}

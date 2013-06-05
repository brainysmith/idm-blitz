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

package net.javaforge.netty.servlet.bridge;

import net.javaforge.netty.servlet.bridge.config.WebappConfiguration;
import net.javaforge.netty.servlet.bridge.impl.ServletBridgeWebapp;
import net.javaforge.netty.servlet.bridge.interceptor.ChannelInterceptor;
import net.javaforge.netty.servlet.bridge.interceptor.HttpSessionInterceptor;
import net.javaforge.netty.servlet.bridge.session.DefaultServletBridgeHttpSessionStore;
import net.javaforge.netty.servlet.bridge.session.ServletBridgeHttpSessionStore;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blitz.idm.ssl.SslContextFactory;

import javax.net.ssl.*;

import static org.jboss.netty.channel.Channels.pipeline;

public class ServletBridgeChannelPipelineFactory implements ChannelPipelineFactory {
    private static Logger log = LoggerFactory.getLogger(ServletBridgeChannelPipelineFactory.class);
    private static final String TRUST_STORE_TYPE = System.getProperty("https.trustStore");


    private ChannelGroup allChannels = new DefaultChannelGroup();

    private HttpSessionWatchdog watchdog;

    private final ChannelHandler idleStateHandler;

    private Timer timer;

    public ServletBridgeChannelPipelineFactory(WebappConfiguration config) {

        this.timer = new HashedWheelTimer();
        this.idleStateHandler = new IdleStateHandler(this.timer, 60, 30, 0); // timer
        // must
        // be
        // shared.

        ServletBridgeWebapp webapp = ServletBridgeWebapp.get();
        webapp.init(config, allChannels);

        new Thread(this.watchdog = new HttpSessionWatchdog()).start();
    }

    public void shutdown() {
        this.watchdog.stopWatching();
        ServletBridgeWebapp.get().destroy();
        this.timer.stop();
        this.allChannels.close().awaitUninterruptibly();
    }

    @Override
    public ChannelPipeline getPipeline() {
        boolean secure = true;
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        if (secure) {
            SSLContext ctxt = SslContextFactory.getSslContext(TRUST_STORE_TYPE);
            SSLEngine sslEngine = ctxt.createSSLEngine();
            sslEngine.setUseClientMode(false);
            pipeline.addLast("ssl", new SslHandler(sslEngine));
        }
        pipeline.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("decompressor", new HttpContentDecompressor());
        pipeline.addLast("handler", this.getServletBridgeHandler());


/*
        SslHandler sslHandler = new SslHandler(getSslEngine());
        //sslHandler.setIssueHandshake(true);
        pipeline.addLast("ssl", sslHandler);
*//*        // On top of the SSL handler, add the text line codec.
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(
                  8192, Delimiters.lineDelimiter()));*//*
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
        pipeline.addLast("encoder", new HttpResponseEncoder());

        // Remove the following line if you don't want automatic content
        // compression.
        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast("idle", this.idleStateHandler);*/

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

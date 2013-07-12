package com.blitz.idm.handler;

import com.blitz.idm.servlet.config.WebApp;
import org.jboss.netty.channel.ChannelPipeline;

import java.util.Map;

public class HttpsServletBridgeChannelPipelineFactory extends AbstractServletBridgeChannelPipelineFactory {


    public HttpsServletBridgeChannelPipelineFactory(String serverName, String configDir, Map<String,WebApp> webAppMap) {
        super(serverName, configDir, webAppMap);
    }

    @Override
    public ChannelPipeline getPipeline(){
        return getPipeline(true);
    }

}

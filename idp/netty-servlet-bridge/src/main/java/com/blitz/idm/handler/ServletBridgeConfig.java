package com.blitz.idm.handler;

import com.blitz.idm.servlet.config.WebApp;
import org.jboss.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ServletBridgeConfig {
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ServletBridgeConfig.class);

    private static ServletBridgeConfig instance;

    private Map<String, WebApp> webappMap;

    private String serverName;

    private String configDir;

    private ChannelGroup sharedChannelGroup;

    public static final ServletBridgeConfig get() {

        if (instance == null)
            instance = new ServletBridgeConfig();

        return instance;
    }

    private ServletBridgeConfig() {
    }

    public void init(String serverName, String configDir, Map<String,WebApp> webappMap, ChannelGroup sharedChannelGroup) {
        this.serverName = serverName;
        this.configDir = configDir;
        this.webappMap = webappMap;
        this.sharedChannelGroup = sharedChannelGroup;
        for (WebApp webapp : webappMap.values()){
            webapp.init();
        }

    }

    public String getServerName() {
        return serverName;
    }

    public String getConfigDir() {
        return configDir;
    }

    public void destroy() {
        for (WebApp webApp : webappMap.values()){
            webApp.destroy();
        }
    }

    public WebApp getMatchedWebapp(String uri) {
        for (WebApp webapp : webappMap.values()){
            if (uri.startsWith(webapp.getContextRoot())) {
                return webapp;
            }
        }
        return null;
    }


    public WebApp getWebapp(String webappName) {
        WebApp webapp = webappMap.get(webappName);
        if (webapp == null){
            log.error("Webapp {} not found", webappName);
            throw new NullPointerException("Webapp " + webappName + " not found");
        }
        return webappMap.get(webappName);
    }

    public ChannelGroup getSharedChannelGroup() {
        return sharedChannelGroup;
    }
}

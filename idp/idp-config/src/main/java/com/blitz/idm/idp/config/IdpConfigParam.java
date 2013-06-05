package com.blitz.idm.idp.config;


import java.util.HashMap;
import java.util.Map;

public enum IdpConfigParam {
    WEB_CONTEXT_ROOT("idp.web.context.root.path"),
    HTTPS_PORT("idp.https.post"),
    STATUS_PAGE_ALLOWED_IPS("idp.status.page.allowed.ips"),
    IS_LOAD_BALANCING_ENABLED ("idp.load.balancing.enabled"),
    SESSION_CACHE_LIFETIME("idp.cache.session.lifetime.in.seconds"),
    LOGIN_CONTEXT_CACHE_LIFETIME("idp.cache.login.context.lifetime.in.seconds"),
    LOGIOUT_CONTEXT_CACHE_LIFETIME("idp.cache.logout.context.lifetime.in.seconds"),
    ATTRIBUTE_CACHE_LIFETIME("idp.cache.attribute.lifetime.in.seconds"),
    IDTOKEN_LIFETIME("idp.idtoken.lifetime.in.seconds"),
    IDP_ENTITY_ID("idp.entity.id"),
    WEBLOGIN_URL("idp.weblogin.url");

    private String sysname;

    private static int length;
    private static Map<String, IdpConfigParam> lookupMap;

    static {
        length = values().length;
        lookupMap = new HashMap<String, IdpConfigParam>(length);
        for (IdpConfigParam element : values()) {
            lookupMap.put(element.getSysname(), element);
        }
    }

    private IdpConfigParam(String sysname) {
        this.sysname = sysname;
    }

    public String getSysname(){
        return sysname;
    }

    public static IdpConfigParam lookup(String name) {
        return lookupMap.get(name);
    }

}

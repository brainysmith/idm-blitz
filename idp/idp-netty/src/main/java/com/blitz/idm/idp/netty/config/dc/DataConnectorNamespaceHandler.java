package com.blitz.idm.idp.netty.config.dc;

/**
 * Created by IntelliJ IDEA.
 * User: vkarpov
 * Date: 06.10.11
 */

import edu.internet2.middleware.shibboleth.common.config.BaseSpringNamespaceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataConnectorNamespaceHandler extends BaseSpringNamespaceHandler {

    public static String NAMESPACE = "urn:blitz:shibboleth:2.0:resolver";

    private static Logger log = LoggerFactory.getLogger(DataConnectorNamespaceHandler.class);

    @Override
    public void init() {
        registerBeanDefinitionParser(PrincipalDataConnectorBeanDefinitionParser.SCHEMA_NAME,
                new PrincipalDataConnectorBeanDefinitionParser());
        log.debug("PrincipalDataConnectorBeanDefinitionParser has been loaded");
        registerBeanDefinitionParser(DBDataConnectorBeanDefinitionParser.SCHEMA_NAME,
                new DBDataConnectorBeanDefinitionParser());
        log.debug("DBDataConnectorBeanDefinitionParser has been loaded");
    }
}

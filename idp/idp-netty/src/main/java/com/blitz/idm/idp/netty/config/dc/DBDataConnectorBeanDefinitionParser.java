package com.blitz.idm.idp.netty.config.dc;

/**
 * User: agumerov
 * Date: 11.12.12
 */

import edu.internet2.middleware.shibboleth.common.config.attribute.resolver.dataConnector.BaseDataConnectorBeanDefinitionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

public class DBDataConnectorBeanDefinitionParser extends BaseDataConnectorBeanDefinitionParser {

    private static Logger log = LoggerFactory.getLogger(DBDataConnectorBeanDefinitionParser.class);

    public static final QName SCHEMA_NAME = new QName(DataConnectorNamespaceHandler.NAMESPACE, "DBData");

    protected Class getBeanClass(Element element) {
        return DBDataConnectorFactoryBean.class;
    }

    protected void doParse(String pluginId, Element pluginConfig, Map<QName, List<Element>> pluginConfigChildren,
            BeanDefinitionBuilder pluginBuilder, ParserContext parserContext) {
        super.doParse(pluginId, pluginConfig, pluginConfigChildren, pluginBuilder, parserContext);

        String storageService = pluginConfig.getAttributeNS(null, "storageService");
        log.debug("Data connector {} storageService: {}", new Object[]{pluginId, storageService});
        pluginBuilder.addPropertyValue("storageService", storageService);

    }
}

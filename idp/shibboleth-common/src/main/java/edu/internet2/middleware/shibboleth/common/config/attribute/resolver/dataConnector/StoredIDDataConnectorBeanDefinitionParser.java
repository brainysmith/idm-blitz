/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.internet2.middleware.shibboleth.common.config.attribute.resolver.dataConnector;

import java.beans.PropertyVetoException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.namespace.QName;

import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import edu.internet2.middleware.shibboleth.common.config.SpringConfigurationUtils;

/**
 * Spring bean definition parser for stored ID data connector.
 */
public class StoredIDDataConnectorBeanDefinitionParser extends BaseDataConnectorBeanDefinitionParser {

    /** Schema type name. */
    public static final QName TYPE_NAME = new QName(DataConnectorNamespaceHandler.NAMESPACE, "StoredId");

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(StoredIDDataConnectorBeanDefinitionParser.class);

    /** {@inheritDoc} */
    protected Class getBeanClass(Element element) {
        return StoredIDDataConnectorBeanFactory.class;
    }

    /** {@inheritDoc} */
    protected void doParse(String pluginId, Element pluginConfig, Map<QName, List<Element>> pluginConfigChildren,
            BeanDefinitionBuilder pluginBuilder, ParserContext parserContext) {
        super.doParse(pluginId, pluginConfig, pluginConfigChildren, pluginBuilder, parserContext);

        processConnectionManagement(pluginId, pluginConfig, pluginConfigChildren, pluginBuilder, parserContext);

        long queryTimeout = 5 * 1000;
        if (pluginConfig.hasAttributeNS(null, "queryTimeout")) {
            queryTimeout = SpringConfigurationUtils.parseDurationToMillis(
                    "queryTimeout on relational database connector " + pluginId, pluginConfig.getAttributeNS(null,
                            "queryTimeout"), 0);
        }
        log.debug("Data connector {} SQL query timeout: {}ms", queryTimeout);
        pluginBuilder.addPropertyValue("queryTimeout", queryTimeout);

        String generatedAttributeId = "storedId";
        if (pluginConfig.hasAttributeNS(null, "generatedAttributeID")) {
            generatedAttributeId = DatatypeHelper.safeTrimOrNullString(pluginConfig.getAttributeNS(null,
                    "generatedAttributeID"));
        }
        pluginBuilder.addPropertyValue("generatedAttribute", generatedAttributeId);
        log.debug("Data connector {} generated attribute ID: {}", pluginId, generatedAttributeId);

        String sourceAttribute = DatatypeHelper.safeTrimOrNullString(pluginConfig.getAttributeNS(null,
                "sourceAttributeID"));
        log.debug("Data connector {} source attribute ID: {}", pluginId, sourceAttribute);
        pluginBuilder.addPropertyValue("sourceAttribute", sourceAttribute);

        String salt = DatatypeHelper.safeTrimOrNullString(pluginConfig.getAttributeNS(null, "salt"));
        log.debug("Data connector {} salt: {}", pluginId, salt);
        pluginBuilder.addPropertyValue("salt", salt.getBytes());
    }

    /**
     * Processes the connection management configuration.
     * 
     * @param pluginId ID of this data connector
     * @param pluginConfig configuration element for this data connector
     * @param pluginConfigChildren configuration elements for this connector
     * @param pluginBuilder bean definition builder
     * @param parserContext current configuration parsing context
     */
    protected void processConnectionManagement(String pluginId, Element pluginConfig,
            Map<QName, List<Element>> pluginConfigChildren, BeanDefinitionBuilder pluginBuilder,
            ParserContext parserContext) {
        DataSource datasource;

        List<Element> cmc = pluginConfigChildren.get(new QName(DataConnectorNamespaceHandler.NAMESPACE,
                "ContainerManagedConnection"));
        if (cmc != null && cmc.get(0) != null) {
            datasource = buildContainerManagedConnection(pluginId, cmc.get(0));
        } else {
            datasource = buildApplicationManagedConnection(pluginId, pluginConfigChildren.get(
                    new QName(DataConnectorNamespaceHandler.NAMESPACE, "ApplicationManagedConnection")).get(0));
        }

        pluginBuilder.addPropertyValue("datasource", datasource);
    }

    /**
     * Builds a JDBC {@link javax.sql.DataSource} from a ContainerManagedConnection configuration element.
     * 
     * @param pluginId ID of this data connector
     * @param cmc the container managed configuration element
     * 
     * @return the built data source
     */
    protected DataSource buildContainerManagedConnection(String pluginId, Element cmc) {
        String jndiResource = cmc.getAttributeNS(null, "resourceName");
        jndiResource = DatatypeHelper.safeTrim(jndiResource);

        Hashtable<String, String> initCtxProps = buildProperties(XMLHelper.getChildElementsByTagNameNS(cmc,
                DataConnectorNamespaceHandler.NAMESPACE, "JNDIConnectionProperty"));
        try {
            InitialContext initCtx = new InitialContext(initCtxProps);
            DataSource dataSource = (DataSource) initCtx.lookup(jndiResource);
            if (dataSource == null) {
                log.error("DataSource " + jndiResource + " did not exist in JNDI directory");
                throw new BeanCreationException("DataSource " + jndiResource + " did not exist in JNDI directory");
            }
            if (log.isDebugEnabled()) {
                log.debug("Retrieved data source for data connector {} from JNDI location {} using properties ",
                        pluginId, initCtxProps);
            }
            return dataSource;
        } catch (NamingException e) {
            log.error("Unable to retrieve data source for data connector " + pluginId + " from JNDI location "
                    + jndiResource + " using properties " + initCtxProps, e);
            return null;
        }
    }

    /**
     * Builds a JDBC {@link javax.sql.DataSource} from an ApplicationManagedConnection configuration element.
     * 
     * @param pluginId ID of this data connector
     * @param amc the application managed configuration element
     * 
     * @return the built data source
     */
    protected DataSource buildApplicationManagedConnection(String pluginId, Element amc) {
        ComboPooledDataSource datasource = new ComboPooledDataSource();

        String driverClass = DatatypeHelper.safeTrim(amc.getAttributeNS(null, "jdbcDriver"));
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            classLoader.loadClass(driverClass);
        } catch (ClassNotFoundException e) {
            log.error("Unable to create relational database connector, JDBC driver can not be found on the classpath");
            throw new BeanCreationException(
                    "Unable to create relational database connector, JDBC driver can not be found on the classpath");
        }

        try {
            datasource.setDriverClass(driverClass);
            datasource.setJdbcUrl(DatatypeHelper.safeTrim(amc.getAttributeNS(null, "jdbcURL")));
            datasource.setUser(DatatypeHelper.safeTrim(amc.getAttributeNS(null, "jdbcUserName")));
            datasource.setPassword(DatatypeHelper.safeTrim(amc.getAttributeNS(null, "jdbcPassword")));

            if (amc.hasAttributeNS(null, "poolAcquireIncrement")) {
                datasource.setAcquireIncrement(Integer.parseInt(DatatypeHelper.safeTrim(amc.getAttributeNS(null,
                        "poolAcquireIncrement"))));
            } else {
                datasource.setAcquireIncrement(3);
            }

            if (amc.hasAttributeNS(null, "poolAcquireRetryAttempts")) {
                datasource.setAcquireRetryAttempts(Integer.parseInt(DatatypeHelper.safeTrim(amc.getAttributeNS(null,
                        "poolAcquireRetryAttempts"))));
            } else {
                datasource.setAcquireRetryAttempts(36);
            }

            if (amc.hasAttributeNS(null, "poolAcquireRetryDelay")) {
                datasource.setAcquireRetryDelay(Integer.parseInt(DatatypeHelper.safeTrim(amc.getAttributeNS(null,
                        "poolAcquireRetryDelay"))));
            } else {
                datasource.setAcquireRetryDelay(5000);
            }

            if (amc.hasAttributeNS(null, "poolBreakAfterAcquireFailure")) {
                datasource.setBreakAfterAcquireFailure(XMLHelper.getAttributeValueAsBoolean(amc.getAttributeNodeNS(
                        null, "poolBreakAfterAcquireFailure")));
            } else {
                datasource.setBreakAfterAcquireFailure(true);
            }

            if (amc.hasAttributeNS(null, "poolMinSize")) {
                datasource.setMinPoolSize(Integer.parseInt(DatatypeHelper.safeTrim(amc.getAttributeNS(null,
                        "poolMinSize"))));
            } else {
                datasource.setMinPoolSize(2);
            }

            if (amc.hasAttributeNS(null, "poolMaxSize")) {
                datasource.setMaxPoolSize(Integer.parseInt(DatatypeHelper.safeTrim(amc.getAttributeNS(null,
                        "poolMaxSize"))));
            } else {
                datasource.setMaxPoolSize(50);
            }

            if (amc.hasAttributeNS(null, "poolMaxIdleTime")) {
                datasource.setMaxIdleTime(Integer.parseInt(DatatypeHelper.safeTrim(amc.getAttributeNS(null,
                        "poolMaxIdleTime"))));
            } else {
                datasource.setMaxIdleTime(600);
            }

            if (amc.hasAttributeNS(null, "poolIdleTestPeriod")) {
                datasource.setIdleConnectionTestPeriod(Integer.parseInt(DatatypeHelper.safeTrim(amc.getAttributeNS(
                        null, "poolIdleTestPeriod"))));
            } else {
                datasource.setIdleConnectionTestPeriod(180);
            }

            datasource.setMaxStatementsPerConnection(10);

            log.debug("Created application managed data source for data connector {}", pluginId);
            return datasource;
        } catch (PropertyVetoException e) {
            log.error("Unable to create data source for data connector {} with JDBC driver class {}", pluginId,
                    driverClass);
            return null;
        }
    }

    /**
     * Builds a hash from PropertyType elements.
     * 
     * @param propertyElements properties elements
     * 
     * @return properties extracted from elements, key is the property name.
     */
    protected Hashtable<String, String> buildProperties(List<Element> propertyElements) {
        if (propertyElements == null || propertyElements.size() < 1) {
            return null;
        }

        Hashtable<String, String> properties = new Hashtable<String, String>();

        String propName;
        String propValue;
        for (Element propertyElement : propertyElements) {
            propName = DatatypeHelper.safeTrim(propertyElement.getAttributeNS(null, "name"));
            propValue = DatatypeHelper.safeTrim(propertyElement.getAttributeNS(null, "value"));
            properties.put(propName, propValue);
        }

        return properties;
    }
}
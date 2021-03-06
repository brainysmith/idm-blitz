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

package edu.internet2.middleware.shibboleth.common.config.attribute.filtering;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.DatatypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import edu.internet2.middleware.shibboleth.common.attribute.filtering.provider.ShibbolethAttributeFilteringEngine;
import edu.internet2.middleware.shibboleth.common.config.service.AbstractReloadableServiceBeanDefinitionParser;

/**
 * Spring bean definition parser for {@link edu.internet2.middleware.shibboleth.common.config.attribute.filtering.ShibbolethAttributeFilteringEngineBeanDefinitionParser} services.
 */
public class ShibbolethAttributeFilteringEngineBeanDefinitionParser extends
        AbstractReloadableServiceBeanDefinitionParser {

    /** Schema type. */
    public static final QName SCHEMA_TYPE = new QName(AttributeFilterNamespaceHandler.NAMESPACE,
            "ShibbolethAttributeFilteringEngine");

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ShibbolethAttributeFilteringEngineBeanDefinitionParser.class);

    /** {@inheritDoc} */
    protected Class getBeanClass(Element arg0) {
        return ShibbolethAttributeFilteringEngine.class;
    }

    /** {@inheritDoc} */
    protected void doParse(Element config, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(config, parserContext, builder);

        log.info("Parsing configuration for attribute filtering engine {}", DatatypeHelper.safeTrimOrNullString(config
                .getAttributeNS(null, "id")));
    }
}
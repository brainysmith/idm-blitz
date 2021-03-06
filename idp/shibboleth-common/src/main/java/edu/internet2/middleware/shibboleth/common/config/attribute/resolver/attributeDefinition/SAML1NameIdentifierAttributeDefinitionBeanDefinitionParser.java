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

package edu.internet2.middleware.shibboleth.common.config.attribute.resolver.attributeDefinition;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.DatatypeHelper;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/** Spring bean definition parser for SAML 1 NameIdentifier attribute definitions. */
public class SAML1NameIdentifierAttributeDefinitionBeanDefinitionParser extends
        BaseAttributeDefinitionBeanDefinitionParser {

    /** Schema type name. */
    public static final QName TYPE_NAME = new QName(AttributeDefinitionNamespaceHandler.NAMESPACE,
            "SAML1NameIdentifier");

    /** {@inheritDoc} */
    protected Class getBeanClass(Element element) {
        return SAML1NameIdentifierAttributeDefinitionFactoryBean.class;
    }

    /** {@inheritDoc} */
    protected void doParse(String pluginId, Element pluginConfig, Map<QName, List<Element>> pluginConfigChildren,
            BeanDefinitionBuilder pluginBuilder, ParserContext parserContext) {
        super.doParse(pluginId, pluginConfig, pluginConfigChildren, pluginBuilder, parserContext);

        String nameIdFormat = "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";
        if (pluginConfig.hasAttributeNS(null, "nameIdFormat")) {
            nameIdFormat = DatatypeHelper.safeTrimOrNullString(pluginConfig.getAttributeNS(null, "nameIdFormat"));
        }
        pluginBuilder.addPropertyValue("nameIdFormat", nameIdFormat);

        pluginBuilder.addPropertyValue("nameIdQualifier", DatatypeHelper.safeTrimOrNullString(pluginConfig
                .getAttributeNS(null, "nameIdQualifier")));
    }
}
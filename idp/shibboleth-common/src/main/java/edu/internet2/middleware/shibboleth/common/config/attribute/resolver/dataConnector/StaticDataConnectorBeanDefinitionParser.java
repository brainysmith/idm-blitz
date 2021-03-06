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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import edu.internet2.middleware.shibboleth.common.attribute.BaseAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.provider.BasicAttribute;

/**
 * Spring Bean Definition Parser for static data connector.
 */
public class StaticDataConnectorBeanDefinitionParser extends BaseDataConnectorBeanDefinitionParser {

    /** Schema type name. */
    public static final QName TYPE_NAME = new QName(DataConnectorNamespaceHandler.NAMESPACE, "Static");

    /** Local name of attribute. */
    public static final QName ATTRIBUTE_ELEMENT_NAME = new QName(DataConnectorNamespaceHandler.NAMESPACE,
            "Attribute");

    /** {@inheritDoc} */
    protected Class getBeanClass(Element element) {
        return StaticDataConnectorFactoryBean.class;
    }

    /** {@inheritDoc} */
    protected void doParse(String pluginId, Element pluginConfig, Map<QName, List<Element>> pluginConfigChildren,
            BeanDefinitionBuilder pluginBuilder, ParserContext parserContext) {
        super.doParse(pluginId, pluginConfig, pluginConfigChildren, pluginBuilder, parserContext);

        List<BaseAttribute<String>> attributes = processAttributes(pluginConfigChildren.get(ATTRIBUTE_ELEMENT_NAME));

        pluginBuilder.addPropertyValue("staticAttributes", attributes);
    }

    /**
     * Parses the configuration elements defining the static {@link BaseAttribute}s.
     * 
     * @param attributeElems configuration elements defining the static {@link BaseAttribute}s
     * 
     * @return the static {@link BaseAttribute}s
     */
    protected List<BaseAttribute<String>> processAttributes(List<Element> attributeElems) {
        if (attributeElems == null || attributeElems.size() == 0) {
            return null;
        }

        List<BaseAttribute<String>> attributes = new ArrayList<BaseAttribute<String>>();
        BasicAttribute<String> attribute;
        for (Element attributeElem : attributeElems) {
            attribute = new BasicAttribute<String>(DatatypeHelper.safeTrimOrNullString(attributeElem.getAttributeNS(
                    null, "id")));
            for (Element valueElem : XMLHelper.getChildElementsByTagNameNS(attributeElem,
                    DataConnectorNamespaceHandler.NAMESPACE, "Value")) {
                attribute.getValues().add(valueElem.getTextContent());
            }

            attributes.add(attribute);
        }

        return attributes;
    }
}
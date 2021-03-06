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

package edu.internet2.middleware.shibboleth.common.attribute.encoding.provider;

import edu.internet2.middleware.shibboleth.common.attribute.encoding.AttributeEncoder;

/**
 * Base class for {@link AttributeEncoder}s.
 * 
 * @param <EncodedType> the type of object created by encoding the attribute
 */
public abstract class AbstractAttributeEncoder<EncodedType> implements AttributeEncoder<EncodedType> {

    /** Name of the attribute. */
    private String attributeName;

    /** {@inheritDoc} */
    public String getAttributeName() {
        return attributeName;
    }

    /** {@inheritDoc} */
    public void setAttributeName(String newAttributeName) {
        attributeName = newAttributeName;
    }
}
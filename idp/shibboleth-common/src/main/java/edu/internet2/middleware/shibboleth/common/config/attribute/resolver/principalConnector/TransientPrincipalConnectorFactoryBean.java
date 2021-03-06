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

package edu.internet2.middleware.shibboleth.common.config.attribute.resolver.principalConnector;

import org.opensaml.util.storage.StorageService;

import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.attributeDefinition.TransientIdEntry;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.principalConnector.TransientPrincipalConnector;

/**
 * Spring factory bean for {@link TransientPrincipalConnector}s.
 */
public class TransientPrincipalConnectorFactoryBean extends BasePrincipalConnectorFactoryBean {

    /** Store used to map transient identifier tokens to principal names. */
    private StorageService<String, TransientIdEntry> identifierStore;

    /** {@inheritDoc} */
    public Class getObjectType() {
        return TransientPrincipalConnector.class;
    }

    /**
     * Gets the store used to map transient identifier tokens to principal names.
     * 
     * @return store used to map transient identifier tokens to principal names
     */
    public StorageService<String, TransientIdEntry> getIdentifierStore() {
        return identifierStore;
    }

    /**
     * Sets the store used to map transient identifier tokens to principal names.
     * 
     * @param store store used to map transient identifier tokens to principal names
     */
    public void setIdentifierStore(StorageService<String, TransientIdEntry> store) {
        identifierStore = store;
    }

    /** {@inheritDoc} */
    protected Object createInstance() throws Exception {
        TransientPrincipalConnector connector = new TransientPrincipalConnector(getIdentifierStore());
        populatePrincipalConnector(connector);

        return connector;
    }
}
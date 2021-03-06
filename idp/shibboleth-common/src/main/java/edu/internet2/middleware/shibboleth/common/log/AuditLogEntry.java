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

package edu.internet2.middleware.shibboleth.common.log;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Represents an auditable event in the system.
 */
public class AuditLogEntry {

    /** Name of the Logger for the shibboleth audit log. */
    public static final String AUDIT_LOGGER_NAME = "Shibboleth-Audit";
    
    /** Formatter used to convert timestamps to strings. */
    private static DateTimeFormatter dateFormatter = ISODateTimeFormat.basicDateTimeNoMillis();

    /** UTC IS8601 timestamp of the audit event. */
    private DateTime auditEventTime;

    /** Entity ID of the provider (message issuer). */
    private String assertingPartyId;

    /** Entity ID of the relying party. */
    private String relyingPartyId;

    /** URI of binding used by the relying party. */
    private String requestBinding;

    /** URI of binding used to respond to relying party. */
    private String responseBinding;

    /** URI of profile in use. */
    private String messageProfile;

    /** Unique ID of the request message. */
    private String requestId;

    /** Unqiue ID of the response message. */
    private String responseId;

    /** Principal ID of the user the request was made about. */
    private String principalName;

    /** URIs of the authentication methods currently active for the user. */
    private String principalAuthenticationMethod;

    /** Internal ID of the user attributes released. */
    private List<String> releasedAttributes;
    
    /** Value of the SAML name identifier. */
    private String nameIdValue;

    /** Constructor. */
    public AuditLogEntry() {
        auditEventTime = new DateTime();
        releasedAttributes = new ArrayList<String>();
    }
    
    /**
     * Gets the provider (message issuer) ID.
     * 
     * @return provider (message issuer) ID
     */
    public String getAssertingPartyId() {
        return assertingPartyId;
    }
    
    /**
     * Gets the timestamp for this audit event.
     * 
     * @return timestamp for this audit event
     */
    public DateTime getAuditEventTime() {
        return auditEventTime;
    }

    /**
     * Gets the URI of the message profile being used.
     * 
     * @return URI of the message profile being used
     */
    public String getMessageProfile() {
        return messageProfile;
    }

    /**
     * Gets the value of the SAML name identifier.
     * @return value of the SAML name identifier
     */
    public String getNameIdValue() {
        return nameIdValue;
    }

    /**
     * Gets the authentication method, identified by their URI, used to log into the relying party.
     * 
     * @return authentication method, identified by their URI, used to log into the relying party
     */
    public String getPrincipalAuthenticationMethod() {
        return principalAuthenticationMethod;
    }

    /**
     * Gets the principal ID of the user.
     * 
     * @return principal ID of the user
     */
    public String getPrincipalName() {
        return principalName;
    }

    /**
     * Gets the list of internal IDs of the attributes that were released.
     * 
     * @return internal IDs of the attributes that were released
     */
    public List<String> getReleasedAttributes() {
        return releasedAttributes;
    }

    /**
     * Gets the entity ID of the relying party.
     * 
     * @return entity ID of the relying party
     */
    public String getRelyingPartyId() {
        return relyingPartyId;
    }

    /**
     * Gets the URI of the binding used during the request.
     * 
     * @return URI of the binding used during the request
     */
    public String getRequestBinding() {
        return requestBinding;
    }

    /**
     * Gets the unique ID of the request.
     * 
     * @return unique ID of the request
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the URI of the binding used during the response.
     * 
     * @return URI of the binding used during the response
     */
    public String getResponseBinding() {
        return responseBinding;
    }

    /**
     * Gets the unique ID of the response message.
     * 
     * @return unique ID of the response message
     */
    public String getResponseId() {
        return responseId;
    }

    /**
     * Sets the provider (message issuer) ID.
     * 
     * @param id provider (message issuer) ID
     */
    public void setAssertingPartyId(String id) {
        assertingPartyId = id;
    }

    /**
     * Sets the URI of the message profile being used.
     * 
     * @param profileURI URI of the message profile being used
     */
    public void setMessageProfile(String profileURI) {
        messageProfile = profileURI;
    }

    /**
     * Sets the value of the SAML name identifier.
     * 
     * @param value value of the SAML name identifier
     */
    public void setNameIdValue(String value) {
        nameIdValue = value;
    }

    /**
     * Sets the authentication method, identified by their URI, used to log into the relying party.
     * 
     * @param method authentication method, identified by their URI, used to log into the relying party
     */
    public void setPrincipalAuthenticationMethod(String method) {
        principalAuthenticationMethod = method;
    }

    /**
     * Sets the principal ID of the user.
     * 
     * @param id principal ID of the user
     */
    public void setPrincipalName(String id) {
        principalName = id;
    }

    /**
     * Sets the entity ID of the relying party.
     * 
     * @param entityId entity ID of the relying party
     */
    public void setRelyingPartyId(String entityId) {
        relyingPartyId = entityId;
    }

    /**
     * Sets the URI of the binding used during the request.
     * 
     * @param bindingURI URI of the binding used during the request
     */
    public void setRequestBinding(String bindingURI) {
        requestBinding = bindingURI;
    }

    /**
     * Sets the unique ID of the request.
     * 
     * @param id unique ID of the request
     */
    public void setRequestId(String id) {
        requestId = id;
    }

    /**
     * Sets the URI of the binding used during the response.
     * 
     * @param bindingURI URI of the binding used during the response
     */
    public void setResponseBinding(String bindingURI) {
        responseBinding = bindingURI;
    }

    /**
     * Sets the unique ID of the response message.
     * 
     * @param id unique ID of the response message
     */
    public void setResponseId(String id) {
        responseId = id;
    }
    
    /** {@inheritDoc} */
    public String toString() {
        StringBuilder entryString = new StringBuilder();

        entryString.append(getAuditEventTime().toString(dateFormatter.withZone(DateTimeZone.UTC)));
        entryString.append("|");

        if (getRequestBinding() != null) {
            entryString.append(getRequestBinding());
        }
        entryString.append("|");

        if (getRequestId() != null) {
            entryString.append(getRequestId());
        }
        entryString.append("|");

        entryString.append(getRelyingPartyId());
        entryString.append("|");

        entryString.append(getMessageProfile());
        entryString.append("|");

        entryString.append(getAssertingPartyId());
        entryString.append("|");

        entryString.append(getResponseBinding());
        entryString.append("|");

        entryString.append(getResponseId());
        entryString.append("|");

        if (getPrincipalName() != null) {
            entryString.append(getPrincipalName());
        }
        entryString.append("|");

        if (getPrincipalAuthenticationMethod() != null) {
            entryString.append(getPrincipalAuthenticationMethod());
        }
        entryString.append("|");

        for (String attribute : getReleasedAttributes()) {
            entryString.append(attribute);
            entryString.append(",");
        }
        entryString.append("|");

        return entryString.toString();
    }
}
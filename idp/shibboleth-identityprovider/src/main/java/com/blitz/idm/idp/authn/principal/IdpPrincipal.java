package com.blitz.idm.idp.authn.principal;

/**
 * User: agumerov
 * Date: 24.10.11
 */


import com.blitz.idm.idp.config.AuthnMethodEnum;
import org.opensaml.saml2.metadata.EntityDescriptor;

import java.io.Serializable;

public interface IdpPrincipal extends java.security.Principal, Serializable {

    /**
     * Returns the subject`s OID.
     *
     * @return the subject`s OID.
     */
    public long getOid();

    /**
     * Returns the authentication method by which a subject has been authenticated.
     *
     * @return a authentication method by which a subject has been authenticated.
     */
    public AuthnMethodEnum getAuthnMethod();

    /**
     * Returns the OID of an organization as staff unit of which subject has logged in.
     *
     * @return the organization`s OID.
     */
    public long getOrgOid();

    /**
     * Returns assurance level the subject session.
     *
     * @return assurance level the subject session..
     */
    public long getAssuranceLevel();

    public boolean forceReauthn(EntityDescriptor entityDescriptor);

    public boolean showOrgData(EntityDescriptor entityDescriptor);
}

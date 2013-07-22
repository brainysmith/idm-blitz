package com.blitz.idm.idp.netty.dc;

/**
 * User: agumerov
 * Date: 11.12.12
 */

import com.blitz.idm.idp.authn.principal.IdpPrincipal;
import com.blitz.idm.idp.config.IdpApp;
import com.blitz.idm.idp.dc.AbstractCachedDataConnector;
import com.blitz.idm.idp.dc.AttributeGroup;
import com.blitz.idm.idp.netty.authn.ExtIdpPrincipal;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeResolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PrincipalDataConnector extends AbstractCachedDataConnector {

    private static Logger log = LoggerFactory.getLogger(PrincipalDataConnector.class);

    private boolean initialized = false;

    private String idpEntityId;

    private long idTokenLifetime;

    public void initialize() {
        initialized = true;

        idpEntityId = IdpApp.javaProxyConf().entityId();
        idTokenLifetime = IdpApp.javaProxyConf().idTokenLifetime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<AttributeGroup> getAttributeGroups(Set<String> requiredAtrNames, boolean showOrgData) {
        Set<AttributeGroup> groups = new HashSet<AttributeGroup>(2);
        if (showOrgData) {
            groups.add(AttributeGroupEnum.STAFF_UNIT_ID_TOKEN);
        } else {
            groups.add(AttributeGroupEnum.PERSON_ID_TOKEN);
        }
        if (showOrgData) {
            groups.add(AttributeGroupEnum.PRINCIPAL_ORG_INFO);
        } else {
            groups.add(AttributeGroupEnum.PRINCIPAL_INFO);
        }
        return groups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCached(AttributeGroup grp) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> populateAttributes(AttributeGroup grp, IdpPrincipal principal, String sessionId, String peerEntityId) throws AttributeResolutionException {
        ExtIdpPrincipal prl = (ExtIdpPrincipal) principal;
        AttributeGroupEnum attGroup = AttributeGroupEnum.lookup(grp.getSysname());
        switch (attGroup) {
            case PERSON_ID_TOKEN:
                return getIdTokenAttributes(false, prl, sessionId, peerEntityId);
            case STAFF_UNIT_ID_TOKEN:
                return getIdTokenAttributes(true, prl, sessionId, peerEntityId);
            case PRINCIPAL_INFO:
                return getPrincipalAttributes(prl);
            case PRINCIPAL_ORG_INFO:
                return getPrincipalOrganizationAttributes(prl);
            default:
                throw new IllegalArgumentException("Unknown attribute group " + grp.getSysname());
        }
    }


    /**
     * Get attributes which value based on principal data.
     *
     * @param principal {@link com.blitz.idm.idp.netty.authn.ExtIdpPrincipal} actual principal
     * @throws {@link }AttributeResolutionException} if there is a problem in populating the attributes
     */
    private Map<String, Object> getPrincipalAttributes(ExtIdpPrincipal principal) throws AttributeResolutionException {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeEnum.GLOBAL_ROLE.getSourceAttributeId(), TestValueEnum.GLOBAL_ROLE.getValue());
        attributes.put(AttributeEnum.PERSON_TYPE.getSourceAttributeId(), TestValueEnum.PERSON_TYPE.getValue());
        attributes.put(AttributeEnum.AUTHN_METHOD.getSourceAttributeId(), TestValueEnum.AUTHN_METHOD.getValue());
        attributes.put(AttributeEnum.USER_ID.getSourceAttributeId(), TestValueEnum.USER_ID.getValue());
        attributes.put(AttributeEnum.USER_NAME.getSourceAttributeId(), TestValueEnum.USER_NAME.getValue());
        attributes.put(AttributeEnum.USER_NAME_OLD.getSourceAttributeId(), TestValueEnum.USER_NAME.getValue());
        attributes.put(AttributeEnum.ASSURANCE_LEVEL.getSourceAttributeId(), TestValueEnum.ASSURANCE_LEVEL.getValue());
        return attributes;
    }

    /**
     * Get attributes which value based on principal organization data.
     *
     * @param principal {@link com.blitz.idm.idp.netty.authn.ExtIdpPrincipal} actual principal
     * @throws {@link }AttributeResolutionException} if there is a problem in populating the attributes
     */
    private Map<String, Object> getPrincipalOrganizationAttributes(ExtIdpPrincipal principal) throws AttributeResolutionException {
        Map<String, Object> attributes = getPrincipalAttributes(principal);
        attributes.put(AttributeEnum.GLOBAL_ROLE.getSourceAttributeId(), TestValueEnum.GLOBAL_ROLE.getValue());
        attributes.put(AttributeEnum.ORGANIZATION_ID.getSourceAttributeId(), TestValueEnum.ORGANIZATION_ID.getValue());
        attributes.put(AttributeEnum.ORGANIZATION_SHORT_NAME.getSourceAttributeId(),TestValueEnum.ORGANIZATION_SHORT_NAME.getValue());
        attributes.put(AttributeEnum.ORGANIZATION_TYPE.getSourceAttributeId(),TestValueEnum.ORGANIZATION_TYPE.getValue());
        return attributes;
    }

    /**
     * Get attributes which value based on IDToken.
     * @param isEmployee    current principal role
     * @param principal    {@link com.blitz.idm.idp.netty.authn.ExtIdpPrincipal} actual principal
     * @param sessionId    {@link edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.ShibbolethResolutionContext} id of current idp session
     * @param peerEntityId {@link edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.ShibbolethResolutionContext} peer sp entity id
     * @throws {@link }AttributeResolutionException} if there is a problem in populating the attributes
     */
    private Map<String, Object> getIdTokenAttributes(boolean isEmployee, ExtIdpPrincipal principal, String sessionId, String peerEntityId) throws AttributeResolutionException {
        Map<String, Object> attributes = new HashMap<String, Object>();
        //attributes.put(AttributeEnum.ID_TOKEN.getSourceAttributeId(), generateIDToken(isEmployee, peerEntityId, principal, sessionId));
        return attributes;
    }


    /**
     * Generate base64 IDToken {@link String}.
     *
     * @param isEmployee    current principal role
     * @param peerEntityId {@link String} peer sp entity Id
     * @param principal    {@link com.blitz.idm.idp.netty.authn.ExtIdpPrincipal} actual principal
     * @param sessionId    {@link String} current session id
     * @throws edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeResolutionException thrown if there is a problem in generating the token
     */
    private String generateIDToken(boolean  isEmployee, String peerEntityId, ExtIdpPrincipal principal,
                                   String sessionId) throws AttributeResolutionException {
        return "IdTokenString";
/*        SubjectClaim sbjClaim = new SubjectClaim(principal.getOid(), principal.getName(), SubjectTypeEnum.PERSON, principal.isTrusted());
        StaffUnitClaim stuClaim = null;
        if (isEmployee) {
            stuClaim = new StaffUnitClaim(principal.getOrgOid(), principal.getOrgName(), principal.getOrgType(), principal.isChief());

        }
        Date notBefore = new Date(System.currentTimeMillis());
        IDToken token;
        if (stuClaim != null) {
            token = new IDTokenImpl(idpEntityId, idTokenLifetime, peerEntityId, notBefore, sessionId, principal.getAuthnMethod(), sbjClaim, stuClaim, JWA.RS256);
        } else {
            token = new IDTokenImpl(idpEntityId, idTokenLifetime, peerEntityId, notBefore, sessionId, principal.getAuthnMethod(), sbjClaim, JWA.RS256);
        }
        try {
            String base64Token = token.toBase64();
            log.debug("IDToken {} created", base64Token);
            return base64Token;
        } catch (TokenException e) {
            log.error("Error creating IDToken for peer sp entity Id = {} : {}", peerEntityId, e);
            throw new AttributeResolutionException(e.getMessage());
        }*/
    }

}

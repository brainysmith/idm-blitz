package com.blitz.idm.idp.netty.dc;

/**
 * User: agumerov
 * Date: 11.12.12
 */

import edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeResolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blitz.idm.idp.authn.principal.IdpPrincipal;
import com.blitz.idm.idp.dc.AbstractCachedDataConnector;
import com.blitz.idm.idp.dc.AttributeGroup;
import com.blitz.idm.idp.netty.authn.ExtIdpPrincipal;

import java.util.*;

public class DBDataConnector extends AbstractCachedDataConnector {

    private static Logger log = LoggerFactory.getLogger(DBDataConnector.class);

    private boolean initialized = false;

    public void initialize() {
        initialized = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<AttributeGroup> getAttributeGroups(Set<String> atrNames, boolean showOrgData) {
        Set<AttributeGroup> groups = new HashSet<AttributeGroup>();
        for (String name : atrNames) {
            switch (name.hashCode()) {
                /* PERSON SHORT INFO GROUP */
                case AttributeEnum.HashCode.PERSON_LASTNAME_HASH_CODE:
                case AttributeEnum.HashCode.PERSON_FIRSTNAME_HASH_CODE:
                case AttributeEnum.HashCode.PERSON_MIDDLENAME_HASH_CODE:
                case AttributeEnum.HashCode.PERSON_SNILS_HASH_CODE:
                case AttributeEnum.HashCode.PERSON_SNILS_OLD_HASH_CODE:
                case AttributeEnum.HashCode.PERSON_INN_HASH_CODE:
                case AttributeEnum.HashCode.PERSON_INN_OLD_HASH_CODE:
                case AttributeEnum.HashCode.PERSON_CITIZENSHIP_HASH_CODE: {
                    groups.add(AttributeGroupEnum.PERSON_SHORT_INFO);
                    break;
                }
                /* PERSON FULL INFO GROUP */
                case AttributeEnum.HashCode.PERSON_CONTACTS_HASH_CODE:
                case AttributeEnum.HashCode.PERSON_EMAIL_HASH_CODE:
                case AttributeEnum.HashCode.PERSON_MOBILE_HASH_CODE:
                case AttributeEnum.HashCode.PERSON_DOCUMENTS_HASH_CODE:
                case AttributeEnum.HashCode.PERSON_ADDRESSES_HASH_CODE: {
                    groups.add(AttributeGroupEnum.PERSON_FULL_INFO);
                    break;
                }
                /* PERSON IN ORGANIZATION GROUP */
                case AttributeEnum.HashCode.PERSON_IN_ORG_HASH_CODE: {
                    break;
                }
                /* ORGANIZATION SHORT INFO GROUP */
                case AttributeEnum.HashCode.ORGANIZATION_OGRN_HASH_CODE:
                case AttributeEnum.HashCode.ORGANIZATION_INN_HASH_CODE:
                case AttributeEnum.HashCode.ORGANIZATION_SHORT_NAME_HASH_CODE:
                case AttributeEnum.HashCode.ORGANIZATION_KPP_HASH_CODE:
                case AttributeEnum.HashCode.ORGANIZATION_BRANCH_KPP_HASH_CODE:
                case AttributeEnum.HashCode.ORGANIZATION_LEGAL_FORM_HASH_CODE:
                case AttributeEnum.HashCode.ORGANIZATION_NSI_ID_HASH_CODE:
                case AttributeEnum.HashCode.STAFF_UNIT_POSITION_HASH_CODE: {
                    if (showOrgData) {
                         groups.add(AttributeGroupEnum.STAFF_UNIT_SHORT_INFO);
                    }
                    break;
                }
                /* ORGANIZATION FULL INFO GROUP */
                case AttributeEnum.HashCode.ORGANIZATION_ADDRESSES_HASH_CODE:
                case AttributeEnum.HashCode.ORGANIZATION_CONTACTS_HASH_CODE: {
                    if (showOrgData) {
                        groups.add(AttributeGroupEnum.STAFF_UNIT_FULL_INFO);
                    }
                    break;
                }
                /* BUSINESS PERSON INFO GROUP */
                case AttributeEnum.HashCode.PERSON_OGRN_HASH_CODE: {
                    break;
                }
                /* SYSTEM AUTHORITY INFO GROUP */
                case AttributeEnum.HashCode.SYSTEM_AUTHORITIES_HASH_CODE: {
                    if (showOrgData){
                        groups.add(AttributeGroupEnum.STAFF_UNIT_AUTHORITIES_INFO);
                    } else {
                        groups.add(AttributeGroupEnum.PERSON_AUTHORITIES_INFO);
                    }
                    break;
                }
            }
        }
        // attempt to delete nested person group
        if (groups.contains(AttributeGroupEnum.PERSON_FULL_INFO)) {
            groups.remove(AttributeGroupEnum.PERSON_SHORT_INFO);
        }
        // attempt to delete nested organization group
        if (groups.contains(AttributeGroupEnum.STAFF_UNIT_FULL_INFO)) {
            groups.remove(AttributeGroupEnum.STAFF_UNIT_SHORT_INFO);
        }
        return groups;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCached(AttributeGroup grp) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> populateAttributes(AttributeGroup grp, IdpPrincipal principal, String sessionId, String peerEntityId) throws AttributeResolutionException {
        ExtIdpPrincipal prl = (ExtIdpPrincipal) principal;
        AttributeGroupEnum attGroup = AttributeGroupEnum.lookup(grp.getSysname());
        switch (attGroup) {
            case PERSON_SHORT_INFO:
                return getPersonBriefAttributes(/*getPerson(prl)*/);
            case PERSON_FULL_INFO:
                return getPersonFullAttributes(/*getPersonWithAdditionalInfo(prl)*/);
            case STAFF_UNIT_SHORT_INFO:
                return getStaffUnitBriefAttributes(/*getStaffUnit(prl)*/);
            case STAFF_UNIT_FULL_INFO:
                return getStaffUnitFullAttributes(/*getStaffUnitWithAdditionalInfo(prl)*/);
            case PERSON_AUTHORITIES_INFO:
                return new HashMap<String, Object>(); //getAuthorityInfoAttributes(AccountAuthorities.getPrivateAuthoritiesByOid(peerEntityId, prl.getOid()));
            case STAFF_UNIT_AUTHORITIES_INFO:
                return new HashMap<String, Object>(); //getAuthorityInfoAttributes(AccountAuthorities.getCorporateAuthoritiesByOids(peerEntityId, prl.getOid(), prl.getOrgOid()));
            default:
                throw new IllegalArgumentException("Unknown attribute group " + grp.getSysname());
        }


    }


    /**
     * Get attributes which value based on person data.
     *
     * @param person {@link PersonEntity} actual principal person
     * @return {@link java.util.Map<String, Object>} populated attribute map
     * @throws {@link AttributeResolutionException} if there is a problem in populating the attributes
     */
    private Map<String, Object> getPersonBriefAttributes(/*PersonEntity person*/) throws AttributeResolutionException {
/*        if (person == null) {
            return new HashMap<String, Object>();
        }*/
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeEnum.PERSON_LASTNAME.getSourceAttributeId(), TestValueEnum.PERSON_LASTNAME.getValue());
        attributes.put(AttributeEnum.PERSON_FIRSTNAME.getSourceAttributeId(),  TestValueEnum.PERSON_FIRSTNAME.getValue());
       if (TestValueEnum.PERSON_MIDDLENAME.getValue() != null)
            attributes.put(AttributeEnum.PERSON_MIDDLENAME.getSourceAttributeId(), TestValueEnum.PERSON_MIDDLENAME.getValue());
        if (TestValueEnum.PERSON_SNILS.getValue() != null) {
            attributes.put(AttributeEnum.PERSON_SNILS.getSourceAttributeId(), TestValueEnum.PERSON_SNILS.getValue());
            attributes.put(AttributeEnum.PERSON_SNILS_OLD.getSourceAttributeId(), TestValueEnum.PERSON_SNILS.getValue());
        }
        if (TestValueEnum.PERSON_INN.getValue() != null) {
            attributes.put(AttributeEnum.PERSON_INN.getSourceAttributeId(), TestValueEnum.PERSON_INN.getValue());
            attributes.put(AttributeEnum.PERSON_INN_OLD.getSourceAttributeId(), TestValueEnum.PERSON_INN.getValue());
        }
        return attributes;
    }

    /**
     * Populate attributes which value based on person children objects data.
     *
     * @param person {@link PersonEntity} actual principal person
     * @return {@link java.util.Map<String, Object>} populated attribute map
     * @throws {@link AttributeResolutionException} if there is a problem in populating the attributes
     */
    private Map<String, Object> getPersonFullAttributes(/*PersonEntity person*/) throws AttributeResolutionException {
/*        if (person == null) {
            return new HashMap<String, Object>();
        }*/
        Map<String, Object> attributes = getPersonBriefAttributes(/*person*/);
        if (TestValueEnum.PERSON_EMAIL.getValue() != null)
            attributes.put(AttributeEnum.PERSON_EMAIL.getSourceAttributeId(), TestValueEnum.PERSON_EMAIL.getValue());
        if (TestValueEnum.PERSON_MOBILE.getValue() != null)
            attributes.put(AttributeEnum.PERSON_MOBILE.getSourceAttributeId(), TestValueEnum.PERSON_MOBILE.getValue());
        if (TestValueEnum.PERSON_CITIZENSHIP.getValue() != null)
            attributes.put(AttributeEnum.PERSON_CITIZENSHIP.getSourceAttributeId(),TestValueEnum.PERSON_CITIZENSHIP.getValue());
        //TODO attributes.put(AttributeEnum.PERSON_CONTACTS.getSourceAttributeId(), person.getMiddleName());
        //TODO attributes.put(AttributeEnum.PERSON_ADDRESSES.getSourceAttributeId(), person.getSnils());
        //TODO attributes.put(AttributeEnum.PERSON_DOCUMENTS.getSourceAttributeId(), person.getInn());

        return attributes;
    }

    /**
     * Get main attributes which value based on principal staff unit data.
     *
     * @param stu {@link StaffUnitEntity} actual principal staff unit
     * @return {@link java.util.Map<String, Object>} populated attribute map
     * @throws {@link AttributeResolutionException} if there is a problem in populating the attributes
     */
    private Map<String, Object> getStaffUnitBriefAttributes(/*StaffUnitEntity stu*/) throws AttributeResolutionException {
/*        if (stu == null) {
            return new HashMap<String, Object>();
        }*/
        //OrganizationEntity org = stu.getOrganization();
        Map<String, Object> attributes = new HashMap<String, Object>();
        if (TestValueEnum.STAFF_UNIT_POSITION.getValue() != null)
            attributes.put(AttributeEnum.STAFF_UNIT_POSITION.getSourceAttributeId(), TestValueEnum.STAFF_UNIT_POSITION.getValue());
        if (TestValueEnum.ORGANIZATION_OGRN.getValue() != null)
            attributes.put(AttributeEnum.ORGANIZATION_OGRN.getSourceAttributeId(), TestValueEnum.ORGANIZATION_OGRN.getValue());
        if (TestValueEnum.ORGANIZATION_INN.getValue() != null)
            attributes.put(AttributeEnum.ORGANIZATION_INN.getSourceAttributeId(), TestValueEnum.ORGANIZATION_INN.getValue());
        if (TestValueEnum.ORGANIZATION_SHORT_NAME.getValue() != null)
            attributes.put(AttributeEnum.ORGANIZATION_SHORT_NAME.getSourceAttributeId(), TestValueEnum.ORGANIZATION_SHORT_NAME.getValue());
        if (TestValueEnum.ORGANIZATION_KPP.getValue() != null)
            attributes.put(AttributeEnum.ORGANIZATION_KPP.getSourceAttributeId(),TestValueEnum.ORGANIZATION_KPP.getValue());
        if (TestValueEnum.ORGANIZATION_NSI_ID.getValue() != null)
            attributes.put(AttributeEnum.ORGANIZATION_NSI_ID.getSourceAttributeId(), TestValueEnum.ORGANIZATION_NSI_ID.getValue());
        // TODO attributes.put(AttributeEnum.ORGANIZATION_BRANCH_KPP.getSourceAttributeId(), org.getLegalForm());
        return attributes;
    }

    /**
     * Populate all attributes which value based on principal staff unit data.
     *
     * @param stu {@link StaffUnitEntity} actual principal staff unit
     * @return {@link java.util.Map<String, Object>} populated attribute map
     * @throws {@link AttributeResolutionException} if there is a problem in populating the attributes
     */
    private Map<String, Object> getStaffUnitFullAttributes(/*StaffUnitEntity stu*/) throws AttributeResolutionException {
/*        if (stu == null) {
            return new HashMap<String, Object>();
        }*/
        //OrganizationEntity org = stu.getOrganization();
        Map<String, Object> attributes = getStaffUnitBriefAttributes(/*stu*/);
        if (TestValueEnum.ORGANIZATION_LEGAL_FORM.getValue() != null) {
            attributes.put(AttributeEnum.ORGANIZATION_LEGAL_FORM.getSourceAttributeId(), TestValueEnum.ORGANIZATION_LEGAL_FORM.getValue());
        }
        // TODO attributes.put(AttributeEnum.ORGANIZATION_CONTACTS.getSourceAttributeId(), org.getOgrn());
        // TODO attributes.put(AttributeEnum.ORGANIZATION_ADDRESSES.getSourceAttributeId(), org.getInn());
        return attributes;
    }

/*    *//**
     * Get attributes which value based on subject system authorities data.
     *
     * @param principalAuthorities {@link java.util.List<AccountAuthorityEntity>} actual principal
     * @return {@link java.util.Map<String, Object>} populated attribute map
     * @throws {@link AttributeResolutionException} if there is a problem in populating the attributes
     *//*
    private Map<String, Object> getAuthorityInfoAttributes(*//*List<AccountAuthorityEntity> principalAuthorities*//*) throws AttributeResolutionException {
        return new HashMap<String, Object>();
        if (principalAuthorities == null || principalAuthorities.isEmpty()) {
            return new HashMap<String, Object>();
        }
        Map<String, Object> attributes = new HashMap<String, Object>();
        String xmlAuthorities = "";
        for (AccountAuthorityEntity authz : principalAuthorities) {
            String xmlAuthority = String.format("<%1$s:%2$s system=\"%3$s\">%4$s</%1$s:%2$s>",
                    BaseAttribute.PREFIX, Authority.TYPE_LOCAL_NAME, authz.getAuthority().getItSystem().getNsiId(), authz.getAuthority().getSysname());
            xmlAuthorities = xmlAuthorities + xmlAuthority;

        }
        xmlAuthorities = String.format("<%1$s:%2$s xmlns:%1$s=\"%3$s\">%4$s</%1$s:%2$s>",
                BaseAttribute.PREFIX, Authorities.TYPE_LOCAL_NAME, BaseAttribute.NAMESPACE, xmlAuthorities);
        attributes.put(AttributeEnum.SYSTEM_AUTHORITIES.getSourceAttributeId(), xmlAuthorities);
        return attributes;
    }

    *//**
     * Get person entity for principal.
     *
     * @param principal {@link com.blitz.idm.idp.netty.authn.ExtIdpPrincipal} actual principal
     * @return {@link PersonEntity} or null - if not found
     *//*
    private PersonEntity getPerson(ExtIdpPrincipal principal) {
       PersonEntity person = PersonEntity.findByOid(principal.getOid());
        if (person != null) {
            return person;
        }
        log.error("Person not found for principal {}", principal);
        return null;
    }

    *//**
     * Get person entity with additional info for principal.
     *
     * @param principal {@link com.blitz.idm.idp.netty.authn.ExtIdpPrincipal} actual principal
     * @return {@link PersonEntity} or null - if not found
     *//*
     private PersonEntity getPersonWithAdditionalInfo(ExtIdpPrincipal principal) {
        PersonEntity person = PersonEntity.findFullInfoByOid(principal.getOid());
        if (person != null) {
            return person;
        }
        log.error("Person not found for principal {}", principal);
        return null;
    }

   *//**
     * Get principal staff unit entity.
     *
     * @param principal {@link com.blitz.idm.idp.netty.authn.ExtIdpPrincipal} actual principal
     * @return {@link StaffUnitEntity} or null - if not found
     *//*
    private StaffUnitEntity getStaffUnit(ExtIdpPrincipal principal) {
        if (principal.isEmployee()) {
            StaffUnitEntity stu = StaffUnitEntity.findByOrgOidAndPersonOid(principal.getOrgOid(), principal.getOid());
            if (stu != null) {
                return stu;
            }
        }
        log.warn("Staff unit not found for principal {}", principal);
        return null;
    }

    *//**
     * GGet principal staff unit entity with additional info.
     *
     * @param principal {@link com.blitz.idm.idp.netty.authn.ExtIdpPrincipal} actual principal
     * @return {@link StaffUnitEntity} or null - if not found
     *//*
    private StaffUnitEntity getStaffUnitWithAdditionalInfo(ExtIdpPrincipal principal) {
        if (principal.isEmployee()) {
            StaffUnitEntity stu = StaffUnitEntity.findFullInfoByOrgOidAndPersonOid(principal.getOrgOid(), principal.getOid());
            if (stu != null) {
                return stu;
            }
        }
        log.warn("Staff unit not found for principal {}", principal);
        return null;
    }*/

}

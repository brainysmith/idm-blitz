package com.blitz.idm.idp.netty.md;

import com.blitz.idm.idp.config.AssuranceLevelEnum;
import com.blitz.idm.idp.config.GlobalRoleEnum;
import com.blitz.idm.idp.config.OrganizationTypeEnum;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.xml.XMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blitz.idm.idp.netty.config.md.ext.api.*;

import java.util.*;

public class ExtMetadataFilter {

    /**
     * Class logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ExtMetadataFilter.class);

    private AssuranceLevelEnum assuranceLevel;
    private boolean needEmployeeRole;
    private boolean needPersonRole;
    private Set<OrganizationTypeEnum> orgTypes;

    public ExtMetadataFilter(EntityDescriptor entityDescriptor) {
        Extensions extensions = entityDescriptor.getExtensions();
        AssuranceLevelEnum  assuranceLevel = null;
        Set<GlobalRoleEnum> globalRoles = null;
        Set<OrganizationTypeEnum>  orgTypes = null;
        if (extensions != null) {
            for (XMLObject child : extensions.getUnknownXMLObjects()) {
                if (child instanceof SupportedAssuranceLevels) {
                    assuranceLevel = getSupportedAssuranceLevel((SupportedAssuranceLevels) child);
                } else if (child instanceof SupportedGlobalRoles) {
                    Map<GlobalRoleEnum,Set<OrganizationTypeEnum>> globalRoleMap = getSupportedGlobalRoles((SupportedGlobalRoles) child);
                    globalRoles = globalRoleMap.keySet();
                    orgTypes = globalRoleMap.get(GlobalRoleEnum.EMPLOYEE);
                } else {
                    log.warn("Unsupported metadata extension element for peer entity id {} ", entityDescriptor.getEntityID());
                }
            }
        }
        this.assuranceLevel = assuranceLevel;
        this.needEmployeeRole = false;
        this.needPersonRole = false;
        if (globalRoles != null
                && !globalRoles.isEmpty()
                && globalRoles.size() == 1) {
            if (globalRoles.contains(GlobalRoleEnum.EMPLOYEE)) {
                needEmployeeRole = true;
            } else {
                needPersonRole = true;
            }
        }
        if (globalRoles != null && !globalRoles.isEmpty())
            this.orgTypes = orgTypes;
    }

    /**
     * Check whether selected assurance level forceReauthn to required level.
     *
     * @param assuranceLevel the principal assurance level
     * @return true when matched and false if not
     */
    public boolean isPossibleAssuranceLevel(AssuranceLevelEnum assuranceLevel) {
        if (assuranceLevel == null)
            throw new NullPointerException("Assurance level can't be null");
        return (this.assuranceLevel == null || !assuranceLevel.isLessThan(this.assuranceLevel));
    }

    /**
     * Check whether selected role forceReauthn to required roles.
     *
     * @param isEmployee the principal employee role
     * @param orgType    the principal orgType
     * @return true when matched and false if not
     */
    public boolean isPossibleRole(boolean isEmployee, OrganizationTypeEnum orgType) {
        // person
        if (!isEmployee)
            return !needEmployeeRole;
        // employee
        else
            return (orgTypes == null || orgTypes.isEmpty() || orgTypes.contains(orgType));
    }

    public AssuranceLevelEnum getAssuranceLevel() {
        return assuranceLevel;
    }

    public boolean needEmployeeRole() {
        return needEmployeeRole;
    }

    public boolean needPersonRole() {
        return needPersonRole;
    }

    public Set<OrganizationTypeEnum> getOrgTypes() {
        return orgTypes;
    }

    /*    public static boolean containsRequiredAttribute(EntityDescriptor entityDescriptor, String attributeId) {
            if (attributeId == null) {
                log.debug("Attribute id can't be null");
                throw new NullPointerException("Attribute id name can't be null");
            }
            log.debug("Check whether the entity descriptor contains the attribute with id {}", attributeId);

            Set<String> names = getRequiredAttributeNames(entityDescriptor);
            return (names != null && names.contains(attributeId));
        }

        public static Set<String> getRequiredAttributeNames(EntityDescriptor entityDescriptor) {
            if (entityDescriptor == null) {
                log.info("PeerEntityMetadata is undefined");
                return null;
            }
            log.debug("Found the entity descriptor {}", entityDescriptor.getEntityID());

            AttributeAuthorityDescriptor attributeAuthDescriptor = entityDescriptor.getAttributeAuthorityDescriptor(SAMLConstants.SAML20P_NS);
            if (attributeAuthDescriptor == null) {
                log.info("AttributeAuthorityDescriptor is undefined");
                return null;
            }
            log.debug("Found the attribute authority descriptor");

            List<Attribute> attributes = attributeAuthDescriptor.getAttributes();
            if (attributes == null) {
                log.info("A list of the attributes is undefined");
                return null;
            }
            Set<String> names = new HashSet<String>(attributes.size());
            for (Attribute attr : attributes) {
                names.add(attr.getName());
            }
            return names;
        }*/




        private static Map<GlobalRoleEnum, Set<OrganizationTypeEnum>>  getSupportedGlobalRoles(SupportedGlobalRoles sgr) {
            Map<GlobalRoleEnum, Set<OrganizationTypeEnum>>  supportedGlobalRoles = new HashMap<GlobalRoleEnum, Set<OrganizationTypeEnum>>();
            List<GlobalRole> globalRoles = sgr.getGlobalRoles();
            if (globalRoles != null) {
                for (GlobalRole gr : globalRoles) {
                    Set<OrganizationTypeEnum> supportedOrgTypes = getSupportedOrgTypes(gr);
                    supportedGlobalRoles.put(gr.getID(), supportedOrgTypes);
                }
            }
            return supportedGlobalRoles;
        }

        private static AssuranceLevelEnum getSupportedAssuranceLevel(SupportedAssuranceLevels sal) {
            List<AssuranceLevel> assuranceLevels = sal.getAssuranceLevels();
            if (assuranceLevels != null) {
                for (AssuranceLevel al : assuranceLevels) {
                    return al.getID();
                }
            }
            return null;
        }

        private static Set<OrganizationTypeEnum> getSupportedOrgTypes(GlobalRole globalRole) {
            Set<OrganizationTypeEnum> supportedOrgTypes = new HashSet<OrganizationTypeEnum>();
            SupportedOrgTypes sot = globalRole.getSupportedOrgTypes();
            if (sot != null) {
                List<OrgType> ots = sot.getOrgTypes();
                if (ots != null) {
                    for (OrgType ot : ots) {
                        supportedOrgTypes.add(ot.getID());
                    }
                }
            }
            return supportedOrgTypes;
        }



}
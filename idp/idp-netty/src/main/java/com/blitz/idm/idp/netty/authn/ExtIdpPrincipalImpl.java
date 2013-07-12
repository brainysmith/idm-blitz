package com.blitz.idm.idp.netty.authn;

import com.blitz.idm.idp.config.AssuranceLevelEnum;
import com.blitz.idm.idp.config.AuthnMethodEnum;
import com.blitz.idm.idp.config.OrganizationTypeEnum;
import com.blitz.idm.idp.netty.md.ExtMetadataFilter;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * User: agumerov
 * Date: 24.10.11
 */

/**
 * A Idp internal principal implementation of {@link ExtIdpPrincipal}.
 */

public class ExtIdpPrincipalImpl implements ExtIdpPrincipal {
    /**
     * Class logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ExtIdpPrincipalImpl.class);


    private long oid;
    private String name;
    private AuthnMethodEnum authnMethod;
    private boolean trusted;
    private long orgOid;
    private OrganizationTypeEnum orgType;
    private String orgName;
    private boolean chief;


    /**
     * Constructor.
     */
/*    public ExtIdpPrincipalImpl(AuthnToken token) {
        if (token == null) {
            log.error("Response token can't be null");
            throw new NullPointerException("Response token can't be null");
        }

        this.authnMethod = token.getAuthnMethod();
        this.oid = token.getOid();
        this.name = token.getName();
        this.trusted = token.isTrusted();
        this.orgOid = token.getOrgOid();
        this.orgName = token.getOrgName();
        this.orgType = token.getOrgType();
        this.chief = token.isChief();
    }*/
    public ExtIdpPrincipalImpl(AuthnMethodEnum authnMethod, long oid, String name, boolean trusted,
                               long orgOid, String orgName, OrganizationTypeEnum orgType, boolean chief) {
        this.authnMethod = authnMethod;
        this.oid = oid;
        this.name = name;
        this.trusted = trusted;
        this.orgOid = orgOid;
        this.orgName = orgName;
        this.orgType = orgType;
        this.chief = chief;
    }

    @Override
    public long getOid() {
        return this.oid;
    }

    @Override
    public AuthnMethodEnum getAuthnMethod() {
        return this.authnMethod;
    }

    @Override
    public boolean isEmployee() {
        return (this.orgOid != 0L);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTrusted() {
        return this.trusted;
    }

    @Override
    public long getOrgOid() {
        return orgOid;
    }

    @Override
    public OrganizationTypeEnum getOrgType() {
        return orgType;
    }

    @Override
    public String getOrgName() {
        return orgName;
    }

    @Override
    public boolean isChief() {
        return chief;
    }

    @Override
    public String toString() {
        if (isEmployee()) {
            return String.format("[%1$s,%2$s,%3$s,%4$s,%5$s:%6$s,%7$s,%8$s]",
                    this.authnMethod, Long.toString(this.oid), this.name, Boolean.toString(isTrusted()),
                    this.orgOid, this.getName(), this.orgType, Boolean.toString(isChief()));
        } else {
            return String.format("[%1$s,%2$s,%3$s,%4$s]",
                    this.authnMethod, Long.toString(this.oid), this.name, Boolean.toString(isTrusted()));
        }
    }

    @Override
    public long getAssuranceLevel() {
        return getAssuranceLevelEnum().getLevel();
    }

    @Override
    public AssuranceLevelEnum getAssuranceLevelEnum() {
        if (!isTrusted())
            return AssuranceLevelEnum.ASSURANCE_LEVEL_10;
        if (authnMethod == AuthnMethodEnum.DS) {
            return AssuranceLevelEnum.ASSURANCE_LEVEL_30;
        }
        return AssuranceLevelEnum.ASSURANCE_LEVEL_20;
    }

    private AssuranceLevelEnum getMaxPossibleAssuranceLevel() {
        if (!isTrusted())
            return AssuranceLevelEnum.ASSURANCE_LEVEL_10;
        return AssuranceLevelEnum.ASSURANCE_LEVEL_30;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o)
            return true;

        return ((o instanceof ExtIdpPrincipal) &&
                this.getClass() == o.getClass() &&
                this.oid == (((ExtIdpPrincipal) o).getOid()) &&
                this.name.equals(((ExtIdpPrincipal) o).getName()));
    }

    @Override
    public boolean forceReauthn(EntityDescriptor entityDescriptor) {
        ExtMetadataFilter metadataFilter = new ExtMetadataFilter(entityDescriptor);
        return (!metadataFilter.isPossibleAssuranceLevel(getAssuranceLevelEnum()) &&
                metadataFilter.isPossibleAssuranceLevel(getMaxPossibleAssuranceLevel()));
    }



    @Override
    public boolean showOrgData(EntityDescriptor entityDescriptor) {
        ExtMetadataFilter metadataFilter = new ExtMetadataFilter(entityDescriptor);
        if (isEmployee() && metadataFilter.needPersonRole()) {
            log.debug("Peer with entity id {} required only person attributes", entityDescriptor.getEntityID());
        }
        return (isEmployee() && !metadataFilter.needPersonRole());
    }
}

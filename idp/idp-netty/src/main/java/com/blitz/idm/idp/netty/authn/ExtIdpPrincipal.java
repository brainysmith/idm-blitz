package com.blitz.idm.idp.netty.authn;

import com.blitz.idm.idp.authn.principal.IdpPrincipal;
import com.blitz.idm.idp.config.AssuranceLevelEnum;
import com.blitz.idm.idp.config.OrganizationTypeEnum;

public interface ExtIdpPrincipal extends IdpPrincipal {

    public AssuranceLevelEnum getAssuranceLevelEnum();

    public boolean isEmployee();

    public boolean isTrusted();

    public OrganizationTypeEnum getOrgType();

    public String getOrgName();

    public boolean isChief();



}

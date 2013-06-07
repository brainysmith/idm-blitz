package com.blitz.idm.idp.netty.config.md.ext.api;

import com.blitz.idm.idp.config.GlobalRoleEnum;

import javax.xml.namespace.QName;

public interface GlobalRole extends BaseAttributeMetadataExtension<GlobalRoleEnum>{

    public static final String ELEMENT_LOCAL_NAME = "GlobalRole";

    public static final QName ELEMENT_NAME = new QName(BLITZ_MDEXT_NS, ELEMENT_LOCAL_NAME);

    public SupportedOrgTypes getSupportedOrgTypes();

    public void setSupportedOrgTypes(SupportedOrgTypes supportedOrgTypes);

}

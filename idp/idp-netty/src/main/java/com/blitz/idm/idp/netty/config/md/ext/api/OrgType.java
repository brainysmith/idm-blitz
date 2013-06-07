package com.blitz.idm.idp.netty.config.md.ext.api;

import com.blitz.idm.idp.config.OrganizationTypeEnum;

import javax.xml.namespace.QName;

public interface OrgType extends BaseAttributeMetadataExtension<OrganizationTypeEnum> {

    public static final String ELEMENT_LOCAL_NAME = "OrgType";

    public static final QName ELEMENT_NAME = new QName(BLITZ_MDEXT_NS, ELEMENT_LOCAL_NAME);

}

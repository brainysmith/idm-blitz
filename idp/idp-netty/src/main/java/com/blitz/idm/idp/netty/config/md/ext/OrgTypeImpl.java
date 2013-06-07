package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.config.OrganizationTypeEnum;
import com.blitz.idm.idp.netty.config.md.ext.api.OrgType;

import javax.xml.XMLConstants;

public class OrgTypeImpl extends BaseAttributeMetadataExtensionImpl<OrganizationTypeEnum> implements OrgType {

    OrgTypeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    public OrgTypeImpl(OrganizationTypeEnum id) {
        super(OrgType.BLITZ_MDEXT_NS, OrgType.ELEMENT_LOCAL_NAME, XMLConstants.DEFAULT_NS_PREFIX);
        if(id == null){
            throw new IllegalArgumentException("An organization type ID is undefined");
        }
        setID(id);
    }
}

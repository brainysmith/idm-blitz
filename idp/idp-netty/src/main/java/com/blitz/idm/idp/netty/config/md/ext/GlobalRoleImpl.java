package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.config.GlobalRoleEnum;
import com.blitz.idm.idp.netty.config.md.ext.api.GlobalRole;
import com.blitz.idm.idp.netty.config.md.ext.api.SupportedOrgTypes;

import javax.xml.XMLConstants;

public class GlobalRoleImpl extends BaseAttributeMetadataExtensionImpl<GlobalRoleEnum> implements GlobalRole {

    private SupportedOrgTypes supportedOrgTypes;

    GlobalRoleImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        supportedOrgTypes = null;
    }

    public GlobalRoleImpl(GlobalRoleEnum id) {
        super(GlobalRole.BLITZ_MDEXT_NS, GlobalRole.ELEMENT_LOCAL_NAME, XMLConstants.DEFAULT_NS_PREFIX);
        supportedOrgTypes = null;
        if(id == null){
            throw new IllegalArgumentException("A global role ID is undefined");
        }
        setID(id);
    }

    @Override
    public SupportedOrgTypes getSupportedOrgTypes() {
        return supportedOrgTypes;
    }

    @Override
    public void setSupportedOrgTypes(SupportedOrgTypes supportedOrgTypes) {
        this.supportedOrgTypes = supportedOrgTypes;
    }
}

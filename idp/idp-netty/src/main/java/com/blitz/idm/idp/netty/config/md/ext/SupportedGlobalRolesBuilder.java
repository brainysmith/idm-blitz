package com.blitz.idm.idp.netty.config.md.ext;


import com.blitz.idm.idp.netty.config.md.ext.api.SupportedGlobalRoles;
import org.opensaml.xml.AbstractXMLObjectBuilder;

import javax.xml.XMLConstants;

public class SupportedGlobalRolesBuilder extends AbstractXMLObjectBuilder<SupportedGlobalRoles> {

    public SupportedGlobalRoles buildObject() {
        return buildObject(SupportedGlobalRoles.BLITZ_MDEXT_NS, SupportedGlobalRoles.ELEMENT_LOCAL_NAME, XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Override
    public SupportedGlobalRoles buildObject(String namespaceURI, String elementLocalName, String namespacePrefix) {
        return new SupportedGlobalRolesImpl(namespaceURI, elementLocalName, namespacePrefix);
    }
}

package com.blitz.idm.idp.netty.config.md.ext;

import org.opensaml.xml.AbstractXMLObjectBuilder;
import com.blitz.idm.idp.netty.config.md.ext.api.GlobalRole;

import javax.xml.XMLConstants;

public class GlobalRoleBuilder extends AbstractXMLObjectBuilder<GlobalRole> {

    public GlobalRole buildObject() {
        return buildObject(GlobalRole.BLITZ_MDEXT_NS, GlobalRole.ELEMENT_LOCAL_NAME, XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Override
    public GlobalRole buildObject(String namespaceURI, String elementLocalName, String namespacePrefix) {
        return new GlobalRoleImpl(namespaceURI, elementLocalName, namespacePrefix);
    }
}

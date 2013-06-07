package com.blitz.idm.idp.netty.config.md.ext;

import org.opensaml.xml.AbstractXMLObjectBuilder;
import com.blitz.idm.idp.netty.config.md.ext.api.SupportedOrgTypes;

import javax.xml.XMLConstants;

public class SupportedOrgTypesBuilder extends AbstractXMLObjectBuilder<SupportedOrgTypes> {

    public SupportedOrgTypes buildObject() {
        return buildObject(SupportedOrgTypes.BLITZ_MDEXT_NS, SupportedOrgTypes.ELEMENT_LOCAL_NAME, XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Override
    public SupportedOrgTypes buildObject(String namespaceURI, String elementLocalName, String namespacePrefix) {
        return new SupportedOrgTypesImpl(namespaceURI, elementLocalName, namespacePrefix);
    }
}

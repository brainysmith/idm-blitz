package com.blitz.idm.idp.netty.config.md.ext;

import org.opensaml.xml.AbstractXMLObjectBuilder;
import com.blitz.idm.idp.netty.config.md.ext.api.SupportedAssuranceLevels;

import javax.xml.XMLConstants;

public class SupportedAssuranceLevelsBuilder extends AbstractXMLObjectBuilder<SupportedAssuranceLevels> {

    public SupportedAssuranceLevels buildObject() {
        return buildObject(SupportedAssuranceLevels.BLITZ_MDEXT_NS, SupportedAssuranceLevels.ELEMENT_LOCAL_NAME, XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Override
    public SupportedAssuranceLevels buildObject(String namespaceURI, String elementLocalName, String namespacePrefix) {
        return new SupportedAssuranceLevelsImpl(namespaceURI, elementLocalName, namespacePrefix);
    }
}

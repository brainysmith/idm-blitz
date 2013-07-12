package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.netty.config.md.ext.api.AssuranceLevel;
import org.opensaml.xml.AbstractXMLObjectBuilder;

import javax.xml.XMLConstants;

public class AssuranceLevelBuilder extends AbstractXMLObjectBuilder<AssuranceLevel> {

    public AssuranceLevel buildObject() {
        return buildObject(AssuranceLevel.BLITZ_MDEXT_NS, AssuranceLevel.ELEMENT_LOCAL_NAME, XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Override
    public AssuranceLevel buildObject(String namespaceURI, String elementLocalName, String namespacePrefix) {
        return new AssuranceLevelImpl(namespaceURI, elementLocalName, namespacePrefix);
    }
}

package com.blitz.idm.idp.netty.config.md.ext;

import org.opensaml.xml.AbstractXMLObjectBuilder;
import com.blitz.idm.idp.netty.config.md.ext.api.OrgType;

import javax.xml.XMLConstants;

public class OrgTypeBuilder extends AbstractXMLObjectBuilder<OrgType> {

    public OrgType buildObject() {
        return buildObject(OrgType.BLITZ_MDEXT_NS, OrgType.ELEMENT_LOCAL_NAME, XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Override
    public OrgType buildObject(String namespaceURI, String elementLocalName, String namespacePrefix) {
        return new OrgTypeImpl(namespaceURI, elementLocalName, namespacePrefix);
    }
}

package com.blitz.idm.idp.netty.config.md.ext;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;

public class SupportedGlobalRolesMarshaller extends AbstractXMLObjectMarshaller {

    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element element) throws MarshallingException {
    }

    @Override
    protected void marshallElementContent(XMLObject xmlObject, Element element) throws MarshallingException {
    }
}

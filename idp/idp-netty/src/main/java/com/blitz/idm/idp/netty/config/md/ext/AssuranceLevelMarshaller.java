package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.netty.config.md.ext.api.AssuranceLevel;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;

public class AssuranceLevelMarshaller extends AbstractXMLObjectMarshaller {
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element element) throws MarshallingException {
        AssuranceLevel assuranceLevel = (AssuranceLevel)xmlObject;

        if(assuranceLevel.getID() != null){
            element.setAttribute(AssuranceLevel.ID_ATTRIBUTE_NAME, assuranceLevel.getID().getSysname());
        }
        else{
            throw new MarshallingException("Undefined ID attribute");
        }
    }

    @Override
    protected void marshallElementContent(XMLObject xmlObject, Element element) throws MarshallingException {
    }
}

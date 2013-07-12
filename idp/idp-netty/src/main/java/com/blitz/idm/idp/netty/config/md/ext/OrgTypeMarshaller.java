package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.netty.config.md.ext.api.OrgType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;

public class OrgTypeMarshaller extends AbstractXMLObjectMarshaller {

    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element element) throws MarshallingException {
        OrgType orgType = (OrgType)xmlObject;

        if(orgType.getID() != null){
            element.setAttribute(OrgType.ID_ATTRIBUTE_NAME, orgType.getID().getSysname());
        }
        else{
            throw new MarshallingException("Undefined ID attribute");
        }
    }

    @Override
    protected void marshallElementContent(XMLObject xmlObject, Element element) throws MarshallingException {
    }
}

package com.blitz.idm.idp.netty.config.md.ext;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;
import com.blitz.idm.idp.netty.config.md.ext.api.GlobalRole;

public class GlobalRoleMarshaller extends AbstractXMLObjectMarshaller {
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element element) throws MarshallingException {
        GlobalRole globalRole = (GlobalRole)xmlObject;

        if(globalRole.getID() != null){
            element.setAttribute(GlobalRole.ID_ATTRIBUTE_NAME, globalRole.getID().getSysname());
        }
        else{
            throw new MarshallingException("Undefined ID attribute");
        }
    }

    @Override
    protected void marshallElementContent(XMLObject xmlObject, Element element) throws MarshallingException {
    }
}

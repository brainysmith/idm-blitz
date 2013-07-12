package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.netty.config.md.ext.api.GlobalRole;
import com.blitz.idm.idp.netty.config.md.ext.api.SupportedGlobalRoles;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;

public class SupportedGlobalRolesUnmarshaller extends AbstractXMLObjectUnmarshaller {

    private static Logger log = LoggerFactory.getLogger(SupportedGlobalRolesUnmarshaller.class);

    @Override
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject) throws UnmarshallingException
    {
        SupportedGlobalRoles supportedGlobalRoles = (SupportedGlobalRoles)parentXMLObject;

        if(childXMLObject instanceof GlobalRole){
            GlobalRole globalRole = (GlobalRole)childXMLObject;

            if(!supportedGlobalRoles.getGlobalRoles().contains(globalRole)){
                supportedGlobalRoles.getGlobalRoles().add(globalRole);
            }
        }
        else{
            throw new UnmarshallingException("An unknown child element " + childXMLObject.getElementQName());
        }
    }

    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attr) throws UnmarshallingException {
        throw new UnmarshallingException("An unknown attribute " + attr.getName());
    }

    @Override
    protected void processElementContent(XMLObject xmlObject, String s) {
        log.warn("{} contain some content", SupportedGlobalRoles.ELEMENT_LOCAL_NAME);
    }
}

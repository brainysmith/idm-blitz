package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.config.GlobalRoleEnum;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import com.blitz.idm.idp.netty.config.md.ext.api.GlobalRole;
import com.blitz.idm.idp.netty.config.md.ext.api.SupportedOrgTypes;

public class GlobalRoleUnmarshaller extends AbstractXMLObjectUnmarshaller {

    private static Logger log = LoggerFactory.getLogger(GlobalRoleUnmarshaller.class);

    @Override
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject) throws UnmarshallingException {
        GlobalRole globalRole = (GlobalRole)parentXMLObject;

        if(childXMLObject instanceof SupportedOrgTypes) {
            globalRole.setSupportedOrgTypes((SupportedOrgTypes) childXMLObject);
        }
        else {
            throw new UnmarshallingException(GlobalRole.ELEMENT_LOCAL_NAME + " contains unknown child element");
        }
    }

    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attr) throws UnmarshallingException {
        GlobalRole globalRole = (GlobalRole)xmlObject;

        if(attr.getName().equals(GlobalRole.ID_ATTRIBUTE_NAME)){
            if(GlobalRoleEnum.lookup(attr.getValue()) != null){
                globalRole.setID(GlobalRoleEnum.lookup(attr.getValue()));
            }
            else{
                throw new UnmarshallingException("Invalid value of global role ID " + attr.getValue());
            }
        }
        else{
            throw new UnmarshallingException("Unsupported attribute '" + attr.getName() + "' of global role element");
        }
    }

    @Override
    protected void processElementContent(XMLObject xmlObject, String s){
        log.warn("{} contain some content", GlobalRole.ELEMENT_LOCAL_NAME);
    }
}

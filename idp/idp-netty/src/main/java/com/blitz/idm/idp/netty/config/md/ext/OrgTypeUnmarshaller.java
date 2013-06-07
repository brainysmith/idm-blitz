package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.config.OrganizationTypeEnum;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import com.blitz.idm.idp.netty.config.md.ext.api.OrgType;

public class OrgTypeUnmarshaller extends AbstractXMLObjectUnmarshaller {

    private static Logger log = LoggerFactory.getLogger(OrgTypeUnmarshaller.class);

    @Override
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject) throws UnmarshallingException {
        throw new UnmarshallingException(OrgType.ELEMENT_LOCAL_NAME + " contains unknown child element");
    }

    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attr) throws UnmarshallingException {
        OrgType orgType = (OrgType)xmlObject;

        if(attr.getName().equals(OrgType.ID_ATTRIBUTE_NAME)){
            if(OrganizationTypeEnum.lookup(attr.getValue()) != null){
                orgType.setID(OrganizationTypeEnum.lookup(attr.getValue()));
            }
            else{
                throw new UnmarshallingException("Invalid value of organization type ID " + attr.getValue());
            }
        }
        else{
            throw new UnmarshallingException("Unsupported attribute '" + attr.getName() + "' of organization type element");
        }
    }

    @Override
    protected void processElementContent(XMLObject xmlObject, String s){
        log.warn("{} contain some content", OrgType.ELEMENT_LOCAL_NAME);
    }
}

package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.netty.config.md.ext.api.OrgType;
import com.blitz.idm.idp.netty.config.md.ext.api.SupportedOrgTypes;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;

public class SupportedOrgTypesUnmarshaller extends AbstractXMLObjectUnmarshaller {

    private static Logger log = LoggerFactory.getLogger(SupportedOrgTypesUnmarshaller.class);

    @Override
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject) throws UnmarshallingException
    {
        SupportedOrgTypes supportedOrgTypes = (SupportedOrgTypes)parentXMLObject;

        if(childXMLObject instanceof OrgType){
            OrgType orgType = (OrgType)childXMLObject;

            if(!supportedOrgTypes.getOrgTypes().contains(orgType)){
                supportedOrgTypes.getOrgTypes().add(orgType);
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
        log.warn("{} contain some content", SupportedOrgTypes.ELEMENT_LOCAL_NAME);
    }
}

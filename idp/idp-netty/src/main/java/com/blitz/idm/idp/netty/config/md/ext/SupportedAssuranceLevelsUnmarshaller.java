package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.netty.config.md.ext.api.AssuranceLevel;
import com.blitz.idm.idp.netty.config.md.ext.api.SupportedAssuranceLevels;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;

public class SupportedAssuranceLevelsUnmarshaller extends AbstractXMLObjectUnmarshaller {

    private static Logger log = LoggerFactory.getLogger(SupportedAssuranceLevelsUnmarshaller.class);

    @Override
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject) throws UnmarshallingException {
        SupportedAssuranceLevels supportedAssuranceLevels = (SupportedAssuranceLevels)parentXMLObject;

        if(childXMLObject instanceof AssuranceLevel){
            AssuranceLevel assuranceLevel = (AssuranceLevel)childXMLObject;

            if(!supportedAssuranceLevels.getAssuranceLevels().contains(assuranceLevel)){
                supportedAssuranceLevels.getAssuranceLevels().add(assuranceLevel);
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
        log.warn("{} contain some content", SupportedAssuranceLevels.ELEMENT_LOCAL_NAME);
    }
}

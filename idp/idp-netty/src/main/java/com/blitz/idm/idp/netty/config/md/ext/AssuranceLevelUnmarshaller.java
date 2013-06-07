package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.config.AssuranceLevelEnum;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import com.blitz.idm.idp.netty.config.md.ext.api.AssuranceLevel;

public class AssuranceLevelUnmarshaller extends AbstractXMLObjectUnmarshaller {

    private static Logger log = LoggerFactory.getLogger(AssuranceLevelUnmarshaller.class);

    @Override
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject) throws UnmarshallingException {
        throw new UnmarshallingException(AssuranceLevel.ELEMENT_LOCAL_NAME + " contains unknown child element");
    }

    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attr) throws UnmarshallingException {
        AssuranceLevel assuranceLevel = (AssuranceLevel)xmlObject;

        if(attr.getName().equals(AssuranceLevel.ID_ATTRIBUTE_NAME)){
            if(AssuranceLevelEnum.lookup(attr.getValue()) != null){
                assuranceLevel.setID(AssuranceLevelEnum.lookup(attr.getValue()));
            }
            else{
                throw new UnmarshallingException("Invalid value of assurance level ID " + attr.getValue());
            }
        }
        else{
            throw new UnmarshallingException("Unsupported attribute '" + attr.getName() + "' of assurance level element");
        }
    }

    @Override
    protected void processElementContent(XMLObject xmlObject, String s){
        log.warn("{} contain some content", AssuranceLevel.ELEMENT_LOCAL_NAME);
    }
}

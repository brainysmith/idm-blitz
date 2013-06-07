package com.blitz.idm.idp.netty.config.md.atr;

/**
 * Created by IntelliJ IDEA.
 * User: vkarpov
 * Date: 10.11.11
 * Time: 20:02
 * To change this template use File | Settings | File Templates.
 */

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;
import com.blitz.idm.idp.netty.config.md.atr.api.Authority;

public class AuthorityUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
    }

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
/*        Authority authority = (Authority)xmlObject;

        if(attribute.getName().equals(Authority.ATTRIBUTE_NAME_SYSTEM)){
            if(DeviceTypeID.valueOf(attribute.getValue()) != null){
                authority.setSystem(attribute.getValue());
            }
            else{
                throw new UnmarshallingException("Invalid value of system attribute '" + attribute.getValue() + "'");
            }
        }
        else{
            throw new UnmarshallingException("Unsupported attribute '" + attribute.getName() + "' of authority element");
        }*/
    }

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject xmlObject, String elementContent) {
        Authority authority = (Authority)xmlObject;
        authority.setValue(elementContent);
    }
}

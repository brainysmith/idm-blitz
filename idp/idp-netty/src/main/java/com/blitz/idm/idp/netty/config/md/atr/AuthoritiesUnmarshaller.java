package com.blitz.idm.idp.netty.config.md.atr;

/**
 * Created by IntelliJ IDEA.
 * User: vkarpov
 * Date: 11.11.11
 * Time: 11:32
 * To change this template use File | Settings | File Templates.
 */

import com.blitz.idm.idp.netty.config.md.atr.api.Authorities;
import com.blitz.idm.idp.netty.config.md.atr.api.Authority;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

public class AuthoritiesUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
        Authorities authorities = (Authorities)parentXMLObject;

        if(childXMLObject instanceof Authority){
            Authority authority = (Authority)childXMLObject;

            if(!authorities.getAuthorities().contains(authority)){
                authorities.getAuthorities().add(authority);
            }
        }
        else{
            throw new UnmarshallingException("An unknown child element " + childXMLObject.getElementQName());
        }
    }

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
    }

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject xmlObject, String elementContent) {
    }
}

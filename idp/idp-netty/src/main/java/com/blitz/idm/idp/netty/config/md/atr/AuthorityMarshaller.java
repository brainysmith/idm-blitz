package com.blitz.idm.idp.netty.config.md.atr;

/**
 * Created by IntelliJ IDEA.
 * User: vkarpov
 * Date: 10.11.11
 * Time: 20:36
 * To change this template use File | Settings | File Templates.
 */

import com.blitz.idm.idp.netty.config.md.atr.api.Authority;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

public class AuthorityMarshaller extends AbstractXMLObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        Authority authority = (Authority)xmlObject;

        if(authority.getSystem() != null){
            domElement.setAttribute(Authority.ATTRIBUTE_NAME_SYSTEM, authority.getSystem().toString());
        }
        else{
            throw new MarshallingException("Undefined 'system' attribute");
        }
    }

    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException {
        Authority authority = (Authority)xmlObject;
        XMLHelper.appendTextContent(domElement, authority.getValue());
    }
}

package com.blitz.idm.idp.netty.config.md.atr;

/**
 * Created by IntelliJ IDEA.
 * User: vkarpov
 * Date: 11.11.11
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */

import com.blitz.idm.idp.netty.config.md.atr.api.Authorities;
import org.opensaml.xml.AbstractXMLObjectBuilder;

public class AuthoritiesBuilder  extends AbstractXMLObjectBuilder<Authorities> {

    /** {@inheritDoc} */
    public Authorities buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new AuthoritiesImpl(namespaceURI, localName, namespacePrefix);
    }

}

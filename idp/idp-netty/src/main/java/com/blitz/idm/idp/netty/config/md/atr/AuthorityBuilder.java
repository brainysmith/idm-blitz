package com.blitz.idm.idp.netty.config.md.atr;

/**
 * Created by IntelliJ IDEA.
 * User: vkarpov
 * Date: 10.11.11
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */

import com.blitz.idm.idp.netty.config.md.atr.api.Authority;
import org.opensaml.xml.AbstractXMLObjectBuilder;

public class AuthorityBuilder extends AbstractXMLObjectBuilder<Authority> {

    /** {@inheritDoc} */
    public Authority buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new AuthorityImpl(namespaceURI, localName, namespacePrefix);
    }

}

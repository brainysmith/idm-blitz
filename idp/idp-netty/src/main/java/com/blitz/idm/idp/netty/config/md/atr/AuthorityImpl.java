package com.blitz.idm.idp.netty.config.md.atr;

/**
 * Created by IntelliJ IDEA.
 * User: vkarpov
 * Date: 10.11.11
 * Time: 19:55
 * To change this template use File | Settings | File Templates.
 */

import org.opensaml.xml.schema.impl.XSAnyImpl;
import com.blitz.idm.idp.netty.config.md.atr.api.Authority;

public class AuthorityImpl extends XSAnyImpl implements Authority {

    private String value;

    private String system;

    protected AuthorityImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setSystem(String system) {
        this.system = system;
    }

    @Override
    public String getSystem() {
        return system;
    }
}

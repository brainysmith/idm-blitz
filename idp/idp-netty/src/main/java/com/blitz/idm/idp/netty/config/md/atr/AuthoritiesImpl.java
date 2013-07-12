package com.blitz.idm.idp.netty.config.md.atr;

/**
 * Created by IntelliJ IDEA.
 * User: vkarpov
 * Date: 11.11.11
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */

import com.blitz.idm.idp.netty.config.md.atr.api.Authorities;
import com.blitz.idm.idp.netty.config.md.atr.api.Authority;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.impl.XSAnyImpl;

import java.util.ArrayList;
import java.util.List;

public class AuthoritiesImpl extends XSAnyImpl implements Authorities {

    private List<Authority> authorities;

    private final int AUTHORITY_ARRAY_INIT_CAPACITY = 15;

    protected AuthoritiesImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        authorities= new ArrayList<Authority>(AUTHORITY_ARRAY_INIT_CAPACITY);
    }

    @Override
    public List<Authority> getAuthorities() {
        return authorities;
    }

    @Override
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> result = new ArrayList<XMLObject>(getAuthorities().size() + super.getOrderedChildren().size());
        result.addAll(getAuthorities());
        result.addAll(super.getOrderedChildren());
        return result;
    }
}

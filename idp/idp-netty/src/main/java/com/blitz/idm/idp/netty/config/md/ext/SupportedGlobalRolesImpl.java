package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.config.GlobalRoleEnum;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.validation.AbstractValidatingXMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blitz.idm.idp.netty.config.md.ext.api.GlobalRole;
import com.blitz.idm.idp.netty.config.md.ext.api.SupportedGlobalRoles;

import java.util.ArrayList;
import java.util.List;

public class SupportedGlobalRolesImpl extends AbstractValidatingXMLObject implements SupportedGlobalRoles {

    private static Logger log = LoggerFactory.getLogger(SupportedGlobalRolesImpl.class);

    private ArrayList<GlobalRole> globalRoles;

    SupportedGlobalRolesImpl(String namespaceURI, String elementLocalName, String namespacePrefix){
        super(namespaceURI, elementLocalName, namespacePrefix);
        globalRoles = new ArrayList<GlobalRole>(GlobalRoleEnum.values().length);

        log.debug("SupportedGlobalRolesImpl created");
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void setValue(String s) {
    }

    @Override
    public List<XMLObject> getOrderedChildren() {
        return null;
    }

    @Override
    public List<GlobalRole> getGlobalRoles() {
        return globalRoles;
    }
}

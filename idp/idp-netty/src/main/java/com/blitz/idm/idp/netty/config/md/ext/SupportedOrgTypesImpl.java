package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.config.OrganizationTypeEnum;
import com.blitz.idm.idp.netty.config.md.ext.api.OrgType;
import com.blitz.idm.idp.netty.config.md.ext.api.SupportedOrgTypes;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.validation.AbstractValidatingXMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SupportedOrgTypesImpl extends AbstractValidatingXMLObject implements SupportedOrgTypes {

    private static Logger log = LoggerFactory.getLogger(SupportedOrgTypesImpl.class);

    private ArrayList<OrgType> orgTypes;

    SupportedOrgTypesImpl(String namespaceURI, String elementLocalName, String namespacePrefix){
        super(namespaceURI, elementLocalName, namespacePrefix);
        orgTypes = new ArrayList<OrgType>(OrganizationTypeEnum.values().length);

        log.debug("SupportedOrgTypesImpl created");
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
    public List<OrgType> getOrgTypes() {
        return orgTypes;
    }
}

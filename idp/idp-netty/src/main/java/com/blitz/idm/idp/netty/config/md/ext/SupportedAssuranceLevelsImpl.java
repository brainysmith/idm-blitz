package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.config.AssuranceLevelEnum;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.validation.AbstractValidatingXMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blitz.idm.idp.netty.config.md.ext.api.AssuranceLevel;
import com.blitz.idm.idp.netty.config.md.ext.api.SupportedAssuranceLevels;

import java.util.ArrayList;
import java.util.List;

public class SupportedAssuranceLevelsImpl extends AbstractValidatingXMLObject implements SupportedAssuranceLevels {

    private static Logger log = LoggerFactory.getLogger(SupportedAssuranceLevelsImpl.class);

    private ArrayList<AssuranceLevel> assuranceLevels;

    SupportedAssuranceLevelsImpl(String namespaceURI, String elementLocalName, String namespacePrefix){
        super(namespaceURI, elementLocalName, namespacePrefix);
        assuranceLevels = new ArrayList<AssuranceLevel>(AssuranceLevelEnum.values().length);
    }

    @Override
    public List<XMLObject> getOrderedChildren() {
        return null;
    }

    @Override
    public List<AssuranceLevel> getAssuranceLevels() {
        return assuranceLevels;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void setValue(String s) {
    }
}

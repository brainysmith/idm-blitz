package com.blitz.idm.idp.netty.config.md.ext;

import com.blitz.idm.idp.config.AssuranceLevelEnum;
import com.blitz.idm.idp.netty.config.md.ext.api.AssuranceLevel;

import javax.xml.XMLConstants;

public class AssuranceLevelImpl extends BaseAttributeMetadataExtensionImpl<AssuranceLevelEnum> implements AssuranceLevel {

    AssuranceLevelImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    public AssuranceLevelImpl(AssuranceLevelEnum id) {
        super(AssuranceLevel.BLITZ_MDEXT_NS, AssuranceLevel.ELEMENT_LOCAL_NAME, XMLConstants.DEFAULT_NS_PREFIX);
        if(id == null){
            throw new IllegalArgumentException("A assurence level ID is undefined");
        }
        setID(id);
    }
}

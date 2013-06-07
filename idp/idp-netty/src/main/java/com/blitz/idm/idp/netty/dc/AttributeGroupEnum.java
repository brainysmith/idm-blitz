package com.blitz.idm.idp.netty.dc;

import com.blitz.idm.idp.dc.AttributeGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * User: agumerov
 * Date: 14.12.12
 */
public enum AttributeGroupEnum implements AttributeGroup {
    PRINCIPAL_INFO("prlInfo"),
    PRINCIPAL_ORG_INFO("prlOrgInfo"),
    PERSON_ID_TOKEN("psoIdToken"),
    STAFF_UNIT_ID_TOKEN("stuIdToken"),

    PERSON_SHORT_INFO("psoShort"),
    PERSON_FULL_INFO("psoFull"),

    STAFF_UNIT_SHORT_INFO("stuShort"),
    STAFF_UNIT_FULL_INFO("stuFull"),

    STAFF_UNIT_AUTHORITIES_INFO("stuAuthz"),
    PERSON_AUTHORITIES_INFO("psoAuthz");

    //AUTHN_CERTIFICATE("authnCert");

    private static Map<String, AttributeGroupEnum> lookupMap;

    static {
        lookupMap = new HashMap<String, AttributeGroupEnum>(values().length);
        for (AttributeGroupEnum element : values()) {
            lookupMap.put(element.sysname, element);
        }
    }

    public static AttributeGroupEnum lookup(String sysname) {
        return lookupMap.get(sysname);
    }

    private final String sysname;

    private AttributeGroupEnum(String sysname) {
        this.sysname = sysname;
    }

    public String getSysname() {
        return sysname;
    }
}

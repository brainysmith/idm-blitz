package com.blitz.idm.idp.config;


import java.util.HashMap;
import java.util.Map;

public enum OrganizationTypeEnum {
    AGENCY("A", "OrganizationTypeEnum.Agency"),
    BUSINESS_PERSON("B", "OrganizationTypeEnum.Businessman"),
    LEGAL_ORGANIZATION("L", "OrganizationTypeEnum.LegalOrganization"),
    VIRTUAL_ORGANIZATION("V", "OrganizationTypeEnum.VirtualOrganization");

    private static Map<String, OrganizationTypeEnum> lookupMap;
    private static String possibleValues;

    static {
        lookupMap = new HashMap<String, OrganizationTypeEnum>(values().length);
        StringBuilder sb = (new StringBuilder()).append("[");

        for (OrganizationTypeEnum element : values()) {
            lookupMap.put(element.sysname, element);
            sb.append(element.getSysname()).append(";");
        }

        possibleValues = sb.append("]").toString();
    }

    public static OrganizationTypeEnum lookup(String sysname) {
        return lookupMap.get(sysname);
    }

    private final String sysname;
    private final String msgKey;

    private OrganizationTypeEnum(String sysname, String msgKey) {
        this.sysname = sysname;
        this.msgKey = msgKey;
    }

    public String getSysname() {
        return sysname;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public static String getPossibleValues() {
        return possibleValues;
    }
}

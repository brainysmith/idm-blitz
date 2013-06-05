package com.blitz.idm.idp.config;

import java.util.HashMap;
import java.util.Map;

public enum AuthnMethodEnum {
    PWD("PWD"),
    DS("DS");

    private static Map<String, AuthnMethodEnum> lookupMap;
    private static String possibleValues;

    static {
        lookupMap = new HashMap<String, AuthnMethodEnum>(values().length);
        StringBuilder sb = (new StringBuilder()).append("[");

        for (AuthnMethodEnum element : values()) {
            lookupMap.put(element.getSysname(), element);
            sb.append(element.getSysname()).append(";");
        }

        possibleValues = sb.append("]").toString();
    }

    private String msgKey;

    private AuthnMethodEnum(String msgKey) {
        this.msgKey = this.getClass().getSimpleName() + "." + msgKey;
    }

    public String getSysname() {
        return this.name();
    }


    public String getMsgKey() {
        return msgKey;
    }

    public static AuthnMethodEnum lookup(String sysname) {
        return lookupMap.get(sysname);
    }

    public static String getPossibleValues() {
        return possibleValues;
    }
}

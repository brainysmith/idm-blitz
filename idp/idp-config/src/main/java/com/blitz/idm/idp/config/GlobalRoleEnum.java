package com.blitz.idm.idp.config;

import java.util.HashMap;
import java.util.Map;

public enum GlobalRoleEnum {
    PERSON("P", "GlobalRoleEnum.Person"),
    EMPLOYEE("E", "GlobalRoleEnum.Employee"),
    IT_SYSTEM("S", "GlobalRoleEnum.ItSystem");

    private static Map<String, GlobalRoleEnum> lookupMap;

    static {
        lookupMap = new HashMap<String, GlobalRoleEnum>(values().length);
        for (GlobalRoleEnum element : values()) {
            lookupMap.put(element.sysname, element);
        }
    }

    public static GlobalRoleEnum lookup(String sysname) {
        return lookupMap.get(sysname);
    }

    private final String sysname;
    private final String msgKey;

    private GlobalRoleEnum(String sysname, String msgKey) {
        this.sysname = sysname;
        this.msgKey = msgKey;
    }

    public String getSysname() {
        return sysname;
    }

    public String getMsgKey() {
        return msgKey;
    }
}

package com.blitz.idm.idp.config;

import java.util.HashMap;
import java.util.Map;

public enum AssuranceLevelEnum {
    ASSURANCE_LEVEL_10(10, "AssuranceLevelEnum.AssuranceLevel10"),
    ASSURANCE_LEVEL_20(20, "AssuranceLevelEnum.AssuranceLevel20"),
    ASSURANCE_LEVEL_30(30, "AssuranceLevelEnum.AssuranceLevel30"),
    ASSURANCE_LEVEL_40(40, "AssuranceLevelEnum.AssuranceLevel40");

    private static Map<String, AssuranceLevelEnum> lookupMap;

    static {
        lookupMap = new HashMap<String, AssuranceLevelEnum>(values().length);
        for (AssuranceLevelEnum element : values()) {
            lookupMap.put(element.sysname, element);
        }
    }

    public static AssuranceLevelEnum lookup(String sysname) {
        return lookupMap.get(sysname);
    }

    private final int level;
    private final String sysname;
    private final String msgKey;

    private AssuranceLevelEnum(int level, String msgKey) {
        this.level = level;
        this.sysname = String.valueOf(level);
        this.msgKey = msgKey;
    }

    public String getSysname() {
        return sysname;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public int getLevel() {
        return level;
    }

    public boolean isLessThan(AssuranceLevelEnum assuranceLevel) {
        if (assuranceLevel == null)
            throw new NullPointerException("Assurance level can't be null");
        return (this.level < assuranceLevel.getLevel());
    }
}

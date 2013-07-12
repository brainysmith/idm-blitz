package com.blitz.idm.idp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: agumerov
 */
public enum ContactTypeEnum {
    PHONE("PHN", false, false,"ContactTypeEnum.Phone"),
    MOBILE("MBT", false, true, "ContactTypeEnum.Mobile"),
    EMAIL("EML", false, true, "ContactTypeEnum.Email"),
    CORPORATE_EMAIL("CEM", true, false, "ContactTypeEnum.CorporateEmail");

    private final static Logger logger = LoggerFactory.getLogger(ContactTypeEnum.class);
    private static Map<String, ContactTypeEnum> lookupMap;

    static {
        lookupMap = new HashMap<String, ContactTypeEnum>(values().length);
        for (ContactTypeEnum element : values()) {
            lookupMap.put(element.sysname, element);
        }
    }

    public static ContactTypeEnum lookup(String sysname) {
        return lookupMap.get(sysname);
    }

    private final String sysname;
    private final boolean isCorporate;
    private final boolean needVerification;
    private final String msgKey;

    private ContactTypeEnum(String sysname, boolean isCorporate, boolean needVerification, String msgKey) {
        this.sysname = sysname;
        this.isCorporate = isCorporate;
        this.needVerification = needVerification;
        this.msgKey = msgKey;
    }


    public String getSysname() {
        return sysname;
    }

    public boolean isNeedVerification() {
        return needVerification;
    }

    public boolean isCorporate() {
        return isCorporate;
    }

    public String getMsgKey() {
        return msgKey;
    }

}
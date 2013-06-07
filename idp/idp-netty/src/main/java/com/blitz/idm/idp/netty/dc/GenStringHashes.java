package com.blitz.idm.idp.netty.dc;

import com.blitz.idm.idp.netty.config.md.atr.api.Authorities;
import com.blitz.idm.idp.netty.config.md.atr.api.Authority;
import com.blitz.idm.idp.netty.config.md.atr.api.BaseAttribute;

import java.util.HashMap;
import java.util.Map;

/**
 * User: agumerov
 * Date: 19.12.12
 */
public class GenStringHashes {

    public static void main(String[] args) {
        //printHashCodes();
        //printXmlAuthorities();
    }

    public static void printHashCodes() {
        AttributeEnum.printHashCodes();
    }

    public static void printXmlAuthorities() {
        String xmlAuthorities = "";
        Map<String, String> authorityMap = new HashMap<String, String>();
        authorityMap.put("BLITZ", "AUTHZ_ADMIN");
        authorityMap.put("BLITZ", "ORG_ADMIN");
        for (Map.Entry<String, String> authz : authorityMap.entrySet()) {
            String xmlAuthority = String.format("<%1$s:%2$s system=\"%3$s\">%4$s</%1$s:%2$s>",
                    BaseAttribute.PREFIX, Authority.TYPE_LOCAL_NAME, authz.getKey(), authz.getValue());
            xmlAuthorities = xmlAuthorities + xmlAuthority;

        }
        xmlAuthorities = String.format("<%1$s:%2$s xmlns:%1$s=\"%3$s\">%4$s</%1$s:%2$s>", BaseAttribute.PREFIX, Authorities.TYPE_LOCAL_NAME, BaseAttribute.NAMESPACE, xmlAuthorities);
        System.out.println(xmlAuthorities);
    }

}

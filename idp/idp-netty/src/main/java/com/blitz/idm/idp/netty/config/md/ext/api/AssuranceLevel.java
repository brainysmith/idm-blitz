package com.blitz.idm.idp.netty.config.md.ext.api;

import com.blitz.idm.idp.config.AssuranceLevelEnum;

import javax.xml.namespace.QName;

/**
 * Created by IntelliJ IDEA.
 * User: izaytsev
 * Date: 20.02.12
 * Time: 12:56
 */
public interface AssuranceLevel extends BaseAttributeMetadataExtension<AssuranceLevelEnum>{

    public static final String ELEMENT_LOCAL_NAME = "AssuranceLevel";

    public static final QName ELEMENT_NAME = new QName(BLITZ_MDEXT_NS, ELEMENT_LOCAL_NAME);
}

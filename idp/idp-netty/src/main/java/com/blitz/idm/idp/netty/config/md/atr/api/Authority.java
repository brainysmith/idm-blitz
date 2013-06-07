package com.blitz.idm.idp.netty.config.md.atr.api;

/**
 * Created by IntelliJ IDEA.
 * User: vkarpov
 * Date: 10.11.11
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */

import javax.xml.namespace.QName;

public interface Authority extends BaseAttribute {

    public static final String TYPE_LOCAL_NAME = "Authority";

    public static final QName TYPE_NAME = new QName(NAMESPACE, TYPE_LOCAL_NAME, PREFIX);

    public static final String ATTRIBUTE_NAME_SYSTEM = "system";

    public void setValue(String value);

    public String getValue();

    public void setSystem(String system);

    public String getSystem();
}

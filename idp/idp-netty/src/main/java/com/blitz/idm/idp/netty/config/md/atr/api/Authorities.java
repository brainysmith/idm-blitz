package com.blitz.idm.idp.netty.config.md.atr.api;

/**
 * Created by IntelliJ IDEA.
 * User: vkarpov
 * Date: 11.11.11
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */

import javax.xml.namespace.QName;
import java.util.List;

public interface Authorities extends BaseAttribute {

    public static final String TYPE_LOCAL_NAME = "Authorities";

    public static final QName TYPE_NAME = new QName(NAMESPACE, TYPE_LOCAL_NAME, PREFIX);

    public List<Authority> getAuthorities();

}

package com.blitz.idm.idp.netty.config.md.atr.api;

/**
 * Created by IntelliJ IDEA.
 * User: vkarpov
 * Date: 11.11.11
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */

import org.opensaml.common.SAMLObject;

public interface BaseAttribute extends SAMLObject {

    public static final String NAMESPACE = "urn:blitz:shibboleth:2.0:attribute:encoder";

    public static final String PREFIX = "blitz-encoder";

}

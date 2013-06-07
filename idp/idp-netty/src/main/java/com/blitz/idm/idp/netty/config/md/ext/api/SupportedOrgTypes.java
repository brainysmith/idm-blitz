package com.blitz.idm.idp.netty.config.md.ext.api;

import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.validation.ValidatingXMLObject;

import javax.xml.namespace.QName;
import java.util.List;

public interface SupportedOrgTypes extends ValidatingXMLObject, XSString {

    public static final String BLITZ_MDEXT_NS = "urn:blitz:shibboleth:2.0:mdext";

    public static final String ELEMENT_LOCAL_NAME = "SupportedOrgTypes";

    public static  final QName ELEMENT_NAME = new QName(BLITZ_MDEXT_NS, ELEMENT_LOCAL_NAME);

    public List<OrgType> getOrgTypes();
}

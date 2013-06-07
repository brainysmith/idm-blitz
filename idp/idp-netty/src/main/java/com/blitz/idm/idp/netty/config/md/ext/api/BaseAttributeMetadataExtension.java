package com.blitz.idm.idp.netty.config.md.ext.api;

import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.validation.ValidatingXMLObject;

public interface BaseAttributeMetadataExtension<T> extends ValidatingXMLObject, XSString {

        public static final String BLITZ_MDEXT_NS = "urn:blitz:shibboleth:2.0:mdext";

        public static final String ID_ATTRIBUTE_NAME = "ID";

        T getID();

        void setID(T id);
}

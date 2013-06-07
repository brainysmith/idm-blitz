package com.blitz.idm.idp.netty.config.md.ext;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.validation.AbstractValidatingXMLObject;
import com.blitz.idm.idp.netty.config.md.ext.api.BaseAttributeMetadataExtension;

import java.util.List;

public class BaseAttributeMetadataExtensionImpl<T> extends AbstractValidatingXMLObject implements BaseAttributeMetadataExtension<T> {

    protected BaseAttributeMetadataExtensionImpl(String namespaceURI, String elementLocalName, String namespacePrefix){
        super(namespaceURI, elementLocalName, namespacePrefix);
        ID = null;
    }

    @Override
    public List<XMLObject> getOrderedChildren() {
        return null;
    }

    @Override
    public T getID() {
        return ID;
    }

    @Override
    public void setID(T id) {
        ID = id;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void setValue(String s) {
    }

    public boolean equals(BaseAttributeMetadataExtensionImpl o)
    {
        if(o == null)
            return false;

        if(o == this)
            return true;

        if(o.getID() == null && getID() == null){
            return true;
        }

        if(getID() != null){
            return getID().equals(o.getID());
        }

        return false;
    }

    public boolean equals(Object o)
    {
        if(o instanceof BaseAttributeMetadataExtensionImpl){
            return equals((BaseAttributeMetadataExtensionImpl)o);
        }
        else{
            return false;
        }
    }

    private T ID;
}

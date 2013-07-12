package com.blitz.idm.servlet.config;


import net.javaforge.netty.servlet.bridge.util.Utils;

import javax.servlet.ServletContext;

public abstract class WebAppHandler<T, C> extends ParameterMap {

    private Class<? extends T> handlerClass;

    public WebAppHandler(String handlerName, Class<? extends T> handlerClass) {
        super(handlerName);
        this.handlerClass= handlerClass;
    }

    public Class<? extends T> getHandlerClass() {
        return handlerClass;
    }

    public T createNewHandlerInstance() {
        return Utils.newInstance(getHandlerClass());
    }

    abstract C createNewConfigInstance(ServletContext servletContext);


}

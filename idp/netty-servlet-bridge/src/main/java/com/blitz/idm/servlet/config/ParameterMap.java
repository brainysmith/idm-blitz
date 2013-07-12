package com.blitz.idm.servlet.config;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ParameterMap {

    private Map<String, String> initParameters = new HashMap<String, String>();
    private String name;

    public ParameterMap(String name) {
        this.name = name;
    }

    protected String getName() {
        return name;
    }

    protected Map<String, String> getParameters() {
        return Collections.unmodifiableMap(initParameters);
    }

    protected void addParameters(Map<String, String> initParameters) {
        this.initParameters.putAll(initParameters);
    }

    protected void addParameter(String name, String value) {
        this.initParameters.put(name, value);
    }

    protected String getParameter(String name) {
        if (this.initParameters == null)
            return null;

        return this.initParameters.get(name);
    }

    protected Enumeration getParameterNames() {
        return (this.initParameters == null ?
                Collections.<String>enumeration(Collections.<String>emptySet())
                : Collections.<String>enumeration(this.initParameters.keySet()));
    }


}

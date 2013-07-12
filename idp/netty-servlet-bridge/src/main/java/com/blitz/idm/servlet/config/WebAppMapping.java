package com.blitz.idm.servlet.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public abstract class WebAppMapping<T, C> extends WebAppHandler<T, C>{

    private static final Logger log = LoggerFactory.getLogger(WebAppMapping.class);

    private String[] sanitizedUrlPatterns;

    private Pattern[] regexPatterns;

    private boolean initialized = false;

    public WebAppMapping(Class<? extends T> handlerClass, String... urlPatterns) {
        super(handlerClass.getSimpleName(), handlerClass);
        if (urlPatterns == null || urlPatterns.length == 0)
            throw new IllegalStateException(
                    "No url patterns were assigned to handler: "
                            + handlerClass.getSimpleName());

        this.regexPatterns = new Pattern[urlPatterns.length];
        this.sanitizedUrlPatterns = new String[urlPatterns.length];

        for (int i = 0; i < urlPatterns.length; i++) {
            String regex = urlPatterns[i].replaceAll("\\*", ".*");
            this.regexPatterns[i] = Pattern.compile(regex);
            this.sanitizedUrlPatterns[i] = urlPatterns[i].replaceAll("\\*", "");
            if (this.sanitizedUrlPatterns[i].endsWith("/"))
                this.sanitizedUrlPatterns[i] = this.sanitizedUrlPatterns[i]
                        .substring(0, this.sanitizedUrlPatterns[i].length() - 1);
        }
    }

/*    public void init() {
        try {

            log.debug("Initializing handler: {}", this.handler.getHandlerClass());

            this.doInit();
            this.initialized = true;

        } catch (ServletException e) {

            this.initialized = false;
            log.error("Handler '" + this.handler.getHandlerClass()
                    + "' was not initialized!", e);
        }
    }

    public void destroy() {
        try {

            log.debug("Destroying handler: {}", this.handler.getHandlerClass());

            this.doDestroy();
            this.initialized = false;

        } catch (ServletException e) {

            this.initialized = false;
            log.error("Handler '" + this.handler.getHandlerClass()
                    + "' was not destroyed!", e);
        }
    }

    protected abstract void doInit() throws ServletException;

    protected abstract void doDestroy() throws ServletException;*/

    public boolean matchesUrlPattern(String uri) {
        return getMatchingUrlPattern(uri) != null;
    }

    public String getMatchingUrlPattern(String uri) {
        int indx = uri.indexOf('?');

        String path = indx != -1 ? uri.substring(0, indx) : uri.substring(0);
        if (path.endsWith("/"))
            path.substring(0, path.length() - 1);

        for (int i = 0; i < regexPatterns.length; i++) {
            Pattern pattern = regexPatterns[i];
            if (pattern.matcher(path).matches()) {
                return sanitizedUrlPatterns[i];
            }
        }

        return null;

    }

    public boolean isInitialized() {
        return initialized;
    }

}

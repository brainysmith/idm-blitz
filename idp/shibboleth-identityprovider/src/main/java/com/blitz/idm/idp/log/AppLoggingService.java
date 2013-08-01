package com.blitz.idm.idp.log;

import com.blitz.idm.idp.config.IdpApp;

/**
 *
 */
public class AppLoggingService {

    public AppLoggingService(){
        IdpApp.appLogger().info("Logger has been initialized.");
    }

}

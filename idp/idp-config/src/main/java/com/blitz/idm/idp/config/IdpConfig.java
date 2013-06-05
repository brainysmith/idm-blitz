package com.blitz.idm.idp.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class IdpConfig {
    public static final String CONFIG_FILE_NAME = "idp.properties";
    public static final String CONFIG_FILE_SUB_DIR = "conf";
    public static final String ENV_VAR_IDP_HOME = "IDP_HOME";
    public static String IDP_HOME_DIR = null;

    private static final Logger log = LoggerFactory.getLogger(IdpConfig.class);
    private static IdpConfig instance;
    private HashMap<IdpConfigParam, String> parameters = new HashMap<IdpConfigParam, String>();


    private IdpConfig(String configFilePath) {
        init(configFilePath);
    }

    private static IdpConfig getInstance() {
        if(instance == null) {
            if(instance == null) {
                String configFilePath =  getConfigFilePath();
                instance = new IdpConfig(configFilePath);
            }
        }
        return instance;
    }

    public static String getProperty(IdpConfigParam key) {
       return getInstance().parameters.get(key);
    }

    public static String getStringProperty(IdpConfigParam key) {
        String value = getProperty(key);
        if (value == null) {
            log.error("String configuration parameter {} not found", key.getSysname());
            throw new NullPointerException("String configuration parameter " + key.getSysname() + " not found.");
        }
        return value;
    }

    public static boolean getBooleanProperty(IdpConfigParam key) {
        String value = getProperty(key);
         if (value == null) {
             log.error("Boolean configuration parameter {} not found", key.getSysname());
             throw new NullPointerException("Boolean configuration parameter " + key.getSysname() + " not found.");
         }
        return Boolean.parseBoolean(value);
    }

    public static long getLongProperty(IdpConfigParam key) {
        String value = getProperty(key);
        if (value == null) {
            log.error("Long configuration parameter {} not found", key.getSysname());
            throw new NullPointerException("Long configuration parameter " + key.getSysname() + " not found.");
        }
        return Long.parseLong(value);
    }

    public static int getIntProperty(IdpConfigParam key) {
        String value = getProperty(key);
        if (value == null) {
            log.error("Int configuration parameter {} not found", key.getSysname());
            throw new NullPointerException("Int configuration parameter " + key.getSysname() + " not found.");
        }
        return Integer.parseInt(value);
    }

    private static String getConfigFilePath() {
        return getConfigDir() + CONFIG_FILE_NAME;
    }

    public static String getConfigDir() {
        return getIdpHomeDir() + CONFIG_FILE_SUB_DIR + "/";
    }

    public static String getIdpHomeDir() {
        if (IDP_HOME_DIR == null) {
            String idpHomeDir = System.getenv(ENV_VAR_IDP_HOME);
            if (idpHomeDir == null) {
                log.error("Environment variable {} is not set", ENV_VAR_IDP_HOME);
                throw new NullPointerException("Environment variable " + ENV_VAR_IDP_HOME + " is not set");
            }
            if (!idpHomeDir.endsWith("/")){
                idpHomeDir = idpHomeDir + "/";
            }
            IDP_HOME_DIR = idpHomeDir;
        }
        return IDP_HOME_DIR;
    }


    private void init(String configFilePath)  {
        try {
        BufferedReader fr = new BufferedReader(new FileReader(configFilePath));
        String line = "";
        while((line = fr.readLine()) != null) {
          if(!line.startsWith("#")) {
             String[] strArr = line.split("=",2);
             if(strArr != null && strArr.length == 2) {
                 String key = strArr[0];
                 String value = strArr[1];
                 if(key != null && !key.equals("") && value != null && !value.equals("")) {
                    parameters.put(IdpConfigParam.lookup(key.trim()), value.trim());
                 }
             }
          }
        }
        }catch (IOException e) {
            log.error("Exception occur : {}", e);
            throw new IllegalStateException("Exception occur : " + e.getStackTrace());
        }
    }


}

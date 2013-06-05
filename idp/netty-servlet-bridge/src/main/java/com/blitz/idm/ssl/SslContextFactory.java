package com.blitz.idm.ssl;

import com.blitz.idm.idp.config.IdpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class SslContextFactory {

    private static Logger log = LoggerFactory.getLogger(FakeKeyStore.class);

    //the sslContext should be reused on each connection
    private static SSLContext instance = null;
    private static final String NO_CA_VERIFICATION = "noCA";
    private static final String DEFAULT_SSL_CONTEXT_TYPE = "TLS";

    public static SSLContext getSslContext(String trustStoreType) {
        if (instance == null) {
            instance = generateSslContext(trustStoreType);
        }
        return instance;
    }

    private static SSLContext generateSslContext(String trustStoreType) {
        KeyManagerFactory kmf = getKeyManagerFactory();

        // Load the configured trust manager

        TrustManager[] tm = null;
        try {
            if (trustStoreType != null && trustStoreType.equals(NO_CA_VERIFICATION)){
                log.warn("HTTPS configured with no client side CA verification. Requires for client certifiate verification.");
                tm = new TrustManager[] {new NoCATrustManager()};
            } else {
                log.debug("Using default trust store for client side CA verification");
            }
            // Configure the SSL context
            SSLContext sslContext = SSLContext.getInstance(DEFAULT_SSL_CONTEXT_TYPE);
            sslContext.init(kmf.getKeyManagers(), tm, null);
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static KeyManagerFactory getKeyManagerFactory() {

        String keyStorePath = System.getProperty("https.keyStore");
        if (keyStorePath != null){
            return createKeyManagerFactory(keyStorePath);
        } else {
            // Load a generated key store
            log.warn("Using generated key with self signed certificate for HTTPS. This should not be used in production.");
            return FakeKeyStore.getKeyManagerFactory(IdpConfig.getConfigDir());
        }
    }


    private static KeyManagerFactory createKeyManagerFactory(String keyStorePath) {

        try {
            // Load the configured key store
            KeyStore keyStore = KeyStore.getInstance(System.getProperty("https.keyStoreType", "JKS"));
            char[] password = System.getProperty("https.keyStorePassword", "").toCharArray();
            String algorithm = System.getProperty("https.keyStoreAlgorithm", KeyManagerFactory.getDefaultAlgorithm());
            File file = new File(keyStorePath);
            if (file.isFile()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
                keyStore.load(fis, password);
                log.debug("Using HTTPS keystore at {}", file.getAbsolutePath());
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
                kmf.init(keyStore, password);
                return kmf;
            } else {
                log.error("It's not a file at {}", keyStorePath);
            }
        } catch (Exception e) {
            log.error("Error loading HTTPS keystore from {} : {}", keyStorePath, e);
        }
        return null;
    }

    private static KeyStore getKeyStore(String storePath, char[] passPhrase) throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance("JCEKS");
        FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(storePath);
            ks.load(fis, passPhrase);
            return ks;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

}

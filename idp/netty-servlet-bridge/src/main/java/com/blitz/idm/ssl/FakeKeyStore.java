package com.blitz.idm.ssl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.x509.*;

import javax.net.ssl.KeyManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * A fake key store
 */
public final class FakeKeyStore {
    private static Logger log = LoggerFactory.getLogger(FakeKeyStore.class);

    private static final String GeneratedKeyStore = "generated.keystore";
    private static final String CERTIFICATE_DN_NAME = "CN=localhost, OU=Unit Testing, O=Mavericks, L=Moon Base 1, ST=Cyberspace, C=CY";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String KEY_GENERATION_ALGORITHM = "RSA";
    private static final String KEY_ALIAS = "playgenerated";
    private static final char[] KEYSTORE_PASSWORD = "".toCharArray();
    private static final int KEY_SIZE = 1024;
    private static final String KEY_MANAGER_FACTORY_TYPE = "SunX509";
    private static final long CERTIFICATE_LIFETIME_IN_MILLIS = 50l * 365l * 24l * 60l * 60l * 1000l;


    public static KeyManagerFactory getKeyManagerFactory(String appPath) {

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            File keyStoreFile = new File(appPath, GeneratedKeyStore);
            if (!keyStoreFile.exists()) {

                log.info("Generating HTTPS key pair in " + keyStoreFile.getAbsolutePath() + " - this may take some time. If nothing happens, try moving the mouse/typing on the keyboard to generate some entropy.");

                // Generate the key pair
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_GENERATION_ALGORITHM);
                keyPairGenerator.initialize(KEY_SIZE);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                // Generate a self signed certificate
                X509Certificate cert = createSelfSignedCertificate(keyPair);

                // Create the key store, first set the store pass
                keyStore.load(null, KEYSTORE_PASSWORD);
                keyStore.setKeyEntry(KEY_ALIAS, keyPair.getPrivate(), KEYSTORE_PASSWORD, new X509Certificate[]{cert});
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(keyStoreFile);
                    keyStore.store(fos, KEYSTORE_PASSWORD);
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(keyStoreFile);
                    keyStore.load(fis, KEYSTORE_PASSWORD);
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            }

            // Load the key and certificate into a key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEY_MANAGER_FACTORY_TYPE);
            kmf.init(keyStore, KEYSTORE_PASSWORD);
            return kmf;
        } catch (Exception e) {
            log.error("Error loading fake key store", e);
        }
        return null;
    }

    private static X509Certificate createSelfSignedCertificate(KeyPair keyPair) {
        X509CertInfo certInfo = new X509CertInfo();
        try {
            // Serial number and version
            certInfo.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(new BigInteger(64, new SecureRandom())));
            certInfo.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));

            // Validity
            Date validFrom = new Date();
            Date validTo = new Date(validFrom.getTime() + CERTIFICATE_LIFETIME_IN_MILLIS);
            CertificateValidity validity = new CertificateValidity(validFrom, validTo);
            certInfo.set(X509CertInfo.VALIDITY, validity);

            // Subject and issuer
            X500Name owner = new X500Name(CERTIFICATE_DN_NAME);
            certInfo.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
            certInfo.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));

            // Key and algorithm
            certInfo.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
            AlgorithmId algorithm = new AlgorithmId(AlgorithmId.sha1WithRSAEncryption_oid);
            certInfo.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algorithm));

            // Create a new certificate and sign it
            X509CertImpl cert = new X509CertImpl(certInfo);
            cert.sign(keyPair.getPrivate(), SIGNATURE_ALGORITHM);

            // Since the SHA1withRSA provider may have a different algorithm ID to what we think it should be,
            // we need to reset the algorithm ID, and resign the certificate
            AlgorithmId actualAlgorithm = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
            certInfo.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, actualAlgorithm);
            X509CertImpl newCert = new X509CertImpl(certInfo);
            newCert.sign(keyPair.getPrivate(), SIGNATURE_ALGORITHM);
            return newCert;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}

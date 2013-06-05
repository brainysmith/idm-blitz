package com.blitz.idm.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

// Trust manager without CA verification
public class NoCATrustManager implements X509TrustManager {
        X509Certificate[] nullArray = new X509Certificate[]{};
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {};
        public void  checkServerTrusted(X509Certificate[] x509Certificates, String s) {};
        public X509Certificate[] getAcceptedIssuers(){return nullArray;};
}

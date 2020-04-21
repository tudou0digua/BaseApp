package com.demo.baseapp.base.http;

import com.demo.baseapp.base.util.LOGGER;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Represents an ordered list of {@link X509TrustManager}s with additive trust. If any one of the composed managers
 * trusts a certificate chain, then it is trusted by the composite manager.
 * <p>
 * This is necessary because of the fine-print on {@link }: Only the first instance of a particular key
 * and/or trust manager implementation type in the array is used. (For example, only the first
 * javax.net.ssl.X509KeyManager in the array will be used.)
 *
 * @author codyaray
 * @see <a href="http://stackoverflow.com/questions/1793979/registering-multiple-keystores-in-jvm">
 * http://stackoverflow.com/questions/1793979/registering-multiple-keystores-in-jvm
 * </a>
 * @since 4/22/2013
 */
@SuppressWarnings("unused")
public class CompositeX509TrustManager implements X509TrustManager {
    private final List<X509TrustManager> trustManagers;

    public CompositeX509TrustManager(List<X509TrustManager> trustManagers) {
        this.trustManagers = new ArrayList<>(trustManagers);
    }

    public CompositeX509TrustManager(KeyStore keystore) {
        List<X509TrustManager> trustManagers = new ArrayList<>();
        X509TrustManager defaultManager = getDefaultTrustManager();
        X509TrustManager otherManager = getTrustManager(keystore);
        if (defaultManager != null) {
            trustManagers.add(defaultManager);
        }
        if (otherManager != null) {
            trustManagers.add(otherManager);
        }
        this.trustManagers = trustManagers;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        int index = 0;
        for (X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkClientTrusted(chain, authType);
                LOGGER.e(SSLProvider.TAG, "checkClientTrusted index " + index + " success" + " | total" + trustManagers.size());
                return; // someone trusts them. success!
            } catch (CertificateException e) {
                // maybe someone else will trust them
                LOGGER.e(SSLProvider.TAG, "checkClientTrusted index " + index + e.getMessage() + " | total" + trustManagers.size());
            }
            index++;
        }

        throw new CertificateException("checkClientTrusted fail");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        int index = 0;
        for (X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkServerTrusted(chain, authType);
                LOGGER.e(SSLProvider.TAG, "checkServerTrusted index " + index + " success" + " | total" + trustManagers.size());
                return; // someone trusts them. success!
            } catch (CertificateException e) {
                LOGGER.e(SSLProvider.TAG, "checkServerTrusted index " + index + e.getMessage() + " | total" + trustManagers.size());
                // maybe someone else will trust them
            }
            index++;
        }

        throw new CertificateException("checkServerTrusted fail");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        List<X509Certificate> certificates = new LinkedList<X509Certificate>();
        for (X509TrustManager trustManager : trustManagers) {
            certificates.addAll(Arrays.asList(trustManager.getAcceptedIssuers()));
        }
        return certificates.toArray(new X509Certificate[0]);
    }

    public static TrustManager[] getTrustManagers(KeyStore keyStore) {
        return new TrustManager[]{new CompositeX509TrustManager(keyStore)};
    }

    public static X509TrustManager getDefaultTrustManager() {
        return getTrustManager(null);
    }

    public static X509TrustManager getTrustManager(KeyStore keystore) {
        return getTrustManager(TrustManagerFactory.getDefaultAlgorithm(), keystore);
    }

    public static X509TrustManager getTrustManager(String algorithm, KeyStore keystore) {

        TrustManagerFactory factory;
        try {
            factory = TrustManagerFactory.getInstance(algorithm);
            factory.init(keystore);
            TrustManager[] managers = factory.getTrustManagers();
            if (managers != null && managers.length > 0) {
                for (TrustManager trustManager : managers) {
                    if (trustManager instanceof X509TrustManager) {
                        return (X509TrustManager) trustManager;
                    }
                }
            }
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

}
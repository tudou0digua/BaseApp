package com.demo.baseapp.base.http;

import android.content.Context;

import com.demo.baseapp.base.util.LOGGER;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * SSLProvider
 * Author: chenbin
 * Time: 2020-04-21
 */
public class SSLProvider {
    public static final String TAG = "SSLProvider";
    private static final String KEY  = "KEY";

    private static volatile SSLContext SSL_CONTEXT = null;
    private static X509TrustManager TRUST_MANAGER = null;
    private static final boolean ADD_CA_CER = true;

    public static SSLContext getSSLContext(Context context) {
        if (SSL_CONTEXT != null) {
            return SSL_CONTEXT;
        }

        try {
            List<KeyStore> list = getKeyStoreList(context);
            if (list.size() > 0) {
                TRUST_MANAGER = getCompositeTrustManager(list);
            } else {
                LOGGER.e(TAG, "getSSLContext empty keystore");
                TRUST_MANAGER = getCompositeTrustManager(list);
            }
            SSL_CONTEXT = SSLContext.getInstance("TLS");
            SSL_CONTEXT.init(null, new TrustManager[]{TRUST_MANAGER}, new SecureRandom());
            LOGGER.e(TAG, "getSSLContext init success!");
            return SSL_CONTEXT;
        } catch (Exception e) {
            LOGGER.e(TAG, e.getMessage());
        }
        return null;
    }

    private static List<KeyStore> getKeyStoreList(Context context) {
        List<KeyStore> list = new ArrayList<>();

        //load keystore file
        KeyStore ks = null;
        try {
            ks = getKeyStore(context.getResources().getAssets().open("xxx.bks"));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.e(TAG, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.e(TAG, e.getMessage());
        }
        if (ks != null) {
            LOGGER.e(TAG, "ks add success !");
            list.add(ks);
        }

        return list;
    }

    private static KeyStore getKeyStore(InputStream tsIn) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("bks");
            keyStore.load(tsIn, KEY.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.e(TAG, e.getMessage());
        } finally {
            try {
                tsIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return keyStore;
    }

    public static X509TrustManager getTrustManager(Context context) {
        if (TRUST_MANAGER != null) {
            return TRUST_MANAGER;
        }
        getSSLContext(context);
        if (TRUST_MANAGER != null) {
            return TRUST_MANAGER;
        }
        return null;
    }

    private static X509TrustManager getCompositeTrustManager(List<KeyStore> keyStores) {
        List<X509TrustManager> result = new ArrayList<>();
        for (KeyStore ks : keyStores) {
            X509TrustManager manager = getTrustManager(ks);
            if (manager != null) {
                result.add(manager);
            }
        }
        //add ca certification
        if (ADD_CA_CER) {
            result.add(CompositeX509TrustManager.getDefaultTrustManager());
        }
        if (result.size() > 0) {
            return new CompositeX509TrustManager(result);
        } else {
            return CompositeX509TrustManager.getDefaultTrustManager();
        }
    }

    private static X509TrustManager getTrustManager(KeyStore trustStore) {
        X509TrustManager trustManager = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length == 0 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            } else {
                trustManager = (X509TrustManager) trustManagers[0];
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (KeyStoreException e) {
            LOGGER.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return trustManager;
    }
}

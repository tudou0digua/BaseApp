package com.demo.baseapp.base.http;

import android.content.Context;

import com.demo.baseapp.base.util.CommonRes;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitManager
 * Time: 2020-04-21
 */
public class RetrofitManager {
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECT_TIMEOUT = 15000;

    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();
    private static HttpLoggingInterceptor loggerInterceptor;

    public static Retrofit getDefaultRetrofit() {
        if (retrofit == null) {
            synchronized (RetrofitManager.class) {
                if (retrofit == null) {
                    String baseUrl = "";
                    retrofit = new Retrofit.Builder()
                            .client(getDefaultOkHttpClient())
                            .baseUrl(baseUrl)
                            .addConverterFactory(gsonConverterFactory)
                            .addCallAdapterFactory(rxJavaCallAdapterFactory)
                            .build();
                }
            }
        }
        return retrofit;
    }

    private static OkHttpClient getDefaultOkHttpClient() {
        if (okHttpClient == null) {
            Context appContext = CommonRes.getInstance().getAppContext();
            SSLContext sslContext = SSLProvider.getSSLContext(appContext);
            X509TrustManager trustManager = SSLProvider.getTrustManager(appContext);
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .addInterceptor(new DefaultInterception())
                    //no cache
                    .cache(null);

            HttpLoggingInterceptor loggerInterceptor = getHttpLoggingInterceptor();
            builder.addNetworkInterceptor(loggerInterceptor);

            if (sslContext != null && trustManager != null) {
                builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
            }
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    private static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        if (loggerInterceptor == null) {
            loggerInterceptor = new HttpLoggingInterceptor(new HttpInterceptLogger());
            loggerInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        return loggerInterceptor;
    }
}

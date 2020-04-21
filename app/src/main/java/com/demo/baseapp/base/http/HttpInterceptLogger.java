package com.demo.baseapp.base.http;


import com.demo.baseapp.base.util.LOGGER;

import okhttp3.logging.HttpLoggingInterceptor;

public class HttpInterceptLogger implements HttpLoggingInterceptor.Logger {
    private static final String TAG = "HttpInterceptLogger";

    @Override
    public void log(String message) {
        LOGGER.d(TAG, message);
    }
}

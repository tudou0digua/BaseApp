package com.demo.baseapp.base.util;

import android.content.Context;

/**
 * CommonUtil
 * Author: chenbin
 * Time: 2020-04-21
 */
public class CommonRes {
    private static CommonRes instance;
    private Context appContext;

    public static CommonRes getInstance() {
        if (instance == null) {
            instance = new CommonRes();
        }
        return instance;
    }

    public Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }
}

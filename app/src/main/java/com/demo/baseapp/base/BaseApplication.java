package com.demo.baseapp.base;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.ProcessUtils;
import com.demo.baseapp.base.util.CommonRes;

import androidx.multidex.MultiDex;

/**
 * BaseApplication
 * Author: chenbin
 * Time: 2020-04-21
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //replace with main process name
        if (!"com.demo.baseapp".equals(ProcessUtils.getCurrentProcessName())) {
            //not main process
            return;
        }

        CommonRes.getInstance().setAppContext(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

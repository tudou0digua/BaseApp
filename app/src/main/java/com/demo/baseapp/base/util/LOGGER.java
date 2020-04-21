package com.demo.baseapp.base.util;

import android.util.Log;

/**
 * LOGGER
 * Time: 2020-04-21
 */
public class LOGGER {

    public static final int LEVEL_V = 1;
    public static final int LEVEL_D = 2;
    public static final int LEVEL_I = 3;
    public static final int LEVEL_W = 4;
    public static final int LEVEL_E = 5;

    private static int logLevel = LEVEL_E;

    public static void setLogLevel(int logLevel) {
        LOGGER.logLevel = logLevel;
    }

    public static void d(String tag, String text) {
        if (LEVEL_D >= logLevel) {
            Log.d(tag, text);
        }
    }

    public static void e(String tag, String text) {
        if (LEVEL_E >= logLevel) {
            Log.d(tag, text);
        }
    }
}

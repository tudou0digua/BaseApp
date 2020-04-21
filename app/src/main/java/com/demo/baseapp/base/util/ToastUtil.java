package com.demo.baseapp.base.util;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

/**
 * ToastUtil
 * Author: chenbin
 * Time: 2020-04-21
 */
public class ToastUtil {

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, @StringRes int text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}

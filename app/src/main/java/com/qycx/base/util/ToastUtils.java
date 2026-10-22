package com.qycx.base.util;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.qycx.MyApplication;

public class ToastUtils {
    private static Toast mToast;

    public static void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MyApplication.getContext(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}

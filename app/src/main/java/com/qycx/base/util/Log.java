package com.qycx.base.util;

import android.os.Environment;

import com.qycx.MyApplication;
import com.qycx.base.showInfo_open;
import com.qycx.base.tmpFunc;
import com.qycx.gleslibrary.BuildConfig;

import java.util.Locale;


public class Log {
    private static final String TAG = "openRtComm";
    static String pkgName = MyApplication.getContext().getPackageName();

    static boolean isDebug = true;
   // static boolean isDebug = BuildConfig.DEBUG;

    public static void i(String msg) {
        if (!isDebug) return;
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StackTraceElement stackTrace = null;
        for (StackTraceElement element : trace) {
            if(element.getClassName().startsWith(pkgName) && !element.getClassName().endsWith("Log"))
            {
                stackTrace = element;
                break;
            }
        }
        if(stackTrace == null) return;
        String clsName = stackTrace.getClassName();
        int pos = clsName.indexOf("$");
        if (pos > 0) {
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1, pos);
        }else{
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        }
        String log = String.format(Locale.getDefault(), "[%s] [%s:%d] %s", clsName, stackTrace.getMethodName(), stackTrace.getLineNumber(), msg);
        android.util.Log.i(TAG, log);
    }

    public static void i(String tag, String msg) {
        if (!isDebug) return;
        android.util.Log.i(tag, msg);
    }

    public static void d(String msg) {
        if (!isDebug) return;
        /*
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StackTraceElement stackTrace = null;
        for (StackTraceElement element : trace) {
            if(element.getClassName().startsWith(pkgName) && !element.getClassName().endsWith("Log"))
            {
                stackTrace = element;
                break;
            }
        }
        if(stackTrace == null) return;
        String clsName = stackTrace.getClassName();
        int pos = clsName.indexOf("$");
        if (pos > 0) {
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1, pos);
        }else{
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        }
        String log = String.format(Locale.getDefault(), "[%s] [%s:%d] %s", clsName, stackTrace.getMethodName(), stackTrace.getLineNumber(), msg);

         */
        int threadId  = tmpFunc.qyFuncs.myGetCurrentThreadId();
        msg  =  msg  +  " "  +  threadId;
        String log = msg;

        android.util.Log.d(TAG, log);
    }

    public static void d(String tag, String msg) {
        if (!isDebug) return;
        //
        int threadId  = tmpFunc.qyFuncs.myGetCurrentThreadId();
        msg  =  msg  +  " "  +  threadId;
        //
        android.util.Log.d(tag, msg);
    }

    public static void w(String msg) {
        if (!isDebug) return;
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StackTraceElement stackTrace = null;
        for (StackTraceElement element : trace) {
            if(element.getClassName().startsWith(pkgName) && !element.getClassName().endsWith("Log"))
            {
                stackTrace = element;
                break;
            }
        }
        if(stackTrace == null) return;
        String clsName = stackTrace.getClassName();
        int pos = clsName.indexOf("$");
        if (pos > 0) {
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1, pos);
        }else{
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        }
        String log = String.format(Locale.getDefault(), "[%s] [%s:%d] %s", clsName, stackTrace.getMethodName(), stackTrace.getLineNumber(), msg);
        android.util.Log.w(TAG, log);
    }

    public static void w(String tag, String msg) {
        if (!isDebug) return;
        android.util.Log.w(tag, msg);
    }

    public static void e(String msg) {
        if (!isDebug) return;
//        showInfo_open.qyFuncs.showInfo_open(msg);
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StackTraceElement stackTrace = null;
        for (StackTraceElement element : trace) {
            if(element.getClassName().startsWith(pkgName) && !element.getClassName().endsWith("Log"))
            {
                stackTrace = element;
                break;
            }
        }
        if(stackTrace == null) return;
        String clsName = stackTrace.getClassName();
        int pos = clsName.indexOf("$");
        if (pos > 0) {
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1, pos);
        }else{
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        }

        //
        int threadId  = tmpFunc.qyFuncs.myGetCurrentThreadId();
        msg  =  msg  +  " tid: "  +  threadId;

        //
        String log = String.format(Locale.getDefault(), "[%s] [%s:%d] %s", clsName, stackTrace.getMethodName(), stackTrace.getLineNumber(), msg);


        android.util.Log.e(TAG, log);
    }

    public static void e(String tag, String msg) {
        if (!isDebug) return;
        android.util.Log.e(tag, msg);
    }
}

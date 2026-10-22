


package com.qycx.base;


import static com.qycx.base.qyWmComm.Consts.CONST_Status_Window_Function_Open;

import android.util.Log;

import java.nio.ByteBuffer;

public class traceLog {
    private static final String TAG = traceLog.class.getSimpleName();
    public static class qyFuncs {

        public static int getCurTime(byte[] timeBuf) {
            if (CONST_Status_Window_Function_Open == 0) {
                Log.e(TAG,"Not finished: GetCurTime");
            }
            return 0;

        }

        public static int getTimelStr(long t, byte[] timeBuf, int size) {
            return 0;
        }

        public static int getTimelStr(long t, ByteBuffer timeBuf, int size) {
            return getTimelStr(t,timeBuf.array(),size);
        }

        public static int traceLog(String str) {
            int threadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            str += " ." + threadId;

            //
            if (null != guiShare.GuiShare.pf_logd) {
                guiShare.GuiShare.pf_logd.call("openRtComm", str);
            } else {
                Log.d(TAG, str);
            }
            return 0;
        }

        public static int traceLog(String tag, String str1) {
            //Log.e(TAG,"%s %s", str0, str1);
            if (null != guiShare.GuiShare.pf_logd) {
                String str = String.format("%s %s", tag, str1);
                guiShare.GuiShare.pf_logd.call(tag, str);
            }
            return 0;
        }

        public static int traceLogA(String str) {
            //Log.e(TAG,str);
            if (null != guiShare.GuiShare.pf_logd) {
                guiShare.GuiShare.pf_logd.call("", str);
            }

            return 0;
        }

    }
}

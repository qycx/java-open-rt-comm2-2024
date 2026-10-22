package com.qycx.base;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public  class clipboardFunc
{
    public static class qyFuncs_Droid
    {
        public static int copyToClipboard(String text)
        {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            Activity activity = (Activity)pQyMc.gui.hMainWnd;

            if (activity == null) return -1;

            ClipboardManager mClipboardManager = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("test", text);
            mClipboardManager.setPrimaryClip(mClipData);

            return 0;
        }

        public static int pasteFromClipboard()
        {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            Activity activity = (Activity)pQyMc.gui.hMainWnd;

            if (activity == null) return -1;

            ClipboardManager mClipboardManager;
            mClipboardManager = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clipData = mClipboardManager.getPrimaryClip();
            ClipData.Item item = clipData.getItemAt(0);
            String text = item.getText().toString();

            return 0;
        }




    }


}



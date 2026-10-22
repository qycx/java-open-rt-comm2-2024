






package com.qycx.base;


import android.widget.Toast;

import com.qycx.MyApplication;
import com.qycx.base.util.ToastUtils;

public class DlgNotification
{
    public static class qyFuncs_Droid
    {
        public static guiShare.PF_gui_displayNotification gui_displayNotification=(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_taskStatus pStatus)->
        {
            ToastUtils.showToast(pStatus.u_notification.content);
            return 0;
        };
    }
}

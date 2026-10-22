package com.qycx;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.qycx.base.qnmCommProc_mis;

//    [BroadcastReceiver(Enabled = true, Exported = true)]
//    [IntentFilter(new[] { "com.qycx.ACTION_ACCEPT_DONE" })]
public class AcceptReceiver extends BroadcastReceiver {
    private static final String TAG = AcceptReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // 在这里处理接收到的消息
        Log.d(TAG, "接受视频消息");
        MainActivity.gui_notify_doCmd.call(qnmCommProc_mis.Consts.CONST_imOp_recv_accept);
        //获取notificationId
        int notificationId = intent.getIntExtra("notificationId", -1);
        if (notificationId != -1) {
            //获取NotificationManagerCompat
            var notificationManager = NotificationManagerCompat.from(context);
            //取消通知
            notificationManager.cancel(notificationId);
        }
    }
}

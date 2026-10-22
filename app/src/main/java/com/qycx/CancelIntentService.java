package com.qycx;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;

public class CancelIntentService extends Service {
    @Override
    public Binder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 插入我们要在Service中完成的代码
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.qycx.ACTION_CANCEL_DONE");
        sendBroadcast(broadcastIntent);
        return Service.START_STICKY;
    }
}

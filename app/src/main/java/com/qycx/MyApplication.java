package com.qycx;

import me.jessyan.autosize.*;
import me.jessyan.autosize.utils.ScreenUtils;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.qycx.base.GlobalCrashHandler;
import com.qycx.base.ctxQyMc;
import com.qycx.base.network.NetworkConnectChangedReceiver;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.util.Log;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

public class MyApplication extends Application implements LifecycleObserver, onAdaptListener, LifecycleEventObserver {

    public Ringtone r;
    
    public NotificationManager mNotificationManager;
    Uri notification;

    public MediaPlayer mediaPlayer;

    public static Context mContext;

    public static Context getContext() {
        return mContext;
    }
    private final NetworkConnectChangedReceiver networkConnectChangedReceiver = new NetworkConnectChangedReceiver();

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate() {
        super.onCreate();

       GlobalCrashHandler.getInstance()
                .setDebugMode(true)
                .setMainCrashHandler((t,e)->{
                    Log.e("qy","全局异常:"  +  e.getMessage());
                    Log.e("qyE", "64");
                    System.out.println("未捕获的主线程异常："+e.getMessage());
                })
                .setUncaughtCrashHandler((t,e)->{
                    Log.e("qy","未捕获的子线程异常");
                    Log.e("qy", "69");
                    System.out.println("未捕获的子线程异常："+e.getMessage());

                })
                .register(this);

        //CrashReport.initCrashRep0-===================================================================================================================================================================================================================================================================ort(getApplicationContext(), "a8910bdb32", true);
        //CrashReport.setDumpFilePath(getApplicationContext(),"/sdcard/Download/bugly.txt");
        //
        mContext = this;
        mediaPlayer = MediaPlayer.create(this, R.raw.receive);
        mediaPlayer.setLooping(true);

        AutoSizeConfig.getInstance().setOnAdaptListener(this);

        boolean bNoLeakCannary = false;
        bNoLeakCannary = true;
        if (!bNoLeakCannary) {
            SetupLeakCanary();
        }

        var lifecycleOwner = ProcessLifecycleOwner.get();
        var lifecycle = lifecycleOwner.getLifecycle();
        lifecycle.addObserver(this);
        initNotification();
        lifecycleOwner.getLifecycle().addObserver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGE");
        filter.addAction("android.net.conn.STATE_CHANGE");

        registerReceiver(networkConnectChangedReceiver, filter,RECEIVER_EXPORTED);
        new Thread(cleanDumpFile).start();
    }

    @Override
    public void onTerminate() {
        Log.d("onTerminate");
        super.onTerminate();
    }

    private void initNotification() {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        //首先，我们需要创建一个NotificationChannel实例:
        NotificationChannel mChannel = new NotificationChannel("清扬通信", "视频会议通知", NotificationManager.IMPORTANCE_HIGH);

        //然后，我们可以设置描述，灯光颜色，振动模式等属性：
        mChannel.setDescription("清扬视频视频会议通知");
        // Enable lights and vibration.
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mChannel.setSound(notification, audioAttributes);
        // 最后，我们必须将通道添加到NotificationManager：
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);
    }

    protected void SetupLeakCanary() {

    }

    public void onAdaptAfter(Object p0, Activity p1) {
    }

    public void onAdaptBefore(Object p0, Activity activity) {
        AutoSizeConfig.getInstance().setScreenWidth(ScreenUtils.getScreenSize(activity)[0]);
        AutoSizeConfig.getInstance().setScreenHeight(ScreenUtils.getScreenSize(activity)[1]);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            AutoSizeConfig.getInstance().setDesignWidthInDp(1100);
            AutoSizeConfig.getInstance().setDesignHeightInDp(540);
        } else {
            AutoSizeConfig.getInstance().setDesignWidthInDp(440);
            AutoSizeConfig.getInstance().setDesignHeightInDp(960);
        }
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        switch (event) {
            case ON_RESUME:
                pQyMc.gui.bForeground = true;
                break;
            case ON_PAUSE:
                pQyMc.gui.bForeground = false;
                break;
        }
    }

    private final Runnable cleanDumpFile = () -> {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if(directory == null) return;
        File[] files = directory.listFiles();
        if(files == null) return;
        for (File file : files) {
            if(file.getName().startsWith("dump"))
            {
                file.delete();
            }
        }
        Log.i("dump文件清理完成");
    };
}

package com.qycx.base.network;

import static com.qycx.base.Consts.AUDIO_UPLOAD;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.qycx.base.statuscenter.messagebean.CurrentNet;
import com.qycx.base.statuscenter.messagebean.CurrentNetStatusEvent;
import com.qycx.base.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

/**
 * Author: Zhiqiang Zhu
 * Date:  2024/7/9 11:06
 * Discription:
 **/

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static String TAG = "zzq";
    Network network = new Network();
    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.isConnected()) {
                    network.setConnected(true);
                    //通知观察者网络状态已改变
                    NetworkChange.getInstance().notifyDataChange(network);
                    if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        network.setWifi(true);
                        //通知观察者网络状态已改变
                        NetworkChange.getInstance().notifyDataChange(network);
                        Log.d(AUDIO_UPLOAD+"*************** wifi is available ***********************");
                        Toast.makeText(context, "<stage00>当前wifi连接可用", Toast.LENGTH_SHORT).show();
                        //EventBus.getDefault().post(new CurrentNetStatusEvent(CurrentNet.WIFI));
                    } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        network.setMobile(true);
                        //通知观察者网络状态已改变
                        NetworkChange.getInstance().notifyDataChange(network);
                        Toast.makeText(context, "当前移动网络连接可用", Toast.LENGTH_SHORT).show();
                        //EventBus.getDefault().post(new CurrentNetStatusEvent(CurrentNet.MOBILE));
                        Log.d(AUDIO_UPLOAD + "<stage00>当前移动网络连接可用");
                    }
                } else {
                    network.setConnected(false);
                    //通知观察者网络状态已改变
                    NetworkChange.getInstance().notifyDataChange(network);
                    //EventBus.getDefault().post(new CurrentNetStatusEvent(CurrentNet.NO_NET));
                    Toast.makeText(context, "当前没有网络连接，请确保你已经打开网络", Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "当前没有网络连接，请确保你已经打开网络");
                }
            } else {
                network.setWifi(false);
                network.setMobile(false);
                network.setConnected(false);
                //通知观察者网络状态已改变
                NetworkChange.getInstance().notifyDataChange(network);
                //Log.d(TAG, "当前没有网络连接，请确保你已经打开网络");
                Log.d("***************network is  unavailable, please check. a12345+sig_e53531 ***********************");
                Toast.makeText(context, "当前没有网络连接，请确保你已经打开网络", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

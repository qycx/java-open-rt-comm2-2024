package com.qycx.base.statuscenter;

import com.qycx.base.audioRecorder_android;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.statuscenter.messagebean.AudioOutBufSizeEvent;
import com.qycx.base.statuscenter.messagebean.AudioOutStatusEvent;
import com.qycx.base.statuscenter.messagebean.ChangeNetspeedEvent;
import com.qycx.base.statuscenter.messagebean.CurrentNet;
import com.qycx.base.statuscenter.messagebean.CurrentNetStatusEvent;
import com.qycx.base.statuscenter.messagebean.MediaOutStatus;
import com.qycx.base.statuscenter.messagebean.MicrophoneMutedEvent;
import com.qycx.base.statuscenter.messagebean.NetConnectEvent;
import com.qycx.base.statuscenter.messagebean.NetConnectStatus;
import com.qycx.base.statuscenter.messagebean.VideoOutBufSizeEvent;
import com.qycx.base.statuscenter.messagebean.VideoOutStatusEvent;
import com.qycx.base.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 ***********************************************
 * Author: Zhiqiang Zhu
 * Date:  2024/7/8 09:48
 * Discription: collect all the meeting info
 ***********************************************
 **/

public class MeetingStatusCenter {

    private static MeetingStatusCenter instance;
    private int outAudioBufSize1;
    private int outAudioBufSize2;
    private int outAudioBufSize3;
    private int outVideoBufSize12;
    private int outVideoBufSize1;
    private int outVideoBufSize2;
    private int outVideoBufSize3;
    private int net_count = 0;
    private static int LIST_LENGTH = 5;
    //private ArrayList<NetConnectStatus> statusList ;
    private NetConnectStatus[] statusList = new NetConnectStatus[LIST_LENGTH]; // 创建一个整型数组，长度为5

    public CurrentNet getNetStatus() {
        return netStatus;
    }

    private CurrentNet netStatus = CurrentNet.WIFI;
    private MediaOutStatus audioOutStatus = MediaOutStatus.GOOD;
    private MediaOutStatus videoOutStatus = MediaOutStatus.GOOD;

    public MediaOutStatus getAudioOutStatus() {
        return audioOutStatus;
    }

    public MediaOutStatus getVideoOutStatus() {
        return videoOutStatus;
    }

    public int getOutAudioBufSize() {

        return Math.max(outAudioBufSize1, Math.max(outAudioBufSize2, outAudioBufSize3));
    }

    public int getOutVideoBufSize() {
        //return Math.max(outVideoBufSize1, Math.max(outVideoBufSize2, outVideoBufSize3));
        return outVideoBufSize2;
    }

    public boolean isRegistered() {
        return registered;
    }

    private boolean registered = false;

    private MeetingStatusCenter() {
        for(int i = 0; i < LIST_LENGTH; i++){
            statusList[i] = NetConnectStatus.CONNECTED;
        }
    }

    public static synchronized MeetingStatusCenter getInstance() {
        if (instance == null) {
            instance = new MeetingStatusCenter();
        }
        return instance; // 返回单例对象
    }

    //主线程---子线程接受
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void OnCurrentNetStatusEvent(CurrentNetStatusEvent bean) {
        netStatus = bean.getNetStatus();
//        EventBus.getDefault().post(new CurrentNetStatusEvent(CurrentNet.NO_NET));
    }

    //主线程---子线程接受
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void OnVideoOutStatusEvent(VideoOutStatusEvent bean) {
        videoOutStatus = bean.getMediaOutStatus();
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void OnAudioOutStatusEvent(AudioOutStatusEvent bean) {
        audioOutStatus = bean.getMediaOutStatus();
    }

    //主线程---子线程接受
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void OnReceiveNetStatusEvent(NetConnectEvent bean) {
        NetConnectStatus status = bean.getNetStatus();
        statusList[net_count % LIST_LENGTH] = status;
        net_count++;
    }
    public boolean getCurrentNetStatus(){
       int count = 0;
       for( int i = 0; i < 5; i++) {
           if(statusList[i] == NetConnectStatus.DISCONNECTED){
               count++;
           }
       }
       if(count > 0){
          // Log.d( "OnReceiveNetStatusEvent getCurrentNetStatus " +NetConnectStatus.DISCONNECTED );
           return false;
       }
       //Log.d( "OnReceiveNetStatusEvent getCurrentNetStatus " +NetConnectStatus.CONNECTED );
       return true;
    }

    //主线程---子线程接受
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void OnReceiveNetSpeedEvent(ChangeNetspeedEvent bean) {
        int inSpeed  = bean.inSpeed;
        int outSpeed = bean.outSpeed;
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void OnReceiveVideoBufSizeEvent(VideoOutBufSizeEvent bean) {
        switch(bean.pos) {
            case 1:
                outVideoBufSize1 = bean.bufSize;
                outVideoBufSize12 = outVideoBufSize1;
                break;
            case 2:
                outVideoBufSize2 = bean.bufSize;
                outVideoBufSize12 = outVideoBufSize2;
                break;
            case 3:
                outVideoBufSize3 = bean.bufSize;
                break;
        }
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void OnReceiveAudioBufSizeEvent(AudioOutBufSizeEvent bean) {
        switch(bean.pos) {
            case 1:
                outAudioBufSize1 = bean.bufSize;
                break;
            case 2:
                outAudioBufSize2 = bean.bufSize;
                break;
            case 3:
                outAudioBufSize3 = bean.bufSize;
                break;
        }
        if(bean.pos == 1){
            Log.d( ">>>>>>>>>>>>>>>>>>>>> Audio bufSize is "  + bean.bufSize + " pos " + bean.pos + " id " + bean.id );
        }
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void OnReceiveMicrophoneMuteEvent(MicrophoneMutedEvent bean) {
        boolean isMuted = bean.isMuted();
    }

    public void registerEventBus(Object subscriber){
        registered = true;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        outAudioBufSize1 = 0;
        outAudioBufSize2 = 0;
        outAudioBufSize3 = 0;

        outVideoBufSize1 = 0;
        outVideoBufSize2 = 0;
        outVideoBufSize3 = 0;
        outVideoBufSize12 = 0;
    }

    public void unRegisterEventBus(Object subscriber){
        if (EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber);
        }

        registered = false;
        outAudioBufSize1 = 0;
        outAudioBufSize2 = 0;
        outAudioBufSize3 = 0;

        outVideoBufSize1 = 0;
        outVideoBufSize2 = 0;
        outVideoBufSize3 = 0;
        outVideoBufSize12 = 0;
    }
}

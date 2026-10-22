package com.qycx.base.statuscenter.messagebean;

/**
 * Author: Zhiqiang Zhu
 * Date:  2024/7/11 09:31
 * Discription:A class to tell media tranfer status, used for eventbus
 **/
public class AudioOutStatusEvent {

    public AudioOutStatusEvent(MediaOutStatus mediaOutStatus) {
        this.mediaOutStatus = mediaOutStatus;
    }

    public MediaOutStatus getMediaOutStatus() {
        return mediaOutStatus;
    }

    public MediaOutStatus mediaOutStatus;
}

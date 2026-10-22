package com.qycx.base.statuscenter.messagebean;

/**
 * Author: Zhiqiang Zhu
 * Date:  2024/7/11 09:43
 * Discription:
 **/
public class VideoOutStatusEvent {
    public   VideoOutStatusEvent(MediaOutStatus mediaOutStatus) {
        this.mediaOutStatus = mediaOutStatus;
    }

    public MediaOutStatus getMediaOutStatus() {
        return mediaOutStatus;
    }

    public MediaOutStatus mediaOutStatus;
}

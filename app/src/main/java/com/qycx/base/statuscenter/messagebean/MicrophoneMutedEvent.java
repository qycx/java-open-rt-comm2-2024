package com.qycx.base.statuscenter.messagebean;

/**
 * Author: Zhiqiang Zhu
 * Date:  2024/7/8 12:44
 * Discription:
 **/
public class MicrophoneMutedEvent {

    boolean isMuted = false;

    public MicrophoneMutedEvent(boolean isMuted) {
        this.isMuted = isMuted;
    }

    public boolean isMuted() {
        return isMuted;
    }
}

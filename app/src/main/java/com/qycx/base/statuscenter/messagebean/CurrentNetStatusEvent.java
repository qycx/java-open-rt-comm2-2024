package com.qycx.base.statuscenter.messagebean;

/**
 * Author: Zhiqiang Zhu
 * Date:  2024/7/12 13:33
 * Discription:
 **/
public class CurrentNetStatusEvent {
    public CurrentNetStatusEvent(CurrentNet netStatus) {
        this.netStatus = netStatus;
    }

    public CurrentNet getNetStatus() {
        return netStatus;
    }
    CurrentNet netStatus;
}

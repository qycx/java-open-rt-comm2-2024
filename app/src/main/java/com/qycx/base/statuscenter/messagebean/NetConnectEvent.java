package com.qycx.base.statuscenter.messagebean;

/**
 * Author: Zhiqiang Zhu
 * Date:  2024/7/16 14:04
 * Discription:
 **/
public class NetConnectEvent {
    private NetConnectStatus netStatus;

    public NetConnectEvent(NetConnectStatus netStatus) {
        this.netStatus = netStatus;
    }

    public NetConnectStatus getNetStatus() {
        return netStatus;
    }
}

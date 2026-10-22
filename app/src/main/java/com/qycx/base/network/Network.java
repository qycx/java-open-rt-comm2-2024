package com.qycx.base.network;

/**
 * Author: Zhiqiang Zhu
 * Date:  2024/7/9 11:02
 * Discription:
 **/
public class Network {

    private boolean wifi;
    private boolean mobile;
    private boolean connected;

    public Network() {
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

}

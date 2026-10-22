package com.qycx.base.network;

/**
 * Author: Zhiqiang Zhu
 * Date:  2024/7/9 11:00
 * Discription:
 **/

import java.util.Observable;

/**
 * Created by ：Luo
 * <p>  被观察者类
 * Created Time ：2017/8/30
 */

public class NetworkChange extends Observable {

    private static NetworkChange instance = null;

    public static NetworkChange getInstance() {
        if (null == instance) {
            instance = new NetworkChange();
        }
        return instance;
    }
    public void notifyDataChange(Network net) {
        setChanged();
        notifyObservers(net);
    }
}

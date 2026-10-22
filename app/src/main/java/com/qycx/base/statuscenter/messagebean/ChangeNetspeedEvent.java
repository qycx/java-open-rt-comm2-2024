package com.qycx.base.statuscenter.messagebean;

/**
 **********************************************
 * Author: Zhiqiang Zhu
 * Date:  2024/7/8 09:45
 * Discription: Video and Audio speed info.
 **********************************************
 **/

public class ChangeNetspeedEvent{
    public int inSpeed;    // video audio in  spped
	public int outSpeed;  //  video audio out spped

    public ChangeNetspeedEvent(int inspeed, int outspeed) {
        this.inSpeed = inspeed;
        this.outSpeed   = outspeed;
    }
}

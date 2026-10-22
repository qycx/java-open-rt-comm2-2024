package com.qycx.base.statuscenter.messagebean;

/**
 * Author: Zhiqiang Zhu
 * Date:  2024/7/8 10:53
 * Discription: video out buf size
 **/
public class VideoOutBufSizeEvent {
    public int bufSize;
    public long id;
    public int pos;

    public VideoOutBufSizeEvent(int bufSize, int pos, long id) {
        this.bufSize = bufSize;
        this.id   = id;
        this.pos  = pos;
    }
}

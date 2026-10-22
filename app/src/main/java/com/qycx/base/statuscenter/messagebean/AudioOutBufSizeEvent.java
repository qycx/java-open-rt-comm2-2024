package com.qycx.base.statuscenter.messagebean;

/**
 * Author: Zhiqiang Zhu
 * Date:  2024/7/8 10:53
 * Discription: Audio buf size
 **/
public class AudioOutBufSizeEvent {
    public int bufSize;
    public int pos;
    public long id;

    public AudioOutBufSizeEvent(int bufSize, int pos, long id) {
        this.bufSize = bufSize;
        this.pos     = pos;
        this.id      = id;
    }
}

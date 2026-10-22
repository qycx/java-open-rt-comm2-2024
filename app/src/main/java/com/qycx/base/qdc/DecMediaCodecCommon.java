package com.qycx.base.qdc;

import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;

import com.qycx.base.qdcCommProc_open;

public abstract class DecMediaCodecCommon extends Thread {
    public qdcCommProc_open.QOI_common m_qoi = new qdcCommProc_open.QOI_common();
    public byte[] m_tmpDataBuf;
    public int m_old_lPktId;
}

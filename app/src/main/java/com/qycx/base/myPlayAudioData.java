


package com.qycx.base;

import java.nio.ByteBuffer;

public class myPlayAudioData {
    public static class myPLAY_AUDIO_DATA extends qnmCommProc_mis.MIS_MSGU {
        //
        public int lPktId_getNewPkt;                      //  2015/02/13.???????. ???????
        public int tmp_lPktId_getNewPkt;

        //
        public qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead head = new qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead();

        //
        public int uiSize_dec;                                //  2016/12/10
        //public QY_AUDIO_HEADER ah;                                     //  2016/12/06
        public qyCommProc_open.WAVEFORMATEX wfx = new qyCommProc_open.WAVEFORMATEX();

        //  char									*		pBuf;									//  2009/12/12
        public qyMemoryFunc.QY_MEMORY memory = new qyMemoryFunc.QY_MEMORY();                                   //  2010/04/23

    }

    ;


}

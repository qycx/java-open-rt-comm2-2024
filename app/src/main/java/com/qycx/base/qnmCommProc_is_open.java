


package com.qycx.base;

import java.nio.ByteBuffer;

public class qnmCommProc_is_open {
    public static class MY_DEBUG_STEP_showInfo {                                            //  2014/05/08
        public String tWhere;//[16];
        public int uiStep;
    };

    public static class MY_DEBUG_STEP {
        //M_debugStep_common
        //#define		M_debugStep_common
        public int nStep_debug;
        public int dwTickCnt_step_debug;                        //  2012/03/21

        public int iErr_thread;                                //  2012/09/08
        public MY_DEBUG_STEP_showInfo showInfo;

    };


    //
    public static class MACRO_qyQThreadProcInfo_common1 {
        public qyq2.QY_Q2 q2;
        public boolean bQInited;

        //
        public QY_qBuf2 qBuf2;              //  2024/09/16

        //
        public int dwThreadId;
        //
        //public Thread hThread;
        public Object hThread;
        //
        public int ind_hThread;
        //
        public volatile boolean bQuit;
        public long dwTickCnt_start;
        public boolean bRunning;
        public MY_DEBUG_STEP debugStep;

        //
        public MACRO_qyQThreadProcInfo_common1() {
            this.q2 = new qyq2.QY_Q2();
            this.qBuf2=new QY_qBuf2();
        }

        //
        public void memset0() {

        }

    };


    public static class AUDIO_PLAY_CFG {
        public int usMinTimeInMsOfAudioDataPlaying;                 //  2009/06/14
        //
        public int usAudioDataAddedToPlayEveryTime;                 //  2009/06/16
        //
        public int usNotifyIntervalInMs_needMoreAudioData;              //  2009/06/14. = CONST_notifyInterval
        //
        public int usMinTimeInMsToPlay;                             //  2012/01/27
        //
        public int usMaxTimeInMsToPlay;                             //  2009/06/03
        public int usMaxTimeInMsToBeRemoved;                            //  2009/06/03

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 6);
            buffer.putInt(usMinTimeInMsOfAudioDataPlaying);
            buffer.putInt(usAudioDataAddedToPlayEveryTime);
            buffer.putInt(usNotifyIntervalInMs_needMoreAudioData);
            buffer.putInt(usMinTimeInMsToPlay);
            buffer.putInt(usMaxTimeInMsToPlay);
            buffer.putInt(usMaxTimeInMsToBeRemoved);
            return buffer.array();
        }

    };

}



package com.qycx.base;

public class qyDefs_open {


    public static class Consts {

        //
        public final static int NAL_TRAIL_N = 0;
        public final static int NAL_TRAIL_R = 1;
        public final static int NAL_TSA_N = 2;
        public final static int NAL_TSA_R = 3;
        public final static int NAL_STSA_N = 4;
        public final static int NAL_STSA_R = 5;
        public final static int NAL_RADL_N = 6;
        public final static int NAL_RADL_R = 7;
        public final static int NAL_RASL_N = 8;
        public final static int NAL_RASL_R = 9;
        public final static int NAL_BLA_W_LP = 16;
        public final static int NAL_BLA_W_RADL = 17;
        public final static int NAL_BLA_N_LP = 18;
        public final static int NAL_IDR_W_RADL = 19;
        public final static int NAL_IDR_N_LP = 20;
        public final static int NAL_CRA_NUT = 21;
        public final static int NAL_VPS = 32;
        public final static int NAL_SPS = 33;
        public final static int NAL_PPS = 34;
        public final static int NAL_AUD = 35;
        public final static int NAL_EOS_NUT = 36;
        public final static int NAL_EOB_NUT = 37;
        public final static int NAL_FD_NUT = 38;
        public final static int NAL_SEI_PREFIX = 39;
        public final static int NAL_SEI_SUFFIX = 40;

        //
        public final static int CONST_qyStatus_ok = 0;
        //  public final static int		CONST_qyStatus_err									-1
        public final static int CONST_qyStatus_err = 255;      //  2007/08/02,
        //
        public final static int CONST_qyStatus_del = 32;       //  2007/05/12,
        public final static int CONST_qyStatus_update = 33;        //  2007/05/12

        //  		//  2009/01/01

        public final static int CONST_qyStatus_is_base = 500;      //
        //
        public final static int CONST_imTaskStatus_null = 0;        //
        //
        public final static int CONST_imTaskStatus_req = (CONST_qyStatus_is_base + 11);        //
        public final static int CONST_imTaskStatus_resp = (CONST_qyStatus_is_base + 12);        //  same as applyToRecv
        //
        public final static int CONST_imTaskStatus_applyToSend = (CONST_qyStatus_is_base + 21);        //
        public final static int CONST_imTaskStatus_waitToSend = (CONST_qyStatus_is_base + 22);        //
        public final static int CONST_imTaskStatus_applyToRecv = (CONST_qyStatus_is_base + 23);        //
        public final static int CONST_imTaskStatus_waitToRecv = (CONST_qyStatus_is_base + 24);        //  after accepted
        ///
        public final static int CONST_imTaskStatus_sending = (CONST_qyStatus_is_base + 27);        //
        public final static int CONST_imTaskStatus_receiving = (CONST_qyStatus_is_base + 28);        //
        //
        public final static int CONST_imTaskStatus_sendBreak = (CONST_qyStatus_is_base + 31);        //
        public final static int CONST_imTaskStatus_recvBreak = (CONST_qyStatus_is_base + 32);        //
        //
        public final static int CONST_imTaskStatus_recvFinished = (CONST_qyStatus_is_base + 50);        //
        public final static int CONST_imTaskStatus_sendFinished = (CONST_qyStatus_is_base + 51);        //
        //
        public final static int CONST_imTaskStatus_acceptedByReceiver = (CONST_qyStatus_is_base + 60);        //  2010/07/19
        public final static int CONST_imTaskStatus_dualByReceiver = (CONST_qyStatus_is_base + 61);        //  2010/07/19
        //
        public final static int CONST_imTaskStatus_err_missingOrgReq = (CONST_qyStatus_is_base + 100);  //
        public final static int CONST_imTaskStatus_err_cantGet_index_activeMems_from = (CONST_qyStatus_is_base + 101);    //

        //
        public final static int CONST_imTaskStatus_canceledBySender = (CONST_qyStatus_is_base + 200);    //
        public final static int CONST_imTaskStatus_canceledByReceiver = (CONST_qyStatus_is_base + 201);    //
        public final static int CONST_imTaskStatus_deniedByReceiver = (CONST_qyStatus_is_base + 203);    //
        public final static int CONST_imTaskStatus_autoCanceledByReceiver = (CONST_qyStatus_is_base + 204);    //  2014/07/30


    }

    //  2014/10/11
    public static class CAP_RECT {
        public int iX, iY;             //  
        public int iW, iH;
    };

    public static class qyFuncs {
        public static byte M_nalType_h264(byte[] p) {
            return (byte) ((p)[2] == 1 ? ((p)[3] & 0x1f) : ((p)[4] & 0x1f));
        }

        //
        public static void MACRO_safeFree(long x) {
//            if ((null != x)) {
//                qyMemoryFunc.qyFuncs.myfree(x);
//                (x) = 0;
//            }
        }

        //
        public static void MACRO_mysafeFree(long x) {
//            if ((null != x)) {
//                qyMemoryFunc.qyFuncs.myfree(x);
//                (x) = 0;
//            }
        }


    }
}

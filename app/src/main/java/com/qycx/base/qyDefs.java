package com.qycx.base;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class qyDefs {
    public static class Consts {

        //  下面的值为函数间返回值，跟CONST_qyRc_ok不同。
        //
        public final static int CONST_qyCharSpace = 0x20;
        public final static int CONST_qyTimeLen = 14;
        public final static int CONST_qyMaxVerLen = 8;

        public final static int CONST_objType_null = 0;
        public final static int CONST_objType_qySwitch = 1;
        public final static int CONST_objType_qySwitchPort = 2;
        public final static int CONST_objType_col2 = 3;
        public final static int CONST_objType_col3 = 4;
        public final static int CONST_objType_col4 = 5;
        public final static int CONST_objType_unRegGrp = 6;
        public final static int CONST_objType_othersGrp = 7;
        public final static int CONST_objType_unRegPc = 8;
        public final static int CONST_objType_pc = CONST_objType_unRegPc;                      //  2007/06/09£¬±íÃ÷ÊÇ¸öPC
        public final static int CONST_objType_otherPc = 9;
        public final static int CONST_objType_mac = CONST_objType_otherPc;                     //  2007/06/09, ±íÃ÷Ã»ÓÐÊ²Ã´ÍêÕûµÄPCÐÅÏ¢£¬Ö»±íÃ÷Ò»¸öMAC
        public final static int CONST_objType_os = 10;                                         //  PC»ú²Ù×÷ÏµÍ³
        public final static int CONST_objType_pcEnv = 11;                                          //  PC»úÈíÓ²¼þ»·¾³
        public final static int CONST_objType_cfgSpOs = 12;                                            //  ²¹¶¡ÅäÖÃÀïµÄ²Ù×÷ÏµÍ³, 2005/01/23
        public final static int CONST_objType_cfgSpServicePack = 13;                                           //  ²¹¶¡ÅäÖÃÀïµÄServicePack, 2005/01/23
        public final static int CONST_objType_zb = 14;
        public final static int CONST_objType_bb = 15;
        public final static int CONST_objType_dw = CONST_objType_col2;
        public final static int CONST_objType_bm = CONST_objType_col3;
        public final static int CONST_objType_syr = CONST_objType_col4;
        public final static int CONST_objType_pcStatus = 16;                                           //  			2006/08/14
        public final static int CONST_objType_pcUsrInfo = 17;                                          //				2006/08/14
        public final static int CONST_objType_pcOsClass = 18;                                      //				2006/08/14
        public final static int CONST_objType_pcOpNode = 19;                                   //  			2006/08/15
        public final static int CONST_objType_procedObj = 20;                                      //  			2007/01/02
        public final static int CONST_objType_android = 21;



        //  2007/11/24
        //  注：网络管理的objType常数最好是另外起一个范围来定义。1000以内最好留给更重要的通用常数。
        //
        public final static int CONST_objType_browser = 501;                                       //  devType, browser
        //  public final static int		CONST_objType_phone		=					502			;								//  qnmCommProc_open.h
        //  public final static int		CONST_objType_dev		=					503			;								//  qnmCommProc_open.h
        public final static int CONST_objType_toPath = 504;                    //
        public final static int CONST_objType_webcam = 505;                                            //  2009/08/28
        public final static int CONST_objType_screen = 506;                                            //  2009/08/28
        //
        public final static int CONST_objType_rtspStream = 507;                                            //  2014/05/02
        //
        public final static int CONST_objType_avStream = 530;                                          //  2014/09/25. 通用的stream, 非具体指定
        //
        public final static int CONST_objType_mosaicStream_video = 535;                                        //  2014/10/30
        public final static int CONST_objType_mosaicStream_resource = 536;                                         //  2014/10/30

        //
        public final static int CONST_objType_proxiedStream_video = 550;                                           //  2014/11/20

        //
        public final static int CONST_objType_cosult_mem = 560;                                //  2017/08/30

        //
        public final static int CONST_objType_qvcf = 580;                                      //  2015/05/09

        //
        public final static int CONST_objType_offlineFile = 600;                                   //  2015/08/03


        //  2007/05/12, mis
        public final static int CONST_objType_mis_base = 1000;
        //
        public final static int CONST_objType_admin = (CONST_objType_mis_base + 10);           //  2007/08/23, 系统管理员
        //
        public final static int CONST_objType_me = (CONST_objType_mis_base + 20);
        //
        public final static int CONST_objType_messenger = (CONST_objType_mis_base + 21);
        //
        public final static int CONST_objType_webGuest = (CONST_objType_mis_base + 22);            //  2007/11/21, webGuest
        //
        public final static int CONST_objType_phoneMessenger = (CONST_objType_mis_base + 23);          //  2008/01/30, 用手机注册，但是没有客户端的
        public final static int CONST_objType_phoneGuest = (CONST_objType_mis_base + 24);          //  2008/01/07
        //
        public final static int CONST_objType_newDevGuest = (CONST_objType_mis_base + 50);         //  2008/01/07, newDev


        //																										//  20-100之间留给各种用户对象；100以上留给各类组
        public final static int CONST_objType_imGrps = (CONST_objType_mis_base + 100);     //  2007/08/23
        public final static int CONST_objType_imGrp = (CONST_objType_mis_base + 101);
        //
        public final static int CONST_objType_tmpGrp = (CONST_objType_mis_base + 102);     //  2008/01/22, 临时组。通常在于临时组成一个Q, 然后传递给dlgTalk
        public final static int CONST_objType_tmpMsgr = (CONST_objType_mis_base + 103);        //  2018/11/11. 临时对象，可能是messenger,也可能是组


    }


    public interface PF_commonHandler {
        int call(Object p0, Object p1, Object p2);
    }

    public interface PF_bCommonHandler {
        boolean call(Object p0, Object p1, Object p2);
    }

    public static class QY_MESSENGER_ID {                               //
        public long ui64Id;

        public ByteBuffer toByteBuffer(){
            ByteBuffer buffer=ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(ui64Id);
            return buffer;
        }
        public byte[] toByteArray(){
            ByteBuffer buffer=ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(ui64Id);
            return buffer.array();
        }
    }

    ;


    public static class COMMON_PARAM {
        public Object p0;
        public Object p1;
        public Object p2;
        //  void	*			p3;
        //  void	*			p4;
    }

    ;


    public static class qyFuncs {


        public static void MACRO_safeStrMemCpy(String strIn, byte[] mem, int size) {
            byte[] b_strIn = strIn.getBytes(StandardCharsets.UTF_8);
            int len = Math.min(size, b_strIn.length);
            System.arraycopy(b_strIn, 0, mem, 0, len);
            if (len < size) mem[len] = 0;
        }

        public static void MACRO_makeCommonParam3(Object p0Param, Object p1Param, Object p2Param, qyDefs.COMMON_PARAM commonParam) {
            if (commonParam == null) return;
            commonParam.p0 = p0Param;
            commonParam.p1 = p1Param;
            commonParam.p2 = p2Param;
        }


        public static void M_free(long p) {
//            if (0 != p) {
//                qyMemoryFunc.qyFuncs.myfree((p));
//                (p) = 0;
//            }
//            return;
        }

    }


}

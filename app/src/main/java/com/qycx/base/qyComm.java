

package com.qycx.base;

import static com.qycx.base.qyComm.Consts.*;
import static com.qycx.base.qyDefs.Consts.CONST_qyMaxVerLen;
import static com.qycx.base.qyDefs.Consts.CONST_qyTimeLen;
import static com.qycx.base.qyFuncs.Consts.CONST_qyMaxIpLen;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class qyComm {


    public static class Consts {
        //  all service_ids
        //  #define		CONST_qyServiceId_rasMonoring			ASR_SERVICE_RAS_MONITORING
        public final static int CONST_qyServiceId_base = 10;                       // service_id的基数
        //  public final static int 		CONST_qyServiceId_imc					(  CONST_qyServiceId_base  +  1  )
        public final static int CONST_qyServiceId_netMc = (CONST_qyServiceId_base + 2);        //  2007/03/31
        public final static int CONST_qyServiceId_qwm = CONST_qyServiceId_netMc;
        //  public final static int 		CONST_qyServiceId_internetMgr			(  CONST_qyServiceId_base  +  110  )
        public final static int CONST_qyServiceId_is = (CONST_qyServiceId_base + 120); //  2007/03/07
        public final static int CONST_qyServiceId_mis = CONST_qyServiceId_is;

        public final static int CONST_qyServiceId_showInfo = (CONST_qyServiceId_base + 130);


        //  命令码, !!! 严重警告：以下命令码，只能添加，不能修改，以避免出现各产品不兼容的局面
        //			 类型：short
        //			 低端保留：
        public final static int CONST_qyCmd_tellService = 0;                       //  "启动服务"
        public final static int CONST_qyCmd_end = 255;                 //  "结束会话"


        //			 用户定义:
        public final static int CONST_qyCmd_base = 2000;
        //					  其他定义在qyCommProc.h中，须>CONST_qyCmd_base大，<CONST_qyCmd_upReserved

        //			 高端保留：
        public final static int CONST_qyCmd_upReserved = ((int) 0xe000);    //  57344
        public final static int CONST_qyCmd_update = ((int) 0xe001);
        public final static int CONST_qyCmd_getFile = ((int) 0xe002);
        public final static int CONST_qyCmd_getDigest = ((int) 0xe003);


        //
        //			 short
        //
        public final static int CONST_qyRc_ok = ((int) 0);
        public final static int CONST_qyRc_err = ((int) 255);
        //
        public final static int CONST_qyRc_user = ((int) 2000);
        public final static int CONST_qyRc_redirect = ((int) (CONST_qyRc_user + 1));
        public final static int CONST_qyRc_needAutoReg = ((int) (CONST_qyRc_user + 2));
        //  public final static int 		CONST_qyRc_notFound						(  (  short  )(  CONST_qyRc_user  +  3  )  )	//  2005/02/01
        public final static int CONST_qyRc_needVerified = ((int) (CONST_qyRc_user + 4));     //  2007/04/22
        public final static int CONST_qyRc_anotherLogonExists = ((int) (CONST_qyRc_user + 5));       //  2011/02/02


        public final static int CONST_qyRc_peerOffline = ((int) CONST_qyRc_user + 100);          //  2007/05/29
        public final static int CONST_qyRc_servBusy = ((int) CONST_qyRc_user + 101);         //  2007/05/29
        public final static int CONST_qyRc_unknown = ((int) CONST_qyRc_user + 102);          //  2007/07/01

        //
        public final static int CONST_qyRc_upReserved = ((int) 0xe000);  //  57344
        public final static int CONST_qyRc_quit = ((int) 0xe001);
        public final static int CONST_qyRc_update = ((int) 0xe002);
        public final static int CONST_qyRc_hint = ((int) 0xe003);

        public final static int CONST_commFlg_resp = (1 << 7);
        public final static int CONST_commFlg_moreData = (1 << 6);                                 //  2007/05/07
        public final static int CONST_commFlg_talkData = (1 << 5);                                  //  数据区的格式为MIS_MSG_TALKDATA，否则为IM_MSG_CONTENTU
        public final static int CONST_commFlg_routeTalkData = (1 << 4);                                    //  数据区的格式为用MSG_ROUTE+IM_MSG_CONTENTU

        //
        public final static int bufSize_qyCommServiceData = 8192;

    }

    public static class QY_COMM_SERVICE {
        public int serviceId;  //服务id
        public byte[] version = new byte[CONST_qyMaxVerLen];
        public int encType;
        public byte[] data = new byte[bufSize_qyCommServiceData];

        public byte[] toArrayBuffer(){
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2 + CONST_qyMaxVerLen + bufSize_qyCommServiceData);
            buffer.putInt(serviceId);
            buffer.put(version);
            buffer.putInt(encType);
            buffer.put(data);
            return buffer.array();
        }
    }

    ;


    public static class QY_COMM_BUFSIZE {
        int len;
        byte[] data;
    }

    ;

    //  2007/04/22,
    public static class QY_COMM_HEAD {
        public short usCode;
        public byte ucFlg;
        public byte reserved;
        public int len;

        public void memset0()
        {
            usCode  =  0;
            ucFlg  =  0;
            reserved  =  0;
            len  =  0;

        }

        public ByteBuffer toArrayBuffer(ByteBuffer tmp_bb){
            //ByteBuffer buffer=ByteBuffer.allocateDirect(8);
            if  (  tmp_bb==null)  return  null;
            if  (  tmp_bb.capacity()  <  8  )  return  null;
            tmp_bb.clear();
            ByteBuffer buffer  =  tmp_bb;

            //buffer=ByteBuffer.allocateDirect(8);

            //
            buffer.putShort(usCode);
            buffer.put(ucFlg);
            buffer.put(reserved);
            buffer.putInt(len);
            return buffer;
        }
    }

    ;


//#define		QY_COMM_REQHEAD							QY_COMM_HEAD
//#define		QY_COMM_RESPHEAD						QY_COMM_HEAD


    public static class QY_COMM_REQ {
        public QY_COMM_HEAD head;
        //union			{
        qyComm.QY_COMM_SERVICE service = new qyComm.QY_COMM_SERVICE();       //  这个service好象没有什么用处。2007/04/18
        //}	u;
    }

    ;


    public static class QY_COMM_RESP {
        public QY_COMM_HEAD head=new QY_COMM_HEAD();

        public byte[] toByteArray(ByteBuffer tmp_bb){
            ByteBuffer buf1 = head.toArrayBuffer(tmp_bb);
            if  (  buf1==null) return null;
            ByteBuffer buffer=ByteBuffer.allocateDirect(buf1.capacity());
            buffer.put(buf1);
            return buffer.array();
        }
    }

    ;


    public static class QY_COMM_SESSION {
        //
        public int uiType;                                //  2015/08/24
        //
        public qyComm.QY_COMM_SERVICE service;
        public int lenInBytes_service;                    //  接收到的QY_COMM_SERVICE 的字节熟. 2008/06/10

        public qyEncFunc.QY_ENC_CTX commEncCtx;                          //  2004/01/03

        //
        public byte[] startTime = new byte[CONST_qyTimeLen + 1];//[];    //  2004/07/26
        public String clientIp;
        //
        public int uiSessionId;                       //  会话序号，2004/09/11
        //
        public int usLastReqCode_i;                 //  2007/04/24, 输入的
        public int usLastRespCode_i;                    //  最后一个响应的响应码
        public int usLastReqCode_o;                 //  2007/04/24, 输出的
        public int usLastRespCode_o;                    //  最后一个响应的响应码
        //
        public int nTalks;                                //  与客户机交流的次数
        //
        public boolean bAdmin;                                //  2011/01/10

    }

    ;


    public static class qyFuncs {

        //
        public static boolean isRcQuit(int x) {
            return ((x) == CONST_qyRc_quit);
        }

        public static boolean isRcOk(int x) {
            return ((x) == CONST_qyRc_ok);
        }

        public static boolean isRcRedirect(int x) {
            return ((x) == CONST_qyRc_redirect);
        }

        public static boolean isRcUpdate(int x) {
            return ((x) == CONST_qyRc_update);
        }

        public static boolean isRcHint(int x) {
            return ((x) == CONST_qyRc_hint);
        }

        public static boolean isRcNeedVerified(int x) {
            return ((x) == CONST_qyRc_needVerified);
        }

        //
        public static boolean isUcFlgResp(byte ucFlg) {
            return (0 != (ucFlg & CONST_commFlg_resp));
        }                //  2007/04/22

        public static boolean isUcFlgMoreData(byte ucFlg) {
            return (0 != (ucFlg & CONST_commFlg_moreData));
        }           //  2007/05/07

        public static boolean isUcFlgTalkData(byte ucFlg) {
            return (0 != (ucFlg & CONST_commFlg_talkData));
        }           //  2007/07/11

        public static boolean isUcFlgRouteTalkData(byte ucFlg) {
            return (0 != (ucFlg & CONST_commFlg_routeTalkData));
        }        //  2007/11/29


    }
}



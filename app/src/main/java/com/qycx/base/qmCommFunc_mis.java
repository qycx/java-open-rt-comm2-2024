
package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.Consts.CONST_channelType_robot;
import static com.qycx.base.Consts.CONST_channelType_rtOp;
import static com.qycx.base.Consts.CONST_channelType_talking;
import static com.qycx.base.Consts.CONST_maxConnsPerCli_mis;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferVideoData;
import static com.qycx.base.qyCommProc.Consts.bufSize_publicKeyBlob;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

public class qmCommFunc_mis {

    public static class ENC_CTX_isCnt extends qyEncFunc.QY_ENC_CTX {
        //public QY_ENC_CTX common;                                          //  2007/06/20

        //
        public static class ENC_CTX_isCnt_var {
            public int uiBlobLen_publicKey;
            public ByteBuffer publicKeyBlob = ByteBuffer.allocate(bufSize_publicKeyBlob);
            public String publicKeyBlob_base64;
        }

        public ENC_CTX_isCnt_var m_var = new ENC_CTX_isCnt_var();

        //
        public boolean ucbPublicKeyDownloaded;                           //   公钥是否下载
        public boolean ucbPublicKeyVerified;                         //   公钥是否验证
        //
    }

    ;

    public static class CE_TASK_MEM {
        public qyDefs.QY_MESSENGER_ID idInfo_send = new qyDefs.QY_MESSENGER_ID();
        public long tStartTime;
        public int uiTranNo;
        //
        public int iTaskId;
        public int iStatus;

        public qyDefs.QY_MESSENGER_ID idInfo_recv = new qyDefs.QY_MESSENGER_ID();            //  ½ÓÊÕ·½

    }

    ;

    public static class MIS_CNT_cfg {
        public String misServName;//[CONST_maxMisServNameLen + 1];

        public qnmCommProc.QWM_DYN_CFG dynCfg;

        //
        public int iObjQType;                                      //  2007/04/05
        public String mutexName_syncQmObjQ;//[cntof_mutexName];
        public String semaphoreName_syncQmObjQ;//[cntof_semName];
        public int uiMaxCnt_semaphore_qmObjQ;


        //
        public genericQueue.GENERIC_Q_CFG inCacheQ = new genericQueue.GENERIC_Q_CFG();                                     //  2008/04/02
        //genericQueue.GENERIC_Q_CFG								inputQ;											//  2015/09/09
        //
        public genericQueue.GENERIC_Q_CFG toSendQ = new genericQueue.GENERIC_Q_CFG();
        public genericQueue.GENERIC_Q_CFG outputQ = new genericQueue.GENERIC_Q_CFG();
        //

        public genericQueue.GENERIC_Q_CFG talkingFriendQ = new genericQueue.GENERIC_Q_CFG(), recentFriendQ = new genericQueue.GENERIC_Q_CFG();

        //
        public genericQueue.GENERIC_Q_CFG imObj_intQ = new genericQueue.GENERIC_Q_CFG();

        //
        public int uiSizePerSnd_media;                                //  2011/01/30

        public short usIntervalInS_getIntervalParams;             //
        //
        public short usMaxMsgrRecentFriends;
        public short usIntervalInS_refreshRecentFriends;              //
        //
        public short usIntervalInS_refreshContactList;                //

        //  2015/07/28
        //unsigned  char								ucbNotUseP2pCall;								

        //
        public short usMaxContactUpdateInfosPerSnd_grp;               //  2014/02/06
        public short usMaxContactUpdateInfosPerSnd_grpMem;            //
        public short usMaxContactUpdateInfosPerSnd_contact;           //

        //
        public short usIntervalInS_tryToReg;                          //
        public short usIntervalInS_tryToSndDevInfo;

        public String mutexNamePrefix_syncTask;//[cntof_mutexName_prefix];             //

        //


    }

    ;


    public static class MIS_CHANNEL_status {
        //  unsigned  long							ulbServNotConnected:1;
        public boolean ulbAuthed;                             //是否认证过
        public boolean ulbNeedVerified;                     //是否校验过

        //
        public boolean ulbSessionErr1;                              //

        public qyCommProc.IM_NET_STAT netStat = new qyCommProc.IM_NET_STAT();
        public qyCommProc.IM_NET_STAT oldNetStat = new qyCommProc.IM_NET_STAT();

        public int     iIoStep;
        public boolean ulbIoQuit1;                                  //  IO 线程是否退出
        //
        public boolean ulbInputQuit;                                   //  2015/09/09
        public boolean ulbToSendQuit;                              //  
        public boolean ulbSndQuit;                                 //  
        public boolean ulbRecvQuit;                                    //  
        //

        public long dwTickCnt_lastStartToRecv;                        //  2009/02/25
        public long dwTickCnt_lastRecvd;
        //
        public long dwTickCnt_lastEndSending;                     //  2011/01/23

        //
        public long dwTickCnt_noSending_start;                        //  2012/05/21

        //
        public int dwLastTickCnt_applyForChkChannels;

    }

    public static class MIS_CHANNEL {

        //
        public boolean m_b_useQ2_toOutput  = false;//true;//false;//true;

        //
        public qmCommFunc_mis.MIS_CNT pMisCnt;                                       //  2016/09/09

        //
        public int uiType;                  //通道类型
        //

        public qmCrypt.ENC_CTX_session commEncCtx;                                     //  2008/06/08 sock通道加密类型结构
        public boolean ucbEncInited;
        public long  totalSize = 0;
        public int   totalTime = 0;
        public int   maxSize   = 0;

        //
        //public long endPoint  =  0;
        public int dbg_sn;

        //
        public qyComm.QY_COMM_SESSION session = new qyComm.QY_COMM_SESSION();              //会话结构
        public commonSock.QY_SOCK sock = new commonSock.QY_SOCK();                     //sock结构
        public AtomicBoolean bSessionOpen=new AtomicBoolean(false);                                //会话是否打开建立

        //
        //public genericQueue.GENERIC_Q inCacheQ;                                     //  recv
        public byte[] inCache_buf;
        public int inCache_len;
        public int inCache_tn;
        public int inCache_lastSeqNo;


        //
        public qyq2.QY_Q2 toSendQ2;

        //
        public qyq2.QY_Q2 outputQ21;
        public  QY_qBuf2 outputQBuf2;
        public int getBufferLength(int count){
            switch(uiType) {
                case CONST_channelType_talking:
                    return count * 100 ;//max:361 totalsize:1471 time:20average:73
                case CONST_channelType_robot:
                    break;
                case CONST_channelType_media:
                    return count * 6 * 1024;  //视频一个包平均是 5321， max:104192 totalsize:31129951 time:5850average:5321
                    ///return (int) (4000000 * 2.5 / 8);//  2.5 secends mp3
                case CONST_channelType_realTimeMedia:
                    return count * 1024;      //音频一个包平均是是445 max:2530 totalsize:1853912 time:4180average:443
                case CONST_channelType_rtOp:
                    //    return  1024 * 5;
                    break;
            }
            return 500 * count;
        }

        public int getOutBufferLength(){
            switch(uiType) {
                case CONST_channelType_talking:
                    break;
                case CONST_channelType_robot:
                    break;
                case CONST_channelType_media:
                    return bufSize_transferVideoData;//  2.5 secends mp3
                case CONST_channelType_realTimeMedia:
                    return bufSize_transferAudioData;  // 1.5 seconds
                case CONST_channelType_rtOp:
                    //    return  1024 * 5;
                    break;
            }
            return 1024 * 2;
        }


        //
        public volatile int pass_count = 0; //zzq 计数
        public int getOutQ2Size(){ //zzq
            if  (  m_b_useQ2_toOutput ) {
                return getQ2Nodes(outputQ21);
            }
            else {
                 return outputQBuf2.size();
            }
        };
        public int getSendQ2Size(){ //zzq
            return getQ2Nodes(toSendQ2);
        };

        public Thread hThread_io;                                      //  
        public int dwThreadId_io;                                    //  

        //
        public Thread hThread_toSend;


        //
        public Object p0;                                               //  可以传给thread_work_iocp参数. 2012/02/24

        public MIS_CHANNEL_status status = new MIS_CHANNEL_status();

    }


    public static class MIS_CNT_refreshContactList {                                                //  2997/08/02
        public long dwLastTickCnt_startToRetrieve;                   //  2007/07/26
        public long dwLastTickCnt_respRecvd;                      //  2014/02/11

        //
        public long tLastRefreshedTime_misServ;                      //  
        public long tStartTime;                                      //  
        public int uiTranNo;
        //
        public qyDefs.QY_MESSENGER_ID idInfo_lastRefreshed = new qyDefs.QY_MESSENGER_ID();                           //
        //
    }

    public static class MIS_CNT_refreshImObjRules {                                                //  2010/08/17
        public int dwLastTickCnt;
        public long tLastRefreshedTime;
        //
        public long tStart_toRetrieveAll;                         //  2011/11/12
    }

    ;


    public static class MIS_CNT_status {
        //
        public boolean ulbSeriousErr;                             //  
        //
        public int usRunningStatus;                             //  2007/06/07  在线状态

        //IM_TASK_STAT netStat;

    }

    ;


    //
    public static class MIS_MSG_TALKINGFRIEND_QMC {
        public int uiType;
        //
        public Object hWnd;

        //  		
        public qnmCommProc_mis.MSGR_ADDR addr;
        //
    }

    ;

    public static class MisCnt_refreshVideoConferenceActiveMems {
        public int dwLastTickCnt_refreshed;                     //  2009/06/01
        public boolean bNeedRefreshed;                                    //  2009/06/01
        public int iTaskId;
    }

    ;

    //
    public static class TMP_talkerDesc {
        public String talkerDesc;//[cntof_talkerDesc];
    }

    public static class TMP_displayName {
        public String displayName;//[cntof_displayName];
    }


    //
    public static class MIS_CNT {
        public ctxQmc.CCtxQmc pProcInfoParam;                                   //  2009/09/07
        public MIS_CNT_cfg cfg = new MIS_CNT_cfg();
        public ENC_CTX_isCnt commEncCtx;                                      //  2007/06/20  服务器上下文通信加密结构
        public boolean ucbEncInited;
        public qyObjs.QY_LOGICAL_SERVER server = new qyObjs.QY_LOGICAL_SERVER();
        public int usConnectCntInterval;
        public MIS_CHANNEL[] channels = new MIS_CHANNEL[CONST_maxConnsPerCli_mis];    //[CONST_maxConnsPerCli_mis];
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();               //  Me idInfo
        //public String displayName;                          //  Me 
        public TMP_displayName displayName = new TMP_displayName();
        public TMP_talkerDesc talkerDesc = new TMP_talkerDesc();
        public TMP_displayName displayName_withIdInfo = new TMP_displayName();
        public boolean bIntervalParamsGot;                                //  2007/09/20  是否对配置参数更新 取通信参数
        public long dwLastTickCnt_getIntervalParams;

        public List<MIS_MSG_TALKINGFRIEND_QMC> talkingFriendQ;
        public genericQueue.GENERIC_Q recentFriendQ;
        public genericQueue.GENERIC_Q imObj_intQ;

        public Semaphore hMutex_syncTask;
        public Semaphore hMutex_syncStartAv;
        public Semaphore hMutex_syncSendAv;
        public Semaphore hMutex_syncActiveMem;
        public Semaphore hMutex_syncIndex_dec;
        public Semaphore hMutex_syncShareEglContext;
        public MIS_CNT_refreshContactList refreshContactList = new MIS_CNT_refreshContactList();
        public MIS_CNT_refreshImObjRules refreshImObjRules = new MIS_CNT_refreshImObjRules();
        public MisCnt_refreshVideoConferenceActiveMems refreshVideoConferenceActiveMems = new MisCnt_refreshVideoConferenceActiveMems();

        public MIS_CNT_status status = new MIS_CNT_status();

        public MIS_CNT() {
            int ii = 0;
            //
            this.commEncCtx = new ENC_CTX_isCnt();
        }
    }

    ;
}

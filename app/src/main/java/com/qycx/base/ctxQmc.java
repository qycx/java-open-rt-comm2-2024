package com.qycx.base;

import static com.qycx.base.Consts.*;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_encA;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_encV;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_playA;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_snd;
import static com.qycx.base.audioRecord_webRtc.qyFuncs.init_webRtc;
import static com.qycx.base.ctxQmc.Consts.DEFAULT_loopCtrl_chkPlayers;
import static com.qycx.base.ctxQmc.Consts.DEFAULT_usTimeoutInMs_player_a;
import static com.qycx.base.ctxQyMc.Consts.CONST_aPlayType_android;
import static com.qycx.base.ctxQyMc.Consts.CONST_vPlayType_v;
import static com.qycx.base.doApplyForPlayer.qyFuncs.myWaitForQThread_infinite;
import static com.qycx.base.genericQueue.qyFuncs.exitGenericQ;
import static com.qycx.base.genericQueue.qyFuncs.initGenericQ;
import static com.qycx.base.qmCommonFunc_mis.Consts.*;
import static com.qycx.base.qmCusModuleDefs.Consts.*;
import static com.qycx.base.qmObjQProc.Consts.CONST_objQType_misClient;
import static com.qycx.base.qmcTaskInfo.qyFuncs.freeTaskInfos;
import static com.qycx.base.qnmCommProc_mis.Consts.*;
import static com.qycx.base.qyComm.Consts.*;
import static com.qycx.base.qyCommProc.Consts.CONST_maxCnt_sema_syncQmObjQ;
import static com.qycx.base.qyCommProc.Consts.DEFAULT_mutexNamePrefix_syncTask;
import static com.qycx.base.qyCommProc.Consts.DEFAULT_mutexName_syncQmObjQ_client;
import static com.qycx.base.qyCommProc.Consts.DEFAULT_semaphoreName_syncQmObjQ_client;
import static com.qycx.base.qyCommProc_open.Consts.*;
import static com.qycx.base.qyDefs.Consts.*;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.exitMisMsg;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.initMisMsg;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.mcTask_free;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.mcTask_new;
import static com.qycx.base.qyMcMainRealTimeMediaProc.Consts.DEFAULT_usMaxCnt_pSharedObjs;
import static com.qycx.base.qyMcMainRealTimeMediaProc.Consts.DEFAULT_usMaxCnt_taskInfos;


import static com.qycx.base.qyTmpMemory.qyFuncs.cleanEx_myDRAW_VIDEO_DATA;
import static com.qycx.base.qyTmpMemory.qyFuncs.clean_MIS_MSG_procVideo_qmc;
import static com.qycx.base.qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA;
import static com.qycx.base.qyq2.qyFuncs.exitQyQ2;
import static com.qycx.base.qyq2.qyFuncs.initQyQ2;


import com.qycx.Activity_dlgTalk;
import com.qycx.base.util.Log;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ctxQmc {

    public static class Consts {
        public final static int DEFAULT_usTimeoutInMs_player_a = 5000; //  2017/07/03	//  10000					//  2010/06/25
        public final static int DEFAULT_usTimeoutInMs_player_v = 10000;

        public final static int DEFAULT_loopCtrl_timer_dlgTalk_chkRecvdTasks = 7;  //  30				//  2010/06/26
        public final static int DEFAULT_loopCtrl_chkPlayers = 5;    //10						//  2010/06/26

    }

    public static class QMC_cfg_envVar {
        public int usMaxQNodes_mediaQ;
        public int usQNodes_warningInterval_mediaQ;
        public int usMaxQNodes_rtMediaQ;
        public int usQNodes_warningInterval_rtMediaQ;
        public int usMaxQNodes_rtOpQ;                                           //  2016/12/26
        public int usQNodes_warningInterval_rtOpQ;

        //
        public int usMaxQNodes_playSoundQ;
        public int usMaxQNodes_playVideoQ;                                      //  2012/11/09
        public int usMaxQNodes_displayVideoQ;                                   //  2012/11/09

        //
        public int usMaxQNodes_dlgTalk_processQ;                                //  2010/07/22
        public int usMaxnItems_dlgTalk_list;                                    //  2010/07/22
        //
        public int usMaxQNodes_imMsgLogQ;

        //  2017/08/15
        public boolean default_ucbLetConfMgrSetMicOn;


    }

    public static class Policy_notification {
        public boolean ucbPermitEveryOneNotify;                     //  允许任何人呼叫
        public boolean ucbNotNotifyDuringConference;                 //  禁止会议中有通知消息
        public boolean ucbNotNotify;                                 //  禁止任何通知消息
        //
        public boolean ucbBeep;
        //
        public int usTimeInMsToPop;                             //  pop窗口弹出要花多少时间
        public int iW_pic;
        public int iH_pic;
        public int iH_static;
    }

    public static class POLICY_isClient_video {
        public int uiCamCapType;                                     //  2010/07/26
    }

    public static class POLICY_isClient_audio {
        public int uiRecordType;                                     //  2010/08/02
        //

        //  2014/07/31
        public boolean ucbNot_selectDefaultRecorder;

    }

    public static class POLICY_isClient {
        public boolean ucbRunAsAdvertisingPlayer;                            //  2013/11/14. 是否做为广告机运行

        //  
        public boolean ucbDlgShareDynBmps_autopopupandhideOnStartup;

        //
        //
        public boolean ucbUserecycleobalVWall;                                    //  2012/04/02. 是否使用recycleobalVWall
        //
        public byte ucRuleIfMsgArrive;
        public String dirToSaveFile;//[MAX_PATH + 1];

        //
        public boolean ucbSaveMsg;
        public String dirToSaveMsg;//[MAX_PATH + 1];                       //  2014/04/26

        //
        public int uiTimeOfVideoFileInS;
        public String dirToSaveVideo;//[MAX_PATH + 1];                     //  2014/08/03

        //  2014/03/29
        public byte ucHardwareAccl;
        //

        //  2011/03/28
        //  unsigned  char									snmpbShowMsgBox;
        public boolean ucbShowNotification;
        public boolean ucbScrollForEnlagedImg;                               //  2012/04/07. 对双击放大图像是否不滚动

        //
        //
        public POLICY_isClient_video video = new POLICY_isClient_video();
        public POLICY_isClient_audio audio = new POLICY_isClient_audio();


        //  2014/07/31
        public boolean ucbNot_selectDefaultRecorder;


        //
        public int usTimeoutInMsToSwitch;                           //  2008/10/28, media realTimeMedia daemon 
        public int usTimeoutInMsToSwitch_rt;                        //  rt

        //  2012/03/31
        public int usMax_nPlayers;                                  //  2012/03/30
        public int usMax_nPlayers_gps;                              //  2012/05/07
        public int usMax_nThreads_dec;
        public int uiMaxQNodes_vwRuleQ;

        //  2009/02/25
        public boolean ucbManullyPermitToSpeakOnVideoConference;     //  2009/02//24

        //  2016/02/15
        public static class PolicyIsClient_avRules {
            public int uiCmd_autoAnswer_av;
            //  2017/07/07
            public boolean ucbLetConfMgrSetMicOn;
        }

        public PolicyIsClient_avRules avRules;
        //

        //  2010/08/24
        public Policy_notification notification;

        //  2010/09/03
        public boolean dlgTalk_ucbShowImGrpMems;
        //  
        public boolean dlgTalk_snmpbShowMsgBox;

        //  2012/08/01
        //  COM_PORT_cfg									comPort_ptz;

        //  2012/06/25
        public boolean ucbPreferCuda;                                //  进行视频压缩硬加速时优先采用cuda
        //  2012/11/06
        public boolean ucbLooping_mediaFile;                       //  循环播放媒体文件

    }


    public static class QMC_cfg {

        public int uiMaxSendMsgs;                                             //  
        public short usMaxMsgrRecentFriends;                                      //  2007/06/04

        //
        public int uiSizePerSnd_file_noAv;                                        //  2014/07/13
        public int uiSizePerSnd_file_avExists;                                    //  2014/07/13

        //
        public int uiSizePerSnd_media;                                            //  2008/04/01
        public short usNeedShowFrameInfoIntervalInS;                              //

        public int uiBufSize_myDrawVideoData;                                 //  2010/04/24
        public int uiBufSize_myPlayAudioData;                                 //  2010/04/24

        //
        public QMC_cfg_envVar envVar = new QMC_cfg_envVar();


        //		
        public genericQueue.GENERIC_Q_cfgEx mgrQEx = new genericQueue.GENERIC_Q_cfgEx();
        public genericQueue.GENERIC_Q_CFG displayQ = new genericQueue.GENERIC_Q_CFG();
        public genericQueue.GENERIC_Q_CFG workQ = new genericQueue.GENERIC_Q_CFG();
        public genericQueue.GENERIC_Q_CFG schedulerQ = new genericQueue.GENERIC_Q_CFG();
        public genericQueue.GENERIC_Q_CFG robotQ = new genericQueue.GENERIC_Q_CFG();
        public genericQueue.GENERIC_Q_CFG mediaQ = new genericQueue.GENERIC_Q_CFG();
        public genericQueue.GENERIC_Q_CFG realTimeMediaQ = new genericQueue.GENERIC_Q_CFG();
        public genericQueue.GENERIC_Q_CFG rtOpQ = new genericQueue.GENERIC_Q_CFG();       //  svrThreadÍ¨³£ÓÃÀ´ÏòÆäËûµÄcontactÌá¹©ÐÅÏ¢£¬±ÈÈç´«ÎÄ¼þµÈ
        //
        public genericQueue.GENERIC_Q_CFG processQ_robot = new genericQueue.GENERIC_Q_CFG();
        //
        public genericQueue.GENERIC_Q_CFG processQ_media = new genericQueue.GENERIC_Q_CFG();                                               //

        public genericQueue.GENERIC_Q_CFG outputQ_toMix_audio = new genericQueue.GENERIC_Q_CFG();                                      //  ÒôÆµµÄÊä³ö¶ÓÁÐ¡£
        public genericQueue.GENERIC_Q_CFG outputQ_toMosaic_video = new genericQueue.GENERIC_Q_CFG();                                      //  2009/07/26

        //
        public genericQueue.GENERIC_Q_CFG playSoundQ = new genericQueue.GENERIC_Q_CFG();
        public genericQueue.GENERIC_Q_CFG playVideoQ = new genericQueue.GENERIC_Q_CFG();                                                   //  2009/05/02
        public genericQueue.GENERIC_Q_CFG displayVideoQ = new genericQueue.GENERIC_Q_CFG();                                                //  2009/07/25

        //
        //GENERIC_Q_CFG								preTransformQ;												//  2011/12/09
        public genericQueue.GENERIC_Q_CFG transformQ = new genericQueue.GENERIC_Q_CFG();                                                   //  2009/05/12

        //
        //GENERIC_Q_CFG								vCamQ;														//  2010/06/18

        //  2017/01/29
        public genericQueue.GENERIC_Q_CFG appQ = new genericQueue.GENERIC_Q_CFG();                                                     //  2017/01/29
        //
        public genericQueue.GENERIC_Q_CFG vppQ = new genericQueue.GENERIC_Q_CFG();                                                     //  2011/12/04

        //
        public genericQueue.GENERIC_Q_CFG toShareAudioQ = new genericQueue.GENERIC_Q_CFG();                                                //  2011/01/21
        public genericQueue.GENERIC_Q_CFG toShareVideoQ = new genericQueue.GENERIC_Q_CFG();                                                //  2011/01/21

        //
        public genericQueue.GENERIC_Q_CFG toShareVideo_postQ = new genericQueue.GENERIC_Q_CFG();                                           //  2011/12/06

        //
        public genericQueue.GENERIC_Q_CFG dispatchQ = new genericQueue.GENERIC_Q_CFG();                                                    //  2009/06/17

        //  2016/03/15
        public genericQueue.GENERIC_Q_CFG talkerThreadQ = new genericQueue.GENERIC_Q_CFG();                                                //  2016/03/15

        //  2016/06/15
        public genericQueue.GENERIC_Q_CFG shareDynBmpsQ = new genericQueue.GENERIC_Q_CFG();                                                //  2016/06/15

        public genericQueue.GENERIC_Q_CFG guestQ = new genericQueue.GENERIC_Q_CFG();                                                       //  2008/01/18,
        //GENERIC_Q_CFG								phoneMsgrQ;													//  2008/02/10, phoneMessengerQ
        public genericQueue.GENERIC_Q_CFG dynBmpQ = new genericQueue.GENERIC_Q_CFG();                                                  //  2009/09/05

        public POLICY_isClient policy = new POLICY_isClient();

        //
        public String mutexName_syncSendAv;//[cntof_mutexName];
        public String mutexName_syncActiveMem_prefix1;//[cntof_mutexName_prefix];                  //  2009/05/31
        public String mutexName_syncStartAv;//[cntof_mutexName];

        //
        public boolean b_use2Buf_mediaQ  =  false;//true;
        public boolean b_use2Buf_rtMediaQ  =  false;//true;
        public boolean b_use2Buf_rtOpQ  =  false;//true;
        public boolean b_use2Buf_toSendQ=false;//true;
        //
        public boolean b_use2Buf_playSoundQ_localAv = false;//false;//true;
        public boolean b_use2Buf_playSoundQ_ = false;//true;//false;//true;
        //
        public boolean b_use2Buf_playVideoQ = false;
        public boolean b_use2Buf_displayQ = false;
        public boolean b_use2Buf_capAudioQ = false;
        public boolean b_use2Buf_capAudioAppQ  =false;

        public boolean b_use2Buf_capBmpQ  =  false;
        public boolean b_use2Buf_capBmpVppQ=false;
        public boolean b_use2Buf_transAQ=false;
        public boolean b_use2Buf_transVQ=false;
        public boolean b_use2Buf_transPVQ=false;



        //  2014/09/22
        public boolean ucb__SUPPORT_shareDynBmps__;

        //  2008/05/17
        //unsigned  char								ucbNeedShowFrameInfo_forVideoConference;
        //
        public boolean ucb__SUPPORT_dmoToDec__;                                  //  2010/08/05
        //  2014/04/03
        public boolean ucb__USE_hdEnc_conf__;

        //
        public boolean ucb__USE_decObj_a_mediaCodec_async__;

        //  2012/05/15
        public boolean ucbTestGps;
        //  2015/05/18
        public boolean ucbPlayLocalAudio_captured;                                   //  2015/05/18
        public boolean ucbPlayRemoteAudio_viewDynBmp;                                //  2015/05/19		 

        //
        public boolean ucb__USE_d2dToRenderTxt__;                                    //  2016/05/18

        //
        public boolean ucb_USE_Fragment_dlgTalk_av;

        public static class Special_dbg {
            public boolean bNo_videoCap;
            public boolean bNo_audioRecord;
            public boolean bNo_audioPlayer;
            //
            public boolean bNo_encV;
            //
            public boolean bDoGC_mainWnd_OnTimer;
            //
            public boolean bTrace_tmpHandler_findOrgMsgAndProc_is;
        }

        //
        public Special_dbg mSpecialDbg = new Special_dbg();
        public boolean ucb_USE_specialDbg;


        //
        public int usMaxCnt_capProcInfoBmps;
        public int usMaxCnt_capProcInfoAudios;

        public int usMaxCnt_pSharedObjs;                                        //  2008/10/04


        //  2015/11/10
        public int usMaxCnt_qmcObjVarInfos;                                     //  2015/11/10
        //  2014/09/23
        public int usMaxCnt_taskInfos;

        //
        public int usTimeoutInMs_player_a;                                      //  2010/06/25
        public int usTimeoutInMs_player_v;

        public int loopCtrl_chkPlayers;                                     //  default 10. 2010/06/26
        public int loopCtrl_timer_dlgTalk_chkRecvdTasks;                        //  default 30. 2010/06/26. 

        //
        public boolean notify_avCall_bNoSound;
        public int usTimeoutInMs_waitForProcessingOf_avTask;
        public int usTimeoutInMs_waitForProcessingOf_otherTask;
        public int usTimeoutInMs_waitIfNoData;

        //
        public boolean b_useAudioQ2_toDecA = false;//false;
        public boolean b_useQ2_toPlayA = false;

        //
        public boolean b_useVideoQ2_toDecV = false;

        //
        public boolean b_useQ2_toEncA  = false;//false;
        //
        public boolean b_useQ2_toEncV  = false;

        //
        public boolean b_useQ2_postEncV  = false;

        //
        public boolean b_no_KeyLowLatency_a;

        //
        public boolean b_no_4k;

        public ctxQmcTmpl.QMC_cfg_debugStatusInfo debugStatusInfo = new ctxQmcTmpl.QMC_cfg_debugStatusInfo();    //  2015/01/23

    }


    public static class QMC_av_channel_status {
        public boolean bChannelReady;
        public long dwLastModifiedTickCnt;
        //
        public int iThreadsStatus;
    }


    public static class TMP_tex_info {
        //
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        public int uiObjType;

        //
        public int texId;
        public int iW;
        public int iH;

    }

    //
    public static class TMP_task_texes {
        public int iTaskId;
        //
        public ctxQmc.TMP_tex_info[] tti_peers = new TMP_tex_info[MAX_conf_activeMems];
        //
        public int usCnt_activeMems;
    }


    //
    public static class TMP_texes {

        public TMP_task_texes taskAvTexes;

        public TMP_texes() {
            taskAvTexes = new TMP_task_texes();
            taskAvTexes.tti_peers = new ctxQmc.TMP_tex_info[MAX_conf_activeMems];
            for (int i = 0; i < taskAvTexes.tti_peers.length; i++) {
                taskAvTexes.tti_peers[i] = new ctxQmc.TMP_tex_info();
            }
            //
            //tti_peer = new ctxQmc.TMP_tex_info();

        }
    }


    public static class QMC_av {

        public QMC_av_channel_status talk = new QMC_av_channel_status();
        public QMC_av_channel_status audio = new QMC_av_channel_status();
        public QMC_av_channel_status video = new QMC_av_channel_status();
        public QMC_av_channel_status robot = new QMC_av_channel_status();
        public QMC_av_channel_status rtOp = new QMC_av_channel_status();

        public boolean m_bInSleepMode;

        //
        public int sdwTickCnt_applyForChkChannels;

        //
        public static class QMC_localAv {

            public qmcSharedObj.CAP_procInfo_video videoCaptureProcInfo = new qmcSharedObj.CAP_procInfo_video();                                       //  200903/18

            //
            public qyDefs.QY_MESSENGER_ID idInfo_curConference = new qyDefs.QY_MESSENGER_ID();                                       //  (idInfo)
            public int uiTaskType_curConference;                                  //  
            //

            public qmcSharedObj.CAP_procInfo_recordSound recordSoundProcInfo = new qmcSharedObj.CAP_procInfo_recordSound();                                       //  2009/02/11

            //  
            public qmcCommFunc_is_open.QY_PLAYER player = new qmcCommFunc_is_open.QY_PLAYER();

            //  2014/04/07
            //HWND hWnd_selectAvCompressor;

            //
            public long dwLastTickCnt_aCap;
            public long dwLastTickCnt_vCap;


        }

        public QMC_localAv localAv = new QMC_localAv();

        //
        public TMP_texes texes = new TMP_texes();

        //  
        public int usCnt_players;
        public qmcCommFunc_is_open.QY_PLAYER[] pPlayers;
        //
        public boolean bNeedChkPlayers;                   //  2018/06/16
    }

    //


    public static abstract class CCtxQmc extends ctxQmcTmpl.CCtxQmcTmpl {
        public ctxQmc.QMC_cfg cfg = new QMC_cfg();

        public int  uiDevType = CONST_objType_android;                                                 //  2007/08/03
        public int  uiTerminalType  =  0;												//

        //
        public qnmCommProc_mis.QY_COMM_AUTHINFO_MIS authInfo = new qnmCommProc_mis.QY_COMM_AUTHINFO_MIS();

        //
        int uiLastTranNo;
        public boolean bQuit_threadProc_isCli;
        public boolean bEnd_threadProc_isCli;
        public Thread hThread_mgr;
        public int dwThreadId_mgr;
        public Thread hThread_display;                                         //  2007/06/09		
        //DWORD dwThreadId_display;
        //
        public Thread hThread_work;
        public int dwThreadId_work;
        //
        public Thread hThread_scheduler;
        //DWORD dwThreadId_scheduler;
        //
        public Thread hThread_robot;                                               //  2007/07/04
        public int dwThreadId_robot;
        //
        public Thread hThread_media;
        public int dwThreadId_media;
        //
        public Thread hThread_realTimeMedia;
        public int dwThreadId_realTimeMedia;
        //  2016/12/26
        public Thread hThread_rtOp;
        //DWORD dwThreadId_rtOp;

        //
        public int dwThreadId_gui;

        //
        public long tgt_start;          //  系统启动的时刻，在initVar里设置

        //
        public genericQueue.GENERIC_Q mgrQ;
        //
        public genericQueue.GENERIC_Q displayQ;
        public genericQueue.GENERIC_Q workQ;
        public genericQueue.GENERIC_Q schedulerQ;
        public genericQueue.GENERIC_Q robotQ;
        public qyq2.QY_Q2 mediaQ2;
        public qyq2.QY_Q2 realTimeMediaQ2;
        public qyq2.QY_Q2 rtOpQ2;


        public mcSharedQ.CMcSharedQ processQ_media;

        public genericQueue.GENERIC_Q imMsgLogQ;

        //
        public policyAvParams.PolicyAvParams p_gAvParams;

        //
        public Object pFuncs_for_isCliHelp;                                     //  2013/06/08


        public qnmCommProc_mis.IpcProc m_ipcProc = new qnmCommProc_mis.IpcProc();

        //  av, 2008/03/18
        public QMC_av av = new QMC_av();

        //
        public Semaphore hMutex_syncQdcUnit;
        //
        public EventWaitHandle hEvt_notify_thread_isCli;

        public qmcSharedObj.CAP_procInfo_bmpCommon[] pCapProcInfoBmps;
        public qmcSharedObj.CAP_procInfo_audioCommon[] pCapProcInfoAudios;


        //  
        public qmcSharedObj.QY_sharedObj_sync[] pSharedObjSyncs;                                          //  2014/09/30


        //音频包的全局QBuffer
        //生产者：压缩线程
        //消费者：预发送线程
        public QY_qBuf2 pOutputQBuf2_local_a;

        //视频包的全局QBuffer
        public QY_qBuf2 pOutputQBuf2_local_v;



        //
        public qmcTaskInfo.QMC_TASK_INFO[] pQmcTaskInfos;

        //unsigned char ucCnt_misCnts;
        public qmCommFunc_mis.MIS_CNT pMisCnt;   //服务器相关结构

        //
        public boolean  bUse100k;
        //
        /*
        public int use100k_enc_sleepInMs;
        public int use100k_v_w;
        public int use100k_v_h;
        public int use100k_v_biterate;
        public int use100k_v_perSnd_sleepInMs;

        //
        public int use100k_v_frameRate;
        public int use100k_v_I_FRAME_INTERVAL;

         */

        //
        public com.qycx.tmpffmpeglibrary.TmpCusSystem.CfgInfo_100k  cfgInfo100k  =  new com.qycx.tmpffmpeglibrary.TmpCusSystem.CfgInfo_100k();

        //
        public boolean use100k_onlyV;       //  true 仅发送视频. false 发送音频
        //




        //
        public static class QMC_waitToQuit {
            public long dwTickCnt_start;
            public int dwTimeInMs;
            //
            public String hint;
        }

        public QMC_waitToQuit waitToQuit = new QMC_waitToQuit();                                                   //  2011/02/02


        //
        public ctxQmcTmpl.QMC_status status = new ctxQmcTmpl.QMC_status();


        //
        public int sdwTickCnt_applyForChkChannels = 0;

        //
        public boolean m_bNeedToRemoveTaskAtOnce = false;

        //
        public int m_n_swapBuffersFailed  =  0;

        //
        public CCtxQmc() {
            m_iCtxType = ctxQyTmpl.Consts.CONST_ctxType_qmc;
        }


        @Override
        public int setQmDbFuncs(int iDbType, myDb.QM_dbFuncs pDbFuncs) {
            return -1;
        }

        abstract public int postMsg2Mgr_mc(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMIS_CNT, qnmCommProc_mis.MSG_ROUTE pRoute, int uiMisMsgType, byte ucFlg, short usCode, long tStartTime, int uiTranNo, int uiSeqNo, qnmCommProc_mis.IM_CONTENTU data, int dataLen, long pIdInfo_logicalPeer, long pIdInfo_dst, int uiChannelType, qnmCommProc_mis.CMIS_MSGU pMsgBuf, boolean bLog);


        abstract public int postReciveMsg2Mgr_mc(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMIS_CNT, qnmCommProc_mis.MSG_ROUTE pRoute, int uiMisMsgType, qyMsgFunc.QY_MSG_HEAD msgHead, int uiSeqNo, qnmCommProc_mis.MIS_MSGU data, long pIdInfo_logicalPeer, long pIdInfo_dst, int uiChannelType, qnmCommProc_mis.CMIS_MSGU pMsgBuf, boolean bLog);

        abstract public int postImMsg2Log_isClient(qnmCommProc_mis.MIS_MSGU pMsg, int lenInBytes_msg);


        abstract public int recoverMessenger(myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, qnmCommProc_mis.QY_MESSENGER_INFO pObj,
                                             qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, long tLastModifiedTime, boolean bLog, genericQueue.GENERIC_Q pLogQ);

        //abstract public  int recoverImObjRules(QM_dbFuncs pDbFuncs, Object pDb, int iDbType, String misServName, qyDefs.QY_MESSENGER_ID pIdInfo, REFRESH_imObjRules_req pReq, time_t tLastModifiedTime)  =  NULL;
        abstract public int recoverImGrp(myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, int uiObjType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo,
                                         long tLastModifiedTime, boolean bNoGrpName);

        abstract public int recoverImGrpMem(myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qnmCommProc_mis.IM_GRP_MEM pGrpMem, long tLastModifiedTime);

        public Activity_dlgTalk tryToTalkToMessenger_any(long ui64Id, boolean bNeedNotShowWnd, boolean bActivateWnd, Object phWnd) {
            return null;
        }

        public qmcTaskInfo.QMC_TASK_INFO getQmcTaskInfoByIndex(ctxCaller.CTX_caller ctxCaller, int index, String hint) {
            ctxQmc.CCtxQmc pProcInfo = this;

            return qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(ctxCaller, pProcInfo, index, hint);
        }

        public qmcSharedObj.QY_sharedObj_sync getSharedObjSyncByIndex(int index) {
            ctxQmc.CCtxQmc pProcInfo = this;

            if (null == pProcInfo) return null;
            if (null == pProcInfo.pSharedObjSyncs) return null;
            if (index < 0 || index >= pProcInfo.cfg.usMaxCnt_pSharedObjs) return null;

            qmcSharedObj.QY_sharedObj_sync[] pSharedObjInfos = pProcInfo.pSharedObjSyncs;

            return pSharedObjInfos[index];

        }

        public qmcSharedObj.CAP_procInfo_bmpCommon getCapBmpBySth(int index, int uiCapType) {
            ctxQmc.CCtxQmc pProcInfo = this;

            if (index < 0 || index >= pProcInfo.cfg.usMaxCnt_capProcInfoBmps) return null;
            //  if  (  !index  )  return  null;
            if (0 == index) return pProcInfo.av.localAv.videoCaptureProcInfo; //  2009/06/02
            if (0 != uiCapType) {
                if (pProcInfo.pCapProcInfoBmps[index].uiType != uiCapType) return null;
            }
            return pProcInfo.pCapProcInfoBmps[index];
        }


        public qmcSharedObj.CAP_procInfo_audioCommon getCapAudioBySth(int index, int uiCapType) {
            ctxQmc.CCtxQmc pProcInfo = this;

            if (index < 0 || index >= pProcInfo.cfg.usMaxCnt_capProcInfoAudios) return null;
            //  if  (  !index  )  return  null;
            if (0 == index) return pProcInfo.av.localAv.recordSoundProcInfo;
            if (0 != uiCapType) {
                if (pProcInfo.pCapProcInfoAudios[index].uiType != uiCapType) return null;
            }
            return pProcInfo.pCapProcInfoAudios[index];


        }

        //
        public int resizeRgb24(byte[] src, int srcWidth, int srcHeight, byte[] dst, int dstWidth, int dstHeight) {
            return -1;
        }

        public int initCompressVideo(qyCommProc_open.BITMAPINFOHEADER pBmpInfo_input, int uiCapType, qnmCommProc_mis.VIDEO_COMPRESSOR_CFG pCompressor, boolean bCapDev,
                                     int uiBufSize_pData, qmcSharedObj.COMPRESS_VIDEO pCompressVideo) {
            return compressVideo.qyFuncs.initCompressVideo(this, pBmpInfo_input, uiCapType, pCompressor, bCapDev, uiBufSize_pData, pCompressVideo);
        }

        //  2016/04/26
        public int exitCompressVideo(boolean bCapDev, qmcSharedObj.COMPRESS_VIDEO pCompressVideo) {
            return compressVideo.qyFuncs.exitCompressVideo(this, bCapDev, pCompressVideo);
        }

        public int stopLocalAudioRecorder(int index_sharedObj, int nTries) {
            return guiShare.GuiShare.pf_stopLocalAudioRecorder.call(this, index_sharedObj, nTries);
        }

        public qyMcMainCusModule.CUS_MODULES getCusModules() {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) return null;

            return pQyMc.cusModules;
        }

        public int toShareVideo_post(ctxCaller.CTX_caller ctxCaller, qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar, int uiCapType, int iIndex_capProcInfo, qnmCommProc_mis.TRANSFER_VIDEO_DATA tvd, qmcSharedObj.COMPRESS_VIDEO pCompressVideo, qmcSharedObj.QY_SHARED_OBJ pSharedObj, qnmCommProc_mis.MIS_MSGU pMsgBuf) {
            return qmc_toShareVideo_post.qyFuncs.toShareVideo_post_qmc(ctxCaller, tmpVar,this, uiCapType, iIndex_capProcInfo, tvd,  pCompressVideo, pSharedObj, pMsgBuf);
        }

        public int drawLocalVideoData(int uiTranNo, qnmCommProc_mis.myDRAW_VIDEO_DATA pkts, int pktsLen, boolean pbPktsRedirected, int uiCapType, int iIndex_capBmp, int iTaskId, qmCommFunc_mis.MIS_CNT pMIS_CNT, qyDefs.QY_MESSENGER_ID pIdInfo_peer) {
            return -1;
            //return  qyFuncs.drawLocalVideoData(this, uiTranNo, pkts, pktsLen, pbPktsRedirected, uiCapType, iIndex_capBmp, iTaskId, (MIS_CNT)pMIS_CNT, pIdInfo_peer);
        }

        public int drawVideoData(qnmCommProc_mis.myDRAW_VIDEO_DATA pkts[], int pktsLen, boolean pbPktsRedirected, qmcTransform.QY_TRANSFORM pQY_TRANSFORM) {
            ctxQmc.CCtxQmc pProcInfo = this;
            return qyMcMainProc_isCli_media.qyFuncs.drawVideoData(pProcInfo, pkts, pktsLen, pbPktsRedirected, pQY_TRANSFORM);
        }

        public int playLocalAudioData(int iTaskId, int uiTranNo, qyCommProc_open.WAVEFORMATEX pWf, myPlayAudioData.myPLAY_AUDIO_DATA pPkt, int pktLen, boolean pbPktRedirected, int uiCapType, int iIndex_capAudio) {
            //return  ::playLocalAudioData(  this,  iTaskId,  uiTranNo,  pWf,  pPkt,  pktLen,  pbPktRedirected,  uiCapType,  iIndex_capAudio  );
            return qyMcMainMediaReceiver.qyFuncs.playLocalAudioData(this, iTaskId, uiTranNo, pWf, pPkt.head.uiSampleTimeInMs, pPkt.memory.m_pBuf, pPkt.uiSize_dec, uiCapType, iIndex_capAudio);

        }

        //
        public int playAudioData(int iSampleTimeInMs, int uiPts, byte[] pInput, int inputLen, int iIndex_player) {
            return qyMcMainMediaReceiver.qyFuncs.playAudioData(this, iSampleTimeInMs, uiPts, pInput, inputLen, iIndex_player);
        }


        public int get_deced_pktResType(int uiModuleType, int iFourcc, int pusPktResType_o)                //  2015/10/04
        {
            int iErr = -1;
            int usPktResType = 0;

            if (uiModuleType == CONST_moduleType_mediaCodec) {
                usPktResType = CONST_pktResType_sharedTex;
                return usPktResType;
            }

            return iErr;
        }


        public int doApplyForPlayer(Object hMainWnd, qnmCommProc_mis.MIS_MSGU pMsg) {
            return doApplyForPlayer.qyFuncs.doApplyForPlayer(hMainWnd, pMsg);
        }

        public qyq2.QY_Q2 get_realTimeMediaQ2() {
            return this.realTimeMediaQ2;
        }


        public qyq2.QY_Q2 get_mediaQ2() {
            return this.mediaQ2;
        }

        //
        public boolean bRealtimeMediaChannelReady() {
            return this.av.audio.bChannelReady;
        }


        //  2015/10/14
        public boolean bMediaChannelReady() {
            return this.av.video.bChannelReady;
        }

    }


    public static class qyFuncs {

        public static boolean bQThreadExists(qmcCommFunc_is_open.PLAY_AUDIO_procInfo p) {
            if (p.uiPlayType != 0) {
                Log.d("not finished a.bQThreadExists");
                return true;
            }
            //
            return false;
        }

        public static boolean bQThreadExists(qmcCommFunc_is_open.PLAY_VIDEO_procInfo p) {
            if (p.uiPlayType != 0) {
                Log.d("not finished v.bQThreadExists");
                return true;
            }
            //
            return false;
        }


        public static ctxQyMc.PF_exitVar exitVar_isGui = (ctxQyMc.CCtxQyMc pQyMc, qyMcMainObj.QY_SERVICEGUI_INFO pSci, IntBuffer p2) -> {
            //QY_MC pQyMc = (QY_MC)p0;
            //qyMcMainObj.QY_SERVICEGUI_INFO pSci = (qyMcMainObj.QY_SERVICEGUI_INFO)p1;
            //  p2
            int i;


            if (pQyMc.iServiceId != CONST_qyServiceId_is) return 0;

            //
            showInfo_open.qyFuncs.showInfo_open(0, null,"exitVar_isGui enters.");

            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pSci.pVar;

            //
            closeOtherPlayers(pProcInfo);


            //  2010/08/05
            pProcInfo.processQ_media.emptyQ();
            //pProcInfo.processQ_robot.emptyQ(  );
            //  2014/09/24
            freeTaskInfos(pProcInfo);
            //
            //  2014/09/24
            //if (bNoTaskInfoExists(pProcInfo))
            {
                //MACRO_safeFree(pProcInfo.pQmcTaskInfos);
                pProcInfo.pQmcTaskInfos = null;
            }

            //
            if  (  pProcInfo.pOutputQBuf2_local_a!=null) {
                pProcInfo.pOutputQBuf2_local_a.exitQyQ2();
                pProcInfo.pOutputQBuf2_local_a=null;
            }
            if  (  pProcInfo.pOutputQBuf2_local_v!=null) {
                pProcInfo.pOutputQBuf2_local_v.exitQyQ2();;
                pProcInfo.pOutputQBuf2_local_v=null;
            }

            //
            pProcInfo.pSharedObjSyncs  =  null;


            //
            if (pProcInfo.av.texes != null) {
                if (pProcInfo.av.texes.taskAvTexes != null) {
                    pProcInfo.av.texes.taskAvTexes.tti_peers = null;
                    pProcInfo.av.texes.taskAvTexes = null;
                }
                pProcInfo.av.texes = null;
            }

            if (pProcInfo.av.pPlayers != null) {  //  ÕâÀï»¹Ã»ÅªºÃ£¬ÔõÃ´À´²»Ò»¿ªÊ¼Æô¶¯Ïß³ÌÄØ¡£¶ø´ï¡£¡£¡£¡£¡£

                for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                    if (!bQThreadExists(pProcInfo.av.pPlayers[i].audio)) {
                        if (pProcInfo.av.pPlayers[i].audio.bQInited) {
                            exitQyQ2(pProcInfo.av.pPlayers[i].audio.q2);
                            pProcInfo.av.pPlayers[i].audio.q2 = null;
                        }
                        pProcInfo.av.pPlayers[i].audio.qBuf2.exitQyQ2();
                        pProcInfo.av.pPlayers[i].audio.qBuf2 = null;
                    } else {
                    }
                    if (pProcInfo.av.pPlayers[i].video.bQInited) {
                        exitQyQ2(pProcInfo.av.pPlayers[i].video.q2);
                        pProcInfo.av.pPlayers[i].video.q2 = null;
                    }
                    if (pProcInfo.av.pPlayers[i].video.ucbDisplayQInited) {
                        exitQyQ2(pProcInfo.av.pPlayers[i].video.displayQ2);
                        pProcInfo.av.pPlayers[i].video.displayQ2 = null;
                    }

                    //
                    pProcInfo.av.pPlayers[i] = null;
                }

                //free(pProcInfo.av.pPlayers);
                pProcInfo.av.pPlayers = null;
            }

            if (!bQThreadExists(pProcInfo.av.localAv.player.audio)) {
                if (pProcInfo.av.localAv.player.audio.bQInited) {
                    exitQyQ2(pProcInfo.av.localAv.player.audio.q2);
                    pProcInfo.av.localAv.player.audio.q2 = null;
                }
                pProcInfo.av.localAv.player.audio.qBuf2.exitQyQ2();
                pProcInfo.av.localAv.player.audio.qBuf2 = null;
            } else {
            }
            if (pProcInfo.av.localAv.player.video.bQInited) {
                exitQyQ2(pProcInfo.av.localAv.player.video.q2);
                pProcInfo.av.localAv.player.video.q2 = null;
            }
            if (pProcInfo.av.localAv.player.video.ucbDisplayQInited) {
                exitQyQ2(pProcInfo.av.localAv.player.video.displayQ2);  //  2009/07/25
                pProcInfo.av.localAv.player.video.displayQ2 = null;
            }
            //
            pProcInfo.av.localAv.player = null;

            //for  (  int  i  =  0;  i  <  mycountof(  pProcInfo.misCnts  );  i  ++  )
            {
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;   //  getMisCntByIndex(0, pProcInfo, i);//&pProcInfo.misCnts[i];
                if (pMisCnt != null) {
                    //
                    //if (pMisCnt.cfg.dynCfg.cnt.usCntPort!=0)
                    {

                        if (pProcInfo.hEvt_notify_thread_isCli != null) {
                            pProcInfo.hEvt_notify_thread_isCli.Close();
                            pProcInfo.hEvt_notify_thread_isCli = null;
                        }

                        if (pProcInfo.hMutex_syncQdcUnit != null) {
//                            pProcInfo.hMutex_syncQdcUnit.unlock();
                            pProcInfo.hMutex_syncQdcUnit = null;
                        }
                        //
                        if (pMisCnt.hMutex_syncTask != null) {
//                            pMisCnt.hMutex_syncTask.unlock();
                            pMisCnt.hMutex_syncTask = null;
                        }
                        if (pMisCnt.hMutex_syncStartAv != null) {
//                            pMisCnt.hMutex_syncStartAv.unlock();
                            pMisCnt.hMutex_syncStartAv = null;
                        }
                        if (pMisCnt.hMutex_syncSendAv != null) {
//                            pMisCnt.hMutex_syncSendAv.unlock();
                            pMisCnt.hMutex_syncSendAv = null;
                        }
                        if (pMisCnt.hMutex_syncActiveMem != null) {
//                            pMisCnt.hMutex_syncActiveMem.unlock();
                            pMisCnt.hMutex_syncActiveMem = null;
                        }
                        if (pMisCnt.hMutex_syncIndex_dec != null) {
//                            pMisCnt.hMutex_syncIndex_dec.unlock();
                            pMisCnt.hMutex_syncIndex_dec = null;
                        }
                        if (pMisCnt.hMutex_syncShareEglContext != null) {
//                            pMisCnt.hMutex_syncShareEglContext.unlock();
                            pMisCnt.hMutex_syncShareEglContext = null;
                        }


                        //  ¹Ø±Õ´¥·¢ÐÅºÅµÆ
                        int j;
                        for (j = 0; j < pMisCnt.channels.length; j++) {
                            qmCommFunc_mis.MIS_CHANNEL pChannel = pMisCnt.channels[j];
                            if (pChannel == null) continue;
                            //
                            //
                            if  (  pChannel.m_b_useQ2_toOutput) {
                                exitQyQ2(pChannel.outputQ21);
                                pChannel.outputQ21 = null;
                            }
                            else  {
                                  pChannel.outputQBuf2.exitQyQ2();
                                  pChannel.outputQBuf2=null;
                            }
                            exitQyQ2(pChannel.toSendQ2);
                            pChannel.toSendQ2 = null;
                            //
                            //exitGenericQ(pChannel.inCacheQ);
                            //pChannel.inCacheQ = null;
                            pChannel.inCache_buf=null;

                            //
                            pMisCnt.channels[j] = null;
                        }
                        pMisCnt.channels = null;

                        //
                        exitGenericQ(pMisCnt.recentFriendQ);
                        pMisCnt.recentFriendQ = null;

                        //exitGenericQ(pMisCnt.talkingFriendQ); pMisCnt.talkingFriendQ = null;
                        pMisCnt.talkingFriendQ.clear();
                        pMisCnt.talkingFriendQ = null;

                        //
                        exitGenericQ(pMisCnt.imObj_intQ);
                        pMisCnt.imObj_intQ = null;

                        //
                    }

                }

                //  2012/05/08
            }
            //  2015/07/02
            //MACRO_safeFree(pProcInfo.pMisCnts);
            pProcInfo.pMisCnt = null;


            //
            exitGenericQ(pProcInfo.imMsgLogQ);        //  2010/07/17

            //
            pProcInfo.processQ_media.exitQ();
            //pProcInfo.processQ_robot.exitQ(  );

            //
            exitGenericQ(pProcInfo.mgrQ);
            exitGenericQ(pProcInfo.robotQ);           //  2007/07/04
            exitGenericQ(pProcInfo.displayQ);
            exitGenericQ(pProcInfo.workQ);
            exitGenericQ(pProcInfo.schedulerQ);
            //  exitGenericQ(  &pProcInfo.mediaQ  );
            //  exitGenericQ(  &pProcInfo.realTimeMediaQ  );
            exitQyQ2(pProcInfo.mediaQ2);
            exitQyQ2(pProcInfo.realTimeMediaQ2);
            exitQyQ2(pProcInfo.rtOpQ2);   //  2016/12/26

            audioRecord_webRtc.qyFuncs.exit_webRtc();
            String str="exitVar_isGui leaves.";
            Log.d(str);
            showInfo_open.qyFuncs.showInfo_open(0, null,str);
            return 0;
        };


        public static ctxQyMc.PF_initVar initVar_isGui = (ctxQyMc.CCtxQyMc pQyMc, qyMcMainObj.QY_SERVICEGUI_INFO pSci, IntBuffer p2) -> {
            int iErr = -1;
            int i;
            String str;

            if (pQyMc.iServiceId != CONST_qyServiceId_is) return 0;

            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) {
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pSci.pVar;

            str="initVar_isGui enters";
            Log.d(str);
            showInfo_open.qyFuncs.showInfo_open(0, null,str);
            pProcInfo.pQyMc = pQyMc;
            pProcInfo.p_gAvParams = new policyAvParams.PolicyAvParams();
            pProcInfo.pFuncs_for_isCliHelp = new funcsForIsCliHelp.FUNCS_for_isCliHelp();
            pProcInfo.str_who_showInfo = "qmc";
            pProcInfo.cfg.uiMaxSendMsgs = CONST_maxSendMsgs_isClient;
            pProcInfo.cfg.usMaxMsgrRecentFriends = CONST_usMaxMsgrRecentFriends;
            pProcInfo.cfg.uiSizePerSnd_file_noAv = DEFAULT_uiSizePerSnd_file_noAv;
            pProcInfo.cfg.uiSizePerSnd_file_avExists = DEFAULT_uiSizePerSnd_file_avExists;
            pProcInfo.cfg.uiSizePerSnd_media = DEFAULT_uiSizePerSnd_media_isCli_android;       //  2008/04/01
            pProcInfo.cfg.usNeedShowFrameInfoIntervalInS = DEFAULT_usNeedShowFrameInfoIntervalInS;
            pProcInfo.cfg.uiBufSize_myDrawVideoData = bufSize_myDrawVideoData_4k;    //  2014/06/01
            pProcInfo.cfg.uiBufSize_myDrawVideoData = bufSize_myDrawVideoData_2k;    //  2014/06/01
            pProcInfo.cfg.uiBufSize_myDrawVideoData = 1;    // for mediaCodec.
            pProcInfo.cfg.uiBufSize_myPlayAudioData = bufSize_myPlayAudioData1;   //  2010/04/24
            pProcInfo.cfg.envVar.usMaxQNodes_mediaQ = CONST_uiMaxQNodes_mediaQ_x64;
            pProcInfo.cfg.envVar.usQNodes_warningInterval_mediaQ = CONST_uiQNodes_warningInterval_mediaQ;
            pProcInfo.cfg.envVar.usMaxQNodes_rtMediaQ = CONST_uiMaxQNodes_mediaQ_x64;
            pProcInfo.cfg.envVar.usQNodes_warningInterval_rtMediaQ = CONST_uiQNodes_warningInterval_mediaQ;
            pProcInfo.cfg.envVar.usMaxQNodes_rtOpQ = pProcInfo.cfg.envVar.usMaxQNodes_rtMediaQ;
            pProcInfo.cfg.envVar.usQNodes_warningInterval_rtOpQ = pProcInfo.cfg.envVar.usQNodes_warningInterval_rtMediaQ;
            pProcInfo.cfg.envVar.usMaxQNodes_playSoundQ = CONST_uiMaxQNodes_playSoundQ;
            pProcInfo.cfg.envVar.usMaxQNodes_playVideoQ = CONST_uiMaxQNodes_playVideoQ;
            pProcInfo.cfg.envVar.usMaxQNodes_displayVideoQ = CONST_uiMaxQNodes_displayVideoQ;
            pProcInfo.cfg.envVar.usMaxQNodes_dlgTalk_processQ = 200;       //
            pProcInfo.cfg.envVar.usMaxQNodes_imMsgLogQ = 200;
            pProcInfo.cfg.envVar.default_ucbLetConfMgrSetMicOn = false;
            pProcInfo.cfg.ucb__SUPPORT_shareDynBmps__ = false;
            pProcInfo.cfg.ucb__SUPPORT_dmoToDec__ = false;      //  2012/03/22, 因为filterGraph有死锁现象,所以,这里选择使用dmo.
            pProcInfo.cfg.ucb__USE_hdEnc_conf__ = false;   //  2015/10/05	//  true;	//  FALSE;	//  2014/04/03
            pProcInfo.cfg.ucb__USE_decObj_a_mediaCodec_async__ = false;

            init_webRtc();

            if (guiShare.GuiShare.pf_getPolicyIsClient.call(pProcInfo, pProcInfo.cfg.policy) != 0)
            {
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pProcInfo.uiDevType = CONST_objType_android;
//            pProcInfo.uiDevType = CONST_objType_pc;


            //
            //
            pProcInfo.uiTerminalType = CONST_terminalType_conf;//qyGetTerminalType();



            //
            pProcInfo.authInfo.iAppType = pQyMc.iAppType;
            pProcInfo.authInfo.ver = qyCustom.qyFuncs.qnmVerStr(pSci.cfg.iServiceId);
            pProcInfo.authInfo.usLangId = pQyMc.env.usLangId;
            pProcInfo.authInfo.uiDevType = pProcInfo.uiDevType;
            pProcInfo.authInfo.usAuthType = CONST_authType_logon;
            pProcInfo.pMisCnt = new qmCommFunc_mis.MIS_CNT();
            pProcInfo.pMisCnt.cfg.dynCfg = pQyMc.cfg.dynCfg;

            pProcInfo.cfg.mgrQEx.common.name = "mgrQ";
            pProcInfo.cfg.mgrQEx.common.uiMaxQNodes = CONST_uiMaxQNodes_mgrQ_isCli;
            pProcInfo.cfg.mgrQEx.common.mutexName_prefix = "qMgrQ_isCli";
            pProcInfo.cfg.mgrQEx.common.bUseEvt = true;
//            pProcInfo.cfg.mgrQEx.pfQElemNew = mymalloc;
//            pProcInfo.cfg.mgrQEx.pfQElemFree = myfree;

            pProcInfo.cfg.displayQ.name = "displayQ";
            pProcInfo.cfg.displayQ.mutexName_prefix = "qDispQ";
            pProcInfo.cfg.displayQ.uiMaxCnt_semaTrigger = CONST_uiMaxCnt_semaTrigger_displayQ;
            pProcInfo.cfg.displayQ.uiMaxQNodes = CONST_uiMaxQNodes_displayQ;

            pProcInfo.cfg.workQ.name = "workQ";
            pProcInfo.cfg.workQ.mutexName_prefix = "qWorkQ";
            pProcInfo.cfg.workQ.uiMaxCnt_semaTrigger = CONST_uiMaxCnt_semaTrigger_workQ;
            pProcInfo.cfg.workQ.uiMaxQNodes = CONST_uiMaxQNodes_workQ;

            pProcInfo.cfg.schedulerQ.name = "schedulerQ";
            pProcInfo.cfg.schedulerQ.mutexName_prefix = "qSchQ";
            pProcInfo.cfg.schedulerQ.uiMaxCnt_semaTrigger = CONST_uiMaxCnt_semaTrigger_schedulerQ;
            pProcInfo.cfg.schedulerQ.uiMaxQNodes = CONST_uiMaxQNodes_schedulerQ;

            pProcInfo.cfg.robotQ.name = "robotQ";
            pProcInfo.cfg.robotQ.mutexName_prefix = "qRobotQ";
            pProcInfo.cfg.robotQ.uiMaxCnt_semaTrigger = CONST_uiMaxCnt_semaTrigger_robotQ;
            pProcInfo.cfg.robotQ.uiMaxQNodes = CONST_uiMaxQNodes_robotQ;

            pProcInfo.cfg.mediaQ.name = "mediaQ";
            pProcInfo.cfg.mediaQ.mutexName_prefix = "qMediaQ";
            pProcInfo.cfg.mediaQ.uiMaxQNodes = pProcInfo.cfg.envVar.usMaxQNodes_mediaQ;

            pProcInfo.cfg.realTimeMediaQ.name = "rtMediaQ";
            pProcInfo.cfg.realTimeMediaQ.mutexName_prefix = "qRtMediaQ";
            pProcInfo.cfg.realTimeMediaQ.uiMaxQNodes = pProcInfo.cfg.envVar.usMaxQNodes_rtMediaQ;

            pProcInfo.cfg.rtOpQ.name = "rtOpQ";
            pProcInfo.cfg.rtOpQ.mutexName_prefix = "qRtOpQ";
            pProcInfo.cfg.rtOpQ.uiMaxQNodes = pProcInfo.cfg.envVar.usMaxQNodes_rtOpQ;

            pProcInfo.cfg.processQ_robot.name = "processQ_robot";
            pProcInfo.cfg.processQ_robot.uiMaxQNodes = CONST_uiMaxQNodes_processQ_robot;

            pProcInfo.cfg.processQ_media.name = "processQ_media";
            pProcInfo.cfg.processQ_media.uiMaxQNodes = CONST_uiMaxQNodes_processQ_media;

            pProcInfo.cfg.outputQ_toMix_audio.name = "output_mixQ_a";
            pProcInfo.cfg.outputQ_toMix_audio.mutexName_prefix = "qoutput_mixQ_a";
            pProcInfo.cfg.outputQ_toMix_audio.uiMaxQNodes = CONST_uiMaxQNodes_outputQ_toMix_audio;

            pProcInfo.cfg.outputQ_toMosaic_video.name = "output_mixQ_v";
            pProcInfo.cfg.outputQ_toMosaic_video.mutexName_prefix = "qOutput_mixQ_v";
            pProcInfo.cfg.outputQ_toMosaic_video.uiMaxQNodes = CONST_uiMaxQNodes_outputQ_toMosaic_video;

            pProcInfo.cfg.playSoundQ.name = "playSoundQ";
            pProcInfo.cfg.playSoundQ.mutexName_prefix = "playSoundQ";
            pProcInfo.cfg.playSoundQ.uiMaxQNodes = pProcInfo.cfg.envVar.usMaxQNodes_playSoundQ;

            pProcInfo.cfg.playVideoQ.name = "playVideoQ";
            pProcInfo.cfg.playVideoQ.mutexName_prefix = "playVideoQ";
            pProcInfo.cfg.playVideoQ.uiMaxQNodes = pProcInfo.cfg.envVar.usMaxQNodes_playVideoQ;

            pProcInfo.cfg.displayVideoQ.name = "displayVQ";
            pProcInfo.cfg.displayVideoQ.mutexName_prefix = "displayVQ";
            pProcInfo.cfg.displayVideoQ.uiMaxQNodes = pProcInfo.cfg.envVar.usMaxQNodes_displayVideoQ;

            pProcInfo.cfg.transformQ.name = "transQ";
            pProcInfo.cfg.transformQ.mutexName_prefix = "transQ";
            pProcInfo.cfg.transformQ.uiMaxQNodes = CONST_uiMaxQNodes_transformQ;

            pProcInfo.cfg.appQ.name = "appQ";
            pProcInfo.cfg.appQ.mutexName_prefix = "appQ";
            pProcInfo.cfg.appQ.uiMaxQNodes = CONST_uiMaxQNodes_appQ;

            pProcInfo.cfg.vppQ.name = "vppQ";
            pProcInfo.cfg.vppQ.mutexName_prefix = "vppQ";
            pProcInfo.cfg.vppQ.uiMaxQNodes = CONST_uiMaxQNodes_vppQ;

            pProcInfo.cfg.toShareAudioQ.name = "shareAQ";
            pProcInfo.cfg.toShareAudioQ.mutexName_prefix = "shareAQ";
            pProcInfo.cfg.toShareAudioQ.uiMaxQNodes = CONST_uiMaxQNodes_toShareAudioQ;

            pProcInfo.cfg.toShareVideoQ.name = "shareVQ";
            pProcInfo.cfg.toShareVideoQ.mutexName_prefix = "shareVQ";
            pProcInfo.cfg.toShareVideoQ.uiMaxQNodes = CONST_uiMaxQNodes_toShareVideoQ;

            pProcInfo.cfg.toShareVideo_postQ.name = "shareVPostQ";
            pProcInfo.cfg.toShareVideo_postQ.mutexName_prefix = "shareVPostQ";
            pProcInfo.cfg.toShareVideo_postQ.uiMaxQNodes = CONST_uiMaxQNodes_toShareVideo_postQ;

            pProcInfo.cfg.dispatchQ.name = "dispatchQ";
            pProcInfo.cfg.dispatchQ.mutexName_prefix = "dsptchQ";
            pProcInfo.cfg.dispatchQ.uiMaxQNodes = CONST_uiMaxQNodes_dispatchQ;

            pProcInfo.cfg.talkerThreadQ.name = "talkerThreadQ";
            pProcInfo.cfg.talkerThreadQ.mutexName_prefix = "talkerThreadQ";
            pProcInfo.cfg.talkerThreadQ.uiMaxQNodes = CONST_uiMaxQNodes_talkerThreadQ;

            pProcInfo.cfg.shareDynBmpsQ.name = "shareDynBmpsQ";
            pProcInfo.cfg.shareDynBmpsQ.mutexName_prefix = "shareDynBmpsQ";
            pProcInfo.cfg.shareDynBmpsQ.uiMaxQNodes = CONST_uiMaxQNodes_shareDynBmpsQ;

            pProcInfo.cfg.guestQ.name = "guestQ";
            pProcInfo.cfg.guestQ.uiMaxQNodes = CONST_uiMaxQNodes_guestQ;

            pProcInfo.cfg.dynBmpQ.name = "dynBmpQ";
            pProcInfo.cfg.dynBmpQ.mutexName_prefix = "dynBmpQ";
            pProcInfo.cfg.dynBmpQ.uiMaxQNodes = CONST_uiMaxQNodes_dynBmpQ;

            pProcInfo.cfg.mutexName_syncSendAv = "qySyncSendAv";
            pProcInfo.cfg.mutexName_syncActiveMem_prefix1 = "qySyncAct";
            pProcInfo.cfg.mutexName_syncStartAv = "qyStartAv";

            //
            pProcInfo.tgt_start=tmpFunc.qyFuncs.mytimeGetTime();

            //
            pProcInfo.mgrQ = new genericQueue.GENERIC_Q();
            if (initGenericQ(pProcInfo.cfg.mgrQEx, pProcInfo.mgrQ) != 0) {
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pProcInfo.robotQ = new genericQueue.GENERIC_Q();
            if (initGenericQ(pProcInfo.cfg.robotQ, null, null, null, pProcInfo.robotQ) != 0) {
                Log.e("initGenericQ 1");
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pProcInfo.displayQ = new genericQueue.GENERIC_Q();
            if (initGenericQ(pProcInfo.cfg.displayQ, null, null, null, pProcInfo.displayQ) != 0) {
                Log.e("initGenericQ 2");
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pProcInfo.workQ = new genericQueue.GENERIC_Q();
            if (initGenericQ(pProcInfo.cfg.workQ, null, null, null, pProcInfo.workQ) != 0) {
                Log.e("initGenericQ 3");
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pProcInfo.schedulerQ = new genericQueue.GENERIC_Q();
            if (initGenericQ(pProcInfo.cfg.schedulerQ, null, null, null, pProcInfo.schedulerQ) != 0) {
                Log.e("initGenericQ 4");
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pProcInfo.mediaQ2 = new qyq2.QY_Q2();
            if (initQyQ2(pProcInfo.cfg.mediaQ, pProcInfo.cfg.b_use2Buf_mediaQ,  pQyMc.cfg.rwLockParam, 1, null, null, null, null, pProcInfo.mediaQ2) != 0) {
                Log.e("initGenericQ 5");
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pProcInfo.realTimeMediaQ2 = new qyq2.QY_Q2();
            if (initQyQ2(pProcInfo.cfg.realTimeMediaQ, pProcInfo.cfg.b_use2Buf_rtMediaQ,pQyMc.cfg.rwLockParam, 1, null, null, null, null, pProcInfo.realTimeMediaQ2) != 0) {
                Log.e("initGenericQ 6");
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            //  2016/12/26
            pProcInfo.rtOpQ2 = new qyq2.QY_Q2();
            if (initQyQ2(pProcInfo.cfg.rtOpQ, pProcInfo.cfg.b_use2Buf_rtOpQ,pQyMc.cfg.rwLockParam, 1, null, null, null, null, pProcInfo.rtOpQ2) != 0) {
                Log.e("initGenericQ 7");
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pProcInfo.processQ_media = new mcSharedQ.CMcSharedQ();
            if (!pProcInfo.processQ_media.bSetQInfo("mcSyncMedia", 10, 10000, 10000, 10000, 10000)) {
                Log.e("initGenericQ 8");
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }
            if (pProcInfo.processQ_media.initQ(pProcInfo.cfg.processQ_media, pQyMc.cfg.rwLockParam, mcTask_new, initMisMsg, exitMisMsg, mcTask_free) != 0) {
                Log.e("initGenericQ 9");
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pProcInfo.imMsgLogQ = new genericQueue.GENERIC_Q();
            if (0 != initGenericQ("ImMsgLogQ", "ImMsgLogQ", 0, pProcInfo.cfg.envVar.usMaxQNodes_imMsgLogQ, pProcInfo.imMsgLogQ)) {
                Log.e("initGenericQ 10");

                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;

            }

            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            pMisCnt.pProcInfoParam = pProcInfo;
            pMisCnt.channels = new qmCommFunc_mis.MIS_CHANNEL[CONST_maxConnsPerCli_mis];
            for (int iii = 0; iii < pMisCnt.channels.length; iii++) {
                pMisCnt.channels[iii] = new qmCommFunc_mis.MIS_CHANNEL();
            }
            //
            pMisCnt.channels[0].uiType = CONST_channelType_talking;
            pMisCnt.channels[1].uiType = CONST_channelType_robot;
            pMisCnt.channels[2].uiType = CONST_channelType_media;
            pMisCnt.channels[3].uiType = CONST_channelType_realTimeMedia;
            pMisCnt.channels[4].uiType = CONST_channelType_rtOp;

            pMisCnt.cfg.iObjQType = CONST_objQType_misClient;
            pMisCnt.cfg.mutexName_syncQmObjQ = DEFAULT_mutexName_syncQmObjQ_client;
            pMisCnt.cfg.semaphoreName_syncQmObjQ = DEFAULT_semaphoreName_syncQmObjQ_client;
            pMisCnt.cfg.uiMaxCnt_semaphore_qmObjQ = CONST_maxCnt_sema_syncQmObjQ;

            pMisCnt.cfg.inCacheQ.name = "inCacheQ";
            pMisCnt.cfg.inCacheQ.mutexName_prefix = "qmcInCacheQ";
            pMisCnt.cfg.inCacheQ.uiMaxQNodes = CONST_uiMaxQNodes_inCacheQ_isCli;

            pMisCnt.cfg.toSendQ.name = "toSendQ";
            pMisCnt.cfg.toSendQ.mutexName_prefix = "qmcToSendQ";
            pMisCnt.cfg.toSendQ.uiMaxQNodes = CONST_uiMaxQNodes_toSendQ;

            pMisCnt.cfg.outputQ.name = "outputQ";
            pMisCnt.cfg.outputQ.mutexName_prefix = "qmcOutputQ";
            pMisCnt.cfg.outputQ.uiMaxQNodes = CONST_uiMaxQNodes_outputQ;

            pMisCnt.cfg.talkingFriendQ.name = "talkingFriendQ";
            pMisCnt.cfg.talkingFriendQ.mutexName_prefix = "qyMcTalkQ";
            pMisCnt.cfg.talkingFriendQ.uiMaxQNodes = CONST_uiMaxQNodes_talkingFriendQ;

            pMisCnt.cfg.recentFriendQ.name = "recentFriendQ";
            pMisCnt.cfg.recentFriendQ.mutexName_prefix = "qyMcRecentQ";
            pMisCnt.cfg.recentFriendQ.uiMaxQNodes = CONST_uiMaxQNodes_recentFriendQ;

            pMisCnt.cfg.imObj_intQ.name = "imObj_intQ";
            pMisCnt.cfg.imObj_intQ.mutexName_prefix = "imObj_intQ";
            pMisCnt.cfg.imObj_intQ.uiMaxQNodes = CONST_uiMaxQNodes_imObj_intQ;

            pMisCnt.cfg.usIntervalInS_getIntervalParams = CONST_usIntervalInS_getIntervalParams;
            pMisCnt.cfg.usMaxMsgrRecentFriends = pProcInfo.cfg.usMaxMsgrRecentFriends;
            pMisCnt.cfg.usIntervalInS_refreshRecentFriends = CONST_usIntervalInS_refreshRecentFriends;
            pMisCnt.cfg.usIntervalInS_refreshContactList = CONST_usIntervalInS_refreshContactList;

            pMisCnt.cfg.usMaxContactUpdateInfosPerSnd_grp = CONST_maxContactUpdateInfos_grp;
            pMisCnt.cfg.usMaxContactUpdateInfosPerSnd_grpMem = CONST_maxContactUpdateInfos_grpMem;
            pMisCnt.cfg.usMaxContactUpdateInfosPerSnd_contact = CONST_maxContactUpdateInfos_contact;

            pMisCnt.cfg.usIntervalInS_tryToReg = CONST_usIntervalInS_tryToReg;
            pMisCnt.cfg.usIntervalInS_tryToSndDevInfo = CONST_usIntervalInS_tryToSndDevInfo;
            pMisCnt.cfg.mutexNamePrefix_syncTask = DEFAULT_mutexNamePrefix_syncTask;

            int j;
            genericQueue.GENERIC_Q_CFG tmpqCfg;

            for (j = 0; j < pMisCnt.channels.length; j++) {
                qmCommFunc_mis.MIS_CHANNEL pChannel = pMisCnt.channels[j];
                if (pChannel == null) continue;

                pChannel.pMisCnt = pMisCnt;
                tmpqCfg = pMisCnt.cfg.inCacheQ;
                tmpqCfg.name = tmpqCfg.name + "-" + j;
                tmpqCfg.mutexName_prefix = tmpqCfg.mutexName_prefix + "-" + j;
                /*
                pChannel.inCacheQ = new genericQueue.GENERIC_Q();
                if (initGenericQ(tmpqCfg, null, null, null, pChannel.inCacheQ) != 0) {
                    exitVar_isGui.call(pQyMc, pSci, p2);
                    return iErr;
                }

                 */

                tmpqCfg = pMisCnt.cfg.toSendQ;
                tmpqCfg.name = tmpqCfg.name + "-" + j;
                tmpqCfg.mutexName_prefix = tmpqCfg.mutexName_prefix + "-" + j;
                pChannel.toSendQ2 = new qyq2.QY_Q2();
                if (initQyQ2(tmpqCfg, pProcInfo.cfg.b_use2Buf_toSendQ,pQyMc.cfg.rwLockParam, 1, null, null, null, null, pChannel.toSendQ2) != 0) {
                    exitVar_isGui.call(pQyMc, pSci, p2);
                    return iErr;
                }

                tmpqCfg = pMisCnt.cfg.outputQ;
                tmpqCfg.name = tmpqCfg.name + "-" + j;
                tmpqCfg.mutexName_prefix = tmpqCfg.mutexName_prefix + "-" + j;
                if  (  pChannel.m_b_useQ2_toOutput) {
                    pChannel.outputQ21 = new qyq2.QY_Q2();
                    if (initQyQ2(tmpqCfg, false,  pQyMc.cfg.rwLockParam, 1, null, null, null, null, pChannel.outputQ21) != 0) {
                        exitVar_isGui.call(pQyMc, pSci, p2);
                        return iErr;
                    }
                }
                else  {
                      pChannel.outputQBuf2=new QY_qBuf2();
                      //
                      //pChannel.outputQBuf2.m_bDbg=true;
                      //
                    //  int maxDataBufLen  =  2000000  *  2;  //  2秒，按2M流量
                   //   pChannel.outputQBuf2.initQyQ2(CONST_qType_snd,tmpqCfg.uiMaxQNodes,maxDataBufLen,pQyMc.cfg.rwLockParam,true,"channel,outputQBuf2"  );
                      pChannel.outputQBuf2.initQyQ2(CONST_qType_snd,tmpqCfg.uiMaxQNodes,pChannel.getBufferLength(tmpqCfg.uiMaxQNodes),false,pQyMc.cfg.rwLockParam,true,"channel.outputQBuf2"  );

                      //
                      //Log.d("qy","here.1314");

                      //
                }
            }

            pMisCnt.talkingFriendQ = new ArrayList<>();

            pMisCnt.recentFriendQ = new genericQueue.GENERIC_Q();
            if (initGenericQ(pMisCnt.cfg.recentFriendQ, null, null, null, pMisCnt.recentFriendQ) != 0) {
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pMisCnt.imObj_intQ = new genericQueue.GENERIC_Q();
            if (initGenericQ(pMisCnt.cfg.imObj_intQ, null, null, null, pMisCnt.imObj_intQ) != 0) {
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pMisCnt.hMutex_syncTask = new Semaphore(1);
            pMisCnt.hMutex_syncStartAv = new Semaphore(1);
            pMisCnt.hMutex_syncSendAv = new Semaphore(1);
            pMisCnt.hMutex_syncActiveMem = new Semaphore(1);
            pMisCnt.hMutex_syncIndex_dec = new Semaphore(1);
            pMisCnt.hMutex_syncShareEglContext = new Semaphore(1);
            pProcInfo.hMutex_syncQdcUnit = new Semaphore(1);

            pProcInfo.hEvt_notify_thread_isCli = new EventWaitHandle(false, EventResetMode.AutoReset, "notify_thread_isCli");
            pProcInfo.av.localAv.player = new qmcCommFunc_is_open.QY_PLAYER();
            genericQueue.GENERIC_Q_cfgEx qCfgEx = new genericQueue.GENERIC_Q_cfgEx();

            pProcInfo.av.localAv.player.audio.q2 = new qyq2.QY_Q2();
            if (0 != initQyQ2(pProcInfo.cfg.playSoundQ, pProcInfo.cfg.b_use2Buf_playSoundQ_localAv,  pQyMc.cfg.rwLockParam, 2, null, null, null, (genericQueue.PF_qElemRemove) qyTmpMemory.qyFuncs.clean_myPLAY_AUDIO_DATA, pProcInfo.av.localAv.player.audio.q2)) {
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pProcInfo.av.localAv.player.audio.bQInited = true;
            pProcInfo.av.localAv.player.audio.qBuf2 = new QY_qBuf2();

            int maxDataBufLen_playA = 48000 * 2 * 1;  // 1.5 seconds
            String name = pProcInfo.cfg.playSoundQ.name;

            if (0 != pProcInfo.av.localAv.player.audio.qBuf2.initQyQ2(CONST_qType_playA, pProcInfo.cfg.playSoundQ.uiMaxQNodes, maxDataBufLen_playA, false,pQyMc.cfg.rwLockParam, false, name)) {
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            qCfgEx.common = pProcInfo.cfg.playVideoQ;
            qCfgEx.pf_QElemRemoveEx = cleanEx_myDRAW_VIDEO_DATA;
            qCfgEx.pfQElemRemove = clean_myDRAW_VIDEO_DATA;
            pProcInfo.av.localAv.player.video.q2 = new qyq2.QY_Q2();
            if (initQyQ2(qCfgEx, pProcInfo.cfg.b_use2Buf_playVideoQ,  pQyMc.cfg.rwLockParam, 2, null, pProcInfo.av.localAv.player.video.q2) != 0) {
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }
            pProcInfo.av.localAv.player.video.bQInited = true;

            qCfgEx.common = pProcInfo.cfg.displayVideoQ;
            qCfgEx.pf_QElemRemoveEx = qyTmpMemory.qyFuncs.cleanEx_MIS_MSG_procVideo_qmc;
            qCfgEx.pfQElemRemove = clean_MIS_MSG_procVideo_qmc;
            pProcInfo.av.localAv.player.video.displayQ2 = new qyq2.QY_Q2();
            if (initQyQ2(qCfgEx, pProcInfo.cfg.b_use2Buf_displayQ,  pQyMc.cfg.rwLockParam, 2, null, pProcInfo.av.localAv.player.video.displayQ2) != 0) {
                exitVar_isGui.call(pQyMc, pSci, p2);
                return iErr;
            }

            pProcInfo.av.localAv.player.video.ucbDisplayQInited = true;
            pProcInfo.av.usCnt_players = pProcInfo.cfg.policy.usMax_nPlayers;
            pProcInfo.av.pPlayers = new qmcCommFunc_is_open.QY_PLAYER[pProcInfo.av.usCnt_players];

            for (i = 0; i < pProcInfo.av.usCnt_players; i++) {

                pProcInfo.av.pPlayers[i] = new qmcCommFunc_is_open.QY_PLAYER();
                genericQueue.GENERIC_Q_CFG tmpCfg = pProcInfo.cfg.playSoundQ;
                tmpCfg.name = tmpCfg.name + i;
                tmpCfg.mutexName_prefix = tmpCfg.mutexName_prefix + i;

                pProcInfo.av.pPlayers[i].audio.q2 = new qyq2.QY_Q2();
                if (initQyQ2(tmpCfg, pProcInfo.cfg.b_use2Buf_playSoundQ_,pQyMc.cfg.rwLockParam, 2, null, null, null, qyTmpMemory.qyFuncs.clean_myPLAY_AUDIO_DATA, pProcInfo.av.pPlayers[i].audio.q2) != 0) {
                    exitVar_isGui.call(pQyMc, pSci, p2);
                    return iErr;
                }

                pProcInfo.av.pPlayers[i].audio.bQInited = true;
                pProcInfo.av.pPlayers[i].audio.qBuf2 = new QY_qBuf2();

                name = tmpCfg.name;
                if (pProcInfo.av.pPlayers[i].audio.qBuf2.initQyQ2(CONST_qType_playA, tmpCfg.uiMaxQNodes, maxDataBufLen_playA, false,  pQyMc.cfg.rwLockParam, false, name) != 0) {
                    exitVar_isGui.call(pQyMc, pSci, p2);
                    return iErr;
                }

                tmpCfg = pProcInfo.cfg.playVideoQ;
                tmpCfg.name = tmpCfg.name + i;
                tmpCfg.mutexName_prefix = tmpCfg.mutexName_prefix + i;

                qCfgEx.common = tmpCfg;
                qCfgEx.pf_QElemRemoveEx = cleanEx_myDRAW_VIDEO_DATA;
                qCfgEx.pfQElemRemove = clean_myDRAW_VIDEO_DATA;
                pProcInfo.av.pPlayers[i].video.q2 = new qyq2.QY_Q2();
                if (initQyQ2(qCfgEx, pProcInfo.cfg.b_use2Buf_playVideoQ,  pQyMc.cfg.rwLockParam, 2, null, pProcInfo.av.pPlayers[i].video.q2) != 0) {
                    exitVar_isGui.call(pQyMc, pSci, p2);
                    return iErr;
                }
                pProcInfo.av.pPlayers[i].video.bQInited = true;

                tmpCfg = pProcInfo.cfg.displayVideoQ;
                tmpCfg.name = tmpCfg.name + i;
                tmpCfg.mutexName_prefix = tmpCfg.mutexName_prefix + i;

                qCfgEx.common = tmpCfg;
                qCfgEx.pf_QElemRemoveEx = qyTmpMemory.qyFuncs.cleanEx_MIS_MSG_procVideo_qmc;
                qCfgEx.pfQElemRemove = clean_MIS_MSG_procVideo_qmc;
                pProcInfo.av.pPlayers[i].video.displayQ2 = new qyq2.QY_Q2();
                if (initQyQ2(qCfgEx, pProcInfo.cfg.b_use2Buf_displayQ,pQyMc.cfg.rwLockParam, 2, null, pProcInfo.av.pPlayers[i].video.displayQ2) != 0) {
                    exitVar_isGui.call(pQyMc, pSci, p2);
                    return iErr;
                }
                pProcInfo.av.pPlayers[i].video.ucbDisplayQInited = true;
            }

            pProcInfo.av.localAv.recordSoundProcInfo = new qmcSharedObj.CAP_procInfo_recordSound();
            pProcInfo.av.localAv.videoCaptureProcInfo = new qmcSharedObj.CAP_procInfo_video();

            //  2008/10/04.
            pProcInfo.cfg.usMaxCnt_pSharedObjs = DEFAULT_usMaxCnt_pSharedObjs;

            pProcInfo.pSharedObjSyncs = new qmcSharedObj.QY_sharedObj_sync[pProcInfo.cfg.usMaxCnt_pSharedObjs];
            for (i = 0; i < pProcInfo.pSharedObjSyncs.length; i++) {
                pProcInfo.pSharedObjSyncs[i] = new qmcSharedObj.QY_sharedObj_sync();
            }

            pProcInfo.cfg.usMaxCnt_capProcInfoBmps = pProcInfo.cfg.usMaxCnt_pSharedObjs;
            pProcInfo.pCapProcInfoBmps = new qmcSharedObj.CAP_procInfo_video[pProcInfo.cfg.usMaxCnt_capProcInfoBmps];
            for (i = 0; i < pProcInfo.pCapProcInfoBmps.length; i++) {
                pProcInfo.pCapProcInfoBmps[i] = new qmcSharedObj.CAP_procInfo_video();
            }

            pProcInfo.cfg.usMaxCnt_capProcInfoAudios = pProcInfo.cfg.usMaxCnt_pSharedObjs;
            pProcInfo.pCapProcInfoAudios = new qmcSharedObj.CAP_procInfo_recordSound[pProcInfo.cfg.usMaxCnt_capProcInfoAudios];
            for (i = 0; i < pProcInfo.pCapProcInfoAudios.length; i++) {
                pProcInfo.pCapProcInfoAudios[i] = new qmcSharedObj.CAP_procInfo_recordSound();
            }

            //
            {
                genericQueue.GENERIC_Q_CFG qCfg;
                int  maxDataBufLen;

                qCfg = pProcInfo.cfg.outputQ_toMix_audio;

                pProcInfo.pOutputQBuf2_local_a  =  new QY_qBuf2();
                maxDataBufLen  =  128000  *  3  /  2;  //  1.5秒的音频数据
                if  (  0!=pProcInfo.pOutputQBuf2_local_a.initQyQ2(CONST_qType_encA,qCfg.uiMaxQNodes,maxDataBufLen,false,  pQyMc.cfg.rwLockParam,true,"encA")  ) {
                    Log.e("qy","initVar: pOutputQBuf2_local_a init failed");
                }

                //
                qCfg = pProcInfo.cfg.outputQ_toMosaic_video;

                pProcInfo.pOutputQBuf2_local_v=new QY_qBuf2();
                maxDataBufLen  =  2000000  *  1  /  8;
                //
                //
                if  (  0!=pProcInfo.pOutputQBuf2_local_v.initQyQ2(CONST_qType_encV,qCfg.uiMaxQNodes,maxDataBufLen,false,  pQyMc.cfg.rwLockParam,true,"encV")  )
                {
                    Log.e("qy","initVar: pOutputQBuf2_local_v init failed");
                }

            }



            //
            pProcInfo.cfg.usMaxCnt_taskInfos = DEFAULT_usMaxCnt_taskInfos;
            pProcInfo.pQmcTaskInfos = new qmcTaskInfo.QMC_TASK_INFO[pProcInfo.cfg.usMaxCnt_taskInfos];
            for (i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
                pProcInfo.pQmcTaskInfos[i] = new qmcTaskInfo.QMC_TASK_INFO();
            }

            pProcInfo.av.texes = new TMP_texes();
            pProcInfo.cfg.usTimeoutInMs_player_a = DEFAULT_usTimeoutInMs_player_a;
            pProcInfo.cfg.usTimeoutInMs_player_v = Consts.DEFAULT_usTimeoutInMs_player_v;

            pProcInfo.cfg.loopCtrl_timer_dlgTalk_chkRecvdTasks = Consts.DEFAULT_loopCtrl_timer_dlgTalk_chkRecvdTasks;
            pProcInfo.cfg.loopCtrl_chkPlayers = DEFAULT_loopCtrl_chkPlayers;

            pProcInfo.cfg.notify_avCall_bNoSound = true;
            pProcInfo.cfg.usTimeoutInMs_waitForProcessingOf_avTask = 15000;
            pProcInfo.cfg.usTimeoutInMs_waitForProcessingOf_otherTask = 60000;
            pProcInfo.cfg.usTimeoutInMs_waitIfNoData = 10000;

            guiShare.GuiShare.pf_customSystem.call();



            //
            iErr = 0;
            return iErr;
        };


        public static ctxQyMc.PF_init_afterLogon init_afterLogon_isGui = (ctxQyMc.CCtxQyMc pQyMc, qyMcMainObj.QY_SERVICEGUI_INFO pSci, IntBuffer p2) -> {
            int iErr = -1;

            iErr = 0;
            return iErr;
        };


        //
        public static int closeOtherPlayers(ctxQmc.CCtxQmc pProcInfo) {
            int iErr = -1;

            if (null == pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            int i;
            showInfo_open.qyFuncs.showInfo_open(0, "", ("closeOtherPlayers enters"));
            if (pProcInfo.av.pPlayers != null) {

                for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                    qmcCommFunc_is_open.QY_PLAYER pPlayer = pProcInfo.av.pPlayers[i];
                    qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio = pProcInfo.av.pPlayers[i].audio;
                    qmcCommFunc_is_open.PLAY_VIDEO_procInfo pPlayVideo = pProcInfo.av.pPlayers[i].video;
                    if (0 != pPlayAudio.uiPlayType) {
                        pPlayAudio.bQuit = true;

                    }
                    if (0 != pPlayVideo.uiPlayType) {
                        pPlayVideo.bQuit = true;
                        //if (pPlayVideo->q2.hEvents[0]) SetEvent(pPlayVideo->q2.hEvents[0]);
                    }
                }
                for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                    qmcCommFunc_is_open.QY_PLAYER pPlayer = pProcInfo.av.pPlayers[i];
                    qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio = pProcInfo.av.pPlayers[i].audio;
                    qmcCommFunc_is_open.PLAY_VIDEO_procInfo pPlayVideo = pProcInfo.av.pPlayers[i].video;

                    //
                    if (0 != pPlayAudio.uiPlayType) {
                        if (pPlayAudio.uiPlayType == CONST_aPlayType_android) {
                            guiShare.GuiShare.pf_stopPlayAudio_android.call(pPlayer);
                        }
                        pPlayAudio.uiPlayType = 0;
                    }

                    //
                    if (0 != pPlayVideo.uiPlayType) {
                        if (pPlayVideo.uiPlayType == CONST_vPlayType_v) {
                            myWaitForQThread_infinite(pPlayVideo, "closeOtherPlayers");
                        }
                        pPlayVideo.uiPlayType = 0;
                    }
                }
            }
            showInfo_open.qyFuncs.showInfo_open(0, "", ("closeOtherPlayers leaves"));

            return 0;
        }
    }
}


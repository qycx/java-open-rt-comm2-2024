


package com.qycx.base;

import static com.qycx.base.Consts.CONST_qyAppAvLevel_fullHd;
import static com.qycx.base.Consts.CONST_qyAppType_client;
import static com.qycx.base.Consts.CONST_qySystemId_messenger;
import static com.qycx.base.commonSock.qyFuncs.setSockTimeout;
import static com.qycx.base.ctxQmc.qyFuncs.exitVar_isGui;
import static com.qycx.base.ctxQmc.qyFuncs.initVar_isGui;
import static com.qycx.base.ctxQmc.qyFuncs.init_afterLogon_isGui;
import static com.qycx.base.qmCommonFunc_mis.Consts.CONST_maxQNodes_guiProcessQ;
import static com.qycx.base.qmCommonFunc_mis.Consts.CONST_mutexName_guiProcessQ;
import static com.qycx.base.qmCommonFunc_mis.Consts.DEFAULT_iTimeoutInMs_channelIdle_others;
import static com.qycx.base.qmCommonFunc_mis.Consts.DEFAULT_iTimeoutInMs_channelIdle_recv_talk;
import static com.qycx.base.qnmCustom.Consts.CONST_qyCustomId_general;
import static com.qycx.base.qyComm.Consts.CONST_qyServiceId_is;
import static com.qycx.base.qyMcLogon.qyFuncs.qyMcLogoff;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.mcThreadProc_isCli;
import static com.qycx.base.qyMcMainObj.Consts.QY_MC_TOTAL_DAEMON_THREADS;
import static com.qycx.base.qyMcMainProc.qyFuncs.bChkQyMcEnv;
import static com.qycx.base.qyShowInfoCommProc.qyFuncs.exitShowInfo;
import static com.qycx.base.qyq2.Consts.CONST_uiInitCnt_sema_q2SyncFlg;

import android.content.Context;
import android.content.SharedPreferences;

import com.qycx.MyApplication;
import com.qycx.base.util.Log;

import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class ctxQyMc {
    public static class Consts {
        public final static int CONST_aPlayType_null = 0;
        public final static int CONST_aPlayType_wave = 1;
        public final static int CONST_aPlayType_dSound = 2;
        public final static int CONST_aPlayType_webRtc = 3;
        //
        public final static int CONST_aPlayType_android = 10;
        //
        public final static int CONST_vPlayType_v = 100;
        //
        public final static int CONST_vPlayType_android = 110;

    }

    public interface PF_newVar {
        int call(ctxQyMc.CCtxQyMc pQyMc, qyMcMainObj.QY_SERVICEGUI_INFO pSci, IntBuffer p2);
    }

    public interface PF_freeVar {
        int call(ctxQyMc.CCtxQyMc pQyMc, qyMcMainObj.QY_SERVICEGUI_INFO pSci, IntBuffer p2);
    }

    public interface PF_initVar {
        int call(ctxQyMc.CCtxQyMc pQyMc, qyMcMainObj.QY_SERVICEGUI_INFO pSci, IntBuffer p2);
    }

    public interface PF_init_afterLogon {
        int call(ctxQyMc.CCtxQyMc pQyMc, qyMcMainObj.QY_SERVICEGUI_INFO pSci, IntBuffer p2);
    }

    public interface PF_exitVar {
        int call(ctxQyMc.CCtxQyMc pQyMc, qyMcMainObj.QY_SERVICEGUI_INFO pSci, IntBuffer p2);
    }

    public static class PARAM_initQyMc {
        //  2015/07/09. pInfo->cfg.pfNewVar(  pQM,  pInfo,  0  )  
        public static PF_newVar pfNewVar;                                                  //  2015/01/20
        public static PF_freeVar pfFreeVar;
        public boolean bCalledByQmd;                                              //  2015/07/17
    }

    ;


    public static class QY_MC_CFG {
        int i;

        public boolean bGetPolicyFromIni;                         //  2010/07/27. especially for winCe

        //
        //QNM_MODULES_LOADEDINFO modulesLoadedInfo;                           //  2004/08/16
        //
        public boolean ucbThinkPad;                              //  
        public boolean ucbBhqj;                                  //  2005/04/20
        public boolean ucbHaveDj;                                    //  2005/08/27
        public boolean ucbAdvancedOptionEnabled;                 //  2005/11/18
        public boolean ucbAutoLogonSupported;                        //  2006/03/08£¬__SUPPORT_AUTOLOGON__
        public boolean ucbRemoteDesktopSupported;                    //  2006/03/08£¬__NOTSUPPORT_REMOTEDESKTOP__
        public boolean ucbPcMonSupported;                            //  2006/03/08£¬__NOTSUPPORT_PCMON__
        public boolean ucbFileMonSupported;                      //  2006/03/25
        public boolean ucbRasControlNotSupported;                    //  2009/03/05
        //


        //  2011/03/26
        public boolean ucbNotSupported_dblClk;                       //  在触摸屏时，最好不支持双击。

        //
        public boolean ucbNotNeedLocked;                         //  
        public boolean ucbUseTrayIcon;                               //  
        public int iIDR_TRAY;
        //
        //
        public boolean ucbDsnless;                                   //  DNSLESS2007/06/13
        public String defaultDbName;//[MAX_PATH +1];              //  ACCESS DSN

        //char name[QIM_MAXNAMELEN + 1];
        public int mutexTimeout;

        public qnmCommProc1.QNM_DB_CFG db = new qnmCommProc1.QNM_DB_CFG();                                          //


        public commonSock.SOCK_TIMEOUT to = new commonSock.SOCK_TIMEOUT();   //sock超时的结构
        public commonSock.SOCK_PARAM sockParam;//=new commonSock.SOCK_PARAM();
        public int iTimeoutInMs_channelIdle_others;            //  2009/02/25
        //  int										iTimeoutInMs_channelIdle_recv_others;		//  2011/01/31
        public int iTimeoutInMs_channelIdle_recv_talk;         //  2011/01/31

        public int usDelayInMsToViewToolbar;                    //  2010/09/02

        //
        public qySyncCommProc.RW_lock_param rwLockParam = new qySyncCommProc.RW_lock_param();                              //  2009/05/13

        //  
        public boolean ucbStartRegScan;

        //
        public int iType_qnmTmpTab;

        //WCHAR cfgFullFileName[CONST_qyMaxPathLen + 1];  //  added on 2003/09/19. 不提供给用户编辑 
        //WCHAR iniFullFileName[CONST_qyMaxPathLen + 1];  //  2009/07/29。提供给用户编辑
        //WCHAR qyMcTitle[256 + 1];

        public boolean ucbAdvancedVer;                               //  2005/08/30

        //QNM_CUSTOM_COMM custom;

        public int usMaxDisplayedElemsPerWnd;                   //  2007/06/09
        public int usMaxDisplayedWnds;                          //
        //
        public int usMaxDisplayedTasksInDlgTalk;                //  2007/06/25

        //
        public qnmCommProc.QWM_DYN_CFG dynCfg = new qnmCommProc.QWM_DYN_CFG();                                     //  2007/06/16
        public String seqNo;//[64+1];
        public int iSystemId_wanted;                           //  ÎªÈ·±£ÏµÍ³Éú³ÉµÄÕýÈ·ÐÔ£¬¼ÓÈëÁË¶ÔiSystemIdµÄºË¶Ô

        //  2009/03/05
        //QY_MC_logonCfg logon;

    }

    ;

    //
    public static class CCtxQyMc_globals {
        public int lTranNo_wallMem;                                          //  2016/01/05
        //
        public int lTranNo_player;                                                //  2016/02/18
        //
    }

    ;

    //

    //
    public static class CCtxQyMc extends ctxQyMcTmpl.CCtxQyMcTmpl {

        //
        public int iSystemType_reserved;
        public int iSystemId;
        public int iAppType;
        public int iAppId;
        public int iCustomId;
        public int iServiceId;
        public byte[] ver;

        //
        public ConcurrentHashMap<Long, qyShowInfoCommProc.Ctx_thread_tmpVar> iThreadCtxtMap = new ConcurrentHashMap();
        public Semaphore hMutex_syncCtxThreadTmpVar = new Semaphore(1);



        //
        public qnmCommProc.QY_ENV env = new qnmCommProc.QY_ENV();                         //  2005/11/03

        //
        public int uiAPlayType;

        public qmcCmdParams.QMC_APP_PARAMS appParams = new qmcCmdParams.QMC_APP_PARAMS();

        public QY_MC_CFG cfg = new QY_MC_CFG();


        public qyLangCommProc.QNM_CUSRES_INFO cusRes = new qyLangCommProc.QNM_CUSRES_INFO();                         //  2005/11/03
        boolean ucbResInited;


        //
        public qyMcMainObj.QY_MC_GUI gui = new qyMcMainObj.QY_MC_GUI();

        //
        public qnmCommProc1.QY_OBJ_DB procedObjDb = new qnmCommProc1.QY_OBJ_DB();             //  2006/12/17
        public int iDsnIndex_mainSys;

        //
        public boolean bLogon;              //是否已登录
        public int dwTickCnt_logon;              //  2013/04/06
        //QYMC_USR curUsrInfo;

        //
        public volatile boolean bQuit;
        public boolean bGuiQuit;                      //  2008/10/03
        public int usTimes_tryToQuit;
        //
        public boolean bAskingToQuit;                 //  2011/03/18
        public boolean bQuitVerified;                 //  2011/03/16
        //
        public boolean bPrepareToGuiQuit;             //  2016/03/28

        //
        public boolean b_notifyPanic;
        public long dwLastTickCnt_notifyPanic;
        public int nTimes_notifyPanic;


        //
        public Object pShowInfoStruct;

        //
        public myDb.QM_dbFuncs p_g_dbFuncs = new myDb.QM_dbFuncs();                                                //  2016/09/04  数据库函数集

        //  2009/10/11
        public byte ucbDaemonDemo;              //服务器是否为demo版
        public int usMaxVideoConferenceMems;    //会议最大参会人限制
        public byte ucDays_left;                                              //  2010/03/22
        //
        public int usAppAvLevel_default;                                        //  2013/06/28
        public int usAppAvLevel_daemon;                                     //  2011/10/22
        //
        public int usSubSystemId_daemon;                                        //  2017/06/21

        //
        public boolean ucbSupported_remoteStorage_default;                           //  2014/08/03
        public boolean ucbSupported_fileServer_default;                          //  2015/08/04

        //
        public boolean ucbSupported_gps_default;                                 //  2013/06/28

        //
        //
        public int uiMaxTimeInS_videoConference;                              //  2011/08/01
        public int uiMaxWarningInS_videoConference;                           //  2013/08/20

        //
        public int uiMaxTimeInS_videoConference_general;                      //  2011/08/01
        public int uiMaxTimeInS_videoConference_freeVer;                      //  2011/08/01


        //  2015/07/09. pInfo->cfg.pfNewVar(  pQM,  pInfo,  0  )  
        public static PF_newVar pfNewVar;                                                  //  2015/01/20
        public static PF_freeVar pfFreeVar;
        public boolean bCalledByQmd;                                              //  2015/07/17

        //
        public CCtxQyMc_globals globals = new CCtxQyMc_globals();


        //
        public qyMcMainCusModule.CUS_MODULES cusModules = new qyMcMainCusModule.CUS_MODULES();


        public qyMcMainObj.QY_SERVICEGUI_INFO serviceGuiInfo = new qyMcMainObj.QY_SERVICEGUI_INFO();

        public Object[] hDaemonThreads; //[QY_MC_TOTAL_DAEMON_THREADS];
        public qmcFunc.PF_threadProc threadProcs[]; //[QY_MC_TOTAL_DAEMON_THREADS];

        //
        public boolean b_isHarmony;
        public int iManufacturer;
        public int iModel;
        //
        public boolean fake_bFolder;

        //
        public CCtxQyMc() {
        }


        public ctxQmc.CCtxQmc get_pProcInfo() {
            return (ctxQmc.CCtxQmc) this.serviceGuiInfo.pVar;
        }

        public int setQmDbFuncs(int iDbType, myDb.QM_dbFuncs pDbFuncs) {
            ctxQmc.CCtxQmc pProcInfo = get_pProcInfo();
            if (null == pProcInfo) return -1;

            return pProcInfo.setQmDbFuncs(iDbType, pDbFuncs);
        }

        //
        public int yv12ToRgb24(byte[] yuv, byte[] rgb, int width, int height, boolean bFlipImg) {
            //Log.d("not finised");
            return -1;
        }


    }

    public static class qyFuncs {


        public static int initQyMc(long g_hInst, PARAM_initQyMc pParam_initQyMc, CCtxQyMc pQyMc) {
            int iErr = -1;

            //
            String str;

            //
            CCtxQyMc g_pQyMc = pQyMc;

            //
            //  2015/07/08. 调用前，应先new
            if (g_pQyMc == null) {
                Log.d("initQyMc failed, g_pQyMc is not newed");
                return -1;
            }

            Log.d("initQyMc entersm");


            //
            //PARAM_initQyMc param_initQyMc;
            //memset(param_initQyMc, 0, sizeof(param_initQyMc));
            //if (pPARAM_initQyMc) memcpy(param_initQyMc, pPARAM_initQyMc, sizeof(param_initQyMc));


            //
            g_pQyMc.iSystemId = CONST_qySystemId_messenger;
            //  g_pQyMc->iAppType   =   CONST_qyAppType_mc;
            g_pQyMc.iAppType = CONST_qyAppType_client;      //  2004/05/23ÐÞ¸Ä
            g_pQyMc.iCustomId = CONST_qyCustomId_general;
            g_pQyMc.iServiceId = CONST_qyServiceId_is;                                       //  2007/03/07
            //
            //g_pQyMc.g_hInst = g_hInst;                                                                         // Store instance handle in our global variable
            //
            g_pQyMc.usAppAvLevel_default = CONST_qyAppAvLevel_fullHd;                                          //  2013/06/28
            //
            //  2015/07/17
            g_pQyMc.pfNewVar = pParam_initQyMc.pfNewVar;
            g_pQyMc.pfFreeVar = pParam_initQyMc.pfFreeVar;
            g_pQyMc.bCalledByQmd = pParam_initQyMc.bCalledByQmd;

            //

            String errHintWndCls = "";// CONST_qnmStatusToolWndClass_client;
            String statusTool_servIp;
            Context context = MyApplication.getContext();
            SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            if(sp!=null)
            {
                String statusServerInitVal  =  null;
                statusServerInitVal  =  "10.0.1.102";
                statusServerInitVal  =  "192.168.1.23";
                //statusServerInitVal="10.0.10.41";

                //
     //           String statusServer = sp.getString("statusServer", "10.0.10.84");
                String statusServer = sp.getString("statusServer", statusServerInitVal);
                if(statusServer != null && !statusServer.trim().isEmpty())
                {
                    qyShowInfoCommProc.qyFuncs.initShowInfo_cli(0, ("syncQ_showInfo_qmc"), statusServer, g_pQyMc.pShowInfoStruct);
                }
            }



            //  2013/07/31
            //  g_pQyMc->pShowInfoStruct  =  get_pShowInfoStruct(  );

            str ="initQyMc, after initShowInfo_cli";
            Log.d(str);
            showInfo_open.qyFuncs.showInfo_open(0, null,str);

            //
            CCtxQyMc pQM = g_pQyMc;
            int i;
            int dwThreadDaemonId;                     //ÓÃÓÚºóÌ¨Ë¢ÐÂ£¬ÔÝ²»Ê¹ÓÃ

            qnmCommProc.qyFuncs.getQyEnv(null, pQM.env);

            if (getQyMcInitialCfg(pQyMc) != 0) {
                exitQyMc(pQyMc);
                return iErr;
            }


            pQM.p_g_dbFuncs = new myDb.QM_dbFuncs();// &global_dbFuncs;
            //memset(pQM->p_g_dbFuncs, 0, sizeof(global_dbFuncs));


            //pQM->env.iEnvType = QY_ENVTYPE_MCGUI;
            if (!bChkQyMcEnv(pQM)) {
                showInfo_open.qyFuncs.showInfo_open(0, "", "bChkQyMcEnv failed");
                exitQyMc(pQyMc);
                return iErr;
            }


            //  2008/01/25

            //
            //pQM->gui.ctx_gui_thread.dwThreadId = GetCurrentThreadId();

            if (qnmDb.CQnmDb.initDbPool() != 0) {
                exitQyMc(pQyMc);
                return iErr;
            }


            pQM.gui.processQ = new genericQueue.GENERIC_Q();
            if (genericQueue.qyFuncs.initGenericQ("guiProcQ", CONST_mutexName_guiProcessQ, 0, CONST_maxQNodes_guiProcessQ, pQM.gui.processQ) != 0) {
                exitQyMc(pQyMc);
                return iErr;
            }
            pQM.gui.ucbProcessQInited = true;


            pQM.cusModules = new qyMcMainCusModule.CUS_MODULES();


            //  2016/09/19
            pQM.serviceGuiInfo.pParent = pQM.get_pProcInfo();

            //  2015/01/20
            if (pQM.serviceGuiInfo.cfg.pfNewVar == null) return iErr;

            if (pQM.serviceGuiInfo.cfg.pfNewVar.call(pQM, pQM.serviceGuiInfo, null) != 0) {
                exitQyMc(pQyMc);
                return iErr;
            }

            if (pQM.serviceGuiInfo.pVar == null) {
                exitQyMc(pQyMc);
                return iErr;
            }
            //	 			
            if (pQM.serviceGuiInfo.cfg.pfInitVar != null) {
                if (pQM.serviceGuiInfo.cfg.pfInitVar.call(pQM, pQM.serviceGuiInfo, null) != 0) {
                    exitQyMc(pQyMc);
                    return iErr;
                }
            }
            pQM.serviceGuiInfo.ucbVarInited = true;


            iErr = 0;

            return iErr;
        }

        //
        public static int startToExitQyMc(ctxQyMc.CCtxQyMc pQyMc) {
            if (pQyMc == null) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            String str;
            str = "startToExitQyMc: pQyMc.bQuit set true";
            showInfo_open.qyFuncs.showInfo_open(str);
            Log.d("qy",str);

            //
            pQyMc.bQuit = true;

            //
            tmpFunc.qyFuncs.mySetEvent(pProcInfo.hEvt_notify_thread_isCli);
            return 0;
        }


        public static void exitQyMc(ctxQyMc.CCtxQyMc pQM) {
            if (pQM == null) {
                Log.d("exitQyMc enters, pQM is null");

                return;
            }

            //
            //Log.e("exitQyMc enters");
            Log.d("exitQyMc enters");
            showInfo_open.qyFuncs.showInfo_open(0, null, "exitQyMc enters");

            pQM.bQuit = true;

            if (pQM.bLogon) qyMcLogoff();

            if (pQM.serviceGuiInfo.ucbVarInited) {
                if (pQM.serviceGuiInfo.cfg.pfExitVar != null) {
                    pQM.serviceGuiInfo.cfg.pfExitVar.call(pQM, pQM.serviceGuiInfo, null);
                }
                pQM.serviceGuiInfo.ucbVarInited = false;
            }
            if (pQM.serviceGuiInfo.pVar != null) {
                if (pQM.serviceGuiInfo.cfg.pfFreeVar != null) {  //  2015/01/20
                    pQM.serviceGuiInfo.cfg.pfFreeVar.call(pQM, pQM.serviceGuiInfo, null);
                }

                //
            }


            if (pQM.gui.ucbProcessQInited)
                genericQueue.qyFuncs.exitGenericQ(pQM.gui.processQ);   //  2007/06/01
            pQM.gui.processQ = null;

            //
            qnmDb.CQnmDb.exitDbPool();

            //
            if (pQM.p_g_dbFuncs != null) pQM.p_g_dbFuncs = null;

            Log.d(("All exit."));
            showInfo_open.qyFuncs.showInfo_open(0, null, ("All exit."));
            //
            exitShowInfo(pQM.pShowInfoStruct);
        }


        //
        static int getQyMcInitialCfg(ctxQyMc.CCtxQyMc pQyMc) {
            int iErr = -1;
            //QY_MC_CFG pCfg =pQyMc.cfg;

            //if (initQnmEnc(pQyMc->encCtx, NULL, NULL,pQyMc->commEncCtx)) return iErr;
            //pQyMc->ucbEncInited = TRUE;

            //pQyMc.cfg.bGetPolicyFromIni = true;

            //

            if (qyMcMainProc.qyFuncs.getDynCfg_isClient(pQyMc, pQyMc.cfg.dynCfg.cnt) != 0)
                return iErr;

            commonSock.SOCK_TIMEOUT pTo = pQyMc.cfg.to;
            {
                setSockTimeout(pTo);
            }
            pQyMc.cfg.sockParam = new commonSock.SOCK_PARAM();
            pQyMc.cfg.sockParam.capacity_buf_r = 1024;//sizeof(qnmCommProc_mis.MIS_MSGU);
            pQyMc.cfg.sockParam.capacity_buf_w = 1024;//sizeof(qnmCommProc_mis.MIS_MSGU);
            pQyMc.cfg.sockParam.to = pQyMc.cfg.to;


            //
            pQyMc.cfg.iTimeoutInMs_channelIdle_others = DEFAULT_iTimeoutInMs_channelIdle_others;
            //  pQyMc->cfg.iTimeoutInMs_channelIdle_recv_others  =  DEFAULT_iTimeoutInMs_channelIdle_recv_others;
            pQyMc.cfg.iTimeoutInMs_channelIdle_recv_talk = DEFAULT_iTimeoutInMs_channelIdle_recv_talk;

            //
            pQyMc.cfg.rwLockParam.uiMaxCnt_sema = CONST_uiInitCnt_sema_q2SyncFlg;
            pQyMc.cfg.rwLockParam.uiInitCnt_sema = pQyMc.cfg.rwLockParam.uiMaxCnt_sema - 1;
            pQyMc.cfg.rwLockParam.uiMilliSeconds_mutex_r = 10000;
            pQyMc.cfg.rwLockParam.uiMilliSeconds_sema_r = 10000;
            pQyMc.cfg.rwLockParam.uiMilliSeconds_mutex_w = 10000;
            pQyMc.cfg.rwLockParam.uiMilliSeconds_sema_w = 10000;


            //
            int index;
            qyMcMainObj.QY_SERVICEGUI_CFG serviceClientCfg;

            //
            pQyMc.threadProcs = new qmcFunc.PF_threadProc[QY_MC_TOTAL_DAEMON_THREADS];//[];
            pQyMc.hDaemonThreads = new Object[QY_MC_TOTAL_DAEMON_THREADS];//[];

            //
            index = 0;
            pQyMc.threadProcs[index] = qyMc_func_ts_noMfc.qyFuncs.mcThreadProc;
            index++;

            {
                pQyMc.serviceGuiInfo.cfg.iServiceId = pQyMc.iServiceId;
                //  serviceClientCfg.iDbType  =  pQyMc->cfg.db.iDbType;

                //		 
                //serviceClientCfg.uiVarSize = getVarSize_is(pQyMc);
                pQyMc.serviceGuiInfo.cfg.pfInitVar = initVar_isGui;
                pQyMc.serviceGuiInfo.cfg.pfInit_afterLogon = init_afterLogon_isGui;
                pQyMc.serviceGuiInfo.cfg.pfExitVar = exitVar_isGui;

                //
                {
                    //  2015/01/20
                    if (pQyMc.pfNewVar == null) return iErr;

                    if (pQyMc.pfFreeVar == null) return iErr;
                    //
                    pQyMc.serviceGuiInfo.cfg.pfNewVar = pQyMc.pfNewVar;
                    pQyMc.serviceGuiInfo.cfg.pfFreeVar = pQyMc.pfFreeVar;
                }

                //
                //if  (  !isQmCmd(  pQyMc  )  )  
                {
                    pQyMc.threadProcs[index] = mcThreadProc_isCli;
                    index++;
                }
            }
            if (index > pQyMc.threadProcs.length) {
                //("threadProcs overflowed"));
                return iErr;
            }

            //

            iErr = 0;


            return iErr;
        }

    }


}

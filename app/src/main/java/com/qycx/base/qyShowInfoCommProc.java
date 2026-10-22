
package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.cntof_mutexName_prefix;
import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_showInfo;
import static com.qycx.base.commonSock.qyFuncs.setSockTimeout;
import static com.qycx.base.genericQueue.Consts.cntof_qCfg_name;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_showInfo_small;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_showInfo_small_java;
import static com.qycx.base.qnmCommProc.qyFuncs.qnmCloseSession;
import static com.qycx.base.qnmCommProc.qyFuncs.qnmOpenSession;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_decObj_a_mediaCodec_tmpDataBuf;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_decObj_mediaCodec_tmpDataBuf;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_encV_tmpDataBuf;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;
import static com.qycx.base.qyComm.Consts.CONST_qyServiceId_showInfo;
import static com.qycx.base.qyCommFunc.qyFuncs.qySendReq;
import static com.qycx.base.qyCommProc.Consts.CONST_uiMaxQNodes_showInfoQ_is;
import static com.qycx.base.qyCommProc.Consts.CONST_uiMaxQNodes_showInfoQ_state_is;
import static com.qycx.base.qyCommProc.Consts.MAX_pipeCli_idleInMs;
import static com.qycx.base.qyShowInfoCommProc.Consts.DEFAULT_qyPort_showInfo;
import static com.qycx.base.qyq2.Consts.CONST_uiInitCnt_sema_q2SyncFlg;
import static com.qycx.base.qyq2.Consts.cntof_q2Cfg_name;
import static com.qycx.base.qyq2.qyFuncs.exitQyQ2;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;
import static com.qycx.base.qyq2.qyFuncs.initQyQ2;
import static com.qycx.base.qyq2.qyFuncs.isQ2Empty;
import static com.qycx.base.qyq2.qyFuncs.q2GetMsg;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.mySleep;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;
import static com.qycx.base.tmpFunc.qyFuncs.mytcsicmp;

import com.qycx.base.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class qyShowInfoCommProc {

    public static class Consts {
        public final static int DEFAULT_qyPort_showInfo = 8765;
    }

    public interface PF_qyShowInfo0 { int call(long pShowStructParam, long pReserved);
    }



    //
    public static class Ctx_thread_showInfo {

        //
        public qyCommProc.QY_SHOW_SMALL showStruct    = new qyCommProc.QY_SHOW_SMALL();
        public qyCommProc.Var_QY_SHOW_SMALL tmp_Var_QY_SHOW_SMALL  =  new qyCommProc.Var_QY_SHOW_SMALL();

        //
        public qyCommonFunc9.TMP_buf256 buf256 = new qyCommonFunc9.TMP_buf256();

        public QY_qBuf2.HeadData_showInfo tmp_headData=new QY_qBuf2.HeadData_showInfo();
        public ByteBuffer bb_tmp_headData  =  ByteBuffer.allocate(QY_qBuf2.HeadData_showInfo.s_length );

        //
        public ByteBuffer mySYSTEMTIME_toByteArray_buffer = ByteBuffer.allocate(Short.BYTES * 8);


        public qyShowInfo.PARAM_showInfo tmp_param  =  new qyShowInfo.PARAM_showInfo();

        //
        public qyMcMainDaemon_isCli.qyFuncs.TMP_buf4096 tmpBuf_buf4096 = new qyMcMainDaemon_isCli.qyFuncs.TMP_buf4096();// todo qy

        //
        public byte [] tmp_byte192=new byte[192];

    };

    //
    public static class Ctx_thread_main {

        //
        public qyCommProc.mySYSTEMTIME  tmp_showState_sys_when  = new qyCommProc.mySYSTEMTIME();


    };


    public static class Ctx_thread_encA{
        //public qyCommProc.mySYSTEMTIME  tmp_showState_sys_when  = new qyCommProc.mySYSTEMTIME();
        public qnmCommProc_mis.TRANSFER_AUDIO_DATA toShareAudio_tad_req  =  new qnmCommProc_mis.TRANSFER_AUDIO_DATA();
        public ByteBuffer toShareAudio_tad_req_buf = ByteBuffer.allocate(bufSize_transferAudioData); //todo move_copy
        //public qnmCommProc_mis.MIS_MSG_TASK toShareAudio_pMsgTask = new qnmCommProc_mis.MIS_MSG_TASK();

    };


    public static class Ctx_thread_decObj_a  {

        public QY_qBuf2.HeadData_playA tmp_headDataPlayA = new QY_qBuf2.HeadData_playA();

    };


    public static class Ctx_thread_deca{
        //public qyCommProc.mySYSTEMTIME  tmp_showState_sys_when  = new qyCommProc.mySYSTEMTIME();
        //public ByteBuffer tmp_content_buffer = ByteBuffer.allocate(909600);
        byte[] data = new byte[16000];
    };

    //
    public static class Ctx_thread_playa{
        //public qyCommProc.mySYSTEMTIME  tmp_showState_sys_when  = new qyCommProc.mySYSTEMTIME();
        byte[] data = new byte[4000];
    };

    public static class Ctx_thread_toShareVideo_post  {

        qyMcMainProc.PARAM_findOrgMsg tmp_param_findOrgMsg =new qyMcMainProc.PARAM_findOrgMsg();

    };


    public static class Ctx_thread_decv{
        //public qyCommProc.mySYSTEMTIME  tmp_showState_sys_when  = new qyCommProc.mySYSTEMTIME();
        byte[] data = new byte[900000];
    };


    public static class Ctx_thread_toSend_a {
        //
        public qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead tmp_audio_head  =  new qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead();
        public byte[] tmp_audio_buf  =  new byte[bufSize_decObj_a_mediaCodec_tmpDataBuf];

    }


    public static class Ctx_thread_toSend_v {
        //
        public qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead tmp_video_head  =  new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();
        public byte[] tmp_video_buf  =  new byte[bufSize_encV_tmpDataBuf];

    }



    public static class Ctx_thread_toSend{

        //
        public qyCommProc.mySYSTEMTIME  tmp_showState_sys_when  = new qyCommProc.mySYSTEMTIME();
        public ByteBuffer tmp_content_buffer = ByteBuffer.allocate(909600);

        //
        public Ctx_thread_toSend_a  a;
        public Ctx_thread_toSend_v  v;








    };





    //
    public static class Ctx_thread_tmpVar  {
        long  m_threadId;
        long  m_timestamp;

        //
        public Ctx_thread_showInfo  m_ctxThreadShowInfo = new Ctx_thread_showInfo();

        //
        public Ctx_thread_main m_ctxThreadMain;

        //
        public Ctx_thread_encA m_ctxThreadEncA;
        public Ctx_thread_decObj_a  m_ctxThreadDecObjA;
        public Ctx_thread_deca m_ctxThreadDecA;
        public Ctx_thread_playa m_ctxThreadPlayA;

        //
        public Ctx_thread_toShareVideo_post m_ctxThreadToShareVideoPost;
        public Ctx_thread_decv m_ctxThreadDecV;

        //
        public Ctx_thread_toSend   m_ctxThreadtoSend;



        //
        public Ctx_thread_tmpVar( ) {
            this.m_timestamp = myGetTickCount(null);



            //
            return;
        }




    };



    //
    public static class qyFuncs {




        //
        public static class ShowInfo_struct {
            public boolean bInited;
            //
            public String errHintWndCls;
            public static PF_qyShowInfo0 errHintPf;
            //
            public int dwThreadId;
            //public Thread hThread;
            public Object hThread;
            public boolean bQuit;
            //
            public boolean bShowMore;
            //
            public String mutexName_syncQ;
            //
            public boolean bUsePipe;          //  2012/02/03
            public boolean bUseSock;
            public String servIp;
            public int usPort;
            //
            public boolean bCli;
            //
            public qySyncCommProc.RW_lock_param rwLockParam = new qySyncCommProc.RW_lock_param();      //  2009/05/14

            //
            //public qyq2.QY_Q2 q2 = new qyq2.QY_Q2();
            public QY_qBuf2   q2 = new QY_qBuf2();
            public boolean bQInited;
            //
            //public qyq2.QY_Q2 state_q2 = new qyq2.QY_Q2();         //  2015/08/23
            public QY_qBuf2   state_q2 = new  QY_qBuf2();
            public boolean bQInited_state;

            //
            public String cfg_serverIp;

            //
            public boolean status_bSessionOpen;
        }


        public static qyShowInfoCommProc.qyFuncs.ShowInfo_struct sShowInfoStruct = null;

        //
        public static qyShowInfoCommProc.qyFuncs.ShowInfo_struct g_pShowInfoStruct = null;//&sShowInfoStruct;//NULL;


        //


        public static int setErrHintPf(PF_qyShowInfo0 pf) {
            sShowInfoStruct.errHintPf = pf;

            return 0;
        }


        public static void exitShowInfo(Object ppCtx_showInfo) {
            qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj();

            //
            //Log.e(("exitShowInfo enters"));


            //sShowInfoStruct.

            //
            int i;
            if(sShowInfoStruct == null) return;
            //
            if (sShowInfoStruct.status_bSessionOpen) {
                for (i = 0; i < 10; i++) {
                    if (sShowInfoStruct.q2.isQ2Empty()) break;
                    mySleep(10);
                    continue;
                }
            }

            sShowInfoStruct.bQuit = true;
            int len = 0;
            sShowInfoStruct.q2.q2PostMsgAndTrigger(null,null, 0,len,"exitShowInfo.124");
            //MACRO_qyNullThread( sShowInfoStruct.hThread);
            guiShare.GuiShare.pf_waitForThread1.call(sShowInfoStruct.hThread, 10000);
            sShowInfoStruct.hThread = null;

            if (sShowInfoStruct.bQInited) {
                sShowInfoStruct.q2.exitQyQ2();
                sShowInfoStruct.bQInited = false;
            }
            //  2015/08/23
            if (sShowInfoStruct.bQInited_state) {
//                sShowInfoStruct.state_q2.exitQyQ2();
                sShowInfoStruct.state_q2.exitQyQ2();
                sShowInfoStruct.bQInited_state = false;
            }

            //
            sShowInfoStruct.bInited = false;
            //
            ppCtx_showInfo = null;

            //
            g_pShowInfoStruct = null;

            //
            //Log.e(("exitShowInfo leaves"));


        }


        //
        public static int initShowInfo_cli(long p0, String mutexName_syncQ, String servIp, Object ppCtx_showInfo) {
            return initShowInfo_func(p0, mutexName_syncQ, ("unused"), false, true, servIp, true, ppCtx_showInfo);
        }


        //
        public static int initShowInfo_func(long p0, String mutexName_syncQ, String errHintWndCls, boolean bUsePipe, boolean bUseSock, String servIp, boolean bCli, Object ppCtx_showInfo) {
            int iErr = -1;
            int dwThreadDaemonId;

            if (null != g_pShowInfoStruct) return -1;
            //
            //
            if (null == mutexName_syncQ || 0 == mutexName_syncQ.length()) return -1;
            if (null == errHintWndCls || 0 == errHintWndCls.length()) return -1;

            //
            //memset(sShowInfoStruct, 0, sizeof(sShowInfoStruct));
            sShowInfoStruct = new qyShowInfoCommProc.qyFuncs.ShowInfo_struct();
            g_pShowInfoStruct = sShowInfoStruct;

            //
            //lstrcpyn(sShowInfoStruct.mutexName_syncQ, mutexName_syncQ, mycountof(sShowInfoStruct.mutexName_syncQ));
            sShowInfoStruct.mutexName_syncQ = mutexName_syncQ;
            //if (0!=setErrHintWndCls(errHintWndCls)) return iErr;
            sShowInfoStruct.bUsePipe = bUsePipe;
            //
            if (!sShowInfoStruct.bUsePipe) {
                sShowInfoStruct.bUseSock = bUseSock;
            }
            if (sShowInfoStruct.bUseSock) {
                //
                sShowInfoStruct.cfg_serverIp = servIp;

                //safeStrnCpy(servIp, sShowInfoStruct.servIp, mycountof(sShowInfoStruct.servIp));
                sShowInfoStruct.servIp = (servIp);
                sShowInfoStruct.usPort = DEFAULT_qyPort_showInfo;
            }

            //
            sShowInfoStruct.bCli = bCli;

            //  memset(  &rwLockParam,  0,  sizeof(  rwLockParam  )  );
            sShowInfoStruct.rwLockParam.uiMaxCnt_sema = CONST_uiInitCnt_sema_q2SyncFlg;
            sShowInfoStruct.rwLockParam.uiInitCnt_sema = sShowInfoStruct.rwLockParam.uiMaxCnt_sema - 1;
            sShowInfoStruct.rwLockParam.uiMilliSeconds_mutex_r = 10000;
            sShowInfoStruct.rwLockParam.uiMilliSeconds_sema_r = 10000;
            sShowInfoStruct.rwLockParam.uiMilliSeconds_mutex_w = 10000;
            sShowInfoStruct.rwLockParam.uiMilliSeconds_sema_w = 10000;


            //
            genericQueue.GENERIC_Q_CFG qCfg = new genericQueue.GENERIC_Q_CFG();
            //mymemset( ( byte[])qCfg, 0, sizeof(genericQueue.GENERIC_Q_CFG));
            //_sntprintf(qCfg.name, mycountof(qCfg.name), ("%s"), ("showInfoQ"));
            qCfg.name = "showInfoQ";//, cntof_q2Cfg_name);
            //_sntprintf(qCfg.mutexName_prefix, mycountof(qCfg.mutexName_prefix), ("%s"), sShowInfoStruct.mutexName_syncQ);
            //  qCfg.uiMaxCnt_semaTrigger  =  CONST_maxCnt_sema_showInfoQ_is;
            qCfg.uiMaxQNodes = CONST_uiMaxQNodes_showInfoQ_is;
            //
            qCfg.b_showInfoQ = true;

            //
            sShowInfoStruct.q2 = new QY_qBuf2();// QY_Q2();

            //if (0!=initQyQ2(qCfg, sShowInfoStruct.rwLockParam, 1, null,  null, null,  null, sShowInfoStruct.q2)) return iErr;
            //int maxDataBufLen = (int) (qCfg.uiMaxQNodes);
            int maxDataBufLen = (int)(qCfg.uiMaxQNodes * qyCommProc.QY_SHOW_SMALL.s_Length);


            if (0 != sShowInfoStruct.q2.initQyQ2(CONST_qType_showInfo, (int) qCfg.uiMaxQNodes, maxDataBufLen, false,  sShowInfoStruct.rwLockParam, true, "showInfo.q2"))
               return iErr;
            sShowInfoStruct.bQInited = true;

            //  2015/08/23
            //mymemset( ( byte[])qCfg, 0, sizeof(genericQueue.GENERIC_Q_CFG));
            //_sntprintf(qCfg.name, mycountof(qCfg.name), ("%s"), ("showInfo_state_Q"));
            qCfg.name = "showInfo_state_Q";//, cntof_qCfg_name);
            qCfg.mutexName_prefix = sShowInfoStruct.mutexName_syncQ;//, cntof_mutexName_prefix);
            //  qCfg.uiMaxCnt_semaTrigger  =  CONST_maxCnt_sema_showInfoQ_is;
            qCfg.uiMaxQNodes = CONST_uiMaxQNodes_showInfoQ_state_is;
            //qCfg.uiMaxQNodes = CONST_uiMaxQNodes_showInfoQ_is;

            //
            //sShowInfoStruct.state_q2 = new qyq2.QY_Q2();// QY_Q2(); todo
            //if (0!=initQyQ2(qCfg, sShowInfoStruct.rwLo0ckParam, 1, null,  null, null,  null, sShowInfoStruct.state_q2)) return iErr;
            sShowInfoStruct.bQInited_state = true;
            maxDataBufLen = (int)(qCfg.uiMaxQNodes * qyCommProc.QY_SHOW_SMALL.s_Length);
            if (0 != sShowInfoStruct.state_q2.initQyQ2(CONST_qType_showInfo, (int) qCfg.uiMaxQNodes, maxDataBufLen, false,  sShowInfoStruct.rwLockParam, true, "showInfo.stateQ2")){
//            if (0 != initQyQ2(qCfg, sShowInfoStruct.rwLockParam, 1, null, null, null, null, sShowInfoStruct.state_q2)) {
                exitShowInfo(ppCtx_showInfo);
                return iErr;
            }

            //todo 关闭showinfo工具
            guiShare.GuiShare.pf_startThread1.call(showInfoThreadProc, 0);
            //
            sShowInfoStruct.bInited = true;

            //  2013/07/31
            //set_g_pShowInfoStruct(  &sShowInfoStruct  );
            //
            ppCtx_showInfo = sShowInfoStruct;

            //
            iErr = 0;


            if (0 != iErr) {

                exitShowInfo(ppCtx_showInfo);

            }

            return iErr;
        }


        public static qmcFunc.PF_threadProc showInfoThreadProc = (Object obj) -> {
            int dwRet;
            qyCommProc.QY_SHOW_SMALL showStruct = new qyCommProc.QY_SHOW_SMALL();
            //byte[] byteArray = headData.toByteArray();
            int len = 0;
            qnmCommProc_mis.MIS_MSGU misMsgu;
            //HWND firstWnd = NULL;
            boolean bUsePipe = sShowInfoStruct.bUsePipe;
            //HANDLE hPipe = NULL;
            //
            //LPTSTR				lpszPipename		=		sShowInfoStruct.bCli  ?  CONST_statusPipeName_cli  :  CONST_statusPipeName_serv;
            String lpszPipename = "";// sShowInfoStruct.bCli ? CONST_statusPipeName_cli_pipeCli : CONST_statusPipeName_serv_pipeCli;
            //
            boolean fSuccess = false;
            int cbRead = 0, cbWritten = 0, dwMode = 0;
            //
            long dwTickCnt_lastData = tmpFunc.qyFuncs.myGetTickCount(null);

            //
            commonSock.QY_SOCK sock = new commonSock.QY_SOCK();
            qyComm.QY_COMM_SESSION session = new qyComm.QY_COMM_SESSION();
            boolean bSessionOpen = false;
            commonSock.SOCK_PARAM sockParam;

            sockParam = new commonSock.SOCK_PARAM();
            sockParam.capacity_buf_r = 1024;
            sockParam.capacity_buf_w = 1024;
            if (sockParam.to == null) {
                sockParam.to = new commonSock.SOCK_TIMEOUT();
            }
            setSockTimeout(sockParam.to);

            Log.i("showInfoThreadProc starts");

            boolean bLastGotState = false;


            //
            if  (  true  )  {

                    //test.testCls_Droid.testRwLock();
                test.testCls_Droid.testInShow();
            }


            //
            qyObjs.QWM_MACS_INFO macsInfo = new qyObjs.QWM_MACS_INFO();
            qyCommonFunc9.TMP_buf2 ver = new qyCommonFunc9.TMP_buf2();

            //
            long dwTickCnt_lastConnect  =  0;
            ByteBuffer tmp_bb = ByteBuffer.allocate(512);
            qyComm.QY_COMM_HEAD tmp_head  =  new qyComm.QY_COMM_HEAD();

            //
            while (!sShowInfoStruct.bQuit) {

                //  2006/06/03
                //dwRet = WaitForSingleObject(sShowInfoStruct.q2.hEvents[0], 5000);
                //if (dwRet == WAIT_FAILED) return iErr;
                myWaitOne(sShowInfoStruct.q2.hEvent0, 1000);

                //  超时也要接着处理，以getMsg为准
                for (; !sShowInfoStruct.bQuit; ) {

                    //q2AvoidTriggerTooHigh(null, sShowInfoStruct.q2);

                    //
                    Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(0,0);
                    if  (  tmpVar  ==  null )  {
                        Log.d("qy","showInfoThreadProc failed: tmpVar is null");
                        return;
                    }
                    Ctx_thread_showInfo ctxThreadShowInfo=tmpVar.m_ctxThreadShowInfo;


                    //
                    boolean bMsgGot = false;

                    //
                    //Log.d("tn_msgInput","showInfoThreadProc. 1");

                    //
                    sShowInfoStruct.q2.m_bDbg=true;

                    //
                    byte[] byteArray_showStruct = showStruct.toByteArray(ctxThreadShowInfo, ctxThreadShowInfo.tmp_Var_QY_SHOW_SMALL);


                    //
                    if (!bLastGotState) {
                            if (!sShowInfoStruct.state_q2.isQ2Empty()) {
//                        if (!isQ2Empty(sShowInfoStruct.state_q2)) {
                                QY_qBuf2.HeadData_showInfo headData = ctxThreadShowInfo.tmp_headData;//new QY_qBuf2.HeadData_showInfo();

                                len = byteArray_showStruct.length;//sizeof(QY_SHOW_SMALL);
                                len = sShowInfoStruct.state_q2.q2GetMsg(headData, byteArray_showStruct, 0,  len);
                                if (len > 0) {

                                    // misMsgu = q2GetMsg(sShowInfoStruct.state_q2);
                                    //if (null == misMsgu)
                                    //   break;

                                    //
                                    //
                                    //
                                    {
                                        long lE = myGetTickCount(null) - headData.tick_post;
                                        if (lE > 2000) {
                                            //Log.d("qy", "stateStr.iE_post is " + lE  + " skipped");
                                            //
                                            continue;
                                        }
                                    }



                                    //
                                    bMsgGot = true;
                                    bLastGotState = true;

                                /*
                                //
                                showStruct.fromBytes(byteArray_showStruct);

                                //
                                Log.d("qy","showInfoThreadProc: showStruct.who_from "  +  showStruct.who_from  + " size "  + sShowInfoStruct.state_q2.size());

                                 */

                                /*
                                //
                                byte ucPercent = 0;
                                int nQNodes = (sShowInfoStruct.state_q2.getQ2Nodes());
//                            int nQNodes = getQ2Nodes(sShowInfoStruct.state_q2);
                                ucPercent = (byte) (nQNodes * 100.0f / (sShowInfoStruct.state_q2.cfg.uiMaxQNodes * 2));
                                //
                                showStruct.ucPercent_showInfoQ = ucPercent;

                                 */

                                }

                            }

                        }

                    // for debug


                    //
                    if (!bMsgGot) {
                        //
                        bLastGotState = false;
                        //
                        QY_qBuf2.HeadData_showInfo headData = ctxThreadShowInfo.tmp_headData;//new QY_qBuf2.HeadData_showInfo();

                        //byte[] byteArray = headData.toByteArray();
                        //len = byteArray.length;//sizeof(QY_SHOW_SMALL);
                        //byte[] byteArray1 = new byte[showStruct.s_Length];//showStruct.toByteArray();
                        //                       misMsgu =  q2GetMsg(sShowInfoStruct.q2);
                        //                       if (null == misMsgu)
                        //                           break;

                        int n = sShowInfoStruct.q2.size();

                        //
                        if ( false ) {
                            Log.d("tn_msgInput", "showInfoThreadProc before q2GetMsg. nQNodes " + sShowInfoStruct.q2.size());
                        }

                        //
                        try {
                            len = sShowInfoStruct.q2.q2GetMsg(headData, byteArray_showStruct, 0,  byteArray_showStruct.length);
                        } catch (Exception e) {
                            Log.e("tn_msgInput", e.getMessage());
                        }
                        if (len > 0) {

                            //
                            if  (  true  )
                            {
                                long lE = myGetTickCount(null) - headData.tick_post;

                                if (lE > 2000) {
                                    //Log.d("qy", "showStr.iE_post is " + lE  +  " skipped");
                                    //
                                    continue;
                                }

                            }

                            //
                            bMsgGot = true;
                        //Log.d("qbuffer", "got normal message "+ showStruct.what);

                        /*
                        showStruct.fromBytes(byteArray1);
                        byte ucPercent = 0;
                        int nQNodes = (sShowInfoStruct.q2.getQ2Nodes());
                        ucPercent = (byte) (nQNodes * 100.0f / (sShowInfoStruct.q2.cfg.uiMaxQNodes * 2));
                        //
                        showStruct.ucPercent_showInfoQ = ucPercent;

                         */

                            //if (misMsgu instanceof qyCommProc.QY_SHOW_SMALL) {
                            //    showStruct = (qyCommProc.QY_SHOW_SMALL) misMsgu;
                            //}
                        }
                    }

                    //
                    if  (  !bMsgGot)  {
                        break;
                    }

                    if (bUsePipe) {
                        Log.i("pipe is not supported in ce");
                        continue;
                    }
                    if (sShowInfoStruct.bUseSock) {
                        //
                        if (bSessionOpen != sShowInfoStruct.status_bSessionOpen) {
                            sShowInfoStruct.status_bSessionOpen = bSessionOpen;
                        }

                        //
                        long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
                        if (dwTickCnt - dwTickCnt_lastData > MAX_pipeCli_idleInMs) {
                            if (bSessionOpen) {
                                qnmCloseSession(session, sock, sockParam.to);
                                bSessionOpen = false;
                            }
                        }
                        dwTickCnt_lastData = dwTickCnt;

                        //
                        if (0 != mytcsicmp(sShowInfoStruct.cfg_serverIp, sShowInfoStruct.servIp)) {
                            if (bSessionOpen) {
                                qnmCloseSession(session, sock, sockParam.to);
                                bSessionOpen = false;
                            }
                            sShowInfoStruct.servIp = sShowInfoStruct.cfg_serverIp;
                        }
                        //
                        if (!bSessionOpen) {

                            if  (  Math.abs(dwTickCnt  -  dwTickCnt_lastConnect)< 2000  )  {
                                continue;
                            }
                            dwTickCnt_lastConnect=dwTickCnt;

                            //qyObjs.QWM_MACS_INFO macsInfo = new qyObjs.QWM_MACS_INFO();
                            //mymemset( ( byte[])macsInfo, 0, sizeof(QWM_MACS_INFO));
                            macsInfo.nMacs = 1;
                            if (0 != qnmOpenSession(sShowInfoStruct.servIp, sShowInfoStruct.usPort, macsInfo,
                                    (byte) 0, CONST_qyServiceId_showInfo, ver.buf, null, session, sock, sockParam, null, 0, tmp_head,  tmp_bb)) {
                                continue;
                            }
                            bSessionOpen = true;
                        }
                        //
                        //
                        if (0 != qySendReq(session, sock, sockParam.to, CONST_qyCmd_showInfo_small_java, byteArray_showStruct, byteArray_showStruct.length,tmp_head,  tmp_bb)) {
                            qnmCloseSession(session, sock, sockParam.to);
                            bSessionOpen = false;
                            continue;
                        }
                        continue;
                    }
                }


            }


            if (bSessionOpen) {
                qnmCloseSession(session, sock, sockParam.to);
            }

            //
            sShowInfoStruct.status_bSessionOpen = false;

            //
            Log.i("showInfoThreadProc ends");





            return;
        };

        public static int showInfo_getQ2Nodes() {
            int nQ2Nodes = sShowInfoStruct.q2.getQ2Nodes();
            return nQ2Nodes;
        }

        //
        public static int showInfo_getStatus(String status) {
            if(sShowInfoStruct == null)
            {
                return -1;
            }
            status = String.format("%s %s %s", sShowInfoStruct.servIp, sShowInfoStruct.status_bSessionOpen ? "connected" : "not connected", showInfo_getQ2Nodes());
            return 0;

        }

        public static int showInfo_cfgServer(String serv) {
            if(sShowInfoStruct==null) return -1;
            sShowInfoStruct.cfg_serverIp = serv;
            return 0;
        }

        public static String showInfo_get_cfgServer(String serv) {
            if (sShowInfoStruct == null) return null;
            return sShowInfoStruct.cfg_serverIp;
        }

        //
        public static int errLog(String fmt) {

            return 0;

        }


    }


}

package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.Consts.CONST_channelType_talking;
import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.ctxCaller.Consts.CONST_ctxCallerType_thread;
import static com.qycx.base.ctxQyTmpl.Consts.CONST_ctxType_qmc;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.dlgTalk_OnQyComm;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.findTalker;
import static com.qycx.base.genericQueue.qyFuncs.qGetMsg;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_confKey;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_confNvrInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_htmlContent;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskProcReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAudioData;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvReplyInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferVideoData;
import static com.qycx.base.mainWnd_procMsg.qyFuncs_Droid.do_closeTask1;
import static com.qycx.base.mainWnd_procMsg.qyFuncs_Droid.mainWnd_chk_tasks;
import static com.qycx.base.mainWnd_procMsg.qyFuncs_Droid.mainWnd_procMsgInput_transferAvInfo;
import static com.qycx.base.mainWnd_procMsg.qyFuncs_Droid.mainWnd_procMsgInput_transferAvReplyInfo;
import static com.qycx.base.mainWnd_procMsg.qyFuncs_Droid.mainWnd_procMsgInput_transferFileReq;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc;
import static com.qycx.base.prepare.qyFuncs_Droid.g_transes;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qnmCommProc.Consts.CONST_stateType_players;
import static com.qycx.base.qnmCommProc.Consts.CONST_stateType_qs;
import static com.qycx.base.qnmCommProc.Consts.CONST_stateType_sys;
import static com.qycx.base.qnmCommProc.Consts.CONST_stateType_talks;
import static com.qycx.base.qnmCommProc.Consts.CONST_stateType_tasks;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_applyForOrgReq;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_applyForPlayer_qmc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_taskStatus;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_procOfflineResSubtype_download;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_getMsgContent;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.bChannelOk;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qwmNetworkStatusCommProc.qyFuncs.CONST_qyStatusTable_en;
import static com.qycx.base.qyCommProc.qyFuncs.bTaskAlive;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_autoCanceledByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_canceledByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_canceledBySender;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_err_cantGet_index_activeMems_from;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_err_missingOrgReq;
import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.bChkMessengerChannelsReady;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.myGetPeer;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.notifyPanicToApp;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_chkTask;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_msgArrive;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_showHint;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_task_end;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_toStartLocalAudioPlayer;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.qyWmComm.Consts.QY_RC_WMOK;
import static com.qycx.base.tmpFunc.qyFuncs.myGetCurrentProcessId;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.mytoString;
import static com.qycx.base.tmpFunc_Droid.qyFuncs_Droid.myGetTickCount_android;
import static com.qycx.base.videoFunc_open.Consts.cntof_buf5;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.widget.Toast;

import com.qycx.Activity_dlgTalk;
import com.qycx.MainActivity;
import com.qycx.base.util.Log;
import com.qycx.base.util.ToastUtils;

public class qyMcMainWndProc_isCli {

    public static class qyFuncs_Droid {

        public static int initVar_onCreate_mainFrame(long p0, Object hMainWnd, qyMcMainWndProc.QY_MC_mainWndVar pVar) {
            int iErr = -1;
            //
            if (null == hMainWnd) return -1;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            if (pProcInfo.m_iCtxType != CONST_ctxType_qmc) return -1;

            pProcInfo.dwThreadId_gui = myGetCurrentProcessId();
            pVar.ctxCaller = new ctxCaller.CTX_caller();
            pVar.ctxCaller.type = CONST_ctxCallerType_thread;
            pVar.ctxCaller.dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            pVar.pMsgBuf_msgArrive = new qnmCommProc_mis.MIS_MSGU();
            guiShare.GuiShare.pf_mainWnd_setTimer.call();

            iErr = 0;
            return iErr;
        }

        public static int exitVar_onDestroy_mainFrame(long p0, Object hMainWnd, qyMcMainWndProc.QY_MC_mainWndVar pVar) {
            int iErr = -1;

            if (null == hMainWnd) {
                Log.i("exitVar_onDestroy_mainFrame failed: pMainWnd or pVar is null");
                return -1;
            }

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) return -1;


            Log.i("exitVar_onDestroy_mainFrame enters.");
            Log.d(("exitVar_onDestroy_mainFrame enters."));

            //
            //if (pVar->uiTimerId) KillTimer(hMainWnd, pVar->uiTimerId);
            guiShare.GuiShare.pf_mainWnd_killTimer.call();

            if (pVar.pMsgBuf_msgArrive != null) {
//                qyMemoryFunc.qyFuncs.myfree( pVar.pMsgBuf_msgArrive);
                pVar.pMsgBuf_msgArrive = null;
            }
            return 0;
        }


        public static int tmpHandler_findMissingOrgReq(Object p0, qnmCommProc_mis.MIS_MSG_taskStatus pMsgTaskStatus, Object pQElem) {
            int iErr = -1;
            //  p0;

            int index_taskInfo = (int) pQElem;
            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = new qyDefs.QY_MESSENGER_ID();

            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, index_taskInfo, "tmpHandler_findMissingOrgReq");
            if(pTaskInfo == null) return iErr;
            if(!pTaskInfo.bUsed) return iErr;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();

            if(pTaskData == null){
                showInfo_open.qyFuncs.showInfo_open("tmpHandler_findMissingOrgReq error: line_148 pTaskData is null ");
                return iErr;
            }
            qnmCommProc_mis.MIS_MSG_TASK tmp_task = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
            {
                if (tmp_task.uiType != CONST_misMsgType_task) return iErr;
                qnmCommProc_mis.IM_CONTENTU pContent = (qnmCommProc_mis.IM_CONTENTU) tmp_task.data.buf;
                if (pContent.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo)
                    return iErr;
                qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                qnmCommProc_mis.AV_TRAN_INFO pAvTranInfo = transferAvInfo.ass.tranInfo;
                //
                if (pMsgTaskStatus.uiContentType == CONST_imCommType_transferAudioData && pMsgTaskStatus.uiTranNo == pAvTranInfo.audio.uiTranNo_openAvDev) {
                    idInfo_logicalPeer.ui64Id = tmp_task.addr_logicalPeer.idInfo.ui64Id;
                    //
                    iErr = 1;
                    return iErr;
                }
                if (pMsgTaskStatus.uiContentType == CONST_imCommType_transferVideoData && pMsgTaskStatus.uiTranNo == pAvTranInfo.video.uiTranNo_openAvDev) {
                    idInfo_logicalPeer.ui64Id = tmp_task.addr_logicalPeer.idInfo.ui64Id;
                    //
                    iErr = 1;
                    return iErr;
                }
            }

            iErr = 0;
            return iErr;
        }

        static int logCountA = 0;
        static int logCount = 0;

        static int logCatB = 0;

        public static int mainWnd_OnQyPostComm(Object hMainWnd, qyMcMainWndProc.QY_MC_mainWndVar var, int wParam, int lParam) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            if (pProcInfo.m_iCtxType != CONST_ctxType_qmc) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            boolean bDbg = false;
            String str;

            int len = 0;
            int i;

            if (pQyMc.bGuiQuit) {
                Log.e("mainWnd_OnQyPostComm failed, bGuiQuit is true");
                return -1;
            }
            if (pQyMc.bPrepareToGuiQuit) {
                Log.e("mainWnd_OnQyPostComm failed, bPrepareToGuiQuit is true");
                return -1;
            }

            switch (wParam) {
                case CONST_qyWmParam_msgArrive: {
                    //  CQyMalloc			mallocObj;
                    qnmCommProc_mis.MIS_MSGU pMsg = var.pMsgBuf_msgArrive; //  (  MIS_MSGU  *  )mallocObj.malloc(  sizeof(  MIS_MSGU  )  );
//                    qnmCommProc_mis.MIS_MSGU pMsg = new qnmCommProc_mis.MIS_MSGU(); //  (  MIS_MSGU  *  )mallocObj.malloc(  sizeof(  MIS_MSGU  )  );
                    if (null == pMsg) return iErr;

                    for (; !pQyMc.bQuit; ) {
                        if (null == (pMsg = (qnmCommProc_mis.MIS_MSGU) qGetMsg(pQyMc.gui.processQ)))
                            break;
//                        showInfo_open.qyFuncs.showInfo_open("Msg, TmpId: " + pMsg.tmpID);
                        switch (pMsg.uiType) {
                            case CONST_misMsgType_talk:
                            case CONST_misMsgType_task: {
                                Object hWnd = null;
                                qnmCommProc_mis.MSGR_ADDR addr_logicalPeer = new qnmCommProc_mis.MSGR_ADDR();
                                boolean bNeedNotShowWnd = false;

                                if (pMsg.uiType == CONST_misMsgType_talk) {
                                    //
                                    if (null == (pMisCnt = pProcInfo.pMisCnt))
                                        return iErr;
                                    qnmCommProc_mis.MIS_MSG_TALK talk = (qnmCommProc_mis.MIS_MSG_TALK) pMsg;
                                    if (0 != myGetPeer("", talk.data.route, talk.addr_logicalPeer, addr_logicalPeer))
                                        return iErr;

                                    qnmCommProc_mis.IM_CONTENTU tmp_pContent = talk.data.buf;
                                    if(tmp_pContent.uiType == CONST_imCommType_htmlContent)
                                    {
                                        guiShare.GuiShare.pf_gui_procMsg.call(pMsg);
                                    }

                                } else if (pMsg.uiType == CONST_misMsgType_task) {
                                    //
                                    if (null == (pMisCnt = pProcInfo.pMisCnt))
                                        return iErr;
                                    qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                                    if (0 != myGetPeer("", task.data.route, task.addr_logicalPeer, addr_logicalPeer))
                                        return iErr;

                                    qnmCommProc_mis.IM_CONTENTU pContent = task.data.buf;

                                    if (pContent.uiType == CONST_imCommType_transferFileReq && ((qnmCommProc_mis.TRANSFER_FILE_REQ) pContent).usSubtype == CONST_procOfflineResSubtype_download) {
                                        if (null == (hWnd = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, addr_logicalPeer.idInfo))) {

                                            return iErr;
                                        }

                                        //
                                        if (0 != guiShare.GuiShare.pf_dlgTalk_qPostMsg.call(hWnd, pMsg, len))
                                            return iErr;


                                        guiShare.GuiShare.pf_PostMessage.call(hWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                                        //
                                        break;
                                    }
                                    //
                                    if (0 == task.iTaskId) {

                                        bNeedNotShowWnd = true;
                                    }

                                } else return iErr;

                            }
                            break;

                            case CONST_misMsgType_input:    //  2014/09/05
                            {
                                Object hWnd = null;
                                qnmCommProc_mis.MSGR_ADDR addr_logicalPeer = new qnmCommProc_mis.MSGR_ADDR();
                                qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                                qnmCommProc_mis.IM_CONTENTU pContent = input.data.buf;



                                //  2014/05/05
                                boolean bMsgProcessed = false;
                                switch (pContent.uiType) {
                                    case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                                        bMsgProcessed = true;

                                        //
                                        Log.d("qy","before mainWnd_procMsgInput_transferAvInfo. from "  +  input.data.route.idInfo_from.ui64Id);
                                        //
                                        mainWnd_procMsgInput_transferAvInfo(hMainWnd, var, input);

                                        break;
                                    case CONST_imCommType_transferAvReplyInfo:
                                        bMsgProcessed = true;
                                        //
                                        mainWnd_procMsgInput_transferAvReplyInfo(hMainWnd, var, input);
                                        //
                                        break;
                                    case CONST_imCommType_transferFileReq:
                                        bMsgProcessed = true;
                                        //
                                        mainWnd_procMsgInput_transferFileReq(hMainWnd, var, input);
                                        //
                                        break;

                                    case CONST_imCommType_taskProcReq:  //  2014/10/09

                                        Object hTalk = null;
                                        if (null != (hTalk = findTalker(pMisCnt, input.addr_logicalPeer.idInfo))) {
//                                    {
                                            bMsgProcessed = true;
                                            dlgTalk_OnQyComm(hTalk, 0, pMsg);
                                        }
                                        break;

                                    default:
                                        break;
                                }
                                if (bMsgProcessed) break;

                                //
                                if (0 == input.addr_logicalPeer.idInfo.ui64Id) {
                                    break;
                                }

                                addr_logicalPeer.idInfo.ui64Id = input.addr_logicalPeer.idInfo.ui64Id;
                                hWnd = null;

                                //  2013/09/09

                                if (pContent.uiType == CONST_imCommType_confKey) {
                                    int a = 0;

                                }

                                if (pContent.uiType == CONST_imCommType_confNvrInfo) {
                                    int a = 3;
                                }

                                //
                                if (null == hWnd) {
                                    //  2014/01/14
                                    boolean bNeedNotShowWnd = false;
                                    if (null == (hWnd = pProcInfo.tryToTalkToMessenger_any(addr_logicalPeer.idInfo.ui64Id, bNeedNotShowWnd, false, hWnd))) {
//                                        str = ("mainWnd_onQyPostComm. trytoTalkToMessenger_any "+addr_logicalPeer.idInfo.ui64Id+" failed");
//                                        Log.e(str);
                                        return iErr;
                                    }
                                }
                                if (pContent.uiType == CONST_imCommType_confKey) {
                                    int a = 0;

                                }
                                guiShare.GuiShare.pf_gui_dlgTalk_procMsg.call(hWnd, pMsg);
                            }

                            break;


                            case CONST_misMsgType_taskStatus: {
                                Object hWnd = null;
                                qnmCommProc_mis.MSGR_ADDR addr_logicalPeer = new qnmCommProc_mis.MSGR_ADDR();
                                qnmCommProc_mis.MIS_MSG_taskStatus taskStatus = (qnmCommProc_mis.MIS_MSG_taskStatus) pMsg;
                                if (taskStatus.ucbNotification) {
                                    displayNotification(pProcInfo, taskStatus);
                                    break;
                                }

                                if (taskStatus.iStatus == CONST_imTaskStatus_canceledBySender || taskStatus.iStatus == CONST_imTaskStatus_canceledByReceiver) {
                                    int jj = 0;
                                    int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(mainActivity.var_common.ctxCaller, pProcInfo, taskStatus.iTaskId);
                                    if (index_taskInfo < 0) {
                                        return iErr;
                                    }
                                    qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, index_taskInfo, "mainWnd_onqyPostComm");
                                    if (pTaskInfo == null) return iErr;
                                    qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                                    if (pTaskData == null) return iErr;
                                    //
                                    if (pTaskData.msgU.uiType != CONST_misMsgType_task) return iErr;
                                    if (taskStatus.iStatus == CONST_imTaskStatus_canceledBySender) {
                                        qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                                        if (task.data.route.idInfo_from.ui64Id == pMisCnt.idInfo.ui64Id) {
                                            return iErr;
                                        }
                                    }
                                    //
                                    do_closeTask1(pTaskInfo.var.iTaskId, true, "");
                                    //
                                    break;
                                }

                                addr_logicalPeer.idInfo.ui64Id = taskStatus.idInfo_logicalPeer.ui64Id;
                                boolean bNeeded_applyForOrgReq = false;

                                if (taskStatus.iStatus == CONST_imTaskStatus_err_cantGet_index_activeMems_from) {
                                    bNeeded_applyForOrgReq = true;
                                } else {

                                    if (taskStatus.iStatus == CONST_imTaskStatus_err_missingOrgReq) {       //
                                        for (i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
                                            if (tmpHandler_findMissingOrgReq(null, (qnmCommProc_mis.MIS_MSG_taskStatus )pMsg, i) > 0) {
                                                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, i, "");
                                                if (pTaskInfo == null) return iErr;
                                                if (!pTaskInfo.bUsed) return iErr;
                                                pTaskInfo.var.dwTickCnt_recv_lastRefreshed1 = myGetTickCount(null);
                                                //
                                                {
                                                    qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                                                    if (pTaskData == null) return iErr;

                                                    qnmCommProc_mis.MSGR_ADDR addr = ((qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU).addr_logicalPeer;
                                                    taskStatus.idInfo_logicalPeer = addr.idInfo;
                                                    qnmCommProc_mis.MIS_MSG_TASK tmp_task = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                                                    {
                                                        if (tmp_task.uiType != CONST_misMsgType_task)
                                                            return iErr;

                                                        boolean bMeInitiator = false;
                                                        qnmCommProc_mis.IM_CONTENTU pContent = M_getMsgContent(tmp_task.uiType, tmp_task.ucFlg, tmp_task.data);
                                                        qnmCommProc_mis.IM_CONTENTU tmp_pContent = pContent;
                                                        qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                                                        if (pContent.uiType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {
                                                            if (transferAvInfo.confCfg.idInfo_initiator.ui64Id == pMisCnt.idInfo.ui64Id) {
                                                                bMeInitiator = true;
                                                            }
                                                        }

                                                        if (logCountA < 10) {
                                                            ++logCountA;
                                                        } else {
                                                            logCountA = 0;

                                                            str = "err_missing: it is msgTask. status is " + tmp_task.iStatus + " peer：" + addr.idInfo.ui64Id;
                                                            //Log.d(str);
                                                        }

                                                        boolean bTryToRemove = false;
                                                        int maxWaits = 48;
                                                        //


                                                        //
                                                        if (tmp_task.iStatus == CONST_imTaskStatus_autoCanceledByReceiver) {
                                                            bTryToRemove = true;
                                                            //
                                                            //Log.d("err_missing: autoCanceled, so set bTryToRemove to true. peer:" + addr.idInfo.ui64Id);

                                                        }
                                                        //
                                                        if (bTaskAlive(tmp_task.iStatus)
                                                            //&&  tmp_pMsg->task.iStatus  !=  CONST_imTaskStatus_resp
                                                        ) {
                                                            long dwTickCnt = myGetTickCount(null);
                                                            if (0 == pTaskInfo.var.tmpErrMissing.dwTickCnt_startToCalc) {
                                                                pTaskInfo.var.tmpErrMissing.dwTickCnt_startToCalc = dwTickCnt;
                                                                pTaskInfo.var.tmpErrMissing.nTimes_errMissing = 0;
                                                                //
                                                                Log.d("err_missing: tickCnt_start is 0");
                                                            } else {
                                                                int iDiffInMs = (int) (dwTickCnt - pTaskInfo.var.tmpErrMissing.dwTickCnt_startToCalc);
                                                                int maxWaitsInMs = 5000;    //  3000
                                                                if (Math.abs(iDiffInMs) > maxWaitsInMs) {
                                                                    Log.d("err_missing: iDiffInMs > " + maxWaitsInMs + ". nTimes " + pTaskInfo.var.tmpErrMissing.nTimes_errMissing + " reset");
                                                                    //
                                                                    pTaskInfo.var.tmpErrMissing.dwTickCnt_startToCalc = dwTickCnt;
                                                                    pTaskInfo.var.tmpErrMissing.nTimes_errMissing = 0;
                                                                    //

                                                                }
                                                            }
                                                            pTaskInfo.var.tmpErrMissing.nTimes_errMissing++;
                                                            showInfo_open.qyFuncs.showInfo_open("nTimes_errMissing: " + pTaskInfo.var.tmpErrMissing.nTimes_errMissing);
                                                            //
                                                            if (pTaskInfo.var.tmpErrMissing.nTimes_errMissing >= maxWaits && !bMeInitiator) {
                                                                //  这表明任务不完整

                                                                bTryToRemove = true;

                                                                //
                                                                Log.d("err_missing: nTimes_err: " + pTaskInfo.var.tmpErrMissing.nTimes_errMissing + " so set bTryToRemove to true");


                                                                //
                                                                if (logCountC < maxWaits) {
                                                                    ++logCountC;
                                                                } else {
                                                                    logCountC = 0;
                                                                    Log.d("err_missing: nTimes >= " + maxWaits);
                                                                }

                                                            }
                                                        }
                                                        //
                                                        if (bTryToRemove) {
                                                            if (logCountD < maxWaits) {
                                                                ++logCountD;
                                                            } else {
                                                                logCountD = 0;
                                                                Log.d("bTryToRemove err_missing 880");

                                                            }
                                                            //
                                                            int iTaskId_av = 0;
                                                            qyDefs.QY_MESSENGER_ID idInfo_a = new qyDefs.QY_MESSENGER_ID();
                                                            idInfo_a.ui64Id = 0;
                                                            //  if (!bExists_activeTaskAv( iTaskId_av,  idInfo_a))
                                                            //  {

                                                            Activity_dlgTalk hTalk = null;
                                                            if ((hTalk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, addr.idInfo)) != null && dlgTalkProc_help.qyFuncs_Droid.bDlgTalkActive(hTalk)) {

                                                                Log.d("after findTalker.bTalkActive, bPrepare: " + hTalk.m_var.av.taskInfo.bPrepare + " peer: " + addr.idInfo.ui64Id);

                                                                //
                                                                long currentTickCount = myGetTickCount(null);
                                                                //  currentTickCount - activity_DlgTalk.m_var.tickCountOnCreate > 1500 ||
                                                                if (!hTalk.m_var.av.taskInfo.bPrepare) {

                                                                    Log.d("talker is active. bNeedRemoved_toRecover set to true");


                                                                    pTaskInfo.var.bNeedRemoved_toRecover = true;

                                                                    //
                                                                    pProcInfo.m_bNeedToRemoveTaskAtOnce = true;
                                                                }


                                                            }
                                                            //  }

                                                        }

                                                    }
                                                }
                                                //
                                                break;
                                            }
                                        }
                                        if (i == pProcInfo.cfg.usMaxCnt_taskInfos) {
                                            bNeeded_applyForOrgReq = true;

                                        }
                                    }

                                    //
                                    guiShare.GuiShare.pf_gui_procMsg.call(pMsg);

                                }


                                if (bNeeded_applyForOrgReq) {

                                    //
                                    //TCHAR tBuf[128];
                                    //
                                    qnmCommProc_mis.TASK_PROC_REQ taskProcReq = new qnmCommProc_mis.TASK_PROC_REQ();
                                    int lenInBytes;
                                    //qyFuncs.mymemset((byte[])taskProcReq, 0, sizeof(TASK_PROC_REQ));
                                    taskProcReq.uiType = CONST_imCommType_taskProcReq;
                                    taskProcReq.usOp = CONST_imOp_recv_applyForOrgReq;
                                    //  taskProcReq.tStartTime_org  =  0;
                                    taskProcReq.uiTranNo_org = taskStatus.uiTranNo;
                                    //  taskProcReq.uiSerialNo_org  =  0;
                                    taskProcReq.uiContentType_org = taskStatus.uiContentType;
                                    //
                                    lenInBytes = 128;//sizeof(TASK_PROC_REQ);
                                    //
                                    //
                                    //
                                    long tStartTran = tmpFunc.qyFuncs.mytime();
                                    int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
                                    //  2017/10/11
                                    if (taskStatus.iStatus == CONST_imTaskStatus_err_cantGet_index_activeMems_from) {
                                        str = ("cantGet_index_activeMems_from. ");
                                    } else {
                                        str = ("taskAvOrgReq not found. ");
                                    }
                                    //
                                    str += String.format("send taskProcReq to %s. tn %s. kkkk", taskStatus.idInfo_taskSender.ui64Id, uiTranNo);

                                    //
                                    if  (  taskStatus.idInfo_taskSender.ui64Id  <  10000)  {
                                        //Log.d("qy","heher");
                                    }

//                                    byte[] data = taskProcReq.toByteArray();
                                    lenInBytes = 0;//buf1.length;
                                    if (0 != postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, (short) taskStatus.cmdCode, tStartTran, uiTranNo, (short) 0, 0, 0, taskProcReq, (int) lenInBytes, addr_logicalPeer.idInfo.ui64Id, 0, 0, taskStatus.idInfo_taskSender.ui64Id, 0, (qnmCommProc_mis.MIS_MSGU) null, false))
                                        return iErr;

                                    //  2017/10/03
                                    pProcInfo.status.dwTickCnt_sendTo_applyForOrgReq = myGetTickCount(null);

                                }

                            }
                            break;

                            case CONST_misMsgType_applyForPlayer_qmc:
                                pProcInfo.doApplyForPlayer(hMainWnd, pMsg);
                                break;

                            default:
                                //Log.e("CMainFrame::OnQyPostComm: msgArrive:  unprocessed: msgType: %d", pMsg->uiType);
                                break;
                        }
                    }
                }
                break;

                case CONST_qyWmParam_task_end:
                    qmcVideoCapture_isCli.qyFuncs.tryToFreeSharedObjs(pProcInfo);
                    break;
                case CONST_qyWmParam_chkTask:
                    qmcVideoCapture.qyFuncs.qmcChkTasks_gui(pProcInfo);
                    break;
                case CONST_qyWmParam_toStartLocalAudioPlayer:      //  2013/05/26
                    //startLocalAudioPlayer(pProcInfo);
                    break;
                case CONST_qyWmParam_showHint: {
//                    Toast.makeText(mainActivity, "修改密码成功", Toast.LENGTH_SHORT).show();
                    ToastUtils.showToast("修改密码成功");
                }
                break;
                default:
                    break;
            }
            iErr = QY_RC_WMOK;

            return iErr;
        }

        static int logCountC = 0;
        static int logCountD = 0;

        //
        public static int mainWnd_showStatus(qyMcMainWndProc.QY_MC_mainWndVar var) {




            //
            showState_sys();


            //
            showState_players();

            if (0 == (var.loopCtrl % 3)) {
                showState_tasks(var);
                showState_talks();
                showState_qs();
            }







            //
            return 0;

        }

        private static ActivityManager.MemoryInfo getAvailableMemory() {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            ActivityManager activityManager = (ActivityManager) mainActivity.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            return memoryInfo;
        }

        public static int showState_sys() {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //
            qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(Consts.CONST_iParamType_main,0);
            if  (  tmpVar ==  null )  {
                return  -1;
            }
            qyShowInfoCommProc.Ctx_thread_main ctxThreadMain = tmpVar.m_ctxThreadMain;
            if  (  ctxThreadMain ==null) {
                return  -1;
            }

            //
            int iPos = 0;

            //
            qyCommProc.mySYSTEMTIME when = ctxThreadMain.tmp_showState_sys_when;//new qyCommProc.mySYSTEMTIME();
            tmpFunc.qyFuncs.myGetLocalTime(when);

            Runtime rt = Runtime.getRuntime();
            long javaMaxHeapSize = rt.maxMemory();
            long javaTotalHeapSize = rt.totalMemory();
            long javaFreeHeapSize = rt.freeMemory();
            //
            long nativeHeap = Debug.getNativeHeapSize();
            long nativeAllocHeap = Debug.getNativeHeapAllocatedSize();
            long nativeFreeSize = Debug.getNativeHeapFreeSize();

            ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

            int iM = 1024 * 1024;

            //
            //Log.d("tn_msgInput","showState_sys.696");


            //
            String str;
            str = String.format("maxHeap %dM, ", javaMaxHeapSize / 1024 / 1024);
            //
            str += String.format("totalHeap %dM, freeHeap %dM.", javaTotalHeapSize / iM, javaFreeHeapSize / iM);
            //
            str += String.format(" total %dM,avail %dM", memoryInfo.totalMem / iM, memoryInfo.availMem / iM);
            showInfo_open.qyFuncs.showState_open(0, "", str, CONST_stateType_sys, 0, when, iPos, 0);
            iPos++;

            //
            //Log.d("tn_msgInput","showState_sys. 705");



            //
            str = "";
            str += String.format("nativeHeap %dM,nativeAlloc %dM,nativeFree %dM", nativeHeap / iM, nativeAllocHeap / iM, nativeFreeSize / iM);
            showInfo_open.qyFuncs.showState_open(0, "", str, CONST_stateType_sys, 0, when, iPos, 0);
            iPos++;


            //
            str = "";
            qmCommFunc_mis.MIS_CHANNEL channel = getChannelByType(pMisCnt, CONST_channelType_talking);
            if (!bChannelOk(channel,false)) {
                str += ("t not ok");
            }

            channel = getChannelByType(pMisCnt, CONST_channelType_media);
            if (!bChannelOk(channel,false)) {
                str += (" v not ok");
            }
            channel = getChannelByType(pMisCnt, CONST_channelType_realTimeMedia);
            if (!bChannelOk(channel,false)) {
                str += (" a not ok");
            }
            if (str != null) {
                str = "channel " + str + " ";
            }
            //
            if (str != null) {
                showInfo_open.qyFuncs.showState_open(0, "", str, CONST_stateType_sys, 0, when, iPos, 0);
                iPos++;
            }
//
//            String tServ;
//            byte[] pIp = pMisCnt.server.ip;
//            {
//                tServ = mytoString(pIp);
//            }

            //
            /*

            str = String.format("FPS:{0:000.0} IO:{1,6:0.0},{2,6:0.0} t(%s,{4,3}) a({5,5:0.0},{6,5:0.0} {7},{8}) v({9},{10} {11},{12}) f({13},{14} {15},{16}) CPU {17}% {18}. s({19})",
                    pProcInfo.status.render.fps, pProcInfo.status.netStat.ins.uiInSpeedInKbps / 1000.0f, pProcInfo.status.netStat.ins.uiOutSpeedInKbps / 1000.0f,
                    pProcInfo.av.talk.bChannelReady ? ("  ") : ("no"), pProcInfo.av.talk.iThreadsStatus,
                    pProcInfo.status.netStat.uiInSpeedInKbps_a / 1000.0f, pProcInfo.status.netStat.uiOutSpeedInKbps_a / 1000.0f, pProcInfo.av.audio.bChannelReady ? ("  ") : ("no"), pProcInfo.av.audio.iThreadsStatus,
                    pProcInfo.status.netStat.uiInSpeedInKbps_v / 1000.0f, pProcInfo.status.netStat.uiOutSpeedInKbps_v / 1000.0f, pProcInfo.av.video.bChannelReady ? ("  ") : ("no"), pProcInfo.av.video.iThreadsStatus,
                    pProcInfo.status.netStat.uiInSpeedInKbps_f / 1000.0f, pProcInfo.status.netStat.uiOutSpeedInKbps_f / 1000.0f, pProcInfo.av.robot.bChannelReady ? ("  ") : ("no"), pProcInfo.av.robot.iThreadsStatus,
                    pProcInfo.status.cpu.iCpuUsage, "", 0);

             */
            float fi =   (float)pProcInfo.status.netStat.ins.uiInSpeedInKbps / 1000.0f;
            float fo  =  (float)pProcInfo.status.netStat.ins.uiOutSpeedInKbps / 1000.0f;
            float fia  =  (float)pProcInfo.status.netStat.uiInSpeedInKbps_a / 1000.0f;
            float foa  =  (float)pProcInfo.status.netStat.uiOutSpeedInKbps_a / 1000.0f;
            float fiv  =  (float)pProcInfo.status.netStat.uiInSpeedInKbps_v / 1000.0f;
            float fov  =  (float)pProcInfo.status.netStat.uiOutSpeedInKbps_v / 1000.0f;

            str = "IO:" +   fi  + ","  + fo
                   +  " t( " +  (  pProcInfo.av.talk.bChannelReady ? ("  ") : ("no")  )  +  " "  +  pProcInfo.av.talk.iThreadsStatus  +  ")"
                   +  " a(" +  fia    +  ","  +  foa   +  " "  + (pProcInfo.av.audio.bChannelReady ? ("  ") : ("no"))  + " "  +   pProcInfo.av.audio.iThreadsStatus
                    +  " v(" +  fiv  +  ","  +  fov  +  " "  + (pProcInfo.av.video.bChannelReady ? ("  ") : ("no"))  +  " "  +   pProcInfo.av.video.iThreadsStatus
                 //   +  " f(" +  pProcInfo.status.netStat.uiInSpeedInKbps_f / 1000.0f  +  ","  +  pProcInfo.status.netStat.uiOutSpeedInKbps_f / 1000.0f  +  " "  + (pProcInfo.av.robot.bChannelReady ? ("  ") : ("no"))  +   pProcInfo.av.robot.iThreadsStatus
                    +  " CPU "  +  pProcInfo.status.cpu.iCpuUsage
            ;



            showInfo_open.qyFuncs.showState_open(0, "", str, CONST_stateType_sys, 0, when, iPos, 0);
            iPos++;



            //
            str = "trans idle:";
            int i;
            if (g_transes != null) {
                for (i = 0; i < g_transes.length; i++) {
                    if (g_transes[i].bUsed) continue;
                    str += i;
                }
            }
            showInfo_open.qyFuncs.showState_open(0, "", str, CONST_stateType_sys, 0, when, iPos, 0);
            iPos++;






            //
            showInfo_open.qyFuncs.showState_open(0, "", str, CONST_stateType_sys, 0, when, -1, 0);




            //
            return 0;

        }

        //
        public static class Param_tmp_showState_task {
            public int usStateType;
            public int usStateSubtype;
            public qyCommProc.mySYSTEMTIME when;
            public int iPos;
        }

        //
        @SuppressLint("DefaultLocale")
        public static mcSharedQ.PF_enumTask tmp_showState_task = (ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU p0Param, qyMcMainProc.PARAM_findOrgMsg p1Param, taskCommon.TASK_common taskElemParam) -> {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            Param_tmp_showState_task param = (Param_tmp_showState_task) p1Param.param_tmp_showState_task;
            // p1Param
            //
            taskCommon.TASK_common taskElem = (taskCommon.TASK_common) taskElemParam;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskElem.m_index_taskInfo, "findOrgMsg");
            if (pTaskInfo == null) return iErr;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return iErr;

            String str;

            //
            qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
            {
                qnmCommProc_mis.IM_CONTENTU pContent = (qnmCommProc_mis.IM_CONTENTU) pMsgTask.data.buf;
                if (pContent.uiType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {
                    if (pTaskData.uiType != CONST_taskDataType_conf) return iErr;
                    qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                    qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();
                    //
                    taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                    taskAv.PROC_TASK_AV pTaskAv = task.myTask;
                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                    str = String.format("activeMems %d, maxSpeakers %d, limit_trans %d", videoConference.usCntLimit_activeMems_from, transferAvInfo.confCfg.usMaxSpeakers, pTaskAv.trans.usCntLimit_transforms);
                    showInfo_open.qyFuncs.showState_open(0, "", str, param.usStateType, param.usStateSubtype, param.when, param.iPos, pTaskInfo.var.iTaskId);
                    param.iPos++;
                }
            }
            iErr = 0;
            return iErr;
        };

        public static int showState_tasks(qyMcMainWndProc.QY_MC_mainWndVar var) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = null;

            qyCommProc.mySYSTEMTIME when = new qyCommProc.mySYSTEMTIME();
            tmpFunc.qyFuncs.myGetLocalTime(when);

            //
            int iPos = 0;
            int i;
            String str;
            int index_taskInfo;
            int uiTickCnt_now = (int) myGetTickCount_android.call();

            for (index_taskInfo = 0; index_taskInfo < pProcInfo.cfg.usMaxCnt_taskInfos; index_taskInfo++) {
                pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(var.ctxCaller, index_taskInfo, "mainWnd_chk_tasks");
                if (!pTaskInfo.bUsed) continue;
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (pTaskData == null) continue;
                //
                str = String.format("task[%d]: ", index_taskInfo);
                //
                qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                {
                    if (pMsgTask.uiType != CONST_misMsgType_task) {
                        continue;
                    }

                    //
                    str += String.format("%d.%d, peer %d. ", pMsgTask.data.route.idInfo_from.ui64Id, pMsgTask.data.route.idInfo_to.ui64Id, pMsgTask.addr_logicalPeer.idInfo.ui64Id);

                    //
                    qnmCommProc_mis.IM_CONTENTU pContent = (qnmCommProc_mis.IM_CONTENTU) pMsgTask.data.buf;
                    //
                    switch (pContent.uiType) {
                        case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                            str += "av";
                            showInfo_open.qyFuncs.showState_open(0, "showTasks", str, CONST_stateType_tasks, 0, when, iPos, pTaskInfo.var.iTaskId);
                            iPos++;

                            qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                            str = String.format("tn_a %d, tn_v %d", transferAvInfo.ass.tranInfo.audio.uiTranNo_openAvDev, transferAvInfo.ass.tranInfo.video.uiTranNo_openAvDev);

                            showInfo_open.qyFuncs.showState_open(0, "showTasks", str, CONST_stateType_tasks, 0, when, iPos, pTaskInfo.var.iTaskId);
                            iPos++;

                            //
                            if (pTaskData.uiType != CONST_taskDataType_conf) break;
                            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

                            int a;
                            if(videoConference == null){
                                showInfo_open.qyFuncs.showInfo_open(0, "showState_tasks","error: 855 videoConference is null");
                                continue;
                            }
                            if(videoConference.activeMems_from == null){
                                showInfo_open.qyFuncs.showInfo_open(0, "showState_tasks","error: 859 videoConference.activeMems_from  is null");
                                continue;

                            }
                            for (a = 0; a < videoConference.activeMems_from.length; a++) {
                                qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[a];
                                //
                                if (pActiveMem_from.avStream.idInfo.ui64Id != 0) {
                                    str = "";
                                    str += String.format("act%d:%d:", a, pActiveMem_from.avStream.idInfo.ui64Id);
                                    str += String.format("a(%d,%d),", pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev, (int) pActiveMem_from.avStream.obj.tranInfo.audio.compressor.ucCompressors);
                                    str += String.format("v(%d,%d)", pActiveMem_from.avStream.obj.tranInfo.video.uiTranNo_openAvDev, (int) pActiveMem_from.avStream.obj.tranInfo.video.compressor.ucCompressors);

                                    int iFourcc = pActiveMem_from.avStream.obj.tranInfo.video.vh_stream.dwFourcc;

                                    videoFunc_open.TMP_buf5 fourccStr = new videoFunc_open.TMP_buf5();
                                    //byte fourccStr[4 + 1] = "";
                                    videoFunc_open.qyFuncs.iFourcc2Str(iFourcc, fourccStr.buf, cntof_buf5);
                                    str += mytoString(fourccStr.buf);
                                    //
                                    showInfo_open.qyFuncs.showState_open(0, "showTasks", str, CONST_stateType_tasks, 0, when, iPos, pTaskInfo.var.iTaskId);
                                    iPos++;

                                }
                            }

                            //
                            Param_tmp_showState_task param = new Param_tmp_showState_task();
                            param.usStateType = CONST_stateType_tasks;
                            param.usStateSubtype = 0;
                            param.when = when;
                            param.iPos = iPos;

                            qnmCommProc_mis.CMIS_MSGU p0 = new qnmCommProc_mis.CMIS_MSGU();
                            qyMcMainProc.PARAM_findOrgMsg p1 = new qyMcMainProc.PARAM_findOrgMsg();
                            p1.param_tmp_showState_task = param;
                            pProcInfo.processQ_media.qTraverse(null, tmp_showState_task, p0.msgU, p1);
                            iPos = param.iPos;

                            break;
                        default:
                            str += "other";

                            //
                            showInfo_open.qyFuncs.showState_open(0, "showTasks", str, CONST_stateType_tasks, 0, when, iPos, pTaskInfo.var.iTaskId);
                            iPos++;
                            break;
                    }

                    if (qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(var.ctxCaller, pProcInfo, index_taskInfo)) {
                        str = "task alive.";
                    } else {
                        str = "task not alive. ";
                        if (pTaskInfo.var.bClosed) str += "closed.";
                    }
                    //
                    str += (" iStatus ") + (pMsgTask.iStatus) + (" ");
                    str += qyGetDesByType1(CONST_qyStatusTable_en, pMsgTask.iStatus);
                    str += (".");

                    //
                    long iElapseInMs;
                    iElapseInMs = Math.abs(uiTickCnt_now - pTaskInfo.var.dwTickCnt_recv_lastRefreshed1);
                    str += String.format(" lastRecv: %sms elapsed", iElapseInMs);
                    showInfo_open.qyFuncs.showState_open(0, "showTasks", str, CONST_stateType_tasks, 0, when, iPos, pTaskInfo.var.iTaskId);
                    iPos++;
                }

            }

            //
            str = String.format("process_mediaQ.nQNodes %d", pProcInfo.processQ_media.m_var.uiQNodes);
            showInfo_open.qyFuncs.showState_open(0, "showTasks", str, CONST_stateType_tasks, 0, when, iPos, 0);
            iPos++;

            //
            ctxQmc.TMP_texes texes = pProcInfo.av.texes;
            ctxQmc.TMP_task_texes taskTexes = texes.taskAvTexes;
            int usCnt_activeMems = taskTexes.usCnt_activeMems;
            if (usCnt_activeMems > taskTexes.tti_peers.length) return iErr;
            str = "tti:";
            for (i = 0; i < usCnt_activeMems; i++) {
                ctxQmc.TMP_tex_info tti = taskTexes.tti_peers[i];
                if (tti.idInfo.ui64Id == 0) continue;
                str += "[" + i + "] " + tti.idInfo.ui64Id + "(" + tti.uiObjType + "), ";
            }
            showInfo_open.qyFuncs.showState_open(0, "showTasks", str, CONST_stateType_tasks, 0, when, iPos, 0);
            iPos++;

            showInfo_open.qyFuncs.showState_open(0, "showTasks", "", CONST_stateType_tasks, 0, when, -1, 0);


            return 0;
        }


        //
        public static int showState_players() {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            int i;
            myDb.CMyDb pDb = (myDb.CMyDb) pQyMc.gui.pDb;
            if (pDb == null) return -1;

            //
            qyCommProc.mySYSTEMTIME when = new qyCommProc.mySYSTEMTIME();
            tmpFunc.qyFuncs.myGetLocalTime(when);

            //
            int iPos = 0;
            //int i;
            String str;


            //
            qmcCommFunc_is_open.QY_PLAYER pPlayer = new qmcCommFunc_is_open.QY_PLAYER();
            qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio;
            qmcCommFunc_is_open.PLAY_VIDEO_procInfo pPlayVideo;

            //  comment: player是一个数组,在pProcInfo.av.pPlayers[]. pProcInfo是一个全局的结构
            //
            for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                pPlayer = pProcInfo.av.pPlayers[i];
                pPlayAudio = pProcInfo.av.pPlayers[i].audio;
                pPlayVideo = pProcInfo.av.pPlayers[i].video;

                if (!pPlayer.audio.bRunning && !pPlayer.video.bRunning) continue;

                str = "";
                str += String.format("player[%s] %s:", i, pPlayer.idInfo_recorder.ui64Id);

                if (pPlayer.audio.bRunning) {
                    int n = 0;
                    if (pProcInfo.cfg.b_useQ2_toPlayA) {
                        n = qyq2.qyFuncs.getQ2Nodes(pPlayAudio.q2);
                    } else {
                        n = pPlayAudio.qBuf2.getQ2Nodes();
                    }
                    str += String.format("a.qNodes %s", n);
                }

                //
                showInfo_open.qyFuncs.showState_open(0, "showPlayers", str, CONST_stateType_players, 0, when, iPos, 0);
                iPos++;

            }

            //
            showInfo_open.qyFuncs.showState_open(0, "showPlayers", "", CONST_stateType_players, 0, when, -1, 0);

            return 0;
        }


        //
        public static int showState_talks() {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            int i;

            //
            qyCommProc.mySYSTEMTIME when = new qyCommProc.mySYSTEMTIME();
            tmpFunc.qyFuncs.myGetLocalTime(when);

            //
            int iPos = 0;
            //int i;
            String str;

            for (i = 0; i < pMisCnt.talkingFriendQ.size(); i++) {
                Activity_dlgTalk dlgTalk = (Activity_dlgTalk) pMisCnt.talkingFriendQ.get(i).hWnd;
                qnmCommProc_mis.MSGR_ADDR addr = pMisCnt.talkingFriendQ.get(i).addr;

                //
                str = "";
                str += ("talk: " + addr.idInfo.ui64Id);
                showInfo_open.qyFuncs.showState_open(0, "showTalks", str, CONST_stateType_talks, 0, when, iPos, 0);
                iPos++;

                //
                str = "";
                str += "taskExists " + dlgTalk.m_var.av.taskInfo.bTaskExists + ",";
                str += "taskId " + dlgTalk.m_var.av.taskInfo.iTaskId;
                showInfo_open.qyFuncs.showState_open(0, "showTalks", str, CONST_stateType_talks, 0, when, iPos, 0);
                iPos++;


            }

            showInfo_open.qyFuncs.showState_open(0, "showTalks", "", CONST_stateType_talks, 0, when, -1, 0);

            return 0;
        }


        //
        public static int showState_qs() {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            int i;
            myDb.CMyDb pDb = (myDb.CMyDb) pQyMc.gui.pDb;
            if (pDb == null) return -1;

            //
            qyCommProc.mySYSTEMTIME when = new qyCommProc.mySYSTEMTIME();
            tmpFunc.qyFuncs.myGetLocalTime(when);

            //
            int iPos = 0;
            //int i;
            String str;


            str = "";
            str += String.format("qyImTaskTab: qNodes %s/maxQNodes %s", pDb.m_var_pQ_qyImTaskTab.uiQNodes, pDb.m_var_pQ_qyImTaskTab.cfg.uiMaxQNodes);
            showInfo_open.qyFuncs.showState_open(0, "showQs", str, CONST_stateType_qs, 0, when, iPos, 0);
            iPos++;


            showInfo_open.qyFuncs.showState_open(0, "showQs", "", CONST_stateType_qs, 0, when, -1, 0);

            return 0;
        }


        //
        public static int mainWnd_OnTimer(Object hMainWnd, qyMcMainWndProc.QY_MC_mainWndVar var, int nIDEvent) {
            int iErr = -1;


            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) return -1;

            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            if (pProcInfo.m_iCtxType != ctxQyTmpl.Consts.CONST_ctxType_qmc) {
                return -1;
            }
            //
            boolean bDbg = false;
            String str;

            var.loopCtrl++;

            // test
//            showInfo_open.qyFuncs.showInfo_open(0, "mainWnd_onTimer", String.valueOf(var.loopCtrl));


            {
                //
                if (0 == (var.loopCtrl % 5)) bChkMessengerChannelsReady(pQyMc);


                if (null != pProcInfo) {

                    if (0 != pProcInfo.waitToQuit.dwTickCnt_start) {
                        if (myGetTickCount(null) - pProcInfo.waitToQuit.dwTickCnt_start > pProcInfo.waitToQuit.dwTimeInMs) {
                            //qyFuncs.Log.e(("It's time to quit"));
                            notifyPanicToApp(pQyMc, -1, pProcInfo.waitToQuit.hint);
                        }
                    }
                    // fixme to change...
                    mainWnd_chk_tasks(hMainWnd, var);
                    qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                }

                //
                mainWnd_showStatus(var);

            }

            iErr = 0;
            return iErr;
        }

        public static int displayNotification(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_taskStatus pStatus) {
            int iErr = -1;
            //
            if (null == pProcInfo) return -1;

            //
            //pProcInfo->status.notification.dwTickCnt_lastDisplayed = GetTickCount();
            guiShare.GuiShare.pf_gui_displayNotification.call(pProcInfo, pStatus);

            iErr = 0;
            return 0;   //  iErr;
        }

        public static int qmcApplyForChkingTasks_gui(ctxQyMc.CCtxQyMc pQyMc)   //  2009/09/10
        {
            if (null != pQyMc) {
                guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_chkTask, 0);
            }
            return 0;
        }
    }
}




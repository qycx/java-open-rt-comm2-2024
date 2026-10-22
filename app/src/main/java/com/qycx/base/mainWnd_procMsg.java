package com.qycx.base;

import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.refreshTaskAvTexes;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.bDlgTalkActive;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.findTalker;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskProcReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvReplyInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReq;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_transferFile;
import static com.qycx.base.qmcTaskInfo.qyFuncs.freeTaskInfo;
import static com.qycx.base.qmcTaskInfo.qyFuncs.newTaskInfoIndex;
import static com.qycx.base.qmcTaskInfo.qyFuncs.qmcTaskInfo_setClosed;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_accept;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_send_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_getMsgContent;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_unknown;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgRouteTalkData;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgTalkData;
import static com.qycx.base.qyCommProc.qyFuncs.bTaskNeedAcception;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_acceptedByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_applyToRecv;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_applyToSend;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_autoCanceledByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_canceledByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_canceledBySender;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_dualByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_req;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_resp;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_waitToRecv;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_waitToSend;

import static com.qycx.base.tmpProc.qyFuncs.removeTask;

import com.qycx.Activity_dlgTalk;
import com.qycx.MainActivity;
import com.qycx.base.util.Log;

import java.lang.ref.WeakReference;

public class mainWnd_procMsg {
    public static class qyFuncs_Droid {
        public static int msgInput2Task(qnmCommProc_mis.MIS_MSG_INPUT pInput, qnmCommProc_mis.MIS_MSG_TASK pTask) {
            if (!isUcFlgTalkData(pInput.ucFlg) && !isUcFlgRouteTalkData(pInput.ucFlg))
                return -1;

            pTask.uiType = CONST_misMsgType_task;

            //
            pTask.uiCliIndex = pInput.uiCliIndex;                           //  input daemon
            //  2008/01/05, vDev_coms uiCliIndex
            pTask.uiSessionId = pInput.uiSessionId;
            pTask.uiStep = pInput.uiStep;                                //  2008/04/28

            pTask.ucbConvrted = pInput.ucbConvrted;                      //  input 2007/7/14

            pTask.ucbConvrted_msgRoute = pInput.ucbConvrted_msgRoute;             //  daemon input 
            pTask.lenInBytes_msgRouteInDataBuf = pInput.lenInBytes_msgRouteInDataBuf;        //  daemon input data.route route data.buf
            //  lenInBytes_msgRouteInDataBuf data.buf route

            //  mc MisCnt qmd uiCliIndex, 2007/06/10
            //Object pMisCnt;                          //  misCntµÄ, 2007/06/02
            pTask.addr_logicalPeer = pInput.addr_logicalPeer;                 //  messenger. 

            //
            pTask.ucFlg = pInput.ucFlg;
            //
            pTask.usCode = pInput.usCode;
            pTask.usCode_org = pInput.usCode_org;                          //  
            pTask.tStartTime = pInput.tStartTime;
            pTask.uiTranNo = pInput.uiTranNo;
            pTask.usSeqNo = pInput.usSeqNo;

            pTask.tStartTime_processed = pInput.tStartTime_processed;                //  iocp uiTranNo_mgr iocp
            pTask.uiTranNo_processed = pInput.uiTranNo_processed;
            pTask.nTimes_tryToSend = pInput.nTimes_tryToSend;                  //  mgr iocp 
            //
            pTask.tTime_alive = pInput.tTime_alive;                     //  2014/12/09. 当没有找到新的视频数据时，刷新此变量，也可以表明任务是活跃的
            //
            pTask.tTime_applyForChkTaskAlive = pInput.tTime_applyForChkTaskAlive;          //  2010/10/30
            pTask.nTimes_applyForChkTaskAlive = pInput.nTimes_applyForChkTaskAlive;      //  2014/12/09

            //
            pTask.uiTaskMode_unused = pInput.uiTaskMode_unused;                 //  2009/08/23

            //
            pTask.iTaskId = pInput.iTaskId;                            //  
            pTask.uiTaskType = pInput.uiTaskType;                            //  
            pTask.idInfo_taskSender = pInput.idInfo_taskSender;                  //  
            pTask.idInfo_taskReceiver = pInput.idInfo_taskReceiver;                //  
            pTask.iStatus = pInput.iStatus;                            //  2007/07/18
            pTask.tLastModifiedTime = pInput.tLastModifiedTime;                   //  2014/05/01

            //  
            pTask.tRecvTime = pInput.tRecvTime;                           //  
            pTask.dwTickCnt_start = pInput.dwTickCnt_start;                  //  ( ms ). 
            pTask.dwTickCnt_toSend = pInput.dwTickCnt_toSend;                 //  2011/01/22. start->toSendQ
            //  
            pTask.usElapsed_recved = pInput.usElapsed_recved;                    //  
            pTask.usElapsed_serv_startToProcess = pInput.usElapsed_serv_startToProcess;       //  mtMgrThreadProc_is 
            pTask.index_debugInfo = pInput.index_debugInfo;                    //  debugInfo index, debugInfo 2008/11/19

            //
            pTask.uiChannelType = pInput.uiChannelType;                     //  2008/04/17, 
            //
            pTask.uiChannelType_input = pInput.uiChannelType_input;               //  2017/10/03

            //
            pTask.lenInBytes = pInput.lenInBytes;
            //
            //pTask.data = pInput.data;       //  这里是错的，应该将数据

            //
            pTask.data.route.idInfo_from.ui64Id  =  pInput.data.route.idInfo_from.ui64Id;
            pTask.data.route.idInfo_to.ui64Id=pInput.data.route.idInfo_to.ui64Id;
            //
            switch (pInput.data.buf.uiType) {
                case  CONST_imCommType_transferAvInfo:  {
                      pTask.data.buf=new qnmCommProc_mis.TRANSFER_AV_INFO();
                      qnmCommProc_mis.TRANSFER_AV_INFO tai  =  (qnmCommProc_mis.TRANSFER_AV_INFO) pTask.data.buf;
                      //
                      Log.e("qy","这里是错的，应该将pInput.data.buf的数据考入pTask.data.buf. 暂时直接指向");
                      //
                      pTask.data.buf=pInput.data.buf;
                      }
                      break;
                default:
                        String str;
                        str  =  "msgInput2Task not finished, buf.uiType "  +  pInput.data.buf.uiType;
                        showInfo_open.qyFuncs.showInfo_open(str);
                        return  -1;
                        //
                        //break;
            }


            //
            return 0;
        }


        //
        public static ctxCaller.CTX_caller mainWnd_getCtxCaller(Object hMainWnd) {
            MainActivity mainActivity = (MainActivity) hMainWnd;
            return mainActivity.var_common.ctxCaller;
        }


        //
        public static int mainWnd_procMsgInput_transferAvInfo(Object hMainWnd, qyMcMainWndProc.QY_MC_mainWndVar var, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput) {
            int iErr = -1;

            //
            boolean bDbg = false;
            String str;

            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            MainActivity mainActivity = (MainActivity)pQyMc.gui.hMainWnd;
            int i;
            int index_taskInfo = -1;

            do {

                qnmCommProc_mis.IM_CONTENTU pContent = M_getMsgContent(pMsgInput.uiType, pMsgInput.ucFlg, pMsgInput.data);
                if (pContent.uiType != CONST_imCommType_transferAvInfo)
                    return iErr;

                //
                if (!pProcInfo.status.retrieveImObjList.ulbAllContactsRetrieved) {
                    Log.e("mainWnd_procMsgInput_transferAvInfo failed, ulbAllContactsRetrieved is false");
                    return iErr;
                }


                //
                if (pMsgInput.iTaskId == 0) {
                    Log.e("mainWnd_procMsgInput failed, iTaskId is 0");
                    return iErr;
                }

                for (i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
                    qmcTaskInfo.QMC_TASK_INFO e = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, i, "mainWnd_procMsgInput_transferAvInfo");
                    if (!e.bUsed) continue;
                    //
                    if (e.var.iTaskId == pMsgInput.iTaskId) {
                        {
                            break;
                        }
                    }
                }
                //
                if (i < pProcInfo.cfg.usMaxCnt_taskInfos) {
                    // skipped
                    iErr = 0;
                    return iErr;
                }

//            iIndex_taskInfo = newTaskInfoIndex(ctxCaller, pProcInfo, uiTaskDataType, hWnd_task, iTaskId_suggested, "procSendAvInfoFunc");
                index_taskInfo = newTaskInfoIndex(mainActivity.var_common.ctxCaller, pProcInfo, CONST_taskDataType_conf, null, pMsgInput.iTaskId, "mainWnd_procMsgInput_transferAvInfo");
                if (index_taskInfo < 0) {
                    str = ("mainWnd_procMsgInput_transferAvInfo.newTaskInfoIndex failed: too many tasks");
                    showInfo_open.qyFuncs.showInfo_open(str);
                    Log.e("qy", str);
                    //
                    return iErr;

                }

                qmcTaskInfo.QMC_TASK_INFO tts = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, index_taskInfo, "mainWnd_procMsgInput_transferAvInfo");
                if (tts == null) return iErr;
                qmcTaskInfo.QMC_taskData_common pTaskData = tts.getTaskData();
                if (pTaskData == null) return iErr;
                qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                {
                    //
                    if (0 != msgInput2Task(pMsgInput, pMsgTask)) {
                        showInfo_open.qyFuncs.showInfo_open("mainWnd_procMsgInput_transferAvInfo.msgInput2Task failed");
                        break;
                    }

                    //
                    test.testCls_Droid.testChk("mainWnd_procMsgInput_transferAInfo.198");

                    //
                    guiShare.GuiShare.pf_gui_procMsg.call(pMsgInput);

                    //
                    test.testCls_Droid.testChk("mainWnd_procMsgInput_transferAvInfo.199");

                    //
                    if (bTaskNeedAcception(pMsgTask.iStatus)) {
                        if (var.notifyTaskStatus.bExists_task && var.notifyTaskStatus.bAvCall) {
                            iErr = 0;
                            return iErr;
                        }
                        var.notifyTaskStatus.bExists_task = true;
                        var.notifyTaskStatus.bAvCall = true;
                        var.notifyTaskStatus.index_taskInfo = index_taskInfo;

                        //
                        test.testCls_Droid.testChk("mainWnd_procMsgInput_transferInfo.208");

                        //
                        guiShare.GuiShare.pf_gui_notify_chk.call();
                    }
                }

                //
                iErr = 0;

            } while ( false );

            //
            test.testCls_Droid.testChk("mainWnd_procMsgInput_transferAvInfo");



            return iErr;

        }

        public static int mainWnd_procMsgInput_transferAvReplyInfo(Object hMainWnd, qyMcMainWndProc.QY_MC_mainWndVar var, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput) {
            int iErr = -1;

            //
            boolean bDbg = false;
            String str;

            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            //
            myDb.QM_dbFuncs g_dbFuncs = pQyMc.p_g_dbFuncs;
            //
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            int i;
            int index_taskInfo = -1;

            qnmCommProc_mis.IM_CONTENTU pContent = M_getMsgContent(pMsgInput.uiType, pMsgInput.ucFlg, pMsgInput.data);
            if (pContent.uiType != CONST_imCommType_transferAvReplyInfo) return iErr;


            //
            str = String.format("mainWnd_procMsgInput_avReply enters: from %s", pMsgInput.data.route.idInfo_from.ui64Id);
            Object pDb = null;
            qnmDb.CQnmDb db = new qnmDb.CQnmDb();
            {

                int usRespCode = CONST_qyRc_unknown;

                //if (null == pCtx.pProcessQ) return iErr;

                if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                pDb = db.m_pDbMem.pDb;

                qyDefs.QY_MESSENGER_ID idInfo = pMisCnt.idInfo;
                qnmCommProc_mis.TRANSFER_AV_replyInfo transferAvReplyInfo = (qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent;
                qnmCommProc_mis.IM_TASK_RCD rcd = new qnmCommProc_mis.IM_TASK_RCD();
                if (!g_dbFuncs.pf_bGetImTaskRcdBySth.call(pDb, pQyMc.cfg.db.iDbType, false, "", idInfo, transferAvReplyInfo.tStartTime_org, transferAvReplyInfo.uiTranNo_org, 0, rcd)) {
                    return iErr;
                }

                qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                if (pProcInfo.processQ_media.qTraverse_mono(null, findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc, pMsgInput, param) <= 0) {
                    Log.i( "procMsgTask_mc_media failed: ");
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, NULL, "", "", ("%s err: "), qyGetDesByType1(getResTable(0,pQyMc->cusRes, CONST_resId_qyCommTypeTable), pContent->uiType));
                    //
                    //  postMsg2Mgr...
                    return iErr;
                }

                refreshTaskAvTexes(pMsgInput.iTaskId);
            }


            //
            iErr = 0;
            
            return iErr;

        }


        //
        public static int mainWnd_procMsgInput_transferFileReq(Object hMainWnd, qyMcMainWndProc.QY_MC_mainWndVar var, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput) {
            int iErr = -1;

            //
            boolean bDbg = false;
            String str;

            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            int i;
            int index_taskInfo = -1;

            qnmCommProc_mis.IM_CONTENTU pContent =qnmCommProc_mis.qyFuncs.M_getMsgContent(pMsgInput.uiType, pMsgInput.ucFlg, pMsgInput.data);
            if (pContent.uiType != CONST_imCommType_transferFileReq) return iErr;

            if (pMsgInput.iTaskId == 0) {
                //Log.d("mainWnd_procMsgInput failed, iTaskId is 0");
                return iErr;
            }

            for (i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
                qmcTaskInfo.QMC_TASK_INFO e = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, i, "mainWnd_procMsgInput_transferAvInfo");
                if (!e.bUsed) continue;
                //
                if (e.var.iTaskId == pMsgInput.iTaskId) {
                    {
                        break;
                    }
                }
            }
            //
            if (i < pProcInfo.cfg.usMaxCnt_taskInfos) {
                // skipped
                iErr = 0;
                return iErr;
            }


            index_taskInfo =qmcTaskInfo.qyFuncs.newTaskInfoIndex(mainActivity.var_common.ctxCaller, pProcInfo, CONST_taskDataType_transferFile, null, pMsgInput.iTaskId, "mainWnd_procMsgInput_transferFileReq");
            if (index_taskInfo < 0) {
                //Log.d("mainWnd_procMsgInput_transferFileReq failed: too many tasks");
                return iErr;

            }
            //            
            qmcTaskInfo.QMC_TASK_INFO tts = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, index_taskInfo, "mainWnd_procMsgInput_transferFileReq");
            if (tts == null) return iErr;
            qmcTaskInfo.QMC_taskData_common pTaskData = tts.getTaskData();
            if (pTaskData == null) return iErr;
            qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
            {
                msgInput2Task(pMsgInput, pMsgTask);

                guiShare.GuiShare.pf_gui_procMsg.call(pMsgInput);


            }

            iErr = 0;

            
            
            return iErr;

        }


        //
        //  检测活跃的视频会议是否已存在，以talkerSubtype_video是否存在为标准
        public static boolean bExists_activeTaskAv(int piTaskId_activeTaskAv, qyDefs.QY_MESSENGER_ID pIdInfo_peer) {
            boolean bRet = false;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            int index_taskInfo;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //
            for (index_taskInfo = 0; index_taskInfo < pProcInfo.cfg.usMaxCnt_taskInfos; index_taskInfo++) {
                pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, index_taskInfo, "");
                if (false == pTaskInfo.bUsed) continue;
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                qyDefs.QY_MESSENGER_ID idInfo_peer;
                qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                {
                    ;
                    if (pMsgTask.uiType != CONST_misMsgType_task) continue;
                    qnmCommProc_mis.IM_CONTENTU pContent = M_getMsgContent(pMsgTask.uiType, pMsgTask.ucFlg, pMsgTask.data);
                    if (pContent.uiType != CONST_imCommType_transferAvInfo)
                        continue;
                    //
                    if (!qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(null, pProcInfo, index_taskInfo))
                        continue;
                    //
                    idInfo_peer = pMsgTask.addr_logicalPeer.idInfo;
                }
                //
                Activity_dlgTalk dlgTalk = null;
                if (null == (dlgTalk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, idInfo_peer))) continue;
                //
                dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;
                if (m_var.av.taskInfo.bTaskExists) {
                    //
                    piTaskId_activeTaskAv = pTaskInfo.var.iTaskId;
                    pIdInfo_peer = idInfo_peer;
                    //
                    bRet = true;
                    break;
                }
                //
                continue;
            }

            
            return bRet;
        }


        //
        public static boolean isAutoAccepted(qmcTaskInfo.QMC_TASK_INFO pTaskInfo) {
            boolean bRet = false;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            int iTaskId = 0;
            qyDefs.QY_MESSENGER_ID idInfo_peer=new qyDefs.QY_MESSENGER_ID();
            idInfo_peer.ui64Id = 0;

            //
            if (bExists_activeTaskAv(iTaskId, idInfo_peer)) {
                return false;
            }

            //
            do {
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (pTaskData == null) break;
                if (pTaskData.msgU.uiType != CONST_misMsgType_task) break;

                if (pTaskData.uiType != CONST_taskDataType_conf) break;

                //
                qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                qnmCommProc_mis.MSGR_ADDR addr = task.addr_logicalPeer;
                Activity_dlgTalk hTalk = null;
                if ((hTalk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, addr.idInfo)) != null
                        && bDlgTalkActive(hTalk)) {

                    dlgTalkProc.DLG_TALK_var m_var = hTalk.m_var;
                    if (m_var.av.bNeedAvCall) {
                        bRet = true;
                        break;
                    }

                    //
                    if (m_var.av.bAutoCancel_lostVideo) {
                        long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
                        int iDiffInMs = (int) (dwTickCnt - m_var.av.dwTickCnt_autoCancel_lostVideo);
                        if (iDiffInMs < 30 * 60 * 1000) // 30分钟内保持视频会话
                        {
                            //
                            bRet = true;
                            break;
                        }
                    }
                }
            } while (false);

            //                            
            return bRet;
        }


        //
        public static int mainWnd_chk_tasks(Object hMainWnd, qyMcMainWndProc.QY_MC_mainWndVar var) {
            int iErr = -1;

            //
            //showInfo_open.qyFuncs.showInfo_open("mainWnd_chk_tasks");
            //Log.d("tn_msgInput","mainWnd_chk_tasks");


            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            //
            long uiTickCnt_now = tmpFunc.qyFuncs.myGetTickCount(null);
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = null;
            int i;
            int iElapseInMs = 0;


            boolean bChkToRemove = false;
            //
            if (0 == (var.loopCtrl % 10)) {
                bChkToRemove = true;
            }
            if (pProcInfo.m_bNeedToRemoveTaskAtOnce) {
                pProcInfo.m_bNeedToRemoveTaskAtOnce = false;
                bChkToRemove = true;
            }
            //
            if (bChkToRemove) {
                for (i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
                    pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, i, "mainWnd_chk_tasks");
                    if (!pTaskInfo.bUsed) continue;
                    qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                    if (pTaskData == null) continue;
                    //
                    qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                    {
                        if (pMsgTask.uiType != CONST_misMsgType_task) {
                            continue;
                        }

                        //
                        boolean bNeedRemoved = false;

                        //
                        if (pTaskInfo.var.bNeedRemoved_toRecover) {
                            bNeedRemoved = true;
                            //
                            Log.d( "mainWnd_chk_tasks.670 bNeedRemoved=true");

                        } else {
                            //if (qyFuncs.bTaskAlive(pMsgTask->iStatus)) continue;
                            if (qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(mainActivity.var_common.ctxCaller, pProcInfo, i))
                                continue;

                            //qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(mainActivity.var_common.ctxCaller, pProcInfo, i);
                            //
                            iElapseInMs = Math.abs((int) (uiTickCnt_now - pTaskInfo.var.dwTickCnt_recv_lastRefreshed1));
                            if (iElapseInMs > pProcInfo.cfg.usTimeoutInMs_waitIfNoData) {
                                bNeedRemoved = true;
                                //
                                Log.d( "mainWnd_chk_tasks.682 bNeedRemoved=true");
                            }
                        }

                        //
                        if (bNeedRemoved) {
                            //
                            Log.d( "mainWnd_chk_tasks: freeTaskInfo");
                            //
                            freeTaskInfo(mainActivity.var_common.ctxCaller, pProcInfo, i, "mainWnd_chk_tasks");
                        }
                    }

                }
            }

            if (var.notifyTaskStatus.bExists_task) {
                pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(var.ctxCaller, var.notifyTaskStatus.index_taskInfo, "mainWnd_chk_tasks");
                if (pTaskInfo == null
                        || !qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(var.ctxCaller, pProcInfo, var.notifyTaskStatus.index_taskInfo)) {
                    var.notifyTaskStatus.bExists_task = false;
                    guiShare.GuiShare.pf_gui_notify_cancel.call();
                }
            }

            if (var.notifyTaskStatus.bExists_task && var.notifyTaskStatus.bAvCall) {


                pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(var.ctxCaller, var.notifyTaskStatus.index_taskInfo, "mainWnd_chk_tasks");
                if (pTaskInfo == null
                        || !qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(var.ctxCaller, pProcInfo, var.notifyTaskStatus.index_taskInfo)) {
                    var.notifyTaskStatus.bExists_task = false;
                    guiShare.GuiShare.pf_gui_notify_cancel.call();
                } else {
                    //
                    if (isAutoAccepted(pTaskInfo)) {
                        MainActivity.gui_notify_doCmd.call(CONST_imOp_recv_accept);
                        //
                        iErr = 0;
                        return iErr;
                    }

                    //
                    if (var.processingAvTask.bTaskExists
                            && var.processingAvTask.index_taskInfo == var.notifyTaskStatus.index_taskInfo
                    ) {
                        iErr = 0;
                        return iErr;
                    }

                    //
                    iElapseInMs = Math.abs((int) (uiTickCnt_now - pTaskInfo.var.dwTickCnt_start));
                    if (iElapseInMs < pProcInfo.cfg.usTimeoutInMs_waitForProcessingOf_avTask) {
                        iErr = 0;
                        return iErr;
                    }

                    //Log.d("mainWnd_chk_tasks: timeout, to cancel task");

                    // to cancel task
                    do_cancelTask1(pTaskInfo.var.iTaskId, true, "timeout");

                    //
                    var.notifyTaskStatus.bExists_task = false;
                    guiShare.GuiShare.pf_gui_notify_cancel.call();
                }
            }

            //
            for (i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
                pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, i, "mainWnd_chk_tasks");
                if (!pTaskInfo.bUsed) continue;
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (pTaskData == null) continue;

                //
                if (!qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(mainActivity.var_common.ctxCaller, pProcInfo, i))
                    continue;

                //
                qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                {
                    if (pMsgTask.uiType != CONST_misMsgType_task) {
                        continue;
                    }

                    //
                    qnmCommProc_mis.IM_CONTENTU pContent = M_getMsgContent(pMsgTask.uiType, pMsgTask.ucFlg, pMsgTask.data);
                    if (null == pContent) return iErr;
                    //
                    if (pContent.uiType == CONST_imCommType_transferAvInfo) {

                        //
                        if (!bTaskNeedAcception(pMsgTask.iStatus)) continue;

                        //
                        var.notifyTaskStatus.index_taskInfo = i;
                        var.notifyTaskStatus.bExists_task = true;
                        var.notifyTaskStatus.bAvCall = true;

                        //
                        guiShare.GuiShare.pf_gui_notify_chk.call();

                        iErr = 0;
                        return iErr;
                    }
                    // 对其他任务，等待60秒后暂时都作废
                    iElapseInMs = Math.abs((int) (uiTickCnt_now - pTaskInfo.var.dwTickCnt_recv_lastRefreshed1));
                    if (iElapseInMs > pProcInfo.cfg.usTimeoutInMs_waitForProcessingOf_otherTask) {
                        do_cancelTask1(pTaskInfo.var.iTaskId, true, "timeout for nonAvTask");
                        continue;
                    }


                }
                continue;
            }

            //
            if (var.notifyTaskStatus.bExists_task) {
                guiShare.GuiShare.pf_gui_notify_cancel.call();
                var.notifyTaskStatus.bExists_task = false;

            }

            iErr = 0;
            

            return iErr;


        }

        //
        public static int do_cancelTask1(int iTaskId, boolean bAutoCancel, String hint) {

            int iErr = -1;
            boolean bDbg = false;
            String str;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            //TCHAR tBuf[256] = "";
            //char buf[256] = "";
            qySyncCommProc.CQyMalloc mallocObj=new qySyncCommProc.CQyMalloc();

            //if (!hDlg || !pParam || !pMsgElem) return -1;
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();

            long tStartTran = tmpFunc.qyFuncs.mytime();
            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

            int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(mainActivity.var_common.ctxCaller, pProcInfo, iTaskId);
            if (index_taskInfo < 0) return -1;

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, index_taskInfo, "do_cancelTask");
            if (pTaskInfo == null) return -1;

            //
            if (!pTaskInfo.bUsed) return 0;

            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return iErr;

            //
            qnmCommProc_mis.MIS_MSG_TASK pMsg = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
            {
                if (pMsg.uiType != CONST_misMsgType_task) {
                    return iErr;
                }

                qnmCommProc_mis.IM_CONTENTU pContent = (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
                int iStatus;
                int lenInBytes;


                Object pDb = null;
                qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                {
                    if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                    pDb = db.m_pDbMem.pDb;

                    switch (pContent.uiType) {
                        case CONST_imCommType_transferAvInfo:
                        case CONST_imCommType_transferFileReq:
                            switch (pMsg.iStatus) {
                                case CONST_imTaskStatus_req:
                                case CONST_imTaskStatus_applyToSend:
                                case CONST_imTaskStatus_waitToSend:
                                case CONST_imTaskStatus_acceptedByReceiver:
                                case CONST_imTaskStatus_dualByReceiver: {


                                    iStatus = CONST_imTaskStatus_canceledBySender;
                                    g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, pMsg.iTaskId);
                                    pMsg.iStatus = iStatus;        //  2012/05/15

                                    //  °ÑÏÔÊ¾×´Ì¬ÐÞ¸ÄÏÂ.2008/11/14
                                    //showTaskStatus(pMisCnt,pMgrVar->addr.idInfo,pMsg->idInfo_taskSender,pMsg->idInfo_taskReceiver, FALSE, pContent->uiType, pMsg->iTaskId, iStatus, 0, 0, "", (""));

                                    //  Í¨Öª¶Ô·½
                                    qnmCommProc_mis.TASK_PROC_REQ taskProcReq=new qnmCommProc_mis.TASK_PROC_REQ();
                                    //qyFuncs.mymemset((byte[])taskProcReq, 0, sizeof(TASK_PROC_REQ));
                                    taskProcReq.uiType = CONST_imCommType_taskProcReq;
                                    taskProcReq.usOp = CONST_imOp_send_cancel;
                                    taskProcReq.tStartTime_org = pMsg.tStartTime;
                                    taskProcReq.uiTranNo_org = pMsg.uiTranNo;
                                    taskProcReq.uiContentType_org = pContent.uiType;
                                    //
                                    lenInBytes=128;//sizeof(TASK_PROC_REQ);

                                    //  2015/09/08
                                    int uiChannelType = pMsg.uiChannelType;
                                    //
                                    uiChannelType = 0;
//                                    byte[] data = taskProcReq.toByteArray();

                                    if (0 != postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, pMsg.usCode, tStartTran, uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType,  taskProcReq, (int) lenInBytes, pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.idInfo_taskSender.ui64Id, pMsg.idInfo_taskSender.ui64Id, pMsg.idInfo_taskSender.ui64Id, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false))
                                        return iErr;
                                }
                                break;
                                case CONST_imTaskStatus_resp:
                                case CONST_imTaskStatus_applyToRecv:
                                case CONST_imTaskStatus_waitToRecv: {


                                    iStatus = bAutoCancel ? CONST_imTaskStatus_autoCanceledByReceiver : CONST_imTaskStatus_canceledByReceiver;
                                    //Log.d("##941" + iStatus);
                                    g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, pMsg.iTaskId);
                                    pMsg.iStatus = iStatus;        //  2012/05/15

                                    //  °ÑÏÔÊ¾×´Ì¬ÐÞ¸ÄÏÂ.2008/11/14
                                    //showTaskStatus(pMisCnt,pMgrVar->addr.idInfo,pMsg->idInfo_taskSender,pMsg->idInfo_taskReceiver, FALSE, pContent->uiType, pMsg->iTaskId, iStatus, 0, 0, "", (""));

                                    //  Í¨Öª¶Ô·½
                                    qnmCommProc_mis.TASK_PROC_REQ taskProcReq=new qnmCommProc_mis.TASK_PROC_REQ();
                                    //qyFuncs.mymemset((byte[])taskProcReq, 0, sizeof(TASK_PROC_REQ));
                                    taskProcReq.uiType = CONST_imCommType_taskProcReq;
                                    taskProcReq.usOp = CONST_imOp_recv_cancel;
                                    taskProcReq.tStartTime_org = pMsg.tStartTime;
                                    taskProcReq.uiTranNo_org = pMsg.uiTranNo;
                                    taskProcReq.uiContentType_org = pContent.uiType;
                                    //
                                    lenInBytes=128;//sizeof(TASK_PROC_REQ);

                                    //  2015/09/08
                                    int uiChannelType = pMsg.uiChannelType;
                                    //
                                    uiChannelType = 0;
//                                    byte[] data = taskProcReq.toByteArray();
                                    if (0 != postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, pMsg.usCode, tStartTran, uiTranNo, (short) 0, pMsg.iTaskId, 0,  taskProcReq, (int) lenInBytes, pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.idInfo_taskSender.ui64Id, pMsg.idInfo_taskSender.ui64Id, pMsg.idInfo_taskSender.ui64Id, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false))
                                        return iErr;
                                }
                                break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                }


            }
            //
            pTaskInfo.var.dwTickCnt_recv_lastRefreshed1 = tmpFunc.qyFuncs.myGetTickCount(null);

            iErr = 0;
            return iErr;
        }


        public static int do_closeTask1(int iTaskId, boolean bAutoCancel, String hint) {

            int iErr = -1;
            boolean bDbg = false;
            String str;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            //TCHAR tBuf[256] = "";
            //char buf[256] = "";
            qySyncCommProc.CQyMalloc mallocObj=new qySyncCommProc.CQyMalloc();

            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();

            long tStartTran = 0;
            int uiTranNo = 0;

            int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(mainActivity.var_common.ctxCaller, pProcInfo, iTaskId);
            if (index_taskInfo < 0) return -1;

            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, index_taskInfo, "do_closeTask");
            if (pTaskInfo == null) return -1;

            //
            if (!pTaskInfo.bUsed) return 0;

            //
            str = String.format("qyFuncs_Droid.do_closeTask enters, index_taskInfo %d. %s", index_taskInfo, hint);
            Log.d( "do_closeTask1: "+str);

            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return iErr;

            //
            qnmCommProc_mis.MIS_MSG_TASK pMsg = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
            {
                if (pMsg.uiType != CONST_misMsgType_task) {
                    return iErr;
                }

                qnmCommProc_mis.IM_CONTENTU pContent =  (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
                int iStatus;
                int lenInBytes;


                Object pDb = null;
                qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                {
                    if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                    pDb = db.m_pDbMem.pDb;

                    switch (pContent.uiType) {
                        case CONST_imCommType_transferAvInfo:
                        case CONST_imCommType_transferFileReq:
                            switch (pMsg.iStatus) {
                                case CONST_imTaskStatus_req:
                                case CONST_imTaskStatus_applyToSend:
                                case CONST_imTaskStatus_waitToSend:
                                case CONST_imTaskStatus_acceptedByReceiver:
                                case CONST_imTaskStatus_dualByReceiver: {


                                    iStatus = CONST_imTaskStatus_canceledBySender;
                                    g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, pMsg.iTaskId);
                                    pMsg.iStatus = iStatus;        //  2012/05/15

                                    //  °ÑÏÔÊ¾×´Ì¬ÐÞ¸ÄÏÂ.2008/11/14
                                    //showTaskStatus(pMisCnt,pMgrVar->addr.idInfo,pMsg->idInfo_taskSender,pMsg->idInfo_taskReceiver, FALSE, pContent->uiType, pMsg->iTaskId, iStatus, 0, 0, "", (""));

                                }
                                break;
                                case CONST_imTaskStatus_resp:
                                case CONST_imTaskStatus_applyToRecv:
                                case CONST_imTaskStatus_waitToRecv: {


                                    iStatus = bAutoCancel ? CONST_imTaskStatus_autoCanceledByReceiver : CONST_imTaskStatus_canceledByReceiver;
                                    //Log.d("##1091" + iStatus);

                                    g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, pMsg.iTaskId);
                                    pMsg.iStatus = iStatus;        //  2012/05/15

                                    //  °ÑÏÔÊ¾×´Ì¬ÐÞ¸ÄÏÂ.2008/11/14
                                    //showTaskStatus(pMisCnt,pMgrVar->addr.idInfo,pMsg->idInfo_taskSender,pMsg->idInfo_taskReceiver, FALSE, pContent->uiType, pMsg->iTaskId, iStatus, 0, 0, "", (""));

                                }
                                break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                }

                Object hTalk;
                if (null != (hTalk = findTalker(pMisCnt, pMsg.addr_logicalPeer.idInfo))) {
                    Activity_dlgTalk dlgTalk;
                    if (hTalk instanceof WeakReference) {
                        dlgTalk = ((WeakReference<Activity_dlgTalk>) hTalk).get();
                    } else {
                        dlgTalk = (Activity_dlgTalk) hTalk;
                    }
//                    Activity_dlgTalk dlgTalk = ((WeakReference<Activity_dlgTalk>) hTalk).get();
                    dlgTalk.do_closeTask(index_taskInfo);
                } else {
                    mainWnd_do_closeTask(index_taskInfo);
                }
            }
            //
            pTaskInfo.var.dwTickCnt_recv_lastRefreshed1 = tmpFunc.qyFuncs.myGetTickCount(null);

            iErr = 0;

            return iErr;
        }


        //
        public static int mainWnd_do_closeTask(int index_taskInfo) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, index_taskInfo, "mainWnd_do_closeTask");
            if (pTaskInfo == null) return -1;

            removeTask(mainActivity.var_common.ctxCaller, pTaskInfo.var.iTaskId);
            qmcTaskInfo_setClosed(mainActivity.var_common.ctxCaller, pProcInfo, index_taskInfo, "mainWnd_do_closeTask");

            iErr = 0;
            
            return iErr;

        }


        //
        public static int mainWnd_procTask(int index_taskInfo, int usOp) {
            int iErr = -1;


            iErr = 0;
            
            return iErr;

        }


    }
}


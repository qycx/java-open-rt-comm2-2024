


package com.qycx.base;

import static com.qycx.base.DlgTalk.qyFuncs_Droid.refreshTalkToInfo_mgr;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.chkResources;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.dlgTalk_closeTaskAv;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.dlgTalk_displayAvStatus;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.isTalkerShadowMgr;
import static com.qycx.base.dlgTalk_capImgProc.qyFuncs_Droid.dlgTalk_freeCapImageBySth_mgr;
import static com.qycx.base.dlgTalk_capImgProc.qyFuncs_Droid.dlgTalk_freeCapImageByTaskId_me_mgr;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskProcReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReq;
import static com.qycx.base.qmcFunc_isCli.qyFuncs.msg2TaskRcd;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_dual_permit;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recvFile_saveas;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_accept;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_deny;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_send_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_send_permit;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_autoCanceledByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_canceledByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_canceledBySender;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_deniedByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_req;
import static com.qycx.base.qyMcMainWndProc_isCli.qyFuncs_Droid.qmcApplyForChkingTasks_gui;

import com.qycx.Activity_dlgTalk;
import com.qycx.MainActivity;
import com.qycx.base.util.Log;

public class dlgTalk_procTask {
    public static class qyFuncs_Droid {
        public static int dlgTalk_procTask1(Activity_dlgTalk hCurDlgTalk, int iTaskId, short usOp, String paramStrBuf, dlgTalkPublic.PARAM_dlgTalk_procTask pParam) {
            int iErr = -1;
            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) {
                Log.e("pQyMc == null");
                return -1;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            //
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            boolean bDbg = false;
            String str;
            if (null == hCurDlgTalk) return -1;
            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar_cur = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                ;
                dlgTalkProc.DLG_TALK_var pCurVar = hCurDlgTalk.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar_cur.getVar(hCurDlgTalk);
                if (null == pCurVar) return -1;

                Activity_dlgTalk hDlgTalk = hCurDlgTalk;

                //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
                {
                    ;
                    dlgTalkProc.DLG_TALK_var pm_var = hDlgTalk.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlgTalk);
                    if (null == pm_var) return -1;
                    dlgTalkProc.DLG_TALK_var m_var = pm_var;
                    //
                    int lenInBytes = 0;
                    //
                    Object pDb = null;
                    int iDbType = pQyMc.cfg.db.iDbType;
                    qySyncCommProc.CQyMalloc mallocObj_pRcd = new qySyncCommProc.CQyMalloc();
                    {
                        ;
                        qnmCommProc_mis.IM_TASK_RCD pRcd = new qnmCommProc_mis.IM_TASK_RCD();// (qnmCommProc_mis.IM_TASK_RCD) mallocObj_pRcd.mallocf(sizeof(IM_TASK_RCD));
                        if (null == pRcd) return -1;
                        qnmCommProc_mis.IM_TASK_RCD rcd = pRcd;
                        qnmCommProc_mis.IM_CONTENTU pContent = null;
                        //
                        String pT;
                        //
                        //TCHAR tBuf[256];
                        int nItem;
                        //TCHAR sqlBuf[CONST_maxSqlBufLen + 1] = "";
                        //  DWORD							dwProcessId;
                        int iStatus = 0;
                        int iStatus_old = 0;

                        int i;

                        //
                        if (0 == iTaskId) return -1;

                        //  2012/05/01
                        if (!isTalkerShadowMgr(m_var.addr)) return -1;
                        //TALKER_shadow_mgr pShadowMgr = (TALKER_shadow_mgr)m_var.pShadowInfo;
                        //if (!pShadowMgr) return -1;


                        qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                        {
                            ;

                            if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                            pDb = db.m_pDbMem.pDb;

                            int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(mainActivity.var_common.ctxCaller, pProcInfo, iTaskId);
                            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, index_taskInfo, "dlgTalk_procTask1");
                            if (pTaskInfo == null) return iErr;
                            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                            if (pTaskData == null) return iErr;

                            //
                            qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                            {
                                if (pMsgTask.uiType != CONST_misMsgType_task) {
                                    return iErr;
                                }

                                msg2TaskRcd(pMisCnt, pMsgTask, 0, pRcd);
                                pContent = (qnmCommProc_mis.IM_CONTENTU) pMsgTask.data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsgTask.data.buf;

                                switch (usOp) {
                                    case CONST_imOp_send_permit: {                     //
                                        if (rcd.uiContentType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {

                                            if (pProcInfo.processQ_media.qTraverse(mainActivity.var_common.ctxCaller, findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc, pMsgTask, 0) <= 0) {
                                                return iErr;
                                            }
                                        }

                                        iStatus = CONST_imTaskStatus_req;
                                        //Log.d(TAG,"##175  " + iStatus);
                                        //

                                        //
                                        lenInBytes = (int) (pMsgTask.lenInBytes);// System.Runtime.InteropServices.Marshal.OffsetOf < qnmCommProc_mis > ("buf"));
                                        if (lenInBytes < 0) return iErr;
                                        //
                                        //  2015/09/07
                                        int uiChannelType = pMsgTask.uiChannelType;
                                        //  2015/09/07
                                        uiChannelType = 0;
                                        //
                                        qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = m_var.addr.idInfo;
                                        qyDefs.QY_MESSENGER_ID idInfo_dst = rcd.idInfo_recv;
//                                        if (0 != postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, pMsgTask.usCode, pMsgTask.tStartTime, pMsgTask.uiTranNo, (short) 0, pMsgTask.iTaskId, pMsgTask.uiTaskType,pMsgTask.data.buf.array(), (int) lenInBytes, idInfo_logicalPeer,
//                                        pMsgTask.idInfo_taskSender,pMsgTask.idInfo_taskReceiver,
//                                        idInfo_dst, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false))return iErr;

                                    }
                                    break;
                                    case CONST_imOp_recv_accept:
                                    case CONST_imOp_recvFile_saveas:
                                    case CONST_imOp_dual_permit:

                                        if (0 != dlgTalk_procTask_recv_accept(hCurDlgTalk, pCurVar, hDlgTalk, pm_var, iTaskId, usOp, paramStrBuf, rcd, pMsgTask, pContent, iStatus, iStatus_old, g_dbFuncs, pDb, pParam))
                                            return iErr;

                                        break;

                                    case CONST_imOp_recv_deny:
                                    case CONST_imOp_recv_cancel: {

                                        if (usOp == CONST_imOp_recv_deny) {
                                            iStatus = CONST_imTaskStatus_deniedByReceiver;
                                        } else {
                                            if (pParam.bAutoCancel) {
                                                iStatus = CONST_imTaskStatus_autoCanceledByReceiver;
                                            } else {
                                                iStatus = CONST_imTaskStatus_canceledByReceiver;
                                                //Log.d(TAG,"##234");
                                            }
                                        }
                                        //
                                        iStatus_old = pMsgTask.iStatus;
                                        //
                                        if (0 != g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, rcd.id))
                                            return iErr;
                                        pMsgTask.iStatus = iStatus;    //  2009/10/17
                                        pMsgTask.tLastModifiedTime = tmpFunc.qyFuncs.mytime();
                                        ;   //  2014/05/01

                                        qnmCommProc_mis.TASK_PROC_REQ taskProcReq = new qnmCommProc_mis.TASK_PROC_REQ();
                                        qyDefs.QY_MESSENGER_ID idInfo_dst = new qyDefs.QY_MESSENGER_ID();

                                        //qyFuncs.mymemset((byte[])taskProcReq, 0, sizeof(TASK_PROC_REQ));
                                        taskProcReq.uiType = CONST_imCommType_taskProcReq;
                                        taskProcReq.usOp = usOp;
                                        taskProcReq.tStartTime_org = rcd.tSendTime;
                                        taskProcReq.uiTranNo_org = rcd.uiTranNo;
                                        taskProcReq.uiSerialNo_org = rcd.uiSerialNo;
                                        taskProcReq.uiContentType_org = rcd.uiContentType;

                                        //  2014/11/22
                                        idInfo_dst.ui64Id = rcd.idInfo_send.ui64Id;
                                        //
                                        lenInBytes = 128;//sizeof(TASK_PROC_REQ);
                                        long tStartTran = tmpFunc.qyFuncs.mytime();
                                        int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
                                        qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = m_var.addr.idInfo;

//                                        byte[] data = taskProcReq.toByteArray();
                                        int len = 0;//
                                        if (0 != postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, pMsgTask.usCode, tStartTran, uiTranNo, (short) 0, iTaskId, rcd.uiMode, taskProcReq, len, idInfo_logicalPeer.ui64Id,
                                                pMsgTask.idInfo_taskSender.ui64Id, pMsgTask.idInfo_taskReceiver.ui64Id,
                                                idInfo_dst.ui64Id, pMsgTask.uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false))
                                            return iErr;

                                        //  2009/02/20
                                        if (m_var.av.taskInfo.bTaskExists) {
                                            if (m_var.av.taskInfo.iTaskId == rcd.id) {
                                                dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask1.recv_cancel");
                                            }
                                        }
                                        //  2013/11/18

                                        //  2010/12/23
                                        chkResources(hDlgTalk, true);

                                        //  2012/03/24
                                        qmcApplyForChkingTasks_gui(pQyMc);
                                        //
                                    }
                                    //
                                    break;
                                    case CONST_imOp_send_cancel: {

                                        iStatus = CONST_imTaskStatus_canceledBySender;
                                        iStatus_old = pMsgTask.iStatus;
                                        //
                                        if (0 != g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, rcd.id))
                                            return iErr;
                                        pMsgTask.iStatus = iStatus;    //  2009/10/17
                                        pMsgTask.tLastModifiedTime = tmpFunc.qyFuncs.mytime();
                                        ;   //  2014/05/01

                                        qnmCommProc_mis.TASK_PROC_REQ taskProcReq = new qnmCommProc_mis.TASK_PROC_REQ();
                                        //qyFuncs.mymemset((byte[])taskProcReq, 0, sizeof(TASK_PROC_REQ));
                                        taskProcReq.uiType = CONST_imCommType_taskProcReq;
                                        taskProcReq.usOp = usOp;
                                        taskProcReq.tStartTime_org = rcd.tSendTime;
                                        taskProcReq.uiTranNo_org = rcd.uiTranNo;
                                        taskProcReq.uiSerialNo_org = rcd.uiSerialNo;
                                        taskProcReq.uiContentType_org = rcd.uiContentType;
                                        //
                                        lenInBytes = 128;//sizeof(TASK_PROC_REQ);
                                        long tStartTran = tmpFunc.qyFuncs.mytime();
                                        int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
                                        //  2015/09/07
                                        int uiChannelType = pMsgTask.uiChannelType;
                                        //  2015/09/07
                                        uiChannelType = 0;
                                        //
                                        qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = m_var.addr.idInfo;
                                        qyDefs.QY_MESSENGER_ID idInfo_dst = rcd.idInfo_recv;
//                                        byte[] data = taskProcReq.toByteArray();
                                        int len = 0;
                                        if (0 != postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, pMsgTask.usCode, tStartTran, uiTranNo, (short) 0, iTaskId, rcd.uiMode, taskProcReq, 0, idInfo_logicalPeer.ui64Id,
                                                pMsgTask.idInfo_taskSender.ui64Id, pMsgTask.idInfo_taskReceiver.ui64Id,
                                                idInfo_dst.ui64Id, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false)) {
                                            return iErr;
                                        }

                                        //
                                        //  freeCapImageByTaskId(  hDlgTalk,  0,  rcd.id,  &m_var.av.peerImages  );
                                        dlgTalk_freeCapImageBySth_mgr(hDlgTalk, rcd.id, null);
                                        int idc = 0;// m_var.av.idc_me;  //  0
                                        dlgTalk_freeCapImageByTaskId_me_mgr(hDlgTalk, idc, rcd.id);


                                        //  2009/02/20
                                        if (m_var.av.taskInfo.bTaskExists) {
                                            if (m_var.av.taskInfo.iTaskId == iTaskId) {
                                                dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask1.send_cancel");
                                                //  2014/10/09
                                                dlgTalk_capImgProc.qyFuncs_Droid.dlgTalk_freeCapImages_bgWall_mgr(hDlgTalk, 0);
                                            }
                                        }

                                        //  2011/10/10
                                        chkResources(hDlgTalk, true);

                                        //  2012/03/24
                                        qmcApplyForChkingTasks_gui(pQyMc);
                                    }
                                    break;

                                    default:
                                        return iErr;
                                }
                            }
                        }
                    }

                    iErr = 0;


                    dlgTalk_displayAvStatus(hDlgTalk, m_var, 0, 0, null);

                    if (0 != iErr) {
                    }
                    dyn_isCliD3d.qyFuncs.dyn_d3dWall_refreshWallMemByTalker(hDlgTalk);
                    refreshTalkToInfo_mgr(hDlgTalk);
                }
            }

            return iErr;

        }


        //
        public static int dlgTalk_procTask_recv_accept(Object hCurDlgTalk, dlgTalkProc.DLG_TALK_var pCurVar, Object hMgr, dlgTalkProc.DLG_TALK_var pMgrVar, int iTaskId, short usOp, String paramStrBuf,
                                                       qnmCommProc_mis.IM_TASK_RCD rcd, qnmCommProc_mis.MIS_MSG_TASK pMsgTask, qnmCommProc_mis.IM_CONTENTU pContent,
                                                       int iStatus, int iStatus_old,
                                                       myDb.QM_dbFuncs g_dbFuncs,
                                                       Object pDb,
                                                       //TALKER_shadow_mgr pShadowMgr,
                                                       dlgTalkPublic.PARAM_dlgTalk_procTask pParam
        ) {
            int iErr = -1;

            Object hDlgTalk = hMgr;
            dlgTalkProc.DLG_TALK_var m_var = pMgrVar;

            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            int i;
            int lenInBytes;

            switch (rcd.uiContentType) {
                case CONST_imCommType_transferFileReq:
                    return dlgTalk_procTask_transferFileReq.qyFuncs_Droid.dlgTalk_procTask_transferFileReq(hCurDlgTalk, pCurVar, hMgr, pMgrVar, iTaskId, usOp, paramStrBuf, rcd, pMsgTask, pContent, iStatus, iStatus_old, g_dbFuncs, pDb);
                case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                    return dlgTalk_procTask_transferAvInfo.qyFuncs_Droid.dlgTalk_procTask_transferAvInfo(hCurDlgTalk, pCurVar, hMgr, pMgrVar, iTaskId, usOp, paramStrBuf, rcd, pMsgTask, pContent, iStatus, iStatus_old, g_dbFuncs, pDb, pParam);
                default:
                    break;
            }


            iErr = 0;

            return iErr;

        }

    }
}

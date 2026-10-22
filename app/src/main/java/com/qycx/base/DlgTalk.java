
package com.qycx.base;


import static com.qycx.base.Consts.CONST_channelType_robot;
import static com.qycx.base.Consts.MAX_PATH;
import static com.qycx.base.Consts.cntof_fileName;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.tmpHandler_showMsg;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.dlgTalk_qPostMsg;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReq;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc;
import static com.qycx.base.qmcFunc_isCli.qyFuncs.msg2TaskRcd;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_transferFile;
import static com.qycx.base.qmcTaskInfo.qyFuncs.newTaskInfoIndex;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendRobotTask;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.newTaskId;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_talkData;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_unknown;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_sendFile;
import static com.qycx.base.qyDefs.Consts.CONST_objType_imGrp;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_waitToSend;


import static com.qycx.base.tmpFunc.qyFuncs.safeTcsnCpy;
import static com.qycx.base.tmpFunc_Droid.qyFuncs_Droid.getDirAndFinalName;

import com.qycx.base.util.Log;


public class DlgTalk {

    public static class qyFuncs_Droid {
        public static int refreshTalkToInfo_mgr(Object hDlgTalk_mgr) {
            return -1;
        }

        public static int dlgTalk_doLayout(Object hDlgTalk) {
            return -1;
        }


        public static int dlgTalk_procTask_display_op_mgr(Object hDlgTalk_mgr, int iTaskId, int usOp, qnmCommProc_mis.MIS_MSG_TASK pMsgTask, qnmCommProc_mis.IM_TASK_RCD rcd, int iStatus_old, int iStatus, String paramStrBuf) {
            return -1;
        }

        public static int procSendFile(String fullFilePath, String fileName, Object pPROC_offlineRes_u, Object m_hWnd, dlgTalkProc.DLG_TALK_var pDLG_TALK_var, int piTaskId) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            funcsForIsCliHelp.FUNCS_for_isCliHelp pFuncs = get_funcs_for_isCliHelp.qyFuncs.QY_GET_FUNCS_for_isCliHelp(pProcInfo);
            if (null == pFuncs) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //
            dlgTalkProc.DLG_TALK_var pm_var = (dlgTalkProc.DLG_TALK_var) pDLG_TALK_var;
            if (null == pm_var) return -1;
            dlgTalkProc.DLG_TALK_var m_var = pm_var;

            //QY_MC pQyMc = QY_GET_GBUF();
            //IHTMLDocument2 pDoc = NULL;
            //TCHAR tBuf[256] = "";
            long ui64FileLen = 0;
            qySyncCommProc.CQyMalloc mallocObj;

            if (null == fullFilePath) return -1;

            ui64FileLen = tmpFunc_Droid.qyFuncs_Droid.getFileLen(fullFilePath);

            if (ui64FileLen == 0) {
                showTaskStatusFunc.qyFuncs.showNotification_open(0, 0, 0, ("send failed, fileLen is 0"));
                return -1;
            }

            int iTaskId = 0;

            {
                Object pDb = null;
                qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                {
                    if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return 0;
                    pDb = db.m_pDbMem.pDb;

                    iTaskId = newTaskId(pDb, pQyMc.cfg.db.iDbType);
                }
            }
            int iTaskId_suggested = iTaskId;

            int iIndex_taskInfo = -1;

            int uiTaskDataType = 0;
            uiTaskDataType = CONST_taskDataType_transferFile;
            iIndex_taskInfo = newTaskInfoIndex(null, pProcInfo, uiTaskDataType, m_hWnd, iTaskId_suggested, ("procSendFile"));
            if (iIndex_taskInfo < 0) {
                Log.e( "procSendAvInfoFunc faile, newTaskInfoIndex failed");
                return 0;
            }
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, iIndex_taskInfo, "");
            if (pTaskInfo == null) return 0;

            long tStartTran = tmpFunc.qyFuncs.mytime();
            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

            //  ÏÈ·ÅÈëÊý¾Ý¿âÖÐ£¬·¢±éÖØ·¢ºÍ¼ÇÂ¼×´Ì¬
            qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();
            //qnmCommProc_mis.MIS_MSG_TASK				*	pMsg	=	(  qnmCommProc_mis.MIS_MSG_TASK  *  )mallocObj.mallocf(  sizeof(  qnmCommProc_mis.MIS_MSG_TASK  )  );
            qnmCommProc_mis.MIS_MSG_TASK pMsg = (qnmCommProc_mis.MIS_MSG_TASK) taskData.msgU;
            {
                if (null == pMsg) return 0;
                qnmCommProc_mis.TRANSFER_FILE_REQ pContent;
                int lenInBytes;
                qnmCommProc_mis.IM_TASK_RCD rcd=new qnmCommProc_mis.IM_TASK_RCD();
                //
                pMsg.uiType = CONST_misMsgType_task;
                //
                //pMsg->pMisCnt = m_var.pMisCnt;
                //
                pMsg.ucFlg = CONST_commFlg_talkData;
                //
                pMsg.usCode = CONST_qyCmd_sendRobotTask;
                pMsg.tStartTime = tStartTran;
                pMsg.uiTranNo = uiTranNo;
                //
                pContent = (qnmCommProc_mis.TRANSFER_FILE_REQ) pMsg.data.buf;// (qnmCommProc_mis.TRANSFER_FILE_REQ) pMsg.data.buf;
                //qyFuncs.mymemset(pContent, 0, sizeof(TRANSFER_FILE_REQ));
                pContent.uiType = CONST_imCommType_transferFileReq;
                pContent.fullFilePath=fullFilePath;//, MAX_PATH + 1);
                if (null != fileName && fileName.length() != 0) {
                    safeTcsnCpy(fileName, pContent.fileName, cntof_fileName);
                } else {
                    String dirName = null;
                    String finalFileName = null;
                    if (0 != getDirAndFinalName(pContent.fullFilePath, dirName, finalFileName))
                        return 0;
                    safeTcsnCpy(finalFileName, pContent.fileName, cntof_fileName);
                }
                pContent.ui64FileLen = ui64FileLen;
                //  2014/07/13
                int uiSizePerSnd_internal = 0;
                if (pProcInfo.processQ_media.isEmpty())
                    uiSizePerSnd_internal = pProcInfo.cfg.uiSizePerSnd_file_noAv;
                else uiSizePerSnd_internal = pProcInfo.cfg.uiSizePerSnd_file_avExists;
                pContent.uiSizePerSnd_suggest = uiSizePerSnd_internal;

                taskAv.TASK_transferFile task = new taskAv.TASK_transferFile();
                //memset(task, 0, sizeof(task));
                task.m_index_taskInfo = iIndex_taskInfo;

                qnmCommProc_mis.PROC_TASK_FILE pContent_myTask = task.myTask;


                //  2009/10/20
                pContent_myTask.hWnd_task = m_hWnd;

                //  2007/07/18
                pMsg.uiTaskType = CONST_imTaskType_sendFile;
                //
                pMsg.idInfo_taskSender.ui64Id = pMisCnt.idInfo.ui64Id;
                pMsg.idInfo_taskReceiver.ui64Id = m_var.addr.idInfo.ui64Id;
                //
                pMsg.iStatus = CONST_imTaskStatus_waitToSend;


                //  ÕâÀïµÄ¸³ÖµËÆºõÓ¦ÔÚ·¢ËÍÊ±Ê¹ÓÃ, 2007/07/24
                pMsg.data.route.idInfo_from.ui64Id = pMsg.idInfo_taskSender.ui64Id;
                pMsg.data.route.idInfo_to.ui64Id = pMsg.idInfo_taskReceiver.ui64Id;
                //

                lenInBytes =128;//sizeof(TRANSFER_FILE_REQ);
                pMsg.lenInBytes =lenInBytes;// (int) System.Runtime.InteropServices. + lenInBytes;

                if (null == msg2TaskRcd(pMisCnt, pMsg, CONST_qyRc_unknown, rcd))
                    return 0;

                {
                    Object pDb = null;
                    qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                    {
                        if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return 0;
                        pDb = db.m_pDbMem.pDb;
                        //
                        if (storeTaskInDb.qyFuncs.storeTaskInDb(pDb, pQyMc.cfg.db.iDbType, pMisCnt, iTaskId_suggested, rcd, false, "") < 0)
                            return 0;
                    }
                }

                if (0 != iTaskId_suggested) {       //  2014/10/29
                    if (rcd.id != iTaskId_suggested) {
                        return 0;
                    }
                }

                qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = m_var.addr.idInfo;

                //
                pMsg.iTaskId = rcd.id;
                pMsg.uiTaskMode_unused = rcd.uiMode;
                pMsg.uiChannelType = CONST_channelType_robot;  //  2009/10/17

                //
                pMsg.addr_logicalPeer.idInfo = idInfo_logicalPeer;

                //
                dlgTalkProc.PARAM_PROC_ROW param = new dlgTalkProc.PARAM_PROC_ROW();
                //qyFuncs.mymemset((byte[])param, 0, sizeof(param));
                param.bScrollIntoView = true;
                param.bNewMsg = true;
                if (0 != tmpHandler_showMsg(m_hWnd, param, pMsg)) return 0;

                if (0 != dlgTalk_qPostMsg.call(m_hWnd, pMsg, pMsg.lenInBytes))
                    return 0;

                if (m_var.addr.uiObjType == CONST_objType_imGrp) {
                    pContent.bGrpTask = true; //  2007/09/05, ¸³ÉÏ×é±êÊ¶
                }

                if (0 != pProcInfo.processQ_media.qPostMsg(task, 0)) {
                    Log.e( "procMsgTask_mc_robot: 1. qPostMsg failed");
                    return 0;
                }


                //  mgr
                int uiChannelType = CONST_channelType_robot;
                //
                uiChannelType = 0;


//                byte[] data = pContent.toByteArray();
                int len = 0;//
                qyDefs.QY_MESSENGER_ID idInfo_dst = m_var.addr.idInfo;
                if (0 != postMsgTask2Mgr_mc(null, pMisCnt, CONST_misMsgType_task, (byte) 0, (short) CONST_qyCmd_sendRobotTask, tStartTran, uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType,  pContent, len, idInfo_logicalPeer.ui64Id, pMsg.idInfo_taskSender.ui64Id, pMsg.idInfo_taskSender.ui64Id, idInfo_dst.ui64Id, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false))
                    return 0;

                if (0 != piTaskId) piTaskId = rcd.id;
            }

            return 0;
        }


    }
}


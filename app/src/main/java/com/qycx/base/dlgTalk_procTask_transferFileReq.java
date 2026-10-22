


package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_robot;
import static com.qycx.base.Consts.MAX_PATH;
import static com.qycx.base.DlgTalk.qyFuncs_Droid.dlgTalk_procTask_display_op_mgr;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.chkResources;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_queryCustomerServiceOfficerReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReplyReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReq;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendRobotTaskReply;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_dual_permit;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recvFile_saveas;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_accept;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_content;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_waitToRecv;
import static com.qycx.base.tmpProc.qyFuncs.bSupported_fileServer;

public class dlgTalk_procTask_transferFileReq {

    public static class qyFuncs_Droid {


        public static int dlgTalk_procTask_transferFileReq(Object hCurDlgTalk, dlgTalkProc.DLG_TALK_var pCurVar, Object hMgr, dlgTalkProc.DLG_TALK_var pMgrVar, int iTaskId, short usOp, String paramStrBuf,
                                                           qnmCommProc_mis.IM_TASK_RCD rcd, qnmCommProc_mis.MIS_MSG_TASK pMsgTask_unused, qnmCommProc_mis.IM_CONTENTU pContent,
                                                           int iStatus, int iStatus_old,
                                                           myDb.QM_dbFuncs g_dbFuncs,
                                                           Object pDb //,
                                                           //TALKER_shadow_mgr pShadowMgr
        ) {
            int iErr = -1;

            Object hDlgTalk = hMgr;
            dlgTalkProc.DLG_TALK_var m_var = pMgrVar;

            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            int i;
            int lenInBytes;

            String str;

            //
            if (null == pMsgTask_unused) {
                return -1;
            }

            //
            if (rcd.uiContentType != CONST_imCommType_transferFileReq) return -1;

            //
            int iIndex_taskInfo = -1;

            iIndex_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(null, pProcInfo, iTaskId);
            if (iIndex_taskInfo < 0) {
                return iErr;

            }
            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, iIndex_taskInfo, "");
            qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();
            qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) taskData.msgU;
            {
                if (pMsgTask.uiType != CONST_misMsgType_task) {
                    return iErr;
                }
                if (pMsgTask.iTaskId != iTaskId) {
                    return -1;
                }


                //
                switch (usOp) {
                    case CONST_imOp_recv_accept:
                    case CONST_imOp_recvFile_saveas:
                    case CONST_imOp_dual_permit: {

                        //
                        boolean ucbSendLocalAv = false;
                        int iIndex_sharedObj = -1;
                        int iIndex_sharedObjUsr = -1;
                        qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
                        int iIndex_capBmp = -1;
                        qmcSharedObj.CAP_procInfo_video pCapBmp = null;
                        int iIndex_capAudio = -1;
                        qmcSharedObj.CAP_procInfo_audioCommon pCapAudio = null;
                        int iIndex_shmCmd = -1;
                        //QM_SHM_CMD pShmCmd = null;
                        boolean bFirstUsr = false;
                        qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = null;
                        qyGuiCommProc.TALKER_policy talkerPolicy=new qyGuiCommProc.TALKER_policy();

                        //  2011/08/08
                        //AV_COMPRESSOR_CFG avCompressor_reply;       //  响应要取得compressor, 目前只有av才有需要. 2012/02/16
                        //  if  (  myGetAvCompressorCfg(  CONST_capType_av,  0,  CONST_capUsage_conf,  0,  &avCompressor_reply  )  )  goto  errLabel;
                        //if (0!=myGetAvCompressorCfg(CONST_capType_av, 0, 0, 0,  avCompressor_reply)) return iErr;    //  2014/08/15

                        DlgSelectAvCompressor.qyFuncs_Droid.getTalkerPolicy(m_var.addr.idInfo, talkerPolicy);

                        if (rcd.uiContentType == CONST_imCommType_transferFileReq) {
                            String pathCStr = "";
                            //TCHAR tFileBuf[256] = "";
                            //
                            if (bSupported_fileServer(pQyMc)) {
                            } else {
                                //pathCStr = CQyString(pProcInfo->cfg.policy.dirToSaveFile) + CQyString(rcd.subject);
                                String pDir = pProcInfo.cfg.policy.dirToSaveFile;
                                {
                                    String psubject = rcd.subject;
                                    {
                                        //
                                        str = new String(pDir);
                                        //
                                        pathCStr = "" + new String(pDir) + new String(psubject);
                                    }
                                }
                            }
                            if (usOp == CONST_imOp_recvFile_saveas) {

                                return iErr;
                            }
                            qnmCommProc_mis.TRANSFER_FILE_REQ transferFileReq = (qnmCommProc_mis.TRANSFER_FILE_REQ) pContent;
                            tmpFunc.qyFuncs.safeTcsnCpy(pathCStr, transferFileReq.fullFilePath, MAX_PATH + 1);
                            rcd.content=pathCStr;
                        } else if (rcd.uiContentType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {
                            //
                        } else if (rcd.uiContentType == CONST_imCommType_queryCustomerServiceOfficerReq) {
                            ;
                        } else {
                            return iErr;
                        }

                        //
                        //if  (  !(  pMsgTask  =  findTaskInProcessQ(  &m_var,  iTaskId,  0  )  )  )  goto  errLabel;
                        if (null == pMsgTask) return iErr;

                        //
                        iStatus = CONST_imTaskStatus_waitToRecv;
                        iStatus_old = pMsgTask.iStatus;
                        //
                        if (0 != g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, rcd.id))
                            return iErr;
                        pMsgTask.iStatus = iStatus;

                        //
                        dlgTalk_procTask_display_op_mgr(hDlgTalk, iTaskId, usOp, pMsgTask, rcd, iStatus_old, iStatus, paramStrBuf);

                        //
                        if (rcd.uiContentType == CONST_imCommType_transferFileReq) {
                            /**/
                            taskAv.TASK_transferFile task = new taskAv.TASK_transferFile();
                            //memset(task, 0, sizeof(task));
                            //
                            task.m_index_taskInfo = iIndex_taskInfo;
                            pTaskInfo.var.m_bReplyTask = true;
                            //
                            qnmCommProc_mis.TRANSFER_FILE_REPLYREQ transferFileReplyReq=new qnmCommProc_mis.TRANSFER_FILE_REPLYREQ();
                            qnmCommProc_mis.PROC_TASK_FILE transferFileReplyReq_myTask = task.myTask;

                            //
                            //qyFuncs.mymemset((byte[])transferFileReplyReq, 0, sizeof(TRANSFER_FILE_REPLYREQ));
                            transferFileReplyReq.uiType = CONST_imCommType_transferFileReplyReq;
                            transferFileReplyReq.usOp = usOp;
                            transferFileReplyReq.tStartTime_org = rcd.tSendTime;
                            transferFileReplyReq.uiTranNo_org = rcd.uiTranNo;
                            transferFileReplyReq.uiSerialNo_org = rcd.uiSerialNo;

                            qnmCommProc_mis.TRANSFER_FILE_REQ transferFileReq = (qnmCommProc_mis.TRANSFER_FILE_REQ) pContent;
                            task.myTask.ui64FileLen = transferFileReq.ui64FileLen;    //  _tcstol(  rcd.propStrs[0],  &pT,  10  );
                            //_sntprintf(task.myTask.fileName_transferred, mycountof(task.myTask.fileName_transferred), ("%s"), rcd.subject);
                            String psubject = rcd.subject;
                            {
                                task.myTask.fileName_transferred = new String(psubject);
                            }
                            String pcontent = rcd.content;
                            {
                                //_sntprintf(task.myTask.fullFilePath_save, mycountof(task.myTask.fullFilePath_save), ("%s"), rcd.content);
                                task.myTask.fullFilePath_save = new String(pcontent);
                            }
                            //  2014/07/13
                            int uiSizePerSnd_internal = 0;
                            if (pProcInfo.processQ_media.isEmpty())
                                uiSizePerSnd_internal = pProcInfo.cfg.uiSizePerSnd_file_noAv;
                            else uiSizePerSnd_internal = pProcInfo.cfg.uiSizePerSnd_file_avExists;
                            task.myTask.uiSizePerSnd_internal = (int) Math.min(uiSizePerSnd_internal, transferFileReq.uiSizePerSnd_suggest); //  PDEFAULT_usSizePerSnd_file;
                            //
                            transferFileReplyReq_myTask.hWnd_task = hDlgTalk;       //  2009/10/20
                            //
                            lenInBytes=128;//sizeof(TRANSFER_FILE_REPLYREQ);
                            long tStartTran = tmpFunc.qyFuncs.mytime();
                            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
                            task.myTask.reply_info.tStartTime_reply = tStartTran;
                            task.myTask.reply_info.uiTranNo_reply = uiTranNo;
                            task.myTask.reply_info.replyReq = transferFileReplyReq;

                            //
                            if (0 != pProcInfo.processQ_media.qPostMsg(task, 0)) {
                                return iErr;
                            }

                            //
                            int uiChannelType = CONST_channelType_robot;
                            //
                            uiChannelType = 0;
                            //
                            qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = m_var.addr.idInfo;
                            qyDefs.QY_MESSENGER_ID idInfo_dst = rcd.idInfo_send;
//                            byte[] data = transferFileReplyReq.toByteArray();
                            int len = 0;
                            if (0 != postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc((ctxCaller.CTX_caller) null, pMisCnt, CONST_misMsgType_task, (byte) 0, (short) CONST_qyCmd_sendRobotTaskReply, tStartTran, uiTranNo, (short) 0, iTaskId, rcd.uiMode,  transferFileReplyReq, len, idInfo_logicalPeer.ui64Id, pMsgTask.idInfo_taskSender.ui64Id, pMsgTask.idInfo_taskReceiver.ui64Id, idInfo_dst.ui64Id, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false))
                            return iErr;

                        } else if (rcd.uiContentType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {

                            //
                        } else if (rcd.uiContentType == CONST_imCommType_queryCustomerServiceOfficerReq) {       //  2011/04/08

                        } else {
                            return iErr;
                        }
                        //

                        //  2010/12/24
                        chkResources(hDlgTalk, false);

                    }

                    break;
                    default:
                        break;
                }
            }

            iErr = 0;
            
            return iErr;

        }


    }


}

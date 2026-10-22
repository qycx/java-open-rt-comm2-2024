


package com.qycx.base;

import static com.qycx.base.Consts.*;
import static com.qycx.base.dlgProgress.qyFuncs.notifyProgressEnd;
import static com.qycx.base.findOrgMsgAndProcess_is.qyFuncs.b_tmpHandler_findOrgMsgAndProc_is;
import static com.qycx.base.findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc;
import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.imCommType_defs.Consts.*;
import static com.qycx.base.isTmpFunc.qyFuncs.printMisMsg;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.procMsgTask2Output_mc;
import static com.qycx.base.qisChannel.qyFuncs.startChannel;
import static com.qycx.base.qmcFunc_isCli.qyFuncs.msg2TaskRcd;
import static com.qycx.base.qnmCommProc.Consts.*;
import static com.qycx.base.qnmCommProc_mis.Consts.*;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.MACRO_makeImSimpleResp;
import static com.qycx.base.qyComm.Consts.*;
import static com.qycx.base.qyComm.qyFuncs.isRcOk;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgResp;
import static com.qycx.base.qyCommProc.Consts.*;
import static com.qycx.base.qyCommProc.qyFuncs.CONST_qyRcTable;
import static com.qycx.base.qyCommProc.qyFuncs.bTaskAlive;
import static com.qycx.base.qyCusResPublic.Consts.*;
import static com.qycx.base.qyDefs.Consts.CONST_qyTimeLen;
import static com.qycx.base.qyDefs_open.Consts.*;

import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.getIdInfo_logicalPeer;
import static com.qycx.base.qyWmComm.Consts.*;
import static com.qycx.base.showTaskStatusFunc.qyFuncs.showTaskStatus;
import static com.qycx.base.traceLog.qyFuncs.getTimelStr;

import com.qycx.base.util.Log;


public class qyMcMainProc_isCli_mgr {

    public static class Consts {
        public final static int CONST_nIntervalInMs_tryToResumeVideoTask = 200;        //  5000		//  2014/10/13

    }


    public static class qyFuncs {


        //
        public static int procMsgInput_mc_mgr(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_INPUT pMsg, int lenInBytes_msg,
                                              boolean bMsgInput, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;

            int dataLen = 0;
            //int uiMsgLen = (int)Marshal.OffsetOf<qnmCommProc_mis.MIS_MSG_TASK>("data") + (int)Marshal.OffsetOf<qnmCommProc_mis>("buf");
            //qnmCommProc_mis.IM_CONTENTU pContent = (qnmCommProc_mis.IM_CONTENTU)pMsg->data.buf;
            //  CWnd				*	pMainWnd			=	AfxGetApp(  )->m_pMainWnd;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;

            //  IM_TASK_RCD			rcd;
            int tmpiRet;
            //
            int lenInBytes = 0;
            qnmCommProc_mis.IM_SIMPLE_RESP respSimple = new qnmCommProc_mis.IM_SIMPLE_RESP();
            int usRespCode = CONST_qyRc_unknown;
            int uiChannelType = CONST_channelType_talking;
            //
            qyMcMainDaemon_isCli.TMP_timeBuf timeBuf = new qyMcMainDaemon_isCli.TMP_timeBuf();

            //char timeBuf[CONST_qyTimeLen + 1] = "";
            //
            boolean bRemoveIfErr = true;       //  ÔÚÔËÐÐ³ö´íÊ±ÊÇ·ñÉ¾³ýÏà¹Ø½Úµã¡£
            //
            //  TCHAR					sqlBuf[CONST_maxSqlBufLen  +  1]	=	(  ""  );
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;

            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();

            if (null == pQyMc.gui.hMainWnd) return -1;
            if (null == pMisCnt) return -1;

            if (pMsg.addr_logicalPeer.idInfo.ui64Id == 0) {   //  Ç°ÃæÃ»ÓÐ¸³ÖµµÄ¶¼ÊÇÊÕµ½µÄÏûÏ¢
                if (0 != getIdInfo_logicalPeer(pMisCnt, pMsg.data.route, pMsg.addr_logicalPeer)) {
                    switch (pMsg.uiTaskType) {
                        case CONST_imTaskType_shareDynBmp:
                        case CONST_imTaskType_shareGps:
                            break;
                        default: {
                            showInfo_open.qyFuncs.showInfo_open( ("procMsgTask_mc_mgr: getIdInfo_logicalPeer failed."));
                            return iErr;
                        }
                        
                    }
                }
            }

            if (!isUcFlgResp(pMsg.ucFlg)) {

                switch (pMsg.usCode) {
                    case CONST_qyCmd_sendMedia: {       //  
                        //
                        if (0 != procMsgInput_mc_media_mgr(pQyMc, pProcInfo, pMsg, lenInBytes_msg, bMsgInput, pCtx, pChannel)) {
                            return iErr;
                        }

                        //
                        iErr = 0;
                        return iErr;
                    }

                    case CONST_qyCmd_sendRobotTask:
                    case CONST_qyCmd_sendRobotTaskReply:
                    case CONST_qyCmd_sendRobotTaskData: {
                        //
                        if (0 != procMsgInput_mc_robot_mgr(pQyMc, pProcInfo, pMsg, lenInBytes_msg, bMsgInput, pCtx, pChannel)) {
                            return iErr;
                        }
                        //
                        iErr = 0;
                        return iErr;

                    }
                    

                    default:
                        break;
                }

                return iErr;


            } else {
                //
                //printMisMsg(null, null, (qnmCommProc_mis.MIS_MSGU) pMsg);

                if (!isRcOk(pMsg.usCode)) return iErr;
            }


            iErr = 0;

            

            if (0 != iErr) {
            }

            return iErr;
        }


        public static int procMsgTask_mc_mgr(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_TASK pMsg, int lenInBytes_msg, boolean bMsgInput, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            int dataLen = 0;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;
            int tmpiRet;
            int lenInBytes = 0;
            qnmCommProc_mis.IM_SIMPLE_RESP respSimple = new qnmCommProc_mis.IM_SIMPLE_RESP();
            int usRespCode = CONST_qyRc_unknown;
            int uiChannelType = CONST_channelType_talking;
            //
            qyMcMainDaemon_isCli.TMP_timeBuf timeBuf = new qyMcMainDaemon_isCli.TMP_timeBuf();
            boolean bRemoveIfErr = true;       //  ÔÚÔËÐÐ³ö´íÊ±ÊÇ·ñÉ¾³ýÏà¹Ø½Úµã¡£
            //
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;

            if (null == pQyMc.gui.hMainWnd) return -1;
            if (null == pMisCnt) return -1;

            if (pMsg.addr_logicalPeer.idInfo.ui64Id == 0) {
                if (0 != getIdInfo_logicalPeer(pMisCnt, pMsg.data.route, pMsg.addr_logicalPeer)) {
                    switch (pMsg.uiTaskType) {
                        case CONST_imTaskType_shareDynBmp:
                        case CONST_imTaskType_shareGps:
                            break;
                        default: {
                            showInfo_open.qyFuncs.showInfo_open( ("procMsgTask_mc_mgr: getIdInfo_logicalPeer failed."));
                            return iErr;
                        }
                    }
                }
            }


            if (!isUcFlgResp(pMsg.ucFlg)) {

                //
                switch (pMsg.usCode) {
                    case CONST_qyCmd_sendMedia: {       //  
                        //
                        if (0 != procMsgTask_mc_media_mgr(pQyMc, pProcInfo, pMsg, lenInBytes_msg, bMsgInput, pCtx, pChannel)) {
                            return iErr;
                        }

                        //
                        iErr = 0;
                        return iErr;
                    }
                    case CONST_qyCmd_sendRobotTask:
                    case CONST_qyCmd_sendRobotTaskReply:
                    case CONST_qyCmd_sendRobotTaskData: {
                        //
                        if (0 != procMsgTask_mc_robot_mgr(pQyMc, pProcInfo, pMsg, lenInBytes_msg, bMsgInput, pCtx, pChannel)) {
                            return iErr;
                        }
                        //
                        iErr = 0;
                        return iErr;

                    }

                    default:
                        break;
                }


                //  2015/09/07
                if (!bMsgInput) {

                    if (0 != procMsgTask2Output_mc(pProcInfo, pMisCnt, pMsg, lenInBytes_msg, pChannel, ("thread_mgr"))) {
                        return iErr;
                    }

                    iErr = 0;
                    return iErr;

                }

                return iErr;


            } else {

                if (!bMsgInput) {

                    if (0 != procMsgTask2Output_mc(pProcInfo, pMisCnt, pMsg, lenInBytes_msg, pChannel, ("thread_mgr"))) {
                        return iErr;
                    }

                    iErr = 0;
                    return iErr;
                }
                if (!isRcOk(pMsg.usCode)) return iErr;

            }


            iErr = 0;

            

            if (0 != iErr) {
            }

            return iErr;
        }


        //
        public static int procMsgInput_mc_media_mgr(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_INPUT pMsg, int lenInBytes_msg, boolean bMsgInput, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            //
            int dataLen = 0;
            //int uiMsgLen = (int)Marshal.OffsetOf<qnmCommProc_mis.MIS_MSG_TASK>("data") + (int)Marshal.OffsetOf<qnmCommProc_mis>("buf");
            qnmCommProc_mis.IM_CONTENTU pContent =  (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
            //  CWnd				*	pMainWnd			=	AfxGetApp(  )->m_pMainWnd;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;

            //  IM_TASK_RCD			rcd;
            int tmpiRet;
            //
            int lenInBytes = 0;
            qnmCommProc_mis.IM_SIMPLE_RESP respSimple = new qnmCommProc_mis.IM_SIMPLE_RESP();
            Short usRespCode = CONST_qyRc_unknown;
            int uiChannelType = CONST_channelType_talking;
            //char timeBuf[CONST_qyTimeLen + 1] = "";
            qyMcMainDaemon_isCli.TMP_timeBuf timeBuf = new qyMcMainDaemon_isCli.TMP_timeBuf();
            //
            boolean bRemoveIfErr = true;       //  ÔÚÔËÐÐ³ö´íÊ±ÊÇ·ñÉ¾³ýÏà¹Ø½Úµã¡£
            //
            //  TCHAR					sqlBuf[CONST_maxSqlBufLen  +  1]	=	(  ""  );
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();

            if (null == pQyMc.gui.hMainWnd) return -1;
            if (null == pMisCnt) return -1;

            if (pMsg.addr_logicalPeer.idInfo.ui64Id == 0) {   //  Ç°ÃæÃ»ÓÐ¸³ÖµµÄ¶¼ÊÇÊÕµ½µÄÏûÏ¢
                if (0 != getIdInfo_logicalPeer(pMisCnt, pMsg.data.route, pMsg.addr_logicalPeer)) {
                    switch (pMsg.uiTaskType) {
                        case CONST_imTaskType_shareDynBmp:
                        case CONST_imTaskType_shareGps:
                            break;
                        default: {
                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, NULL, "", "", ("procMsgTask_mc_mgr: getIdInfo_logicalPeer failed."));
                            return iErr;
                        }
                        
                    }
                }
            }

            if (!isUcFlgResp(pMsg.ucFlg)) {

                if (bMsgInput && pMsg.usCode == CONST_qyCmd_sendMedia) {

                    //  2009/10/18
                    pMsg.uiChannelType = pChannel.uiType;

                    switch (pContent.uiType) {
                        case imCommType_defs.Consts.CONST_imCommType_transferAvInfo: {
                            Object pDb = null;
                            qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                            {
                                usRespCode = CONST_qyRc_unknown;
                                qySyncCommProc.CQyMalloc mallocObj_rcd = new qySyncCommProc.CQyMalloc();
                                {
                                    qnmCommProc_mis.IM_TASK_RCD pRcd = new qnmCommProc_mis.IM_TASK_RCD();// (qnmCommProc_mis.IM_TASK_RCD) mallocObj_rcd.mallocf(sizeof(IM_TASK_RCD));
                                    if (null == pRcd) return iErr;

                                    if (null == pCtx.pProcessQ) return iErr;

                                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;

                                    //
                                    //
                                    if (transferAvInfo.uiTaskType == CONST_imTaskType_shareDevice_grp) {
                                        if (pProcInfo.uiTerminalType == CONST_terminalType_conf) {
                                            //
                                            //traceLog((TCHAR*)_T("对会议终端，强制改成会议任务"));
                                            //
                                            transferAvInfo.uiTaskType = CONST_imTaskType_transferAvInfo;
                                            transferAvInfo.shareDevice_grp_index = 0;
                                        }
                                    }

                                    //
                                    pMsg.uiTaskType = transferAvInfo.uiTaskType;
                                    pMsg.iStatus = CONST_imTaskStatus_resp;
                                    //Log.d("##805 " + pMsg->iStatus);

                                    //  2009/09/07
                                    if (transferAvInfo.uiTaskType == CONST_imTaskType_viewDynBmp) {

                                        if (0 != qPostMsg(pMsg, pQyMc.gui.processQ)) {
                                            return iErr;
                                        }
                                        guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                        iErr = 0;
                                        return iErr;
                                    }

                                    //  2014/11/21. 这里要把代理的信息更新到起始任务里。
                                    if (transferAvInfo.proxiedTranInfo.idInfo.ui64Id !=0 && 0 != transferAvInfo.proxiedTranInfo.uiObjType) {
                                        if (transferAvInfo.proxiedTranInfo.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {

                                            qyMcMainProc.PARAM_findOrgMsg param=new qyMcMainProc.PARAM_findOrgMsg();
                                            //mymemset((byte[])param, 0, sizeof(PARAM_findOrgMsg));
                                            param.bNeedUpdate = true;

                                            tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg,  param);

                                            iErr = 0;
                                            return iErr;
                                        }
                                    }

                                    //
                                    if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys))
                                        return iErr;
                                    pDb = db.m_pDbMem.pDb;

                                    //
                                    if (null == msg2TaskRcd(pMisCnt, pMsg, usRespCode, pRcd)) {
                                        return iErr;
                                    }
                                    tmpiRet = storeTaskInDb.qyFuncs.storeTaskInDb(pDb, pQyMc.cfg.db.iDbType, pMisCnt, 0, pRcd, false, "procMsgInput_mc_media_mgr");
                                    if (tmpiRet < 0) {
                                        return iErr;
                                    }
                                    //  //  2008/06/04
                                    if (tmpiRet > 0) {   //  rcd.iStatus
                                        tmpiRet = pCtx.pProcessQ.qTraverse_mono(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, null);
                                        if (tmpiRet < 0) return iErr;
                                        if (tmpiRet > 0) {
                                            iErr = 0;
                                            return iErr;
                                        }
                                        //  
                                        if (pRcd.iStatus != pMsg.iStatus && !bTaskAlive(pRcd.iStatus)) {
                                            if (0 != g_dbFuncs.pf_updateTaskStatus.call(pDb, pMsg.iStatus, pRcd.id))
                                                return iErr;
                                        }

                                    }

                                    //  2007/07/01						   
                                    pMsg.iTaskId = pRcd.id;
                                    pMsg.uiTaskMode_unused = pRcd.uiMode;

                                    //
                                    if (0 != qPostMsg(pMsg, pQyMc.gui.processQ)) {
                                        return iErr;
                                    }
                                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                                }
                            }

                            iErr = 0;
                            return iErr;

                        }
                        
                        case CONST_imCommType_transferAvReplyInfo: {
                            Object pDb = null;
                            qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                            {

                                usRespCode = CONST_qyRc_unknown;

                                if (null == pCtx.pProcessQ) return iErr;

                                if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                                pDb = db.m_pDbMem.pDb;

                                qnmCommProc_mis.TRANSFER_AV_replyInfo transferAvReplyInfo = (qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent;
                                if (transferAvReplyInfo.usOp != CONST_imOp_recv_accept) {
                                    Log.i( "procMsgTask_mc_media, usSp is not accept");
                                    return iErr;
                                }
                                //
                                if (transferAvReplyInfo.usOp == CONST_imOp_recv_accept) {
                                    if (0 != qPostMsg(pMsg, pQyMc.gui.processQ)) {
                                        return iErr;
                                    }
                                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                } else {
                                    //Log.e("pContent->uiType is %d", pContent->uiType);
                                    return iErr;
                                }

                                //  2013/02/13
                                if (0 != transferAvReplyInfo.uiTranNo_org) {
                                    if (transferAvReplyInfo.uiTranNo_org == pProcInfo.status.progress.uiTranNo) {
                                        notifyProgressEnd(transferAvReplyInfo.uiTranNo_org, 0);
                                    }
                                }

                                startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferAvReplyInfo"));

                                usRespCode = CONST_qyRc_ok;

                                MACRO_makeImSimpleResp(pMsg.usCode, pContent.uiType, 0, respSimple);
//                                byte[] data = respSimple.toByteArray();
                                int len = 0 ;//(uint)sizeof(IM_SIMPLE_RESP)
                                postMsgTask2Mgr_mc(pCtx.ctxCaller, pMisCnt, CONST_misMsgType_outputTask, (byte) CONST_commFlg_resp, (Short) usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType,  respSimple, len, pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.idInfo_taskSender.ui64Id, pMsg.idInfo_taskReceiver.ui64Id, pMsg.data.route.idInfo_from.ui64Id, pChannel.uiType, (qnmCommProc_mis.MIS_MSGU) null, false);

                            }

                            iErr = 0;
                            return iErr;

                        }
                        

                        default:
                            return iErr;
                        
                    }

                }   //
                return iErr;


            } else {

                //printMisMsg(null, null, (qnmCommProc_mis.MIS_MSGU) pMsg);

                if (!isRcOk(pMsg.usCode)) return iErr;

            }


            iErr = 0;

            

            if (0 != iErr) {
            }

            return iErr;
        }


        //
        public static int procMsgTask_mc_media_mgr(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_TASK pMsg, int lenInBytes_msg, boolean bMsgInput, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            //
            int dataLen = 0;
            //int uiMsgLen = (int)Marshal.OffsetOf<qnmCommProc_mis.MIS_MSG_TASK>("data") + (int)Marshal.OffsetOf<qnmCommProc_mis>("buf");
            qnmCommProc_mis.IM_CONTENTU pContent =  (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
            //  CWnd				*	pMainWnd			=	AfxGetApp(  )->m_pMainWnd;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;

            //  IM_TASK_RCD			rcd;
            int tmpiRet;
            //
            int lenInBytes = 0;
            qnmCommProc_mis.IM_SIMPLE_RESP respSimple = new qnmCommProc_mis.IM_SIMPLE_RESP();
            short usRespCode = CONST_qyRc_unknown;
            int uiChannelType = CONST_channelType_talking;
            //char timeBuf[CONST_qyTimeLen + 1] = "";
            qyMcMainDaemon_isCli.TMP_timeBuf timeBuf = new qyMcMainDaemon_isCli.TMP_timeBuf();
            //
            boolean bRemoveIfErr = true;       //  ÔÚÔËÐÐ³ö´íÊ±ÊÇ·ñÉ¾³ýÏà¹Ø½Úµã¡£
            //
            //  TCHAR					sqlBuf[CONST_maxSqlBufLen  +  1]	=	(  ""  );
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();

            //
            if (null == pQyMc.gui.hMainWnd) return -1;
            if (null == pMisCnt) return -1;

            if (pMsg.addr_logicalPeer.idInfo.ui64Id == 0) {   //  Ç°ÃæÃ»ÓÐ¸³ÖµµÄ¶¼ÊÇÊÕµ½µÄÏûÏ¢
                if (0 != getIdInfo_logicalPeer(pMisCnt, pMsg.data.route, pMsg.addr_logicalPeer)) {
                    switch (pMsg.uiTaskType) {
                        case CONST_imTaskType_shareDynBmp:
                        case CONST_imTaskType_shareGps:
                            break;
                        default: {
                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, NULL, "", "", ("procMsgTask_mc_mgr: getIdInfo_logicalPeer failed."));
                            return iErr;
                        }

                    }
                }
            }

            if (!isUcFlgResp(pMsg.ucFlg)) {

                if (!bMsgInput && pMsg.usCode == CONST_qyCmd_sendMedia) {       //  Ö÷¶¯·¢³ö
                    switch (pContent.uiType) {

                        case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                            qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                            if (transferAvInfo.tmp_usSubtype != CONST_transferAvInfo_subtype_task) {
                                return iErr;
                            }
                            //TRANSFER_AV_task pTransferAvTask; pTransferAvTask = (TRANSFER_AV_task)pContent;

                            //
                            if (null == pCtx.pProcessQ) return iErr;
                            qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                            tmpiRet = pCtx.pProcessQ.qTraverse_mono(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, param);
                            if (tmpiRet <= 0) {
                                return iErr;
                            }

                            break;
                        case CONST_imCommType_transferAvReplyInfo:

                            if (null == pCtx.pProcessQ) return iErr;
                            qnmCommProc_mis.TRANSFER_AV_replyInfo transferAvReplyInfo = (qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent;
                            if (transferAvReplyInfo.usOp != CONST_imOp_recv_accept) {
                            } else {
                                //  Ö»ÓÐ¶ÓÁÐÖÐµÄÏûÏ¢²ÅÊÇ¿ÉÒÔ´«µÝÊý¾ÝµÄ
                                param = new qyMcMainProc.PARAM_findOrgMsg();
                                tmpiRet = pCtx.pProcessQ.qTraverse_mono(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, param);
                                if (tmpiRet <= 0) {
                                    return iErr;
                                }

                            }

                            break;
                        case CONST_imCommType_transferVideoData: {

                            bRemoveIfErr = false;           //  2008/05/14, ²»ÄÜÒòÎª³ö´í½«É¾³ý½Úµã¡£ÓÐÊ±Êý¾Ý±ÈavProcInfoÅÜµÄ¿ì£¬²»ÄÜÒò´Ë°Ñ½Úµã¸øÉ¾³ýÁË¡£

                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("send transferVideoData"));

                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                                //Log.e("procMsgTask_mc_media: ´¦ÀíConsts.CONST_imCommType_transferVideoData %s¡£", tmpiRet ? "failed" : "not found");
                                return iErr;
                            }

                        }
                        iErr = 0;
                        return iErr;

                        case CONST_imCommType_transferAudioData: {

                            bRemoveIfErr = false;           //  2008/05/14, ²»ÄÜÒòÎª³ö´í½«É¾³ý½Úµã¡£ÓÐÊ±Êý¾Ý±ÈavProcInfoÅÜµÄ¿ì£¬²»ÄÜÒò´Ë°Ñ½Úµã¸øÉ¾³ýÁË¡£

                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("send transferAudioData"));

                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                                //Log.e("procMsgTask_mc_media: ´¦ÀíConsts.CONST_imCommType_transferAudioData %s¡£", tmpiRet ? "failed" : "not found");
                                return iErr;
                            }

                        }
                        iErr = 0;
                        return iErr;

                        case CONST_imCommType_taskProcReq:
                            switch (((qnmCommProc_mis.TASK_PROC_REQ)pContent).usOp) {
                                case CONST_imOp_send_cancel:
                                case CONST_imOp_recv_cancel:

                                    if (null == pCtx.pProcessQ) return iErr;

                                    //  printQ_mis(  &pCtx.processQ  );
                                    //  printQ_mis(  &pCtx.processQ  );

                                    break;
                                default:
                                    break;
                            }

                            break;
                        case CONST_imCommType_imGrp: {  //  2018/10/27
                            int ii = 0;
                        }
                        break;

                        //
                        default:
                            return iErr;

                    }

                    //printMisMsg(null, null, (qnmCommProc_mis.MIS_MSGU) pMsg);

                    if (pMsg.data.route.idInfo_to.ui64Id == 0) {
                        switch (pMsg.uiTaskType) {
                            case CONST_imTaskType_shareDynBmp:
                            case CONST_imTaskType_shareGps:
                                //
                                iErr = 0;
                                return iErr;

                            default:
                                break;
                        }
                        Log.i( "procMsgTask_mc_media failed, idInfo_to is 0");
                        return iErr;
                    }

                    if (0 != procMsgTask2Output_mc(pProcInfo, pMisCnt, pMsg, lenInBytes_msg, pChannel, ("thread_mgr")))
                        return iErr;

                    //
                    iErr = 0;
                    return iErr;

                }

                if (bMsgInput && pMsg.usCode == CONST_qyCmd_sendMedia) {

                    //  2009/10/18
                    pMsg.uiChannelType = pChannel.uiType;

                    switch (pContent.uiType) {
                        case imCommType_defs.Consts.CONST_imCommType_transferAvInfo: {
                            Object pDb = null;
                            qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                            {

                                usRespCode = CONST_qyRc_unknown;
                                qySyncCommProc.CQyMalloc mallocObj_rcd = new qySyncCommProc.CQyMalloc();
                                {
                                    ;
                                    qnmCommProc_mis.IM_TASK_RCD pRcd = new qnmCommProc_mis.IM_TASK_RCD();
                                    if (null == pRcd) return iErr;

                                    if (null == pCtx.pProcessQ) return iErr;

                                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;

                                    //


                                    //
                                    pMsg.uiTaskType = transferAvInfo.uiTaskType;
                                    pMsg.iStatus = CONST_imTaskStatus_resp;
                                    //Log.d("##1704");

                                    //  2009/09/07
                                    if (transferAvInfo.uiTaskType == CONST_imTaskType_viewDynBmp) {

                                        if (0 != qPostMsg(pMsg, pQyMc.gui.processQ)) {
                                            return iErr;
                                        }
                                        guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                        iErr = 0;
                                        return iErr;
                                    }

                                    //  2014/11/21. 这里要把代理的信息更新到起始任务里。
                                    if (transferAvInfo.proxiedTranInfo.idInfo.ui64Id !=0 && 0 != transferAvInfo.proxiedTranInfo.uiObjType) {
                                        if (transferAvInfo.proxiedTranInfo.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {

                                            qyMcMainProc.PARAM_findOrgMsg param=new qyMcMainProc.PARAM_findOrgMsg();
                                            //mymemset((byte[])param, 0, sizeof(PARAM_findOrgMsg));
                                            param.bNeedUpdate = true;

                                            tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg,  param);

                                            iErr = 0;
                                            return iErr;
                                        }
                                    }

                                    //
                                    if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys))
                                        return iErr;
                                    pDb = db.m_pDbMem.pDb;

                                    //
                                    if (null == msg2TaskRcd(pMisCnt, pMsg, usRespCode, pRcd)) {
                                        return iErr;
                                    }
                                    tmpiRet = storeTaskInDb.qyFuncs.storeTaskInDb(pDb, pQyMc.cfg.db.iDbType, pMisCnt, 0, pRcd, false, "procMsgTask_mc_media_mgr kk");
                                    if (tmpiRet < 0) {
                                        return iErr;
                                    }
                                    //  //  2008/06/04
                                    if (tmpiRet > 0) {   //  rcd.iStatus
                                        tmpiRet = pCtx.pProcessQ.qTraverse_mono(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0);
                                        if (tmpiRet < 0) return iErr;
                                        if (tmpiRet > 0) {
                                            iErr = 0;
                                            return iErr;
                                        }
                                        //  
                                        if (pRcd.iStatus != pMsg.iStatus && !bTaskAlive(pRcd.iStatus)) {
                                            if (0 != g_dbFuncs.pf_updateTaskStatus.call(pDb, pMsg.iStatus, pRcd.id))
                                                return iErr;
                                        }

                                    }

                                    //  2007/07/01						   
                                    pMsg.iTaskId = pRcd.id;
                                    pMsg.uiTaskMode_unused = pRcd.uiMode;

                                    //
                                    if (0 != qPostMsg(pMsg, pQyMc.gui.processQ)) {
                                        return iErr;
                                    }
                                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                    iErr = 0;
                                    return iErr;
                                }
                            }
                        }

                        case CONST_imCommType_transferAvReplyInfo: {
                            Object pDb = null;
                            qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                            {
                                ;

                                usRespCode = CONST_qyRc_unknown;

                                if (null == pCtx.pProcessQ) return iErr;

                                if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                                pDb = db.m_pDbMem.pDb;

                                qnmCommProc_mis.TRANSFER_AV_replyInfo transferAvReplyInfo = (qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent;
                                if (transferAvReplyInfo.usOp != CONST_imOp_recv_accept) {
                                    Log.i( "procMsgTask_mc_media, usSp is not accept");
                                    return iErr;
                                }
                                //
                                if (transferAvReplyInfo.usOp == CONST_imOp_recv_accept) {
                                    qyDefs.QY_MESSENGER_ID idInfo = pMisCnt.idInfo;
                                    if (!g_dbFuncs.pf_bGetImTaskRcdBySth.call(pDb, pQyMc.cfg.db.iDbType, false, "", idInfo, transferAvReplyInfo.tStartTime_org, transferAvReplyInfo.uiTranNo_org, 0, null)) {
                                        return iErr;
                                    }

                                    //
                                    //  printQ_mis(  &pCtx.pProcessQ.m_q,  0  );
                                    //
                                    //printMisMsg(null, null, (qnmCommProc_mis.MIS_MSGU) pMsg);

                                    //  
                                    if (pCtx.pProcessQ.qTraverse_mono(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0) <= 0) {
                                        Log.i( "procMsgTask_mc_media failed: ");
                                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, NULL, "", "", ("%s err: "), qyGetDesByType1(getResTable(0,pQyMc->cusRes, CONST_resId_qyCommTypeTable), pContent->uiType));
                                        //
                                        //  postMsg2Mgr...
                                        return iErr;
                                    }
                                } else {
                                    //Log.e("pContent->uiType is %d", pContent->uiType);
                                    return iErr;
                                }

                                //  2013/02/13
                                if (0 != transferAvReplyInfo.uiTranNo_org) {
                                    if (transferAvReplyInfo.uiTranNo_org == pProcInfo.status.progress.uiTranNo) {
                                        notifyProgressEnd(transferAvReplyInfo.uiTranNo_org, 0);
                                    }
                                }

                                startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferAvReplyInfo"));

                                //  ÏÂÃæ×¼±¸·¢ËÍ×¼±¸ºÃµÄÏìÓ¦°ü
                                usRespCode = CONST_qyRc_ok;

                                MACRO_makeImSimpleResp(pMsg.usCode, pContent.uiType, 0, respSimple);
//                                byte[] data = respSimple.toByteArray();
                                int len = 0;//(uint)sizeof(IM_SIMPLE_RESP)
                                postMsgTask2Mgr_mc(pCtx.ctxCaller, pMisCnt, CONST_misMsgType_outputTask, (byte) CONST_commFlg_resp, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType,  respSimple, len, pMsg.addr_logicalPeer.idInfo.ui64Id,
                                        pMsg.idInfo_taskSender.ui64Id,
                                        pMsg.idInfo_taskReceiver.ui64Id,
                                        pMsg.data.route.idInfo_from.ui64Id,
                                        pChannel.uiType, (qnmCommProc_mis.MIS_MSGU) null, false);

                                iErr = 0;
                                return iErr;
                            }
                        }

                        case CONST_imCommType_transferVideoData: {

                            bRemoveIfErr = false;           //  2008/05/14, ²»ÄÜÒòÎª³ö´í½«É¾³ý½Úµã¡£ÓÐÊ±Êý¾Ý±ÈavProcInfoÅÜµÄ¿ì£¬²»ÄÜÒò´Ë°Ñ½Úµã¸øÉ¾³ýÁË¡£

                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferVideoData"));

                            //  2009/05/12						   
                            pMsg.uiChannelType = pChannel.uiType;

                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                                getTimelStr(pMsg.tStartTime, timeBuf.timeBuf, CONST_qyTimeLen + 1);

                                //  if  (  pMsg->data.route.idInfo_to.ui64Id  !=  pMisCnt->idInfo.ui64Id  )  
                                {
                                    //
                                    if (tmpFunc.qyFuncs.myGetTickCount64(null) - pCtx.dwTickCnt_tryToResumeVideoConference > Consts.CONST_nIntervalInMs_tryToResumeVideoTask) {
                                        pCtx.dwTickCnt_tryToResumeVideoConference = tmpFunc.qyFuncs.myGetTickCount64(null);

                                        showTaskStatus(pMisCnt, pMsg.addr_logicalPeer.idInfo, pMsg.data.route.idInfo_from, null, false, pMsg.usCode, 0, ((qnmCommProc_mis.TRANSFER_VIDEO_DATA)pContent).uiTranNo_openAvDev, pContent.uiType, 0, CONST_imTaskStatus_err_missingOrgReq);
                                    }
                                }
                                //
                                //
                                return iErr;
                            }


                            iErr = 0;
                            return iErr;

                        }


                        case CONST_imCommType_transferAudioData: {

                            bRemoveIfErr = false;           //  2008/05/14, ²»ÄÜÒòÎª³ö´í½«É¾³ý½Úµã¡£ÓÐÊ±Êý¾Ý±ÈavProcInfoÅÜµÄ¿ì£¬²»ÄÜÒò´Ë°Ñ½Úµã¸øÉ¾³ýÁË¡£

                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferAudioData"));

                            //  2009/05/12						   
                            pMsg.uiChannelType = pChannel.uiType;

                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                                getTimelStr(pMsg.tStartTime, timeBuf.timeBuf, CONST_qyTimeLen + 1);

                                //  if  (  pMsg->data.route.idInfo_to.ui64Id  !=  pMisCnt->idInfo.ui64Id  )  
                                {
                                    if (tmpFunc.qyFuncs.myGetTickCount64(null) - pCtx.dwTickCnt_tryToResumeVideoConference > 5000) {
                                        pCtx.dwTickCnt_tryToResumeVideoConference = tmpFunc.qyFuncs.myGetTickCount64(null);

                                        showTaskStatus(pMisCnt, pMsg.addr_logicalPeer.idInfo, pMsg.data.route.idInfo_from, null, false, pMsg.usCode, 0, ((qnmCommProc_mis.TRANSFER_AUDIO_DATA)pContent).uiTranNo_openAvDev, pContent.uiType, 0, CONST_imTaskStatus_err_missingOrgReq);
                                    }
                                }
                                //
                                //
                                return iErr;      //  ÀíÓÉÍ¬ÉÏ
                            }


                            iErr = 0;
                            return iErr;

                        }


                        case CONST_imCommType_remoteAssistReq:

                            bRemoveIfErr = false;           //  2008/05/14, ²»ÄÜÒòÎª³ö´í½«É¾³ý½Úµã¡£ÓÐÊ±Êý¾Ý±ÈavProcInfoÅÜµÄ¿ì£¬²»ÄÜÒò´Ë°Ñ½Úµã¸øÉ¾³ýÁË¡£

                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("recv remoteAssistReq"));

                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                                getTimelStr(pMsg.tStartTime, timeBuf.timeBuf, CONST_qyTimeLen + 1);
                                Log.i( "procMsgTask_mc_media failed: ");
                                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, null, "", "", ("%s err: st [%s], tn_openAvDev [%d]"), qyGetDesByType1(getResTable(0,pQyMc->cusRes, CONST_resId_qyCommTypeTable), pContent->uiType), CQyString(timeBuf), pContent->transferVideoData.uiTranNo_openAvDev);
                                //
                                return iErr;      //  ÀíÓÉÍ¬ÉÏ
                            }

                            iErr = 0;
                            return iErr;



                        //  2012/04/20

                        default:
                            return iErr;

                    }

                }   //  

                if (!bMsgInput) {

                    if (0 != procMsgTask2Output_mc(pProcInfo, pMisCnt, pMsg, lenInBytes_msg, pChannel, ("thread_mgr"))) {
                        return iErr;
                    }

                    iErr = 0;
                    return iErr;

                }

                return iErr;


            } else {
                if (!isRcOk(pMsg.usCode)) return iErr;
            }

            iErr = 0;
            return iErr;
        }


        public static int procMsgInput_mc_robot_mgr(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_INPUT pMsg, int lenInBytes_msg, boolean bMsgInput, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            //
            int dataLen = 0;
            //int uiMsgLen = (int)Marshal.OffsetOf<qnmCommProc_mis.MIS_MSG_TASK>("data") + (int)Marshal.OffsetOf<qnmCommProc_mis>("buf");
            int uiChannelType = CONST_channelType_talking;
            qnmCommProc_mis.IM_CONTENTU pContent = pMsg.data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
            Object hMainWnd = pQyMc.gui.hMainWnd;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;
            short usRespCode;
            String str;

            qySyncCommProc.CQyMalloc mallocObj_rcd = new qySyncCommProc.CQyMalloc();
            {
                qnmCommProc_mis.IM_TASK_RCD pRcd =new qnmCommProc_mis.IM_TASK_RCD();
                if (null == pRcd) return -1;
                qnmCommProc_mis.IM_TASK_RCD rcd = pRcd;
                int tmpiRet;
                //
                int lenInBytes = 0;
                myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
                if (null == pDbFuncs) return -1;
                myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();


                if (null == hMainWnd) return -1;
                if (null == pMisCnt) return -1;

                if (pMsg.addr_logicalPeer.idInfo.ui64Id == 0) {   //  Ç°ÃæÃ»ÓÐ¸³ÖµµÄ¶¼ÊÇÊÕµ½µÄÏûÏ¢
                    if (0 != getIdInfo_logicalPeer(pMisCnt, pMsg.data.route, pMsg.addr_logicalPeer)) {
                        Log.i( "procMsgTask_mc_robot: getIdInfo_logicalPeer failed.");
                        return iErr;
                    }
                }


                if (!isUcFlgResp(pMsg.ucFlg)) {
                    if (!bMsgInput && pMsg.usCode == CONST_qyCmd_sendRobotTask) {
                        iErr = 0;
                        return iErr;
                    }

                    if (bMsgInput && pMsg.usCode == CONST_qyCmd_sendRobotTask) {

                        usRespCode = CONST_qyRc_unknown;
                        Object pDb = null;

                        qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                        {
                            ;
                            if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                            pDb = db.m_pDbMem.pDb;

                            //  2009/10/18
                            pMsg.uiChannelType = pChannel.uiType;

                            //
                            switch (pContent.uiType) {
                                case CONST_imCommType_transferFileReq:
                                    pMsg.uiTaskType = CONST_imTaskType_recvFile;
                                    pMsg.iStatus = CONST_imTaskStatus_applyToRecv;
                                    break;
                                default:
                                    str = String.format("´íÎóµÄÊý¾Ý¸ñÊ½£ºpContent.uiType is %d", pContent.uiType);

                                    return iErr;

                            }

                            if (null == msg2TaskRcd(pMisCnt, pMsg, usRespCode, pRcd)) {
                                Log.i( "msg2TaskRcd failed");
                                return iErr;
                            }
                            tmpiRet = storeTaskInDb.qyFuncs.storeTaskInDb(pDb, pQyMc.cfg.db.iDbType, pMisCnt, 0, pRcd, false, "procMsgInput_mc_robot_mgr");
                            if (tmpiRet < 0) {
                                Log.i( "mcThreadProc_mis_work(  ): storeTaskInDb failed");
                                return iErr;
                            }
                            if (tmpiRet > 0) {
                                switch (pContent.uiType) {
                                    case CONST_imCommType_transferFileReq:
                                        if (rcd.iStatus != CONST_imTaskStatus_applyToRecv && rcd.iStatus != CONST_imTaskStatus_waitToRecv) {
                                            qnmCommProc_mis.IM_SIMPLE_RESP respSimple = new qnmCommProc_mis.IM_SIMPLE_RESP();
                                            //
                                            MACRO_makeImSimpleResp(pMsg.usCode, pContent.uiType, rcd.iStatus, respSimple);
//                                            byte[] data = respSimple.toByteArray();
                                            int len =0;//(uint)sizeof(IM_SIMPLE_RESP)
                                            postMsgTask2Mgr_mc((ctxCaller.CTX_caller) null, pMisCnt, CONST_misMsgType_outputTask, (byte) CONST_commFlg_resp, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType,  respSimple,len, pMsg.addr_logicalPeer.idInfo.ui64Id,
                                                    pMsg.idInfo_taskSender.ui64Id,
                                                    pMsg.idInfo_taskReceiver.ui64Id,
                                                    pMsg.data.route.idInfo_from.ui64Id,
                                                    uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false);

                                            Log.i( "ÕâÀïÊÇËµÃ÷´ËÈÎÎñÒÑ½áÊøÁË£¬²»¹ÜÊ²Ã´½á¹û¡£²é³ö½á¹û·¢»ØÈ¥");

                                            iErr = 0;
                                            return iErr;

                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }

                            //  ×¢Òâ£¬ÕâÀïÒª½«Êý¾Ý¿âÖÐÏàÓ¦µÄID¸ø¸³ÉÏ£¬2007/07/01
                            pMsg.iTaskId = rcd.id;
                            pMsg.uiTaskMode_unused = rcd.uiMode;


                            Log.i( "");

                        }

                        if (0 != qPostMsg(pMsg, pQyMc.gui.processQ)) {
                            Log.i( "mcThreadProc_mis_work(  ): qPostMsg(  ) failed");
                            return iErr;
                        }

                        guiShare.GuiShare.pf_PostMessage.call(hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                        iErr = 0;
                        return iErr;

                    }

                    if (!bMsgInput && pMsg.usCode == CONST_qyCmd_sendRobotTaskReply) {
                        //
                        iErr = 0;
                        return iErr;

                    }

                    if (bMsgInput && pMsg.usCode == CONST_qyCmd_sendRobotTaskReply) {
                        qnmCommProc_mis.IM_SIMPLE_RESP respSimple = new qnmCommProc_mis.IM_SIMPLE_RESP();

                        qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                        {
                            ;
                            if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;

                            if (pContent.uiType == CONST_imCommType_transferFileReplyReq) {
                                if (((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ)pContent).usOp != CONST_imOp_recv_accept)
                                    return iErr;

                                if (!g_dbFuncs.pf_bGetImTaskRcdBySth.call(db.m_pDbMem.pDb, pQyMc.cfg.db.iDbType, false, "", pMisCnt.idInfo, ((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ)pContent).tStartTime_org, ((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ)pContent).uiTranNo_org, 0, pRcd)) {
                                    Log.i( "procMsgTask_mc_robot failed: bGetImTaskRcdBySth failed");
                                    return iErr;
                                }

                                if (pCtx.pProcessQ.qTraverse(null, tmpHandler_findOrgMsgAndProc, pMsg, 0) <= 0) {
                                    Log.i( "procMsgTask_mc_robot failed: ");
                                    //  postMsg2Mgr...
                                    return iErr;
                                }

                            } else {
                                str = String.format("´íÎóµÄÊý¾Ý¸ñÊ½£ºpContent.uiType is %d", pContent.uiType);

                                //
                                return iErr;
                            }

                        }

                        //
                        startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferFileReply"));

                        //  ÏÂÃæ×¼±¸·¢ËÍ×¼±¸ºÃµÄÏìÓ¦°ü
                        usRespCode = CONST_qyRc_ok;
                        uiChannelType = 0;// CONST_channelType_robot;

                        MACRO_makeImSimpleResp(pMsg.usCode, pContent.uiType, 0, respSimple);

                        str = "send respSimple to " + pMsg.data.route.idInfo_from.ui64Id;
                        showInfo_open.qyFuncs.showInfo_open(0, null, str);
//                        byte[] data = respSimple.toByteArray();
                        int len =0;//(uint)sizeof(IM_SIMPLE_RESP)
                        postMsgTask2Mgr_mc((ctxCaller.CTX_caller) null, pMisCnt, CONST_misMsgType_outputTask, (byte) CONST_commFlg_resp, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType, respSimple,len,
                                pMsg.addr_logicalPeer.idInfo.ui64Id,
                                pMsg.idInfo_taskSender.ui64Id,
                                pMsg.idInfo_taskReceiver.ui64Id,
                                pMsg.data.route.idInfo_from.ui64Id,
                                uiChannelType, null, false);

                        iErr = 0;
                        return iErr;
                    }
                    Log.i( "procMsgTask_mc_robot:  Óöµ½ÁËÎÞ·¨½âÊÍµÄÇé¿ö£¬´íÎó");
                    return iErr;


                } else {

                    //printMisMsg(null, null, (qnmCommProc_mis.MIS_MSGU) pMsg);

                    if (!isRcOk(pMsg.usCode)) return iErr;

                    Log.i( "procMsgTask_mc_robot:  ¶ÔÓÚÏìÓ¦°ü£¬»¹Ã»ÓÐ´¦ÀíÄØ");
                }

            }


            iErr = 0;

            

            if (0 != iErr) {
            }

            return iErr;
        }

        //
        public static int procMsgTask_mc_robot_mgr(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_TASK pMsg, int lenInBytes_msg, boolean bMsgInput, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            //
            int dataLen = 0;
            //int uiMsgLen = (int)Marshal.OffsetOf<qnmCommProc_mis.MIS_MSG_TASK>("data") + (int)Marshal.OffsetOf<qnmCommProc_mis>("buf");
            int uiChannelType = CONST_channelType_talking;
            qnmCommProc_mis.IM_CONTENTU pContent =pMsg.data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
            Object hMainWnd = pQyMc.gui.hMainWnd;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;
            short usRespCode;
            String str;

            qySyncCommProc.CQyMalloc mallocObj_rcd = new qySyncCommProc.CQyMalloc();
            {
                qnmCommProc_mis.IM_TASK_RCD pRcd = new qnmCommProc_mis.IM_TASK_RCD();// (qnmCommProc_mis.IM_TASK_RCD)mallocObj_rcd.mallocf(sizeof(IM_TASK_RCD));
                if (null == pRcd) return -1;
                qnmCommProc_mis.IM_TASK_RCD rcd = pRcd;
                int tmpiRet;
                //
                int lenInBytes = 0;
                myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
                if (null == pDbFuncs) return -1;
                myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();


                if (null == hMainWnd) return -1;
                if (null == pMisCnt) return -1;

                if (pMsg.addr_logicalPeer.idInfo.ui64Id == 0) {   //  Ç°ÃæÃ»ÓÐ¸³ÖµµÄ¶¼ÊÇÊÕµ½µÄÏûÏ¢
                    if (0 != getIdInfo_logicalPeer(pMisCnt, pMsg.data.route, pMsg.addr_logicalPeer)) {
                        Log.i( "procMsgTask_mc_robot: getIdInfo_logicalPeer failed.");
                        return iErr;
                    }
                }


                if (!isUcFlgResp(pMsg.ucFlg)) {

                    if (!bMsgInput && pMsg.usCode == CONST_qyCmd_sendRobotTask) {

                        switch (pContent.uiType) {
                            case CONST_imCommType_transferFileReq:
                                if (pCtx.pProcessQ.qTraverse(null, tmpHandler_findOrgMsgAndProc, pMsg, 0) <= 0) {
                                    str= "procMsgTask_mc_robot_mgr failed: fileReq not found";
                                    Log.d( str);
                                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                    return iErr;
                                }

                                break;
                            case CONST_imCommType_taskProcReq:
                                switch (((qnmCommProc_mis.TASK_PROC_REQ)pContent).usOp) {
                                    case CONST_imOp_send_cancel:
                                    case CONST_imOp_recv_cancel:
                                        //  printQ_mis(  &pCtx.processQ  );
                                        //pCtx.pProcessQ.qRemoveMsg(tmpHandler_findOrgMsgAndProc_is, pMsg,0);
                                        Log.i( "cannot remove task here");
                                        //  printQ_mis(  &pCtx.processQ  );

                                        break;
                                    default:
                                        break;
                                }

                                break;
                            default:
                                str = String.format("´íÎóµÄÊý¾Ý¸ñÊ½£ºpContent.uiType is %d", pContent.uiType);

                                return iErr;

                        }

                        if (0 != procMsgTask2Output_mc(pProcInfo, pMisCnt, pMsg, lenInBytes_msg, pChannel, ("thread_mgr")))
                            return iErr;


                        iErr = 0;
                        return iErr;
                    }

                    if (bMsgInput && pMsg.usCode == CONST_qyCmd_sendRobotTask) {

                        usRespCode = CONST_qyRc_unknown;
                        Object pDb = null;

                        qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                        {
                            ;
                            if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                            pDb = db.m_pDbMem.pDb;

                            //  2009/10/18
                            pMsg.uiChannelType = pChannel.uiType;

                            //
                            switch (pContent.uiType) {
                                case CONST_imCommType_transferFileReq:
                                    pMsg.uiTaskType = CONST_imTaskType_recvFile;
                                    pMsg.iStatus = CONST_imTaskStatus_applyToRecv;
                                    break;
                                default:
                                    str = String.format("´íÎóµÄÊý¾Ý¸ñÊ½£ºpContent.uiType is %d", pContent.uiType);

                                    return iErr;

                            }


                            //  ÕâÀïÒªÈë¿â£¬È»ºóÔÙÍ¨Öª½çÃæ

                            if (null == msg2TaskRcd(pMisCnt, pMsg, usRespCode, pRcd)) {
                                Log.i( "msg2TaskRcd failed");
                                return iErr;
                            }
                            tmpiRet = storeTaskInDb.qyFuncs.storeTaskInDb(pDb, pQyMc.cfg.db.iDbType, pMisCnt, 0, pRcd, false, "");
                            if (tmpiRet < 0) {
                                Log.i( "mcThreadProc_mis_work(  ): storeTaskInDb failed");
                                return iErr;
                            }
                            if (tmpiRet > 0) {
                                switch (pContent.uiType) {
                                    case CONST_imCommType_transferFileReq:
                                        if (rcd.iStatus != CONST_imTaskStatus_applyToRecv && rcd.iStatus != CONST_imTaskStatus_waitToRecv) {
                                            qnmCommProc_mis.IM_SIMPLE_RESP respSimple = new qnmCommProc_mis.IM_SIMPLE_RESP();
                                            //
                                            MACRO_makeImSimpleResp(pMsg.usCode, pContent.uiType, rcd.iStatus, respSimple);
//                                            byte[] data = respSimple.toByteArray();
                                            int len =0;//(uint)sizeof(IM_SIMPLE_RESP)
                                            postMsgTask2Mgr_mc((ctxCaller.CTX_caller) null, pMisCnt, CONST_misMsgType_outputTask, (byte) CONST_commFlg_resp, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType,  respSimple,len, pMsg.addr_logicalPeer.idInfo.ui64Id,
                                                    pMsg.idInfo_taskSender.ui64Id,
                                                    pMsg.idInfo_taskReceiver.ui64Id,
                                                    pMsg.data.route.idInfo_from.ui64Id, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false);

                                            Log.i( "ÕâÀïÊÇËµÃ÷´ËÈÎÎñÒÑ½áÊøÁË£¬²»¹ÜÊ²Ã´½á¹û¡£²é³ö½á¹û·¢»ØÈ¥");

                                            iErr = 0;
                                            return iErr;

                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }

                            //  ×¢Òâ£¬ÕâÀïÒª½«Êý¾Ý¿âÖÐÏàÓ¦µÄID¸ø¸³ÉÏ£¬2007/07/01
                            pMsg.iTaskId = rcd.id;
                            pMsg.uiTaskMode_unused = rcd.uiMode;
                        }

                        Log.i( "");


                        if (0 != qPostMsg(pMsg, pQyMc.gui.processQ)) {
                            Log.i( "mcThreadProc_mis_work(  ): qPostMsg(  ) failed");
                            return iErr;
                        }

                        guiShare.GuiShare.pf_PostMessage.call(hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                        iErr = 0;
                        return iErr;

                    }


                    if (!bMsgInput && pMsg.usCode == CONST_qyCmd_sendRobotTaskReply) {

                        Log.i( "ÎÄ¼þ½ÓÊÕÇëÇóÒÑ±»È·ÈÏ, ÐÅÏ¢´ÓtalkingÍ¨µÀÖÐ·¢³ö");

                        switch (pContent.uiType) {
                            case CONST_imCommType_transferFileReplyReq:
                                if (((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ)pContent).usOp != CONST_imOp_recv_accept) {
                                    pCtx.pProcessQ.qRemoveMsg(null, b_tmpHandler_findOrgMsgAndProc_is, pMsg, 0);  //  ²»ÔÊÐí´«ËÍ
                                } else {

                                    //  ¼ÓÈë¶ÓÁÐ¡£Ö»ÓÐ¶ÓÁÐÖÐµÄÏûÏ¢²ÅÊÇ¿ÉÒÔ´«µÝÊý¾ÝµÄ								   
                                    if (pCtx.pProcessQ.qTraverse(null, tmpHandler_findOrgMsgAndProc, pMsg, 0) <= 0) {
                                        //
                                        //  ÒªÊÇÔÚprocessQÀïÃ»ÓÐ£¬ÄÇÃ´¾Í¼ÓÈë¶ÓÁÐ; 
                                        //
                                        pMsg.idInfo_taskSender.ui64Id = pMsg.data.route.idInfo_to.ui64Id;
                                        pMsg.idInfo_taskReceiver.ui64Id = pMsg.data.route.idInfo_from.ui64Id;
                                        //
                                        if (0 != pCtx.pProcessQ.qPostMsg(pMsg, lenInBytes_msg)) {
                                            Log.i( "procMsgTask_mc_robot: 2. ÕâÀï¼Ó²»ÁËÈÎÎñÁË£¬Ó¦¸ÃÍ¨Öª¹ÜÀíÏß³Ì£¬ËµÈÎÎñ¶ÓÁÐÂúÁË¡£²»ÄÜÔÙ¼ÓÁË");
                                            return iErr;
                                        }
                                    }

                                    //  2008/11/18
                                    startChannel(pProcInfo, pMisCnt, pChannel, ("send transferFileReply"));

                                }
                                break;
                            default:
                                str = String.format("´íÎóµÄÊý¾Ý¸ñÊ½£ºpContent.uiType is %d", pContent.uiType);

                                return iErr;

                        }

                        if (0 != procMsgTask2Output_mc(pProcInfo, pMisCnt, pMsg, lenInBytes_msg, pChannel, ("thread_mgr")))
                            return iErr;

                        iErr = 0;
                        return iErr;

                    }


                    //  ·¢ËÍÎÄ¼þµÄ»Ø¸´°ü£¬´ÓmgrÖÐÀ´
                    if (bMsgInput && pMsg.usCode == CONST_qyCmd_sendRobotTaskReply) {
                        qnmCommProc_mis.IM_SIMPLE_RESP respSimple = new qnmCommProc_mis.IM_SIMPLE_RESP();

                        qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                        {
                            ;
                            if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;

                            if (pContent.uiType == CONST_imCommType_transferFileReplyReq) {
                                //  char					timeBuf[CONST_qyTimeLen  +  1]  =  "";

                                //  ¶Ô·½²»ÔÊÐí
                                if (((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ)pContent).usOp != CONST_imOp_recv_accept)
                                    return iErr;

                                //  
                                if (!g_dbFuncs.pf_bGetImTaskRcdBySth.call(db.m_pDbMem.pDb, pQyMc.cfg.db.iDbType, false, "", pMisCnt.idInfo, ((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ)pContent).tStartTime_org, ((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ)pContent).uiTranNo_org, 0, pRcd)) {
                                    Log.i( "procMsgTask_mc_robot failed: bGetImTaskRcdBySth failed");
                                    return iErr;
                                }

                                //
                                //printQ_mis(  &pCtx.pProcessQ.m_q,  0  );
                                //
                                //printMisMsg(null, null, (qnmCommProc_mis.MIS_MSGU) pMsg);

                                //  
                                if (pCtx.pProcessQ.qTraverse(null, tmpHandler_findOrgMsgAndProc, pMsg, 0) <= 0) {
                                    Log.i( "procMsgTask_mc_robot failed: ");
                                    //  postMsg2Mgr...
                                    return iErr;
                                }

                            } else {
                                str = String.format("´íÎóµÄÊý¾Ý¸ñÊ½£ºpContent.uiType is %d", pContent.uiType);

                                return iErr;
                            }
                        }

                        startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferFileReply"));

                        //  ÏÂÃæ×¼±¸·¢ËÍ×¼±¸ºÃµÄÏìÓ¦°ü
                        usRespCode = CONST_qyRc_ok;
                        uiChannelType = CONST_channelType_robot;

                        MACRO_makeImSimpleResp(pMsg.usCode, pContent.uiType, 0, respSimple);
//                        byte[] data = respSimple.toByteArray();
                        int len = 0;//(uint)sizeof(IM_SIMPLE_RESP)
                        postMsgTask2Mgr_mc((ctxCaller.CTX_caller) null, pMisCnt, CONST_misMsgType_outputTask, (byte) CONST_commFlg_resp, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType,  respSimple,len,
                                pMsg.addr_logicalPeer.idInfo.ui64Id,
                                pMsg.idInfo_taskSender.ui64Id,
                                pMsg.idInfo_taskReceiver.ui64Id,
                                pMsg.data.route.idInfo_from.ui64Id,
                                uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false);

                        iErr = 0;
                        return iErr;

                    }   //  

                    Log.i( "procMsgTask_mc_robot:  Óöµ½ÁËÎÞ·¨½âÊÍµÄÇé¿ö£¬´íÎó");
                    return iErr;


                } else {

                    //qyShowInfo1(CONST_qyShowType_debug, 0, "", pProcInfo.str_who_showInfo, null, "", "", ("µÃµ½%I64uµÄÏìÓ¦%s"), pMsg->data.route.idInfo_from.ui64Id, qyGetDesByType1(qyFuncs.CONST_qyRcTable, pMsg->usCode));
                    str = String.format("µÃµ½%I64uµÄÏìÓ¦%s", pMsg.data.route.idInfo_from.ui64Id, qyGetDesByType1(CONST_qyRcTable, pMsg.usCode));

                    if (!isRcOk(pMsg.usCode)) return iErr;

                    Log.i( "procMsgTask_mc_robot:  ¶ÔÓÚÏìÓ¦°ü£¬»¹Ã»ÓÐ´¦ÀíÄØ");
                }
            }


            iErr = 0;

            

            if (0 != iErr) {
            }

            return iErr;
        }

    }
}

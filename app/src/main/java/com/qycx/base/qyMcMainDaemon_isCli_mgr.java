
package com.qycx.base;

import static com.qycx.base.Consts.*;
import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.findOrgMsgAndProcess_is.qyFuncs.*;
import static com.qycx.base.genericQueue.qyFuncs.*;
import static com.qycx.base.imCommType_defs.Consts.*;
import static com.qycx.base.isProtocolFunc.qyFuncs.*;
import static com.qycx.base.isTmpFunc.qyFuncs.*;//printMisMsg;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc;
import static com.qycx.base.qmcCommFunc_mis.Consts.*;
import static com.qycx.base.qmcVideoCapture.qyFuncs.qmcApplyForRemovingInvalidTasks;
import static com.qycx.base.qnmCommProc.Consts.*;
import static com.qycx.base.qnmCommProc_mis.Consts.*;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.*;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_resp;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_ok;
import static com.qycx.base.qyComm.qyFuncs.*;
import static com.qycx.base.qyCommProc.Consts.*;
import static com.qycx.base.qyCommProc.qyFuncs.CONST_qyCmdTable;
import static com.qycx.base.qyCommProc.qyFuncs.CONST_qyRcTable;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_viewDynBmp;
import static com.qycx.base.qyDefs_open.Consts.*;
import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.postMsg2OutputQ_mc;
import static com.qycx.base.qyMcMainDaemon_isCli_mgr.Consts.*;
import static com.qycx.base.qyMcMainProc_isCli_mgr.qyFuncs.procMsgInput_mc_mgr;
import static com.qycx.base.qyMcMainProc_isCli_mgr.qyFuncs.procMsgTask_mc_mgr;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_msgArrive;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.showTaskStatusFunc.qyFuncs.showTaskStatus;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;

import static com.qycx.base.tmpFunc.qyFuncs.mywcslen;

import com.qycx.base.util.Log;

import java.nio.ByteBuffer;


public class qyMcMainDaemon_isCli_mgr {

    public static class Consts {

        public final static int cntof_tHintBuf = 255 + 1;
        public final static int cntof_tBuf128 = 128;
        public final static int cntof_tBuf1024 = 1024;
    }

    public static class TMP_tHintBuf {
        public String tHintBuf;//[cntof_tHintBuf];
    }

    public static class TMP_tBuf128 {
        public String tBuf;//[cntof_tBuf128];
    }

    public static class TMP_tBuf1024 {
        public String tBuf;//[cntof_tBuf1024];
    }

    public static class qyFuncs {

        public static int procMsgReq_mgr(qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSGU pMsgU, int lenInBytes_msg, ctxMcThread.CTX_MC_thread pCtx) {
            int iErr = -1;
            if (null == pMsgU || pMsgU.uiType != CONST_misMsgType_req) {
                return -1;
            }
            qnmCommProc_mis.MIS_MSG_REQ pMsgReq = (qnmCommProc_mis.MIS_MSG_REQ) pMsgU;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;

            int dataLen = 0;
            int uiMsgLen = 81736; //offsetof(  MIS_MSG_REQ,  data  );
            qnmCommProc_mis.IM_CONTENTU pContent = pMsgReq.data;
//            pCtx.pMsgBuf_forInput.msgU = pMsgU;
            /*
            qnmCommProc_mis.MIS_MSG_REQ pMsg = new qnmCommProc_mis.MIS_MSG_REQ();// (qnmCommProc_mis.MIS_MSG_REQ) pCtx.pMsgBuf_forInput.msgU;
            pMsg.buffer = ByteBuffer.allocate(bufSize_transferVideoData);

             */


            //
            qmCommFunc_mis.MIS_CHANNEL pChannel = null;
            int lenInBytes = 2444440;   // sizeof(  pMsg->data  );
            switch (pMsgReq.usCode) {
                case CONST_qyCmd_refreshImObjListReq:
                    if ((dataLen = qyhtonImContentU(0,pMsgReq.data)) < 0)
                        return iErr;
                    break;
                    /*
                case CONST_qyCmd_sendVDevReq:
                    pMsgReq.buffer = ByteBuffer.allocate(bufSize_transferVideoData);
                    if (0 >(lenInBytes = qyhtonRouteTalkData(tmpVar,null, pMsgReq.data, 0, true, pMsgReq.ucFlg, pMsg.buffer, null, 0)))
                        return iErr;
                    pMsgReq.lenInBytes = lenInBytes;
                    lenInBytes += 0;// (int)Marshal.OffsetOf<MIS_MSG_REQ>("data");

                    dataLen = pMsgReq.lenInBytes; //  2007/12/22
                    break;

                     */
                case CONST_qyCmd_getCfgs:
                default:
                    switch (pContent.uiType) {
                        case 0:
                            //
                            break;
                        case CONST_imCommType_getCfgsReq:
//                            pMsgReq.buffer.position(0);
                        case CONST_imCommType_retrieveToPaths:
                        default:
                            //
                            if  (  pContent.uiType  ==  CONST_imCommType_getCfgsReq)  {
                                Log.d("qy","getCfgsReq");
                            }

                            //
                            pMsgReq.buffer.position(0);
                            if (0 >(lenInBytes = qyhtonRouteTalkData(tmpVar,null, pMsgReq.data, 0, true, pMsgReq.ucFlg, pMsgReq.buffer,null, 0)))
                                return iErr;
                            pMsgReq.lenInBytes = lenInBytes;
                            lenInBytes += 0;
                            Log.d(  "procMsgReq_mgr:  CONST_qyCmd_getCfgs:  contentType is "+ pContent.uiType  );
                            break;
                    }
                    break;

            }

//            pMsgReq.lenInBytes = dataLen;
            //
            uiMsgLen += dataLen;


            if (null == (pChannel = getChannelByType(pProcInfo.pMisCnt, CONST_channelType_talking)))
                return iErr;

            if (0 != postMsg2OutputQ_mc(pProcInfo.pMisCnt, pMsgReq, pChannel)) {
                Log.e("postMsg2OutputQ_mc failed");
                return iErr;
            }

            iErr = 0;

            if (0 != iErr) {
            }

            return iErr;
        }


        static int procMsgTalk_mc_mgr(qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar, ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSG_TALK pMsgTalk, int lenInBytes_msg, ctxMcThread.CTX_MC_thread pCtx) {
            int iErr = -1;
            if  (pMsgTalk.uiType != CONST_misMsgType_talk)
            {
                Log.e("pMsgTalk.uiType = " + pMsgTalk.uiType + "  != CONST_misMsgType_talk");
                return iErr;
            }
            int dataLen = 0;
            int lenInBytes;
            if (null == pMisCnt) return -1;
            qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_talking);
            if (null == pChannel) return -1;

            {
                qnmCommProc_mis.MIS_MSG_TALK pMsg;
                qnmCommProc_mis.IM_CONTENTU pContent = null;

                if (pMsgTalk.data.route.idInfo_to.ui64Id == 0) {
                    if (pMsgTalk.data.route.uiDevType_to == 0) {
                        qPostMsgAndTrigger(pMsgTalk, pProcInfo.workQ);
                    }
                    iErr = 0;
                    return iErr;
                }

                if (pMsgTalk.data.route.idInfo_from.ui64Id == 0) {
                    pMsgTalk.data.route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;
                }

                switch (pMsgTalk.usCode) {
                    case CONST_qyCmd_talkTo:
                    case CONST_qyCmd_sendTask:
                        break;
                    case CONST_qyCmd_sendTaskReply:
                        if (null == (pChannel = getChannelByType(pMisCnt, CONST_channelType_robot)))
                            return iErr;
                        break;
                    default:
                        return iErr;
                }

                pMsg = pMsgTalk;
                lenInBytes = 2444440;//sizeof(qnmCommProc_mis);
                pMsg.buffer = ByteBuffer.allocate(bufSize_transferVideoData);
                if (0 >(lenInBytes = qyhtonRouteTalkData(tmpVar, pMsgTalk.data.route, pMsgTalk.data.buf, 0, true, pMsgTalk.ucFlg, pMsg.buffer, null, 0  )))
                {
                    Log.e("qyhtonRouteTalkData failed");
                    return iErr;
                }
                pMsg.lenInBytes = lenInBytes;
                Log.d("lenInBytes:"+lenInBytes);
                lenInBytes += 81736;//(int)Marshal.OffsetOf<MIS_MSG_TALK>("data");

                if (0 != postMsg2OutputQ_mc(pMisCnt, pMsg, lenInBytes, pChannel)) {
                    Log.e( "postMsg2OutputQ_mc failed");
                    return iErr;
                }
            }

            iErr = 0;
            return iErr;
        }

        public static Runnable mcThreadProc_mis_mgr = () -> {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            int len;
            int loopCtrl = 0;
            int uiChannelType = CONST_channelType_talking;

            pProcInfo.dwThreadId_mgr = tmpFunc.qyFuncs.myGetCurrentThreadId();

            ctxMcThread.CTX_MC_thread ctx = new ctxMcThread.CTX_MC_thread();
            ctx.pProcessQ = pProcInfo.processQ_media;  //  2015/09/06
            ctx.pMsgBuf_forInput = new qnmCommProc_mis.CMIS_MSGU();

            qnmCommProc_mis.MIS_MSGU pMsg;

            for (; !pProcInfo.bQuit_threadProc_isCli; loopCtrl++) {
                myWaitOne(pProcInfo.mgrQ.hEvent, 3500);
                qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(CONST_iParamType_toSend,0);
                //
                while (!pProcInfo.bQuit_threadProc_isCli) {
                    len = 2526176;//(int) sizeof(qnmCommProc_mis.MIS_MSGU);
                    if ((pMsg = (qnmCommProc_mis.MIS_MSGU) qGetMsg(pProcInfo.mgrQ)) == null) break;
                    switch (pMsg.uiType) {
                        case CONST_misMsgType_input: {
                            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                            if (null == pMisCnt) {
                                break;
                            }
                            qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, uiChannelType);
                            if (null == pChannel)
                                break;
                            qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
//                            Log.i("openRtComm", "Mgr got pkg， tn: " + input.uiTranNo + " dataLen: " + input.lenInBytes);
                            if (isUcFlgResp(input.ucFlg)) {
                                if (0 != procMsgInput_mgr_resp(pQyMc, pProcInfo, input, len, ctx, pChannel)) break;
                            } else {
                                if (0 != procMsgInput_mgr_req(pQyMc, pProcInfo, input, len, ctx, pChannel)) break;
                            }
                        }
                        break;

                        case CONST_misMsgType_req:
                            if (procMsgReq_mgr(tmpVar, pProcInfo, pMsg, len, ctx) != 0) break;

                            break;

                        case CONST_misMsgType_talk:
                            if (0 != procMsgTalk_mc_mgr(tmpVar, pQyMc, pProcInfo, pProcInfo.pMisCnt, (qnmCommProc_mis.MIS_MSG_TALK) pMsg, len, ctx))
                                break;

                            break;
                        case CONST_misMsgType_task: {
                            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                            qmCommFunc_mis.MIS_CHANNEL pChannel;

                            if (null == pMisCnt) {
                                break;
                            }
                            if (null == (pChannel = getChannelByType(pMisCnt, uiChannelType)))
                                break;

                            if (0 != procMsgTask_mc_mgr(pQyMc, pProcInfo, (qnmCommProc_mis.MIS_MSG_TASK) pMsg, len, false, ctx, pChannel))
                                break;
                        }
                        break;


                        default:
                            int ii;
                            ii = pMsg.uiType;
                            break;
                    }
                }
            }


            iErr = 0;

            pProcInfo.status.threadsStatus.ulbMgrQuit = true;
        };

        static int procMsgInput_mgr_resp(ctxQyMc.CCtxQyMc pQyMcParam, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSGU pMsgU, int lenInBytes_msg, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            if(pMsgU.uiType != CONST_misMsgType_input) return iErr;

            ctxQyMc.CCtxQyMc pQyMc = pQyMcParam;
            int msgLen = 0;
            qmCommFunc_mis.MIS_CNT pMisCnt = null;
            qnmCommProc_mis.IM_CONTENTU pContent = null;

            qnmCommProc_mis.MIS_MSG_INPUT pMsgInput = (qnmCommProc_mis.MIS_MSG_INPUT) pMsgU;
            if (null == pMsgInput)
            {
                Log.e("procMsgInput_mgr_resp failed: pMsgBuf is null");
                return -1;
            }
            TMP_tHintBuf tHintBuf = new TMP_tHintBuf();
            pMisCnt = pProcInfo.pMisCnt;

            if (!isUcFlgResp(pMsgInput.ucFlg)) return iErr;

            if (!pMsgInput.ucbConvrted) {

                msgLen = 81736;// (int)Marshal.OffsetOf<MIS_MSG_INPUT>("data");

                if (isUcFlgTalkData(pMsgInput.ucFlg) || isUcFlgRouteTalkData(pMsgInput.ucFlg)) {
                    //此时pMsg.data似乎尚未解析出来，需要从pMsg.buffer里读取数据
                    qnmCommProc_mis.MIS_MSG_routeTalkData pAddrTalkData = pMsgInput.data;
                    if (null == (pAddrTalkData.buf = qyntohRouteTalkData(pMsgInput.ucFlg, pMsgInput.buffer, pMsgInput.lenInBytes, pAddrTalkData.route, tHintBuf.tHintBuf, cntof_tHintBuf)))
                        return iErr;
                    if (tHintBuf.tHintBuf != null)
                        showInfo_open.qyFuncs.showInfo_open((tHintBuf.tHintBuf));
                    //
                    msgLen += 0;
                    pContent = pAddrTalkData.buf;
                } else {
                    if (0 == pMsgInput.lenInBytes) {
                        Log.e( "procMsgInput_mgr_resp failed: msgInput.lenInBytes is 0");
                        return iErr;
                    }
                    if (null == (pMsgInput.data.buf = qyntohRouteTalkData(pMsgInput.ucFlg,  pMsgInput.buffer, pMsgInput.lenInBytes, null, tHintBuf.tHintBuf, cntof_tHintBuf)))
                        return iErr;
                    
                    if (tHintBuf.tHintBuf != null)
                        showInfo_open.qyFuncs.showInfo_open( tHintBuf.tHintBuf);
                }
                pMsgInput.ucbConvrted = true;
            }
            
            pContent = M_getMsgContent(pMsgInput.uiType, pMsgInput.ucFlg, pMsgInput.data);
            if (null == pContent) {
                Log.e( "procMsgInput_mgr_resp failed: pContent is null" );
                return iErr;
            }

            boolean bNeedProcess = false;
            String str="";
            switch (pContent.uiType) {
                case CONST_imCommType_simpleResp: {
                    str = String.format("    simpleResp¦%s,  %d", qyGetDesByType1(CONST_qyRcTable, pMsgInput.usCode), pMsgInput.uiTranNo);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    if (null == pCtx.pProcessQ) return iErr;
                    switch (((qnmCommProc_mis.IM_SIMPLE_RESP)pContent).usCmd_org) {
                        case CONST_qyCmd_sendRobotTaskReply:
                        {
                            qPostMsgAndTrigger(pMsgInput, pProcInfo.robotQ);
                            break;
                        }
                        case CONST_qyCmd_sendMedia:
                        {
                            q2PostMsgAndTrigger(pMsgInput, pProcInfo.mediaQ2,"procMsgInput_mgr_resp.357");
                            break;
                        }
                        case CONST_qyCmd_refreshImObjListReq:
                        {
                            qPostMsgAndTrigger(pMsgInput, pProcInfo.schedulerQ);
                            break;
                        }
                        default:
                            bNeedProcess = true;
                            break;
                    }
                }
                break;
                case CONST_imCommType_getCfgsReq:
                case CONST_imCommType_retrieveImGrpListResp:
                case CONST_imCommType_retrieveImGrpMemListResp:
                case CONST_imCommType_retrieveContactListResp:
                case CONST_imCommType_retrieveImObjListEndResp:
                    Log.i(String.format("conversion finished: uiType: %s, Tn: %s", pContent.uiType, pMsgInput.uiTranNo));
                    if (!isRcOk(pMsgInput.usCode)) return iErr;
                    if (0 != qPostMsgAndTrigger(pMsgInput, pProcInfo.schedulerQ)) {
                        Log.e( "qPostMsgAndTrigger failed");
                        return iErr;
                    }
                    break;
                default:
                    bNeedProcess = true;
                    Log.e("procMsgInput_mgr_resp( "+pContent.uiType+" ):  unprocessed imCommType[" + qyGetDesByType1(CONST_qyCmdTable,pMsgInput.usCode_org)+"] - ");
                break;
            }
            if (!bNeedProcess) {
                iErr = 0;
                return iErr;
            }

            iErr = 0;
            return iErr;
        }

        static int procMsgInput_mgr_req(ctxQyMc.CCtxQyMc pQyMcParam, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, int lenInBytes_msg, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            if(pMsgInput.uiType!=CONST_misMsgType_input) return iErr;
            ctxQyMc.CCtxQyMc pQyMc = pQyMcParam;

            String str;
            int len;
            //
            TMP_tHintBuf tHintBuf = new TMP_tHintBuf();
            TMP_tBuf128 tBuf = new TMP_tBuf128();

            if (isUcFlgResp(pMsgInput.ucFlg)) return iErr;

            boolean bNeedProcess = false;

            switch (pMsgInput.usCode) {
                case CONST_qyCmd_talkTo: {
                    qnmCommProc_mis.MIS_MSG_TALK talk = new qnmCommProc_mis.MIS_MSG_TALK();
             
                    talk.uiType = CONST_misMsgType_talk;
                    talk.ucFlg = pMsgInput.ucFlg;
                    talk.usCode = pMsgInput.usCode;
                    talk.tStartTime = pMsgInput.tStartTime;
                    talk.uiTranNo = pMsgInput.uiTranNo;
                    talk.usSeqNo = pMsgInput.usSeqNo;
                    talk.tRecvTime = pMsgInput.tRecvTime;

                    if (null == (pMsgInput.data.buf = qyntohRouteTalkData(pMsgInput.ucFlg,  pMsgInput.buffer, pMsgInput.lenInBytes,
                            pMsgInput.data.route, tHintBuf.tHintBuf, cntof_tHintBuf)))
                    {
                        return iErr;
                    }
                    talk.data.route = pMsgInput.data.route;

                    qnmCommProc_mis.IM_HTML_CONTENT pHtmlContent = (qnmCommProc_mis.IM_HTML_CONTENT) pMsgInput.data.buf;
                    len = Math.min(cntof_imHtmlContent_wBuf * Character.BYTES, (mywcslen(pHtmlContent.wBuf) + 1) * Character.BYTES);
                    talk.lenInBytes = len;
                    talk.data.buf = pHtmlContent;
                    len = talk.lenInBytes;
                    if (0 != qPostMsgAndTrigger(talk, pProcInfo.workQ))
                        return iErr;
                }
                break;
                case CONST_qyCmd_sendTask:
                case CONST_qyCmd_sendTaskReply:
                    if (0 != qPostMsgAndTrigger(pMsgInput, pProcInfo.workQ))
                        return iErr;
                    break;
                default:
                    bNeedProcess = true;
                    break;
            }
            if (!bNeedProcess) {
                iErr = 0;
                return iErr;
            }


//
            qmCommFunc_mis.MIS_CNT pMisCnt = null;
            qnmCommProc_mis.MIS_MSG_routeTalkData pAddrTalkData = null;
            int msgLen = 0;
            qnmCommProc_mis.IM_CONTENTU pContent = null;

            int lenInBytes = 0;
//
            boolean bRemoveIfErr = true;
//  2008/11/19
            qnmCommProc_mis.MSG_ROUTE route;
            byte ucFlg = 0;

            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs = new myDb.QM_dbFuncs();


//if (!pMsgInput->pMisCnt) return -1;

//if (sizeof(pMsgInput->data)  < sizeof(qnmCommProc_mis)  )  {
//	return iErr;
//}


            pMisCnt = pProcInfo.pMisCnt;

            if (isUcFlgResp(pMsgInput.ucFlg)) return iErr;

            if (!pMsgInput.ucbConvrted) {
                msgLen = 216;//(int)Marshal.OffsetOf<MIS_MSG_INPUT>("data");

                if (isUcFlgTalkData(pMsgInput.ucFlg) || isUcFlgRouteTalkData(pMsgInput.ucFlg)) {
                    pAddrTalkData = pMsgInput.data;
                    //
                    if (null == (pMsgInput.data.buf = qyntohRouteTalkData(pMsgInput.ucFlg, pMsgInput.buffer, pMsgInput.lenInBytes, pMsgInput.data.route, tHintBuf.tHintBuf, cntof_tHintBuf))) {
                        return iErr;
                    }
                    //if (tHintBuf[0]) qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", "", 0, "", "", ("    %s"), tHintBuf);
                    //
                    msgLen += 0;
                    //
//                    pContent = (qnmCommProc_mis.IM_CONTENTU) pAddrTalkData.buf;
                    pContent = pMsgInput.data.buf;
                } else {
                    if (0 == pMsgInput.lenInBytes) {
                        return iErr;
                    }
//
//                    if (0 != qyntohRouteTalkData(pMsgInput.ucFlg,  pMsgInput.data, pMsgInput.lenInBytes, null, (qnmCommProc_mis.IM_CONTENTU) pMsg.data, tHintBuf.tHintBuf, cntof_tHintBuf)) {
//                        return iErr;
//                    }
                    //if (tHintBuf[0]) qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", "", 0, "", "", ("    %s"), tHintBuf);
                    //
//                    pContent = (qnmCommProc_mis.IM_CONTENTU) pMsg.data;
                }
                pMsgInput.ucbConvrted = true;
            }

            pAddrTalkData = MACRO_msgRouteTalkData(pMsgInput.ucFlg, pMsgInput.data);
            if (null == pAddrTalkData) {
                return iErr;
            }

            pContent = M_getMsgContent(pMsgInput.uiType, pMsgInput.ucFlg, pMsgInput.data);
            if (null == pContent) {
                return iErr;
            }

            bNeedProcess = false;
            switch (pContent.uiType) {


                case CONST_imCommType_transferFileReq: {
                    qnmCommProc_mis.TRANSFER_FILE_REQ pReq = null;
                    //
                    pMsgInput.lenInBytes = 0;//Math.min((int) sizeof(qnmCommProc_mis), (int) ( + sizeof(TRANSFER_FILE_REQ)));
                    //
                    pReq = (qnmCommProc_mis.TRANSFER_FILE_REQ) pContent;
                    //
                    //  ÏÂÃæÊÇÄÚ²¿´¦Àí±äÁ¿
                    pReq.fullFilePath = pReq.fileName;//, MAX_PATH + 1);
                    //
                    //pMsgInput->uiType = CONST_misMsgType_task;
                    //
                    pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                    pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;
                    //
                    len = (int) ((int) 0 + pMsgInput.lenInBytes);
                    //
                    if (0 != procMsgInput_mc_mgr(pQyMc, pProcInfo, pMsgInput, len, true, pCtx, pChannel))
                        return iErr;
                }
                break;

                case CONST_imCommType_transferFileReplyReq: {
                    qnmCommProc_mis.TRANSFER_FILE_REPLYREQ pReq = null;
                    //
                    pMsgInput.lenInBytes = 0;//Math.min((int) sizeof(qnmCommProc_mis), (int)  + (int) sizeof(TRANSFER_FILE_REPLYREQ));
                    //
                    pReq = (qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContent;
                    //
                    //pMsgInput->uiType = CONST_misMsgType_task;
                    //
                    pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;
                    pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                    //
                    len = (int) ((int) 0 + pMsgInput.lenInBytes);

                    if (0 != procMsgInput_mc_mgr(pQyMc, pProcInfo, pMsgInput, len, true, pCtx, pChannel))
                        return iErr;
                }
                break;

                case CONST_imCommType_transferFileDataReq: {

                    msgLen += 0;//sizeof(TRANSFER_FILEDATA_REQ);
                    if (0 != genericQueue.qyFuncs.qPostMsgAndTrigger(pMsgInput, pProcInfo.robotQ))
                        return iErr;
                }
                break;
                case CONST_imCommType_transferFileEndReq: {

                    //  TRANSFER_FILEEND_REQ    req;

                    Log.i( "transferFileEndReq");

                    //  memset(  &req,  0,  sizeof(  req  )  );
                    //  if  (  stream2Data(  &ptr,  size,  tmpHandler_stream2TransferFileEndReq,  0,  &req  )  )  goto  errLabel;
                    //  memcpy(  pContent,  &req,  min(  sizeof(  pContent[0]  ),  sizeof(  req  )  )  );

                    pMsgInput.lenInBytes = 0;// (int) Math.min(sizeof(qnmCommProc_mis), (int)  + sizeof(TRANSFER_FILEEND_REQ));

                    if (null == pCtx.pProcessQ) return iErr;
                    //
                    //printQ_mis(  &pCtx->pProcessQ->m_q,  0  );

                    //
                    //printMisMsg(null, null, (qnmCommProc_mis.MIS_MSGU) pMsgInput);

                    qnmCommProc_mis.TRANSFER_FILEEND_RESP resp = new qnmCommProc_mis.TRANSFER_FILEEND_RESP();
                    //
                    //mymemset((byte[])resp, 0, sizeof(TRANSFER_FILEEND_RESP));
                    resp.uiType = CONST_imCommType_transferFileEndResp;
                    //  2015/09/08
                    resp.tmpInternal.bChkToRemove_i = true;
                    //
                    if (pCtx.pProcessQ.qTraverse(null, tmpHandler_findOrgMsgAndProc, pMsgInput, resp) <= 0)
                        break;
                    if (resp.tmpInternal.bNeedRemoved_o) {
                        //pCtx->pProcessQ->qRemoveMsg(  tmpHandler_findOrgMsgAndProc_is,  pMsgInput,  0  );
                        qmcApplyForRemovingInvalidTasks(pProcInfo, 0);  //  2015/09/07
                    }

                    //
                    //printQ_mis(  &pCtx->pProcessQ->m_q,  0  );
                }
                break;


                case CONST_imCommType_taskProcReq: {
                    
                    if (((qnmCommProc_mis.TASK_PROC_REQ)pContent).usOp == CONST_imOp_recv_applyForKeyFrame) {
                        str = "get applyForKeyFrame from " + pMsgInput.data.route.idInfo_from.ui64Id + ", tn " + pMsgInput.uiTranNo;
                        Log.d(str);
                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    }
                    pMsgInput.lenInBytes = 0;// Math.min((int) sizeof(qnmCommProc_mis), (int)  + (int) sizeof(TASK_PROC_REQ));

                    if (null == pCtx.pProcessQ) {
                        return iErr;
                    }
                    boolean bBreak = false;
                    qnmCommProc_mis.CIM_CONTENTU contentU = new qnmCommProc_mis.CIM_CONTENTU();
                    contentU.imContentU = new qnmCommProc_mis.TASK_PROC_RESP();
                    qnmCommProc_mis.TASK_PROC_RESP resp = (qnmCommProc_mis.TASK_PROC_RESP) contentU.imContentU;
                    resp.uiType = CONST_imCommType_taskProcResp;
                    qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                    param.pContent_o = contentU;
                    param.bBreak = bBreak;
                    int tmpiRet;
                    tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsgInput, param);

                    if (((qnmCommProc_mis.TASK_PROC_REQ)pContent).usOp == CONST_imOp_send_cancel) {
                        Object pDb = null;
                        qmCommFunc_mis.CE_TASK_MEM taskMem = new qmCommFunc_mis.CE_TASK_MEM();
                        int iStatus = CONST_imTaskStatus_canceledBySender;
                        String subject;
                        String content;

                        //
                        qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                        {
                            if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                            pDb = db.m_pDbMem.pDb;

                            qnmCommProc_mis.IM_TASK_RCD rcd = new qnmCommProc_mis.IM_TASK_RCD();
                            if (!g_dbFuncs.pf_bGetImTaskRcdBySth.call(pDb, pQyMc.cfg.db.iDbType, false, "", pAddrTalkData.route.idInfo_from, ((qnmCommProc_mis.TASK_PROC_REQ)pContent).tStartTime_org, ((qnmCommProc_mis.TASK_PROC_REQ)pContent).uiTranNo_org, 0, rcd))
                                return iErr;

                            //mymemset((byte[])taskMem, 0, sizeof(CE_TASK_MEM));
                            taskMem.iStatus = rcd.iStatus;
                            taskMem.iTaskId = rcd.id;
                            taskMem.idInfo_send.ui64Id = rcd.idInfo_send.ui64Id;
                            taskMem.idInfo_recv.ui64Id = rcd.idInfo_recv.ui64Id;
                            subject = rcd.subject;
                            content = rcd.content;

                            //  2014/11/22
                            switch (taskMem.iStatus) {
                                case CONST_imTaskStatus_applyToRecv:
                                case CONST_imTaskStatus_resp:
                                case CONST_imTaskStatus_waitToRecv:
                                    break;
                                default:
                                    iErr = 0;
                                    return iErr;
                            }

                            //
                            if (0 != myDb.QM_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, taskMem.iTaskId))
                                return iErr;
                            //
                        }
                        //

                        showTaskStatus(pMisCnt, pMsgInput.addr_logicalPeer.idInfo, taskMem.idInfo_send, taskMem.idInfo_recv, false, ((qnmCommProc_mis.TASK_PROC_REQ)pContent).uiContentType_org, taskMem.iTaskId, iStatus, (byte) 0, 0, subject, content)
                        ;

                        //  2015/10/08
                        break;
                    }


                    if (tmpiRet > 0) {
                        if (((qnmCommProc_mis.TASK_PROC_REQ)pContent).usOp == CONST_imOp_recv_applyForChkTaskAlive) {


                            str = String.format("Resp to {%s}, task is alive", pMsgInput.data.route.idInfo_from.ui64Id);
                            showInfo_open.qyFuncs.showInfo_open( str);
                            //
                            //
                            resp.tStartTime_org = ((qnmCommProc_mis.TASK_PROC_REQ)pContent).tStartTime_org;
                            resp.uiTranNo_org = ((qnmCommProc_mis.TASK_PROC_REQ)pContent).uiTranNo_org;
                            resp.usCmd_org = pMsgInput.usCode;
                            resp.usOp_org = ((qnmCommProc_mis.TASK_PROC_REQ)pContent).usOp;
                            //
                            lenInBytes = 128;//sizeof(TASK_PROC_RESP);
                            //
                            ucFlg = 0;
                            ucFlg |= CONST_commFlg_resp;
//                            byte[] data = resp.toByteArray();
                            if (0 != postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, null, CONST_misMsgType_outputTask, ucFlg, (short) CONST_qyRc_ok, pMsgInput.tStartTime, pMsgInput.uiTranNo, (short) 0, resp, lenInBytes, pMsgInput.addr_logicalPeer.idInfo.ui64Id,
                                    pAddrTalkData.route.idInfo_from.ui64Id, pMsgInput.uiChannelType, pCtx.pMsgBuf, false))
                                return iErr;

                            break;
                        }
                    }

                    iErr = 0;
                    return iErr;
                }
                case imCommType_defs.Consts.CONST_imCommType_transferAvInfo: {
                    qnmCommProc_mis.TRANSFER_AV_INFO pReq = null;

                    pMsgInput.lenInBytes = 0;// Math.min((int) sizeof(qnmCommProc_mis), (int) ( + sizeof(qnmCommProc_mis.TRANSFER_AV_INFO)));
                    //
                    pReq = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                    //  2014/09/28
                    if (pReq.idInfo_logicalPeer.ui64Id != 0) {
                        if (pReq.uiTaskType == CONST_imTaskType_viewDynBmp) {

                            //pMsgInput->uiType = CONST_misMsgType_task;
                            pMsgInput.uiTaskType = pReq.uiTaskType;

                            //
                            pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                            pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;
                            //
                            len = pMsgInput.lenInBytes;

                            if (pReq.idInfo_logicalPeer.ui64Id != 0) {
                                if (null == pMisCnt) return iErr;
                                if (pReq.idInfo_logicalPeer.ui64Id == pMisCnt.idInfo.ui64Id) {
                                    str="procMsgInput_other_req failed, viewDynBmp, req.idInfo_logicalPeer is me";
                                    Log.e(str);
                                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                    return iErr;
                                }
                                pMsgInput.addr_logicalPeer.idInfo.ui64Id = pReq.idInfo_logicalPeer.ui64Id;
                            } else {
                                pMsgInput.addr_logicalPeer.idInfo.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                            }

                            if (0 != qPostMsg(pMsgInput, pQyMc.gui.processQ)) {
                                Log.i( "mcThreadProc_mis_work(  ): qPostMsg(  ) failed");
                                return iErr;
                            }
                            guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                            iErr = 0;
                            return iErr;
                        }
                    }

                    //
                    //

                    if (pReq.idInfo_logicalPeer.ui64Id != 0) {
                        if (null == pMisCnt) return iErr;
                        if (pReq.idInfo_logicalPeer.ui64Id == pMisCnt.idInfo.ui64Id) {
                            str="procMsgInput_other_req failed, req.idInfo_logicalPeer is me";
                            Log.e(str);
                            showInfo_open.qyFuncs.showInfo_open(0, null,str);
                            return iErr;
                        }
                        pMsgInput.addr_logicalPeer.idInfo.ui64Id = pReq.idInfo_logicalPeer.ui64Id;
                    }

                    //
                    //pMsgInput->uiType = CONST_misMsgType_task;
                    //
                    pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                    pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;
                    //
                    len = pMsgInput.lenInBytes;
                    //
                    //
                    //if (procMsgTask_mc_mgr(pQyMc, pProcInfo, pMsgInput, len, true, pCtx, pChannel)) return iErr;
                    if (0 != procMsgInput_mc_mgr(pQyMc, pProcInfo, pMsgInput, len, true, pCtx, pChannel))
                        return iErr;
                }
                break;

                case CONST_imCommType_transferAvReplyInfo: {
                    qnmCommProc_mis.TRANSFER_AV_replyInfo pReq = null;
                    //
                    if (pChannel.uiType == CONST_channelType_realTimeMedia) {   //  realTimeMedia
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, NULL, "", "", ("Error: %s ÊÕµ½ %I64u µÄ %s"), qyGetDesByType1(CONST_channelTypeTable, pChannel->uiType), pMsgInput->data.route.idInfo_from.ui64Id, qyGetDesByType1(getResTable(0,pQyMc->cusRes, CONST_resId_qyCommTypeTable), pContent.uiType));
                        return iErr;
                    }
                    //
                    pMsgInput.lenInBytes = 0;// (int) Math.min(sizeof(qnmCommProc_mis), (int) ( + sizeof(qnmCommProc_mis.TRANSFER_AV_replyInfo)));
                    //
                    pReq = (qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent;
                    //
                    //pMsgInput->uiType = CONST_misMsgType_task;
                    //
                    pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;
                    pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                    //
                    len = pMsgInput.lenInBytes;

                    if (0 != procMsgInput_mc_mgr(pQyMc, pProcInfo, pMsgInput, len, true, pCtx, pChannel))
                        return iErr;
                }
                break;


                case CONST_imCommType_transferVideoData: {

                    msgLen += 0;
                    if (0 != q2PostMsgAndTrigger(pMsgInput, pProcInfo.mediaQ2,"procMsgInput_mgr_req.818"))
                        return iErr;
                }
                break;

                case CONST_imCommType_transferAudioData: {
                    //CQyQ2Help q2Help;

                    //msgLen += sizeof(qnmCommProc_mis.TRANSFER_AUDIO_DATA);
                    if (0 != q2PostMsgAndTrigger(pMsgInput, pProcInfo.realTimeMediaQ2,"procMsgInput_mgr_req.827"))
                        return iErr;
                }
                break;


                default:
                    bNeedProcess = true;
                    break;
            }
            if (!bNeedProcess) {
                iErr = 0;
                return iErr;
            }


//
            /**/
            str = String.format("procMsgInput_mgr_req: unprocessed contentType %s", pContent.uiType);
            showInfo_open.qyFuncs.showInfo_open(0, null,str);

            iErr = 0;

            return iErr;
        }

    }
}

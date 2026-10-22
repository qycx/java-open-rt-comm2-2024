package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.Consts.CONST_channelType_robot;
import static com.qycx.base.Consts.CONST_channelType_rtOp;
import static com.qycx.base.genericQueue.qyFuncs.emptyGenericQ;
import static com.qycx.base.genericQueue.qyFuncs.isQFull;
import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.genericQueue.qyFuncs.qPostMsgAndTrigger;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_confReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_getCfgsReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_lStream;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_ptzControlCmd;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_ptzControlReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_remoteAssistReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAudioData;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferVideoData;
import static com.qycx.base.isFunc.qyFuncs.CONST_channelTypeTable;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputTask;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputTask_toMix;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_req;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_routeTalkData;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferVideoData;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_imHtmlContent_wBuf;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_getMsgContent;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.bChannelOk;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_talkData;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_warning;
import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;
import static com.qycx.base.qyq2.qyFuncs.q2AvoidTriggerTooHigh;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;


import com.qycx.base.util.Log;

import java.nio.ByteBuffer;

public class postMsg2MgrFunc {

    public static class qyFuncs {
        static int logCountE = 0;

        public static int postMsg2MgrFunc_mc(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMIS_CNT, qnmCommProc_mis.MSG_ROUTE pRoute, int uiMisMsgType, byte ucFlg, short usCode, long tStartTime, int uiTranNo, short usSeqNo, int iTaskId, int uiTaskType, qnmCommProc_mis.IM_CONTENTU data, int dataLen,
                                             long pIdInfo_logicalPeer1,
                                             long pIdInfo_taskSender,
                                             long pIdInfo_taskReceiver,
                                             long pIdInfo_dst,
                                             int uiChannelType,
                                             qnmCommProc_mis.MIS_MSGU pMsgBuf, boolean bLog) {
            int iErr = -1;

            qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = new qyDefs.QY_MESSENGER_ID();
            idInfo_logicalPeer.ui64Id = pIdInfo_logicalPeer1;

            if (null == pMIS_CNT) {
                Log.i("postMsg2MgrFunc_mc failed: pMisCnt is null.");
                return -1;
            }

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;

            qnmCommProc_mis.MIS_MSGU pMsg = pMsgBuf;
            if (null == pMsg) {
                if (logCountE < 5) {
                    ++logCountE;
                } else {
                    logCountE = 0;
                    Log.w("postMsg2MgrFunc_mc: pMsgBuf is null, need to malloc");
                }
            }

            int len = 0;
            int tmpiRet;

            //  2011/01/23
            boolean bMix = false;
            if (uiMisMsgType == CONST_misMsgType_outputTask_toMix) {
                bMix = true;
                uiMisMsgType = CONST_misMsgType_task;
            }
            if (null == (pMsg = makeMsg2MgrFunc_mc(pMIS_CNT, pRoute, uiMisMsgType, ucFlg, usCode, tStartTime, uiTranNo, usSeqNo, iTaskId, uiTaskType, data, dataLen, idInfo_logicalPeer.ui64Id, pIdInfo_taskSender, pIdInfo_taskReceiver, pIdInfo_dst, uiChannelType,null))) {
                return iErr;
            }

            if (pMsg.uiType == CONST_misMsgType_outputTask) {
                qmCommFunc_mis.MIS_CHANNEL pChannel = null;

                qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                if (null == (pChannel = getChannelByType(pMIS_CNT, task.uiChannelType)))
                    return iErr;

                if (0 > (len = procMsgTask2Output_mc(pProcInfo, pMIS_CNT, task, (int) len, pChannel, ("postMsg2MgrFunc_mc"))))
                    return iErr;

                iErr = 0;
                return iErr;
            }

            //  2011/01/23
            if (bMix) {
                if ((tmpiRet = pProcInfo.processQ_media.qTraverse(ctxCaller, findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                    Log.e(("postMsg2MgrFunc_mc: msg_task_toMix"), tmpiRet != 0 ? "failed" : "not found");
                    return iErr;
                }
                iErr = 0;
                return iErr;
            }

            switch (uiChannelType) {
                case CONST_channelType_robot: {
                    if (0 > (tmpiRet = qPostMsgAndTrigger(pMsg, pProcInfo.robotQ))) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                    break;
                }
                case CONST_channelType_media: {
                    if (0 > (tmpiRet = q2PostMsgAndTrigger(pMsg, pProcInfo.mediaQ2,"postMsg2MgrFunc_mc.126"))) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                    break;
                }
                case CONST_channelType_realTimeMedia: {
                    if (0 > (tmpiRet = q2PostMsgAndTrigger(pMsg, pProcInfo.realTimeMediaQ2,"postMsg2MgrFunc_mc.133"))) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                    break;
                }
                case CONST_channelType_rtOp: {
                    if (0 > (tmpiRet = q2PostMsgAndTrigger(pMsg, pProcInfo.rtOpQ2,"postMsg2MgrFunc_mc.140"))) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                    break;
                }
                default: {

                    if (0 > (tmpiRet = qPostMsgAndTrigger(pMsg, pProcInfo.mgrQ))) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                    break;
                }
            }

            if (bLog) {   //  2010/07/22
                postImMsg2Log_isClient(pMsg, len);
            }

            iErr = 0;
            return iErr;
        }

        public static int postReciveMsg2Mgr(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMIS_CNT, qnmCommProc_mis.MSG_ROUTE pRoute, int uiMisMsgType, qyMsgFunc.QY_MSG_HEAD msgHead, short usSeqNo, int iTaskId, int uiTaskType, qnmCommProc_mis.MIS_MSGU data,
                                            long pIdInfo_logicalPeer1,
                                            long pIdInfo_taskSender,
                                            long pIdInfo_taskReceiver,
                                            long pIdInfo_dst,
                                            int uiChannelType,
                                            qnmCommProc_mis.MIS_MSGU pMsgBuf, boolean bLog)
        {
            int iErr = -1;

            qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = new qyDefs.QY_MESSENGER_ID();
            idInfo_logicalPeer.ui64Id = pIdInfo_logicalPeer1;

            if (null == pMIS_CNT) {
                Log.i("postMsg2MgrFunc_mc failed: pMisCnt is null.");
                return -1;
            }


            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;

            int len = 0;
            int tmpiRet;

            //  2011/01/23
            boolean bMix = false;
            if (uiMisMsgType == CONST_misMsgType_outputTask_toMix) {
                bMix = true;
                uiMisMsgType = CONST_misMsgType_task;
            }
            data.uiType = uiMisMsgType;

            ///todo 这个if不会执行
            if (data.uiType == CONST_misMsgType_outputTask) {
                qmCommFunc_mis.MIS_CHANNEL pChannel = null;

                qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) data;
                if (null == (pChannel = getChannelByType(pMIS_CNT, task.uiChannelType)))
                    return iErr;

                if (0 > (len = procMsgTask2Output_mc(pProcInfo, pMIS_CNT, task, (int) len, pChannel, ("postMsg2MgrFunc_mc"))))
                    return iErr;

                iErr = 0;
                return iErr;
            }

            //  2011/01/23
            if (bMix) {
                if ((tmpiRet = pProcInfo.processQ_media.qTraverse(ctxCaller, findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc, data, 0)) <= 0) {
                    Log.e(("postMsg2MgrFunc_mc: msg_task_toMix"), tmpiRet != 0 ? "failed" : "not found");
                    return iErr;
                }
                iErr = 0;
                return iErr;
            }

            switch (uiChannelType) {
                case CONST_channelType_robot: {
                    if (0 > (tmpiRet = qPostMsgAndTrigger(data, pProcInfo.robotQ))) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                    break;
                }
                case CONST_channelType_media: {
                    if (0 > (tmpiRet = q2PostMsgAndTrigger(data, pProcInfo.mediaQ2,"postReceiveMsg2Mgr.233"))) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                    //
                    //Log.d(Consts.VIDEO_DOWNLOAD,"1 mediaQ2.qs0 " + pProcInfo.mediaQ2.qs0.uiQNodes + " mediaQ2.qs1 " + pProcInfo.mediaQ2.qs1.uiQNodes);
                    //
                    break;
                }
                case CONST_channelType_realTimeMedia: {
                    if (0 > (tmpiRet = q2PostMsgAndTrigger(data, pProcInfo.realTimeMediaQ2,"postReceiveMsg2Mgr.241"))) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                    //
                    //Log.d(Consts.AUDIO_DOWNLOAD,"1 realTimeMediaQ2.qs0 " + pProcInfo.realTimeMediaQ2.qs0.uiQNodes + " realTimeMediaQ2.qs1 " + pProcInfo.realTimeMediaQ2.qs1.uiQNodes);
                    //
                    break;
                }
                case CONST_channelType_rtOp: {
                    if (0 > (tmpiRet = q2PostMsgAndTrigger(data, pProcInfo.rtOpQ2,"postReceiveMsg2Mgr.249"))) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                    break;
                }
                default: {
                    if (0 > (tmpiRet = qPostMsgAndTrigger(data, pProcInfo.mgrQ))) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                    break;
                }
            }

            if (bLog) {   //  2010/07/22
                postImMsg2Log_isClient(data, len);
            }

            iErr = 0;
            return iErr;
        }
        public static int postImMsg2Log_isClient(qnmCommProc_mis.MIS_MSGU pMsg, int lenInBytes_msg) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;

            int tmpiRet = -1;

//            qPostMsg(pMsg, (int) lenInBytes_msg, pProcInfo.imMsgLogQ);
//            byte buf[] = null;
            if (0 != qPostMsgAndTrigger(pMsg, pProcInfo.schedulerQ)) return iErr;

            //
            iErr = 0;

            iErr = 0;

            return iErr;
        }


        public static int makeMsg2MgrFunc_mc(qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MSG_ROUTE pRoute, int uiMisMsgType, byte ucFlg, short usCode, long tStartTime, int uiTranNo, short usSeqNo, int iTaskId, int uiTaskType, Object obj,
                                             long pIdInfo_logicalPeer,
                                             long pIdInfo_taskSender,
                                             long pIdInfo_taskReceiver,
                                             long pIdInfo_dst,
                                             int uiChannelType, qnmCommProc_mis.MIS_MSGU pMsg, int pMsgLen) {
            int iErr = -1;
            int len = 0;

            if (pMisCnt == null) {
                Log.e(("makeMsg2MgrFunc_mc failed, pMisCnt is null"));
                return -1;
            }
            if (null == pMsg) {
                Log.e(("makeMsg2MgrFunc_mc failed, pMsg is null"));
                return -1;
            }
            pMsg.uiType = uiMisMsgType;
            switch (uiMisMsgType) {
                case CONST_misMsgType_input: {
                    qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                    input.ucFlg = ucFlg;
                    input.usCode = usCode;

                    input.tStartTime = tStartTime;
                    input.uiTranNo = uiTranNo;
                    input.usSeqNo = usSeqNo;

                    input.tRecvTime = tmpFunc.qyFuncs.mytime();
                    input.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);
                    input.uiChannelType = uiChannelType;
                    input.uiChannelType_input = uiChannelType;
                    //
                    //  pMsg->input.usDataType;
                    input.lenInBytes = 0;//(int) Math.min(sizeof(qnmCommProc_mis), dataLen);
                    //qyMemoryFunc.qyFuncs.mymemcpy(  input.data, data, input.lenInBytes);
                    //
                    len = (int) ((int) 0 + input.lenInBytes);

                    break;
                }
                case CONST_misMsgType_req: {
                    qnmCommProc_mis.MIS_MSG_REQ req = (qnmCommProc_mis.MIS_MSG_REQ) pMsg;
                    req.ucFlg = ucFlg;
                    req.usCode = usCode;
                    req.tStartTime = tStartTime;
                    req.uiTranNo = uiTranNo;
                    req.usSeqNo = usSeqNo;
                    break;
                }
                case CONST_misMsgType_talk: {
                    qnmCommProc_mis.MIS_MSG_TALK talk = (qnmCommProc_mis.MIS_MSG_TALK) pMsg;
                    talk.uiType = uiMisMsgType;
                    talk.ucFlg = (byte) (ucFlg | CONST_commFlg_talkData);
                    talk.usCode = usCode;
                    talk.tStartTime = tStartTime;
                    talk.uiTranNo = uiTranNo;
                    talk.usSeqNo = usSeqNo;
                    //
                    if (pRoute != null) {
                        //qyMemoryFunc.qyFuncs.mymemcpy(  talk.data.route, pRoute, (int) sizeof(MSG_ROUTE));
                    } else talk.data.route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;
                    //				   
                    talk.data.route.idInfo_to.ui64Id = pIdInfo_dst;
                    talk.addr_logicalPeer.idInfo.ui64Id = pIdInfo_logicalPeer;
                    talk.uiChannelType = uiChannelType;       //  2008/04/17
                    //
//                    len = (int) Math.min(bufSize_routeTalkData, dataLen);
                    //qyMemoryFunc.qyFuncs.mymemcpy( talk.data.buf, data, (int) len);
                    //
                    talk.lenInBytes = (int) ((int) +len);
                    //
                    len = (int) (0 + talk.lenInBytes);

                    break;
                }
                case CONST_misMsgType_task:
                case CONST_misMsgType_outputTask: {
                    qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                    task.uiType = uiMisMsgType;
                    task.ucFlg = (byte) (ucFlg | CONST_commFlg_talkData);
                    task.usCode = usCode;
                    //
                    task.tStartTime = tStartTime;
                    task.uiTranNo = uiTranNo;
                    task.usSeqNo = usSeqNo;
                    //
                    task.iTaskId = iTaskId;
                    task.uiTaskType = uiTaskType;
                    //
                    if (pRoute != null) {
                        //qyMemoryFunc.qyFuncs.mymemcpy(  task.data.route, pRoute, (int) sizeof(MSG_ROUTE));
                    } else task.data.route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;
                    //
                    task.data.route.idInfo_to.ui64Id = pIdInfo_dst;
                    task.addr_logicalPeer.idInfo.ui64Id = pIdInfo_logicalPeer;
                    task.idInfo_taskSender.ui64Id = pIdInfo_taskSender;
                    task.idInfo_taskReceiver.ui64Id = pIdInfo_taskReceiver;

                    if (0 == task.dwTickCnt_start)
                        task.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);   //  2009/03/22
                    //
                    task.uiChannelType = uiChannelType;       //  2008/04/17

                    //
//                    len = (int) Math.min(bufSize_routeTalkData, dataLen);
                    //qyMemoryFunc.qyFuncs.mymemcpy( task.data.buf, data, (int) len);
                    //
                    task.lenInBytes = (int) ((int) +len);
                    //
                    len = (int) ((int) task.lenInBytes);

                    break;
                }
                default: {
                    String str;
                    //str=String.format("makeMsg2MgrFunc_mc failed: unkown msg, %s", qyGetDesByType1(CONST_misMsgTypeTable, pMsg->uiType));
                    Log.e("makeMsg2MgrFunc_mc failed: unkown msgType");
                }
                return iErr;

            }

            //
//            if (0 == len) return iErr;

            iErr = 0;


//            if (0 == iErr) {
//                if (pMsgLen != 0) {
//                    pMsgLen = len;
//                }
//            }

            return iErr;
        }


        //
        //  msgU_i用来当缓冲，避免new了.
        //  如果需要new, 则输入msg_i为null
        //
        public static qnmCommProc_mis.MIS_MSGU makeMsg2MgrFunc_mc(qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MSG_ROUTE pRoute, int uiMisMsgType, byte ucFlg, short usCode, long tStartTime, int uiTranNo, short usSeqNo, int iTaskId, int uiTaskType, qnmCommProc_mis.IM_CONTENTU data, int dataLen,
                                                                  long pIdInfo_logicalPeer,
                                                                  long pIdInfo_taskSender,
                                                                  long pIdInfo_taskReceiver,
                                                                  long pIdInfo_dst,
                                                                  int uiChannelType,
                                                                  qnmCommProc_mis.MIS_MSGU msgU_i) {
            int iErr = -1;
            int len = 0;

            if (pMisCnt == null) {
                Log.e(("makeMsg2MgrFunc_mc failed, pMisCnt is null"));
                return null;
            }

            switch (uiMisMsgType) {
                case CONST_misMsgType_input: {
                    qnmCommProc_mis.MIS_MSG_INPUT input = new qnmCommProc_mis.MIS_MSG_INPUT();
                    input.uiType = uiMisMsgType;
                    input.ucFlg = ucFlg;
                    input.usCode = usCode;
                    input.tStartTime = tStartTime;
                    input.uiTranNo = uiTranNo;
                    input.usSeqNo = usSeqNo;
                    input.tRecvTime = tmpFunc.qyFuncs.mytime();                     //  2008/04/05
                    input.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);       //  2008/05/23
                    input.uiChannelType = uiChannelType;              //  2008/04/17
                    input.uiChannelType_input = uiChannelType;        //  2017/10/03
                    int cls_size = 2444440;//sizeof(MIS_MSG_routeTalkData)
                    input.lenInBytes = Math.min(cls_size, dataLen);
                    //qyMemoryFunc.qyFuncs.mymemcpy(  input.data, data, input.lenInBytes);
//                    input.buffer = ByteBuffer.wrap(data);
                    int offset = 81736;// offsetof(MIS_MSG_INPUT, data);
                    len = offset + input.lenInBytes;
                    return input;
                }
                case CONST_misMsgType_req: {
                    qnmCommProc_mis.MIS_MSG_REQ req = new qnmCommProc_mis.MIS_MSG_REQ();
                    //req.buffer = ByteBuffer.allocate(bufSize_transferVideoData);
                    req.buffer = ByteBuffer.allocate(cntof_imHtmlContent_wBuf);
                    req.uiType = uiMisMsgType;
                    req.ucFlg = ucFlg;
                    req.usCode = usCode;
                    req.tStartTime = tStartTime;
                    req.uiTranNo = uiTranNo;
                    req.usSeqNo = usSeqNo;
                    req.lenInBytes = dataLen;
                    req.data = data;
                    switch (data.uiType)
                    {
                        case CONST_imCommType_getCfgsReq: {
                            qnmCommProc_mis.QIS_getCfgs_req getCfgsReq = (qnmCommProc_mis.QIS_getCfgs_req) data;
                            req.buffer.put(getCfgsReq.toByteArray());
                    //        Log.d("mrgfunc","buffer 1 length is " + getCfgsReq.toByteArray().length);
                            break;
                        }
                        case CONST_imCommType_confReq:
                        {
                            qnmCommProc_mis.CONF_req conf_req = (qnmCommProc_mis.CONF_req)data;
                            req.buffer.put(conf_req.toByteArray());
                     //       Log.d("mrgfunc","buffer 2 length is " +conf_req.toByteArray().length);
                            break;
                        }
                        case CONST_imCommType_lStream:
                        {
                            qnmCommProc_mis.IM_L_STREAM stream = (qnmCommProc_mis.IM_L_STREAM)data;
                            req.buffer.putInt(stream.uiType);
                            req.buffer.putInt(stream.uiContentType);
                            req.buffer.putInt(stream.ulStreamLen);
                            req.buffer.put(stream.buf.array(),0,stream.buf.limit());
                      //      Log.d("mrgfunc","buffer 3 length is " +stream.buf.limit());
                            break;
                        }
                        default:
                            Log.w("unknow uiType :" + req.uiType);
                            break;
                    }
                    return req;
                }
                case CONST_misMsgType_talk: {
                    qnmCommProc_mis.MIS_MSG_TALK talk = new qnmCommProc_mis.MIS_MSG_TALK();
                    talk.uiType = uiMisMsgType;
                    talk.ucFlg = (byte) (ucFlg | CONST_commFlg_talkData);
                    talk.usCode = usCode;
                    talk.tStartTime = tStartTime;
                    talk.uiTranNo = uiTranNo;
                    talk.usSeqNo = usSeqNo;
                    talk.data.route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;
                    talk.data.route.idInfo_to.ui64Id = pIdInfo_dst;
                    talk.addr_logicalPeer.idInfo.ui64Id = pIdInfo_logicalPeer;
                    talk.uiChannelType = uiChannelType;       //  2008/04/17
                    talk.data.buf = data;
                    talk.lenInBytes = Math.min(bufSize_routeTalkData, dataLen);
                    len = talk.lenInBytes;
                    return talk;
                }
                case CONST_misMsgType_task:
                case CONST_misMsgType_outputTask: {
                    qnmCommProc_mis.MIS_MSG_TASK task=null;
                    if  (  msgU_i!=null) {
                        if ( !(msgU_i instanceof qnmCommProc_mis.MIS_MSG_TASK) ) {
                            return null;
                        }
                        task =  (qnmCommProc_mis.MIS_MSG_TASK) msgU_i;
                    }
                    else {
                        //
                        task = new qnmCommProc_mis.MIS_MSG_TASK();
                    }
                    //
                    task.uiType = uiMisMsgType;
                    task.ucFlg = (byte) (ucFlg | CONST_commFlg_talkData);
                    task.usCode = usCode;
                    //
                    task.tStartTime = tStartTime;
                    task.uiTranNo = uiTranNo;
                    task.usSeqNo = usSeqNo;
                    //
                    task.iTaskId = iTaskId;
                    task.uiTaskType = uiTaskType;
                    //
                    if (pRoute != null) {
                        //qyMemoryFunc.qyFuncs.mymemcpy(  task.data.route, pRoute, (int) sizeof(MSG_ROUTE));
                    } else task.data.route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;

                    task.data.route.idInfo_to.ui64Id = pIdInfo_dst;
                    task.addr_logicalPeer.idInfo.ui64Id = pIdInfo_logicalPeer;
                    task.idInfo_taskSender.ui64Id = pIdInfo_taskSender;
                    task.idInfo_taskReceiver.ui64Id = pIdInfo_taskReceiver;

                    if (0 == task.dwTickCnt_start)
                        task.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);   //  2009/03/22
                    //
                    task.uiChannelType = uiChannelType;       //  2008/04/17

                    //
                    len = (int) Math.min(bufSize_routeTalkData, dataLen);
                    //qyMemoryFunc.qyFuncs.mymemcpy( task.data.buf, data, (int) len);
                    task.data.buf = data;
                    task.lenInBytes = (int) ((int) +len);
                    //
                    len = (int) ((int) task.lenInBytes);

                    return task;
                }
                default: {
                    Log.e("makeMsg2MgrFunc_mc failed: unkown msgType");
                    return null;
                }
            }
        }


        //
        public static int procMsgTask2Output_mc(ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSG_TASK pMsgTask, int lenInBytes_msg, qmCommFunc_mis.MIS_CHANNEL pChannel, String tHint) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
            qnmCommProc_mis.IM_CONTENTU pContent = M_getMsgContent(pMsgTask.uiType, pMsgTask.ucFlg, pMsgTask.data);
            if (null == tHint) tHint = "";

            if (pMsgTask.data.route.idInfo_to.ui64Id == 0) {
                Log.i("procMsgTask2Output_mc failed: route.idInfo_to is 0");
                return iErr;
            }
            pMsgTask.data.route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;

            boolean tmp_bChannelOk = bChannelOk(pChannel,false);
            if (!tmp_bChannelOk) {

                //  2015/08/22
                String str = String.format("procMsgTask2Output_mc failed: %s is not ready. %s", qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType), tHint);

                if (logCountF < 6) {
                    ++logCountF;
                } else {
                    showInfo_open.qyFuncs.showInfo_open( str);

                    logCountF = 0;

                }

            }
            if (pContent != null) {
                switch (pContent.uiType) {
                    case CONST_imCommType_transferVideoData:
                    case CONST_imCommType_transferAudioData:
                    case CONST_imCommType_remoteAssistReq:
                    case CONST_imCommType_ptzControlReq:                    //  2012/8/07
                    case CONST_imCommType_ptzControlCmd:
                        if (!tmp_bChannelOk) {
                            return iErr;
                        }
                        break;
                    default:
                        break;
                }
            }


            {

                try//(qyq2.CQyQ2Help q2Help = new qyq2.CQyQ2Help())
                {
                    genericQueue.GENERIC_Q pQ_toPostMsg  =  pChannel.toSendQ2.qs0;
                    if (0 != qPostMsg(pMsgTask, pQ_toPostMsg)) {
                        if (!isQFull(pQ_toPostMsg)) {
                            {
                                return iErr;
                            }
                        } else {
                            switch (pChannel.uiType) {
                                case CONST_channelType_media:
                                case CONST_channelType_realTimeMedia:
                                    emptyGenericQ(pQ_toPostMsg);
                                    showInfo_open.qyFuncs.showInfo_open( ("procMsgTask2Output_mc: emptyQ now"));
                                    break;
                                default:
                                    break;
                            }
                            //  2015/08/22. 队列满时，不能退出，应接着触发发送
                        }
                    }
                } catch (Exception e) {
                    Log.e(e.getMessage());
                    return -1;
                }
            }
            if  (  pChannel.m_b_useQ2_toOutput) {
                int tmpiRet = q2PostMsgAndTrigger(null, pChannel.outputQ21, "procMsgTask2Output_mc.637");
                if (0 == tmpiRet) {
                    //  2008/11/22
                    if (pChannel.outputQ21.ucbTriggerErr) {
                        q2AvoidTriggerTooHigh("", pChannel.outputQ21);
                    }
                }
            }
            else  {
                  pChannel.outputQBuf2.q2PostMsgAndTrigger(null,null,0, 0,"");

            }


            iErr = 0;


            return iErr;
        }

        static int logCountF = 0;

        public static int postMsg2Mgr_mc(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMIS_CNT, qnmCommProc_mis.MSG_ROUTE pRoute, int uiMisMsgType, byte ucFlg, short usCode, long tStartTime, int uiTranNo, int uiSeqNo, qnmCommProc_mis.IM_CONTENTU data, int dataLen, long pIdInfo_logicalPeer, long pIdInfo_dst, int uiChannelType, qnmCommProc_mis.CMIS_MSGU pMsgBuf, boolean bLog) {
            if (pMsgBuf != null) {
                return postMsg2MgrFunc_mc(ctxCaller, pMIS_CNT, pRoute, uiMisMsgType, ucFlg, usCode, tStartTime, uiTranNo, (short) uiSeqNo, 0, 0, data, dataLen, pIdInfo_logicalPeer, 0, 0, pIdInfo_dst, uiChannelType, pMsgBuf.msgU, bLog);
            } else {
                return postMsg2MgrFunc_mc(ctxCaller, pMIS_CNT, pRoute, uiMisMsgType, ucFlg, usCode, tStartTime, uiTranNo, (short) uiSeqNo, 0, 0, data, dataLen, pIdInfo_logicalPeer, 0, 0, pIdInfo_dst, uiChannelType, null, bLog);
            }
        }

        public static int postReciveMsg2Mgr(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMIS_CNT, qnmCommProc_mis.MSG_ROUTE pRoute, int uiMisMsgType, qyMsgFunc.QY_MSG_HEAD msgHead, int uiSeqNo, qnmCommProc_mis.MIS_MSGU data, long pIdInfo_logicalPeer, long pIdInfo_dst, int uiChannelType, qnmCommProc_mis.CMIS_MSGU pMsgBuf, boolean bLog)
        {
            if (pMsgBuf != null) {
                return postReciveMsg2Mgr(ctxCaller, pMIS_CNT, pRoute, uiMisMsgType, msgHead, (short) uiSeqNo, 0, 0, data, pIdInfo_logicalPeer, 0, 0, pIdInfo_dst, uiChannelType, pMsgBuf.msgU, bLog);
            } else {
                return postReciveMsg2Mgr(ctxCaller, pMIS_CNT, pRoute, uiMisMsgType, msgHead, (short) uiSeqNo, 0, 0, data, pIdInfo_logicalPeer, 0, 0, pIdInfo_dst, uiChannelType, null, bLog);
            }
        }

        public static int postMsgTask2Mgr_mc(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMIS_CNT, int uiMisMsgType, byte ucFlg, short usCode, long tStartTime, int uiTranNo, short uiSeqNo, int iTaskId, int uiTaskType, qnmCommProc_mis.IM_CONTENTU data, int dataLen, long pIdInfo_logicalPeer, long pIdInfo_taskSender, long pIdInfo_taskReceiver, long pIdInfo_dst, int uiChannelType, qnmCommProc_mis.MIS_MSGU pMsgBuf, boolean bLog) {
            return postMsg2MgrFunc_mc(ctxCaller, pMIS_CNT, null, uiMisMsgType, ucFlg, usCode, tStartTime, uiTranNo, (short) uiSeqNo, iTaskId, uiTaskType, data, dataLen, pIdInfo_logicalPeer, pIdInfo_taskSender, pIdInfo_taskReceiver, pIdInfo_dst, uiChannelType, pMsgBuf, bLog);
        }

    }
}

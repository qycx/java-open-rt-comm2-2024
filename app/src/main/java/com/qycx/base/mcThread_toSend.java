package com.qycx.base;

import static com.qycx.base.Consts.*;
import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.ctxCaller.Consts.*;
import static com.qycx.base.ctxQmcTmpl.Consts.CONST_fn_dump_pcm_enc;
import static com.qycx.base.findOrgMsgAndProcess_is.qyFuncs.canSend;
import static com.qycx.base.genericQueue.qyFuncs.emptyGenericQ;
import static com.qycx.base.genericQueue.qyFuncs.isQEmpty;
import static com.qycx.base.genericQueue.qyFuncs.qRemoveMsg;
import static com.qycx.base.genericQueue.qyFuncs.qTraverse;
import static com.qycx.base.imCommType_defs.Consts.*;
import static com.qycx.base.isFunc.qyFuncs.CONST_channelTypeTable;
import static com.qycx.base.isProtocolFunc.qyFuncs.qyhtonRouteTalkData;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendMedia;
import static com.qycx.base.qnmCommProc_mis.Consts.*;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_getMsgContent;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.bChannelOk;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.bThread_toSendOk;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.setSessionErr;
import static com.qycx.base.qyComm.Consts.*;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgResp;
import static com.qycx.base.qyDefs.qyFuncs.MACRO_makeCommonParam3;
import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.postMsg2OutputQ_mc;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes_toPostMsg;
import static com.qycx.base.qyq2.qyFuncs.q2AvoidTriggerTooHigh;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;

import com.qycx.base.qnmCommProc_mis.CMIS_MSGU;
import com.qycx.base.statuscenter.messagebean.AudioOutBufSizeEvent;
import com.qycx.base.statuscenter.messagebean.ChangeNetspeedEvent;
import com.qycx.base.statuscenter.messagebean.VideoOutBufSizeEvent;
import com.qycx.base.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;

public class mcThread_toSend {

    public static class qyFuncs {
        public static qyDefs.PF_commonHandler tmpHandler_toSend = (Object p0, Object p1, Object pMsgElemParam) -> {
            int iErr = -1;
            qyDefs.COMMON_PARAM pCommonParam0 = (qyDefs.COMMON_PARAM) p0;
            qyDefs.COMMON_PARAM pCommonParam1 = (qyDefs.COMMON_PARAM) p1;


            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pCommonParam0.p0;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pCommonParam0.p1;
            qmCommFunc_mis.MIS_CHANNEL pChannel = (qmCommFunc_mis.MIS_CHANNEL) pCommonParam0.p2;
            //
            qmCommonFunc_mis.TRAN_INFO_MIS pTranInfo = (qmCommonFunc_mis.TRAN_INFO_MIS) pCommonParam1.p0;
            qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar =(qyShowInfoCommProc.Ctx_thread_tmpVar) pCommonParam1.p2  ;

            qnmCommProc_mis.MIS_MSG_TASK pMsgTask_o = (qnmCommProc_mis.MIS_MSG_TASK) pTranInfo.pMsg_o;

            int lenInBytes = 0;

            if (null == pProcInfo || null == pMisCnt || null == pChannel || null == pMsgTask_o) {
                String str = "tmpHandler_toSend failed: pMisCnt or pChannel or pMsgTask_o is null";
                Log.e(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                return -1;
            }

            if (pChannel.uiType == CONST_channelType_realTimeMedia) {
                //Log.d(AUDIO_UPLOAD, "<stage2.9> tmpHandler_toSend enter" + Thread.currentThread().getName());
            }
            if (pChannel.uiType == CONST_channelType_media) {
                //Log.d(VIDEO_UPLOAD, "<stage2.9> tmpHandler_toSend enter" + Thread.currentThread().getName());
            }

            switch (((qnmCommProc_mis.MIS_MSGU) pMsgElemParam).uiType) {
                case CONST_misMsgType_task:
                case CONST_misMsgType_outputTask: {
                    qnmCommProc_mis.MIS_MSG_TASK pMsgElem = (qnmCommProc_mis.MIS_MSG_TASK) pMsgElemParam;

                    pMsgElem.nTimes_tryToSend++;

                    /*
                    if (pMsgElem.data.route.debugInfo.dwTickCnt_me_start != 0) {
                        pMsgElem.dwTickCnt_start = pMsgElem.data.route.debugInfo.dwTickCnt_me_start;
                        pMsgElem.dwTickCnt_toSend = myGetTickCount(null);
                    }
                    if (pMsgElem.data.route.debugInfo.tTime_start != 0) {
                        if (isUcFlgResp(pMsgElem.ucFlg))
                            pMsgElem.data.route.debugInfo.usElapsed_peer_resp_toSend = (int) (myGetTickCount(null) - pMsgElem.data.route.debugInfo.dwTickCnt_peer_recved);
                        else {
                            pMsgElem.data.route.debugInfo.usElapsed_toSend = (int) (myGetTickCount(null) - pMsgElem.data.route.debugInfo.dwTickCnt_me_start);
                        }
                        pMsgElem.index_debugInfo = pMsgElem.data.route.debugInfo.index;
                    }
                     */

                    pMsgElem.data.route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;

//                    if (pMsgElem.data.buf instanceof qnmCommProc_mis.TRANSFER_VIDEO_DATA) {
//                        return iErr;
//                    }
//
//                    if (pMsgElem.data.buf instanceof qnmCommProc_mis.TRANSFER_AUDIO_DATA) {
//                        return iErr;
//                    }

                    if (pMsgElem.data.buf instanceof qnmCommProc_mis.TRANSFER_AV_replyInfo) {
//                        return iErr;
                        int a = 1;
                    }

                    if  (  pMsgTask_o  ==  null  )  {
                        Log.e("qy","tmpHandler_toSend: pMsgTask_o is null");
                        return  iErr;
                    }

                    //
                    if  (  pChannel.m_b_useQ2_toOutput) {
                        //pMsgTask_o = qnmCommProc_mis.MIS_MSG_TASK.copyHeadInfo(pMsgElem);
                        pMsgTask_o  =  new qnmCommProc_mis.MIS_MSG_TASK();
                        //
                        qnmCommProc_mis.MIS_MSG_TASK.copyHeadInfo(pMsgElem, pMsgTask_o);
                        //
                        pMsgTask_o.buffer=ByteBuffer.allocate(bufSize_transferVideoData);

                    }
                    else  {
                        qnmCommProc_mis.MIS_MSG_TASK.copyHeadInfo(pMsgElem, pMsgTask_o);
                        //
                        pMsgTask_o.buffer.position(0);
                        pMsgTask_o.buffer.limit(pMsgTask_o.buffer.capacity());

                    }

                    //
                    if  (  false) {
                        if (pChannel.uiType == CONST_channelType_realTimeMedia && pMsgElem.data.buf.uiType == CONST_imCommType_transferAudioData) {

                            qnmCommProc_mis.TRANSFER_AUDIO_DATA tad = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pMsgElem.data.buf;
                            byte[] b = tad.buf.array();
                            String fn = "toSend.mp3";
                            //GuiShare.pf_dumpData1.call(0, 0, 0, 0, false, false, fn);
                            guiShare.GuiShare.pf_dumpData1.call(b, (int) tad.uiLen, 0, 0, true, false, fn);

                        }
                    }

                    //pMsgTask_o.buffer = ByteBuffer.allocate(bufSize_transferVideoData);
                    if (0 > (lenInBytes = qyhtonRouteTalkData(tmpVar, pMsgElem.data.route, pMsgElem.data.buf, 0, true, pMsgElem.ucFlg, pMsgTask_o.buffer, null, 0))) {
                        Log.e("tmpHandler_toSend failed: htonRouteTalkData failed");
                        showInfo_open.qyFuncs.showInfo_open("tmpHandler_toSend failed: htonRouteTalkData failed");
                        return iErr;
                    }
                    pMsgTask_o.lenInBytes = (int) lenInBytes;

                    int size = 0;
                    if  (  pChannel.m_b_useQ2_toOutput) {
                        size = getQ2Nodes(pChannel.outputQ21);
                    }
                    else  {
                          size  =  pChannel.outputQBuf2.size();
                    }

                    //
                    if(pChannel.uiType == CONST_channelType_media)
                    {
                        //Log.d(VIDEO_UPLOAD,"2.10 postMsg2OutputQ_mc");
                    }
                    if (0 != postMsg2OutputQ_mc(pMisCnt, pMsgTask_o, lenInBytes, pChannel)) { //在这数据发送。zzq
                        String str ="tmpHandler_toSend failed: postMsg2OutputQ_mc failed";
                        Log.d(str);
                        showInfo_open.qyFuncs.showInfo_open(str);
                        return iErr;
                    }

                    //
                    pMsgElem.tStartTime_processed = pTranInfo.tNow;
                    pMsgElem.uiTranNo_processed = pTranInfo.uiTranNo;

                    pTranInfo.nNodes_processed++;

                    iErr = 0;
                    return iErr;

                }

                default:
                    //qyShowInfo1(CONST_qyShowType_debug, 0, "", pProcInfo.str_who_showInfo, 0, "", "", ("tmpHandler_toSend failed: toSendQÀïÓÐÎ´´¦ÀíµÄÏûÏ¢ %s"), qyGetDesByType1(CONST_misMsgTypeTable, ((qnmCommProc_mis.MIS_MSGU )pMsgElemParam)->uiType));
                    break;

            }


            int uiMaxSendMsgs = pTranInfo.uiMaxSendMsgs != 0 ? pTranInfo.uiMaxSendMsgs : pProcInfo.cfg.uiMaxSendMsgs;   //  2011/01/23
            if (pTranInfo.nNodes_processed >= uiMaxSendMsgs) {
                iErr = 1; return iErr;    //
            }


            iErr = 0;

            return iErr;
        };

        public static int tmpHandler_needRemoveMsg_cli(Object p0, Object p1, Object p2) {
            int iErr = -1;
            //  p0
            qyDefs.COMMON_PARAM pCommonParam1 = (qyDefs.COMMON_PARAM) p1;
            qnmCommProc_mis.MIS_MSGU pMsgElem = (qnmCommProc_mis.MIS_MSGU) p2;
            //
            qmCommonFunc_mis.TRAN_INFO_MIS pTranInfo = (qmCommonFunc_mis.TRAN_INFO_MIS) pCommonParam1.p0;

            switch (pMsgElem.uiType) {
                case CONST_misMsgType_task:
                case CONST_misMsgType_outputTask:
                    break;
                default:
                    Log.i("tmpHandler_needRemoveMsg_cli: msgType error.");
                    return iErr;
            }

            if (((qnmCommProc_mis.MIS_MSG_TASK) pMsgElem).nTimes_tryToSend != 0) {           //  Õâ¸öÌõ¼þÊÇÎªÁËÈ·±£Ö»ÄÜÉ¾³ýÒÑ·¢¹ýµÄ¡£
                //
                //showInfo_open.qyFuncs.showInfo_open(0,"","needRemove msg.id "  + pMsgElem.tmpID);
                //
                iErr = 1;
                return iErr;
            }

            iErr = 0;

            return iErr;
        }

        public static qyDefs.PF_bCommonHandler b_tmpHandler_needRemoveMsg_cli = (Object p0, Object p1, Object p2) -> {
            int iRet = tmpHandler_needRemoveMsg_cli(p0, p1, p2);
            return iRet != 0;
        };


        /*
        public static int tmpInitMsgMixer(ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSG_TASK pMsgTask_av, qnmCommProc_mis.AV_TRAN_INFO pAvTranInfo, taskAv.PROC_TASK_AV pTask, int iContentType, qnmCommProc_mis.MIS_MSG_TASK pMsgTask_toBeMixed, qnmCommProc_mis.MIS_MSG_TASK pMsgMixer) {
            int iErr = -1;
            //
            //Log.d(AUDIO_UPLOAD, "<stage2.410> tmpInitMsgMixer enter uiType : " + pMsgMixer.uiType );
            //
            com.qycx.base.ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "tmpInitMsgMixer");
            if (null == pTaskInfo) {
                Log.d(AUDIO_UPLOAD, "<stage2.41> pTaskInfo null");
                return -1;
            }

            //
            if (0 != pMsgMixer.uiType) {
                Log.d(AUDIO_UPLOAD, "<stage2.41> pMsgMixer.uiType error " + pMsgMixer.uiType );
                return -1;
            }

            pMsgMixer.uiType = CONST_misMsgType_task;
            //pMsgMixer->pMisCnt = pMisCnt;

            pMsgMixer.ucFlg = CONST_commFlg_talkData;  //  CONST_commFlg_talkData;
            pMsgMixer.ucFlg |= CONST_commFlg_routeTalkData;        //  2011/01/26

            pMsgMixer.usCode = CONST_qyCmd_sendMedia;

            long tStartTran = tmpFunc.qyFuncs.mytime();
            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
            pMsgMixer.tStartTime = tStartTran;
            pMsgMixer.uiTranNo = uiTranNo;

            //
            pMsgMixer.uiChannelType = pMsgTask_av.uiChannelType;

            //
            switch (iContentType) {
                case CONST_imCommType_transferAudioData: {
                    qnmCommProc_mis.TRANSFER_AUDIO_DATA pMixer = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pMsgMixer.data.buf;
                    //
                    qnmCommProc_mis.IM_CONTENTU pContent_toBeMixed = M_getMsgContent(pMsgTask_toBeMixed.uiType, pMsgTask_toBeMixed.ucFlg, pMsgTask_toBeMixed.data);
                    if (pContent_toBeMixed.uiType != iContentType) {
                        Log.d(AUDIO_UPLOAD,("tmpInitMsgMixer failed, content_toBeMixed.type != contentType"));
                        return iErr;
                    }
                    pMixer.clearData();
                    pMixer.uiType = CONST_imCommType_transferAudioData;
                    qnmCommProc_mis.TRANSFER_AUDIO_DATA transferAudioData = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent_toBeMixed;
                    pMixer.uiTranNo_openAvDev = transferAudioData.uiTranNo_openAvDev;
                    if (0 == transferAudioData.uiTranNo_openAvDev) {
                        String str ="tmpInitMsgMixer err: pContent_toBeMixed.transferAudioData.uiTranNo_openAvDev is 0";
                        Log.d(str);
                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    }
                    boolean ucbNeedShowFrameInfo = false;
                }
                break;
                case CONST_imCommType_transferVideoData: {
                    //
                    //Log.d(VIDEO_UPLOAD,"2.9 tmpInitMsgMixer");
                    //
                    qnmCommProc_mis.TRANSFER_VIDEO_DATA pMixer = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pMsgMixer.data.buf;
                    qnmCommProc_mis.IM_CONTENTU pContent_toBeMixed = M_getMsgContent(pMsgTask_toBeMixed.uiType, pMsgTask_toBeMixed.ucFlg, pMsgTask_toBeMixed.data);
                    if (pContent_toBeMixed.uiType != iContentType) {
                        Log.e(VIDEO_UPLOAD, "tmpInitMsgMixer failed, content_toBeMixed.type != contentType");
                        return iErr;
                    }
                    qnmCommProc_mis.IM_CONTENTU pContent_av = M_getMsgContent(pMsgTask_av.uiType, pMsgTask_av.ucFlg, pMsgTask_av.data);
                    pMixer.clearDataTo0();
                    pMixer.uiType = (int) iContentType;
                    qnmCommProc_mis.TRANSFER_VIDEO_DATA transferVideoData = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent_toBeMixed;
                    pMixer.uiTranNo_openAvDev = transferVideoData.uiTranNo_openAvDev;

                    if (0 == transferVideoData.uiTranNo_openAvDev) {
                        String str = "tmpInitMsgMixer err: pContent_toBeMixed.transferVideoData.uiTranNo_openAvDev is 0";
                        Log.d(str);
                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    }

                    boolean ucbNeedShowFrameInfo = false;
                }
                break;
                default:
                    break;
            }

            //
            if (pTask.bVideoConference()) {
                if (!pTask.ucbVideoConferenceStarter) {
                    pMsgMixer.data.route.idInfo_to.ui64Id = pMsgTask_av.data.route.idInfo_from.ui64Id;
                } else {
                    pMsgMixer.data.route.idInfo_to.ui64Id = pMsgTask_av.addr_logicalPeer.idInfo.ui64Id;
                }
            } else if (pTask.doProxy.bExists_avProxy) {   //  2014/11/21
                pMsgMixer.data.route.idInfo_to.ui64Id = pMsgTask_av.addr_logicalPeer.idInfo.ui64Id;
            } else {
                pMsgMixer.data.route = pTaskInfo.var.curRoute_sendLocalAv.route;
            }

            //
            //if (pMsgTask_toBeMixed.data.route.debugInfo.dwTickCnt_me_start != 0) {   //  2011/01/24
            //    pMsgMixer.dwTickCnt_start = pMsgTask_toBeMixed.data.route.debugInfo.dwTickCnt_me_start;
            //    pMsgMixer.dwTickCnt_toSend = myGetTickCount(null);       //  2011/01/22
            //}
            /
            /
            //
            //Log.d(AUDIO_UPLOAD, "<stage2.410> tmpInitMsgMixer exit uiType: " + pMsgMixer.uiType );
            //
            iErr = 0;
            return iErr;
        }
*/

    public static int tmpInitMsgMixer(ctxQmc.CCtxQmc pProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSG_TASK pMsgTask_av, qnmCommProc_mis.AV_TRAN_INFO pAvTranInfo, taskAv.PROC_TASK_AV pTask, int iContentType, int uiTranNo_openAvDev,  qnmCommProc_mis.MIS_MSG_TASK pMsgTask_toBeMixed1, qnmCommProc_mis.MIS_MSG_TASK pMsgMixer) {
        int iErr = -1;
        //
        //Log.d(AUDIO_UPLOAD, "<stage2.410> tmpInitMsgMixer enter uiType : " + pMsgMixer.uiType );
        //
        com.qycx.base.ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "tmpInitMsgMixer");
        if (null == pTaskInfo) {
            Log.d(AUDIO_UPLOAD, "<stage2.41> pTaskInfo null");
            return -1;
        }

        //
        if (0 != pMsgMixer.uiType) {
            Log.d(AUDIO_UPLOAD, "<stage2.41> pMsgMixer.uiType error " + pMsgMixer.uiType );
            return -1;
        }

        pMsgMixer.uiType = CONST_misMsgType_task;
        //pMsgMixer->pMisCnt = pMisCnt;

        pMsgMixer.ucFlg = CONST_commFlg_talkData;  //  CONST_commFlg_talkData;
        pMsgMixer.ucFlg |= CONST_commFlg_routeTalkData;        //  2011/01/26

        pMsgMixer.usCode = CONST_qyCmd_sendMedia;

        long tStartTran = tmpFunc.qyFuncs.mytime();
        int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
        pMsgMixer.tStartTime = tStartTran;
        pMsgMixer.uiTranNo = uiTranNo;

        //
        pMsgMixer.uiChannelType = pMsgTask_av.uiChannelType;

        //
        switch (iContentType) {
            case CONST_imCommType_transferAudioData: {
                qnmCommProc_mis.TRANSFER_AUDIO_DATA pMixer = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pMsgMixer.data.buf;
                //
                /*
                qnmCommProc_mis.IM_CONTENTU pContent_toBeMixed = M_getMsgContent(pMsgTask_toBeMixed.uiType, pMsgTask_toBeMixed.ucFlg, pMsgTask_toBeMixed.data);
                if (pContent_toBeMixed.uiType != iContentType) {
                    Log.d(AUDIO_UPLOAD,("tmpInitMsgMixer failed, content_toBeMixed.type != contentType"));
                    return iErr;
                }
                */
                 //
                pMixer.clearData();
                pMixer.uiType = CONST_imCommType_transferAudioData;
                //qnmCommProc_mis.TRANSFER_AUDIO_DATA transferAudioData = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent_toBeMixed;
                pMixer.uiTranNo_openAvDev = uiTranNo_openAvDev;
                if (0 == uiTranNo_openAvDev) {
                    String str ="tmpInitMsgMixer err: pContent_toBeMixed.transferAudioData.uiTranNo_openAvDev is 0";
                    Log.d(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                }
                boolean ucbNeedShowFrameInfo = false;
            }
            break;
            case CONST_imCommType_transferVideoData: {
                //
                //Log.d(VIDEO_UPLOAD,"2.9 tmpInitMsgMixer");
                //
                qnmCommProc_mis.TRANSFER_VIDEO_DATA pMixer = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pMsgMixer.data.buf;
                /*
                qnmCommProc_mis.IM_CONTENTU pContent_toBeMixed = M_getMsgContent(pMsgTask_toBeMixed.uiType, pMsgTask_toBeMixed.ucFlg, pMsgTask_toBeMixed.data);
                if (pContent_toBeMixed.uiType != iContentType) {
                    Log.e(VIDEO_UPLOAD, "tmpInitMsgMixer failed, content_toBeMixed.type != contentType");
                    return iErr;
                }
                */
                 //
                qnmCommProc_mis.IM_CONTENTU pContent_av = M_getMsgContent(pMsgTask_av.uiType, pMsgTask_av.ucFlg, pMsgTask_av.data);
                pMixer.clearDataTo0();
                pMixer.uiType = (int) iContentType;
                //qnmCommProc_mis.TRANSFER_VIDEO_DATA transferVideoData = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent_toBeMixed;
                pMixer.uiTranNo_openAvDev = uiTranNo_openAvDev;

                if (0 == uiTranNo_openAvDev) {
                    String str = "tmpInitMsgMixer err: pContent_toBeMixed.transferVideoData.uiTranNo_openAvDev is 0";
                    Log.d(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                }

                boolean ucbNeedShowFrameInfo = false;
            }
            break;
            default:
                break;
        }

        //
        if (pTask.bVideoConference()) {
            if (!pTask.ucbVideoConferenceStarter) {
                pMsgMixer.data.route.idInfo_to.ui64Id = pMsgTask_av.data.route.idInfo_from.ui64Id;
            } else {
                pMsgMixer.data.route.idInfo_to.ui64Id = pMsgTask_av.addr_logicalPeer.idInfo.ui64Id;
            }
        } else if (pTask.doProxy.bExists_avProxy) {   //  2014/11/21
            pMsgMixer.data.route.idInfo_to.ui64Id = pMsgTask_av.addr_logicalPeer.idInfo.ui64Id;
        } else {
            pMsgMixer.data.route = pTaskInfo.var.curRoute_sendLocalAv.route;
        }

            /*
            if (pMsgTask_toBeMixed.data.route.debugInfo.dwTickCnt_me_start != 0) {   //  2011/01/24
                pMsgMixer.dwTickCnt_start = pMsgTask_toBeMixed.data.route.debugInfo.dwTickCnt_me_start;
                pMsgMixer.dwTickCnt_toSend = myGetTickCount(null);       //  2011/01/22
            }
             */
        //
        //Log.d(AUDIO_UPLOAD, "<stage2.410> tmpInitMsgMixer exit uiType: " + pMsgMixer.uiType );
        //
        iErr = 0;
        return iErr;
    }





        //
        public static qyDefs.PF_commonHandler tmpHandler_toMixer_audio = (Object p0, Object p1, Object pMsgElemParam) -> {
            //
            //Log.d(AUDIO_UPLOAD, "<stage2.6> tmpHandler_toMixer_audio enter>>>");

            if  (  true  ) {
                String str;
                qnmCommProc_mis.MIS_MSGU tmp_pMsg = (qnmCommProc_mis.MIS_MSGU) pMsgElemParam;
                showInfo_open.qyFuncs.showInfo_open(0, "", "tmpHandler_toMixer_audio enters: msg.id " + tmp_pMsg.tmpID);
            }

            //
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //
            qyDefs.COMMON_PARAM pCommonParam0 = (qyDefs.COMMON_PARAM) p0;
            qyDefs.COMMON_PARAM pCommonParam1 = (qyDefs.COMMON_PARAM) p1;

            qyDefs.COMMON_PARAM pCommonParam0_toSend = (qyDefs.COMMON_PARAM) pCommonParam0.p0;
            //qnmCommProc_mis.MIS_MSG_TASK pMsgTask_av = (qnmCommProc_mis.MIS_MSG_TASK)pCommonParam0.p1;
            taskCommon.TASK_common taskElem = (taskCommon.TASK_common) pCommonParam0.p1;

            //
            com.qycx.base.ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
            //mymemset((byte[])ctxCaller, 0, sizeof(CTX_caller));

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskElem.m_index_taskInfo, "tmpHandler_toMixer_audio");
            if (pTaskInfo == null) {
                return -1;
            }
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) {
                return -1;
            }
            //
            qnmCommProc_mis.MIS_MSG_TASK pMsgTask_av = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
            {
                if (pMsgTask_av.uiType != CONST_misMsgType_task) {
                    return -1;
                }

                //CCtxQmc pProcInfo = (ctxQmc.CCtxQmc)pCommonParam0_toSend.p0;
                //MIS_CNT pMisCnt = (MIS_CNT)pCommonParam0_toSend.p1;
                qmCommFunc_mis.MIS_CHANNEL pChannel = (qmCommFunc_mis.MIS_CHANNEL) pCommonParam0_toSend.p2;

                //ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;// (QY_MC)pProcInfo->pQyMc;

                qnmCommProc_mis.IM_CONTENTU pContent_av = pMsgTask_av.data.buf;//(qnmCommProc_mis.IM_CONTENTU) pMsgTask_av.data.buf;
                qnmCommProc_mis.AV_TRAN_INFO pAvTran = null;
                qnmCommProc_mis.AV_TRAN_INFO tmp_avTranInfo;
                taskAv.PROC_TASK_AV pTask = null;

                if (pContent_av.uiType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {
                    //
                    if (!pTaskInfo.var.m_bReplyTask) {
                        qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent_av;
                        if (transferAvInfo.tmp_usSubtype != CONST_transferAvInfo_subtype_task) {
                            return -1;
                        }
                        taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                        //
                        //TRANSFER_AV_task pTransferAvTask = (TRANSFER_AV_task)pContent_av;
                        //
                        pAvTran = transferAvInfo.ass.tranInfo;
                        pTask = task.myTask;// &pTransferAvTask->myTask;
                    } else {

                        //else if (pContent_av->uiType == CONST_imCommType_transferAvReplyInfo)

                        taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                        pTask = task.myTask;// get_transferAvReplyInfo_pTask(pContent_av->transferAvReplyInfo, (""));  //  &pContent_av->transferAvReplyInfo.myTask;
                        if (null == pTask) {
                            return -1;
                        }
                        tmp_avTranInfo = pTask.reply_avTranInfo;
                        pAvTran = tmp_avTranInfo;// &pContent_av->transferAvReplyInfo.tranInfo;

                    }
                }
                if (null == pAvTran) {
                    return -1;
                }


                qmCommonFunc_mis.TRAN_INFO_MIS pTranInfo = (qmCommonFunc_mis.TRAN_INFO_MIS) pCommonParam1.p0;
                qnmCommProc_mis.MIS_MSG_TASK pMsgTask_o = (qnmCommProc_mis.MIS_MSG_TASK) pTranInfo.pMsg_o;

                qnmCommProc_mis.MIS_MSG_TASK pMsgMixer = (qnmCommProc_mis.MIS_MSG_TASK) pCommonParam1.p1;
                qnmCommProc_mis.TRANSFER_AUDIO_DATA pMixer = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pMsgMixer.data.buf;//(qnmCommProc_mis.TRANSFER_AUDIO_DATA) pMsgMixer.data.buf;


                switch (((qnmCommProc_mis.MIS_MSGU) pMsgElemParam).uiType) {
                    case CONST_misMsgType_task: {
                        qnmCommProc_mis.MIS_MSG_TASK pMsgElem = (qnmCommProc_mis.MIS_MSG_TASK) pMsgElemParam;// (qnmCommProc_mis.MIS_MSG_TASK)  pMsgElemParam;
                        //MIS_CNT pMisCntElem = (MIS_CNT)pMsgElem->pMisCnt;
                        qnmCommProc_mis.IM_CONTENTU pContentElem = (qnmCommProc_mis.IM_CONTENTU) pMsgElem.data.buf;//(qnmCommProc_mis.IM_CONTENTU) pMsgElem.data.buf;

                        pMsgElem.nTimes_tryToSend++;

                        switch (pContentElem.uiType) {
                            case CONST_imCommType_transferAudioData: {

                                qnmCommProc_mis.TRANSFER_AUDIO_DATA transferAudioData = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContentElem;
                                if (0 == pMsgMixer.uiType) {
                                    tmpInitMsgMixer(pProcInfo, pMisCnt, pMsgTask_av, pAvTran, pTask, CONST_imCommType_transferAudioData, transferAudioData.uiTranNo_openAvDev,pMsgElem, pMsgMixer);
                                }
                                //Log.d(AUDIO_UPLOAD +" <stage3>  tmpHandler_toMixer_audio mix size is " + pTask.mixer.getSize());
                                if (pMixer.uiTranNo_openAvDev != transferAudioData.uiTranNo_openAvDev) {

                                    if (0 != pMixer.usCnt) {
                                        if (pQyMc.env.bDebugInfo && pProcInfo.cfg.debugStatusInfo.ucbViewAudioStatus) {
                                            //qyShowInfo1(  CONST_qyShowType_detail,  0,  "",  pProcInfo->who_showInfo,  NULL,  (  ""  ),  (  ""  ),  (  "tmpHadler_toMixer: flush mixer data. mixer.usCnt %d, mixer.uiLen %d,  mem.len %d."  ),  pMixer->usCnt,  pMixer->uiLen,  pContentElem->transferAudioData.mems[i].uiLen  );
                                        }
                                        tmpHandler_toSend.call(pCommonParam0_toSend, p1, pMsgMixer); //send  audio ?????? not!!!!
                                    }
                                    pMsgMixer.memset0();
                                    //
                                    if (0 == pMsgMixer.uiType) {
                                        tmpInitMsgMixer(pProcInfo, pMisCnt, pMsgTask_av, pAvTran, pTask, CONST_imCommType_transferAudioData, transferAudioData.uiTranNo_openAvDev,pMsgElem, pMsgMixer);
                                    }

                                }


                                //  È»ºó°ÑÊý¾Ý¼Óµ½»º³åÀï
                                if (pMixer.usCnt > CONST_maxTransferAvDataMems) {                       //  Êµ¼ÊÕâÀïÊÇ²»¶ÔµÄ£¬°´Àí²»ÄÜÓÐÕâÑùµÄÇé¿ö³öÏÖ, ÒòÎª
                                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, null, "", "", ("tmpHadler_toMixer failed:  pMixer->usCnt %d >= mems %d."), pMixer->usCnt, mycountof(pMixer->mems));
                                    return iErr;
                                }
                                if (0 == transferAudioData.usCnt) {
                                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, null, "", "", ("tmpHadler_toMixer:  transferAudioData.usCnt is 0."));
                                    break;
                                }
                                if (transferAudioData.usCnt > CONST_maxTransferAvDataMems) {
                                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, null, "", "", ("tmpHadler_toMixer failed:  transferAudioData.usCnt %d >= mems %d."), pContentElem->transferAudioData.usCnt, mycountof(pContentElem->transferAudioData.mems));
                                    return iErr;
                                }
                                if (transferAudioData.uiLen > bufSize_transferAudioData) {
                                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, null, "", "", ("tmpHadler_toMixer failed:  transferAudioData.uiLen %d too big."), pContentElem->transferAudioData.uiLen);
                                    return iErr;
                                }

                                qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pContentElem_transferAudioData_mems[] =  ((qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContentElem).mems;// (qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead ) pContentElem.transferAudioData.buf_TRANSFER_AUDIO_dataMemHead_mems;
                                int i;
                                qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem;
                                int iPos;

                                for (iPos = 0, i = 0; i < transferAudioData.usCnt; iPos += pContentElem_transferAudioData_mems[i].uiLen, i++) {
                                    //
                                    if (iPos + pContentElem_transferAudioData_mems[i].uiLen > transferAudioData.uiLen) {
                                        Log.i("tmpHandler_toMixer failed: iPos + mems[i].uiLen > content.uiLen");
                                        return iErr;
                                    }

                                    //
                                    if (pMixer.usCnt == CONST_maxTransferAvDataMems
                                            || pContentElem_transferAudioData_mems[i].uiLen + pMixer.uiLen > bufSize_transferVideoData
                                    ) {
                                        if (pMixer.usCnt != 0) {
                                            tmpHandler_toSend.call(pCommonParam0_toSend, p1, pMsgMixer); //send audio???? not!
                                        }

                                        pMsgMixer.memset0();
                                        pMixer = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) (pMsgMixer.data.buf);
                                        if (0 == pMsgMixer.uiType) {
                                            tmpInitMsgMixer(pProcInfo, pMisCnt, pMsgTask_av, pAvTran, pTask, CONST_imCommType_transferAudioData, transferAudioData.uiTranNo_openAvDev,  pMsgElem, pMsgMixer);
                                        }
                                    }

                                    pMem = transferAudioData.mems[i];

                                    if  (  pMem  ==  null  )  {
                                        Log.e("qy","th_toMixer_audio err: tad.mem is null");
                                    }

                                    if (pMem != null) {
                                        pMixer.mems[pMixer.usCnt].copyFrom(pMem);
                                        pMixer.buf.position(pMixer.uiLen);
                                        transferAudioData.buf.position(iPos);
                                        transferAudioData.buf.limit(iPos + pMem.uiLen);
                                        pMixer.buf.put(transferAudioData.buf);                  // to do
                                        pMixer.uiLen += pMem.uiLen;
                                    }

                                    if(true) {
                                        String str;
                                        //str  =  "tmp_mixer_audio: pts " + pMem.uiPts + " old.usCnt "  + transferAudioData.usCnt + " i "  +  i  +  " msg.id: "  +  pMsgElem.tmpID;
                                        str  =  "tmp_mixer_audio: pts " + pMem.uiPts + " old.usCnt "  + transferAudioData.usCnt + " i "  +  i ;
                                        showInfo_open.qyFuncs.showInfo_open(str);

                                    }

                                    pMixer.usCnt++;
                                    //

                                }
                                if (pQyMc.env.bDebugInfo && pProcInfo.cfg.debugStatusInfo.ucbViewAudioStatus) {
                                    //qyShowInfo1(CONST_qyShowType_detail, 0, "", pProcInfo->who_showInfo, null, "", "", ("tmpHadler_toMixer ok: mixer.usCnt %d, mixer.uiLen %d"), pMixer->usCnt, pMixer->uiLen);
                                }

                            }
                            break;
                            default:
                                break;
                        }


                        pMsgElem.tStartTime_processed = pTranInfo.tNow;
                        pMsgElem.uiTranNo_processed = pTranInfo.uiTranNo;

                        pTranInfo.nNodes_processed++;

                    }
                    break;
                    default:
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, null, "", "", ("ÔÚtmpHandler_toMixerÀï³öÏÖÁËÎ´´¦ÀíµÄÏûÏ¢ÀàÐÍ, %s"), qyGetDesByType1(CONST_misMsgTypeTable, ((qnmCommProc_mis.MIS_MSGU )pMsgElemParam)->uiType));
                        return iErr;

                }

                if (pTranInfo.nNodes_processed >= pProcInfo.cfg.uiMaxSendMsgs) {
                    iErr = 1;
                    return iErr;
                }
            }

            iErr = 0;

            return iErr;
        };

        /***
         * 可以把多个视频帧放在一个数据包里发出。
         */

        //
        public static qyDefs.PF_commonHandler tmpHandler_toMixer_video = (Object p0, Object p1, Object pMsgElemParam) -> {
            int iErr = -1;

            //Log.d(VIDEO_UPLOAD,"2.8 tmpHandler_toMixer_video");
            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //
            qyDefs.COMMON_PARAM pCommonParam0 = (qyDefs.COMMON_PARAM) p0;
            qyDefs.COMMON_PARAM pCommonParam1 = (qyDefs.COMMON_PARAM) p1;

            qyDefs.COMMON_PARAM pCommonParam0_toSend = (qyDefs.COMMON_PARAM) pCommonParam0.p0;
            taskCommon.TASK_common taskElem = (taskCommon.TASK_common) pCommonParam0.p1;

            //
            com.qycx.base.ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskElem.m_index_taskInfo, "tmpHandler_toMixer_video");
            if (pTaskInfo == null) {
                return -1;
            }
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) {
                return -1;
            }

            //
            qnmCommProc_mis.MIS_MSG_TASK pMsgTask_av = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
            {
                if (pMsgTask_av.uiType != CONST_misMsgType_task) {
                    return -1;
                }

                qmCommFunc_mis.MIS_CHANNEL pChannel = (qmCommFunc_mis.MIS_CHANNEL) pCommonParam0_toSend.p2;
                qnmCommProc_mis.IM_CONTENTU pContent_av = pMsgTask_av.data.buf;
                qnmCommProc_mis.AV_TRAN_INFO pAvTran = null;
                qnmCommProc_mis.AV_TRAN_INFO tmp_avTranInfo;
                taskAv.PROC_TASK_AV pTask = null;

                if (pContent_av.uiType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {
                    if (!pTaskInfo.var.m_bReplyTask) {
                        taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                        qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent_av;
                        pAvTran = transferAvInfo.ass.tranInfo;
                        pTask = task.myTask;
                        if (null == pTask) {
                            return -1;
                        }
                    } else {
                        taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;

                        pTask = task.myTask;
                        if (null == pTask) return -1;
                        tmp_avTranInfo = pTask.reply_avTranInfo;
                        pAvTran = tmp_avTranInfo;
                    }
                }
                if (null == pAvTran) {
                    return -1;
                }

                qmCommonFunc_mis.TRAN_INFO_MIS pTranInfo = (qmCommonFunc_mis.TRAN_INFO_MIS) pCommonParam1.p0;
                qnmCommProc_mis.MIS_MSG_TASK pMsgTask_o = (qnmCommProc_mis.MIS_MSG_TASK) pTranInfo.pMsg_o;

                qnmCommProc_mis.MIS_MSG_TASK pMsgMixer = (qnmCommProc_mis.MIS_MSG_TASK) pCommonParam1.p1;
                qnmCommProc_mis.TRANSFER_VIDEO_DATA pMixer = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) (pMsgMixer.data.buf);

                switch (((qnmCommProc_mis.MIS_MSGU) pMsgElemParam).uiType) {
                    case CONST_misMsgType_task: {
                        qnmCommProc_mis.MIS_MSG_TASK pMsgElem = (qnmCommProc_mis.MIS_MSG_TASK) pMsgElemParam;
                        qnmCommProc_mis.IM_CONTENTU pContentElem = pMsgElem.data.buf;

                        pMsgElem.nTimes_tryToSend++;

                        switch (pContentElem.uiType) {
                            case CONST_imCommType_transferVideoData: {
                                qnmCommProc_mis.TRANSFER_VIDEO_DATA transferVideoData = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContentElem;

                                if (0 == pMsgMixer.uiType) {
                                    tmpInitMsgMixer(pProcInfo, pMisCnt, pMsgTask_av, pAvTran, pTask, CONST_imCommType_transferVideoData, transferVideoData.uiTranNo_openAvDev,  pMsgElem, pMsgMixer);
                                    //
                                }
                                if (pMixer.uiTranNo_openAvDev != transferVideoData.uiTranNo_openAvDev) {
                                    if (0 != pMixer.usCnt) {
                                        if (pQyMc.env.bDebugInfo && pProcInfo.cfg.debugStatusInfo.ucbViewVideoStatus) {

                                        }
                                        //Log.d(VIDEO_UPLOAD, "<stage3> tmpHandler_toMixer_video pTask.photomosaic. size is  " +  pTask.photomosaic.getSize() );
                                        tmpHandler_toSend.call(pCommonParam0_toSend, p1, pMsgMixer); //zzq,send video 发送打包好的视频数据
                                    }
                                    pMsgMixer.memset0();
                                    if (0 == pMsgMixer.uiType) {
                                        tmpInitMsgMixer(pProcInfo, pMisCnt, pMsgTask_av, pAvTran, pTask, CONST_imCommType_transferVideoData, transferVideoData.uiTranNo_openAvDev,  pMsgElem, pMsgMixer);
                                    }
                                }

                                if (pMixer.usCnt > CONST_maxTransferAvDataMems) {

                                    return iErr;
                                }
                                if (0 == transferVideoData.usCnt) {

                                    break;
                                }
                                if (transferVideoData.usCnt > CONST_maxTransferAvDataMems) {

                                    return iErr;
                                }
                                if (transferVideoData.uiLen > bufSize_transferAudioData) {
                                    return iErr;
                                }

                                qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pContentElem_transferVideoData_mems[] = transferVideoData.mems;
                                int i;
                                qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem;
                                int iPos;

                                for (iPos = 0, i = 0; i < transferVideoData.usCnt; iPos += pContentElem_transferVideoData_mems[i].uiLen, i++) {
                                    //
                                    if (iPos + pContentElem_transferVideoData_mems[i].uiLen > transferVideoData.uiLen) {
                                        return iErr;
                                    }

                                    //
                                    if (pMixer.usCnt == CONST_maxTransferAvDataMems
                                            || pContentElem_transferVideoData_mems[i].uiLen + pMixer.uiLen > bufSize_transferVideoData
                                        //||  pContentElem->transferVideoData.stat.tSelectTime		//  2011/01/25. 对需要对方发送响应包的请求包，应改独立发送。所以先发送其他的包，然后独立发送此包
                                    ) {
                                        if (pMixer.usCnt != 0) {
                                            tmpHandler_toSend.call(pCommonParam0_toSend, p1, pMsgMixer); //send video????
                                        }

                                        pMsgMixer.memset0();
                                        if (0 == pMsgMixer.uiType) {
                                            tmpInitMsgMixer(pProcInfo, pMisCnt, pMsgTask_av, pAvTran, pTask, CONST_imCommType_transferVideoData, transferVideoData.uiTranNo_openAvDev,pMsgElem, pMsgMixer);
                                        }
//                                        pMixer = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) (pMsgMixer.data.buf);
                                    }

                                    pMem = transferVideoData.mems[i];

                                    if (pMem != null) {
                                        pMixer.mems[i] = pMem;
                                        pMixer.buf.position(pMixer.uiLen);
                                        transferVideoData.buf.position(iPos);
                                        transferVideoData.buf.limit(iPos + pMem.uiLen);
                                        pMixer.buf.put(transferVideoData.buf);
                                        pMixer.uiLen += pMem.uiLen;
                                    }

                                    //
                                    showInfo_open.qyFuncs.showInfo_open(0,"","tmp_mixer_video: pts "  +  pMem.uiPts);


                                    //
                                    pMixer.usCnt++;

                                }

                                if (pQyMc.env.bDebugInfo && pProcInfo.cfg.debugStatusInfo.ucbViewVideoStatus) {

                                }

                            }
                            break;
                            default:
                                break;
                        }


                        pMsgElem.tStartTime_processed = pTranInfo.tNow;
                        pMsgElem.uiTranNo_processed = pTranInfo.uiTranNo;

                        pTranInfo.nNodes_processed++;

                    }
                    break;
                    default:
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, null, "", "", ("ÔÚtmpHandler_toMixerÀï³öÏÖÁËÎ´´¦ÀíµÄÏûÏ¢ÀàÐÍ, %s"), qyGetDesByType1(CONST_misMsgTypeTable, ((qnmCommProc_mis.MIS_MSGU )pMsgElemParam)->uiType));
                        return iErr;
                }

                if (pTranInfo.nNodes_processed >= pProcInfo.cfg.uiMaxSendMsgs) {
                    iErr = 1;
                    return iErr;
                }
            }

            iErr = 0;

            return iErr;
        };


        public static mcSharedQ.PF_enumTask tmpHandler_toSendMedia = (ctxCaller.CTX_caller ctxCaller,
                                                                      qnmCommProc_mis.MIS_MSGU param_0_unused,
                                                                      qyMcMainProc.PARAM_findOrgMsg param_1,
                                                                      taskCommon.TASK_common taskElemParam) -> {
            int iErr = -1;

            //
            boolean bDbg = false;

            qyDefs.COMMON_PARAM pCommonParam0 = param_1.commonParam0;
            qyDefs.COMMON_PARAM pCommonParam1 = param_1.commonParam1;
            qyDefs.COMMON_PARAM p0 = pCommonParam0;
            qyDefs.COMMON_PARAM p1 = pCommonParam1;

            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pCommonParam0.p0;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pCommonParam0.p1;
            qmCommFunc_mis.MIS_CHANNEL pChannel = (qmCommFunc_mis.MIS_CHANNEL) pCommonParam0.p2;
            //
            qmCommonFunc_mis.TRAN_INFO_MIS pTranInfo = (qmCommonFunc_mis.TRAN_INFO_MIS) pCommonParam1.p0;
            qnmCommProc_mis.MIS_MSG_TASK pMsgTask_o = (qnmCommProc_mis.MIS_MSG_TASK) pTranInfo.pMsg_o;

            qnmCommProc_mis.MIS_MSG_TASK pMsgMixer = (qnmCommProc_mis.MIS_MSG_TASK) pCommonParam1.p1;

            qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar =(qyShowInfoCommProc.Ctx_thread_tmpVar) pCommonParam1.p2  ;

            qnmCommProc_mis.IM_CONTENTU pContent_mixer = pMsgMixer.data.buf;

            qyDefs.COMMON_PARAM commonParam0 = new qyDefs.COMMON_PARAM();
            int tmpiRet = -1;
            if (pChannel.uiType == CONST_channelType_realTimeMedia) {
                //Log.d(AUDIO_UPLOAD, "<stage2.4> enter tmpHandler_toSendMedia > uiType:" + pMsgMixer.uiType + " name:" + Thread.currentThread().getName());
            }
            if (pChannel.uiType == CONST_channelType_media) {
                //Log.d(VIDEO_UPLOAD, "2.6 enter tmpHandler_toSendMedia > uiType:" + pMsgMixer.uiType + " name:"+ Thread.currentThread().getName());
            }

            if (null == pProcInfo || null == pMisCnt || null == pChannel || null == pMsgTask_o || null == pMsgMixer) {
                Log.d("tmpHandler_toSendMedia failed. params error.");
                return -1;
            }

            taskCommon.TASK_common taskElem = taskElemParam;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskElem.m_index_taskInfo, "tmpHandler_toSendMedia");
            if (pTaskInfo == null) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;

            qnmCommProc_mis.MIS_MSGU pMsgElemParam = pTaskData.msgU;
            {
                switch (pMsgElemParam.uiType) {
                    case CONST_misMsgType_task: {
                        qnmCommProc_mis.MIS_MSG_TASK pMsgElem = (qnmCommProc_mis.MIS_MSG_TASK) pMsgElemParam;
                        qnmCommProc_mis.IM_CONTENTU pContentElem = pMsgElem.data.buf;
                        {
                            if (pChannel.uiType == CONST_channelType_realTimeMedia) {
                                qnmCommProc_mis.AV_TRAN_INFO pAvTranInfo = null;
                                qnmCommProc_mis.AV_TRAN_INFO tmp_avTranInfo;
                                taskAv.PROC_TASK_AV pTask = null;

                                if (pContentElem.uiType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {
                                    if (!pTaskInfo.var.m_bReplyTask) {
                                        taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;

                                        pAvTranInfo = ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).ass.tranInfo;
                                        pTask = task.myTask;
                                        if (null == pTask) return iErr;
                                    } else {
                                        taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                                        pTask = task.myTask;
                                        if (null == pTask) return iErr;
                                        tmp_avTranInfo = pTask.reply_avTranInfo;
                                        pAvTranInfo = tmp_avTranInfo;
                                    }
                                }

                                //
                                if (null != pAvTranInfo) {

                                    //
                                    if  (  pProcInfo.cfg.b_useQ2_toEncA) {

                                        //
                                        try //(qyq2.CQyQ2Help q2Help = new qyq2.CQyQ2Help())
                                        {
                                            genericQueue.GENERIC_Q pOutputQ_audio = null;//q2Help.getQ_toGetMsg(pTask.mixer.pOutputQ2);
                                            if (null == pOutputQ_audio) {
                                                iErr = 0;
                                                return iErr;
                                            }
                                            pMsgMixer.memset0();
                                            MACRO_makeCommonParam3(p0, taskElem, 0, commonParam0);
                                            tmpiRet = qTraverse(pOutputQ_audio, tmpHandler_toMixer_audio, commonParam0, p1);
                                            if (tmpiRet < 0) {
                                                emptyGenericQ(pOutputQ_audio);
                                                iErr = 0;
                                                Log.d(AUDIO_UPLOAD, " <stage2.611> zzq audio  mix error, the data will be cleared: " + tmpiRet);
                                                return iErr;
                                            }

                                            if (pMsgMixer.uiType == CONST_misMsgType_task
                                                    && pContent_mixer.uiType == CONST_imCommType_transferAudioData
                                                    && ((qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent_mixer).usCnt != 0) {
                                                //Log.d(AUDIO_UPLOAD, " <stage2.9> tmpHandler_toSend");
                                                int tmpi = tmpHandler_toSend.call(p0, p1, pMsgMixer); //send audio data. zzq ?????
                                                if (tmpi < 0) {
                                                    Log.d(AUDIO_UPLOAD, " <stage2.612>tmpHandler_toSendMedia: tmpHandler_toSend>  failed " + tmpi);
                                                }
                                            } else {
                                                boolean b1 = pMsgMixer.uiType == CONST_misMsgType_task;
                                                //Log.d(AUDIO_UPLOAD, " <stage2.7> tmpHandler_toSend audio other error: b1:"  + b1 + " uiType:"+pMsgMixer.uiType);
                                            }

                                            if (qRemoveMsg(pOutputQ_audio, b_tmpHandler_needRemoveMsg_cli, 0, p1) < 0) {
                                                Log.i("qRemoveMsg skipped.");
                                            }

                                            if (pTranInfo.nNodes_processed >= pProcInfo.cfg.uiMaxSendMsgs) {
                                                //                                        Log.d(AUDIO_UPLOAD, " <stage2.613>tmpHandler_toSendMedia: nNodes_processed >  uiMaxSendMsgs: " + pTranInfo.nNodes_processed  +" > " +  pProcInfo.cfg.uiMaxSendMsgs);

                                                iErr = 1;
                                                return iErr;
                                            }

                                            iErr = 0;
                                            return iErr;

                                        } catch (Exception e) {
                                            Log.e(e.getMessage());
                                            Log.e(AUDIO_UPLOAD, " <stage2.614> tmpHandler_toSendMedia: exception ");
                                            return -1;
                                        }
                                    }
                                    else  {
                                          //
                                          if  (  tmpVar.m_ctxThreadtoSend.a==null)  {
                                              showInfo_open.qyFuncs.showInfo_open("toSendMedia failed: ctxThreadToSend.a is null");
                                              return  -1;
                                          }
                                          qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead head  =  tmpVar.m_ctxThreadtoSend.a.tmp_audio_head;//new qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead();
                                          byte[] audio =  tmpVar.m_ctxThreadtoSend.a.tmp_audio_buf;//new byte[bufSize_decObj_a_mediaCodec_tmpDataBuf];
                                          for  (  ; ;) {
                                              if (pTranInfo.nNodes_processed >= pProcInfo.cfg.uiMaxSendMsgs)
                                                  break;
                                              //
                                              int tmpLen = 0;
                                              tmpLen  =  pProcInfo.pOutputQBuf2_local_a.q2GetMsg(head,audio,0,audio.length);
                                              //tmpLen = pTask.mixer.pOutputQBuf2.q2GetMsg(head, audio, 0, audio.length);
                                              if (tmpLen <= 0) break;

                                              //
                                              if  (  true ) {
                                                  //Log.d("qy", "mixer.pOutputQBuf2.q2GetMsg returns tmpLen:" + tmpLen + " head.len: " + head.uiLen  +  " st "  +  head.uiSampleTimeInMs  +  " pts "  +  head.uiPts);
                                              }

                                              //
                                              if  (  false  ) {
                                                  String fn = "toSendMedia.mp3";
                                                  //GuiShare.pf_dumpData1.call(0, 0, 0, 0, false, false, fn);
                                                  guiShare.GuiShare.pf_dumpData1.call(audio, (int) head.uiLen, 0, 0, true, false, fn);
                                              }


                                              //
                                              pTranInfo.nNodes_processed++;


                                              //
                                              qnmCommProc_mis.MIS_MSG_TASK pMsgTask_av = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;

                                              if (pMsgTask_av.uiType != CONST_misMsgType_task) {
                                                  return -1;
                                              }


                                              //ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;// (QY_MC)pProcInfo->pQyMc;

                                              qnmCommProc_mis.IM_CONTENTU pContent_av = pMsgTask_av.data.buf;//(qnmCommProc_mis.IM_CONTENTU) pMsgTask_av.data.buf;
                                              qnmCommProc_mis.AV_TRAN_INFO pAvTran = null;
                                              //qnmCommProc_mis.AV_TRAN_INFO tmp_avTranInfo;
                                              //taskAv.PROC_TASK_AV pTask = null;

                                              //
                                              pMsgMixer.memset0();

                                              //
                                              tmpInitMsgMixer(pProcInfo, pMisCnt, pMsgTask_av, pAvTranInfo, pTask, CONST_imCommType_transferAudioData, head.uiTranNo_openAvDev, null, pMsgMixer);

                                              qnmCommProc_mis.TRANSFER_AUDIO_DATA tad = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent_mixer;
                                              tad.uiTranNo_openAvDev = head.uiTranNo_openAvDev;
                                              tad.usCnt = 1;
                                              tad.mems[0].copyFrom(head);
                                              System.arraycopy(audio, 0, tad.buf.array(), 0, head.uiLen);
                                              tad.buf.position(head.uiLen);
                                              tad.uiLen = head.uiLen;

                                              //
                                              int tmpi = tmpHandler_toSend.call(p0, p1, pMsgMixer); //send audio data. zzq ?????

                                              //
                                              continue;
                                          }

                                    }
                                }
                            }

                            if (pChannel.uiType == CONST_channelType_media) {
                                qnmCommProc_mis.AV_TRAN_INFO pAvTranInfo = null;
                                qnmCommProc_mis.AV_TRAN_INFO tmp_avTranInfo;
                                taskAv.PROC_TASK_AV pTask = null;

                                if (pContentElem.uiType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {
                                    if (!pTaskInfo.var.m_bReplyTask) {
                                        taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                                        pAvTranInfo = ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).ass.tranInfo;
                                        pTask = task.myTask;
                                        if (null == pTask) return iErr;
                                    } else {
                                        taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                                        pTask = task.myTask;
                                        if (null == pTask) return iErr;
                                        tmp_avTranInfo = pTask.reply_avTranInfo;
                                        pAvTranInfo = tmp_avTranInfo;
                                    }
                                }

                                if (null != pAvTranInfo) {

                                    //
                                    if  (  pProcInfo.cfg.b_useQ2_toEncV) {

                                        try //(qyq2.CQyQ2Help q2Help = new qyq2.CQyQ2Help())
                                        {
                                            genericQueue.GENERIC_Q pOutputQ_video = null;//q2Help.getQ_toGetMsg(pTask.photomosaic.pOutputQ2);
                                            if (null == pOutputQ_video) {
                                                iErr = 0;
                                                return iErr;
                                            }
                                            //
                                            //Log.d(VIDEO_UPLOAD,"2.7 q2Help.getQ_toGetMsg(pTask.photomosaic.pOutputQ2)");

                                            pMsgMixer.memset0();

                                            MACRO_makeCommonParam3(p0, taskElem, 0, commonParam0);
                                            tmpiRet = qTraverse(pOutputQ_video, tmpHandler_toMixer_video, commonParam0, p1);

                                            if (tmpiRet < 0) {
                                                emptyGenericQ(pOutputQ_video);
                                                iErr = 0;
                                                return iErr;
                                            }

                                            if (pMsgMixer.uiType == CONST_misMsgType_task
                                                    && pContent_mixer.uiType == CONST_imCommType_transferVideoData
                                                    && ((qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent_mixer).usCnt != 0) {
                                                tmpHandler_toSend.call(p0, p1, pMsgMixer); //send video data zzq??????
                                            } else {
                                                boolean b1 = pMsgMixer.uiType == CONST_misMsgType_task;
                                                //Log.d(VIDEO_UPLOAD, " <stage2.7> video other error: b1:"  + b1 +  " uiType:" + pMsgMixer.uiType);
                                            }

                                            if (qRemoveMsg(pOutputQ_video, b_tmpHandler_needRemoveMsg_cli, 0, p1) < 0) {
                                                Log.i("Çå³ýÀ¬»ø°ü³ö´í¡£skipped.");
                                            }

                                            if (pTranInfo.nNodes_processed >= pProcInfo.cfg.uiMaxSendMsgs) {
                                                iErr = 1;
                                                return iErr;
                                            }

                                            iErr = 0;
                                            return iErr;
                                        } catch (Exception e) {
                                            Log.e(e.getMessage());
                                            return -1;
                                        }

                                    }
                                    else  {
                                          //
                                        if  (  tmpVar.m_ctxThreadtoSend.v  ==  null)  {
                                            showInfo_open.qyFuncs.showInfo_open("toSendMedia failed: ctxThreadToSend.v is null");
                                            return  -1;
                                        }
                                        qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead head  =  tmpVar.m_ctxThreadtoSend.v.tmp_video_head;//new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();
                                        byte[] video =  tmpVar.m_ctxThreadtoSend.v.tmp_video_buf;  //new byte[bufSize_decObj_mediaCodec_tmpDataBuf];
                                        for  (  ; ;) {
                                            if (pTranInfo.nNodes_processed >= pProcInfo.cfg.uiMaxSendMsgs)
                                                break;

                                            //
                                            //if  (  pTask.photomosaic.pOutputQBuf2.size() !=  0  )
                                            {
                                                test.testCls_Droid.testBreak();
                                            }

                                            qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, pMsgElem.index_sharedObj);
                                            if (null == pSharedObj) {
                                                break;
                                            }

                                            //
                                            boolean bSend = true;
                                            //
                                            bSend = canSend(pSharedObj); // 判断能否发言。
                                            if  (  !bSend)  {
                                                //Log.d("qy", "no send");
                                                break;
                                            }

                                            //
                                            int tmpLen = 0;
                                            //tmpLen = pTask.photomosaic.pOutputQBuf2.q2GetMsg(head, video, 0, video.length);
                                            tmpLen=pProcInfo.pOutputQBuf2_local_v.q2GetMsg(head,video,0,video.length);
                                            if (tmpLen <= 0) break;


                                            //
                                            if  (  true  ) {
                                                //Log.d("qy", "photomosaic: q2GetMsg returns tmpLen:" + tmpLen + " head.len: " + head.uiLen  +  " pts "  +  head.uiPts);
                                            }


                                            //
                                            pTranInfo.nNodes_processed++;


                                            //
                                            qnmCommProc_mis.MIS_MSG_TASK pMsgTask_av = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;

                                            if (pMsgTask_av.uiType != CONST_misMsgType_task) {
                                                return -1;
                                            }


                                            //ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;// (QY_MC)pProcInfo->pQyMc;

                                            qnmCommProc_mis.IM_CONTENTU pContent_av = pMsgTask_av.data.buf;//(qnmCommProc_mis.IM_CONTENTU) pMsgTask_av.data.buf;
                                            qnmCommProc_mis.AV_TRAN_INFO pAvTran = null;
                                            //qnmCommProc_mis.AV_TRAN_INFO tmp_avTranInfo;
                                            //taskAv.PROC_TASK_AV pTask = null;

                                            //
                                            pMsgMixer.memset0();

                                            //
                                            tmpInitMsgMixer(pProcInfo, pMisCnt, pMsgTask_av, pAvTranInfo, pTask, CONST_imCommType_transferVideoData, head.uiTranNo_openAvDev, null, pMsgMixer);

                                            qnmCommProc_mis.TRANSFER_VIDEO_DATA tvd = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent_mixer;
                                            tvd.uiTranNo_openAvDev = head.uiTranNo_openAvDev;
                                            tvd.usCnt = 1;
                                            tvd.mems[0].copyFrom(head);
                                            System.arraycopy(video, 0, tvd.buf.array(), 0, head.uiLen);
                                            tvd.buf.position(head.uiLen);
                                            tvd.uiLen = head.uiLen;

                                            //
                                            int tmpi = tmpHandler_toSend.call(p0, p1, pMsgMixer); //send audio data. zzq ?????

                                            //
                                            continue;
                                        }


                                    }
                                }
                            }
                        }

                        iErr = 0;
                        return iErr;

                    }

                    default:
                        break;
                }
            }


            iErr = 0;


            return iErr;
        };

        public static class mcThreadProc_mis_toSend implements Runnable {

            qmCommFunc_mis.MIS_CHANNEL pChannel;

            public mcThreadProc_mis_toSend(Object lpParameter) {
                pChannel = (qmCommFunc_mis.MIS_CHANNEL) lpParameter;
            }


            @Override
            public void run() {
                if (null == pChannel) {
                    return;
                }
                if(pChannel.uiType == CONST_channelType_realTimeMedia){
                }

                if(pChannel.uiType == CONST_channelType_media){
                    Log.d(VIDEO_UPLOAD, "<stage0> mcThreadProc_mis_toSend enter.");
                }
                if(pChannel.uiType == CONST_channelType_realTimeMedia){
                    Log.d(AUDIO_UPLOAD, "<stage0> mcThreadProc_mis_toSend enter.");
                }

                qmCommFunc_mis.MIS_CNT pMisCnt = pChannel.pMisCnt;
                if (null == pMisCnt) {
                    return;
                }
                ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
                if (null == pProcInfo) {
                    return;
                }
                ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
                if (null == pQyMc) {
                    return;
                }
                //
                int loopCtrl = 0;

                qnmCommProc_mis.MIS_MSGU pMsg_o = null;

                qyDefs.COMMON_PARAM commonParam0 = new qyDefs.COMMON_PARAM();
                qyDefs.COMMON_PARAM commonParam1 = new qyDefs.COMMON_PARAM();

                int tmpiRet = -1;
                qmCommonFunc_mis.TRAN_INFO_MIS tranInfo = new qmCommonFunc_mis.TRAN_INFO_MIS();
                qnmCommProc_mis.MIS_MSG_TASK pMsgMixer = null;

                String where_showInfo = String.format("%s_toS", qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType));

                ctxMcThread.CTX_MC_thread ctx = new ctxMcThread.CTX_MC_thread();
                ctx.ctxCaller.type = CONST_ctxCallerType_thread;
                ctx.ctxCaller.dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
                ctx.ctxCaller.tNameBuf.tNameBuf = where_showInfo;
                showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, ("starts"));
                pMsg_o = new qnmCommProc_mis.MIS_MSG_TASK();
                switch (pChannel.uiType) {
                    case CONST_channelType_realTimeMedia:
                        pMsgMixer = new qnmCommProc_mis.MIS_MSG_TASK();
                        //pMsgMixer.uiType = CONST_misMsgType_task;
                        qnmCommProc_mis.TRANSFER_AUDIO_DATA transferAudioData = new qnmCommProc_mis.TRANSFER_AUDIO_DATA();
                        //pMsgMixer.buffer = ByteBuffer.allocate(bufSize_transferAudioData);
                        if  (  pProcInfo.cfg.b_useQ2_toEncA) {
                            transferAudioData.buf = ByteBuffer.allocate(bufSize_transferAudioData); //todo move_copy
                        }
                        else  {
                            transferAudioData.buf = ByteBuffer.allocate(bufSize_decObj_a_mediaCodec_tmpDataBuf); //todo move_copy
                        }
                        pMsgMixer.data.buf = transferAudioData;
                        Log.d(AUDIO_UPLOAD, "<stage2.39> pMsgMixer is set." + pMsgMixer.uiType);
                        //
                        if  (  pProcInfo.cfg.b_useQ2_toEncA) {
                            pMsg_o.buffer = ByteBuffer.allocate(bufSize_transferAudioData);
                        }
                        else  {
                            pMsg_o.buffer = ByteBuffer.allocate(transferAudioData.buf.capacity()  +  2048);
                        }
                        break;
                    case CONST_channelType_media:
                        pMsgMixer = new qnmCommProc_mis.MIS_MSG_TASK();
                     //   pMsgMixer.uiType = CONST_misMsgType_task;
                        //pMsgMixer.buffer = ByteBuffer.allocate(bufSize_transferVideoData);
                        qnmCommProc_mis.TRANSFER_VIDEO_DATA transferVideoData = new qnmCommProc_mis.TRANSFER_VIDEO_DATA();
                        if  (  pProcInfo.cfg.b_useQ2_toEncV) {
                            transferVideoData.buf = ByteBuffer.allocate(bufSize_transferVideoData); //todo move_copy
                        }
                        else  {
                            transferVideoData.buf = ByteBuffer.allocate(bufSize_decObj_mediaCodec_tmpDataBuf); //todo move_copy
                        }
                        pMsgMixer.data.buf = transferVideoData;
                        Log.d(VIDEO_UPLOAD, "<stage2.39> pMsgMixer is set." + pMsgMixer.uiType);
                        //
                        if  (  pProcInfo.cfg.b_useQ2_toEncV) {
                            pMsg_o.buffer = ByteBuffer.allocate(bufSize_transferVideoData);
                        }
                        else  {
                            pMsg_o.buffer = ByteBuffer.allocate(transferVideoData.buf.capacity()+2048);
                        }
                        //
                        break;
                    default:
                        //
                        pMsg_o.buffer = ByteBuffer.allocate(bufSize_transferVideoData);
                        //
                        break;
                }

                genericQueue.GENERIC_Q pToSendQ = null;

                //
                for (; bThread_toSendOk(pChannel); loopCtrl++) {

                    //
                    myWaitOne(pChannel.toSendQ2.hEvent0, 3500);

                    //
                    if(pChannel.uiType == CONST_channelType_realTimeMedia){
                        //Log.d(AUDIO_UPLOAD, "<stage2.5> mcThreadProc_mis_toSend run. uiType:" + pMsgMixer.uiType);
                        test.testCls_Droid.testBreak();
                    }

                    //
                    if(pChannel.uiType == CONST_channelType_media){
                        //Log.d(VIDEO_UPLOAD, "2.4 mcThreadProc_mis_toSend run. uiType:" + pMsgMixer.uiType);
                        test.testCls_Droid.testBreak();
                    }

//                    q2AvoidTriggerTooHigh(null, pChannel.toSendQ2);
                    int q2NodesToPostMsg = 0;
                    if  (  pChannel.m_b_useQ2_toOutput) {
                        q2NodesToPostMsg=getQ2Nodes_toPostMsg(pChannel.outputQ21);
                    }
                    else {
                        if  (  pChannel.outputQBuf2==null)  {
                            break;
                        }
                        q2NodesToPostMsg = pChannel.outputQBuf2.getQ2Nodes_toPostMsg();
                    }
                    //
                    if (q2NodesToPostMsg > 40) { //这个地方属于硬编码,实际上是有问题的！
                        if(pChannel.uiType == CONST_channelType_realTimeMedia) {
                            Log.e(AUDIO_UPLOAD, "<stage2.52>: hardcore q2NodesToPostMsg size is-> " + q2NodesToPostMsg);
                        }
                        else if(pChannel.uiType == CONST_channelType_media) {
                            Log.e(VIDEO_UPLOAD, "<stage2.52>: hardcore q2NodesToPostMsg size is-> " + q2NodesToPostMsg);
                        }
                        continue;
                    }


                    tranInfo.memset0();
                    tranInfo.uiTranNo = getuiNextTranNo.call();
                    if (tranInfo.uiTranNo == -1) {
                        if (null != pMisCnt) {
                            //pChannel.status.ulbSessionErr = true;
                            setSessionErr(pChannel,true,"mcThreadProc_mis_toSend.run.1446");
                            //
                            pChannel.status.ulbToSendQuit = true;
                        }
                        if(pChannel.uiType == 4)
                        {
                            Log.e(VIDEO_UPLOAD,"tranInfo.uiTranNo == -1");
                            //pChannel.hThread_io = null;
                        }
                        return;
                    }
                    tranInfo.tNow = tmpFunc.qyFuncs.mytime();
                    tranInfo.pMsg_o = pMsg_o;
                    qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar =  getCtxThreadTmpVar(CONST_iParamType_toSend,pChannel.uiType);
                    if  (  tmpVar==null)  {

                    }

                    MACRO_makeCommonParam3(pProcInfo, pMisCnt, pChannel, commonParam0);
                    MACRO_makeCommonParam3(tranInfo, pMsgMixer, tmpVar, commonParam1);

                    if (pChannel.uiType == CONST_channelType_realTimeMedia) {
                        //
                        //showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, "before traverse");
                        //
                        CMIS_MSGU p0 = new CMIS_MSGU();
                        qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                        param.commonParam0 = commonParam0;
                        param.commonParam1 = commonParam1;
                        int bufSize = pChannel.getOutQ2Size();
                        tmpiRet = pProcInfo.processQ_media.qTraverse(ctx.ctxCaller, tmpHandler_toSendMedia, p0.msgU, param);
                        if (tmpiRet < 0) {
                            String str = "mcThreadProc_mis_toSend: tmpHandler_toSendMedia failed.";
                            Log.e("qy",str);
                            showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, ("ends"));
                            if (null != pMisCnt) {
                                //pChannel.status.ulbSessionErr = true;
                                setSessionErr(pChannel,true,"mcThreadProc_mis_toSend.run.1483");
                                //
                                pChannel.status.ulbToSendQuit = true;
                            }
                            showInfo_open.qyFuncs.showInfo_open(0, null,str);
                            return;
                        }
                        //
                        //showInfo_open.qyFuncs.showInfo_open(0, where_showInfo,"after traverse");
                    }
                    if (pChannel.uiType == CONST_channelType_media) {
                        //
                        //showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, "before traverse");
                        //
                        CMIS_MSGU p0 = new CMIS_MSGU();
                        qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                        param.commonParam0 = commonParam0;
                        param.commonParam1 = commonParam1;
                        int bufSize = pChannel.getOutQ2Size();
                        //
                        if  (  false  ) {
                            Log.d(VIDEO_UPLOAD, "2.5 tmpHandler_toSendMedia, before send 1, pChannel.outputQ2 size: " +
                                    bufSize + " thread " + Thread.currentThread().getId());
                        }
                        //
                        tmpiRet = pProcInfo.processQ_media.qTraverse(ctx.ctxCaller, tmpHandler_toSendMedia, p0.msgU, param);
                        if (tmpiRet < 0) {
                            String str= "mcThreadProc_mis_toSend: tmpHandler_toSendMedia failed.";
                            Log.e(str);
                            showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, ("ends"));
                            if (null != pMisCnt) {
                                //pChannel.status.ulbSessionErr = true;
                                setSessionErr(pChannel,true,"toSend.run.1515");
                                //
                                pChannel.status.ulbToSendQuit = true;
                            }
                            showInfo_open.qyFuncs.showInfo_open(0, where_showInfo,str);
                            return;
                        }
                    }

                    tranInfo.uiMaxSendMsgs = 4;
                    do {
                        if (null == pToSendQ || isQEmpty(pToSendQ)) {
                            try //(qyq2.CQyQ2Help q2Help = new qyq2.CQyQ2Help())
                            {
                                pToSendQ = pChannel.toSendQ2.qs0;//q2Help.getQ_toGetMsg(pChannel.toSendQ2);
                                if (null == pToSendQ) break;
                            } catch (Exception e) {
                                Log.e(e.getMessage());
                                if (null!=pMisCnt)
                                {
                                    //pChannel.status.ulbSessionErr = true;
                                    setSessionErr(pChannel,true,"toSend.run.1536");
                                    pChannel.status.ulbToSendQuit = true;
                                }
                                return;
                            }

                            if (isQEmpty(pToSendQ)) break;
                        }

                        tmpiRet = qTraverse(pToSendQ, tmpHandler_toSend, commonParam0, commonParam1);
                        if (tmpiRet < 0) {
                            //Log.e("mcThreadProc_mis_toSend: tmpHandler_toSendMedia failed.");
                            if (null!=pMisCnt)
                            {
                                //pChannel.status.ulbSessionErr = true;
                                setSessionErr(pChannel,true,"toSend.run.1551");
                                //
                                pChannel.status.ulbToSendQuit = true;
                            }
                            return;
                        }
                        if (qRemoveMsg(pToSendQ, b_tmpHandler_needRemoveMsg_cli, 0, commonParam1) < 0) {
                            Log.e("qRemoveMsg pToSendQ failed, skipped.");
                        }
                    }
                    while (tranInfo.nNodes_processed < pProcInfo.cfg.uiMaxSendMsgs);
                }
                if(pChannel.uiType == CONST_channelType_realTimeMedia){
                    Log.d(AUDIO_UPLOAD, "<stage0> mcThreadProc_mis_toSend exit.");
                }

                if(pChannel.uiType == CONST_channelType_media){
                    Log.d(VIDEO_UPLOAD, "<stage0> mcThreadProc_mis_toSend exit.");
                }

            }
        }

        ;

    }
}

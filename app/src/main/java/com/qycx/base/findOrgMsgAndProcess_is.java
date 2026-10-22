


package com.qycx.base;

import static com.qycx.base.Consts.*;
import static com.qycx.base.Consts.VIDEO_UPLOAD;
import static com.qycx.base.ctxQmcTmpl.Consts.CONST_fn_dump_h264_enc;
import static com.qycx.base.genericQueue.qyFuncs.emptyGenericQ;
import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_remoteAssistReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_simpleResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskProcReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskProcResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAudioData;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAudioDataResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvReplyInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileDataReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileDataResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileEndReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileEndResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReplyReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferGpsInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferVideoData;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferVideoDataResp;
import static com.qycx.base.qisChannel.qyFuncs.startChannel;
import static com.qycx.base.qmcProcTask.qyFuncs.old_procTaskAvReply;
import static com.qycx.base.qmcProcTask.qyFuncs.procTaskAudioDataResp;
import static com.qycx.base.qmcProcTask.qyFuncs.procTaskAvReply;
import static com.qycx.base.qmcProcTask.qyFuncs.tmp_procTask_recv_cancel;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcTaskInfo.Consts.*;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_applyForChkTaskAlive;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_applyForKeyFrame;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_applyForOrgReq;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_requestToSend;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_stopSending;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxTransferAvDataMems;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_req;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_TRANSFER_AUDIO_dataMemHead_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_TRANSFER_VIDEO_dataMemHead_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferFileDataResp;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.MACRO_getDispatch;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.MACRO_getTransform;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.chkToApplyForChannel;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.isEmpty_ROUTE_sendLocalAv;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgResp;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_qwmComm;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_shareDynBmp;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_canceledByReceiver;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_canceledBySender;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_receiving;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_recvFinished;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_sendFinished;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_sending;
import static com.qycx.base.qyWmComm.Consts.*;
import static com.qycx.base.qyq2.qyFuncs.emptyQ2;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;
import static com.qycx.base.qyq2.qyFuncs.isQ2Warning;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsg;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.showTaskStatusFunc.qyFuncs.showNotification;
import static com.qycx.base.showTaskStatusFunc.qyFuncs.showTaskStatus;
import static com.qycx.base.taskAv.qyFuncs.bApplyForAvProxy;
import static com.qycx.base.taskAvFunc.qyFuncs.getTaskAvFrom;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import com.qycx.base.statuscenter.MeetingStatusCenter;
import com.qycx.base.statuscenter.messagebean.AudioOutBufSizeEvent;
import com.qycx.base.statuscenter.messagebean.MediaOutStatus;
import com.qycx.base.statuscenter.messagebean.VideoOutBufSizeEvent;
import com.qycx.base.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class findOrgMsgAndProcess_is {
    public static class Consts {
        public final static int cntof_filePath = MAX_PATH + 1;
    }

    static class TMP_filePath {
        String filePath;//[cntof_filePath];
    }

    ;

    public static class qyFuncs {

        public static boolean canSend(qmcSharedObj.QY_SHARED_OBJ pSharedObj) {
            boolean bRet = true;

            switch (pSharedObj.var.uiType) {
                case 0:
                    if (pSharedObj.var.ucbNotSendData) {
                        bRet = false;
                    }
                    break;
                default:
                    break;
            }

            return bRet;
        }


        //  int  tmpHandler_findOrgMsgAndProcess_mis_procTaskAudioData(  MC_VAR_isCli  *  pProcInfo,  MSGR_ADDR  *  pAddr,  int  iTaskId,  qnmCommProc_mis.MIS_MSG_TASK  *  pMsgTask,  qnmCommProc_mis.TRANSFER_AUDIO_DATA  *  pTransferAudioData,  AV_TRAN_INFO  *  pTranInfo,  taskAv.PROC_TASK_AV  *  pTask  )
        public static int tmpHandler_findOrgMsgAndProc_procTaskAudioData(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo,
                                                                                qnmCommProc_mis.MSGR_ADDR pAddr, int iTaskId, qnmCommProc_mis.MIS_MSG_TASK pMsgTask,
                                                                                qnmCommProc_mis.TRANSFER_AUDIO_DATA pTransferAudioData, qnmCommProc_mis.IM_CONTENTU pContentElem,
                                                                                taskAv.PROC_TASK_AV pTask) {
            int iErr = -1;
            int index = 0;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            /*


            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "findOrgMsg_procTaskAudioData");
            if (null == pTaskInfo) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return iErr;
            if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead[] pTransferAudioData_mems = pTransferAudioData.mems;

            if (pTask.ucbVideoConferenceStarter) {
            } else if (pTask.bVideoConference()) {
                //
                if (pMsgTask.bFrom_sharedObj) {
                    qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, pMsgTask.index_sharedObj);
                    if (null == pSharedObj) return iErr;
                    //
                    int i;
                    qmcSharedObj.SHARED_OBJ_USR pSou;
                    for (i = 0; i < (pSharedObj.usrs.length); i++) {
                        pSou = pSharedObj.usrs[i];
                        if (0 == pSou.iTaskId) continue;
                        if (pSou.iTaskId != iTaskId) continue;

                        //						
                        if (pMsgTask.data.route.idInfo_from.ui64Id == pMisCnt.idInfo.ui64Id) {

                            //  Log.e(  (  "audio data sent by myself"  )  );
                            boolean bSend = true;

                            bSend = canSend(pSharedObj); //判断申请发言 zzq

                            if (bSend) {
//                                byte[] buf = pMsgTask.toByteArray();
                                //q2PostMsg(buf,  buf.length, pTask.mixer.pOutputQ2);
                             //   EventBus.getDefault().post(new AudioOutBufSizeEvent(pTask.mixer.getSize(),1, Thread.currentThread().getId()));
                                //Log.d(AUDIO_UPLOAD, " <stage2>  tmpHandler_findOrgMsgAndProcess_mis_procTaskAudioData pTask.mixer.pOutputQ2 size is " + pTask.mixer.getSize());

                                qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_realTimeMedia);
                                qnmCommProc_mis.TRANSFER_AUDIO_DATA tad = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pMsgTask.data.buf;
                                //
                                if(false){
                                    tad.buf.flip();
                                    byte[] audio = tad.buf.array();
                                    int len = tad.buf.limit();
                                    guiShare.GuiShare.pf_dumpData1.call(audio,len, 0, 0,true, false,"dump_procTaskData.mp3");

                                }
                                //
                                if  (  pProcInfo.cfg.debugStatusInfo.b_print_test) {
                                    showInfo_open.qyFuncs.showInfo_open(0, "", "find_procTaskAudioData: qPost: msg.id " + pMsgTask.tmpID);
                                }
                                //
                                if  (  pProcInfo.cfg.b_useQ2_toEncA) {
                                    q2PostMsg(pMsgTask, pTask.mixer.pOutputQ2, "find_procTaskAudioData.174");
                                }
                                else  {
                                    int j;
                                    int  iPos  =  0;
                                    byte[]audio  =  tad.buf.array();
                                    for  (  j  =  0;  j  <  tad.usCnt; j  ++  )  {
                                        qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead mem_i  =  tad.mems[j];
                                        qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead mem_o  =  new qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead();
                                        mem_o.copyFrom(mem_i);
                                        //
                                        //mem_o.tmp_pMsgTask=pMsgTask;
                                        //
                                        pTask.mixer.pOutputQBuf2.q2PostMsg(mem_o,audio,iPos,mem_o.uiLen,"");
                                        iPos  +=  mem_o.uiLen;
                                    }
                                      //pTask.mixer.pOutputQBuf2.q2PostMsg()

                                }

                                //
                                if(pChannel.hThread_io == null){
                                    Log.d(AUDIO_UPLOAD, " <stage2.0>  tmpHandler_findOrgMsgAndProcess_mis_procTaskAudioData pChannel.hThread_io is null!!!!!!!!" );
                                    startChannel(pProcInfo, pMisCnt, pChannel, ("proc transferAudioData"));
                                }

                                if (null != pChannel) {

                                    //
                                    chkToApplyForChannel(pProcInfo, pChannel, CONST_channelType_realTimeMedia);

                                    //
                                    if  (  pChannel.m_b_useQ2_toOutput) {
                                        q2PostMsgAndTrigger(null, pChannel.outputQ21, "find_procTaskAudioData.188");
                                    }
                                    else  {
                                          pChannel.outputQBuf2.q2PostMsgAndTrigger(null,null,  0,  0,  "find_procTaskAudioData.188");

                                    }
                                    if(pChannel.uiType == 4) {
                                        int bufSize = pChannel.getOutQ2Size();
                                    }
                                }
                            }

                            //							
                            iErr = 1;
                            return iErr;

                        }

                        //						
                        break;
                    }
                }

                //
                if (pTask.mem0_from.idInfo.ui64Id == pMsgTask.data.route.idInfo_from.ui64Id
                        && pTask.mem0_from.audio.ta_recvd.uiTranNo_openAvDev == pTransferAudioData.uiTranNo_openAvDev) {
                    int i = 0;
                    int iPos = 0;
                    qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem = null;
                    int index_activeMems_from = 0;
                    int usIndex_pMems_from = 0;
                    qmcTransform.QY_TRANSFORM pTransform = null;
                    qnmCommProc_mis.MIS_MSG_TASK pMsgTask_toTransform = null;
                    qnmCommProc_mis.TRANSFER_AUDIO_DATA pNew = null;

                    if (null == pTask.audio.pMsgU_toTransform) {
                        Log.e("findOrgMsg failed. pMsgU_toTransform is null");
                        return iErr;
                    }
                    pMsgTask_toTransform = (qnmCommProc_mis.MIS_MSG_TASK) pTask.audio.pMsgU_toTransform;

                    if (0 == pTransferAudioData.usCnt) {
                        pTransferAudioData.usCnt = 1;  //  ¶ÔÀÏµÄÊý¾Ý°ü£¬Õâ¸öusCntÃ»Ìî£¬°üº¬Ò»¸öÊý¾Ý
                        pTransferAudioData_mems[0].uiLen = pTransferAudioData.uiLen;
                    }
                    if (pTransferAudioData.usCnt > CONST_maxTransferAvDataMems) return iErr;

                    for (iPos = 0, i = 0; i < pTransferAudioData.usCnt; iPos = (int) (iPos + pTransferAudioData_mems[i].uiLen), i++) {
                        pMem = pTransferAudioData_mems[i];
                        if (0 == pMem.uiLen) {

                            continue;
                        }

                        if (iPos + pTransferAudioData_mems[i].uiLen > pTransferAudioData.uiLen) {
                            return iErr;
                        }

                        if (pMem.idInfo.ui64Id == 0)
                            pMem.idInfo.ui64Id = pMsgTask.data.route.idInfo_from.ui64Id;
                        for (index_activeMems_from = 0; index_activeMems_from < videoConference.usCntLimit_activeMems_from; index_activeMems_from++) {
                            if (pMem.idInfo.ui64Id == videoConference.activeMems_from[index_activeMems_from].avStream.idInfo.ui64Id
                                    && pMem.uiTranNo_openAvDev == videoConference.activeMems_from[index_activeMems_from].avStream.obj.tranInfo.audio.uiTranNo_openAvDev)
                                break;
                        }
                        if (index_activeMems_from == videoConference.usCntLimit_activeMems_from) {
                            showInfo_open.qyFuncs.showInfo_open( ("findOrgMsg: audio, can't get index_activeMems_from, skipped."));

                            //  2009/06/01
                            if (pTask.bVideoConference() && !pTask.ucbVideoConferenceStarter) {
                                //MIS_CNT pMisCnt = (MIS_CNT)pMsgTask->pMisCnt;
                                pMisCnt.refreshVideoConferenceActiveMems.iTaskId = iTaskId;
                                pMisCnt.refreshVideoConferenceActiveMems.bNeedRefreshed = true;
                            }

                            continue;
                        }
                        //
                        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_activeMems_from];
                        //  2014/12/12

                        //
                        System.arraycopy(pMsgTask, 0, pMsgTask_toTransform, 0, 128);
                        pMsgTask_toTransform.data.route.idInfo_from.ui64Id = videoConference.activeMems_from[index_activeMems_from].avStream.idInfo.ui64Id;
                        pMsgTask_toTransform.data.route.idInfo_to.ui64Id = pMsgTask.data.route.idInfo_to.ui64Id;
                        pNew = null;// (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pMsgTask_toTransform.data.buf;
                        qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pNew_mems[] = null;//(qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead) pNew.buf_TRANSFER_AUDIO_dataMemHead_mems;
                        //mymemset(pNew, 0, (int)Marshal.OffsetOf<qnmCommProc_mis.TRANSFER_AUDIO_DATA>("buf"));
                        pNew.uiType = pTransferAudioData.uiType;
                        pNew.uiTranNo_openAvDev = pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev;
//                        qyMemoryFunc.qyFuncs.mymemcpy((byte[]) pNew_mems[0], pMem, 0);
                        System.arraycopy(pMem, 0, pNew_mems[0], 0, 128);
                        pNew.usCnt = 1;
                        pNew.uiLen = pMem.uiLen;
                        ByteBuffer buffer = pTransferAudioData.buf;
                        buffer.position(iPos);
                        //qyMemoryFunc.qyFuncs.mymemcpy(pNew.buf.array(), buffer.array(), pNew.uiLen);

                        //										
                        pNew.tmpInternal.param.index_pMems_from = -1;// pTask.activeMems_from[index_activeMems_from].usIndex_pMems_from;
                        pNew.tmpInternal.param.index_activeMems_from = index_activeMems_from;

                        //
                        pMsgTask_toTransform.lenInBytes = pNew.uiLen;

                        pTransform = MACRO_getTransform(pTask, index_activeMems_from);
                        if (null != pTransform) {
                            //  qPostMsgAndTrigger_toDo(  pMsgTask,  offsetof(  qnmCommProc_mis.MIS_MSG_TASK,  data  )  +  pMsgTask->lenInBytes,  &pTransform->video  );
                            q2PostMsgAndTrigger(pMsgTask_toTransform, pTransform.audio.q2,"find_procTaskAudiodata.291");
                        }
                    }

                    iErr = 1;
                    return iErr;
                }

            } else {
                //
                if (pMsgTask.bFrom_sharedObj) {
                    qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, pMsgTask.index_sharedObj);
                    if (null == pSharedObj) return iErr;
                    int i;
                    for (i = 0; i < pSharedObj.usrs.length; i++) {
                        qmcSharedObj.SHARED_OBJ_USR pSou = pSharedObj.usrs[i];
                        if (0 == pSou.iTaskId) continue;
                        if (pSou.iTaskId != iTaskId) continue;
                        //
                        if (pMsgTask.data.route.idInfo_from.ui64Id == pMisCnt.idInfo.ui64Id) {
                            //  Log.e(  (  "audio data sent by myself"  )  );

                            boolean bSend = true;
                            bSend = canSend(pSharedObj);

                            if (bSend) {

                                //
                                q2PostMsg(pMsgTask, pTask.mixer.pOutputQ2,"find_procTaskAudioData.317");

                                //
                                qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_realTimeMedia);
                                if (null != pChannel) {
                                    if(pChannel.hThread_io == null)
                                    {
                                        startChannel(pProcInfo,pMisCnt,pChannel,"start audio Channel");
                                    }
                                    //
                                    chkToApplyForChannel(pProcInfo, pChannel, CONST_channelType_realTimeMedia);

                                    //
                                    if  (  pChannel.m_b_useQ2_toOutput) {
                                        q2PostMsgAndTrigger(null, pChannel.outputQ21, "find_procTaskAudioData.328");
                                    }
                                    else  {
                                          pChannel.outputQBuf2.q2PostMsgAndTrigger(null,null,0,  0,"");

                                    }

                                }
                            }
                            //
                            iErr = 1;
                            return iErr;

                        }


                        break;
                    }
                }


                //  2014/11/21
                if (pTask.applyForProxy.bRecvd_proxyReply) {       //  applier for proxy
                    if (pTask.applyForProxy.proxyReply.proxy.uiTranNo_openAvDev_a == pTransferAudioData.uiTranNo_openAvDev) {
                        iErr = 1;
                        return iErr;
                    }
                }


                //  2014/11/21
                if (!pTask.receiver.bRecvd_proxiedTranInfo) {  //  一般性的数据包处理，或者是proxy

                    //  search in mems_from directly
                    for (index = 0; index < videoConference.usCntLimit_activeMems_from; index++) {
                        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index];

                        //
                        if (pActiveMem_from.avStream.idInfo.ui64Id == pMsgTask.data.route.idInfo_from.ui64Id
                                && pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev == pTransferAudioData.uiTranNo_openAvDev) {
                            //  if  (  procTaskAudio(  pProcInfo,  &pMsgElem->addr_logicalPeer,  pMsgTask,  &pContent->transferAudioData,  &pTransferAvInfoElem->tranInfo,  &pTransferAvInfoElem->myTask,  index,  0  )  )  goto  errLabel;

                            pTransferAudioData.tmpInternal.param.index_pMems_from = index;
                            pTransferAudioData.tmpInternal.param.index_activeMems_from = 0;

                            //  2014/11/21
                            if (pTask.doProxy.bExists_avProxy) {

                                int index_activeMems_from = 0;

                                //
                                qnmCommProc_mis.QIS_DISPATCH pDispatch = MACRO_getDispatch(pTask, index_activeMems_from);
                                if (null != pDispatch) {
                                    q2PostMsgAndTrigger(pMsgTask, pDispatch.audio.q2,"find_procTaskAudiuoData.376");
                                }
                                iErr = 1;
                                return iErr;
                            }

                            //
                            qmcTransform.QY_TRANSFORM pTransform = MACRO_getTransform(pTask, pTransferAudioData.tmpInternal.param.index_activeMems_from);
                            if (null != pTransform) {
                                //  qPostMsgAndTrigger_toDo(  pMsgTask,  offsetof(  qnmCommProc_mis.MIS_MSG_TASK,  data  )  +  pMsgTask->lenInBytes,  &pTransform->audio  );
                                q2PostMsgAndTrigger(pMsgTask, pTransform.audio.q2,"find_procTaskAudioData.386");
                            }

                            iErr = 1;
                            return iErr;
                        }
                    }
                    //
                } else {  //  receiver from proxy				  			 
                    if (pTask.mem0_from.idInfo.ui64Id == pMsgTask.data.route.idInfo_from.ui64Id
                            && pTask.mem0_from.audio.ta_recvd.uiTranNo_openAvDev == pTransferAudioData.uiTranNo_openAvDev) {
                        //  2014/11/21
                        if (pTask.receiver.proxied_from.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {
                            iErr = 1;
                            return iErr;
                        }

                        //
                        int i = 0;
                        int iPos = 0;
                        qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem = null;
                        int index_activeMems_from = 0;
                        int usIndex_pMems_from = 0;
                        qmcTransform.QY_TRANSFORM pTransform = null;
                        qnmCommProc_mis.MIS_MSG_TASK pMsgTask_toTransform = null;
                        qnmCommProc_mis.TRANSFER_AUDIO_DATA pNew = null;

                        if (null == pTask.video.pMsgU_toTransform) {
                            Log.i("findOrgMsg failed. pMsgTask_toTransform is null");
                            return iErr;
                        }
                        pMsgTask_toTransform = null;//(qnmCommProc_mis.MIS_MSG_TASK) pTask.video.pMsgU_toTransform;

                        if (0 == pTransferAudioData.usCnt) {
                            pTransferAudioData.usCnt = 1;  //  ¶ÔÀÏµÄÊý¾Ý°ü£¬Õâ¸öusCntÃ»Ìî£¬°üº¬Ò»¸öÊý¾Ý
                            pTransferAudioData_mems[0].uiLen = pTransferAudioData.uiLen;
                        }
                        if (pTransferAudioData.usCnt > CONST_maxTransferAvDataMems) return iErr;

                        for (iPos = 0, i = 0; i < pTransferAudioData.usCnt; iPos = (int) (iPos + pTransferAudioData_mems[i].uiLen), i++) {
                            pMem = pTransferAudioData_mems[i];
                            if (0 == pMem.uiLen) {
                                Log.i("findOrgMsg: transferAudioMem.uiLen is 0. skipped.");
                                continue;
                            }

                            if (iPos + pTransferAudioData_mems[i].uiLen > pTransferAudioData.uiLen) {
                                Log.i("findOrgMsg failed: iPos + mem.uiLen > data.uiLen");
                                return iErr;
                            }

                            usIndex_pMems_from = 1;
                            qnmCommProc_mis.TASK_AV_FROM pFrom = null;
                            pFrom = getTaskAvFrom(pTask, (int) usIndex_pMems_from, (""));
                            if (null == pFrom) return iErr;

                            //
//                            qyMemoryFunc.qyFuncs.mymemcpy(pMsgTask_toTransform, pMsgTask, 0);
                            System.arraycopy(pMsgTask, 0, pMsgTask_toTransform, 0, 128);
                            pMsgTask_toTransform.data.route.idInfo_from.ui64Id = pFrom.idInfo.ui64Id;
                            pMsgTask_toTransform.data.route.idInfo_to.ui64Id = pMsgTask.data.route.idInfo_to.ui64Id;
                            pNew = null;// (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pMsgTask_toTransform.data.buf;
                            qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pNew_mems = null;// (qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead) pNew.buf_TRANSFER_AUDIO_dataMemHead_mems;
                            //mymemset(pNew, 0, (int)Marshal.OffsetOf<qnmCommProc_mis.TRANSFER_AUDIO_DATA>("buf"));
                            pNew.uiType = pTransferAudioData.uiType;
                            pNew.uiTranNo_openAvDev = pFrom.audio.ta_recvd.uiTranNo_openAvDev;
//                            qyMemoryFunc.qyFuncs.mymemcpy((byte[]) pNew_mems[0], pMem, 0);
//                            System.arraycopy(pMem, 0, pNew_mems, 0, 128);
                            pNew.usCnt = 1;
                            pNew.uiLen = pMem.uiLen;
//                            qyMemoryFunc.qyFuncs.mymemcpy(pNew.buf, (pTransferAudioData.buf + iPos), pNew.uiLen);
                            System.arraycopy(pTransferAudioData.buf, iPos, pNew.buf, 0, pNew.uiLen);
                            //										
                            pNew.tmpInternal.param.index_pMems_from = (int) usIndex_pMems_from;
                            pNew.tmpInternal.param.index_activeMems_from = index_activeMems_from;

                            //
                            pMsgTask_toTransform.lenInBytes = (int) + +pNew.uiLen;

                            pTransform = MACRO_getTransform(pTask, index_activeMems_from);
                            if (null != pTransform) {
                                //  
                                q2PostMsgAndTrigger(pMsgTask_toTransform, pTransform.audio.q2,"find_procTaskAudioData.468");
                            }

                            //  2014/11/11
                            continue;

                        }

                        iErr = 1;
                        return iErr;
                    }

                    //
                    iErr = 0;
                    return iErr;
                }
            }


             */

            iErr = 0;
            return iErr;

        }

        //
        public static int tmpHandler_findOrgMsgAndProc_procInputAudioData(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MSGR_ADDR pAddr, int iTaskId, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, qnmCommProc_mis.TRANSFER_AUDIO_DATA pTransferAudioData, qnmCommProc_mis.IM_CONTENTU pContentElem, taskAv.PROC_TASK_AV pTask) {
            int iErr = -1;
            int index = 0;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            //
            boolean bDbg = true;
            String str;

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "findOrgMsg_procInputAudioData");
            if (null == pTaskInfo) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;
            if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pTransferAudioData_mems[] =  pTransferAudioData.mems;// (qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead) pTransferAudioData.buf_TRANSFER_AUDIO_dataMemHead_mems;
            int bufSize = bufSize_TRANSFER_AUDIO_dataMemHead_mems;

            //
            if (pTask.ucbVideoConferenceStarter) {
            } else if (pTask.bVideoConference()) {

                if (pMsgInput.iTaskId == iTaskId) {
                    //  2011/01/23				  
                    if (pMsgInput.data.route.idInfo_from.ui64Id == pMisCnt.idInfo.ui64Id) {
                        //  Log.e(  (  "audio data sent by myself"  )  );
//                        byte[] buf = pMsgInput.toByteArray();
                        //q2PostMsg(buf, buf.length, pTask.mixer.pOutputQ2);
                        qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_realTimeMedia);
                        if (pChannel != null) {
                            if  (  pChannel.m_b_useQ2_toOutput) {
                                q2PostMsgAndTrigger(null, pChannel.outputQ21, "find_procInputAudioData.523");
                            }
                            else  {
                                pChannel.outputQBuf2.q2PostMsgAndTrigger(null, null, 0,0,  "find_procInputAudioData.523");

                            }
                        }

                        //
                        iErr = 1;
                        return iErr;
                    }
                }

                if (pTask.mem0_from.idInfo.ui64Id == pMsgInput.data.route.idInfo_from.ui64Id
                        && pTask.mem0_from.audio.ta_recvd.uiTranNo_openAvDev == pTransferAudioData.uiTranNo_openAvDev) {
                    int i = 0;
                    int iPos = 0;
                    qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem;
                    int index_activeMems_from = 0;
                    int usIndex_pMems_from = 0;
                    qmcTransform.QY_TRANSFORM pTransform = null;
                    qnmCommProc_mis.MIS_MSG_INPUT pMsgInput_toTransform = null;
                    qnmCommProc_mis.TRANSFER_AUDIO_DATA pNew = null;

                    if (null == pTask.audio.pMsgU_toTransform) {
                        Log.i("findOrgMsg failed. pMsgTask_toTransform is null");
                        return iErr;
                    }
//                    pMsgInput_toTransform = (qnmCommProc_mis.MIS_MSG_INPUT) pTask.audio.pMsgU_toTransform;// (qnmCommProc_mis.MIS_MSG_INPUT) pTask.audio.pMsgU_toTransform;

                    if (0 == pTransferAudioData.usCnt) {
                        pTransferAudioData.usCnt = 1;  //  ¶ÔÀÏµÄÊý¾Ý°ü£¬Õâ¸öusCntÃ»Ìî£¬°üº¬Ò»¸öÊý¾Ý
                        pTransferAudioData_mems[0].uiLen = pTransferAudioData.uiLen;
                    }
                    if (pTransferAudioData.usCnt > CONST_maxTransferAvDataMems) return iErr;

//                    iPos = (int) (iPos + pTransferAudioData_mems[i].uiLen), i+
                    for (iPos = 0, i = 0; i < pTransferAudioData.usCnt; iPos += pMem.uiLen, i++) {
                        pMem = getMem.qyFuncs.getMem(pTransferAudioData_mems, bufSize, i);// &pTransferAudioData_mems[i];
                        if (0 == pMem.uiLen) {

                            continue;
                        }

                        if (iPos + pMem.uiLen > pTransferAudioData.uiLen) {
                            return iErr;
                        }

                        if (pMem.idInfo.ui64Id == 0)
                            pMem.idInfo.ui64Id = pMsgInput.data.route.idInfo_from.ui64Id;
                        //
                        for (index_activeMems_from = 0; index_activeMems_from < videoConference.usCntLimit_activeMems_from; index_activeMems_from++) {
                            if (pMem.idInfo.ui64Id == videoConference.activeMems_from[index_activeMems_from].avStream.idInfo.ui64Id
                                    && pMem.uiTranNo_openAvDev == videoConference.activeMems_from[index_activeMems_from].avStream.obj.tranInfo.audio.uiTranNo_openAvDev) {
                                break;
                            }
                        }
                        if (index_activeMems_from == videoConference.usCntLimit_activeMems_from) {

                            showInfo_open.qyFuncs.showInfo_open( ("findOrgMsg: audio, can't get index_activeMems_from, skipped."));

                            //
                            iErr = CONST_qyRet_cantGet_index_activeMems_from;
                            return iErr;

                        }
                        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_activeMems_from];


                        //
                        if (pProcInfo.cfg.debugStatusInfo.ucb_chkInputPts) {
                            if (0 != pActiveMem_from.last_iPts_a) {
                                if (pMem.uiPts != pActiveMem_from.last_iPts_a + 1) {
                                    str = String.format("a.act[%s],%s:pts err, mem.pts %s, last_iPts %s", index_activeMems_from,
                                            pActiveMem_from.avStream.idInfo.ui64Id,
                                            pMem.uiPts,
                                            pActiveMem_from.last_iPts_a);
                                    Log.d(str);
                                    showInfo_open.qyFuncs.showInfo_open(0, "", str);
                                }
                            }

                        }
                        //							
                        pActiveMem_from.last_iPts_a = (int) pMem.uiPts;

                        //
                        if (pActiveMem_from.avStream.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {
                            //skip me
                            continue;
                        }



                        pTransform = MACRO_getTransform(pTask, index_activeMems_from);
                        if  (  pTransform==null)  {
                            continue;
                        }

                        //
                        if (!pProcInfo.cfg.b_useAudioQ2_toDecA) {  //  这里避免了下面的内存分配
                            //byte[] buf = pNew.buf.array();

                            //
                            //guiShare.GuiShare.pf_dumpData1.call(buf, pMem.uiLen, 0, 0, true, false, "dump_deced_pcm.mp3");


                            pTransform.audio.qBuf2.q2PostMsgAndTrigger(pMem,pTransferAudioData.buf.array(),iPos,  pMem.uiLen,"find_procInputAudioData.671");
                            //
                            //Log.d(AUDIO_DOWNLOAD, "3 qBuf2.qs0.uiQNodes "+pTransform.audio.qBuf2.qs0.uiQNodes+" qBuf2.qs1.uiQNodes "+pTransform.audio.qBuf2.qs1.uiQNodes);
                            continue;
                        }



                        //
//                        MACRO_memset_misMsgInput(pMsgInput_toTransform);
//                        qyMemoryFunc.qyFuncs.mymemcpy(pMsgInput_toTransform, pMsgInput, 0);
                        pMsgInput_toTransform = new qnmCommProc_mis.MIS_MSG_INPUT();
                        pMsgInput.copyDataUntilData(pMsgInput_toTransform);
                        pMsgInput_toTransform.data.buf = new qnmCommProc_mis.TRANSFER_AUDIO_DATA();

//                        pMsgInput_toTransform = pMsgInput;
//                        System.arraycopy(pMsgInput, 0, pMsgInput_toTransform, 0, 128);
                        pMsgInput_toTransform.data.route.idInfo_from.ui64Id = videoConference.activeMems_from[index_activeMems_from].avStream.idInfo.ui64Id;
                        pMsgInput_toTransform.data.route.idInfo_to.ui64Id = pMsgInput.data.route.idInfo_to.ui64Id;
                        pNew = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pMsgInput_toTransform.data.buf;//(qnmCommProc_mis.TRANSFER_AUDIO_DATA) pMsgInput_toTransform.data.buf;
                        // fixme fo change
                        pNew.buf = ByteBuffer.allocate(((qnmCommProc_mis.TRANSFER_AUDIO_DATA)pMsgInput.data.buf).buf.capacity());
//                        qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead[] pNew_mems = pNew.mems;// (qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead) pNew.buf_TRANSFER_AUDIO_dataMemHead_mems;
                        //mymemset(pNew, 0, (int)Marshal.OffsetOf<qnmCommProc_mis.TRANSFER_AUDIO_DATA>("buf"));
                        pNew.uiType = pTransferAudioData.uiType;
                        pNew.uiTranNo_openAvDev = pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev;

//                        qyMemoryFunc.qyFuncs.mymemcpy((byte[]) pNew_mems, pMem, 0);
//                        System.arraycopy(pMem, 0, pNew_mems, 0, 128);
                        pNew.mems[0] = pMem;
                        pNew.usCnt = 1;
                        pNew.uiLen = pMem.uiLen;
//                        pNew.buf = ByteBuffer.allocate(pNew.uiLen);
                        if (pNew.buf == null) continue;
                        System.arraycopy(pTransferAudioData.buf.array(), iPos, pNew.buf.array(), 0, pNew.uiLen);
//                        qyMemoryFunc.qyFuncs.mymemcpy(pNew.buf, (pTransferAudioData.buf + iPos), pNew.uiLen);
//                        System.arraycopy(pTransferAudioData.buf, iPos, pNew.buf, 0, pNew.uiLen);
//                        System.arraycopy(pTransferAudioData.buf.array(), iPos, pNew.buf.array(), 0, pNew.uiLen);

                        //										
                        pNew.tmpInternal.param.index_pMems_from = usIndex_pMems_from;// pTask.activeMems_from[index_activeMems_from].usIndex_pMems_from;
                        pNew.tmpInternal.param.index_activeMems_from = index_activeMems_from;

                        //
                        //
                        //
                        if  (  pProcInfo.cfg.debugStatusInfo.b_print_test) {
                            long tick_2 = myGetTickCount(null);
                            long iE = tick_2 - ctxCaller.tick_before;
                            str = "rtMediaQ size " + getQ2Nodes(pProcInfo.realTimeMediaQ2) + " find_searchInput costs " + iE;
                            showInfo_open.qyFuncs.showInfo_open(str);
                        }



                        //
//                        pMsgInput_toTransform.lenInBytes = (int) + pNew.uiLen;

                        if (null != pTransform) {
                            //  
                            if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
                                //guiShare.GuiShare.pf_dumpData1.call(pMsgInput_toTransform.buffer.array(), pMem.uiLen, 0, 0, true, false, "dump_deced_pcm.mp3");
                                q2PostMsgAndTrigger(pMsgInput_toTransform, pTransform.audio.q2,"find_procInputAudioData.121");
                            } else {
                                byte[] buf = pNew.buf.array();

                                //
                                //guiShare.GuiShare.pf_dumpData1.call(buf, pMem.uiLen, 0, 0, true, false, "dump_deced_pcm.mp3");


                                pTransform.audio.qBuf2.q2PostMsgAndTrigger(pMem,buf,0,  pNew.uiLen,"find_procInputAudioData.671");
                                //
                                //Log.d(AUDIO_DOWNLOAD, "3 qBuf2.qs0.uiQNodes "+pTransform.audio.qBuf2.qs0.uiQNodes+" qBuf2.qs1.uiQNodes "+pTransform.audio.qBuf2.qs1.uiQNodes);
                            }
                        }
                    }

                    iErr = 1;
                    return iErr;
                }

            } else {

                if (pMsgInput.iTaskId == iTaskId) {
                    //  2011/01/23				  
                    if (pMsgInput.data.route.idInfo_from.ui64Id == pMisCnt.idInfo.ui64Id) {
                        //  Log.e(  (  "audio data sent by myself"  )  );

//                        q2PostMsg(pMsgInput,  pTask.mixer.pOutputQ2);
                        qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_realTimeMedia);
                        if (null != pChannel) {
                            if  (  pChannel.m_b_useQ2_toOutput) {
                                q2PostMsgAndTrigger(null, pChannel.outputQ21, "find_procInputAudioData.691");
                            }
                            else  {
                                  pChannel.outputQBuf2.q2PostMsgAndTrigger(null,null,0, 0,"");

                            }
                        }

                        //
                        iErr = 1;
                        return iErr;
                    }
                }

                //  2014/11/21
                if (pTask.applyForProxy.bRecvd_proxyReply) {       //  applier for proxy
                    if (pTask.applyForProxy.proxyReply.proxy.uiTranNo_openAvDev_a == pTransferAudioData.uiTranNo_openAvDev) {

                        iErr = 1;
                        return iErr;
                    }
                }


                //  2014/11/21
                if (!pTask.receiver.bRecvd_proxiedTranInfo) {  //  一般性的数据包处理，或者是proxy

                    //  search in mems_from directly
                    for (index = 0; index < videoConference.usCntLimit_activeMems_from; index++) {
                        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index];

                        //
                        if (pActiveMem_from.avStream.idInfo.ui64Id == pMsgInput.data.route.idInfo_from.ui64Id
                                && pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev == pTransferAudioData.uiTranNo_openAvDev) {

                            pTransferAudioData.tmpInternal.param.index_pMems_from = -1;
                            pTransferAudioData.tmpInternal.param.index_activeMems_from = index;

                            //  2014/11/21
                            if (pTask.doProxy.bExists_avProxy) {

                                int index_activeMems_from = 0;

                                //
                                qnmCommProc_mis.QIS_DISPATCH pDispatch = MACRO_getDispatch(pTask, index_activeMems_from);
                                if (null != pDispatch) {
                                    q2PostMsgAndTrigger(pMsgInput, pDispatch.audio.q2,"find_procInputAudioData.731");
                                }
                                iErr = 1;
                                return iErr;
                            }

                            //
                            qmcTransform.QY_TRANSFORM pTransform = MACRO_getTransform(pTask, pTransferAudioData.tmpInternal.param.index_activeMems_from);
                            if (null != pTransform) {
                                if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
                                    //  qPostMsgAndTrigger_toDo(  pMsgTask,  offsetof(  qnmCommProc_mis.MIS_MSG_TASK,  data  )  +  pMsgTask->lenInBytes,  &pTransform->audio  );
                                    q2PostMsgAndTrigger(pMsgInput, pTransform.audio.q2,"find_procInputAudioData.742");
                                } else {
                                    qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem;
                                    int iPos;
                                    int i;
                                    for (iPos = 0, i = 0; i < pTransferAudioData.usCnt; iPos = (int) (iPos + pMem.uiLen), i++) {
                                        pMem = getMem.qyFuncs.getMem(pTransferAudioData_mems, bufSize, i);// &pTransferAudioData_mems[i];
                                        if (pMem == null) return iErr;

                                        if (0 == pMem.uiLen) {
                                            continue;
                                        }

                                        if (iPos + pMem.uiLen > pTransferAudioData.uiLen) {
                                            return iErr;
                                        }

                                        if (pMem.idInfo.ui64Id == 0)
                                            pMem.idInfo.ui64Id = pMsgInput.data.route.idInfo_from.ui64Id;

                                        //
                                        if (0 == pMem.uiTranNo_openAvDev)
                                            pMem.uiTranNo_openAvDev = pTransferAudioData.uiTranNo_openAvDev;
                                        /*
                                        pTransferAudioData.buf.position(iPos);
                                        int len = pTransferAudioData.buf.remaining();
                                        byte[] data = new byte[len];
                                        pTransferAudioData.buf.get(data);
                                        pTransform.audio.qBuf2.q2PostMsgAndTrigger(pMem, data, 0,  pMem.uiLen,"find_procInputAudioData.769");
                                        data = null;

                                         */
                                        pTransform.audio.qBuf2.q2PostMsgAndTrigger(pMem, pTransferAudioData.buf.array(), iPos,  pMem.uiLen,"find_procInputAudioData.769");

                                        //
                                    }

                                    //
                                }
                            }

                            iErr = 1;
                            return iErr;
                        }
                    }
                    //
                } else {  //  receiver from proxy				  			 
                    if (pTask.mem0_from.idInfo.ui64Id == pMsgInput.data.route.idInfo_from.ui64Id
                            && pTask.mem0_from.audio.ta_recvd.uiTranNo_openAvDev == pTransferAudioData.uiTranNo_openAvDev) {
                        //  2014/11/21
                        if (pTask.receiver.proxied_from.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {
                            iErr = 1;
                            return iErr;
                        }

                        //
                        int i = 0;
                        int iPos = 0;
                        qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem = null;
                        int index_activeMems_from = 0;
                        int usIndex_pMems_from = 0;
                        qmcTransform.QY_TRANSFORM pTransform = null;
                        qnmCommProc_mis.MIS_MSG_INPUT pMsgInput_toTransform = null;
                        qnmCommProc_mis.TRANSFER_AUDIO_DATA pNew = null;

                        if (null == pTask.video.pMsgU_toTransform) {
                            Log.i("findOrgMsg failed. pMsgTask_toTransform is null");
                            return iErr;
                        }
                        pMsgInput_toTransform = null;// (qnmCommProc_mis.MIS_MSG_INPUT) pTask.video.pMsgU_toTransform;

                        if (0 == pTransferAudioData.usCnt) {
                            pTransferAudioData.usCnt = 1;  //  ¶ÔÀÏµÄÊý¾Ý°ü£¬Õâ¸öusCntÃ»Ìî£¬°üº¬Ò»¸öÊý¾Ý
//                            pTransferAudioData_mems[0].uiLen = pTransferAudioData.uiLen;
                        }
                        if (pTransferAudioData.usCnt > CONST_maxTransferAvDataMems) return iErr;

                        for (iPos = 0, i = 0; i < pTransferAudioData.usCnt; iPos = (int) (iPos + pTransferAudioData_mems[i].uiLen), i++) {
                            pMem = pTransferAudioData_mems[i];
                            if (0 == pMem.uiLen) {
                                Log.i("findOrgMsg: transferAudioMem.uiLen is 0. skipped.");
                                continue;
                            }

                            if (iPos + pTransferAudioData_mems[i].uiLen > pTransferAudioData.uiLen) {
                                Log.i("findOrgMsg failed: iPos + mem.uiLen > data.uiLen");
                                return iErr;
                            }

                            usIndex_pMems_from = 1;
                            qnmCommProc_mis.TASK_AV_FROM pFrom = null;
                            pFrom = getTaskAvFrom(pTask, (int) usIndex_pMems_from, (""));
                            if (null == pFrom) return iErr;

                            //
//                            MACRO_memset_misMsgInput(pMsgInput_toTransform);
//                            qyMemoryFunc.qyFuncs.mymemcpy(pMsgInput_toTransform, pMsgInput, 0);
                            System.arraycopy(pMsgInput, 0, pMsgInput_toTransform, 0, 128);
                            pMsgInput_toTransform.data.route.idInfo_from.ui64Id = pFrom.idInfo.ui64Id;
                            pMsgInput_toTransform.data.route.idInfo_to.ui64Id = pMsgInput.data.route.idInfo_to.ui64Id;
                            pNew = null;// (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pMsgInput_toTransform.data.buf;
                            qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pNew_mems = null;// (qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead) pNew.buf_TRANSFER_AUDIO_dataMemHead_mems;
                            //mymemset(pNew, 0, (int)Marshal.OffsetOf<qnmCommProc_mis.TRANSFER_AUDIO_DATA>("buf"));
                            pNew.uiType = pTransferAudioData.uiType;
                            pNew.uiTranNo_openAvDev = pFrom.audio.ta_recvd.uiTranNo_openAvDev;
//                            qyMemoryFunc.qyFuncs.mymemcpy((byte[]) pNew_mems, pMem, 0);
//                            System.arraycopy(pMem, 0, pNew_mems, 0, 128);
                            pNew.mems[0] = pMem;
                            pNew.usCnt = 1;
                            pNew.uiLen = pMem.uiLen;
//                            qyMemoryFunc.qyFuncs.mymemcpy(pNew.buf, (pTransferAudioData.buf + iPos), pNew.uiLen);
                            System.arraycopy(pTransferAudioData.buf.array(), iPos, pNew.buf.array(), 0, pNew.uiLen);

                            //										
                            pNew.tmpInternal.param.index_pMems_from = (int) usIndex_pMems_from;
                            pNew.tmpInternal.param.index_activeMems_from = index_activeMems_from;

                            //
                            pMsgInput_toTransform.lenInBytes = (int) + +pNew.uiLen;

                            pTransform = MACRO_getTransform(pTask, index_activeMems_from);
                            if (null != pTransform) {
                                //  
                                q2PostMsgAndTrigger(pMsgInput_toTransform, pTransform.audio.q2,"find_procInputAudioData.121");
                            }

                            //  2014/11/11
                            continue;

                        }

                        iErr = 1;
                        return iErr;
                    }

                    //
                    iErr = 0;
                    return iErr;
                }
            }


            iErr = 0;

            return iErr;

        }


        //
        public static int tmpHandler_findOrgMsgAndProc_procTaskVideoData(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MSGR_ADDR pAddr, int iTaskId, qnmCommProc_mis.MIS_MSG_TASK pMsgTask, qnmCommProc_mis.TRANSFER_VIDEO_DATA pTransferVideoData, qnmCommProc_mis.IM_CONTENTU pContentElem, taskAv.PROC_TASK_AV pTask) {
            int iErr = -1;
            int index = 0;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            /*


            //
            //Log.d(com.qycx.base.Consts.VIDEO_UPLOAD,"2.2 tmpHandler_findOrgMsgAndProcess_mis_procTaskVideoData");
            //
            qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pTransferVideoDataMems[] = pTransferVideoData.mems;
            //
            boolean bDbg = false;
            String str;

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "findOrgMsg_procTaskVideoData");
            if (null == pTaskInfo) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;
            if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            //
            if (pTask.ucbVideoConferenceStarter) {
            } else if (pTask.bVideoConference()) {
                //
                //
                if (pMsgTask.bFrom_sharedObj) {
                    qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, pMsgTask.index_sharedObj);
                    if (null == pSharedObj) return iErr;
                    int i;
                    for (i = 0; i < (pSharedObj.usrs.length); i++) {
                        qmcSharedObj.SHARED_OBJ_USR pSou = pSharedObj.usrs[i];
                        if (0 == pSou.iTaskId) continue;
                        if (pSou.iTaskId != iTaskId) continue;

                        //					   
                        if (pMsgTask.data.route.idInfo_from.ui64Id == pMisCnt.idInfo.ui64Id) {
                            //  Log.e(  (  "video data sent by myself"  )  );

                            boolean bSend = true;
                            //
                            bSend = canSend(pSharedObj); // 判断能否发言。

                            if (bSend) {
                                //if(MeetingStatusCenter.getInstance().getCurrentNetStatus()) {
                                //Log.d(com.qycx.base.Consts.VIDEO_UPLOAD,"2.3 pTask.photomosaic.pOutputQ2");

                                //
                                qnmCommProc_mis.TRANSFER_VIDEO_DATA tvd  = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pMsgTask.data.buf;

                                //
                                if  (  pProcInfo.cfg.b_useQ2_toEncV) {
                                    q2PostMsg(pMsgTask, pTask.photomosaic.pOutputQ2, "find_procTaskVideoData.933"); //原来这么用的，可能不对,先改回来。
                                }
                                else  {
                                      int j;
                                      int iPos =  0;
                                      byte [] video = tvd.buf.array();
                                      for  (  j  =  0;  j  <  tvd.usCnt;j  ++) {
                                          qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead mem_i  =  tvd.mems[j];
                                          qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead mem_o = new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();
                                          mem_o.copyFrom(mem_i);
                                          //
                                          pTask.photomosaic.pOutputQBuf2.q2PostMsg(mem_o,video,iPos,mem_o.uiLen,"");
                                          iPos  +=  mem_o.uiLen;
                                      }
                                }


                                //  q2PostMsgAndTrigger(pMsgTask, pTask.photomosaic.pOutputQ2); //zzq改成这样了，需要测试。
                                qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_media);
                                if (null != pChannel) { //
                                    if(pChannel.hThread_io == null)
                                    {
                                        startChannel(pProcInfo,pMisCnt,pChannel,"start audio Channel");
                                    }
                                    chkToApplyForChannel(pProcInfo, pChannel, CONST_channelType_media);
//                                    Log.d(IO_TAG,"3 tmpHandler_findOrgMsgAndProcess_mis_procTaskVideoData");
                                    if  (  pChannel.m_b_useQ2_toOutput) {
                                        q2PostMsgAndTrigger(null, pChannel.outputQ21, "find_procTaskVideoData.943");
                                    }
                                    else  {
                                          pChannel.outputQBuf2.q2PostMsgAndTrigger(null,null,0, 0,"");

                                    }
                                }
                            }

                            //
                            iErr = 1;
                            return iErr;

                        }

                        //
                        break;
                    }
                }

                if (pTask.mem0_from.idInfo.ui64Id == pMsgTask.data.route.idInfo_from.ui64Id
                        && pTask.mem0_from.video.tv_recvd.uiTranNo_openAvDev == pTransferVideoData.uiTranNo_openAvDev) {
                    int i = 0;
                    int iPos = 0;
                    qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem = null;
                    int index_activeMems_from = 0;
                    int usIndex_pMems_from = 0;
                    qmcTransform.QY_TRANSFORM pTransform = null;
                    qnmCommProc_mis.MIS_MSG_TASK pMsgTask_toTransform = null;
                    qnmCommProc_mis.TRANSFER_VIDEO_DATA pNew = null;

                    if (null == pTask.video.pMsgU_toTransform) {
                        return iErr;
                    }
                    pMsgTask_toTransform = (qnmCommProc_mis.MIS_MSG_TASK) pTask.video.pMsgU_toTransform;

                    if (0 == pTransferVideoData.usCnt) {
                        pTransferVideoData.usCnt = 1;  //  ¶ÔÀÏµÄÊý¾Ý°ü£¬Õâ¸öusCntÃ»Ìî£¬°üº¬Ò»¸öÊý¾Ý
                        pTransferVideoDataMems[0].uiLen = pTransferVideoData.uiLen;
                    }
                    if (pTransferVideoData.usCnt > CONST_maxTransferAvDataMems) return iErr;

                    for (iPos = 0, i = 0; i < pTransferVideoData.usCnt; iPos = (int) (iPos + pTransferVideoDataMems[i].uiLen), i++) {
                        pMem = pTransferVideoDataMems[i];
                        if (0 == pMem.uiLen) {
                            continue;
                        }

                        if (iPos + pTransferVideoDataMems[i].uiLen > pTransferVideoData.uiLen) {
                            return iErr;
                        }

                        if (pMem.idInfo.ui64Id == 0)
                            pMem.idInfo.ui64Id = pMsgTask.data.route.idInfo_from.ui64Id;
                        //

                        //
                        for (index_activeMems_from = 0; index_activeMems_from < videoConference.usCntLimit_activeMems_from; index_activeMems_from++) {
                            if (pMem.idInfo.ui64Id == videoConference.activeMems_from[index_activeMems_from].avStream.idInfo.ui64Id
                                    && pMem.uiTranNo_openAvDev == videoConference.activeMems_from[index_activeMems_from].avStream.obj.tranInfo.video.uiTranNo_openAvDev)
                                break;
                        }
                        if (index_activeMems_from == videoConference.usCntLimit_activeMems_from) {
                            if (pTask.bVideoConference() && !pTask.ucbVideoConferenceStarter) {
                                pMisCnt.refreshVideoConferenceActiveMems.iTaskId = iTaskId;
                                pMisCnt.refreshVideoConferenceActiveMems.bNeedRefreshed = true;
                            }

                            continue;
                        }

                        //
                        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_activeMems_from];

                        pMsgTask_toTransform = pMsgTask;
                        pMsgTask_toTransform.data.route.idInfo_from.ui64Id = videoConference.activeMems_from[index_activeMems_from].avStream.idInfo.ui64Id;
                        pMsgTask_toTransform.data.route.idInfo_to.ui64Id = pMsgTask.data.route.idInfo_to.ui64Id;
                        pNew = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pMsgTask_toTransform.data.buf;
                        pNew.uiType = pTransferVideoData.uiType;
                        pNew.uiTranNo_openAvDev = pActiveMem_from.avStream.obj.tranInfo.video.uiTranNo_openAvDev;
                        pNew.mems[0] = pMem;
                        pNew.usCnt = 1;
                        pNew.uiLen = pMem.uiLen;
                        pNew.buf = pTransferVideoData.buf;
                        pNew.tmpInternal.param.index_pMems_from = -1;
                        pNew.tmpInternal.param.index_activeMems_from = index_activeMems_from;

                        //
                        pMsgTask_toTransform.lenInBytes = pNew.uiLen;

                        pTransform = MACRO_getTransform(pTask, index_activeMems_from);
                        if (pTransform != null) {
                            //
                            q2PostMsgAndTrigger(pMsgTask_toTransform, pTransform.v_preTransThread.q2,"find_procTaskVideoData.1031");
                        }

                        //  2014/11/11
                        continue;
                    }

                    iErr = 1;
                    return iErr;
                }
            }   //  end of processing confMem
            else {  //
                // 
                if (pMsgTask.bFrom_sharedObj) {
                    qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, pMsgTask.index_sharedObj);
                    if (null == pSharedObj) return iErr;
                    int i;
                    for (i = 0; i < pSharedObj.usrs.length; i++) {
                        qmcSharedObj.SHARED_OBJ_USR pSou = pSharedObj.usrs[i];
                        if (0 == pSou.iTaskId) continue;
                        if (pSou.iTaskId != iTaskId) continue;

                        if (pMsgTask.data.route.idInfo_from.ui64Id == pMisCnt.idInfo.ui64Id) {
                            //  Log.e(  (  "video data sent by myself"  )  );

                            boolean bSend = false;
                            bSend = canSend(pSharedObj);

                            if (bSend) {
                                if (isEmpty_ROUTE_sendLocalAv(pTaskInfo.var.curRoute_sendLocalAv)) {
                                    bSend = false;
                                }
                            }

                            if (bSend) {
                                q2PostMsg(pMsgTask, pTask.photomosaic.pOutputQ2,"find_procTaskVideoData.1066");
                                qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_media);
                                if (null != pChannel) {
                                    if(pChannel.hThread_io == null)
                                    {
                                        startChannel(pProcInfo,pMisCnt,pChannel,"start Video Channel");
                                    }
                                    chkToApplyForChannel(pProcInfo, pChannel, CONST_channelType_media);

                                    //
                                    if  (  pChannel.m_b_useQ2_toOutput) {
                                        q2PostMsgAndTrigger(null, pChannel.outputQ21, "find_procTaskVideoData.1076");
                                    }
                                    else  {
                                          pChannel.outputQBuf2.q2PostMsgAndTrigger(null,null,0,0,"");

                                    }
                                }


                            }

                            //				  
                            iErr = 1;
                            return iErr;
                        }


                        break;
                    }

                }


                //  2014/11/21
                if (pTask.applyForProxy.bRecvd_proxyReply) {       //  applier for proxy
                    if (pTask.applyForProxy.proxyReply.proxy.uiTranNo_openAvDev_v == pTransferVideoData.uiTranNo_openAvDev) {

                        iErr = 1;
                        return iErr;
                    }
                }

                //  2014/11/21
                if (!pTask.receiver.bRecvd_proxiedTranInfo) {  //  一般性的数据包处理，或者是proxy

                    //  search in mems_from directly
                    for (index = 0; index < videoConference.usCntLimit_activeMems_from; index++) {

                        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index];

                        //
                        if (pActiveMem_from.avStream.idInfo.ui64Id == pMsgTask.data.route.idInfo_from.ui64Id
                                && pActiveMem_from.avStream.obj.tranInfo.video.uiTranNo_openAvDev == pTransferVideoData.uiTranNo_openAvDev) {
                            pTransferVideoData.tmpInternal.param.index_pMems_from = -1;
                            pTransferVideoData.tmpInternal.param.index_activeMems_from = index;

                            //  2014/11/21
                            if (pTask.doProxy.bExists_avProxy) {

                                int index_activeMems_from = 0;

                                //
                                qnmCommProc_mis.QIS_DISPATCH pDispatch = MACRO_getDispatch(pTask, index_activeMems_from);
                                if (pDispatch != null) {
                                    q2PostMsgAndTrigger(pMsgTask, pDispatch.video.q2,"find_procTaskVideoData.1125");
                                }
                                iErr = 1;
                                return iErr;
                            }

                            //
                            qmcTransform.QY_TRANSFORM pTransform = MACRO_getTransform(pTask, pTransferVideoData.tmpInternal.param.index_activeMems_from);
                            if (pTransform != null) {
                                //  
                                qyq2.QY_Q2 pQ2 = pTransform.v_preTransThread.q2;
                                if (null == pTransform.v_preTransThread.hThread
                                        && null != pTransform.video.hThread) {
                                    pQ2 = pTransform.video.q2;
                                }

                                //
                                q2PostMsgAndTrigger(pMsgTask, pQ2,"find_procTaskVideoData.1142");
                            }
                            iErr = 1;
                            return iErr;
                        }
                    }
                    //
                } else {  //  receiver from proxy				  			 
                    if (pTask.mem0_from.idInfo.ui64Id == pMsgTask.data.route.idInfo_from.ui64Id
                            && pTask.mem0_from.video.tv_recvd.uiTranNo_openAvDev == pTransferVideoData.uiTranNo_openAvDev) {
                        //  2014/11/21
                        if (pTask.receiver.proxied_from.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {
                            iErr = 1;
                            return iErr;
                        }

                        //
                        int i = 0;
                        int iPos = 0;
                        qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem = null;
                        int index_activeMems_from = 0;
                        int usIndex_pMems_from = 0;
                        qmcTransform.QY_TRANSFORM pTransform = null;
                        qnmCommProc_mis.MIS_MSG_TASK pMsgTask_toTransform = null;
                        qnmCommProc_mis.TRANSFER_VIDEO_DATA pNew = null;

                        if (null == pTask.video.pMsgU_toTransform) {
                            return iErr;
                        }
                        pMsgTask_toTransform = (qnmCommProc_mis.MIS_MSG_TASK) pTask.video.pMsgU_toTransform;

                        if (0 == pTransferVideoData.usCnt) {
                            pTransferVideoData.usCnt = 1;  //  ¶ÔÀÏµÄÊý¾Ý°ü£¬Õâ¸öusCntÃ»Ìî£¬°üº¬Ò»¸öÊý¾Ý
                            pTransferVideoDataMems[0].uiLen = pTransferVideoData.uiLen;
                        }
                        if (pTransferVideoData.usCnt > CONST_maxTransferAvDataMems) return iErr;

                        for (iPos = 0, i = 0; i < pTransferVideoData.usCnt; iPos = (int) (iPos + pTransferVideoDataMems[i].uiLen), i++) {
                            pMem = pTransferVideoDataMems[i];
                            if (0 == pMem.uiLen) {
                                continue;
                            }

                            if (iPos + pTransferVideoDataMems[i].uiLen > pTransferVideoData.uiLen) {
                                return iErr;
                            }

                            usIndex_pMems_from = 1;
                            qnmCommProc_mis.TASK_AV_FROM pFrom = null;
                            pFrom = getTaskAvFrom(pTask, (int) usIndex_pMems_from, (""));
                            if (null == pFrom) return iErr;

                            pMsgTask_toTransform = pMsgTask;
                            pMsgTask_toTransform.data.route.idInfo_from.ui64Id = pFrom.idInfo.ui64Id;
                            pMsgTask_toTransform.data.route.idInfo_to.ui64Id = pMsgTask.data.route.idInfo_to.ui64Id;
                            pNew = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pMsgTask_toTransform.data.buf;
                            pNew.uiType = pTransferVideoData.uiType;
                            pNew.uiTranNo_openAvDev = pFrom.video.tv_recvd.uiTranNo_openAvDev;
                            pNew.mems[0] = pMem;
                            pNew.usCnt = 1;
                            pNew.uiLen = pMem.uiLen;
                            pNew.buf = pTransferVideoData.buf;
                            pNew.tmpInternal.param.index_pMems_from = (int) usIndex_pMems_from;
                            pNew.tmpInternal.param.index_activeMems_from = index_activeMems_from;

                            //
                            pMsgTask_toTransform.lenInBytes = (int) +pNew.uiLen;

                            pTransform = MACRO_getTransform(pTask, index_activeMems_from);
                            if (pTransform != null) {
                                //  
                                q2PostMsgAndTrigger(pMsgTask_toTransform, pTransform.v_preTransThread.q2,"find_procTaskVideoData.1213");
                            }

                            //  2014/11/11
                            continue;

                        }

                        iErr = 1;
                        return iErr;
                    }

                    //
                    iErr = 0;
                    return iErr;
                }
            }

             */


            iErr = 0;

            return iErr;
        }


        //
        public static int tmpHandler_findOrgMsgAndProc_procInputVideoData(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MSGR_ADDR pAddr, int iTaskId, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, qnmCommProc_mis.TRANSFER_VIDEO_DATA pTransferVideoData, qnmCommProc_mis.IM_CONTENTU pContentElem, taskAv.PROC_TASK_AV pTask) {
//            Log.e("哈哈呵呵", "tmpHandler_findOrgMsgAndProcess_mis_procInputVideoData" );

            int iErr = -1;
            int index = 0;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead[] pTransferVideoDataMems = pTransferVideoData.mems;
            int bufSize = bufSize_TRANSFER_VIDEO_dataMemHead_mems;
            qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pNewMems = null;
            //
            boolean bDbg = false;
            String str;

            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "findOrgMsg_procInputVideoData");
            if (null == pTaskInfo) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;
            if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            if (pTask.ucbVideoConferenceStarter) {
            } else if (pTask.bVideoConference()) {//会议分支, zzq
//                Log.e("哈哈呵呵", "tmpHandler_findOrgMsgAndProcess_mis_procInputVideoData: bVideoConference" );

                //
                if (pMsgInput.iTaskId == iTaskId) {
                    //  2011/01/23				  
                    if (pMsgInput.data.route.idInfo_from.ui64Id == pMisCnt.idInfo.ui64Id) {
                        /*
                        q2PostMsg(pMsgInput, pTask.photomosaic.pOutputQ2,"find_procInputVideoData.1268");
                        qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_media);
                        if (null != pChannel) {
                            if  (  pChannel.m_b_useQ2_toOutput) {
                                q2PostMsgAndTrigger(null, pChannel.outputQ21, "find_procInputVideoData.1271");
                            }
                            else  {
                                pChannel.outputQBuf2.q2PostMsgAndTrigger(null,null,0,  0,"");
                            }

                        }

                         */
                        Log.e("qy","th_findOrgMsgAndProc_procInputVideodata: Err here");

                        //
                        iErr = 1;
                        return iErr;
                    }
                }

                if (pTask.mem0_from.idInfo.ui64Id == pMsgInput.data.route.idInfo_from.ui64Id
                        && pTask.mem0_from.video.tv_recvd.uiTranNo_openAvDev == pTransferVideoData.uiTranNo_openAvDev) {
                    int i = 0;
                    int iPos = 0;
                    qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem = null;
                    int index_activeMems_from = 0;
                    int usIndex_pMems_from = 0;
                    qmcTransform.QY_TRANSFORM pTransform = null;
                    qnmCommProc_mis.MIS_MSG_INPUT pMsgInput_toTransform = null;
                    qnmCommProc_mis.TRANSFER_VIDEO_DATA pNew = null;

                    if (null == pTask.video.pMsgU_toTransform) {
                        return iErr;
                    }
//                    pMsgInput_toTransform = (qnmCommProc_mis.MIS_MSG_INPUT) pTask.video.pMsgU_toTransform;

                    if (0 == pTransferVideoData.usCnt) {
                        pTransferVideoData.usCnt = 1;  //  ¶ÔÀÏµÄÊý¾Ý°ü£¬Õâ¸öusCntÃ»Ìî£¬°üº¬Ò»¸öÊý¾Ý
                        pTransferVideoDataMems[0].uiLen = pTransferVideoData.uiLen;
                    }
                    if (pTransferVideoData.usCnt > CONST_maxTransferAvDataMems) return iErr;


                    for (iPos = 0, i = 0; i < pTransferVideoData.usCnt; iPos = (int) (iPos + pMem.uiLen), i++) {
                        pMem = getMem.qyFuncs.getMem(pTransferVideoDataMems, bufSize, i);
                        if (pMem == null) continue;

                        if (0 == pMem.uiLen) {
                            continue;
                        }

                        if (iPos + pMem.uiLen > pTransferVideoData.uiLen) {
                            return iErr;
                        }

                        if (pMem.idInfo.ui64Id == 0)
                            pMem.idInfo.ui64Id = pMsgInput.data.route.idInfo_from.ui64Id;

                        //
                        if (videoConference.usCntLimit_activeMems_from > videoConference.activeMems_from.length) {
                            str = String.format("usCntLimit_activeMems_from " + videoConference.usCntLimit_activeMems_from + ", activeMems.length " + videoConference.activeMems_from.length);
                            Log.d(str);
                        }

                        for (index_activeMems_from = 0; index_activeMems_from < videoConference.usCntLimit_activeMems_from; index_activeMems_from++) {
                            if (pMem.idInfo.ui64Id == videoConference.activeMems_from[index_activeMems_from].avStream.idInfo.ui64Id
                                    && pMem.uiTranNo_openAvDev == videoConference.activeMems_from[index_activeMems_from].avStream.obj.tranInfo.video.uiTranNo_openAvDev)
                                break;
                        }
                        if (index_activeMems_from == videoConference.usCntLimit_activeMems_from) {
                            str = String.format("findOrgMsg: video, conf" + pAddr.idInfo.ui64Id + ", from " + pMem.idInfo.ui64Id + ", can't get index_activeMems_from, skipped.");
                            Log.e(str);
                            showInfo_open.qyFuncs.showInfo_open(0, null, str);
                            iErr = CONST_qyRet_cantGet_index_activeMems_from;
                            return iErr;
                        }
                        //
                        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_activeMems_from];


                        //
                        if  (  true  ) {
                            //showInfo_open.qyFuncs.showInfo_open("find_procInputVideoData: pts " + pMem.uiPts);
                        }


                        //
                        if (pProcInfo.cfg.debugStatusInfo.ucb_chkInputPts) {

                            //
                            if (0 != pActiveMem_from.last_iPts_v) {
                                if (pMem.uiPts != pActiveMem_from.last_iPts_v + 1) {
                                    str = String.format("v.act[" + index_activeMems_from + "]," + pActiveMem_from.avStream.idInfo.ui64Id + ":pts err, mem.pts " + pMem.uiPts + ", last_iPts " + pActiveMem_from.last_iPts_v);
                                    Log.d(str);
                                    showInfo_open.qyFuncs.showInfo_open(0, null, str);
                                }
                            }

                        }

                        //
                        boolean  bSkipped  = false;
                        if  (  pActiveMem_from.last_iPts_v   !=  0)  {
                            if  (  pActiveMem_from.last_iPts_v  +1  !=  pMem.uiPts)  {
                                showInfo_open.qyFuncs.showInfo_open("find_procInputVideoData: Err: last_pts "  +  pActiveMem_from.last_iPts_v  +  " mem.pts " + pMem.uiPts  + " skipped");
                                bSkipped=true;

                                //
                                qmCommFunc_mis.MIS_CHANNEL pChannel  =  getChannelByType(pMisCnt,CONST_channelType_media);


                            }
                        }

                        //							
                        pActiveMem_from.last_iPts_v = (int) pMem.uiPts;

                        //
                        if  (  bSkipped)  {
                            continue;
                        }

                        //
                        pTransform = MACRO_getTransform(pTask, index_activeMems_from);
                        if  (  pTransform==null)  {
                            continue;
                        }


                        //
                        if (!pProcInfo.cfg.b_useVideoQ2_toDecV) {  //  这里已经避免了下面的内存分配

                            //
                            pTransform.v_preTransThread.qBuf2.q2PostMsgAndTrigger(pMem,pTransferVideoData.buf.array(),iPos, pMem.uiLen,"find_procInputVideoData.1404");
                            //
                            //Log.d(VIDEO_DOWNLOAD, "3 v_preTransThread.qBuf2.qs0 " + pTransform.v_preTransThread.qBuf2.qs0.uiQNodes + " v_preTransThread.qBuf2.qs1 " +pTransform.v_preTransThread.qBuf2.qs1.uiQNodes );

                            continue;
                        }



//                        pMsgInput_toTransform = pMsgInput;
                        pMsgInput_toTransform = new qnmCommProc_mis.MIS_MSG_INPUT();
                        pMsgInput.copyDataUntilData(pMsgInput_toTransform);
                        pMsgInput_toTransform.data.buf = new qnmCommProc_mis.TRANSFER_VIDEO_DATA();

                        pMsgInput_toTransform.data.route.idInfo_from.ui64Id = videoConference.activeMems_from[index_activeMems_from].avStream.idInfo.ui64Id;
                        pMsgInput_toTransform.data.route.idInfo_to.ui64Id = pMsgInput.data.route.idInfo_to.ui64Id;
                        pNew = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pMsgInput_toTransform.data.buf;
                        pNew.uiType = pTransferVideoData.uiType;
                        pNew.uiTranNo_openAvDev = pActiveMem_from.avStream.obj.tranInfo.video.uiTranNo_openAvDev;
                        pNew.mems[0] = pMem;
                        pNew.usCnt = 1;
                        pNew.uiLen = pMem.uiLen;
//
//                        pNew.buf = pTransferVideoData.buf;
                        pNew.buf = ByteBuffer.allocate(pNew.uiLen);
                        if (pNew.buf == null) continue;
//                        System.arraycopy(pTransferAudioData.buf.array(), iPos, pNew.buf.array(), 0, pNew.uiLen);
                        System.arraycopy(pTransferVideoData.buf.array(), iPos, pNew.buf.array(), 0, pNew.uiLen);
                        pNew.tmpInternal.param.index_pMems_from = -1;
                        pNew.tmpInternal.param.index_activeMems_from = index_activeMems_from;

//                        int buf1_len = 0;//(int)Marshal.OffsetOf<MIS_MSG_routeTalkData>("buf");
//                        int buf2_len = 0;//(int)Marshal.OffsetOf<TRANSFER_VIDEO_DATA>("buf");
//                        pMsgInput_toTransform.lenInBytes = buf1_len + buf2_len + pNew.uiLen;


                        //
                        //
                        //long tick_2  =  myGetTickCount(null);
                        //long iE  =  tick_2  -  ctxCaller.tick_before;

                        //
                        if (pTransform != null) {
                            //
                            if (pProcInfo.cfg.b_useVideoQ2_toDecV) {
                                if (isQ2Warning(pTransform.v_preTransThread.q2, 10)) {

                                    qyq2.QY_Q2 a = pTransform.v_preTransThread.q2;
                                    qyq2.QY_Q2_cfg b = a.cfg;
                                    str = "tmp_findOrg_procInputVideoData: " + b.name + " is full, empty it";
                                    Log.d(str);
                                    emptyQ2(pTransform.v_preTransThread.q2);
                                }
                            }


                            // for debug
                            pMem.tmp_tn_msgInput  =  pMsgInput.uiTranNo;
                            //
                            //Log.e("tn_msgInput","findOrgMsg_v: tn_msgInput " +  pMem.tmp_tn_msgInput + " mem[" + i +"] pts " + pMem.uiPts  );

                            //  
                            if (pProcInfo.cfg.b_useVideoQ2_toDecV) {
                                int i1 = q2PostMsgAndTrigger(pMsgInput_toTransform, pTransform.v_preTransThread.q2,"find_procInputVideodata.1401");
//                                Log.e("哈哈呵呵", "tmpHandler_findOrgMsgAndProcess_mis_procInputVideoData: q2PostMsgAndTrigger: " + getQ2Nodes(pTransform.v_preTransThread.q2));
                            } else {
                                pTransform.v_preTransThread.qBuf2.q2PostMsgAndTrigger(pMem,pNew.buf.array(),0, pNew.uiLen,"find_procInputVideoData.1404");
                                //
                                //Log.d(VIDEO_DOWNLOAD, "3 v_preTransThread.qBuf2.qs0 " + pTransform.v_preTransThread.qBuf2.qs0.uiQNodes + " v_preTransThread.qBuf2.qs1 " +pTransform.v_preTransThread.qBuf2.qs1.uiQNodes );
                            }
                        }



                        //  2014/11/11
                        continue;
                    }

                    iErr = 1;
                    return iErr;
                }
            }   //  end of processing confMem
            else {  //  

                if (pMsgInput.iTaskId == iTaskId) {
                    //  2011/01/23				  
                    if (pMsgInput.data.route.idInfo_from.ui64Id == pMisCnt.idInfo.ui64Id) {

                        /*
                        q2PostMsg(pMsgInput, pTask.photomosaic.pOutputQ2,"find_procInputVideoData.1423");
                        qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_media);
                        if (pChannel != null) {
                            q2PostMsgAndTrigger(null, pChannel.toSendQ2,"find_procInputVideoData.1426");
                        }

                         */

                        //
                        Log.e("qy","th_findOrgMsgAndProc_procInputVideoData: err here");

                        //
                        iErr = 1;
                        return iErr;
                    }
                }

                //  2014/11/21
                if (pTask.applyForProxy.bRecvd_proxyReply) {       //  applier for proxy
                    if (pTask.applyForProxy.proxyReply.proxy.uiTranNo_openAvDev_v == pTransferVideoData.uiTranNo_openAvDev) {
                        iErr = 1;
                        return iErr;
                    }
                }

                //  2014/11/21
                if (!pTask.receiver.bRecvd_proxiedTranInfo) {  //  一般性的数据包处理，或者是proxy
                    //  search in mems_from directly
                    for (index = 0; index < videoConference.usCntLimit_activeMems_from; index++) {
                        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index];
                        //
                        qnmCommProc_mis.MIS_MSG_INPUT tmp_msgInput = pMsgInput;
                        qnmCommProc_mis.TRANSFER_VIDEO_DATA tmp_transferVideoData = pTransferVideoData;

                        //
                        if (pActiveMem_from.avStream.idInfo.ui64Id == pMsgInput.data.route.idInfo_from.ui64Id
                                && pActiveMem_from.avStream.obj.tranInfo.video.uiTranNo_openAvDev == pTransferVideoData.uiTranNo_openAvDev) {

                            pTransferVideoData.tmpInternal.param.index_pMems_from = -1;// index;
                            pTransferVideoData.tmpInternal.param.index_activeMems_from = index;

                            //  2014/11/21
                            if (pTask.doProxy.bExists_avProxy) {

                                int index_activeMems_from = 0;

                                //
                                qnmCommProc_mis.QIS_DISPATCH pDispatch = MACRO_getDispatch(pTask, index_activeMems_from);
                                if (pDispatch != null) {
                                    q2PostMsgAndTrigger(pMsgInput, pDispatch.video.q2,"find_procInputVideoData.1467");
                                }
                                iErr = 1;
                                return iErr;
                            }

                            //
                            qmcTransform.QY_TRANSFORM pTransform = null;
                            pTransform = MACRO_getTransform(pTask, pTransferVideoData.tmpInternal.param.index_activeMems_from);
                            //pTransform = pTask.getLastTransform();
                            if (pTransform != null) {
                                //if (pTransform.index_activeMems_from_decV_bak == index)
                                {
                                    //  
                                    qyq2.QY_Q2 pQ2 = pTransform.v_preTransThread.q2;
                                    {
                                        if (pProcInfo.cfg.b_useVideoQ2_toDecV) {
                                            q2PostMsgAndTrigger(pMsgInput, pQ2,"find_procInputVideoData.1484");
                                            String str_i = "findOrg q2PostMsgAndTrigger: TN: " +  pMsgInput.uiTranNo + " pts: ";
                                            showInfo_open.qyFuncs.showInfo_open(str_i);
//                                            Log.e(str_i);
                                        } else {//点对点, zzq
                                            qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem;
                                            int iPos;
                                            int i;
                                            for (iPos = 0, i = 0; i < pTransferVideoData.usCnt; iPos = (int) (iPos + pMem.uiLen), i++) {
                                                pMem = getMem.qyFuncs.getMem(pTransferVideoDataMems, bufSize, i);// &pTransferAudioData_mems[i];
                                                if (pMem == null) return iErr;

                                                if (0 == pMem.uiLen) {

                                                    continue;
                                                }

                                                if (iPos + pMem.uiLen > pTransferVideoData.uiLen) {
                                                    return iErr;
                                                }

                                                if (pMem.idInfo.ui64Id == 0)
                                                    pMem.idInfo.ui64Id = pMsgInput.data.route.idInfo_from.ui64Id;

                                                //
                                                if (0 == pMem.uiTranNo_openAvDev)
                                                    pMem.uiTranNo_openAvDev = pTransferVideoData.uiTranNo_openAvDev;

                                                //
                                                if  (  false  )  {
                                                    Log.d("qy",""  +  pMsgInput.data.route.idInfo_from.ui64Id  +  ":find: v, pts "  + pMem.uiPts);
                                                }

                                                /*
                                                pTransferVideoData.buf.position(iPos);
                                                int remaining = pTransferVideoData.buf.remaining();
                                                byte[] data = new byte[remaining];
                                                pTransferVideoData.buf.get(data);

                                                //
                                                // for debug
                                                pMem.tmp_tn_msgInput  =  pMsgInput.uiTranNo;
                                                //
                                                if  (  true  ) {
                                                    //Log.e("tn_msgInput", "findOrgMsg_v: p2p: tn_msgInput " + pMem.tmp_tn_msgInput + " mem[" + i + "] pts " + pMem.uiPts);
                                                    long time = myGetTickCount(null);
                                                    str  =  "findOrgMsg_v: p2p: tn_msgInput " + pMem.tmp_tn_msgInput + " mem[" + i + "] pts " + pMem.uiPts + " mytime: " + time+ " size: " + pTransform.v_preTransThread.qBuf2.size();
                                                    showInfo_open.qyFuncs.showInfo_open(str);
                                                    Log.d(VIDEO_DOWNLOAD1, str);

                                                }
                                                //
                                                pTransform.v_preTransThread.qBuf2.q2PostMsgAndTrigger(pMem,data,0,pMem.uiLen,"find_procInputVideoData.1529");

                                                 */
                                                pTransform.v_preTransThread.qBuf2.q2PostMsgAndTrigger(pMem,pTransferVideoData.buf.array(),iPos,pMem.uiLen,"find_procInputVideoData.1529");

                                                //
                                            }

                                            //
                                        }
                                    }


                                    ///////////////////////////////////

                                }
                            }
                            iErr = 1;
                            return iErr;
                        }
                    }
                    //
                } else {
                    //
                    iErr = 0;
                    return iErr;
                }
            }


            iErr = 0;


            return iErr;
        }


        public static int procTaskProcReq_applyForKeyFrame(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MSGR_ADDR pAddr, int iTaskId, qnmCommProc_mis.MIS_MSG_TASK pMsgTask, qnmCommProc_mis.TASK_PROC_REQ pTaskProcReq, qnmCommProc_mis.IM_CONTENTU pContentElem, taskAv.PROC_TASK_AV pTask) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;


            String str = "procTaskProcReq_applyForKeyFrame called";
            Log.d(str);
            showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    //
            if (pTask.ucbVideoConferenceStarter) {

                if (pTaskProcReq.vStream.uiTranNo_openAvDev == pTask.mem0_from.video.tv_recvd.uiTranNo_openAvDev) {
                    //
                    setFlg_forceKeyFrame.qyFuncs.setFlg_forceKeyFrame(pProcInfo, pTask.iIndex_sharedObj);
                    //
                    iErr = 0;
                    return iErr;
                }

                //  2014/12/06
                if (pContentElem.uiType != CONST_imCommType_transferAvInfo) {
                    return iErr;
                }
                qnmCommProc_mis.TRANSFER_AV_INFO pTransferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem;
                //
                if (pTaskProcReq.vStream.uiTranNo_openAvDev == pTransferAvInfo.confMosaicTranInfo_video.video.uiTranNo_openAvDev) {
                    //
                    setFlg_forceKeyFrame.qyFuncs.setFlg_forceKeyFrame(pProcInfo, pTask.confMosaicMaker.iIndex_sharedObj_video);
                    //
                    iErr = 0;
                    return iErr;
                }
                //
                if (pTaskProcReq.vStream.uiTranNo_openAvDev == pTransferAvInfo.confMosaicTranInfo_resource.video.uiTranNo_openAvDev) {
                    //
                    setFlg_forceKeyFrame.qyFuncs.setFlg_forceKeyFrame(pProcInfo, pTask.confMosaicMaker.iIndex_sharedObj_resource);
                    //
                    iErr = 0;
                    return iErr;
                }

                Log.d(("Not finished: procTaskProcReq_applyForKeyFrame, conf starter: for others"));
                return iErr;

            } else if (pTask.bVideoConference()) {
                setFlg_forceKeyFrame.qyFuncs.setFlg_forceKeyFrame(pProcInfo, pTask.iIndex_sharedObj);
            } else {
                setFlg_forceKeyFrame.qyFuncs.setFlg_forceKeyFrame(pProcInfo, pTask.iIndex_sharedObj);
            }


            iErr = 0;

            return iErr;

        }

        public static int tmpHandler_find_procTaskVideoDataResp(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_TASK pMsgElem, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, qnmCommProc_mis.TRANSFER_VIDEO_dataResp pResp) {
            int iErr = -1;
            qnmCommProc_mis.IM_CONTENTU pContentElem = null;// (qnmCommProc_mis.IM_CONTENTU) pMsgElem.data.buf;
            int index = 0;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            iErr = 0;

            return iErr;
        }


        //
        public static boolean bInActiveMems(ctxQmc.CCtxQmc pProcInfo, qyDefs.QY_MESSENGER_ID idInfo, int tn_a, int tn_v, taskAv.TASK_transferAv task) {
            boolean bRet = false;
            //task.myTask.ucbVideoConference
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(null, pProcInfo, task.m_index_taskInfo, "");
            if (pTaskInfo == null) return bRet;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskInfo.getTaskData();
            if (pTc == null) return bRet;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();
            if (videoConference == null) return bRet;

            int i;
            for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
                qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom activeMem = videoConference.activeMems_from[i];
                if (idInfo.ui64Id == activeMem.avStream.idInfo.ui64Id) {
                    if (tn_a != 0
                            && tn_a == activeMem.avStream.obj.tranInfo.audio.uiTranNo_openAvDev) {
                        bRet = true;
                        return bRet;
                    }
                    if (tn_v != 0
                            && tn_v == activeMem.avStream.obj.tranInfo.video.uiTranNo_openAvDev) {
                        bRet = true;
                        return bRet;
                    }
                }
            }
            return bRet;
        }


        //
        public static int tmp_fwrite(qnmCommProc_mis.TRANSFER_FILEDATA_RESP pFileDataResp, taskAv.TASK_transferFile task) {
            int iErr = -1;
            if (1 != guiShare.GuiShare.pf_fwrite.call(pFileDataResp.buf.array(), pFileDataResp.uiLen, 1, task.myTask.fp)) {
                Log.e("fwite failed");
                showInfo_open.qyFuncs.showInfo_open(0, null, "fwite failed");
                return iErr;
            }
            iErr = 0;
            return iErr;

        }

        public static int tmp_tfopen(qnmCommProc_mis.IM_CONTENTU pContentElem, taskAv.TASK_transferFile task) {
            task.myTask.fp = guiShare.GuiShare.pf_tfopen.call(((qnmCommProc_mis.TRANSFER_FILE_REQ) pContentElem).fullFilePath, "rb");
            return 0;
        }

        public static int tmp_fread(qnmCommProc_mis.TRANSFER_FILEDATA_RESP pFileDataResp_o, int nRead, taskAv.TASK_transferFile task) {
            if (1 != guiShare.GuiShare.pf_fread.call(pFileDataResp_o.buf.array(), nRead, 1, task.myTask.fp)) {
                return -1;
            }
            return 0;
        }


        public static String tmp_get_fullFilePath(qnmCommProc_mis.IM_CONTENTU pContentElem) {
            return ((qnmCommProc_mis.TRANSFER_FILE_REQ) pContentElem).fullFilePath;
        }

        public static String tmp_get_fileName(qnmCommProc_mis.IM_CONTENTU pContentElem) {
            return ((qnmCommProc_mis.TRANSFER_FILE_REQ) pContentElem).fileName;

        }

        public static int tmp_qPost_msgInput_taskProcReq(qnmCommProc_mis.MIS_MSG_INPUT pMsgInput) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

            qPostMsg(pMsgInput, pQyMc.gui.processQ);

            return 0;

        }

        //
        public static int tmp_qPost_msgInput(qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, int len, genericQueue.GENERIC_Q pQ) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            qPostMsg(pMsgInput, pQ);
            return 0;

        }

        public static int tmp_qPost_msgTask(qnmCommProc_mis.MIS_MSG_TASK pMsgTask, int len, genericQueue.GENERIC_Q pQ) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            qPostMsg(pMsgTask, pQ);
            return 0;

        }


        public static mcSharedQ.PF_enumTask tmpHandler_findOrgMsgAndProc = (ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU pMsg_toBeSearched, com.qycx.base.qyMcMainProc.PARAM_findOrgMsg p1Param, com.qycx.base.taskCommon.TASK_common taskElem) ->
        {
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            int tmpiRet = 0;
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            qmCommFunc_mis.MIS_CNT pMisCntElem = pProcInfo.pMisCnt;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;


            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskElem.m_index_taskInfo, "findOrgMsg");
            if (pTaskInfo == null) return iErr;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return iErr;

            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                Log.e("for test, tmpHandler_findOrgMsgAndProc_is return 0");
                return 0;
            }

            qnmCommProc_mis.MIS_MSGU pMsgElemParam = pTaskData.msgU;
            {
                switch ((pMsgElemParam).uiType) {
                    case CONST_misMsgType_req:
                        iErr = findOrgMsg_req(ctxCaller, pMsg_toBeSearched, p1Param, taskElem);
                        return iErr;
                    case CONST_misMsgType_task:
                        iErr = findOrgMsg_task(ctxCaller, pMsg_toBeSearched, p1Param, taskElem);
                        return iErr;
                    default:
                        break;
                }
            }
            return iErr;
        };

        public static int findOrgMsg_req(ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU pMsg_toBeSearched, qyMcMainProc.PARAM_findOrgMsg p1Param, taskCommon.TASK_common taskElemParam) {

            int iErr = -1;

            String str;

            //IntPtr p1 = (IntPtr)p1Param;
            qyMcMainProc.PARAM_findOrgMsg p1 = p1Param;
            //  p1
            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            int tmpiRet = 0;
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;
            qmCommFunc_mis.MIS_CNT pMisCntElem = pProcInfo.pMisCnt;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;


            //
            taskCommon.TASK_common taskElem = (taskCommon.TASK_common) taskElemParam;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskElem.m_index_taskInfo, "findOrgMsg");
            if (pTaskInfo == null) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;

            //
            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                //Log.e("for test, tmpHandler_findOrgMsgAndProc_is return 0");
                return 0;
            }

            //fixed (MIS_MSGU* pMsgElemParam =pTaskData.msgU)
            qnmCommProc_mis.MIS_MSGU pMsgElemParam = pTaskData.msgU;

            qnmCommProc_mis.MIS_MSG_REQ pMsgElem = (qnmCommProc_mis.MIS_MSG_REQ) pMsgElemParam;
            //MIS_MSGU* pMsgU = (MIS_MSGU*)pMsgElem;
            //IM_CONTENTU* pContentElem = M_getMsgContent(pMsgElem->uiType,  pMsgElem->ucFlg, &pMsgElem->data);
            qnmCommProc_mis.IM_CONTENTU pContentElem = pMsgElem.data;// M_getMsgContent(pMsgElem->uiType, pMsgElem->ucFlg, &pMsgElem->data);
            //
            switch (pMsg_toBeSearched.uiType) {
                case CONST_misMsgType_input: {
                    qnmCommProc_mis.MIS_MSG_INPUT pMsgInput = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg_toBeSearched;
                    //
                    if (pMsgElem.tStartTime == pMsgInput.tStartTime
                            && pMsgElem.uiTranNo == pMsgInput.uiTranNo) {
                        pMsgInput.usCode_org = pMsgElem.usCode;
                        iErr = 1;
                        return iErr;        //  ÕÒµ½ÁË
                    }
                    //
                }
                break;
                default:
                    break;
            }
            //
            iErr = 0;
            errLabel:
            return iErr;
        }


        public static int findOrgMsg_task(ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU pMsg_toBeSearchedParam, qyMcMainProc.PARAM_findOrgMsg p1Param, taskCommon.TASK_common taskElemParam) {

            int iErr = -1;

            String str;

            qnmCommProc_mis.MIS_MSGU pMsg_toBeSearched = pMsg_toBeSearchedParam;
            //IntPtr p1 = (IntPtr)p1Param;
            qyMcMainProc.PARAM_findOrgMsg p1 = p1Param;
            //  p1
            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            int tmpiRet = 0;
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;
            qmCommFunc_mis.MIS_CNT pMisCntElem = pProcInfo.pMisCnt;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            taskCommon.TASK_common taskElem = (taskCommon.TASK_common) taskElemParam;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskElem.m_index_taskInfo, "findOrgMsg");
            if (pTaskInfo == null) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;

            //
            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                Log.e("for test, tmpHandler_findOrgMsgAndProc_is return 0");
                return 0;
            }

            //fixed (MIS_MSGU* pMsgElemParam =pTaskData.msgU)
            qnmCommProc_mis.MIS_MSGU pMsgElemParam = pTaskData.msgU;
            qnmCommProc_mis.MIS_MSG_TASK pMsgElem = (qnmCommProc_mis.MIS_MSG_TASK) pMsgElemParam;
            qnmCommProc_mis.IM_CONTENTU pContentElem = pMsgElem.data.buf;

            switch (pMsg_toBeSearched.uiType) {
                case CONST_misMsgType_task:
                    iErr = findOrgMsg_task_searchTask(ctxCaller, pMsg_toBeSearchedParam, p1Param, taskElemParam);
                    break;
                case CONST_misMsgType_input:
                    iErr = findOrgMsg_task_searchInput(ctxCaller, pMsg_toBeSearchedParam, p1Param, taskElemParam);
                    break;
                default:
                    break;
            }

            return iErr;

        }


        public static int findOrgMsg_task_searchTask(ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU pMsg_toBeSearchedParam, qyMcMainProc.PARAM_findOrgMsg p1Param, taskCommon.TASK_common taskElem) {

            int iErr = -1;

            String str;

            qnmCommProc_mis.MIS_MSGU pMsg_toBeSearched = pMsg_toBeSearchedParam;
            //IntPtr p1 = (IntPtr)p1Param;
            qyMcMainProc.PARAM_findOrgMsg p1 = p1Param;
            //  p1
            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            int tmpiRet = 0;
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;
            qmCommFunc_mis.MIS_CNT pMisCntElem = pProcInfo.pMisCnt;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;


            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskElem.m_index_taskInfo, "findOrgMsg");
            if (pTaskInfo == null) return iErr;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return iErr;

            //
            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                Log.e("for test, tmpHandler_findOrgMsgAndProc_is return 0");
                return 0;
            }

            qnmCommProc_mis.MIS_MSGU pMsgElemParam = pTaskData.msgU;

            qnmCommProc_mis.MIS_MSG_TASK pMsgElem = (qnmCommProc_mis.MIS_MSG_TASK) pMsgElemParam;
            qnmCommProc_mis.IM_CONTENTU pContentElem = pMsgElem.data.buf;

            {
                qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pMsg_toBeSearched;
                qnmCommProc_mis.IM_CONTENTU pContent = pMsgTask.data.buf;

                if (!isUcFlgResp(pMsgTask.ucFlg)) {
                    if (pContentElem.uiType == CONST_imCommType_transferFileReq) {
                        if (pContent.uiType == CONST_imCommType_transferFileReplyReq) {

                            qnmCommProc_mis.TRANSFER_FILE_REPLYREQ pReplyReq = (qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContent;
                            if (pMsgElem.tStartTime == pReplyReq.tStartTime_org
                                    && pMsgElem.uiTranNo == pReplyReq.uiTranNo_org) {
                                Log.d("tmpHandler_findOrgMsgAndProc_is: msg - replyReq is matched");
                                pMsgTask.addr_logicalPeer.idInfo.ui64Id = pMsgElem.addr_logicalPeer.idInfo.ui64Id;
                                iErr = 1;
                                return iErr;
                            }
                        }
                        if (pContent.uiType == CONST_imCommType_transferFileReq) {
                            if (pMsgElem.tStartTime == pMsgTask.tStartTime
                                    && pMsgElem.uiTranNo == pMsgTask.uiTranNo) {
                                iErr = 1;
                                return iErr;
                            }

                        }
                        if (pContent.uiType == CONST_imCommType_taskProcReq) {
                            qnmCommProc_mis.TASK_PROC_REQ task_proc_req = (qnmCommProc_mis.TASK_PROC_REQ) pContent;
                            if (pMsgElem.tStartTime == task_proc_req.tStartTime_org
                                    && pMsgElem.uiTranNo == task_proc_req.uiTranNo_org) {
                                //

                                Log.d("tmpHandler_findOrgMsgAndProc_is: msg - procReq is matched");

                                iErr = 1;
                                return iErr;
                            }
                        }
                    }
                    if (pContentElem.uiType == CONST_imCommType_transferFileReplyReq) {
                        if (pContent.uiType == CONST_imCommType_taskProcReq) {
                            qnmCommProc_mis.TRANSFER_FILE_REPLYREQ transferFileReplyRe = (qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContentElem;
                            qnmCommProc_mis.TRANSFER_FILE_REPLYREQ taskProcReq = (qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContent;
                            if (transferFileReplyRe.tStartTime_org == taskProcReq.tStartTime_org
                                    && transferFileReplyRe.uiTranNo_org == taskProcReq.uiTranNo_org) {

                                Log.d("tmpHandler_findOrgMsgAndProc_is: msg - procReq matched");

                                iErr = 1;
                                return iErr;
                            }
                        }
                    }
                    //
                    if (pContentElem.uiType == CONST_imCommType_transferAvInfo) {
                        //  media
                        tmpiRet = findOrgMsg_task_searchTask_av(ctxCaller, pMsg_toBeSearchedParam, p1Param, taskElem);
                        if (tmpiRet != 0) {
                            iErr = tmpiRet;
                            return iErr;
                        }

                    }
                    //  2012/04/19
                    if (pContentElem.uiType == CONST_imCommType_transferGpsInfo) {                               //  media·¢Æð¶Ë
                        //
                    }

                }

                //
            }


            iErr = 0;
            errLabel:
            return iErr;

        }

        public static int findOrgMsg_task_searchInput(ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU pMsg_toBeSearchedParam, qyMcMainProc.PARAM_findOrgMsg p1Param, taskCommon.TASK_common taskElemParam) {

            int iErr = -1;
            if(p1Param == null) return iErr;

            String str;


            //bDbg = true;


            //
            qnmCommProc_mis.MIS_MSGU pMsg_toBeSearched = pMsg_toBeSearchedParam;
            //IntPtr p1 = (IntPtr)p1Param;
            qyMcMainProc.PARAM_findOrgMsg p1 = p1Param;
            //  p1
            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            int tmpiRet = 0;
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;
            qmCommFunc_mis.MIS_CNT pMisCntElem = pProcInfo.pMisCnt;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;


            //
            taskCommon.TASK_common taskElem = (taskCommon.TASK_common) taskElemParam;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskElem.m_index_taskInfo, "findOrgMsg");
            if (pTaskInfo == null) return iErr;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return iErr;

            //
            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                //Log.e("for test, tmpHandler_findOrgMsgAndProc_is return 0");
                return 0;
            }

            if (pProcInfo.cfg.mSpecialDbg.bTrace_tmpHandler_findOrgMsgAndProc_is) {
            }
            if (p1Param.bBreak) {
                int ii = 0;
            }


            //fixed (MIS_MSGU* pMsgElemParam =pTaskData.msgU)
            qnmCommProc_mis.MIS_MSGU pMsgElemParam = pTaskData.msgU;

            qnmCommProc_mis.MIS_MSG_TASK pMsgElem = (qnmCommProc_mis.MIS_MSG_TASK) pMsgElemParam;
            qnmCommProc_mis.IM_CONTENTU pContentElem = pMsgElem.data.buf;


            {
                qnmCommProc_mis.MIS_MSG_INPUT pMsgInput = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg_toBeSearched;
                qnmCommProc_mis.IM_CONTENTU pContent = pMsgInput.data.buf;// M_getMsgContent(pMsgInput->uiType,pMsgInput->ucFlg, &pMsgInput->data);
                //
                //if (pMsgInput->pMisCnt != pMisCntElem) break;

                //
                //  2014/08/28
                qnmCommProc_mis.IM_CONTENTU tmp_pContent = pContent;

                //
                if (p1Param.bBreak) {
                    int ii = 0;
                }

                if (pContent.uiType == CONST_imCommType_taskProcReq) {
                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_recv_applyForKeyFrame) {
                        int i;
                        i = 9;
                    }
                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_recv_applyForOrgReq) {
                        int i;
                        i = 9;
                    }
                }


                //
                if (isUcFlgResp(pMsgInput.ucFlg) && !isUcFlgResp(pMsgElem.ucFlg)) {
                    switch (pContentElem.uiType) {
                        case CONST_imCommType_transferAvInfo: {
                            if (!pTaskInfo.var.m_bReplyTask) {
                                qnmCommProc_mis.TRANSFER_AV_INFO pTransferAvInfoElem = (qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem;
                                taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                                taskAv.PROC_TASK_AV pTaskElem =
                                        task.myTask;// get_transferAvInfo_pTask(pTransferAvInfoElem, ""));
                                //if (!pTaskElem) goto errLabel;
                                //
                                if (pContent.uiType == CONST_imCommType_transferVideoDataResp) {
                                    tmpiRet = tmpHandler_find_procTaskVideoDataResp(pProcInfo, pMsgElem, pMsgInput, (qnmCommProc_mis.TRANSFER_VIDEO_dataResp) pContent);
                                    if (0 != tmpiRet) {
                                        iErr = tmpiRet;
                                        return iErr;
                                    }
                                }
                                if (pContent.uiType == CONST_imCommType_transferAudioDataResp) {
                                    qnmCommProc_mis.TRANSFER_AUDIO_dataResp transferAudioDataResp = (qnmCommProc_mis.TRANSFER_AUDIO_dataResp) pContent;
                                    if (pMsgElem.data.route.idInfo_from.ui64Id == pMsgInput.data.route.idInfo_to.ui64Id
                                            && pTransferAvInfoElem.ass.tranInfo.audio.uiTranNo_openAvDev == transferAudioDataResp.uiTranNo_openAvDev) {

                                        if (0 != procTaskAudioDataResp(pProcInfo, pTransferAvInfoElem.ass.tranInfo, pTaskElem, pMsgInput, transferAudioDataResp)) {
                                            return iErr;
                                        }

                                        iErr = 1;
                                        return iErr;
                                    }
                                }
                                //
                            } else {
                                //case CONST_imCommType_transferAvReplyInfo:
                                taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                                qnmCommProc_mis.TRANSFER_AV_replyInfo pTransferAvReplyInfoElem1 = (qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem;
                                taskAv.PROC_TASK_AV pTaskElem = task.myTask;// get_transferAvReplyInfo_pTask(pTransferAvProcInfoElem, ""));
                                //if (!pTaskElem) goto errLabel;
                                qnmCommProc_mis.AV_TRAN_INFO tmp_avTranInfo = pTaskElem.reply_avTranInfo;
                                qnmCommProc_mis.AV_TRAN_INFO pAvTranInfoElem =
                                        tmp_avTranInfo;// &pTransferAvReplyInfoElem1->tranInfo;

                                //
                                if (pContent.uiType == CONST_imCommType_transferVideoDataResp) {

                                    tmpiRet = tmpHandler_find_procTaskVideoDataResp(pProcInfo, pMsgElem, pMsgInput, (qnmCommProc_mis.TRANSFER_VIDEO_dataResp) pContent);
                                    if (0 != tmpiRet) {
                                        iErr = tmpiRet;
                                        return iErr;
                                    }
                                }
                                if (pContent.uiType == CONST_imCommType_transferAudioDataResp) {
                                    qnmCommProc_mis.TRANSFER_AUDIO_dataResp transferAudioDataResp = (qnmCommProc_mis.TRANSFER_AUDIO_dataResp) pContent;
                                    if (pMsgElem.data.route.idInfo_to.ui64Id == pMsgInput.data.route.idInfo_from.ui64Id
                                            && pAvTranInfoElem.audio.uiTranNo_openAvDev == transferAudioDataResp.uiTranNo_openAvDev) {

                                        if (0 != procTaskAudioDataResp(pProcInfo, pAvTranInfoElem, pTaskElem, pMsgInput, transferAudioDataResp)) {
                                            return iErr;
                                        }
                                        iErr = 1;
                                        return iErr;
                                    }
                                }
                                //
                            }
                        }
                        break;
                        case CONST_imCommType_transferGpsInfo: {
                        }
                        break;
                        case CONST_imCommType_transferFileReq: {
                            taskAv.TASK_transferFile task = (taskAv.TASK_transferFile) taskElem;
                            if (pTaskData.uiType != CONST_taskDataType_transferFile) {
                                return iErr;
                            }
                            qmcTaskInfo.QMC_taskData_transferFile pTt = (qmcTaskInfo.QMC_taskData_transferFile) pTaskData;
                            qnmCommProc_mis.MIS_MSG_INPUT tmp_pMsgInput = pMsgInput;

                            if ((pMsgInput.tStartTime == task.myTask.reply_info.tStartTime_reply
                                    && pMsgInput.uiTranNo == task.myTask.reply_info.uiTranNo_reply)
                                    || (pMsgInput.tStartTime == task.myTask.reply_info.tStartTime_lastFileDataReq
                                    && pMsgInput.uiTranNo == task.myTask.reply_info.uiTranNo_lastFileDataReq)
                            ) {
                                Log.d("");

                                if (null != p1 && pContent.uiType == CONST_imCommType_simpleResp && ((qnmCommProc_mis.IM_SIMPLE_RESP) pContent).uiContentType_org == task.myTask.reply_info.replyReq.uiType) {
                                    qnmCommProc_mis.TRANSFER_FILE_REPLYREQ pFileReplyReq =
                                            task.myTask.reply_info.replyReq;
                                    {
                                        if (null == p1.pFileDataReq_o || 0 == task.myTask.ui64FileLen || 0 == task.myTask.uiSizePerSnd_internal) {
                                            Log.d("tmpHandler_findOrgMsgAndProc_is failed");
                                            return iErr;
                                        }
                                        qnmCommProc_mis.TRANSFER_FILEDATA_REQ pFileDataReq_o = p1.pFileDataReq_o.transferFileDataReq;
                                        pFileDataReq_o.uiSizePerSnd = task.myTask.uiSizePerSnd_internal;
                                        pFileDataReq_o.tStartTime_org = pFileReplyReq.tStartTime_org;
                                        pFileDataReq_o.uiTranNo_org = pFileReplyReq.uiTranNo_org;
                                        pFileDataReq_o.uiSerialNo_org = pFileReplyReq.uiSerialNo_org;
                                        //
                                        pFileDataReq_o.uiDataBlockIndex = 0;   //  Õâ±íÃ÷ÊÇ´ÓÍ·¿ªÊ¼

                                        //
                                        task.myTask.reply_info.tStartTime_lastFileDataReq = pFileDataReq_o.tmpInternal.tStartTime_new;
                                        task.myTask.reply_info.uiTranNo_lastFileDataReq = pFileDataReq_o.tmpInternal.uiTranNo_new;

                                        //
                                        str = String.format("findOrgMsg: start to retrieve file. total %d bytes. perSnd %d", task.myTask.ui64FileLen, pFileDataReq_o.uiSizePerSnd);
                                        Log.e(str);
                                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                        //
                                        iErr = 1;
                                        return iErr;
                                    }
                                }

                                if (null != p1 && pContent.uiType == CONST_imCommType_transferFileDataResp) {
                                    //
                                    qnmCommProc_mis.TRANSFER_FILE_REPLYREQ pFileReplyReq =
                                            task.myTask.reply_info.replyReq;
                                    {
                                        qnmCommProc_mis.TRANSFER_FILEDATA_RESP pFileDataResp = (qnmCommProc_mis.TRANSFER_FILEDATA_RESP) pContent;
                                        long ui64offset = 0;
                                        int nWrite = 0;

                                        if (null == p1.pContent_o || 0 == task.myTask.ui64FileLen || 0 == task.myTask.uiSizePerSnd_internal || bufSize_transferFileDataResp < task.myTask.uiSizePerSnd_internal) {
                                            Log.d("tmpHandler_findOrgMsgAndProc_is failed: ");
                                            return iErr;
                                        }
                                        qnmCommProc_mis.IM_CONTENTU pContent_o = p1.pContent_o.imContentU;

                                        if (pContent_o.uiType == CONST_imCommType_transferFileEndReq) {
                                            qnmCommProc_mis.TRANSFER_FILEEND_REQ pFileEndReq_o = (qnmCommProc_mis.TRANSFER_FILEEND_REQ) pContent_o;

                                            //  2015/09/08
                                            if (pFileEndReq_o.tmpInternal.bChkToRemove_i) {
                                                //pFileReplyReq->myTask.bNeedRemoved  =  TRUE;
                                                pTaskInfo.var.bNeedRemoved = true;
                                                //  2015/09/08
                                                pFileEndReq_o.tmpInternal.bNeedRemoved_o = true;
                                            }

                                            //
                                            pFileEndReq_o.tStartTime_org = pFileReplyReq.tStartTime_org;
                                            pFileEndReq_o.uiTranNo_org = pFileReplyReq.uiTranNo_org;
                                            pFileEndReq_o.uiSerialNo_org = pFileReplyReq.uiSerialNo_org;

                                            iErr = 1;
                                            return iErr;
                                        }

                                        if (pContent_o.uiType == CONST_imCommType_transferFileDataReq) {

                                            qnmCommProc_mis.TRANSFER_FILEDATA_REQ pFileDataReq_o = (qnmCommProc_mis.TRANSFER_FILEDATA_REQ) pContent_o;
                                            pFileDataReq_o.uiSizePerSnd = task.myTask.uiSizePerSnd_internal;
                                            pFileDataReq_o.tStartTime_org = pFileReplyReq.tStartTime_org;
                                            pFileDataReq_o.uiTranNo_org = pFileReplyReq.uiTranNo_org;
                                            pFileDataReq_o.uiSerialNo_org = pFileReplyReq.uiSerialNo_org;


                                            //
                                            if (pProcInfo.cfg.debugStatusInfo.ucbViewFileStatus) {
                                                str = String.format("get pFileDataResp->uiDataBlockIndex %d, %d bytes. tn %d", pFileDataResp.uiDataBlockIndex, pFileDataResp.uiLen, pMsgInput.uiTranNo);
                                                Log.e(str);
                                                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                            }


                                            //
                                            {    //  2009/10/20. check whether resp is ok
                                                boolean bRespOk = true;
                                                if (0 == pFileDataResp.uiDataBlockIndex) {
                                                    if (0 != task.myTask.uiLastDataBlockIndex)
                                                        bRespOk = false;
                                                } else {
                                                    if (pFileDataResp.uiDataBlockIndex != task.myTask.uiLastDataBlockIndex + 1)
                                                        bRespOk = false;
                                                }
                                                if (!bRespOk) {
                                                    //

                                                    Log.d("pFileDataResp->uiDataBlockIndex err");

                                                    str = String.format("Err: pFileDataResp->uiDataBlockIndex %d, fileReplyReq.uiLastDataBlockIndex %d", pFileDataResp.uiDataBlockIndex, task.myTask.uiLastDataBlockIndex);
                                                    Log.e(str);
                                                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                                    task.myTask.usErrors++;
                                                    return iErr;
                                                }
                                            }


                                            ui64offset = ((long) pFileDataResp.uiDataBlockIndex) * task.myTask.uiSizePerSnd_internal;
                                            if (ui64offset >= task.myTask.ui64FileLen) {
                                                str = "ui64offset >= pFileReplyReq->ui64FileLen err";
                                                Log.e(str);
                                                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                                return iErr;
                                            }


                                            if (null == task.myTask.fp) {
                                                String tmpFilePath = "";
                                                //
                                                //_sntprintf(tmpFilePath, mycountof(tmpFilePath), "%s.tmp"), task->myTask.fullFilePath_save);
                                                tmpFilePath = task.myTask.fullFilePath_save + ".tmp";
                                                //
                                                task.myTask.fp = guiShare.GuiShare.pf_tfopen.call(tmpFilePath, ("wb"));
                                                if (null == task.myTask.fp) {
                                                    str = String.format("fileDataResp: fopen %s failed", tmpFilePath);
                                                    Log.e(str);
                                                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                                    return iErr;
                                                }
                                                //
                                                pTt.dwStartTickCnt = myGetTickCount(null);
                                                pTt.dwLastReportTickCnt = pTt.dwStartTickCnt;
                                                task.myTask.ucPercent = 0;
                                                //
                                                //
                                                //
                                                showTaskStatus((Object) pMisCnt, pMsgElem.addr_logicalPeer.idInfo, pMsgElem.idInfo_taskSender, pMsgElem.idInfo_taskReceiver, false, CONST_imCommType_transferFileReq, pMsgElem.iTaskId, CONST_imTaskStatus_receiving, (byte) 0, 0, null, null);
                                            }

                                            if (0 != guiShare.GuiShare.pf_fseeki64.call(task.myTask.fp, ui64offset, 0//SEEK_SET
                                            )) {
                                                Log.d("tmpHandler_findOrgMsgAndProc_is failed:  _fseeki64 err");
                                                str = String.format(("_fseeki64 err"));
                                                Log.e(str);
                                                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                                return iErr;
                                            }

                                            nWrite = (int) Math.min(task.myTask.ui64FileLen - ui64offset, task.myTask.uiSizePerSnd_internal);
                                            if (pFileDataResp.uiLen != nWrite) {
                                                Log.d("pFileDataResp->uiLen  !=  nWrite");
                                                str = String.format("pFileDataResp->uiLen!=nWrite err,%d,%I64u,%I64u,%d,%d", nWrite, task.myTask.ui64FileLen, ui64offset, task.myTask.uiSizePerSnd_internal, pFileDataResp.uiLen);
                                                Log.e(str);
                                                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                                return iErr;
                                            }

                                            tmp_fwrite(pFileDataResp, task);

                                            task.myTask.uiLastDataBlockIndex = pFileDataResp.uiDataBlockIndex;


                                            //
                                            long dwCurTickCnt;
                                            //
                                            dwCurTickCnt = myGetTickCount(null);
                                            if (dwCurTickCnt > pTt.dwLastReportTickCnt + 1000) {   //
                                                pTt.dwLastReportTickCnt = dwCurTickCnt;
                                                //
                                                float f = 0;
                                                byte ucPercent;
                                                //
                                                f = (float) task.myTask.uiLastDataBlockIndex * task.myTask.uiSizePerSnd_internal;
                                                f = f / task.myTask.ui64FileLen;
                                                ucPercent = (byte) (f * 100);

                                                //  if  (  ucPercent  !=  pFileReplyReq->ucPercent  )
                                                {   //  ±¨¸æ×´Ì¬

                                                    showTaskStatus(pMisCnt, pMsgElem.addr_logicalPeer.idInfo, pMsgElem.idInfo_taskSender, pMsgElem.idInfo_taskReceiver, false, CONST_imCommType_transferFileReq, pMsgElem.iTaskId, CONST_imTaskStatus_receiving, ucPercent, (long) task.myTask.uiLastDataBlockIndex * task.myTask.uiSizePerSnd_internal, null, null);

                                                }
                                            }


                                            pFileDataReq_o.uiDataBlockIndex = task.myTask.uiLastDataBlockIndex + 1;


                                            if (pFileDataReq_o.uiDataBlockIndex * task.myTask.uiSizePerSnd_internal >= task.myTask.ui64FileLen) {
                                                //  Òª°ÑÁÙÊ±ÎÄ¼þÒÆ³ÉÕýÊ½ÎÄ¼þ
                                                //TCHAR tmpFilePath[MAX_PATH + 1] = "");
                                                String tmpFilePath;
                                                //
                                                str = String.format(("all file data received. %I64u bytes. to copyfile"), task.myTask.ui64FileLen);
                                                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                                if (null != task.myTask.fp) {

                                                    Log.d("tmpHandler_findOrgMsgAndProc_is: before fclose");

                                                    guiShare.GuiShare.pf_fclose.call(task.myTask.fp);
                                                    task.myTask.fp = null;

                                                    Log.d("tmpHandler_findOrgMsgAndProc_is: after fclose");
                                                }
                                                //
                                                //_sntprintf(tmpFilePath, mycountof(tmpFilePath), "%s.tmp"), task->myTask.fullFilePath_save);
                                                //fixed (char* fullFilePath_save = task.myTask.fullFilePath_save)
                                                String fullFilePath_save = task.myTask.fullFilePath_save;
                                                {
                                                    // tmpFilePath = new String(fullFilePath_save) + ".tmp";
                                                    tmpFilePath = fullFilePath_save + ".tmp";
                                                }
                                                //
                                                if (!guiShare.GuiShare.pf_CopyFile.call(tmpFilePath, task.myTask.fullFilePath_save, false)) {
                                                    //TCHAR tBuf[256] = "");

                                                    Log.e(String.format("CopyFile %s->%s failed", tmpFilePath, task.myTask.fullFilePath_save));
                                                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                                    str = String.format(("copyfile failed. %s->%s"), tmpFilePath, task.myTask.fullFilePath_save);
                                                    Log.e(str);
                                                    str = String.format(("Copy failed. %s"), task.myTask.fileName_transferred);
                                                    return iErr;
                                                }
                                                guiShare.GuiShare.pf_DeleteFile.call(tmpFilePath);

                                                {
                                                    Object pDb = null;
                                                    qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                                                    {
                                                        ;
                                                        if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys))
                                                            return iErr;
                                                        pDb = db.m_pDbMem.pDb;

                                                        if (0 != g_dbFuncs.pf_updateTaskStatus.call(pDb, CONST_imTaskStatus_recvFinished, pMsgElem.iTaskId))
                                                            return iErr;
                                                    }
                                                }

                                                showTaskStatus((Object) pMisCnt, pMsgElem.addr_logicalPeer.idInfo, pMsgElem.idInfo_taskSender, pMsgElem.idInfo_taskReceiver, false, CONST_imCommType_transferFileReq, pMsgElem.iTaskId, CONST_imTaskStatus_recvFinished, (byte) 100, task.myTask.ui64FileLen, task.myTask.fileName_transferred, task.myTask.fullFilePath_save);

                                                //
                                                pFileDataReq_o.tmpInternal.ucbFileEnd = true;
                                                Log.d("bFileEnd is true");

                                                //
                                                str = "Transfering file ends.";
                                                Log.i(str);
                                                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                            }

                                            //
                                            if (!pFileDataReq_o.tmpInternal.ucbFileEnd) {
                                                task.myTask.reply_info.tStartTime_lastFileDataReq = pFileDataReq_o.tmpInternal.tStartTime_new;
                                                task.myTask.reply_info.uiTranNo_lastFileDataReq = pFileDataReq_o.tmpInternal.uiTranNo_new;
                                            }

                                            //
                                            iErr = 1;
                                            return iErr;
                                        }

                                        Log.d("");
                                    }
                                }

                                iErr = 1;
                                return iErr;
                            }
                            //
                        }

                        break;

                        default:
                            break;
                    }

                    if (pMsgInput.tStartTime == pMsgElem.tStartTime
                            && pMsgInput.uiTranNo == pMsgElem.uiTranNo) {
                        Log.d("");


                        /////////////////////////
                        iErr = 1;
                        return iErr;
                    }
                }
                if (!isUcFlgResp(pMsgInput.ucFlg) && !isUcFlgResp(pMsgElem.ucFlg)) {
                    switch (pContentElem.uiType) {
                        case CONST_imCommType_transferAvInfo: {
                            if (!pTaskInfo.var.m_bReplyTask) {
                                qnmCommProc_mis.TRANSFER_AV_INFO pTransferAvInfoElem = (qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem;
                                taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                                taskAv.PROC_TASK_AV pTaskElem = task.myTask;// get_transferAvInfo_pTask(pTransferAvInfoElem, ""));

                                if (pContent.uiType == CONST_imCommType_transferAvReplyInfo) {
                                    if (pMsgElem.tStartTime == ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent).tStartTime_org
                                            && pMsgElem.uiTranNo == ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent).uiTranNo_org) {
                                        //OutputDebugString("tmpHandler_findOrgMsgAndProc_is . msg - reply is matched\n"));            //

                                        procTaskAvReply(pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pTransferAvInfoElem.ass.tranInfo, pTaskElem, pMsgInput);

                                        //
                                        iErr = 1;
                                        return iErr;
                                    }
                                }
                                if (pContent.uiType == CONST_imCommType_transferVideoData) {                           //

                                    tmpiRet = tmpHandler_findOrgMsgAndProc_procInputVideoData(ctxCaller, pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgInput, (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent, pContentElem, pTaskElem);
                                    if (0 != tmpiRet) {
                                        iErr = tmpiRet;
                                        return iErr;
                                    }
                                }
                                //////////////////////////
                                ///
                                if (pContent.uiType == CONST_imCommType_transferAudioData) {


                                    //
                                    tmpiRet = tmpHandler_findOrgMsgAndProc_procInputAudioData(ctxCaller, pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgInput, (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent, pContentElem, pTaskElem);
                                    if (0 != tmpiRet) {
                                        iErr = tmpiRet;
                                        return iErr;
                                    }
                                }
                                //
                                if (pContent.uiType == CONST_imCommType_taskProcReq) {

                                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_recv_applyForKeyFrame) {
                                        int ii = 0;


                                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, (""), pProcInfo->who_showInfo, 0, ""), ""), "recv applyforKeyFrame from %I64u, tn_openAvDev %d. matched iTaskId %d"), pMsgInput->data.route.idInfo_from.ui64Id, pContent->taskProcReq.vStream.uiTranNo_openAvDev, pMsgElem->iTaskId);

                                        boolean tmp_bFound = false;
                                        //
                                        if (pTaskElem.ucbVideoConferenceStarter) {
                                            return iErr;
                                        } else {

                                            qnmCommProc_mis.IM_CONTENTU tmp_pContent1 = pContent;
                                            qnmCommProc_mis.IM_CONTENTU tmp_pContentElem1 = pContentElem;


                                            if (pTaskData.uiType != CONST_taskDataType_conf)
                                                return iErr;
                                            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                                            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();
                                            if (videoConference == null) return iErr;

                                            for (int i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
                                                qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom activeMem = videoConference.activeMems_from[i];
                                                if (activeMem.avStream.idInfo.ui64Id == pMisCnt.idInfo.ui64Id
                                                        && activeMem.avStream.obj.tranInfo.video.uiTranNo_openAvDev == ((qnmCommProc_mis.TASK_PROC_REQ) pContent).vStream.uiTranNo_openAvDev) {
                                                    tmp_bFound = true;
                                                    break;

                                                }
                                            }

                                            //
                                            if (tmp_bFound) {


                                                Log.d("findOrgMsgAndProcess_mis: get a applyForKeyFrame msg");


                                                //
                                                tmpiRet = procTaskProcReq_applyForKeyFrame(pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgElem, (qnmCommProc_mis.TASK_PROC_REQ) pContent, pContentElem, pTaskElem);
                                                if (tmpiRet != 0) {
                                                    iErr = tmpiRet;
                                                    return iErr;
                                                }

                                                iErr = 1;
                                                return iErr;
                                            }
                                            //

                                        }
                                    }

                                }
                            } else {
                                //case CONST_imCommType_transferAvReplyInfo:
                                //if (pContentElem->uiType == CONST_imCommType_transferAvReplyInfo)
                                //  media»Ø¸´¶Ë
                                taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                                //TRANSFER_AV_replyInfo* pTransferAvReplyInfoElem =pContentElem->transferAvReplyInfo;
                                taskAv.PROC_TASK_AV pTaskElem = task.myTask;// get_transferAvReplyInfo_pTask(pTransferAvReplyInfoElem, ""));
                                //if (!pTaskElem) goto errLabel;
                                int index = 0;

                                if (pContent.uiType == CONST_imCommType_transferAudioData) {




                                    //
                                    tmpiRet = tmpHandler_findOrgMsgAndProc_procInputAudioData(ctxCaller, pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgInput, (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent, pContentElem, pTaskElem);
                                    if (0 != tmpiRet) {
                                        iErr = tmpiRet;
                                        return iErr;
                                    }

                                }
                                if (pContent.uiType == CONST_imCommType_transferVideoData) {                           //  ¶Ô·½µÄÊý¾Ý
                                    //
                                    tmpiRet = tmpHandler_findOrgMsgAndProc_procInputVideoData(ctxCaller, pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgInput, (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent, pContentElem, pTaskElem);
                                    if (0 != tmpiRet) {
                                        iErr = tmpiRet;
                                        return iErr;
                                    }

                                }

                                if (pContent.uiType == CONST_imCommType_transferAvInfo) {       //  ÖØ·¢Ê±ÕâÀïÒªÐ£ÑéµÄ

                                    if (pMsgElem.data.route.idInfo_to.ui64Id == pMsgInput.data.route.idInfo_from.ui64Id
                                            && ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem).tStartTime_org == pMsgInput.tStartTime
                                            && ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem).uiTranNo_org == pMsgInput.uiTranNo) {
                                        //

                                        Log.d("tmpHandler_findOrgMsgAndProc_is: msg - req is matched");

                                        //
                                        iErr = 1;
                                        return iErr;
                                    }
                                }
                                if (pContent.uiType == CONST_imCommType_transferAvReplyInfo) {                           //  ´¦Àí
                                    if (((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem).tStartTime_org == ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent).tStartTime_org
                                            && ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem).uiTranNo_org == ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent).uiTranNo_org) {
                                        //

                                        Log.d("tmpHandler_findOrgMsgAndProc_is . msg - procReq matched.");

                                        //
                                        iErr = 1;
                                        return iErr;
                                    }
                                }
                                if (pContent.uiType == CONST_imCommType_taskProcReq) {   //
                                    //
                                    if (pMsgElem.tStartTime == ((qnmCommProc_mis.TASK_PROC_REQ) pContent).tStartTime_org
                                            && pMsgElem.uiTranNo == ((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org)
                                    //if (pContentElem->transferAvReplyInfo.tStartTime_org == pContent->taskProcReq.tStartTime_org
                                    //&& pContentElem->transferAvReplyInfo.uiTranNo_org == pContent->taskProcReq.uiTranNo_org)
                                    {
                                        //

                                        Log.d("tmpHandler_findOrgMsgAndProc_is: msg - procReq is matched");

                                        //
                                        iErr = 1;
                                        return iErr;
                                    }
                                    //
                                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_recv_applyForKeyFrame) {

                                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, (""), pProcInfo->who_showInfo, 0, ""), ""), "recv applyforKeyFrame from %I64u, tn_openAvDev %d. matched iTaskId %d"), pMsgInput->data.route.idInfo_from.ui64Id, pContent->taskProcReq.vStream.uiTranNo_openAvDev, pMsgElem->iTaskId);

                                        boolean tmp_bFound = false;
                                        //
                                        if (pTaskElem.ucbVideoConferenceStarter) {
                                            return iErr;
                                        } else {

                                            qnmCommProc_mis.IM_CONTENTU tmp_pContent1 = pContent;
                                            qnmCommProc_mis.IM_CONTENTU tmp_pContentElem1 = pContentElem;

                                            qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, 0);
                                            if (pSharedObj != null) {
                                                int ii;
                                                for (ii = 0; ii < pSharedObj.usrs.length; ii++) {
                                                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).vStream.uiTranNo_openAvDev == pSharedObj.usrs[ii].uiTranNo_openAvDev_v) {
                                                        tmp_bFound = true;
                                                        break;
                                                    }
                                                }

                                            }
                                        }
                                        //
                                        if (tmp_bFound) {


                                            Log.d("findOrgMsgAndProcess_mis: get a applyForKeyFrame msg");


                                            //
                                            tmpiRet = procTaskProcReq_applyForKeyFrame(pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgElem, (qnmCommProc_mis.TASK_PROC_REQ) pContent, pContentElem, pTaskElem);
                                            if (tmpiRet != 0) {
                                                iErr = tmpiRet;
                                                return iErr;
                                            }

                                            iErr = 1;
                                            return iErr;
                                        }
                                        //
                                    }


                                    ///////////////
                                }

                            }  //  ÒÔÉÏÎªmedia»Ø¸´¶Ë¡£

                        }
                        break;
                        case CONST_imCommType_transferFileReq: {
                            if (pContent.uiType == CONST_imCommType_transferFileReplyReq) {
                                qnmCommProc_mis.TRANSFER_FILE_REPLYREQ pReply = (qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContent;
                                if (pReply.tStartTime_org == pMsgElem.tStartTime
                                        && pReply.uiTranNo_org == pMsgElem.uiTranNo) {
                                    iErr = 1;
                                    return iErr;
                                }
                            }
                        }
                        break;
                        default:
                            break;

                    }

                    switch (pContent.uiType) {

                        case CONST_imCommType_transferFileDataReq:

                            if (pContentElem.uiType == CONST_imCommType_transferFileReq
                                    && ((qnmCommProc_mis.TRANSFER_FILEDATA_REQ) pContent).tStartTime_org == pMsgElem.tStartTime
                                    && ((qnmCommProc_mis.TRANSFER_FILEDATA_REQ) pContent).uiTranNo_org == pMsgElem.uiTranNo) {
                                //
                                taskAv.TASK_transferFile task = (taskAv.TASK_transferFile) taskElem;
                                //
                                qnmCommProc_mis.TRANSFER_FILE_REQ pFileReq = (qnmCommProc_mis.TRANSFER_FILE_REQ) pContentElem;
                                qnmCommProc_mis.TRANSFER_FILEDATA_REQ pFileDataReq = (qnmCommProc_mis.TRANSFER_FILEDATA_REQ) pContent;
                                long ui64offset = 0;
                                int nRead = 0;

                                if (null == p1.pFileDataResp_o) {
                                    return iErr;
                                }

                                qnmCommProc_mis.TRANSFER_FILEDATA_RESP pFileDataResp_o =
                                        p1.pFileDataResp_o.transferFileDataResp;

                                if (null == p1.pFileDataResp_o || 0 == pFileReq.ui64FileLen || 0 == pFileDataReq.uiSizePerSnd || bufSize_transferFileDataResp < pFileDataReq.uiSizePerSnd) {

                                    Log.d("tmpHandler_findOrgMsgAndProc_is failed: sizeof(  buf  )  <  uiSizePerSnd");
                                    return iErr;
                                }

                                ui64offset = ((long) pFileDataReq.uiDataBlockIndex) * pFileDataReq.uiSizePerSnd;
                                if (ui64offset >= pFileReq.ui64FileLen) {
                                    Log.e("findOrgMsg error: fileDataReq, blockIndex [" + pFileDataReq.uiDataBlockIndex + "] * sizePerSnd [" + pFileDataReq.uiSizePerSnd + "] > fileLen [" + pFileReq.ui64FileLen + "]");
                                    return iErr;
                                }

                                long dwCurTickCnt;
                                //
                                dwCurTickCnt = myGetTickCount(null);
                                if (dwCurTickCnt > pFileReq.dwLastReportTickCnt + 3000) {
                                    pFileReq.dwLastReportTickCnt = dwCurTickCnt;
                                    //
                                    float f = 0;
                                    byte ucPercent;
                                    //
                                    f = (float) pFileDataReq.uiDataBlockIndex * pFileDataReq.uiSizePerSnd;
                                    f = f / pFileReq.ui64FileLen;
                                    ucPercent = (byte) (f * 100);

                                    //  if  (  ucPercent  !=  pFileReq->ucPercent  )
                                    {   //  ±¨¸æ×´Ì¬

                                        showTaskStatus(pMisCnt, pMsgElem.addr_logicalPeer.idInfo, pMsgElem.idInfo_taskSender, pMsgElem.idInfo_taskReceiver, false, CONST_imCommType_transferFileReq, pMsgElem.iTaskId, CONST_imTaskStatus_sending, ucPercent, pFileDataReq.uiDataBlockIndex * pFileDataReq.uiSizePerSnd, null, null);

                                    }
                                }


                                Log.d("");


                                if (null == task.myTask.fp) {
                                    tmp_tfopen(pContentElem, task);
                                    if (null == task.myTask.fp) {

                                        Log.d("tmpHandler_findOrgMsgAndProc_is failed: _tfopen {%S} err", ((qnmCommProc_mis.TRANSFER_FILE_REQ) pContentElem).fullFilePath);

                                        return iErr;
                                    }
                                }

                                if (0 != guiShare.GuiShare.pf_fseeki64.call(task.myTask.fp, ui64offset, 0)) {

                                    Log.d("tmpHandler_findOrgMsgAndProc_is failed:  _fseeki64 err");
                                    return iErr;
                                }
                                nRead = (int) Math.min(pFileReq.ui64FileLen - ui64offset, pFileDataReq.uiSizePerSnd);

                                if (tmp_fread(pFileDataResp_o, nRead, task) != 0) {
                                    return iErr;
                                }
                                pFileDataResp_o.uiLen = (int) nRead;
                                pFileDataResp_o.uiDataBlockIndex = pFileDataReq.uiDataBlockIndex;

                                iErr = 1;
                                return iErr;
                            }
                            break;
                        case CONST_imCommType_transferFileEndReq:

                            if (pContentElem.uiType == CONST_imCommType_transferFileReq                    //  ·¢ËÍ·½´¦Àí
                                    && ((qnmCommProc_mis.TRANSFER_FILEEND_REQ) pContent).tStartTime_org == pMsgElem.tStartTime
                                    && ((qnmCommProc_mis.TRANSFER_FILEEND_REQ) pContent).uiTranNo_org == pMsgElem.uiTranNo) {
                                taskAv.TASK_transferFile task = (taskAv.TASK_transferFile) taskElem;
                                //
                                int iStatus = CONST_imTaskStatus_sendFinished;
                                //

                                Log.d("transferFileEnd ");

                                //
                                if (p1.pContent_o != null) {
                                    qnmCommProc_mis.IM_CONTENTU pContent_o = p1.pContent_o.imContentU;

                                    if (pContent_o.uiType == CONST_imCommType_transferFileEndResp) {
                                        qnmCommProc_mis.TRANSFER_FILEEND_RESP pFileEndResp_o = (qnmCommProc_mis.TRANSFER_FILEEND_RESP) pContent_o;

                                        if (!((qnmCommProc_mis.TRANSFER_FILE_REQ) pContentElem).bGrpTask) {
                                            //  2015/09/08
                                            if (pFileEndResp_o.tmpInternal.bChkToRemove_i) {
                                                task.myTask.bNeedRemoved = true;
                                                //
                                                pFileEndResp_o.tmpInternal.bNeedRemoved_o = true;
                                            }
                                            //
                                            Object pDb = null;
                                            qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                                            {
                                                if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys))
                                                    return iErr;
                                                pDb = db.m_pDbMem.pDb;
                                                if (0 != myDb.QM_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, pMsgElem.iTaskId))
                                                    return iErr;
                                            }
                                            //
                                            //fixed (char* fileName = ((qnmCommProc_mis.TRANSFER_FILE_REQ)pContentElem).fullFilePath)
                                            String fileName = tmp_get_fullFilePath(pContentElem);
                                            {
                                                showTaskStatus((Object) pMisCnt, pMsgElem.addr_logicalPeer.idInfo, pMsgElem.idInfo_taskSender, pMsgElem.idInfo_taskReceiver, false, CONST_imCommType_transferFileReq, pMsgElem.iTaskId, iStatus, (byte) 100, ((qnmCommProc_mis.TRANSFER_FILE_REQ) pContentElem).ui64FileLen, fileName, fileName);
                                            }
                                        } else {
                                            String fileName = tmp_get_fullFilePath(pContentElem);
                                            {

                                                showTaskStatus((Object) pMisCnt, pMsgElem.addr_logicalPeer.idInfo, pMsgElem.idInfo_taskSender, pMsgInput.addr_logicalPeer.idInfo, true, CONST_imCommType_transferFileReq, pMsgElem.iTaskId, iStatus, (byte) 100, ((qnmCommProc_mis.TRANSFER_FILE_REQ) pContentElem).ui64FileLen, fileName, fileName);
                                            }
                                        }
                                    }
                                }
                                //
                                iErr = 1;
                                return iErr;
                            }
                            break;

                        case CONST_imCommType_taskProcReq:

                            if (pContentElem.uiType == CONST_imCommType_transferFileReplyReq       //  ½ÓÊÕ·½´¦Àí
                                    && ((qnmCommProc_mis.TASK_PROC_REQ) pContent).tStartTime_org == ((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContentElem).tStartTime_org
                                    && ((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org == ((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContentElem).uiTranNo_org) {
                                int iStatus = CONST_imTaskStatus_canceledBySender;
                                //

                                Log.d("transferFileProcReq ");

                                //
                                if (p1.pContent_o != null) {
                                    qnmCommProc_mis.IM_CONTENTU pContent_o = p1.pContent_o.imContentU;

                                    if (pContent_o.uiType == CONST_imCommType_taskProcResp) {
                                        qnmCommProc_mis.TASK_PROC_RESP pFileProcResp = (qnmCommProc_mis.TASK_PROC_RESP) pContent_o;

                                        pFileProcResp.bNeedRemoved = true;

                                        //
                                        Object pDb = null;
                                        qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                                        {
                                            if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys))
                                                return iErr;
                                            pDb = db.m_pDbMem.pDb;
                                            if (0 != g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, pMsgElem.iTaskId))
                                                return iErr;
                                        }
                                    }
                                }
                                //
                                iErr = 1;
                                return iErr;
                            }
                            if (pContentElem.uiType == CONST_imCommType_transferFileReq) {       //  ·¢ËÍ·½´¦Àí
                                if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).tStartTime_org == pMsgElem.tStartTime
                                        && ((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org == pMsgElem.uiTranNo) {
                                    int iStatus = CONST_imTaskStatus_canceledByReceiver;
                                    Log.e("##4148" + iStatus);

                                    //

                                    Log.d("tmpHandler_findOrgMsgAndProc_is: msg - procReq found");

                                    //
                                    if (p1.pContent_o != null) {
                                        qnmCommProc_mis.IM_CONTENTU pContent_o = p1.pContent_o.imContentU;

                                        if (pContent_o.uiType == CONST_imCommType_taskProcResp) {
                                            qnmCommProc_mis.TASK_PROC_RESP pFileProcResp = (qnmCommProc_mis.TASK_PROC_RESP) pContent_o;

                                            if (!((qnmCommProc_mis.TRANSFER_FILE_REQ) pContentElem).bGrpTask) {
                                                pFileProcResp.bNeedRemoved = true;

                                                //
                                                Object pDb = null;
                                                qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                                                {
                                                    ;
                                                    if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys))
                                                        return iErr;
                                                    pDb = db.m_pDbMem.pDb;
                                                    if (0 != g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, pMsgElem.iTaskId))
                                                        return iErr;
                                                }
                                                //
                                                //fixed (char* fileName = ((qnmCommProc_mis.TRANSFER_FILE_REQ)pContentElem).fileName)
                                                String fileName = tmp_get_fileName(pContentElem);
                                                {
                                                    //fixed (char* fullFilePath = ((qnmCommProc_mis.TRANSFER_FILE_REQ)pContentElem).fullFilePath)
//                                                    String fullFilePath = tmp_get_fullFilePath(pContentElem);
//                                                    {
//                                                        showTaskStatus(pMisCnt, pMsgElem.addr_logicalPeer, pMsgElem.idInfo_taskSender, null, false,CONST_imCommType_transferFileReq, pMsgElem.iTaskId, iStatus, 0, 0, fileName, fullFilePath);
//                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //
                                    iErr = 1;
                                    return iErr;
                                }
                            }
                            //
                            //  transferAvInfo,
                            //
                            if (pContentElem.uiType == CONST_imCommType_transferAvReplyInfo) {       //  ½ÓÊÕ·½´¦Àí
                                taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                                taskAv.PROC_TASK_AV pTaskElem = task.myTask;// get_transferAvReplyInfo_pTask(&pContentElem->transferAvReplyInfo, ""));
                                //if (!pTaskElem) goto errLabel;
                                //
                                if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).tStartTime_org == ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem).tStartTime_org
                                        && ((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org == ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem).uiTranNo_org) {
                                    int iStatus = CONST_imTaskStatus_canceledBySender;
                                    //

                                    Log.d("taskProcReq ");

                                    //
                                    if (p1.pContent_o != null) {
                                        qnmCommProc_mis.IM_CONTENTU pContent_o = p1.pContent_o.imContentU;

                                        if (pContent_o.uiType == CONST_imCommType_taskProcResp) {
                                            qnmCommProc_mis.TASK_PROC_RESP pFileProcResp = (qnmCommProc_mis.TASK_PROC_RESP) pContent_o;

                                            pFileProcResp.bNeedRemoved = true;

                                            //
                                            Object pDb = null;
                                            qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                                            {
                                                ;

                                                if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys))
                                                    return iErr;
                                                pDb = db.m_pDbMem.pDb;
                                                if (0 != g_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, pMsgElem.iTaskId))
                                                    return iErr;
                                            }
                                            //
//                                            showTaskStatus(pMisCnt, pMsgElem.addr_logicalPeer.idInfo, pMsgElem.idInfo_taskSender, null, false, CONST_imCommType_transferAvInfo, pMsgElem.iTaskId, iStatus, 0, 0, null, null);
                                        }
                                    }
                                    //
                                    iErr = 1;
                                    return iErr;
                                }

                                //  2014/08/26
                                if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_recv_applyForKeyFrame) {

                                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, (""), pProcInfo->who_showInfo, 0, ""), ""), "recv applyforKeyFrame from %I64u."), pMsgInput->data.route.idInfo_from.ui64Id);

                                    //
                                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).vStream.uiTranNo_openAvDev == ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem).tranInfo.video.uiTranNo_openAvDev) {
                                        Log.d("findOrgMsgAndProcess_mis: get a applyForKeyFrame msg");
                                        tmpiRet = procTaskProcReq_applyForKeyFrame(pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgElem, ((qnmCommProc_mis.TASK_PROC_REQ) pContent), pContentElem, pTaskElem);
                                        if (0 != tmpiRet) {
                                            iErr = tmpiRet;
                                            return iErr;
                                        }

                                        iErr = 1;
                                        return iErr;
                                    }
                                }

                            }
                            if (pContentElem.uiType == CONST_imCommType_transferAvInfo) {       //  ·¢ËÍ·½´¦Àí
                                //
                                if (!pTaskInfo.var.m_bReplyTask) {
                                    taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                                    taskAv.PROC_TASK_AV pTaskElem = task.myTask;// get_transferAvInfo_pTask(&pContentElem->transferAvInfo, ""));
                                    //if (!pTaskElem) goto errLabel;
                                    //
                                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).tStartTime_org == pMsgElem.tStartTime
                                            && ((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org == pMsgElem.uiTranNo) {
                                        //

                                        Log.d("tmpHandler_findOrgMsgAndProc_is: msg - taskProcReq. task found");


                                        //
                                        if (p1.pContent_o != null) {
                                            qnmCommProc_mis.IM_CONTENTU pContent_o = p1.pContent_o.imContentU;

                                            if (pContent_o.uiType == CONST_imCommType_taskProcResp) {
                                                qnmCommProc_mis.TASK_PROC_RESP pFileProcResp = (qnmCommProc_mis.TASK_PROC_RESP) pContent_o;

                                                pFileProcResp.idInfo_logicalPeer.ui64Id = pMsgElem.addr_logicalPeer.idInfo.ui64Id;

                                                if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_recv_applyForChkTaskAlive) {
                                                    //
                                                    Log.e("findOrgMsgAndProc: taskProcReq. usOp recv_applyForChkTaskAlive, task found");
                                                    //
                                                    iErr = 1;
                                                    return iErr;
                                                }

                                                int iStatus = CONST_imTaskStatus_canceledByReceiver;
                                                Log.e("##4288");
                                                //  2014/12/03
                                                boolean bCanCancel = true;
                                                if (((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).bGrpTask) {
                                                    bCanCancel = false;
                                                }
                                                if (pTaskElem.doProxy.bExists_avProxy) {
                                                    bCanCancel = false;
                                                }

                                                //  2017/09/11
                                                if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_recv_cancel) {
                                                    tmp_procTask_recv_cancel(pProcInfo, pTaskElem, pMsgInput, ((qnmCommProc_mis.TASK_PROC_REQ) pContent));
                                                }

                                                //
                                                //if  (  !pContentElem->transferAvInfo.bGrpTask  )
                                                if (bCanCancel) {
                                                    pFileProcResp.bNeedRemoved = true;

                                                    //  2014/12/03

                                                    //
                                                    Object pDb = null;
                                                    try (qnmDb.CQnmDb db = new qnmDb.CQnmDb()) {
                                                        if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys))
                                                            return iErr;
                                                        pDb = db.m_pDbMem.pDb;
                                                        if (0 != myDb.QM_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, pMsgElem.iTaskId))
                                                            return iErr;
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    //
                                                    showTaskStatus((Object) pMisCnt, pMsgElem.addr_logicalPeer.idInfo, pMsgElem.idInfo_taskSender, (qyDefs.QY_MESSENGER_ID) null, false, pContentElem.uiType, pMsgElem.iTaskId, iStatus, (byte) 0, 0, null, null);
                                                }
                                            }
                                        }
                                        //
                                        iErr = 1;
                                        return iErr;
                                    }
                                    //  2014/08/26
                                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_recv_applyForKeyFrame) {

                                        boolean tmp_bFound = false;
                                        //
                                        if (pTaskElem.ucbVideoConferenceStarter) {
                                            //  2014/08/28. 暂时采用直接模式
                                            if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).vStream.uiTranNo_openAvDev == pTaskElem.mem0_from.video.tv_recvd.uiTranNo_openAvDev) {
                                                tmp_bFound = true;
                                            }
                                            //  2014/12/06
                                            if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).vStream.uiTranNo_openAvDev == ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).confMosaicTranInfo_video.video.uiTranNo_openAvDev
                                                    || ((qnmCommProc_mis.TASK_PROC_REQ) pContent).vStream.uiTranNo_openAvDev == ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).confMosaicTranInfo_resource.video.uiTranNo_openAvDev) {
                                                tmp_bFound = true;

                                            }
                                        } else {
                                            if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).vStream.uiTranNo_openAvDev == ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).ass.tranInfo.video.uiTranNo_openAvDev) {
                                                tmp_bFound = true;
                                            }
                                        }
                                        //
                                        if (tmp_bFound) {


                                            Log.d("findOrgMsgAndProcess_mis: get a applyForKeyFrame msg");


                                            //
                                            tmpiRet = procTaskProcReq_applyForKeyFrame(pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgElem, ((qnmCommProc_mis.TASK_PROC_REQ) pContent), pContentElem, pTaskElem);
                                            if (tmpiRet != 0) {
                                                iErr = tmpiRet;
                                                return iErr;
                                            }

                                            iErr = 1;
                                            return iErr;
                                        }
                                        //
                                    }
                                    //  2014/10/09
                                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_stopSending
                                            || ((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_requestToSend) {
                                        if (0 != ((qnmCommProc_mis.TASK_PROC_REQ) pContent).viewDynBmp.uiObjType) {
                                            if (((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).ass.resObj.uiObjType == ((qnmCommProc_mis.TASK_PROC_REQ) pContent).viewDynBmp.uiObjType
                                                    && ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).ass.resObj.usIndex_obj == ((qnmCommProc_mis.TASK_PROC_REQ) pContent).viewDynBmp.usIndex_obj) {

                                                Log.e("findOrgMsgAndProc: get taskProcReq msg");


                                                pMsgInput.iTaskId = pMsgElem.iTaskId;
                                                pMsgInput.addr_logicalPeer.idInfo.ui64Id = pMsgElem.addr_logicalPeer.idInfo.ui64Id;

                                                //
                                                //pContent->taskProcReq.hWnd_task = pTaskElem.hWnd_task;

                                                //
                                                tmp_qPost_msgInput_taskProcReq(pMsgInput);
                                                guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                                iErr = 1;
                                                return iErr;
                                            }
                                        }
                                    }
                                    //  2009/10/18
                                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_recv_applyForOrgReq) {

                                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, (""), pProcInfo->who_showInfo, 0, ""), ""), "recv applyforOrgReq from %I64u. _tn %d"), pMsgInput->data.route.idInfo_from.ui64Id, pMsgInput->uiTranNo);
                                        //task.myTask.ucbVideoConference

                                        boolean tmp_bMatched = false;
                                        if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiContentType_org == CONST_imCommType_transferAudioData) {
                                            if (task.myTask.bVideoConference()) {
                                                if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org == ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).ass.tranInfo.audio.uiTranNo_openAvDev) {
                                                    tmp_bMatched = true;
                                                }
                                            } else {
                                                if (bInActiveMems(pProcInfo, pMisCnt.idInfo, ((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org, 0, task)) {
                                                    tmp_bMatched = true;
                                                }
                                            }
                                            if (tmp_bMatched) {

                                                Log.d("findOrgMsgAndProcess_mis: get a applyForOrgReq msg");

                                                pMsgInput.iTaskId = pMsgElem.iTaskId;
                                                //  2014/11/18
                                                if (((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).idInfo_logicalPeer.ui64Id != 0) {
                                                    pMsgInput.addr_logicalPeer.idInfo.ui64Id = ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).idInfo_logicalPeer.ui64Id;
                                                } else {
                                                    pMsgInput.addr_logicalPeer.idInfo.ui64Id = pMsgElem.addr_logicalPeer.idInfo.ui64Id;
                                                }

                                                //
                                                //
                                                Log.e("post req applyForOrgReq for audioData to gui");

                                                //
                                                tmp_qPost_msgInput_taskProcReq(pMsgInput);
                                                guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                                iErr = 1;
                                                return iErr;
                                            }
                                        }
                                        //  2010/12/27
                                        if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiContentType_org == CONST_imCommType_transferVideoData) {
                                            if (task.myTask.bVideoConference()) {
                                                if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org == ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).ass.tranInfo.video.uiTranNo_openAvDev) {
                                                    tmp_bMatched = true;
                                                }
                                            } else {
                                                if (bInActiveMems(pProcInfo, pMisCnt.idInfo, 0, ((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org, task)) {
                                                    tmp_bMatched = true;
                                                }
                                            }
                                            if (tmp_bMatched) {

                                                Log.d("findOrgMsgAndProcess_mis: get a applyForOrgReq msg. taskFound");

                                                //
                                                if (pMsgElem.uiTaskType == CONST_imTaskType_shareDynBmp) {
                                                    if (pTaskElem.iIndex_sharedObj != 0) {
                                                        qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, pTaskElem.iIndex_sharedObj);
                                                        if (pSharedObj != null) {
                                                            removeFromMsgrs_sendLocalAv.qyFuncs.removeFromMsgrs_sendLocalAv(pProcInfo, pMisCnt, pMsgInput.data.route.idInfo_from, pTaskInfo.var.curRoute_sendLocalAv);
                                                            iErr = 1;
                                                            return iErr;
                                                        }
                                                    }
                                                }

                                                //
                                                pMsgInput.iTaskId = pMsgElem.iTaskId;
                                                //  2014/11/18
                                                if (((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).idInfo_logicalPeer.ui64Id != 0) {
                                                    pMsgInput.addr_logicalPeer.idInfo.ui64Id = ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).idInfo_logicalPeer.ui64Id;
                                                } else {
                                                    pMsgInput.addr_logicalPeer.idInfo.ui64Id = pMsgElem.addr_logicalPeer.idInfo.ui64Id;
                                                }

                                                str=  "post req applyForOrgReq for videoData to gui";
                                                Log.i(str);
                                                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                                //
                                                tmp_qPost_msgInput_taskProcReq(pMsgInput);
                                                guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                                iErr = 1;
                                                return iErr;
                                            }
                                        }
                                    }
                                } else {
                                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp == CONST_imOp_recv_applyForOrgReq) {
                                        qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, 0);
                                        if (null != pSharedObj) {
                                            int ii = 0;

                                            qnmCommProc_mis.IM_CONTENTU tmp_pContent1 = pContent;


                                            boolean tmp_bMatched = false;

                                            if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiContentType_org == CONST_imCommType_transferAudioData) {

                                                ii = 0;
                                            } else if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiContentType_org == CONST_imCommType_transferVideoData) {
                                                ii = 1;
                                                for (ii = 0; ii < pSharedObj.usrs.length; ii++) {
                                                    if (((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org == pSharedObj.usrs[ii].uiTranNo_openAvDev_v) {
                                                        tmp_bMatched = true;
                                                        break;
                                                    }
                                                }

                                            } else {
                                                Log.e("taskProcReq.uiContentType_org err");
                                            }

                                            //
                                            if (tmp_bMatched) {
                                                //
                                                pMsgInput.iTaskId = pMsgElem.iTaskId;
                                                //  2014/11/18
                                                if (((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).idInfo_logicalPeer.ui64Id != 0) {
                                                    pMsgInput.addr_logicalPeer.idInfo.ui64Id = ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem).idInfo_logicalPeer.ui64Id;
                                                } else {
                                                    pMsgInput.addr_logicalPeer.idInfo.ui64Id = pMsgElem.addr_logicalPeer.idInfo.ui64Id;
                                                }

                                                //
                                                Log.i("post req applyForOrgReq for videoData to gui");

                                                //
                                                tmp_qPost_msgInput_taskProcReq(pMsgInput);
                                                guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                                iErr = 1;
                                                return iErr;


                                            }

                                        }

                                    }

                                }
                            }

                            break;
                        default:
                            break;
                    }
                }
                //
            }

            iErr = 0;
            errLabel:
            return iErr;

        }


        public static int findOrgMsg_task_searchTask_av(ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU pMsg_toBeSearchedParam, qyMcMainProc.PARAM_findOrgMsg p1Param, taskCommon.TASK_common taskElemParam) {
            int iErr = -1;


            String str;

            qnmCommProc_mis.MIS_MSGU pMsg_toBeSearched = pMsg_toBeSearchedParam;
            //IntPtr p1 = (IntPtr)p1Param;
            qyMcMainProc.PARAM_findOrgMsg p1 = p1Param;
            //  p1
            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            int tmpiRet = 0;
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;
            qmCommFunc_mis.MIS_CNT pMisCntElem = pProcInfo.pMisCnt;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;


            //
            taskCommon.TASK_common taskElem = (taskCommon.TASK_common) taskElemParam;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskElem.m_index_taskInfo, "findOrgMsg");
            if (pTaskInfo == null) return iErr;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return iErr;

            //
            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                Log.e("for test, tmpHandler_findOrgMsgAndProc_is return 0");
                return 0;
            }


            //fixed (MIS_MSGU* pMsgElemParam =pTaskData.msgU)
            qnmCommProc_mis.MIS_MSGU pMsgElemParam = pTaskData.msgU;

            qnmCommProc_mis.MIS_MSG_TASK pMsgElem = (qnmCommProc_mis.MIS_MSG_TASK) pMsgElemParam;
            qnmCommProc_mis.IM_CONTENTU pContentElem = pMsgElem.data.buf;


            qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pMsg_toBeSearched;
            qnmCommProc_mis.IM_CONTENTU pContent = pMsgTask.data.buf;


            if (!pTaskInfo.var.m_bReplyTask) {
                taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                qnmCommProc_mis.TRANSFER_AV_INFO pTransferAvInfoElem = ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem);
                taskAv.PROC_TASK_AV pTaskElem =
                        task.myTask;// get_transferAvInfo_pTask(pTransferAvInfoElem, ""));
                //if (!pTaskElem) goto errLabel;
                int index = 0;

                if (pContent.uiType == CONST_imCommType_transferAvInfo) {                               //
                    qnmCommProc_mis.TRANSFER_AV_INFO pTransferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                    //
                    if (pMsgElem.tStartTime == pMsgTask.tStartTime
                            && pMsgElem.uiTranNo == pMsgTask.uiTranNo) {
                        boolean canGet = false;
                        //
                        if (pTaskElem.ucbVideoConferenceStarter) canGet = true;
                        else if (bApplyForAvProxy(pMisCntElem, pTransferAvInfoElem))
                            canGet = true;
                        else {

                            //  Log.e(    "findOrgMsg:
                            //  让talk来校验

                            canGet = true;
                        }
                        //

                        if (!canGet) {
                            Log.e("findOrgMsg: can't get req again, canGot is false");
                        }

                        //
                        if (canGet) {
                            //  note: 2014/11/21. 这里不再直接覆盖输入内容了，而是要求一定要用param_findOrgMsg来输出. 不知道对原来的有没有影响
                            //  memcpy(  &pContent->transferAvInfo.tranInfo,  &pTransferAvInfoElem->tranInfo,  sizeof(  pContent->transferAvInfo.tranInfo  )  );
                            //

                            Log.d("findOrgMsgAndProc_mis: get req again");


                            //
                            //PARAM_findOrgMsg pParam = (PARAM_findOrgMsg)p1;
                            if (p1Param != null) {
                                if (p1Param.pMsgTask_o != null) {
                                    p1Param.pMsgTask_o.msgU = pMsgElem;
                                }
                            }
                        }

                        iErr = 1;
                        return iErr;
                    }

                    if (bApplyForAvProxy(pMisCntElem, pTransferAvInfoElem)
                            && pTransferAvInfo.proxiedTranInfo.idInfo.ui64Id != 0
                            && pTransferAvInfo.proxiedTranInfo.uiObjType != 0) {
                        if (pTransferAvInfo.proxiedTranInfo.idInfo.ui64Id == pMisCntElem.idInfo.ui64Id) {
                            if (pTransferAvInfo.proxiedTranInfo.ti.audio.uiTranNo_openAvDev != 0 || pTransferAvInfo.proxiedTranInfo.ti.video.uiTranNo_openAvDev != 0) {
                                boolean bNotMatched = false;
                                if (pTransferAvInfo.proxiedTranInfo.ti.audio.uiTranNo_openAvDev != 0) {
                                    if (pTransferAvInfo.proxiedTranInfo.ti.audio.uiTranNo_openAvDev != pTransferAvInfoElem.ass.tranInfo.audio.uiTranNo_openAvDev)
                                        bNotMatched = true;
                                }
                                if (pTransferAvInfo.proxiedTranInfo.ti.video.uiTranNo_openAvDev != 0) {
                                    if (pTransferAvInfo.proxiedTranInfo.ti.video.uiTranNo_openAvDev != pTransferAvInfoElem.ass.tranInfo.video.uiTranNo_openAvDev)
                                        bNotMatched = true;
                                }
                                if (!bNotMatched) {

                                    Log.e("proxy reply returned");

                                    com.qycx.base.qyMcMainProc.PARAM_findOrgMsg pParam = (com.qycx.base.qyMcMainProc.PARAM_findOrgMsg) p1;
                                    if (pParam != null) {
                                        if (pParam.bNeedUpdate) {
                                            pTaskElem.applyForProxy.bRecvd_proxyReply = true;
                                            //
                                            pTaskElem.applyForProxy.proxyReply.proxy.uiTranNo_openAvDev_a = pTransferAvInfo.ass.tranInfo.audio.uiTranNo_openAvDev;
                                            pTaskElem.applyForProxy.proxyReply.proxy.uiTranNo_openAvDev_v = pTransferAvInfo.ass.tranInfo.video.uiTranNo_openAvDev;
                                        }
                                    }
                                    iErr = 1;
                                    return iErr;
                                }
                            }
                        }
                    }

                }

                if (pContent.uiType == CONST_imCommType_transferAvReplyInfo) {
                    if (pMsgElem.tStartTime == ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent).tStartTime_org
                            && pMsgElem.uiTranNo == ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent).uiTranNo_org) {
                        //OutputDebugString("tmpHandler_findOrgMsgAndProc_is . msg - reply is matched\n"));            //

                        old_procTaskAvReply(pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pTransferAvInfoElem.ass.tranInfo, pTaskElem, pMsgTask);

                        //
                        iErr = 1;
                        return iErr;
                    }
                }

                //
                if (pContent.uiType == CONST_imCommType_transferVideoData) {                           //
                    //
                    tmpiRet = tmpHandler_findOrgMsgAndProc_procTaskVideoData(ctxCaller, pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgTask, (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent, pContentElem, pTaskElem);
                    if (0 != tmpiRet) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                }
                if (pContent.uiType == CONST_imCommType_transferAudioData) {
                    //
                    tmpiRet = tmpHandler_findOrgMsgAndProc_procTaskAudioData(ctxCaller, pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgTask, (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent, pContentElem, pTaskElem);
                    if (0 != tmpiRet) {
                        iErr = tmpiRet;
                        return iErr;
                    }
                }
                if (pContent.uiType == CONST_imCommType_remoteAssistReq) {
                }

                if (pContent.uiType == CONST_imCommType_taskProcReq) {   //  file·
                    if (pMsgElem.iTaskId != 0 && pMsgElem.iTaskId == pMsgTask.iTaskId) {

                        Log.d("tmpHandler_findOrgMsgAndProc_is: msg - procReq.iTaskId [" + pMsgTask.iTaskId + "] is matched");

                        //
                        iErr = 1;
                        return iErr;
                    }

                    if (pMsgElem.tStartTime == ((qnmCommProc_mis.TASK_PROC_REQ) pContent).tStartTime_org
                            && pMsgElem.uiTranNo == ((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org) {

                        Log.d("tmpHandler_findOrgMsgAndProc_is: msg - procReq is matched");

                        //
                        iErr = 1;
                        return iErr;
                    }
                }
            } else {

                //  ÒÔÉÏÎªmedia·¢Æð¶Ë

                //if (pContentElem->uiType == CONST_imCommType_transferAvReplyInfo)
                {                           //  media»Ø¸´¶Ë
                    taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskElem;
                    //TRANSFER_AV_replyInfo* pTransferAvReplyInfoElem =pContentElem->transferAvReplyInfo;
                    qnmCommProc_mis.TRANSFER_AV_INFO pTransferAvInfoElem = ((qnmCommProc_mis.TRANSFER_AV_INFO) pContentElem);

                    taskAv.PROC_TASK_AV pTaskElem = task.myTask;// get_transferAvReplyInfo_pTask(pTransferAvReplyInfoElem, ""));
                    //if (!pTaskElem) goto errLabel;
                    int index = 0;

                    //  ÒòÎªaudio¶ÔÊµÊ±ÐÔÒªÇó×î¸ß£¬ËùÒÔ·ÅÔÚµÚÒ»Î»±È½Ï
                    if (pContent.uiType == CONST_imCommType_transferAudioData) {
                        //
                        tmpiRet = tmpHandler_findOrgMsgAndProc_procTaskAudioData(ctxCaller, pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgTask, (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent, pContentElem, pTaskElem);
                        if (0 != tmpiRet) {
                            iErr = tmpiRet;
                            return iErr;
                        }

                    }
                    if (pContent.uiType == CONST_imCommType_transferVideoData) {                           //  ¶Ô·½µÄÊý¾Ý
                        //
                        tmpiRet = tmpHandler_findOrgMsgAndProc_procTaskVideoData(ctxCaller, pProcInfo, pMsgElem.addr_logicalPeer, pMsgElem.iTaskId, pMsgTask, (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent, pContentElem, pTaskElem);
                        if (0 != tmpiRet) {
                            iErr = tmpiRet;
                            return iErr;
                        }

                    }

                    if (pContent.uiType == CONST_imCommType_transferAvInfo) {       //  ÖØ·¢Ê±ÕâÀïÒªÐ£ÑéµÄ
                        if (pMsgElem.data.route.idInfo_to.ui64Id == pMsgTask.data.route.idInfo_from.ui64Id
                                && ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem).tStartTime_org == pMsgTask.tStartTime
                                && ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem).uiTranNo_org == pMsgTask.uiTranNo) {
                            //

                            Log.d("tmpHandler_findOrgMsgAndProc_is: msg - req is matched");

                            //
                            iErr = 1;
                            return iErr;
                        }
                    }
                    if (pContent.uiType == CONST_imCommType_transferAvReplyInfo) {                           //  ´¦Àí
                        if (pMsgElem.tStartTime == ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent).tStartTime_org
                                && pMsgElem.uiTranNo == ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent).uiTranNo_org)
                        //if (pContentElem->transferAvReplyInfo.tStartTime_org == pContent->transferAvReplyInfo.tStartTime_org
                        //	&& pContentElem->transferAvReplyInfo.uiTranNo_org == pContent->transferAvReplyInfo.uiTranNo_org)
                        {
                            //

                            Log.d("tmpHandler_findOrgMsgAndProc_is . msg - avReplyInfo matched.");

                            //
                            iErr = 1;
                            return iErr;
                        }
                    }
                    if (pContent.uiType == CONST_imCommType_taskProcReq) {   //
                        if (((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem).tStartTime_org == ((qnmCommProc_mis.TASK_PROC_REQ) pContent).tStartTime_org
                                && ((qnmCommProc_mis.TRANSFER_AV_replyInfo) pContentElem).uiTranNo_org == ((qnmCommProc_mis.TASK_PROC_REQ) pContent).uiTranNo_org) {
                            //

                            Log.d("tmpHandler_findOrgMsgAndProc_is: msg - procReq is matched");

                            //
                            iErr = 1;
                            return iErr;
                        }
                    }
                }
            }

            iErr = 0;
            errLabel:
            return iErr;

        }


        public static mcSharedQ.PF_bEnumTask b_tmpHandler_findOrgMsgAndProc_is = (ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU pMsg_toBeSearchedParam, qyMcMainProc.PARAM_findOrgMsg p1Param, taskCommon.TASK_common taskElemParam) ->
        {
            int tmpiRet = tmpHandler_findOrgMsgAndProc.call(ctxCaller, (qnmCommProc_mis.MIS_MSGU) pMsg_toBeSearchedParam, (qyMcMainProc.PARAM_findOrgMsg) p1Param, (taskCommon.TASK_common) taskElemParam);
            return (tmpiRet != 0);
        };
    }
}

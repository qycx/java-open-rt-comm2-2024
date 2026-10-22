


package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.Consts.CONST_channelType_robot;
import static com.qycx.base.Consts.CONST_channelType_talking;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_encA;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_encV;
import static com.qycx.base.ctxCaller.Consts.CONST_ctxCallerType_thread;
import static com.qycx.base.findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc;
import static com.qycx.base.genericQueue.qyFuncs.qGetMsg;
import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.getNextVPkt.qyFuncs.detachAPkt;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_confKey;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_confLayout;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_confNvrInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_simpleResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_statusConfLayoutParam;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskInteractionReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskProcReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskProcResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAudioData;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAudioDataResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvReplyInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileDataReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileDataResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileEndReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileEndResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReplyReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferFileReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferGpsDataResp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferVideoData;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferVideoDataResp;
import static com.qycx.base.isFunc.qyFuncs.CONST_channelTypeTable;
import static com.qycx.base.isProtocolFunc.qyFuncs.qyntohRouteTalkData;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc;
import static com.qycx.base.qdcCommProc_open.Consts.CONST_qoiType_decA;
import static com.qycx.base.qdcCommProc_open.Consts.CONST_qoiType_encA;
import static com.qycx.base.qisChannel.qyFuncs.startChannel;
import static com.qycx.base.qisChannel.qyFuncs.waitForChannel;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_ffmpeg;
import static com.qycx.base.qmcFunc_isCli.qyFuncs.msg2TaskRcd;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;
import static com.qycx.base.qmcTransform.qyFuncs.exitTransforms;
import static com.qycx.base.qmcTransform.qyFuncs.initTransforms;
import static com.qycx.base.qmcTransform_start.qyFuncs.startTransformThreads;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_audioCompressors_acm;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_audioCompressors_dmo;
import static com.qycx.base.qmcVideoCapture.qyFuncs.qmcApplyForRemovingInvalidTasks;
import static com.qycx.base.qmcVideoFunc.qyFuncs.askSharedObjToStop;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendMedia;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendRobotTask;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendRobotTaskData;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendRobotTaskReply;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_accept;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_send_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_applyForChkChannels_qmc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputTask;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_req;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_transferAvInfo_subtype_task;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_activeMems_from;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_mems_taskAv;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.MACRO_makeImSimpleResp;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.MACRO_msgRouteTalkData;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_getMsgContent;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_setMyStep;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_resp;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_routeTalkData;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_ok;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_servBusy;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_unknown;
import static com.qycx.base.qyComm.qyFuncs.isRcOk;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgResp;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgRouteTalkData;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgTalkData;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_qwmComm;
import static com.qycx.base.qyCommProc.qyFuncs.CONST_qyCmdTable;
import static com.qycx.base.qyCommProc.qyFuncs.CONST_qyRcTable;
import static com.qycx.base.qyCommProc.qyFuncs.waitForThread_io;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_dec;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_recvFile;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_transferAvInfo;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_viewDynBmp;
import static com.qycx.base.qyDefs.qyFuncs.MACRO_makeCommonParam3;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_applyToRecv;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_waitToRecv;
import static com.qycx.base.qyDefs_open.qyFuncs.MACRO_safeFree;
import static com.qycx.base.qyDmItemCommProc.qyFuncs.qyGetDesByType1;
import static com.qycx.base.qyMcMainCusModule.qyFuncs.M_getCompressModule;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.getIdInfo_logicalPeer;
import static com.qycx.base.qyMcMainDaemon_isCli_mgr.Consts.cntof_tBuf128;
import static com.qycx.base.qyMcMainDaemon_isCli_mgr.Consts.cntof_tHintBuf;
import static com.qycx.base.qyMcMainProc_isCli_media.qyFuncs.procMsgInput_mc_media;
import static com.qycx.base.qyMcMainProc_isCli_robot.Consts.MIN_uiSizePerSnd_file_big;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_misMsgInited;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_msgArrive;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.qyWmComm_is.qyFuncs.CONST_misMsgTypeTable;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;
import static com.qycx.base.qyq2.qyFuncs.exitQyQ2;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;
import static com.qycx.base.qyq2.qyFuncs.initQyQ2;
import static com.qycx.base.smplFunc_a.qyFuncs.smplBitstreamReader_ReadNextFrame_func_a;
import static com.qycx.base.taskAv.qyFuncs.M_getMutexName_syncActiveMem;
import static com.qycx.base.taskAvProc.qyFuncs.adjustModuleType_decA;
import static com.qycx.base.tmpFunc.qyFuncs.byte2bool;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpProc.qyFuncs.clearShiftKeys;

import com.qycx.base.util.Log;

public class qyMcMainProc_isCli_robot {

    public static class Consts {

        public final static int MIN_uiSizePerSnd_file = (256 * 1024);


        public final static int MIN_taskTimeoutInS = 5;

        public final static int MIN_uiSizePerSnd_file_big = MIN_uiSizePerSnd_file;
        public final static int MIN_uiSizePerSnd_file_mid = 128 * 1024;
        public final static int MIN_uiSizePerSnd_file_small = 64 * 1024;


    }

    public static class qyFuncs {
        public static mcSharedQ.PF_qElemNewEx_mcTask mcTask_new = (int iAllocType, int size, mcSharedQ.QELEM_mcTask p) -> {
            return 1;
        };

        public static mcSharedQ.PF_qElemFreeEx_mcTask mcTask_free = (mcSharedQ.QELEM_mcTask p) ->
        {
            return;
        };


        public static int initDispatch_qis(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MSGR_ADDR pAddr_logicalPeer, int iTaskId, qnmCommProc_mis.AV_TRAN_INFO pTranInfo, taskAv.PROC_TASK_AV pTask) {
            int iErr = -1;


            iErr = 0;


            if (0 != iErr) {
                exitDispatch_qis(pTask);
            }

            return iErr;
        }


        public static void exitDispatch_qis(taskAv.PROC_TASK_AV pTask) {
            int i;

            return;
        }

        public static mcSharedQ.PF_qElemExit_mcTask exitMisMsg = (Object p_msg) -> {

            //Object pMsgParam = (Object)p_msg;

            taskCommon.TASK_common pTaskParam = (taskCommon.TASK_common) p_msg;
            String str;

            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return;

            //
            com.qycx.base.ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
            //mymemset((byte[])ctxCaller, 0, sizeof(CTX_caller));
            ctxCaller.type = CONST_ctxCallerType_thread;
            ctxCaller.dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            ctxCaller.bTmp = true;

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTaskParam.m_index_taskInfo, "exitMisMsg");
            if (pTaskInfo == null) return;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return;

            //
            qnmCommProc_mis.MIS_MSGU pMsg = pTaskData.msgU;
            {

                int index;
                int i;
                str="exitMisMsg enters";
                Log.d(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);

                switch (pMsg.uiType) {
                    case CONST_misMsgType_task: {
                        qnmCommProc_mis.IM_CONTENTU pContent = null;
                        {
                            qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                            pContent = M_getMsgContent(task.uiType, task.ucFlg, task.data);
                        }
                        if (null == pContent) return;

                        switch (pContent.uiType) {
                            case CONST_imCommType_transferFileReq: {
                                taskAv.TASK_transferFile task = (taskAv.TASK_transferFile) pTaskParam;
                                qnmCommProc_mis.PROC_TASK_FILE pTask = task.myTask;
                                if (pTask == null) break;
                                if (pTask.fp != null) {
                                    guiShare.GuiShare.pf_fclose.call(pTask.fp);
                                    pTask.fp = null;
                                }

                                //Log.e("exitMisMsg: transferFileReq finished");
                            }
                            break;
                            case CONST_imCommType_transferFileReplyReq:

                                break;
                            case imCommType_defs.Consts.CONST_imCommType_transferAvInfo: {
                                taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) pTaskParam;
                                taskAv.PROC_TASK_AV pTask = task.myTask;

                                //PROC_TASK_AV	*	pTask  =  get_transferAvReplyInfo_pTask(  &pContent->transferAvReplyInfo,  (  ""  )  );  //  &pContent->transferAvReplyInfo.myTask;
                                if (null == pTask) break;

                                if (pTaskData.uiType != CONST_taskDataType_conf) return;
                                qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                                qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

                                //
                                if (!pTaskInfo.var.m_bReplyTask) {
                                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                                    if (transferAvInfo.tmp_usSubtype != CONST_transferAvInfo_subtype_task) {
                                        break;
                                    }
                                    //TRANSFER_av_task pTransferAvTask = (TRANSFER_av_task)pContent;

                                    //  2008/11/20¡£°´Àí£¬Ö»ÓÐÔ¶³ÌÐ­Öú²Å¿ÉÒÔÇåÀí¡£µ«ÊÇËÆºõÒ²Ã»ÓÐÊ²Ã´Ó°Ïì¡£
                                    clearShiftKeys();

                                    //  2010/09/05
                                    qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
                                    if (null != (pSharedObj = getSharedObjByIndex(pProcInfo, pTask.iIndex_sharedObj))) {
                                    }

                                    //  2009/06/17
                                    exitDispatch_qis(pTask);

                                    //  2009/05/12
                                    exitTransforms(pProcInfo, pTask);
                                    guiShare.GuiShare.pf_freeIndex_qisTrans.call(pTask.index_trans);
                                    pTask.index_trans = -1;
                                    pTask.trans = null;

                                    //
                                    for (index = 0; index < videoConference.usCntLimit_mems_from; index++) {
                                        //  exitTaskAvFrom_video(  pProcInfo,  &pContent->transferAvInfo.myTask.pMems_from[index]  );
                                        //  exitTaskAvFrom_audio(  pProcInfo,  &pContent->transferAvInfo.myTask.pMems_from[index]  );
                                    }

                                    //  if  (  pTask.mixer.pMsgTask_toCompress  )  free(  pTask.mixer.pMsgTask_toCompress  );

                                    //  if  (  pTask.mixer.pOutputQ  )  genericQFree(  pTask.mixer.pOutputQ  );
                                    /*
                                    if (null != pTask.mixer.pOutputQ2) {
                                        exitQyQ2(pTask.mixer.pOutputQ2);
                                        //MACRO_safeFree(pTask.mixer.pOutputQ2);
                                        pTask.mixer.pOutputQ2 = null;
                                    }
                                    if  (  null!=  pTask.mixer.pOutputQBuf2) {
                                        pTask.mixer.pOutputQBuf2.exitQyQ2();
                                        pTask.mixer.pOutputQBuf2=null;
                                    }


                                    if (null != pTask.photomosaic.pOutputQ2) {       //  2009/07/26
                                        exitQyQ2(pTask.photomosaic.pOutputQ2);
                                        //MACRO_safeFree(pTask.photomosaic.pOutputQ2);
                                        pTask.photomosaic.pOutputQ2 = null;
                                    }
                                    if  (  null!=pTask.photomosaic.pOutputQBuf2) {
                                        pTask.photomosaic.pOutputQBuf2.exitQyQ2();
                                        pTask.photomosaic.pOutputQBuf2=null;
                                    }

                                     */

                                    //  2008/10/06
                                    if (!pTask.doProxy.bExists_avProxy) {       //  2014/11/20
                                        if (transferAvInfo.uiTaskType != CONST_imTaskType_transferAvInfo) {   //
                                            askSharedObjToStop(pProcInfo, pSharedObj, pQyMc.gui.hMainWnd, ("exitMisMsg avInfo"));
                                        }
                                    }

                                    //  2009/05/31
//                                    long tmpp =  pTask.audio.pMsgU_toTransform;
//                                    MACRO_safeFree(tmpp);
//                                    pTask.audio.pMsgU_toTransform = (qnmCommProc_mis.MIS_MSGU ) tmpp;
//                                    tmpp =  pTask.video.pMsgU_toTransform;
//                                    MACRO_safeFree(tmpp);
//                                    pTask.video.pMsgU_toTransform = (qnmCommProc_mis.MIS_MSGU ) tmpp;

                                    //  2009/02/16
                                    if (null != videoConference.pMems_from1) {
                                        //qyMemoryFunc.qyFuncs.myfree(pTask.pMems_from);
                                        videoConference.pMems_from1 = null;
                                    }

                                } else {
                                    //case CONST_imCommType_transferAvReplyInfo:
                                    {
                                        if (null == pTask) break;

                                        //  2008/11/20¡£°´Àí£¬Ö»ÓÐÔ¶³ÌÐ­Öú²Å¿ÉÒÔÇåÀí¡£µ«ÊÇËÆºõÒ²Ã»ÓÐÊ²Ã´Ó°Ïì¡£
                                        clearShiftKeys();

                                        //  2010/09/05

                                        //  2009/05/12
                                        exitTransforms(pProcInfo, pTask);
                                        guiShare.GuiShare.pf_freeIndex_qisTrans.call(pTask.index_trans);
                                        pTask.index_trans = -1;
                                        pTask.trans = null;

                                        //
                                        for (index = 0; index < videoConference.usCntLimit_mems_from; index++) {
                                            //  exitTaskAvFrom_video(  pProcInfo,  &pContent->transferAvReplyInfo.myTask.pMems_from[index]  );
                                            //  exitTaskAvFrom_audio(  pProcInfo,  &pContent->transferAvReplyInfo.myTask.pMems_from[index]  );
                                        }

                                        //
                                        /*
                                        if (null != pTask.mixer.pOutputQ2) {
                                            exitQyQ2(pTask.mixer.pOutputQ2);
                                            //MACRO_safeFree(pTask.mixer.pOutputQ2);
                                            pTask.mixer.pOutputQ2 = null;
                                        }
                                        if  (  null!=pTask.mixer.pOutputQBuf2) {
                                            pTask.mixer.pOutputQBuf2.exitQyQ2();
                                            //
                                            pTask.mixer.pOutputQBuf2=null;
                                        }


                                        if (null != pTask.photomosaic.pOutputQ2) {       //  2009/07/26
                                            exitQyQ2(pTask.photomosaic.pOutputQ2);
                                            //MACRO_safeFree(pTask.photomosaic.pOutputQ2);
                                            pTask.photomosaic.pOutputQ2 = null;
                                        }

                                         */

                                        //  2009/05/31
//                                        long tmpp =  pTask.audio.pMsgU_toTransform;
//                                        MACRO_safeFree(tmpp);
//                                        pTask.audio.pMsgU_toTransform = (qnmCommProc_mis.MIS_MSGU ) tmpp;
//                                        tmpp =  pTask.video.pMsgU_toTransform;
//                                        MACRO_safeFree(tmpp);
//                                        pTask.video.pMsgU_toTransform = (qnmCommProc_mis.MIS_MSGU ) tmpp;
                                        //  2009/02/16
                                        if (null != videoConference.pMems_from1) {
                                            //free(pTask.pMems_from);
                                            videoConference.pMems_from1 = null;
                                        }
                                    }
                                }
                            }
                            //
                            break;
                            default:
                                break;
                        }

                    }
                    break;
                    default:
                        break;
                }

            }


            str= "exitMisMsg leaves";
            Log.d(str);
            showInfo_open.qyFuncs.showInfo_open(0, null,str);
            //Log.d("exitMisMsg leaves");

            //
            return;
        };

        public static mcSharedQ.PF_qElemInit_mcTask initMisMsg = (Object p_msg) -> {
            int iErr = -1;
            taskCommon.TASK_common pTaskParam = (taskCommon.TASK_common) p_msg;
            String str;

            // 
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;

            //
            com.qycx.base.ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
            ctxCaller.dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            ctxCaller.bTmp = true;

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTaskParam.m_index_taskInfo, "initMisMsg");
            if (pTaskInfo == null) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;

            //
            qnmCommProc_mis.MIS_MSG_TASK pMsg = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
            {
                ;
                //  int								index;
                int i;

                showInfo_open.qyFuncs.showInfo_open(0 , 0 , "", "initMisMsg enters");

                switch (pMsg.uiType) {
                    case CONST_misMsgType_task: {
                        qnmCommProc_mis.IM_CONTENTU pContent = null;
                        {
                            pContent = M_getMsgContent(pMsg.uiType, pMsg.ucFlg, pMsg.data);
                        }
                        if (null == pContent) return iErr;

                        switch (pContent.uiType) {
                            case imCommType_defs.Consts.CONST_imCommType_transferAvInfo: {
                                if (pTaskData.uiType != CONST_taskDataType_conf){
                                    exitMisMsg.call(p_msg);
                                    return iErr;
                                }
                                qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                                qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

                                taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) pTaskParam;
                                taskAv.PROC_TASK_AV pTask = task.myTask;
                                if (null == pTask) {
                                    exitMisMsg.call(p_msg);
                                    return iErr;
                                }

                                if (pTask.iIndex_taskInfo != task.m_index_taskInfo) {
                                    int ii = 0;
                                    Log.d("qy","initMisMsg: 435");
                                }
                                //
                                pTask.iIndex_taskInfo = task.m_index_taskInfo;

                                if (!pTaskInfo.var.m_bReplyTask) {
                                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                                    if (transferAvInfo.tmp_usSubtype != CONST_transferAvInfo_subtype_task) {
                                        //  2016/04/1
                                        exitMisMsg.call(p_msg);
                                        return iErr;
                                    }
                                    //
                                    //TRANSFER_av_task pTransferAvTask = (TRANSFER_av_task)pContent;
                                    //
                                    qnmCommProc_mis.AV_TRAN_INFO pAvTran = transferAvInfo.ass.tranInfo;

                                    //  2014/08/04
                                    pTask.saveVideo.uiObjType = transferAvInfo.ass.resObj.uiObjType;          //  2014/08/08
                                    pTask.saveVideo.usIndex_obj = transferAvInfo.ass.resObj.usIndex_obj;
                                    pTask.saveVideo.ucbSaveVideo_req = byte2bool(transferAvInfo.viewDynBmp.ucbSaveVideo);

                                    //
                                    if (0 == videoConference.usCntLimit_mems_from || videoConference.usCntLimit_mems_from > MAX_mems_taskAv)
                                    {
                                        exitMisMsg.call(p_msg);
                                        return iErr;
                                    }

                                    //
                                    if (pTask.ucbVideoConferenceStarter) {   //

                                        //  2009/02/17
                                        //  memcpy(  &pTask.pMems_from[0],  &pTask.mem0_from,  sizeof(  pTask.pMems_from[0]  )  );

                                        //
                                        if (pTask.videoConferenceCfg.iCols * pTask.videoConferenceCfg.iRows != videoConference.usCntLimit_mems_from)
                                        {
                                            exitMisMsg.call(p_msg);
                                            return iErr;
                                        }
                                        //  if  (  !pTask.videoConferenceCfg.iWidth_pic  ||  !pTask.videoConferenceCfg.iHeight_pic  )  goto  errLabel;
                                        //pTc->videoConference.usCntLimit_activeMems_from = pTask.videoConferenceCfg.usCntLimit_activeMems_from;
                                        if (0 == videoConference.usCntLimit_activeMems_from || videoConference.usCntLimit_activeMems_from > MAX_activeMems_from) {
                                            //Log.e("initMisMsg: usCntLimit_activeMems_from error, %d", pTask.usCntLimit_activeMems_from);
                                            exitMisMsg.call(p_msg);
                                            return iErr;
                                        }

                                    }

                                    //  if  (  pTask.ucbVideoConferenceStarter  )
                                    {   //  2011/01/23.
                                        //
                                        genericQueue.GENERIC_Q_CFG qCfg = pProcInfo.cfg.outputQ_toMix_audio;
                                        str = "";
                                        /*
                                        //if  (  pProcInfo.cfg.b_useQ2_toEncA)
                                        {
                                            pTask.mixer.pOutputQ2 = new qyq2.QY_Q2();
                                            if (0 != initQyQ2(qCfg, pQyMc.cfg.rwLockParam, 1, null,
                                                    null, null, null, pTask.mixer.pOutputQ2))       //init audio queue zzq
                                            {
                                                exitMisMsg.call(p_msg);
                                                return iErr;
                                            }
                                        }
                                        //else
                                        {
                                              pTask.mixer.pOutputQBuf2  =  new QY_qBuf2();
                                              int  maxDataBufLen  =  128000  *  3  /  2;  //  1.5秒的音频数据
                                              if  (  0!=pTask.mixer.pOutputQBuf2.initQyQ2(CONST_qType_encA,qCfg.uiMaxQNodes,maxDataBufLen,false,  pQyMc.cfg.rwLockParam,true,"encA")  )
                                              {
                                                  exitMisMsg.call(p_msg);
                                                  return  iErr;
                                              }
                                        }



                                        //
                                        qCfg = pProcInfo.cfg.outputQ_toMosaic_video;
                                        pTask.photomosaic.pOutputQ2 = new qyq2.QY_Q2();
                                        if (0 != initQyQ2(qCfg, pQyMc.cfg.rwLockParam, 1, null,
                                                null, null, null, pTask.photomosaic.pOutputQ2)) //init video queue zzq
                                        {
                                            exitMisMsg.call(p_msg);
                                            return iErr;
                                        }
                                        {
                                            pTask.photomosaic.pOutputQBuf2=new QY_qBuf2();
                                            int maxDataBufLen  =  2000000  *  1  /  8;
                                            if  (  0!=pTask.photomosaic.pOutputQBuf2.initQyQ2(CONST_qType_encV,qCfg.uiMaxQNodes,maxDataBufLen,false,  pQyMc.cfg.rwLockParam,true,"encV")  )
                                            {
                                                exitMisMsg.call(p_msg);
                                                return  iErr;
                                            }

                                        }

                                         */
                                    }

                                    //
                                    pTask.index_trans = guiShare.GuiShare.pf_newIndex_qisTrans.call();
                                    pTask.trans = guiShare.GuiShare.pf_getQisTransByIndex.call(pTask.index_trans);
                                    if (pTask.trans == null) {
                                        str ="initMisMsg.newIndex_qisTrans failed";
                                        Log.e(str);
                                        showInfo_open.qyFuncs.showInfo_open(0, "",str);
                                        exitMisMsg.call(p_msg);
                                        return iErr;
                                    }

                                    //
                                    videoConference.uiTranNo_lastModified_pMems_from = getuiNextTranNo.call();


                                    //  2009/05/12. init transforms
                                    switch (transferAvInfo.uiTaskType) {
                                        case CONST_imTaskType_transferAvInfo:
                                            if (pTask.bVideoConference()) {
                                                pTask.trans.usCntLimit_transforms = videoConference.usCntLimit_activeMems_from;
                                            } else {
                                                // add one more trans to dec video
                                                pTask.trans.usCntLimit_transforms = (int) (videoConference.usCntLimit_activeMems_from + 1);
                                            }
                                            //
                                            break;
                                        case CONST_imTaskType_viewDynBmp:           //  2009/09/08
                                            pTask.trans.usCntLimit_transforms = videoConference.usCntLimit_activeMems_from;
                                            break;
                                        default:
                                            break;
                                    }
                                    if (pTask.doProxy.bExists_avProxy) {
                                        pTask.trans.usCntLimit_transforms = 1;
                                    }
                                    qnmCommProc_mis.MIS_MSG_TASK task1 = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                                    if (0 != initTransforms(pProcInfo, task1.addr_logicalPeer,
                                            task1.iTaskId, transferAvInfo.uiTaskType, transferAvInfo.ass.tranInfo, pTask, task1))
                                    {
                                        exitMisMsg.call(p_msg);
                                        return iErr;
                                    }
                                    if (0 != startTransformThreads(pProcInfo, transferAvInfo.uiTaskType, pTask, "initMisMsg "))
                                    {
                                        exitMisMsg.call(p_msg);
                                        return iErr;
                                    }

                                    //
                                    //Log.d("initMisMsg.377");

                                    //
                                    if (pTask.ucbVideoConferenceStarter) {
                                        //  2011/12/08
                                        pTask.usCntLimit_dispatchs = pTask.trans.usCntLimit_transforms;
                                    }
                                    if (pTask.doProxy.bExists_avProxy) {  //  2014/11/21
                                        pTask.usCntLimit_dispatchs = pTask.trans.usCntLimit_transforms;
                                    }

                                    //  2014/11/21
                                    if (0 != pTask.usCntLimit_dispatchs) {
                                        //  2009/06/17
                                        if (0 != initDispatch_qis(pProcInfo, task1.addr_logicalPeer, task1.iTaskId, transferAvInfo.ass.tranInfo, pTask))
                                            return iErr;
                                    }

                                    //  2009/10/15
                                    //_sntprintf(pTask.debugHint, mycountof(pTask.debugHint), ("avReq: taskId %d, %s"), pMsg->task.iTaskId, qyGetDesByType1(getResTable(0,pQyMc->cusRes, CONST_resId_imTaskTypeTable), pContent->transferAvInfo.uiTaskType));
                                    //  Log.e(  "%S",  pTask.debugHint  );

                                    //  2018/10/30
                                    if (transferAvInfo.uiTaskType == CONST_imTaskType_transferAvInfo) {
                                        guiShare.GuiShare.pf_PostMessage.call(pTask.hWnd_task, CONST_qyWm_postComm, CONST_qyWmParam_misMsgInited, 0);
                                    }

                                    //
                                    //Log.d("initMisMsg.408");


                                    //
                                    break;
                                }

                                //
                                //Log.d("initMisMsg.416");


                                //case CONST_imCommType_transferAvReplyInfo:
                                {
                                    //
                                    qnmCommProc_mis.AV_TRAN_INFO tmp_avTranInfo = task.myTask.reply_avTranInfo;
                                    qnmCommProc_mis.AV_TRAN_INFO pAvTran = tmp_avTranInfo;// pContent->transferAvReplyInfo.tranInfo;
                                    //
                                    //if (!pTask) return iErr;

                                    //
                                    if (videoConference.usCntLimit_mems_from < 2 || videoConference.usCntLimit_mems_from > MAX_mems_taskAv) {
                                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("initMisMsg failed, transferAvReply, usCntLimit_mems_from %d, err"), pTask.usCntLimit_mems_from);
                                        exitMisMsg.call(p_msg);
                                        return iErr;
                                    }


                                    //
                                    if (pTask.bVideoConference() || pTask.receiver.bRecvd_proxiedTranInfo) {
                                        //  2009/05/31
                                        pTask.audio.pMsgU_toTransform = new qnmCommProc_mis.MIS_MSGU();// (qnmCommProc_mis.MIS_MSGU ) mymalloc(sizeof(qnmCommProc_mis.MIS_MSGU));
                                        pTask.video.pMsgU_toTransform = new qnmCommProc_mis.MIS_MSGU();// (qnmCommProc_mis.MIS_MSGU ) mymalloc(sizeof(qnmCommProc_mis.MIS_MSGU));
                                    }

                                    if (pTask.receiver.bRecvd_proxiedTranInfo) {
                                    }
                                    //  2011/01/26
                                    //  if  (  pTask.ucbVideoConference  )
                                    {   //  2011/01/23.

                                        //
                                        genericQueue.GENERIC_Q_CFG qCfg;
                                        //memcpy(qCfg,pProcInfo->cfg.outputQ_toMix_audio, sizeof(qCfg));
                                        qCfg = pProcInfo.cfg.outputQ_toMix_audio;
                                        /*
                                        if  (  pProcInfo.cfg.b_useQ2_toEncA) {
                                            pTask.mixer.pOutputQ2 = new qyq2.QY_Q2();
                                            if (0 != initQyQ2(qCfg, pQyMc.cfg.rwLockParam, 1, null,
                                                    null, null, null, pTask.mixer.pOutputQ2)) {
                                                exitMisMsg.call(p_msg);
                                                return iErr;
                                            }
                                        }
                                        else  {
                                              pTask.mixer.pOutputQBuf2=new QY_qBuf2();
                                              int maxDataBufLen  =  128000  *  2  /  8;
                                              if  (  0!=pTask.mixer.pOutputQBuf2.initQyQ2(CONST_qType_encA,qCfg.uiMaxQNodes,maxDataBufLen,false,pQyMc.cfg.rwLockParam,true,"encA")) {
                                                  exitMisMsg.call(p_msg);
                                                  return  -1;
                                              }

                                        }

                                         */

                                        //
                                        qCfg = pProcInfo.cfg.outputQ_toMosaic_video;
                                        /*
                                        pTask.photomosaic.pOutputQ2 = new qyq2.QY_Q2();
                                        if (0 != initQyQ2(qCfg, pQyMc.cfg.rwLockParam, 1, null, null, null, null, pTask.photomosaic.pOutputQ2))
                                        {
                                            exitMisMsg.call(p_msg);
                                            return iErr;
                                        }
                                        {
                                            pTask.photomosaic.pOutputQBuf2=new QY_qBuf2();
                                            int maxDataBufLen  =  2000000  *  2  /  8;      //  2兆bit每秒，所以这里是设置了2秒
                                            if  (  0!=pTask.photomosaic.pOutputQBuf2.initQyQ2(CONST_qType_encV,qCfg.uiMaxQNodes,maxDataBufLen,false,pQyMc.cfg.rwLockParam,true,"encV")  )
                                            {
                                                exitMisMsg.call(p_msg);
                                                return  iErr;
                                            }

                                        }

                                         */

                                    }

                                    pTask.index_trans = guiShare.GuiShare.pf_newIndex_qisTrans.call();
                                    pTask.trans = guiShare.GuiShare.pf_getQisTransByIndex.call(pTask.index_trans);
                                    if (pTask.trans == null) {
                                        str = "initMisMsg.newIndex_qisTrans failed";
                                        Log.d(str);
                                        showInfo_open.qyFuncs.showInfo_open(0, "",str);
                                        exitMisMsg.call(p_msg);
                                        return iErr;
                                    }

                                    //  2009/05/12. init transforms
                                    //
                                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                                    switch (transferAvInfo.uiTaskType) {
                                        case CONST_imTaskType_transferAvInfo:
                                            pTask.trans.usCntLimit_transforms = videoConference.usCntLimit_activeMems_from;
                                            break;
                                        default:
                                            pTask.trans.usCntLimit_transforms = 1;
                                            break;
                                    }

                                    try {
//                                        qnmCommProc_mis.MIS_MSG_TASK task1 = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
//                                        qnmCommProc_mis.TRANSFER_AV_replyInfo transferAvReplyInfo = (qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent;
                                        if (0 != initTransforms(pProcInfo, pMsg.addr_logicalPeer, pMsg.iTaskId, pMsg.uiTaskType, transferAvInfo.ass.tranInfo, pTask, pMsg))
                                        {
                                            exitMisMsg.call(p_msg);
                                            return iErr;
                                        }
                                        if (0 != startTransformThreads(pProcInfo, pMsg.uiTaskType, pTask, "initMisMsg haha"))
                                        {
                                            exitMisMsg.call(p_msg);
                                            return iErr;
                                        }
                                    } catch (Exception e) {
                                        str = "initMisMsg.554, initTransforms exception";
                                        Log.e(str);
                                        showInfo_open.qyFuncs.showInfo_open(0, "",str);
                                        exitMisMsg.call(p_msg);
                                        return iErr;
                                    }
                                }
                            }
                            break;
                            default:
                                break;
                        }

                    }
                    break;
                    default:
                        break;
                }
            }

            iErr = 0;


            if (iErr != 0) exitMisMsg.call(p_msg);
            Log.d("initMisMsg leaves with " + ((iErr != 0) ? "failed" : "OK"));
            showInfo_open.qyFuncs.showInfo_open(0 , 0 , "", "initMisMsg leaves with " + ((iErr != 0) ? "failed" : "OK"));
            return iErr;
        };

        public static int initTaskAvFrom_audio(ctxQmc.CCtxQmc pProcInfo, qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from, int index_activeMems_from, qmcTransform.QY_TRANSFORM pTransform) {
            int iErr = -1;
            //TCHAR tHintBuf[256] = "";
            int tmpiRet = -1;
            int i;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;//QY_GET_GBUF(  );
            if (null == pQyMc) return -1;
            ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;
            //if (!pQmcCfg) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //
            taskAv.PROC_TASK_AV pTask = (taskAv.PROC_TASK_AV) pTransform.pTask;
            if (null == pTask) {  //  2014/08/04
                showInfo_open.qyFuncs.showInfo_open( ("initTaskAvFrom_audio failed: pTask is null."));
                return -1;
            }
            qmcTransform.TRANSFORM_AUDIO_procInfo pTransformAudio = pTransform.audio;


            //
            qyCommProc_open.WAVEFORMATEX pWf_compress = null;
            qyCommProc_open.WAVEFORMATEX pWf_pcm = null;
            qyCommProc_open.WAVEFORMATEX pWf_decompress = null;


            if (pTransform.audio.ucbInited) {   //  2009/05/29
                Log.i("initTaskAvFrom_audio failed: already inited.");
                showInfo_open.qyFuncs.showInfo_open( ("initTaskAvFrom_audio failed: already inited."));
                exitTaskAvFrom_audio(pProcInfo, pTransform, "initTaskAvFrom_audio failed");
                return iErr;
            }

            if (!pTransform.audio.ucbInited) {
                if (0 == pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev) {
                    showInfo_open.qyFuncs.showInfo_open( ("initTaskAvFrom_audio failed: uiTranNo_openAvDev is 0, audio data can't be accepted."));
                    exitTaskAvFrom_audio(pProcInfo, pTransform, "initTaskAvFrom_audio failed");
                    return iErr;
                }

                //
                String str;
                str = "initTaskAvFrom_audio: index_act "  + index_activeMems_from  + " idInfo "  +  pTransformAudio.idInfo.ui64Id;
                showInfo_open.qyFuncs.showInfo_open(str);


                //
                try {
                    //  2009/05/28
                    {
                        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                            ;
                            //TCHAR mutexName[128];
                            qyMcMainDaemon_isCli_mgr.TMP_tBuf128 mutexName = new qyMcMainDaemon_isCli_mgr.TMP_tBuf128();
                            int cntof_mutexName = cntof_tBuf128;
                            //  2011/07/03
                            //  _sntprintf(  mutexName,  mycountof(  mutexName  ),  (  "%s%d"  ),  pProcInfo->cfg.mutexName_syncActiveMem_prefix,  pTransform.iTaskId  );
                            M_getMutexName_syncActiveMem(pQmcCfg, pTransform.iTaskId, mutexName.tBuf, cntof_mutexName);
                            if (0 != syncObj.sync(mutexName.tBuf, pMisCnt.hMutex_syncActiveMem, 1000,"initTaskAvFrom_audio.762"))
                            {
                                exitTaskAvFrom_audio(pProcInfo, pTransform, "initTaskAvFrom_audio failed");
                                return iErr;
                            }

                            pTransform.audio.idInfo.ui64Id = pActiveMem_from.avStream.idInfo.ui64Id;
                            //  2016/12/23
                            pTransform.audio.ta = pActiveMem_from.avStream.obj.tranInfo.audio;
                            syncObj.Dispose();
                        }
                    }
                } catch (Exception e) {
                    exitTaskAvFrom_audio(pProcInfo, pTransform, "initTaskAvFrom_audio failed");
                    return iErr;
                }

                //  2015/09/11
                qnmCommProc_is_open.MY_DEBUG_STEP pdebugStep = pTransform.audio.debugStep;
                {
                    //mymemset(pdebugStep, 0, sizeof(MY_DEBUG_STEP));
                }
                qmcTransform.TRANSFORM_AUDIO_procInfo_status pstatus = pTransform.audio.status;
                {
                    //mymemset(pstatus, 0, sizeof(TRANSFORM_AUDIO_procInfo_status));
                    pTransform.audio.status.clearData();
                }
                qmcTransform.TRANSFORM_AUDIO_procInfo_decInfo pdecInfo = pTransform.audio.decInfo;
                {
                    //mymemset(pdecInfo, 0, sizeof(TRANSFORM_AUDIO_procInfo_decInfo));
                    pdecInfo.clearData();
                }

                //
                M_setMyStep(pTransform.audio.debugStep, 33);

                //
                if (0 == pActiveMem_from.avStream.obj.tranInfo.audio.compressor.ucCompressors)
                    pTransform.audio.ta.compressor.ucCompressors = CONST_audioCompressors_acm;      //  2009/03/30

                //  2014/03/25
                //memset(pTransform.audio.u, 0, sizeof(pTransform.audio.u)  );
                pTransform.audio.u_qoi.myQoi.common.memset0();


                //
                if (pTransform.audio.ta.compressor.ucCompressors == CONST_audioCompressors_acm) {

                    Log.w("acm not supported");
                    exitTaskAvFrom_audio(pProcInfo, pTransform, "initTaskAvFrom_audio failed");
                    return iErr;
                } else if (pTransform.audio.ta.compressor.ucCompressors == CONST_audioCompressors_dmo) {
                    Log.w("dmo not supported");
                    exitTaskAvFrom_audio(pProcInfo, pTransform, "initTaskAvFrom_audio failed");
                    return iErr;

                } else {
                    int uiModuleType = 0;             //  2012/09/09
                    uiModuleType = CONST_moduleType_ffmpeg;
                    uiModuleType = adjustModuleType_decA(uiModuleType);
                    qyMcMainCusModule.CUS_MODULE_compress pModule = M_getCompressModule(pQyMc.cusModules, uiModuleType); //  2012/09/09
                    if (null == pModule || null == pModule.pf_qdcInitDecompressAudio) {
                        exitTaskAvFrom_audio(pProcInfo, pTransform, "initTaskAvFrom_audio failed");
                        return iErr;
                    }
                    //  2016/12/05
                    pTransform.audio.uiModuleType_toDec = uiModuleType;

                    ///////////////////////////
                    //  2016/12/10
                    //  2014/03/25
                    qnmCommProc_mis.AUDIO_COMPRESSOR_CFG compressor = new qnmCommProc_mis.AUDIO_COMPRESSOR_CFG();
                    //memcpy(compressor,pFrom.audio.ta_recvd.compressor, sizeof(compressor));
                    compressor.common.head = pActiveMem_from.avStream.obj.tranInfo.audio.compressor;
                    compressor.common.pCurPkt = pTransformAudio.pCurPkt;
                    compressor.common.size_pCurPkt = 128;//sizeof(myPlayAudioData.myPLAY_AUDIO_DATA);
                    compressor.common.pAudioQ2 = pTransformAudio.q2;
                    compressor.common.pParent_transform = pTransform;
                    compressor.common.uiTransformType_pParent = CONST_uiTransformType_dec;
                    compressor.common.pf_bTransformAudioQuit = qyMcMainMediaProc_decVideo_rtsp.qyFuncs.bTransformAudioQuit;
                    compressor.common.pf_getNextAPkt = getNextVPkt.qyFuncs.getNextAPkt;
                    compressor.common.pf_detachPkt = detachAPkt;
                    // fixme 看到没引用地方，先注释了
//                    compressor.common.pf_clean_myPLAY_AUDIO_DATA = (qyCommProc_open.PF_clean_myPLAY_AUDIO_DATA) clean_myPLAY_AUDIO_DATA;
                    compressor.common.iDecParam_index_pMems_from = -1;
                    compressor.common.dec.pf_smplBitstreamReader_ReadNextFrame_func = smplBitstreamReader_ReadNextFrame_func_a;
                    pTransformAudio.u_qoi.myQoi.common.uiType = CONST_qoiType_decA;

                    if (0 != pModule.pf_qdcInitDecompressAudio.call(compressor, pTransform.audio.ta.ah_compress, pTransform.audio.ta.ah_decompress, pTransform.audio.u_qoi.myQoi.common)) {
                        Log.e("initTaskAvFrom_video: initDmoDecompressVideo failed.");
                        exitTaskAvFrom_audio(pProcInfo, pTransform, "initTaskAvFrom_audio failed");
                        return iErr;
                    }
                }

                if (pTask.saveVideo.ucbSaveVideo_req) {
                }

                pTransform.audio.index_activeMems_from = index_activeMems_from;

                pTransform.audio.ucbInited = true;
            }


            iErr = 0;

            if (0 != iErr) {   //  2012/03/20
                exitTaskAvFrom_audio(pProcInfo, pTransform, "initTaskAvFrom_audio failed");
            }

            return iErr;
        }


        //  void  exitTaskAvFrom_audio(  MC_VAR_isCli  *  pProcInfo,  TASK_AV_FROM  *  pFrom  )
        public static void exitTaskAvFrom_audio(ctxQmc.CCtxQmc pProcInfo, qmcTransform.QY_TRANSFORM pTransform, String hint) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;// QY_GET_GBUF();
            //  if  (  !pFrom  ||  !pFrom.idInfo.ui64Id  )  return;
            int i;

            //
            if (hint == null) hint = "";


            //
            qyMcMainCusModule.CUS_MODULES pCusModules = pProcInfo.getCusModules();
            if (null == pCusModules) {
                //MACRO_qyAssert(0, ("exitTaskAvFrom_video: pCusModules is null"));
                return;
            }

            //  Log.e(  "exitTaskAvFrom_audio: %I64u enters",  pFrom.idInfo.ui64Id  );

            //  if  (  pFrom.audio.ucbInited  )  
            {
                if (0 == pTransform.audio.ta.compressor.ucCompressors) ;
                else if (pTransform.audio.ta.compressor.ucCompressors == CONST_audioCompressors_acm) {

                    //Log.e("acm not supported");
                } else if (pTransform.audio.ta.compressor.ucCompressors == CONST_audioCompressors_dmo) {
                    //exitDecompressAudio_dmo(pTransform.audio.u.dmo);
                    //Log.e("dmo not supported");
                } else {
                    qyMcMainCusModule.CUS_MODULE_compress pModule = M_getCompressModule(pCusModules, pTransform.audio.uiModuleType_toDec);    //  2012/09/09
                    if (null != pModule) {
                        if (null != pModule.pf_qdcExitDecompressAudio) {
                            pModule.pf_qdcExitDecompressAudio.call(pTransform.audio.u_qoi.myQoi.common);
                        }
                    }
                }
                //  2015/05/19
                MACRO_safeFree(pTransform.audio.saveAudio.pSaveAudioPkt);

                //  2010/05/04
                //memset(pTransform.audio.u, 0, sizeof(pTransform.audio.u)  );
                pTransform.audio.u_qoi.myQoi.common.memset0();

                //
                qyCommProc_open.WAVEFORMATEX tmpp;
                if (null != pTransform.audio.pWf_compress) {
                    tmpp = pTransform.audio.pWf_compress;
//                    M_free(tmpp);
                    pTransform.audio.pWf_compress = (qyCommProc_open.WAVEFORMATEX) tmpp;
                }
                if (null != pTransform.audio.pWf_pcm) {
                    tmpp = pTransform.audio.pWf_pcm;
//                    M_free(tmpp);
                    pTransform.audio.pWf_pcm = (qyCommProc_open.WAVEFORMATEX) tmpp;
                    ;
                }
                if (null != pTransform.audio.pWf_decompress) {
                    tmpp = pTransform.audio.pWf_decompress;
//                    M_free(tmpp);
                    pTransform.audio.pWf_decompress = (qyCommProc_open.WAVEFORMATEX) tmpp;
                }
//
//                if (null != pTransform.audio.pBuf_pcm) {
//                    tmpp = (qyCommProc_open.WAVEFORMATEX)pTransform.audio.pBuf_pcm;
//                    pTransform.audio.pBuf_pcm =  tmpp;
//                }

                //  if  (  pFrom.audio.pDecompressBuf_transferAudioData  )  M_free(  pFrom.audio.pDecompressBuf_transferAudioData  );

                //  2012/03/19
                qmcTransform.TRANSFORM_AUDIO_procInfo_status pstatus = pTransform.audio.status;
                {
                    //mymemset(pstatus, 0, sizeof(TRANSFORM_AUDIO_procInfo_status));
                }

                //
                pTransform.audio.ucbInited = false;
            }

            //

            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("exitTaskAvFrom_audio %d"), pTransform.index_activeMems_from);
            int index_activeMems_from = pTransform.index_activeMems_from;
            String str;
            str = ("exitTaskAvFrom_audio: index_act "  + index_activeMems_from  + "idInfo "  +  pTransform.audio.idInfo.ui64Id  +  " "  +  hint);
            Log.i("exitTaskAvFrom_audio leaves");
            showInfo_open.qyFuncs.showInfo_open(0, null,str);
        }

        
        public static int procMsgInput_other_req(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, int lenInBytes_msg, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            if (pMsgInput.uiType != CONST_misMsgType_input)
            {
                return -1;
            }
            int dataLen = 0;
            int len = 0;
            qmCommFunc_mis.MIS_CNT pMisCnt = null;
            int msgLen = 0;
            int uiChannelType = CONST_channelType_robot;

            String str;


            int lenInBytes = 0;
            //
            boolean bRemoveIfErr = true;
            //  2008/11/19
            byte ucFlg = 0;

            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return iErr;

            pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;

            if (isUcFlgResp(pMsgInput.ucFlg)) return iErr;

            //
            if (!pMsgInput.ucbConvrted) {

                if (pChannel.uiType == CONST_channelType_realTimeMedia) {
                    int ii = 0;
                }
                if  (  pChannel.uiType==CONST_channelType_media) {
                    //Log.e("tn_msgInput", "before ntoh: tn "  +  pMsgInput.uiTranNo);
                }

                //
                long  tickCnt_beforeNtoh  =  myGetTickCount(null);
                //
                msgLen = msgInput_ntoh(pCtx, pMisCnt, pMsgInput);
                if (msgLen < 0) {
                    showInfo_open.qyFuncs.showInfo_open("procMsgInput_other_req:ntoh failed");
                    return iErr;
                }
                //
                if  (  false  ) {
                    long tickCnt_afterNtoh = myGetTickCount(null);
                    str = "rtMediaQ size " + getQ2Nodes(pProcInfo.realTimeMediaQ2) + " ntoh costs " + (tickCnt_afterNtoh - tickCnt_beforeNtoh);
                    showInfo_open.qyFuncs.showInfo_open(str);
                }



//                pMsgU = pCtx.pMsgBuf_forInput.msgU;
                pMsgInput = (qnmCommProc_mis.MIS_MSG_INPUT) pCtx.pMsgBuf_forInput.msgU;//pMsgU.input;
            }
            qnmCommProc_mis.MIS_MSG_routeTalkData pAddrTalkData = pMsgInput.data;
            qnmCommProc_mis.IM_CONTENTU pContent = pMsgInput.data.buf;

            /*
            if (0 != pAddrTalkData.route.debugInfo.tTime_start) {
                pAddrTalkData.route.debugInfo.dwTickCnt_peer_recved = tmpFunc.qyFuncs.myGetTickCount(null);
                pAddrTalkData.route.debugInfo.tTime_peer_recved = pMsgInput.tRecvTime;
            }
             */
            int data_len = 464;//(int) Marshal.OffsetOf < MIS_MSG_routeTalkData > ("buf")
            switch (pContent.uiType) {
                case CONST_imCommType_transferFileDataReq:
                    Log.d("CONST_imCommType_transferFileDataReq");
                    break;
                case CONST_imCommType_transferFileEndReq: {
                    pMsgInput.lenInBytes = Math.min(sizeof_MIS_MSG_routeTalkData(), data_len + sizeof_TRANSFER_FILEEND_REQ());

                    if (null == pCtx.pProcessQ) return iErr;
                    qnmCommProc_mis.TRANSFER_FILEEND_RESP resp = new qnmCommProc_mis.TRANSFER_FILEEND_RESP();
                    resp.uiType = CONST_imCommType_transferFileEndResp;
                    if (pCtx.pProcessQ.qTraverse(null, tmpHandler_findOrgMsgAndProc, pMsgInput, resp) <= 0)
                        break;
                    if (resp.tmpInternal.bNeedRemoved_o) {
                        qmcApplyForRemovingInvalidTasks(pProcInfo, 0);  //  2015/09/07
                    }
                    break;
                }
                case CONST_imCommType_transferVideoData: {

                    //
                    if  (  true  )  {
                        //Log.e("tn_msgInput","after ntoh: transferVideoData: tn "  +  pMsgInput.uiTranNo);
                        //


                    }


                    //
                    bRemoveIfErr = false;
                    //  2009/02/25
                    startChannel(pProcInfo, pMisCnt, pChannel, ("proc transferVideoData"));
                    pMsgInput.lenInBytes = Math.min(sizeof_MIS_MSG_routeTalkData(), data_len + sizeof_TRANSFER_VIDEO_DATA());
                    qnmCommProc_mis.TRANSFER_VIDEO_DATA pReq = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent;

                    //
                    if  (  false )  {
                        int jj;
                        for  (  jj  =  0;  jj  <  pReq.usCnt;jj++ )  {
                            qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead mem  = pReq.mems[jj];
                            str  =  "chnType "  +  pChannel.uiType  +  ": dbg_sn"  +  pMsgInput.dbg_sn  +  " tvd: "  + mem.idInfo.ui64Id  +  ": cnt "  +  pReq.usCnt  +  " mem"  +  jj  +  " pts "  +  mem.uiPts;
                            showInfo_open.qyFuncs.showInfo_open(str);
                        }

                    }


                    //
                    pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                    pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;
                    pMsgInput.usCode = CONST_qyCmd_sendMedia;
                    if (0 > procMsgInput_mc_media(pQyMc, pProcInfo, pMsgInput, pMsgInput.lenInBytes, true, pCtx, pChannel)) {
//                        Log.e("procMsgInput_mc_media failed");
                        return iErr;
                    }
                    break;
                }
                case CONST_imCommType_transferAudioData: {
                    qnmCommProc_mis.TRANSFER_AUDIO_DATA pReq = null;
                    bRemoveIfErr = false;
                    //  2009/02/25
                    startChannel(pProcInfo, pMisCnt, pChannel, ("proc transferAudioData"));
                    pMsgInput.lenInBytes = Math.min(sizeof_MIS_MSG_routeTalkData(), data_len + sizeof_TRANSFER_AUDIO_DATA());
                    pReq = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent;
                    pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                    pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;

                    //
                    long tick_before_proc = myGetTickCount(null);
                    //
                    if (0 != procMsgInput_mc_media(pQyMc, pProcInfo, pMsgInput, pMsgInput.lenInBytes, true, pCtx, pChannel)) {
                        //showInfo_open.qyFuncs.showInfo_open("procMsgINput_other_req：procMsgInput_mc_media failed");
                        return iErr;
                    }
                    //
                    if  (  false  ) {
                        long tick_after_proc = myGetTickCount(null);
                        str = "rtMediaQ size " + getQ2Nodes(pProcInfo.realTimeMediaQ2) + " procMsgInput_mc_media costs " + (tick_after_proc - tick_before_proc);
                        showInfo_open.qyFuncs.showInfo_open(str);
                    }
                    //
                    break;
                }
                case CONST_imCommType_taskInteractionReq: {
                    qnmCommProc_mis.TASK_INTERACTION_REQ pReq = null;

                    pMsgInput.lenInBytes =  Math.min(sizeof_MIS_MSG_routeTalkData(), data_len + sizeof_TASK_INTERACTION_REQ());
                    pReq = (qnmCommProc_mis.TASK_INTERACTION_REQ) pContent;

                    pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                    pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;

                    if (pReq.idInfo_imGrp_related.ui64Id != 0)
                        pMsgInput.addr_logicalPeer.idInfo.ui64Id = pReq.idInfo_imGrp_related.ui64Id;
                    else
                        pMsgInput.addr_logicalPeer.idInfo.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;

                    if (0 > qPostMsg(pMsgInput, pQyMc.gui.processQ)) {
                        Log.e("mcThreadProc_mis_work(  ): qPostMsg(  ) failed");
                        return iErr;
                    }
                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                    break;
                }
                case CONST_imCommType_confKey:
                {
                    qnmCommProc_mis.CONF_KEY pReq = null;
                    pMsgInput.lenInBytes =  Math.min(sizeof_MIS_MSG_routeTalkData(), data_len + sizeof_CONF_KEY());

                    pReq = (qnmCommProc_mis.CONF_KEY) pContent;

                    pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                    pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;

                    if (pReq.idInfo_imGrp_related.ui64Id != 0)
                        pMsgInput.addr_logicalPeer.idInfo.ui64Id = pReq.idInfo_imGrp_related.ui64Id;
                    else
                        pMsgInput.addr_logicalPeer.idInfo.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;

                    if (0 != qPostMsg(pMsgInput, pQyMc.gui.processQ)) {
                        Log.e("mcThreadProc_mis_work(  ): qPostMsg(  ) failed");
                        return iErr;
                    }
                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                    break;
                }
                case CONST_imCommType_statusConfLayoutParam:
                {
                    qnmCommProc_mis.StatusConfLayoutParam pReq = null;

                    pMsgInput.lenInBytes =  Math.min(sizeof_MIS_MSG_routeTalkData(), data_len + sizeof_StatusConfLayoutParam());
                    pReq = (qnmCommProc_mis.StatusConfLayoutParam) pContent;

                    pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                    pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;

                    if (pReq.idInfo_imGrp_related.ui64Id != 0)
                        pMsgInput.addr_logicalPeer.idInfo.ui64Id = pReq.idInfo_imGrp_related.ui64Id;
                    else
                        pMsgInput.addr_logicalPeer.idInfo.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;

                    if (0 != qPostMsg(pMsgInput, pQyMc.gui.processQ)) {
                        Log.e("mcThreadProc_mis_work(  ): qPostMsg(  ) failed");
                        return iErr;
                    }
                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                    break;
                }
                case CONST_imCommType_confLayout:
                {
                    pMsgInput.lenInBytes =  Math.min(sizeof_MIS_MSG_routeTalkData(), data_len + sizeof_CONF_LAYOUT());
                    qnmCommProc_mis.CONF_LAYOUT pReq = (qnmCommProc_mis.CONF_LAYOUT) pContent;

                    pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                    pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;

                    if (pReq.idInfo_imGrp_related.ui64Id != 0)
                        pMsgInput.addr_logicalPeer.idInfo.ui64Id = pReq.idInfo_imGrp_related.ui64Id;
                    else
                        pMsgInput.addr_logicalPeer.idInfo.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;

                    if (0 > qPostMsg(pMsgInput, pQyMc.gui.processQ)) {
                        Log.e("mcThreadProc_mis_work(  ): qPostMsg(  ) failed");
                        return iErr;
                    }
                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                    break;
                }
                case CONST_imCommType_confNvrInfo:
                {
                    qnmCommProc_mis.ConfNvrInfo pReq = null;

                    pMsgInput.lenInBytes =  Math.min(sizeof_MIS_MSG_routeTalkData(), data_len + sizeof_ConfNvrInfo());
                    pReq = (qnmCommProc_mis.ConfNvrInfo) pContent;
                    //
                    {
                        qnmCommProc_mis.ConfNvrInfo tmpReq = pReq;
                    }
                    //
                    pMsgInput.idInfo_taskSender.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;
                    pMsgInput.idInfo_taskReceiver.ui64Id = pAddrTalkData.route.idInfo_to.ui64Id;

                    if (pReq.idInfo_imGrp_related.ui64Id != 0)
                        pMsgInput.addr_logicalPeer.idInfo.ui64Id = pReq.idInfo_imGrp_related.ui64Id;
                    else
                        pMsgInput.addr_logicalPeer.idInfo.ui64Id = pAddrTalkData.route.idInfo_from.ui64Id;

                    if (0 != qPostMsg(pMsgInput, pQyMc.gui.processQ)) {
                        return iErr;
                    }
                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                    break;
                }
                default:
                    str = String.format("procMsgInput_other_req: unprocessed contentType " + pContent.uiType);
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    return iErr;
            }

            iErr = 0;
            return iErr;
        }


        public static int sizeof_MIS_MSG_routeTalkData()
        {
            return 964848;//sizeof(MIS_MSG_routeTalkData);
        }

        public static int sizeof_StatusConfLayoutParam()
        {
            //todo 需要计算正确的大小
            return 10;//sizeof(StatusConfLayoutParam);
        }
        public static int sizeof_TASK_INTERACTION_REQ()
        {
            //todo 需要计算正确的大小
            return 10;//sizeof(TASK_INTERACTION_REQ);
        }

        public static int sizeof_TRANSFER_FILEEND_REQ()
        {
            //todo 需要计算正确的大小
            return 10;//sizeof(TRANSFER_FILEEND_REQ);
        }
        public static int sizeof_TRANSFER_VIDEO_DATA()
        {
            return 930616;//sizeof(TRANSFER_VIDEO_DATA);
        }
        public static int sizeof_TRANSFER_AUDIO_DATA()
        {
            return 532016;//sizeof(TRANSFER_AUDIO_DATA);
        }
        public static int sizeof_ConfNvrInfo()
        {
            //todo 需要计算正确的大小
            return 10;//sizeof(ConfNvrInfo);
        }

        public static int sizeof_CONF_LAYOUT()
        {
            //todo 需要计算正确的大小
            return 10;//sizeof(CONF_LAYOUT);
        }

        public static int sizeof_CONF_KEY()
        {
            return 147480;//sizeof(CONF_KEY);
        }

        
        public static int msgInput_ntoh(ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput)
        {
            int iErr = -1;
            String tHintBuf = "";

            if  (pMsgInput.ucbConvrted)
            {
                return iErr;
            }

            if (null == pCtx.pMsgBuf_forInput)
            {
                Log.e("msgInput_ntoh failed, null == p_Ctx.pMsgBuf_forInput");
                return iErr;
            }

            qnmCommProc_mis.MIS_MSG_INPUT pMsg = new qnmCommProc_mis.MIS_MSG_INPUT();
            pMsgInput.copyDataUntilData(pMsg);

            if (isUcFlgTalkData(pMsgInput.ucFlg) || isUcFlgRouteTalkData(pMsgInput.ucFlg))
            {
                if (null == (pMsg.data.buf = qyntohRouteTalkData(pMsgInput.ucFlg, pMsgInput.buffer, pMsgInput.lenInBytes, pMsg.data.route, tHintBuf, cntof_tHintBuf)))
                {
                    Log.e("qyntohRouteTalkData failed. ");
                    return iErr;
                }

                if (0 > getIdInfo_logicalPeer(pMisCnt, pMsg.data.route, pMsg.addr_logicalPeer))
                {
                    Log.e("getIdInfo_logicalPeer failed");
                    return iErr;
                }
            }
            else
            {
                if (null == (pMsgInput.data.buf = qyntohRouteTalkData(pMsgInput.ucFlg, pMsgInput.buffer, pMsgInput.lenInBytes, null,tHintBuf, cntof_tHintBuf))) {
                    Log.e("qyntohRouteTalkData failed. ");
                    return iErr;
                }
            }
            pMsg.ucbConvrted = true;
            pCtx.pMsgBuf_forInput.msgU = pMsg;


            iErr = 0;
            return iErr;
        }


        public static int procMsgInput_other_resp(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, int lenInBytes_msg, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            //
            int dataLen = 0;
            qmCommFunc_mis.MIS_CNT pMisCnt = null;
            qnmCommProc_mis.MIS_MSG_routeTalkData pAddrTalkData = null;
            qnmCommProc_mis.IM_CONTENTU pContent = null;
            int uiChannelType = CONST_channelType_robot;

            //
            String str;


            qySyncCommProc.CQyMalloc mallocObj = new qySyncCommProc.CQyMalloc();
            {
                qnmCommProc_mis.MIS_MSG_INPUT pMsg = null;// (qnmCommProc_mis.MIS_MSG_INPUT ) pCtx.pMsgBuf_forInput;
                if (null == pMsg) {
                    pMsg = new qnmCommProc_mis.MIS_MSG_INPUT();
                }
                if (null == pMsg) return -1;
                //
                int lenInBytes = 0;
                //
                //TCHAR tHintBuf[255 + 1] = "";
                qyMcMainDaemon_isCli_mgr.TMP_tHintBuf tHintBuf = new qyMcMainDaemon_isCli_mgr.TMP_tHintBuf();
                //TCHAR tBuf[255 + 1] = "";
                qyMcMainDaemon_isCli_mgr.TMP_tBuf128 tBuf128 = new qyMcMainDaemon_isCli_mgr.TMP_tBuf128();
                boolean bRemoveIfErr = true;
                //  2008/11/19
                qnmCommProc_mis.MSG_ROUTE route = new qnmCommProc_mis.MSG_ROUTE();
                byte ucFlg = 0;
                pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;

                if (!isUcFlgResp(pMsgInput.ucFlg)) return iErr;


                if (!pMsgInput.ucbConvrted) {
//                    MACRO_memset_misMsgInput(pMsg);
                    //qyMemoryFunc.qyFuncs.mymemcpy( pMsg, pMsgInput, 0);

                    if (isUcFlgTalkData(pMsgInput.ucFlg) || isUcFlgRouteTalkData(pMsgInput.ucFlg)) {
                        pAddrTalkData = null;// (qnmCommProc_mis)pMsg.data;
                        //
//                        if (0 != qyntohRouteTalkData(pMsgInput.ucFlg, ( byte[]) pMsgInput.data.toByteArray(), pMsgInput.lenInBytes, pMsg.data.route, (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf, tHintBuf.tHintBuf, cntof_tHintBuf))
//                        {
//                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("    qyntohRouteTalkData failed. %s"), tHintBuf);
//                            return iErr;
//                        }
                        //if (tHintBuf[0]) qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("    %s"), tHintBuf);
                        //
                        if (0 != getIdInfo_logicalPeer(pMisCnt, pAddrTalkData.route,
                                pMsg.addr_logicalPeer)) return iErr;
                        //			 
                    } else {
                        if (pMsgInput.usCode == CONST_qyRc_servBusy) {
                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("Server returns: %s"), qyGetDesByType1(CONST_qyRcTable, pMsgInput->usCode));
                            iErr = 0;
                            return iErr;
                        }
                        if (pMsgInput.lenInBytes < Integer.BYTES) {   //  Ö¸Ã»ÓÐcontentType
                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("Failed:  Server returns: %s"), qyGetDesByType1(CONST_qyRcTable, pMsgInput->usCode));
                            return iErr;
                        }

//                        if (0 != qyntohRouteTalkData(pMsgInput.ucFlg, ( byte[]) pMsgInput.data, pMsgInput.lenInBytes, null, (qnmCommProc_mis.IM_CONTENTU)pMsg.data, tHintBuf.tHintBuf, cntof_tHintBuf))
//                        {
//                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("qyntohRouteTalkData failed. %s"), tHintBuf);
//                            return iErr;
//                        }

                        //if (tHintBuf[0]) qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("%s"), tHintBuf);
                        //		   
                    }
                    pMsg.ucbConvrted = true;
                    //
                    pMsgInput = pMsg;
                }

                pAddrTalkData = MACRO_msgRouteTalkData(pMsgInput.ucFlg, pMsgInput.data);
                if (null == pAddrTalkData) {
                    Log.i("procMsgInput_other_resp failed: pAddrTalkData is null");
                    return iErr;
                }

                //
                pContent = M_getMsgContent(pMsgInput.uiType, pMsgInput.ucFlg, pMsgInput.data);
                if (null == pContent) return iErr;

                switch (pContent.uiType) {
                    case CONST_imCommType_simpleResp: {

                        //Log.e("procMsgInput_robot_resp: %S, %S", qyGetDesByType1(CONST_qyCmdTable, pContent->simpleResp.usCmd_org), qyGetDesByType1(CONST_qyRcTable, pMsgInput->usCode));

                        if (null == pAddrTalkData) return iErr;
                        if (null == pCtx.pProcessQ) return iErr;

                        qnmCommProc_mis.IM_SIMPLE_RESP simpleResp = (qnmCommProc_mis.IM_SIMPLE_RESP) pContent;
                        switch (simpleResp.usCmd_org) {
                            case CONST_qyCmd_sendRobotTaskReply: {
                                //
                                long tStartTran = tmpFunc.qyFuncs.mytime();
                                int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
                                //
                                qnmCommProc_mis.TRANSFER_FILEDATA_REQ req = new qnmCommProc_mis.TRANSFER_FILEDATA_REQ();

                                //mymemset((byte[])req, 0, sizeof(TRANSFER_FILEDATA_REQ));
                                req.uiType = CONST_imCommType_transferFileDataReq;
                                //
                                req.tmpInternal.tStartTime_new = tStartTran;
                                req.tmpInternal.uiTranNo_new = uiTranNo;
                                //
                                if (pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsgInput, req) <= 0)
                                    return iErr;
                                lenInBytes = 128;//sizeof(TRANSFER_FILEDATA_REQ);

                                if (0 != startChannel(pProcInfo, pMisCnt, pChannel, ("robot channel"))) {
                                    Log.i("robot Channel");
                                    return iErr;
                                }

                                int len = 0;//
//                                byte[] data = req.toByteArray();
                                if (0 != postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, (qnmCommProc_mis.MSG_ROUTE) null, CONST_misMsgType_outputTask, (byte) 0, (short) CONST_qyCmd_sendRobotTaskData, pMsgInput.tStartTime, pMsgInput.uiTranNo, (short) 0, req, (int) lenInBytes, pMsgInput.addr_logicalPeer.idInfo.ui64Id, pAddrTalkData.route.idInfo_from.ui64Id, uiChannelType, null, false))
                                    return iErr;
                            }
                            break;
                            case CONST_qyCmd_sendMedia: {
                                if (simpleResp.uiContentType_org == CONST_imCommType_transferAvReplyInfo) {
                                    if (!isRcOk(pMsgInput.usCode)) {
                                        Log.i("rc is not ok");
                                        return iErr;
                                    }

                                    if (pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsgInput, null) <= 0)
                                        return iErr;

                                    Log.i("");
                                    if (0 != startChannel(pProcInfo, pMisCnt, pChannel, (""))) {
                                        Log.i("start Media Channel Failed.");
                                        return iErr;
                                    }
                                }

                                //  Log.e(  "ÕâÀï£¬ËÆºõÓ¦¸Ã×öÊÓÆµ´¦Ê¼»¯µÄ¹¤×÷¡£µÈÆô¶¯ÁËÊÓÆµ¼à¿Ø£¬ÔÚÓÐÊÓÆµÊý¾ÝÊ±£¬×ÔÈ»£¬¾Í·¢ËÍ¹ýÈ¥ÁË"  );

                            }
                            break;
                            default:
                                break;
                        }

                    }
                    break;
                    case CONST_imCommType_taskProcResp: {

                        bRemoveIfErr = false;

                        if (null == pAddrTalkData) return iErr;
                        if (null == pCtx.pProcessQ) return iErr;

                        qnmCommProc_mis.TASK_PROC_RESP taskProcResp = (qnmCommProc_mis.TASK_PROC_RESP) pContent;
                        if (taskProcResp.idInfo_logicalPeer.ui64Id != 0) {
                            if (taskProcResp.idInfo_logicalPeer.ui64Id == pMisCnt.idInfo.ui64Id) {
                                //  2015/06/08
                                pMsgInput.addr_logicalPeer.idInfo.ui64Id = pMsgInput.data.route.idInfo_from.ui64Id;
                                taskProcResp.idInfo_logicalPeer.ui64Id = 0;
                            } else {
                                pMsgInput.addr_logicalPeer.idInfo.ui64Id = taskProcResp.idInfo_logicalPeer.ui64Id;
                            }
                        }


//                        byte[] byteArray = pMsgInput.toByteArray();
                        if (0 != qPostMsg(pMsgInput, pQyMc.gui.processQ)) {
                            return iErr;
                        }
                        guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                    }
                    break;
                    case CONST_imCommType_transferFileDataResp: {

                        bRemoveIfErr = false;   //  2010/12/05

                        if (null == pCtx.pProcessQ) return iErr;
                        //
                        //printQ_mis(  &pCtx->pProcessQ->m_q,  0  );
                        //
                        //printMisMsg(null, null, (qnmCommProc_mis.MIS_MSGU )pMsgInput);

                        qnmCommProc_mis.TRANSFER_FILEDATA_REQ req = new qnmCommProc_mis.TRANSFER_FILEDATA_REQ();
                        //mymemset((byte[])req, 0, sizeof(TRANSFER_FILEDATA_REQ));
                        req.uiType = CONST_imCommType_transferFileDataReq;
                        if (pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsgInput, req) <= 0)
                            return iErr;
                        lenInBytes = 128;//sizeof(TRANSFER_FILEDATA_REQ);

                        startChannel(pProcInfo, pMisCnt, pChannel, ("proc transferFileDataResp"));

                        //
                        ucFlg = 0;
                        //mymemset((byte[])route, 0, sizeof(MSG_ROUTE));
                        route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;
                        if (true) {
                            ucFlg |= CONST_commFlg_routeTalkData;
                            /*
                            route.debugInfo.dwTickCnt_me_start = tmpFunc.qyFuncs.myGetTickCount(null);
                            route.debugInfo.tTime_start = tmpFunc.qyFuncs.mytime();
                             */
                        }

                        if (!req.tmpInternal.ucbFileEnd) {

                            //  2008/11/19.
                            //route.debugInfo.index = (int) req.uiDataBlockIndex;
//                            byte[] data = req.toByteArray();
                            if (0 != postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, route,
                                    CONST_misMsgType_outputTask, ucFlg, (short) CONST_qyCmd_sendRobotTaskData, pMsgInput.tStartTime, pMsgInput.uiTranNo, (short) 0, req, (int) lenInBytes, pMsgInput.addr_logicalPeer.idInfo.ui64Id,
                                    pAddrTalkData.route.idInfo_from.ui64Id, uiChannelType, null, false))
                                return iErr;
                            break;
                        }

                        qnmCommProc_mis.TRANSFER_FILEEND_REQ endReq = new qnmCommProc_mis.TRANSFER_FILEEND_REQ();
                        //mymemset((byte[])endReq, 0, sizeof(TRANSFER_FILEEND_REQ));
                        endReq.uiType = CONST_imCommType_transferFileEndReq;
                        //  2015/09/07
                        endReq.tmpInternal.bChkToRemove_i = true;
                        //
                        //  if  (  pCtx->pProcessQ->qRemoveMsg(  tmpHandler_findOrgMsgAndProc_is,  pMsgInput,  &endReq  )  )  goto  errLabel;
                        if (pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsgInput, endReq) <= 0)
                            return iErr;
                        //  2015/09/08
                        if (endReq.tmpInternal.bNeedRemoved_o) {
                            qmcApplyForRemovingInvalidTasks(pProcInfo, 0);
                        }
                        //
                        lenInBytes = 128;//sizeof(TRANSFER_FILEEND_REQ);
                        //
                        //route.debugInfo.index = -1; //
//                        byte[] data = endReq.toByteArray();
                        if (0 != postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, route,
                                CONST_misMsgType_outputTask, ucFlg, (short) CONST_qyCmd_sendRobotTaskData, pMsgInput.tStartTime, pMsgInput.uiTranNo, (short) 0, endReq, (int) lenInBytes, pMsgInput.addr_logicalPeer.idInfo.ui64Id,
                                pAddrTalkData.route.idInfo_from.ui64Id, uiChannelType, null, false))
                            return iErr;
                    }
                    break;

                    case CONST_imCommType_transferVideoDataResp:

                        bRemoveIfErr = false;

                        if (null == pCtx.pProcessQ) return iErr;
                        if (pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsgInput, 0) <= 0) {
                            showInfo_open.qyFuncs.showInfo_open(0, null, ("Err: videoDataResp not mateched"));
                            return iErr;
                        }

                        break;

                    case CONST_imCommType_transferAudioDataResp:

                        bRemoveIfErr = false;

                        if (null == pCtx.pProcessQ) return iErr;
                        if (pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsgInput, 0) <= 0) {
                            showInfo_open.qyFuncs.showInfo_open(0, null, ("Err: audioDataResp not mateched"));
                            return iErr;
                        }

                        break;

                    case CONST_imCommType_transferGpsDataResp:
                        bRemoveIfErr = false;

                        if (null == pCtx.pProcessQ) return iErr;
                        if (pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsgInput, 0) <= 0) {
                            showInfo_open.qyFuncs.showInfo_open(0, null, ("Err: gpsDataResp not mateched"));
                            return iErr;
                        }

                        break;

                    default:
                        break;
                }
            }


            //
            iErr = 0;


            if (0 != iErr) {
            }
            return iErr;
        }

        //
        public static mcSharedQ.PF_enumTask tmpHandler_restartRobotTask = (ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU p0, qyMcMainProc.PARAM_findOrgMsg p1, taskCommon.TASK_common taskElem) -> {
            int iErr = -1;
            qyDefs.COMMON_PARAM pCommonParam0 = (qyDefs.COMMON_PARAM) p1.commonParam0;
            ctxQyMc.CCtxQyMc pQyMc = (ctxQyMc.CCtxQyMc) pCommonParam0.p0;
            //qyMcMainObj.QY_SERVICEGUI_INFO pSci = (qyMcMainObj.QY_SERVICEGUI_INFO)pCommonParam0->p1;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pCommonParam0.p2;
            //
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, taskElem.m_index_taskInfo, "");
            if (null == pTaskInfo) return -1;

            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;

            String str;

            qnmCommProc_mis.MIS_MSGU pMsgElem = pTaskData.msgU;
            {
                if (pTaskInfo.var.bNeedRemoved || pTaskInfo.var.bTaskInvalid) {
                    // skipped			 
                    return 0;
                }

                if (pMsgElem.uiType == CONST_misMsgType_task) {
                    qnmCommProc_mis.IM_CONTENTU pContent = null;// (qnmCommProc_mis.IM_CONTENTU) pMsgElem.task.data.buf;

                    //if  (  pContent->uiType  ==  CONST_imCommType_transferFileReplyReq  )  
                    if (pContent.uiType == CONST_imCommType_transferFileReq
                            && pTaskInfo.var.m_bReplyTask) {
                        taskAv.TASK_transferFile task = (taskAv.TASK_transferFile) taskElem;
                        qmcTaskInfo.QMC_taskData_transferFile pTt = (qmcTaskInfo.QMC_taskData_transferFile) pTaskInfo.getTaskData();

                        long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
                        int iDiffInMs = (int) (dwTickCnt - pTt.dwLastReportTickCnt);
                        if (iDiffInMs > Consts.MIN_taskTimeoutInS * 1000) {
                            task.myTask.nTimes_toRestart++;
                            if (task.myTask.uiLastDataBlockIndex < 30) {
                                if (task.myTask.nTimes_toRestart > 3) {
                                    task.myTask.uiSizePerSnd_internal = task.myTask.uiSizePerSnd_internal / 2;
                                    //
                                    //
                                    int minVal = MIN_uiSizePerSnd_file_big;
                                    //				
                                    if (task.myTask.nTimes_toRestart > 9) {
                                        minVal = Consts.MIN_uiSizePerSnd_file_small;
                                    } else if (task.myTask.nTimes_toRestart > 6) {
                                        minVal = Consts.MIN_uiSizePerSnd_file_mid;
                                    }
                                    //
                                    if (task.myTask.uiSizePerSnd_internal < minVal) {
                                        task.myTask.uiSizePerSnd_internal = (int) minVal;
                                    }

                                    //
                                    //TCHAR tBuf[128];
                                    str = String.format("nTimes_toRestart %d, sizePerSnd adjusted to %d", task.myTask.nTimes_toRestart, task.myTask.uiSizePerSnd_internal);
                                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                }
                            }

                            //
                            qnmCommProc_mis.TRANSFER_FILEDATA_REQ req = new qnmCommProc_mis.TRANSFER_FILEDATA_REQ();
                            int lenInBytes;
                            int uiChannelType = CONST_channelType_talking;        //  »Ö¸´²Ù×÷ÓÃtalkingÍ¨µÀ¡£ÒòÎª¿ÉÄÜrobotÍ¨µÀËÀÁË

                            //mymemset((byte[])req, 0, sizeof(TRANSFER_FILEDATA_REQ));
                            req.uiType = CONST_imCommType_transferFileDataReq;

                            req.uiSizePerSnd = task.myTask.uiSizePerSnd_internal;
                            req.tStartTime_org = task.myTask.reply_info.replyReq.tStartTime_org;
                            req.uiTranNo_org = task.myTask.reply_info.replyReq.uiTranNo_org;
                            req.uiSerialNo_org = task.myTask.reply_info.replyReq.uiSerialNo_org;

                            //
                            if (0 == task.myTask.uiLastDataBlockIndex) req.uiDataBlockIndex = 0;
                            else req.uiDataBlockIndex = task.myTask.uiLastDataBlockIndex + 1;

                            //
                            long tStartTran = tmpFunc.qyFuncs.mytime();
                            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
                            qyDefs.QY_MESSENGER_ID idInfo_dst = ((qnmCommProc_mis.MIS_MSG_TASK) pMsgElem).data.route.idInfo_from;
                            str = String.format("restart robot task %d times, uiDataBlockIndex %d, sizePerSnd %d. to %I64u. tn %d, channel %s",
                                    task.myTask.nTimes_toRestart, req.uiDataBlockIndex, req.uiSizePerSnd, idInfo_dst.ui64Id, uiTranNo, qyGetDesByType1(CONST_channelTypeTable, uiChannelType));
                            showInfo_open.qyFuncs.showInfo_open(0, null,str);
                            task.myTask.reply_info.tStartTime_lastFileDataReq = tStartTran;
                            task.myTask.reply_info.uiTranNo_lastFileDataReq = uiTranNo;
//                            byte[] data = req.toByteArray();
                            lenInBytes = 128;//sizeof(TRANSFER_FILEDATA_REQ);
                            qnmCommProc_mis.MIS_MSG_TASK ele_task = (qnmCommProc_mis.MIS_MSG_TASK) pMsgElem;
                            if (0 != postMsg2Mgr_mc((com.qycx.base.ctxCaller.CTX_caller) null, pMisCnt, (qnmCommProc_mis.MSG_ROUTE) null, CONST_misMsgType_outputTask, (byte) 0, (short) CONST_qyCmd_sendRobotTaskData, tStartTran, uiTranNo, (short) 0, req, (int) lenInBytes, ele_task.addr_logicalPeer.idInfo.ui64Id,
                                    idInfo_dst.ui64Id, uiChannelType, null, false)) return iErr;
                        }
                    }
                }
            }

            iErr = 0;

            return iErr;
        };


        //
        public static int restartRobotTask(ctxQyMc.CCtxQyMc pQyMc, qmCommFunc_mis.MIS_CNT pMisCnt, ctxMcThread.CTX_MC_thread pCtx) {
            qyDefs.COMMON_PARAM commonParam = new qyDefs.COMMON_PARAM();
            qmCommFunc_mis.MIS_CHANNEL pChannel = null;

            if (null == pMisCnt) return -1;

            if  (  pQyMc==null) return  -1;
            ctxQmc.CCtxQmc pProcInfo  = pQyMc.get_pProcInfo();
            if  (  pProcInfo==null) return  -1;

            if  (  !pProcInfo.av.talk.bChannelReady  )  return  -1;

            //pChannel = getChannelByType(pMisCnt, CONST_channelType_talking);        //  ×¢Òâ£ºÕâÀïÊ¹ÓÃµÄÊÇtalkingÍ¨µÀ¡£ÒòÎª»Ö¸´ÈÎÎñµÄÖ¸ÁîÐèÒªÔÚtalking·¢ËÍ¡£
            //if (!qnmMainFunc_isCli.qyFuncs.bChannelOk(pChannel)) return -1;

            //
            MACRO_makeCommonParam3(pQyMc, 0, pMisCnt, commonParam);
            pCtx.pProcessQ.qTraverse(null, qyMcMainProc_isCli_robot.qyFuncs.tmpHandler_restartRobotTask, commonParam, 0);

            return 0;
        }


        //
        public static Runnable mcThreadProc_mis_robot = () -> {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            int loopCtrl = 0;
            int i;
            int dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            qnmCommProc_mis.MIS_MSGU pMsg = null;
            int len;
            int dwRet;
            //  long							lPrev;
            ctxMcThread.CTX_MC_thread ctx;
            boolean bMsgArrive = false;
            //
            int uiChannelType = CONST_channelType_robot;

            String str;

            ctx = new ctxMcThread.CTX_MC_thread();
            ctx.pProcessQ = pProcInfo.processQ_media; //  robot;


            pMsg = new qnmCommProc_mis.MIS_MSGU();

            for (; !pQyMc.bQuit; loopCtrl++) {

                //dwRet = WaitForSingleObject(pProcInfo->robotQ.hSemaTrigger, 3500);
                //if (dwRet == WAIT_FAILED) return iErr;
                tmpFunc.qyFuncs.myWaitOne(pProcInfo.robotQ.hSemaTrigger, 3500);

                //
                bMsgArrive = false;

                for (; !pQyMc.bQuit; ) {

                    if (null == (pMsg = (qnmCommProc_mis.MIS_MSGU) qGetMsg(pProcInfo.robotQ)))
                        break;

                    bMsgArrive = true;

                    switch (pMsg.uiType) {
                        case CONST_misMsgType_input: {
                            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;// (MIS_CNT)pMsg->input.pMisCnt;
                            qmCommFunc_mis.MIS_CHANNEL pChannel = null;
                            qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                            if (null == pMisCnt) {
                                Log.i("mcThreadProc_mis_robot failed: pMisCnt is null");
                                break;
                            }
                            str = String.format("mcThreadProc_mis_robot: " + (isUcFlgResp(input.ucFlg) ? ("resp") : ("req")) + " " + qyGetDesByType1(CONST_qyCmdTable, input.usCode));

                            if (null == (pChannel = getChannelByType(pMisCnt, uiChannelType)))
                                break;

                            if (isUcFlgResp(input.ucFlg)) {
                                if (0 != procMsgInput_other_resp(pQyMc, pProcInfo, input, input.lenInBytes, ctx, pChannel))
                                    break;
                            } else {
                                if (0 != procMsgInput_other_req(pQyMc, pProcInfo, input, input.lenInBytes, ctx, pChannel))
                                    break;
                            }

                        }
                        break;
                        case CONST_misMsgType_req:
                            break;
                        case CONST_misMsgType_talk:
                            Log.i("mcThreadProc_mis_robot: msg_talk ");
                            break;
                        case CONST_misMsgType_task: {
                            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                            qmCommFunc_mis.MIS_CHANNEL pChannel = null;

                            if (null == pMisCnt) break;
                            if (null == (pChannel = getChannelByType(pMisCnt, uiChannelType)))
                                break;

                            if (0 != procMsgTask_mc_robot(pQyMc, pProcInfo, (qnmCommProc_mis.MIS_MSG_TASK) pMsg, ((qnmCommProc_mis.MIS_MSG_TASK) pMsg).lenInBytes, false, ctx, pChannel))
                                break;
                        }
                        break;
                        case CONST_misMsgType_applyForChkChannels_qmc: {
                            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;// (MIS_CNT)pMsg->applyForChkChannels_qmc.pMisCnt;
                            if (null == pMisCnt) break;

                            //  if  (  pMsg->applyForChkChannels_qmc.bTaskExists  )
                            {
                                startChannel(pProcInfo, pMisCnt, getChannelByType(pMisCnt, CONST_channelType_robot), null);

                                //  if  (  pMsg->applyForChkChannels_qmc.bTaskNeedRestart  )
                                {
                                    //Log.e(("robot: to restart robot task"));
                                    str = String.format("robot: to restart tasks");
                                    showInfo_open.qyFuncs.showInfo_open(0, null,str);

                                    restartRobotTask(pQyMc, pMisCnt, ctx);
                                }

                            }

                        }
                        break;
                        case CONST_misMsgType_outputTask:
                        default:
                            str = String.format("µÃµ½Î´´¦ÀíµÄÏûÏ¢: %S", qyGetDesByType1(CONST_misMsgTypeTable, pMsg.uiType));

                            break;
                    }

                }


                if (0 == (loopCtrl % 5)) {

                    //  ÏÂÃæ×¢Òâ°Ñ²»ÓÃµÄÍ¨µÀ¸ø»ØÊÕ»ØÀ´
                    //  for  (  i  =  0;  i  <  mycountof(  pProcInfo->misCnts  );  i  ++  )
                    for (i = 0; i < 1; i++) {
                        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;// getMisCntByIndex(0, pProcInfo, i);  //&pProcInfo->misCnts[i];
                        qmCommFunc_mis.MIS_CHANNEL pChannel = null;
                        //
                        if (null == pMisCnt) continue;
                        //
                        if (0 == pMisCnt.cfg.dynCfg.cnt.usCntPort) continue;

                        if (null == (pChannel = getChannelByType(pMisCnt, uiChannelType)))
                            continue;
                        //
                        if (null == pChannel.hThread_io) continue;

                        waitForThread_io(pChannel, false);
                        if (null == pChannel.hThread_io) {
                            str = String.format("%s is closed", qyGetDesByType1(CONST_channelTypeTable, pChannel.uiType));
                            showInfo_open.qyFuncs.showInfo_open(0, null,str);
                        }
                    }
                }
            }


            iErr = 0;

            for (i = 0; i < 1; i++) {
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;// getMisCntByIndex(0, pProcInfo, i);  //  &pProcInfo->misCnts[i]; 
                //
                if (null == pMisCnt) continue;
                //
                if (0 == pMisCnt.cfg.dynCfg.cnt.usCntPort) continue;

                waitForChannel(pProcInfo, pMisCnt, CONST_channelType_robot, true);
            }
            if (pProcInfo != null) pProcInfo.status.threadsStatus.ulbRobotQuit = true;
        };


        //
        public static int procMsgTask_mc_robot(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_TASK pMsg, int lenInBytes_msg, boolean bMsgInput, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            int dataLen = 0;
            int uiMsgLen = 0;
            int uiChannelType = CONST_channelType_talking;
            qnmCommProc_mis.IM_CONTENTU pContent = pMsg.data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
            Object hMainWnd = pQyMc.gui.hMainWnd;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            short usRespCode;
            String str;

            //
            qySyncCommProc.CQyMalloc mallocObj_rcd = new qySyncCommProc.CQyMalloc();
            {
                ;
                qnmCommProc_mis.IM_TASK_RCD pRcd = new qnmCommProc_mis.IM_TASK_RCD();// (IM_TASK_RCD ) mallocObj_rcd.mallocf(sizeof(IM_TASK_RCD));
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

                if (pMsg.addr_logicalPeer.idInfo.ui64Id == 0) {
                    if (0 != getIdInfo_logicalPeer(pMisCnt, pMsg.data.route, pMsg.addr_logicalPeer)) {
                        Log.i("procMsgTask_mc_robot: getIdInfo_logicalPeer failed.");
                        return iErr;
                    }
                }

                if (!isUcFlgResp(pMsg.ucFlg)) {
                    if (!bMsgInput && pMsg.usCode == CONST_qyCmd_sendRobotTask) {

                        switch (pContent.uiType) {
                            case CONST_imCommType_transferFileReq:
                                if (pCtx.pProcessQ.qTraverse(null, tmpHandler_findOrgMsgAndProc, pMsg, 0) <= 0) {
                                    return iErr;
                                }

                                break;
                            case CONST_imCommType_taskProcReq:
                                switch (((qnmCommProc_mis.TASK_PROC_REQ) pContent).usOp) {
                                    case CONST_imOp_send_cancel:
                                    case CONST_imOp_recv_cancel:

                                        //  2015/09/08
                                        //  pCtx->pProcessQ->qRemoveMsg(  tmpHandler_findOrgMsgAndProc_is,  pMsg,  0  );
                                        //
                                        str="Not finished. kkkkkkk";
                                        Log.d(str);
                                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                        return iErr;

                                    default:
                                        break;
                                }

                                break;
                            default:
                                return iErr;
                        }

                        //  Í¨¹ýMGR·¢³öÈ¥
                        //  if  (  postMsg2Mgr_mc(  pQyMc,  pSci,  pMisCnt,  pMsg->uiType,  pMsg->ucFlg,  pMsg->usCode,  pMsg->tStartTime,  pMsg->uiTranNo,  pMsg->uiSeqNo,  (  char  *  )stream,  lenInBytes,  pMsg->idInfo_logicalPeer,  pMsg->data.addr.idInfo_to,  0  )  )  goto  errLabel;
//                        byte[] data = req.toByteArray();
                        if (0 != postMsg2Mgr_mc(null, pMisCnt, null, pMsg.uiType, pMsg.ucFlg, pMsg.usCode, pMsg.tStartTime, pMsg.uiTranNo, pMsg.usSeqNo, null, pMsg.lenInBytes, pMsg.addr_logicalPeer.idInfo.ui64Id,
                                pMsg.data.route.idInfo_to.ui64Id, 0, null, false)) return iErr;

                        iErr = 0;
                        return iErr;
                    }

                    if (bMsgInput && pMsg.usCode == CONST_qyCmd_sendRobotTask) {

                        usRespCode = CONST_qyRc_unknown;
                        Object pDb = null;

                        qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                        {
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
                                Log.i("msg2TaskRcd failed");
                                return iErr;
                            }
                            tmpiRet = storeTaskInDb.qyFuncs.storeTaskInDb(pDb, pQyMc.cfg.db.iDbType, pMisCnt, 0, pRcd, false, "");
                            if (tmpiRet < 0) {
                                Log.i("mcThreadProc_mis_work(  ): storeTaskInDb failed");
                                return iErr;
                            }
                            if (tmpiRet > 0) {
                                switch (pContent.uiType) {
                                    case CONST_imCommType_transferFileReq:
                                        if (rcd.iStatus != CONST_imTaskStatus_applyToRecv && rcd.iStatus != CONST_imTaskStatus_waitToRecv) {
                                            qnmCommProc_mis.IM_SIMPLE_RESP respSimple = new qnmCommProc_mis.IM_SIMPLE_RESP();
//                                            byte[] data = respSimple.toByteArray();
                                            MACRO_makeImSimpleResp(pMsg.usCode, pContent.uiType, rcd.iStatus, respSimple);
                                            int len = 0;//
                                            postMsgTask2Mgr_mc((ctxCaller.CTX_caller) null, pMisCnt, CONST_misMsgType_outputTask, (byte) CONST_commFlg_resp, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType, respSimple, len,
                                                    pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.idInfo_taskSender.ui64Id,
                                                    pMsg.idInfo_taskReceiver.ui64Id,
                                                    pMsg.data.route.idInfo_from.ui64Id, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false);

                                            Log.i("ÕâÀïÊÇËµÃ÷´ËÈÎÎñÒÑ½áÊøÁË£¬²»¹ÜÊ²Ã´½á¹û¡£²é³ö½á¹û·¢»ØÈ¥");

                                            iErr = 0;
                                            return iErr;

                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                        pMsg.iTaskId = rcd.id;
                        pMsg.uiTaskMode_unused = rcd.uiMode;

                        if (0 != qPostMsg(pMsg, pQyMc.gui.processQ)) {
                            Log.i("mcThreadProc_mis_work(  ): qPostMsg(  ) failed");
                            return iErr;
                        }

                        guiShare.GuiShare.pf_PostMessage.call(hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                        iErr = 0;
                        return iErr;

                    }


                    if (!bMsgInput && pMsg.usCode == CONST_qyCmd_sendRobotTaskReply) {


                        switch (pContent.uiType) {
                            case CONST_imCommType_transferFileReplyReq:
                                if (((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContent).usOp != CONST_imOp_recv_accept) {
                                    //pCtx->pProcessQ->qRemoveMsg(  tmpHandler_findOrgMsgAndProc_is,  pMsg,  0  );	//  ²»ÔÊÐí´«ËÍ					
                                    //  2015/09/08
                                    str="Not finished. kkkkkkk";
                                    Log.d(str);
                                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                    return iErr;
                                } else {

                                    if (pCtx.pProcessQ.qTraverse(null, tmpHandler_findOrgMsgAndProc, pMsg, 0) <= 0) {
                                        pMsg.idInfo_taskSender.ui64Id = pMsg.data.route.idInfo_to.ui64Id;
                                        pMsg.idInfo_taskReceiver.ui64Id = pMsg.data.route.idInfo_from.ui64Id;
                                        //
                                        if (0 != pCtx.pProcessQ.qPostMsg(pMsg, lenInBytes_msg)) {
                                            Log.i("procMsgTask_mc_robot: 2.");
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

                        if (0 != postMsg2Mgr_mc(null, pMisCnt, null, pMsg.uiType, pMsg.ucFlg, pMsg.usCode, pMsg.tStartTime, pMsg.uiTranNo, pMsg.usSeqNo, null, pMsg.lenInBytes, pMsg.addr_logicalPeer.idInfo.ui64Id,
                                pMsg.data.route.idInfo_to.ui64Id, 0, null, false)) return iErr;

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
                                if (((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContent).usOp != CONST_imOp_recv_accept)
                                    return iErr;

                                //  
                                if (!g_dbFuncs.pf_bGetImTaskRcdBySth.call(db.m_pDbMem.pDb, pQyMc.cfg.db.iDbType, false, "", pMisCnt.idInfo, ((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContent).tStartTime_org, ((qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContent).uiTranNo_org, 0, pRcd)) {
                                    Log.i("procMsgTask_mc_robot failed: bGetImTaskRcdBySth failed");
                                    return iErr;
                                }

                                if (pCtx.pProcessQ.qTraverse(null, tmpHandler_findOrgMsgAndProc, pMsg, 0) <= 0) {
                                    Log.i("procMsgTask_mc_robot failed: ");
                                    //  postMsg2Mgr...
                                    return iErr;
                                }

                            } else {
                                str = String.format("pContent.uiType is %d", pContent.uiType);

                                return iErr;
                            }
                        }

                        startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferFileReply"));

                        //  ÏÂÃæ×¼±¸·¢ËÍ×¼±¸ºÃµÄÏìÓ¦°ü
                        usRespCode = CONST_qyRc_ok;
                        uiChannelType = CONST_channelType_robot;

                        MACRO_makeImSimpleResp(pMsg.usCode, pContent.uiType, 0, respSimple);

//                        byte[] data = respSimple.toByteArray();
                        int len = 0;
                        postMsgTask2Mgr_mc((ctxCaller.CTX_caller) null, pMisCnt, CONST_misMsgType_outputTask, (byte) CONST_commFlg_resp, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType, respSimple, len,
                                pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.idInfo_taskSender.ui64Id,
                                pMsg.idInfo_taskSender.ui64Id,
                                pMsg.data.route.idInfo_from.ui64Id, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false);

                        iErr = 0;
                        return iErr;

                    }   //  

                    Log.i("procMsgTask_mc_robot:  Óöµ½ÁËÎÞ·¨½âÊÍµÄÇé¿ö£¬´íÎó");
                    return iErr;


                } else {
                    str = String.format("µÃµ½%I64uµÄÏìÓ¦%s", pMsg.data.route.idInfo_from.ui64Id, qyGetDesByType1(CONST_qyRcTable, pMsg.usCode));
                    if (!isRcOk(pMsg.usCode)) return iErr;

                    Log.i("procMsgTask_mc_robot:  ¶ÔÓÚÏìÓ¦°ü£¬»¹Ã»ÓÐ´¦ÀíÄØ");
                }
            }


            iErr = 0;

            return iErr;
        }


    }
}

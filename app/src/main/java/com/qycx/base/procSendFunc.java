


package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_talking;
import static com.qycx.base.dlgTalk_videoConference_public.qyFuncs.addTo_activeMems_from;
import static com.qycx.base.findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc;
import static com.qycx.base.isProtocolFunc.qyFuncs.bSupported_sendVhDec;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc;
import static com.qycx.base.qmcCommFunc_mis.Consts.CONST_talkerObjType_dynBmp;
import static com.qycx.base.qmcFunc_isCli.qyFuncs.msg2TaskRcd;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapBmpBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;
import static com.qycx.base.qmcTaskInfo.qyFuncs.newTaskInfoIndex;
import static com.qycx.base.qmcVideoFunc.qyFuncs.getSharedObjUsr;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendMedia;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_transferAvInfo_subtype_task;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_mems_taskAv;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_mosaicMems_taskAv;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_resourceMems_taskAv_conf;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_resourceMems_taskAv_p2p;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_talkData;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_unknown;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_transferAvInfo;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_viewDynBmp;
import static com.qycx.base.qyCustom.qyFuncs.parse_qnmVerStr;
import static com.qycx.base.qyCustom.qyFuncs.qnmVerStr;
import static com.qycx.base.qyDefs.Consts.CONST_objType_imGrp;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_req;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;

import com.qycx.base.util.Log;

import java.nio.charset.StandardCharsets;

import kotlin.Triple;

public class procSendFunc {
    public static class qyFuncs {

        public static int procSendAvInfo(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMisCnt, Object hWnd_task, boolean bDlgTalk, int uiTaskType, qmcSharedObj.COMPRESS_VIDEO pCompressVideo, qmcSharedObj.COMPRESS_AUDIO pCompressAudio, int iIndex_sharedObj, int iIndex_sharedObjUsr, qmcCommFunc_mis.TALKER_dynBmp pTalkerDynBmp, qyDefs.QY_MESSENGER_ID p_content_idInfo_logicalPeer, int piTaskId, qnmCommProc_mis.MSGR_ADDR pAddr, qmcCommFunc_mis.P_sendAvInfo_taskInfo pOst) {
            int uiTranNo_avTask_starter = 0;
            //
            qnmCommProc_mis.TASK_av_props pTaskAvProps = null;

            //
            qnmCommProc_mis.AV_COMPRESSOR_CFG acc = new qnmCommProc_mis.AV_COMPRESSOR_CFG();
            //
            qmcMosaic.QMC_mosaic_maker qmm = new qmcMosaic.QMC_mosaic_maker();

            //  
            return procSendAvInfoFunc(ctxCaller, pMisCnt, hWnd_task, bDlgTalk, uiTaskType, null, null, acc, qmm, pCompressVideo, pCompressAudio, iIndex_sharedObj, iIndex_sharedObjUsr, pTalkerDynBmp, p_content_idInfo_logicalPeer, uiTranNo_avTask_starter, pTaskAvProps, piTaskId, pAddr, pOst);
        }


        //  2015/03/11
        public static int procSendAvInfo(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMisCnt, Object hWnd_task, boolean bDlgTalk, int uiTaskType, qmcSharedObj.COMPRESS_VIDEO pCompressVideo, qmcSharedObj.COMPRESS_AUDIO pCompressAudio, int iIndex_sharedObj, int iIndex_sharedObjUsr, qmcCommFunc_mis.TALKER_dynBmp pTalkerDynBmp, qyDefs.QY_MESSENGER_ID p_content_idInfo_logicalPeer, qnmCommProc_mis.TASK_av_props pTaskAvProps, int piTaskId, qnmCommProc_mis.MSGR_ADDR pAddr) {
            int uiTranNo_avTask_starter = 0;
            //
            //TASK_av_props  *  pTaskAvProps  =  null;
            qmcCommFunc_mis.P_sendAvInfo_taskInfo pOst = new qmcCommFunc_mis.P_sendAvInfo_taskInfo();

            //
            qnmCommProc_mis.AV_COMPRESSOR_CFG acc = new qnmCommProc_mis.AV_COMPRESSOR_CFG();
            //
            qmcMosaic.QMC_mosaic_maker qmm = new qmcMosaic.QMC_mosaic_maker();

            //  
            return procSendAvInfoFunc(ctxCaller, pMisCnt, hWnd_task, bDlgTalk, uiTaskType, null, null, acc, qmm, pCompressVideo, pCompressAudio, iIndex_sharedObj, iIndex_sharedObjUsr, pTalkerDynBmp, p_content_idInfo_logicalPeer, uiTranNo_avTask_starter, pTaskAvProps, piTaskId, pAddr, pOst);
        }


        //
        public static int procSendAvInfo_conf(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMisCnt, Object hWnd_task, boolean bDlgTalk, int uiTaskType, qnmCommProc_mis.VIDEO_CONFERENCE_CFG pVideoConferenceCfg, qyGuiCommProc.TALKER_policy pTalkerPolicy, qnmCommProc_mis.AV_COMPRESSOR_CFG pAvCompressor_conf, qmcMosaic.QMC_mosaic_maker pMosaic, qmcSharedObj.COMPRESS_VIDEO pCompressVideo, qmcSharedObj.COMPRESS_AUDIO pCompressAudio, int iIndex_sharedObj, int iIndex_sharedObjUsr, qmcCommFunc_mis.TALKER_dynBmp pTalkerDynBmp, qyDefs.QY_MESSENGER_ID p_content_idInfo_logicalPeer, int piTaskId, qnmCommProc_mis.MSGR_ADDR pAddr, qmcCommFunc_mis.P_sendAvInfo_taskInfo pOst) {
            int uiTranNo_avTask_starter = 0;
            //
            qnmCommProc_mis.TASK_av_props pTaskAvProps = null;

            return procSendAvInfoFunc(ctxCaller, pMisCnt, hWnd_task, bDlgTalk, uiTaskType, pVideoConferenceCfg, pTalkerPolicy, pAvCompressor_conf, pMosaic, pCompressVideo, pCompressAudio, iIndex_sharedObj, iIndex_sharedObjUsr, pTalkerDynBmp, p_content_idInfo_logicalPeer, uiTranNo_avTask_starter, pTaskAvProps, piTaskId, pAddr, pOst);
        }

        // 2014/11/22
        public static int procSendAvInfo(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMisCnt, Object hWnd_task, boolean bDlgTalk, int uiTaskType, qmcSharedObj.COMPRESS_VIDEO pCompressVideo, qmcSharedObj.COMPRESS_AUDIO pCompressAudio, int iIndex_sharedObj, int iIndex_sharedObjUsr, qmcCommFunc_mis.TALKER_dynBmp pTalkerDynBmp, qyDefs.QY_MESSENGER_ID p_content_idInfo_logicalPeer, int uiTranNo_avTask_starter, int piTaskId, qnmCommProc_mis.MSGR_ADDR pAddr) {
            //
            qnmCommProc_mis.TASK_av_props pTaskAvProps = null;
            qmcCommFunc_mis.P_sendAvInfo_taskInfo pOst = new qmcCommFunc_mis.P_sendAvInfo_taskInfo();

            qnmCommProc_mis.AV_COMPRESSOR_CFG acc = new qnmCommProc_mis.AV_COMPRESSOR_CFG();
            qmcMosaic.QMC_mosaic_maker qmm = new qmcMosaic.QMC_mosaic_maker();

            //  
            return procSendAvInfoFunc(ctxCaller, pMisCnt, hWnd_task, bDlgTalk, uiTaskType, null, null, acc, qmm, pCompressVideo, pCompressAudio, iIndex_sharedObj, iIndex_sharedObjUsr, pTalkerDynBmp, p_content_idInfo_logicalPeer, uiTranNo_avTask_starter, pTaskAvProps, piTaskId, pAddr, pOst);
        }


        public static int getMosaicTranInfo(qmcShareDynBmp.SHARE_dyn_bmp pShareDynBmp, qnmCommProc_mis.MOSAIC_TRAN_INFO p) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            p.resObj.uiObjType = pShareDynBmp.resObj.uiObjType;
            p.resObj.usIndex_obj = pShareDynBmp.resObj.usIndex_obj;
            //			  
            if (!pShareDynBmp.var.ucbLocalVideoOpen) return iErr;
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, pShareDynBmp.var.iIndex_sharedObj);
            if (null == pSharedObj) return iErr;
            int index_capBmp = pSharedObj.var.iIndex_capBmp;
            qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp = getCapBmpBySth(pProcInfo, index_capBmp, 0);
            if (null == pCapBmp) return iErr;
            qmcSharedObj.COMPRESS_VIDEO pCompressVideo = pCapBmp.compressVideo;

            
            p.video.compressor = pCompressVideo.compressor.common.head;
            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = getSharedObjUsr(pSharedObj, pSharedObj.var.iIndex_curUsr);
            if (null == pSharedObjUsr) return iErr;
            p.video.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_v;
            //
            
            p.video.vh_compress = pCompressVideo.vh_compress;

            
            p.video.vh_stream = pCompressVideo.vh_stream;
            //
            
            p.video.vh_decompress = pCompressVideo.vh_decompress;


            iErr = 0;


            return iErr;
        }


        public static int procSendAvInfoFunc(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMisCnt, Object hWnd_task, boolean bDlgTalk, int uiTaskType, qnmCommProc_mis.VIDEO_CONFERENCE_CFG pVideoConferenceCfg, qyGuiCommProc.TALKER_policy pTalkerPolicy, qnmCommProc_mis.AV_COMPRESSOR_CFG pAvCompressor_conf, qmcMosaic.QMC_mosaic_maker pMosaic, qmcSharedObj.COMPRESS_VIDEO pCompressVideo, qmcSharedObj.COMPRESS_AUDIO pCompressAudio, int iIndex_sharedObj, int iIndex_sharedObjUsr, qmcCommFunc_mis.TALKER_dynBmp pTalkerDynBmp, qyDefs.QY_MESSENGER_ID p_content_idInfo_logicalPeer, int uiTranNo_avTask_starter, qnmCommProc_mis.TASK_av_props pTaskAvProps, int piTaskId, qnmCommProc_mis.MSGR_ADDR pAddr, qmcCommFunc_mis.P_sendAvInfo_taskInfo pPst) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            //TCHAR tBuf[256] = "";
            qySyncCommProc.CQyMalloc mallocObj;
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
            //  int				iIndex_sharedObjUsr		=	0;
            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = null;

            boolean ucbSendLocalAv = false;

            //  2013/02/11
            boolean bReSend_viewDynBmp = false;
            qnmCommProc_mis.DYN_BMP_RULE pDynBmp = null;

            //  2014/10/29
            int iTaskId_suggested = 0;

            //
            qmcCommFunc_mis.TALKER_dynBmp pRuleU = (qmcCommFunc_mis.TALKER_dynBmp) pTalkerDynBmp;

            if (null == pMisCnt) return -1;

            //  2017/08/02
            boolean ucbVideoConference = false;
            //  2017/07/17
            boolean ucbAvConsole = false;
            //  2017/08/25
            int usConfType = 0;


            //  2016/03/13
            //if (pPst != null)
            {
                //  2017/08/02
                ucbVideoConference = pPst.i.ucbVideoConference;
                //  2017/07/17
                ucbAvConsole = pPst.i.ucbAvConsole;
                //  2017/08/25
                usConfType = pPst.i.usConfType;
                //
                //memset(pPst->o, 0, sizeof(pPst->o)  );
                pPst.o = new qmcCommFunc_mis.P_sendAvInfo_taskInfo_o();
            }


            //  2013/02/11
            if (pRuleU != null) {
                if (pRuleU.uiType1 == CONST_talkerObjType_dynBmp) {
                    pDynBmp = pRuleU.dynBmpRule;
                    //
                    if (0 != pRuleU.taskInfo.iTaskId) {
                        if (uiTaskType == CONST_imTaskType_viewDynBmp) {
                            bReSend_viewDynBmp = true;
                        }
                    }
                }
            }

            //
            int iIndex_taskInfo = -1;


            //  2014/10/29
            if (0 != piTaskId) {
                if (0 != piTaskId) {
                    iTaskId_suggested = piTaskId;
                }
            }
            if (bReSend_viewDynBmp) {
                iTaskId_suggested = pRuleU.taskInfo.iTaskId;
            }

            //
            if (!bReSend_viewDynBmp) {
                int uiTaskDataType = 0;
                switch (uiTaskType) {
                    case CONST_imTaskType_transferAvInfo:
                        uiTaskDataType = CONST_taskDataType_conf;
                        break;
                    default:
                        break;
                }

                iIndex_taskInfo = newTaskInfoIndex(ctxCaller, pProcInfo, uiTaskDataType, hWnd_task, iTaskId_suggested, "procSendAvInfoFunc");
                if (iIndex_taskInfo < 0) return iErr;
            }
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, iIndex_taskInfo, "procSendAvInfoFunc");
            if (pTaskInfo == null) return iErr;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return iErr;
            if (pTaskData.uiType != CONST_taskDataType_conf) return iErr;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            //
            switch (uiTaskType) {
                case CONST_imTaskType_transferAvInfo:
                    ucbSendLocalAv = true;
                    break;
                default:
                    break;
            }


            pSharedObj = getSharedObjByIndex(pProcInfo, iIndex_sharedObj);
            if (null == pSharedObj) {
                if (uiTaskType != CONST_imTaskType_viewDynBmp) return iErr;
            } else {       //  2010/09/04
                pSharedObjUsr = getSharedObjUsr(pSharedObj, iIndex_sharedObjUsr);
                if (null == pSharedObjUsr) return iErr;
                //
                if (0 != pCompressAudio.uiTranNo_openAvDev_org) {
                    pSharedObjUsr.uiTranNo_openAvDev_a = (int) (pCompressAudio.uiTranNo_openAvDev_org + iIndex_sharedObjUsr); //  pSharedObj->iIndex_curUsr;
                }
                if (0 != pCompressVideo.uiTranNo_openAvDev_org) {   //
                    pSharedObjUsr.uiTranNo_openAvDev_v = (int) (pCompressVideo.uiTranNo_openAvDev_org + iIndex_sharedObjUsr); //  pSharedObj->iIndex_curUsr;
                }
            }
            long tStartTran = tmpFunc.qyFuncs.mytime();
            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
            //  2013/02/13
            if (bReSend_viewDynBmp) {
                tStartTran = pRuleU.taskInfo.tStartTran;
                uiTranNo = pRuleU.taskInfo.uiTranNo;
            }

            //
            taskAv.TASK_transferAv task = new taskAv.TASK_transferAv();
            task.m_index_taskInfo = iIndex_taskInfo;

            //  
            qnmCommProc_mis.MIS_MSG_TASK pMsg = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
            {
                pMsg.data.buf = new qnmCommProc_mis.TRANSFER_AV_INFO();

                qnmCommProc_mis.TRANSFER_AV_INFO pContent_req = null;
                taskAv.PROC_TASK_AV pContent_myTask = task.myTask;

                int lenInBytes;
                qnmCommProc_mis.IM_TASK_RCD rcd = new qnmCommProc_mis.IM_TASK_RCD();
                //

                pMsg.uiType = CONST_misMsgType_task;
                pMsg.ucFlg = CONST_commFlg_talkData;
                pMsg.usCode = CONST_qyCmd_sendMedia;
                pMsg.tStartTime = tStartTran;
                pMsg.uiTranNo = uiTranNo;

                pContent_req = (qnmCommProc_mis.TRANSFER_AV_INFO ) pMsg.data.buf;
                pContent_req.uiType = imCommType_defs.Consts.CONST_imCommType_transferAvInfo;
                pContent_req.tmp_usSubtype = CONST_transferAvInfo_subtype_task;
                pContent_req.uiTaskType = uiTaskType;

                pContent_myTask.ucbSendLocalAv = ucbSendLocalAv;                                   //  2008/06/12
                pContent_myTask.iIndex_sharedObj = iIndex_sharedObj;
                pContent_myTask.iIndex_sharedObjUsr = iIndex_sharedObjUsr;                         //  2014/12/11
                pContent_myTask.uiTaskType = uiTaskType;
                pContent_myTask.ucbStarter = true;                                             //  2010/08/31

                String verStr = qnmVerStr(pQyMc.iServiceId);
                qyCommonFunc9.TMP_buf256 buf256 = new qyCommonFunc9.TMP_buf256();
                buf256.buf = verStr.getBytes(StandardCharsets.UTF_8);
                Triple<Short,Byte,Byte> trip;
                if (null ==( trip = parse_qnmVerStr(buf256.buf))) return iErr;
                pContent_req.ass.tranInfo.head.shClientVer = trip.component1();


                if (null == pVideoConferenceCfg) {
                    switch (uiTaskType) {
                        case CONST_imTaskType_transferAvInfo:
                            videoConference.usMaxSpeakers = 2;
                            videoConference.usCntLimit_activeMems_from = (int) (videoConference.usMaxSpeakers + MAX_resourceMems_taskAv_p2p);
                            videoConference.usCntLimit_mems_from = 2;                      //  2010/08/31
                            break;
                        default:
                            videoConference.usCntLimit_activeMems_from = 1;
                            videoConference.usCntLimit_mems_from = 2;                  //  2010/09/03

                            break;
                    }
                } else {
                    pContent_myTask.videoConferenceCfg = pVideoConferenceCfg;
                    videoConference.usMaxSpeakers = pContent_myTask.videoConferenceCfg.usMaxSpeakers;
                    videoConference.usCntLimit_activeMems_from = (int) (pContent_myTask.videoConferenceCfg.usMaxSpeakers + MAX_mosaicMems_taskAv + MAX_resourceMems_taskAv_conf);
                    videoConference.usCntLimit_mems_from = (int) (pContent_myTask.videoConferenceCfg.iRows * pContent_myTask.videoConferenceCfg.iCols);

                    pTc.ucbVideoConference = true;                 //  2009/05/30
                    pContent_myTask.ucbVideoConferenceStarter = true;
                }
                if (0 == videoConference.usCntLimit_mems_from || videoConference.usCntLimit_mems_from > MAX_mems_taskAv)
                    return iErr;
                //  2017/08/02
                if (pContent_myTask.bVideoConference() != ucbVideoConference) {
                    return iErr;
                }

                //
                //if (pAddr)
                {
                    if (pAddr.uiObjType == CONST_objType_imGrp) {
                        pContent_req.bGrpTask = true; //  2007/09/05, ¸³ÉÏ×é±êÊ¶									   
                    }
                }

                //  2017/08/02
                pContent_req.confCfg.usMaxSpeakers = pContent_myTask.videoConferenceCfg.usMaxSpeakers;
                pContent_req.confCfg.ucbVideoConference = (byte) (ucbVideoConference ? 1 : 0);
                pContent_req.confCfg.usConfType = usConfType;

                //  2017/08/26
                //if (null!=pPst)
                {
                    pTc.shared_eglContext = pPst.i.shared_eglContext;
                }


                // audio
                if (!pContent_myTask.ucbVideoConferenceStarter) {
                    if (0 != pCompressVideo.uiTranNo_openAvDev_org) {
                        if (null == pSharedObjUsr) return iErr;
                        pContent_req.ass.tranInfo.video.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_v;
                        pContent_req.ass.tranInfo.video.compressor = pCompressVideo.compressor.common.head;
                        pContent_req.ass.tranInfo.video.vh_compress = pCompressVideo.vh_compress;
                        pContent_req.ass.tranInfo.video.vh_stream = pCompressVideo.vh_stream;
                        pContent_req.ass.tranInfo.video.vh_decompress = pCompressVideo.vh_decompress;
                    }

                    if (0 != pCompressAudio.uiTranNo_openAvDev_org) {
                        if (null == pSharedObjUsr) return iErr;
                        pContent_req.ass.tranInfo.audio.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_a;
                        pContent_req.ass.tranInfo.audio.compressor = pCompressAudio.compressor.common.head;
                        pContent_req.ass.tranInfo.audio.ah_compress = pCompressAudio.ah_compress;
                        pContent_req.ass.tranInfo.audio.ah_decompress = pCompressAudio.ah_decompress;
                    }

                    if (pDynBmp != null) {       //  2009/09/07
                        //
                        //  2016/07/24
                        pContent_req.ass.resObj = pDynBmp.dynBmp.resObj;
                        //
                        //  2014/08/04
                        pContent_req.viewDynBmp.ucbSaveVideo = (byte) (pDynBmp.ucbSaveVideo ? 1 : 0);
                    }

                    switch (uiTaskType) {
                        case CONST_imTaskType_viewDynBmp:
                            break;
                        default:
                            qnmCommProc_mis.AV_stream_simple ass = new qnmCommProc_mis.AV_stream_simple(); //mymemset((byte[])ass, 0, sizeof(AV_stream_simple));
                            ass.tranInfo = pContent_req.ass.tranInfo;
                            addTo_activeMems_from(pMisCnt, pMisCnt.idInfo, ass, pTc);
                            break;
                    }


                } else {
                    pContent_req.confCfg.conf.usMaxFps_toShareBmp_conf = pAvCompressor_conf.video.common.head.usMaxFps_toShareBmp; //  pProcInfo->cfg.policy.video.usFrames_perSecond_videoConference;
                    //  2014/08/15
                    pContent_req.confCfg.conf.iWidth_pic_conf = pAvCompressor_conf.video.common.head.iWidth_pic;
                    pContent_req.confCfg.conf.iHeight_pic_conf = pAvCompressor_conf.video.common.head.iHeight_pic;
                    //  2016/03/26
                    if (pTalkerPolicy != null) {
                        //  2017/07/06
                        pContent_req.confCfg.conf.confCtrl.ucbConfCtrl_starter = (byte) (pTalkerPolicy.ucbConfCtrl_starter ? 1 : 0);
                    }
                    //  Note: 会议里的tranInfo起的是组优先策略的作用，不是发一个视频信息. 2014/11/13
                    pContent_req.ass.tranInfo.video.uiTranNo_openAvDev = getuiNextTranNo.call();
                    pContent_req.ass.tranInfo.audio.uiTranNo_openAvDev = pContent_req.ass.tranInfo.video.uiTranNo_openAvDev;

                    {
                        if (0 != pMosaic.video.resObj.uiObjType) {
                            if (0 != getMosaicTranInfo(pMosaic.video, pContent_req.confMosaicTranInfo_video))
                                return iErr;
                        }
                        if (0 != pMosaic.resource.resObj.uiObjType) {
                            if (0 != getMosaicTranInfo(pMosaic.resource, pContent_req.confMosaicTranInfo_resource))
                                return iErr;
                        }
                    }

                }

                if (p_content_idInfo_logicalPeer != null) {
                    pContent_req.idInfo_logicalPeer.ui64Id = p_content_idInfo_logicalPeer.ui64Id;
                }
                pContent_req.uiTranNo_avTask_starter = uiTranNo_avTask_starter;
                if (pTaskAvProps != null) {
                    pContent_req.ass.tranInfo.taskAvProps = pTaskAvProps;
                }

                pContent_myTask.mem0_from.idInfo.ui64Id = pMisCnt.idInfo.ui64Id;
                pContent_myTask.mem0_from.tStartTime_task = tStartTran;
                if (0 != pCompressVideo.uiTranNo_openAvDev_org) {
                    if (null == pSharedObjUsr) return iErr;
                    pContent_myTask.mem0_from.video.tv_recvd.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_v;                                             //
                    pContent_myTask.mem0_from.video.tv_recvd.compressor = pCompressVideo.compressor.common.head;
                    pContent_myTask.mem0_from.video.tv_recvd.vh_compress = pCompressVideo.vh_compress;
                    if (bSupported_sendVhDec()) {
                        pContent_myTask.mem0_from.video.tv_recvd.vh_decompress.bih = pCompressVideo.vh_decompress.bih;
                    }
                    pContent_myTask.mem0_from.video.tv_recvd.vh_stream = pCompressVideo.vh_stream;
                }
                if (0 != pCompressAudio.uiTranNo_openAvDev_org) {
                    if (null == pSharedObjUsr) return iErr;
                    pContent_myTask.mem0_from.audio.ta_recvd.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_a;
                    pContent_myTask.mem0_from.audio.ta_recvd.compressor = pCompressAudio.compressor.common.head;
                    pContent_myTask.mem0_from.audio.ta_recvd.ah_compress = pCompressAudio.ah_compress;
                    pContent_myTask.mem0_from.audio.ta_recvd.ah_decompress = pCompressAudio.ah_decompress;
                    pContent_myTask.mem0_from.audio.ta_recvd.ah_stream = pCompressAudio.ah_stream;
                }

                if (!pContent_myTask.ucbVideoConferenceStarter) {

                } else {
                    pContent_myTask.video.usMaxFps_toShareBmp_conf = pContent_req.confCfg.conf.usMaxFps_toShareBmp_conf;
                }
                pContent_myTask.hWnd_task = hWnd_task; //  2009/09/09
                //  2014/11/11
                //if (pMosaic)
                {
                    if (0 != pMosaic.video.resObj.uiObjType) {
                        pContent_myTask.confMosaicMaker.iIndex_sharedObj_video = pMosaic.video.var.iIndex_sharedObj;
                        pContent_myTask.confMosaicMaker.iIndex_sharedObj_resource = pMosaic.resource.var.iIndex_sharedObj;
                        //
                        pContent_myTask.confMosaicMaker.bExists_confMosaic = true;
                    }
                }

                //  2007/07/18
                pMsg.uiTaskType = uiTaskType;
                pMsg.idInfo_taskSender.ui64Id = pMisCnt.idInfo.ui64Id;
                {
                    pMsg.idInfo_taskReceiver.ui64Id = pAddr.idInfo.ui64Id;
                    pMsg.addr_logicalPeer.idInfo.ui64Id = pAddr.idInfo.ui64Id;
                }
                if (null != p_content_idInfo_logicalPeer && 0 != p_content_idInfo_logicalPeer.ui64Id) {       //  2014/11/22
                    pMsg.addr_logicalPeer.idInfo.ui64Id = p_content_idInfo_logicalPeer.ui64Id;
                }
                //
                pMsg.iStatus = CONST_imTaskStatus_req;
                pMsg.data.route.idInfo_from.ui64Id = pMsg.idInfo_taskSender.ui64Id;
                pMsg.data.route.idInfo_to.ui64Id = pMsg.idInfo_taskReceiver.ui64Id;

                lenInBytes = 49224;//sizeof(qnmCommProc_mis.TRANSFER_AV_INFO);
                pMsg.lenInBytes = lenInBytes + 1136;//Marshal.OffsetOf<MIS_MSG_routeTalkData>("buf")

                if (null == msg2TaskRcd(pMisCnt, pMsg, CONST_qyRc_unknown, rcd))
                    return iErr;

                {
                    Object pDb = null;

                    qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                    {
                        if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                        pDb = db.m_pDbMem.pDb;
                        //
                        if (storeTaskInDb.qyFuncs.storeTaskInDb(pDb, pQyMc.cfg.db.iDbType, pMisCnt, iTaskId_suggested, rcd,
                                false, "procSendAvInfoFunc") < 0) return iErr;
                    }
                }

                //  2013/02/13
                if (iTaskId_suggested != 0) {       //  2014/10/29
                    if (rcd.id != iTaskId_suggested) {
                        return iErr;
                    }
                }
                //
                if (uiTaskType == CONST_imTaskType_viewDynBmp) {
                    if (pRuleU != null && pRuleU.uiType1 == CONST_talkerObjType_dynBmp) {
                        if (pRuleU.taskInfo.bNeedProgress) {
                            //startProgress(uiTranNo);
                        }
                    }
                }

                pContent_myTask.iIndex_taskInfo = iIndex_taskInfo;

                //
                pMsg.iTaskId = rcd.id;
                pMsg.uiTaskMode_unused = rcd.uiMode;
                pMsg.uiChannelType = CONST_channelType_media;      //  2009/10/17

                //


                //
                if (bDlgTalk) {

                    if (uiTaskType != CONST_imTaskType_viewDynBmp) {   //  2014/09/26

                        //
                        if (0 != guiShare.GuiShare.pf_dlgTalk_qPostMsg.call(hWnd_task, pMsg, (int) pMsg.lenInBytes))
                            return iErr;
                    }

                    //


                }


                ///////////////
                //

                //  2009/04/28
                if (pSharedObj != null) {
                    if (null == pSharedObjUsr) return iErr;

                    pSharedObjUsr.iTaskId = rcd.id;
                    //
                    //if (pAddr)
                    {
                        pSharedObjUsr.idInfo_to.ui64Id = pAddr.idInfo.ui64Id;         //  2010/09/02
                        pSharedObjUsr.ucbVideoConferenceStarter = pContent_myTask.ucbVideoConferenceStarter;
                        pSharedObjUsr.ucbVideoConference = pContent_myTask.bVideoConference();        //  2018/11/10
                        //
                        //pTc.ucbNotSendData = (ucbSendLocalAv && pAddr.uiObjType == CONST_objType_imGrp);
                        pSharedObj.var.ucbNotSendData = (ucbSendLocalAv && pAddr.uiObjType == CONST_objType_imGrp);
                    }
                }
                if (piTaskId != 0) piTaskId = rcd.id;

                //
                if (0 != pProcInfo.processQ_media.qPostMsg(task, 0)) {
                    Log.e( "procMsgTask_mc_robot: 1. qPostMsg failed");
                    return iErr;
                }
                if (task.myTask.ucbVideoConferenceStarter) {
                    if (pProcInfo.processQ_media.qTraverse(ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0) <= 0)
                        return iErr;
                }

                int uiChannelType = CONST_channelType_media;
                //  
                uiChannelType = CONST_channelType_talking;
                //
                qyDefs.QY_MESSENGER_ID idInfo_dst = new qyDefs.QY_MESSENGER_ID();
                idInfo_dst.ui64Id = pAddr.idInfo.ui64Id;

                if (0 != postMsgTask2Mgr_mc(ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, (short) CONST_qyCmd_sendMedia, tStartTran, uiTranNo, (short) 0,
                        pMsg.iTaskId, pMsg.uiTaskType, pContent_req, lenInBytes, pMsg.addr_logicalPeer.idInfo.ui64Id,
                        pMsg.idInfo_taskSender.ui64Id, pMsg.idInfo_taskReceiver.ui64Id, idInfo_dst.ui64Id, uiChannelType,  null, false))
                    return iErr;

                //  2009/02/20
                if (bDlgTalk) {

                    if (uiTaskType == CONST_imTaskType_transferAvInfo
                        //&& pPst!=null
                    ) {
                        //
                        //  2016/03/14

                        //
                        pPst.o.iTaskId = rcd.id;
                        //  m_var.av.taskInfo.iIndex_sharedObj  =  iIndex_sharedObj;
                        pPst.o.idInfo_starter.ui64Id = rcd.idInfo_send.ui64Id;

                        pPst.o.tStartTime_org = rcd.tSendTime;
                        pPst.o.uiTranNo_org = rcd.uiTranNo;
                        pPst.o.uiContentType_org = rcd.uiContentType;

                        pPst.o.ucbStarter = pContent_myTask.ucbStarter;                                   //  2010/08/31
                        pPst.o.ucbVideoConference = pContent_myTask.bVideoConference();                   //  2010/08/31
                        //  m_var.av.taskInfo.ucbVideoConferenceStarter  =  pContent_myTask.ucbVideoConferenceStarter;

                        //
                        pPst.o.uiInitW = (int) pContent_req.ass.tranInfo.video.vh_decompress.bih.biWidth;
                        pPst.o.uiInitH = (int) pContent_req.ass.tranInfo.video.vh_decompress.bih.biHeight;

                        //  2014/09/25
                        pPst.o.iIndex_taskInfo = pContent_myTask.iIndex_taskInfo;


                        //  2010/08/31

                        //

                        //
                        //  if  (  pContent_myTask.ucbVideoConferenceStarter  )  
                        {
                            //
                            pPst.o.local_avStream.tranInfo.audio = pContent_myTask.mem0_from.audio.ta_recvd;
                            pPst.o.local_avStream.tranInfo.video = pContent_myTask.mem0_from.video.tv_recvd;

                        }


                    }
                }

                //  2013/02/13
                if (uiTaskType == CONST_imTaskType_viewDynBmp) {
                    if (pRuleU != null && pRuleU.uiType1 == CONST_talkerObjType_dynBmp) {
                        if (!bReSend_viewDynBmp) {  //  新任务才需要设置
                            pRuleU.taskInfo.iTaskId = rcd.id;
                            pRuleU.taskInfo.tStartTran = tStartTran;
                            pRuleU.taskInfo.uiTranNo = uiTranNo;
                        }
                    }
                }

            }


            iErr = 0;


            //  2013/02/13
            if (iErr != 0) {
                if (bReSend_viewDynBmp) {  //  重发viewDynBmp如果失败
                }
            }

            return iErr;
        }


        //

        // 
        public static int procSendAvReplyInfo(qmCommFunc_mis.MIS_CNT pMisCnt, Object hWnd_task, qnmCommProc_mis.MIS_MSG_TASK pMsgTask, boolean ucbSendLocalAv, int iIndex_sharedObj, qnmCommProc_mis.DYN_BMP_RULE pDynBmp, qnmCommProc_mis.TASK_av_props pTaskAvProps, qnmCommProc_mis.MSGR_ADDR pAddr) {
            int iErr = -1;
            ctxQmc.CCtxQmc pProcInfo = null;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            qnmCommProc_mis.IM_CONTENTU pContent = null;

            if (null == pMisCnt) return -1;
            pProcInfo = pQyMc.get_pProcInfo();    //  (  MC_VAR_isCli  *  )pMisCnt.pProcInfoParam;

            pContent = (qnmCommProc_mis.IM_CONTENTU) pMsgTask.data.buf;//(qnmCommProc_mis.IM_CONTENTU) pMsgTask.data.buf;

            //TRANSFER_AV_reply_task transferAvReplyInfo;
            int lenInBytes = 0;
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = null;

            iErr = 0;

            return iErr;
        }


        //  2014/08/28.
        public static int procSendTaskProcReq(qmCommFunc_mis.MIS_CNT pMisCnt, int usCode, int usOp, long tStartTime_org, int uiTranNo_org, int uiContentType_org, int iTaskId, int uiTaskType, qnmCommProc_mis.QIS_RES_OBJ pDynBmp, int uiChannelType, boolean bOutputDirectly, String pHint, qnmCommProc_mis.MSGR_ADDR pAddr) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//(  MC_VAR_isCli  *  )pMisCnt.pProcInfoParam;
            qnmCommProc_mis.TASK_PROC_REQ taskProcReq = new qnmCommProc_mis.TASK_PROC_REQ();
            int lenInBytes;


            iErr = 0;

            return iErr;
        }


    }
}




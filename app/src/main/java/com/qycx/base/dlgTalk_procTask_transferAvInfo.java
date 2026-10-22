
package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.DlgSelectAvCompressor.qyFuncs_Droid.getTalkerPolicy;
import static com.qycx.base.DlgTalk.qyFuncs_Droid.dlgTalk_doLayout;
import static com.qycx.base.DlgTalk.qyFuncs_Droid.refreshTalkToInfo_mgr;
import static com.qycx.base.audioRecorder_dsound.qyFuncs_Droid.startLocalAudioRecorder;
import static com.qycx.base.dlgPolicyIsClient.qyFuncs_Droid.myGetAvCompressorCfg;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.chkResources;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.dlgTalk_closeTaskAv;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.dlgTalk_init_respInfo;
import static com.qycx.base.dlgTalk_videoConference.qyFuncs_Droid.dlgTalk_requestToSpeak;
import static com.qycx.base.doCmd_startAvCall.qyFuncs_Droid.setProcessingAvTask;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvReplyInfo;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc;
import static com.qycx.base.qmcSharedObj.Consts.CONST_capType_av;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcVideoCapture.qyFuncs.setCurSharedObjUsr_localAv;
import static com.qycx.base.qmcVideoCapture_isCli.qyFuncs.newCapProcInfoAudioIndex;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendMedia;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_dual_permit;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recvFile_saveas;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_accept;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_transferAvReplyInfo_subtype_task;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_activeMems_from;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_mems_taskAv;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_mosaicMems_taskAv;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_resourceMems_taskAv_conf;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_resourceMems_taskAv_p2p;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_remoteAssist;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_transferAvInfo;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_1080p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_2160p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_240p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_480p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_576p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_720p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_848x480;
import static com.qycx.base.qyDefs.Consts.CONST_objType_imGrp;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_waitToRecv;
import static com.qycx.base.qyWndContentType.Consts.CONST_qyWndContentType_talker;
import static com.qycx.base.refreshTalkerList.qyFuncs.resetTalkerList_mgr;
import static com.qycx.base.tmpFunc.qyFuncs.byte2bool;

import com.qycx.MainActivity;
import com.qycx.base.util.Log;
import com.qycx.base.util.javatuples.Triplet;

public class dlgTalk_procTask_transferAvInfo {
    public static class qyFuncs_Droid {

        public static int getAvLevelByWh(int wParam, int hParam) {
            int level = 0;

            //
            int w = hParam;
            int h = wParam;


            //
            if (0 == h) return 0;

            //			
            if (h <= 240) level = CONST_policyAvLevel_240p;
            else if (h <= 480) {
                if (w >= 848) level = CONST_policyAvLevel_848x480;
                else level = CONST_policyAvLevel_480p;
            } else if (h <= 576) {
                level = CONST_policyAvLevel_576p;
            } else if (h <= 720) {
                level = CONST_policyAvLevel_720p;
            } else if (h <= 1080) {
                level = CONST_policyAvLevel_1080p;
            } else if (h <= 2160) {
                level = CONST_policyAvLevel_2160p;
            } else {
                level = CONST_policyAvLevel_1080p;
            }


            return level;
        }

        public static int getAvLevel_reply(qnmCommProc_mis.TRANSFER_AV_INFO pReq) {
            int avLevel = CONST_policyAvLevel_480p;

            //
            if (null == pReq) return 0;
            int iVal = 0;
            if (0 == pReq.confCfg.ucbVideoConference) {
                iVal = pReq.ass.tranInfo.video.vh_decompress.bih.biWidth * pReq.ass.tranInfo.video.vh_decompress.bih.biHeight;
            } else {
                iVal = pReq.confMosaicTranInfo_video.video.vh_decompress.bih.biWidth * pReq.confMosaicTranInfo_video.video.vh_decompress.bih.biHeight;
            }

            //
            if (iVal > (1920 + 200) * (1080 + 200)) avLevel = CONST_policyAvLevel_2160p;
            else if (iVal >= 1920 * 1080) avLevel = CONST_policyAvLevel_1080p;
            else if (iVal >= 1280 * 720) avLevel = CONST_policyAvLevel_720p;
            else if (iVal >= 640 * 480) avLevel = CONST_policyAvLevel_480p;
            else avLevel = CONST_policyAvLevel_240p;


            return avLevel;

        }

        //接收任务
        public static int dlgTalk_procTask_transferAvInfo(Object hCurTalk, dlgTalkProc.DLG_TALK_var pCurVar, Object hMgr, dlgTalkProc.DLG_TALK_var pMgrVar, int iTaskId, int usOp, String paramStrBuf,
                                                          qnmCommProc_mis.IM_TASK_RCD rcd, qnmCommProc_mis.MIS_MSG_TASK pMsgTask, qnmCommProc_mis.IM_CONTENTU pContent,
                                                          int iStatus, int iStatus_old,
                                                          myDb.QM_dbFuncs g_dbFuncs,
                                                          Object pDb,
                                                          //TALKER_shadow_mgr pShadowMgr,
                                                          dlgTalkPublic.PARAM_dlgTalk_procTask pParam
        ) {
            int iErr = -1;

            //
            boolean bDbg = false;
            String str;

            Object hDlgTalk = hMgr;
            dlgTalkProc.DLG_TALK_var m_var = pMgrVar;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            boolean bPrepareSet = false;
            //
            int i;
            int lenInBytes;

            //
            if  (  pMsgTask.data.route.idInfo_from.ui64Id  <  10000  )  {
                //Log.d("qy","kkkk");
            }

            //
            boolean ucbGot_iIndex_sharedObj_localAv = false;

            if (rcd.uiContentType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo)
                return -1;

            //
            showInfo_open.qyFuncs.showInfo_open("dlgTalk_procTask_transferAvInfo enters");


            do {

                //
                int iIndex_taskInfo = -1;
                iIndex_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(mainActivity.var_common.ctxCaller, pProcInfo, iTaskId);
                if (iIndex_taskInfo < 0) {
                    Log.e("dlgTalk_procTask_transferAvInfo failed, index_taskInfo <0");
                    if (ucbGot_iIndex_sharedObj_localAv) {
                        //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                        //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                        dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                    }
                    //  2010/12/24
                    chkResources(hDlgTalk, false);
                    if (bPrepareSet) {
                        m_var.av.taskInfo.bPrepare = false;
                    }
                    return iErr;
                }
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, iIndex_taskInfo, "dlgTalk_procTask_transferAvInfo");
                if (pTaskInfo == null) {
                    Log.e("pTaskInfo == null 137  ");
                    if (ucbGot_iIndex_sharedObj_localAv) {
                        //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                        //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                        dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                    }
                    //  2010/12/24
                    chkResources(hDlgTalk, false);
                    if (bPrepareSet) {
                        m_var.av.taskInfo.bPrepare = false;
                    }
                    return iErr;
                }
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (pTaskData == null) {
                    Log.e("pTaskData == null  ");
                    if (ucbGot_iIndex_sharedObj_localAv) {
                        //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                        //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                        dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                    }
                    //  2010/12/24
                    chkResources(hDlgTalk, false);
                    if (bPrepareSet) {
                        m_var.av.taskInfo.bPrepare = false;
                    }
                    return iErr;
                }

                if (pTaskData.uiType != qmcTaskInfo.Consts.CONST_taskDataType_conf) {
                    Log.e("pTaskData.uiType != CONST_taskDataType_conf ");
                    if (ucbGot_iIndex_sharedObj_localAv) {
                        //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                        //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                        dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                    }
                    //  2010/12/24
                    chkResources(hDlgTalk, false);
                    if (bPrepareSet) {
                        m_var.av.taskInfo.bPrepare = false;
                    }
                    return iErr;
                }

                qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

                try {
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
                            //
                            boolean bFirstUsr = false;
                            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = null;
                            qyGuiCommProc.TALKER_policy talkerPolicy = new qyGuiCommProc.TALKER_policy();

                            if (pContent.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {
                                if (ucbGot_iIndex_sharedObj_localAv) {
                                    //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                    //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                    dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                }
                                //  2010/12/24
                                chkResources(hDlgTalk, false);
                                if (bPrepareSet) {
                                    m_var.av.taskInfo.bPrepare = false;
                                }
                                return iErr;
                            }
                            int level = getAvLevel_reply((qnmCommProc_mis.TRANSFER_AV_INFO) pContent);
                            if (level >= CONST_policyAvLevel_2160p) {
                                Log.d("not support 4k");
                                m_var.is4k = true;
                                if (ucbGot_iIndex_sharedObj_localAv) {
                                    //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                    //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                    dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                }
                                //  2010/12/24
                                chkResources(hDlgTalk, false);
                                if (bPrepareSet) {
                                    m_var.av.taskInfo.bPrepare = false;
                                }
                                return iErr;
                            } else {
                                m_var.is4k = false;
                            }


                            //  2011/08/08
                            qnmCommProc_mis.AV_COMPRESSOR_CFG avCompressor_reply = new qnmCommProc_mis.AV_COMPRESSOR_CFG();       //  响应要取得compressor, 目前只有av才有需要. 2012/02/16
                            if (0 != myGetAvCompressorCfg(CONST_capType_av, 0, 0, level, avCompressor_reply)) {
                                Log.e("0 != myGetAvCompressorCfg(CONST_capType_av, 0, 0, level,  avCompressor_reply)");
                                if (ucbGot_iIndex_sharedObj_localAv) {
                                    //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                    //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                    dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                }
                                //  2010/12/24
                                chkResources(hDlgTalk, false);
                                if (bPrepareSet) {
                                    m_var.av.taskInfo.bPrepare = false;
                                }
                                return iErr;
                            }

                            //  2017/08/08
                            if (rcd.uiType == CONST_imTaskType_transferAvInfo) {
                                qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                                if (0 == transferAvInfo.confCfg.ucbVideoConference) {
                                    if (pMgrVar.addr.uiObjType == CONST_objType_imGrp) {
                                        transferAvInfo.confCfg.ucbVideoConference = tmpFunc.qyFuncs.bool2byte(true);
                                    }
                                }
                            }

                            //  2016/04/17
                            //  2014/08/15
                            qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                            if (rcd.uiType == CONST_imTaskType_transferAvInfo && 0 != transferAvInfo.confCfg.ucbVideoConference) {
                                int iWidth_pic_real = Math.min(avCompressor_reply.video.common.head.iWidth_pic, transferAvInfo.confCfg.conf.iWidth_pic_conf);
                                int iHeight_pic_real = Math.min(avCompressor_reply.video.common.head.iHeight_pic, transferAvInfo.confCfg.conf.iHeight_pic_conf);
                                //
                                int level_real = getAvLevelByWh(iWidth_pic_real, iHeight_pic_real);
                                if (0 != myGetAvCompressorCfg(CONST_capType_av, 0, 0, level_real, avCompressor_reply)) {
                                    Log.e(("dlgTalk_procTask_transferAvInfo failed, myGetAvCompressorCfg failed. real"));
                                    if (ucbGot_iIndex_sharedObj_localAv) {
                                        //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                        //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                        dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                    }
                                    //  2010/12/24
                                    chkResources(hDlgTalk, false);
                                    if (bPrepareSet) {
                                        m_var.av.taskInfo.bPrepare = false;
                                    }
                                    return iErr;  //  2014/08/15
                                }
                                //
                                if  (  0!=transferAvInfo.confCfg.ucbVideoConference) {
                                    if  (  transferAvInfo.confCfg.conf.conf_bitrateInKbps_ul!=0) {
                                        avCompressor_reply.video.common.head.iAvgBitrate = transferAvInfo.confCfg.conf.conf_bitrateInKbps_ul  *  1000;
                                    }
                                }
                                //
                                int usMaxFps_toShareBmp_real = avCompressor_reply.video.common.head.usMaxFps_toShareBmp;
                                if (0 != transferAvInfo.confCfg.conf.usMaxFps_toShareBmp_conf) {
                                    usMaxFps_toShareBmp_real = Math.min(usMaxFps_toShareBmp_real, transferAvInfo.confCfg.conf.usMaxFps_toShareBmp_conf);
                                    //
                                    if (0 != avCompressor_reply.video.common.head.iAvgBitrate) {
                                        if (usMaxFps_toShareBmp_real < avCompressor_reply.video.common.head.usMaxFps_toShareBmp) {
                                            if (0 != avCompressor_reply.video.common.head.usMaxFps_toShareBmp) {
                                                avCompressor_reply.video.common.head.iAvgBitrate = avCompressor_reply.video.common.head.iAvgBitrate * usMaxFps_toShareBmp_real / avCompressor_reply.video.common.head.usMaxFps_toShareBmp;
                                            }
                                        }
                                    }
                                }
                            }
                            getTalkerPolicy(m_var.addr.idInfo, talkerPolicy);
                            if (rcd.uiContentType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {

                                if (rcd.uiType == CONST_imTaskType_transferAvInfo) {
                                    if (m_var.av.taskInfo.bTaskExists) {
                                        Log.e("av is already active");
                                        if (ucbGot_iIndex_sharedObj_localAv) {
                                            //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                            //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                            dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                        }
                                        //  2010/12/24
                                        chkResources(hDlgTalk, false);
                                        if (bPrepareSet) {
                                            m_var.av.taskInfo.bPrepare = false;
                                        }
                                        return iErr;
                                    }
                                    bPrepareSet = true;
                                    m_var.av.taskInfo.bPrepare = true;
                                }

                                if (usOp == CONST_imOp_dual_permit) {
                                    ucbSendLocalAv = true;

                                    try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                                        String pName = pProcInfo.cfg.mutexName_syncStartAv;
                                        {
                                            if (0 != syncObj.sync(pName, pMisCnt.hMutex_syncStartAv, "dlgTalk_procTask_transferAvInfo.368")) {
                                                if (ucbGot_iIndex_sharedObj_localAv) {
                                                    //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                    //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                    dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                                }
                                                //  2010/12/24
                                                chkResources(hDlgTalk, false);
                                                if (bPrepareSet) {
                                                    m_var.av.taskInfo.bPrepare = false;
                                                }
                                                return iErr;
                                            }
                                        }
                                        //
                                        if  (  true  )  {
                                            //  这里检查一下，看是否前面sharedObj没有释放
                                            qmcSharedObj.QY_SHARED_OBJ  tmp_pSharedObj  =  getSharedObjByIndex(  pProcInfo,0);
                                            if  (  tmp_pSharedObj.bUsed)  {
                                                str  =  "dlgTalk_procTask_transferAvInfo error: sharedObj[0] already true. before getNewSharedObj";
                                                showInfo_open.qyFuncs.showInfo_open(str);
                                                Log.e("qy",str);
                                            }
                                        }

                                        //
                                        Triplet<Integer, Integer, Boolean> trip;
                                        //iIndex_sharedObj,iIndex_sharedObjUsr,bFirstUsr
                                        if (null == (trip = sharedObjUsr.qyFuncs.getNewSharedObjUsr_localAv(pProcInfo, hDlgTalk, CONST_qyWndContentType_talker))) {
                                            //
                                            str  =  "dlgTalk_procTask_transferAvInfo failed: getNewSharedObjUsr failed";
                                            showInfo_open.qyFuncs.showInfo_open(str);
                                            Log.e("qy",str);
                                            //
                                            if (ucbGot_iIndex_sharedObj_localAv) {
                                                //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                            }
                                            //  2010/12/24
                                            chkResources(hDlgTalk, false);
                                            if (bPrepareSet) {
                                                m_var.av.taskInfo.bPrepare = false;
                                            }
                                            return iErr;
                                        }
                                        iIndex_sharedObj = trip.getValue0();
                                        iIndex_sharedObjUsr = trip.getValue1();
                                        bFirstUsr = trip.getValue2();

                                        //
                                        str  =  "dlgTalk_procTask_transferAvInfo: getNewSharedObj returns "  +  iIndex_sharedObj;
                                        showInfo_open.qyFuncs.showInfo_open(str);

                                        //
                                        if (null == (pSharedObj = getSharedObjByIndex(pProcInfo, iIndex_sharedObj))) {
                                            if (ucbGot_iIndex_sharedObj_localAv) {
                                                //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                            }
                                            //  2010/12/24
                                            chkResources(hDlgTalk, false);
                                            if (bPrepareSet) {
                                                m_var.av.taskInfo.bPrepare = false;
                                            }
                                            return iErr;
                                        }
                                        m_var.av.ucbGot_iIndex_sharedObj_localAv = ucbGot_iIndex_sharedObj_localAv = true;
                                        m_var.av.iIndex_sharedObj_localAv = iIndex_sharedObj;
                                        m_var.av.iIndex_usr_localAv = iIndex_sharedObjUsr;
                                        //
                                        pSharedObjUsr = qmcVideoFunc.qyFuncs.getSharedObjUsr(pSharedObj, m_var.av.iIndex_usr_localAv);
                                        if (null == pSharedObjUsr) {
                                            Log.e("null == pSharedObjUsr");
                                            if (ucbGot_iIndex_sharedObj_localAv) {
                                                //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                            }
                                            //  2010/12/24
                                            chkResources(hDlgTalk, false);
                                            if (bPrepareSet) {
                                                m_var.av.taskInfo.bPrepare = false;
                                            }
                                            return iErr;
                                        }
                                        //
                                        if (bFirstUsr) {
                                            int iIndex_capBmp = -1;
                                            qmcSharedObj.CAP_procInfo_video pCapBmp = null;
                                            int iIndex_capAudio = -1;
                                            qmcSharedObj.CAP_procInfo_audioCommon pCapAudio = null;
                                            int iIndex_shmCmd = -1;

                                            //
                                            pProcInfo.pOutputQBuf2_local_a.emptyQ2();
                                            pProcInfo.pOutputQBuf2_local_v.emptyQ2();
                                            if  (  pProcInfo.bUse100k) {
                                                qmCommFunc_mis.MIS_CHANNEL  pChannel  =  getChannelByType(pMisCnt,CONST_channelType_media);
                                                if  (  pChannel!=null){
                                                    pChannel.outputQBuf2.emptyQ2();
                                                }
                                            }


                                            //
                                            iIndex_capBmp = qmcVideoCapture_isCli.qyFuncs.newCapProcInfoBmpIndex(pProcInfo, iIndex_sharedObj);
                                            pCapBmp = (qmcSharedObj.CAP_procInfo_video) qmcSharedObj.qyFuncs.getCapBmpBySth(pProcInfo, iIndex_capBmp, 0);
                                            if (null == pCapBmp) {
                                                if (ucbGot_iIndex_sharedObj_localAv) {
                                                    //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                    //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                    dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                                }
                                                //  2010/12/24
                                                chkResources(hDlgTalk, false);
                                                if (bPrepareSet) {
                                                    m_var.av.taskInfo.bPrepare = false;
                                                }
                                                return iErr;
                                            }
                                            pCapBmp.uiType = CONST_capType_av;
                                            pCapBmp.iIndex_sharedObj = iIndex_sharedObj;
                                            pCapBmp.uiTranNo_sharedObj = pSharedObj.var.uiTranNo;
                                            pCapBmp.compressVideo.debugInfo.tStart = tmpFunc.qyFuncs.mytime();
                                            pCapBmp.compressVideo.debugInfo.nStep = 1;
                                            //
                                            iIndex_capAudio = newCapProcInfoAudioIndex(pProcInfo, iIndex_sharedObj);
                                            pCapAudio = qmcSharedObj.qyFuncs.getCapAudioBySth(pProcInfo, iIndex_capAudio, 0);
                                            if (null == pCapAudio) {
                                                Log.e("null == pCapAudio");
                                                if (ucbGot_iIndex_sharedObj_localAv) {
                                                    //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                    //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                    dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                                }
                                                //  2010/12/24
                                                chkResources(hDlgTalk, false);
                                                if (bPrepareSet) {
                                                    m_var.av.taskInfo.bPrepare = false;
                                                }
                                                return iErr;
                                            }
                                            pCapAudio.uiType = CONST_capType_av;
                                            pCapAudio.iIndex_sharedObj = iIndex_sharedObj;
                                            pCapAudio.uiTranNo_sharedObj = pSharedObj.var.uiTranNo;

                                            pSharedObj.var.iIndex_capBmp = iIndex_capBmp;
                                            pSharedObj.var.iIndex_capAudio = iIndex_capAudio;
                                            pSharedObj.var.iIndex_shmCmd = iIndex_shmCmd;
                                            {
                                                //  老的工作模式. 2013/03/19
                                                qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pAudioCompressor = avCompressor_reply.audio;
                                                //  2013/08/03
                                                //
                                                String capDevDesc = "";

                                                //  2008/4/17,
                                                if (0 == startLocalAudioRecorder(pProcInfo.cfg.policy.audio.uiRecordType, null, capDevDesc, iIndex_sharedObj, pAudioCompressor)) {

                                                }

                                                qnmCommProc_mis.VIDEO_COMPRESSOR_CFG videoCompressor = avCompressor_reply.video;
                                                if (0 != transferAvInfo.confCfg.conf.usMaxFps_toShareBmp_conf)
                                                    videoCompressor.common.head.usMaxFps_toShareBmp = (short) Math.min(videoCompressor.common.head.usMaxFps_toShareBmp, transferAvInfo.confCfg.conf.usMaxFps_toShareBmp_conf);
                                                //  2014/08/15
                                                if (0 != transferAvInfo.confCfg.conf.iWidth_pic_conf)
                                                    videoCompressor.common.head.iWidth_pic = Math.min(videoCompressor.common.head.iWidth_pic, transferAvInfo.confCfg.conf.iWidth_pic_conf);
                                                if (0 != transferAvInfo.confCfg.conf.iHeight_pic_conf)
                                                    videoCompressor.common.head.iHeight_pic = Math.min(videoCompressor.common.head.iHeight_pic, transferAvInfo.confCfg.conf.iHeight_pic_conf);

                                                //
                                                if  (  false  ) {
                                                    if (transferAvInfo.confCfg.hgInfo.bFlow512k) {
                                                        //
                                                        videoCompressor.common.head.iAvgBitrate = 422000;// 412000;// 512000;
                                                    } else {
                                                        videoCompressor.common.head.iAvgBitrate = 1300000;
                                                    }
                                                }

                                                if (0 != startQThreadToShareAv.qyFuncs.newstartQThreadToShareAv(pProcInfo, iIndex_sharedObj, false)) {
                                                    Log.e("qyFuncs.newstartQThreadToShareAv failed");
                                                    if (ucbGot_iIndex_sharedObj_localAv) {
                                                        //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                        //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                        dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                                    }
                                                    //  2010/12/24
                                                    chkResources(hDlgTalk, false);
                                                    if (bPrepareSet) {
                                                        m_var.av.taskInfo.bPrepare = false;
                                                    }
                                                    return iErr;
                                                }

                                                if (!pProcInfo.av.localAv.videoCaptureProcInfo.bCapDevConnected) {

                                                    if (pProcInfo.cfg.policy.video.uiCamCapType == qmcSharedObj.Consts.CONST_camCapType_rtsp) {       //  2014/02/23

                                                    } else {
                                                        String displayName = "";
                                                        int uiCamCapType = pProcInfo.cfg.policy.video.uiCamCapType;
                                                        videoCompressor.shared_eglContext = m_var.m_share_eglContext;
                                                        int tmpiRet = qmcVideoCapture.qyFuncs.openLocalVideo(pProcInfo, uiCamCapType, displayName, hDlgTalk, CONST_qyWndContentType_talker, videoCompressor, iIndex_sharedObj);
                                                        if (0 == tmpiRet) {
                                                            //  m_var.av.ucbLocalVideoOpen  =  true;
                                                        }
                                                    }
                                                }
                                                if (pProcInfo.av.localAv.videoCaptureProcInfo.bCapDevConnected) {
                                                    pProcInfo.av.localAv.videoCaptureProcInfo.curhWnd = hDlgTalk;
                                                    //
                                                    //  2014/09/23. 建立多流
                                                    level = 0;
                                                    if (0 != doCmd_startAvCall.qyFuncs_Droid.makeSlaveStream(hDlgTalk, iIndex_sharedObj, level)) {
                                                        Log.e("0 != makeSlaveStream(hDlgTalk, iIndex_sharedObj, level)");
                                                        if (ucbGot_iIndex_sharedObj_localAv) {
                                                            //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                            //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                            dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                                        }
                                                        //  2010/12/24
                                                        chkResources(hDlgTalk, false);
                                                        if (bPrepareSet) {
                                                            m_var.av.taskInfo.bPrepare = false;
                                                        }
                                                        return iErr;
                                                    }
                                                    qmcVideoCapture.qyFuncs.startCapture(hDlgTalk, CONST_qyWndContentType_talker, pProcInfo.av.localAv.videoCaptureProcInfo);
                                                }
                                            }
                                        }

                                        //  2017/09/17
                                        dlgTalk_init_respInfo(hMgr, pMgrVar, byte2bool(transferAvInfo.confCfg.ucbVideoConference));

                                        //  2010/09/04
                                        qmcSharedObj.COMPRESS_AUDIO pCompressAudio = pProcInfo.av.localAv.recordSoundProcInfo.compressAudio;
                                        qmcSharedObj.COMPRESS_VIDEO pCompressVideo = pProcInfo.av.localAv.videoCaptureProcInfo.compressVideo;

                                        if (0 != pCompressAudio.uiTranNo_openAvDev_org) {
                                            pSharedObjUsr.uiTranNo_openAvDev_a = (int) (pCompressAudio.uiTranNo_openAvDev_org + iIndex_sharedObjUsr);
                                        }
                                        if (0 != pCompressVideo.uiTranNo_openAvDev_org) {
                                            pSharedObjUsr.uiTranNo_openAvDev_v = (int) (pCompressVideo.uiTranNo_openAvDev_org + iIndex_sharedObjUsr);
                                        }
                                    }
                                }
                            }

                            if (null == pMsgTask) {
                                if (ucbGot_iIndex_sharedObj_localAv) {
                                    //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                    //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                    dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                }
                                //  2010/12/24
                                chkResources(hDlgTalk, false);
                                if (bPrepareSet) {
                                    m_var.av.taskInfo.bPrepare = false;
                                }
                                return iErr;
                            }

                            //
                            iStatus = CONST_imTaskStatus_waitToRecv;
                            iStatus_old = pMsgTask.iStatus;
                            //
                            if (0 != myDb.QM_dbFuncs.pf_updateTaskStatus.call(pDb, iStatus, rcd.id)) {
                                if (ucbGot_iIndex_sharedObj_localAv) {
                                    //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                    //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                    dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                }
                                //  2010/12/24
                                chkResources(hDlgTalk, false);
                                if (bPrepareSet) {
                                    m_var.av.taskInfo.bPrepare = false;
                                }
                                return iErr;
                            }
                            pMsgTask.iStatus = iStatus;

                            if (rcd.uiType != CONST_imTaskType_transferAvInfo) {
                                boolean bDo_avProxy = false;
                                if (m_var.addr.uiObjType == CONST_objType_imGrp) {
                                    if (pMsgTask.data.route.idInfo_to.ui64Id == pMisCnt.idInfo.ui64Id
                                            && transferAvInfo.idInfo_logicalPeer.ui64Id == m_var.addr.idInfo.ui64Id) {
                                        if (m_var.av.taskInfo.bTaskExists
                                                && m_var.av.taskInfo.ucbStarter) {
                                            if (0 != m_var.av.taskInfo.mosaicMaker.video.resObj.uiObjType
                                                    || 0 != m_var.av.taskInfo.mosaicMaker.resource.resObj.uiObjType) {
                                                bDo_avProxy = true;
                                            }
                                        }

                                    }
                                }
                            }

                            if (rcd.uiContentType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {

                                long tStartTran = tmpFunc.qyFuncs.mytime();
                                int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
                                taskAv.TASK_transferAv task = new taskAv.TASK_transferAv();
                                //
                                task.m_index_taskInfo = iIndex_taskInfo;
                                pTaskInfo.var.m_bReplyTask = true;
                                //
                                //fixed (qnmCommProc_mis.MIS_MSG_TASK pMsg =task.msgU.task;
                                {
                                    ;
                                    qnmCommProc_mis.TRANSFER_AV_replyInfo transferAvReplyInfo = new qnmCommProc_mis.TRANSFER_AV_replyInfo();
                                    taskAv.PROC_TASK_AV transferAvReplyInfo_myTask = task.myTask;

                                    //
                                    //memset(transferAvReplyInfo, 0, sizeof(transferAvReplyInfo));
                                    //qyFuncs.mymemset((byte[])transferAvReplyInfo, 0, sizeof(qnmCommProc_mis.TRANSFER_AV_replyInfo));
                                    transferAvReplyInfo.uiType = CONST_imCommType_transferAvReplyInfo;
                                    //  2016/04/18
                                    transferAvReplyInfo.tmp_usSubtype = CONST_transferAvReplyInfo_subtype_task;
                                    //
                                    transferAvReplyInfo.usOp = CONST_imOp_recv_accept;
                                    transferAvReplyInfo.tStartTime_org = pMsgTask.tStartTime;
                                    transferAvReplyInfo.uiTranNo_org = pMsgTask.uiTranNo;
                                    //
                                    if (ucbSendLocalAv) {   //
                                        qmcSharedObj.COMPRESS_VIDEO pCompressVideo = pProcInfo.av.localAv.videoCaptureProcInfo.compressVideo;
                                        qmcSharedObj.COMPRESS_AUDIO pCompressAudio = pProcInfo.av.localAv.recordSoundProcInfo.compressAudio;

                                        //  2009/04/19
                                        if (0 != transferAvInfo.confCfg.conf.usMaxFps_toShareBmp_conf) {
                                            //  2009/06/04
                                            transferAvReplyInfo_myTask.video.usMaxFps_toShareBmp_conf = pCompressVideo.compressor.common.head.usMaxFps_toShareBmp;
                                        }

                                        transferAvReplyInfo.tranInfo.video.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_v;
                                        transferAvReplyInfo.tranInfo.video.compressor = pCompressVideo.compressor.common.head;
                                        transferAvReplyInfo.tranInfo.video.vh_compress = pCompressVideo.vh_compress;
                                        transferAvReplyInfo.tranInfo.video.vh_stream = pCompressVideo.vh_stream;
                                        transferAvReplyInfo.tranInfo.video.vh_decompress.bih = pCompressVideo.vh_decompress.bih;
                                        transferAvReplyInfo.tranInfo.audio.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_a;
                                        transferAvReplyInfo.tranInfo.audio.compressor = pCompressAudio.compressor.common.head;
                                        transferAvReplyInfo.tranInfo.audio.ah_compress = pCompressAudio.ah_compress;
                                        transferAvReplyInfo.tranInfo.audio.ah_stream = pCompressAudio.ah_stream;
                                        transferAvReplyInfo.tranInfo.audio.ah_decompress = pCompressAudio.ah_decompress;
                                    }

                                    //  2017/09/08
                                    transferAvReplyInfo_myTask.uiTaskType = rcd.uiType;
                                    transferAvReplyInfo_myTask.ucbSendLocalAv = ucbSendLocalAv;
                                    transferAvReplyInfo_myTask.iIndex_sharedObj = iIndex_sharedObj;     //  sharedObj
                                    transferAvReplyInfo_myTask.iIndex_taskInfo = iIndex_taskInfo;
                                    transferAvReplyInfo_myTask.mem0_from.idInfo.ui64Id = pMsgTask.data.route.idInfo_from.ui64Id;
                                    transferAvReplyInfo_myTask.mem0_from.tStartTime_task = pMsgTask.tStartTime;
                                    //
                                    if (rcd.uiType == CONST_imTaskType_transferAvInfo) {                           //  2009/06/01
                                        if (0 != transferAvInfo.confCfg.ucbVideoConference) {
                                            pTc.ucbVideoConference = true;                           //  2009/05/30
                                            transferAvReplyInfo_myTask.mem0_from.ucbVideoConferenceStarter = true;      //  2009/04/02
                                        }
                                    }
                                    //  2014/09/23
                                    transferAvReplyInfo_myTask.mem0_from.shClientVer = transferAvInfo.ass.tranInfo.head.shClientVer;
                                    transferAvReplyInfo_myTask.mem0_from.video.tv_recvd = transferAvInfo.ass.tranInfo.video;
                                    transferAvReplyInfo_myTask.mem0_from.audio.ta_recvd = transferAvInfo.ass.tranInfo.audio;

                                    int uiTranNo_openAvDev_confMosaic_video = 0;                              //  Video
                                    int uiTranNo_openAvDev_confMosaic_resource = 0;                           //  Video

                                    if (transferAvReplyInfo_myTask.bVideoConference()) {
                                        if (0 != transferAvInfo.confMosaicTranInfo_video.resObj.uiObjType) {
                                            uiTranNo_openAvDev_confMosaic_video = transferAvInfo.confMosaicTranInfo_video.video.uiTranNo_openAvDev;
                                        }
                                        if (0 != transferAvInfo.confMosaicTranInfo_resource.resObj.uiObjType) {
                                            uiTranNo_openAvDev_confMosaic_resource = transferAvInfo.confMosaicTranInfo_resource.video.uiTranNo_openAvDev;
                                        }
                                    }

                                    //  2014/11/21
                                    if (!transferAvReplyInfo_myTask.bVideoConference()) {
                                        if (transferAvInfo.proxiedTranInfo.idInfo.ui64Id == 0
                                                && 0 != transferAvInfo.proxiedTranInfo.uiObjType) {
                                            transferAvReplyInfo_myTask.receiver.bRecvd_proxiedTranInfo = true;
                                            transferAvReplyInfo_myTask.receiver.proxied_from.idInfo.ui64Id = transferAvInfo.proxiedTranInfo.idInfo.ui64Id;
                                            transferAvReplyInfo_myTask.receiver.proxied_from.shClientVer = transferAvInfo.proxiedTranInfo.ti.head.shClientVer;
                                            transferAvReplyInfo_myTask.receiver.proxied_from.video.tv_recvd = transferAvInfo.proxiedTranInfo.ti.video;
                                            transferAvReplyInfo_myTask.receiver.proxied_from.audio.ta_recvd = transferAvInfo.proxiedTranInfo.ti.audio;
                                        }
                                    }

                                    if (transferAvReplyInfo_myTask.bVideoConference())
                                        videoConference.usCntLimit_mems_from = Math.min((int) 30, (int) MAX_mems_taskAv);
                                    if (videoConference.usCntLimit_mems_from < 2)
                                        videoConference.usCntLimit_mems_from = 2;

                                    if (!transferAvReplyInfo_myTask.bVideoConference()) {
                                        videoConference.usMaxSpeakers = 2;
                                        videoConference.usCntLimit_activeMems_from = (int) (videoConference.usMaxSpeakers + MAX_resourceMems_taskAv_p2p);
                                    } else {   //  2009/05/31
                                        videoConference.usMaxSpeakers = transferAvInfo.confCfg.usMaxSpeakers;
                                        if (0 == videoConference.usMaxSpeakers) {
                                            Log.e("dlgTalk_procTask_transferAvInfo: talker " + m_var.addr.idInfo.ui64Id + " failed: usMaxSpeakers is 0");
                                            if (ucbGot_iIndex_sharedObj_localAv) {
                                                //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                            }
                                            //  2010/12/24
                                            chkResources(hDlgTalk, false);
                                            if (bPrepareSet) {
                                                m_var.av.taskInfo.bPrepare = false;
                                            }
                                            return iErr;
                                        }

                                        videoConference.usCntLimit_activeMems_from = (int) (transferAvInfo.confCfg.usMaxSpeakers + MAX_mosaicMems_taskAv + MAX_resourceMems_taskAv_conf);
                                        if (videoConference.usCntLimit_activeMems_from > MAX_activeMems_from) {
                                            Log.e(("dlgTalk_procTask_transferAvInfo failed: usCntLimit_activeMems_from too big"));
                                            if (ucbGot_iIndex_sharedObj_localAv) {
                                                //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                            }
                                            //  2010/12/24
                                            chkResources(hDlgTalk, false);
                                            if (bPrepareSet) {
                                                m_var.av.taskInfo.bPrepare = false;
                                            }
                                            return iErr;
                                        }
                                    }
                                    transferAvReplyInfo_myTask.hWnd_task = hDlgTalk;        //  2009/09/09

                                    //
                                    lenInBytes = 128;//sizeof(qnmCommProc_mis.TRANSFER_AV_replyInfo);

                                    if (ucbSendLocalAv) {
                                        if (null == pSharedObj || null == pSharedObjUsr) {
                                            if (ucbGot_iIndex_sharedObj_localAv) {
                                                //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                            }
                                            //  2010/12/24
                                            chkResources(hDlgTalk, false);
                                            if (bPrepareSet) {
                                                m_var.av.taskInfo.bPrepare = false;
                                            }
                                            return iErr;
                                        }

                                        pSharedObj.var.ucbNotSendData = (m_var.addr.uiObjType == CONST_objType_imGrp);
                                        if (pParam.av_autoAnswer_av_task_bTaskNeedAutoAnswered)
                                            pSharedObj.var.ucbNotSendData = true;  //  2010/09/07

                                        //  2010/09/03
                                        pSharedObjUsr.iTaskId = pMsgTask.iTaskId;
                                        pSharedObjUsr.idInfo_to.ui64Id = pMsgTask.idInfo_taskSender.ui64Id;
                                        pSharedObjUsr.ucbVideoConference = byte2bool(transferAvInfo.confCfg.ucbVideoConference);    //  2018/11/10

                                    }
                                    //
                                    transferAvReplyInfo_myTask.reply_avTranInfo = transferAvReplyInfo.tranInfo;
                                    pTc.shared_eglContext = m_var.m_share_eglContext;
                                    if (0 != pProcInfo.processQ_media.qPostMsg(task, 0)) {
                                        if (ucbGot_iIndex_sharedObj_localAv) {
                                            //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                            //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                            dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                        }
                                        //  2010/12/24
                                        chkResources(hDlgTalk, false);
                                        if (bPrepareSet) {
                                            m_var.av.taskInfo.bPrepare = false;
                                        }
                                        return iErr;
                                    }
                                    //  2015/09/07
                                    int uiChannelType = CONST_channelType_media;
                                    //  2015/09/07
                                    uiChannelType = 0;
                                    //  2012/04/01
                                    qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = m_var.addr.idInfo;

                                    if (0 != postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, (short) CONST_qyCmd_sendMedia, tStartTran, uiTranNo, (short) 0, pMsgTask.iTaskId, pMsgTask.uiTaskType, transferAvReplyInfo, lenInBytes, idInfo_logicalPeer.ui64Id,
                                            pMsgTask.idInfo_taskSender.ui64Id, pMsgTask.idInfo_taskReceiver.ui64Id,
                                            pMsgTask.idInfo_taskSender.ui64Id, uiChannelType, (qnmCommProc_mis.MIS_MSGU) null, false)) {
                                        if (ucbGot_iIndex_sharedObj_localAv) {
                                            //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                            //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                            dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                        }
                                        //  2010/12/24
                                        chkResources(hDlgTalk, false);
                                        if (bPrepareSet) {
                                            m_var.av.taskInfo.bPrepare = false;
                                        }
                                        return iErr;
                                    }

                                    //  209/02/20
                                    if (rcd.uiType == CONST_imTaskType_transferAvInfo) {
                                        m_var.av.taskInfo.memset0();
                                        m_var.av.taskInfo.bTaskExists = true;
                                        m_var.av.taskInfo.iTaskId = iTaskId;
                                        m_var.av.taskInfo.idInfo_starter.ui64Id = rcd.idInfo_send.ui64Id;
                                        m_var.av.taskInfo.uiInitW = (int) transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biWidth;
                                        m_var.av.taskInfo.uiInitH = (int) transferAvInfo.ass.tranInfo.video.vh_decompress.bih.biHeight;

                                        //
                                        m_var.av.taskInfo.iIndex_taskInfo = iIndex_taskInfo;
                                        if (m_var.av.taskInfo.iIndex_taskInfo < 0) {
                                            Log.d("dlgTalk_procTask_transferAvInfo failed, index_taskInfo <0");
                                            if (ucbGot_iIndex_sharedObj_localAv) {
                                                //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                            }
                                            //  2010/12/24
                                            chkResources(hDlgTalk, false);
                                            if (bPrepareSet) {
                                                m_var.av.taskInfo.bPrepare = false;
                                            }
                                            return iErr;
                                        }

                                        m_var.av.taskInfo.tStartTime_org = rcd.tSendTime;
                                        m_var.av.taskInfo.uiTranNo_org = rcd.uiTranNo;
                                        m_var.av.taskInfo.uiContentType_org = rcd.uiContentType;
                                        m_var.av.taskInfo.local_avStream.tranInfo = transferAvReplyInfo.tranInfo;
                                        m_var.av.taskInfo.ucbVideoConference = transferAvReplyInfo_myTask.bVideoConference();
                                        m_var.av.taskInfo.usConfType = transferAvInfo.confCfg.usConfType;
                                        m_var.av.taskInfo.idInfo_initiator = transferAvInfo.confCfg.idInfo_initiator;
                                        int ii = (int) m_var.av.taskInfo.idInfo_initiator.ui64Id;

                                        if (m_var.addr.uiObjType != CONST_objType_imGrp) {
                                            m_var.av.taskInfo.bShow_localVideo = true;
                                        }

                                        //  2014/11/12
                                        if (m_var.av.taskInfo.ucbVideoConference) {
                                            if (0 != uiTranNo_openAvDev_confMosaic_video || 0 != uiTranNo_openAvDev_confMosaic_resource) {
                                                m_var.av.taskInfo.mosaicTrans.bExists_confMosaic = true;
                                                m_var.av.taskInfo.mosaicTrans.uiTranNo_openAvDev_confMosaic_video = uiTranNo_openAvDev_confMosaic_video;
                                                m_var.av.taskInfo.mosaicTrans.uiTranNo_openAvDev_confMosaic_resource = uiTranNo_openAvDev_confMosaic_resource;
                                            }
                                        }

                                        //  2016/03/26
                                        if (m_var.av.taskInfo.ucbVideoConference) {
                                            //
                                            //  2017/07/06
                                            if (0 != transferAvInfo.confCfg.conf.confCtrl.ucbConfCtrl_starter) {
                                                m_var.av.taskInfo.ucbConfCtrl = true;
                                            }
                                        }

                                        resetTalkerList_mgr(hMgr);

                                        int uiTaskType = rcd.uiType;
                                        if (uiTaskType == CONST_imTaskType_transferAvInfo) {
                                            if (!m_var.av.taskInfo.ucbVideoConference) {
                                                if (0 != addToMsgrs_sendLocalAv.qyFuncs.addToMsgrs_sendLocalAv(pProcInfo, pMisCnt, m_var.addr.idInfo, false, pTaskInfo.var.curRoute_sendLocalAv)) {
                                                    if (ucbGot_iIndex_sharedObj_localAv) {
                                                        //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                                                        //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                                                        dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                                                    }
                                                    //  2010/12/24
                                                    chkResources(hDlgTalk, false);
                                                    if (bPrepareSet) {
                                                        m_var.av.taskInfo.bPrepare = false;
                                                    }
                                                    return iErr;
                                                }
                                            }
                                        }

                                        if (!m_var.av.taskInfo.ucbVideoConference) {       //  2012/05/03. 在一对一接收时,自动将对方加入话筒列表,以便显示.
                                            {
                                                qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[0];
                                                pActiveMem_from.avStream.idInfo.ui64Id = m_var.addr.idInfo.ui64Id;
                                                pActiveMem_from.avStream.obj.tranInfo = transferAvInfo.ass.tranInfo;
                                            }
                                            //  2013/04/10
                                            if (!pParam.av_autoAnswer_av_task_bTaskNeedAutoAnswered) {
                                                qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[1];
                                                pActiveMem_from.avStream.idInfo.ui64Id = pMisCnt.idInfo.ui64Id;
                                            }
                                        }

                                        //  2010/09/07
                                        if (ucbSendLocalAv) {               //
                                            if (!pParam.av_autoAnswer_av_task_bTaskNeedAutoAnswered) {
                                                setCurSharedObjUsr_localAv(pProcInfo, iIndex_sharedObj, hDlgTalk);
                                            }
                                        }

                                        DlgInstantAssistant.qyFuncs_Droid.closeInstantAssistant(hDlgTalk);
                                        dlgTalk_doLayout(hDlgTalk);
                                    }

                                    if (rcd.uiType == CONST_imTaskType_transferAvInfo) {
                                        if (ucbSendLocalAv && 0 != transferAvInfo.confCfg.ucbVideoConference
                                        ) {
                                            qmcCommFunc_mis.ConfMem_confCtrl confCtrl = new qmcCommFunc_mis.ConfMem_confCtrl();
                                            if (pMgrVar.av.taskInfo.bTaskExists
                                                    && pMgrVar.av.taskInfo.idInfo_starter.ui64Id == confCtrl.idInfo_starter.ui64Id
                                                    && pMgrVar.av.taskInfo.tStartTime_org == confCtrl.tStartTime
                                                    && pMgrVar.av.taskInfo.uiTranNo_org == confCtrl.uiTranNo) {
                                                pMgrVar.av.taskInfo.confMem_confCtrl = confCtrl;
                                            }
                                            //
                                            if (pMgrVar.av.taskInfo.confMem_confCtrl.ucbPermitToSpeak) {
                                                //
                                                if (pProcInfo.cfg.policy.avRules.ucbLetConfMgrSetMicOn) {

                                                    //  2017/07/13
                                                    setCurSharedObjUsr_localAv(pProcInfo, pMgrVar.av.iIndex_sharedObj_localAv, hMgr);

                                                    dlgTalk_requestToSpeak(hMgr, true);
                                                }
                                            }
                                        }
                                        //
                                        setProcessingAvTask(pMgrVar.av.taskInfo.iIndex_taskInfo, pMgrVar.av.taskInfo.iTaskId);
                                    } else if (rcd.uiType == CONST_imTaskType_remoteAssist) {
                                    }
                                }
                            }

                            //
                        }
                        break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    //
                    //String str;
                    str = "dlgTalk_procTask_transferAvInfo exception:" + e.getMessage();
                    Log.d(str);
                    showInfo_open.qyFuncs.showInfo_open(str);
                    //
                    if (ucbGot_iIndex_sharedObj_localAv) {
                        //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                        //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                        dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                    }
                    //  2010/12/24
                    chkResources(hDlgTalk, false);
                    if (bPrepareSet) {
                        m_var.av.taskInfo.bPrepare = false;
                    }
                    return iErr;
                }

                //
                refreshTalkToInfo_mgr(hDlgTalk);

                iErr = 0;

            } while (false);


            //
            if (0 != iErr) {
                if (ucbGot_iIndex_sharedObj_localAv) {
                    //  
                    //freeSharedObjUsr_localAv(  m_var.av.iIndex_sharedObj_localAv,  hDlgTalk  );
                    //m_var.av.ucbGot_iIndex_sharedObj_localAv  =  false;
                    dlgTalk_closeTaskAv(hDlgTalk, m_var, "dlgTalk_procTask_transferAvInfo.failed");
                }
            }

            //  2010/12/24	
            chkResources(hDlgTalk, false);
            if (bPrepareSet) {
                m_var.av.taskInfo.bPrepare = false;

            }

            //
            test.testCls_Droid.testPrintTasks(true,  test.tag_100k,"dlgTalk_procTask_av");
            //
            Log.d("qy","dlgTalk_procTask_transferAvInfo: leaves with "  +  iErr);

            //
            return iErr;

        }

    }
}

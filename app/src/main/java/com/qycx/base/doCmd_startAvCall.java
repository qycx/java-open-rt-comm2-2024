package com.qycx.base;


import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.DlgSelectAvCompressor.qyFuncs_Droid.bNoPrompt_selectAvCompressor;
import static com.qycx.base.DlgSelectAvCompressor.qyFuncs_Droid.getTalkerPolicy;
import static com.qycx.base.audioRecorder_dsound.qyFuncs_Droid.startLocalAudioRecorder;
import static com.qycx.base.confReq.qyFuncs_Droid.sendConfReq;
import static com.qycx.base.ctxImgProcess.qyFuncs.resetChromaKey;
import static com.qycx.base.dlgPolicyIsClient.qyFuncs_Droid.myGetAvCompressorCfg;
import static com.qycx.base.getPolicyIsClient.qyFuncs_Droid.getVideoConferenceCfg;
import static com.qycx.base.getTalkerDesc.Consts.cntof_desc;
import static com.qycx.base.mcThreadProc_bgWall.qyFuncs_Droid.exitTalkerThread;
import static com.qycx.base.mcThreadProc_bgWall.qyFuncs_Droid.initTalkerThread;
import static com.qycx.base.policyIsClientFunc.qyFuncs_Droid.getPolicyAvLevel;
import static com.qycx.base.policyIsClientFunc.qyFuncs_Droid.get_conf_iFourcc;
import static com.qycx.base.policyIsClientFunc.qyFuncs_Droid.get_conf_usMaxSpeakers;
import static com.qycx.base.qmcSharedObj.Consts.CONST_capType_av;
import static com.qycx.base.qmcSharedObj.Consts.CONST_capUsage_3dConf;
import static com.qycx.base.qmcSharedObj.Consts.CONST_capUsage_conf;
import static com.qycx.base.qmcSharedObj.Consts.CONST_recordType_android;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapAudioBySth;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcVideoCapture.qyFuncs.openLocalVideo;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.newTaskId;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_transferAvInfo;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_viewDynBmp;
import static com.qycx.base.qyDefs.Consts.CONST_objType_imGrp;
import static com.qycx.base.qyDefs.Consts.CONST_objType_tmpGrp;
import static com.qycx.base.qyWndContentType.Consts.CONST_qyWndContentType_talker;
import static com.qycx.base.qyWndContentType.Consts.CONST_talkUsage_doConf;
import static com.qycx.base.refreshTalkerList.qyFuncs.resetTalkerList_mgr;
import static com.qycx.base.startQThreadToShareAv.qyFuncs.newstartQThreadToShareAv;

import com.qycx.Activity_dlgTalk;
import com.qycx.MainActivity;
import com.qycx.base.addToMsgrs_sendLocalAv;
import com.qycx.base.util.Log;
import com.qycx.base.util.javatuples.Triplet;

public class doCmd_startAvCall {
    public static class qyFuncs_Droid {


        public static int confStarter_fillGrpMems(Object hDlgTalk, dlgTalkProc.DLG_TALK_var pm_var) {
            int iErr = -1;
            dlgTalkProc.DLG_TALK_var m_var = pm_var;


            iErr = 0;

            return iErr;
        }


        public static int doCmd_startAvCall(Object hParent, Activity_dlgTalk hCurTalk, int level, boolean b3D, boolean ucbAvConsole, dlgTalkPublic.PARAM_startAvCall pParam) {
            int iErr = -1;

            Object hMgr = null;
            dlgTalkProc.DLG_TALK_var pMgrVar = null;

            dlgTalkProc.DLG_TALK_var pCurVar = null;
            //
            boolean bTaskStarted = false;          //  2014/10/31

            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;
            funcsForIsCliHelp.FUNCS_for_isCliHelp pFuncs = get_funcs_for_isCliHelp.qyFuncs.QY_GET_FUNCS_for_isCliHelp(pProcInfo);
            if (null == pFuncs) return -1;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            dlgTalkPublic.PARAM_startAvCall param = new dlgTalkPublic.PARAM_startAvCall();
            int usConfType = 0;
            boolean bNoPrompt = false;
            int iCapUsage = 0;
            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar_cur = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar_mgr;

                qnmCommProc_mis.VIDEO_CONFERENCE_CFG videoConferenceCfg = new qnmCommProc_mis.VIDEO_CONFERENCE_CFG();
                boolean bVideoConference = false;
                int iIndex_sharedObj = -1;
                int iIndex_sharedObjUsr = -1;
                //
                qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;


                boolean bFirstUsr = false;
                int iTaskId = 0;                //  2014/10/29


                try(qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj())
                {                       //  2016/03/15
                    pCurVar = hCurTalk.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar_cur.getVar(hCurTalk);
                    if (null == pCurVar) return -1;
                    hMgr = hCurTalk;
                    pMgrVar = pCurVar;

                    if (pMgrVar.addr.uiObjType == CONST_objType_imGrp) {
                        //
                        if (!pQyMc.appParams.canStartConf) {
                            bNoPrompt = bNoPrompt_selectAvCompressor(CONST_capType_av, 0);
                            if (pQyMc.appParams.bNoPrompt) {
                                bNoPrompt = true;
                            }
                            b3D = true;

                            int uiCapType = CONST_capType_av;
                            int uiSubCapType = 0;
                            iCapUsage = CONST_capUsage_3dConf;
                            //
                            int iResId = 0;// gAvParams.iResId_policyAvLevelTable;

                            if (qyWmComm.Consts.CONST_do_1080P_AV_Call == level)
                                level = getPolicyAvLevel.call(pQyMc, uiCapType, uiSubCapType, iCapUsage);
                            int conf_iFourcc = get_conf_iFourcc();
                            int usMaxSpeakers = (int) get_conf_usMaxSpeakers();

                            sendConfReq(null, hMgr, pMgrVar.addr.idInfo, (short) 0, (short) level, conf_iFourcc, (short) usMaxSpeakers);

                            return 0;
                        }


                    }

                    //  2015/07/29

                    //
                    if (null == pMisCnt) {
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }
                    //
                    boolean ucbNotUseAvCall = false;

                    if (pMgrVar.av.taskInfo.bTaskExists) {
                        Log.d( ("doCmd_startAvCall failed, av.bTaskExists is true"));
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }

                    //  2017/08/25
                    usConfType = 0;

                    bNoPrompt = bNoPrompt_selectAvCompressor(CONST_capType_av, 0);


                    String pName = pProcInfo.cfg.mutexName_syncStartAv;
                    {
                        if (0 != syncObj.sync(pName, pMisCnt.hMutex_syncStartAv,"doCmd_startAvCall.158"))
                        {
                            if (pMgrVar != null)
                            {
                                guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                            }
                            return iErr;
                        }
                    }

                    //  2014/12/24
                    if (pMgrVar.av.taskInfo.bTaskExists) {
                        Log.i( "av is started already");
                        syncObj.Dispose();
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }

                    //assert(!pMgrVar.av.taskInfo.bTaskExists);
                    //memset(pMgrVar.av.taskInfo, 0, sizeof(pMgrVar.av.taskInfo));
                    pMgrVar.av.taskInfo.memset0();
                    pMgrVar.av.taskInfo.bTaskExists = true;
                    //
                    bTaskStarted = true;

                    //  2016/03/06
                    exitTalkerThread(pProcInfo, pMgrVar.talkerThreadInfo);
                    if (0 != initTalkerThread(pProcInfo, hMgr, pMgrVar, pMgrVar.talkerThreadInfo)) {
                        syncObj.Dispose();
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }

                    Triplet<Integer, Integer, Boolean> trip;
                    if (null == (trip = sharedObjUsr.qyFuncs.getNewSharedObjUsr_localAv(pProcInfo, hMgr, CONST_qyWndContentType_talker))) {
                        syncObj.Dispose();
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }
                    iIndex_sharedObj = trip.getValue0();
                    iIndex_sharedObjUsr = trip.getValue1();
                    bFirstUsr = trip.getValue2();
                    pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, iIndex_sharedObj);
                    if (null == pSharedObj) {
                        Log.d( ("startAvCall failed: getSharedObj return  null"));
                        syncObj.Dispose();
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }
                    //
                    qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = qmcVideoFunc.qyFuncs.getSharedObjUsr(pSharedObj, iIndex_sharedObjUsr);
                    if (null == pSharedObjUsr) {
                        syncObj.Dispose();
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }
                    //
                    pMgrVar.av.ucbGot_iIndex_sharedObj_localAv = true;
                    pMgrVar.av.iIndex_sharedObj_localAv = iIndex_sharedObj;
                    pMgrVar.av.iIndex_usr_localAv = iIndex_sharedObjUsr;

                    //  2011/08/07
                    qnmCommProc_mis.AV_COMPRESSOR_CFG avCompressor_req = new qnmCommProc_mis.AV_COMPRESSOR_CFG();
                    if (0 != myGetAvCompressorCfg(CONST_capType_av, 0, 0, level, avCompressor_req)) {
                        syncObj.Dispose();
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }
                    //

                    //
                    qnmCommProc_mis.AV_COMPRESSOR_CFG avCompressor_conf = new qnmCommProc_mis.AV_COMPRESSOR_CFG();
                    //  2014/12/15
                    iCapUsage = b3D ? CONST_capUsage_3dConf : CONST_capUsage_conf;
                    //
                    if (0 != myGetAvCompressorCfg(CONST_capType_av, 0, iCapUsage, level, avCompressor_conf)) {
                        syncObj.Dispose();
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }

                    //  2014/10/07
                    qyGuiCommProc.TALKER_policy policy = new qyGuiCommProc.TALKER_policy();
                    getTalkerPolicy(pMgrVar.addr.idInfo, policy);

                    switch (pMgrVar.addr.uiObjType) {
                        case CONST_objType_imGrp:
                            if (0 != getVideoConferenceCfg(null, "", "", pMgrVar.addr.idInfo, videoConferenceCfg)) {
                                syncObj.Dispose();
                                if (pMgrVar != null)
                                {
                                    guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                                }
                                return iErr;
                            }
                            //  2014/12/05
                            //
                            bVideoConference = true;

                            //
                            if (policy.ucbConfCtrl_starter) {
                                pMgrVar.av.taskInfo.ucbConfCtrl = true;
                                //
                                //resetGrpConfCtrl(hMgr,pShadowMgr->confCtrl.grpConfCtrl);
                            }
                            break;
                        default:
                            break;
                    }


                    //
                    if (bFirstUsr) {
                        qmcSharedObj.CAP_procInfo_recordSound pCapAudio = null;         //  2013/03/19
                        qmcSharedObj.CAP_procInfo_video pCapBmp = null;

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
                        pCapAudio = (qmcSharedObj.CAP_procInfo_recordSound) getCapAudioBySth(pProcInfo, pSharedObj.var.iIndex_capAudio, 0);
                        if (null == pCapAudio) {
                            syncObj.Dispose();
                            if (pMgrVar != null)
                            {
                                guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                            }
                            return iErr;
                        }
                        pCapBmp = (qmcSharedObj.CAP_procInfo_video) qmcSharedObj.qyFuncs.getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);
                        if (null == pCapBmp) {
                            syncObj.Dispose();
                            if (pMgrVar != null)
                            {
                                guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                            }
                            return iErr;
                        }

                        //
                        pCapAudio.iIndex_sharedObj = iIndex_sharedObj;
                        pCapAudio.uiTranNo_sharedObj = pSharedObj.var.uiTranNo;

                        //
                        pCapBmp.iIndex_sharedObj = iIndex_sharedObj;
                        pCapBmp.uiTranNo_sharedObj = pSharedObj.var.uiTranNo;


                        //  2013/03/19. 这里要清理一下
                        pCapAudio.uiType = 0;
                        pCapBmp.uiType = 0;

                        //  2013/09/20
                        resetChromaKey();

                        {     //  老的工作模式. 2013/03/19

                            //
                            if (true) {

                                int uiRecordType = CONST_recordType_android;//pProcInfo.cfg.policy.audio.uiRecordType
                                if (0 == startLocalAudioRecorder(uiRecordType, null, "", iIndex_sharedObj, avCompressor_req.audio)) {
                                    //  pMgrVar.av.ucbLocalAudioRecorderStarted  =  true;
                                }

                            } else {

                            }

                            //
                            //if (pQyMc.iCustomId != CONST_qyCustomId_hzj								&& 1)
                            if (true) {

                                //  2011/07/27
                                if (0 != newstartQThreadToShareAv(pProcInfo, iIndex_sharedObj, false)) {
                                    {
                                        syncObj.Dispose();
                                        if (pMgrVar != null)
                                        {
                                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                                        }
                                        return iErr;
                                    }
                                }

                                //
                                if (!pProcInfo.av.localAv.videoCaptureProcInfo.bCapDevConnected) {

                                    {

                                        //TCHAR displayName[MAX_monikerDisplayNameLen + 1] = "";
                                        String displayName = "";

                                        //
                                        avCompressor_req.video.shared_eglContext = pMgrVar.m_share_eglContext;

                                        //if (getMonikerDisplayName(getMoniker_v(pMgrVar.pCapStuff, pMgrVar.iMenuId_selectedVideo), displayName, mycountof(displayName))) displayName[0] = 0;
                                        int uiCamCapType = pProcInfo.cfg.policy.video.uiCamCapType;
                                        if (0 == openLocalVideo(pProcInfo, uiCamCapType, displayName, hMgr, CONST_qyWndContentType_talker, avCompressor_req.video, iIndex_sharedObj)) {
                                            //  pMgrVar.av.u.av.uc.ucbLocalVideoOpen  =  true;
                                        }

                                    }
                                }
                                if (pProcInfo.av.localAv.videoCaptureProcInfo.bCapDevConnected) {
                                    pProcInfo.av.localAv.videoCaptureProcInfo.curhWnd = hMgr;

                                    //
                                    //  2014/09/23. 建立多流
                                    level = 0;
                                    if (0 != makeSlaveStream(hMgr, iIndex_sharedObj, level)) {
                                        syncObj.Dispose();
                                        if (pMgrVar != null)
                                        {
                                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                                        }
                                        return iErr;
                                    }

                                }
                            }

                        }
                    }

                    //  2014/10/29
                    {
                        Object pDb = null;
                        qnmDb.CQnmDb db = new qnmDb.CQnmDb();
                        {
                            if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) {
                                syncObj.Dispose();
                                if (pMgrVar != null)
                                {
                                    guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                                }
                                return iErr;
                            }
                            pDb = db.m_pDbMem.pDb;

                            iTaskId = newTaskId(pDb, pQyMc.cfg.db.iDbType);
                            pMgrVar.av.taskInfo.iTaskId = iTaskId;
                        }
                    }
                    //  2014/10/30
                    if (pMgrVar.addr.uiObjType == CONST_objType_imGrp) {
                        if (b3D) {
                        }
                    }

                    //  2016/03/14
                    qmcCommFunc_mis.P_sendAvInfo_taskInfo pst = new qmcCommFunc_mis.P_sendAvInfo_taskInfo();
                    //qyFuncs.mymemset((byte[])pst, 0, sizeof(P_sendAvInfo_taskInfo));
                    //  2017/08/02
                    pst.i.ucbVideoConference = (pMgrVar.addr.uiObjType == CONST_objType_imGrp);
                    //  2017/07/17
                    pst.i.ucbAvConsole = ucbAvConsole;
                    //  2017/08/25
                    pst.i.usConfType = usConfType;
                    //
                    pst.i.shared_eglContext = pMgrVar.m_share_eglContext;

                    switch (pMgrVar.addr.uiObjType) {
                        case CONST_objType_imGrp:
                            qmcMosaic.QMC_mosaic_maker pMosaic = pMgrVar.av.taskInfo.mosaicMaker;
                            if (0 != procSendFunc.qyFuncs.procSendAvInfo_conf(mainActivity.var_common.ctxCaller, pMisCnt, hMgr, true, CONST_imTaskType_transferAvInfo, videoConferenceCfg, policy, avCompressor_conf, pMosaic, pProcInfo.av.localAv.videoCaptureProcInfo.compressVideo, pProcInfo.av.localAv.recordSoundProcInfo.compressAudio, iIndex_sharedObj, iIndex_sharedObjUsr, null, null, iTaskId, pMgrVar.addr, pst)) {
                                syncObj.Dispose();
                                if (pMgrVar != null)
                                {
                                    guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                                }
                                return iErr;
                            }

                            break;
                        case CONST_objType_tmpGrp: {
                            syncObj.Dispose();
                            if (pMgrVar != null)
                            {
                                guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                            }
                            return iErr;
                        }
                        default:
                            //
                            if (0 != procSendFunc.qyFuncs.procSendAvInfo(mainActivity.var_common.ctxCaller, pMisCnt, hMgr, true, CONST_imTaskType_transferAvInfo, pProcInfo.av.localAv.videoCaptureProcInfo.compressVideo, pProcInfo.av.localAv.recordSoundProcInfo.compressAudio, iIndex_sharedObj, iIndex_sharedObjUsr, null, null, iTaskId, pMgrVar.addr, pst)) {
                                syncObj.Dispose();
                                if (pMgrVar != null)
                                {
                                    guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                                }
                                return iErr;
                            }

                            break;
                    }

                    dlgTalkProc.DLG_TALK_var m_var = pMgrVar;
                    //MIS_CNT  *  pMisCnt  =  pMgrVar.pMisCnt;
                    qnmCommProc_mis.MSGR_ADDR pAddr = pMgrVar.addr;
                    int lenInBytes = 0;
                    //
                    if (!m_var.av.taskInfo.bTaskExists) {  //  2014/10/31
                        Log.d( ("procSendAvInfo failed, taskInfo.bTaskExists is false"));
                        syncObj.Dispose();
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }

                    //
                    m_var.av.taskInfo.iTaskId = pst.o.iTaskId;
                    //  m_var.av.taskInfo.iIndex_sharedObj  =  iIndex_sharedObj;
                    m_var.av.taskInfo.idInfo_starter.ui64Id = pst.o.idInfo_starter.ui64Id;

                    m_var.av.taskInfo.tStartTime_org = pst.o.tStartTime_org;
                    m_var.av.taskInfo.uiTranNo_org = pst.o.uiTranNo_org;
                    m_var.av.taskInfo.uiContentType_org = pst.o.uiContentType_org;

                    m_var.av.taskInfo.ucbStarter = pst.o.ucbStarter;                                    //  2010/08/31
                    m_var.av.taskInfo.ucbVideoConference = pst.o.ucbVideoConference;                    //  2010/08/31
                    //  m_var.av.taskInfo.ucbVideoConferenceStarter  =  pContent->myTask.ucbVideoConferenceStarter;

                    //  2017/08/25
                    m_var.av.taskInfo.usConfType = usConfType;

                    //
                    m_var.av.taskInfo.uiInitW = pst.o.uiInitW;
                    m_var.av.taskInfo.uiInitH = pst.o.uiInitH;

                    //  2014/09/25
                    m_var.av.taskInfo.iIndex_taskInfo = pst.o.iIndex_taskInfo;


                    //  2010/08/31
                    if (!m_var.av.taskInfo.ucbVideoConference) {
                        //  m_var.av.taskInfo.bNeedAutoRequestToSpeak  =  true;

                        //
                        m_var.av.taskInfo.bShow_localVideo = true;
                    }

                    dlgTalkProc.TALK_drawLayout pLayout = m_var.m_drawLayout;
                    {
                        //qyFuncs.mymemset(pLayout, 0, sizeof(TALK_drawLayout));
                    }

                    //
                    //
                    qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "doCmd_startAvCall");
                    if (null == pTaskInfo) {
                        syncObj.Dispose();
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }
                    qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                    if (null == pTaskData) {
                        syncObj.Dispose();
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }
                    if (pTaskData.uiType != CONST_taskDataType_conf) {
                        syncObj.Dispose();
                        if (pMgrVar != null)
                        {
                            guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                        }
                        return iErr;
                    }
                    qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                    qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

                    //
                    if (m_var.av.taskInfo.ucbVideoConference) {
                        pTc.grp.idInfo_initiator.ui64Id = param.idInfo_initiator.ui64Id;
                    }

                    //
                    int uiTaskType = CONST_imTaskType_transferAvInfo;

                    //
                    if (uiTaskType == CONST_imTaskType_transferAvInfo) {
                        if (!m_var.av.taskInfo.ucbVideoConference) {
                            if (0 != addToMsgrs_sendLocalAv.qyFuncs.addToMsgrs_sendLocalAv(pProcInfo, pMisCnt, m_var.addr.idInfo, false, pTaskInfo.var.curRoute_sendLocalAv)) {
                                syncObj.Dispose();
                                if (pMgrVar != null)
                                {
                                    guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                                }
                                return iErr;
                            }
                        }
                    }


                    //
                    if (true) {
                        if (uiTaskType != CONST_imTaskType_viewDynBmp) {   //  2014/09/26
                            m_var.av.taskInfo.local_avStream = pst.o.local_avStream;
                        }
                    }

                    //
                    if (!m_var.av.taskInfo.ucbVideoConference
                        //||  pQyMc.iCustomId  ==  CONST_qyCustomId_hzj
                    ) {
                        //  2018/10/28. hzj需要自动发言
                        //  2013/04/09. 在一对一时,自动将自己加入话筒列表,以便显示.
                        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = null;

                        pActiveMem_from = videoConference.activeMems_from[0];
                        if (pActiveMem_from.avStream.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {
                            String pDesc = pActiveMem_from.desc.desc;
                            {
                                qyCommProc_open.getTalkerDescParams getTalkerDescParams = new qyCommProc_open.getTalkerDescParams();
                                getTalkerDescParams.pMisCnt = pMisCnt;
                                getTalkerDescParams.idInfo = pActiveMem_from.avStream.idInfo;
                                getTalkerDescParams.talkerDesc = pDesc;
                                getTalkerDescParams.talkerDescCnt = cntof_desc;
                                getTalkerDescParams.shortName = null;
                                getTalkerDescParams.shortNameCnt = 0;
                                getTalkerDesc.qyFuncs.getTalkerDesc(getTalkerDescParams);
                                //_sntprintf(m_var.videoConference.activeMems_from[0].desc, mycountof(m_var.videoConference.activeMems_from[0].desc), ("%s(%I64u)"), pMisCnt.displayName, pMisCnt.idInfo.ui64Id);
                            }
                        }

                    }

                    if (pMgrVar.iTalkerSubType != CONST_talkUsage_doConf) {
                        if (pMgrVar.addr.uiObjType == CONST_objType_imGrp) {
                            if (0 != confStarter_fillGrpMems(hMgr, pMgrVar)) {
                                Log.d( ("doCmd_startAvCall: confStart_fillGrpMems failed"));
                                syncObj.Dispose();
                                if (pMgrVar != null)
                                {
                                    guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                                }
                                return iErr;
                            }
                        }
                    }


                    if (bFirstUsr) {
                        if (pProcInfo.av.localAv.videoCaptureProcInfo.bCapDevConnected) {
                            qmcVideoCapture.qyFuncs.startCapture(hMgr, CONST_qyWndContentType_talker, pProcInfo.av.localAv.videoCaptureProcInfo);
                        }
                    }

                    //
                    qmcVideoCapture.qyFuncs.setCurSharedObjUsr_localAv(pProcInfo, iIndex_sharedObj, hMgr);
                    //  2015/05/23
                    if (b3D) {
                        pSharedObjUsr = qmcVideoFunc.qyFuncs.getSharedObjUsr(pSharedObj, iIndex_sharedObjUsr);
                        if (null == pSharedObjUsr) {
                            syncObj.Dispose();
                            if (pMgrVar != null)
                            {
                                guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                            }
                            return iErr;
                        }
                        if (pSharedObjUsr.hWnd_starter != hMgr) {
                            syncObj.Dispose();
                            return iErr;
                        }
                        pSharedObjUsr.bIn3DConf = true;
                    }

                    resetTalkerList_mgr(hMgr);
                } catch (Exception e) {
                    Log.e(e.getMessage());
                    if (pMgrVar != null)
                    {
                        guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
                    }
                    return -1;
                }
            }

            setProcessingAvTask(pMgrVar.av.taskInfo.iIndex_taskInfo, pMgrVar.av.taskInfo.iTaskId);
            qyMcMainDaemon_isCli.qyFuncs.bChkMessengerChannelsReady(pQyMc);

            iErr = 0;

            if (pMgrVar != null) {
                guiShare.GuiShare.pf_talk_addNewMsg.call(hCurTalk, pMgrVar.av.taskInfo.idInfo_starter, pMgrVar.av.taskInfo.tStartTime_org, pMgrVar.av.taskInfo.uiTranNo_org, 0, 0, "视频呼叫", pMgrVar.av.taskInfo.iTaskId);
            }
            return iErr;
        }


        public static int makeSlaveStream(Object hDlg, int iIndex_sharedObj_master, int level) {
            //Log.d("not finished");

            return 0;
        }

        //
        public static int setProcessingAvTask(int iIndex_taskInfo, int iTaskId) {
            qyMcMainWndProc.QY_MC_mainWndVar mainWndVar = (qyMcMainWndProc.QY_MC_mainWndVar) guiShare.GuiShare.pf_mainWnd_getVar.call();

            if (mainWndVar.processingAvTask.bTaskExists) {
                //Log.d("processingAvTask.bTaskExists already is true");
            }

            mainWndVar.processingAvTask.bTaskExists = true;
            mainWndVar.processingAvTask.index_taskInfo = iIndex_taskInfo;
            mainWndVar.processingAvTask.iTaskId = iTaskId;

            return 0;
        }

        public static int clearProcessingAvTask() {
            qyMcMainWndProc.QY_MC_mainWndVar mainWndVar = (qyMcMainWndProc.QY_MC_mainWndVar) guiShare.GuiShare.pf_mainWnd_getVar.call();
            qyMcMainWndProc.PROCESSING_avTask p = mainWndVar.processingAvTask;
            {
                //qyFuncs.mymemset(p, 0, sizeof(PROCESSING_avTask));
            }
            return 0;
        }
    }
}

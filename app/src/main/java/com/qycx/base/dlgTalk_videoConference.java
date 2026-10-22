
package com.qycx.base;

import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.isTalkerShadowMgr;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.dlgTalk_canSpeak;
import static com.qycx.base.dlgTalk_videoConference_public.qyFuncs.getMemFromActiveIndex;
import static com.qycx.base.dlgTalk_videoConference_public.qyFuncs.isActiveMem;
import static com.qycx.base.dlgTalk_videoConference_public.qyFuncs.isActiveMemsFull;
import static com.qycx.base.dlgTalk_videoConference_public.qyFuncs.isMemFromActive;
import static com.qycx.base.getTalkerDesc.Consts.cntof_desc;
import static com.qycx.base.get_funcs_for_isCliHelp.qyFuncs.QY_GET_FUNCS_for_isCliHelp;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_confKey;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_confLayout;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskInteractionReq;
import static com.qycx.base.mainWnd_procMsg.qyFuncs_Droid.mainWnd_getCtxCaller;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc;
import static com.qycx.base.qmcCommFunc_mis.qyFuncs.M_getActiveMemDesc;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendTask;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_requestToSpeak;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_sendStreamInfo;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_stopSpeaking;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_conf_activeMems;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_talkerDesc;
import static com.qycx.base.qyCusResHelp.qyFuncs.getResStr;
import static com.qycx.base.qyDefs.Consts.CONST_objType_imGrp;

import static com.qycx.base.showTaskStatusFunc.qyFuncs.showNotification;
import static com.qycx.base.tmpFunc.qyFuncs.safeTcsnCpy;

import com.qycx.Activity_dlgTalk;
import com.qycx.Activity_dlgTalk_av;
import com.qycx.MainActivity;
import com.qycx.base.addToMsgrs_sendLocalAv;
import com.qycx.base.util.Log;

public class dlgTalk_videoConference {
    public static class qyFuncs_Droid {
        //int  requestToSpeak_videoConferenceStarter(  HWND  hDlgTalk, qyDefs.QY_MESSENGER_ID  *  pIdInfo,  boolean  bRequestToSpeak  )
        public static int confStarter_requestToSpeak(Object hDlgTalkParam, qyDefs.QY_MESSENGER_ID idInfo, qnmCommProc_mis.AV_stream_simple pAss, String talkerDesc, boolean bRequestToSpeak) {
            int iErr = -1;
            qyDefs.QY_MESSENGER_ID pIdInfo = idInfo;

            if (pIdInfo.ui64Id == 0) return -1;
            if (null == talkerDesc) talkerDesc = "";

            String str;


            //
            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                Activity_dlgTalk hDlgTalk=(Activity_dlgTalk)hDlgTalkParam;
                dlgTalkProc.DLG_TALK_var pm_var = hDlgTalk.m_var;//help_getDlgTalkVar.getVar(hDlgTalk);
                if (null == pm_var) return -1;
                dlgTalkProc.DLG_TALK_var m_var = pm_var;
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//(  MC_VAR_isCli  *  )pMisCnt->pProcInfoParam;
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
                //
                funcsForIsCliHelp.FUNCS_for_isCliHelp pFuncs = QY_GET_FUNCS_for_isCliHelp(pProcInfo);
                if (null == pFuncs) return -1;


                if (!m_var.av.taskInfo.bTaskExists) return -1;
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "confStarter_requestToSpeak");
                if (null == pTaskInfo) return -1;
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (pTaskData == null) return -1;
                if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
                qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

                //
                //qmcTaskInfo.DLG_TALK_videoConference pVc = videoConference; ;
                int i;
                int index_activeMems_from;
                boolean bNeedRefresh_requestingMems_from = false;
                boolean bNeedRefresh_display = false;

                if (pIdInfo.ui64Id == 0) return -1;

                if (refreshTaskAv_videoConferenceStarter(hDlgTalk, 0) < 0) return -1;

                qnmCommProc_mis.RES_obj_simple ros=new qnmCommProc_mis.RES_obj_simple();
                //qyFuncs.mymemset((byte[])ros, 0, sizeof(RES_obj_simple));
                if (pAss != null) {
                    ros = pAss.resObj;
                }

                //
                if (bRequestToSpeak) {
                    int index_active = getMemFromActiveIndex(pTc,pIdInfo,ros);

                    if (index_active<0)
                    {

                        for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
                            if (videoConference.activeMems_from[i].avStream.idInfo.ui64Id == 0)
                                break;
                        }
                        if (i == videoConference.usCntLimit_activeMems_from) {
                            //
                            showInfo_open.qyFuncs.showInfo_open("confStarter_requestToSpeak failed, no room for new speaker");
                            //
                        } else {
                            index_activeMems_from = i;

                            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_activeMems_from];

                            pActiveMem_from.memset0();

                            //
                            videoConference.activeMems_from[index_activeMems_from].avStream.idInfo.ui64Id = pIdInfo.ui64Id;
                            //
                            if (pAss != null) {
                                pActiveMem_from.avStream.obj = pAss;
                            }
                            String pDesc = pActiveMem_from.desc.desc;

                            if (!talkerDesc.isEmpty()) {
                                safeTcsnCpy(talkerDesc, pDesc, cntof_desc);
                            } else {
                                //qyFuncs.M_getActiveMemDesc(pMisCnt, pVc.activeMems_from[index_activeMems_from].mem.idInfo, pVc.activeMems_from[index_activeMems_from].desc, mycountof(pVc.activeMems_from[index_activeMems_from].desc));
                                M_getActiveMemDesc(pMisCnt, videoConference.activeMems_from[index_activeMems_from].avStream.idInfo, pDesc, cntof_desc);
                            }
                            //
                            videoConference.bNeedRefresh_activeMems_from = true;

                            //Log.e(TAG,"alloc activeMems_from[%d] for %I64u, %d", index_activeMems_from, pIdInfo->ui64Id, i);

                            if (isMemFromRequesting(pTc,  pIdInfo))
                            {
                                removeFrom_requestingMems_from(pTc,  pIdInfo);
                                bNeedRefresh_requestingMems_from = true;
                            }
                        }

                    }
                    else
                    {
                        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActive = videoConference.activeMems_from[index_active];
                        if (pAss != null) {
                            if (pAss.tranInfo.audio.uiTranNo_openAvDev == 0 && pAss.tranInfo.video.uiTranNo_openAvDev == 0) {
                                return iErr;
                            }
                            if (pActive.avStream.obj.tranInfo.audio.uiTranNo_openAvDev != pAss.tranInfo.audio.uiTranNo_openAvDev
                                    || pActive.avStream.obj.tranInfo.video.uiTranNo_openAvDev != pAss.tranInfo.video.uiTranNo_openAvDev) {
                                pActive.avStream.obj.tranInfo = pAss.tranInfo;
                                //
                                Log.d(("update activeMem"));
                                //
                                videoConference.bNeedRefresh_activeMems_from = true;
                            }
                        }
                    }

                } else {  //  stop speaking
                    for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
                        if (videoConference.activeMems_from[i].avStream.idInfo.ui64Id == pIdInfo.ui64Id)
                            break;
                    }
                    if (i < videoConference.usCntLimit_activeMems_from) {
                        qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom p = videoConference.activeMems_from[i];
                        {
                            //memset(pVc.activeMems_from[i], 0, sizeof(pVc.activeMems_from[i]));
                            //qyFuncs.mymemset(p, 0, sizeof(DLG_TALK_videoConference_activeMemFrom));
                            p.memset0();

                        }
                        //
                        videoConference.bNeedRefresh_activeMems_from = true;
                    }
                    //
                }

                if (videoConference.bNeedRefresh_activeMems_from) {
                    if (refreshTaskAv_videoConferenceStarter(hDlgTalk, 0) < 0)
                        return iErr;
                    bNeedRefresh_display = true;

                    //  2010/09/12
                    if (!bRequestToSpeak) {
                        //dlgTalk_freeCapImageBySth_mgr(hDlgTalk, m_var.av.taskInfo.iTaskId, pIdInfo);    //  2010/09/12
                    }
                }
                if (bNeedRefresh_requestingMems_from) bNeedRefresh_display = true;

                //
                if (pIdInfo.ui64Id == pMisCnt.idInfo.ui64Id) {   //  It is me
                    qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
                    qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = null;

                    pSharedObj = getSharedObjByIndex(pProcInfo, m_var.av.iIndex_sharedObj_localAv);
                    if (null == pSharedObj) return iErr;
                    pSharedObjUsr = qmcVideoFunc.qyFuncs.getSharedObjUsr(pSharedObj, m_var.av.iIndex_usr_localAv);
                    if (null == pSharedObjUsr) return iErr;

                    //
                    boolean ucbNotSendData = !isMemFromActive(pTc,  pIdInfo, null, pSharedObjUsr.uiTranNo_openAvDev_a,pSharedObjUsr.uiTranNo_openAvDev_v);

                    //
                    if (pSharedObj.var.ucbNotSendData != ucbNotSendData) {
                        pSharedObj.var.ucbNotSendData = ucbNotSendData;
                        bNeedRefresh_display = true;
                    }
                    //
                    if (!ucbNotSendData) {
                        //
                        if (pSharedObj.var.iIndex_curUsr == m_var.av.iIndex_usr_localAv) {
                            //
                            if (!m_var.av.taskInfo.ucbVideoConference) {
                            } else {
                                if (pTaskInfo.var.curRoute_sendLocalAv.videoConference_idInfo_to.ui64Id == 0) {
                                    if (0 != addToMsgrs_sendLocalAv.qyFuncs.addToMsgrs_sendLocalAv(pProcInfo, pMisCnt, m_var.addr.idInfo, true, pTaskInfo.var.curRoute_sendLocalAv)) {
                                        //
                                        return iErr;
                                    }
                                }
                            }
                            setFlg_forceKeyFrame.qyFuncs.setFlg_forceKeyFrame(pProcInfo, m_var.av.iIndex_sharedObj_localAv);
                        }
                    }
                }

                if (bNeedRefresh_display) {
                    refreshTalkerList.qyFuncs.resetTalkerList_mgr(hDlgTalk);
                }
            }

            iErr = 0;
            
            return iErr;
        }


        public static void confOthers_sendStreamInfo(Activity_dlgTalk_av activityDlgTalk) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();

            if  (activityDlgTalk==null)return;
            if  (activityDlgTalk.mOwner_hTalk==null)return;
            dlgTalkProc.DLG_TALK_var pm_var = activityDlgTalk.mOwner_hTalk.get().get_pm_var();//.(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(activityDlgTalk.mOwner_hTalk);
            qnmCommProc_mis.TASK_INTERACTION_REQ req=new qnmCommProc_mis.TASK_INTERACTION_REQ();
            req.uiType = CONST_imCommType_taskInteractionReq;
            req.usOp = CONST_imOp_sendStreamInfo;
            req.avStream.obj.tranInfo = pm_var.av.taskInfo.local_avStream.tranInfo;
            req.iHkStatus = audioRecorder_android.qyFuncs_Droid.isMicrophoneMuted() ? 1 : 0;
            req.idInfo_imGrp_related.ui64Id = pm_var.addr.idInfo.ui64Id;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            long tStartTran = tmpFunc.qyFuncs.mytime();
            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
            long ui64Id = activityDlgTalk.mOwner_hTalk.get().m_var.av.taskInfo.idInfo_starter.ui64Id;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller,  pMisCnt, CONST_misMsgType_task, (byte) 0, (short) CONST_qyCmd_sendTask, tStartTran, uiTranNo, (short) 0, 0, 0,  req, 0, 0, 0, 0, ui64Id,
                    0, (qnmCommProc_mis.MIS_MSGU) null, false);
        }

        public static int confOthers_requestToSpeak(Object hDlgTalkParam, boolean bRequestToSpeak) {
            int iErr = -1;

            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                ;
                Activity_dlgTalk hDlgTalk=(Activity_dlgTalk)hDlgTalkParam;
                dlgTalkProc.DLG_TALK_var pm_var = hDlgTalk.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlgTalk);
                if (null == pm_var) return -1;
                dlgTalkProc.DLG_TALK_var m_var = pm_var;
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//(  MC_VAR_isCli  *  )m_var.pMisCnt->pProcInfoParam;
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

                //
                //
                if (!m_var.av.taskInfo.bTaskExists) return -1;
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "confOthers_requestToSpeak");
                if (null == pTaskInfo) return -1;

                //
                int iTaskId = m_var.av.taskInfo.iTaskId;

                //
                funcsForIsCliHelp.FUNCS_for_isCliHelp pFuncs = QY_GET_FUNCS_for_isCliHelp(pProcInfo);
                if (null == pFuncs) return -1;
                //
                qnmCommProc_mis.TASK_INTERACTION_REQ req=new qnmCommProc_mis.TASK_INTERACTION_REQ();
                int lenInBytes = 0;

                if (bRequestToSpeak) {
                    qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;

                    if (null == (pSharedObj = getSharedObjByIndex(pProcInfo, m_var.av.iIndex_sharedObj_localAv)))
                        return iErr;

                    if (pSharedObj.var.iIndex_curUsr == m_var.av.iIndex_usr_localAv) {
                        if (pTaskInfo.var.curRoute_sendLocalAv.route.idInfo_to.ui64Id == 0) {
                            //
                            if (0 != addToMsgrs_sendLocalAv.qyFuncs.addToMsgrs_sendLocalAv(pProcInfo, pMisCnt, m_var.av.taskInfo.idInfo_starter, false, pTaskInfo.var.curRoute_sendLocalAv)) {
                                return iErr;
                            }
                        }

                        //  2014/08/25			
                        setFlg_forceKeyFrame.qyFuncs.setFlg_forceKeyFrame(pProcInfo, m_var.av.iIndex_sharedObj_localAv);

                    }
                }

                //qyFuncs.mymemset((byte[])req, 0, sizeof(TASK_INTERACTION_REQ));
                req.uiType = CONST_imCommType_taskInteractionReq;
                req.usOp = (short) (bRequestToSpeak ? CONST_imOp_requestToSpeak : CONST_imOp_stopSpeaking);
                req.tStartTime_org = m_var.av.taskInfo.tStartTime_org;
                req.uiTranNo_org = m_var.av.taskInfo.uiTranNo_org;
                req.iHkStatus = audioRecorder_android.qyFuncs_Droid.isMicrophoneMuted() ? 1 : 0;
                //req.uiContentType_org  =  m_var.av.taskInfo.uiContentType_org;
                //
                //  2015/02/17
                if (bRequestToSpeak) {
                    req.avStream.obj.tranInfo = m_var.av.taskInfo.local_avStream.tranInfo;
                    qyCommProc_open.getTalkerDescParams getTalkerDescParams = new qyCommProc_open.getTalkerDescParams();
                    getTalkerDescParams.pMisCnt = pMisCnt;
                    getTalkerDescParams.idInfo = pMisCnt.idInfo;
                    getTalkerDescParams.talkerDesc = req.talkerDesc;
                    getTalkerDescParams.talkerDescCnt = cntof_talkerDesc;
                    getTalkerDescParams.shortName = null;
                    getTalkerDescParams.shortNameCnt = 0;
                    getTalkerDesc.qyFuncs.getTalkerDesc(getTalkerDescParams);
                    req.talkerDesc = getTalkerDescParams.talkerDesc;
                }
                //
                if (m_var.av.taskInfo.ucbVideoConference)
                    req.idInfo_imGrp_related.ui64Id = m_var.addr.idInfo.ui64Id;
                //	
                lenInBytes=128;//sizeof(TASK_INTERACTION_REQ);
                long tStartTran = tmpFunc.qyFuncs.mytime();
                int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
                qyDefs.QY_MESSENGER_ID idInfo_dst = m_var.av.taskInfo.idInfo_starter;

//                byte[] data = req.toByteArray();
                int len = 0;
                if (0 != postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, (short) CONST_qyCmd_sendTask, tStartTran, uiTranNo, (short) 0, 0, 0,  req, len, 0, 0, 0, idInfo_dst.ui64Id,
                0, (qnmCommProc_mis.MIS_MSGU) null, false))return iErr;
                //
            }

            iErr = 0;
            
            return iErr;
        }

        public static int refreshTaskAv_videoConferenceStarter(Object hDlgTalkParam, long pMsgTask) {
            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                Activity_dlgTalk hDlgTalk=(Activity_dlgTalk)hDlgTalkParam;
                if (hDlgTalk==null)return  -1;
                dlgTalkProc.DLG_TALK_var pm_var = hDlgTalk.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlgTalk, ("refreshTaskAv_videoConferenceStarter"));
                if (null == pm_var) return -1;
                dlgTalkProc.DLG_TALK_var m_var = pm_var;
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();  //  (  MC_VAR_isCli  *  )pMisCnt->pProcInfoParam;

                if (!m_var.av.taskInfo.bTaskExists) return 0;
                //  if  (  !m_var.av.taskInfo.ucbVideoConferenceStarter  )  return  0;
                if (!m_var.av.taskInfo.ucbStarter) return 0;

                //return pProcInfo.processQ_media.qTraverse(tmpHandler_refreshTaskAv_videoConferenceStarter, hDlgTalk, pMsgTask);
                return 0;
            }
        }


        //  2009/05/31
        public static int refreshTaskAv_videoConferenceOthers(Object hDlgParam) {
            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                if  (  hDlgParam==null)return  -1;
                Activity_dlgTalk hDlg  =  (Activity_dlgTalk)hDlgParam;

                dlgTalkProc.DLG_TALK_var pm_var = hDlg.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlg);
                ;
                if (null == pm_var) return -1;
                dlgTalkProc.DLG_TALK_var m_var = pm_var;
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//(  MC_VAR_isCli  *  )m_var.pMisCnt->pProcInfoParam;

                if (!m_var.av.taskInfo.bTaskExists) return 0;
                //  if  (  m_var.av.taskInfo.ucbVideoConferenceStarter  )  return  0;
                if (m_var.av.taskInfo.ucbStarter) return 0;

                return 0;
            }
        }


        //
        public static int getConfKey(Object hDlgTalk, int iTaskId,  qnmCommProc_mis.CONF_KEY pConfKey, String hint) {
            int iErr = -1;

            Activity_dlgTalk dlgTalk = (Activity_dlgTalk)hDlgTalk;
            {
                dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;
                if (null == m_var) return -1;

                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
                MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

                if (null == pConfKey) return -1;

                int i;
                int lenInBytes;
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

                if (!m_var.av.taskInfo.bTaskExists) return -1;
                if (!m_var.av.taskInfo.ucbStarter) return -1;
                if (m_var.av.taskInfo.iTaskId != iTaskId) return -1;

                //
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "getConfKey");
                if (null == pTaskInfo) return -1;
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (null == pTaskData) return -1;
                if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
                qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;

                qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

                pConfKey.uiType = CONST_imCommType_confKey;
                if (m_var.av.taskInfo.ucbVideoConference)
                    pConfKey.idInfo_imGrp_related.ui64Id = m_var.addr.idInfo.ui64Id;

                //  2010/12/29
                if (m_var.av.taskInfo.bTaskExists && m_var.av.taskInfo.ucbStarter) {
                    pConfKey.usLayoutType_starter = m_var.m_layout.usLayoutType_cur;
                }
                
                qnmCommProc_mis.VIDEO_CONFERENCE_MEM[] pstatus_mems =  pConfKey.buf_VIDEO_CONFERENCE_MEM_mems;
                for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
                    if (videoConference.activeMems_from[i].avStream.idInfo.ui64Id == 0) continue;

                    if (pConfKey.usCnt >= MAX_conf_activeMems) {
                        Log.e("sendConfKey err: status too many mems, can't add a activeMem");
                        break;
                    }
                    pstatus_mems[pConfKey.usCnt].avStream = videoConference.activeMems_from[i].avStream;

                    //  2013/07/18. 需要将第几个话筒的属性传给所有的成员,以便协调显示格局
                    pstatus_mems[pConfKey.usCnt].usIndex_speaker = (short) i;
                    pConfKey.usCnt++;
                }
            }

            iErr = 0;


            return iErr;
        }


        public static int sendConfKey(Object hDlgTalkParam, qyDefs.QY_MESSENGER_ID idInfo_to, String hint) {
            int iErr = -1;

            qnmCommProc_mis.CONF_KEY status = new qnmCommProc_mis.CONF_KEY();

            qyDefs.QY_MESSENGER_ID pIdInfo_to = idInfo_to;

            int lenInBytes;

            //
            if (null == pIdInfo_to || pIdInfo_to.ui64Id == 0) return -1;


            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                if  (  hDlgTalkParam==null) return  -1;
                Activity_dlgTalk hDlgTalk=(Activity_dlgTalk)hDlgTalkParam;
                dlgTalkProc.DLG_TALK_var pm_var = hDlgTalk.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlgTalk);
                if (null == pm_var) return -1;
                dlgTalkProc.DLG_TALK_var m_var = pm_var;
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//(  MC_VAR_isCli  *  )pMisCnt->pProcInfoParam;
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

                //
                if (!m_var.av.taskInfo.bTaskExists) return -1;
                if (!m_var.av.taskInfo.ucbStarter) return -1;

                if (0 != getConfKey(hDlgTalk, m_var.av.taskInfo.iTaskId, status, ("sendVideoConferenctStatus")))
                    return iErr;

                lenInBytes=128;//sizeof(CONF_KEY);
                long tStartTran = tmpFunc.qyFuncs.mytime();
                int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

                //
                if  (  true  ) {
                    int i;
                    String str;
                    str  =" tn "  +  uiTranNo;
                    for (i = 0; i < status.usCnt;i++  )  {
                        qnmCommProc_mis.VIDEO_CONFERENCE_MEM mem = status.buf_VIDEO_CONFERENCE_MEM_mems[i];
                        str  += " mem" + i  +  ":"  +  mem.avStream.idInfo.ui64Id  +  " " + "v"  +  " "  +  mem.avStream.obj.tranInfo.video.uiTranNo_openAvDev;
                    }
                    showInfo_open.qyFuncs.showInfo_open(0,0,test.tag_100k,str);
                }

                //
//                byte[] data = status.toByteArray();
                  int len = 0;
                if (0 != postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, (short) CONST_qyCmd_sendTask, tStartTran, uiTranNo, (short) 0, 0, 0,  status, len, 0, 0, 0, pIdInfo_to.ui64Id, 0, (qnmCommProc_mis.MIS_MSGU) null, false))
                    return iErr;
            }

            iErr = 0;
            
            return iErr;
        }

        //public static  boolean isMemFromRequesting(qmcTaskInfo.DLG_TALK_videoConference pVc, qyDefs.QY_MESSENGER_ID idInfo_from)
        public static boolean isMemFromRequesting(qmcTaskInfo.QMC_taskData_conf pTc, qyDefs.QY_MESSENGER_ID idInfo_from) {
            int i;
            qyDefs.QY_MESSENGER_ID pIdInfo_from = idInfo_from;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            for (i = 0; i < videoConference.requestingMems_from.length; i++) {
                if (pIdInfo_from.ui64Id == videoConference.requestingMems_from[i].avStream.idInfo.ui64Id)
                    break;
            }
            if (i == videoConference.requestingMems_from.length) return false;

            return true;
        }

        //public static  int addTo_requestingMems_from(MIS_CNT pMisCnt, qmcTaskInfo.DLG_TALK_videoConference pVc, qyDefs.QY_MESSENGER_ID idInfo)
        public static int addTo_requestingMems_from(qmCommFunc_mis.MIS_CNT pMisCnt, qmcTaskInfo.QMC_taskData_conf pTc, qyDefs.QY_MESSENGER_ID idInfo) {
            int i;
            qyDefs.QY_MESSENGER_ID pIdInfo = idInfo;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            if (null == pIdInfo || pIdInfo.ui64Id == 0) return -1;

            if (isMemFromRequesting(pTc, pIdInfo))return 0;
            for (i = 0; i < videoConference.requestingMems_from.length; i++) {
                if (videoConference.requestingMems_from[i].avStream.idInfo.ui64Id == 0) break;
            }
            if (i == (videoConference.requestingMems_from.length)) return -1;

            videoConference.requestingMems_from[i].avStream.idInfo.ui64Id = pIdInfo.ui64Id;

            String pDesc = "";
            if(videoConference.requestingMems_from[i].desc!=null)
            {
                pDesc = videoConference.requestingMems_from[i].desc.desc;
            }

            {
                //  getTalkerDesc(  pMisCnt,  pIdInfo,  pVc.requestingMems_from[i].desc,  mycountof(  pVc.requestingMems_from[i].desc  ),  0,  0  );
                M_getActiveMemDesc(pMisCnt,pIdInfo, pDesc, cntof_desc);
            }

            return 0;
        }

        public static int removeFrom_requestingMems_from(qmcTaskInfo.QMC_taskData_conf pTc, qyDefs.QY_MESSENGER_ID pIdInfo) {
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            int i;
            for (i = 0; i < (videoConference.requestingMems_from.length); i++) {
                if (videoConference.requestingMems_from[i].avStream.idInfo.ui64Id == pIdInfo.ui64Id) {
                    qmcTaskInfo.DLG_TALK_videoConference_requestingMems_from p = videoConference.requestingMems_from[i];
                    {
                        //mymemset(pVc.requestingMems_from[i], 0, sizeof(pVc.requestingMems_from[i]));
                        //qyFuncs.mymemset(p, 0, sizeof(DLG_TALK_videoConference_requestingMems_from));
                        p.memset0();
                    }
                    break;
                }
            }
            return 0;
        }

        public static int dlgTalk_requestToSpeak(Object hDlgTalk_mgrParam, boolean bEnable) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if(pQyMc==null) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if(pProcInfo==null) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            ctxCaller.CTX_caller ctxCaller = mainWnd_getCtxCaller(pQyMc.gui.hMainWnd);
            String str = "";

            //
            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar_mgr = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                if  (  hDlgTalk_mgrParam==null)return  -1;
                Activity_dlgTalk hDlgTalk_mgr=(Activity_dlgTalk) hDlgTalk_mgrParam;
                ;
                dlgTalkProc.DLG_TALK_var pMgrVar = hDlgTalk_mgr.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar_mgr.getVar(hDlgTalk_mgr);
                if (null == pMgrVar) return -1;
                if (!isTalkerShadowMgr(pMgrVar.addr)) return -1;

                if (!pMgrVar.av.taskInfo.bTaskExists) return -1;
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pMgrVar.av.taskInfo.iIndex_taskInfo, "dlgTalk_requestToSpeak");
                if (null == pTaskInfo) return iErr;
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (null == pTaskData) return iErr;
                qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;


                //  2017/07/11
                if (bEnable) {
                    if (pMgrVar.av.taskInfo.bTaskExists
                            && pMgrVar.addr.uiObjType == CONST_objType_imGrp
                            && pMgrVar.av.taskInfo.ucbConfCtrl
                            && !pMgrVar.av.taskInfo.ucbStarter
                        //&& !pShadowMgr->av.ucbMeConfMgr
                    ) {
                        if (!pMgrVar.av.taskInfo.confMem_confCtrl.ucbPermitToSpeak) {
//                            str = getResStr(0, pQyMc.cusRes, R.string.CONST_resId_cannotSpeak);
                            showNotification(0, null, null, 0, 0, 0, str);
                            return iErr;
                        }
                    }

                    //
                    if (pMgrVar.av.taskInfo.ucbVideoConference
                            && !pMgrVar.av.taskInfo.ucbStarter) {
                        //
                        if (!isActiveMem(ctxCaller, pMgrVar.av.taskInfo.iTaskId, pMisCnt.idInfo)) {
                            if (isActiveMemsFull(ctxCaller, pMgrVar.av.taskInfo.iTaskId, true)) {
                                str = String.format(getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_tooManySpeaks));
                                showNotification(0, null, null, 0, 0, 0, str);
                                return iErr;
                            }
                        }
                    }
                }
                //  BOOL							bEnableState	=	m_var.canSpeak;
                boolean bEnableState = dlgTalk_canSpeak(hDlgTalk_mgr);

                //  2010/12/19
                qmcSharedObj.QY_SHARED_OBJ  pSharedObj  =  getSharedObjByIndex(pProcInfo,pMgrVar.av.iIndex_sharedObj_localAv);
                if  (  pSharedObj!=null) {
                    //pMgrVar.av.taskInfo.bEnableToSpeak = bEnable;
                    pSharedObj.var.ucbNotSendData = !bEnable;
                }

                if (pMgrVar.av.taskInfo.ucbStarter) {
                    qnmCommProc_mis.AV_stream_simple ass = pMgrVar.av.taskInfo.local_avStream;
                    confStarter_requestToSpeak(hDlgTalk_mgr, pMisCnt.idInfo, ass, null, bEnable);
                    //if ((bEnable && !bEnableState) || (!bEnable && bEnableState))
                    {
                        sendConfKey(hDlgTalk_mgr, pMgrVar.addr.idInfo, "");
                    }
                } else {
                    //  if  (  (  bEnable  &&  !bEnableState  )  ||  (  !bEnable  &&  bEnableState  )  )  
                    {
                        confOthers_requestToSpeak(hDlgTalk_mgr, bEnable);
                    }
                }
            }

            iErr = 0;
            
            return iErr;
        }


        public static int sendConfLayout(Activity_dlgTalk hDlgTalkParam, byte ucbResp, short usLayoutType, short usOp, qnmCommProc_mis.ConfLayoutParam pParam, qyDefs.QY_MESSENGER_ID idInfo_imGrp_related, qyDefs.QY_MESSENGER_ID idInfo_requester, qyDefs.QY_MESSENGER_ID idInfo_to, String hint) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if(pQyMc==null) return -1;

            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            if  (  hDlgTalkParam==null)return  -1;
            Activity_dlgTalk hDlgTalk=(Activity_dlgTalk)hDlgTalkParam;
            dlgTalkProc.DLG_TALK_var pm_var = hDlgTalk.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlgTalk);
            if (null == pm_var) return -1;
            dlgTalkProc.DLG_TALK_var m_var = pm_var;

            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//(  MC_VAR_isCli  *  )pMisCnt->pProcInfoParam;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            qnmCommProc_mis.CONF_LAYOUT layout=new qnmCommProc_mis.CONF_LAYOUT();
            int i;
            int lenInBytes;

            if (null == hint) hint = "";


            //
            qyDefs.QY_MESSENGER_ID pIdInfo_to = idInfo_to;
            if (null == pIdInfo_to || pIdInfo_to.ui64Id == 0) return -1;

            //memset(layout, 0, sizeof(layout));
            layout.uiType = CONST_imCommType_confLayout;
            //if  (  m_var.av.taskInfo.ucbVideoConference  )  layout.idInfo_imGrp_related.ui64Id  =  m_var.addr.idInfo.ui64Id;
            layout.idInfo_imGrp_related.ui64Id = idInfo_imGrp_related.ui64Id;

            layout.ucbResp = ucbResp;

            layout.usLayoutType = usLayoutType;
            //
            layout.usOp = usOp;
            //
            qnmCommProc_mis.ConfLayoutParam pPParam = pParam;
            layout.confLayoutParam = pParam;

            //
            lenInBytes=128;//sizeof(CONF_LAYOUT);
            long tStartTran = tmpFunc.qyFuncs.mytime();
            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
//            byte[] data = layout.toByteArray();
            int len = 0;
            if (0 != postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, (short) CONST_qyCmd_sendTask, tStartTran, uiTranNo, (short) 0, 0, 0,  layout,len, 0, 0, 0, pIdInfo_to.ui64Id, 0, (qnmCommProc_mis.MIS_MSGU) null, false))
                return iErr;

            iErr = 0;

            
            return iErr;
        }


    }
}

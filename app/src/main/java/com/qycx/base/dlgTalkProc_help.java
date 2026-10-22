


package com.qycx.base;

import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.isTalkerShadowMgr;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_applyForTalkerShadow_qmc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_taskStatus;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_recvFile;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_remoteAssist;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_sendFile;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_shareScreen;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_transferAvInfo;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;

import com.qycx.Activity_dlgTalk;
import com.qycx.MainActivity;
import com.qycx.base.util.Log;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class dlgTalkProc_help {
    public static class qyFuncs_Droid {

        public static Object findTalker(qmCommFunc_mis.MIS_CNT pMisCntParam, qyDefs.QY_MESSENGER_ID pIdInfo) {
            if(pMisCntParam.talkingFriendQ == null || pMisCntParam.talkingFriendQ.isEmpty()) return null;
            Optional<qmCommFunc_mis.MIS_MSG_TALKINGFRIEND_QMC> obj = pMisCntParam.talkingFriendQ.stream().filter(x -> x.addr.idInfo.ui64Id == pIdInfo.ui64Id).findFirst();
            if (obj.orElse(null) != null)
                return Objects.requireNonNull(obj.orElse(null)).hWnd;
            else
                return null;
        }

        public static guiShare.PF_findTalker findTalker = (ctxQyMc.CCtxQyMc pQyMcParam, qmCommFunc_mis.MIS_CNT pMisCntParam, qyDefs.QY_MESSENGER_ID pIdInfo) -> {

            int iErr = -1;

            qmCommFunc_mis.MIS_CNT pMisCnt = pMisCntParam;

            if (null == pMisCnt || pIdInfo.ui64Id == 0) return null;

            int index = IntStream.range(0, pMisCnt.talkingFriendQ.size()).filter(x -> pMisCnt.talkingFriendQ.get(x).addr.idInfo.ui64Id == pIdInfo.ui64Id).findFirst().orElse(-1);

            if (index < 0) return null;
            return (Activity_dlgTalk) pMisCnt.talkingFriendQ.get(index).hWnd;

        };

        //
        public static int dlgTalk_get_usLayoutType_local(Object hDlgTalk) {

            int usLayoutType_local = 0;


            return usLayoutType_local;
        }


        //
        public static TableItem.TableItem_dlgTalk_msg findTaskInProcessQ(dlgTalkProc.DLG_TALK_var m_var, int iTaskId, boolean bNextAv, boolean bToShareScreen) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            //
            TableItem.TableItem_dlgTalk_msg pMsg = null;
            //TableItem_dlgTalk_msg pQNode = null;

            if (null == m_var.m_pProcessQ) return null;
            List<TableItem.TableItem_dlgTalk_msg> m_pProcessQ = m_var.m_pProcessQ;
            boolean bFound = false;


            for (var pQNode : m_pProcessQ) {
                pMsg = pQNode;
                if (pMsg.uiMsgType == CONST_misMsgType_task) {
                    if (iTaskId != 0) {
                        if (iTaskId == pMsg.iTaskId) {
                            bFound = true;
                            break;
                        }
                    } else if (bNextAv) {
                        if (pMsg.uiTaskType == CONST_imTaskType_transferAvInfo
                            //&& qyFuncs.bTaskAlive(pMsg.iStatus)
                        ) {
                            bFound = true;
                            break;
                        }
                    } else if (bToShareScreen) {       //  2011/03/12
                        if ((pMsg.uiTaskType == CONST_imTaskType_shareScreen
                                || pMsg.uiTaskType == CONST_imTaskType_remoteAssist       //  2015/11/05
                        )
                                //&& qyFuncs.bTaskAlive(pMsg.iStatus)
                                && pMsg.ui64Id_from == pMisCnt.idInfo.ui64Id) {
                            bFound = true;
                            break;
                        }
                    }
                }
            }
            //if (!pQNode) return NULL;
            if (!bFound) return null;

            //return pMsg->task;
            return pMsg;
        }


        //

        public static guiShare.PF_dlgTalk_qPostMsg dlgTalk_qPostMsg = (Object hDlgTalkParam, Object pQElem, int size) -> {
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;

            //
            qyDefs.QY_MESSENGER_ID idInfo_from = new qyDefs.QY_MESSENGER_ID();
            idInfo_from.ui64Id = 0;
            long tStartTime = 0;
            int uiTranNo = 0;
            int usCode = 0;
            int uiContentType = 0;
            String msg = "";
            int iTaskId = 0;
            int iStatus = 0;


            //
            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                if  (  hDlgTalkParam==null)  {
                    showInfo_open.qyFuncs.showInfo_open("dlgTalk_qPostMsg failed, hDlgTalk is null");
                    return  -1;
                }
                ;Activity_dlgTalk hDlgTalk  =  (Activity_dlgTalk)hDlgTalkParam;
                dlgTalkProc.DLG_TALK_var pVar = (dlgTalkProc.DLG_TALK_var) hDlgTalk.m_var;// help_getDlgTalkVar.getVar(hDlgTalk);
                if (null == pVar) {
                    //  2014/01/24
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("Gui"), NULL, "", "", ("dlgTalk_qPostMsg failed,  getDlgTalkVar is null"));
                    return -1;
                }
                if (null == pVar.m_pProcessQ) {
                    //  2014/01/24
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, NULL, "", "", ("dlgTalk_qPostMsg failed,  pVar->m_ProcessQ is null"));
                    return -1;
                }
                if (null == pQElem || 0 == size) return -1;

                qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pQElem;
                switch (pMsg.uiType) {
                    case CONST_misMsgType_task:
                        qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                        idInfo_from.ui64Id = task.data.route.idInfo_from.ui64Id;
                        tStartTime = task.tStartTime;
                        uiTranNo = task.uiTranNo;
                        iTaskId = task.iTaskId;
                        iStatus = task.iStatus;

                        //
                        if (0 != task.iTaskId) {
                            switch (task.uiTaskType) {
                                case CONST_imTaskType_sendFile:
                                case CONST_imTaskType_recvFile: {

                                }
                                break;
                                default: {
                                    //  2016/02/17
                                    TableItem.TableItem_dlgTalk_msg tmppMsg = findTaskInProcessQ(pVar, task.iTaskId, false, false);
                                    //
                                    if (tmppMsg != null) {

                                        //
                                        return -1;
                                    }
                                    break;

                                }
                            }
                        }
                        //
                        qnmCommProc_mis.IM_CONTENTU pContent;
                        pContent = qnmCommProc_mis.qyFuncs.M_getMsgContent(task.uiType, task.ucFlg, task.data);
                        //
                        uiContentType = pContent.uiType;
                        //
                        switch (pContent.uiType) {
                            case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                                msg = "视频通话";
                                break;
                            default:
                                break;
                        }
                        //
                        break;
                    case CONST_misMsgType_talk:
                        break;
                    case CONST_misMsgType_taskStatus:

                        break;
                    case CONST_misMsgType_applyForTalkerShadow_qmc:
                        break;
                    default:

                        break;
                }
                //return  ::qPostMsg(pQElem, size, pVar->m_pProcessQ);
                guiShare.GuiShare.pf_talk_addNewMsg.call(hDlgTalk, idInfo_from, tStartTime, uiTranNo, usCode, uiContentType, msg, iTaskId);
            }

            iErr = 0;

            return iErr;
        };


        //
        public static int removeTalker(Object pQyMcParam, Object hDlgTalk, int unused) {

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            if (null == pMisCnt) return -1;

            //
            qyDefs.COMMON_PARAM commonParam1;
            //MACRO_makeCommonParam3((Object)pAddr, (Object)unused, 0, commonParam1);

            //qRemoveMsg(pMisCnt->talkingFriendQ, tmpHandler_findTalker, NULL,commonParam1);
//            pMisCnt.talkingFriendQ.RemoveAll(item = > item.hWnd == hDlgTalk);

            pMisCnt.talkingFriendQ.removeIf(x -> x.hWnd == hDlgTalk);
            return 0;
        }


        //
        public static boolean bDone_retrieveAllImObjRules(qmCommFunc_mis.MIS_CNT pMisCnt) {
            return true;
        }


        //
        public static boolean bDlgTalkActive(Object hDlgTalk) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            if (pQyMc.gui.hLastActiveTalk == hDlgTalk) return true;
            return false;
        }

        //
        public static int talkToMessenger(Object pQyMcParam, qnmCommProc_mis.MSGR_ADDR pAddr, boolean bNeedNotShowWnd, boolean bActivateWnd, isCliHelpPublic.PARAM_talkToMessenger param) {
            int iErr = -1;
            if (null == pAddr) return -1;
            ctxQyMc.CCtxQyMc pQyMc = (ctxQyMc.CCtxQyMc) pQyMcParam;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;

            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            if (!bDone_retrieveAllImObjRules(pMisCnt)) {
                Log.e("talkToMessenger failed: not all imObjRules retrieved");
                return -1;
            }
            int index = -1;
            for (int i = 0; i < pMisCnt.talkingFriendQ.size(); i++) {
                if (pMisCnt.talkingFriendQ.get(i).addr.idInfo.ui64Id == pAddr.idInfo.ui64Id) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                if (bNeedNotShowWnd) {
                    iErr = 0;
                    return iErr;
                }

                if (bDlgTalkActive(pMisCnt.talkingFriendQ.get(index).hWnd)) {
                    Log.e("need to show dlgTalk");
                    iErr = 0;
                    return iErr;
                }

                Activity_dlgTalk dlgTalk = (Activity_dlgTalk) pMisCnt.talkingFriendQ.get(index).hWnd;
                dlgTalk.doFinish("talkToMessenger, ");
            }

            Intent intent = new Intent(mainActivity, Activity_dlgTalk.class);
            intent.putExtra("ui64Id", pAddr.idInfo.ui64Id);
            intent.putExtra("info", "haha");

            intent.putExtra("wParam", param.wParam);
            intent.putExtra("lParam", param.lParam);

            mainActivity.startActivity(intent);

            iErr = 0;
            return iErr;
        }


        public static int dlgTalk_set_newMsgArrives(Object hDlgTalk, dlgTalkProc.DLG_TALK_var pm_var, boolean bOn) {
            return 0;

        }


        //
        public static boolean dlgTalk_canSpeak(Object hDlgTalk_mgrParam) {
            boolean bRet = false;

            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                Activity_dlgTalk hDlgTalk_mgr=(Activity_dlgTalk)hDlgTalk_mgrParam;
                dlgTalkProc.DLG_TALK_var pm_var = hDlgTalk_mgr.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlgTalk_mgr, ("dlgTalk_canSpeak"));
                if (null == pm_var) return false;
                dlgTalkProc.DLG_TALK_var m_var = pm_var;
                //
                if (!isTalkerShadowMgr(m_var.addr)) return false;
                //
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                if(pQyMc==null) return false;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();  //  (  MC_VAR_isCli  *  )m_var.pMisCnt->pProcInfoParam;
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
                MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
                qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
                qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = null;

                if (!m_var.av.taskInfo.bTaskExists) return false;
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "dlgTalk_canSpeak");
                if (null == pTaskInfo) return bRet;
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (pTaskData == null) return bRet;
                if (pTaskData.uiType != CONST_taskDataType_conf) return bRet;
                qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                //qmcTaskInfo.DLG_TALK_videoConference pVc = pTc.videoConference;


                if (null == (pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, m_var.av.iIndex_sharedObj_localAv)))
                    return bRet;
                if (pSharedObj.var.iIndex_curUsr != m_var.av.iIndex_usr_localAv)
                    return bRet;        //  2010/09/07
                if (null == (pSharedObjUsr = qmcVideoFunc.qyFuncs.getSharedObjUsr(pSharedObj, m_var.av.iIndex_usr_localAv)))
                    return bRet;

                //  if  (  m_var.av.taskInfo.ucbVideoConference  )
                {   //  2010/09/04
                    if (!dlgTalk_videoConference_public.qyFuncs.isMemFromActive(pTc, pMisCnt.idInfo, null, pSharedObjUsr.uiTranNo_openAvDev_a,pSharedObjUsr.uiTranNo_openAvDev_v)
                        //|| pTc.videoConference.bNeedRefresh_activeMems_from
                    ) return bRet;
                }

                if (pSharedObj.var.ucbNotSendData) return bRet;

                if (pTaskInfo.var.curRoute_sendLocalAv.videoConference_idInfo_to.ui64Id == 0 && pTaskInfo.var.curRoute_sendLocalAv.route.idInfo_to.ui64Id == 0)
                    return bRet;
            }

            bRet = true;

            return bRet;

        }

        public static int askToRefreshLayout_mgr(Object hDlgTalk) {
            return -1;
        }
    }
}

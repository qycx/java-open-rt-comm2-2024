
package com.qycx.base;

import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvInfo;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_activeMems_from;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_getMsgContent;

public class dbgFunc {
    public static class qyFuncs {
//
//        public static int chkTc(qmcTaskInfo.QMC_taskData_conf pTc, String hint) {
//            String str;
//            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();
//            String str1 = String.format("requestMem.length %s", videoConference.requestingMems_from.length);
//
//            if (videoConference.usCntLimit_activeMems_from > videoConference.activeMems_from.length) {
//                str = String.format("chkTc 0 %s,usCntLimit %s, length %s ", hint, videoConference.usCntLimit_activeMems_from, videoConference.activeMems_from.length);
//
//                int ii = 0;
//            }
//            if (videoConference.activeMems_from.length > MAX_activeMems_from) {
//                str = String.format("chkTc 1 %s,usCntLimit %s, length %s ", hint, videoConference.usCntLimit_activeMems_from, videoConference.activeMems_from.length);
//
//                int ii = 0;
//            }
//            if (videoConference.activeMems_from.length == 0) {
//                str = String.format("chkTc 2 %s,usCntLimit %s, length %s ", hint, videoConference.usCntLimit_activeMems_from, videoConference.activeMems_from.length);
//
//                int ii = 0;
//            }
//            if (videoConference.requestingMems_from.length > 20) {
//                int ii = 1;
//            }
//            if (videoConference.requestingMems_from.length == 0) {
//                int ii = 0;
//            }
//
//            return 0;
//        }
//
//        //
//        public static mcSharedQ.PF_enumTask tmpHandler_chkAllTc = (ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU p0, qyMcMainProc.PARAM_findOrgMsg p1Param, taskCommon.TASK_common taskElemParam)-> {
//            int iErr= -1;
//            String hint = (String) p1Param;
//            taskCommon.TASK_common task = taskElemParam;
//
//            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
//            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
//
//            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(ctxCaller, pProcInfo, task.m_index_taskInfo, "");
//            if (pTaskInfo == null) return iErr;
//            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
//            if (pTaskData == null) return iErr;
//
//            if (pTaskData.uiType == CONST_taskDataType_conf) {
//                qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
//                chkTc(pTc, hint);
//            }
//            return 0;
//        };
//
//
//        public static int chkAllTc(String hint) {
//            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
//            if (pQyMc == null) return -1;
//            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
//            if (pProcInfo == null) return -1;
//
//            pProcInfo.processQ_media.qTraverse(null,dbgFunc.qyFuncs.tmpHandler_chkAllTc, null, hint);
//            return 0;
//        }
//
//        public static int tmp_showTc(ctxCaller.CTX_caller ctxCaller, int iTaskId, String hint) {
//            int iErr = -1;
//            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
//            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
//
//            if (null == hint) hint = "";
//
//            //
//            int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(ctxCaller, pProcInfo, iTaskId);
//            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(ctxCaller, pProcInfo, index_taskInfo, "tmp_showTc");
//            if (pTaskInfo == null) return -1;
//
//            String str;
//
//            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
//            if (pTaskData.uiType != CONST_taskDataType_conf) return iErr;
//            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
//            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();
//
//            //
//            chkTc(pTc, hint);
//
//            //
//            String str1 = "";
//
//            str = String.format("tmp_showTc called. index_taskInfo %s, usCntLimit_activeMems %s, length %s. requestMems.length %s， {4} ", index_taskInfo, videoConference.usCntLimit_activeMems_from, videoConference.activeMems_from.length, videoConference.requestingMems_from.length, hint);
//            //
//            str += str1;
//
//            qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
//            if (pMsgTask.uiType == CONST_misMsgType_task) {
//                qnmCommProc_mis.IM_CONTENTU pContent = M_getMsgContent(pMsgTask.uiType, pMsgTask.ucFlg,pMsgTask.data);
//                if (pContent != null) {
//                    if (pContent.uiType == imCommType_defs.Consts.CONST_imCommType_transferAvInfo) {
//                        qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
//                        int tn_a = transferAvInfo.ass.tranInfo.audio.uiTranNo_openAvDev;
//                        int tn_v = transferAvInfo.ass.tranInfo.video.uiTranNo_openAvDev;
//                        //
//                        str += String.format("a %s,v %s.", tn_a, tn_v);
//                    }
//                }
//            }
//
//            int i;
//            for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
//                qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem = videoConference.activeMems_from[i];
//                //
//                if (pActiveMem.avStream.idInfo.ui64Id != 0) {
//                    str += String.format("act[%s]: %s %s %s, ", i, pActiveMem.avStream.idInfo.ui64Id, pActiveMem.avStream.obj.tranInfo.audio.uiTranNo_openAvDev, pActiveMem.avStream.obj.tranInfo.video.uiTranNo_openAvDev);
//                }
//            }
//            //
//            str += "";// getTaskStatus(iTaskId);
//            //
//            str += hint;
//
//            qyCommProc.mySYSTEMTIME when=new qyCommProc.mySYSTEMTIME();
//            tmpFunc.qyFuncs.myGetLocalTime(when);
//            showInfo_open.qyFuncs.showState_open(null, "", str, 0, 0, when, 1, 0);
//
//            iErr = 0;
//
//            return iErr;
//        }
//
//        //
//        public static int showAllTc(ctxCaller.CTX_caller ctxCaller, String hint) {
//            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
//            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
//            int i;
//            qmcTaskInfo.QMC_TASK_INFO pTaskInfo;
//
//            if (hint == null) hint = "";
//            String str;
//            //
//            for (i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
//                pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(ctxCaller, pProcInfo, i, "showAllTc");
//                if (!pTaskInfo.bUsed) continue;
//                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
//                if (pTaskData == null) continue;
//                if (pTaskData.uiType != CONST_taskDataType_conf) continue;
//                //
//                if (!qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(ctxCaller, pProcInfo, i)) continue;
//                //
//                str = String.format("%s", hint);
//                tmp_showTc(ctxCaller, pTaskInfo.var.iTaskId, str);
//            }
//            return 0;
//        }
    }
}

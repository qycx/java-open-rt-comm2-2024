


package com.qycx.base;

import static com.qycx.base.getTalkerDesc.Consts.cntof_desc;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvReplyInfo;
import static com.qycx.base.qmcCommFunc_mis.qyFuncs.M_getActiveMemDesc;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;

import com.qycx.base.util.Log;

public class qmcProcTask {
    public static class qyFuncs {

        //
        public static int old_procTaskAvReply(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MSGR_ADDR pAddr_logicalPeer, int iTaskId, qnmCommProc_mis.AV_TRAN_INFO pAvTran, taskAv.PROC_TASK_AV pTask, qnmCommProc_mis.MIS_MSG_TASK pMsgTask) {
            int iErr = -1;
            int index = 0;
            qnmCommProc_mis.IM_CONTENTU pContent = (qnmCommProc_mis.IM_CONTENTU) pMsgTask.data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsgTask.data.buf;
            //
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            return iErr;
        }


        //
        public static int procTaskAvReply(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MSGR_ADDR pAddr_logicalPeer, int iTaskId, qnmCommProc_mis.AV_TRAN_INFO pAvTran, taskAv.PROC_TASK_AV pTask, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput) {
            int iErr = -1;

            boolean bDbg = false;
            String str;

            //
            int index = 0;
            qnmCommProc_mis.IM_CONTENTU pContent =  pMsgInput.data.buf;
            //
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //
            ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
            //mymemset((byte[])ctxCaller, 0, sizeof(CTX_caller));

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "procTaskAvReply");
            if (null == pTaskInfo) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;
            if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            //
            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from;

            //
            if (pContent.uiType != CONST_imCommType_transferAvReplyInfo) return -1;
            //
            if (0 != ((qnmCommProc_mis.TRANSFER_AV_replyInfo)pContent).tranInfo.audio.uiTranNo_openAvDev && 0 != pAvTran.audio.uiTranNo_openAvDev) {

            }

            //
            pMsgInput.addr_logicalPeer.idInfo.ui64Id = pAddr_logicalPeer.idInfo.ui64Id;

            //
            pMsgInput.iTaskId = iTaskId;

            //
            if (!pTask.bVideoConference()) {
                int i;
                for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
                    if (videoConference.activeMems_from[i].avStream.idInfo.ui64Id == pMsgInput.data.route.idInfo_from.ui64Id
                            && videoConference.activeMems_from[i].avStream.obj.resObj.uiObjType == 0) {
                        break;
                    }
                }

                //
                if (i < videoConference.usCntLimit_activeMems_from) {
                    pActiveMem_from = videoConference.activeMems_from[i];
                    qnmCommProc_mis.TRANSFER_AV_replyInfo  transferAvReplyInfo = (qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent;
                    if (pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev == transferAvReplyInfo.tranInfo.audio.uiTranNo_openAvDev
                            && pActiveMem_from.avStream.obj.tranInfo.video.uiTranNo_openAvDev == transferAvReplyInfo.tranInfo.video.uiTranNo_openAvDev) {
                        str = String.format("procTaskAvReply: %d, mem_from'av matched, OK", pMsgInput.data.route.idInfo_from.ui64Id);
                        Log.i(str);
                        showInfo_open.qyFuncs.showInfo_open(0, null, str);
                        iErr = 0;
                        return iErr;
                    }
                    pActiveMem_from.avStream.obj.tranInfo = transferAvReplyInfo.tranInfo;
                    str = String.format("procTaskAvReply: %d, OK", pMsgInput.data.route.idInfo_from.ui64Id);
                    Log.i(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null, str);
                    iErr = 0;
                    return iErr;

                }
                for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
                    if (0 == videoConference.activeMems_from[i].avStream.idInfo.ui64Id) break;
                }
                if (i == videoConference.usCntLimit_activeMems_from) {
                    str = String.format("procTaskAvReply: %s, no free index in activeMems_from, failed.", pMsgInput.data.route.idInfo_from.ui64Id);
                    showInfo_open.qyFuncs.showInfo_open(0, null, str);

                    //
                    iErr = 0;
                    return iErr;
                }

                //qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom*
                pActiveMem_from = videoConference.activeMems_from[i];
                pActiveMem_from.avStream.idInfo = pMsgInput.data.route.idInfo_from;
                pActiveMem_from.avStream.obj.tranInfo = ((qnmCommProc_mis.TRANSFER_AV_replyInfo)pContent).tranInfo;
                //
                String pDesc = pActiveMem_from.desc.desc;
                {
                    M_getActiveMemDesc(pMisCnt, pActiveMem_from.avStream.idInfo, pDesc, cntof_desc);
                }
                str = String.format("procTaskAvReply: %s, OK", pMsgInput.data.route.idInfo_from.ui64Id);
                showInfo_open.qyFuncs.showInfo_open(0, null, str);
                iErr = 0;
                return iErr;
            }

            iErr = 0;

            

            if (0 == iErr) {

            }


            return iErr;
        }


        //
        public static int procTaskAudioDataResp(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.AV_TRAN_INFO pTranInfo, taskAv.PROC_TASK_AV pTask, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, qnmCommProc_mis.TRANSFER_AUDIO_dataResp pResp) {
            int iErr = -1;


            iErr = 0;
            

            //if (tHintBuf[0]) qyShowInfo1(CONST_qyShowType_qwmComm,pMsgInput->uiStep, "", pProcInfo->who_showInfo, 0, "", "", ("%s"), tHintBuf);

            return iErr;

        }


        public static int tmp_procTask_recv_cancel(ctxQmc.CCtxQmc pProcInfo, taskAv.PROC_TASK_AV pTask, qnmCommProc_mis.MIS_MSG_INPUT pMsgInput, qnmCommProc_mis.TASK_PROC_REQ pTaskProcReq) {
            int iErr = -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            showInfo_open.qyFuncs.showInfo_open(0, null, "tmp_procTask_recv_cancel called. ............................................................................................");
           

            iErr = 0;
            return iErr;
        }


    }
}

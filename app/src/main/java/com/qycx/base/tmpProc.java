


package com.qycx.base;

import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth;
import static com.qycx.base.qnmCommProc_mis.Consts.*;


public class tmpProc {

    public static class qyFuncs {

        public static qyTypes.QY_DMITEM[] CONST_imOpTable_en = new qyTypes.QY_DMITEM[]
                {
                        new qyTypes.QY_DMITEM(CONST_imOp_says, ("says")),

                        new qyTypes.QY_DMITEM(CONST_imOp_tryToSendFile, ("tryToSendFile")),
                        //
                        new qyTypes.QY_DMITEM(CONST_imOp_recv_accept, ("recv_accept")),
                        new qyTypes.QY_DMITEM(CONST_imOp_recv_deny, ("recv_deny")),
                        new qyTypes.QY_DMITEM(CONST_imOp_recvFile_saveas, ("recvFile_saveas")),
                        //
                        new qyTypes.QY_DMITEM(CONST_imOp_recv_cancel, ("recv_cancel")),
                        new qyTypes.QY_DMITEM(CONST_imOp_send_cancel, ("send_cancel")),
                        new qyTypes.QY_DMITEM(CONST_imOp_send_permit, ("send_permit")),
                        //
                        new qyTypes.QY_DMITEM(CONST_imOp_dual_permit, ("dual_permit")),
                        //
                        new qyTypes.QY_DMITEM(CONST_imOp_recv_applyForOrgReq, ("recv_applyForOrgReq")),
                        new qyTypes.QY_DMITEM(CONST_imOp_recv_applyForChkTaskAlive, ("recv_applyForChkTaskAlive")),        //  2009/10/19
                        new qyTypes.QY_DMITEM(CONST_imOp_recv_applyForKeyFrame, ("recv_applyForKeyFrame")),
                        //
                        //  2009/02/21
                        new qyTypes.QY_DMITEM(CONST_imOp_requestToSpeak, ("request to speak")),
                        new qyTypes.QY_DMITEM(CONST_imOp_stopSpeaking, ("stop speaking")),

                        //  2014/10/09
                        new qyTypes.QY_DMITEM(CONST_imOp_requestToSend, ("request to send")),        //  2014/10/09
                        new qyTypes.QY_DMITEM(CONST_imOp_stopSending, ("stop sending")),        //  2014/10/09

                        //

                        //
                        new qyTypes.QY_DMITEM(CONST_imOp_openFile, ("openFile")),
                        //
                        new qyTypes.QY_DMITEM(CONST_imOp_myMax, ("imOp_myMax")),
                        //
                        new qyTypes.QY_DMITEM(-1, ""),

                };


        public static mcSharedQ.PF_bEnumTask tmpHandler_bTask = (ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU p0, qyMcMainProc.PARAM_findOrgMsg p1, taskCommon.TASK_common taskMem) ->
        {
            boolean bRet = false;
            int iTaskId = (int) p1.iTaskId;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskMem.m_index_taskInfo, "tmpHandler_bTask");
            if (pTaskInfo == null) return false;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return false;

            if (pTaskData.msgU.uiType == CONST_misMsgType_task && ((qnmCommProc_mis.MIS_MSG_TASK)pTaskData.msgU).iTaskId == iTaskId) {
                bRet = true;
            }

            return bRet;
        };

        public static int removeTask(ctxCaller.CTX_caller ctxCaller, int iTaskId) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            qnmCommProc_mis.CMIS_MSGU p0_unused = new qnmCommProc_mis.CMIS_MSGU();
            qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
            param.iTaskId = iTaskId;
            pProcInfo.processQ_media.qRemoveMsg(ctxCaller, tmpHandler_bTask, p0_unused.msgU, param);


            return 0;

        }

        public static void clearShiftKeys() {
            //Log.e(TAG,("clearShiftKeys,  test_touchscreen"));
            return;
        }

        //
        public static boolean bSupported_fileServer(ctxQyMc.CCtxQyMc pQyMc) {
            return false;
        }


        //
        public static int getStatusByTaskId(int iTaskId) {
            int iErr = -1;
            int iStatus = 0;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            if (iTaskId == 0) return -1;
            int index_taskInfo = getQmcTaskInfoIndexBySth(null, pProcInfo, iTaskId);
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(null, pProcInfo, index_taskInfo, "");
            if (pTaskInfo == null) return iErr;
            qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();
            if (taskData == null) return iErr;
            qnmCommProc_mis.MIS_MSGU pMsg = taskData.msgU;
            {
                switch (pMsg.uiType) {
                    case CONST_misMsgType_task:
                        iStatus = ((qnmCommProc_mis.MIS_MSG_TASK)pMsg).iStatus;
                        break;
                    default:
                        return iErr;
                }

            }


            iErr = 0;
            
            return iErr != 0 ? iErr : iStatus;
        }

    }
}

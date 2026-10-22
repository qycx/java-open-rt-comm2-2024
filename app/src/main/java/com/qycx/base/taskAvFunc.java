


package com.qycx.base;

import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;

public class taskAvFunc {
    public static class qyFuncs {
        public static qnmCommProc_mis.TASK_AV_FROM getTaskAvFrom(qmcTransform.QY_TRANSFORM pTransform, int index_pMems_from, int index_activeMems_from, String hint) {
            qnmCommProc_mis.TASK_AV_FROM pFrom = null;
            taskAv.PROC_TASK_AV pTask = (taskAv.PROC_TASK_AV) pTransform.pTask;
            if (null == pTask) return null;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pQyMc.get_pProcInfo();

            ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
            //mymemset((byte[])ctxCaller, 0, sizeof(CTX_caller));

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "getTaskAvFrom");
            if (null == pTaskInfo) return null;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return null;
            if (pTaskData.uiType != CONST_taskDataType_conf) return null;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;

            if (null == hint) hint = "";

            return pFrom;
        }


        public static qnmCommProc_mis.TASK_AV_FROM getTaskAvFrom(taskAv.PROC_TASK_AV pTask, int index_pMems_from, String hint) {
            qnmCommProc_mis.TASK_AV_FROM pFrom = null;


            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pQyMc.get_pProcInfo();

            //
            ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
            //mymemset((byte[])ctxCaller, 0, sizeof(CTX_caller));


            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "getTaskAvFrom");
            if (null == pTaskInfo) return null;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (null == pTaskData) return null;
            if (pTaskData.uiType != CONST_taskDataType_conf) return null;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;

            if (null == hint) hint = "";

            return pFrom;
        }

    }
}

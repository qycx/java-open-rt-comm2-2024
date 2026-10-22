package com.qycx.base.old;

import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_dec;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.qmcTaskInfo;
import com.qycx.base.qmcTransform;
import com.qycx.base.qyMcMain_global;

public class chkFromPlayerIndex_v {
    public static class qyFuncs {
        
        public static int chkFromPlayerIndex_v(int uiTransformType, Object pQY_TRANSFORM, int iSampleTimeInMs) {
            int iErr = -1;

            if (uiTransformType != CONST_uiTransformType_dec) return -1;

            //if  (  !pQdcObjInfo  )  return  -1;
            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pQY_TRANSFORM;    //  pQdcObjInfo->cfg.pParent_transform;
            if (null == pTransform) return -1;

            //
            long dwCurTickCnt = myGetTickCount(null);

            //
            pTransform.video.decInfo.iSampleTimeInMs_postDec = iSampleTimeInMs;
            pTransform.video.decInfo.cnt_postDec++;
            //  2014/12/06
            pTransform.video.decInfo.dwLastTickCnt_postDec = dwCurTickCnt;

            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            do {
                int index_taskInfo = getQmcTaskInfoIndexBySth(null, pProcInfo, pTransform.iTaskId);
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, index_taskInfo, "");
                if (pTaskInfo == null) break;
                if (!pTaskInfo.bUsed) break;
                qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();

                //
                taskData.tmpStatus.lastTickCnt_dec_v = myGetTickCount(null);

                //
            } while (false);


            //
            iErr = 0;
            return iErr;
        }
    }
}




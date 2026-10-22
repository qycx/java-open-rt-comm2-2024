


package com.qycx.base;

import static com.qycx.base.qmcSharedObj.qyFuncs.getCapBmpBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;

public class setFlg_forceKeyFrame {
    public static class qyFuncs {
        public static int setFlg_forceKeyFrame(ctxQmc.CCtxQmc pProcInfo, int iIndex_sharedObj) {
            int iErr = -1;

            boolean bDbg = false;
            String str;

            //  MC_VAR_isCli	*	pProcInfo		=	QY_GET_procInfo_isCli(  );
            if (null == pProcInfo) return -1;

            qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
            qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp = null;
            //TCHAR tBuf[128] = "";


            pSharedObj = getSharedObjByIndex(pProcInfo, iIndex_sharedObj);
            if (null == pSharedObj) return iErr;
            pCapBmp = getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);
            if (null == pCapBmp) return iErr;


            //
            long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);

            //
            int iElapseInMs = (int) (dwTickCnt - pCapBmp.compressVideo.encInfo.dwTickCnt_startToForceKeyFrame);

            if (Math.abs(iElapseInMs) > 500) {
                //
                pCapBmp.compressVideo.encInfo.dwTickCnt_startToForceKeyFrame = dwTickCnt;

                //  2017/10/14
                str = String.format("%s, iIndex_sharedObj %s", ("setFlg_forceKeyFrame ok"), iIndex_sharedObj);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);

            }

            iErr = 0;
            return iErr;

        }

    }
}

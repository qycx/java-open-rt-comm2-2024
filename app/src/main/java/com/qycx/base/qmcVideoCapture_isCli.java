


package com.qycx.base;

import static com.qycx.base.freeSharedObj.qyFuncs.freeSharedObjByIndex;
import static com.qycx.base.qmcMasterSlave.qyFuncs.bExists_slave;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;

public class qmcVideoCapture_isCli {
    public static class qyFuncs {

        public static int tryToFreeSharedObjs(ctxQmc.CCtxQmc pProcInfo) {

            if (null == pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;    //
            if (null == pQyMc) return -1;

            return 0;
        }


        //
        public static int freeSharedObjAndSlave(Object hWnd, int iWndContentType, int index) {
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;    //
            if (null == pQyMc) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            boolean bDbg = true;


            qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, index);
            if (pSharedObj == null) return iErr;

            if (!pSharedObj.bUsed) {
                iErr = 0;
                return iErr;
            }

            {
                //  2014/09/28
                if (!pSharedObj.var.bSlave) {
                    if (bExists_slave(pProcInfo, index)) {
                        qmcSharedObj.QY_SHARED_OBJ pSharedObj_master = pSharedObj;
                        int iIndex_slave = pSharedObj_master.var.master.slaveKey.iIndex_sharedObj;
                        qmcSharedObj.QY_SHARED_OBJ pSharedObj_slave = getSharedObjByIndex(pProcInfo, iIndex_slave);

                        shareDynBmp_closeTaskAv.qyFuncs.shareDynBmp_closeTaskAv(pSharedObj_master.var.master.pShareDynBmp_slave);

                        freeSharedObjByIndex(hWnd, iWndContentType, iIndex_slave);
                    }
                }

                freeSharedObjByIndex(hWnd, iWndContentType, index);
            }

            iErr = 0;

            

            return iErr;
        }


        public static int newCapProcInfoBmpIndex(ctxQmc.CCtxQmc pProcInfo, int iIndex_sharedObj) {
            if (iIndex_sharedObj < 0 || iIndex_sharedObj >= pProcInfo.cfg.usMaxCnt_capProcInfoBmps)
                return -1;
            return iIndex_sharedObj;
        }

        public static int newCapProcInfoAudioIndex(ctxQmc.CCtxQmc pProcInfo, int iIndex_sharedObj) {
            if (iIndex_sharedObj < 0 || iIndex_sharedObj >= pProcInfo.cfg.usMaxCnt_capProcInfoAudios)
                return -1;
            return iIndex_sharedObj;
        }

    }
}

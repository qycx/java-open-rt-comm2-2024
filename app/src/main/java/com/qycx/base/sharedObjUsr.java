
package com.qycx.base;

import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjSyncByIndex;
import static com.qycx.base.qmcVideoCapture_isCli.qyFuncs.freeSharedObjAndSlave;
import static com.qycx.base.qmcVideoFunc.qyFuncs.getSharedObjUsr;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;
import static com.qycx.base.syncMtCntFunc.qyFuncs.syncMtCnt_start;

import com.qycx.base.util.Log;
import com.qycx.base.util.javatuples.Pair;
import com.qycx.base.util.javatuples.Quartet;
import com.qycx.base.util.javatuples.Triplet;

public class sharedObjUsr {
    public static class qyFuncs {


        // int iIndex_sharedObj,
        // //boolean bLocalAv, int  piIndex_sharedObjUsr, boolean pbFirstUsr
        public static Pair<Integer, Boolean> getNewSharedObjUsrFunc(ctxQmc.CCtxQmc pProcInfo, Object hWnd, int iWndContentType, int iIndex_sharedObj,boolean bLocalAv) {
            int iErr = -1;
            //MC_VAR_isCli				*		pProcInfo			=	QY_GET_procInfo_isCli(  );
            if (null == pProcInfo) return null;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) return null;

            int index = iIndex_sharedObj;

            qmcSharedObj.QY_sharedObj_sync pSharedObjInfo = null;
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
            boolean bFirstUsr = false;

            if (!guiShare.GuiShare.pf_IsWindow.call(hWnd)) return null;

            pSharedObjInfo = getSharedObjSyncByIndex(pProcInfo, index);
            if (null == pSharedObjInfo) return null;

            pSharedObj = pSharedObjInfo.sharedObj;
            //
            if (pSharedObj.bUsed) {
                if (pSharedObj.var.bQuit) {
                    Log.d("getNewSharedObjUsrFunc failed, sharedObj.bQuit is true");
                    return null;
                }
            }

            if (!pSharedObj.bUsed) {
                pSharedObjInfo.sharedObj.memset0();
                pSharedObj = pSharedObjInfo.sharedObj;
                pSharedObj.var.uiTranNo = getuiNextTranNo.call();
                pSharedObj.bUsed = true;

                //pSharedObj.pProcInfo = pProcInfo;                  //  2015/10/15

                pSharedObj.var.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);       //  2011/08/01
                pSharedObj.var.iIndex_curUsr = -1;                     //  2010/09/07

                syncMtCnt_start(pSharedObjInfo.syncMtCnt, qy_getuiNextTranNo.qyFuncs.getuiNextTranNo);

                bFirstUsr = true;
            }

            int i;
            for (i = 0; i < pSharedObj.usrs.length; i++) {
                if (pSharedObj.usrs[i].hWnd_starter == hWnd) break;
            }
            if (i == (pSharedObj.usrs.length)) {
                for (i = 0; i < (pSharedObj.usrs.length); i++) {
                    if (!guiShare.GuiShare.pf_IsWindow.call(pSharedObj.usrs[i].hWnd_starter)) break;
                }
                if (i == (pSharedObj.usrs.length)) return null;
            }
            pSharedObj.usrs[i].memset0();
            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = getSharedObjUsr(pSharedObj, i);
            pSharedObjUsr.hWnd_starter = hWnd;
            pSharedObjUsr.iWndContentType = iWndContentType;

            return new Pair<>(i,bFirstUsr);
        }


        // int  piIndex_sharedObj, int  piIndex_sharedObjUsr, boolean pbFirstUsr
        public static Triplet<Integer, Integer, Boolean> getNewSharedObjUsr_localAv(ctxQmc.CCtxQmc pProcInfo, Object hWnd, int iWndContentType) {
            int iIndex_sharedObj_localAv = 0;
            Pair<Integer,Boolean> pair = getNewSharedObjUsrFunc(pProcInfo, hWnd, iWndContentType, iIndex_sharedObj_localAv, true);
            if (null == pair) {
                return null;
            }
            return new Triplet<>(iIndex_sharedObj_localAv, pair.getValue0(),pair.getValue1());
        }

        //
        public static Pair<Integer, Boolean> getNewSharedObjUsr(ctxQmc.CCtxQmc pProcInfo, Object hWnd, int iWndContentType, int iIndex_sharedObj) {
            return getNewSharedObjUsrFunc(pProcInfo, hWnd, iWndContentType, iIndex_sharedObj, false);
        }

        public static int freeSharedObjUsrFunc(ctxQmc.CCtxQmc pMC_VAR_isCli, int index, boolean bLocalAv, Object hWnd, int iWndContentType) {
            int iErr = -1;

            //
            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pMC_VAR_isCli;

            //
            if (index != 0) {
                //Log.e(TAG,("freeSharedObjUsr_localAv failed, index error"));
                return -1;
            }
            //  int								index				=	0;
            //  MC_VAR_isCli				*		pProcInfo			=	QY_GET_procInfo_isCli(  );
            if (null == pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;//QY_GET_GBUF(  );
            if (null == pQyMc) return -1;
            //
            //qmcSharedObj.QY_sharedObj_sync		*		pSharedObjInfos		=	NULL;
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;

            //if (!guiShare.GuiShare.pf_IsWindow.call(hWnd)) return -1;
            if (hWnd == null) return -1;

            //pSharedObjInfos  =  (  qmcSharedObj.QY_sharedObj_sync  *  )pProcInfo->pSharedObjSyncs;
            //pSharedObj  =  &pSharedObjInfos[index].sharedObj;
            pSharedObj = getSharedObjByIndex(pProcInfo, index);
            if (null == pSharedObj) return -1;

            //
            //if (pSharedObj.bQuit) return -1;

            if (!pSharedObj.bUsed) return -1;

            int i;
            for (i = 0; i < pSharedObj.usrs.length; i++) {
                if (pSharedObj.usrs[i].hWnd_starter == hWnd) {
                    //memset(pSharedObj.usrs[i], 0, sizeof(pSharedObj.usrs[i])  );
                    pSharedObj.usrs[i].memset0();

                    if (pSharedObj.var.iIndex_curUsr == i)
                        pSharedObj.var.iIndex_curUsr = -1;     //  2010/09/07
                }
            }

            //  check if sharedObjUsr exists.
            for (i = 0; i < pSharedObj.usrs.length; i++) {
                if (pSharedObj.usrs[i].hWnd_starter != null) break;
            }
            if (i == pSharedObj.usrs.length) {
                //askSharedObjToStop(pProcInfo, pSharedObj, pQyMc.gui.hMainWnd, ("freeSharedObjUsr_localAv"));
                freeSharedObjAndSlave(hWnd, iWndContentType, index);
            }

            return 0;
        }


        //
        public static int freeSharedObjUsr_localAv(ctxQmc.CCtxQmc pMC_VAR_isCli, int index, Object hWnd, int iWndContentType) {
            boolean bLocalAv = true;
            return freeSharedObjUsrFunc(pMC_VAR_isCli, index, bLocalAv, hWnd, iWndContentType);
        }

        //
        public static int freeSharedObjUsr(ctxQmc.CCtxQmc pMC_VAR_isCli, int index, Object hWnd, int iWndContentType) {
            boolean bLocalAv = false;
            return freeSharedObjUsrFunc(pMC_VAR_isCli, index, bLocalAv, hWnd, iWndContentType);
        }


        //

        //  2011/03/16
        public static int resetCurSharedObjUsrFunc(ctxQmc.CCtxQmc pProcInfo, int index_sharedObj, boolean bLocalAv) {
            if (null == pProcInfo) return -1;
            //
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, index_sharedObj);
            if (null == pSharedObj) return -1;

            pSharedObj.var.iIndex_curUsr = -1;

            return 0;
        }


        //
        public static int resetCurSharedObjUsr_localAv(ctxQmc.CCtxQmc pMC_VAR_isCli, int index_sharedObj) {
            boolean bLocalAv = true;
            return resetCurSharedObjUsrFunc(pMC_VAR_isCli, index_sharedObj, bLocalAv);
        }

        //
        public static int resetCurSharedObjUsr(ctxQmc.CCtxQmc pMC_VAR_isCli, int index_sharedObj) {
            boolean bLocalAv = false;
            return resetCurSharedObjUsrFunc(pMC_VAR_isCli, index_sharedObj, bLocalAv);
        }


    }
}

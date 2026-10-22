


package com.qycx.base;

import static com.qycx.base.Consts.cntof_mutexName_prefix;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_postEncV;
import static com.qycx.base.avCapHelpCommProc.Consts.CONST_evtName_qm_data_a;
import static com.qycx.base.avCapHelpCommProc.Consts.CONST_evtName_qm_data_v;
import static com.qycx.base.avCapHelpCommProc.Consts.CONST_evtName_qm_toShareVideo_post;
import static com.qycx.base.avCapHelpCommProc.Consts.CONST_evtName_qm_vpp;
import static com.qycx.base.genericQueue.Consts.cntof_qCfg_name;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapAudioBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapBmpBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qyMcMainDaemon_isCli_mgr.Consts.cntof_tBuf128;
import static com.qycx.base.qyMcMainMediaProc_encAudio.qyFuncs.mcThreadProc_toShareAudio;
//import static com.qycx.base.qyMcMainMediaProc_toShareVideo_post.qyFuncs.mcThreadProc_toShareVideo_post;
import static com.qycx.base.qyTmpMemory.qyFuncs.cleanEx_myDRAW_VIDEO_DATA;
import static com.qycx.base.qyTmpMemory.qyFuncs.clean_myPLAY_AUDIO_DATA;
import static com.qycx.base.qyq2.Consts.cntof_q2Cfg_name;
import static com.qycx.base.qyq2.qyFuncs.initQyQ2;

import com.qycx.base.util.Log;


public class startQThreadToShareAv {
    public static class qyFuncs {
        public static int newstartQThreadToShareAv(ctxQmc.CCtxQmc pProcInfo, int iIndex_sharedObj, boolean bAudio) {
            int iErr = -1;
            if (null == pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.QMC_cfg pCfg = pProcInfo.cfg;

            qmcSharedObj.QY_SHARED_OBJ pSharedObj;

            qyMcMainDaemon_isCli_mgr.TMP_tBuf128 tName = new qyMcMainDaemon_isCli_mgr.TMP_tBuf128();
            int cntof_tName = cntof_tBuf128;
            genericQueue.GENERIC_Q_CFG tmpCfg;
            genericQueue.GENERIC_Q_cfgEx qCfgEx;
            int dwThreadDaemonId;
            String str;

            pSharedObj = getSharedObjByIndex(pProcInfo, iIndex_sharedObj);
            if (null == pSharedObj) return iErr;

            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                Log.e("for test, newstartQThreadToShareAv return 0");
                return 0;
            }

            if (bAudio) {
                qmcSharedObj.CAP_procInfo_audioCommon pCapAudio = null;
                pCapAudio = (qmcSharedObj.CAP_procInfo_audioCommon) getCapAudioBySth(pProcInfo, pSharedObj.var.iIndex_capAudio, 0);
                if (null == pCapAudio) return iErr;

                //  2015/10/11
                if (pCapAudio.thread.bQInited) {
                    str ="startQThreadToShareAv failed, capA.bQInited is already true";
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    return iErr;
                }
                tmpCfg = pCfg.toShareAudioQ;
                tmpCfg.name = String.format("%s-%d", tmpCfg.name, pSharedObj.var.uiTranNo);
                tmpCfg.mutexName_prefix = String.format("%s-%d", tmpCfg.mutexName_prefix, pSharedObj.var.uiTranNo);
                tName.tBuf = String.format("%s%d", CONST_evtName_qm_data_a, pSharedObj.var.uiTranNo);

                if (0 != initQyQ2(tmpCfg, false,  pQyMc.cfg.rwLockParam, 2, new String(tName.tBuf), null, null, clean_myPLAY_AUDIO_DATA, pCapAudio.thread.q2))
                    return iErr;
                pCapAudio.thread.bQInited = true;

                if (null == pCapAudio.thread.hThread) {
                    pCapAudio.thread.bQuit = false;
                    pCapAudio.thread.hThread=guiShare.GuiShare.pf_startThread1.call( mcThreadProc_toShareAudio, iIndex_sharedObj);
                }
            }

            {
                qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp = null;
                pCapBmp = getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);
                if (null == pCapBmp) return iErr;

                tmpCfg = pCfg.toShareVideo_postQ;
                tmpCfg.name = String.format("%s-%d", tmpCfg.name, pSharedObj.var.uiTranNo);
                tmpCfg.mutexName_prefix = String.format("%s-%d", tmpCfg.mutexName_prefix, pSharedObj.var.uiTranNo);
                tName.tBuf = String.format("%s%d", CONST_evtName_qm_toShareVideo_post, pSharedObj.var.uiTranNo);
                if (pCapBmp.postThread.bQInited) {
                    str="startQThreadToShareAv failed, capV.bQInited is  already true.";
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    return iErr;
                }

                if  (  pProcInfo.cfg.b_useQ2_postEncV) {
                    qCfgEx = new genericQueue.GENERIC_Q_cfgEx();
                    qCfgEx.common = tmpCfg;
                    qCfgEx.pf_QElemRemoveEx = (genericQueue.PF_qElemRemoveEx) cleanEx_myDRAW_VIDEO_DATA;
                    if (0 != initQyQ2(qCfgEx, false,pQyMc.cfg.rwLockParam, 2, tName.tBuf, pCapBmp.postThread.q2)) {
                        return iErr;
                    }
                    pCapBmp.postThread.bQInited = true;
                }
                else {

                    //
                    int maxDataBufLen = 2000000  /  8;  //  1秒数据
                    if (0 != pCapBmp.postThread.qBuf2.initQyQ2(CONST_qType_postEncV, 30, maxDataBufLen, false, pQyMc.cfg.rwLockParam, true, "postThread")) {
                        return iErr;
                    }
                }

                //  2010/12/29
                pCapBmp.compressVideo.debugInfo.nStep = 35;

                tmpCfg = pCfg.toShareVideoQ;
                tmpCfg.name = String.format("%s-%d", tmpCfg.name, pSharedObj.var.uiTranNo);
                tmpCfg.mutexName_prefix = String.format("%s-%d", tmpCfg.mutexName_prefix, pSharedObj.var.uiTranNo);
                tName.tBuf = String.format("%s%d", CONST_evtName_qm_data_v, pSharedObj.var.uiTranNo);
                if (pCapBmp.thread.bQInited) {
                    Log.e("startQThreadToShareAv failed, bQInited is  already true.");
                    return iErr;
                }
                qCfgEx = new genericQueue.GENERIC_Q_cfgEx();
                qCfgEx.common = tmpCfg;
                qCfgEx.pf_QElemRemoveEx = (genericQueue.PF_qElemRemoveEx) cleanEx_myDRAW_VIDEO_DATA;
                if (0 != initQyQ2(qCfgEx, pProcInfo.cfg.b_use2Buf_capBmpQ,pQyMc.cfg.rwLockParam, 2, tName.tBuf, pCapBmp.thread.q2))
                {
                    return iErr;
                }
                pCapBmp.thread.bQInited = true;
                pCapBmp.compressVideo.debugInfo.nStep = 37;
                
                //  2011/12/04
                tmpCfg = pCfg.vppQ;
                str = String.format("%s-%d", new String(tmpCfg.name), pSharedObj.var.uiTranNo);
                tmpCfg.name = str;
                str = String.format("%s-%d", new String(tmpCfg.mutexName_prefix), pSharedObj.var.uiTranNo);
                tmpCfg.mutexName_prefix = str;
                str = String.format("%s%d", CONST_evtName_qm_vpp, pSharedObj.var.uiTranNo);
                tName.tBuf = str;

                if (pCapBmp.vppThread.bQInited) {
                    Log.e("startQThreadToShareAv failed, vppThread.bQInited is  already true.");
                    return iErr;
                }

                qCfgEx = new genericQueue.GENERIC_Q_cfgEx();
                qCfgEx.common = tmpCfg;
                qCfgEx.pf_QElemRemoveEx = cleanEx_myDRAW_VIDEO_DATA;
                if (0 != initQyQ2(qCfgEx, pProcInfo.cfg.b_use2Buf_capBmpVppQ,pQyMc.cfg.rwLockParam, 2, tName.tBuf, pCapBmp.vppThread.q2))
                    return iErr;
                pCapBmp.vppThread.bQInited = true;

                if (null == pCapBmp.postThread.hThread) {
                    pCapBmp.postThread.bQuit = false;
                    //pCapBmp.postThread.hThread=guiShare.GuiShare.pf_startThread1.call( mcThreadProc_toShareVideo_post, iIndex_sharedObj);
                }
            }

            iErr = 0;
            return iErr;
        }

    }
}

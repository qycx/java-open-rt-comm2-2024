


package com.qycx.base;

import static com.qycx.base.genericQueue.qyFuncs.genericQFree;
import static com.qycx.base.genericQueue.qyFuncs.initGenericQ;
import static com.qycx.base.qmcDecAudio.qyFuncs.clean_PKT_info_toDec;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_threadStep_start;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_threadStep_toWait;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_tmpPktEncV;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_setStep;
import static com.qycx.base.qyMcMainMediaProc_decVideo_rtsp.Consts.CONST_mutexNamePrefix_decV_inDataCacheQ;
import static com.qycx.base.qyMcMainMediaProc_decVideo_rtsp.Consts.CONST_mutexNamePrefix_decV_tmpDataQ;
import static com.qycx.base.qyMcMainMediaProc_decVideo_rtsp.qyFuncs.doDecodeVideo;
import static com.qycx.base.qyTmpMemory.qyFuncs.cleanEx_myDRAW_VIDEO_DATA;
import static com.qycx.base.qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA;
import static com.qycx.base.qyq2.qyFuncs.isQ2Empty;
import static com.qycx.base.taskAvProc.qyFuncs.exitTaskAvFrom_video;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;

import android.annotation.SuppressLint;

import com.qycx.base.util.Log;

public class qmcDecVideo {
    public static class qyFuncs {
        @SuppressLint("DefaultLocale")
        public static qmcFunc.PF_threadProc mcThreadProc_decVideo = (Object lpParameter) ->
        {
            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) lpParameter;
            if (null == pTransform) return;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return;
            qmcTransform.TRANSFORM_VIDEO_procInfo pTransformVideo = pTransform.video;
            int i;

            String str1;
            String where_showInfo = "transV";
            {
                qnmCommProc_mis.MIS_MSG_INPUT pMsg = new qnmCommProc_mis.MIS_MSG_INPUT();
                {
                    qnmCommProc_mis.myDRAW_VIDEO_DATA pkt_dec = new qnmCommProc_mis.myDRAW_VIDEO_DATA();
                    {
                        str1 = ("mcThreadProc_decVideo enters");
                        showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, str1);

                        M_setStep(pTransformVideo.debugStep, CONST_threadStep_start);

                        pTransformVideo.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);
                        pTransformVideo.bRunning = true;

                        int uiBufSize_pkt_dec = pProcInfo.cfg.uiBufSize_myDrawVideoData;
                        int iAllocType = 0;

                        genericQueue.GENERIC_Q_cfgEx qCfgEx;    //  2012/11/10
                        genericQueue.GENERIC_Q_CFG tmpCfg = new genericQueue.GENERIC_Q_CFG();
                        String str;

                        int tn = (int) tmpFunc.qyFuncs.myGetTickCount(null);
                        int iTaskId = pTransform.iTaskId;
                        int index_activeMems_from = pTransform.index_activeMems_from;

                        int size;
                        pTransformVideo.pInDataCacheQ = new genericQueue.GENERIC_Q();
                        pTransformVideo.pTmpDataQ = new genericQueue.GENERIC_Q();

                        str = String.format("%s-%d-%d-%d", CONST_mutexNamePrefix_decV_inDataCacheQ, iTaskId, index_activeMems_from, tn);
                        tmpCfg.name = str;
                        tmpCfg.mutexName_prefix = str;
                        tmpCfg.uiMaxQNodes = 100;

                        qCfgEx = new genericQueue.GENERIC_Q_cfgEx();
                        qCfgEx.common = tmpCfg;
                        qCfgEx.pf_QElemRemoveEx = cleanEx_myDRAW_VIDEO_DATA;
                        qCfgEx.pfQElemRemove = clean_myDRAW_VIDEO_DATA;

                        if (0 != initGenericQ(qCfgEx, pTransformVideo.pInDataCacheQ)) {
                            //  2009/05/28
                            exitTaskAvFrom_video(pProcInfo, pTransform, "mcThreadProc_decVideo quit");
                            //  2014/03/24
                            if (null != pTransformVideo.pInDataCacheQ)
                            {
                                genericQFree(pTransformVideo.pInDataCacheQ); pTransformVideo.pInDataCacheQ = null;
                            }
                            if (null != pTransformVideo.pTmpDataQ)
                            {
                                genericQFree(pTransformVideo.pTmpDataQ); pTransformVideo.pTmpDataQ = null;
                            }
                            str1 = String.format("mcThreadProc_decVideo: {%s} ends.", pTransformVideo.q2.cfg.name);
                            showInfo_open.qyFuncs.showInfo_open(0, null, str1);
                            pTransformVideo.bRunning = false;
                            return;
                        }

                        str = String.format("%s-%d-%d-%d", CONST_mutexNamePrefix_decV_tmpDataQ, iTaskId, index_activeMems_from, tn);
                        tmpCfg.name = str;

                        str = String.format("%s-%d-%d-%d", CONST_mutexNamePrefix_decV_tmpDataQ, iTaskId, index_activeMems_from, tn);
                        tmpCfg.mutexName_prefix = str;
                        tmpCfg.uiMaxQNodes = 100;

                        qCfgEx = new genericQueue.GENERIC_Q_cfgEx();
                        qCfgEx.common = tmpCfg;

                        qCfgEx.pf_QElemRemoveEx = cleanEx_myDRAW_VIDEO_DATA;
                        qCfgEx.pfQElemRemove = clean_myDRAW_VIDEO_DATA;

                        if (0 != initGenericQ(qCfgEx, pTransformVideo.pTmpDataQ)) {
                            exitTaskAvFrom_video(pProcInfo, pTransform, "mcThreadProc_decVideo quit");
                            //  2014/03/24
                            if (null != pTransformVideo.pInDataCacheQ)
                            {
                                genericQFree(pTransformVideo.pInDataCacheQ); pTransformVideo.pInDataCacheQ = null;
                            }
                            if (null != pTransformVideo.pTmpDataQ)
                            {
                                genericQFree(pTransformVideo.pTmpDataQ); pTransformVideo.pTmpDataQ = null;
                            }
                            str1 = String.format("mcThreadProc_decVideo: {%s} ends.", pTransformVideo.q2.cfg.name);
                            showInfo_open.qyFuncs.showInfo_open(0, null, str1);
                            pTransformVideo.bRunning = false;
                            return;
                        }
                        pTransformVideo.curPktInfo_toDec.pMIS_MSGU = pMsg;
                        pTransformVideo.curPktInfo_toDec.pPkt_dec = pkt_dec;
                        pMsg.uiType = 0;
                        pTransformVideo.curPktInfo_toDec.pkt_encV.bufSize = bufSize_tmpPktEncV;

                        for (; !pTransformVideo.bQuit; ) {
                            M_setStep(pTransformVideo.debugStep, CONST_threadStep_toWait);

                            int toInMs = 5000;
                            toInMs = 1000;// 33;
                            if (pProcInfo.cfg.b_useVideoQ2_toDecV) {
                                myWaitOne(pTransformVideo.q2.hEvent0, toInMs);
                            } else {
                                myWaitOne(pTransform.v_preTransThread.qBuf2.hEvent0, toInMs);
                            }

                            if (pProcInfo.cfg.b_useVideoQ2_toDecV) {
                                if (isQ2Empty(pTransform.v_preTransThread.q2) && isQ2Empty(pTransform.video.q2)) {
                                    continue;
                                }
                            } else {
                                if (pTransform.v_preTransThread.qBuf2.isQ2Empty()) {
                                    continue;
                                }
                            }

                            //
                            //doDecodeVideo(pTransform, pMsgBuf);
                            doDecodeVideo(pTransform, null);

                            //
                            clean_PKT_info_toDec(pTransform.video.curPktInfo_toDec, (""));
                            //Log.d(TAG,"pTransformVideo clean_PKT_info_toDec");


                            //
                            continue;
                        }
                    }
                }
            }

            Log.i("mcThreadProc_decVideo will quit");
            exitTaskAvFrom_video(pProcInfo, pTransform, "mcThreadProc_decVideo quit");
            //  2014/03/24
            if (null != pTransformVideo.pInDataCacheQ)
            {
                genericQFree(pTransformVideo.pInDataCacheQ); pTransformVideo.pInDataCacheQ = null;
            }
            if (null != pTransformVideo.pTmpDataQ)
            {
                genericQFree(pTransformVideo.pTmpDataQ); pTransformVideo.pTmpDataQ = null;
            }
            str1 = String.format("mcThreadProc_decVideo: {%s} ends.", pTransformVideo.q2.cfg.name);
            showInfo_open.qyFuncs.showInfo_open(0, null, str1);
            pTransformVideo.bRunning = false;

        };
    }
}

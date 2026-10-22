package com.qycx.base;

import static com.qycx.base.Consts.cntof_mutexName_prefix;
import static com.qycx.base.audioRecorder_android.qyFuncs_Droid.bGetCapturePara_audioCapdev_android;
import static com.qycx.base.audioRecorder_android.qyFuncs_Droid.exitAudioCapDev_android;
import static com.qycx.base.audioRecorder_android.qyFuncs_Droid.initAudioCapDev_android;
import static com.qycx.base.audioRecorder_android.qyFuncs_Droid.startAudioCapDev_android;
import static com.qycx.base.audioRecorder_android.qyFuncs_Droid.stopAudioCapDev_android;
import static com.qycx.base.avCapHelpCommProc.Consts.CONST_evtName_qm_app;
import static com.qycx.base.avCapHelpCommProc.Consts.CONST_evtName_qm_data_a;
import static com.qycx.base.genericQueue.Consts.cntof_qCfg_name;
import static com.qycx.base.qmcSharedObj.Consts.CONST_capType_av;
import static com.qycx.base.qmcSharedObj.Consts.CONST_recordType_android;
import static com.qycx.base.qmcSharedObj.Consts.CONST_recordType_custom;
import static com.qycx.base.qmcSharedObj.Consts.CONST_recordType_dSound;
import static com.qycx.base.qmcSharedObj.Consts.CONST_recordType_directX;
import static com.qycx.base.qmcSharedObj.Consts.CONST_recordType_webRtc;
import static com.qycx.base.qyTmpMemory.qyFuncs.clean_myPLAY_AUDIO_DATA;

import com.qycx.base.util.javatuples.Pair;

public class audioRecorder_dsound {
    public static class qyFuncs_Droid {
        public static int startLocalAudioRecorder(int uiRecordType, Object pGuid, String capDevDesc, int iIndex_sharedObj, qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pCompressor) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;

            qnmCommProc_mis.QY_qThreadProcInfo_common pRecThread = pProcInfo.av.localAv.recordSoundProcInfo.recThread;
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, iIndex_sharedObj);
            if (null == pSharedObj){
                stopLocalAudioRecorder.call(pProcInfo, iIndex_sharedObj, 1);
                return iErr;
            }
            pSharedObj.var.bSoundRecorder = true;

            //  2013/03/18
            if (pProcInfo.av.localAv.recordSoundProcInfo.bCapDevConnected){
                stopLocalAudioRecorder.call(pProcInfo, iIndex_sharedObj, 1);
                return iErr;
            }

            pProcInfo.av.localAv.recordSoundProcInfo.uiType = CONST_capType_av;
            pProcInfo.av.localAv.recordSoundProcInfo.iIndex_sharedObj = iIndex_sharedObj;
            pProcInfo.av.localAv.recordSoundProcInfo.uiRecordType = uiRecordType;

            qyCommProc_open.WAVEFORMATEX pWf_input = null;
            qyCommProc_open.WAVEFORMATEX wf_input;

            //  2013/03/18
            switch (uiRecordType) {
                case CONST_recordType_directX:

                    break;
                case CONST_recordType_android:
                    if (0 != initAudioCapDev_android(null, "", null, pCompressor, false,
                            pQyMc.gui.hMainWnd, iIndex_sharedObj, pProcInfo.av.localAv.recordSoundProcInfo.u_android.pAudioCapDev))
                    {
                        stopLocalAudioRecorder.call(pProcInfo, iIndex_sharedObj, 1);
                        return iErr;
                    }
                    pProcInfo.av.localAv.recordSoundProcInfo.bCapDevConnected = true;

                    Pair<qyCommProc_open.WAVEFORMATEX, Integer> pair = bGetCapturePara_audioCapdev_android(pProcInfo, pSharedObj.var.iIndex_capAudio, 8,
                            pProcInfo.av.localAv.recordSoundProcInfo.u_android.pAudioCapDev, null);
                    pProcInfo.av.localAv.recordSoundProcInfo.wf_org = pair.getValue0();
                    pProcInfo.av.localAv.recordSoundProcInfo.iRecorder_audioSessionId = pair.getValue1();
                    wf_input = pair.getValue0();
                    pWf_input = wf_input;

                    break;
                default:
                    break;
            }
            if (null == pWf_input) {
                pProcInfo.av.localAv.recordSoundProcInfo.wf_org = pCompressor.wf_decompress_default;
                wf_input = pProcInfo.av.localAv.recordSoundProcInfo.wf_org;
                pWf_input = wf_input;
            }

            //  2013/08/02
            qmcSharedObj.CAP_procInfo_recordSound pCapAudio = null;
            pCapAudio = pProcInfo.av.localAv.recordSoundProcInfo;

            //
            //  2016/12/10
            pCompressor.pAudioQ2 = pCapAudio.thread.q2;
            pCompressor.pParent_transform = pCapAudio;
            //
            if (0 != compressAudio.qyFuncs.initCompressAudio(pProcInfo, pWf_input,
                    pCompressor, pProcInfo.av.localAv.recordSoundProcInfo.compressAudio)) {
                stopLocalAudioRecorder.call(pProcInfo, iIndex_sharedObj, 1);
                return iErr;
            }

            //  2013/08/02
            genericQueue.GENERIC_Q_CFG tmpCfg;
            String str_tName;
            {
                tmpCfg = pProcInfo.cfg.toShareAudioQ;
                tmpCfg.name = String.format("%s-%d", tmpCfg.name, pSharedObj.var.uiTranNo);
                tmpCfg.mutexName_prefix = String.format("%s-%d", tmpCfg.mutexName_prefix, pSharedObj.var.uiTranNo);

                str_tName = String.format("%s-%d", CONST_evtName_qm_data_a, pSharedObj.var.uiTranNo);
                if (0 != qyq2.qyFuncs.initQyQ2(tmpCfg, pProcInfo.cfg.b_use2Buf_capAudioQ,  pQyMc.cfg.rwLockParam, 2, str_tName, null, null, clean_myPLAY_AUDIO_DATA, pCapAudio.thread.q2)) {
                    stopLocalAudioRecorder.call(pProcInfo, iIndex_sharedObj, 1);
                    return iErr;
                }
                pCapAudio.thread.bQInited = true;

                //  2017/01/29
                tmpCfg = pProcInfo.cfg.appQ;
                tmpCfg.name = String.format("%s-%d", tmpCfg.name, pSharedObj.var.uiTranNo);
                tmpCfg.mutexName_prefix = String.format("%s-%d", tmpCfg.mutexName_prefix, pSharedObj.var.uiTranNo);

                str_tName = String.format("%s%d", CONST_evtName_qm_app, pSharedObj.var.uiTranNo);
                if (0 != qyq2.qyFuncs.initQyQ2(tmpCfg, pProcInfo.cfg.b_use2Buf_capAudioAppQ,  pQyMc.cfg.rwLockParam, 2, str_tName, null, null, clean_myPLAY_AUDIO_DATA, pCapAudio.appThread.q2)) {
                    stopLocalAudioRecorder.call(pProcInfo, iIndex_sharedObj, 1);
                    return iErr;
                }
                pCapAudio.appThread.bQInited = true;

                if (null == pCapAudio.thread.hThread) {
                    pCapAudio.thread.bQuit = false;
                    //这里启动压缩工作线程，待命
                    //采集线程采集到的数据，会通过processInput_post发送到压缩工作线程的队列中(pCapProcInfo.thread.q2)
                    pCapAudio.thread.hThread=guiShare.GuiShare.pf_startThread1.call( qyMcMainMediaProc_encAudio.qyFuncs.mcThreadProc_toShareAudio, iIndex_sharedObj);
                }

                //  2017/01/29
                if (null == pCapAudio.appThread.hThread) {
                    pCapAudio.appThread.bQuit = false;
                    pCapAudio.appThread.hThread=guiShare.GuiShare.pf_startThread1.call(qyMcMainMediaProc_app.qyFuncs.mcThreadProc_app, iIndex_sharedObj);
                }
            }
            pRecThread.bQuit = false;
            pRecThread.bRunning = false;

            switch (uiRecordType) {
                case CONST_recordType_android:
                    if (0 != startAudioCapDev_android(pProcInfo.av.localAv.recordSoundProcInfo.u_android.pAudioCapDev)) {
                        stopLocalAudioRecorder.call(pProcInfo, iIndex_sharedObj, 1);
                        return iErr;
                    }
                    //这里启动音频采集线程
                    pRecThread.hThread=guiShare.GuiShare.pf_startThread1.call( audioRecorder_android.qyFuncs_Droid.recThreadFunc_android, 0);
                    break;
                case CONST_recordType_custom:
                {
                    stopLocalAudioRecorder.call(pProcInfo, iIndex_sharedObj, 1);
                    return iErr;
                }
                default:
                    stopLocalAudioRecorder.call(pProcInfo, iIndex_sharedObj, 1);
                    return iErr;
            }
            iErr = 0;
            return iErr;
        }

        public static guiShare.PF_stopLocalAudioRecorder stopLocalAudioRecorder = (ctxQmc.CCtxQmc pProcInfo, int index_sharedObj, int nTries) -> {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            boolean bDbg = false;
            String str;

            if (null == pProcInfo) return -1;

            str = ("stopLocalAudioRecorder enters.");

            qnmCommProc_mis.QY_qThreadProcInfo_common pRecThread = pProcInfo.av.localAv.recordSoundProcInfo.recThread;

            pRecThread.bQuit = true;

            //  2013/03/19
            switch (pProcInfo.av.localAv.recordSoundProcInfo.uiRecordType) {
                case CONST_recordType_dSound:

                    //  if  (  !nTries  )  nTries  =  10;
                    nTries = 30;    //  这里必须停下来

                    int i;
                    for (i = 0; i < nTries; i++) {
                        if (!qnmCommProc_mis.qyFuncs.bQThreadExists(pRecThread)) break;

                        qnmCommProc_mis.qyFuncs.waitForQThread(pRecThread, 1000);
                    }
                    if (i == nTries) {   //  2013/08/02
                        //qyExitProcess(("stopLocalAudioRecorder failed"));
                    }
                    //
                    break;
                case CONST_recordType_directX:
                    //exitAudioCapDev_dx(pProcInfo.av.localAv.recordSoundProcInfo.u.dx.pAudioCapDev);
                    pProcInfo.av.localAv.recordSoundProcInfo.bCapDevConnected = false;
                    break;
                case CONST_recordType_webRtc:
                    //stopRecord_webRtc();
                    break;
                case CONST_recordType_android:
                    stopAudioCapDev_android(pProcInfo.av.localAv.recordSoundProcInfo.u_android.pAudioCapDev);
                    //
                    nTries = 30;    //  这里必须停下来

                    for (i = 0; i < nTries; i++) {
                        if (!qnmCommProc_mis.qyFuncs.bQThreadExists(pRecThread)) break;

                        qnmCommProc_mis.qyFuncs.waitForQThread(pRecThread, 1000);
                    }
                    if (i == nTries) {   //  2013/08/02
                        //qyExitProcess(("stopLocalAudioRecorder failed"));
                    }
                    //
                    exitAudioCapDev_android(pProcInfo.av.localAv.recordSoundProcInfo.u_android.pAudioCapDev);
                    //
                    pProcInfo.av.localAv.recordSoundProcInfo.bCapDevConnected = false;
                    //
                    break;
                default:
                    break;
            }

            //  2013/08/02
            qmcSharedObj.CAP_procInfo_audioCommon pU = (qmcSharedObj.CAP_procInfo_audioCommon) pProcInfo.av.localAv.recordSoundProcInfo;

            //  2017/01/29
            qmcFunc.qyFuncs.stopQThread(pU.appThread, ("stopLocalAudioRecoder"));
            //  2009/12/07
            qmcFunc.qyFuncs.stopQThread(pU.thread, ("stopLocalAudioRecorder"));

            //  2017/01/29
            if (pU.appThread.bQInited) {
                qyq2.qyFuncs.exitQyQ2(pU.appThread.q2);
                pU.appThread.bQInited = false;
            }
            if (pU.thread.bQInited) {
                qyq2.qyFuncs.exitQyQ2(pU.thread.q2);
                pU.thread.bQInited = false;
            }

            if (!qnmCommProc_mis.qyFuncs.bQThreadExists(pRecThread)) {
                compressAudio.qyFuncs.exitCompressAudio(pProcInfo, pProcInfo.av.localAv.recordSoundProcInfo.compressAudio);
            }
            return 0;
        };
    }
}

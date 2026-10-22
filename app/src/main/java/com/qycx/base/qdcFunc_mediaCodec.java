package com.qycx.base;

import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_mediaCodec;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_mediaSdk_dec;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_setMyStep;
import static com.qycx.base.qyMcMainCusModule.qyFuncs.M_getCompressModule;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.exitTaskAvFrom_audio;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.initTaskAvFrom_audio;
import static com.qycx.base.taskAvProc.qyFuncs.exitTaskAvFrom_video;
import static com.qycx.base.taskAvProc.qyFuncs.initTaskAvFrom_video;

import android.annotation.SuppressLint;

import com.qycx.base.util.Log;

public class qdcFunc_mediaCodec {

    public static class qyFuncs_Droid {

        //  mediaSdk的处理和其他的解压缩不同，是一直自己循环。为了避免从其他的解压缩处理中间跳到这里，需要先exitTaskVideo。清理一下
        public static guiShare.PF_doDecodeVideo_mediaCodec doDecodeVideo_mediaCodec=(qmcTransform.QY_TRANSFORM pTransform, int index_activeMems_from, qnmCommProc_mis.MIS_MSGU pMsgBufParam_unused)-> {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//QY_GET_procInfo_isCli(  );
            if (null == pProcInfo) return -1;

            boolean bDbg = false;
            String str;

            //
            qnmCommProc_mis.MIS_MSGU pMsgBuf_unused =null;// (qnmCommProc_mis.MIS_MSGU ) pMsgBufParam_unused;

            //
            qyMcMainCusModule.CUS_MODULES pCusModules = (qyMcMainCusModule.CUS_MODULES) pProcInfo.getCusModules();
            if (null == pCusModules) {
                return -1;
            }
            qnmCommProc_mis.MSGR_ADDR pAddr = pTransform.pAddr_logicalPeer;
            if (null == pAddr) return -1;
            int iTaskId = pTransform.iTaskId;
            //
            //AV_TRAN_INFO pTranInfo_unused = (AV_TRAN_INFO)pTransform.pTranInfo_unused;
            //if (!pTranInfo_unused) return -1;
            //
            taskAv.PROC_TASK_AV pTask = (taskAv.PROC_TASK_AV) pTransform.pTask;
            if (null == pTask) return -1;

            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, (ctxQmc.CCtxQmc) pProcInfo, pTask.iIndex_taskInfo, "doDecodeVideo_mediaCodec");
            if (null == pTaskInfo) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;
            if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            if (index_activeMems_from < 0 || index_activeMems_from >= videoConference.usCntLimit_activeMems_from)
                return -1;
            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_activeMems_from];
            //
            exitTaskAvFrom_video(pProcInfo, pTransform, "doDecodeVideo_mediaCodec before init");


            M_setMyStep(pTransform.video.debugStep, 21);

            //
            do {

                //  if  (  !pFrom->video.ucbInited  )
                if (!pTransform.video.ucbInited) {
                    int uiModuleType_suggested = CONST_moduleType_mediaSdk_dec;
                    boolean bUseDecTool = false;
                    if (0 != initTaskAvFrom_video(pProcInfo, pActiveMem_from, index_activeMems_from, pTransform.iTaskId, uiModuleType_suggested, bUseDecTool, pTransform)) {
                        {
                            taskAvProc.qyFuncs.exitTaskAvFrom_video(pProcInfo, pTransform,"doDecodeVideo_mediaCodec quit");
                            return iErr;
                        }
                    }
                    //
                    str = String.format("procTaskVideo: initTaskAvFrom_video %s", index_activeMems_from);
                    Log.d( "doDecodeVideo_mediaCodec: " + str);
                }
                if (!pTransform.video.ucbInited) {
                    Log.e( "procTaskVideo failed: video is not inited");
                    taskAvProc.qyFuncs.exitTaskAvFrom_video(pProcInfo, pTransform,"doDecodeVideo_mediaCodec quit");
                    return iErr;
                }

                //
                if (pTransform.video.uiModuleType_toDec != CONST_moduleType_mediaCodec) {
                    taskAvProc.qyFuncs.exitTaskAvFrom_video(pProcInfo, pTransform,"doDecodeVideo_mediaCodec quit");
                    return iErr;
                }

                //
                qyMcMainCusModule.CUS_MODULE_compress pModule = M_getCompressModule(pCusModules, pTransform.video.uiModuleType_toDec);
                if (pModule == null) {
                    taskAvProc.qyFuncs.exitTaskAvFrom_video(pProcInfo, pTransform,"doDecodeVideo_mediaCodec quit");
                    return iErr;
                }

                //
                if (null == pModule.pf_qdcDecompressVideoRun) {
                    taskAvProc.qyFuncs.exitTaskAvFrom_video(pProcInfo, pTransform,"doDecodeVideo_mediaCodec quit");
                    return iErr;
                }

                try {
                    if (0 != pModule.pf_qdcDecompressVideoRun.call(pTransform.video.u_qoi.myQoi.common)) {
                        taskAvProc.qyFuncs.exitTaskAvFrom_video(pProcInfo, pTransform,"doDecodeVideo_mediaCodec quit");
                        return iErr;
                    }
                } catch (Exception e) {
                    Log.e(e.getMessage());
                    e.printStackTrace();
                    taskAvProc.qyFuncs.exitTaskAvFrom_video(pProcInfo, pTransform,"doDecodeVideo_mediaCodec quit");
                    return iErr;
                }


                iErr = 0;

            } while ( false  );
            

            //
            exitTaskAvFrom_video(pProcInfo, pTransform, "doDecodeVideo_mediaCodec quit");

            return iErr;
        };


        //
        //
        public static guiShare.PF_doEncodeVideo_mediaCodec doEncodeVideo_mediaCodec=(qmcSharedObj.CAP_procInfo_bmpCommon pCapProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, byte[] pMsgBufParam)-> {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            qnmCommProc_mis.MIS_MSGU pMsgBuf =null;// (qnmCommProc_mis.MIS_MSGU ) pMsgBufParam;

            //
            qyMcMainCusModule.CUS_MODULES pCusModules = (qyMcMainCusModule.CUS_MODULES) pProcInfo.getCusModules();
            if (null == pCusModules) {
                return -1;
            }

            qyMcMainCusModule.CUS_MODULE_compress pModule = M_getCompressModule(pCusModules, CONST_moduleType_mediaCodec);
            if (pModule == null) return iErr;

            if (null == pModule.pf_qdcCompressVideoRun) {
                return iErr;
            }

            try {
                if (0 != pModule.pf_qdcCompressVideoRun.call(pCapProcInfo.compressVideo.u_qoi.myQoi.common))
                    return iErr;
            } catch (Exception e) {
                e.printStackTrace();
                return iErr;
            }

            iErr = 0;

            
            return iErr;
        };


        //
        @SuppressLint("SuspiciousIndentation")
        public static guiShare.PF_doDecodeAudio_mediaCodec doDecodeAudio_mediaCodec=(qmcTransform.QY_TRANSFORM pTransform, int index_pMems_from1, int index_activeMems_from, qnmCommProc_mis.MIS_MSGU pMsgBufParam_unused)-> {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc) pQyMc.get_pProcInfo();//QY_GET_procInfo_isCli(  );
            if (null == pProcInfo) return -1;

            String str;


            qnmCommProc_mis.MIS_MSGU pMsgBuf_unused =null;// (qnmCommProc_mis.MIS_MSGU ) pMsgBufParam_unused;

            qyMcMainCusModule.CUS_MODULES pCusModules = (qyMcMainCusModule.CUS_MODULES) pProcInfo.getCusModules();
            if (null == pCusModules) {
                return -1;
            }
            qnmCommProc_mis.MSGR_ADDR pAddr = pTransform.pAddr_logicalPeer;
            if (null == pAddr) return -1;
            int iTaskId = pTransform.iTaskId;
            //
            qnmCommProc_mis.AV_TRAN_INFO pTranInfo_unused = (qnmCommProc_mis.AV_TRAN_INFO) pTransform.pTranInfo_unused;
            if (null == pTranInfo_unused) return -1;
            //
            taskAv.PROC_TASK_AV pTask = (taskAv.PROC_TASK_AV) pTransform.pTask;
            if (null == pTask) return -1;

            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, (ctxQmc.CCtxQmc) pProcInfo, pTask.iIndex_taskInfo, "");
            if (null == pTaskInfo) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (null == pTaskData) return -1;
            if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            if (index_activeMems_from < 0 || index_activeMems_from >= videoConference.usCntLimit_activeMems_from)
                return -1;
            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_activeMems_from];
            exitTaskAvFrom_audio(pProcInfo, pTransform, "doDecodeAudio_mediaCodec enters");
            M_setMyStep(pTransform.audio.debugStep, 21);

            do {

                //  if  (  !pFrom->video.ucbInited  )
                if (!pTransform.audio.ucbInited) {
                    int uiModuleType_suggested = CONST_moduleType_mediaCodec;
                    boolean bUseDecTool = false;
                    if (0 != initTaskAvFrom_audio(pProcInfo, pActiveMem_from, index_activeMems_from, pTransform)) {
                        exitTaskAvFrom_audio(pProcInfo, pTransform,"doDecodeAudio_mediaCodec leaves");
                        return iErr;
                    }
                    //
                    str = String.format("procTaskVideo: initTaskAvFrom_audio %d", index_activeMems_from);
                    showInfo_open.qyFuncs.showInfo_open(str);
                }
                if (!pTransform.audio.ucbInited) {
                    Log.e( "procTaskVideo failed: video is not inited");
                    exitTaskAvFrom_audio(pProcInfo, pTransform,"doDecodeAudio_mediaCodec leaves");
                    return iErr;
                }

                //
                if (pTransform.audio.uiModuleType_toDec != CONST_moduleType_mediaCodec) {
                    exitTaskAvFrom_audio(pProcInfo, pTransform,"doDecodeAudio_mediaCodec leaves");
                    return iErr;
                }

                //
                qyMcMainCusModule.CUS_MODULE_compress pModule = M_getCompressModule(pCusModules, pTransform.audio.uiModuleType_toDec);
                if (pModule == null) {
                    exitTaskAvFrom_audio(pProcInfo, pTransform,"doDecodeAudio_mediaCodec leaves");
                    return iErr;
                }

                //
                if (null == pModule.pf_qdcDecompressAudioRun) {
                    exitTaskAvFrom_audio(pProcInfo, pTransform,"doDecodeAudio_mediaCodec leaves");
                    return iErr;
                }

                try {
                    if (0 != pModule.pf_qdcDecompressAudioRun.call(pTransform.audio.u_qoi.myQoi.common)) {
                        exitTaskAvFrom_audio(pProcInfo, pTransform,"doDecodeAudio_mediaCodec leaves");
                        return iErr;
                    }
                } catch (Exception e) {
                    exitTaskAvFrom_audio(pProcInfo, pTransform,"doDecodeAudio_mediaCodec leaves");
                    return iErr;
                }

                iErr = 0;

            }  while  ( false );

            //
            exitTaskAvFrom_audio(pProcInfo, pTransform, "doDecodeAudio_mediaCodec leaves");


            return iErr;
        };


    }
}



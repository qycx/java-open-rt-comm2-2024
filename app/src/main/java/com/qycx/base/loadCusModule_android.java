package com.qycx.base;

import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_ffmpeg;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_mediaCodec;

public class loadCusModule_android {
    public static class qyFuncs_Droid {
        public static int newCusModuleIndex() {
            int index = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

            for (index = 0; index < pQyMc.cusModules.mems.length; index++) {
                if (pQyMc.cusModules.mems[index] == null) break;
                continue;
            }

            if (index == pQyMc.cusModules.mems.length) return -1;

            return index;

        }

        public static guiShare.PF_loadCusModule_android loadCusModule_android=()-> {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            qyMcMainCusModule.CUS_MODULE_compress pModule = null;
            int index;

            //
            index = newCusModuleIndex();
            if (index < 0) return -1;

            //
            pQyMc.cusModules.mems[index] = new qyMcMainCusModule.CUS_MODULE_compress();
            pQyMc.cusModules.mems[index].common.uiType = CONST_moduleType_ffmpeg;

            //
            pModule = pQyMc.cusModules.mems[index];

            //
            pModule.common.bLoaded = true;

            //
            pModule.pf_qdcInit = cusModule_ffmpeg.qyFuncs_Droid.qdcInit_ffmpeg;
            pModule.pf_qdcExit = cusModule_ffmpeg.qyFuncs_Droid.qdcExit_ffmpeg;


            pModule.pf_qdcInitCompressVideo = cusModule_ffmpeg.qyFuncs_Droid.qdcInitCompressVideo_ffmpeg;
            pModule.pf_qdcExitCompressVideo = cusModule_ffmpeg.qyFuncs_Droid.qdcExitCompressVideo_ffmpeg;
            pModule.pf_qdcGetSpsPps = cusModule_ffmpeg.qyFuncs_Droid.qdcGetSpsPps_ffmpeg;
            pModule.pf_qdcCompressVideo = cusModule_ffmpeg.qyFuncs_Droid.qdcCompressVideo_ffmpeg;
            //
            pModule.pf_qdcInitDecompressVideo = cusModule_ffmpeg.qyFuncs_Droid.qdcInitDecompressVideo_ffmpeg;
            pModule.pf_qdcExitDecompressVideo = cusModule_ffmpeg.qyFuncs_Droid.qdcExitDecompressVideo_ffmpeg;
            pModule.pf_qdcDecompressVideo = cusModule_ffmpeg.qyFuncs_Droid.qdcDecompressVideo_ffmpeg;
            //
            pModule.pf_qdcGetOutputByIndex = cusModule_ffmpeg.qyFuncs_Droid.qdcGetOutputByIndex_ffmpeg;
            pModule.pf_qdcClearOutputs = cusModule_ffmpeg.qyFuncs_Droid.qdcClearOutputs_ffmpeg;

            //
            pModule.pf_qdcInitCompressAudio = cusModule_ffmpeg.qyFuncs_Droid.qdcInitCompressAudio_ffmpeg;
            pModule.pf_qdcExitCompressAudio = cusModule_ffmpeg.qyFuncs_Droid.qdcExitCompressAudio_ffmpeg;
            pModule.pf_qdcCompressAudio = cusModule_ffmpeg.qyFuncs_Droid.qdcCompressAudio_ffmpeg;
            pModule.pf_qdcCompressAudioRun = cusModule_ffmpeg.qyFuncs_Droid.qdcCompressAudioRun_ffmpeg;                      //  2016/12/05

            //
            pModule.pf_qdcInitDecompressAudio = cusModule_ffmpeg.qyFuncs_Droid.qdcInitDecompressAudio_ffmpeg;
            pModule.pf_qdcExitDecompressAudio = cusModule_ffmpeg.qyFuncs_Droid.qdcExitDecompressAudio_ffmpeg;
            pModule.pf_qdcDecompressAudio = cusModule_ffmpeg.qyFuncs_Droid.qdcDecompressAudio_ffmpeg;
            pModule.pf_qdcDecompressAudioRun = cusModule_ffmpeg.qyFuncs_Droid.qdcDecompressAudioRun_ffmpeg;                  //  2016/12/05

            index = newCusModuleIndex();
            if (index < 0) return -1;

            //
            pQyMc.cusModules.mems[index] = new qyMcMainCusModule.CUS_MODULE_compress();
            pQyMc.cusModules.mems[index].common.uiType = CONST_moduleType_mediaCodec;

            //
            pModule = pQyMc.cusModules.mems[index];

            //
            pModule.common.bLoaded = true;

            //
            pModule.pf_qdcInit = cusModule_mediaCodec.qyFuncs_Droid.qdcInit_mediaCodec;
            pModule.pf_qdcExit = cusModule_mediaCodec.qyFuncs_Droid.qdcExit_mediaCodec;

            //
            pModule.pf_qdcInitDecompressVideo = cusModule_mediaCodec.qyFuncs_Droid.qdcInitDecompressVideo_mediaCodec;
            pModule.pf_qdcExitDecompressVideo = cusModule_mediaCodec.qyFuncs_Droid.qdcExitDecompressVideo_mediaCodec;
            pModule.pf_qdcDecompressVideo = cusModule_mediaCodec.qyFuncs_Droid.qdcDecompressVideo_mediaCodec;
            pModule.pf_qdcDecompressVideoRun = cusModule_mediaCodec.qyFuncs_Droid.qdcDecompressVideoRun_mediaCodec;
            //
            pModule.pf_qdcInitCompressVideo = cusModule_mediaCodec.qyFuncs_Droid.qdcInitCompressVideo_mediaCodec;
            pModule.pf_qdcExitCompressVideo = cusModule_mediaCodec.qyFuncs_Droid.qdcExitCompressVideo_mediaCodec;
            pModule.pf_qdcGetSpsPps = cusModule_mediaCodec.qyFuncs_Droid.qdcGetSpsPps_mediaCodec;
            pModule.pf_qdcCompressVideoRun = cusModule_mediaCodec.qyFuncs_Droid.qdcCompressVideoRun_mediaCodec;
            //
            //
            pModule.pf_qdcInitDecompressAudio = cusModule_mediaCodec.qyFuncs_Droid.qdcInitDecompressAudio_mediaCodec;
            pModule.pf_qdcExitDecompressAudio = cusModule_mediaCodec.qyFuncs_Droid.qdcExitDecompressAudio_mediaCodec;
            pModule.pf_qdcDecompressAudio = cusModule_mediaCodec.qyFuncs_Droid.qdcDecompressAudio_mediaCodec;
            pModule.pf_qdcDecompressAudioRun = cusModule_mediaCodec.qyFuncs_Droid.qdcDecompressAudioRun_mediaCodec;                  //  2016/12/05

            //
            iErr = 0;

            return iErr;

        };


    }
}

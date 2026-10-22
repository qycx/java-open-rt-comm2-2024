
package com.qycx.base;

import static com.qycx.base.qmcVideoCapture.Consts.CONST_audioCompressors_ffmpeg;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_ffmpeg;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_HEVC;
import static com.qycx.base.qyCommProc_open.Consts.WAVE_FORMAT_MPEGLAYER3;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_1080p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_480p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_720p;

public class dlgPolicyIsClient {
    public static class qyFuncs_Droid {
        public final static int CONST_uiMinTimeInMsOfAudioDataPlaying_05s = 150;
        public final static int CONST_uiAudioDataAddedToPlayEveryTime_05s = 120;
        public final static int CONST_uiNotifyIntervalInMs_needMoreAudioData_05s = 70;
        public final static int CONST_uiMinTimeInMsToPlay_05s = 100;
        public final static int CONST_uiMaxTimeInMsToPlay_05s = 150;
        public final static int CONST_uiMaxTimeInMsToBeRemoved_05s = 120;

        public final static int CONST_uiAudioDataAddedToPlayEveryTime_001s = 10;

        public final static int DEFAULT_uiMinTimeInMsOfAudioDataPlaying = CONST_uiMinTimeInMsOfAudioDataPlaying_05s;
        public final static int DEFAULT_uiAudioDataAddedToPlayEveryTime = CONST_uiAudioDataAddedToPlayEveryTime_001s;// CONST_uiAudioDataAddedToPlayEveryTime_05s;
        public final static int DEFAULT_uiNotifyIntervalInMs_needMoreAudioData = CONST_uiNotifyIntervalInMs_needMoreAudioData_05s;
        public final static int DEFAULT_uiMinTimeInMsToPlay = CONST_uiMinTimeInMsToPlay_05s;//  2012/01/27
        public final static int DEFAULT_uiMaxTimeInMsToPlay = CONST_uiMaxTimeInMsToPlay_05s;
        public final static int DEFAULT_uiMaxTimeInMsToBeRemoved = CONST_uiMaxTimeInMsToBeRemoved_05s;


        public static guiShare.PF_initPolicyAvParams initPolicyAvParams=()->  //  要在登陆后立即运行此函数. 2011/10/22
        {
            return 0;
        };


        public static int fixPlayCfg(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_is_open.AUDIO_PLAY_CFG pPlayCfg) {
            if (null == pProcInfo) return -1;
            //policyAvParams.PolicyAvParams & gAvParams = pProcInfo->p_gAvParams;   //  QY_GET_PolicyAvParams(  );

            int iNotifyInterval = 0;

            //if (pPlayCfg.usMinTimeInMsOfAudioDataPlaying < MIN_uiMinTimeInMsOfAudioDataPlaying || pPlayCfg.usMinTimeInMsOfAudioDataPlaying > MAX_uiMinTimeInMsOfAudioDataPlaying)
            pPlayCfg.usMinTimeInMsOfAudioDataPlaying = DEFAULT_uiMinTimeInMsOfAudioDataPlaying;
            //if (pPlayCfg.usAudioDataAddedToPlayEveryTime < MIN_uiAudioDataAddedToPlayEveryTime || pPlayCfg.usAudioDataAddedToPlayEveryTime > MAX_uiAudioDataAddedToPlayEveryTime)
            pPlayCfg.usAudioDataAddedToPlayEveryTime = DEFAULT_uiAudioDataAddedToPlayEveryTime;

            iNotifyInterval = pPlayCfg.usNotifyIntervalInMs_needMoreAudioData;
            //if (iNotifyInterval < MIN_uiNotifyIntervalInMs_needMoreAudioData || iNotifyInterval > MAX_uiNotifyIntervalInMs_needMoreAudioData)
            iNotifyInterval = DEFAULT_uiNotifyIntervalInMs_needMoreAudioData;
            pPlayCfg.usNotifyIntervalInMs_needMoreAudioData = (int) iNotifyInterval;

            if (0 == pPlayCfg.usMinTimeInMsToPlay)
                pPlayCfg.usMinTimeInMsToPlay = DEFAULT_uiMinTimeInMsToPlay;            //  2012/01/27
            if (0 == pPlayCfg.usMaxTimeInMsToPlay)
                pPlayCfg.usMaxTimeInMsToPlay = DEFAULT_uiMaxTimeInMsToPlay;
            if (0 == pPlayCfg.usMaxTimeInMsToBeRemoved)
                pPlayCfg.usMaxTimeInMsToBeRemoved = DEFAULT_uiMaxTimeInMsToBeRemoved;

            return 0;
        }

        public static int myGetAvCompressorCfg(int uiCapType, int uiSubCapType, int iCapUsage, int level, qnmCommProc_mis.AV_COMPRESSOR_CFG pCompressor) {
            int iErr = -1;
            //qyFuncs.safeStrnCpy("h264", pCompressor.video.common.fourccStr, CONST_fourccStrLen + 1);
            //pCompressor.video.common.head.iFourcc = CONST_fourcc_h264;
            pCompressor.video.common.head.iFourcc = CONST_fourcc_HEVC;

            //
            pCompressor.video.common.head.ucbNeedKeyFrameFirst = 1;

            //
            pCompressor.video.common.head.usMaxFps_toShareBmp = 30;
            pCompressor.video.common.head.usMinFps_toShareBmp = 5;

            //
            switch (level) {
                case CONST_policyAvLevel_480p:
                    pCompressor.video.common.head.iWidth_pic = 480;
                    pCompressor.video.common.head.iHeight_pic = 864;// 640;
                    pCompressor.video.common.head.iAvgBitrate = 600000;
                    break;
                case CONST_policyAvLevel_720p:
                    pCompressor.video.common.head.iWidth_pic = 720;
                    pCompressor.video.common.head.iHeight_pic = 1280;// 640;
                    pCompressor.video.common.head.iAvgBitrate = 1000000;
                    break;
                case CONST_policyAvLevel_1080p:
                    pCompressor.video.common.head.iWidth_pic = 1080;
                    pCompressor.video.common.head.iHeight_pic = 1920;// 640;
                    pCompressor.video.common.head.iAvgBitrate = 1800000;
                    break;
                default:
                    pCompressor.video.common.head.iWidth_pic = 480;
                    pCompressor.video.common.head.iHeight_pic = 864;// 640;
                    pCompressor.video.common.head.iAvgBitrate = 600000;
                    //

                    //
                    break;
            }

            //
            pCompressor.video.common.head.ucCompressors = CONST_videoCompressors_ffmpeg;

            //
            policyIsClientFunc.qyFuncs_Droid.get_pcm_forAndroid(uiCapType, pCompressor.audio.wf_decompress_default);

            //
            pCompressor.audio.wf_compress.wFormatTag = WAVE_FORMAT_MPEGLAYER3;
            pCompressor.audio.common.head.ucCompressors = CONST_audioCompressors_ffmpeg;


            //
            iErr = 0;
            
            return iErr;
        }


    }
}

package com.qycx.base;

import static com.qycx.base.policyAvParams.Consts.DEFAULT_conf_fourcc_others;
import static com.qycx.base.qmcSharedObj.Consts.CONST_capType_av;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_ffmpeg;
import static com.qycx.base.qnmCommProc_mis.Consts.DEFAULT_usMaxSpeakers;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_1080p;
import static com.qycx.base.qyMcMainRealTimeMediaProc.Consts.DEFAULT_iHertz_pcm_av;
import static com.qycx.base.qyMcMainRealTimeMediaProc.Consts.DEFAULT_nChannels_pcm_av;
import static com.qycx.base.qyMcMainRealTimeMediaProc.Consts.DEFAULT_wBitsPerSample_pcm_av;

public class policyIsClientFunc {
    public static class qyFuncs_Droid {
        public static int MACRO_getuiBytesRecorded_perBlockAlign(qnmCommProc_mis.AUDIO_COMPRESSOR_CFG audioCompressor, qyCommProc_open.WAVEFORMATEX wf_org) {
            qyCommProc_open.WAVEFORMATEX pWf_org = wf_org;
            {
                int uiBytesRecorded_perBlockAlign = -1;

                if (0 == (pWf_org.nSamplesPerSec % 25)) {
                    uiBytesRecorded_perBlockAlign = (int) (pWf_org.nSamplesPerSec / 25);
                    return uiBytesRecorded_perBlockAlign;
                }

                if (0 == (pWf_org.nSamplesPerSec % 100)) {
                    uiBytesRecorded_perBlockAlign = (int) (pWf_org.nSamplesPerSec / 100 * 12);
                    return uiBytesRecorded_perBlockAlign;
                }

                uiBytesRecorded_perBlockAlign = (int) pWf_org.nSamplesPerSec / 5;

                return uiBytesRecorded_perBlockAlign;
            }
        }

        public static int get_pcm_forAndroid(int uiCapType, qyCommProc_open.WAVEFORMATEX pWfx) {

            switch (uiCapType) {
                case CONST_capType_av:
                   qmcSoundFunc.qyFuncs.makeWaveFormat_pcm(DEFAULT_nChannels_pcm_av, DEFAULT_iHertz_pcm_av, DEFAULT_wBitsPerSample_pcm_av, pWfx);
                    break;
                default:
                    break;
            }


            return 0;
        }

        public static guiShare.PF_getPolicyAvLevel getPolicyAvLevel=(ctxQyMc.CCtxQyMc pQyMc, int uiCapType, int uiSubCapType, int iCapUsage)-> {
            int iLevel = 0;

            if (null == pQyMc) return 0;

            //iLevel = CONST_policyAvLevel_480p;
            iLevel = CONST_policyAvLevel_1080p;

            //
            return iLevel;

        };

        public static int get_conf_usMaxSpeakers() {
            int usMaxSpeakers = DEFAULT_usMaxSpeakers;
            return usMaxSpeakers;
        }


        //
        public static int get_conf_iFourcc() {
            int iFourcc = DEFAULT_conf_fourcc_others;

            return iFourcc;
        }


        //
        public static int set_conf_iFourcc(int conf_iFourcc, qnmCommProc_mis.AV_COMPRESSOR_CFG pCompressor) {

            pCompressor.video.common.head.iFourcc = conf_iFourcc;
            switch (conf_iFourcc) {

                default:
                    pCompressor.video.common.head.ucCompressors = CONST_videoCompressors_ffmpeg;
                    break;
            }

            return 0;
        }


    }
}

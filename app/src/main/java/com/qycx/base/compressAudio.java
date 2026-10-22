
package com.qycx.base;


import static com.qycx.base.compressAudio.Consts.CONST_mutexNamePrefix_encA;
import static com.qycx.base.genericQueue.qyFuncs.initGenericQ;
import static com.qycx.base.qdcCommProc_open.Consts.CONST_qoiType_encA;
import static com.qycx.base.qmCommonFunc_mis.Consts.MAX_ucTimeoutInS_showFrameInfoResp;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_ffmpeg;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_audioCompressors_acm;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_audioCompressors_dmo;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_audioCompressors_vorbis;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;
import static com.qycx.base.qyMcMainCusModule.qyFuncs.M_getCompressModule;
import static com.qycx.base.qyMcMainRealTimeMediaProc.Consts.MAX_uiBytesRecorded_perBlockAlign;
import static com.qycx.base.qyTmpMemory.qyFuncs.clean_myPLAY_AUDIO_DATA;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;

import com.qycx.base.util.Log;


public class compressAudio {


    public static class Consts {
        public final static String CONST_mutexNamePrefix_encA = ("mtxEncA");
    }

    ;

    public static class qyFuncs {

        public static int initCompressAudio(ctxQmc.CCtxQmc pProcInfo, qyCommProc_open.WAVEFORMATEX pWf_input, qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pCompressor,
                                            qmcSharedObj.COMPRESS_AUDIO p) {
            int iErr = -1;
            //
            if (null == pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;//
            //if  (  !pQyMc  )  return  -1;
            qyMcMainCusModule.CUS_MODULES pCusModules = pProcInfo.getCusModules();
            if (null == pCusModules) return -1;
            //
            qyCommProc_open.WAVEFORMATEX pWfDst = null;
            qyCommProc_open.WAVEFORMATEX pWfPcm = null;

            boolean bDbg = false;
            String str;

            //if (!pWf_input) return -1;
            if (p.bCompressInited) return 0;

            Log.i( "initAudioCompress  enters");

            //  if  (  syncObj.sync(  pProcInfo->cfg.mutexName_syncAv  )  )  goto  errLabel;

            //
            p.ucTimeoutInS_showFrameInfoResp = MAX_ucTimeoutInS_showFrameInfoResp;
            //  p.uiBytesRecorded_perBlockAlign_expected  =  pWf_input->nSamplesPerSec  /  5;
            //  ³õÊ¼»¯¿ØÖÆ²ÎÊý
            p.ucTimeoutInS_showFrameInfoResp = MAX_ucTimeoutInS_showFrameInfoResp;

            //
            //  makeWaveFormat_pcm(  DEFAULT_audio_nChannels,  DEFAULT_audio_iHertz,  DEFAULT_audio_wBitsPerSample,  &p.waveFormatEx  );
            //memcpy(p.ah_decompress.wfx, pWf_input, sizeof(p.ah_decompress.wfx)  );
            p.ah_decompress.wfx = pWf_input;

            //  ×÷Ò»µãÐ£Ñé
            if (MAX_uiBytesRecorded_perBlockAlign * p.ah_decompress.wfx.nBlockAlign > bufSize_transferAudioData) {
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("bufSize_transferAudioData %d too small, should be >= %d"), CONST_bufSize_transferAudioData, MAX_uiBytesRecorded_perBlockAlign  p.ah_decompress.wfx.nBlockAlign);
                exitCompressAudio(pProcInfo, p);
                return iErr;
            }
            if (0 == p.ah_decompress.wfx.nAvgBytesPerSec) {
                exitCompressAudio(pProcInfo, p);
                return iErr;
            }


            //
            //memcpy(p.compressor, pCompressor, sizeof(p.compressor)  );
            p.compressor = pCompressor;

            //  2015/02/13
            qmcSharedObj.COMPRESS_AUDIO pCompressAudio = p;

            //  2014/04/02
            //  2014/03/23
            genericQueue.GENERIC_Q_cfgEx qCfgEx = new genericQueue.GENERIC_Q_cfgEx(); //  2012/11/10
            genericQueue.GENERIC_Q_CFG tmpCfg = new genericQueue.GENERIC_Q_CFG();

            //TCHAR tBuf[128];
            int tn = (int) tmpFunc.qyFuncs.myGetTickCount(null);
            int size;
            pCompressAudio.pInDataCacheQ = new genericQueue.GENERIC_Q();

            tmpCfg.name = String.format("%s-in-%d", CONST_mutexNamePrefix_encA, tn);
            tmpCfg.mutexName_prefix = String.format("%s-in-%d", CONST_mutexNamePrefix_encA, tn);
            tmpCfg.uiMaxQNodes = 100;

            qCfgEx = new genericQueue.GENERIC_Q_cfgEx();
            qCfgEx.common = tmpCfg;

            if (0 != initGenericQ(qCfgEx, pCompressAudio.pInDataCacheQ))
            {
                exitCompressAudio(pProcInfo, p);
                return iErr;
            }

            size = 8;//sizeof(myPlayAudioData.myPLAY_AUDIO_DATA);
            pCompressAudio.pCurPkt = new myPlayAudioData.myPLAY_AUDIO_DATA();// (myPlayAudioData.myPLAY_AUDIO_DATA) mymalloc(size);

            p.uiTranNo_openAvDev_org = getuiNextTranNo.call();   //  2010/09/04

            if (p.compressor.common.head.ucCompressors == CONST_audioCompressors_acm) {
                exitCompressAudio(pProcInfo, p);
                return iErr;
            } else if (p.compressor.common.head.ucCompressors == CONST_audioCompressors_dmo) {
                exitCompressAudio(pProcInfo, p);
                return iErr;

            } else {
                qyMcMainCusModule.CUS_MODULE_compress pModule = null;
                int uiModuleType = 0;             //  2012/09/09

                //
                uiModuleType = CONST_moduleType_ffmpeg;

                pModule = M_getCompressModule(pCusModules, uiModuleType);    //  2012/09/09
                if (null == pModule || null == pModule.pf_qdcInitCompressAudio) {
                    exitCompressAudio(pProcInfo, p);
                    return iErr;
                }
                p.uiModuleType_toEnc = uiModuleType;


                qnmCommProc_mis.AUDIO_COMPRESSOR_CFG compressor;
                //memcpy(compressor,pCompressAudio.compressor, sizeof(compressor));
                compressor = pCompressAudio.compressor;


                ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;
                //if (pQmcCfg)
                {
                    if (pQmcCfg.debugStatusInfo.ucb__Dump_mp3_enc__) {
                        guiShare.GuiShare.pf_dumpAudio_init.call();
                    }
                }

                p.u_qoi.myQoi.common.uiType = CONST_qoiType_encA;

                if (0 != pModule.pf_qdcInitCompressAudio.call(compressor, p.ah_decompress, p.ah_compress, p.u_qoi.myQoi.common)) {
                    Log.e("initCompressAudio.282, qdcInitCompressAudio failed");
                    exitCompressAudio(pProcInfo, p);
                    return iErr;
                }

                if (p.compressor.common.head.ucCompressors == CONST_audioCompressors_vorbis) {

                }

            }

            //
            size = 8;//sizeof(qnmCommProc_mis.TRANSFER_AUDIO_DATA);
            p.pReqCache = new qnmCommProc_mis.TRANSFER_AUDIO_DATA();// (qnmCommProc_mis.TRANSFER_AUDIO_DATA) mymalloc(size);
            if (null == p.pReqCache) return iErr;
            //mymemset(p.pReqCache, 0, size);


            p.bCompressInited = true;

            iErr = 0;


//
//            if (null != pWfDst){
//                qyMemoryFunc.qyFuncs.myfree( pWfDst);
//            }
//            if (null != pWfPcm){
//                qyMemoryFunc.qyFuncs.myfree( pWfPcm);
//            }

            if (0 != iErr) {
                exitCompressAudio(pProcInfo, p);
            }

            return iErr;
        }

        public static int exitCompressAudio(ctxQmc.CCtxQmc pProcInfo, qmcSharedObj.COMPRESS_AUDIO pCompressAudio) {
            int iErr = -1;
            //QY_MC	*	pQyMc	=	(  QY_MC  *  )pProcInfo->pQyMc;//
            //  CQySyncObj			syncObj;
            if (null == pProcInfo) return -1;
            qyMcMainCusModule.CUS_MODULES pCusModules = (qyMcMainCusModule.CUS_MODULES) pProcInfo.getCusModules();
            if (null == pCusModules) return -1;


            Log.i( "exitAudioCompress  enters");

            //  if  (  syncObj.sync(  pProcInfo->cfg.mutexName_syncAv  )  )  goto  errLabel;

            if (0 == pCompressAudio.compressor.common.head.ucCompressors) ;
            else if (pCompressAudio.compressor.common.head.ucCompressors == CONST_audioCompressors_acm) {


            } else if (pCompressAudio.compressor.common.head.ucCompressors == CONST_audioCompressors_dmo) {
                //exitCompressAudio_dmo(pCompressAudio.u.dmo);
            } else {
                qyMcMainCusModule.CUS_MODULE_compress pModule = null;
                int uiModuleType = pCompressAudio.uiModuleType_toEnc;
                pModule = M_getCompressModule(pCusModules, uiModuleType);    //  2012/09/09
                if (null != pModule) {
                    if (null != pModule.pf_qdcExitCompressAudio) {
                        pModule.pf_qdcExitCompressAudio.call(pCompressAudio.u_qoi.myQoi.common);
                    }
                }

                ctxQmc.QMC_cfg pCfg = pProcInfo.cfg;
                //if (pCfg)
                {
                    if (pCfg.debugStatusInfo.ucb__Dump_mp3_enc__) {
                        guiShare.GuiShare.pf_dumpAudio_exit.call();
                    }
                }

            }


            //  2015/02/13
            //  2014/03/25
            clean_myPLAY_AUDIO_DATA.call(pCompressAudio.pCurPkt, ("exitCompressAudio"));
//            long tmpp =  pCompressAudio.pCurPkt;
//            MACRO_safeFree(tmpp);
//            pCompressAudio.pCurPkt = (myPlayAudioData.myPLAY_AUDIO_DATA) tmpp;

            //
            //  2014/03/24
            if (null != pCompressAudio.pInDataCacheQ) {
                //genericQFree(pCompressAudio.pInDataCacheQ); pCompressAudio.pInDataCacheQ = null;
                pCompressAudio.pInDataCacheQ = null;
            }

            //
            if (null != pCompressAudio.pReqCache) {   //  2009/02/11
//                qyMemoryFunc.qyFuncs.myfree( pCompressAudio.pReqCache);
                pCompressAudio.pReqCache = null;
            }

            if (null != pCompressAudio.pDstBuf) {
                pCompressAudio.pDstBuf = null;
            }
            if (null != pCompressAudio.pPcmBuf) {
                pCompressAudio.pPcmBuf = null;
            }

            pCompressAudio.bCompressInited = false;
            //memset(pCompressAudio, 0, sizeof(pCompressAudio[0]));
            pCompressAudio.memset0();

            iErr = 0;

            //  

            Log.i( "exitAudioCompress  leaves ");

            //
            return iErr;
        }

    }
}

package com.qycx.base;

import static com.qycx.base.compressVideo.Consts.CONST_mutexNamePrefix_encV;
import static com.qycx.base.genericQueue.qyFuncs.genericQFree;
import static com.qycx.base.genericQueue.qyFuncs.initGenericQ;
import static com.qycx.base.qmCommonFunc_mis.Consts.MAX_ucTimeoutInS_showFrameInfoResp;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_compress;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_ffmpeg;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_custom;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_dmo;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_ffmpeg;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_stream;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_videoCompressors_vcm;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_HEVC;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_h264;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_encVideo;
import static com.qycx.base.qyCommProc_open.Consts.MAX_spsPpsLen;
import static com.qycx.base.qyMcMainCusModule.qyFuncs.M_getCompressModule;
import static com.qycx.base.qyTmpMemory.qyFuncs.cleanEx_myDRAW_VIDEO_DATA;
import static com.qycx.base.qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;
import static com.qycx.base.taskAvProc.qyFuncs.adjustModuleType_encV;

import com.qycx.base.util.Log;

import java.nio.ByteBuffer;


public class compressVideo {
    public static class Consts {
        public final static String CONST_mutexNamePrefix_encV = ("mtxEncV");
    }

    public static class qyFuncs {
        //
        public static int initCompressVideo(ctxQmc.CCtxQmc pProcInfoParam, qyCommProc_open.BITMAPINFOHEADER pBmpInfo_input, int uiCapType,
                                            qnmCommProc_mis.VIDEO_COMPRESSOR_CFG pCompressor, boolean bCapDev, int uiBufSize_pData,
                                            qmcSharedObj.COMPRESS_VIDEO pCompressVideo) {
            int iErr = -1;

            ctxQmc.CCtxQmc pProcInfo0 = pProcInfoParam;
            if (null == pProcInfo0) return -1;
            ctxQmc.QMC_cfg pQmcCfg = pProcInfo0.cfg;

            qyMcMainCusModule.CUS_MODULES pCusModules = pProcInfo0.getCusModules();
            if (null == pCusModules) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo0.pMisCnt;

            int iFourcc = 0;
            qyCommProc_open.BITMAPINFOHEADER pBmpInfo = null;
            int iFormatSize = 0;
            int biSizeImage = 0;
            int iRet;
            boolean ucbResizable = false;
            boolean ucbResizeByEncoder = false;      //  2014/05/03
            int iW_org = 0;
            int iH_org = 0;

            showInfo_open.qyFuncs.showInfo_open(0, 0, null, "initCompressVideo enters.");

            if (pCompressVideo.bVideoCompressInited) {
                exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                return iErr;
            }

            pCompressVideo.compressor = pCompressor;
            if (0 == pCompressVideo.compressor.common.head.usMinFps_toShareBmp) {
                pCompressVideo.compressor.common.head.usMinFps_toShareBmp = pCompressVideo.compressor.common.head.usMaxFps_toShareBmp;
            }
            pCompressVideo.compressor.common.head.uiCapType = uiCapType;
            pCompressVideo.compressor.common.head.ucHardwareAccl = pQmcCfg.policy.ucHardwareAccl;
            pCompressVideo.usFrames_perSecond_expected = pCompressVideo.compressor.common.head.usMinFps_toShareBmp;
            if (0 == pCompressVideo.usFrames_perSecond_expected) {
                Log.e("initCompressVideo failed: usFrames_perSecond_expected is 0");
                exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                return iErr;
            }
            pCompressVideo.ucTimeoutInS_showFrameInfoResp = MAX_ucTimeoutInS_showFrameInfoResp;
            pCompressVideo.tLastTime_sendVideo = 0;
            pCompressVideo.tLastTime_showFrameInfo = 0;
            pCompressVideo.tFirstTime_showFrameInfo_withoutResp = 0;
            pCompressVideo.tRecvTime_resp_fromCli = 0;

            if (null == pBmpInfo_input) {
                Log.e("initCompressVideo failed: pBmpInfo_input is null");
                exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                return iErr;
            }

            iW_org = pBmpInfo_input.biWidth;
            iH_org = pBmpInfo_input.biHeight;

            if (!ucbResizable) {
                pCompressVideo.vh_decompress.bih = pBmpInfo_input;
            } else {
                //
                boolean ucbAutoClip = false;

                int iX = 0;
                int iY = 0;
                int iW = pCompressor.common.head.iWidth_pic;
                int iH = pCompressor.common.head.iHeight_pic;
                int iW_i = 0, iH_i = 0;
                int iX_src_i = 0, iY_src_i = 0;
                int iX_dst = 0, iY_dst = 0;
                int iW_dst = 0, iH_dst = 0;


                boolean ucb3d = true;

                capImageFunc.qyFuncs.myGetImageInfo(false,                                       //
                        ucbAutoClip,                                    //  根据目标矩形自动裁剪，使目标矩形被充满
                        ucb3d,                                          //  2014/11/14
                        iW_org, iH_org,
                        iX, iY, iW, iH,
                        (int) iW_i, (int) iH_i, iX_src_i, iY_src_i, iX_dst, iY_dst, (int) iW_dst, (int) iH_dst);

                //
                iW_dst = iW_dst / 4 * 4;
                iH_dst = iH_dst / 4 * 4;
                //
                if (0 == iW_dst || 0 == iH_dst) {
                    Log.e("initCompressVideo failed: iW_dst or iH_dst is 0");
                    return iErr;
                }

                qyCommProc_open.BITMAPINFOHEADER pBih = pCompressVideo.vh_decompress.bih;
                {
                    videoFunc_open.qyFuncs.makeBmpInfoHeader_rgb(24, (int) iW_dst, (int) iH_dst, pBih);
                }
                //  下面还要在enc模块里加上resize处理。要把bResizable参数从compressor带进去，
            }
            //  2014/05/03
            pCompressVideo.ucbResizeByEncoder = ucbResizeByEncoder;
            {
                iFourcc = (int) pCompressVideo.compressor.common.head.iFourcc;// fourccStr2i(fourccStr);
            }

            //pCompressVideo.usCapDriverIndex  =  usCapDriverIndex;

            //  2014/04/02
            //  2014/03/23
            genericQueue.GENERIC_Q_cfgEx qCfgEx;
            genericQueue.GENERIC_Q_CFG tmpCfg = new genericQueue.GENERIC_Q_CFG();

            //TCHAR tBuf[128];
            int tn = (int) tmpFunc.qyFuncs.myGetTickCount(null);
            int size;

            pCompressVideo.pInDataCacheQ = new genericQueue.GENERIC_Q();// (GENERIC_Q)mymalloc(size);
            pCompressVideo.pTmpDataQ = new genericQueue.GENERIC_Q();// (GENERIC_Q)mymalloc(size);

            String str;
            str = String.format("%s-in-%d", CONST_mutexNamePrefix_encV, tn);
            tmpCfg.name = str;// cntof_qCfg_name);

            str = String.format("%s-in-%d", CONST_mutexNamePrefix_encV, tn);
            tmpCfg.mutexName_prefix = str;// cntof_mutexName_prefix);
            tmpCfg.uiMaxQNodes = 100;

            qCfgEx = new genericQueue.GENERIC_Q_cfgEx();
            qCfgEx.common = tmpCfg;
            qCfgEx.pf_QElemRemoveEx = (genericQueue.PF_qElemRemoveEx) cleanEx_myDRAW_VIDEO_DATA;
            qCfgEx.pfQElemRemove = (genericQueue.PF_qElemRemove) clean_myDRAW_VIDEO_DATA;

            if (0 != initGenericQ(qCfgEx, (genericQueue.GENERIC_Q) pCompressVideo.pInDataCacheQ)) {
                exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                return iErr;
            }

            str = String.format("%s-tmp-%d", CONST_mutexNamePrefix_encV, tn);
            tmpCfg.name = str;// cntof_qCfg_name);

            str = String.format("%s-tmp-%d", CONST_mutexNamePrefix_encV, tn);
            tmpCfg.uiMaxQNodes = 100;

            qCfgEx = new genericQueue.GENERIC_Q_cfgEx();
            qCfgEx.common = tmpCfg;
            qCfgEx.pf_QElemRemoveEx = (genericQueue.PF_qElemRemoveEx) cleanEx_myDRAW_VIDEO_DATA;
            qCfgEx.pfQElemRemove = (genericQueue.PF_qElemRemove) clean_myDRAW_VIDEO_DATA;

            if (0 != initGenericQ(qCfgEx, pCompressVideo.pTmpDataQ)) {
                exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                return iErr;
            }

            pCompressVideo.pCurPkt = new qnmCommProc_mis.myDRAW_VIDEO_DATA();// (qnmCommProc_mis.myDRAW_VIDEO_DATA) mymalloc(size);

            pCompressVideo.u_qoi.myQoi.common.memset0();

            if (pCompressVideo.compressor.common.head.ucCompressors == CONST_videoCompressors_vcm) {
                Log.e(("vcm is not supported "));
                exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                return iErr;
            } else if (pCompressVideo.compressor.common.head.ucCompressors == CONST_videoCompressors_dmo) {
                Log.e(("dmo is not supported "));
                exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                return iErr;

            } else if (pCompressVideo.compressor.common.head.ucCompressors == CONST_videoCompressors_stream) {
            } else {

                qyMcMainCusModule.CUS_MODULE_compress pModule = null;
                int uiModuleType = 0;             //  2012/09/09

                switch (pCompressVideo.compressor.common.head.ucCompressors) {
                    case CONST_videoCompressors_ffmpeg:
                        uiModuleType = CONST_moduleType_ffmpeg;
                        break;
                    case CONST_videoCompressors_custom:
                        uiModuleType = CONST_moduleType_compress;
                        break;
                    default:
                        Log.e("initCompressVideo: ucCompressors");
                        exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                        return iErr;
                }

                uiModuleType = adjustModuleType_encV(uiModuleType);
                pModule = M_getCompressModule(pCusModules, uiModuleType);    //  2012/09/09
                if (null == pModule || !pModule.common.bLoaded || null == pModule.pf_qdcInitCompressVideo) {
                    Log.e("initCompressVideo failed, pModule or initDecompressVideo is null.");
                    exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                    return iErr;
                }
                pCompressVideo.uiModuleType_toEnc = uiModuleType;

                //  2014/04/02
                qnmCommProc_mis.VIDEO_COMPRESSOR_CFG compressor;
                compressor = pCompressVideo.compressor;
                compressor.common.pParent_transform = pCompressVideo.compressor.common.pParent_transform;
                if (null == compressor.common.pParent_transform) {
                    exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                    return iErr;
                }
                compressor.common.uiTransformType_pParent = CONST_uiTransformType_encVideo;
                {
                    if (0 != pModule.pf_qdcInitCompressVideo.call(compressor, pCompressVideo.vh_decompress, pCompressVideo.vh_compress, pCompressVideo.u_qoi.myQoi.common)) {
                        Log.e("initCompressVideo.415, qdcInitCompressVideo failed");
                        exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                        return iErr;
                    }
                    pCompressVideo.iCompressBufSize = pCompressVideo.vh_compress.bih.biSizeImage;

                    if (pModule.pf_qdcGetSpsPps != null) {
                        qyCommProc_open.VH_stream pVh_stream = pCompressVideo.vh_stream;
                        {
                            //
                            int vpsLen = 0;   //  2017/10/10
                            ByteBuffer pVps = ByteBuffer.allocate(MAX_spsPpsLen);
                            int spsLen = 0;
                            ByteBuffer pSps = ByteBuffer.allocate(MAX_spsPpsLen);
                            int ppsLen = 0;
                            ByteBuffer pPps = ByteBuffer.allocate(MAX_spsPpsLen);

                            switch (iFourcc) {
                                case CONST_fourcc_HEVC: {
                                    //
                                    int k = 0;
//
                                    if (0 != pModule.pf_qdcGetSpsPps.call(pCompressVideo.u_qoi.myQoi.common, pSps, pPps, pVps)) {
                                        Log.e("initCompressVideo failed, qdcGetSpsPps failed.");
                                        exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                                        return iErr;
                                    }

                                    if (pVps.limit() > 0) {
                                        vpsLen = pVps.limit();
                                        pVh_stream.mem0.usLen = vpsLen;
                                        pVh_stream.mem0.spsPps = pVps;
                                        k++;
                                    }
                                    if (pSps.limit() > 0) {
                                        spsLen = pSps.limit();
                                        pVh_stream.mem1.usLen = spsLen;
                                        pVh_stream.mem1.spsPps = pSps;
                                        k++;
                                    }
                                    if (pPps.limit() > 0) {
                                        ppsLen = pPps.limit();
                                        pVh_stream.mem2.usLen = ppsLen;
                                        pVh_stream.mem2.spsPps = pPps;
                                        k++;
                                    }
                                    pVh_stream.usCnt = (int) k;
                                }
                                break;
                                case CONST_fourcc_h264:
                                default: {
                                    int k = 0;
                                    if (0 != pModule.pf_qdcGetSpsPps.call(pCompressVideo.u_qoi.myQoi.common, pSps, pPps, null)) {
                                        Log.e("initCompressVideo failed, qdcGetSpsPps failed.");
                                        exitCompressVideo(pProcInfo0, bCapDev, pCompressVideo);
                                        return iErr;
                                    }
                                    if (pSps.limit() > 0) {
                                        spsLen = pSps.limit();
                                        pVh_stream.mem0.usLen = spsLen;
                                        pVh_stream.mem0.spsPps = pSps;
                                        k++;
                                    }
                                    if (pPps.limit() > 0) {
                                        ppsLen = pPps.limit();
                                        pVh_stream.mem1.usLen = ppsLen;
                                        pVh_stream.mem1.spsPps = pPps;
                                        k++;
                                    }
                                    pVh_stream.usCnt = k;
                                }
                                break;
                            }
                            //
                            pVh_stream.dwFourcc = (int) iFourcc;// pCompressVideo.vh_compress.bih.biCompression;
                            //  2014/05/02
                            Log.d("qdcGetSpsPps: vps " + vpsLen + ", spsLen " + spsLen + ", ppsLen " + ppsLen);
                        }
                    }
                }
            }
            if (!pCompressVideo.ucbUseEvt) {
                if (pCompressVideo.compressor.common.head.ucCompressors != CONST_videoCompressors_stream) {
                    if (pCompressVideo.iCompressBufSize > 0) {
                        pCompressVideo.pCompressBuf = new byte[pCompressVideo.iCompressBufSize];
                    }
                }
            }
            pCompressVideo.pReqBuf_myVideoCapture = new qnmCommProc_mis.TRANSFER_VIDEO_DATA();
            pCompressVideo.uiTranNo_openAvDev_org = getuiNextTranNo.call();  //  2010/09/04
            if (0 != uiBufSize_pData) {
                pCompressVideo.pData = new byte[uiBufSize_pData];
            }
            pCompressVideo.bVideoCompressInited = true;

            iErr = 0;

            showInfo_open.qyFuncs.showInfo_open(0, 0, null, "initCompressVideo leaves");

            return iErr;
        }


        //
        public static int exitCompressVideo(ctxQmc.CCtxQmc pProcInfoParam, boolean bCapDev, qmcSharedObj.COMPRESS_VIDEO pCompressVideo) {
            int iErr = -1;


            ctxQmc.CCtxQmc pProcInfo = pProcInfoParam;
            if (null == pProcInfo) return -1;
            //QY_MC	*	pQyMc		=	(  QY_MC  *  )pProcInfo->pQyMc;//
            qyMcMainCusModule.CUS_MODULES pCusModules = (qyMcMainCusModule.CUS_MODULES) pProcInfo.getCusModules();
            if (null == pCusModules) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //if (!pCompressVideo) return -1;

            //
            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("exitCompressVideo enters. %s"), pCompressVideo.tag_dbg);

            //  2015/10/11
            //
            if (0 == pCompressVideo.compressor.common.head.ucCompressors) ;
            else if (pCompressVideo.compressor.common.head.ucCompressors == CONST_videoCompressors_vcm) {
            } else if (pCompressVideo.compressor.common.head.ucCompressors == CONST_videoCompressors_dmo) {
                //exitCompressVideo_dmo(pCompressVideo.u.dmo);
            } else {
                qyMcMainCusModule.CUS_MODULE_compress pModule = M_getCompressModule(pCusModules, pCompressVideo.uiModuleType_toEnc);  //  2012/09/09
                if (pModule != null) {
                    if (pModule.pf_qdcExitCompressVideo != null) {
                        pModule.pf_qdcExitCompressVideo.call(pCompressVideo.u_qoi.myQoi.common);
                    }
                }
            }
            //

            //
            //  2014/03/25
            clean_myDRAW_VIDEO_DATA.call(pCompressVideo.pCurPkt, ("exitCompressVideo"));
//            byte[] pCurPkt =  pCompressVideo.pCurPkt;
//            MACRO_mysafeFree(pCurPkt);
            pCompressVideo.pCurPkt = null;// (qnmCommProc_mis.myDRAW_VIDEO_DATA) pCurPkt;

            //
            //  2014/03/24
            if (pCompressVideo.pInDataCacheQ != null) {
                genericQFree(pCompressVideo.pInDataCacheQ);
                pCompressVideo.pInDataCacheQ = null;
            }
            if (pCompressVideo.pTmpDataQ != null) {
                genericQFree(pCompressVideo.pTmpDataQ);
                pCompressVideo.pTmpDataQ = null;
            }

            //
            if (pCompressVideo.pReqBuf_myVideoCapture != null) {
//                qyMemoryFunc.qyFuncs.myfree( pCompressVideo.pReqBuf_myVideoCapture);
                pCompressVideo.pReqBuf_myVideoCapture = null;
            }
            if (pCompressVideo.pCompressBuf != null) {
//                qyMemoryFunc.qyFuncs.myfree( pCompressVideo.pCompressBuf);
                pCompressVideo.pCompressBuf = null;
            }


            //
            if (pCompressVideo.pData != null) {

//                qyMemoryFunc.qyFuncs.myfree( pCompressVideo.pData);
                pCompressVideo.pData = null;
            }

            //  pCompressVideo.uiTranNo_openAvDev  =  0;	//  ×¢ÒâÇå0
            //  pCompressVideo.bVideoCompressInited  =  false;		//  ×¢Òâ¿ª¹ØÁ¿¡£


            pCompressVideo.memset0();

            iErr = 0;

            return iErr;

        }


    }
}


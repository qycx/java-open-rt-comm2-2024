package com.qycx.base;

import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferVideoData;
import static com.qycx.base.qnmCommProc_mis.Consts.hEvent_notifyToQuit_Permit;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_I420;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_YV12;
import static com.qycx.base.qyCommProc_open.Consts.CONST_pktResType_sharedTex;


import android.opengl.EGLContext;

import com.qycx.base.qdc.decObj_a_mediaCodec;
import com.qycx.base.qdc.decObj_a_mediaCodec_async;
import com.qycx.base.qdc.decObj_mediaCodec;
import com.qycx.base.qdc.encObj_mediaCodec;
import com.qycx.base.util.Log;

import java.nio.ByteBuffer;

public class cusModule_mediaCodec {
    public static class CQdcObj_mediaCodec {
        public boolean bDec;

        public Object obj_qdcUnit;

        public CQdcObj_mediaCodec() {
            bDec = false;
        }
    }

    ;

    public static class qyFuncs_Droid {

        public static qdcCommProc_open.PF_qdcInit qdcInit_mediaCodec = (qdcCommProc_open.QDC_MGR p) -> {
            int iRet = -1;

            iRet = 0;
            return iRet;
        };

        public static qdcCommProc_open.PF_qdcExit qdcExit_mediaCodec = (qdcCommProc_open.QDC_MGR p) -> {
            int iRet = -1;

            iRet = 0;
            return iRet;
        };


        public static qdcCommProc_open.PF_qdcExitDecompressVideo qdcExitDecompressVideo_mediaCodec = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;
            boolean bDbg = true;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();


            if (null == pQdcObjInfo.var.pObj) return 0;
            CQdcObj_mediaCodec pObj = (CQdcObj_mediaCodec) pQdcObjInfo.var.pObj;

            //TmpFfmpeg.DecV_exit(pObj.m_index_qdcUnit, bDbg);
            decObj_mediaCodec obj = (decObj_mediaCodec) pObj.obj_qdcUnit;
            if (null != obj) {
                if (null != obj.m_th) {

                    try {
                        obj.m_th.join();
                        obj.m_th = null;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e) {
                        Log.e("qy","qyExitDecompressVideo: " + e.getMessage());
                    }

                }
                if (null != obj.m_wrapper) {
                    obj.m_wrapper = null;
                }
                //
            }

            //
            pObj.obj_qdcUnit = null;

            //
            if (pQdcObjInfo.hEvent_notifyToQuit != null) {
                EventWaitHandle hEvt = (EventWaitHandle) pQdcObjInfo.hEvent_notifyToQuit;
                hEvt.Close();
                pQdcObjInfo.hEvent_notifyToQuit = null;
            }

            //
            pQdcObjInfo.bb_input = null;
            pQdcObjInfo.bb_output = null;

            //
            pQdcObjInfo.var.pObj = null;


            //
            iErr = 0;
            return iErr;
        };

        public static qdcCommProc_open.PF_qdcInitDecompressVideo qdcInitDecompressVideo_mediaCodec = (qnmCommProc_mis.VIDEO_COMPRESSOR_CFG pVideoCompressorCfg, qyCommProc_open.QY_VIDEO_HEADER pVh_compress, qyCommProc_open.QY_VIDEO_HEADER pVh_decompress, qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;
            boolean bDbg = true;
            String str_ii = "qdcInitDecompressVideo_mediaCodec start";
            showInfo_open.qyFuncs.showInfo_open(str_ii);
            Log.e(str_ii);



            int iFourcc;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            if (pQdcObjInfo.var.pObj != null)
                return -1;

            //
            pQdcObjInfo.cfg.v = pVideoCompressorCfg.common;


            pQdcObjInfo.var.pObj = new CQdcObj_mediaCodec();
            CQdcObj_mediaCodec pObj = (CQdcObj_mediaCodec) pQdcObjInfo.var.pObj;

            //
            pQdcObjInfo.var.ucbTransformFuncsOk = true;

            //
            pObj.bDec = true;
            iFourcc = pVideoCompressorCfg.common.head.iFourcc;

            pQdcObjInfo.bHeadLen = 1 + 7 * Integer.BYTES;
            pQdcObjInfo.nPktsDeced = 0;

            String pName = String.format("qdcDecEvt" + qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call());
            pQdcObjInfo.hEvent_notifyToQuit = new EventWaitHandle(false, EventResetMode.AutoReset, pName);

            //
            if (pQdcObjInfo.cfg.v.usPktResType_suggested != CONST_pktResType_sharedTex) {
                Log.e("qdcInitDecompressVideo_mediaCodec failed, cfg.usPktResType err");
                qdcExitDecompressVideo_mediaCodec.call(pQdcObjInfo);
                return iErr;
            }
            pQdcObjInfo.var.usPktResType_dec = CONST_pktResType_sharedTex;

            //
            String str;
            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pQdcObjInfo.cfg.v.pParent_transform;
            //
            if (qyq2.qyFuncs.isQ2Warning(pQdcObjInfo.cfg.v.pVideoQ2, 5)) {
                qyq2.qyFuncs.emptyQ2(pQdcObjInfo.cfg.v.pVideoQ2);
                //
                str = String.format("qdcInitDecV: pVideoQ2 is full, empty it");
                Log.d(str);
            }
            if (qyq2.qyFuncs.isQ2Warning(pTransform.v_preTransThread.q2, 5)) {
                qyq2.qyFuncs.emptyQ2(pTransform.v_preTransThread.q2);
                //
                str = String.format("qdcInitDecV: v_preTransQ2 is full, empty it");
                Log.d(str);
            }

            //
            showInfo_open.qyFuncs.showInfo_open("qdcInitDecompressVideo_mediaCodec");

            //
            pObj.obj_qdcUnit = new decObj_mediaCodec();
            decObj_mediaCodec obj = (decObj_mediaCodec) pObj.obj_qdcUnit;

            //
            //obj.m_sharedEglContext = (Android.Opengl.EGLContext)pTc.share_eglContext;
            obj.m_sharedEglContext = (EGLContext) pVideoCompressorCfg.shared_eglContext;
            obj.m_qoi = pQdcObjInfo;
            obj.m_iW = pVh_decompress.bih.biWidth;
            obj.m_iH = pVh_decompress.bih.biHeight;
            obj.m_iFourcc_i = iFourcc;
            obj.mTag = pTransform.video.q2.cfg.name;
            int ii = 0;

            obj.m_wrapper = new decObj_mediaCodec.decObj_mediaCodec_Wrapper(obj);
            obj.m_th = new Thread(obj.m_wrapper, "codec test3");
            //obj.m_th.start();

            //
            iErr = 0;

            if (0 != iErr) {
                qdcExitDecompressVideo_mediaCodec.call(pQdcObjInfo);
            }

            str_ii = "qdcInitDecompressVideo_mediaCodec end";
            showInfo_open.qyFuncs.showInfo_open(str_ii);
            Log.e(str_ii);

            return iErr;
        };

        public static qdcCommProc_open.PF_qdcDecompressVideo qdcDecompressVideo_mediaCodec = (qdcCommProc_open.QOI_common pQdcObjInfo, qyCommProc_open.BITMAPINFOHEADER pBmpInfo_input, ByteBuffer pInput, int iSampleTimeInMs) -> {
            int iErr = -1;
            int iPaddingSize = 128;

            boolean bDbg = false;
            String str;

            CQdcObj_mediaCodec pObj = (CQdcObj_mediaCodec) pQdcObjInfo.var.pObj;

            if (pQdcObjInfo.bb_input == null) {
                pQdcObjInfo.bb_input = ByteBuffer.allocateDirect(bufSize_transferVideoData);
            }
            if (pQdcObjInfo.bb_output == null) {
                pQdcObjInfo.bb_output = ByteBuffer.allocateDirect(128);
            }
            ByteBuffer bb_input = pQdcObjInfo.bb_input;
            ByteBuffer bb_output = pQdcObjInfo.bb_output;
            byte[] pBbInput = bb_input.array();
            if (pBbInput == null)
            {
                return 0;
            }
            byte[] pBbOutput = bb_output.array();
            if (pBbOutput == null)
            {
                return 0;
            }

            if (pBmpInfo_input.biSizeImage + iPaddingSize > bb_input.capacity()) {
                Log.d("qdcDecVideo failed: bb_input is too small");
                return 0;
            }
            pInput.get(pBbInput, 0, pBmpInfo_input.biSizeImage);

            int total_nums = 7;
            byte len = (byte) (1 + total_nums * 4);
            if (pQdcObjInfo.bHeadLen < len) {
                Log.e("qdcDecVideo failed, bHeadLen error");
                return 0;
            }

            pBbOutput[0] = pQdcObjInfo.bHeadLen;

            int k = 0;
            int tmpiPaddingSize = ByteBuffer.wrap(pBbOutput, 1 + k++ * 4, 4).getInt();
            int nPktsDeced = ByteBuffer.wrap(pBbOutput, 1 + k++ * 4, 4).getInt();
            int iFourcc_dec = ByteBuffer.wrap(pBbOutput, 1 + k++ * 4, 4).getInt();
            int iW = ByteBuffer.wrap(pBbOutput, 1 + k++ * 4, 4).getInt();
            int iH = ByteBuffer.wrap(pBbOutput, 1 + k++ * 4, 4).getInt();
            int iDecBufSize = ByteBuffer.wrap(pBbOutput, 1 + k++ * 4, 4).getInt();
            int bBufTooSmall = ByteBuffer.wrap(pBbOutput, 1 + k++ * 4, 4).getInt();

            if (bBufTooSmall != 0) {
                switch (iFourcc_dec) {
                    case CONST_fourcc_I420:
                        qyCommProc_open.BITMAPINFOHEADER bih = new qyCommProc_open.BITMAPINFOHEADER();
                        videoFunc_open.qyFuncs.makeBmpInfoHeader_i420(12, iW, iH, bih);
                        int bufSize_output = bih.biSizeImage + pQdcObjInfo.bHeadLen;
                        if (bufSize_output > bb_output.capacity()) {
                            pQdcObjInfo.bb_output = null;
                            pQdcObjInfo.bb_output = ByteBuffer.allocateDirect(bufSize_output);
                            if (pQdcObjInfo.bb_output == null)
                                return 0;
                        }
                        break;
                    case CONST_fourcc_YV12:
                    default:
                        Log.e("qdcDecVideo_android failed, iFourcc_dec unsupported");
                        return 0;
                }
            }

            pQdcObjInfo.nPktsDeced = nPktsDeced;
            pQdcObjInfo.iDecBufSize = iDecBufSize;


            return 0;
        };


        //
        public static qdcCommProc_open.PF_qdcDecompressVideoRun qdcDecompressVideoRun_mediaCodec = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;
            int i;

            boolean bDbg = false;
            String str;

            CQdcObj_mediaCodec pObj = (CQdcObj_mediaCodec) pQdcObjInfo.var.pObj;
            decObj_mediaCodec obj = (decObj_mediaCodec) pObj.obj_qdcUnit;


            //
            obj.m_th.run();

            //
            for (i = 0; ; i++) {
                if (obj.m_bEnd) {
                    break;
                }
                tmpFunc.qyFuncs.myWaitOne((EventWaitHandle) pQdcObjInfo.hEvent_notifyToQuit, 1000);
            }
            iErr = 0;
            return iErr;
        };


        public static qdcCommProc_open.PF_qdcExitCompressVideo qdcExitCompressVideo_mediaCodec = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            boolean bDbg = true;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            if (null == pQdcObjInfo.var.pObj) return 0;
            CQdcObj_mediaCodec pObj = (CQdcObj_mediaCodec) pQdcObjInfo.var.pObj;

            encObj_mediaCodec obj = (encObj_mediaCodec) pObj.obj_qdcUnit;

            if (obj != null) {
                //
                obj.m_bQuit = true;
                //
                if (null != obj.m_th) {
                    // obj.m_th.join();
                    try {
                        obj.m_th.join();
                        obj.m_th = null;
                    } catch (InterruptedException e) {
                        Log.e("qy","qdcExieCompressVideo_mediaCodec: 325"  + e.getMessage());

                    }
                    catch (Exception e ) {
                        Log.e("qy","qdcExitCompressVideo_mediaCodec: 329"  + e.getMessage());
                    }
                }

                obj = null;
            }

            //
            if (pQdcObjInfo.hEvent_notifyToQuit != null) {
                EventWaitHandle hEvt = (EventWaitHandle) pQdcObjInfo.hEvent_notifyToQuit;
                hEvt.Close();
                pQdcObjInfo.hEvent_notifyToQuit = null;
            }

            pQdcObjInfo.bb_output = null;
            pQdcObjInfo.bb_input = null;
            pQdcObjInfo.var.pObj = null;

            return 0;
        };


        public static qdcCommProc_open.PF_qdcInitCompressVideo qdcInitCompressVideo_mediaCodec = (qnmCommProc_mis.VIDEO_COMPRESSOR_CFG pVideoCompressorCfg, qyCommProc_open.QY_VIDEO_HEADER pVh_decompress, qyCommProc_open.QY_VIDEO_HEADER pVh_compress, qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;
            int iFourcc;
            boolean bDbg = false;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            if (pQdcObjInfo.var.pObj != null) return -1;

            //
            pQdcObjInfo.cfg.v = pVideoCompressorCfg.common;

            pQdcObjInfo.var.pObj = new CQdcObj_mediaCodec();
            CQdcObj_mediaCodec pObj = (CQdcObj_mediaCodec) pQdcObjInfo.var.pObj;

            if (pProcInfo.cfg.mSpecialDbg.bNo_encV) {
                iErr = 0;
                qdcExitCompressVideo_mediaCodec.call(pQdcObjInfo);
                return iErr;
            }

            String pName = String.format("qdcEnccEvt" + qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call());
            pQdcObjInfo.hEvent_notifyToQuit = new EventWaitHandle(false, EventResetMode.AutoReset, pName, hEvent_notifyToQuit_Permit);

            pObj.obj_qdcUnit = new encObj_mediaCodec();
            showInfo_open.qyFuncs.showInfo_open("new encObj_mediaCodec");
            Log.e("new encObj_mediaCodec");
            encObj_mediaCodec obj = (encObj_mediaCodec) pObj.obj_qdcUnit;

            obj.m_sharedEglContext = pVideoCompressorCfg.shared_eglContext;
            obj.m_qoi = pQdcObjInfo;
            obj.m_index_sharedObj = pVideoCompressorCfg.index_sharedObj;
            obj.mWidth = pVh_decompress.bih.biWidth;
            obj.mHeight = pVh_decompress.bih.biHeight;
            obj.mBitRate = 1000000;
            obj.mBitRate = 1300000;
            obj.mBitRate = 512000;
            obj.mBitRate = pVideoCompressorCfg.common.head.iAvgBitrate;
            //
            if (!pVideoCompressorCfg.m_bFront) {
                //obj.mBitRate = 3  pVideoCompressorCfg.common.head.iAvgBitrate;
            }

            obj.m_iFourcc_o = pVideoCompressorCfg.common.head.iFourcc;

            obj.m_wrapper = new encObj_mediaCodec.encObj_mediaCodec_Wrapper(obj);
            obj.m_th = new Thread(obj.m_wrapper);
            obj.m_th.start();


            //
            iErr = 0;


            if (iErr != 0) {
                qdcExitCompressVideo_mediaCodec.call(pQdcObjInfo);
            }

            return iErr;
        };

        public static qdcCommProc_open.PF_qdcGetSpsPps qdcGetSpsPps_mediaCodec = (qdcCommProc_open.QOI_common pQdcObjInfo, ByteBuffer spsBuf, ByteBuffer ppsBuf, ByteBuffer vpsBuf) -> {
            int iErr = -1;
            int k;
            boolean bDbg = false;

            showInfo_open.qyFuncs.showInfo_open(0, 0, null, "qdcGetSpsPps_mediaCodec enters");

            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            if (pProcInfo.cfg.mSpecialDbg.bNo_encV) {
                return 0;
            }


            //
            CQdcObj_mediaCodec pObj = (CQdcObj_mediaCodec) pQdcObjInfo.var.pObj;
            encObj_mediaCodec obj = (encObj_mediaCodec) pObj.obj_qdcUnit;

            int i;
            for (i = 0; i < 10; i++) {
                tmpFunc.qyFuncs.myWaitOne((EventWaitHandle) pQdcObjInfo.hEvent_notifyToQuit, 1000);
                if (obj.m_bGot_spspps) break;
                continue;
            }

            if (!obj.m_bGot_spspps) return iErr;


            Log.e("m_bGot_spspps success");

            if (0 != parseSpsPps.qyFuncs.parseSpsPps(pQdcObjInfo.cfg.v.head.iFourcc, obj.m_tmpBuf_spspps, spsBuf, ppsBuf, vpsBuf)) {
                return iErr;
            }

            showInfo_open.qyFuncs.showInfo_open(0, 0, null, "qdcGetSpsPps_mediaCodec got spspps and leaves");

            iErr = 0;
            return iErr;

        };


        public static qdcCommProc_open.PF_qdcCompressVideoRun qdcCompressVideoRun_mediaCodec = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;

            String str;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            if (pProcInfo.cfg.mSpecialDbg.bNo_encV) {
                return 0;
            }

            CQdcObj_mediaCodec pObj = (CQdcObj_mediaCodec) pQdcObjInfo.var.pObj;
            int i;

            encObj_mediaCodec obj = (encObj_mediaCodec) pObj.obj_qdcUnit;

            //
            //obj.m_th.run();

            //
            for (i = 0; ; i++) {
                if (obj.m_bEnd) {
                    Log.d("qdcEncVRun_mediaCodec: obj.m_bEnd is true, quit now");
                    break;
                }
                tmpFunc.qyFuncs.myWaitOne((EventWaitHandle) pQdcObjInfo.hEvent_notifyToQuit, 1000);
            }

            iErr = 0;

            int nStep = 1;
            showInfo_open.qyFuncs.showInfo_open(0, nStep, null, "qdcEncVRun_mediaCodec leaves");

            return iErr;
        };


        public static qdcCommProc_open.PF_qdcExitDecompressAudio qdcExitDecompressAudio_mediaCodec = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            boolean bDbg = true;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();


            if (null == pQdcObjInfo.var.pObj) return 0;
            CQdcObj_mediaCodec pObj = (CQdcObj_mediaCodec) pQdcObjInfo.var.pObj;

            if (!pProcInfo.cfg.ucb__USE_decObj_a_mediaCodec_async__)
            {
                //TmpFfmpeg.DecV_exit(pObj.m_index_qdcUnit, bDbg);
                decObj_a_mediaCodec obj = (decObj_a_mediaCodec) pObj.obj_qdcUnit;
                if (null != obj) {
                    if (null != obj.m_th) {
                        // obj.m_th.join();
                        try {
                            obj.m_th.join();
                            obj.m_th = null;
                        } catch (InterruptedException e) {
                            Log.e("qy","qdcExitDecompressAudio.510:"  + e.getMessage());
                        }
                        catch (Exception e) {
                            Log.e("qy","qdcExitDecompressAudio.513:"  + e.getMessage());
                        }
                    }

                    //
                    //obj.dec_exit();
                }
            } else {
                decObj_a_mediaCodec_async obj = (decObj_a_mediaCodec_async) pObj.obj_qdcUnit;
                if (null != obj) {
                    try {
                        obj.join();

                    } catch (InterruptedException ie) {

                    }

                }

            }

            //
            pObj.obj_qdcUnit = null;

            //
            if (pQdcObjInfo.hEvent_notifyToQuit != null) {
                EventWaitHandle hEvt = (EventWaitHandle) pQdcObjInfo.hEvent_notifyToQuit;
                hEvt.Close();
                pQdcObjInfo.hEvent_notifyToQuit = null;
            }

            pQdcObjInfo.bb_input = null;
            pQdcObjInfo.bb_output = null;
            pQdcObjInfo.var.pObj = null;
            return 0;
        };


        public static qdcCommProc_open.PF_qdcInitDecompressAudio qdcInitDecompressAudio_mediaCodec = (qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pAudioCompressorCfg, qyCommProc_open.QY_AUDIO_HEADER pAh_compress, qyCommProc_open.QY_AUDIO_HEADER pAh_decompress, qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;
            boolean bDbg = true;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            if (pQdcObjInfo.var.pObj != null) return -1;

            //
            //
            pQdcObjInfo.cfg.a = pAudioCompressorCfg.common;


            //
            pQdcObjInfo.var.pObj = new CQdcObj_mediaCodec();
            if (pQdcObjInfo.var.pObj == null) return iErr;
            CQdcObj_mediaCodec pObj = (CQdcObj_mediaCodec) pQdcObjInfo.var.pObj;

            //
            //
            pQdcObjInfo.var.ucbTransformFuncsOk = true;


            //
            pObj.bDec = true;

            //
            //iFourcc = pAudioCompressorCfg.common.head.iFourcc;

            //

            //
            String pName = String.format("qdcDecAEvt" + qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call());
            pQdcObjInfo.hEvent_notifyToQuit = new EventWaitHandle(false, EventResetMode.AutoReset, new String(pName));

            //
            if (qyq2.qyFuncs.isQ2Warning(pQdcObjInfo.cfg.a.pAudioQ2, 5)) {
                qyq2.qyFuncs.emptyQ2(pQdcObjInfo.cfg.a.pAudioQ2);
                //
                String str;
                str = String.format("qdcInitDecA: pAudioQ2 is full, empty it");
                Log.d(str);
            }

            //if (!pProcInfo.cfg.ucb__USE_decObj_a_mediaCodec_async__)
            {
                pObj.obj_qdcUnit = new decObj_a_mediaCodec();
                decObj_a_mediaCodec obj = (decObj_a_mediaCodec) pObj.obj_qdcUnit;

                //
                //
                obj.m_qoi = pQdcObjInfo;
                obj.m_wrapper = new decObj_a_mediaCodec.decObj_a_mediaCodec_Wrapper(obj);
                obj.m_th = new Thread(obj.m_wrapper, "codec test4");
                //obj.m_th.start();
            }
            /*
            else {
                pObj.obj_qdcUnit = new decObj_a_mediaCodec_async();
                decObj_a_mediaCodec_async obj = (decObj_a_mediaCodec_async) pObj.obj_qdcUnit;

                //
                obj.m_qoi = pQdcObjInfo;

                //
                obj.start();

            }

             */

            iErr = 0;


            if (iErr != 0) {
                qdcExitDecompressAudio_mediaCodec.call(pQdcObjInfo);
            }

            return iErr;

        };

        public static qdcCommProc_open.PF_qdcDecompressAudio qdcDecompressAudio_mediaCodec = (qdcCommProc_open.QOI_common pQdcObjInfo, qyCommProc_open.QY_AUDIO_HEADER pAh_compress, byte[] pInput, int uiInputLen, int iSampleTimeInMs, int ui_rtTimeLen) -> {
            int iErr = -1;
            int iPaddingSize = 128;

            boolean bDbg = false;
            //Log.d("not supported");

            //iErr = 0;


            return iErr;
        };

        public static qdcCommProc_open.PF_qdcDecompressAudioRun qdcDecompressAudioRun_mediaCodec = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;
            int i;

            boolean bDbg = false;
            String str;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            CQdcObj_mediaCodec pObj = (CQdcObj_mediaCodec) pQdcObjInfo.var.pObj;

            if (!pProcInfo.cfg.ucb__USE_decObj_a_mediaCodec_async__) {
                decObj_a_mediaCodec obj = (decObj_a_mediaCodec) pObj.obj_qdcUnit;

                str = String.format("qdcDecARun_mediaCodec enters");
                showInfo_open.qyFuncs.showInfo_open(str);

                //
                obj.m_th.run();;


                //
                for (i = 0; ; i++) {
                    if (obj.m_bEnd) {
                        break;
                    }
                    //
                    tmpFunc.qyFuncs.myWaitOne((EventWaitHandle) pQdcObjInfo.hEvent_notifyToQuit, 1000);
                }
            } else {

                decObj_a_mediaCodec_async obj = (decObj_a_mediaCodec_async) pObj.obj_qdcUnit;

                for (i = 0; ; i++) {
                    if (obj.m_bEnd) {
                        break;
                    }
                    tmpFunc.qyFuncs.myWaitOne((EventWaitHandle) pQdcObjInfo.hEvent_notifyToQuit, 1000);
                }
            }


            iErr = 0;


            str = String.format("qdcDecARun_mediaCodec leaves");
            int nStep = 1;
            showInfo_open.qyFuncs.showInfo_open(0, nStep, null, str);

            return iErr;
        };


    }


}

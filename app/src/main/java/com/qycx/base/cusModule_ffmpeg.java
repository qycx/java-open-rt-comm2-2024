
package com.qycx.base;

import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferVideoData;
import static com.qycx.base.qyCommProc_open.Consts.WAVE_FORMAT_MPEGLAYER3;
import static com.qycx.base.qyq2.qyFuncs.emptyQ2;
import static com.qycx.base.qyq2.qyFuncs.isQ2Warning;

import com.qycx.base.util.Log;
import com.qycx.tmpffmpeglibrary.TmpFfmpeg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;


public class cusModule_ffmpeg {
    //
    static class CQdcObj_ffmpeg {
        public int m_index_qdcUnit;
        //
        public boolean bDec;

        //
        public CQdcObj_ffmpeg() {
            m_index_qdcUnit = -1;
            //
            bDec = false;
            //
            //memset(output, 0, sizeof(output));
        }
    }

    ;


    public static class qyFuncs_Droid {

        //
        public static qdcCommProc_open.PF_qdcInit qdcInit_ffmpeg = (qdcCommProc_open.QDC_MGR p) -> TmpFfmpeg.qdc_init();

        public static qdcCommProc_open.PF_qdcExit qdcExit_ffmpeg = (qdcCommProc_open.QDC_MGR p) -> TmpFfmpeg.qdc_exit();

        public static int newQdcUnitIndex_ffmpeg() {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            int iRet = -1;
            boolean bDbg = true;

            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                if (syncObj.sync("", pProcInfo.hMutex_syncQdcUnit,"newQdcUnitIndex_ffmpeg.52") != 0) {
                    return iRet;
                }
                iRet = TmpFfmpeg.newQdcUnitIndex(bDbg);
                syncObj.Dispose();
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }
            return iRet;

        }

        public static void freeQdcUnitIndex_ffmpeg(int index) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            boolean bDbg = true;

            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                if (syncObj.sync("", pProcInfo.hMutex_syncQdcUnit,"freeQdcUnitIndex_ffmpeg.71") != 0) return;

                TmpFfmpeg.freeQdcUnitIndex(index, bDbg);
            } catch (Exception e) {
                Log.e(e.getMessage());
            }
        }

        public static qdcCommProc_open.PF_qdcExitCompressVideo qdcExitCompressVideo_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            boolean bDbg = true;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();


            if (null == pQdcObjInfo.var.pObj) return 0;
            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;


            //
            if (0 != TmpFfmpeg.encV_exit(pObj.m_index_qdcUnit, bDbg)) {
                //Log.d("qdcExitCompressVideo_android: enc_exit failed");
            }

            //
            pQdcObjInfo.bb_output = null;
            pQdcObjInfo.bb_input = null;

            //
            freeQdcUnitIndex_ffmpeg(pObj.m_index_qdcUnit);
            pObj.m_index_qdcUnit = -1;

            //
            pQdcObjInfo.var.pObj = null;

            return 0;
        };

        public static qdcCommProc_open.PF_qdcInitCompressVideo qdcInitCompressVideo_ffmpeg = (qnmCommProc_mis.VIDEO_COMPRESSOR_CFG pVideoCompressorCfg,
                                                                                              qyCommProc_open.QY_VIDEO_HEADER pVh_decompress,
                                                                                              qyCommProc_open.QY_VIDEO_HEADER pVh_compress,
                                                                                              qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;
            int iFourcc;
            boolean bDbg = false;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            if (pQdcObjInfo.var.pObj != null) return -1;

            pQdcObjInfo.cfg.v = pVideoCompressorCfg.common;
            pQdcObjInfo.var.pObj = new CQdcObj_ffmpeg();

            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            pObj.m_index_qdcUnit = newQdcUnitIndex_ffmpeg();
            if (pObj.m_index_qdcUnit < 0) {
                qdcExitCompressVideo_ffmpeg.call(pQdcObjInfo);
                return iErr;
            }

            {
                iFourcc = pVideoCompressorCfg.common.head.iFourcc;
            }

            if (TmpFfmpeg.encV_init(pObj.m_index_qdcUnit, iFourcc, pVh_decompress.bih.biWidth, pVh_decompress.bih.biHeight, bDbg) != 0)
                return -1;

            pQdcObjInfo.bb_input = ByteBuffer.allocateDirect(pVh_decompress.bih.biSizeImage);
            pQdcObjInfo.bb_output = ByteBuffer.allocateDirect(bufSize_transferVideoData);
            pQdcObjInfo.nLeft_output = 0;
            pQdcObjInfo.bHeadLen = 1 + 12;


            iErr = 0;


            if (iErr != 0) {
                qdcExitCompressVideo_ffmpeg.call(pQdcObjInfo);
            }

            return iErr;
        };


        public static qdcCommProc_open.PF_qdcGetSpsPps qdcGetSpsPps_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo, ByteBuffer spsBuf, ByteBuffer ppsBuf, ByteBuffer vps) -> {
            int iErr = -1;
            ByteBuffer bb = ByteBuffer.allocateDirect(1024);
            IntBuffer lens = IntBuffer.allocate(3);
            byte[] p;
            int k;
            boolean bDbg = false;

            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            if (0 != TmpFfmpeg.encV_getSpsPps(pObj.m_index_qdcUnit, bb, lens.array(), bDbg))
                return -1;

            p = bb.array();
            k = 0;

//            switch (pQdcObjInfo.cfg.v.head.iFourcc) {
//                case CONST_fourcc_h264:
//                    spsBuf.put(p, 0, lens.get(k));
//                    puiSpsSize.set(lens.get(k++));
//                    ppsBuf.put(p, lens.get(k), lens.get(k));
//                    puiPpsSize.set(lens.get(k++));
//                    puiVpsSize.set(0);
//                    break;
//                default:
//                    return iErr;
//            }

            iErr = 0;
            return iErr;
        };


        //
        public static qdcCommProc_open.PF_qdcCompressVideo qdcCompressVideo_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo, qyCommProc_open.BITMAPINFOHEADER pBmpInfo_input,
                                                                                      byte[] pInput, int iSampleTimeInMs) -> {
            int iErr = -1;
            //
            boolean bDbg = false;
            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            //
            if (pQdcObjInfo.bb_input == null || pQdcObjInfo.bb_output == null) return -1;
            ByteBuffer bb_input = (ByteBuffer) pQdcObjInfo.bb_input;
            ByteBuffer bb_output = (ByteBuffer) pQdcObjInfo.bb_output;
            byte[] pBbInput = bb_input.array();
            if (null == pBbInput) return iErr;
            byte[] pBbOutput = bb_output.array();
            if (null == pBbOutput) return iErr;

            if (pBmpInfo_input.biSizeImage > bb_input.capacity()) return iErr;
            //qyMemoryFunc.qyFuncs.mymemcpy(pBbInput, pInput, (int) pBmpInfo_input.biSizeImage);

            if (pQdcObjInfo.nLeft_output != 0) {
                showTaskStatusFunc.qyFuncs.showNotification_open(0, 0, 0, "Err: qdcCompressVideo_android; nLeft is not 0");
                return iErr;
            }
            int bufSize_output = bb_output.capacity();
            if (bufSize_output <= pQdcObjInfo.bHeadLen) return iErr;
            int len_output0;
            int len_output1;
            int len_output2;
            //
            len_output0 = bufSize_output - pQdcObjInfo.bHeadLen;
            //
            int len = 0;
//            pBbOutput = pQdcObjInfo.bHeadLen;
//            len = Byte.BYTES;// sizeof( byte);
//            *(int) (pBbOutput + len) = len_output0;
//            //
//            if (0 != TmpFfmpeg.encV_data(pObj.m_index_qdcUnit, bb_input, pBmpInfo_input.biSizeImage, bb_output, bufSize_output, bDbg)) {
//                //Log.d("qdcCompressVideo_android: enc_data failed");
//                return iErr;
//            }
//            //
//            len = Byte.BYTES;//sizeof( byte);
//            len_output0 = pBbOutput + len;
//            len += Byte.BYTES;//Integer.BYTES;
//            len_output1 = pBbOutput + len;
//            len += Byte.BYTES;//Integer.BYTES;
//            len_output2 = pBbOutput + len;
//            len += Byte.BYTES;//Integer.BYTES;
//            if (len > pQdcObjInfo.bHeadLen) {
//                return iErr;
//            }
            //
            pQdcObjInfo.nLeft_output = len_output0;
            if (pQdcObjInfo.nLeft_output != 0) {
                int ii = 0;
            }

            int kk;
//            switch (len_output1) {
//                case 888:
//                    break;
//                case 889:
//                case 890:
//                    kk = 0;
//                    break;
//                default:
//                    kk = 1;
//                    break;
//            }

            iErr = 0;


            return iErr;

        };

        //
        public static qdcCommProc_open.PF_qdcGetOutputByIndex qdcGetOutputByIndex_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo, int index, ByteBuffer pbOutData,qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead head) -> {
            int iErr = -1;
            boolean bDbg = false;

            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            //
            if (pQdcObjInfo.bb_output == null) {
                //Log.d("qdcGetOutputByIndex_android failed, bb_output is null");
                return -1;
            }

            //
            ByteBuffer bb_output = (ByteBuffer) pQdcObjInfo.bb_output;
            byte[] pBbOutput = bb_output.array();
            if (null == pBbOutput) return iErr;

            //
            if (pObj.bDec) {
                //TmpFfmpeg.Dec_getOutputByIndex()
                if (pQdcObjInfo.nPktsDeced == 0) {
                    head.uiLen = 0;
                    return -1;
                }
                if (index == 0) {

                    if (head.uiLen < pQdcObjInfo.iDecBufSize) {
                        return iErr;
                    }

                    System.arraycopy(pBbOutput, pQdcObjInfo.bHeadLen, pbOutData, 0, pQdcObjInfo.iDecBufSize);
                    head.uiLen = (int) pQdcObjInfo.iDecBufSize;
                    pQdcObjInfo.iDecBufSize = 0;
                    iErr = 0;
                    return iErr;
                }
                if (0 != TmpFfmpeg.dec_getOutputByIndex(pObj.m_index_qdcUnit, index - 1, bb_output, bb_output.capacity(), bDbg))
                    return iErr;
                //
//                byte[] p;
//                p = pBbOutput + 1;
//                int iDecBufSize = (int) p;
//                if (iDecBufSize == 0) return iErr;
//                if (puiDataSize < iDecBufSize) return iErr;
//                qyMemoryFunc.qyFuncs.mymemcpy(pbOutData, pBbOutput + pQdcObjInfo.bHeadLen, (int) iDecBufSize);
//                puiDataSize = (int) iDecBufSize;
                iErr = 0;
                return iErr;
            } else {

                if (0 == pQdcObjInfo.nLeft_output) {
                    head.uiLen = 0;
                    return -1;
                }

                if (pQdcObjInfo.nLeft_output > head.uiLen) {
                    return iErr;
                }

                bb_output.position(pQdcObjInfo.bHeadLen);
//                System.arraycopy(pBbOutput, 13, pbOutData, 0, pQdcObjInfo.nLeft_output);
                head.uiLen = (int) pQdcObjInfo.nLeft_output;

                while (pQdcObjInfo.nLeft_output > 0) {
                    byte b = bb_output.get();
                    pbOutData.put(b);
                    pQdcObjInfo.nLeft_output--;
                }

                /*
                byte[]b_output=bb_output.array();
                pbOutData.put(b_output,pQdcObjInfo.bHeadLen,pQdcObjInfo.nLeft_output);

                 */


                //
//                System.arraycopy(pBbOutput, 13 + 4, pbOutData.array(), 0, pQdcObjInfo.nLeft_output);
//                qyMemoryFunc.qyFuncs.mymemcpy(pbOutData, pBbOutput, (int) pQdcObjInfo.nLeft_output);
                pQdcObjInfo.nLeft_output = 0;
            }

            iErr = 0;


            return iErr;
        };

        public static qdcCommProc_open.PF_qdcClearOutputs qdcClearOutputs_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            boolean bDbg = false;
            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            if (pObj.bDec) {
                TmpFfmpeg.dec_clearOutputs(pObj.m_index_qdcUnit, bDbg);

            } else {

            }

            //
            return 0;
        };


        public static qdcCommProc_open.PF_qdcExitDecompressVideo qdcExitDecompressVideo_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;
            boolean bDbg = true;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();


            if (null == pQdcObjInfo.var.pObj) return 0;
            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            TmpFfmpeg.decV_exit(pObj.m_index_qdcUnit, bDbg);

            //
            pQdcObjInfo.bb_input = null;
            pQdcObjInfo.bb_output = null;

            freeQdcUnitIndex_ffmpeg(pObj.m_index_qdcUnit);
            pObj.m_index_qdcUnit = -1;

            //
            pQdcObjInfo.var.pObj = null;


            //
            iErr = 0;
            return iErr;
        };


        public static qdcCommProc_open.PF_qdcInitDecompressVideo qdcInitDecompressVideo_ffmpeg = (qnmCommProc_mis.VIDEO_COMPRESSOR_CFG pVideoCompressorCfg,
                                                                                                  qyCommProc_open.QY_VIDEO_HEADER pVh_compress,
                                                                                                  qyCommProc_open.QY_VIDEO_HEADER pVh_decompress,
                                                                                                  qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;
            boolean bDbg = true;


            int iFourcc;


            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            if (pQdcObjInfo.var.pObj != null) return -1;

            //
            pQdcObjInfo.var.pObj = new CQdcObj_ffmpeg();
            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            pObj.m_index_qdcUnit = newQdcUnitIndex_ffmpeg();
            if (pObj.m_index_qdcUnit < 0) {
                Log.d("qdcInitDeCompressV failed, newQdcUnitIndex failed");
                qdcExitDecompressVideo_ffmpeg.call(pQdcObjInfo);
                return iErr;
            }

            pObj.bDec = true;


            //fixed (byte[] fourccStr = pVideoCompressorCfg.common.fourccStr) 
            {
                iFourcc = pVideoCompressorCfg.common.head.iFourcc;
            }

            if (0 != TmpFfmpeg.decV_init(pObj.m_index_qdcUnit, iFourcc, bDbg)){
                qdcExitDecompressVideo_ffmpeg.call(pQdcObjInfo);
                return iErr;
            }

            //
            pQdcObjInfo.bHeadLen = 1 + 7 * Integer.BYTES;
            pQdcObjInfo.nPktsDeced = 0;


            //
            iErr = 0;

            if (0 != iErr) {
                qdcExitDecompressVideo_ffmpeg.call(pQdcObjInfo);
            }

            return iErr;
        };

        public static qdcCommProc_open.PF_qdcDecompressVideo qdcDecompressVideo_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo,
                                                                                          qyCommProc_open.BITMAPINFOHEADER pBmpInfo_input, ByteBuffer pInput,
                                                                                          int iSampleTimeInMs) ->   //  2012/06/17
        {
            int iErr = -1;
            int iPaddingSize = 128;

            boolean bDbg = false;
            String str;

            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            //
            if (pQdcObjInfo.bb_input == null) {
                pQdcObjInfo.bb_input = ByteBuffer.allocateDirect(bufSize_transferVideoData);
                if (pQdcObjInfo.bb_input == null) return iErr;
                //
                //Log.d("qdcDecVideo_android: bb_input alloc");
            }
            if (pQdcObjInfo.bb_output == null) {
                pQdcObjInfo.bb_output = ByteBuffer.allocateDirect(128);
                if (pQdcObjInfo.bb_output == null) return iErr;
                //
                //Log.d("qdcDecVideo_android: bb_outpt alloc");
            }
            ByteBuffer bb_input = (ByteBuffer) pQdcObjInfo.bb_input;
            ByteBuffer bb_output = (ByteBuffer) pQdcObjInfo.bb_output;
            byte[] pBbInput = bb_input.array();
            if (null == pBbInput) return iErr;
            byte[] pBbOutput = bb_output.array();
            if (null == pBbOutput) return iErr;

            //
            if (pBmpInfo_input.biSizeImage + iPaddingSize > bb_input.capacity()) {
                //Log.d("qdcDecVideo failed: bb_input is too small");
                return iErr;
            }
            //qyMemoryFunc.qyFuncs.mymemcpy( pBbInput, pInput, (int) pBmpInfo_input.biSizeImage);

            //
            int total_nums = 7;
            byte len = (byte) (1 + total_nums * 4);
            //int head_array[7];
            if (pQdcObjInfo.bHeadLen < len) {
                //Log.d("qdcDecVideo failed, bHeadLen error");
                return iErr;
            }

            //
//            pBbOutput = pQdcObjInfo.bHeadLen;

            //
            if (0 != TmpFfmpeg.decV_data(pObj.m_index_qdcUnit, bb_input, pBmpInfo_input.biSizeImage, iPaddingSize, bb_output, bb_output.capacity(), bDbg))
                return iErr;

            /*
            byte[] p = pBbOutput;
            p++;
            //
            int k;
            k = 0;
            int tmpiPaddingSize = (int) (p + k * Integer.BYTES);
            k++;
            int nPktsDeced = (int) (p + k * Integer.BYTES);
            k++;
            int iFourcc_dec = (int) (p + k * Integer.BYTES);
            ;
            k++;
            int iW = (int) (p + k * Integer.BYTES);
            k++;
            int iH = (int) (p + k * Integer.BYTES);
            ;
            k++;
            int iDecBufSize = (int) (p + k * Integer.BYTES);
            ;
            k++;
            int bBufTooSmall = (int) (p + k * Integer.BYTES);
            ;
            k++;

            if (0 != bBufTooSmall) {
                switch (iFourcc_dec) {
                    case CONST_fourcc_I420:
                        qyCommProc_open.BITMAPINFOHEADER bih;
                        videoFunc_open.qyFuncs.makeBmpInfoHeader_i420(12, iW, iH, bih);
                        int bufSize_output = bih.biSizeImage + pQdcObjInfo.bHeadLen;
                        if (bufSize_output > bb_output.capacity()) {
                            pQdcObjInfo.bb_output = null;
                            //
                            pQdcObjInfo.bb_output = ByteBuffer.allocateDirect(bufSize_output);
                            if (pQdcObjInfo.bb_output == null) return iErr;
                            //Log.d("qdcDecVideo_android: bb realloc");
                        }
                        break;
                    case CONST_fourcc_YV12:
                    default:
                        //Log.d("qdcDecVideo_android failed, iFourcc_dec unsupported");
                        return iErr;
                }

            }

            //
            pQdcObjInfo.nPktsDeced = nPktsDeced;
            pQdcObjInfo.iDecBufSize = iDecBufSize;


*/
            return 0;
        };


        public static qdcCommProc_open.PF_qdcExitCompressAudio qdcExitCompressAudio_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            boolean bDbg = true;

            if (null == pQdcObjInfo.var.pObj) return 0;
            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            TmpFfmpeg.encA_exit(pObj.m_index_qdcUnit, bDbg);

            //
            pQdcObjInfo.bb_input = null;
            pQdcObjInfo.bb_output = null;


            freeQdcUnitIndex_ffmpeg(pObj.m_index_qdcUnit);
            pObj.m_index_qdcUnit = -1;

            //
            pQdcObjInfo.var.pObj = null;

            return 0;
        };


        public static qdcCommProc_open.PF_qdcInitCompressAudio qdcInitCompressAudio_ffmpeg = (qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pAudioCompressorCfg_reserved,
                                                                                              qyCommProc_open.QY_AUDIO_HEADER pAh_decompress,
                                                                                              qyCommProc_open.QY_AUDIO_HEADER pAh_compress,
                                                                                              qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;

            boolean bDbg = true;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            if (pQdcObjInfo.var.pObj != null) {
                qdcExitCompressAudio_ffmpeg.call(pQdcObjInfo);
                return -1;
            }

            pQdcObjInfo.var.pObj = new CQdcObj_ffmpeg();

            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            pObj.m_index_qdcUnit = newQdcUnitIndex_ffmpeg();
            if (pObj.m_index_qdcUnit < 0) {
                Log.d("qdcInitCompressA failed, newQdcUnitIndex failed");
                qdcExitCompressAudio_ffmpeg.call(pQdcObjInfo);
                return iErr;
            }

            if (pAh_decompress.wfx.nAvgBytesPerSec == 0) {
                Log.d("initEncA failed, nAvgBytesPerSec is 0");
                qdcExitCompressAudio_ffmpeg.call(pQdcObjInfo);
                return -1;
            }

            if (0 != TmpFfmpeg.encA_init(pObj.m_index_qdcUnit, (short) pAudioCompressorCfg_reserved.wf_compress.wFormatTag, bDbg)) {
                qdcExitCompressAudio_ffmpeg.call(pQdcObjInfo);
                return -1;
            }

            pAh_compress = pAh_decompress;
            pAh_compress.wfx.wFormatTag = WAVE_FORMAT_MPEGLAYER3;

            pQdcObjInfo.bb_input = ByteBuffer.allocateDirect((int) pAh_decompress.wfx.nAvgBytesPerSec);
            pQdcObjInfo.bb_output = ByteBuffer.allocateDirect(bufSize_transferAudioData);
            pQdcObjInfo.bb_input.order(ByteOrder.LITTLE_ENDIAN);
            pQdcObjInfo.bb_output.order(ByteOrder.LITTLE_ENDIAN);
            pQdcObjInfo.nLeft_output = 0;
            pQdcObjInfo.bHeadLen = 1 + 12;

            iErr = 0;
            return iErr;
        };


        public static qdcCommProc_open.PF_qdcCompressAudio qdcCompressAudio_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo,
                                                                                      qyCommProc_open.QY_AUDIO_HEADER pAh_decompress, byte[] pInput,
                                                                                      int uiInputSize, int iSampleTimeInMs) -> {

            int iErr = -1;
            //
            boolean bDbg = false;
            String str;

            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            //
            if (pQdcObjInfo.bb_input == null || pQdcObjInfo.bb_output == null) return -1;
            ByteBuffer bb_input = (ByteBuffer) pQdcObjInfo.bb_input;
            ByteBuffer bb_output = (ByteBuffer) pQdcObjInfo.bb_output;
            byte[] pBbInput = bb_input.array();
            if (null == pBbInput) return iErr;
            byte[] pBbOutput = bb_output.array();
            if (null == pBbOutput) return iErr;

            if (uiInputSize > bb_input.capacity()) return iErr;
            //qyMemoryFunc.qyFuncs.mymemcpy( pBbInput, pInput, (int) uiInputSize);
            bb_input.position(0);
            bb_input.put(pInput, 0, uiInputSize);
            bb_input.position(0);

            if (pQdcObjInfo.nLeft_output != 0) {
                showTaskStatusFunc.qyFuncs.showNotification_open(0, 0, 0, "Err: qdcCompressVideo_android; nLeft is not 0");
                return iErr;
            }
            int bufSize_output = bb_output.capacity();
            if (bufSize_output <= pQdcObjInfo.bHeadLen) return iErr;
            int len_output0;
            int len_output1;
            int len_output2;
            //
            len_output0 = bufSize_output - pQdcObjInfo.bHeadLen;
            //
            int len = 0;

            bb_output.position(0);
            bb_output.put(pQdcObjInfo.bHeadLen);
            bb_output.putInt(len_output0);
            bb_output.position(0);
            if (0 != TmpFfmpeg.encA_data(pObj.m_index_qdcUnit, bb_input, (int) uiInputSize, bb_output, bufSize_output, bDbg)) {
                Log.e("qdcCompressVideo_android: enc_data failed");
                return iErr;
            }
            //
            len = Byte.BYTES;
            bb_output.get();
            len_output0 = (int) bb_output.getInt();
            pQdcObjInfo.nLeft_output = len_output0;
            if (pQdcObjInfo.nLeft_output != 0) {
                int ii = 0;
            }

            iErr = 0;
            return iErr;
        };

        //  2016/12/05
        public static qdcCommProc_open.PF_qdcCompressAudioRun qdcCompressAudioRun_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {

            return 0;
        };

        public static qdcCommProc_open.PF_qdcExitDecompressAudio qdcExitDecompressAudio_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            boolean bDbg = true;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();


            if (null == pQdcObjInfo.var.pObj) return 0;
            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;


            TmpFfmpeg.decA_exit(pObj.m_index_qdcUnit, bDbg);

            //
            pQdcObjInfo.bb_input = null;
            pQdcObjInfo.bb_output = null;

            freeQdcUnitIndex_ffmpeg(pObj.m_index_qdcUnit);
            pObj.m_index_qdcUnit = -1;

            pQdcObjInfo.var.pObj = null;

            return 0;
        };

        public static qdcCommProc_open.PF_qdcInitDecompressAudio qdcInitDecompressAudio_ffmpeg = (qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pAudioCompressorCfg,
                                                                                                  qyCommProc_open.QY_AUDIO_HEADER pAh_compress,
                                                                                                  qyCommProc_open.QY_AUDIO_HEADER pAh_decompress,
                                                                                                  qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;
            boolean bDbg = true;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            if (pQdcObjInfo.var.pObj != null) return -1;

            //
            pQdcObjInfo.cfg.a = pAudioCompressorCfg.common;


            //
            pQdcObjInfo.var.pObj = new CQdcObj_ffmpeg();

            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;

            pObj.m_index_qdcUnit = newQdcUnitIndex_ffmpeg();
            if (pObj.m_index_qdcUnit < 0) {
                Log.e("qdcInitDeCompressA failed, newQdcUnitIndex failed");
                qdcExitDecompressAudio_ffmpeg.call(pQdcObjInfo);
                return iErr;
            }

            //
            pObj.bDec = true;

            //
            //
            if (isQ2Warning(pQdcObjInfo.cfg.a.pAudioQ2, 5)) {
                emptyQ2(pQdcObjInfo.cfg.a.pAudioQ2);
                //
                String str;
                str = String.format("qdcInitDecA: pAudioQ2 is full, empty it");
                Log.d(str);
            }


            //
            if (0 != TmpFfmpeg.decA_init(pObj.m_index_qdcUnit, (short) pAudioCompressorCfg.wf_compress.wFormatTag, bDbg))
                return -1;

            //
            pQdcObjInfo.bHeadLen = 1 + 7 * Integer.BYTES;
            pQdcObjInfo.nPktsDeced = 0;


            iErr = 0;


            if (iErr != 0) {
                qdcExitDecompressAudio_ffmpeg.call(pQdcObjInfo);
            }

            return iErr;
        };

        public static qdcCommProc_open.PF_qdcDecompressAudio qdcDecompressAudio_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo, qyCommProc_open.QY_AUDIO_HEADER pAh_compress, byte[] pInput,
                                                                                          int uiInputLen, int iSampleTimeInMs, int ui_rtTimeLen) -> {
            int iErr = -1;
            int iPaddingSize = 128;

            boolean bDbg = false;

            CQdcObj_ffmpeg pObj = (CQdcObj_ffmpeg) pQdcObjInfo.var.pObj;
            //
            if (pQdcObjInfo.bb_input == null) {
                pQdcObjInfo.bb_input = ByteBuffer.allocateDirect(bufSize_transferAudioData);
                if (pQdcObjInfo.bb_input == null) return iErr;
                //
                //Log.d("qdcDecAudio_android: bb_input alloc");
            }
            if (pQdcObjInfo.bb_output == null) {
                int nAvgBytesPerSec = (int) pAh_compress.wfx.nSamplesPerSec * pAh_compress.wfx.wBitsPerSample / 8 * pAh_compress.wfx.nChannels;
                if (nAvgBytesPerSec == 0) {
                    //Log.d("qdcDecA_android faile, nAvgBytesPerSec is 0");
                    return iErr;
                }
                pQdcObjInfo.bb_output = ByteBuffer.allocateDirect(nAvgBytesPerSec + pQdcObjInfo.bHeadLen);
                if (pQdcObjInfo.bb_output == null) return iErr;
                //
                //Log.d("qdcDecAudio_android: bb_outpt alloc");
            }
            ByteBuffer bb_input = (ByteBuffer) pQdcObjInfo.bb_input;
            ByteBuffer bb_output = (ByteBuffer) pQdcObjInfo.bb_output;
            byte[] pBbInput = bb_input.array();
            if (null == pBbInput) return iErr;
            byte[] pBbOutput = bb_output.array();
            if (null == pBbOutput) return iErr;

            //
            if (uiInputLen + iPaddingSize > bb_input.capacity()) {
                //Log.d("qdcDecVideo failed: bb_input is too small");
                return iErr;
            }
            //qyMemoryFunc.qyFuncs.mymemcpy( pBbInput, pInput, (int) uiInputLen);

            //
            int total_nums = 7;
            byte len = (byte) (1 + total_nums * 4);
            //int head_array[7];
            if (pQdcObjInfo.bHeadLen < len) {
                //Log.d("qdcDecVideo failed, bHeadLen error");
                return iErr;
            }
/*
            pBbOutput = pQdcObjInfo.bHeadLen;

            if (0 != TmpFfmpeg.decA_data(pObj.m_index_qdcUnit, bb_input, (int) uiInputLen, iPaddingSize, bb_output, bb_output.capacity(), bDbg)) {
                return iErr;
            }

            byte[] p = pBbOutput;
            p++;
            //
            int k;
            k = 0;
            int tmpiPaddingSize = (int) (p + k * Integer.BYTES);
            k++;
            int nPktsDeced = (int) (p + k * Integer.BYTES);
            k++;
            int iFmt_dec = (int) (p + k * Integer.BYTES);
            ;
            k++;
            int iDecBufSize = (p + k * Integer.BYTES);
            ;
            k++;
            int bBufTooSmall = (int) (p + k * Integer.BYTES);
            ;
            k++;

            //
            if (0 != bBufTooSmall) {
                //Log.d("qdcDecAudio_android failed, buf too small");
                return iErr;

            }
            if (nPktsDeced != 0) {
                int kk = 0;
            }

            //
            pQdcObjInfo.nPktsDeced = nPktsDeced;
            pQdcObjInfo.iDecBufSize = iDecBufSize;
*/

            iErr = 0;


            return iErr;
        };


        //
        //  2016/12/05
        public static qdcCommProc_open.PF_qdcDecompressAudioRun qdcDecompressAudioRun_ffmpeg = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            //Log.d("not finished");
            return 0;
        };
    }
}

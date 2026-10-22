


package com.qycx.base;

import java.nio.ByteBuffer;

public class qdcCommProc_open {


    public static class Consts {
        //  2011/12/21
        public final static int CONST_qdcRet_bufTooSmall = -2101;
        public final static int CONST_qdcRet_exception = -2102;    //  2014/08/11


        //  2014/08/28
        public final static int DEFAULT_keyDistanceInS = 30;       //  2015/11/06	//  7
        //  2014/12/06
        public final static int DEFAULT_keyDistanceInS_mosaic = 30;


        //  2016/12/17
        public final static int CONST_qoiType_null = 0;
        public final static int CONST_qoiType_encA = 1;
        public final static int CONST_qoiType_decA = 2;
        public final static int CONST_qoiType_encV = 3;
        public final static int CONST_qoiType_decV = 4;

        public final static int MAX_nDecFrames_needSkipped_start = 20;
        public final static int MAX_nDecFrames_needSkipped_request = 20;

    }

    //
    public static class QDC_objInfo_varCommon {

        //
        public Object pObj;

        //
        public boolean ucbTransformFuncsOk;                      //  2014/03/26

        //
        public int cbInputBuffer;
        public int cbOutputBuffer;

        public int iDiffInMs_tStartAudio;                      //  2009/05/26
        public Object pDebugStep;                                   //  2010/04/28
        public int nDecFrame_needSkipped;

        //  2014/08/25
        public static class QDC_objInfo_varCommon_encInfo {
            public boolean bNeed_forceKeyFrame;

            //
            public long dwLastTickCnt_insertKeyFrame;

        }

        public QDC_objInfo_varCommon_encInfo encInfo = new QDC_objInfo_varCommon_encInfo();

        //
        public static class QDC_objInfo_varCommon_decInfo {
            public int nBytes_deced;
        }

        public QDC_objInfo_varCommon_decInfo decInfo = new QDC_objInfo_varCommon_decInfo();


        //  2015/09/27
        public int usPktResType_dec;

    }

    ;

    //
    public static class QOI_common_cfg {
        public qyCommProc_open.AUDIO_COMPRESSOR_cfgCommon a = new qyCommProc_open.AUDIO_COMPRESSOR_cfgCommon();      //  2016/12/06
        public qyCommProc_open.VIDEO_COMPRESSOR_cfgCommon v = new qyCommProc_open.VIDEO_COMPRESSOR_cfgCommon();       //  cfg;
    }

    ;

    public static class QOI_common_v {
        public int iFourcc;

    }

    ;

    public static class QOI_common       //QDC_OBJ_INFO
    {
        //
        //public QOI_common_v v;

        //
        public int uiType;                                //  2016/04/18
        public int usSubtype;                           //  2016/04/18
        public int iUsage;                             //  2016/04/18

        //
        public ByteBuffer bb_input;
        public ByteBuffer bb_output;
        public byte bHeadLen;

        //
        public int nLeft_output;

        //
        public int nPktsDeced;
        public int iDecBufSize;

        //
        public Object hEvent_notifyToQuit;

        //  2014/03/25
        public QOI_common_cfg cfg = new QOI_common_cfg();

        //
        public QDC_objInfo_varCommon var = new QDC_objInfo_varCommon();


        public void memset0() {

        }
    }

    ;


    public static class QDC_MGR {
    }

    ;

    public interface PF_qdcInit {
        int call(QDC_MGR p);
    }

    public interface PF_qdcExit {
        int call(QDC_MGR p);
    }

    public interface PF_qdcInitCompressAudio {
        int call(qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pAudioCompressorCfg_reserved, qyCommProc_open.QY_AUDIO_HEADER pAh_decompress, qyCommProc_open.QY_AUDIO_HEADER pAh_compress, qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcExitCompressAudio {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcCompressAudio {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo, qyCommProc_open.QY_AUDIO_HEADER pAh_decompress, byte[] pInput, int uiInputSize, int iSampleTimeInMs);
    }

    public interface PF_qdcCompressAudioRun {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcInitDecompressAudio {
        int call(qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pAudioCompressorCfg, qyCommProc_open.QY_AUDIO_HEADER pAh_compress, qyCommProc_open.QY_AUDIO_HEADER pAh_decompress, qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcExitDecompressAudio {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcDecompressAudio {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo, qyCommProc_open.QY_AUDIO_HEADER pAh_compress, byte[] pInput, int uiInputLen, int iSampleTimeInMs, int ui_rtTimeLen);
    }

    public interface PF_qdcDecompressAudioRun {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcInitCompressVideo {
        int call(qnmCommProc_mis.VIDEO_COMPRESSOR_CFG pVideoCompressorCfg_reserved, qyCommProc_open.QY_VIDEO_HEADER pVh_decompress, qyCommProc_open.QY_VIDEO_HEADER pVh_compress, qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcExitCompressVideo {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcGetSpsPps {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo, ByteBuffer spsBuf, ByteBuffer ppsBuf, ByteBuffer vpsBuf);
    }

    public interface PF_qdcCompressVideo {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo, qyCommProc_open.BITMAPINFOHEADER pBmpInfo_input, byte[] pInput, int iSampleTimeInMs);
    }

    public interface PF_qdcCompressVideoRun {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcInitDecompressVideo {
        int call(qnmCommProc_mis.VIDEO_COMPRESSOR_CFG pVideoCompressorCfg_reserved, qyCommProc_open.QY_VIDEO_HEADER pVh_compress, qyCommProc_open.QY_VIDEO_HEADER pVh_decompress, qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcExitDecompressVideo {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcDecompressVideo {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo, qyCommProc_open.BITMAPINFOHEADER pBmpInfo_input, ByteBuffer pInput, int iSampleTimeInMs);
    }

    public interface PF_qdcDecompressVideoAndOutput {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo, qyCommProc_open.BITMAPINFOHEADER pBmpInfo_input, byte[] pInput, qyCommProc_open.BITMAPINFOHEADER pBmpInfo_output, byte[] pOutput);
    }

    public interface PF_qdcDecompressVideoRun {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo);
    }

    public interface PF_qdcGetOutputByIndex {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo, int index, ByteBuffer pbOutData, qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead head);
    }

    public interface PF_qdcClearOutputs {
        int call(qdcCommProc_open.QOI_common pQdcObjInfo);
    }

}

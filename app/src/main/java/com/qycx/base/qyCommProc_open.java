


//
package com.qycx.base;

import static com.qycx.base.qyCommProc_open.Consts.bufSize_bExtraData;
import static com.qycx.base.videoFunc_open.qyFuncs.myMAKEFOURCC;

import com.qycx.base.qdc.DecMediaCodecCommon;

import java.nio.ByteBuffer;

public class qyCommProc_open {

    public static class Consts {
        public final static int bufSize_bPrivateData = 1024 + 256;
        public final static int bufSize_bExtraData = 512;

        //
        public final static int CONST_fourccStrLen = 4;


        //
        public final static int MAX_spsPpsLen = 256;

        //
        public final static int cntof_VH_stream_mems = 3;

        //
        public final static int CONST_fourcc_YUY2 = 0x32595559;    //(int)qyFuncs.myMAKEFOURCC('Y', 'U', 'Y', '2');        //  2010/05/31
        public final static int CONST_fourcc_YV12 = 0x32315659;    // (int)qyFuncs.myMAKEFOURCC('Y', 'V', '1', '2');        //  2010/07/11
        public final static int CONST_fourcc_I420 = 0x30323449;    // (int)qyFuncs.myMAKEFOURCC('I', '4', '2', '0');        //  2010/07/11
        public final static int CONST_fourcc_HDYC = 0x43594448;    // (int)qyFuncs.myMAKEFOURCC('H', 'D', 'Y', 'C');        //  2012/01/03
        //
        public final static int CONST_fourcc_v210 = 0x30313276;    // (int)qyFuncs.myMAKEFOURCC('v', '2', '1', '0');        //  2012/02/23

        //  
        public final static int CONST_fourcc_h264 = 0x34363268;    // (int)qyFuncs.myMAKEFOURCC('h', '2', '6', '4');        //  2014/02/24
        //
        public final static int CONST_fourcc_AVC = 0x20435641;        // (int)qyFuncs.myMAKEFOURCC('A', 'V', 'C', ' ');                 //  2017/10/08
        public final static int CONST_fourcc_HEVC = 0x43564548;    // (int)qyFuncs.myMAKEFOURCC('H', 'E', 'V', 'C');                    //  2017/10/08

        //
        public final static int CONST_fourcc_NV21 = 0x3132564e;

        //
        //  public final static int		CONST_fourcc_MP2T	MAKEFOURCC(  'M',  'P',  '2',  'T'	)			//  2014/03/05

        //
        //  public final static int		CONST_fourcc_vp8		0x00385056									//  2014/07/19
        public final static int CONST_fourcc_vp80 = 0x30385056;                                   //  2014/08/06
        public final static int CONST_fourcc_vp8_bad = 0x00385056;                                 //  2014/07/19

        //
        public final static int WAVE_FORMAT_MPEGLAYER3 = 0x0055; /* ISO/MPEG Layer3 Format Tag */


        //
        //
        //  2015/09/30
        public final static int CONST_pktResType_null = 0;                         //  2015/09/30
        public final static int CONST_pktResType_memory = 1;                           //  2015/09/30
        public final static int CONST_pktResType_sharedTex = 2;                            //  2015/09/30

        //
        public final static int CONST_uiTransformType_null = 0;
        public final static int CONST_uiTransformType_encVideo = 1;
        //  
        public final static int CONST_uiTransformType_dec = 2;
        //
        public final static int CONST_uiTransformType_encAudio = 3;

        //
        public final static String MIME_TYPE_avc = "video/avc";    // H.264 Advanced Video Coding

        //
        public final static String MIME_TYPE_hevc = "video/hevc";

        //
        public final static String MIME_TYPE_mp3 = "audio/mpeg";    // 


    }

    public interface PF_bTransformVideoQuit {
        boolean call(Object pQY_TRANSFORM, Object p1, Object p2);
    }

    public interface PF_transformGetSpsPps {
        int call(int uiTransformType, Object pQY_TRANSFORM, int index_pMems_from, ByteBuffer pBuf, int puiSize);
    }

    public interface PF_transformGetBih {
        int call(Object pQY_TRANSFORM, BITMAPINFOHEADER pBih, Object p2);
    }

    public interface PF_getNextVPkt {
        int call(Object pCurPkt_myDRAW_VIDEO_DATA, genericQueue.GENERIC_Q pInDataCacheQ, qyq2.QY_Q2 pVideoQ2);
    }

    public interface PF_parseVPkt_i {
        int call(int uiTransformType, Object pParent_transform, Object pPkt_myDRAW_VIDEO_DATA, int p_old_lPktId_alloc, byte[] ppInput, int puiSize);
    }

    public interface PF_parseDecAPkt_o {
        int call(Object pPktParam, Object ppWfx, byte[] ppOutput, int puiSize);
    }

    public interface PF_parseDecVPkt_o {
        int call(Object pPktParam, byte[] ppBih, byte[] ppOutput, int puiSize);
    }

    public interface PF_clean_myPLAY_AUDIO_DATA {
        void call(Object pmyPLAY_AUDIO_DATA, String hint);
    }

    public interface PF_clean_myDRAW_VIDEO_DATA {
        void call(Object pmyDRAW_VIDEO_DATA, String hint);
    }

    public interface PF_detachPkt {
        int call(Object pPkt_myDRAW_VIDEO_DATA, Object p1);
    }

    public interface PF_updateDecVideoParam {
        int call(BITMAPINFOHEADER pBih, double dFrameRate, Object pParent_transform, int uiTransformType);
    }

    public interface PF_doPostEnc {
        int call(Object pQDC_OBJ_INFO, byte[] pInput, int inputSize, int iSampleTimeInMs, int uiPts, byte[] pBuf_pkt);
    }

    public interface PF_myLoadNextFrame {
        int call(short usPktResType, byte[] p0_pInput, int p1_uiInputSize, byte[] p2_myLoadNextFrame);
    }

    public interface PF_smplPCMReader_LoadNextFrame {
        int call(Object pQdcObjInfoParam, int pold_lPktId, PF_myLoadNextFrame pf_myLoadNextFrame, byte[] p2_myLoadNextFrame, boolean pbNeed_forceKeyFrame);
    }

    public interface PF_smplYUVReader_LoadNextFrame {
        int call(Object pQdcObjInfoParam, int pold_lPktId, PF_myLoadNextFrame pf_myLoadNextFrame, byte[] p2_myLoadNextFrame, boolean pbNeed_forceKeyFrame);
    }

    public interface PF_smplBitstreamReader_ReadNextFrame_func {
        int call(DecMediaCodecCommon dm, ReadNextFrameFucParams readNextFrameFucParams);
    }

    public interface PF_data_to_pcm {
        int call(Object pQdcObjInfoParam, byte[] pData, byte[] pYUVWriter_var);
    }

    public interface PF_img_to_yuv {
        int call(Object pQdcObjInfoParam, byte[] pImg, int cropW, int cropH, int pitch, int w, int h, byte[] pYUVWriter_var);
    }

    public interface PF_smplPCMWriter_WriteNextFrame {
        int call(Object pQdcObjInfoParam, Object pQDC_OBJ_transCommon, int iFourcc, byte[] pYUVWriter_var, byte[] pImg, PF_data_to_pcm pf_data_to_pcm, double dFrameRate, String hint);
    }

    public interface PF_smplYUVWriter_WriteNextFrame {
        int call(Object pQdcObjInfoParam, Object pQDC_OBJ_transCommon, int iFourcc, byte[] pYUVWriter_var, byte[] pImg, int cropW, int cropH, int pitch, PF_img_to_yuv pf_img_to_yuv, double dFrameRate, String hint);
    }

    public interface PF_bTransformAudioQuit {
        boolean call(Object pQY_TRANSFORM, Object p1, Object p2);
    }

    public interface PF_getNextAPkt {
        myPlayAudioData.myPLAY_AUDIO_DATA call(myPlayAudioData.myPLAY_AUDIO_DATA pCurPkt, genericQueue.GENERIC_Q pInDataCacheQ, qyq2.QY_Q2 pAudioQ2);
    }


    public static class AUDIO_COMPRESSOR_cfgCommon_head {
        /**
         * @see qmcVideoCapture.Consts
         * 音频只用 21
         */
        public byte ucCompressors; // 压缩的方式
        public int iAvgBitrateInKbps; // 码流 (平均码流 音频码流32K )

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Byte.BYTES + Integer.BYTES);
            buffer.put(ucCompressors);
            buffer.putInt(iAvgBitrateInKbps);
            return buffer.array();
        }

    }

    public static class AUDIO_COMPRESSOR_cfgCommon {

        public AUDIO_COMPRESSOR_cfgCommon_head head = new AUDIO_COMPRESSOR_cfgCommon_head();


        /////////////////////
        //  2016/12/09
        //  2014/03/25
        public Object pCurPkt;
        public int size_pCurPkt;
        public genericQueue.GENERIC_Q pInDataCacheQ;
        public qyq2.QY_Q2 pAudioQ2;
        //
        public genericQueue.GENERIC_Q unused_pTmpDataQ;
        //
        public int uiTransformType_pParent;
        public Object pParent_transform;
        //
        public static PF_bTransformAudioQuit pf_bTransformAudioQuit;       //  bTransformQuit(  void  *  pTransform,  0,  0  )

        public qyDefs.PF_commonHandler pf_transformGetAh;         //  (  void  *  pQY_TRANSFORM, qyCommProc_open.QY_AUDIO_HEADER  pAh, void *  p2  )
        //

        //
        public static PF_getNextAPkt pf_getNextAPkt;                //   int  getNextVPkt(  myDRAW_VIDEO_DATA  *  pCurPkt,  GENERIC_Q  *	pInDataCacheQ,  QY_Q2  *	pVideoQ2  )

        //
        public static PF_parseDecAPkt_o pf_parseDecAPkt_o;

        //
        public static PF_detachPkt pf_detachPkt;              //  pPkt,  0

        public static PF_clean_myPLAY_AUDIO_DATA pf_clean_myPLAY_AUDIO_DATA;
        //				 
        public int iDecParam_index_pMems_from;                         //  2014/09/07

        //
        //
        public static class AUDIO_COMPRESSOR_CFG_dec {
            //
            public static PF_smplBitstreamReader_ReadNextFrame_func pf_smplBitstreamReader_ReadNextFrame_func;   //  2014/07/9
            //
            //  2015/01/17
            public qyDefs.PF_commonHandler pf_smplPCMWriter_WriteNextFrame_pre;       //  void  *  pQdcObjInfoParam,  void  *  pQDC_OBJ_transCommon,  void  *  pYUVWriter_var
            public static PF_smplPCMWriter_WriteNextFrame pf_smplPCMWriter_WriteNextFrame0;
            public qyDefs.PF_commonHandler pf_smplPCMWriter_WriteNextFrame1;          //  void  *  pQdcObjInfoParam,  void  *  pQDC_OBJ_transCommon,  void  *  pYUVWriter_var

            //
            public static PF_smplPCMWriter_WriteNextFrame pf_smplPCMWriter_WriteNextFrame_all;
            //
            public qyDefs.PF_commonHandler pf_postAPktToRender;                       //  void  *  pQY_TRANSFORM,  void  *  pmyPLAY_PLAY_DATA,  BOOL  *  unused_pbPktRedirected
        }

        public AUDIO_COMPRESSOR_CFG_dec dec;

    }

    public static class VIDEO_input_info {
        public long tStartTran; // 控制帧速用的，计算开始时间
        public int usFramesThisSecond; // 这一秒钟多少帧
        public int usFps = 30; // 平均帧速

        public byte[] toByteArray() {
            int totalLength = Long.BYTES + 2 * Integer.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(totalLength);

            buffer.putLong(tStartTran);
            buffer.putInt(usFramesThisSecond);
            buffer.putInt(usFps);

            return buffer.array();
        }
    }


    public static class VIDEO_COMPRESSOR_cfgCommon_head {
        public byte ucCompressors; // 压缩方式
        public int iFourcc; // 编码 264 265
        public byte ucbNeedKeyFrameFirst; // 需要第一帧是关键帧
        public byte ucSeconds_perFrame; // 帧与帧之间隔几秒
        public short usMaxFps_toShareBmp; // 最大帧速
        public short usMinFps_toShareBmp; // 最小帧速
        public int iWidth_pic; // 视频的宽
        public int iHeight_pic; // 视频的高
        public int profile_idc; // 264,265编码的一个参数
        public int uiCapType; // 采集类型, 采集摄像头、屏幕等等
        public int uiSubCapType; // 采集子类型，媒体文件（无缩放或缩放）
        public int iCapUsage; // 采集子类型，同上
        public int iTargetUsage; // 264、265编码的一个参数
        public int iAvgBitrate; // 平均码流
        public int iPeekBitrate; // 码流峰值
        public byte ucHardwareAccl; // 是否硬件压缩
        public int tmp_max_dec_fps; // 临时的解码帧速
        public VIDEO_input_info inputInfo = new VIDEO_input_info(); // 控制帧速的一些信息
        public int usPktResType_suggested; // 视频帧的资源类型，例如这一帧是三维的纹理或者是内存里的数据包

        public byte[] toByteArray() {
            int totalLength = 11 * Integer.BYTES + 4 + Short.BYTES * 2 * Byte.BYTES;
            if (inputInfo != null) {
                totalLength += inputInfo.toByteArray().length;
            }
            ByteBuffer buffer = ByteBuffer.allocate(totalLength);

            buffer.put(ucCompressors);
            buffer.putInt(iFourcc);
            buffer.put(ucbNeedKeyFrameFirst);
            buffer.put(ucSeconds_perFrame);
            buffer.putInt(usMaxFps_toShareBmp);
            buffer.putInt(usMinFps_toShareBmp);
            buffer.putInt(iWidth_pic);
            buffer.putInt(iHeight_pic);
            buffer.putInt(profile_idc);
            buffer.putInt(uiCapType);
            buffer.putInt(uiSubCapType);
            buffer.putInt(iCapUsage);
            buffer.putInt(iTargetUsage);
            buffer.putInt(iAvgBitrate);
            buffer.putInt(iPeekBitrate);
            buffer.put(ucHardwareAccl);
            buffer.putInt(tmp_max_dec_fps);
            if (inputInfo != null) {
                buffer.put(inputInfo.toByteArray());
            }
            buffer.putInt(usPktResType_suggested);

            return buffer.array();
        }
    }


    public static class VIDEO_COMPRESSOR_cfgCommon {

        //
        public VIDEO_COMPRESSOR_cfgCommon_head head = new VIDEO_COMPRESSOR_cfgCommon_head();

        //
        public int iTaskId;

        //  2014/03/25
        //
        public Object pCurPkt;
        public int size_pCurPkt;
        public genericQueue.GENERIC_Q pInDataCacheQ;
        public qyq2.QY_Q2 pVideoQ2;
        public genericQueue.GENERIC_Q pTmpDataQ;
        //
        public int uiTransformType_pParent;
        public Object pParent_transform;
        //
        public static PF_bTransformVideoQuit pf_bTransformVideoQuit;       //  bTransformQuit(  void  *  pTransform,  0,  0  )
        //
        public static PF_transformGetSpsPps pf_transformGetSpsPps;        //  
        public static PF_transformGetBih pf_transformGetBih;            //  (  void  *  pQY_TRANSFORM, bitmapinfoheader  pBih, void *  p2  )
        //
        public static PF_getNextVPkt pf_getNextVPkt;                //   int  getNextVPkt(  myDRAW_VIDEO_DATA  *  pCurPkt,  GENERIC_Q  *	pInDataCacheQ,  QY_Q2  *	pVideoQ2  )
        //
        public static PF_parseDecVPkt_o pf_parseDecVPkt_o;
        public static PF_detachPkt pf_detachPkt;              //  pPkt,  0
        public static PF_clean_myDRAW_VIDEO_DATA pf_clean_myDRAW_VIDEO_DATA;

        //				 
        public int iDecParam_index_pMems_from;                         //  2014/09/07

        public static class VIDEO_COMPRESSOR_CFG_dec {
            //
            public static PF_smplBitstreamReader_ReadNextFrame_func pf_smplBitstreamReader_ReadNextFrame_func;   //  2014/07/9
            //
            //  2015/01/17			
            public qyDefs.PF_commonHandler pf_smplYUVWriter_WriteNextFrame_pre;       //  void  *  pQdcObjInfoParam,  void  *  pQDC_OBJ_transCommon,  void  *  pYUVWriter_var
            public static PF_smplYUVWriter_WriteNextFrame pf_smplYUVWriter_WriteNextFrame0;
            public qyDefs.PF_commonHandler pf_smplYUVWriter_WriteNextFrame1;          //  void  *  pQdcObjInfoParam,  void  *  pQDC_OBJ_transCommon,  void  *  pYUVWriter_var
            //
            public static PF_smplYUVWriter_WriteNextFrame pf_smplYUVWriter_WriteNextFrame_all;

            //
            public static PF_updateDecVideoParam pf_updateDecVideoParam;                      //  pBih,  pTransform,  transformType
            public qyDefs.PF_commonHandler pf_postVPktToRender;                       //  void  *  pQY_TRANSFORM,  void  *  pmyDRAW_VIDEO_DATA,  BOOL  *  pbPktRedirected
        }

        public VIDEO_COMPRESSOR_CFG_dec dec;

        int tmp_max_dec_fps;
        VIDEO_input_info inputInfo;
        //  2015/10/04
        public int usPktResType_suggested;                      //  2015/10/04. 


    }

    public static class ReadNextFrameFucParams {
        public int uiPts;
        public int uiToInMs;
        public int nBytesRead;
        public long uiSampleTimeInMs;
        public qmcVideoCapture.SMPL_bsRead_param param;
    }

    public static class DecReadSampleDataParams {
        public long presentationTimeUs;
    }

    public static class dec_parseEncVPkt_i_useVideoQ2_params {
        public byte[] pInput = null;    //  m_var.inputInfo.pInput;
        public int uiInputSize = 0;  //  m_var.inputInfo.uiInputSize;
        public int uiSampleTimeInMs = 0; //  2015/01/28
        public int uiPts = 0;
        public getNextPktInfo_toDec_params_v getNextPktInfoToDecParamsV = new getNextPktInfo_toDec_params_v();
    }

    public static class getNextPktInfo_toDec_params_v {
        ByteBuffer onePkt;
        qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead head;
    }

    public static class dec_parseEncVPkt_i_useAudioQ2_params {
        public byte[] pInput = null;    //  m_var.inputInfo.pInput;
        public int uiInputSize = 0;  //  m_var.inputInfo.uiInputSize;
        public long uiSampleTimeInMs = 0; //  2015/01/28
        public int uiPts = 0;
        public getNextPktInfo_toDec_params_a getNextPktInfoToDecParamsA = new getNextPktInfo_toDec_params_a();
    }

    public static class getNextPktInfo_toDec_params_a {
        ByteBuffer onePkt;
        qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead head;
    }

    public static class findOldAvTaskActiveParams {
        public int piTaskId;
        public long piElapseInMs;
    }

    public static class ProcessOutputParams {
//        public int offsetInBytes;
        public int offset_pLeft;
        public int nLeft;
        public int nWrite;
//        public int nWritten_o;
        public int nWritten;
    }

    public static class getTalkerDescParams {
        public qmCommFunc_mis.MIS_CNT pMisCnt;
        public qyDefs.QY_MESSENGER_ID idInfo;
        public String talkerDesc;
        public int talkerDescCnt;
        public String shortName;
        public int shortNameCnt;

    }

    public static class BITMAPINFOHEADER {
        public int biSize;
        public int biWidth;
        public int biHeight;
        public short biPlanes;
        public short biBitCount;
        public int biCompression;
        public int biSizeImage;
        public int biXPelsPerMeter;
        public int biYPelsPerMeter;
        public int biClrUsed;
        public int biClrImportant;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 9 + Short.BYTES * 2); // Assuming the size of BITMAPINFOHEADER is 40 bytes
            buffer.putInt(biSize);
            buffer.putInt(biWidth);
            buffer.putInt(biHeight);
            buffer.putShort(biPlanes);
            buffer.putShort(biBitCount);
            buffer.putInt(biCompression);
            buffer.putInt(biSizeImage);
            buffer.putInt(biXPelsPerMeter);
            buffer.putInt(biYPelsPerMeter);
            buffer.putInt(biClrUsed);
            buffer.putInt(biClrImportant);
            buffer.flip();
            return buffer.array();
        }
    }

    public static class WAVEFORMATEX {
        public short wFormatTag;
        public short nChannels;
        public int nSamplesPerSec;
        public int nAvgBytesPerSec;
        public short nBlockAlign;
        public short wBitsPerSample;
        public short cbSize;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2 + Short.BYTES * 5);
            buffer.putShort(wFormatTag);
            buffer.putShort(nChannels);
            buffer.putInt(nSamplesPerSec);
            buffer.putInt(nAvgBytesPerSec);
            buffer.putShort(nBlockAlign);
            buffer.putShort(wBitsPerSample);
            buffer.putShort(cbSize);
            return buffer.array();
        }
    }

    public static class QY_VIDEO_HEADER {                       //  2009/03/29
        public BITMAPINFOHEADER bih = new BITMAPINFOHEADER();
        public ByteBuffer bPrivateData = ByteBuffer.allocateDirect(Consts.bufSize_bPrivateData);

        public byte[] toByteArray() {
            byte[] bihBytes = bih.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(bihBytes.length);
            buffer.put(bihBytes);
//            buffer.put(bPrivateData);
            return buffer.array();
        }
    }


    public static class QY_AUDIO_HEADER {                       //  2009/05/16

        public qyCommProc_open.WAVEFORMATEX wfx = new WAVEFORMATEX();
        public ByteBuffer bExtraData = ByteBuffer.allocateDirect(bufSize_bExtraData);

        public byte[] toByteArray() {
            byte[] wfxBytes = wfx.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(wfxBytes.length + bufSize_bExtraData);
            buffer.put(wfxBytes);
            buffer.put(bExtraData);
            return buffer.array();
        }
    }


    public static class AH_stream {
        public int kkunused;

        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.putInt(kkunused);
            return buffer.array();
        }
    }

    public static class VH_stream_mem {
        public int usLen;
        public ByteBuffer spsPps;

        public byte[] toByteArray() {
            return null;
        }
    }

    public static class VH_stream {
        public int dwFourcc; // 编码格式，264 265
        public int usCnt; // 有几个member
        public VH_stream_mem mem0 = new VH_stream_mem(), mem1 = new VH_stream_mem(), mem2 = new VH_stream_mem();

        public byte[] toByteArray() {
            int totalLength = 2 * Integer.BYTES + mem0.toByteArray().length + mem1.toByteArray().length + mem2.toByteArray().length;
            ByteBuffer buffer = ByteBuffer.allocate(totalLength);

            buffer.putInt(dwFourcc);
            buffer.putInt(usCnt);
            buffer.put(mem0.toByteArray());
            buffer.put(mem1.toByteArray());
            buffer.put(mem2.toByteArray());

            return buffer.array();
        }
    }


    public static class qyFuncs {
        int i = myMAKEFOURCC((byte) 0, (byte) 0, (byte) 0, (byte) 0);

    }

}

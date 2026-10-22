


package com.qycx.base;

import static com.qycx.base.qmcSharedObj.Consts.MAX_sharedObjUsrs;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class qmcSharedObj {

    public static class Consts {
        //public final static int MAX_sharedObjUsrs = 8;
        public final static int MAX_sharedObjUsrs = 1;

        public final static int MAX_taskRespInfoMems = 360;

        //
        public final static int CONST_capType_null = 0;
        public final static int CONST_capType_av = 1;
        public final static int CONST_capType_screen = 2;
        public final static int CONST_capType_mediaFile = 3;
        public final static int CONST_capType_mediaDevice = 4;         //  2011/12/28
        //
        public final static int CONST_capType_mosaic = 20;         //  2013/12/10. d3dWall的欢迎字幕，或者note等
        //
        public final static int CONST_capType_slave = 25;          //  2016/04/06

        //
        public final static int CONST_capType_qvcf = 30;           //  2015/05/25, 本地播放qvcf文件，不需要压缩，不需要resize


        //
        public final static int CONST_subCapType_null = 0;
        public final static int CONST_subCapType_webcam = 1;
        public final static int CONST_subCapType_mosaic_video = 2;         //  2014/12/01
        public final static int CONST_subCapType_mosaic_resource = 3;          //  2014/12/01
        //
        public final static int CONST_subCapType_unresizable = 10;         //  2015/03/11
        //
        public final static int CONST_subCapType_slave_video = 20;         //  2016/04/06


        //  2014/11/13
        public final static int CONST_capUsage_null = 0;
        public final static int CONST_capUsage_conf = 1;
        public final static int CONST_capUsage_3dConf = 2;         //  2014/12/15
        public final static int CONST_usage_mosaic_sys = 3;            //  2014/12/21

        //
        public final static int CONST_camCapType_null = 0;
        public final static int CONST_camCapType_vfw = 1;
        public final static int CONST_camCapType_directX = 2;
        public final static int CONST_camCapType_rtsp = 3;     //  2014/02/23
        //
        public final static int CONST_camCapType_qvcf = 4;     //  2015/05/11

        //
        public final static int CONST_camCapType_android = 20;

        //
        public final static int CONST_camCapType_slave = 50;       //  2014/09/29. 虚拟的流捕获，实际是另一个流的resize

        //
        public final static int CONST_camCapType_mosaic = 55;      //  2014/10/30

        //
        public final static int CONST_camCapType_custom = 100;     //  2013/03/18. 2014/09/29


        //
        //  2010/08/03
        public final static int CONST_recordType_null = 0;
        public final static int CONST_recordType_waveform = 1;
        public final static int CONST_recordType_dSound = 2;       //  2013/03/18
        public final static int CONST_recordType_directX = 3;      //  2013/03/18
        public final static int CONST_recordType_aec = 4;      //  2013/05/22
        //
        public final static int CONST_recordType_coreAudio = 5;        //  2017/01/28
        //
        public final static int CONST_recordType_webRtc = 7;       //  2017/06/15

        public final static int CONST_recordType_android = 8;
        //
        public final static int CONST_recordType_custom = 10;      //  2013/03/18


        //  2013/03/19
        public final static int CONST_sharedObjType_null = 0;
        public final static int CONST_sharedObjType_av = 101;
        public final static int CONST_sharedObjType_mediaFile = 102;
        public final static int CONST_sharedObjType_mediaDevice = 103;
        public final static int CONST_sharedObjType_webcam_vOnly = 104;
        public final static int CONST_sharedObjType_mosaic = 105;                  //  2014/12/01
        public final static int CONST_sharedObjType_qvcf = 106;                    //  2015/05/11
        public final static int CONST_sharedObjType_webcam_av = 107;                   //  2015/05/18
        public final static int CONST_sharedObjType_rtsp = 108;                    //  2015/10/11

        //
        public final static int CONST_sharedObjSubtype_null = 0;
        public final static int CONST_sharedObjSubtype_slave = 10;
        public final static int CONST_sharedObjSubtype_mosaic = 11;

        //
        public final static int MAX_cnt_preEnc_array = 30;

        //


    }


    //
    //
    public static class CompressVideo_encInfo {
        //  2014/04/22
        public int[] iSampleTimeInMs_preEnc_array;//[MAX_cnt_preEnc_array];
        public int iNextIndex_preEnc;
        public int cnt_preEnc;
        //
        public int iNextIndex_postEnc;
        public int iSampleTimeInMs_postEnc;
        public int cnt_postEnc;

        //  2014/08/15
        public long dwTickCnt_startToForceKeyFrame;


    }

    ;

    public static class CompressVideo_debugInfo {
        public long tStart;
        public int nStep;
    }

    ;


    public static class COMPRESS_VIDEO {

        public boolean bVideoCompressInited;                                      //

        //
        public int uiTranNo_openAvDev_org;                                        //  2010/09/04

        //
        public int left, top;                                                  //
        public int width, height;

        //
        public qnmCommProc_mis.VIDEO_COMPRESSOR_CFG compressor = new qnmCommProc_mis.VIDEO_COMPRESSOR_CFG();                                                    //  2009/03/27


        //  2012/09/15
        public int uiModuleType_toEnc;

        //  2015/10/13
        public boolean ucbUseEvt;


        //QDC_OBJ_INFO								qdcObjInfo;													//  2015/10/11	//  custom;
        public qoi_encV.QOI_encV u_qoi = new qoi_encV.QOI_encV();                                                       //  2016/04/19
        public qyCommProc_open.QY_VIDEO_HEADER vh_decompress = new qyCommProc_open.QY_VIDEO_HEADER();                                              //  2009/03/29
        public qyCommProc_open.QY_VIDEO_HEADER vh_compress = new qyCommProc_open.QY_VIDEO_HEADER();
        public qyCommProc_open.VH_stream vh_stream = new qyCommProc_open.VH_stream();                                                    //  2014/02/25

        //  2014/05/03
        public boolean ucbResizeByEncoder;

        //
        public int iCompressBufSize;
        public byte[] pCompressBuf;

        //
        public qnmCommProc_mis.TRANSFER_VIDEO_DATA pReqBuf_myVideoCapture;                                        //  myVideoCapure

        //  2014/04/02
        public qnmCommProc_mis.myDRAW_VIDEO_DATA pCurPkt;
        //
        public genericQueue.GENERIC_Q pInDataCacheQ;
        public genericQueue.GENERIC_Q pTmpDataQ;


        //
        public boolean ucbFixedRate;                                             //

        //
        public byte ucSeconds_perFrame_expected;                              //
        public int usFrames_perSecond_expected;                             //
        public long tSelectTime;                                             //
        public long dwTickCnt_selectTime;                                     //
        public int uiCnt_pkts_fromSelect;                                     //  SelectTime
        public float fFps_sending;                                             //  uiCnt_pkts_fromSelect / (  GetTickCount(  )  -  dwTickCnt_selectTime  )
        //
        public boolean tmp_bMediaChannelReady;                                        //
        public boolean tmp_ucbNotSendData;                                           //  2011/07/29, 仅用于记录

        public byte ucTimeoutInS_showFrameInfoResp;                               //  showFrameInfo
        //
        public qyCommProc_open.VIDEO_input_info input = new qyCommProc_open.VIDEO_input_info();
        //
        public long tLastTime_sendVideo;                                     //
        public int usFramesThisSecond_toSend;                                   //
        public int usFps_toSend;
        //
        public long tLastTime_post;                                              //  2011/12/07

        //
        public long tLastTime_showFrameInfo;                                 //

        //
        public long tFirstTime_showFrameInfo_withoutResp;                        //
        public long tRecvTime_resp_fromCli;

        //
        public int usFps_ok;                                                    //
        public boolean bTopFrames;                                                    //
        //
        public int usTimes_ok;
        public int usTimes_failure;                                         //
        //
        //
        //
        //	Frames_perSecond_expected usFrames_perSecond_ok
        //  usFrames_perSecond_ok.ÐèÒªÓÐusTimes_noErr
        //  usTimes_err
        //


        public CompressVideo_encInfo encInfo = new CompressVideo_encInfo();

        //  2010/12/28
//TCHAR tag_dbg[64];      //  2016/04/02
        public CompressVideo_debugInfo debugInfo = new CompressVideo_debugInfo();
        public int iVal_dbg;           //  2017/09/05

        //
        public Object pData;

        //
        public void memset0() {
            this.bVideoCompressInited = false;
        }
    }

    ;


    public static class CompressAudio_encInfo_st {
        public long last_iSampleTimeInMs_preEnc;
        public int uiTimeLen_preEnc;
        //
        public int lastEnd_iSampleTimeInMs_postEnc;
    }

    ;

    public static class CompressAudio_encInfo {
        //  2014/04/22
        //int							cnt_preEnc;
        //
        public int cnt_postEnc;

        //
        //  2016/12/15
        public CompressAudio_encInfo_st st = new CompressAudio_encInfo_st();

    }

    ;

    public static class COMPRESS_AUDIO {

        public boolean bCompressInited;

        //  int									uiTranNo_openAvDev;										//  tranNo
        public int uiTranNo_openAvDev_org;                                        //  tranNo. 2010/09/04
        public qnmCommProc_mis.AUDIO_COMPRESSOR_CFG compressor = new qnmCommProc_mis.AUDIO_COMPRESSOR_CFG();

        public int uiModuleType_toEnc;                                            //  2015/02/14

        //

        //QDC_OBJ_INFO								custom;
        public qoi_encA.QOI_encA u_qoi = new qoi_encA.QOI_encA();               //  2016/04/19

        //
        public qyCommProc_open.QY_AUDIO_HEADER ah_decompress = new qyCommProc_open.QY_AUDIO_HEADER();                                              //  2009/05/16
        public qyCommProc_open.QY_AUDIO_HEADER ah_pcm = new qyCommProc_open.QY_AUDIO_HEADER();                                                     //  2009/05/16
        public qyCommProc_open.QY_AUDIO_HEADER ah_compress = new qyCommProc_open.QY_AUDIO_HEADER();                                                //  2009/05/16
        //  2016/12/17
        public qyCommProc_open.AH_stream ah_stream = new qyCommProc_open.AH_stream();                                                    //  2016/12/16

        //
        public byte[] pPcmBuf;
        public int dwPcmBufSize;
        public byte[] pDstBuf;
        public int dwDstBufSize;

        public qnmCommProc_mis.TRANSFER_AUDIO_DATA pReqCache = new qnmCommProc_mis.TRANSFER_AUDIO_DATA();                                                 //  2008/05/25


        //  2015/02/13
        public myPlayAudioData.myPLAY_AUDIO_DATA pCurPkt = new myPlayAudioData.myPLAY_AUDIO_DATA();
        public myPlayAudioData.myPLAY_AUDIO_DATA tmpCurPkt;
        //
        public genericQueue.GENERIC_Q pInDataCacheQ = new genericQueue.GENERIC_Q();


        //
        public byte ucTimeoutInS_showFrameInfoResp;                               //  ´Ó·¢³öshowFrameInfoµ½µÃµ½respµÄ³¬Ê±


        //
        public long tSelectTime;                                             //  ÐÞ¸ÄÖ¡ËÙÊ±¼ä

        public short usCnt_pkts;                                                  //  ´ÓÉÏÒ»¸ö

        //
        public long tLastTime_sendAudio;                                     //  ·¢°üµÄÊ±¼ä
        public long tLastTime_showFrameInfo;                                 //  ÉÏÒ»´ÎµÄ·¢ËÍÖ¡ËÙÐÅÏ¢

        //
        public long tFirstTime_showFrameInfo_withoutResp;
        public long tRecvTime_resp_fromCli;

        //
//  2015/09/10
        public CompressAudio_encInfo encInfo = new CompressAudio_encInfo();


        //
        public void memset0() {
            this.bCompressInited = false;
        }


    }

    ;


    public static class CAP_procInfo_bmpCommon {
        public int uiType;
        public int iIndex_sharedObj;
        public int uiTranNo_sharedObj;
        public Object curhWnd;
        public qyCommProc_open.QY_VIDEO_HEADER vh_org = new qyCommProc_open.QY_VIDEO_HEADER();
        //
        public int mSensorOrientation;  //  此变量用于，是否需要额外的旋转处理。对于dlgTalk_av，从img得到的需要旋转，所以此变量要赋值。
        // 对dlgTalk_av2, 纹理已经旋转，不需要额外处理，所以此值不能赋值.
        //
        public int dwTickCnt_lastVideoGot;
        public Object hicOrg2Rgb;
        //public QY_VIDEO_HEADER vh_rgb;
        public qyCommProc_open.QY_VIDEO_HEADER vh_int;
        public int iOrg2IntBufSize;
        public byte[] pOrg2IntBuf;
        public boolean bOrg2IntBufInited;
        public qnmCommProc_mis.QY_qThreadProcInfo_common vppThread = new qnmCommProc_mis.QY_qThreadProcInfo_common();
        public qnmCommProc_mis.QY_qThreadProcInfo_common thread = new qnmCommProc_mis.QY_qThreadProcInfo_common();
        public qnmCommProc_mis.QY_qThreadProcInfo_common postThread = new qnmCommProc_mis.QY_qThreadProcInfo_common();
        public COMPRESS_VIDEO compressVideo = new COMPRESS_VIDEO();
        public boolean m_bFront;

        public CAP_procInfo_bmpCommon() {

        }

        public void memset0() {
            this.uiType = 0;
            this.iIndex_sharedObj = 0;
            this.uiTranNo_sharedObj = 0;
            this.curhWnd = null;
            this.vh_org = new qyCommProc_open.QY_VIDEO_HEADER();
            //
            this.mSensorOrientation = 0;
            //
            this.dwTickCnt_lastVideoGot = 0;
            this.hicOrg2Rgb = null;
            //public QY_VIDEO_HEADER vh_rgb;
            this.vh_int = new qyCommProc_open.QY_VIDEO_HEADER();
            this.iOrg2IntBufSize = 0;
            this.pOrg2IntBuf = null;
            this.bOrg2IntBufInited = false;

            this.vppThread.memset0();
            this.thread.memset0();
            this.postThread.memset0();
            this.compressVideo.memset0();

            //
            m_bFront = false;
        }

    }

    ;

    public static class CAP_procInfo_audioCommon {
        public int uiType;
        public int iIndex_sharedObj;
        public int uiTranNo_sharedObj;
        public qyCommProc_open.WAVEFORMATEX wf_org;
        public int dwTickCnt_lastAudioGot;
        public qnmCommProc_mis.QY_qThreadProcInfo_common appThread;
        public qnmCommProc_mis.QY_qThreadProcInfo_common thread;
        public COMPRESS_AUDIO compressAudio;

        public int iRecorder_audioSessionId;

        public CAP_procInfo_audioCommon() {
            appThread = new qnmCommProc_mis.QY_qThreadProcInfo_common();
            thread = new qnmCommProc_mis.QY_qThreadProcInfo_common();
            compressAudio = new COMPRESS_AUDIO();
        }

        public void memset0() {
            this.uiType = 0;
            this.iIndex_sharedObj = 0;
            this.uiTranNo_sharedObj = 0;
            this.wf_org = new qyCommProc_open.WAVEFORMATEX();
            this.dwTickCnt_lastAudioGot = 0;
            this.appThread.memset0();
            this.thread.memset0();
            this.compressAudio.memset0();

        }

    }

    ;


    public static class CAP_procInfo_video_capDev {
        public long pVideoCapDev;

        //
        public void memset0() {
            this.pVideoCapDev = 0;
        }

    }

    ;

    public static class CAP_procInfo_video_policy {
        public qyCommProc_open.BITMAPINFOHEADER bih = new qyCommProc_open.BITMAPINFOHEADER();
        //
    }

    ;


    public static class CAP_procInfo_video extends CAP_procInfo_bmpCommon {


        //
        public int uiCamCapType;

        //  2017/09/13
        //TCHAR displayName[256 + 1];
        public String displayName;

        //
        public CAP_procInfo_video_capDev capDev = new CAP_procInfo_video_capDev();

        //
        public CAP_procInfo_video_policy policy = new CAP_procInfo_video_policy();

        //
        public boolean bCapDevConnected;
        public boolean bCaptureStarted;
        public long dwTickCnt_captureStarted;     //  2017/09/19

        //  2017/09/14
        public boolean bCaptureStarted_openLocalVideo;


    }

    ;

    public static class CAP_procInfo_recordSound_cache {

        int bufSize_pAudioDataBuf; //  =  pProcInfo->cfg.uiBufSize_myPlayAudioData;
        byte[] pAudioDataBuf;            //   if  (  !(  pAudioDataBuf  =  (  char  *  )mallocObj_pAudioDataBuf.mallocf(  bufSize_pAudioDataBuf  )  )  )  goto  errLabel;

        //
        int nBytes_toRecord;    //  =  pRecordSoundProcInfo->compressAudio.compressor.uiBytesRecorded_perBlockAlign  *  pRecordSoundProcInfo->compressAudio.ah_decompress.wfx.nBlockAlign;
        int nAvgBytesPerSec;    //  =  pRecordSoundProcInfo->compressAudio.ah_decompress.wfx.nAvgBytesPerSec;

        //
        int dataLen_pAudioDataBuf;

        //
        Object pMsgBuf;

    }

    ;

    //
    public static class CapProcInfoRecordSound_u_android {
        public audioRecorder_android.AudioCapDev_android pAudioCapDev = new audioRecorder_android.AudioCapDev_android();
    }

    ;


    public static class CAP_procInfo_recordSound extends qmcSharedObj.CAP_procInfo_audioCommon {


        public int uiRecordType;                                             //  2010/08/03

        //  2013/03/18

        public CapProcInfoRecordSound_u_android u_android = new CapProcInfoRecordSound_u_android();

//  2011/10/24
//GUID guid_aDevice;                                              //  被选中的audio device
//char capDevDesc[64];                                               //  2013/05/25
//char aec_rendDevDesc[64];

        //  2013/08/02. 录制的线程是这里, 将audioMems.thread留给压缩
        public qnmCommProc_mis.QY_qThreadProcInfo_common recThread;

        //  2009/02/13
        //Object hEvents[CONST_numCaptureEvents + 1];

        //
        public boolean bLocalAudioPlaying;                                            //

        //
        public long dwTickCnt_sendLocalAv;                                        //  2010/05/04

        //
        public int usErrors_processInput;                                       //  2009/05/09
        //
        public boolean bInputErr_aec;                                             //  2013/05/26. 如果aec没有本地player在运行,就会出错
        public int dwStartTickCnt_inputErr;                                  //  2015/02/06. 如果inputErr持久超过5秒，就要退出线程。用户可以在申请发言时，重新初始化aec
        //
        public int dwLastTickCnt_toStartLocalAudioPlayer;                        //  2013/05/26

        //
        public int usTimes_ok;                                                  //
        public int usTimes_failure;                                         //

        //  2013/03/18
        public boolean bCapDevConnected;
        public boolean bCaptureStarted;

        //
        public CAP_procInfo_recordSound_cache cache;

        //
        public CAP_procInfo_recordSound() {
            this.recThread = new qnmCommProc_mis.QY_qThreadProcInfo_common();
        }

    }

    ;


    public static class ROUTE_sendLocalAv_routeInfo {                                                            //  2009/09/11
        public long tModifiedTime;
        public long tRecvTime_lastResp;
    }

    ;

    //
    public static class ROUTE_sendLocalAv {

        public int iIndex_misCnt;                                  //
        public qnmCommProc_mis.MSG_ROUTE route = new qnmCommProc_mis.MSG_ROUTE();
        public qyDefs.QY_MESSENGER_ID videoConference_idInfo_to = new qyDefs.QY_MESSENGER_ID();                      //  2007/11/30,

        //  2017/09/17
        public ROUTE_sendLocalAv_routeInfo routeInfo = new ROUTE_sendLocalAv_routeInfo();

    }

    ;                                     //


    //
    public static class TASK_respInfo_mem {

        //
        qyDefs.QY_MESSENGER_ID idInfo;


    }

    ;

//  comments
//  procTaskVideoDataResp计算出iState, 如果与istate_byDlgTalk不符，那么设置taskRespInfoMem里的bNeedCalc
//  然后再设置taskRespInfo里的bNeedCalc
//  dlgTalk.timer, 发现taskRespInfo里的bNeedCalc,就去遍历所有的mems
//  发现taskRespInfoMem里的bNeedcalc, 就去计算iState. 然后清除mem.bNeedcalc.再清除respInfo.bNeedCalc
//  dlgTalk在某个时间，会遍历计算所有的mems,因为在客户端离线后，就不会自动计算了。

//


    //  2017/09/06
    public static class TASK_respInfo {
        int usCnt;

        TASK_respInfo_mem[] mems;    //[MAX_taskRespInfoMems];                                   //  2009/09/11. mems_to[3];

        //
        boolean bNeedCalc_byProcTaskDataResp;

        public void memset0() {
            this.mems = null;
            this.usCnt = 0;
            this.bNeedCalc_byProcTaskDataResp = false;
        }

    }

    ;


    public static class SHARED_OBJ_USR {
        public Object hWnd_starter;                                              //  2009/08/21
        public int iWndContentType;

        //
        public int uiTaskType;                                                    //  2009/12/23
        public int iTaskId;                                                    //  2009/08/23
        //
        public qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
        public boolean ucbVideoConferenceStarter;
        public boolean ucbVideoConference;                                           //  2018/11/10

        //
        public int uiTranNo_openAvDev_a;                                      //  2010/09/04
        public int uiTranNo_openAvDev_v;                                      //  2010/09/04

        //
        //public boolean ucbNotSendData;                                               //  2009/02/22

        //
        //public int iIndex_activeMems_from_videoConferenceStarter;              //  2009/05/30

        //  2015/05/23
        public boolean bIn3DConf;                                                 //  2015/05/23. 在doCmd_startAvCall的sharedObjUsr(非mosaic)里设置。在检测到这个标志位后，doEncode要忽略，因为不需要。压缩工作都转移到了mosaic里去了

        //
        public boolean ucbInConfMosaic;                                          //  2014/11/16

        //
        public long dwLastTickCnt_applyForPlayer;                             //  2014/12/10

        //  2017/09/06
        public TASK_respInfo taskRespInfo;

        //
        public qyMcMainDaemon_isCli_mgr.TMP_tHintBuf tDbgHint;
        //TCHAR tDbgHint[64];                                             //  2016/03/28

        public void memset0() {
            this.hWnd_starter = null;                                              //  2009/08/21
            this.uiTaskType = 0;                                                    //  2009/12/23
            this.iTaskId = 0;                                                    //  2009/08/23
            //
            this.idInfo_to.ui64Id = 0;
            this.ucbVideoConferenceStarter = false;
            this.ucbVideoConference = false;                                           //  2018/11/10

            //
            this.uiTranNo_openAvDev_a = 0;                                      //  2010/09/04
            this.uiTranNo_openAvDev_v = 0;                                      //  2010/09/04

            //
            //this.ucbNotSendData=false;                                               //  2009/02/22

            //
            //this.iIndex_activeMems_from_videoConferenceStarter=0;              //  2009/05/30

            //  2015/05/23
            this.bIn3DConf = false;                                                 //  2015/05/23. 在doCmd_startAvCall的sharedObjUsr(非mosaic)里设置。在检测到这个标志位后，doEncode要忽略，因为不需要。压缩工作都转移到了mosaic里去了

            //
            this.ucbInConfMosaic = false;                                          //  2014/11/16

            //
            this.dwLastTickCnt_applyForPlayer = 0;                             //  2014/12/10

            //  2017/09/06
            //public TASK_respInfo taskRespInfo;

        }

    }

    ;


    //
    public static class SHARED_obj_key {
        public int iIndex_sharedObj;
        public int uiTranNo;
    }

    ;


    public static class SharedObj_syncAv {
        public int iSampleTimeInMs_org_start;
        public int iSampleTimeInMs_start;
    }

    ;

    public static class SharedObj_status {
        public boolean ucbFpsUnmatched;                                           //  2012/01/06,fps unmatched
    }

    ;


    public static class SharedObj_master {
        //int								uiObjType_slave;
        public qmcShareDynBmp.SHARE_dyn_bmp pShareDynBmp_slave = new qmcShareDynBmp.SHARE_dyn_bmp();
        //
        public SHARED_obj_key slaveKey = new SHARED_obj_key();
    }

    ;

    public static class SharedObj_slave {
        public SHARED_obj_key masterKey = new SHARED_obj_key();
    }

    ;


    public static class QY_SHARED_OBJ {
        //
        public boolean bUsed;                                                     //

        //
        public static class QY_SHARED_OBJ_var {
            public int uiType;                                                        //  2013/03/19

            public int index_sharedObj;
            public int uiTranNo;

            //
            //Object pProcInfo;                                                    //  2015/10/15

            //
            public boolean ucbNotSendData;                                               //  2009/02/22

            //
            public long dwTickCnt_start;                                          //  2011/08/01

            //
            public int usSubtype;                                                   //  2014/10/30
            public boolean bSlave;


            public SharedObj_master master = new SharedObj_master();
            public SharedObj_slave slave = new SharedObj_slave();


            //  2010/09/01
            public int iIndex_curUsr;

            //
            public boolean bCapDev;                                                   //  
            public boolean bDirectX;
            public boolean bRemoteAssist;                                             //  2008/11/10, 
            public boolean bSoundRecorder;

            //
            public Object pShareMediaObj;                                               //  2009/05/07

            //
            public Object pTransInfo_stream;                                            //  2014/03/15. 对rtsp这样的流，需要转换成无压缩视频。pTransInfo_stream是用来解码的结构

            //
            public Object pEvt;                                                     //  2015/10/15. evt有shm等提供数据源

            //
            public int iIndex_capBmp;
            public int iIndex_capAudio;                                            //  2009/04/23
            //
            public int iIndex_shmCmd;                                              //  2009/12/22

            //
            public int iDiffInMs_tStartAudio;                                      //  2009/05/26. used to sync video and audio

            //
            public int iIndex_localPlayer;                                         //  2009/05/02

            public Object hThread;                                                 //  
            public boolean bThreadEnd;

            public boolean bQuit;

            //  2008/10/16
            //public ROUTE_sendLocalAv curRoute_sendLocalAv;                                     //  2014/10/29

            //

            //  2012/11/11
            public SharedObj_syncAv syncAv;

            //  2012/01/06
            public SharedObj_status status;

            //
            public int m_o_texId;
            public int m_o_iW;
            public int m_o_iH;


            // for enc
            public Semaphore hMutex_syncEncFrame;
            public EventWaitHandle hEvt_notifyFrameAvail;
            public boolean bFrameAvail;

            //
        }

        //
        public QY_SHARED_OBJ_var var;

        //
        public qmcSharedObj.SHARED_OBJ_USR[] usrs;//[MAX_sharedObjUsrs];


        //
        public void memset0() {
            this.bUsed = false;                                                     //

            this.var = new QY_SHARED_OBJ_var();
            int i;
            for (i = 0; i < this.usrs.length; i++) {
                this.usrs[i].memset0();
            }

            return;
        }

    }

    ;


    //
    public static class QY_sharedObj_sync {
        public qmcSharedObj.QY_SHARED_OBJ sharedObj;
        //
        public syncMtCntDefs.SYNC_mt_cnt syncMtCnt;

        //
        public QY_sharedObj_sync() {
            this.sharedObj = new qmcSharedObj.QY_SHARED_OBJ();
            this.sharedObj.usrs = new qmcSharedObj.SHARED_OBJ_USR[MAX_sharedObjUsrs];//[MAX_sharedObjUsrs];
            for (int i = 0; i < sharedObj.usrs.length; i++) {
                sharedObj.usrs[i] = new qmcSharedObj.SHARED_OBJ_USR();
            }

            this.syncMtCnt = new syncMtCntDefs.SYNC_mt_cnt();
        }
    }

    ;


    public static class qyFuncs {
        public static boolean isSharedObjUsrIndexValid(int index) {
            return ((index) >= 0 && (index) < MAX_sharedObjUsrs);
        }

        public static QY_sharedObj_sync getSharedObjSyncByIndex(ctxQmc.CCtxQmc pProcInfo, int index) {
            if (null == pProcInfo) return null;
            return pProcInfo.getSharedObjSyncByIndex(index);
        }

        public static qmcSharedObj.QY_SHARED_OBJ getSharedObjByIndex(ctxQmc.CCtxQmc pProcInfo, int index) {
            qmcSharedObj.QY_sharedObj_sync pSharedObjInfo = getSharedObjSyncByIndex(pProcInfo, index);
            if (null == pSharedObjInfo) return null;
            //
            return pSharedObjInfo.sharedObj;
        }


        public static qmcSharedObj.CAP_procInfo_audioCommon getCapAudioBySth(ctxQmc.CCtxQmc pProcInfo, int index, int uiCapType) {
            boolean bRet = false;

            if (null == pProcInfo) return null;

            //
            return pProcInfo.getCapAudioBySth(index, uiCapType);
        }

        //  if  uiCapType is 0, don't need to check capType. and if uiCapType is not 0, ...
        public static CAP_procInfo_bmpCommon getCapBmpBySth(ctxQmc.CCtxQmc pProcInfo, int index, int uiCapType) {

            boolean bRet = false;

            if (null == pProcInfo) return null;

            //
            return pProcInfo.getCapBmpBySth(index, uiCapType);
        }


    }


}




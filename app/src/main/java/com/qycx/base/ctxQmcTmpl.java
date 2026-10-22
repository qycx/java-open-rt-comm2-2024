
package com.qycx.base;

public class ctxQmcTmpl {

    public static class Consts {
        public final static String CONST_fn_dump_h264_enc = "dump_h264_enc.264";
        public final static String CONST_fn_dump_pcm_enc = "dump_pcm_enc.pcm";

        //
        public final static String CONST_fn_dump_h264_dec = "dump_h264_dec.264";

        //
        public final static String CONST_fn_dump_mp3_enc = "dump_mp3_enc.mp3";
        public final static String CONST_fn_dump_mp3_dec = "dump_mp3_dec.mp3";


    }

    public static class QMC_threads_status {
        public boolean ulbMgrQuit;                                             //
        public boolean ulbDisplayQuit;                                         //  display
        public boolean ulbWorkQuit;                                                //  work
        public boolean ulbSchedulerQuit;                                           //  scheduler
        public boolean ulbRobotQuit;                                               //  svr
        public boolean ulbMediaQuit;                                               //  media
        public boolean ulbRealTimeMediaQuit;                                       //  realTimeMedia
        public boolean ulbRtOpQuit;                                                //  2016/12/26
        public boolean ulbLogQuit;                                             //  log

    }

    public static class QMC_status_retrieveImObjList {
        public boolean ulbImGrpExists;                                            //  是否下载了分组信息。2011/04/10.
        public boolean ulbContactExists;                                           //  是否下载了contact信息
        //
        public boolean ulbAllContactsRetrieved;                                    //  是否用户信息都下载了. 2013/08/01
        //
        public int nTimes_refresh;                                              //  因为access写入较慢,所以,在所有用户信息得到后,需要自动的刷新几次. 比如3次
        public int dwTickCnt_lastRefreshed;
    }

    ;


    public static class QMC_status_progress {
        public Object hWnd;
        public int uiTranNo;
        //
        public boolean ucbProgessEnd;
        public int nResult;
        //
    }

    ;


    public static class QmcStatus_ptsStat {
        public qnmCommProc_mis.TRANS_pts_info transPtsInfo_sum;
        public qnmCommProc_mis.TRANS_pts_info transPtsInfo_sum_last;
        //
        public boolean bTooManyPtsErr;
        public byte ucCnt_bTooManyPtsErr;
        public int dwTickCnt_lastModified_bTooManyPtsErr;
        //
        public boolean bWarning;

    }

    ;

    public static class QMC_status_cpu {                                                            //  for cpu usage. 2009/10/10

        public int iCpuUsage;
    }

    ;


    //
    public static class RENDER_status {
        public float fps;
        public int dwTickCnt_calcFps;
    }

    ;


    public static class QMC_status {
        public boolean ulbThreadProcInited;                                        //  mcThreadProc_mis 2007/09/02

        public QMC_threads_status threadsStatus = new QMC_threads_status();                                                                                    //

        //
        public QMC_status_retrieveImObjList retrieveImObjList = new QMC_status_retrieveImObjList();

        //
        public long dwTickCnt_toStat;                                         //  2009/10/11


        //
        public QMC_status_cpu cpu = new QMC_status_cpu();

        public qyCommProc.QMC_net_stat netStat = new qyCommProc.QMC_net_stat();                                                    //  2009/10/10
        //MEMORYSTATUS memory;                                                        //  201007/28
        public RENDER_status render;

        public QMC_status_progress progress = new QMC_status_progress();
        public boolean bInSequence_showDlgProgress;           //  2014/05/07


        //  2012/03/25
        public int nThreads_dec;
        public int nThreads_dispatch;                     //  2013/08/26
        public int nThreads_player;
        public int nThreads_aTrans;                       //  2014/03/15
        public int nThreads_vTrans;                       //  2014/03/15
        public int nThreads_talkerProc;                   //  2016/03/15
        public int nThreads_shareDynBmps;                 //  2016/06/15

        //  2013/03/07
        public int dwTickCnt_chkDb;


        //  2017/10/03
        public long dwTickCnt_sendTo_applyForOrgReq;      //  2017/10/03

        //  2015/06/18
        public QmcStatus_ptsStat ptsStat;


//  2018/10/30
//char vName_1st_webcam[256];	

    }

    ;

    //
    public static class QMC_cfg_debugStatusInfo {                                                           //  2009/02/27
        public boolean ucbViewAudioStatus;
        public boolean ucbViewVideoStatus;                                           //  2009/04/16
        public boolean ucbViewIoStatus;                                          //  2009/04/16
        //
        public boolean ucbViewMgrStatus;                                         //  2017/10/11
        //
        public boolean ucbPrint_a_in;                                                //  2010/06/25
        public boolean ucbPrint_a_out;
        public boolean ucbPrint_v_in;
        public boolean ucbPrint_v_out;
        //
        public boolean ucbPrint_v_draw;                                          //  2010/09/12
        //
        public boolean ucbPrint_a_mt;                                                //  2013/03/21
        //
        public boolean ucbShowVideoCapStatus;                                        //  2012/11/11
        public boolean ucbShowAudioCapStatus;                                        //  2012/11/11
        //
        public boolean ucbShowPreDrawStatus;                                     //  2012/01/26
        public boolean ucbShowToDrawStatus;                                      //  2012/01/26
        //
        public boolean ucbShowRenderStatus;                                      //  2015/10/07
        //
        public boolean ucbShowPlayAudioStatus;                                       //  2012/11/11

        //
        public boolean ucb_chkInputPts;

        //																			//
        public boolean ucbShowPktInputInfo;                                      //  2012/03/16
        public boolean ucbShowEncVStatus;                                            //  2014/06/23
        //
        public boolean ucbShowDec_pipeStatus;                                        //  2016/04/13
        //
        public boolean ucbShowPreDecV_pts;                                           //  2015/06/18
        public boolean ucbShowPreDecAStatus;                                     //  2015/09/11
        public boolean ucbShowPreDecVStatus;                                     //  2014/06/23
        public boolean ucbShowPostDecAStatus;                                        //  2015/09/11
        public boolean ucbShowPostDecVStatus;                                        //  2014/06/23
        //
        public boolean ucbShowDec_sharedTexStatus;                                   //  2016/04/08
        //
        public boolean ucbPrint_rtt;                                             //  2014/11/26
        //
        public boolean ucbPrint_d3d;                                             //  2014/12/09
        //
        public boolean ucbShowDlgTalkStatus;                                     //  2012/05/25

        //
        public boolean ucbShow_onvif;                                                //  2016/07/31
        public boolean ucbPrint_ptz;                                             //  2015/09/19

        //
        public boolean ucbShowRtspCliControl;                                        //  2014/03/23

        //
        public boolean ucbPrint_rtsp;                                                //  2014/09/29

        //
        public boolean ucbDebugSubtitle;                                         //  2014/11/07
        public boolean ucbDebugIa;                                                   //  2014/11/30
        //
        public boolean ucbDebug_newMsgArrives;                                       //  2014/12/03
        public boolean ucbDebug_fillData_d3d;                                        //  2014/12/03

        //
        public boolean ucbViewFileStatus;


        //
        //#define		CONST_fn_dump_h264_enc		"dump_h264_enc.264"
        //#define CONST_fn_dump_h264_enc		"c:\\tttbbb\\test\\dump_h264_enc.264"
        public boolean ucb__Dump_h264_enc__;                                     //  2014/03/30

        //
//#define CONST_fn_dump_h264_dec	"dump_h264_dec.264"
        public boolean ucb__Dump_h264_dec__;                                     //  2014/03/30

        //#define CONST_fn_dump_pcm		"c:\\tttbbb\\test\\dump_pcm.pcm"
        public boolean ucb__Dump_pcm__;

        public boolean ucb__Dump_mp3_enc__ = false;
        public boolean ucb__Dump_mp3_dec__ = false;

        //
        public boolean ucb__Dump_deced_pcm__;

        //
        public boolean ucb_draw_talkers_frm;                                     //  2016/06/02
        public boolean ucb_draw_walls_frm;

        //
        public boolean ucb_notPrint_my_inStream;

        //
        public boolean bPrint_encObj_mediaCodec;

        //
        public boolean bNoRemove_err_missing;

        //
        public boolean b_print_test  =  false;

        //
        public boolean b_dbg_0920  =  true;

    }

    ;

    //
    public abstract static class CCtxQmcTmpl extends ctxQyTmpl.CCtxQyTmpl {
        public ctxQyMc.CCtxQyMc pQyMc;
        //Object pSgiParam;

        public CCtxQmcTmpl() {

        }

        public abstract int setQmDbFuncs(int iDbType, myDb.QM_dbFuncs pDbFuncs);

    }


}

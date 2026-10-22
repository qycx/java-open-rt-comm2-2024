
package com.qycx.base;


public class qmcCommFunc_is_open {
    public static class PLAY_AUDIO_procInfo_var {
        public int uiSampleTimeInMs;                             //  2009/05/03
        public int dwTickCnt_uiSampleTimeInMs_internal;           //  2012/02/24. audioPlayer????
        public long dwTickCnt_uiSampleTimeInMs_syncAv;             //  2012/02/18. ?video???

        //
        public long dwTickCnt_uiSampleTimeInMs_syncRecv;           //  2012/02/24. ?newRecv????, ????????

        //
        public long uiSampleTimeInMs_newRecvd;                     //  2009/05/03
        public long dwTickCnt_uiSampleTimeInMs_newRecvd;

        //  
        public int nTimes_noAudioData;

        public boolean bVideoReady;

        //  2012/02/19
        public int uiTimeInMs_audio_remains;                          //  ???????????
        public long dwTickCnt_GetCurrentPosition;                     //  ??GetCurrentPosition???

        //  2015/07/29
        public int dwTickCnt_beforePlay;


    }

    ;

    //  2012/02/24
    public static class PLAY_AUDIO_procInfo extends qnmCommProc_mis.QY_qThreadProcInfo_common {
        //
        public QY_qBuf2 qBuf2 = new QY_qBuf2();
        public int uiTranNo=0;                                     //  2009/04/27
        public qyCommProc_open.QY_AUDIO_HEADER ah;                                             //  2010/06/17

        public qnmCommProc_is_open.AUDIO_PLAY_CFG playCfg;                                     //  2009/06/03

        public int uiPlayType;
        public long dwLastTickCnt_startPlayer;
	
        public PLAY_AUDIO_procInfo_var m_var = new PLAY_AUDIO_procInfo_var();

        //				
        public boolean bNeedStop_webRtc_my_inStream;                      //  2018/06/16

        //
        public int nTimes_noAudioData_webRtc;

        public static class PLAY_AUDIO_procInfo_cache {
            //
            public int uiBufSize;
            public byte[] pBuf;
            //
//            public byte[] pLeft;    //					=	NULL;									//  2009/04/27
            public int indexStart;
            public int nLeft; //						=	0;

            //
            public long dwTickCnt_lastAudioData;

        };
        public PLAY_AUDIO_procInfo_cache cache = new PLAY_AUDIO_procInfo_cache();

        //  2017/06/22				 
        public static class PLAY_AUDIO_procInfo_lowLatency {
            public int mql;
            public int xql;
            //
            public int last_mql;
            public int last_xql;
            //
            public int last_dangerousMql;
            public int dwTickCnt_last_dangerousMql;

            //
            public long dwTgt_start1;

            //
            public boolean bNeedAdjust;
            public long dwTickCnt_last_adjust;

            //  2017/10/13
            public static class PLAY_AUDIO_procInfo_ptsInfo {
                public int uiPts_lastRecvd;
                public int nErr_ptsMissed;
            }

            ;
            public PLAY_AUDIO_procInfo_ptsInfo ptsInfo;

            //
            public static class PLAY_AUDIO_procInfo_avg_pkt {
                public int nBytes_ptks;
                public int n_pkts;
                //
                public int nAvg_pkt_inMs;
            }

            ;
            public PLAY_AUDIO_procInfo_avg_pkt avg_pkt = new PLAY_AUDIO_procInfo_avg_pkt();
            //
            public int xql_ok;
            public int min_mql_ok;
            public int max_mql_ok;

            //
            public static class PLAY_AUDIO_procInfo_lowLatency_stat {
                public int xql;
                public long dwTickCnt_xql;
                public int mql;
                public long dwTickCnt_mql;
                //
                public long dwTickCnt_start;
                //
                public int iIntervalInMs_xql_mql;

            }

            ;
            public PLAY_AUDIO_procInfo_lowLatency_stat stat = new PLAY_AUDIO_procInfo_lowLatency_stat();
        };
        public PLAY_AUDIO_procInfo_lowLatency lowLatency = new PLAY_AUDIO_procInfo_lowLatency();
    }

    public static class PARAM_fillD3d {
        public int nIntervalInMs_fillData;
        public boolean bFailed_getTextureInfo_toFill;                 //  2014/11/25
        //boolean										bFailed_devCtx_stillUsed;						//  2014/11/25
        //boolean										bFailed_FinishCommandList;

    }

    public static class TMP_fps_info {
        public int iCount;                                         //  帧数.2014/07/02.用d3d的get_fps( )来得到player的fps.然后计算帧间隔，限制各时间在realInterval - 3, realInterval + 3间。fps放在player里，在playList里显示出来	
        public int curTimeInMs;
        public int lastTimeInMs;
        public float fps_real;
        //
        public int avgTimePerFrameInMs;
        //
        public int avgTimePerFrameInMs_real;                     //  2015/09/10

    }

    public static class PLAY_VIDEO_procInfo_mql {
        public long dwTgt_start1;
        public int mql;
        public int xql;

        //
        public int last_mql;                                       //  last_min_qNodes_left;
        public int last_xql;

        //
        public int mql_ok;
        public int mql_big_ok;
        public int xql_ok;

        //
        public boolean bNeedReset;
    }

    public static class PLAY_VIDEO_procInfo_calc {
        public int uiPts_start;
        public int dwTgt_start;
    }

    public static class PLAY_VIDEO_procInfo_lowLatency {

        public PLAY_VIDEO_procInfo_mql mql;

        //
        public boolean bNoAudio;                                      //  2016/04/10
        //
        public boolean bAccurate;                                     //  2016/04/11

        //
        public boolean bNeedAdjust;                                   //  2015/09/13			
        //
        public int nAheadInMs;
        public int nAheadInMs_gt_mql;                              //  2015/03/02. greater than
        public int nDelayInMs_lt_mql;                              //  less than

        public int nTimes_sleep;
        public int dwToInMs_toPostDraw;                          //  2012/02/07
        //
        public int dwToInMs_1740;

        public PLAY_VIDEO_procInfo_calc calc;
    }

    public static class PLAY_VIDEO_procInfo_fillD3d {
        public boolean bNeedFill;                                        //  2014/11/25
        //
        public PARAM_fillD3d param0, param1;
        //
        public boolean bFilled_d3dData;                               //  2013/08/08
    }

    public static class PLAY_VIDEO_procInfo_status {
        public int dwLastTgt_postToDraw;                            //  2012/01/26
        public int nInterval_real_postToDraw;
        //
        public int iSampleTimeInMs_last;
        public int uiPts_last;                                        //  2015/02/19
        public boolean bNoMsgAndWait;                                 //  2012/03/01
        public int nTimes_wait;                                    //  2015/09/11
        //
        public PLAY_VIDEO_procInfo_fillD3d fillD3d;
        //							
        public byte ucSkipped_for_d3dData;                            //  2013/09/09

        //  2015/02/25
        public TMP_fps_info fpsInfo;                                       //  2015/09/10

    }

    public static class PLAY_VIDEO_procInfo_var {
        public int uiSampleTimeInMs;                             //  2009/05/03
        public int dwTgt_uiSampleTimeInMs_internal;
        //
        public int dwTickCnt_uiSampleTimeInMs_syncRecv;

        public int uiSampleTimeInMs_newRecvd;                     //  2009/05/03
        public long dwTickCnt_uiSampleTimeInMs_newRecvd;

        //  2015/09/30
        public int usPktResType;                                    //  2015/09/30. 

        //
        //  2015/02/20
        //
        public long hWnd_display;                                  //  2009/07/25
        public boolean ucbDisplayAllFrames;                          //  2009/07/25

        //
        public String tWhere;//[64];                                       //  2015/09/13

        //
        public PLAY_VIDEO_procInfo_lowLatency lowLatency;


        //
        public PLAY_VIDEO_procInfo_status status;

    }

    public static class PLAY_VIDEO_procInfo extends qnmCommProc_mis.QY_qThreadProcInfo_common {                                               //  2009/05/02


        //
        public int uiTranNo=0;                                     //  2009/04/27

        //
        public qyCommProc_open.QY_VIDEO_HEADER vh;
        public int usFps;                                           //  2015/02/20. 这个来自于传输过来的策略。

        //
        public int uiPlayType;
        public long dwLastTickCnt_startPlayer;

        //
        public int nTimes_noData;

        //
        public PLAY_VIDEO_procInfo_var m_var;

        public qyq2.QY_Q2 displayQ2;                                        //  2009/07/25
        public boolean ucbDisplayQInited;

    }
	
    public static class QY_PLAYER {                                               //  2009/05/02
        public qyDefs.QY_MESSENGER_ID idInfo_recorder = new qyDefs.QY_MESSENGER_ID();                                //

        public int uiSampleTimeInMs_start;                            //  2009/05/27

        public qmcCommFunc_is_open.PLAY_AUDIO_procInfo audio = new PLAY_AUDIO_procInfo();
        public qmcCommFunc_is_open.PLAY_VIDEO_procInfo video = new PLAY_VIDEO_procInfo();

        public int iTaskId;                                        //  2009/09/17

        //
        public int uiTranNo_player;                               //  2016/02/18


        //  2014/12/10
        public long dwLastTickCnt_doApplyForPlayer;                       //  2014/12/10

        //
        public QY_PLAYER() {
          
        }

        //
        public qnmCommProc_mis.PLAYER_ID getPlayerId() {
            qnmCommProc_mis.PLAYER_ID playerId = new qnmCommProc_mis.PLAYER_ID();
            //qyFuncs.mymemset((byte[])playerId, 0, sizeof(PLAYER_ID));
            int i;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            if (this.uiTranNo_player == 0) return playerId;

            for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                qmcCommFunc_is_open.QY_PLAYER pPlayer = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, i);
                if (pPlayer.uiTranNo_player == this.uiTranNo_player) {
                    playerId.index_player = i;
                    playerId.uiTranNo_player = this.uiTranNo_player;
                    break;
                }
            }
            return playerId;

        }
    }
}

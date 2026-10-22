


package com.qycx.base;

import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_decA;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_decV;
import static com.qycx.base.qmcDecVideo.qyFuncs.mcThreadProc_decVideo;
import static com.qycx.base.qmcTransform.Consts.MAX_cnt_preDec_array;
import static com.qycx.base.qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA;
import static com.qycx.base.qyq2.qyFuncs.exitQyQ2;
import static com.qycx.base.qyq2.qyFuncs.initQyQ2;
import static com.qycx.base.tmpFunc.qyFuncs.myEventSet;

import android.util.Log;

public class qmcTransform {
    private static final String TAG = qmcTransform.class.getSimpleName();

    public static class Consts {
        public final static String CONST_mutexName_syncIndex_dec = ("syncInd_dec");
        public final static int MAX_cnt_preDec_array = 30;

    }

    public static class TRANSFORM_AUDIO_procInfo_decInfo_pts {
        public long dwTickCnt_start;         //  2015/01/15
        public int uiPts_first;
        //
        //
        public int uiPts_lastRecvd;           //  2015/01/15

        public void clearData() {
            dwTickCnt_start = 0;
            uiPts_first = 0;
            uiPts_lastRecvd = 0;
        }
    };

    public static class TRANSFORM_AUDIO_procInfo_decInfo_st {
        public int last_iSampleTimeInMs_preDec;
        //
        public int lastEnd_iSampleTimeInMs_postDec;

        public void clearData() {
            last_iSampleTimeInMs_preDec = 0;
            lastEnd_iSampleTimeInMs_postDec = 0;
        }
    };

    public static class TRANSFORM_AUDIO_procInfo_decInfo {

        //  2015/01/15
        public TRANSFORM_AUDIO_procInfo_decInfo_pts pts = new TRANSFORM_AUDIO_procInfo_decInfo_pts();

        //  2016/12/15
        public TRANSFORM_AUDIO_procInfo_decInfo_st st = new TRANSFORM_AUDIO_procInfo_decInfo_st();

        public void clearData() {
            pts.clearData();
            st.clearData();
        }

    };

    public static class TRANSFORM_AUDIO_procInfo_saveAudio {
        //
        public int uiSize_pSaveAudioPkt;
        public long pSaveAudioPkt;
    };

    public static class TRANSFORM_AUDIO_procInfo_status {
        public boolean ucbDecFailed;

        public void clearData() {
            this.ucbDecFailed = false;
        }
    };


    //  2009/05/12
    public static class TRANSFORM_AUDIO_procInfo extends qnmCommProc_mis.QY_qThreadProcInfo_common {
        //MACRO_qyQThreadProcInfo_common

        //
        public QY_qBuf2 qBuf2;


        //  int								uiTranNo;										//  2009/04/27
        //  unsigned  short							usIndex;										//  2009/04/27
        //

        //public 	int index_pMems_from;                               //  2009/05/28
        public int index_activeMems_from;

        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        public qnmCommProc_mis.TRAN_audio ta = new qnmCommProc_mis.TRAN_audio();                                              //  2014/11/26

        public int uiModuleType_toDec;                                //  2016/12/05

        public qoi_decA.QOI_decA u_qoi = new qoi_decA.QOI_decA();

        public myPlayAudioData.myPLAY_AUDIO_DATA pCurPkt;

        public qnmCommProc_mis.PKT_info_toDec curPktInfo_toDec = new qnmCommProc_mis.PKT_info_toDec();

        public qyCommProc_open.WAVEFORMATEX pWf_compress;
        public qyCommProc_open.WAVEFORMATEX pWf_pcm;                                      //  pcm¸ñÊ½
        public qyCommProc_open.WAVEFORMATEX pWf_decompress;                                   //  pcm¸ñÊ½

        public int dwBufSize_pcm;
        public byte[] pBuf_pcm;

        public TRANSFORM_AUDIO_procInfo_decInfo decInfo = new TRANSFORM_AUDIO_procInfo_decInfo();
        public TRANSFORM_AUDIO_procInfo_saveAudio saveAudio = new TRANSFORM_AUDIO_procInfo_saveAudio();
        public TRANSFORM_AUDIO_procInfo_status status = new TRANSFORM_AUDIO_procInfo_status();

        public boolean ucbInited;
    };


    public static class TRANSFORM_VIDEO_procInfo_saveVideo {
        //
        public int uiSize_pSaveVideoPkt;
        public Object pSaveVideoPkt;
    };


    public static class TRANSFORM_VIDEO_procInfo_status {
        public boolean ucbSpsPpsImported;
        public boolean ucbDecOk;
        public boolean ucbDecFailed;
        //
        public boolean bDumpStarted;
        //
        public boolean ucbNeedReinited;    //  2014/04/07

        public void clearData() {
            this.ucbDecFailed = false;
            this.ucbNeedReinited = false;
        }
    };


    public static class TRANSFORM_VIDEO_procInfo_decInfo_pts {
        public long dwTickCnt_start;         //  2015/01/15
        public int uiPts_first;
        //
        //
        public int uiPts_lastRecvd;           //  2015/01/15
    };

    public static class TRANSFORM_VIDEO_procInfo_decInfo_lowLatency {
        public int min_lDiff_pre_post;
        public int dwTickCnt_start;
    };

    public static class TRANSFORM_VIDEO_procInfo_decInfo_applyForKeyFrame {
        public long dwLastTickCnt_applyForKeyFrame;
    };


    public static class TRANSFORM_VIDEO_procInfo_decInfo {
        //
        public double dFrameRate_dec;             //  2014/04/22

        //
        public boolean bGot_keyFrame;

        //  2015/01/15
        public TRANSFORM_VIDEO_procInfo_decInfo_pts pts = new TRANSFORM_VIDEO_procInfo_decInfo_pts();

        //
        //#define		MAX_iDiff_index_preDec								20
        //
        public int iSampleTimeInMs_preDec_array[] = new int[MAX_cnt_preDec_array];//[];
        public int uiPts_preDec_array[] = new int[MAX_cnt_preDec_array];      //  2015/02/19
        //
        public int dwTickCnt_doPretrans;//[MAX_cnt_preDec_array];       //  2015/02/24
        //
        public int iNextIndex_preDec;
        //
        public int cnt_preDec;
        //
        public int nTimes_needKeyFrame;                //  如果有多次出现 dwLastTickCnt_postDec  +  2000ms < GetTickCount(  ). 就认为是keyFrame丢了 。需要重新申请. 2014/12/06

        //  2014/06/24
        public int lDiff_pre_post;

        //
        public int iSampleTimeInMs_postDec;
        public int cnt_postDec;
        //  2014/12/06
        public long dwLastTickCnt_postDec;

        //  2014/06/28
        public TRANSFORM_VIDEO_procInfo_decInfo_lowLatency lowLatency = new TRANSFORM_VIDEO_procInfo_decInfo_lowLatency();


        //  2014/08/28
        public TRANSFORM_VIDEO_procInfo_decInfo_applyForKeyFrame applyForKeyFrame = new TRANSFORM_VIDEO_procInfo_decInfo_applyForKeyFrame();


        public void clearData() {
            this.bGot_keyFrame = false;
            this.pts = new TRANSFORM_VIDEO_procInfo_decInfo_pts();
            this.iNextIndex_preDec = 0;
            this.cnt_preDec = 0;
            this.nTimes_needKeyFrame = 0;
            this.lDiff_pre_post = 0;
            this.iSampleTimeInMs_postDec = 0;
            this.cnt_postDec = 0;
            this.dwLastTickCnt_postDec = 0;
            this.applyForKeyFrame = new TRANSFORM_VIDEO_procInfo_decInfo_applyForKeyFrame();
        }

    };


    //
    //
    public static class TRANSFORM_VIDEO_procInfo extends qnmCommProc_mis.QY_qThreadProcInfo_common {
        //MACRO_qyQThreadProcInfo_common


        //
        //public int index_pMems_from;                               //  2009/05/28
        public int index_activeMems_from;

        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        public qnmCommProc_mis.TRAN_video tv = new qnmCommProc_mis.TRAN_video();                                              //  2014/11/26

        public long dwLastmodifiedTickCnt_keyParams;              //  2014/08/05

        public int uiModuleType_toDec;
        public boolean bUseDecTool;                                   //  2015/01/19
        public int usPktResType;                                    //  2016/04/28

        public qoi_decV.QOI_decV u_qoi = new qoi_decV.QOI_decV();

        //
        //  2014/03/24
        public qnmCommProc_mis.myDRAW_VIDEO_DATA pCurPkt;

        //
        public qnmCommProc_mis.PKT_info_toDec curPktInfo_toDec = new qnmCommProc_mis.PKT_info_toDec();


        //
        public genericQueue.GENERIC_Q pInDataCacheQ;
        public genericQueue.GENERIC_Q pTmpDataQ;

        public TRANSFORM_VIDEO_procInfo_decInfo decInfo = new TRANSFORM_VIDEO_procInfo_decInfo();

        public TRANSFORM_VIDEO_procInfo_saveVideo saveVideo = new TRANSFORM_VIDEO_procInfo_saveVideo();

        public TRANSFORM_VIDEO_procInfo_status status = new TRANSFORM_VIDEO_procInfo_status();

        public boolean ucbInited;

    };


    public static class QY_TRANSFORM {
        //public Object pProcInfo;
        public qnmCommProc_mis.MSGR_ADDR pAddr_logicalPeer;
        public int iTaskId;
        public int uiTaskType;                                        //  2012/03/25
        public Object pTranInfo_unused;                             //  AV_TRAN_INFO  *
        public Object pTask;                                            //  PROC_TASK_AV  *
        public Object pMsgTask;                                     //  2014/08/27

        //
        public qnmCommProc_mis.MIS_MSGU tmp_pMsgU;
        public int sizeof_tmp_pMsgU;

        //
        //  void								*		pVIDEO_capDev_rtsp;								//  2014/03/25
        public Object pCapDev_rtsp;                                 //  2014/03/25

        //
        //  2014/11/11
        public boolean bMosaicVideo;
        public boolean bMosaicResource;
        //  2009/05/29
        public int index_activeMems_from;

        //
        public boolean bNeed_decV;

        //
        public boolean bDecV_forOther;

        //
        //public int index_activeMems_from_decV_bak;

        //
        public TRANSFORM_AUDIO_procInfo audio;

        //
        //  qnmCommProc_mis.QY_qThreadProcInfo_common					v_preTransThread;								//  2011/12/09
        public qnmCommProc_mis.PRETRANS_procInfo v_preTransThread;                             //  2015/02/25
        //
        public qmcTransform.TRANSFORM_VIDEO_procInfo video;

        //  2014/08/04
        public qnmCommProc_mis.SAVE_av_procInfo saveThread;                                        //  2014/08/04

        //  2011/07/03
        public int iIndex_player;                                  //  2009/05/02

        //  2015/06/18
        public qnmCommProc_mis.TRANS_pts_info transPtsInfo;                                        //  2015/06/18


        //
        public QY_TRANSFORM() {
            this.audio = new TRANSFORM_AUDIO_procInfo();
            this.v_preTransThread = new qnmCommProc_mis.PRETRANS_procInfo();
            this.video = new qmcTransform.TRANSFORM_VIDEO_procInfo();
            this.saveThread = new qnmCommProc_mis.SAVE_av_procInfo();

        }

        public int get_index_activeMems_from_decV() {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            taskAv.PROC_TASK_AV myTask = (taskAv.PROC_TASK_AV) this.pTask;
            if (myTask.bVideoConference()) return this.index_activeMems_from;
            else {
                return this.index_activeMems_from;
            }
        }
    };


    //
    public static class QisTrans {
        //
        public boolean bUsed;

        //
        public int usMaxCntLimit_transforms;
        //
        public int usCntLimit_transforms;                            //  2009/05/12
        public QY_TRANSFORM[] pTransforms;                              //  2009/05/12

        //


    };

    public static class qyFuncs {
        

        //
        public static int initTransform(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MSGR_ADDR pAddr_logicalPeer, int iTaskId, int uiTaskType, qnmCommProc_mis.AV_TRAN_INFO pTranInfo_unused, taskAv.PROC_TASK_AV pTask, qnmCommProc_mis.MIS_MSG_TASK pMsgTask, int i, QY_TRANSFORM p) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

            genericQueue.GENERIC_Q_CFG tmpCfg=new genericQueue.GENERIC_Q_CFG();

            ctxQmcTmpl.CCtxQmcTmpl pCtx = pProcInfo;
            qySyncCommProc.RW_lock_param pQyMc_rwLockParam = pQyMc.cfg.rwLockParam;// pCtx->get_qyMc_rwLockParam();
            //if (!pQyMc_rwLockParam) return -1;
            ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;// (QMC_cfg)pCtx->get_qmc_cfg();
            //if (!pQmcCfg) return -1;
            genericQueue.GENERIC_Q_CFG p_cfg_transformQ = pQmcCfg.transformQ;//Ctx->get_cfg_transformQ(  );
            //if (!p_cfg_transformQ) return -1;
            String str;

            //
            if (pTask.bVideoConference()) {
                if (p.index_activeMems_from == 0) {
                    p.bNeed_decV = true;
                }
            } else {
                // let the last transform to dec video
                //if ( i==pTask.usCntLimit_transforms - 1)
                {
                    p.bNeed_decV = true;
                    //
                    p.bDecV_forOther = true;
                }
            }


            //
            //memcpy(tmpCfg, p_cfg_transformQ, sizeof(tmpCfg));
            tmpCfg = p_cfg_transformQ;
            //_sntprintf(tmpCfg.name, mycountof(tmpCfg.name), ("%s-a-%d-%d"), tmpCfg.name, iTaskId, i);
            str = String.format("%s-a-%s-%s", tmpCfg.name, iTaskId, i);
            tmpCfg.name=str;// cntof_qCfg_name);
            //_sntprintf(tmpCfg.mutexName_prefix, mycountof(tmpCfg.mutexName_prefix), ("%s-a-%d-%d"), tmpCfg.mutexName_prefix, iTaskId, i);
            str = String.format("%s-a-%s-%s", tmpCfg.mutexName_prefix, iTaskId, i);
            tmpCfg.mutexName_prefix=str;// cntof_mutexName_prefix);
            //  if  (  initGenericQ(  &tmpCfg,    0,  0,  free,  &p.audio.q  )  )  goto  errLabel;
            if (0 != initQyQ2(tmpCfg, pProcInfo.cfg.b_use2Buf_transAQ,  pQyMc_rwLockParam, 2, null,  null, null,  null, p.audio.q2))
                return iErr;
            p.audio.bQInited = true;
            //
            p.audio.qBuf2 = new QY_qBuf2();
            int maxDataBufLen = 128000 * 2 / 8;//  2 secends mp3
            if (0 != p.audio.qBuf2.initQyQ2(CONST_qType_decA, tmpCfg.uiMaxQNodes, maxDataBufLen, false,  pQyMc_rwLockParam, true, new String(tmpCfg.name)))
                return iErr;
            //
            //memcpy(tmpCfg, p_cfg_transformQ, sizeof(tmpCfg));
            tmpCfg = p_cfg_transformQ;
            //_sntprintf(tmpCfg.name, mycountof(tmpCfg.name), ("%s-v-%d-%d"), tmpCfg.name, iTaskId, i);
            str = String.format("%s-v-%s-%s", tmpCfg.name, iTaskId, i);
            tmpCfg.name=str;// cntof_q2Cfg_name);
            //_sntprintf(tmpCfg.mutexName_prefix, mycountof(tmpCfg.mutexName_prefix), ("%s-v-%d-%d"), tmpCfg.mutexName_prefix, iTaskId, i);
            str = String.format("%s-v-%s-%s", tmpCfg.mutexName_prefix, iTaskId, i);
            tmpCfg.mutexName_prefix=str;// cntof_mutexName_prefix);
            //  if  (  initGenericQ(  &tmpCfg,    0,  0,  qyMemoryFunc.qyFuncs.  &p.video.q  )  )  goto  errLabel;
            if (0 != initQyQ2(tmpCfg, pProcInfo.cfg.b_use2Buf_transVQ,  pQyMc_rwLockParam, 2, null,  null, null,  (genericQueue.PF_qElemRemove) clean_myDRAW_VIDEO_DATA, p.video.q2))
                return iErr;
            p.video.bQInited = true;

            //

            //  2011/12/09
            //  memcpy(  &tmpCfg,  &pProcInfo->cfg.preTransformQ,  sizeof(  tmpCfg  )  );
            //memcpy(tmpCfg, p_cfg_transformQ, sizeof(tmpCfg));
            tmpCfg = p_cfg_transformQ;
            //_sntprintf(tmpCfg.name, mycountof(tmpCfg.name), ("%s-pv-%d-%d"), tmpCfg.name, iTaskId, i);
            str = String.format("%s-pv-%s-%s", tmpCfg.name, iTaskId, i);
            tmpCfg.name=str;// cntof_q2Cfg_name);
            //_sntprintf(tmpCfg.mutexName_prefix, mycountof(tmpCfg.mutexName_prefix), ("%s-pv-%d-%d"), tmpCfg.mutexName_prefix, iTaskId, i);
            str = String.format("%s-pv-%s-%s", tmpCfg.mutexName_prefix, iTaskId, i);
            tmpCfg.mutexName_prefix=str;// cntof_mutexName_prefix);
            //
            if (0 != initQyQ2(tmpCfg, pProcInfo.cfg.b_use2Buf_transPVQ,  pQyMc_rwLockParam, 2, null,  null, null,  null, p.v_preTransThread.q2))
                return iErr;
            p.v_preTransThread.bQInited = true;

            //
            p.v_preTransThread.qBuf2 = new QY_qBuf2();
            maxDataBufLen = (int) (2000000 * 1.5 / 8);//  1.5 secends mp3
            //
            if (pProcInfo.cfg.b_useVideoQ2_toDecV) maxDataBufLen = 1;
            if  (  !p.bNeed_decV  )  {
                maxDataBufLen=1;
            }
            //
            if (0 != p.v_preTransThread.qBuf2.initQyQ2(CONST_qType_decV, tmpCfg.uiMaxQNodes, maxDataBufLen, false,  pQyMc_rwLockParam, true, new String(tmpCfg.name)))
                return iErr;


            //  2014/0804
            if (pTask.saveVideo.ucbSaveVideo_req) {
                //memcpy(tmpCfg, p_cfg_transformQ, sizeof(tmpCfg));
                tmpCfg = p_cfg_transformQ;
                //_sntprintf(tmpCfg.name, mycountof(tmpCfg.name), ("%s-s-%d-%d"), tmpCfg.name, iTaskId, i);
                str = String.format("%s-s-%s-%s", tmpCfg.name, iTaskId, i);
                tmpCfg.name=str;// cntof_qCfg_name);
                //_sntprintf(tmpCfg.mutexName_prefix, mycountof(tmpCfg.mutexName_prefix), ("%s-s-%d-%d"), tmpCfg.mutexName_prefix, iTaskId, i);
                str = String.format("%s-s-%s-%s", tmpCfg.mutexName_prefix, iTaskId, i);
                tmpCfg.mutexName_prefix=str;// cntof_mutexName_prefix);
                //
                if (0 != initQyQ2(tmpCfg, false,  pQyMc_rwLockParam, 2, null,  null, null,  null, p.saveThread.q2))
                    return iErr;
                p.saveThread.bQInited = true;
            }

            //
            //p.pProcInfo = pProcInfo;
            p.pAddr_logicalPeer = pAddr_logicalPeer;
            p.iTaskId = iTaskId;
            p.uiTaskType = uiTaskType; //  2012/03/25
            p.pTranInfo_unused = pTranInfo_unused;     //  2014/11/11
            p.pTask = pTask;
            p.pMsgTask = pMsgTask;     //  2014/08/27
            //
            p.index_activeMems_from = i;

            //
            if (p.tmp_pMsgU != null) {
                Log.d(TAG, "initTransform failed, tmp_pMsgU is not null");
                showInfo_open.qyFuncs.showInfo_open("initTransform failed, tmp_pMsgU is not null");
                return iErr;
            }
            p.sizeof_tmp_pMsgU=128;//sizeof(qnmCommProc_mis.MIS_MSGU);
            p.tmp_pMsgU = new qnmCommProc_mis.MIS_MSGU();// (Object) mymalloc.call(p.sizeof_tmp_pMsgU);
            if (p.tmp_pMsgU == null) return iErr;



            //
            iErr = 0;
            
            return iErr;

        }


        public static void exitTransform(taskAv.PROC_TASK_AV pTask, QY_TRANSFORM p) {
            if (p == null) return;

            //
            if (p.tmp_pMsgU != null) {
                //qyMemoryFunc.qyFuncs.myfree.call((byte[]) p.tmp_pMsgU);
                p.tmp_pMsgU = null;
            }


            //  2014/08/04
            if (p.saveThread.bQInited) {
                exitQyQ2(p.saveThread.q2);
                p.saveThread.bQInited = false;
            }

            //  2011/12/09
            if (p.v_preTransThread.bQInited) {
                exitQyQ2(p.v_preTransThread.q2);
                p.v_preTransThread.bQInited = false;
            }
            //
            if (p.v_preTransThread.qBuf2 != null) {
                p.v_preTransThread.qBuf2.exitQyQ2();
                p.v_preTransThread.qBuf2 = null;
            }

            //
            if (p.audio.qBuf2 != null) {
                p.audio.qBuf2.exitQyQ2();
                p.audio.qBuf2 = null;
            }
            if (p.audio.bQInited) {
                exitQyQ2(p.audio.q2);
                p.audio.bQInited = false;
            }
            if (p.video.bQInited) {
                exitQyQ2(p.video.q2);
                p.video.bQInited = false;
            }

            return;
        }


        //void  exitTransforms(  PROC_TASK_AV  *  pTask  );

        // int  initTransforms(  MC_VAR_isCli  *  pProcInfo,  MSGR_ADDR  *  pAddr_logicalPeer,  int  iTaskId,  int  uiTaskType,  AV_TRAN_INFO  *  pTranInfo,  PROC_TASK_AV  *  pTask  )
        public static int initTransforms(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MSGR_ADDR pAddr_logicalPeer, int iTaskId, int uiTaskType, qnmCommProc_mis.AV_TRAN_INFO pTranInfo_unused, taskAv.PROC_TASK_AV pTask, qnmCommProc_mis.MIS_MSG_TASK pMsgTask) {
            int iErr = -1;

            //

            //QY_MC	*	pQyMc	=	(  QY_MC  *  )pProcInfo->pQyMc;
            int i;
            int len;
            //  DWORD		dwThreadDaemonId;

            if (0 == pTask.trans.usCntLimit_transforms) return 0;
            if (pTask.trans.pTransforms == null) {
                Log.i(TAG, "initTransforms failed, pTransforms is not null");
                return -1;
            }
            if (pTask.trans.usCntLimit_transforms > pTask.trans.usMaxCntLimit_transforms) {
                return -1;
            }

            for (i = 0; i < pTask.trans.usCntLimit_transforms; i++) {
                QY_TRANSFORM p = pTask.trans.pTransforms[i];

                if (0 != initTransform(pProcInfo, pAddr_logicalPeer, iTaskId, uiTaskType, pTranInfo_unused, pTask, pMsgTask, i, p))
                {
                    exitTransforms(pProcInfo, pTask);
                    return iErr;
                }
            }


            //
            iErr = 0;

            
            if (0 != iErr) {
                exitTransforms(pProcInfo, pTask);
            }

            return iErr;
        }

        public static void exitTransforms(ctxQmc.CCtxQmc pProcInfo, taskAv.PROC_TASK_AV pTask) {
            int i;

            if (0 == pTask.trans.usCntLimit_transforms) return;
            if (null == pTask.trans.pTransforms) return;

            stopTransformThreads(pProcInfo, pTask, "exitTransforms");

            for (i = 0; i < pTask.trans.usCntLimit_transforms; i++) {
                QY_TRANSFORM p = pTask.trans.pTransforms[i];

                exitTransform(pTask, p);
            }

            //free(pTask.pTransforms); pTask.pTransforms = null;
            //pTask.trans.pTransforms = null;

            return;
        }


        ///////////////
        /// <summary>
        ///
        public static int startTransformThread_v(ctxQmc.CCtxQmc pProcInfo, QY_TRANSFORM p, String hint) {
            String str;

            //
            ctxQmcTmpl.QMC_status pStatus = pProcInfo.status;// Ctx.get_qmc_status();
            if (hint == null) hint = "";

            //
            if (null == p.video.hThread) {
                str = String.format("startTrans_v %s", hint);
                showInfo_open.qyFuncs.showInfo_open(0, null, str);
                p.video.bQuit = false;
                p.video.hThread = guiShare.GuiShare.pf_startThread1.call( mcThreadProc_decVideo, p);
//                emptyQ2(p.v_preTransThread.q2);
                //  2012/03/25
                //InterlockedIncrement(pStatus->nThreads_dec);
                synchronized (TAG){
                    pStatus.nThreads_dec++;
                }
            }

            return 0;
        }

        public static int stopTransformThread_v(ctxQmc.CCtxQmc pProcInfo, QY_TRANSFORM p, String hint) {
            String str;

            ctxQmcTmpl.QMC_status pStatus = pProcInfo.status;// Ctx.get_qmc_status();

            if (hint == null) hint = "";

            if (null != p.video.hThread) {
                str = String.format("stopTrans_v %s", hint);
                showInfo_open.qyFuncs.showInfo_open(0, null, str);

                        //
                p.video.bQuit = true;
                //
                tmpFunc.qyFuncs.mySetEvent(p.video.q2.hEvent0);
                //waitForThread( p.video.hThread, 20000);
                guiShare.GuiShare.pf_waitForThread1.call(p.video.hThread, 20000);
                p.video.hThread = null;
                //
                //tmp_getHint_video(pTask, p, tBuf, mycountof(tBuf));
                //
                if (null != p.video.hThread) {
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsClient"), 0, "", ("Failed"), tBuf);
                } else {
                    synchronized (TAG){
                        pStatus.nThreads_dec--;
                    }
                }

            }
            if (null != p.video.hThread) {
                p.video.bQuit = true;
                //
                tmpFunc.qyFuncs.mySetEvent(p.video.q2.hEvent0);
                //waitForThread( p.video.hThread, 20000);
                guiShare.GuiShare.pf_waitForThread1.call(p.video.hThread, 20000);
                p.video.hThread = null;
                //
                //tmp_getHint_video(pTask, p, tBuf, mycountof(tBuf));
                //
                if (null != p.video.hThread) {
                    //qyExitProcess(tBuf);
                } else {
                    synchronized (TAG){
                        pStatus.nThreads_dec--;
                    }
                }
            }

            return 0;

        }
        /// </summary>
        /// <param name="pProcInfo"></param>
        /// <param name="pTask"></param>
        /// <param name="p"></param>
        /// <returns></returns>

        //
        public static int stopTransformThread(ctxQmc.CCtxQmc pProcInfo, taskAv.PROC_TASK_AV pTask, QY_TRANSFORM p) {
            //MC_VAR_isCli  *  pProcInfo	=	QY_GET_procInfo_isCli(  );
            int i;
            //TCHAR tBuf[512];
            //  char			buf[128];

            ctxQmcTmpl.CCtxQmcTmpl pCtx = pProcInfo;
            if (null == pCtx) return -1;
            ctxQmcTmpl.QMC_status pStatus = pProcInfo.status;// Ctx.get_qmc_status();
            //if (!pStatus) return -1;
            if (p == null) return -1;


            //  2012/03/20
            //for  (  i  =  0;  i  <  pTask.usCntLimit_transforms;  i  ++  )
            {
                //QY_TRANSFORM	*	p	=	&pTask.pTransforms[i];

                //  2011/12/09
                if (null != p.v_preTransThread.hThread) {
                    p.v_preTransThread.bQuit = true;
                }
                if (null != p.audio.hThread) {
                    p.audio.bQuit = true;
                }
                if (null != p.video.hThread) {
                    p.video.bQuit = true;
                }
                if (null != p.saveThread.hThread) {   //  2014/08/04
                    p.saveThread.bQuit = true;
                }
            }


            //
            //for  (  i  =  0;  i  <  pTask.usCntLimit_transforms;  i  ++  )
            {
                //	 QY_TRANSFORM	*	p	=	&pTask.pTransforms[i];

                //  2011/12/09
                if (null != p.v_preTransThread.hThread) {
                    p.v_preTransThread.bQuit = true;
                    //
                    myEventSet(p.v_preTransThread.q2.hEvent0);
                    //waitForThread( p.v_preTransThread.hThread, 20000);
                    guiShare.GuiShare.pf_waitForThread1.call(p.v_preTransThread.hThread, 20000);
                    p.v_preTransThread.hThread = null;
            
                    if (null != p.v_preTransThread.hThread) {
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsClient"), 0, "", ("Failed"), tBuf);
                    } else{
                        synchronized (TAG){
                            pStatus.nThreads_dec--;
                        }
                    }
                }
                if (null != p.v_preTransThread.hThread) {
                    p.v_preTransThread.bQuit = true;
                    //
                    myEventSet(p.v_preTransThread.q2.hEvent0);
                    //waitForThread( p.v_preTransThread.hThread, 20000);
                    guiShare.GuiShare.pf_waitForThread1.call(p.v_preTransThread.hThread, 20000);
                    p.v_preTransThread.hThread = null;
            
                    if (null != p.v_preTransThread.hThread) {
                        //qyExitProcess(tBuf);
                    } else {
                        synchronized (TAG){
                            pStatus.nThreads_dec--;
                        }
                    }
                }

                if (null != p.audio.hThread) {
                    p.audio.bQuit = true;
                    //
                    if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
                        myEventSet(p.audio.q2.hEvent0);
                    } else {
                        myEventSet(p.audio.qBuf2.hEvent0);

                    }
                    //waitForThread( p.audio.hThread, 20000);
                    guiShare.GuiShare.pf_waitForThread1.call(p.audio.hThread, 20000);
                    p.audio.hThread = null;
             
                    if (null != p.audio.hThread) {
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsClient"), 0, "", ("Failed"), tBuf);
                    } else{
                        synchronized (TAG){
                            pStatus.nThreads_dec--;
                        }
                    }

                }
                if (null != p.audio.hThread) {       //  2009/07/09. retry to wait for audio thread.
                    p.audio.bQuit = true;
                    //
                    if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
                        tmpFunc.qyFuncs.mySetEvent(p.audio.q2.hEvent0);
                    } else {
                        tmpFunc.qyFuncs.mySetEvent(p.audio.qBuf2.hEvent0);
                    }
                    //waitForThread( p.audio.hThread, 20000);
                    guiShare.GuiShare.pf_waitForThread1.call(p.audio.hThread, 20000);
                    p.audio.hThread = null;
              
                    if (null != p.audio.hThread) {
                        //qyExitProcess(tBuf);
                    } else{
                        synchronized (TAG){
                            pStatus.nThreads_dec--;
                        }
                    }
                }

                stopTransformThread_v(pProcInfo, p, "called in stopTrans");

                //2014/08/04
                if (null != p.saveThread.hThread) {
                    p.saveThread.bQuit = true;
                    //
                    tmpFunc.qyFuncs.mySetEvent(p.saveThread.q2.hEvent0);
                    //waitForThread( p.saveThread.hThread, 20000);
                    guiShare.GuiShare.pf_waitForThread1.call(p.saveThread.hThread, 20000);
                    p.saveThread.hThread = null;
                    //
                    //tmp_getHint_video(pTask, p, tBuf, mycountof(tBuf));
                    //
                    if (null != p.saveThread.hThread) {
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsClient"), 0, "", ("Failed"), tBuf);
                    } else {
                        synchronized (TAG){
                            pStatus.nThreads_dec--;
                        }
                    }

                }
                if (null != p.saveThread.hThread) {
                    p.saveThread.bQuit = true;
                    //
                    tmpFunc.qyFuncs.mySetEvent(p.saveThread.q2.hEvent0);
                    //waitForThread( p.saveThread.hThread, 20000);
                    guiShare.GuiShare.pf_waitForThread1.call(p.saveThread.hThread, 20000);
                    p.saveThread.hThread = null;
                    //
                    //tmp_getHint_video(pTask, p, tBuf, mycountof(tBuf));
                    //
                    if (null != p.saveThread.hThread) {
                        //qyExitProcess(tBuf);
                    } else {
                        synchronized (TAG){
                            pStatus.nThreads_dec--;
                        }
                    }
                }
            }
            return 0;
        }


        //
        public static int stopTransformThreads(ctxQmc.CCtxQmc pProcInfo, taskAv.PROC_TASK_AV pTask, String hint) {
            //  MC_VAR_isCli  *  pProcInfo	=	QY_GET_procInfo_isCli(  );
            int i;
            //TCHAR tBuf[512];
            //  char			buf[128];

            ctxQmcTmpl.CCtxQmcTmpl pCtx = pProcInfo;
            if (null == pCtx) return -1;

            //
            String str;

            //
            if (hint == null) hint = "";


            str = String.format("stopTransformThreads enters, %s", hint);
            showInfo_open.qyFuncs.showInfo_open(0, null, str);

            //
            for (i = 0; i < pTask.trans.usCntLimit_transforms; i++) {
                QY_TRANSFORM p = pTask.trans.pTransforms[i];
                if (p == null) continue;

                //
                if (null != p.v_preTransThread.hThread) {
                    p.v_preTransThread.bQuit = true;
                    tmpFunc.qyFuncs.mySetEvent(p.v_preTransThread.q2.hEvent0);
                }
                if (null != p.audio.hThread) {
                    p.audio.bQuit = true;
                    tmpFunc.qyFuncs.mySetEvent(p.audio.q2.hEvent0);
                }
                if (null != p.video.hThread) {
                    p.video.bQuit = true;
                    tmpFunc.qyFuncs.mySetEvent(p.video.q2.hEvent0);
                }
                if (null != p.saveThread.hThread) {   //  2014/08/04
                    p.saveThread.bQuit = true;
                    tmpFunc.qyFuncs.mySetEvent(p.saveThread.q2.hEvent0);
                }

                //
                continue;
            }


            //  2014/11/11
            for (i = 0; i < pTask.trans.usCntLimit_transforms; i++) {
                QY_TRANSFORM p = pTask.trans.pTransforms[i];

                stopTransformThread(pProcInfo, pTask, p);
            }

            str = "stopTransformThreads leaves";
            showInfo_open.qyFuncs.showInfo_open(0, null, str);

            return 0;
        }

        //
        public static boolean isPkt_sharedTex(qmcTransform.QY_TRANSFORM pTransform) {
            boolean bRet = false;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            if (!pProcInfo.cfg.ucb_USE_Fragment_dlgTalk_av) {
                bRet = true;
            }
            return bRet;
        }


    }
}

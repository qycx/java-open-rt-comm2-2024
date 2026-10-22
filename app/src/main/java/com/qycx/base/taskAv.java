


package com.qycx.base;

import static com.qycx.base.genericQueue.qyFuncs.emptyGenericQ;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;

import com.qycx.base.util.Log;

public class taskAv {

    public static class PROC_TASK_AV_video {
        public int usMaxFps_toShareBmp_conf;                   //  2009/06/04

        public qnmCommProc_mis.MIS_MSGU pMsgU_toTransform;
    };

    public static class PROC_TASK_AV_audio {

        public qnmCommProc_is_open.AUDIO_PLAY_CFG playCfg;                                    //  2009/06/14

        //
        public qnmCommProc_mis.MIS_MSGU pMsgU_toTransform;
    };

    public static class PROC_TASK_AV_confMosaicMaker {
        //
        public boolean bExists_confMosaic;
        //
        public int iIndex_sharedObj_video;
        public int iIndex_sharedObj_resource;
    };

    public static class ProcTaskAv_applyForProxy_proxyReply_proxy {
        public int uiTranNo_openAvDev_a;
        public int uiTranNo_openAvDev_v;
    };

    public static class ProcTaskAv_applyForProxy_proxyReply {
        public ProcTaskAv_applyForProxy_proxyReply_proxy proxy;
    };

    public static class ProcTaskAv_applyForProxy {                                            //  2014/11/21. 要求proxy服务的客户端. 申请proxy的特点是有一个content_idInfo_logicalPeer
        //
        public boolean bRecvd_proxyReply;                            //  得到了响应
        //
        public ProcTaskAv_applyForProxy_proxyReply proxyReply;

    };


    public static class ProcTaskAv_photomosaic {   //video zzq

        /*
        public qyq2.QY_Q2 pOutputQ2;                                  //  2009/07/26
        //
        public QY_qBuf2 pOutputQBuf2;

         */

        //
        public void emptyQ21(){ //added by zzq ，careful
            //
            /*
            emptyGenericQ(pOutputQ2.qs0);
            emptyGenericQ(pOutputQ2.qs1);
            //
            if  (  pOutputQBuf2!=null) {
                //pOutputQBuf2.qs0
                Log.e("qy","photomosaic.emptyQ2 not finished");
            }

             */

        };
        public int getSize1(){  //added by zzq
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if  (  pQyMc  ==  null) return  -1;
            ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();

            /*
            if  (  pProcInfo.cfg.b_useQ2_toEncV) {
                return getQ2Nodes(pOutputQ2);
            }
            else  {
                  if  (  pOutputQBuf2==null)  return  -1;
                  //
                  return  pOutputQBuf2.size();
            }

             */
            return  0;
        }
    };

    public static class ProcTaskAv_mixer {   //Audio zzq


        /*
        public qyq2.QY_Q2 pOutputQ2;
        //
        public QY_qBuf2 pOutputQBuf2;

         */

        //
        public int getSize1(){ //added by zzq
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if  (  pQyMc  ==  null) return  -1;
            ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
            //
            /*
            if  (  pProcInfo.cfg.b_useQ2_toEncA) {

                return getQ2Nodes(pOutputQ2);
            }
            else  {
                   if  (  pOutputQBuf2 == null)  return  -1;
                   //
                   return  pOutputQBuf2.size();
            }

             */
            return  0;
        }

        public long tLastTime_showFrameInfo;
        public int usCnt_pkts;

    }

    public static class ProcTaskAv_saveVideo {
        public int uiObjType;                                    //  2014/08/08
        public int usIndex_obj;
        //
        public boolean ucbSaveVideo_req;
        //unsigned  char								ucbSaveVideo_reply;							//  2014/09/07
        //
        public int dwLastTickCnt_notification;
    }

    public static class ProcTaskAv_remoteAssist {
        public int maxxScreen;
        public int maxyScreen;

        //RA_POINTER_EVENT pointerEvent;	

    }


    public static class PROC_TASK_AV {                                           //


        public Object hWnd_task;                                 //  2009/09/09

        public qnmCommProc_mis.VIDEO_CONFERENCE_CFG videoConferenceCfg = new qnmCommProc_mis.VIDEO_CONFERENCE_CFG();


        //
        public int uiTaskType;                                    //  2017/09/08

        //
        public boolean ucbSendLocalAv;                               //  2008/06/12
        public int iIndex_sharedObj;
        //  2014/12/11
        public int iIndex_sharedObjUsr;                        //  2014/12/11


        //  2014/09/24
        public int iIndex_taskInfo;                            //  2014/09/24

        //
        public boolean ucbLocalSource_rtspStream;                    //  2014/03/16

        //
        public boolean ucbStarter;                                   //  2010/08/31
        //public boolean ucbVideoConference;                           //  2009/05/30
        public boolean ucbVideoConferenceStarter;                    //

        //

        //
        public qnmCommProc_mis.TASK_AV_FROM mem0_from;                                 //  the first member in mems_from array. 2009/02/15


        //  2014/08/04
        public ProcTaskAv_saveVideo saveVideo = new ProcTaskAv_saveVideo();


        //  2014/11/10
        public PROC_TASK_AV_confMosaicMaker confMosaicMaker = new PROC_TASK_AV_confMosaicMaker();

        //
//  2014/11/21
        public ProcTaskAv_applyForProxy applyForProxy = new ProcTaskAv_applyForProxy();

        /**/
//  2014/11/19
        public static class ProcTaskAv_doProxy {
            //
            public boolean bExists_avProxy;
            //
         }
        public ProcTaskAv_doProxy doProxy = new ProcTaskAv_doProxy();                                    //  2014/11/19. 用一个客户端为另一个客户端做转发。在mosaic需要这么做


        //  2014/11/21
        public static class ProcTaskAv_receiver {
            //
            public boolean bRecvd_proxiedTranInfo;
            //				
            public qnmCommProc_mis.TASK_AV_FROM proxied_from = new qnmCommProc_mis.TASK_AV_FROM();                                    //  2014/11/21
         }
        public ProcTaskAv_receiver receiver = new ProcTaskAv_receiver();

        
        public int index_trans;
        public qmcTransform.QisTrans trans = new qmcTransform.QisTrans();

        //
        public int usCntLimit_dispatchs;                            //  2011/12/08
        public qnmCommProc_mis.QIS_DISPATCH[] pDispatchs;                                   //  2009/06/17


        //
        public PROC_TASK_AV_video video = new PROC_TASK_AV_video();
        public PROC_TASK_AV_audio audio = new PROC_TASK_AV_audio();


        //  
        public ProcTaskAv_photomosaic photomosaic = new ProcTaskAv_photomosaic();                                //

        //  
        public ProcTaskAv_mixer mixer = new ProcTaskAv_mixer();

        public ProcTaskAv_remoteAssist remoteAssist;

        boolean bTaskInvalid;                              //  2009/09/14

        //
        public qnmCommProc_mis.AV_TRAN_INFO reply_avTranInfo;


//String debugHint[128];								//  2009/10/15

        public PROC_TASK_AV() {
            int ii = 0;

            this.mem0_from = new qnmCommProc_mis.TASK_AV_FROM();

            return;
        }

        //
        public boolean bVideoConference() {
            boolean iErr = false;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(null, pProcInfo, this.iIndex_taskInfo, "");
            if (pTaskInfo == null) return iErr;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return iErr;
            if (pTaskData.uiType != CONST_taskDataType_conf) return iErr;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;

            iErr = pTc.ucbVideoConference;

            

            return iErr;

        }

    }


    public static class TASK_transferAv extends taskCommon.TASK_common {
        public taskAv.PROC_TASK_AV myTask;

        //
        public TASK_transferAv() {
            myTask = new taskAv.PROC_TASK_AV();

            return;
        }

    }


    public static class TASK_transferFile extends taskCommon.TASK_common {
        //M_taskCommon

        //
        public qnmCommProc_mis.PROC_TASK_FILE myTask;

        //
        public TASK_transferFile() {
            myTask = new qnmCommProc_mis.PROC_TASK_FILE();

        }
    }


    public static class qyFuncs {
        public static boolean bApplyForAvProxy(qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.TRANSFER_AV_INFO pTRANSFER_AV_INFO) {
            return ((pTRANSFER_AV_INFO).idInfo_logicalPeer.ui64Id != 0 && pMisCnt.idInfo.ui64Id != (pTRANSFER_AV_INFO).idInfo_logicalPeer.ui64Id);
        }

        //
        public static void M_getMutexName_syncActiveMem(ctxQmc.QMC_cfg pQmcCfg, int iTaskId, String mutexName, int mutexNameCnt) {
            String str;
            String pName = pQmcCfg.mutexName_syncActiveMem_prefix1;
            {
                //_sntprintf(mutexName, mutexNameCnt, ("%s%d.%d"), (pQmcCfg)->mutexName_syncActiveMem_prefix1, GetCurrentProcessId(), iTaskId)
                str = String.format("%s%s.%s", new String(pName), 0, iTaskId);
            }
            return;

        }
    }
}

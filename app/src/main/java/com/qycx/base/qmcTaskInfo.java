


package com.qycx.base;

import static com.qycx.base.ctxCaller.Consts.CONST_ctxCallerType_thread;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_transferFile;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_conf_activeMems;
import static com.qycx.base.qnmCommProc_mis.Consts.MAX_conf_requestingMems;
import static com.qycx.base.qyCommProc.qyFuncs.bTaskAlive;
import static com.qycx.base.tmpProc.qyFuncs.removeTask;

import android.opengl.EGLContext;

import com.qycx.base.util.Log;

import java.lang.ref.WeakReference;

public class qmcTaskInfo {

    public static class Consts {

        public final static int CONST_taskDataType_null = 0;

        public final static int CONST_taskDataType_conf = 1;
        public final static int CONST_taskDataType_transferFile = 2;


    }

    public static class QmcTaskDataCommon_tmpStatus {
        public long lastTickCnt_dec_a;
        public long lastTickCnt_dec_v;


    }

    public abstract static class QMC_taskData_common {
        public int uiType;
        //
        //
        public qnmCommProc_mis.MIS_MSGU msgU = new qnmCommProc_mis.MIS_MSG_TASK();

        //
        public QmcTaskDataCommon_tmpStatus tmpStatus = new QmcTaskDataCommon_tmpStatus();

        //

        public abstract void free();
    }

    ;

    //
    public static class TMP_desc {
        public String desc;//[cntof_desc];

    }

    public static class DLG_TALK_videoConferenceActiveMemFrom {

        public qnmCommProc_mis.AV_stream avStream = new qnmCommProc_mis.AV_stream();
        //
        //public String desc[cntof_desc];
        public TMP_desc desc = new TMP_desc();

        //
        public int dwLastTickCnt_aData;
        public int dwLastTickCnt_vData;
        //
        public int nTimes_tooLong;

        public int last_iPts_a;
        public int last_iPts_v;
        //
        public int dwTickCnt_showPts_a;
        public int dwTickCnt_showPts_v;

        public void memset0() {

            //fixed (AV_stream pAs =avStream)
            {
                //qyFuncs.mymemset((byte[])pAs, 0, sizeof(AV_stream));
                avStream = new qnmCommProc_mis.AV_stream();
            }
            //TMP_desc   pDesc=&desc)
            {
                //qyFuncs.mymemset(pDesc, 0, sizeof(TMP_desc));
                desc = new TMP_desc();
            }
            //
            this.dwLastTickCnt_aData = 0;
            this.dwLastTickCnt_vData = 0;
            this.nTimes_tooLong = 0;
        }
    }

    ;

    //
    public static class DLG_TALK_videoConference_requestingMem_avStream {
        public qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
    }

    ;

    //
    public static class DLG_TALK_videoConference_requestingMems_from {
        //public AV_stream avStream;
        public DLG_TALK_videoConference_requestingMem_avStream avStream = new DLG_TALK_videoConference_requestingMem_avStream();
        //
        //public String desc1[cntof_desc];
        public TMP_desc desc;

        //

        public void memset0() {
            return;
        }
    }

    ;


    //
    public static class DLG_TALK_videoConference {

        //
        public int usMaxSpeakers;
        public int usCntLimit_activeMems_from;
        public qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom[] activeMems_from;//[MAX_speakers_taskAv + MAX_mosaicMems_taskAv + MAX_resourceMems_taskAv_conf];
        public boolean bNeedRefresh_activeMems_from;

        //
        public DLG_TALK_videoConference_requestingMems_from[] requestingMems_from;//[MAX_requestingMems_videoConference];


        //		
        //  Note: fromList 仅由界面使用。activeMems_from可以由其他线程使用		
        public int usCntLimit_mems_from;                           //  from	

        //
        public qnmCommProc_mis.TASK_AV_FROM[] pMems_from1;

        //
        public int uiTranNo_lastModified_pMems_from;              //  if  dwTickCnt_lastModified_pMems_from is not the same as the value is myTask, then the pMems_from will be retrieved from myTask...
        public boolean bNeedRefresh_fromList;                         //  2015/02/17

        public DLG_TALK_videoConference() {
            qmcTaskInfo.DLG_TALK_videoConference videoConference = this;

            //
            int i;
            videoConference.activeMems_from = new qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom[MAX_conf_activeMems];
            for (i = 0; i < videoConference.activeMems_from.length; i++) {
                videoConference.activeMems_from[i] = new qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom();
            }
            videoConference.requestingMems_from = new DLG_TALK_videoConference_requestingMems_from[MAX_conf_requestingMems];
            for (i = 0; i < videoConference.requestingMems_from.length; i++) {
                videoConference.requestingMems_from[i] = new DLG_TALK_videoConference_requestingMems_from();
            }

            return;
        }


        //
        public void memset0() {
            this.usMaxSpeakers = 0;
            this.usCntLimit_activeMems_from = 0;
            //
            this.usCntLimit_mems_from = 0;

            Log.d( "qmcTaskInfo.DLG_TALK_videoConference.memset0 called");
        }
    }

    ;


    public static class QMC_taskData_conf_grp {
        public qyDefs.QY_MESSENGER_ID idInfo_initiator;

    }

    ;

    public static class QMC_taskData_conf_shareScreen {
        boolean bTaskExists;
        int index_sharedObj;
        //
    }

    ;

    public static class QMC_taskData_conf extends qmcTaskInfo.QMC_taskData_common {
        //public int uiType;

        public boolean bInited;

        //
        public boolean ucbVideoConference;                           //  2009/05/30

        //
        public QMC_taskData_conf_grp grp;


        //		
        //public qmcTaskInfo.DLG_TALK_videoConference			videoConference;
        public int ind_videoConference;

        //
        //public boolean ucbNotSendData;                                               //  2009/02/22

        //
        public QMC_taskData_conf_shareScreen shareScreen;

        //
        public EGLContext shared_eglContext;


        //  注意：如有需要，下面的动态分配也可以静态化.2022/01/13
        public QMC_taskData_conf(int index_taskInfo) {

            qmcTaskInfo.QMC_taskData_conf pTc = this;
            //
            pTc.uiType = Consts.CONST_taskDataType_conf;

            //pTc.videoConference = new qmcTaskInfo.DLG_TALK_videoConference();
            pTc.ind_videoConference = guiShare.GuiShare.pf_newIndex_DLG_TALK_videoConference.call(index_taskInfo);
            //
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();
        }

        @Override
        public void free() {
            //throw new NotImplementedException();

            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) this;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            //pTc.videoConference = null;
            guiShare.GuiShare.pf_freeIndex_DLG_TALK_videoConference.call(pTc.ind_videoConference);
        }

        public qmcTaskInfo.DLG_TALK_videoConference getVideoConference() {
            return guiShare.GuiShare.pf_getVideoConference.call(this.ind_videoConference);
        }

    }


    public static class QMC_taskData_transferFile extends qmcTaskInfo.QMC_taskData_common {
        //public int uiType;


        //public boolean bInited;

        //
        public long dwStartTickCnt;                       //
        public long dwLastReportTickCnt;              //

        //
        public QMC_taskData_transferFile() {
            this.uiType = CONST_taskDataType_transferFile;
        }


        @Override
        public void free() {

        }
    }

    public static class TmpErrMissing {
        public long dwTickCnt_startToCalc;
        public int nTimes_errMissing;

    }

    public static class QMC_taskInfo_var {
        public long dwTickCnt_start;

        //
        public long dwTickCnt_recv_lastRefreshed1;        // 最新收到数据的时刻,或者其他被更新状态的时刻
        //
        //
        public int iTaskId;

        //
        public boolean m_bReplyTask;

        //				 
        public boolean bTaskInvalid;                                //  2009/09/14
        //
        public boolean bNeedRemoved;                                //  2015/09/08

        //
        public boolean bNeedRemoved_toRecover;                //  2023/10/03

        //
        public boolean bClosed;

        public qmcSharedObj.ROUTE_sendLocalAv curRoute_sendLocalAv = new qmcSharedObj.ROUTE_sendLocalAv();                                     //  2014/10/29

        public TmpErrMissing tmpErrMissing = new TmpErrMissing();
    }

    ;


    public static class QMC_TASK_INFO {
        public boolean bUsed;

        //
        public QMC_taskInfo_var var;
        //
        public WeakReference<Object> hWnd_task;
        //
        //public qmcTaskInfo.QMC_taskData_common pTaskData;
        public int ind_pTaskData;

        //
        public qmcSharedObj.TASK_respInfo taskRespInfo;

        //
        public qmcTaskInfo.QMC_taskData_common getTaskData() {
            //qmcTaskInfo.QMC_taskData_common pTaskData = (qmcTaskInfo.QMC_taskData_common)obj_pTaskData;
            return guiShare.GuiShare.pf_getTaskData.call(ind_pTaskData);
        }


    }

    public static class qyFuncs {

        public static int newTaskInfoIndex(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, int uiTaskDataType, Object hWnd_task, int iTaskId, String hint) {
            int iErr = -1;

            if (null == pProcInfo) return -1;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = null;
            int i;

            String str;

            if (hint == null) hint = "";
            
            if (0 == iTaskId) return -1;
            
            i = getQmcTaskInfoIndexBySth(ctxCaller, pProcInfo, iTaskId);
            if (i >= 0) {
                Log.e("newTaskInfoIndex failed, iTaskId already has a taskInfo");
                return -1;
            }
            
            //  Note: start from 1.
            for (i = 1; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
                pTaskInfo = pProcInfo.pQmcTaskInfos[i];
                if (null == pTaskInfo) continue;

                if (!pTaskInfo.bUsed) break;
            }
            if(pTaskInfo == null) return iErr;
            if (i == pProcInfo.cfg.usMaxCnt_taskInfos) return iErr;
int ll = 0;
            try {
                pTaskInfo.ind_pTaskData = guiShare.GuiShare.pf_new_QMC_taskData.call(i,uiTaskDataType);
                if(pTaskInfo.ind_pTaskData<0) {
                    showInfo_open.qyFuncs.showInfo_open(0, "newTaskInfoIndex ", "error: line : 368 pf_new_QMC_taskData return -1");
                    return iErr;
                }
                        ll = 1;
                pTaskInfo.var = new QMC_taskInfo_var();
                ll = 2;
                pTaskInfo.var.iTaskId = iTaskId;
                ll = 3;
                pTaskInfo.var.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);
                ll = 4;
                pTaskInfo.var.dwTickCnt_recv_lastRefreshed1 = tmpFunc.qyFuncs.myGetTickCount(null);
                ll = 5;
                pTaskInfo.hWnd_task = new WeakReference<>(hWnd_task);
                ll = 6;
                pTaskInfo.taskRespInfo = new qmcSharedObj.TASK_respInfo();
                ll = 7;
            } catch (Exception e) {
                Log.e( e.getMessage());
                String str1  = e.getMessage();
                showInfo_open.qyFuncs.showInfo_open(0, "", "newTaskInfoIndex except:" + " : " +ll +" " +  str1);
                return iErr;
            }
            pTaskInfo.bUsed = true;

            Log.i("newTaskInfo "+i+" . "+ hint);
            showInfo_open.qyFuncs.showInfo_open("newTaskInfoIndex enters: index: " + i);
            iErr = 0;

            //
            return iErr != 0 ? iErr : i;
        }


        //
        public static int freeTaskInfo(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, int index_taskInfo, String hint) {
            int iErr = -1;

            if (null == pProcInfo) return -1;
            if (index_taskInfo < 0 || index_taskInfo >= pProcInfo.cfg.usMaxCnt_taskInfos) return -1;

            boolean bDbg = false;
            String str;

            //
            if (hint == null) hint = "";

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(ctxCaller, index_taskInfo, "freeTaskInfo");
            if (null == pTaskInfo) return 0;
            //
            if (!pTaskInfo.bUsed) return 0;
            str = String.format("freeTaskInfo %d enters, %s", index_taskInfo,hint);
            showInfo_open.qyFuncs.showInfo_open(0, null, str);
            removeTask(ctxCaller, pTaskInfo.var.iTaskId);
            guiShare.GuiShare.pf_free_QMC_taskData.call(pTaskInfo.ind_pTaskData);
            pTaskInfo.bUsed = false;
            str =String.format("freeTaskInfo %d leaves ok.", index_taskInfo);
            showInfo_open.qyFuncs.showInfo_open(0, null, str);
            return 0;

        }


        public static qmcTaskInfo.QMC_TASK_INFO getQmcTaskInfoByIndex(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, int index, String hint) {
            if (hint == null) hint = "";

            if (index < 0 || index >= pProcInfo.cfg.usMaxCnt_taskInfos) return null;

            if (pProcInfo == null || pProcInfo.pQmcTaskInfos == null) return null;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.pQmcTaskInfos[index];

            return pTaskInfo;
        }


        //
        //
        public static int getQmcTaskInfoIndexBySth(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, int iTaskId) {
            int i;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = null;

            if (iTaskId == 0) return -1;

            for (i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
                pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, i, "getQmcTaskInfoBySth");
                if (!pTaskInfo.bUsed) continue;
                if (pTaskInfo.var.iTaskId == iTaskId) break;
            }
            if (i == pProcInfo.cfg.usMaxCnt_taskInfos) return -1;

            return i;

        }

        //
        public static int qmcTaskInfo_setClosed(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, int index_taskInfo, String hint) {
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = null;

            pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, index_taskInfo, "qmcTaskInfo_setClosed");
            if (pTaskInfo == null) return -1;

            if (!pTaskInfo.bUsed) return -1;

            //
            removeTask(ctxCaller, pTaskInfo.var.iTaskId);

            //
            if (pTaskInfo.var.bClosed) return 0;

            //
            pTaskInfo.var.bClosed = true;

            //
            showInfo_open.qyFuncs.showInfo_open("pTaskInfo.var.bClosed = true "  +  index_taskInfo  + " "  +  hint );

            //
            pTaskInfo.var.dwTickCnt_recv_lastRefreshed1 = tmpFunc.qyFuncs.myGetTickCount(null);

            //
            return 0;
        }

        public static boolean qmcTaskInfo_bAlive(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, int index_taskInfo) {
            boolean bRet = false;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = null;

            pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, index_taskInfo, "qmcTaskInfo_bAlive");
            if (pTaskInfo == null) return bRet;

            if (!pTaskInfo.bUsed) return bRet;

            if (pTaskInfo.var.bClosed) return bRet;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return bRet;

            qnmCommProc_mis.MIS_MSGU tmp_pMsg = pTaskData.msgU;

            if (tmp_pMsg.uiType != CONST_misMsgType_task) return bRet;

            if (!bTaskAlive(((qnmCommProc_mis.MIS_MSG_TASK)tmp_pMsg).iStatus)) return bRet;

            bRet = true;
            return bRet;
        }

        //
        public static int freeTaskInfos(ctxQmc.CCtxQmc pProcInfo) {
            int iErr = -1;
            int i;

            if (null == pProcInfo) return -1;
            //QY_MC pQyMc = (QY_MC)pProcInfo->pQyMc;
            //if (!pQyMc) return -1;

            //
            com.qycx.base.ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();

            //mymemset((byte[])ctxCaller, 0, sizeof(CTX_caller));
            ctxCaller.type = CONST_ctxCallerType_thread;
            ctxCaller.dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            ctxCaller.bTmp = true;

            //
            for (i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(ctxCaller, i, "freeTaskInfo");
                if (null == pTaskInfo) continue;
                //
                if (!pTaskInfo.bUsed) continue;
                //
                if (0 != pTaskInfo.var.iTaskId) {

                }

                //
                freeTaskInfo(ctxCaller, pProcInfo, i, "tryToFreeTaskInfos");
            }


            //
            iErr = 0;

            return iErr;
        }


    }


}




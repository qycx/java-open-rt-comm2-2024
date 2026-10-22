


package com.qycx.base;

import static com.qycx.base.getTalkerDesc.Consts.cntof_desc;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;

import com.qycx.base.util.Log;


public class dlgTalk_videoConference_public {
    public static class qyFuncs {


        //
        public static int getMemFromActiveIndex(qmcTaskInfo.QMC_taskData_conf pTc, qyDefs.QY_MESSENGER_ID idInfo_from, qnmCommProc_mis.RES_obj_simple pResObj_from)
        {
            int  iErr  =  -1;

            int i;

            //qmcTaskInfo.DLG_TALK_videoConference pVc = (qmcTaskInfo.DLG_TALK_videoConference)pDLG_TALK_videoConference;
            qyDefs.QY_MESSENGER_ID pIdInfo_from = idInfo_from;

            //  2014/11/30
            if (null == pIdInfo_from || pIdInfo_from.ui64Id == 0) {
                String str = "Note: isMemFromActive returns false, for idInfo_from is 0";
                Log.d(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                return iErr;
            }
            qnmCommProc_mis.RES_obj_simple resObj_from;
            //qyFuncs.mymemset((byte[])resObj_from, 0, sizeof(RES_obj_simple));
            if (null != pResObj_from) {
                resObj_from = pResObj_from;
            } else {
                resObj_from = new qnmCommProc_mis.RES_obj_simple();
            }

            //
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();
            for (i = 0; i < videoConference.activeMems_from.length; i++) {
                qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[i];
                if (pIdInfo_from.ui64Id == pActiveMem_from.avStream.idInfo.ui64Id
                        && resObj_from.uiObjType == pActiveMem_from.avStream.obj.resObj.uiObjType
                        && resObj_from.usIndex_obj == pActiveMem_from.avStream.obj.resObj.usIndex_obj)
                {
                    break;
                }
            }
            if (i == videoConference.activeMems_from.length) return iErr;

            //if (0 != pIndex_activeMems_from) pIndex_activeMems_from = i;

            //
            return i;
        }




        //public static  boolean isMemFromActive(qmcTaskInfo.DLG_TALK_videoConference pVc, qyDefs.QY_MESSENGER_ID idInfo_from, RES_obj_simple pResObj_from, int  pIndex_activeMems_from)
        public static boolean isMemFromActive(qmcTaskInfo.QMC_taskData_conf pTc, qyDefs.QY_MESSENGER_ID idInfo_from, qnmCommProc_mis.RES_obj_simple pResObj_from, int tn_a, int tn_v) {

            int i;

            /*
            //qmcTaskInfo.DLG_TALK_videoConference pVc = (qmcTaskInfo.DLG_TALK_videoConference)pDLG_TALK_videoConference;
            qyDefs.QY_MESSENGER_ID pIdInfo_from = idInfo_from;

            //  2014/11/30
            if (null == pIdInfo_from || pIdInfo_from.ui64Id == 0) {
                String str = "Note: isMemFromActive returns false, for idInfo_from is 0";
                Log.d(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                return false;
            }
            qnmCommProc_mis.RES_obj_simple resObj_from;
            //qyFuncs.mymemset((byte[])resObj_from, 0, sizeof(RES_obj_simple));
            if (null != pResObj_from) {
                resObj_from = pResObj_from;
            } else {
                resObj_from = new qnmCommProc_mis.RES_obj_simple();
            }

            //
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();
            for (i = 0; i < videoConference.activeMems_from.length; i++) {
                qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[i];
                if (pIdInfo_from.ui64Id == pActiveMem_from.avStream.idInfo.ui64Id
                        && resObj_from.uiObjType == pActiveMem_from.avStream.obj.resObj.uiObjType
                        && resObj_from.usIndex_obj == pActiveMem_from.avStream.obj.resObj.usIndex_obj)
                {
                    if  (  tn_a!=0) {
                        if  (  pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev!=tn_a)  {
                            continue;
                        }
                    }
                    if  (  tn_v!=0)  {
                        if  (  pActiveMem_from.avStream.obj.tranInfo.video.uiTranNo_openAvDev!=tn_v) {
                            continue;
                        }
                    }
                    break;
                }
            }
            if (i == videoConference.activeMems_from.length) return false;

            //if (0 != pIndex_activeMems_from) pIndex_activeMems_from = i;

             */
            int  index_active  =  0;
            index_active  =  getMemFromActiveIndex(pTc,idInfo_from,pResObj_from);
            if  (  index_active<0)  return  false;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();
            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_active];

            if  (  tn_a!=0) {
                if  (  pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev!=tn_a)  {
                    return  false;
                }
            }
            if  (  tn_v!=0)  {
                if  (  pActiveMem_from.avStream.obj.tranInfo.video.uiTranNo_openAvDev!=tn_v) {
                    return false;
                }
            }

            //
            return true;
        }


        //
        //
        //public static  int addTo_activeMems_from(MIS_CNT pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.AV_stream_simple pAss, qmcTaskInfo.DLG_TALK_videoConference pVc)
        public static int addTo_activeMems_from(qmCommFunc_mis.MIS_CNT pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.AV_stream_simple pAss, qmcTaskInfo.QMC_taskData_conf pTc) {
            int iErr = -1;

            if (null == pAss) return -1;

            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            if (isMemFromActive(pTc, pIdInfo, pAss.resObj, pAss.tranInfo.audio.uiTranNo_openAvDev,pAss.tranInfo.video.uiTranNo_openAvDev)) return 0;
            int i;
            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from;
            for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
                pActiveMem_from = videoConference.activeMems_from[i];
                if (pActiveMem_from.avStream.idInfo.ui64Id == 0) break;
            }
            if (i == videoConference.usCntLimit_activeMems_from) {
                String str = "addTo_activeMems_from failed, too many activeMems";
                Log.e(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                return -1;
            }
            pActiveMem_from = videoConference.activeMems_from[i];

            pActiveMem_from.avStream.idInfo = pIdInfo;
            pActiveMem_from.avStream.obj = pAss;

            //
            String pDesc = pActiveMem_from.desc.desc;
            {
                qmcCommFunc_mis.qyFuncs.M_getActiveMemDesc(pMisCnt, pActiveMem_from.avStream.idInfo, pDesc, cntof_desc);
            }


            iErr = 0;


            return iErr;
        }

        //
        //public static  int removeFrom_activeMems_from(MIS_CNT pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.AV_stream_simple pAss, qmcTaskInfo.DLG_TALK_videoConference pVc)
        public static int removeFrom_activeMems_from(qmCommFunc_mis.MIS_CNT pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo, qnmCommProc_mis.AV_stream_simple pAss, qmcTaskInfo.QMC_taskData_conf pTc) {
            int iErr = -1;

            if (null == pAss) return -1;

            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            if (!isMemFromActive(pTc, pIdInfo, pAss.resObj, 0,0)) return 0;
            int i;
            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from;
            for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {
                pActiveMem_from = videoConference.activeMems_from[i];
                if (pActiveMem_from.avStream.idInfo.ui64Id == pIdInfo.ui64Id
                        && pActiveMem_from.avStream.obj.resObj.uiObjType == pAss.resObj.uiObjType
                        && pActiveMem_from.avStream.obj.resObj.usIndex_obj == pAss.resObj.usIndex_obj)
                    break;
            }
            if (i == videoConference.usCntLimit_activeMems_from) {
                String str = "addTo_activeMems_from failed, too many activeMems";
                Log.e(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                return -1;
            }
            pActiveMem_from = videoConference.activeMems_from[i];

            //memset(pActiveMem_from, 0, sizeof(pActiveMem_from[0]));
            pActiveMem_from.memset0();

            iErr = 0;


            return iErr;
        }


        //
        //
        public static boolean isActiveMemsFull(ctxCaller.CTX_caller ctxCaller, int iTaskId, boolean bSpeaker) {
            boolean bRet = false;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(ctxCaller, pProcInfo, iTaskId);
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, index_taskInfo, "isActiveMemsFull");
            if (null == pTaskInfo) return bRet;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskInfo.getTaskData();
            if (pTc.uiType != CONST_taskDataType_conf) return bRet;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            int i;
            String str;

            if (!bSpeaker) {
                for (i = 0; i < videoConference.activeMems_from.length; i++) {
                    if (videoConference.activeMems_from[i].avStream.idInfo.ui64Id == 0) break;
                }
                if (i < videoConference.activeMems_from.length) {
                    return bRet;
                }
            } else {
                //
                int nSpeakers = 0;
                //
                for (i = 0; i < videoConference.activeMems_from.length; i++) {
                    if (videoConference.activeMems_from[i].avStream.idInfo.ui64Id != 0
                            && 0 == videoConference.activeMems_from[i].avStream.obj.resObj.uiObjType) {
                        nSpeakers++;
                    }
                }

                //
                if (videoConference.usMaxSpeakers > 0) {
                    if (nSpeakers < videoConference.usMaxSpeakers) return bRet;
                }

                //
            }

            //
            bRet = true;


            return bRet;

        }

        //
        public static boolean isActiveMem(ctxCaller.CTX_caller ctxCaller, int iTaskId, qyDefs.QY_MESSENGER_ID idInfo) {
            boolean bRet = false;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            if (idInfo.ui64Id == 0) return false;

            //
            int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(ctxCaller, pProcInfo, iTaskId);
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) getQmcTaskInfoByIndex(ctxCaller, pProcInfo, index_taskInfo, "isActiveMemsFull");
            if (null == pTaskInfo) return bRet;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskInfo.getTaskData();
            if (pTc.uiType != CONST_taskDataType_conf) return bRet;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            int i;
            String str;


            //

            //
            for (i = 0; i < videoConference.activeMems_from.length; i++) {
                if (idInfo.ui64Id == videoConference.activeMems_from[i].avStream.idInfo.ui64Id
                        && 0 == videoConference.activeMems_from[i].avStream.obj.resObj.uiObjType) {
                    break;
                }
            }

            if (i == videoConference.activeMems_from.length) return bRet;
            bRet = true;


            return bRet;

        }


    }
}

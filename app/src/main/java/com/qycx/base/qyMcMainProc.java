package com.qycx.base;

import com.qycx.base.util.Log;


public class qyMcMainProc
{
    public static class PARAM_findOrgMsg
    {
        public boolean bNeedUpdate;
        public qnmCommProc_mis.CMIS_MSGU pMsgTask_o;
        public qnmCommProc_mis.CTRANSFER_FILEDATA_REQ pFileDataReq_o;
        public qnmCommProc_mis.CTRANSFER_FILEDATA_RESP pFileDataResp_o;
        public qnmCommProc_mis.CIM_CONTENTU pContent_o;
        public qnmCommProc_mis.TRANSFER_FILEEND_REQ pTransferFileEndReq;
        public qnmCommProc_mis.CTRANSFER_FILEEND_RESP pTransferFileEndResp;
        public qyDefs.COMMON_PARAM commonParam0;
        public qyDefs.COMMON_PARAM commonParam1;

        public int iTaskId;

        //
        public qyMcMainWndProc_isCli.qyFuncs_Droid.Param_tmp_showState_task param_tmp_showState_task;

        //
        public String hint;

        //
        public boolean bBreak;

        //
        public void memset0()
        {
            bNeedUpdate=false;
            pMsgTask_o=null;
            pFileDataReq_o=null;
            pFileDataResp_o=null;
            pContent_o=null;
            pTransferFileEndReq=null;
            pTransferFileEndResp=null;
            commonParam0=null;
            commonParam1=null;

            iTaskId  =  0;

            //
            param_tmp_showState_task=null;

            //
            hint=null;

            //
            bBreak=false;

        }
    }
    ;


    //
    public static class qyFuncs
    {


        public static  boolean bChkQyMcEnv(ctxQyMc.CCtxQyMc pQyMc)
        {
//            int bufSize = bufSize_routeTalkData;
//            int iSize=128;//sizeof(qnmCommProc_mis.IM_CONTENTU) + 1024;
//            if (bufSize < iSize)
//            {
//                int ii=128;//sizeof(IM_L_STREAM);
//                //
//                return false;
//            }
//            int size=128;//sizeof(IM_GRP_INFO);
//            if (CONST_sizeof_imGrpInfo < size)
//            {
//                Log.d("bChkQyMcEnv: sizeof_imGrpInfo error");
//                Thread.currentThread().stop();
//
//            }
//            size=128;//sizeof(qnmCommProc_mis.IM_GRP_MEM);
//            if (CONST_sizeof_imGrpMem < size)
//            {
//                Log.d("bChkQyMcEnv: sizeof_imGrpMem error");
//                Thread.currentThread().stop();
//            }
//            size=128;//sizeof(qnmCommProc_mis.RETRIEVE_contact_mem);
//            if (CONST_sizeof_retrieveContaceMem < size)
//            {
//                Log.d("bChkQyMcEnv: sizeof_retrieveContaceMem error");
//                Thread.currentThread().stop();
//
//            }
//            size=128;//sizeof(REFRESH_imObjRule_reqMem);
//            if (CONST_sizeof_refreshImObjRuleReqMem < size)
//            {
//                Log.d("bChkQyMcEnv: CONST_sizeof_refreshImObjRuleReqMem error");
//                Thread.currentThread().stop();
//            }
//            size=128;//sizeof(qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead);
//            if (CONST_sizeof_TRANSFER_VIDEO_dataMemHead < size)
//            {
//                Log.d("bChkQyMcEnv: CONST_sizeof_TRANSFER_VIDEO_dataMemHead error");
//                Thread.currentThread().stop();
//            }
//            size=128;//sizeof(myDRAW_VIDEO_DATA);
//            if (CONST_sizeof_myDRAW_VIDEO_DATA < size)
//            {
//                Log.d("bChkQyMcEnv: CONST_sizeof_myDRAW_VIDEO_DATA error");
//                Thread.currentThread().stop();
//            }
//            size=128;//sizeof(qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead);
//            if (CONST_sizeof_TRANSFER_AUDIO_dataMemHead < size)
//            {
//                Log.d("bChkQyMcEnv: CONST_sizeof_TRANSFER_AUDIO_dataMemHead error");
//                Thread.currentThread().stop();
//            }
//            size=128;//sizeof(myPlayAudioData.myPLAY_AUDIO_DATA);
//            if (CONST_sizeof_myPLAY_AUDIO_DATA < size)
//            {
//                Log.d("bChkQyMcEnv: CONST_sizeof_myPLAY_AUDIO_DATA error");
//                Thread.currentThread().stop();
//            }
//            size=128;//sizeof(qnmCommProc_mis.VIDEO_CONFERENCE_MEM);
//            if (sizeof_videoConferenceMem < size)
//            {
//                Log.d("bChkQyMcEnv: sizeof_videoConferenceMem error");
//                Thread.currentThread().stop();
//            }
//            size=128;//sizeof(CONF_requesting_mem);
//            if (sizeof_CONF_requesting_mem < size)
//            {
//                Log.d("bChkQyMcEnv: sizeof_requesting_mem error");
//                Thread.currentThread().stop();
//            }
//            size=128;//sizeof(IpcInfo);
//            if (sizeof_ipcInfo < size)
//            {
//                Log.d("bChkQyMcEnv: sizeof_ipcInfo error");
//                Thread.currentThread().stop();
//            }

            return true;

        }

        public static  int getDynCfg_isClient(ctxQyMc.CCtxQyMc pQyMc,  qnmCommProc.QNM_CNT_CFG pDynCfg)
        {
            int iErr = -1;
            //QNM_CNT_CFG cntCfg_sys;
            //QNM_CNT_CFG cntCfg;
            int i;
            //
            //if (!pDynCfg) return -1;

            qnmCommProc.QNM_CNT_CFG pCfg =pDynCfg;
            {

                pCfg.usCntPort = 8768;
                //safeStrnCpy("127.0.0.1", pCfg->cntIp, CONST_qyMaxIpLen + 1);
            }
           

            iErr = 0;
        
            return iErr;
        }


        static void exitAllDaemonThreads(ctxQyMc.CCtxQyMc pQM)
        {
            int i;
            String str;

            //
            ctxQmc.CCtxQmc pProcInfo = pQM.get_pProcInfo();

            //
            Log.i("exitAllDaemonThreads(  ) enters.");

            //
            if (pQM.bLogon)
            {
                //Log.e("exitAllDaemonThreads: ÕâÀïµÄbQuit±êÖ¾ÎªÊ²Ã´Ã»ÉèÖÃÄØ");
                Log.d("Err: exitAllDaemonThreads: bLogon is true");
            }

            tmpFunc.qyFuncs.mySetEvent(pProcInfo.hEvt_notify_thread_isCli);

            //
            for (i = 0; i < pQM.hDaemonThreads.length; i++)
            {
                if (pQM.hDaemonThreads[i] == null) continue;

                guiShare.GuiShare.pf_waitForThread1.call( pQM.hDaemonThreads[i], 20000);
                pQM.hDaemonThreads[i] = null;
            }
            Log.i("exitAllDaemonThreads(  ) leaves.");
        }
    }
}

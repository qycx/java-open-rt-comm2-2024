


package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.makeMsg2MgrFunc_mc;
import static com.qycx.base.qmcVideoFunc.qyFuncs.getSharedObjUsr;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendMedia;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_routeTalkData;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;

import com.qycx.base.util.Log;

import java.util.List;

public class qmc_toShareVideo_post {
    public static class qyFuncs {
        //public static int toShareVideo_post_qmc(ctxCaller.CTX_caller ctxCaller, qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar,  ctxQmc.CCtxQmc pProcInfo, int uiCapType, int iIndex_capProcInfo, qnmCommProc_mis.myDRAW_VIDEO_DATA pkts, int nPkts, qmcSharedObj.COMPRESS_VIDEO pCompressVideo, qmcSharedObj.QY_SHARED_OBJ pSharedObj, qnmCommProc_mis.MIS_MSGU pMsgBuf1)
        public static int toShareVideo_post_qmc(ctxCaller.CTX_caller ctxCaller, qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar, ctxQmc.CCtxQmc pProcInfo, int uiCapType, int iIndex_capProcInfo, qnmCommProc_mis.TRANSFER_VIDEO_DATA tvd, qmcSharedObj.COMPRESS_VIDEO pCompressVideo, qmcSharedObj.QY_SHARED_OBJ pSharedObj, qnmCommProc_mis.MIS_MSGU pMsgBuf1) {
            {
                int iErr = -1;
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                int tmpiRet = 0;
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

                //
                boolean bDbg = false;
                String str;

                //if (null == pkts || 0 == nPkts) return -1;
                if (tvd == null) return -1;

                int uiChannelType = CONST_channelType_media;


            long tStartTran = 0;//pkts.input.tStartTran;           //  2011/08/30
            if (0 == tStartTran) {
                tStartTran = tmpFunc.qyFuncs.mytime();
            }



                boolean ucbNeedShowFrameInfo = false;

                //  2010/09/04
                qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = null;
                if (null == pSharedObj) return iErr;
                pSharedObjUsr = getSharedObjUsr(pSharedObj, pSharedObj.var.iIndex_curUsr);
                if (null == pSharedObjUsr) return iErr;

            /*
            if (pCompressVideo.tLastTime_post != tStartTran) {               //  Õâ±íÃ÷ÊÇÒ»ÃëµÄ¿ªÊ¼

                pCompressVideo.tLastTime_post = tStartTran;

                //
                if ((int) (tStartTran - pCompressVideo.tLastTime_showFrameInfo) > pProcInfo.cfg.usNeedShowFrameInfoIntervalInS) {
                    //
                    ucbNeedShowFrameInfo = true;
                }

                long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
                int nElapseInMs = (int) (dwTickCnt - pCompressVideo.dwTickCnt_selectTime);

                //  2014/04/26. 2008/12/05, uiCnt_pkts_fromSelect
                if (0 == pCompressVideo.tSelectTime
                        || pCompressVideo.uiCnt_pkts_fromSelect > 32 * 1024
                        || pCompressVideo.tmp_bMediaChannelReady != pProcInfo.av.video.bChannelReady
                        || Math.abs(nElapseInMs) > 1 * 60 * 1000 //  2017/09/05
                ) {
                    try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                        String pName = pProcInfo.cfg.mutexName_syncSendAv;
                        if (0 == syncObj.sync(pName, pMisCnt.hMutex_syncSendAv, 0,"toShareVideo_post.69")) {
                            pCompressVideo.tSelectTime = tStartTran;
                            pCompressVideo.dwTickCnt_selectTime = dwTickCnt;
                            pCompressVideo.uiCnt_pkts_fromSelect = 0;
                            pCompressVideo.fFps_sending = 0;
                            pCompressVideo.usTimes_ok = 0;
                            pCompressVideo.usTimes_failure = 0;
                            pCompressVideo.tmp_bMediaChannelReady = pProcInfo.av.video.bChannelReady;

                            ucbNeedShowFrameInfo = true;
                        }
                    } catch (Exception e) {
                        Log.e(e.getMessage());
                        return -1;
                    }
                }

            }

             */

                //  2014/11/16
                if (pSharedObjUsr.ucbInConfMosaic) {
                    iErr = 0;
                    return iErr;
                }

                //
                byte ucFlg = CONST_commFlg_routeTalkData; //  0;
                int usIndex_activeMems_from = 0;
                int myuiSampleTimeInMs = 0;//  2009/05/30
                int ii;

                qnmCommProc_mis.TRANSFER_VIDEO_DATA pReq = null;


                pReq = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) tvd;//(pkts.memory.m_obj);

                //
                if (0 == pReq.usCnt) {
                    showInfo_open.qyFuncs.showInfo_open(0, ("toShareVideo_post_qmc"), (" , req.usCnt is 0"));
                    return iErr;
                }
                //  2010/09/12
                pCompressVideo.uiCnt_pkts_fromSelect += pReq.usCnt;

                int uiTranNo = 0;
                if ((uiTranNo = getuiNextTranNo.call()) == -1) return iErr;
                int lenInBytes = 0;

                lenInBytes = pReq.uiLen;
                boolean ucbFixedRate;
                ucbFixedRate = false;

                {
                    qnmCommProc_mis.MIS_MSGU pMsgBuf;

                    //
                    if (pProcInfo.cfg.b_useQ2_toEncV) {
                        if (null == (pMsgBuf = makeMsg2MgrFunc_mc(pMisCnt, null, CONST_misMsgType_task, ucFlg, CONST_qyCmd_sendMedia, tStartTran, uiTranNo, (short) 0, pSharedObjUsr.iTaskId, 0, pReq, lenInBytes, 0, 0, 0, 0, uiChannelType, null))) {
                            Log.e("postMsg2MgrFunc_mc failed");
                            return iErr;
                        }
                    } else {
                        if (null == (pMsgBuf = makeMsg2MgrFunc_mc(pMisCnt, null, CONST_misMsgType_task, ucFlg, CONST_qyCmd_sendMedia, tStartTran, uiTranNo, (short) 0, pSharedObjUsr.iTaskId, 0, pReq, lenInBytes, 0, 0, 0, 0, uiChannelType, pMsgBuf1))) {
                            Log.e("postMsg2MgrFunc_mc failed");
                            return iErr;
                        }

                    }

                    //
                    qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsgBuf;
                    task.bFrom_sharedObj = true;
                    task.index_sharedObj = pSharedObj.var.index_sharedObj;

                    qyMcMainProc.PARAM_findOrgMsg param = tmpVar.m_ctxThreadToShareVideoPost.tmp_param_findOrgMsg;//new qyMcMainProc.PARAM_findOrgMsg();
                    param.memset0();
                    if ((tmpiRet = pProcInfo.processQ_media.qTraverse(ctxCaller, tmpHandler_findOrgMsgAndProc, task, param)) <= 0) {
                        str  =  ("toShareVideo_post: postMsg2MgrFunc_mc: msg_task_toMix failed. tmpiRet=" + tmpiRet);
                        Log.e("qy",str);
                        showInfo_open.qyFuncs.showInfo_open(str);
                    }
                }

                if (ucbFixedRate != pCompressVideo.ucbFixedRate)
                    pCompressVideo.ucbFixedRate = ucbFixedRate;

                iErr = 0;


                return iErr;

            }


        }
    }
}

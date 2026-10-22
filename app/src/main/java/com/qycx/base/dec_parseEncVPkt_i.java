


package com.qycx.base;

import static com.qycx.base.Consts.CONST_qyRet_needKeyFrameFirst;
import static com.qycx.base.ctxCaller.Consts.CONST_ctxCallerType_thread;
import static com.qycx.base.ctxQmcTmpl.Consts.CONST_fn_dump_h264_enc;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskProcReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferVideoData;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc;
import static com.qycx.base.qmcTransform.Consts.CONST_mutexName_syncIndex_dec;
import static com.qycx.base.qmcTransform.Consts.MAX_cnt_preDec_array;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendMedia;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_applyForKeyFrame;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_usTaskProcObjType_vStream;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_TRANSFER_VIDEO_dataMemHead_mems;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_HEVC;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_dec;
import static com.qycx.base.qyDefs_open.Consts.NAL_IDR_N_LP;
import static com.qycx.base.qyDefs_open.Consts.NAL_IDR_W_RADL;
import static com.qycx.base.qyDefs_open.Consts.NAL_VPS;
import static com.qycx.base.qyMcMainDaemon_isCli_mgr.Consts.cntof_tBuf128;

import com.qycx.base.qdc.DecMediaCodecCommon;
import com.qycx.base.util.Log;

import java.util.Arrays;

public class dec_parseEncVPkt_i {

    public static class qyFuncs {

        public static int chkToForceKeyFrame(ctxCaller.CTX_caller ctxCaller, qmcTransform.QY_TRANSFORM pTransform) {
//            showInfo_open.qyFuncs.showInfo_open("chkToForceKeyFrame enters");
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//QY_GET_procInfo_isCli(  );
            if (null == pProcInfo) {
                showInfo_open.qyFuncs.showInfo_open("chkToForceKeyFrame return pProcInfo");
                return -1;
            }

            if (null == pTransform.pTranInfo_unused) {
                showInfo_open.qyFuncs.showInfo_open("chkToForceKeyFrame return pTransform.pTranInfo_unused");
                return -1;
            }
            if (null == pTransform.pTask
                    || null == pTransform.pMsgTask
            ) {
                showInfo_open.qyFuncs.showInfo_open("chkToForceKeyFrame return pTransform.pMsgTask");
                return -1;
            }

            qnmCommProc_mis.AV_TRAN_INFO pAvTran_unused = (qnmCommProc_mis.AV_TRAN_INFO) pTransform.pTranInfo_unused;
            taskAv.PROC_TASK_AV pTask = (taskAv.PROC_TASK_AV) pTransform.pTask;
            qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTransform.pMsgTask;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;// pMsgTask->pMisCnt;
            if (null == pMisCnt) {
                showInfo_open.qyFuncs.showInfo_open("chkToForceKeyFrame return pMisCnt 111111");

                return iErr;
            }

//            long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
            long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount64(null);
            long iDiffInMs = (long) (dwTickCnt - pTransform.video.decInfo.applyForKeyFrame.dwLastTickCnt_applyForKeyFrame);
            if (Math.abs(iDiffInMs) < 1000) {
                iErr = 0;
//                showInfo_open.qyFuncs.showInfo_open("chkToForceKeyFrame return Math.abs(iDiffInMs) < 1000");
                return iErr;
            }
            pTransform.video.decInfo.applyForKeyFrame.dwLastTickCnt_applyForKeyFrame = dwTickCnt;

            //
            short cmdCode = CONST_qyCmd_sendMedia;
            qnmCommProc_mis.TASK_PROC_REQ taskProcReq = new qnmCommProc_mis.TASK_PROC_REQ();
            int lenInBytes;
            //mymemset( ( byte[])taskProcReq, 0, sizeof(TASK_PROC_REQ));
            taskProcReq.uiType = CONST_imCommType_taskProcReq;
            //
            taskProcReq.usOp = CONST_imOp_recv_applyForKeyFrame;
            //
            taskProcReq.usTaskProcObjType = CONST_usTaskProcObjType_vStream;


            if (pTransform.video.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {
                Log.d( ("chkToForceKeyFrame failed. vStream not deced is mine."));
                showInfo_open.qyFuncs.showInfo_open("chkToForceKeyFrame failed. vStream not deced is mine.");
                return iErr;
            }

//            showInfo_open.qyFuncs.showInfo_open("chkToForceKeyFrame center。。。");

            //
            qyDefs.QY_MESSENGER_ID idInfo_dst = new qyDefs.QY_MESSENGER_ID();
            if (pTask.ucbVideoConferenceStarter) {
                idInfo_dst.ui64Id = pTransform.video.idInfo.ui64Id;
                //
                taskProcReq.vStream.uiTranNo_openAvDev = pTransform.video.tv.uiTranNo_openAvDev;
            } else if (pTask.bVideoConference()) {
                //  2014/08/28. 暂时不考虑代理模式，所以直接发给视频源
                idInfo_dst.ui64Id = pTransform.video.idInfo.ui64Id;
                //
                taskProcReq.vStream.uiTranNo_openAvDev = pTransform.video.tv.uiTranNo_openAvDev;
            } else {
                idInfo_dst.ui64Id = pTransform.video.idInfo.ui64Id;
                //
                taskProcReq.vStream.uiTranNo_openAvDev = pTransform.video.tv.uiTranNo_openAvDev;
            }


            //
            String str;


            //
            lenInBytes = 128;//sizeof(TASK_PROC_REQ);
            //
            long tStartTran = tmpFunc.qyFuncs.mytime();
            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

            //
            if  (  false  )  {
                Log.d("qy","for dbg: chkToForceKeyFrame.127, set uiTranNo to 123456");
                uiTranNo  =  123456;
            }


            //
            if (true) {
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("chkToForceKeyFrame : dst %I64u, cnt_preDec %d, cnt_postDec %d, nTimes_needKeyFrame %d"), idInfo_dst.ui64Id, pTransform.video.decInfo.cnt_preDec, pTransform.video.decInfo.cnt_postDec, pTransform.video.decInfo.nTimes_needKeyFrame);
                str = "chkToForceKeyFrame: dst " + idInfo_dst.ui64Id + ", tn " + uiTranNo;
                //str = str + ", cnt_preDec " + pTransform.video.decInfo.cnt_preDec + ", cnt_postDec " + pTransform.video.decInfo.cnt_postDec + ", nTimes_needKeyFrame " + pTransform.video.decInfo.nTimes_needKeyFrame;
                Log.d( "qy",str);
                showInfo_open.qyFuncs.showInfo_open(str);
            }

//            byte[] data = taskProcReq.toByteArray();
            if (0 != postMsgTask2Mgr_mc(ctxCaller, pMisCnt, CONST_misMsgType_task, (byte) 0, cmdCode, tStartTran, uiTranNo, (short) 0, 0, 0, taskProcReq, (int) lenInBytes, pMsgTask.addr_logicalPeer.idInfo.ui64Id, 0, 0,
                    idInfo_dst.ui64Id, 0, (qnmCommProc_mis.MIS_MSGU) pTransform.tmp_pMsgU, false)) {
                showInfo_open.qyFuncs.showInfo_open("chkToForceKeyFrame failed, postMsgTask2Msg_mc failed");
                return iErr;
            }

            //
            iErr = 0;


            return iErr;

        }

        public static int M_nalType_h265(byte[] p) {
            return ((p)[2] == 1 ? ((p)[3] & 0x7e) : ((p)[4] & 0x7e)) >> 1;
        }

        //
        public static int dec_parseEncVPkt_i_useVideoQ2(int uiTransformType, Object pParent_transform, qnmCommProc_mis.PKT_info_toDec pPktInfo_toDec_param, DecMediaCodecCommon dm, qyCommProc_open.dec_parseEncVPkt_i_useVideoQ2_params decParseEncVPktIUseVideoQ2Params, qmcVideoCapture.SMPL_bsRead_param pParam) {
            int iErr = -1;
            ctxQmc.CCtxQmc pProcInfo = null;    //  QY_GET_procInfo_isCli(  );
            //TCHAR tBuf[256] = "";
            String str;


            //  myDRAW_VIDEO_DATA  *  pPkt  =  pTransform.video.pCurPkt;
            //myDRAW_VIDEO_DATA pPkt = (myDRAW_VIDEO_DATA)pPkt_myDRAW_VIDEO_DATA;
            qnmCommProc_mis.PKT_info_toDec pPktInfo_toDec = pPktInfo_toDec_param;
            {


                //  检查一下。每个包都只能处理一次，否则，容易死循环
//                if (0 == p_old_lPktId_getNewPkt) return -1;
                if (0 == pPktInfo_toDec.lPktId_getNewPkt) {
                    str ="failed: lPktId_alloc 0";
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    return -1;
                }
                if (pPktInfo_toDec.lPktId_getNewPkt == dm.m_old_lPktId) {
                    str = "failed: lPkdId_alloc==old";
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    return -1;
                }
                dm.m_old_lPktId = pPktInfo_toDec.lPktId_getNewPkt;

                //
                if (uiTransformType != CONST_uiTransformType_dec) {
                    //MACRO_qyAssert(0, ("dec_parseEncVPkt failed, transformType err"));
                    return -1;
                }
                qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pParent_transform;
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                pProcInfo = pQyMc.get_pProcInfo();// pTransform.pProcInfo;
                if (null == pProcInfo) return -1;
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

                ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;

                //  2015/06/18
                ctxQmcTmpl.QMC_status pQmcStatus = pProcInfo.status;

                //  2014/04/22
                //
                if (!pPktInfo_toDec.bGot_msg) return -1;
                if (!pPktInfo_toDec.info.bGot_pkt) return -1;


                //  2015/09/11
                qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pPktInfo_toDec.pMIS_MSGU;
//
                if (pMsg.uiType != CONST_misMsgType_input) {
                    str = "dec_parseEncVPkt_i failed, only input msg accepted";
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    return iErr;
                }
                qnmCommProc_mis.IM_CONTENTU pContent = ((qnmCommProc_mis.MIS_MSG_INPUT) pMsg).data.buf;//(qnmCommProc_mis.IM_CONTENTU) input.data.buf;
//                if (pContent.uiType != CONST_imCommType_transferVideoData) return iErr;

                //
                qnmCommProc_mis.TRANSFER_VIDEO_DATA pReq = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent;
//                qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead  pMem;
//                int i;
//                int iPos;

                //
//                iPos = pPktInfo_toDec.info.iPos;
//                if (pPktInfo_toDec.info.index_mems < 0 || pPktInfo_toDec.info.index_mems >= pReq.usCnt) return iErr;
//                i = pPktInfo_toDec.info.index_mems;
//
//                int bufSize = bufSize_TRANSFER_VIDEO_dataMemHead_mems;
                //
                qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem = decParseEncVPktIUseVideoQ2Params.getNextPktInfoToDecParamsV.head;// &pReq_mems[i];

                //
//                if (iPos + pMem.uiLen > pReq.uiLen) {
//
//					return iErr;
//                }
                if (0 == pMem.uiLen) {

                    return iErr;
                }

                //
                if (pMem.uiTranNo_openAvDev == 0) {
                    pMem.uiTranNo_openAvDev = pReq.uiTranNo_openAvDev;
                }
                if (pMem.idInfo.ui64Id == 0) {
                    qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                    pMem.idInfo.ui64Id = input.data.route.idInfo_from.ui64Id;
                }

                if (pTransform.video.decInfo.iNextIndex_preDec >= MAX_cnt_preDec_array) {
                    str = "dec_parseEncVPkt_i failed, iNextIndex_preDec too large";
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    return iErr;
                }

//                int iNextIndex_preDec = pTransform.video.decInfo.iNextIndex_preDec;
//                int iLastIndex_preDec = 0;
//                if (0 == iNextIndex_preDec) iLastIndex_preDec = MAX_cnt_preDec_array - 1;
//                else iLastIndex_preDec = iNextIndex_preDec - 1;

                if (0 == pMem.uiSampleTimeInMs) {
                    str = "Warning: dec_parseEncVPkt_i: uiSampleTimeInMS is 0";
                    Log.d(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                }

                //
                if (0 != pMem.uiSampleTimeInMs
                    //&& pTransform.video.decInfo.iSampleTimeInMs_preDec_array[iLastIndex_preDec] != pMem->uiSampleTimeInMs
                ) {       //  很多包是数据控制包，不是图像数据，时间戳和图像包一样，所以不能入队列。2014/07/11
                    //
                    //  
                    //  注意这里，使用decTool的时候，这里不校验pts, 让dvt来校验. 2015/02/09
                    //
                    if (!pTransform.video.bUseDecTool) {

                        //  2015/01/15
                        if (0 != pMem.uiPts) {
                            if (0 == pTransform.video.decInfo.pts.uiPts_lastRecvd) {
                                pTransform.video.decInfo.pts.uiPts_first = pMem.uiPts;
                                pTransform.video.decInfo.pts.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);
                            } else {
                                if (pMem.uiPts != pTransform.video.decInfo.pts.uiPts_lastRecvd + 1) {
                                    //
                                    str = String.format("dec_parseEncVPkt_i: {%s}: pts err, pkt {%s}, last {%s}", pMem.idInfo.ui64Id, pMem.uiPts, pTransform.video.decInfo.pts.uiPts_lastRecvd);
                                    showInfo_open.qyFuncs.showInfo_open(str);
                                    com.qycx.base.util.Log.e(str);
                                    //  2015/02/20
                                    return iErr;
                                }
                                //
                            }
                        }
                    }
                    //  2015/06/18
                    if (0 != pMem.uiPts) {
                        pTransform.video.decInfo.pts.uiPts_lastRecvd = pMem.uiPts;
                    }

                    //
                    //
                    int d_pre = 0;
                    int d_pretrans = 0;
                    int e_pretrans = 0;
                    //
                    if (pQmcCfg.debugStatusInfo.ucbShowPreDecVStatus) {

                        //  2015/02/24

                        //_sntprintf(tBuf, mycountof(tBuf), ("pts %d. sm %dms. d_pre %dms, i_pre %d. lDiff_pre_post %d. d_preTrans %d"), pPkt->head.uiPts, pPkt->head.uiSampleTimeInMs, d_pre, iNextIndex_preDec, pTransform.video.decInfo.lDiff_pre_post, d_pretrans);
                    }
//                    pTransform.video.decInfo.iSampleTimeInMs_preDec_array[iNextIndex_preDec] = pMem.uiSampleTimeInMs;
//                    pTransform.video.decInfo.uiPts_preDec_array[iNextIndex_preDec] = pMem.uiPts; //  2015/02/19
                    //  2015/02/24
                    //pTransform.video.decInfo.dwTickCnt_doPretrans[iNextIndex_preDec] = pPkt->debugInfo.dwTickCnt_doPretrans;
                    //
                    {
                        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                            ;
                            //TCHAR tName[128];
                            qyMcMainDaemon_isCli_mgr.TMP_tBuf128 tName = new qyMcMainDaemon_isCli_mgr.TMP_tBuf128();
                            int cntof_tName = cntof_tBuf128;

                            //_sntprintf(tName, mycountof(tName), ("%s%d"), CONST_mutexName_syncIndex_dec, pTransform.iTaskId);
                            str = CONST_mutexName_syncIndex_dec + pTransform.iTaskId;
                            tName.tBuf = str;//, cntof_tName);
                            if (0 != syncObj.sync(tName.tBuf, pMisCnt.hMutex_syncIndex_dec,"dec_parseEncVPkt_i_useVideoQ2.328"))
                                return iErr;

                            pTransform.video.decInfo.iNextIndex_preDec++;
                            if (pTransform.video.decInfo.iNextIndex_preDec >= MAX_cnt_preDec_array) {
                                pTransform.video.decInfo.iNextIndex_preDec = pTransform.video.decInfo.iNextIndex_preDec % MAX_cnt_preDec_array;
                            }
                            if (pTransform.video.decInfo.lDiff_pre_post < MAX_cnt_preDec_array) {
                                pTransform.video.decInfo.lDiff_pre_post++;
                            }
                        } catch (Exception e) {
                            Log.e(e.getMessage());
                            return -1;
                        }
                    }
                    //
                    pTransform.video.decInfo.cnt_preDec++;
                    //
                    if (pQmcCfg.debugStatusInfo.ucbShowPreDecVStatus) {  //  2015/02/24
                        //_sntprintf(tBuf, mycountof(tBuf), ("%s, cnt_pre %d, e_pretrans %d. transQ %d+%d"), tBuf, pTransform.video.decInfo.cnt_preDec, e_pretrans, pTransform.video.q2.qs[0].uiQNodes, pTransform.video.q2.qs[1].uiQNodes);
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, ("preDec"), "", ("%s"), tBuf);
                    }
                }

                //byte[] encData = pPkt->memory.m_pBuf + pPkt->uiSize_dec + pPkt->uiOffset_enc;
//                pReq.buf.position(iPos);
                byte[] encData = decParseEncVPktIUseVideoQ2Params.getNextPktInfoToDecParamsV.onePkt.array();// pReq.buf + iPos;

                if (uiTransformType == CONST_uiTransformType_dec) {
                    pTransform = (qmcTransform.QY_TRANSFORM) pParent_transform;

                    taskAv.PROC_TASK_AV pTask = (taskAv.PROC_TASK_AV) pTransform.pTask;
                    if (null == pTask) return iErr;
                    int index_activeMems_from = pTransform.get_index_activeMems_from_decV();//.index_activeMems_from;

                    com.qycx.base.ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
                    //mymemset((byte[])ctxCaller, 0, sizeof(CTX_caller));
                    ctxCaller.type = CONST_ctxCallerType_thread;
                    ctxCaller.dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
                    ctxCaller.bTmp = true;

                    if (pTransform.video.ucbInited) {
                        if (pTransform.video.index_activeMems_from != index_activeMems_from
                                || pTransform.video.idInfo.ui64Id != pMem.idInfo.ui64Id
                                || pTransform.video.tv.uiTranNo_openAvDev != pMem.uiTranNo_openAvDev
                                || pTransform.video.status.ucbDecFailed                                            //  2012/03/19
                        ) {
                            //  2014/04/07
                            pTransform.video.status.ucbNeedReinited = true;
                            //
                            str = "dec_parseEncVPkt_i failed: trans.activeMem " + pTransform.video.index_activeMems_from
                                    + ",id " + pTransform.video.idInfo.ui64Id
                                    + ",tn " + pTransform.video.tv.uiTranNo_openAvDev
                                    + ",decFailed " + pTransform.video.status.ucbDecFailed
                                    + "!= " + index_activeMems_from + "," + pMem.idInfo.ui64Id + "," + pMem.uiTranNo_openAvDev + "," + 0;
                            showInfo_open.qyFuncs.showInfo_open(0, null,str);
                            return iErr;
                        }

                    }

                    // fixme to ..
                    int iFourcc_enc = pTransform.video.tv.vh_stream.dwFourcc;
                    if (iFourcc_enc == CONST_fourcc_HEVC) {

                        //
                        int nalType = M_nalType_h265(encData);
                        //
                        //showInfo_open.qyFuncs.showInfo_open("nalType.. : " + nalType);


                        if (!pTransform.video.decInfo.bGot_keyFrame) {
                            boolean ucbKeyFrame = false;
                            //
                            switch (nalType) {
                                case NAL_IDR_W_RADL:
                                case NAL_IDR_N_LP:
                                    ucbKeyFrame = true;
                                    break;
                                case NAL_VPS:
                                    ucbKeyFrame = true;
                                    break;
                                default: {
                                    int ii = 0;
                                }
                                break;
                            }
                            //
                            if (ucbKeyFrame) {
                                showInfo_open.qyFuncs.showInfo_open("got keyFrame..");
                            }
                            if (!ucbKeyFrame) {
                                //
                                str = ("dec_parseEncVPkt_i failed, %I64u, %d, not a keyFrame: need a keyFrame first") +
                                        pTransform.video.idInfo.ui64Id + " " + pTransform.video.tv.uiTranNo_openAvDev;
                                Log.d( str);
                                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                chkToForceKeyFrame(null, pTransform);
                                //
                                iErr = CONST_qyRet_needKeyFrameFirst;
                                //
                                return iErr;
                            }
                            pTransform.video.decInfo.bGot_keyFrame = true;
                        }
                    }
                }

                //
                decParseEncVPktIUseVideoQ2Params.pInput = encData;
                decParseEncVPktIUseVideoQ2Params.uiInputSize = pMem.uiLen;// Pkt->uiLen_enc;
                //  2015/01/28
                decParseEncVPktIUseVideoQ2Params.uiSampleTimeInMs = pMem.uiSampleTimeInMs;// Pkt->head.uiSampleTimeInMs;
                //  2015/02/09
                decParseEncVPktIUseVideoQ2Params.uiPts = pMem.uiPts;// pPkt->head.uiPts;
                //  2015/02/24
                if (null != pParam) {
                    //pParam->dwTickCnt_doPretrans = pPkt->debugInfo.dwTickCnt_doPretrans;
                }

            }

            iErr = 0;


            return iErr;
        }


    }
}

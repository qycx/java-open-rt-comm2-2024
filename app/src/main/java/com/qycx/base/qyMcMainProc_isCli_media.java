package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_talking;
import static com.qycx.base.Consts.CONST_qyRet_cantGet_index_activeMems_from;
import static com.qycx.base.ctxCaller.Consts.CONST_ctxCallerType_thread;
import static com.qycx.base.dlgProgress.qyFuncs.notifyProgressEnd;
import static com.qycx.base.dyn_isCliD3d.qyFuncs.dyn_fillData_d3d_setDataReady;
import static com.qycx.base.findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc;
import static com.qycx.base.genericQueue.qyFuncs.qGetMsg;
import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_remoteAssistReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_taskProcReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAudioData;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAvReplyInfo;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferVideoData;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc;
import static com.qycx.base.qisChannel.qyFuncs.startChannel;
import static com.qycx.base.qisChannel.qyFuncs.startThread_toSend;
import static com.qycx.base.qisChannel.qyFuncs.waitForChannel;
import static com.qycx.base.qmcFunc_isCli.qyFuncs.msg2TaskRcd;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapAudioBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapBmpBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcVideoFunc.qyFuncs.getSharedObjUsr;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendMedia;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_accept;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_send_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_applyForChkChannels_qmc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_applyForPlayer_qmc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputTask;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.MACRO_makeImSimpleResp;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.bChannelOk;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_resp;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_ok;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_unknown;
import static com.qycx.base.qyComm.qyFuncs.isRcOk;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgResp;
import static com.qycx.base.qyCommProc.qyFuncs.bTaskAlive;
import static com.qycx.base.qyCommProc.qyFuncs.waitForThread_toSend;
import static com.qycx.base.qyCommProc_open.Consts.CONST_pktResType_sharedTex;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_shareDynBmp;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_shareGps;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_viewDynBmp;
import static com.qycx.base.qyDefs.Consts.CONST_qyTimeLen;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_err_cantGet_index_activeMems_from;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_err_missingOrgReq;
import static com.qycx.base.qyDefs_open.Consts.CONST_imTaskStatus_resp;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.getIdInfo_logicalPeer;
import static com.qycx.base.qyMcMainProc_isCli_mgr.Consts.CONST_nIntervalInMs_tryToResumeVideoTask;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.procMsgInput_other_req;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.procMsgInput_other_resp;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_msgArrive;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;
import static com.qycx.base.qyq2.qyFuncs.isQ2Empty;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.qyq2.qyFuncs.q2TriggerToProcess;
import static com.qycx.base.showTaskStatusFunc.qyFuncs.showTaskStatus;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.mySleep;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;
import static com.qycx.base.traceLog.qyFuncs.getTimelStr;

import com.qycx.base.util.Log;

public class qyMcMainProc_isCli_media {

    public static class Consts {
        //  2015/09/14
        public final static int CONST_pMsgs_media = 10;
    }


    public static class qyFuncs {
        public static int drawVideoData(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.myDRAW_VIDEO_DATA[] pkts, int pktsLen, boolean pbPktsRedirected,
                                        qmcTransform.QY_TRANSFORM pTransform) {
            int iErr = -1;
            if (null == pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;

            if (null == pTransform) return -1;
            int iIndex_player = pTransform.iIndex_player;


            int i;
            int nPkts = 0;
//            nPkts = pktsLen / sizeof(myDRAW_VIDEO_DATA);
//            if (0 == nPkts
//                    || 0 != (pktsLen % sizeof(myDRAW_VIDEO_DATA))) {
//                return iErr;
//            }

            //  2015/09/06
            long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);

            //
            for (i = 0; i < nPkts; i++) {

                qnmCommProc_mis.myDRAW_VIDEO_DATA p= pkts[i];
                int len;

                int iSampleTimeInMs = (int) p.head.uiSampleTimeInMs;
                qyCommProc_open.BITMAPINFOHEADER pBmpInfo_input = (qyCommProc_open.BITMAPINFOHEADER) p.bih;
                byte[] pInput =  p.memory.m_pBuf;
                int inputLen = (int) p.bih.biSizeImage;

                if (inputLen > pProcInfo.cfg.uiBufSize_myDrawVideoData) {
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("drawVideoData failed: inputLen %d is too big"), inputLen);
                    return iErr;
                }
                if (inputLen != pBmpInfo_input.biSizeImage) {
                    return iErr;
                }

                //  len  =  offsetof(  myDRAW_VIDEO_DATA,  buf  )  +  pBmpInfo_input->bmiHeader.biSizeImage;
                len=128;//sizeof(myDRAW_VIDEO_DATA);

                //  
                p.head.uiTranNo_openAvDev = pTransform.video.tv.uiTranNo_openAvDev;   //  2015/10/01	//  Player->video.uiTranNo;	//  2014/11/26
                //
                //  p->uiCapType  =  0;
                //  p->iIndex_capBmp  =  0;
                //
                p.iTaskId = pTransform.iTaskId;   //  2015/10/01.	//  pPlayer->iTaskId;

                //
                p.head.uiSampleTimeInMs = (int) iSampleTimeInMs;
                //  p->head.uiSampleTimeInMs_org  =  iSampleTimeInMs_org;

                //  2015/09/06
                p.debugInfo.dwTickCnt_drawVideoData = dwTickCnt;
            }

            qmcCommFunc_is_open.QY_PLAYER pPlayer = null;//new qmcCommFunc_is_open.QY_PLAYER();
            pPlayer = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, iIndex_player);
            if (null == pPlayer) return iErr;

            if (pQyMc.env.bDebugInfo && pProcInfo.cfg.debugStatusInfo.ucbViewAudioStatus) {
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("drawVideoData : me, %d,%d plays"), pPlayer->video.uiTranNo, 0);
            }


            //  这里转发的是图像数据的地址，在mcThreadProc_playVideo和其他处理里，要注意这是地址，并且，在接力棒的最后一棒，要注意释放掉。
            if (false == pbPktsRedirected || pbPktsRedirected) {
                return iErr;
            }
            //
            if (pPlayer.video.m_var.status.bNoMsgAndWait) {
                if (0 != q2PostMsgAndTrigger(pkts, pPlayer.video.q2,"drawVideoData.153"))
                    return iErr;
            } else {
               // if (0 != q2PostMsg(pkts, (int) pktsLen, pPlayer.video.q2)) return iErr;
            }
            //  pkt redirected
            pbPktsRedirected = true;

            iErr = 0;



            return iErr;
        }


        //
        public static int postToDraw(ctxQmc.CCtxQmc pProcInfo, int uiTranNo, qnmCommProc_mis.myDRAW_VIDEO_DATA pPkt, int pktLen,
                                     boolean pbPktRedirected, int uiCapType, int iIndex_capBmp, qyDefs.QY_MESSENGER_ID pIdInfo_sender,
                                     qmCommFunc_mis.MIS_CNT pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo_peer, int iTaskId, Object hWnd_task,
                                     qnmCommProc_mis.PLAYER_ID pPlayerId) {
            int iErr = -1;
            //if (!pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;

            boolean bDbg = false;
            String str;

            //  2016/02/18
            if (null == pPlayerId) return -1;
            int index_player = pPlayerId.index_player;
            //
            if (index_player < 0 || index_player >= pProcInfo.av.usCnt_players) return -1;
            qmcCommFunc_is_open.QY_PLAYER pPlayer = pProcInfo.av.pPlayers[index_player];
            qmcCommFunc_is_open.PLAY_VIDEO_procInfo pPlay = pPlayer.video;

            //  2015/10/01
            int usIndex = pPkt.usIndex_activeMems_from;


            //  CQyQ2Help					q2Help;
            Object hWnd_display = null;

            int iSampleTimeInMs = (int) pPkt.head.uiSampleTimeInMs;
            //int				uiPts						=	pPkt->head.uiPts;		//  2015/02/19
            //
            if (pPkt.usPktResType != CONST_pktResType_sharedTex) {
                if (null == pPkt.memory.m_pBuf) {
                    showInfo_open.qyFuncs.showInfo_open(0, ("postToDraw"), (" failed, pkt.pBuf is null"));
                    return -1;
                }
            }
            //
            qyCommProc_open.BITMAPINFOHEADER pBmpInfo_input = (qyCommProc_open.BITMAPINFOHEADER) pPkt.bih;

            if (pPlay.m_var.status.fillD3d.bFilled_d3dData) {
                dyn_fillData_d3d_setDataReady(pPkt, index_player);
            }

            //  2013/10/04	
            boolean bD3dOnly = false;

            if (!bD3dOnly) {   //  2013/10/04

                //
                qnmCommProc_mis.MIS_MSG_procVideo_qmc msg = new qnmCommProc_mis.MIS_MSG_procVideo_qmc();
                //mymemset((byte[])msg, 0, sizeof(MIS_MSG_procVideo_qmc));
                msg.uiType = qnmCommProc_mis.Consts.CONST_misMsgType_procVideo_qmc;
                //  msg.ucbLocalAv  =  true;
                msg.uiCapType = uiCapType;
                msg.iIndex_capProcInfo = iIndex_capBmp;

                //  2009/04/25
                //if (pIdInfo_sender!=null) 
                msg.idInfo_sender.ui64Id = pIdInfo_sender.ui64Id;
                //  2014/09/04
                //msg.dynBmp.usIndex_obj  =  pPkt->decInfo.dynBmp.usIndex_obj;
                //
                msg.uiTranNo_openAvDev = uiTranNo;
                msg.usIndex = usIndex;

                //
                msg.iTaskId = iTaskId;
                //msg.hWnd_task = hWnd_task;      //  2009/09/14
                //  2016/12/27
                //msg.uiEventId_lastRecvd  =  uiEventId_lastRecvd;

                //
                //if (pMisCnt) msg.pMisCnt = pMisCnt;
                if (null != pIdInfo_peer) msg.idInfo_peer.ui64Id = pIdInfo_peer.ui64Id;

                //qyMemoryFunc.qyFuncs.mymemcpy((byte[]) msg.bih, pBmpInfo_input, (int) sizeof(qyCommProc_open.BITMAPINFOHEADER));

                if (pPkt.usPktResType != CONST_pktResType_sharedTex) {
                    if (pPkt.uiSize_dec < msg.bih.biSizeImage) {
                        return iErr;
                    }
                }
                //  
                //  memcpy(  msg.memory,  pPkt->memory,  sizeof(  msg.memory  )  );
                //qyMemoryFunc.qyFuncs.mymemcpy((byte[]) msg.pkt, pPkt, (int) sizeof(myDRAW_VIDEO_DATA));

                //
                //msg.playerId.index_player  =  index_player;		//  2009/10/03
                msg.playerId = pPlayerId;                      //  2016/02/19
//
//                if (null == pbPktRedirected || pbPktRedirected) {
//                    Log.i( "postToDraw: pbPktRedirected error");
//                    return iErr;
//                }

                boolean bSkip = false;
                if (!bSkip) {
                    int nQ2Nodes = getQ2Nodes(pPlay.displayQ2);
                    if (nQ2Nodes > 10) {
                        bSkip = true;
                        str = String.format("postToDraw: skip the packet if nQNodes %s>10", nQ2Nodes);

                    }
                }
//
//                if (!bSkip) {
//                    //
//                    if (0 == q2PostMsgAndTrigger((byte[]) msg, (int) sizeof(MIS_MSG_procVideo_qmc), pPlay.displayQ2)) {
//                        pbPktRedirected = true;
//                    }
//                }

            }

            //

            int dwTgt = (int) tmpFunc.qyFuncs.mytimeGetTime();    //  pPkt->debugInfo.dwTickCnt_postToDraw;	//  timeGetTime(  );
            //
            if (0 != pPlay.m_var.status.dwLastTgt_postToDraw) {
                pPlay.m_var.status.nInterval_real_postToDraw = (int) (dwTgt - pPlay.m_var.status.dwLastTgt_postToDraw);
            }
            pPlay.m_var.status.dwLastTgt_postToDraw = (int) dwTgt;
            int uiPts_last = pPlay.m_var.status.uiPts_last;
            pPlay.m_var.status.uiPts_last = pPkt.head.uiPts;   //  2015/02/19
            //

            //  2015/09/10
            //
            int nIntervalInMs_fillD3dData = (int) (pPlay.m_var.status.fillD3d.param0.nIntervalInMs_fillData + pPlay.m_var.status.fillD3d.param1.nIntervalInMs_fillData);
            //
            //  2015/09/14
            int nQNodes_left = getQ2Nodes(pPlay.q2);
            //
            /**/
            //
            if (pProcInfo.cfg.debugStatusInfo.ucbShowToDrawStatus) {
            }


            //  2013/09/09
            if (pPlay.m_var.status.fillD3d.bFilled_d3dData) {
                if (0 != pPlay.m_var.lowLatency.dwToInMs_toPostDraw)
                    pPlay.m_var.status.ucSkipped_for_d3dData = 0;
                else {
                    pPlay.m_var.status.ucSkipped_for_d3dData = (byte) (nIntervalInMs_fillD3dData / 16);
                    if (0 != pPlay.m_var.status.ucSkipped_for_d3dData)
                        pPlay.m_var.status.ucSkipped_for_d3dData--;
                }
            }
            //
            int tmp_player_usFps = pPlay.usFps;
            if (0 == tmp_player_usFps) tmp_player_usFps = (int) pPlay.m_var.status.fpsInfo.fps_real;
            if (0 == tmp_player_usFps) tmp_player_usFps = 1;
            //TCHAR* tWhere = pPlay.m_var.tWhere;
            {
                //	
                final int CONST_nIntervalInMs_toAdjust_mql_good = 1000; //  1000	//  1500	//  5000
                //final static int 		CONST_nIntervalInMs_toAdjust_mql_worst						3000	//  1000	//  1500	//  5000

                //  2015/03/02											
                final int M_xql_ok = 25;    //  3
                final int M_mql_big_ok = 6;
                final int M_mql_ok = 3;
                final int M_xql_ok_sharedTex = 50;  //  3
                final int M_mql_big_ok_sharedTex = 4;
                final int M_mql_ok_sharedTex = 2;
                final int MIN_mql_ok_sharedTex = 3;
                final int M_xql_ok_15fps = 10;  //  25	//  10	//  1
                final int M_mql_big_ok_15fps = 4;   //  6
                final int M_mql_ok_15fps = 2;   //  1

                int tmp_xql_ok = M_xql_ok_15fps;
                int tmp_mql_big_ok = M_mql_big_ok_15fps;
                int tmp_mql_ok = M_mql_ok_15fps;
                if (tmp_player_usFps > 15) {
                    if (pPkt.usPktResType == CONST_pktResType_sharedTex) {
                    } else {
                        tmp_xql_ok = M_xql_ok * tmp_player_usFps / 25;
                        tmp_mql_big_ok = M_mql_big_ok * tmp_player_usFps / 25;
                        tmp_mql_ok = M_mql_ok * tmp_player_usFps / 25;
                    }
                }
                //

                //
                //  2015/03/12. 对本地视频，为了尽可能快一点，所以要求最低残余包数应到0
                if (pPlayer.idInfo_recorder.ui64Id == 0) {
                    if (!pPkt.input.bRtsp) {               //  2015/03/12. 只有纯本地采集卡才能降到0.
                        tmp_xql_ok = 0;
                        tmp_mql_big_ok = 0;
                        tmp_mql_ok = 0;
                    }
                }

                //  2015/09/16
                if (pPlay.m_var.lowLatency.mql.xql_ok != tmp_xql_ok) {
                    pPlay.m_var.lowLatency.mql.xql_ok = tmp_xql_ok;
                }
                if (pPlay.m_var.lowLatency.mql.mql_ok != tmp_mql_ok) {
                    pPlay.m_var.lowLatency.mql.mql_ok = tmp_mql_ok;
                }
                if (pPlay.m_var.lowLatency.mql.mql_big_ok != tmp_mql_big_ok) {
                    pPlay.m_var.lowLatency.mql.mql_big_ok = tmp_mql_big_ok;
                }

                //  2015/09/15
                int nIntervalInMs_toAdjust_mql = CONST_nIntervalInMs_toAdjust_mql_good;


                //  2015/02/25		
                int dwCurTgt = (int) dwTgt;
                if (Math.abs((dwCurTgt - pPlay.m_var.lowLatency.mql.dwTgt_start1)) > nIntervalInMs_toAdjust_mql) {
                    //  2015/09/15
                    //_sntprintf(  tBuf_other,  mycountof(  tBuf_other  ),  (  "INT_ADJ %d. "  ),  nIntervalInMs_toAdjust_mql  /  1000  );
                    //
                    if (pPlay.m_var.lowLatency.mql.mql > tmp_mql_ok) {
                        pPlay.m_var.lowLatency.mql.bNeedReset = true;
                        //				
                        //_sntprintf(tBuf_adjust, mycountof(tBuf_adjust), ("Adj: mql > mql_ok %d."), tmp_mql_ok);
                    }
                    //
                    pPlay.m_var.lowLatency.mql.last_xql = pPlay.m_var.lowLatency.mql.xql;
                    pPlay.m_var.lowLatency.mql.last_mql = pPlay.m_var.lowLatency.mql.mql;
                    //			
                    pPlay.m_var.lowLatency.mql.mql = nQNodes_left;
                    pPlay.m_var.lowLatency.mql.xql = nQNodes_left;
                    pPlay.m_var.lowLatency.mql.dwTgt_start1 = dwCurTgt;
                    //

                } else {
                    if (nQNodes_left < pPlay.m_var.lowLatency.mql.mql) {
                        pPlay.m_var.lowLatency.mql.mql = nQNodes_left;
                    }
                    if (nQNodes_left > pPlay.m_var.lowLatency.mql.xql) {
                        pPlay.m_var.lowLatency.mql.xql = nQNodes_left;
                    }
                    //				
                    if (pPlay.m_var.lowLatency.mql.bNeedReset) {
                        if (nQNodes_left <= tmp_mql_big_ok) {
                            pPlay.m_var.lowLatency.mql.bNeedReset = false;
                            //
                            //_sntprintf(tBuf_adjust, mycountof(tBuf_adjust), ("Adj ok: ql <= mql_big_ok %d."), tmp_mql_big_ok);
                        }
                    }
                    //
                    if (nQNodes_left > tmp_xql_ok) {
                        pPlay.m_var.lowLatency.mql.bNeedReset = true;
                        //				
                        //_sntprintf(tBuf_adjust, mycountof(tBuf_adjust), ("Adj: ql > xql_ok %d."), tmp_xql_ok);
                    }

                }
            }

            //

            if (pProcInfo.cfg.debugStatusInfo.ucbShowToDrawStatus) {
                /**/
            }

            //
            //  2016/04/10
            if (pPlay.m_var.lowLatency.bNeedAdjust) {
                int nDelay = pPlay.m_var.lowLatency.nDelayInMs_lt_mql;
                if (nDelay > 0) {
                    nDelay = Math.min(nDelay, 20);
                    if (0 != pPlay.m_var.lowLatency.calc.dwTgt_start) {
                        pPlay.m_var.lowLatency.calc.dwTgt_start += (int) nDelay;
                        //pPlay.m_var.status.dwLastTgt_postToDraw  +=  nDelay;
                    }
                }
                int nAh = pPlay.m_var.lowLatency.nAheadInMs + pPlay.m_var.lowLatency.nAheadInMs_gt_mql;
                if (nAh > 0) {
                    //
                    //
                    nAh = Math.min(nAh, 100);
                    //
                    if (pPlay.m_var.lowLatency.calc.dwTgt_start > nAh) {
                        pPlay.m_var.lowLatency.calc.dwTgt_start -= (int) nAh;
                        //pPlay.m_var.status.dwLastTgt_postToDraw  -=  nAh;
                    }
                }
            }


            //
            //  2016/04/10	
            if (pPlay.m_var.lowLatency.bAccurate) {
                if (0 == pPlay.m_var.lowLatency.calc.dwTgt_start
                        || pPkt.head.uiPts != uiPts_last + 1) {
                    pPlay.m_var.lowLatency.calc.dwTgt_start = (int) dwTgt;
                    pPlay.m_var.lowLatency.calc.uiPts_start = pPkt.head.uiPts;

                    //
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.str_who_showInfo, 0, pPlay.m_var.tWhere, "", ("playVideo: calc_start reset"));
                }
            }


            //
            iErr = 0;




            return iErr;
        }


        //
        public static int getBih_capBmpToPlay(qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp, qyCommProc_open.BITMAPINFOHEADER pBih) {
            if (null == pCapBmp || null == pBih) return -1;

            //if  (  !pCapBmp->common.compressVideo.bVideoCompressInited  )  return  -1;
            //
            if (pCapBmp.compressVideo.ucbResizeByEncoder) {
                videoFunc_open.qyFuncs.makeBmpInfoHeader_rgb(24, pCapBmp.vh_org.bih.biWidth, pCapBmp.vh_org.bih.biHeight, pBih);
            } else {
                //memcpy(pBih,pCapBmp->common.compressVideo.vh_decompress.bih, sizeof(pBih[0]));
                pBih = pCapBmp.compressVideo.vh_decompress.bih;
            }
            return 0;
        }


        //
        public static int drawLocalVideoData(ctxQmc.CCtxQmc pProcInfo, int uiTranNo, qnmCommProc_mis.myDRAW_VIDEO_DATA[] pkts, int pktsLen, boolean pbPktsRedirected, int uiCapType, int iIndex_capBmp, int iTaskId, qmCommFunc_mis.MIS_CNT pMisCnt, qyDefs.QY_MESSENGER_ID pIdInfo_peer) {
            int iErr = -1;
            if (null == pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

            if (null == pkts || 0 == pktsLen) return -1;
            int nPkts = 0;//pktsLen / sizeof(myDRAW_VIDEO_DATA);
//            if (0 == nPkts) return -1;

            qyCommProc_open.BITMAPINFOHEADER pBmpInfo_input = pkts[0].bih;

//            return -1;


            //
            if (uiTranNo == 0) {
                //Log.e("drawLocalVideoData faild, uiTranNo is 0");
                return -1;
            }
            if (0 == pBmpInfo_input.biWidth
                    || 0 == pBmpInfo_input.biHeight
                    || 0 == pBmpInfo_input.biSizeImage) {
                return -1;
            }

            qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp = null;
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
            qmcSharedObj.CAP_procInfo_audioCommon pCapAudio = null;

            pCapBmp = getCapBmpBySth(pProcInfo, iIndex_capBmp, 0);
            if (null == pCapBmp) return iErr;
            pSharedObj = getSharedObjByIndex(pProcInfo, pCapBmp.iIndex_sharedObj);
            if (null == pSharedObj) return iErr;
            pCapAudio = getCapAudioBySth(pProcInfo, pSharedObj.var.iIndex_capAudio, 0);


            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = getSharedObjUsr(pSharedObj, pSharedObj.var.iIndex_curUsr);
            if (null == pSharedObjUsr) return iErr;

            qmcCommFunc_is_open.QY_PLAYER pPlayer = null;

            pPlayer = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, pSharedObj.var.iIndex_localPlayer);
            if (pPlayer != null) {
                if (pPlayer.idInfo_recorder.ui64Id != 0
                        || pPlayer.video.uiTranNo != uiTranNo) {
                    Log.i( "drawLocalVideoData: not correct player");
                    pSharedObj.var.iIndex_localPlayer = -1;
                    pPlayer = null;
                }
            }
            if (null == pPlayer) {
                int k;

                for (k = 0; k < pProcInfo.av.usCnt_players; k++) {
                    if (pProcInfo.av.pPlayers[k].idInfo_recorder.ui64Id == 0
                            && pProcInfo.av.pPlayers[k].video.uiTranNo == uiTranNo) {
                        if (null == pCapAudio) break;

                        //  2010/09/04
                        if (pProcInfo.av.pPlayers[k].idInfo_recorder.ui64Id == 0
                                && pProcInfo.av.pPlayers[k].audio.uiTranNo == pSharedObjUsr.uiTranNo_openAvDev_a) {
                            break;
                        }
                    }
                }

                if (k < pProcInfo.av.usCnt_players) {
                    pSharedObj.var.iIndex_localPlayer = k;
                    pPlayer = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, k);
                    if (null == pPlayer) return iErr;
                }
            }

            if (null == pPlayer || !pPlayer.video.bRunning) {

                //  2014/12/10
                long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
                if (dwTickCnt - pSharedObjUsr.dwLastTickCnt_applyForPlayer < 200) {
                    return iErr;
                }
                pSharedObjUsr.dwLastTickCnt_applyForPlayer = dwTickCnt;

                //
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("drawLocalVideoData : me,%d,%d apply for player. index_capBmp %d"), uiTranNo, 0, iIndex_capBmp);

                //  ÕâÀï×é°ü·¢¸øÖ÷Ïß³Ì¡£
                qnmCommProc_mis.MIS_MSG_applyForPlayer_qmc msg=new qnmCommProc_mis.MIS_MSG_applyForPlayer_qmc();
                //mymemset((byte[])msg, 0, sizeof(MIS_MSG_applyForPlayer_qmc));
                msg.uiType = CONST_misMsgType_applyForPlayer_qmc;
                msg.index_sharedObj = pCapBmp.iIndex_sharedObj;
                msg.iTaskId = iTaskId;
                msg.video.uiTranNo = uiTranNo;
                //
                //  memcpy(  msg.video.vh.bih,  pBmpInfo_input,  sizeof(  msg.video.vh.bih  )  );
                if (0 != getBih_capBmpToPlay(pCapBmp, msg.video.vh.bih)) return iErr;
                //
                //msg.video.ucbFlippedImg  =  pCapBmp->common.compressVideo.compressor.common.ucbOutputFlippedImg;	//  2011/12/11
                //
                msg.video.usFps = pCapBmp.compressVideo.compressor.common.head.usMaxFps_toShareBmp;  //  2015/02/20

                //
                if (null != pCapAudio) {
                    //  msg.audio.uiTranNo  =  pCapAudio->common.compressAudio.uiTranNo_openAvDev;
                    msg.audio.uiTranNo = pSharedObjUsr.uiTranNo_openAvDev_a;                           //  2010/09/04
                    //memcpy(msg.audio.ah,pCapAudio->common.compressAudio.ah_decompress, sizeof(msg.audio.ah));
                    msg.audio.ah = pCapAudio.compressAudio.ah_decompress;
                    ;
                    //
                    //memcpy(msg.audio.playCfg,pCapAudio->common.compressAudio.compressor.playCfg, sizeof(msg.audio.playCfg)); //  2012/01/03
                    msg.audio.playCfg = pCapAudio.compressAudio.compressor.playCfg; //  2012/01/03
                }

//                qPostMsg((byte[]) msg, (int) sizeof(MIS_MSG_applyForPlayer_qmc), pQyMc.gui.processQ);

                guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                iErr = 0;
                return iErr;
            }

            if (pQyMc.env.bDebugInfo && pProcInfo.cfg.debugStatusInfo.ucbViewAudioStatus) {
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("drawLocalVideoData : me, %d,%d plays"), uiTranNo, 0);
            }

            int i;
            for (i = 0; i < nPkts; i++) {

                qnmCommProc_mis.myDRAW_VIDEO_DATA p = pkts[i];

                p.head.uiTranNo_openAvDev = uiTranNo;
                p.uiCapType = uiCapType;
                p.iIndex_capBmp = iIndex_capBmp;
                p.iTaskId = iTaskId;

                if (p.head.uiLen < pBmpInfo_input.biSizeImage) {
                    Log.i( "drawLocalVideoData: uiLen error");
                    return iErr;
                }

                pPlayer.video.m_var.uiSampleTimeInMs_newRecvd = p.head.uiSampleTimeInMs;
                pPlayer.video.m_var.dwTickCnt_uiSampleTimeInMs_newRecvd = tmpFunc.qyFuncs.myGetTickCount(null);

                if (0 != p.head.uiSampleTimeInMs) {
                    if (p.head.uiSampleTimeInMs < pPlayer.video.m_var.uiSampleTimeInMs) {
                        //Log.e("drawLocalVideoData: pkt.uiSampleTimeInMs %d < player.%d. trigger to empty old data", p->head.uiSampleTimeInMs, pPlayer.video.m_var.uiSampleTimeInMs);
                        q2TriggerToProcess(pPlayer.video.q2);
                    }
                    if (0 != pPlayer.video.m_var.uiSampleTimeInMs) {
                        if (p.head.uiSampleTimeInMs > pPlayer.video.m_var.uiSampleTimeInMs + 10000) {   //  2009/05/25. too old audio data
                            //Log.e("drawLocalVideoData: pkt.uiSampleTimeInMs %d > player.%d  +  10000. trigger to empty old data", p->head.uiSampleTimeInMs, pPlayer.video.m_var.uiSampleTimeInMs);
                            q2TriggerToProcess(pPlayer.video.q2);
                        }
                    }
                }
            }

            //  这里转发的是图像数据的地址，在mcThreadProc_playVideo和其他处理里，要注意这是地址，并且，在接力棒的最后一棒，要注意释放掉。
            if (false == pbPktsRedirected || pbPktsRedirected) {
                Log.i( "drawLocalVideoData: pbPktRedirected error");
                return iErr;
            }
            //
            if (pPlayer.video.m_var.status.bNoMsgAndWait) {
                if (0 != q2PostMsgAndTrigger(pkts, pPlayer.video.q2,"drawLocalVideoData.658")) {
                    //Log.e("drawLocalVideoData: q2PostMsgAndTrigger failed");
                    return iErr;
                }
            } else {
//                if (0 != q2PostMsg(pkts, (int) pktsLen, pPlayer.video.q2)) {
//                    //Log.e("drawLocalVideoData: q2PostMsg failed");
//                    return iErr;
//                }
            }
            //
            pbPktsRedirected = true;

            iErr = 0;



            //  if  (  pData_allocated  )  free(  pData_allocated  );

            return iErr;
        }

        public static int procMsgTask_mc_media(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_TASK pMsg, int lenInBytes_msg, boolean bMsgInput, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            //
            //
            int dataLen = 0;
            int uiMsgLen = 0;
            qnmCommProc_mis.IM_CONTENTU pContent = (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            int tmpiRet;
            //
            int lenInBytes = 0;
            qnmCommProc_mis.IM_SIMPLE_RESP respSimple = new qnmCommProc_mis.IM_SIMPLE_RESP();
            short usRespCode = CONST_qyRc_unknown;
            int uiChannelType = CONST_channelType_talking;
            //char timeBuf[CONST_qyTimeLen + 1] = "";
            qyMcMainDaemon_isCli.TMP_timeBuf timeBuf = new qyMcMainDaemon_isCli.TMP_timeBuf();
            //
            boolean bRemoveIfErr = true;
            //
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;

            if (null == pQyMc.gui.hMainWnd) return -1;
            if (null == pMisCnt) return -1;

            if (pMsg.addr_logicalPeer.idInfo.ui64Id == 0) {
                if (0 != getIdInfo_logicalPeer(pMisCnt, pMsg.data.route, pMsg.addr_logicalPeer)) {
                    switch (pMsg.uiTaskType) {
                        case CONST_imTaskType_shareDynBmp:
                        case CONST_imTaskType_shareGps:
                            break;
                        default: {
                            Log.e("procMsgTask_mc_media: getIdInfo_logicalPeer failed.");
                            return iErr;
                        }
                    }
                }
            }


            if (!isUcFlgResp(pMsg.ucFlg)) {

                if (!bMsgInput && pMsg.usCode == CONST_qyCmd_sendMedia) {

                    switch (pContent.uiType) {
                        case CONST_imCommType_transferVideoData: {

                            bRemoveIfErr = false;

                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("send transferVideoData"));

                            qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, param)) <= 0) {
                                Log.e("procMsgTask_mc_media: ´¦ÀíCONST_imCommType_transferVideoData %s¡£" +( tmpiRet != 0 ? "failed" : "not found"));
                                return iErr;
                            }
                        }
                        iErr = 0;
                        return iErr;

                        case CONST_imCommType_transferAudioData: {

                            bRemoveIfErr = false;

                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("send transferAudioData"));
                            qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, param)) <= 0) {
                                Log.e("procMsgTask_mc_media: ´¦ÀíCONST_imCommType_transferAudioData %s¡£"+  (tmpiRet != 0 ? "failed" : "not found"));
                                return iErr;
                            }
                        }
                        iErr = 0;
                        return iErr;
                        case CONST_imCommType_taskProcReq:
                            switch (((qnmCommProc_mis.TASK_PROC_REQ)pContent).usOp) {
                                case CONST_imOp_send_cancel:
                                case CONST_imOp_recv_cancel:
                                    if (null == pCtx.pProcessQ) return iErr;
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            return iErr;
                    }

                    if (pMsg.data.route.idInfo_to.ui64Id == 0) {
                        switch (pMsg.uiTaskType) {
                            case CONST_imTaskType_shareDynBmp:
                            case CONST_imTaskType_shareGps:
                                //
                                iErr = 0;
                                return iErr;
                            default:
                                break;
                        }
                        Log.i( "procMsgTask_mc_media failed, idInfo_to is 0");
                        return iErr;
                    }
                    qnmCommProc_mis.MSG_ROUTE msgRoute = new qnmCommProc_mis.MSG_ROUTE();
                    int tmp_len  = 10;
                    if (0 != postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, msgRoute, pMsg.uiType, pMsg.ucFlg, pMsg.usCode, pMsg.tStartTime, pMsg.uiTranNo, pMsg.usSeqNo, pMsg.data.buf,
                            pMsg.lenInBytes - tmp_len, pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.data.route.idInfo_to.ui64Id, 0, null, false))
                        return iErr;

                    iErr = 0;
                    return iErr;

                }

                if (bMsgInput && pMsg.usCode == CONST_qyCmd_sendMedia) {
                    pMsg.uiChannelType = pChannel.uiType;
                    switch (pContent.uiType) {
                        case imCommType_defs.Consts.CONST_imCommType_transferAvInfo: {
                            Object pDb = null;
                            try(qnmDb.CQnmDb db = new qnmDb.CQnmDb())
                            {
                                usRespCode = CONST_qyRc_unknown;
                                {
                                    qnmCommProc_mis.IM_TASK_RCD pRcd = new qnmCommProc_mis.IM_TASK_RCD();
                                    if (null == pCtx.pProcessQ) return iErr;

                                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                                    pMsg.uiTaskType = transferAvInfo.uiTaskType;
                                    pMsg.iStatus = CONST_imTaskStatus_resp;

                                    if (transferAvInfo.uiTaskType == CONST_imTaskType_viewDynBmp) {
                                        if (0 > qPostMsg(pMsg, pQyMc.gui.processQ)) {
                                            Log.e("TRANSFER_AV_INFO qPostMsg failed");
                                            return iErr;
                                        }
                                        guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                        iErr = 0;
                                        return iErr;
                                    }

                                    //  2014/11/21. 这里要把代理的信息更新到起始任务里。
                                    if (transferAvInfo.proxiedTranInfo.idInfo.ui64Id ==0 && 0 != transferAvInfo.proxiedTranInfo.uiObjType) {
                                        if (transferAvInfo.proxiedTranInfo.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {

                                            qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                                            param.bNeedUpdate = true;
                                            tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, param);
                                            
                                            iErr = 0;
                                            return iErr;
                                        }
                                    }

                                    //
                                    if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys))
                                    {
                                        Log.e("getAvailableDb == null");
                                        return iErr;
                                    }
                                    pDb = db.m_pDbMem.pDb;

                                    if (null == msg2TaskRcd(pMisCnt, pMsg, usRespCode, pRcd)) {
                                        Log.e("msg2TaskRcd failed");
                                        return iErr;
                                    }
                                    tmpiRet = storeTaskInDb.qyFuncs.storeTaskInDb(pDb, pQyMc.cfg.db.iDbType, pMisCnt, 0, pRcd, false, "procMgTask_mc_media kk");
                                    if (tmpiRet < 0) {
                                        return iErr;
                                    }
                                    //  //  2008/06/04
                                    if (tmpiRet > 0) {   //  rcd.iStatus
                                        qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                                        tmpiRet = pCtx.pProcessQ.qTraverse_mono(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, param);
                                        if (tmpiRet < 0) return iErr;
                                        if (tmpiRet > 0) {
                                            iErr = 0;
                                            return iErr;
                                        }
                                        //
                                        if (pRcd.iStatus != pMsg.iStatus && !bTaskAlive(pRcd.iStatus)) {
                                            if (0 != myDb.QM_dbFuncs.pf_updateTaskStatus.call(pDb, pMsg.iStatus, pRcd.id))
                                                return iErr;
                                        }
                                    }

                                    //  2007/07/01
                                    pMsg.iTaskId = pRcd.id;
                                    pMsg.uiTaskMode_unused = pRcd.uiMode;

                                    if (0 > qPostMsg(pMsg, pQyMc.gui.processQ)) {
                                        Log.e("qPostMsg failed");
                                        return iErr;
                                    }
                                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                    iErr = 0;
                                    return iErr;
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                                return iErr;
                            }
                        }
                        case CONST_imCommType_transferAvReplyInfo: {
                            Object pDb = null;
                            try(qnmDb.CQnmDb db = new qnmDb.CQnmDb())
                            {
                                usRespCode = CONST_qyRc_unknown;

                                if (null == pCtx.pProcessQ) return iErr;

                                if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                                pDb = db.m_pDbMem.pDb;
                                qnmCommProc_mis.TRANSFER_AV_replyInfo transferAvReplyInfo = (qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent;
                                if (transferAvReplyInfo.usOp != CONST_imOp_recv_accept) {
                                    Log.i( "procMsgTask_mc_media, usSp is not accept");
                                    return iErr;
                                }

                                qyDefs.QY_MESSENGER_ID idInfo = pMisCnt.idInfo;
                                if (!myDb.QM_dbFuncs.pf_bGetImTaskRcdBySth.call(pDb, pQyMc.cfg.db.iDbType, false, "", idInfo, transferAvReplyInfo.tStartTime_org, transferAvReplyInfo.uiTranNo_org, 0, null)) {
                                    Log.e("pf_bGetImTaskRcdBySth failed");
                                    return iErr;
                                }

                                if (pCtx.pProcessQ.qTraverse_mono(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0) <= 0) {
                                    Log.e( "procMsgTask_mc_media failed: ");
                                    return iErr;
                                }

                                //  2013/02/13
                                if (0 != transferAvReplyInfo.uiTranNo_org) {
                                    if (transferAvReplyInfo.uiTranNo_org == pProcInfo.status.progress.uiTranNo) {
                                        notifyProgressEnd(transferAvReplyInfo.uiTranNo_org, 0);
                                    }
                                }

                                startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferAvReplyInfo"));

                                usRespCode = CONST_qyRc_ok;

                                MACRO_makeImSimpleResp(pMsg.usCode, pContent.uiType, 0, respSimple);
                                int len = 14;//(uint)sizeof(IM_SIMPLE_RESP)
                                postMsgTask2Mgr_mc(pCtx.ctxCaller, pMisCnt, CONST_misMsgType_outputTask, (byte) CONST_commFlg_resp, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType,  respSimple, len, pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.idInfo_taskSender.ui64Id, pMsg.idInfo_taskSender.ui64Id, pMsg.data.route.idInfo_from.ui64Id, pChannel.uiType, (qnmCommProc_mis.MIS_MSGU) null, false);

                                iErr = 0;
                                return iErr;
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                                return iErr;
                            }
                        }
                        case CONST_imCommType_transferVideoData: {

                            bRemoveIfErr = false;

                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferVideoData"));

                            //  2009/05/12
                            pMsg.uiChannelType = pChannel.uiType;

                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                                getTimelStr(pMsg.tStartTime,  timeBuf.timeBuf, CONST_qyTimeLen + 1);
                                Log.e(  "procMsgTask_mc_media failed: video data error"  );

                                if (tmpFunc.qyFuncs.myGetTickCount64(null) - pCtx.dwTickCnt_tryToResumeVideoConference > CONST_nIntervalInMs_tryToResumeVideoTask) {
                                    pCtx.dwTickCnt_tryToResumeVideoConference = tmpFunc.qyFuncs.myGetTickCount64(null);
                                   Log.d("procMsgTask_mc_media: %I64u, videoData err, try to find orgReq.");
                                    qnmCommProc_mis.TRANSFER_VIDEO_DATA transferVideoData = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent;
                                    showTaskStatus(pMisCnt, pMsg.addr_logicalPeer.idInfo, pMsg.data.route.idInfo_from, null, false, pMsg.usCode, 0, transferVideoData.uiTranNo_openAvDev, pContent.uiType, 0, CONST_imTaskStatus_err_missingOrgReq);
                                }
                                return iErr;
                            }
                            iErr = 0;
                            return iErr;
                        }
                        case CONST_imCommType_transferAudioData: {
                            bRemoveIfErr = false;

                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferAudioData"));

                            //  2009/05/12
                            pMsg.uiChannelType = pChannel.uiType;

                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                                getTimelStr(pMsg.tStartTime, timeBuf.timeBuf, CONST_qyTimeLen + 1);
                                Log.e(  "procMsgTask_mc_media failed: audio data error"  );

                                if (tmpFunc.qyFuncs.myGetTickCount64(null) - pCtx.dwTickCnt_tryToResumeVideoConference > 5000) {
                                    pCtx.dwTickCnt_tryToResumeVideoConference = tmpFunc.qyFuncs.myGetTickCount64(null);
                                    Log.d( "Not finished: procMsgTask_mc_media: audioData err, shall send a request to retrieve original avInfo");

                                    qnmCommProc_mis.TRANSFER_AUDIO_DATA transferAudioData = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent;
                                    showTaskStatus(pMisCnt, pMsg.addr_logicalPeer.idInfo, pMsg.data.route.idInfo_from, null, false, pMsg.usCode, 0, transferAudioData.uiTranNo_openAvDev, pContent.uiType, 0, CONST_imTaskStatus_err_missingOrgReq);
                                }
                                return iErr;
                            }
                            iErr = 0;
                            return iErr;
                        }
                        case CONST_imCommType_remoteAssistReq:
                            bRemoveIfErr = false;
                            if (null == pCtx.pProcessQ) return iErr;
                            startChannel(pProcInfo, pMisCnt, pChannel, ("recv remoteAssistReq"));
                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                                getTimelStr(pMsg.tStartTime, timeBuf.timeBuf, CONST_qyTimeLen + 1);
                                return iErr;
                            }

                            iErr = 0;
                            return iErr;

                        default:
                            Log.e("procMsgTask_mc_media failed, bInput, pContent->uiType is " + pContent.uiType);
                            return iErr;
                    }
                }
                return iErr;
            } else {
                if (!isRcOk(pMsg.usCode)) return iErr;
            }
            iErr = 0;
            return iErr;
        }


        //
        public static int procMsgInput_mc_media(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_INPUT pMsg, int lenInBytes_msg, boolean bMsgInput, ctxMcThread.CTX_MC_thread pCtx, qmCommFunc_mis.MIS_CHANNEL pChannel) {
            int iErr = -1;
            if(pMsg.uiType != CONST_misMsgType_input) return iErr;

            //
            long tick_before  =  myGetTickCount(null);
            String str;

            //
            qnmCommProc_mis.IM_CONTENTU pContent = pMsg.data.buf;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            int tmpiRet;
            //
            qnmCommProc_mis.IM_SIMPLE_RESP respSimple = new qnmCommProc_mis.IM_SIMPLE_RESP();
            short usRespCode = CONST_qyRc_unknown;
            int uiChannelType = CONST_channelType_talking;
            //char timeBuf[CONST_qyTimeLen + 1] = "";
            qyMcMainDaemon_isCli.TMP_timeBuf timeBuf = new qyMcMainDaemon_isCli.TMP_timeBuf();
            //
            boolean bRemoveIfErr = true;
            //
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            if (null == pDbFuncs) return -1;
            myDb.QM_dbFuncs g_dbFuncs = pDbFuncs;

            if (null == pQyMc.gui.hMainWnd) return -1;
            if (null == pMisCnt) return -1;

            if (pMsg.addr_logicalPeer.idInfo.ui64Id == 0) {
                if (0 != getIdInfo_logicalPeer(pMisCnt, pMsg.data.route, pMsg.addr_logicalPeer)) {
                    switch (pMsg.uiTaskType) {
                        case CONST_imTaskType_shareDynBmp:
                        case CONST_imTaskType_shareGps:
                            break;
                        default: {
                            return iErr;
                        }
                    }
                }
            }


            //
            if (!isUcFlgResp(pMsg.ucFlg)) {

                if (!bMsgInput && pMsg.usCode == CONST_qyCmd_sendMedia) {

                    switch (pContent.uiType) {
                        case CONST_imCommType_transferVideoData: {
                            bRemoveIfErr = false;
                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("send transferVideoData"));

                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                                Log.e("procMsgTask_mc_media: CONST_imCommType_transferVideoData " +( tmpiRet < 0 ? "failed" : "not found"));
                                return iErr;
                            }
                            iErr = 0;
                            return iErr;
                        }
                        case CONST_imCommType_transferAudioData: {
                            bRemoveIfErr = false;
                            if (null == pCtx.pProcessQ) return iErr;
                            startChannel(pProcInfo, pMisCnt, pChannel, ("send transferAudioData"));
                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                                Log.e("procMsgTask_mc_media: ´¦ÀíCONST_imCommType_transferAudioData "+ ( tmpiRet < 0 ? "failed" : "not found"));
                                return iErr;
                            }
                            iErr = 0;
                            return iErr;
                        }
                        case CONST_imCommType_taskProcReq:
                            switch (((qnmCommProc_mis.TASK_PROC_REQ)pContent).usOp) {
                                case CONST_imOp_send_cancel:
                                case CONST_imOp_recv_cancel:
                                    if (null == pCtx.pProcessQ) return iErr;

                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            return iErr;
                    }

                    if (pMsg.data.route.idInfo_to.ui64Id == 0) {
                        switch (pMsg.uiTaskType) {
                            case CONST_imTaskType_shareDynBmp:
                            case CONST_imTaskType_shareGps:
                                //
                                iErr = 0;
                                return iErr;
                            default:
                                break;
                        }
                        Log.e( "procMsgTask_mc_media failed, idInfo_to is 0");
                        return iErr;
                    }

                    qnmCommProc_mis.MSG_ROUTE msgRoute = new qnmCommProc_mis.MSG_ROUTE();
                    if (0 != postMsg2Mgr_mc(pCtx.ctxCaller, pMisCnt, msgRoute, pMsg.uiType, pMsg.ucFlg, pMsg.usCode, pMsg.tStartTime, pMsg.uiTranNo, pMsg.usSeqNo, pMsg.data.buf,pMsg.lenInBytes, pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.data.route.idInfo_to.ui64Id, 0, null, false))
                        return iErr;

                    iErr = 0;
                    return iErr;
                }


                //
                if (bMsgInput && pMsg.usCode == CONST_qyCmd_sendMedia) {
                    pMsg.uiChannelType = pChannel.uiType;
                    switch (pContent.uiType) {
                        case imCommType_defs.Consts.CONST_imCommType_transferAvInfo: {
                            Object pDb = null;
                            try(qnmDb.CQnmDb db = new qnmDb.CQnmDb())                            {
                                usRespCode = CONST_qyRc_unknown;
                                {
                                    qnmCommProc_mis.IM_TASK_RCD pRcd = new qnmCommProc_mis.IM_TASK_RCD();
                                    if (null == pCtx.pProcessQ) return iErr;

                                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                                    pMsg.uiTaskType = transferAvInfo.uiTaskType;
                                    pMsg.iStatus = CONST_imTaskStatus_resp;

                                    //  2009/09/07
                                    if (transferAvInfo.uiTaskType == CONST_imTaskType_viewDynBmp) {

                                        if (0 != qPostMsg(pMsg, pQyMc.gui.processQ)) {
                                            return iErr;
                                        }
                                        guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                        iErr = 0;
                                        return iErr;
                                    }

                                    //  2014/11/21. 这里要把代理的信息更新到起始任务里。
                                    if (transferAvInfo.proxiedTranInfo.idInfo.ui64Id ==0 && 0 != transferAvInfo.proxiedTranInfo.uiObjType) {
                                        if (transferAvInfo.proxiedTranInfo.idInfo.ui64Id == pMisCnt.idInfo.ui64Id) {

                                            qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                                            param.bNeedUpdate = true;
                                            tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, param);

                                            iErr = 0;
                                            return iErr;
                                        }
                                    }

                                    //
                                    if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys))
                                        return iErr;
                                    pDb = db.m_pDbMem.pDb;

                                    //
                                    if (null == msg2TaskRcd(pMisCnt, pMsg, usRespCode, pRcd)) {
                                        return iErr;
                                    }
                                    tmpiRet = storeTaskInDb.qyFuncs.storeTaskInDb(pDb, pQyMc.cfg.db.iDbType, pMisCnt, 0, pRcd, false, "procMsgInput_mc_media");
                                    if (tmpiRet < 0) {
                                        return iErr;
                                    }
                                    //  //  2008/06/04
                                    if (tmpiRet > 0) {   //  rcd.iStatus
                                        tmpiRet = pCtx.pProcessQ.qTraverse_mono(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0);
                                        if (tmpiRet < 0) return iErr;
                                        if (tmpiRet > 0) {
                                            iErr = 0;
                                            return iErr;
                                        }
                                        //
                                        if (pRcd.iStatus != pMsg.iStatus && !bTaskAlive(pRcd.iStatus)) {
                                            if (0 != g_dbFuncs.pf_updateTaskStatus.call(pDb, pMsg.iStatus, pRcd.id))
                                                return iErr;
                                        }

                                    }

                                    //  2007/07/01
                                    pMsg.iTaskId = pRcd.id;
                                    pMsg.uiTaskMode_unused = pRcd.uiMode;

                                    //
                                    if (0 != qPostMsg(pMsg, pQyMc.gui.processQ)) {
                                        return iErr;
                                    }
                                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);

                                    iErr = 0;
                                    return iErr;
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                                return iErr;
                            }
                        }
                        case CONST_imCommType_transferAvReplyInfo: {
                            Object pDb = null;
                            try(qnmDb.CQnmDb db = new qnmDb.CQnmDb())
                            {
                                usRespCode = CONST_qyRc_unknown;

                                if (null == pCtx.pProcessQ) return iErr;

                                if (null == db.getAvailableDb(pQyMc.iDsnIndex_mainSys)) return iErr;
                                pDb = db.m_pDbMem.pDb;

                                qnmCommProc_mis.TRANSFER_AV_replyInfo transferAvReplyInfo = (qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent;
                                if (transferAvReplyInfo.usOp != CONST_imOp_recv_accept) {
                                    Log.e( "procMsgTask_mc_media, usSp is not accept");
                                    return iErr;
                                }
                                qyDefs.QY_MESSENGER_ID idInfo = pMisCnt.idInfo;
                                if (!myDb.QM_dbFuncs.pf_bGetImTaskRcdBySth.call(pDb, pQyMc.cfg.db.iDbType, false, "", idInfo, transferAvReplyInfo.tStartTime_org, transferAvReplyInfo.uiTranNo_org, 0, null)) {
                                    return iErr;
                                }

                                if (pCtx.pProcessQ.qTraverse_mono(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0) <= 0) {
                                    Log.i( "procMsgTask_mc_media failed: ");
                                    return iErr;
                                }
                                
                                if (0 != transferAvReplyInfo.uiTranNo_org) {
                                    if (transferAvReplyInfo.uiTranNo_org == pProcInfo.status.progress.uiTranNo) {
                                        notifyProgressEnd(transferAvReplyInfo.uiTranNo_org, 0);
                                    }
                                }

                                startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferAvReplyInfo"));

                                usRespCode = CONST_qyRc_ok;

                                MACRO_makeImSimpleResp(pMsg.usCode, pContent.uiType, 0, respSimple);

                                int len = 0;
                                postMsgTask2Mgr_mc(pCtx.ctxCaller, pMisCnt, CONST_misMsgType_outputTask, (byte) CONST_commFlg_resp, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0, pMsg.iTaskId, pMsg.uiTaskType,  respSimple,len, pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.idInfo_taskSender.ui64Id, pMsg.idInfo_taskSender.ui64Id, pMsg.data.route.idInfo_from.ui64Id, pChannel.uiType, null, false);

                                iErr = 0;
                                return iErr;
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                return iErr;
                            }
                        }
                        case CONST_imCommType_transferVideoData: {

                            bRemoveIfErr = false;           //  2008/05/14, avProcInfo

                            if (null == pCtx.pProcessQ) return iErr;

                            //
                            if  (  false  ) {
                                long dwTickCnt = myGetTickCount(null);
                                str = "procMsgInput_mc_media: tvd: bef startChannel ";
                                if (pChannel.hThread_io == null) str += "thread_io is null";
                                else str += "thread_io not null";
                                long iE = dwTickCnt - pChannel.status.dwTickCnt_lastRecvd;
                                str += " e_recvd " + iE + ".";
                                if (bChannelOk(pChannel,false)) str += " ch ok";
                                else str += " ch bad";
                                str += " ioQuit " + pChannel.status.ulbIoQuit1;
                                str += " ioStep " + pChannel.status.iIoStep;
                                showInfo_open.qyFuncs.showInfo_open(str);
                            }
                            //
                            startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferVideoData"));


                            //
                            long tick_beforeFind = myGetTickCount(null);


                            //  2009/05/12
                            pMsg.uiChannelType = pChannel.uiType;
                            qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, param)) <= 0) {



                                //
                                Log.d("qy","procMsgInput_mc_media: tvd, findOrgMsgAndProc failed");

                                //
                                if  (  false  ) {
                                    //
                                    showInfo_open.qyFuncs.showInfo_open(0,0,test.tag_100k,"procMsgInput_mc_media: tvd, findOrgMsgAndProc failed");

                                    //test.testCls_Droid.testPrintTasks(false,  "",  "procMsgInput_mc_media");

                                        //
                                    qnmCommProc_mis.TRANSFER_VIDEO_DATA tvd = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent;
                                    str="tvd: "  +  pMsg.data.route.idInfo_from.ui64Id  +  " "  +  tvd.uiTranNo_openAvDev  +  ":";
                                    int  ii;
                                    for  (  ii  =  0;  ii  <  tvd.usCnt;ii++)  {
                                        qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead mem  =  tvd.mems[ii];
                                         str +=  " mem"  +  ii  +  ":"  +  mem.idInfo.ui64Id  +  " tn "  +  mem.uiTranNo_openAvDev;
                                    }
                                    //
                                    Log.d("qy",str);
                                    showInfo_open.qyFuncs.showInfo_open(0,0,test.tag_100k,str);
                                }



                                //
                                getTimelStr(pMsg.tStartTime, timeBuf.timeBuf.array(), CONST_qyTimeLen + 1);

                                {
                                    //
                                    if (tmpFunc.qyFuncs.myGetTickCount(null) - pCtx.dwTickCnt_tryToResumeVideoConference > CONST_nIntervalInMs_tryToResumeVideoTask) {
                                        pCtx.dwTickCnt_tryToResumeVideoConference = tmpFunc.qyFuncs.myGetTickCount64(null);

//                                        String str = String.format("procMsgInput_mc_media.2456: "+pMsg.data.route.idInfo_from.ui64Id+", videoData err, try to find orgReq.");
//                                        Log.i(str);
                                        if (logCountG < 10) {
                                            ++logCountG;
                                        } else {
                                            logCountG = 0;
                                        }

                                        int iStatus = CONST_imTaskStatus_err_missingOrgReq;

                                        if (tmpiRet == CONST_qyRet_cantGet_index_activeMems_from) {
                                            iStatus = CONST_imTaskStatus_err_cantGet_index_activeMems_from;
                                        }

                                        showTaskStatus(pMisCnt, pMsg.addr_logicalPeer.idInfo, pMsg.data.route.idInfo_from, null, false, pMsg.usCode, 0, ((qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent).uiTranNo_openAvDev, pContent.uiType, 0, iStatus);
                                        iErr = 0;
                                    }
                                }

                                return iErr;
                            }

                            //
                            if (false) {
                                long tick_afterFind = myGetTickCount(null);
                                str = "rtMediaQ size " + getQ2Nodes(pProcInfo.realTimeMediaQ2) + " find costs " + (tick_afterFind - tick_beforeFind);
                                showInfo_open.qyFuncs.showInfo_open(str);
                            }



                            //
                            iErr = 0;
                            return iErr;

                        }

                        case CONST_imCommType_transferAudioData: {

                            bRemoveIfErr = false;           //  2008/05/14, avProcInfo

                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("recv transferAudioData"));

                            //
                            //long tick_afterChannel  =  myGetTickCount(null);
                            pCtx.ctxCaller.tick_before=tick_before;

                            //  2009/05/12
                            pMsg.uiChannelType = pChannel.uiType;
                            qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, param)) <= 0) {
                                getTimelStr(pMsg.tStartTime, timeBuf.timeBuf, CONST_qyTimeLen + 1);
//                                Log.e(  "procMsgTask_mc_media failed: audio data error"  );

                                //  2009/10/17
                                {
                                    if (tmpFunc.qyFuncs.myGetTickCount64(null) - pCtx.dwTickCnt_tryToResumeVideoConference > 2000) {
                                        pCtx.dwTickCnt_tryToResumeVideoConference = tmpFunc.qyFuncs.myGetTickCount64(null);

                                        //
                                        str = String.format("procMsgTask_mc_media: audioData err, shall send a request to retrieve original avInfo. conf%d, from %d", pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.data.route.idInfo_from.ui64Id);
                                        Log.e("qy",str);

                                        //
                                        int iStatus = CONST_imTaskStatus_err_missingOrgReq;
                                        //
                                        if (tmpiRet == CONST_qyRet_cantGet_index_activeMems_from) {
                                            iStatus = CONST_imTaskStatus_err_cantGet_index_activeMems_from;
                                        }

                                        showTaskStatus(pMisCnt, pMsg.addr_logicalPeer.idInfo, pMsg.data.route.idInfo_from, null, false, pMsg.usCode, 0, ((qnmCommProc_mis.TRANSFER_AUDIO_DATA)pContent).uiTranNo_openAvDev, pContent.uiType, 0, iStatus);
                                    }
                                }
                                //
                                //
                                return iErr;      //  ÀíÓÉÍ¬ÉÏ
                            }

                            //
                            //
                            if  (  false  ) {
                                long tick_after = myGetTickCount(null);
                                str = "rtMediaQ size " + getQ2Nodes(pProcInfo.realTimeMediaQ2) + " kkk costs " + (tick_after - tick_before);
                                //str += " afterChannel costs " + (tick_afterChannel - tick_before);
                                showInfo_open.qyFuncs.showInfo_open(str);
                            }

                            //
                            iErr = 0;
                            return iErr;

                        }

                        case CONST_imCommType_remoteAssistReq:

                            bRemoveIfErr = false;

                            if (null == pCtx.pProcessQ) return iErr;

                            startChannel(pProcInfo, pMisCnt, pChannel, ("recv remoteAssistReq"));

                            if ((tmpiRet = pCtx.pProcessQ.qTraverse(pCtx.ctxCaller, tmpHandler_findOrgMsgAndProc, pMsg, 0)) <= 0) {
                                getTimelStr(pMsg.tStartTime, timeBuf.timeBuf, CONST_qyTimeLen + 1);

                                return iErr;      //  ÀíÓÉÍ¬ÉÏ
                            }

                            iErr = 0;
                            return iErr;

                        default:
                            Log.e("procMsgTask_mc_media failed, bInput, pContent->uiType is "+ pContent.uiType);
                            return iErr;
                    }
                }
                return iErr;
            } else {
                if (!isRcOk(pMsg.usCode)) return iErr;
            }

            iErr = 0;
            return iErr;
        }

        static int logCountG = 0;


        //
        public static int doMediaMsg(ctxQyMc.CCtxQyMc pQyMc, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSGU pMsg, int msgLen, int uiChannelType, ctxMcThread.CTX_MC_thread pCtx) {
            int iErr = -1;
            int len = msgLen;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;


            switch (pMsg.uiType) {
                case CONST_misMsgType_input: {
                    //MIS_CNT					*		pMisCnt							=  (  MIS_CNT  *  )pMsg->input.pMisCnt;
                    qmCommFunc_mis.MIS_CHANNEL pChannel = null;

                    //if  (  !pMisCnt  )  break;
                    //  if  (  !(  pChannel  =  getChannelByType(  pMisCnt,  pMsg->input.uiChannelType  )  )  )  break;
                    if (null == (pChannel = getChannelByType(pMisCnt, uiChannelType))) break;
                    qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                    if (isUcFlgResp(input.ucFlg)) {
                        if (0 != procMsgInput_other_resp(pQyMc, pProcInfo, input, (int) len, pCtx, pChannel))
                            break;
                    } else {
                        if (0 != procMsgInput_other_req(pQyMc, pProcInfo, input, (int) len, pCtx, pChannel))
                            break;
                    }

                    //
                }
                break;
                case CONST_misMsgType_talk:
                    break;
                case CONST_misMsgType_task: {
                    //MIS_CNT pMisCnt = (MIS_CNT)pMsg->task.pMisCnt;
                    qmCommFunc_mis.MIS_CHANNEL pChannel = null;

                    //if (!pMisCnt) break;
                    if (null == (pChannel = getChannelByType(pMisCnt, uiChannelType))) break;

                    if (0 != procMsgTask_mc_media(pQyMc, pProcInfo, (qnmCommProc_mis.MIS_MSG_TASK) pMsg, (int) len, false, pCtx, pChannel))
                        break;

                    //  //printMisMsg(  0,  0,  pMsg  );

                }
                break;
                case CONST_misMsgType_applyForChkChannels_qmc: {
                    //MIS_CNT pMisCnt = (MIS_CNT)pMsg->applyForChkChannels_qmc.pMisCnt;
                    if (null == pMisCnt) break;
                    startChannel(pProcInfo, pMisCnt, getChannelByType(pMisCnt, uiChannelType), ("media: applyForChannels"));

                }
                break;
                case CONST_misMsgType_outputTask:
                default:
                    break;
            }


            iErr = 0;


            return iErr;
        }


        //  2015/09/13
        public static Runnable mcThreadProc_mis_media = () -> {

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            int loopCtrl = 0;
            int i;
            int dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            int uiChannelType = CONST_channelType_media;

            //
            String str;
            String where_showInfo = "videoMgr";

            //
            pProcInfo.dwThreadId_media = tmpFunc.qyFuncs.myGetCurrentThreadId();

            //

            qnmCommProc_mis.MIS_MSGU msgs[] = null;


            //qnmCommProc_mis.MIS_MSGU  pMsgs[CONST_pMsgs_media];
//                byte[] pMsgs = new byte[Consts.CONST_pMsgs_media];//[];

            //int msgLens[CONST_pMsgs_media];
            int[] msgLens = new int[Consts.CONST_pMsgs_media];//[];
            qnmCommProc_mis.MIS_MSGU pMsg = null;

            //
            int maxMsgs = Consts.CONST_pMsgs_media;            //  一次最多处理的包数。
            int nMsgs;

            //
            int dwRet;
            //  long								lPrev;
            //
            ctxMcThread.CTX_MC_thread ctx = new ctxMcThread.CTX_MC_thread();

            ctx.ctxCaller.type = CONST_ctxCallerType_thread;
            ctx.ctxCaller.dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            ctx.ctxCaller.tNameBuf.tNameBuf= "thread_media";



            //
            //  CWnd						*		pMainWnd						=	AfxGetApp(  )->m_pMainWnd;
            //
            qyq2.QY_Q2 pTriggerQ2 = pProcInfo.mediaQ2;

            //
            str = ("mcThreadProc_mis_media:  enters");
            showInfo_open.qyFuncs.showInfo_open(0, where_showInfo, str);

            //memset(ctx, 0, sizeof(ctx));
            ctx.pProcessQ = pProcInfo.processQ_media;
            ctx.pMsgBuf_forInput = new qnmCommProc_mis.CMIS_MSGU();
            ctx.pMsgBuf =new qnmCommProc_mis.CMIS_MSGU();
            msgs=new qnmCommProc_mis.MIS_MSGU[maxMsgs];

            for (; !pQyMc.bQuit; loopCtrl++) {

                //  if  (  !pMainWnd  )  pMainWnd					=	AfxGetApp(  )->m_pMainWnd;
                if (null == pQyMc.gui.hMainWnd) {
                    mySleep(1000);
                    continue;
                }

                //dwRet = WaitForSingleObject(pTriggerQ2->hEvents[0], 5000);
                //if (dwRet == WAIT_FAILED) return iErr;
                myWaitOne(pTriggerQ2.hEvent0, 5000);

                for (; !pQyMc.bQuit; ) {
                    if (isQ2Empty(pTriggerQ2)) break;
                    for (i = 0; i < maxMsgs; i++) {
                        msgLens[i] =128;//sizeof(qnmCommProc_mis.MIS_MSGU);
                    }
                    nMsgs = maxMsgs;
                    //  len  =  sizeof(  pMsg[0]  );
                    {
                        try //(qyq2.CQyQ2Help q2Help = new qyq2.CQyQ2Help())
                        {
                            genericQueue.GENERIC_Q pQ_toGetMsg  =  pTriggerQ2.qs0;
//                                    {
//                                    if (0 != qGetMsg(q2Help.getQ_toGetMsg(pTriggerQ2), (long[]) pMsgs, (int[]) msgLens, (int) nMsgs))
                            if (null == (pMsg = (qnmCommProc_mis.MIS_MSGU) qGetMsg(pQ_toGetMsg)))
                                break;
                            //
                            //Log.d(com.qycx.base.Consts.VIDEO_DOWNLOAD,"2 mediaQ2.qs0 " + pProcInfo.mediaQ2.qs0.uiQNodes+" mediaQ2.qs1 " + pProcInfo.mediaQ2.qs0.uiQNodes);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
//                                    }
                        nMsgs = 1;
                    }

                    //
                    for (i = 0; i < nMsgs; i++) {
                        //mymemset((((byte[])(pMsgs[i])) + msgLens[i]), 0, (int)Math.min(256, sizeof(qnmCommProc_mis.MIS_MSGU) - msgLens[i]));
                    }

                    if (0 == nMsgs) continue;

                    for (i = 0; i < nMsgs; i++) {
//                                    qnmCommProc_mis.MIS_MSGU pMsg =null;// (qnmCommProc_mis.MIS_MSGU) pMsgs[i];
                        int msgLen = (int) msgLens[i];

                        // todo
//                                    if (pMsg != null)
                            doMediaMsg(pQyMc, pProcInfo, pMsg, 0, uiChannelType, ctx);

                    }
                }

                //
                if  (  false  )  {
                    str = "mediaQ size " + getQ2Nodes(pProcInfo.mediaQ2) ;
                    showInfo_open.qyFuncs.showInfo_open(str);
                }

                //
                if (0 == (loopCtrl % 5)) {
                    waitForChannel(pProcInfo, pMisCnt, uiChannelType, false);
                }

                //
                //
                {
                    qmCommFunc_mis.MIS_CHANNEL pChannel=getChannelByType(pMisCnt,uiChannelType);
                    if (pChannel.hThread_toSend != null) {
                        waitForThread_toSend(pChannel, false);
                    }
                    if (pChannel.hThread_toSend == null) {
                        startThread_toSend(pProcInfo, pMisCnt, pChannel, "mcThreadProc_media.1632");
                    }
                }

                //
                continue;
            }

            //
            waitForChannel(pProcInfo, pMisCnt, uiChannelType, true);
            //
            {
                qmCommFunc_mis.MIS_CHANNEL pChannel=getChannelByType(pMisCnt,uiChannelType);

                if  (  pChannel.hThread_toSend!=null) {
                    waitForThread_toSend(pChannel, true);
                    if  (  pChannel.hThread_toSend!=null) {
                        showInfo_open.qyFuncs.showInfo_open("Err: video.toSend not closed");
                    }
                    else  {
                          showInfo_open.qyFuncs.showInfo_open("video.toSend closed");
                    }
                }
                else  {
                      showInfo_open.qyFuncs.showInfo_open("video.toSend already closed");
                }
            }
            //
            pProcInfo.status.threadsStatus.ulbMediaQuit = true;
            Log.d("mcThreadProc_mis_media:  leaves");
        };
    }
}

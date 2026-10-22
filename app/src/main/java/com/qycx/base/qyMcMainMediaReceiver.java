


package com.qycx.base;

import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.ctxQmcTmpl.Consts.CONST_fn_dump_mp3_dec;
import static com.qycx.base.ctxQyMc.Consts.*;
import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.imCommType_defs.Consts.*;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc;
import static com.qycx.base.postVPktToSave.qyFuncs.postAPktToSave;
import static com.qycx.base.qmcCommFunc_mis.qyFuncs.MACRO_getnWaitingInMs_audio_syncRecv;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapAudioBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapBmpBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_audioCompressors_acm;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_audioCompressors_dmo;
import static com.qycx.base.qmcVideoFunc.qyFuncs.getSharedObjUsr;
import static com.qycx.base.qnmCommProc_mis.Consts.*;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_resp;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_routeTalkData;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_unknown;
import static com.qycx.base.qyCommProc_open.Consts.*;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_viewDynBmp;
import static com.qycx.base.qyMcMainCusModule.qyFuncs.M_getCompressModule;
import static com.qycx.base.qyMcMainProc_isCli_media.qyFuncs.getBih_capBmpToPlay;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.exitTaskAvFrom_audio;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.initTaskAvFrom_audio;
import static com.qycx.base.qyWmComm.Consts.*;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes_toGetMsg;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsg;
import static com.qycx.base.qyq2.qyFuncs.q2TriggerToProcess;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import com.qycx.base.util.Log;

public class qyMcMainMediaReceiver {

    public static class qyFuncs {

        public static int old_mySendAvResp(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_TASK pMsg, qnmCommProc_mis.CMIS_MSGU pMsgBuf) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;
            if (null == pMisCnt) return -1;

            qnmCommProc_mis.IM_CONTENTU pContent = pMsg.data.buf;//(qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
            int lenInBytes;
            short usRespCode = CONST_qyRc_unknown;
            //TCHAR tBuf[128];
            int i;

            //  2017/09/04
            if (pMsg.data.route.idInfo_from.ui64Id == pMisCnt.idInfo.ui64Id) {
                return 0;
            }

            //
            switch (pContent.uiType) {
                case CONST_imCommType_transferVideoData: {
                    qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead[] pContent_transferVideoData_mems = null;//(qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead) pContent.transferVideoData.buf_TRANSFER_VIDEO_dataMemHead_mems;
                    qnmCommProc_mis.TRANSFER_VIDEO_DATA transferVideoData = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent;
                    for (i = 0; i < transferVideoData.usCnt; i++) {
                        qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem = pContent_transferVideoData_mems[i];
                        if (0 == pMem.stat.tSelectTime) continue;
                        //
                        {

                            qnmCommProc_mis.MSG_ROUTE route=new qnmCommProc_mis.MSG_ROUTE();
                            byte ucFlg = (byte) CONST_commFlg_resp;
                            qnmCommProc_mis.TRANSFER_VIDEO_dataResp resp=new qnmCommProc_mis.TRANSFER_VIDEO_dataResp();

                            //mymemset((byte[])route, 0, sizeof(MSG_ROUTE));
                            //memcpy(route.debugInfo,pMsg->data.route.debugInfo, sizeof(route.debugInfo));
                            //route.debugInfo = pMsg.data.route.debugInfo;

                            //
                            ucFlg |= CONST_commFlg_routeTalkData;

                            //mymemset((byte[])resp, 0, sizeof(TRANSFER_VIDEO_dataResp));
                            resp.uiType = CONST_imCommType_transferVideoDataResp;
                            //
                            resp.idInfo.ui64Id = pMem.idInfo.ui64Id;
                            resp.uiTranNo_openAvDev = pMem.uiTranNo_openAvDev;
                            if (0 == resp.uiTranNo_openAvDev) {
                                resp.uiTranNo_openAvDev = transferVideoData.uiTranNo_openAvDev;
                            }
                            //
                            resp.usFps = (short) transferVideoData.tmpInternal.result.fFps;
                            resp.tSelectTime = transferVideoData.tmpInternal.result.tSelectTime;
                            resp.usCnt_pkts = transferVideoData.tmpInternal.result.usCnt_pkts;
                            resp.usDiff_pkts = transferVideoData.tmpInternal.result.usDiff_pkts;

                            //
                            lenInBytes=128;//sizeof(TRANSFER_VIDEO_dataResp);

                            usRespCode = transferVideoData.tmpInternal.result.usRespCode;
//                            byte[] data = resp.toByteArray();
                            if (0 != postMsg2Mgr_mc(ctxCaller, pMisCnt, route, CONST_misMsgType_outputTask, ucFlg, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0,  resp, (int) lenInBytes, pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.data.route.idInfo_from.ui64Id, pMsg.uiChannelType, pMsgBuf, false))
                                return iErr;

                        }
                    }
                }
                break;
                case CONST_imCommType_transferAudioData:
                    qnmCommProc_mis.TRANSFER_AUDIO_DATA transferAudioData = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent;
                    if (0 != transferAudioData.tLastTime_showFrameInfo_020534) {

                        qnmCommProc_mis.MSG_ROUTE route=new qnmCommProc_mis.MSG_ROUTE();
                        byte ucFlg = (byte) CONST_commFlg_resp;
                        qnmCommProc_mis.TRANSFER_AUDIO_dataResp resp=new qnmCommProc_mis.TRANSFER_AUDIO_dataResp();

                        //mymemset((byte[])route, 0, sizeof(MSG_ROUTE));
                        //memcpy(  &route.debugInfo,  pMsg->data.route.debugInfo,  sizeof(route.debugInfo  )  );
                        //route.debugInfo = pMsg.data.route.debugInfo;

                        ucFlg |= CONST_commFlg_routeTalkData;

                        //mymemset((byte[])resp, 0, sizeof(TRANSFER_AUDIO_dataResp));
                        resp.uiType = CONST_imCommType_transferAudioDataResp;
                        resp.uiTranNo_openAvDev = transferAudioData.uiTranNo_openAvDev;
                        resp.usCnt_pkts = transferAudioData.tmpInternal.result.usCnt_pkts;

                        lenInBytes=128;//sizeof(TRANSFER_AUDIO_dataResp);

                        usRespCode = transferAudioData.tmpInternal.result.usRespCode;
//                        byte[] data = resp.toByteArray();
                        if (0 != postMsg2Mgr_mc(ctxCaller, pMisCnt, route, CONST_misMsgType_outputTask, ucFlg, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0,  resp, (int) lenInBytes, pMsg.addr_logicalPeer.idInfo.ui64Id,
                                pMsg.data.route.idInfo_from.ui64Id,
                                pMsg.uiChannelType, pMsgBuf, false))
                            return iErr;

                    }
                    break;
                case CONST_imCommType_transferGpsData:       //  2012/04/24

                    break;

                default:
                    break;
            }

            iErr = 0;
            
            return iErr;
        }

        //
        public static int mySendAvResp(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.MIS_MSG_INPUT pMsg, qnmCommProc_mis.CMIS_MSGU pMsgBuf) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;
            if (null == pMisCnt) return -1;

            qnmCommProc_mis.IM_CONTENTU pContent = pMsg.data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsg.data.buf;
            int lenInBytes;
            short usRespCode = CONST_qyRc_unknown;
            //TCHAR tBuf[128];
            int i;

            //  2017/09/04
            if (pMsg.data.route.idInfo_from.ui64Id == pMisCnt.idInfo.ui64Id) {
                return 0;
            }

            //
            switch (pContent.uiType) {
                case CONST_imCommType_transferVideoData: {
                    qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pContent_transferVideoData_mems[] =null;// (qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead) pContent.transferVideoData.buf_TRANSFER_VIDEO_dataMemHead_mems;
                    qnmCommProc_mis.TRANSFER_VIDEO_DATA transferVideoData = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent;
                    for (i = 0; i < transferVideoData.usCnt; i++) {
                        qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem = pContent_transferVideoData_mems[i];
                        if (0 == pMem.stat.tSelectTime) continue;
                        //
                        {

                            qnmCommProc_mis.MSG_ROUTE route=new qnmCommProc_mis.MSG_ROUTE();
                            byte ucFlg = (byte) CONST_commFlg_resp;
                            qnmCommProc_mis.TRANSFER_VIDEO_dataResp resp=new qnmCommProc_mis.TRANSFER_VIDEO_dataResp();

                            //mymemset((byte[])route, 0, sizeof(MSG_ROUTE));
                            //memcpy(route.debugInfo,pMsg->data.route.debugInfo, sizeof(route.debugInfo));
                            //route.debugInfo = pMsg.data.route.debugInfo;

                            //
                            ucFlg |= CONST_commFlg_routeTalkData;

                            //mymemset((byte[])resp, 0, sizeof(TRANSFER_VIDEO_dataResp));
                            resp.uiType = CONST_imCommType_transferVideoDataResp;
                            //
                            resp.idInfo.ui64Id = pMem.idInfo.ui64Id;
                            resp.uiTranNo_openAvDev = pMem.uiTranNo_openAvDev;
                            if (0 == resp.uiTranNo_openAvDev) {
                                resp.uiTranNo_openAvDev = transferVideoData.uiTranNo_openAvDev;
                            }
                            //
                            resp.usFps = (short) transferVideoData.tmpInternal.result.fFps;
                            resp.tSelectTime = transferVideoData.tmpInternal.result.tSelectTime;
                            resp.usCnt_pkts = transferVideoData.tmpInternal.result.usCnt_pkts;
                            resp.usDiff_pkts = transferVideoData.tmpInternal.result.usDiff_pkts;

                            //
                            lenInBytes=128;//sizeof(TRANSFER_VIDEO_dataResp);

                            usRespCode = transferVideoData.tmpInternal.result.usRespCode;
//                            byte[] data = resp.toByteArray();
                            if (0 != postMsg2Mgr_mc(ctxCaller, pMisCnt, route, CONST_misMsgType_outputTask, ucFlg, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0,  resp, (int) lenInBytes, pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.data.route.idInfo_from.ui64Id, pMsg.uiChannelType, pMsgBuf, false))
                                return iErr;
                        }
                    }
                }
                break;
                case CONST_imCommType_transferAudioData:
                    qnmCommProc_mis.TRANSFER_AUDIO_DATA transferAudioData = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent;
                    if (0 != transferAudioData.tLastTime_showFrameInfo_020534) {

                        qnmCommProc_mis.MSG_ROUTE route=new qnmCommProc_mis.MSG_ROUTE();
                        byte ucFlg = (byte) CONST_commFlg_resp;
                        qnmCommProc_mis.TRANSFER_AUDIO_dataResp resp=new qnmCommProc_mis.TRANSFER_AUDIO_dataResp();

                        //mymemset((byte[])route, 0, sizeof(MSG_ROUTE));
                        //memcpy(  &route.debugInfo,  pMsg->data.route.debugInfo,  sizeof(route.debugInfo  )  );
                        //route.debugInfo = pMsg.data.route.debugInfo;

                        ucFlg |= CONST_commFlg_routeTalkData;

                        resp.uiType = CONST_imCommType_transferAudioDataResp;
                        resp.uiTranNo_openAvDev = transferAudioData.uiTranNo_openAvDev;
                        resp.usCnt_pkts = transferAudioData.tmpInternal.result.usCnt_pkts;

                        lenInBytes=128;//sizeof(TRANSFER_AUDIO_dataResp);

                        usRespCode = transferAudioData.tmpInternal.result.usRespCode;
//                        byte[] data = resp.toByteArray();
                        if (0 != postMsg2Mgr_mc(ctxCaller, pMisCnt, route, CONST_misMsgType_outputTask, ucFlg, usRespCode, pMsg.tStartTime, pMsg.uiTranNo, (short) 0,  resp, (int) lenInBytes, pMsg.addr_logicalPeer.idInfo.ui64Id, pMsg.data.route.idInfo_from.ui64Id, pMsg.uiChannelType, pMsgBuf, false))
                            return iErr;

                    }
                    break;
                case CONST_imCommType_transferGpsData:       //  2012/04/24

                    break;

                default:
                    break;
            }

            iErr = 0;
            
            return iErr;
        }


        public static int playAudioData(ctxQmc.CCtxQmc pProcInfo, int iSampleTimeInMs, int uiPts, byte[] pInput, int inputLen, int iIndex_player) {
            //Log.d(Consts.AUDIO_DOWNLOAD,"5 playAudioData enter " +  Thread.currentThread().getName());
            //
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

            //
            if (pInput == null) return -1;
            if (inputLen == 0) return -1;
            //if (pInput.length != inputLen) return -1;
            if (pInput.length < inputLen) {
                String str ="playAudioData failed,pInput.length<inputLen";
                Log.d( str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                return -1;
            }


            //
            qmcCommFunc_is_open.QY_PLAYER pPlayer = null;//new qmcCommFunc_is_open.QY_PLAYER();
            pPlayer = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, iIndex_player);
            if (null == pPlayer) return -1;

            if (inputLen > pProcInfo.cfg.uiBufSize_myPlayAudioData) {
                return iErr;
            }

            do {

                //
                if (pProcInfo.cfg.b_useQ2_toPlayA) {

                    //CQyMalloc				mallocObj_pkt;
//                qySyncCommProc.CQyMalloc mallocObj = new qySyncCommProc.CQyMalloc();
                    {
                        ;
                        myPlayAudioData.myPLAY_AUDIO_DATA pkt = new myPlayAudioData.myPLAY_AUDIO_DATA();  //  2015/02/14
                        myPlayAudioData.myPLAY_AUDIO_DATA p = new myPlayAudioData.myPLAY_AUDIO_DATA();
                        int len;


                        len = 128;//sizeof(myPlayAudioData.myPLAY_AUDIO_DATA);//,  buf  );
                        p = pkt;//  (  myPLAY_AUDIO_DATA  *  )mallocObj_pkt.mallocf(  len  );
                        if (null == p) return iErr;
                        //mymemset(p, 0, len);//offsetof(  myPLAY_AUDIO_DATA,  buf  )  );
                        //  p->uiTranNo_openAvDev  =  pPlayer.audio.uiTranNo;
                        //
                        //  2016/12/10
                        int uiSize_dec = inputLen;
                        //  2011/12/06
                        int uiSize_enc = 0;   //  sizeof(  qnmCommProc_mis.TRANSFER_AUDIO_DATA  );
                        uiSize_enc = 1;                 //  2017/10/14. 因为uiSize_dec可能为0， 所以这里要加一个字节，避免head.uiLen为0
                        //
                        //  p->head.uiLen  =  dwNumBytes;
                        p.head.uiLen = uiSize_dec + uiSize_enc;    //  2016/12/10
                        p.uiSize_dec = uiSize_dec;                 //  2016/12/10
                        //
                        p.head.uiSampleTimeInMs = (int) iSampleTimeInMs;
                        //  p->head.uiSampleTimeInMs_org  =  iSampleTimeInMs_org;
                        p.head.uiPts = uiPts;                      //  2017/10/13

                        //
                        //
//                    if (null == mallocObj.mallocf((int) p.head.uiLen)) return iErr;
//                    if (null == (p.memory = mallocObj.get())) return iErr;
                        //
                        //mymemcpy(p->memory.m_pBuf,pInput, p->uiSize_dec);  //  p->head.uiLen  );
//                    System.arraycopy(pInput, 0, p.memory.m_pBuf, 0, p.uiSize_dec);
                        p.memory.m_pBuf = pInput;

                        //
                        if (0 != q2PostMsg(p, pPlayer.audio.q2, "playAudioData.324")) {
                            //  if  (  !pQyMc.bUseDirectSound_toPlay  )
                            if (pQyMc.uiAPlayType == CONST_aPlayType_wave) {
                                //PostThreadMessage(pPlayer.audio.dwThreadId, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                            }
                            return iErr;
                        }
//                    mallocObj.detach();

                        //  2010/05/03
                        //  if  (  !pQyMc.bUseDirectSound_toPlay  )
                        if (pQyMc.uiAPlayType == CONST_aPlayType_wave) {
                            //PostThreadMessage(pPlayer.audio.dwThreadId, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                        }
                    }
                } else {

                    qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(Consts.CONST_iParamType_decObj_a,0);
                    if (tmpVar==null) break;
                    qyShowInfoCommProc.Ctx_thread_decObj_a ctxThreadDecObjA=tmpVar.m_ctxThreadDecObjA;
                    if  (  ctxThreadDecObjA==null)  {
                        break;
                    }

                    //
                    QY_qBuf2.HeadData_playA headData = ctxThreadDecObjA.tmp_headDataPlayA;//new QY_qBuf2.HeadData_playA();
                    //QY_qBuf2.HeadData_playA headData = new QY_qBuf2.HeadData_playA();
                    headData.memset0();;
                    //
                    headData.uiPts = uiPts;
                    headData.uiSize_dec = inputLen;
                    //
                    if  (  true  ) {
                        pPlayer.audio.qBuf2.qs0.m_bDbg = true;
                        //pPlayer.audio.qBuf2.qs1.m_bDbg = true;
                    }
                    //
                    if (0 != pPlayer.audio.qBuf2.q2PostMsg(headData, pInput, 0,inputLen, "playAudioData.343")) {
                        Log.e(Consts.AUDIO_DOWNLOAD, "4.6 pPlayer.audio.qBuf2.q2PostMsg failed.  idInfo:" + pPlayer.idInfo_recorder.ui64Id);
                        return iErr;
                    }
                    //
                    //Log.d(Consts.AUDIO_DOWNLOAD,"5.1 audio.qBuf2.qs0 " + pPlayer.audio.qBuf2.qs0.uiQNodes + "audio.qBuf2.qs1 " + pPlayer.audio.qBuf2.qs0.uiQNodes);
                }

                iErr = 0;

            } while (false );

            //p->memory.m_pBuf  =  null;		//  shall be reset

            return iErr;

        }


        //
        public static int playLocalAudioData(ctxQmc.CCtxQmc pProcInfo, int iTaskId, int uiTranNo, qyCommProc_open.WAVEFORMATEX wf, long iSampleTimeInMs, byte[] pInput, int inputLen, int uiCapType, int iIndex_capAudio) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

            qyCommProc_open.WAVEFORMATEX pWf = wf;
            {


                if (null == pWf) return -1;
                if (0 == uiTranNo) {
                    Log.i( "playLocalAudioData failed, uiTranNo is 0");
                    return -1;
                }

                qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
                qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp = null;
                qmcSharedObj.CAP_procInfo_audioCommon pCapAudio = null;
                qmcCommFunc_is_open.QY_PLAYER pPlayer = null;

                pCapAudio = getCapAudioBySth(pProcInfo, iIndex_capAudio, 0);
                if (null == pCapAudio) return iErr;
                pSharedObj = getSharedObjByIndex(pProcInfo, pCapAudio.iIndex_sharedObj);
                if (null == pSharedObj) return iErr;
                pCapBmp = getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);

                qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = getSharedObjUsr(pSharedObj, pSharedObj.var.iIndex_curUsr);
                if (null == pSharedObjUsr) return iErr;

                //
                pPlayer = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, pSharedObj.var.iIndex_localPlayer);
                if (null != pPlayer) {
                    if (pPlayer.iTaskId != iTaskId
                            || pPlayer.idInfo_recorder.ui64Id != 0
                            || pPlayer.audio.uiTranNo != uiTranNo) {
                        Log.i( "drawLocalVideoData: not correct player");
                        pSharedObj.var.iIndex_localPlayer = -1;
                        pPlayer = null;
                    }
                }

                if (null == pPlayer) {
                    int k;

                    for (k = 0; k < pProcInfo.av.usCnt_players; k++) {

                        //  2015/05/22
                        if (pProcInfo.av.pPlayers[k].iTaskId != iTaskId)
                            continue;     //  2010/09/07

                        //
                        if (pProcInfo.av.pPlayers[k].idInfo_recorder.ui64Id == 0
                                && pProcInfo.av.pPlayers[k].audio.uiTranNo == uiTranNo) {
                            if (null == pCapBmp) break;

                            //  2010/09/04
                            if (pProcInfo.av.pPlayers[k].idInfo_recorder.ui64Id == 0
                                    && pProcInfo.av.pPlayers[k].video.uiTranNo == pSharedObjUsr.uiTranNo_openAvDev_v) {
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

                //
                if (null == pPlayer || !pPlayer.audio.bRunning) {


                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("playLocalAudioData : me,%d,%d apply for player"), uiTranNo, 0);

                    //  ÕâÀï×é°ü·¢¸øÖ÷Ïß³Ì¡£
                    qnmCommProc_mis.MIS_MSG_applyForPlayer_qmc msg=new qnmCommProc_mis.MIS_MSG_applyForPlayer_qmc();
                    //mymemset((byte[])msg, 0, sizeof(MIS_MSG_applyForPlayer_qmc));
                    msg.uiType = CONST_misMsgType_applyForPlayer_qmc;
                    msg.iTaskId = iTaskId;
                    //  msg.idInfo_recorder.ui64Id  =  0;
                    msg.audio.uiTranNo = uiTranNo;
                    //  msg.audio.usIndex  =  0;
                    //mymemcpy(msg.audio.ah, pWf, Math.min(sizeof(msg.audio.ah), sizeof(WAVEFORMATEX) + pWf->cbSize));
                    msg.audio.ah.wfx = pWf;
                    //
                    //memcpy(msg.audio.playCfg,pCapAudio.compressAudio.compressor.playCfg, sizeof(msg.audio.playCfg)); //  2012/01/04
                    msg.audio.playCfg = pCapAudio.compressAudio.compressor.playCfg;

                    //
                    if (null != pCapBmp) {
                        //  msg.video.uiTranNo  =  pCapBmp->common.compressVideo.uiTranNo_openAvDev;
                        msg.video.uiTranNo = pSharedObjUsr.uiTranNo_openAvDev_v;                       //  2010/09/04
                        //  msg.video.usIndex  =  0;
                        //
                        //  memcpy(  msg.video.vh.bih,  &pCapBmp->common.compressVideo.vh_decompress.bih,  sizeof(  msg.video.vh.bih  )  );
                        if (0 != getBih_capBmpToPlay(pCapBmp, msg.video.vh.bih)) return iErr;
                        //
                        //msg.video.ucbFlippedImg  =  pCapBmp->common.compressVideo.compressor.common.ucbOutputFlippedImg;	//  2011/12/11
                        //
                        msg.video.usFps = pCapBmp.compressVideo.compressor.common.head.usMaxFps_toShareBmp;      //  2015/02/20
                    }

//                    byte[] byteArray = msg.toByteArray();
                    qPostMsg(  msg, pQyMc.gui.processQ);

                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                    iErr = 0;
                    return iErr;
                }

                if (pQyMc.env.bDebugInfo && pProcInfo.cfg.debugStatusInfo.ucbViewAudioStatus) {
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("playLocalAudioData : me, %d,%d plays"), uiTranNo, 0);
                }

                {
                    qySyncCommProc.CQyMalloc mallocObj = new qySyncCommProc.CQyMalloc();
                    {
                        ;
                        myPlayAudioData.myPLAY_AUDIO_DATA pkt = new myPlayAudioData.myPLAY_AUDIO_DATA();
                        myPlayAudioData.myPLAY_AUDIO_DATA pPkt = pkt;         //  =	(  myPLAY_AUDIO_DATA  *  )mallocObj.malloc(  offsetof(  myPLAY_AUDIO_DATA,  buf  )  +  len  );
                        if (null == pPkt) return iErr;
                        myPlayAudioData.myPLAY_AUDIO_DATA p = pPkt;

                        if (inputLen > pProcInfo.cfg.uiBufSize_myPlayAudioData) {
                            return iErr;
                        }

                        //
                        //mymemset(pPkt, 0, sizeof(myPlayAudioData.myPLAY_AUDIO_DATA));
                        pPkt.head.idInfo.ui64Id = 0;
                        pPkt.head.uiSampleTimeInMs = (int) iSampleTimeInMs;

                        //
                        int uiSize_dec = inputLen;
                        int uiSize_enc = 0;
                        //
                        pPkt.head.uiLen = uiSize_dec + uiSize_enc;
                        pPkt.uiSize_dec = uiSize_dec;
                        //
//                        if (null == mallocObj.mallocf((int) p.head.uiLen)) return iErr;
                        if (null == (p.memory = mallocObj.get())) return iErr;
                        //qyMemoryFunc.qyFuncs.mymemcpy(p.memory.m_pBuf, pInput, p.uiSize_dec);  //  p->head.uiLen  );

                        long dwTickCnt_now = myGetTickCount(null);

                        //  2009/04/06
                        pPlayer.audio.m_var.uiSampleTimeInMs_newRecvd = pPkt.head.uiSampleTimeInMs;
                        pPlayer.audio.m_var.dwTickCnt_uiSampleTimeInMs_newRecvd = dwTickCnt_now;

                        if (0 != pPkt.head.uiSampleTimeInMs && getQ2Nodes_toGetMsg(pPlayer.audio.q2) > 1) {
                            int iAbs = Math.abs((int) (pPlayer.audio.m_var.dwTickCnt_uiSampleTimeInMs_newRecvd - pPlayer.audio.m_var.dwTickCnt_uiSampleTimeInMs_syncRecv)); //  The iAbs is needed because uiSampleTimeInMs may be a future value.
                            if (pPlayer.audio.m_var.uiSampleTimeInMs_newRecvd + iAbs < pPlayer.audio.m_var.uiSampleTimeInMs) {           //  move backward the media file. need to clear up the old media data
                                //Log.e("pkt.uiSampleTimeInMs %d + iAbs %d < player.%d. trigger to empty old data", pPkt->head.uiSampleTimeInMs, iAbs, pPlayer.audio.m_var.uiSampleTimeInMs);
                                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("playAudioData: pkt.uiSampleTimeInMs %d + abs %d < player.%d. trigger to empty old data"), pPlayer.audio.m_var.uiSampleTimeInMs_newRecvd, iAbs, pPlayer.audio.m_var.uiSampleTimeInMs);

                                q2TriggerToProcess(pPlayer.audio.q2);
                            }
                            if (0 != pPlayer.audio.m_var.uiSampleTimeInMs) {
                                if (pPkt.head.uiSampleTimeInMs > pPlayer.audio.m_var.uiSampleTimeInMs + 10000) {   //  2009/05/25. too old audio data
                                    //Log.e("pkt.uiSampleTimeInMs %d > player.%d  +  10000. trigger to empty old data", pPkt->head.uiSampleTimeInMs, pPlayer.audio.m_var.uiSampleTimeInMs);
                                    q2TriggerToProcess(pPlayer.audio.q2);
                                }
                            }
                            //  2012/01/26
                            if (0 != pPlayer.audio.playCfg.usMaxTimeInMsToPlay) {   //  2009/06/03
                                //TCHAR tBuf[256] = "";
                                long nWaitingInMs_audio =  MACRO_getnWaitingInMs_audio_syncRecv(pPlayer.audio, dwTickCnt_now);
                                if (nWaitingInMs_audio > 0 && nWaitingInMs_audio > pPlayer.audio.playCfg.usMaxTimeInMsToPlay + pPlayer.audio.playCfg.usMinTimeInMsOfAudioDataPlaying) {
                                    q2TriggerToProcess(pPlayer.audio.q2);
                                }
                            }
                        }

//                        byte[] byteArray = pPkt.toByteArray();
                        if (0 != q2PostMsg(pPkt, pPlayer.audio.q2,"playLocalAudioData.537")) {
                            //  if  (  !pQyMc.bUseDirectSound_toPlay  )
                            if (pQyMc.uiAPlayType == CONST_aPlayType_wave)  //  2017/02/16
                            {
                                //PostThreadMessage(pPlayer.audio.dwThreadId, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                            }
                            return iErr;
                        }
                        mallocObj.detach();
                    }
                }

                //  2010/05/03
                //  if  (  !pQyMc.bUseDirectSound_toPlay  )
                if (pQyMc.uiAPlayType == CONST_aPlayType_wave) {
                    //PostThreadMessage(pPlayer.audio.dwThreadId, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                }

            }

            iErr = 0;
            
            return iErr;
        }

        //
        public static int chkFromPlayerIndex_a(int uiTransformType, qmcTransform.QY_TRANSFORM pQY_TRANSFORM, myPlayAudioData.myPLAY_AUDIO_DATA p, Object p2) {
            int iErr = -1;
            //
            //com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a enter ");

            //
            if (uiTransformType != CONST_uiTransformType_dec) {
                com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 1");
                return -1;
            }

            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pQY_TRANSFORM;
            if (null == pTransform) {
                com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 2");
                return -1;
            }

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();// (MC_VAR_isCli)pTransform.pProcInfo;
            if (null == pProcInfo) {
                com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 3");
                return -1;
            }
            int iTaskId = pTransform.iTaskId;

            taskAv.PROC_TASK_AV pTask = (taskAv.PROC_TASK_AV) pTransform.pTask;
            if (null == pTask) {
                com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 4");
                return -1;
            }

            //
            qyCommProc_open.WAVEFORMATEX pWf = pTransform.audio.ta.ah_decompress.wfx;
            {
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;//->getMisCntByName((""));
                if (null == pMisCnt) {
                    com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 5");
                    return -1;
                }

                qyDefs.QY_MESSENGER_ID idInfo_recorder=new qyDefs.QY_MESSENGER_ID();
                int uiTranNo = 0;
                int usIndex = 0;
                int iPos = 0;
                //TCHAR tBuf[256] = "";


                if (null == pWf) {
                    com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 6");
                    return -1;
                }

                idInfo_recorder.ui64Id = p.head.idInfo.ui64Id;
                uiTranNo = p.head.uiTranNo_openAvDev;      //  2016/12/16
                if (0 == uiTranNo) uiTranNo = p.head.tmpInternal.uiTranNo_org;     //  2016/12/16

                //  skip myself
                if (idInfo_recorder.ui64Id == pMisCnt.idInfo.ui64Id) {
                    com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 7");
                    return iErr;
                }
                //  2015/05/19
                if (pTransform.uiTaskType == CONST_imTaskType_viewDynBmp) {
                    if (!pProcInfo.cfg.ucbPlayRemoteAudio_viewDynBmp) {
                        com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 8");
                        //  skipped
                        return iErr;
                    }
                }


                //taskAv.PROC_TASK_AV  *  pTask  =  (  taskAv.PROC_TASK_AV  *  )pTransform.pTask;
                if (null == pTask) {
                    com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 9");
                    return iErr;
                }


                //if (pQyMc.iCustomId != CONST_qyCustomId_hzj)
                {

                    //  2015/02/18
                    //  2010/09/02
                    boolean bNoSharedObj;
                    bNoSharedObj = false;
                    if (pTask.doProxy.bExists_avProxy) bNoSharedObj = true;

                    //
                    if (!bNoSharedObj) {
                        qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
                        pSharedObj = getSharedObjByIndex(pProcInfo, pTask.iIndex_sharedObj);
                        if (null != pSharedObj) {
                            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = getSharedObjUsr(pSharedObj, pSharedObj.var.iIndex_curUsr);
                            if (null != pSharedObjUsr) {
                                if (pSharedObjUsr.iTaskId != pTransform.iTaskId) {
                                    com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 10");
                                    return iErr;
                                }
                            }
                        }
                    }

                    //
                }

                qmcCommFunc_is_open.QY_PLAYER pPlayer = null;//new qmcCommFunc_is_open.QY_PLAYER();
                pPlayer = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, pTransform.iIndex_player);
                if (null != pPlayer) {
                    if (pPlayer.iTaskId != iTaskId
                            || pPlayer.idInfo_recorder.ui64Id != idInfo_recorder.ui64Id
                            || pPlayer.audio.uiTranNo != uiTranNo) {
                        Log.i( "drawVideoData: not correct player");
                        pTransform.iIndex_player = -1;
                        pPlayer = null;
                    }
                }

                if (null == pPlayer) {
                    int k;
                    //
                    for (k = 0; k < pProcInfo.av.usCnt_players; k++) {

                        if (pProcInfo.av.pPlayers[k].iTaskId != iTaskId)
                            continue;     //  2010/09/07

                        if (pProcInfo.av.pPlayers[k].idInfo_recorder.ui64Id == idInfo_recorder.ui64Id
                                && pProcInfo.av.pPlayers[k].audio.uiTranNo == uiTranNo) {
                            if (0 == pTransform.video.tv.uiTranNo_openAvDev) break;

                            if (pProcInfo.av.pPlayers[k].idInfo_recorder.ui64Id == idInfo_recorder.ui64Id
                                    && pProcInfo.av.pPlayers[k].video.uiTranNo == pTransform.video.tv.uiTranNo_openAvDev) {
                                break;
                            }
                        }
                    }
                    if (k < pProcInfo.av.usCnt_players) {
                        pTransform.iIndex_player = k;
                        pPlayer = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, k);
                        if (null == pPlayer) {
                            com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 11");
                            return iErr;
                        }
                    }
                }
                //  2017/07/03
                if (null == pPlayer || 0 == pPlayer.audio.uiPlayType || !pPlayer.audio.bRunning) {


                    //  ÕâÀï×é°ü·¢¸øÖ÷Ïß³Ì¡£
                    qnmCommProc_mis.MIS_MSG_applyForPlayer_qmc msg=new qnmCommProc_mis.MIS_MSG_applyForPlayer_qmc();
                    //mymemset((byte[])msg, 0, sizeof(MIS_MSG_applyForPlayer_qmc));
                    msg.uiType = CONST_misMsgType_applyForPlayer_qmc;
                    //
                    msg.iTaskId = iTaskId;                              //  2009/09/17
                    msg.idInfo_logicalPeer = pTransform.pAddr_logicalPeer.idInfo;
                    //
                    msg.idInfo_recorder.ui64Id = idInfo_recorder.ui64Id;
                    msg.audio.uiTranNo = uiTranNo;
                    //memcpy(msg.audio.ah, pWf, min(sizeof(msg.audio.ah), sizeof(WAVEFORMATEX) + pWf->cbSize));
                    msg.audio.ah.wfx = pWf;

                    if (0 != pTransform.video.tv.uiTranNo_openAvDev) {
                        msg.video.uiTranNo = pTransform.video.tv.uiTranNo_openAvDev;
                        //memcpy(msg.video.vh.bih,pTransform.video.tv.vh_decompress.bih, sizeof(msg.video.vh.bih));
                        msg.video.vh.bih = pTransform.video.tv.vh_decompress.bih;
                        //msg.video.ucbFlippedImg  =  pTransform.video.tv.compressor.common.ucbOutputFlippedImg;		//  2011/12/11
                        msg.video.usFps = pTransform.video.tv.compressor.usMaxFps_toShareBmp;                   //  2015/02/20

                        //  2015/05/20
                        if (0 == msg.video.vh.bih.biWidth || 0 == msg.video.vh.bih.biHeight) {
                            if (msg.idInfo_recorder.ui64Id == 0) {
                                String str;
                                str = String.format("chkFromPlayerIndex_a failed, idInfo 0, trans.v.tv.vh_dec.bih: %sX%s", msg.video.vh.bih.biWidth, msg.video.vh.bih.biHeight);
                                com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 12");
                                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                return iErr;
                            }
                        }
                    }

//                    byte[] byteArray = msg.toByteArray();
                    qPostMsg(msg, pQyMc.gui.processQ);
                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, CONST_qyWm_postComm, CONST_qyWmParam_msgArrive, 0);
                    com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.85 chkFromPlayerIndex_a error 13");

                    iErr = -2;
                    return iErr;
                }

                if (pQyMc.env.bDebugInfo && pProcInfo.cfg.debugStatusInfo.ucbViewAudioStatus) {
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("playAudioData : %I64u plays"), idInfo_recorder.ui64Id);
                }

                qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pPkt;
                pPkt = p.head;
                //

                long dwTickCnt_now = myGetTickCount(null);

                //  2009/04/06
                pPlayer.audio.m_var.uiSampleTimeInMs_newRecvd = pPkt.uiSampleTimeInMs;
                pPlayer.audio.m_var.dwTickCnt_uiSampleTimeInMs_newRecvd = dwTickCnt_now;

                if (0 != pPkt.uiSampleTimeInMs && getQ2Nodes_toGetMsg(pPlayer.audio.q2) > 1) {
                    int iAbs = Math.abs((int) (pPlayer.audio.m_var.dwTickCnt_uiSampleTimeInMs_newRecvd - pPlayer.audio.m_var.dwTickCnt_uiSampleTimeInMs_syncRecv)); //  The iAbs is needed because uiSampleTimeInMs may be a future value.
                    if (pPlayer.audio.m_var.uiSampleTimeInMs_newRecvd + iAbs < pPlayer.audio.m_var.uiSampleTimeInMs) {
                        q2TriggerToProcess(pPlayer.audio.q2);
                    }
                    if (0 != pPlayer.audio.m_var.uiSampleTimeInMs) {
                        if (pPkt.uiSampleTimeInMs > pPlayer.audio.m_var.uiSampleTimeInMs + 10000) {   //  2009/05/25. too old audio data
                            /**/

                            q2TriggerToProcess(pPlayer.audio.q2);
                        }
                    }

                    //  if  (  pTask.audio.playCfg.uiMaxTimeInMsToPlay  )
                    if (0 != pPlayer.audio.playCfg.usMaxTimeInMsToPlay) {   //  2009/06/03
                        int nWaitingInMs_audio = (int) MACRO_getnWaitingInMs_audio_syncRecv(pPlayer.audio, dwTickCnt_now);
                        if (nWaitingInMs_audio > 0 && nWaitingInMs_audio > pPlayer.audio.playCfg.usMaxTimeInMsToPlay + pPlayer.audio.playCfg.usMinTimeInMsOfAudioDataPlaying) {

                            q2TriggerToProcess(pPlayer.audio.q2);
                        }
                    }
                }

            }

            //
            do {
                int index_taskInfo = getQmcTaskInfoIndexBySth(null, pProcInfo, pTransform.iTaskId);
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, index_taskInfo, "");
                if (pTaskInfo == null) break;
                if (!pTaskInfo.bUsed) break;
                qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();

                //
                taskData.tmpStatus.lastTickCnt_dec_a = myGetTickCount(null);

                //
            } while (false);


            //
            iErr = 0;
            
            return iErr;

        }


        //
        public static int transformTaskAudioPkt(qmcTransform.QY_TRANSFORM pTransform, qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMemHead, byte[] pInput1,  myPlayAudioData.myPLAY_AUDIO_DATA pPkt_dec, qnmCommProc_mis.MIS_MSGU pMsgBuf_unused) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;// (QY_MC)pProcInfo.pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            boolean bDbg = true;
            String str;

            //
            qnmCommProc_mis.MSGR_ADDR pAddr = pTransform.pAddr_logicalPeer;
            int iTaskId = pTransform.iTaskId;
            qnmCommProc_mis.AV_TRAN_INFO pTranInfo_unused = (qnmCommProc_mis.AV_TRAN_INFO) pTransform.pTranInfo_unused;
            taskAv.PROC_TASK_AV pTask = (taskAv.PROC_TASK_AV) pTransform.pTask;
            //  myPLAY_AUDIO_DATA	*	pPkt_dec			=	(  myPLAY_AUDIO_DATA  *  )pTransform.audio.pkt_dec;
            //  qnmCommProc_mis.MIS_MSG_TASK		*	pMsgTask_toCompress	=	(  qnmCommProc_mis.MIS_MSG_TASK  *  )pTransform.audio.pMsgTask_toCompress_videoConference;

            //
            int index_activeMems_from = pTransform.index_activeMems_from;

            //
            com.qycx.base.ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
            //mymemset((byte[])ctxCaller, 0, sizeof(CTX_caller));

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "transformTaskAudioPkt");
            if (null == pTaskInfo) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (null == pTaskData) return -1;
            if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            //
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;
            if (null == pMisCnt) return -1;
            int tmpiRet = 0;
            //char timeBuf[32] = "";
            //TCHAR tHintBuf[256] = "";

            int bufSize = bufSize_TRANSFER_AUDIO_dataMemHead_mems;
            //if (null == pPktInfo_toDec) return -1;

            //
            qyMcMainCusModule.CUS_MODULES pCusModules = (qyMcMainCusModule.CUS_MODULES) pProcInfo.getCusModules();
            if (null == pCusModules) return -1;

            //
            if (index_activeMems_from < 0 || index_activeMems_from >= videoConference.usCntLimit_activeMems_from)
                return -1;
            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_activeMems_from];


            //
            //int index = index_pMems_from;
            long curTickCnt = myGetTickCount(null);

            //TCHAR where_showInfo[32] = ""; _sntprintf(where_showInfo, mycountof(where_showInfo), ("transA %d"), pTransform.index_activeMems_from);

            if (null == pAddr) return -1;

            if (!pTransform.audio.ucbInited) {
                Log.i( "procTaskAudio failed: ³õÊ¼»¯Î´Íê³É¡£");
                return iErr;
            }

            int dwBytes_pcm;
            qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem;
            //int iPos;
            qyCommProc_open.WAVEFORMATEX pWfx_toPlay = pTransform.audio.ta.ah_decompress.wfx;

            //for  (  iPos  =  0,  i  =  0;  i  <  pReq->usCnt;  iPos  +=  pReq->mems[i].uiLen,  i  ++  )
            for (int kk = 0; kk < 1; kk++) {

                pMem = pMemHead;

                if (0 == pMem.uiLen) {
                    continue;
                }
                if (0 != pMem.uiTranNo_openAvDev) {   //  2013/08/04. 会议时,参会人如果第2次进入会议,那么需要区分前后2次的音频流是不同的,靠的就是tn
                    if (pMem.uiTranNo_openAvDev != pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev) {
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("transformTaskAudio: err, %I64u, mem.uiTranNo_openAvDev %d != from.audio. %d. skipped"), pMem->idInfo.ui64Id, pMem->uiTranNo_openAvDev, pFrom.audio.ta_recvd.uiTranNo_openAvDev);
                        continue;
                    }
                }
                if (pMem.idInfo.ui64Id != 0) {
                    pPkt_dec.head.idInfo.ui64Id = pMem.idInfo.ui64Id;

                    pPkt_dec.head.tmpInternal.uiTranNo_org = pMem.uiTranNo_openAvDev;
                } else {
                    return iErr;
                }
                //  2017/10/13
                pPkt_dec.head.uiPts = pMem.uiPts;

                //  2015/05/19
                if (pTask.saveVideo.ucbSaveVideo_req) {
                    //
                    //(  BYTE  *  )pReq->buf  +  iPos,  pMem->uiLen,  pMem->uiSampleTimeInMs,  pMem->ui_rtTimeLen
                    //
                    byte[] data = pInput1;// pReq->buf + iPos;
                    int len = (int) pMem.uiLen;
                    postAPktToSave(CONST_uiTransformType_dec, pTransform, pMem.uiSampleTimeInMs, 0, data, len);
                }


                //
                if (pTransform.audio.ta.compressor.ucCompressors == CONST_audioCompressors_acm) {


                    //Log.e("acm not supported");
                    return iErr;
                } else if (pTransform.audio.ta.compressor.ucCompressors == CONST_audioCompressors_dmo) {

                    //Log.e("dmo not supported");
                    return iErr;

                } else {
                    qyMcMainCusModule.CUS_MODULE_compress pModule = M_getCompressModule(pCusModules, pTransform.audio.uiModuleType_toDec);

                    if (null == pModule
                            || null == pModule.pf_qdcDecompressAudio
                            || null == pModule.pf_qdcGetOutputByIndex) return iErr;


                    qdcCommProc_open.QOI_common pDmoInfo = pTransform.audio.u_qoi.myQoi.common;   //  .qdcObjInfo;
                    int index_dmo = 0;

                    //
                    if (pProcInfo.cfg.debugStatusInfo.ucb__Dump_mp3_dec__) {
                        //
                        byte[] audio = pInput1;// pReq->buf + iPos;
                        int len = (int) pMem.uiLen;

                        guiShare.GuiShare.pf_dumpData1.call( audio, len, 0, 0, true, false, CONST_fn_dump_mp3_dec);
                    }


                    //  2011/07/03
                    //  if  (  pModule->compress.pf_qdcDecompressAudio.call(  pDmoInfo,  &pFrom.audio.ah_compress_recvd,  (  BYTE  *  )pReq->buf  +  iPos,  pMem->uiLen,  pMem->uiSampleTimeInMs,  pMem->ui_rtTimeLen  )  )  goto  errLabel;
                    if (pModule.pf_qdcDecompressAudio.call(pDmoInfo, pTransform.audio.ta.ah_compress,  pInput1, pMem.uiLen, (int) pMem.uiSampleTimeInMs, pMem.ui_rtTimeLen) < 0)
                        return iErr;
                    //if (pActiveMem_from.sth.iDiffInMs_tStartAudios[index_dmo] != pDmoInfo.var.iDiffInMs_tStartAudio) pFrom.sth.iDiffInMs_tStartAudios[index_dmo] = pDmoInfo.var.iDiffInMs_tStartAudio;

                    //  2017/10/14
                    boolean bDecDataGot = false;

                    //
                    int j;
                    for (j = 0; ; j++) {
                        pPkt_dec.head.uiSampleTimeInMs = pMem.uiSampleTimeInMs;
                        pPkt_dec.head.uiLen = pProcInfo.cfg.uiBufSize_myPlayAudioData;    //  mycountof(  pPkt_dec->buf  );

                        if (0 != pModule.pf_qdcGetOutputByIndex.call(pDmoInfo, j, pPkt_dec.memory.buffer_m_pBuf, pPkt_dec.head))
                            break;

                        //
                        if (pProcInfo.cfg.debugStatusInfo.ucb__Dump_deced_pcm__) {
                            guiShare.GuiShare.pf_dumpData1.call(pPkt_dec.memory.m_pBuf, (int) pPkt_dec.head.uiLen, 0, 0, true, false, "dump_deced_pcm.pcm");
                            str = String.format("deced_pcm %s", j);

                            if (j == 119) {
                                int kk1 = 0;
                            }
                        }

                        //
                        bDecDataGot = true;

                        //  pWfx_toPlay  =  &pFrom.audio.ah_decompress_recvd.wfx;
                        //  playAudioData(  pProcInfo,  pMisCnt,  pTransform.iTaskId,  pTask,  pWfx_toPlay,  pPkt_dec,  pFrom  );
                        if (0 == chkFromPlayerIndex_a(CONST_uiTransformType_dec, pTransform, pPkt_dec, null)) {
                            playAudioData(pProcInfo, (int) pPkt_dec.head.uiSampleTimeInMs, pPkt_dec.head.uiPts, pPkt_dec.memory.m_pBuf, pPkt_dec.head.uiLen, pTransform.iIndex_player);
                        }

                        if (!pTask.ucbVideoConferenceStarter) continue;

                        //  addToReq_toCompress_audio(  pProcInfo,  pTranInfo,  pTask,  index_activeMems_from,  pPkt_dec,  pMsgTask_toCompress  );

                        continue;
                    }

                    //  2017/10/14
                    if (!bDecDataGot) {  //  2017/10/14. 为了让player能校验uiPts, 故要发个空包过去。
                        pPkt_dec.head.uiLen = 0;
                        //
                        playAudioData(pProcInfo, (int) pPkt_dec.head.uiSampleTimeInMs, pPkt_dec.head.uiPts, pPkt_dec.memory.m_pBuf, pPkt_dec.head.uiLen, pTransform.iIndex_player);
                        //
                        continue;
                    }
                }
            }

            iErr = 0;

            

            if (0 == iErr) {
                if (!pTask.bVideoConference()) {
                    //mySendAvResp(pProcInfo, pMsgTask, pMsgBuf  );
                }
            }

            //if (iErr || tHintBuf[0]) qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, where_showInfo, "", ("%I64u %s: %s"), pMsgTask->data.route.idInfo_from.ui64Id, (iErr ? ("failed") : ("")), tHintBuf);

            return iErr;

        }


        //
        public static int transformTaskAudioPkt_useAudioQ2(qmcTransform.QY_TRANSFORM pTransform, qnmCommProc_mis.MIS_MSG_INPUT pMsgTask, qnmCommProc_mis.TRANSFER_AUDIO_DATA pReq, qnmCommProc_mis.PKT_info_toDec pPktInfo_toDec, int index_pMems_from, int index_activeMems_from, myPlayAudioData.myPLAY_AUDIO_DATA pPkt_dec, qnmCommProc_mis.MIS_MSGU pMsgBuf_unused) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;// (QY_MC)pProcInfo.pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            boolean bDbg = true;
            String str;

            //
            qnmCommProc_mis.MSGR_ADDR pAddr = pTransform.pAddr_logicalPeer;
            int iTaskId = pTransform.iTaskId;
            qnmCommProc_mis.AV_TRAN_INFO pTranInfo_unused = (qnmCommProc_mis.AV_TRAN_INFO) pTransform.pTranInfo_unused;
            taskAv.PROC_TASK_AV pTask = (taskAv.PROC_TASK_AV) pTransform.pTask;
            //  myPLAY_AUDIO_DATA	*	pPkt_dec			=	(  myPLAY_AUDIO_DATA  *  )pTransform.audio.pkt_dec;
            //  qnmCommProc_mis.MIS_MSG_TASK		*	pMsgTask_toCompress	=	(  MIS_MSG_TASK  *  )pTransform.audio.pMsgTask_toCompress_videoConference;

            //
            com.qycx.base.ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
            //mymemset((byte[])ctxCaller, 0, sizeof(CTX_caller));

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "transformTaskAudioPkt");
            if (null == pTaskInfo) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (null == pTaskData) return -1;
            if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            //
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;
            if (null == pMisCnt) return -1;
            int tmpiRet = 0;
            //char timeBuf[32] = "";
            //TCHAR tHintBuf[256] = "";
            if (null == pReq || 0 == pReq.usCnt || pReq.usCnt > CONST_maxTransferAvDataMems)
                return -1;
            int bufSize = bufSize_TRANSFER_AUDIO_dataMemHead_mems;
            if (null == pPktInfo_toDec) return -1;

            qyMcMainCusModule.CUS_MODULES pCusModules = pProcInfo.getCusModules();
            if (null == pCusModules) return -1;

            if (index_activeMems_from < 0 || index_activeMems_from >= videoConference.usCntLimit_activeMems_from)
                return -1;
            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_activeMems_from];


            //
            //int index = index_pMems_from;
            long curTickCnt = myGetTickCount(null);

            //TCHAR where_showInfo[32] = ""; _sntprintf(where_showInfo, mycountof(where_showInfo), ("transA %d"), pTransform.index_activeMems_from);

            if (null == pAddr) return -1;

            if (pTransform.audio.ucbInited) {
                if (pTransform.audio.index_activeMems_from != index_activeMems_from
                        || pTransform.audio.idInfo.ui64Id != pActiveMem_from.avStream.idInfo.ui64Id
                        || pTransform.audio.ta.uiTranNo_openAvDev != pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev
                        || pTransform.audio.status.ucbDecFailed            //  2012/03/19
                ) {
                    //Log.e("procTaskAudio: transform.index_pMems_from %d, uiTranNo_openAvDev %d != %d, %d", pTransform.audio.index_pMems_from, pTransform.audio.ta.uiTranNo_openAvDev, index_pMems_from, pFrom.audio.ta_recvd.uiTranNo_openAvDev);
                    exitTaskAvFrom_audio(pProcInfo, pTransform, "transformTaskAudioPkt enters");
                }
            } else {
                exitTaskAvFrom_audio(pProcInfo, pTransform, "transformTaskAudioPkt enters");
            }

            //  2009/04/13
            if (!pTransform.audio.ucbInited) {
                if (0 != initTaskAvFrom_audio(pProcInfo, pActiveMem_from, index_activeMems_from, pTransform))
                    return iErr;
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("procTaskAudio: initTaskAvFrom_audio %d"), index_pMems_from);
            }
            if (!pTransform.audio.ucbInited) {
                Log.i( "procTaskAudio failed: ³õÊ¼»¯Î´Íê³É¡£");
                return iErr;
            }

            //  check
            if (pMsgTask.data.route.idInfo_from.ui64Id != pActiveMem_from.avStream.idInfo.ui64Id
                    || pReq.uiTranNo_openAvDev != pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev) {
                str = String.format("transformTaskAudioPkt failed: idInfo_from %s, audioData.uiTranNo %s !=  pActive %s, %s", pMsgTask.data.route.idInfo_from.ui64Id, pReq.uiTranNo_openAvDev, pActiveMem_from.avStream.idInfo.ui64Id, pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                //
                return iErr;
            }

            int dwBytes_pcm;
            qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem;
            int iPos;
            qyCommProc_open.WAVEFORMATEX pWfx_toPlay = pTransform.audio.ta.ah_decompress.wfx;

            if (0 == pReq.usCnt || pReq.usCnt > CONST_maxTransferAvDataMems) {
                return iErr;
            }


            //for  (  iPos  =  0,  i  =  0;  i  <  pReq->usCnt;  iPos  +=  pReq->mems[i].uiLen,  i  ++  )
            for (int kk = 0; kk < 1; kk++) {
                if (!pPktInfo_toDec.bGot_msg) return iErr;
                if (!pPktInfo_toDec.info.bGot_pkt) return iErr;
                //
                //  2017/10/14
                iPos = pPktInfo_toDec.info.iPos;
                if (pPktInfo_toDec.info.index_mems < 0 || pPktInfo_toDec.info.index_mems >= pReq.usCnt)
                    return iErr;
                int i;
                i = pPktInfo_toDec.info.index_mems;

                //
                pMem = getMem.qyFuncs.getMem(pReq.mems, bufSize, i);// &pReq_mems[i];

                if (iPos + pMem.uiLen > pReq.uiLen) {
                    return iErr;
                }
                if (0 == pMem.uiLen) {
                    continue;
                }
                if (0 != pMem.uiTranNo_openAvDev) {   //  2013/08/04. 会议时,参会人如果第2次进入会议,那么需要区分前后2次的音频流是不同的,靠的就是tn
                    if (pMem.uiTranNo_openAvDev != pActiveMem_from.avStream.obj.tranInfo.audio.uiTranNo_openAvDev) {
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("transformTaskAudio: err, %I64u, mem.uiTranNo_openAvDev %d != from.audio. %d. skipped"), pMem->idInfo.ui64Id, pMem->uiTranNo_openAvDev, pFrom.audio.ta_recvd.uiTranNo_openAvDev);
                        continue;
                    }
                }
                //memset(  pPkt_dec,  0,  offsetof(  myPLAY_AUDIO_data,  buf  )  );
                //mymemset((byte[])pPkt_dec->head, 0, sizeof(qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead));   //  2015/02/14
                if (pMem.idInfo.ui64Id != 0) {
                    pPkt_dec.head.idInfo.ui64Id = pMem.idInfo.ui64Id;
                    if (pPkt_dec.head.idInfo.ui64Id == pMsgTask.data.route.idInfo_from.ui64Id)
                        pPkt_dec.head.tmpInternal.uiTranNo_org = pReq.uiTranNo_openAvDev;
                    else pPkt_dec.head.tmpInternal.uiTranNo_org = 0;
                } else {
                    pPkt_dec.head.idInfo.ui64Id = pMsgTask.data.route.idInfo_from.ui64Id;
                    pPkt_dec.head.tmpInternal.uiTranNo_org = pReq.uiTranNo_openAvDev;
                }
                //  2017/10/13
                pPkt_dec.head.uiPts = pMem.uiPts;

                //  2015/05/19
                if (pTask.saveVideo.ucbSaveVideo_req) {
                    //
                    //(  BYTE  *  )pReq->buf  +  iPos,  pMem->uiLen,  pMem->uiSampleTimeInMs,  pMem->ui_rtTimeLen
                    pReq.buf.position(iPos);
                    byte[] data = pReq.buf.array();
                    int len = (int) pMem.uiLen;
                    postAPktToSave(CONST_uiTransformType_dec, pTransform, pMem.uiSampleTimeInMs, 0, data, len);
                }


                //
                if (pTransform.audio.ta.compressor.ucCompressors == CONST_audioCompressors_acm) {

                    //Log.e("acm not supported");
                    return iErr;
                } else if (pTransform.audio.ta.compressor.ucCompressors == CONST_audioCompressors_dmo) {

                    //Log.e("dmo not supported");
                    return iErr;

                } else {
                    qyMcMainCusModule.CUS_MODULE_compress pModule = M_getCompressModule(pCusModules, pTransform.audio.uiModuleType_toDec);

                    if (null == pModule
                            || null == pModule.pf_qdcDecompressAudio
                            || null == pModule.pf_qdcGetOutputByIndex) return iErr;


                    qdcCommProc_open.QOI_common pDmoInfo = pTransform.audio.u_qoi.myQoi.common;   //  .qdcObjInfo;
                    int index_dmo = 0;

                    //
                    if (pProcInfo.cfg.debugStatusInfo.ucb__Dump_mp3_dec__) {
                        pReq.buf.position(iPos);
                        byte[] audio = pReq.buf.array();
                        int len = (int) pMem.uiLen;

                        guiShare.GuiShare.pf_dumpData1.call( audio, len, 0, 0, true, false, CONST_fn_dump_mp3_dec);


                    }


                    //  2011/07/03
                    //  if  (  pModule->compress.pf_qdcDecompressAudio.call(  pDmoInfo,  &pFrom.audio.ah_compress_recvd,  (  BYTE  *  )pReq->buf  +  iPos,  pMem->uiLen,  pMem->uiSampleTimeInMs,  pMem->ui_rtTimeLen  )  )  goto  errLabel;
                    pReq.buf.position(iPos);
                    if (pModule.pf_qdcDecompressAudio.call(pDmoInfo, pTransform.audio.ta.ah_compress,  pReq.buf.array(), pMem.uiLen, (int) pMem.uiSampleTimeInMs, pMem.ui_rtTimeLen) < 0)
                        return iErr;
                    //if (pActiveMem_from.sth.iDiffInMs_tStartAudios[index_dmo] != pDmoInfo.var.iDiffInMs_tStartAudio) pFrom.sth.iDiffInMs_tStartAudios[index_dmo] = pDmoInfo.var.iDiffInMs_tStartAudio;

                    //  2017/10/14
                    boolean bDecDataGot = false;

                    //
                    int j;
                    for (j = 0; ; j++) {
                        pPkt_dec.head.uiSampleTimeInMs = pMem.uiSampleTimeInMs;
                        pPkt_dec.head.uiLen = pProcInfo.cfg.uiBufSize_myPlayAudioData;    //  mycountof(  pPkt_dec->buf  );

                        if (0 != pModule.pf_qdcGetOutputByIndex.call(pDmoInfo, j, pPkt_dec.memory.buffer_m_pBuf, pPkt_dec.head))
                            break;

                        //
                        if (pProcInfo.cfg.debugStatusInfo.ucb__Dump_deced_pcm__) {
                            guiShare.GuiShare.pf_dumpData1.call(pPkt_dec.memory.m_pBuf, (int) pPkt_dec.head.uiLen, 0, 0, true, false, "dump_deced_pcm.pcm");
                            str = String.format("deced_pcm %s", j);

                            if (j == 119) {
                                int kk1 = 0;
                            }
                        }

                        //
                        bDecDataGot = true;

                        //  pWfx_toPlay  =  &pFrom.audio.ah_decompress_recvd.wfx;
                        //  playAudioData(  pProcInfo,  pMisCnt,  pTransform.iTaskId,  pTask,  pWfx_toPlay,  pPkt_dec,  pFrom  );
                        if (0 == chkFromPlayerIndex_a(CONST_uiTransformType_dec, pTransform, pPkt_dec, null)) {
                            playAudioData(pProcInfo, (int) pPkt_dec.head.uiSampleTimeInMs, pPkt_dec.head.uiPts, pPkt_dec.memory.m_pBuf, pPkt_dec.head.uiLen, pTransform.iIndex_player);
                        }

                        if (!pTask.ucbVideoConferenceStarter) continue;

                        //  addToReq_toCompress_audio(  pProcInfo,  pTranInfo,  pTask,  index_activeMems_from,  pPkt_dec,  pMsgTask_toCompress  );

                        continue;
                    }

                    //  2017/10/14
                    if (!bDecDataGot) {  //  2017/10/14. 为了让player能校验uiPts, 故要发个空包过去。
                        pPkt_dec.head.uiLen = 0;
                        //
                        playAudioData(pProcInfo, (int) pPkt_dec.head.uiSampleTimeInMs, pPkt_dec.head.uiPts, pPkt_dec.memory.m_pBuf, pPkt_dec.head.uiLen, pTransform.iIndex_player);
                        //
                        continue;
                    }
                }
            }

            iErr = 0;

            

            if (0 == iErr) {
                if (!pTask.bVideoConference()) {
                    //mySendAvResp(pProcInfo, pMsgTask, pMsgBuf  );
                }
            }

            //if (iErr || tHintBuf[0]) qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, where_showInfo, "", ("%I64u %s: %s"), pMsgTask->data.route.idInfo_from.ui64Id, (iErr ? ("failed") : ("")), tHintBuf);

            return iErr;

        }


    }
}

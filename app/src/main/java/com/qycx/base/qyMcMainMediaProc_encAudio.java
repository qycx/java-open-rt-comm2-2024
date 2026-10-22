package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.Consts.CONST_iParamType_encA;
import static com.qycx.base.Consts.CONST_iParamType_playa;
import static com.qycx.base.Consts.CONST_iParamType_encA;
import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.ctxCaller.Consts.CONST_ctxCallerType_thread;
import static com.qycx.base.findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc;
import static com.qycx.base.getNextVPkt.qyFuncs.getNextAPkt;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAudioData;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.makeMsg2MgrFunc_mc;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_ffmpeg;
import static com.qycx.base.qmcSharedObj.Consts.CONST_sharedObjType_mediaDevice;
import static com.qycx.base.qmcSharedObj.Consts.CONST_sharedObjType_mediaFile;
import static com.qycx.base.qmcSharedObj.Consts.CONST_sharedObjType_webcam_av;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapAudioBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_audioCompressors_acm;
import static com.qycx.base.qmcVideoCapture.Consts.CONST_audioCompressors_dmo;
import static com.qycx.base.qmcVideoFunc.qyFuncs.getSharedObjUsr;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendMedia;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_maxTransferAvDataMems;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_TRANSFER_AUDIO_dataMemHead_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_myPLAY_AUDIO_DATA_pkts_encAudio;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.chkToApplyForChannel;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyComm.Consts.CONST_commFlg_routeTalkData;
import static com.qycx.base.qyCommProc.Consts.CONST_qyShowType_qwmComm;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_encAudio;
import static com.qycx.base.qyMcMainCusModule.qyFuncs.M_getCompressModule;
import static com.qycx.base.qyq2.qyFuncs.isQ2Warning;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.tmpFunc.qyFuncs.mySleep;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;
import static com.qycx.base.transformFunc.qyFuncs.bCapAudioQuit;

import android.os.Environment;

import com.qycx.base.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class qyMcMainMediaProc_encAudio {
    static class TMP_buf_myPLAY_AUDIO_DATA_pkts_encAudio {
        public ByteBuffer buf = ByteBuffer.allocateDirect(bufSize_myPLAY_AUDIO_DATA_pkts_encAudio);//[];
    }

    static boolean dump_audio = false;
    static FileOutputStream dump = null;
    public static class qyFuncs {
        //
        public static int doEncodeAudio_default(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, qmcSharedObj.CAP_procInfo_audioCommon pCapProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSGU pMsgBuf) {
            int iErr = -1;

            //  MC_VAR_isCli  *  pProcInfo  =  QY_GET_procInfo_isCli(  );
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

            //
            int iIndex_sharedObj = pCapProcInfo.iIndex_sharedObj;
            qmcSharedObj.QY_sharedObj_sync pSharedObjSync = pProcInfo.getSharedObjSyncByIndex(iIndex_sharedObj);
            if (null == pSharedObjSync) return -1;
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = pSharedObjSync.sharedObj;//getSharedObjByIndex(  pProcInfo,  iIndex_sharedObj  );
            if (null == pSharedObj) return -1;
            //
            ctxQmc.QMC_cfg pCfg = pProcInfo.cfg;
            //if (!pCfg) return -1;

            //
            int loopCtrl  =  0;


            int old_pktId = 0;
            pCapProcInfo.compressAudio.pCurPkt.tmp_lPktId_getNewPkt = 0;
            //
            for (; ; ) {
                //
                loopCtrl  ++  ;

                //
                if (bCapAudioQuit(pCapProcInfo, null, null)) {
                    return iErr;
                }
                //
                for (; ; ) {
                    if (bCapAudioQuit(pCapProcInfo, null, null)) return iErr;
                    if (null != (pCapProcInfo.compressAudio.tmpCurPkt = getNextAPkt.call(pCapProcInfo.compressAudio.pCurPkt, pCapProcInfo.compressAudio.pInDataCacheQ, pCapProcInfo.thread.q2))) {
                        pCapProcInfo.compressAudio.pCurPkt = pCapProcInfo.compressAudio.tmpCurPkt;
                        break;
                    }
                    mySleep(40);
                }

                //
                qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(CONST_iParamType_encA,0);


                //
                byte[] pInput = null;
                int uiInputSize = 0;
                boolean bNeed_forceKeyFrame = false;   //  2014/09/25


//                guiShare.GuiShare.pf_dumpData1.call(pCapProcInfo.compressAudio.pCurPkt.memory.m_pBuf, (int) pCapProcInfo.compressAudio.pCurPkt.head.uiLen, 0, 0, true, false, "dump_deced_pcm.pcm");
                if (0 != enc_parseOrgAPkt_i.qyFuncs.enc_parseOrgAPkt_i(CONST_uiTransformType_encAudio, pCapProcInfo, pCapProcInfo.compressAudio.pCurPkt, pCapProcInfo.compressAudio.pCurPkt.tmp_lPktId_getNewPkt, pInput, uiInputSize, bNeed_forceKeyFrame)) {
                    return iErr;
                }

                myPlayAudioData.myPLAY_AUDIO_DATA pPkt = pCapProcInfo.compressAudio.pCurPkt;
                int pktLen = 128;//sizeof(myPlayAudioData.myPLAY_AUDIO_DATA);

                //
                if  (  pProcInfo.cfg.debugStatusInfo.b_dbg_0920)  {
                    if    (  (  loopCtrl  %  50  )  ==  0  ) {
                        //showInfo_open.qyFuncs.showInfo_open("before toShareAudio "  +  loopCtrl);

                        //Log.d("qy", "before toShareAudio");
                    }
                }

                //进行压缩
                //  toShareAudio(  pProcInfo,  (  CAP_procInfo_audioU  *  )pCapProcInfo,  pPkt->head.uiSampleTimeInMs,  (  BYTE  *  )pPkt->memory.m_pBuf,  pPkt->head.uiLen,  &pCapProcInfo.compressAudio,  getSharedObjByIndex(  pProcInfo,  pCapProcInfo.iIndex_sharedObj  ),  null,  pMsgBuf  );
                toShareAudio(tmpVar ,ctxCaller, pProcInfo, pCapProcInfo, (int) pPkt.head.uiSampleTimeInMs, pPkt.memory.m_pBuf, pPkt.uiSize_dec,
                        pCapProcInfo.compressAudio, getSharedObjByIndex(pProcInfo, pCapProcInfo.iIndex_sharedObj), pMsgBuf);
                //
                boolean bPlayLocal = false;
                switch (pSharedObj.var.uiType) {
                    case CONST_sharedObjType_mediaFile:
                    case CONST_sharedObjType_mediaDevice:
                        bPlayLocal = true;
                        break;
                    case CONST_sharedObjType_webcam_av: //  2015/05/18.
                        if (pCfg.ucbPlayLocalAudio_captured) {
                            bPlayLocal = true;
                        }
                        break;
                    default:
                        break;
                }
                //
                boolean bPktRedirected = false;
                if (bPlayLocal) {
                    //			  	
                    qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = getSharedObjUsr(pSharedObj, pSharedObj.var.iIndex_curUsr);
                    if (null == pSharedObjUsr) {
                        //goto  errLabel;
                        //continue;
                    } else {
                        //  

                        pProcInfo.playLocalAudioData(pSharedObjUsr.iTaskId, pSharedObjUsr.uiTranNo_openAvDev_a, pCapProcInfo.compressAudio.ah_decompress.wfx, pPkt, pktLen, bPktRedirected, pCapProcInfo.uiType, pSharedObj.var.iIndex_capAudio);
                    }
                }
                //
                if (bPktRedirected) {
                    //mallocObj_tmpPkt.detach(  );
                    getNextVPkt.qyFuncs.detachAPkt.call(pPkt, null);
                } else {
                    qyTmpMemory.qyFuncs.clean_myPLAY_AUDIO_DATA.call(pPkt, (""));
                }

            }

        }


        /////////////////////
        public static int doEncodeAudio(ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo, qmcSharedObj.CAP_procInfo_audioCommon pCapProcInfo, qmCommFunc_mis.MIS_CNT pMisCnt, qnmCommProc_mis.MIS_MSGU pMsgBuf) {
            int iErr = -1;
            //  MC_VAR_isCli	*	pProcInfo		=	QY_GET_procInfo_isCli(  );
            qmcSharedObj.COMPRESS_AUDIO pCompressAudio = pCapProcInfo.compressAudio;

            doEncodeAudio_default(ctxCaller, pProcInfo, pCapProcInfo, pMisCnt, pMsgBuf);

            iErr = 0;

            return iErr;

        }


        public static qmcFunc.PF_threadProc mcThreadProc_toShareAudio = (Object lpParameter) ->
        {
            int iIndex_sharedObj = (int) lpParameter;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return;

            qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, iIndex_sharedObj);
            if (null == pSharedObj) return;

            qmcSharedObj.CAP_procInfo_audioCommon pCapProcInfo = getCapAudioBySth(pProcInfo, pSharedObj.var.iIndex_capAudio, 0);
            if (null == pCapProcInfo) return;

            //  2009/12/24

            //
            int dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();

            //
            ctxMcThread.CTX_MC_thread ctx = new ctxMcThread.CTX_MC_thread();
            ctx.ctxCaller.type = CONST_ctxCallerType_thread;
            ctx.ctxCaller.dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            ctx.ctxCaller.tNameBuf.tNameBuf = "thread_toShareA";//, cntof_ctxCaller_tNameBuf);

            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pProcInfo.pMisCnt;
            if (null == pMisCnt) return;

            //qySyncCommProc.CQyMalloc mallocObj_pMsgBuf = new qySyncCommProc.CQyMalloc();
            {
                qnmCommProc_mis.MIS_MSGU pMsgBuf = new qnmCommProc_mis.MIS_MSG_TASK();
                if (null == pMsgBuf) return;

                long dwTickCnt;

                //  2010/09/18
                //  TMP_MEMORY_GRP					tmpMemoryGrp;
                //  boolean							bInited_tmpMemoryGrp		=	false;
                int tmpiRet;

                //
                //Log.e(TAG,"mcThreadProc_toShareAudio %S enters", pCapProcInfo.thread.q2.cfg.name);

                pCapProcInfo.thread.bQuit = false;
                pCapProcInfo.thread.bRunning = true;

                //  qyShowInfo1(  CONST_qyShowType_qwmComm,  0,  "",  pProcInfo->who_showInfo,  0,  (  ""  ),  (  ""  ),  (  "%s, %I64u starts"  ),  pPlay->q2.cfg.name,  pPlayer->idInfo_recorder.ui64Id  );

                if(dump_audio)
                {
                    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    try {
                        dump = new FileOutputStream(directory+"/dump_enc_"+System.currentTimeMillis()+".mp3");
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }

                for (; !pCapProcInfo.thread.bQuit; ) {

                    myWaitOne(pCapProcInfo.thread.q2.hEvent0, 5000);
                    //
                    dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);

                    if (!pCapProcInfo.compressAudio.bCompressInited) {
                        continue;
                    }

                    //
                    for (; !pCapProcInfo.thread.bQuit; ) {


                        //
                        if (null == (pCapProcInfo.compressAudio.tmpCurPkt = getNextAPkt.call(pCapProcInfo.compressAudio.pCurPkt, pCapProcInfo.compressAudio.pInDataCacheQ, pCapProcInfo.thread.q2)))
                            break;

//					guiShare.GuiShare.pf_dumpData1.call(pCapProcInfo.compressAudio.tmpCurPkt.memory.m_pBuf, (int) pCapProcInfo.compressAudio.tmpCurPkt.head.uiLen, 0, 0, true, false, "dump_deced_pcm.pcm");

                        pCapProcInfo.compressAudio.pCurPkt = pCapProcInfo.compressAudio.tmpCurPkt;
//                        myPlayAudioData.myPLAY_AUDIO_DATA p = pCapProcInfo.compressAudio.pCurPkt;
//                        guiShare.GuiShare.pf_dumpData1.call(p.memory.m_pBuf, (int) p.head.uiLen, 0, 0, true, false, "dump_deced_pcm.pcm");

                        //
                        if (0 == pCapProcInfo.compressAudio.pCurPkt.lPktId_getNewPkt) {
                            //MACRO_qyAssert(0, ("mcThreadProc_toShareAudio: id_pkt is 0"));
                            //Log.e(TAG,"mcThreadProc_toShareAudio err: id_pkt is 0");
                        }
                        //
                        long old_pktId = pCapProcInfo.compressAudio.pCurPkt.lPktId_getNewPkt;
                        doEncodeAudio(ctx.ctxCaller, pProcInfo, pCapProcInfo, pMisCnt, pMsgBuf);

                        if (pCapProcInfo.compressAudio.pCurPkt.lPktId_getNewPkt == old_pktId) {   //  如果没有处理掉就需要释放
                            qyTmpMemory.qyFuncs.clean_myPLAY_AUDIO_DATA.call(pCapProcInfo.compressAudio.pCurPkt, (""));
                        }
                    }
                }
                if(dump_audio)
                {
                    try {
                        dump.close();
                        dump = null;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            Log.i("mcThreadProc_toShareAudio will quit");
            pCapProcInfo.thread.bRunning = false;

        };

        public static int toShareAudio(qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar  , ctxCaller.CTX_caller ctxCaller, ctxQmc.CCtxQmc pProcInfo1, qmcSharedObj.CAP_procInfo_audioCommon pRecordSoundProcInfo1, int iSampleTimeInMs, byte[] pInput, int dwBytesRecorded, qmcSharedObj.COMPRESS_AUDIO pCompressAudio, qmcSharedObj.QY_SHARED_OBJ pSharedObj, qnmCommProc_mis.MIS_MSGU pMsgBuf1) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;//QY_GET_GBUF(  );
            //
            int dwBytes_input = dwBytesRecorded;
            long tStartTran = tmpFunc.qyFuncs.mytime();
            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
            byte ucFlg = CONST_commFlg_routeTalkData;    //  0;
            int lenInBytes = 0;
            //
            int uiChannelType = CONST_channelType_realTimeMedia;
            boolean ucbNeedShowFrameInfo = false;
            int i;
            //  2010/09/04
            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = null;


            //
            if (null == pProcInfo1) return -1;
            qyMcMainCusModule.CUS_MODULES pCusModules = pProcInfo1.getCusModules();
            ctxQmc.QMC_cfg pCfg = pProcInfo1.cfg;
            //if  (  !pCfg  )  return  -1;

            if (null == pSharedObj) return iErr;
            pSharedObjUsr = getSharedObjUsr(pSharedObj, pSharedObj.var.iIndex_curUsr);
            if (null == pSharedObjUsr) {
                //Log.e(TAG,"toShareAudio failed, pSharedObjUsr is null");
                return iErr;
            }

            do {

                //
                if  (  true  )  {
                    //Log.d("qy","toShareAudio: st "  +  iSampleTimeInMs);
                }



                //
                if (pCompressAudio.tLastTime_sendAudio != tStartTran) {       //  表明是一秒的开始
                    pCompressAudio.tLastTime_sendAudio = tStartTran;

                    //  定期向客户端下发帧速信息
                    if ((int) (tStartTran - pCompressAudio.tLastTime_showFrameInfo) > pCfg.usNeedShowFrameInfoIntervalInS) {
                        //
                        ucbNeedShowFrameInfo = true;
                    }
                }

                //
                qyq2.QY_Q2 p_realTimeMediaQ2 = pProcInfo1.get_realTimeMediaQ2();
                if (null == p_realTimeMediaQ2) return iErr;
                boolean bQWarning = isQ2Warning(p_realTimeMediaQ2, pCfg.envVar.usQNodes_warningInterval_rtMediaQ);
                if (bQWarning) {
                    showInfo_open.qyFuncs.showInfo_open(("Warning:  realTimeMediaQ too many nodes. audio data will be lossed"));
                }


                //
//            qnmCommProc_mis.TRANSFER_AUDIO_DATA pReq = pCompressAudio.pReqCache;
                qnmCommProc_mis.TRANSFER_AUDIO_DATA pReq;
                if (pProcInfo1.cfg.b_useQ2_toEncA) {
                    pReq = new qnmCommProc_mis.TRANSFER_AUDIO_DATA();
                } else {
                    pReq = tmpVar.m_ctxThreadEncA.toShareAudio_tad_req;
                }
                if (null != pReq && !bQWarning) {
                    //mymemset(pReq, 0, (int)Marshal.OffsetOf<qnmCommProc_mis.TRANSFER_AUDIO_DATA>("buf"));
//                pReq.clearData();
                    pReq.clearData();//            bufSize_transferAudioData
                    //
                    if (pProcInfo1.cfg.b_useQ2_toEncA) {
                        pReq.buf = ByteBuffer.allocate(bufSize_transferAudioData); //todo move_copy
                    } else {
                        pReq.buf = tmpVar.m_ctxThreadEncA.toShareAudio_tad_req_buf;
                    }
                    //
                    pReq.uiType = CONST_imCommType_transferAudioData;
                    pReq.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_a; //  pCompressAudio.uiTranNo_openAvDev  +  iIndex_sharedObjUsr;

                    //

                    int bufSize = bufSize_TRANSFER_AUDIO_dataMemHead_mems;

                    if (pCompressAudio.bCompressInited) {
                        if (pCompressAudio.compressor.common.head.ucCompressors == CONST_audioCompressors_acm) {
                            //Log.e(TAG,"not supported");
                            return iErr;

                        } else if (pCompressAudio.compressor.common.head.ucCompressors == CONST_audioCompressors_dmo) {
                            //Log.e(TAG,"not supported");
                            return iErr;

                        } else {
                            qyMcMainCusModule.CUS_MODULE_compress pModule = null;
                            int uiModuleType = 0;
                            uiModuleType = CONST_moduleType_ffmpeg;
                            pModule = M_getCompressModule(pCusModules, uiModuleType);
                            if (null == pModule
                                    || null == pModule.pf_qdcCompressAudio
                                    || null == pModule.pf_qdcGetOutputByIndex) return iErr;

                            int dwDstBytes = pCompressAudio.u_qoi.myQoi.common.var.cbOutputBuffer;
                            if (null == pCompressAudio.pDstBuf || dwDstBytes >= pCompressAudio.dwDstBufSize) {
                                if (null != pCompressAudio.pDstBuf) {
                                    pCompressAudio.pDstBuf = null;
                                }
                                pCompressAudio.dwDstBufSize = dwDstBytes * 3 / 2 + 256;
                                pCompressAudio.pDstBuf = new byte[pCompressAudio.dwDstBufSize];
                                if (null == pCompressAudio.pDstBuf) return iErr;
                            }

                            dwDstBytes = pCompressAudio.dwDstBufSize;


                            //
                            if (false) {
                                String fn = "toShareAudio.pcm";
                                //GuiShare.pf_dumpData1.call(0, 0, 0, 0, false, false, fn);
                                guiShare.GuiShare.pf_dumpData1.call(pInput, (int) dwBytes_input, 0, 0, true, false, fn);

                            }


//                        guiShare.GuiShare.pf_dumpData1.call(pInput, (int) dwBytes_input, 0, 0, true, false, "dump_deced_pcm.pcm");
                            //这里执行压缩
                            //pf_qdcCompressAudio在初始化的时候被赋值为qdcCompressAudio_ffmpeg
                            //内部调用TmpFfmpeg.encA_data（Native方法）进行压缩，是异步的、实时的。
                            //获得压缩后的output也是异步的、实时的。详见下面
                            if (pModule.pf_qdcCompressAudio.call(pCompressAudio.u_qoi.myQoi.common, pCompressAudio.ah_decompress, pInput, dwBytes_input, iSampleTimeInMs) < 0)
                                return iErr;

                            if (null != pSharedObj && pSharedObj.var.iDiffInMs_tStartAudio != pCompressAudio.u_qoi.myQoi.common.var.iDiffInMs_tStartAudio)
                                pSharedObj.var.iDiffInMs_tStartAudio = pCompressAudio.u_qoi.myQoi.common.var.iDiffInMs_tStartAudio;

                            for (i = 0; i < CONST_maxTransferAvDataMems; i++) {
                                //  2009/05/20
                                if (bufSize_transferAudioData <= pReq.uiLen) {
                                    Log.e("toShareAudio:  pReq->len " + pReq.uiLen);
                                    return iErr;
                                }
                                qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem = getMem.qyFuncs.getMem(pReq.mems, bufSize, i);

                                pMem.uiLen = bufSize_transferAudioData - pReq.uiLen;
//                            pReq.buf.position(pReq.uiLen);
                                //这里获得压缩后的output数据
                                //pf_qdcGetOutputByIndex在初始化的时候被赋值为qdcGetOutputByIndex_ffmpeg
                                //内部调用TmpFfmpeg.dec_getOutputByIndex（Native方法）
                                if (0 != pModule.pf_qdcGetOutputByIndex.call(pCompressAudio.u_qoi.myQoi.common, i, pReq.buf, pMem))
                                    break;

                                //
                                pMem.uiSampleTimeInMs  =  iSampleTimeInMs;

                                //
                                if  (  true  ) {
                                    if (i != 0)
                                    {
                                        Log.e("qy", "toShareAudio error");
                                        showInfo_open.qyFuncs.showInfo_open("toShareAudio error: qdcGetOutputByIndex, i not 0: " + i);
                                    }
                                    if  (  false  ) {
                                        String str;
                                        str = "toShareAudio: i " + i + " len " + pMem.uiLen + " pts " + pMem.uiPts;
                                        str += " inputSize " + dwBytes_input;
                                        showInfo_open.qyFuncs.showInfo_open(str);
                                    }
                                }

                                //
                                //
                                if (dump_audio) {
                                    try {
                                        dump.write(pReq.buf.array(), 0, pMem.uiLen);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                //
                                pReq.uiLen += pMem.uiLen;
                                pReq.usCnt++;


                                //
                                continue;

                            }

                            //
                            if  (  false  )  {
                                if  (  pReq.usCnt  ==  0  )  {
                                    showInfo_open.qyFuncs.showInfo_open("toShareAudio: req.cnt 0, inputSize "  +  dwBytes_input);
                                }
                            }

                            //
                            if (false || pCfg.debugStatusInfo.ucb__Dump_mp3_enc__) {
                                //pReq.buf.position(pReq.uiLen);
                                byte[] array = pReq.buf.array();
                                if (true) {
                                    String fn = "toShareAudio.mp3";
                                    //GuiShare.pf_dumpData1.call(0, 0, 0, 0, false, false, fn);
                                    guiShare.GuiShare.pf_dumpData1.call(array, (int) pReq.uiLen, 0, 0, true, false, fn);

                                }

                            }


                        }

                    }

                    //  2010/09/12
                    if (0 == pReq.usCnt) {
                        Log.e("toShareAudio failed: req.usCnt is 0");
                        return iErr;
                    }
                    //  2015/09/10
                    for (i = 0; i < pReq.usCnt; i++) {
                        int myuiPts = 0;  //  2015/01/15
                        //
                        boolean bNeedPts = true;
                        if (bNeedPts) {
                            myuiPts = pCompressAudio.encInfo.cnt_postEnc;
                        }

                        pCompressAudio.encInfo.cnt_postEnc++;
                        qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem = getMem.qyFuncs.getMem(pReq.mems, bufSize, i);
                        if (pMem == null) return iErr;
                        pMem.uiPts = myuiPts;

                        //
                        pMem.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_a;

                    }

                    //
                    if (true) {
                        if  (  false  ||  pProcInfo1.cfg.debugStatusInfo.b_print_test) {
                            for (i = 0; i < pReq.usCnt; i++) {
                                showInfo_open.qyFuncs.showInfo_open(0, "", "toShareAudio: mem"  + i  +  " pts " + pReq.mems[i].uiPts  +  " len "  +  pReq.mems[i].uiLen);
                            }
                        }
                    }


                    //  2010/09/12
                    pCompressAudio.usCnt_pkts++;
                    if (ucbNeedShowFrameInfo) {
                        ucFlg |= CONST_commFlg_routeTalkData;
                        if (pCompressAudio.tRecvTime_resp_fromCli >= pCompressAudio.tFirstTime_showFrameInfo_withoutResp) {   //  这是为了超时
                            pCompressAudio.tFirstTime_showFrameInfo_withoutResp = tStartTran;
                        }
                        pReq.tLastTime_showFrameInfo_020534 = pCompressAudio.tLastTime_showFrameInfo;                             //  设置标记
                        pReq.usCnt_pkts = pCompressAudio.usCnt_pkts;
                        //
                        pCompressAudio.tLastTime_showFrameInfo = tStartTran;
                        pCompressAudio.usCnt_pkts = 0;
                        //
                        //  qyShowInfo1(  CONST_qyShowType_debug,  0,  "",  pProcInfo1.who_showInfo,  0,  (  ""  ),  (  ""  ),  (  "tn %d, %d bytes audio data. %s"  ),  uiTranNo,  dwBytesRecorded,  ucbNeedShowFrameInfo  ?  (  "含音频取样信息"  )  :  (  ""  )  );
                    }

//                lenInBytes = (int)((int)Marshal.OffsetOf<qnmCommProc_mis.TRANSFER_AUDIO_DATA>("buf") + pReq.uiLen);
                    //
                    //boolean					bHaveMsgrs_sendLocalAv		=	false;
                    //  qmcSharedObj.QY_SHARED_OBJ		*	pSharedObj					=	null;
                    qmCommFunc_mis.MIS_CNT pMisCnt = null;

                    //  pSharedObj  =  getSharedObjByIndex(  pProcInfo,  pRecordSoundProcInfo->iIndex_sharedObj  );
                    if (null != pSharedObj) {

                        pMisCnt = pProcInfo1.pMisCnt;// (MIS_CNT)pProcInfo1.getMisCntByIndex(pSharedObj.curRoute_sendLocalAv.iIndex_misCnt);
                        //if (pMisCnt && pMisCnt->cfg.dynCfg.cnt.usCntPort)
                        {
                            //if (!pSharedObjUsr.ucbNotSendData)
                            {
                                int tmpiRet;
                                //
                                int msgLen = 0;

                                //
                                qnmCommProc_mis.MIS_MSGU pMsgBuf = null;
//                            byte[] data = pReq.toByteArray();
                                if  (  pProcInfo1.cfg.b_useQ2_toEncA) {
                                    if (null == (pMsgBuf = makeMsg2MgrFunc_mc(pMisCnt, null, CONST_misMsgType_task, ucFlg, (short) CONST_qyCmd_sendMedia, tStartTran, uiTranNo, (short) 0, pSharedObjUsr.iTaskId, 0, pReq, lenInBytes, 0, 0, 0, 0, uiChannelType, null))) {
                                        //组包zzq
                                        break;
                                    }
                                }
                                else  {

                                    //
                                    if  (  true  ) {
                                        //
                                        qnmCommProc_mis.TRANSFER_AUDIO_DATA tad  =  pReq;
                                        //
                                        int j;
                                        int iPos = 0;
                                        byte[] audio = tad.buf.array();
                                        for (j = 0; j < tad.usCnt; j++) {
                                            qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead mem_i = tad.mems[j];
                                            qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead mem_o = new qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead();
                                            mem_o.copyFrom(mem_i);
                                            //
                                            //mem_o.tmp_pMsgTask=pMsgTask;
                                            //
                                            pProcInfo1.pOutputQBuf2_local_a.q2PostMsg(mem_o, audio, iPos, mem_o.uiLen, "");
                                            iPos += mem_o.uiLen;
                                        }

                                        //
                                        qmCommFunc_mis.MIS_CHANNEL pChannel  =  getChannelByType(pMisCnt,CONST_channelType_realTimeMedia);
                                        if (null != pChannel) {

                                            //
                                            chkToApplyForChannel(pProcInfo1, pChannel, CONST_channelType_realTimeMedia);

                                            //
                                            if  (  pChannel.m_b_useQ2_toOutput) {
                                                q2PostMsgAndTrigger(null, pChannel.outputQ21, "find_procTaskAudioData.188");
                                            }
                                            else  {
                                                pChannel.outputQBuf2.q2PostMsgAndTrigger(null,null,  0,  0,  "find_procTaskAudioData.188");

                                            }
                                        }


                                        //
                                        iErr  =  0;  break;

                                    }




                                    //
                                    if (null == (pMsgBuf = makeMsg2MgrFunc_mc(pMisCnt, null, CONST_misMsgType_task, ucFlg, (short) CONST_qyCmd_sendMedia, tStartTran, uiTranNo, (short) 0, pSharedObjUsr.iTaskId, 0, pReq, lenInBytes, 0, 0, 0, 0, uiChannelType, pMsgBuf1))) {
                                        //组包zzq
                                        break;
                                    }

                                }

                                qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsgBuf;
                                task.bFrom_sharedObj = true;
                                task.index_sharedObj = pSharedObj.var.index_sharedObj;
                                //
                                qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
                                pMsgBuf.debugAudio = 4;
                                if ((tmpiRet = pProcInfo1.processQ_media.qTraverse(ctxCaller, tmpHandler_findOrgMsgAndProc, pMsgBuf, param)) <= 0) {

                                }
                            }
                        }
                    }
                }

                //
                iErr = 0;

                //
            } while (false );


            return iErr;
        }


    }
}

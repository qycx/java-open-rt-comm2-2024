


package com.qycx.base;

import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_decA;
import static com.qycx.base.QY_qBuf2.Consts.CONST_qType_decV;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferAudioData;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferVideoData;
import static com.qycx.base.old.qmcPreTransform.Consts.DEFAULT_dwToInMs;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_ffmpeg;
import static com.qycx.base.qmCusModuleDefs.Consts.CONST_moduleType_mediaCodec;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_threadStep_toWait;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_TRANSFER_AUDIO_dataMemHead_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_TRANSFER_VIDEO_dataMemHead_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_tmpPktEncA;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_setMyStep;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_setStep;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_set_pktId_getNewPkt;
import static com.qycx.base.qyMcMainMediaReceiver.qyFuncs.transformTaskAudioPkt;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.exitTaskAvFrom_audio;
import static com.qycx.base.qyMcMainProc_isCli_robot.qyFuncs.initTaskAvFrom_audio;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;
import static com.qycx.base.qyq2.qyFuncs.isQ2Empty;
import static com.qycx.base.qyq2.qyFuncs.q2GetMsg;
import static com.qycx.base.taskAvProc.qyFuncs.adjustModuleType_decA;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;

import com.qycx.base.util.Log;

import java.nio.ByteBuffer;

public class qmcDecAudio {

    public static class Consts {
        public final static String CONST_mutexNamePrefix_decA_inDataCacheQ = ("decA_in");
        public final static String CONST_mutexNamePrefix_decA_tmpDataQ = ("decA_tmp");

    }

    public static class qyFuncs {


        public static qmcFunc.PF_threadProc mcThreadProc_decAudio=(Object lpParameter)-> {

            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) lpParameter;
            if (null == pTransform) return;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return;
            qmcTransform.TRANSFORM_AUDIO_procInfo pTransformAudio = pTransform.audio;


            qnmCommProc_mis.MIS_MSGU pMsg = null;
            int len;

            qnmCommProc_mis.IM_CONTENTU pContent = null;

            int dwToInMs = DEFAULT_dwToInMs;

            long dwTickCnt_lastAudioData = tmpFunc.qyFuncs.myGetTickCount(null);
            ;                       //


            myPlayAudioData.myPLAY_AUDIO_DATA pkt_dec = new myPlayAudioData.myPLAY_AUDIO_DATA();
            {
                pTransformAudio.bRunning = true;
                //  memset(  &pTransformAudio.m_var,  0,  sizeof(  pTransformAudio.m_var  )  );

                pMsg = new qnmCommProc_mis.MIS_MSGU();
                //
                //  2015/02/14
                //mymemset( ( byte[])pkt_dec, 0, sizeof(myPlayAudioData.myPLAY_AUDIO_DATA));
                //
                int uiBufSize_pkt_dec = pProcInfo.cfg.uiBufSize_myPlayAudioData;  //  offsetof(  myPLAY_AUDIO_DATA,  buf  )  +  pProcInfo.cfg.uiBufSize_myPlayAudioData;	//
                int iAllocType = 0; //  CONST_iAllocType_audio
                //  if  (  !(  pTransform.audio.pPkt_dec  =  (  myPLAY_AUDIO_DATA  *  )mallocObj_pPkt_dec.mallocf(  iAllocType,  uiBufSize_pPkt_dec  )  )  )  goto  errLabel;
                //
//                        if (null == new byte[uiBufSize_pkt_dec])
//                            return;
//                        if (null == (pkt_dec.memory = mallocObj_pkt_dec_pBuf.get())) return;
                //
                //if  (  pTask.ucbVideoConferenceStarter  )
                {
                    //  if  (  !(  pTransform.audio.pMsgTask_toCompress_videoConference  =  (  MIS_MSG_TALK  *  )mallocObj_pMsgTask_toCompress.malloc(  sizeof(  MIS_MSG_TALK  )  )  )  )  goto  errLabel;
                }

                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", ("IsClient"), 0, "", "", ("%s %d starts"), pTransformAudio.q2.cfg.name, pTransform.index_activeMems_from);

                ///////////////////////////
                //  2016/12/10
                //
                //  2014/03/23
                genericQueue.GENERIC_Q_cfgEx qCfgEx = new genericQueue.GENERIC_Q_cfgEx(); //  2012/11/10
                genericQueue.GENERIC_Q_CFG tmpCfg = new genericQueue.GENERIC_Q_CFG();

                //TCHAR tBuf[128];
                //
                int tn = (int) tmpFunc.qyFuncs.myGetTickCount(null);
                int iTaskId = pTransform.iTaskId;
                int index_activeMems_from = pTransform.index_activeMems_from;
                //
                int size = 0;

                qnmCommProc_mis.PKT_info_toDec pcurPktInfo_toDec = pTransformAudio.curPktInfo_toDec;
                {
                    //mymemset(  pcurPktInfo_toDec, 0, sizeof(qnmCommProc_mis.PKT_info_toDec));
                }
                pTransformAudio.curPktInfo_toDec.pMIS_MSGU = pMsg;
                pTransformAudio.curPktInfo_toDec.pPkt_dec = pkt_dec;
                pMsg.uiType = 0;
                pTransformAudio.curPktInfo_toDec.pkt_encA.bufSize = bufSize_tmpPktEncA;


                //
                for (; !pTransformAudio.bQuit; ) {

                    if (0 == dwToInMs) {
                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("Warning: mcThreadProc_transformAudio: dwToInMs %d"), dwToInMs);
                        dwToInMs = DEFAULT_dwToInMs;
                    }

                    //  2009/07/14
                    M_setStep(pTransformAudio.debugStep, CONST_threadStep_toWait);

                    //
                    long dwTickCnt0 = tmpFunc.qyFuncs.myGetTickCount(null);
                    long dwTickCnt;

                    //
                    if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
                        myWaitOne(pTransformAudio.q2.hEvent0, (int) dwToInMs);
                    } else {
                        myWaitOne(pTransformAudio.qBuf2.hEvent0, (int) dwToInMs);
                    }

                    //
                    for (; !pTransformAudio.bQuit; ) {

                        //
                        //
                        //if (0!=getNextPktInfo_toDec( pTransformAudio.curPktInfo_toDec, pTransformAudio.q2)) break;
                        if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
                            if (isQ2Empty(pTransformAudio.q2)) break;
                        } else {
                            if (pTransformAudio.qBuf2.isQ2Empty()) break;
                        }

                        //
                        pTransform.audio.curPktInfo_toDec.bGot_msg=false;
                        pTransform.audio.curPktInfo_toDec.info.bGot_pkt=false;

                        //
                        if (0 == pTransform.audio.curPktInfo_toDec.lPktId_getNewPkt) {
                            //MACRO_qyAssert(0, ("decAudio: id_pkt is 0"));
                            //Log.e("decAudio err: id_pkt is 0");
                        }
                        //
                        long old_pktId = pTransform.audio.curPktInfo_toDec.lPktId_getNewPkt;
                        //doDecodeAudio(pTransform, pMsgBuf);
                        doDecodeAudio(pTransform, null);
                        if (pTransform.audio.curPktInfo_toDec.lPktId_getNewPkt == old_pktId) {   //  如果没有处理掉就需要释放
                            clean_PKT_info_toDec(pTransform.audio.curPktInfo_toDec, (""));
                        }
                    }
                }
            }

            exitTaskAvFrom_audio(pProcInfo, pTransform, "decAudio quit");
            pTransformAudio.bRunning = false;
        };

        public static int getNextPktInfo_toDec(qnmCommProc_mis.PKT_info_toDec pCurPktInfoParam, qyq2.QY_Q2 pDataQ2, Object getNextPktInfoToDecParams) {
            int iErr = -1;

            qnmCommProc_mis.PKT_info_toDec pCurPktInfo = pCurPktInfoParam;

                qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pCurPktInfo.pMIS_MSGU;
                qnmCommProc_mis.IM_CONTENTU pContent = null;

                //
                if (pCurPktInfo.bGot_msg) {
                    //
                    if (!pCurPktInfo.info.bGot_pkt) {
                        //
                        switch (pMsg.uiType) {
                            case CONST_misMsgType_input:
                                pContent = ((qnmCommProc_mis.MIS_MSG_INPUT) pMsg).data.buf;
                                switch (pContent.uiType) {
                                    case CONST_imCommType_transferAudioData: {
                                        //
//                                        Log.e("213");

                                        //
                                        qnmCommProc_mis.TRANSFER_AUDIO_DATA pReq = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent;
                                        int bufSize = bufSize_TRANSFER_AUDIO_dataMemHead_mems;
                                        //
//                                        showInfo_open.qyFuncs.showInfo_open();
                                        if (pCurPktInfo.info.index_mems >= 0 && pCurPktInfo.info.index_mems + 1 < pReq.usCnt) {
                                            //
//                                            Log.e("进来上面了, index_mems: " + pCurPktInfo.info.index_mems);
                                            int i = pCurPktInfo.info.index_mems;
                                            qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem = getMem.qyFuncs.getMem(pReq.mems, bufSize, i);// &pReq_mems[i];
                                            int iPos = pCurPktInfo.info.iPos;

                                            if (iPos + pMem.uiLen < pReq.uiLen) {
                                                pCurPktInfo.info.iPos += (int) pMem.uiLen;
                                                pCurPktInfo.info.index_mems++;
                                                pCurPktInfo.info.bGot_pkt = true;
                                                M_set_pktId_getNewPkt(pCurPktInfo);
                                                qyCommProc_open.getNextPktInfo_toDec_params_a getNextPktInfoToDecParamsA = (qyCommProc_open.getNextPktInfo_toDec_params_a) getNextPktInfoToDecParams;
                                                getNextPktInfoToDecParamsA.head = pReq.mems[pCurPktInfo.info.index_mems];
                                                ///todo move_copy
                                                getNextPktInfoToDecParamsA.onePkt = ByteBuffer.allocate(getNextPktInfoToDecParamsA.head.uiLen);
                                                int limit = pCurPktInfo.info.iPos + getNextPktInfoToDecParamsA.head.uiLen;
                                                pReq.buf.position(pCurPktInfo.info.iPos);
                                                pReq.buf.limit(limit);
                                                getNextPktInfoToDecParamsA.onePkt.put(pReq.buf);
                                            }




                                        }
//                                        if (pCurPktInfo.info.index_mems >= 0 && pCurPktInfo.info.index_mems + 1 < pReq.usCnt) {
//                                            //
//                                            int i = pCurPktInfo.info.index_mems;
//                                            qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem = getMem.qyFuncs.getMem(pReq.mems, bufSize, i);// &pReq_mems[i];
//                                            int iPos = pCurPktInfo.info.iPos;
//
//                                            if (iPos + pMem.uiLen < pReq.uiLen) {
//
//                                                pCurPktInfo.info.iPos += (int) pMem.uiLen;
//                                                pCurPktInfo.info.index_mems++;
//                                                pCurPktInfo.info.bGot_pkt = true;
//                                                M_set_pktId_getNewPkt(pCurPktInfo);
//
//                                            }
//                                        }
                                        //
                                    }
                                    break;
                                    case CONST_imCommType_transferVideoData: {
                                        qnmCommProc_mis.TRANSFER_VIDEO_DATA pReq = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent;

                                        int bufSize = bufSize_TRANSFER_VIDEO_dataMemHead_mems;
//                                        showInfo_open.qyFuncs.showInfo_open();
                                        if (pCurPktInfo.info.index_mems >= 0 && pCurPktInfo.info.index_mems + 1 < pReq.usCnt) {
                                            //
                                            Log.e("进来上面了, index_mems: " + pCurPktInfo.info.index_mems);
                                            int i = pCurPktInfo.info.index_mems;
                                            qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem = getMem.qyFuncs.getMem(pReq.mems, bufSize, i);// &pReq_mems[i];
                                            int iPos = pCurPktInfo.info.iPos;

                                            if (iPos + pMem.uiLen < pReq.uiLen) {
                                                pCurPktInfo.info.iPos += (int) pMem.uiLen;
                                                pCurPktInfo.info.index_mems++;
                                                pCurPktInfo.info.bGot_pkt = true;
                                                M_set_pktId_getNewPkt(pCurPktInfo);

                                                qyCommProc_open.getNextPktInfo_toDec_params_v getNextPktInfoToDecParamsV = (qyCommProc_open.getNextPktInfo_toDec_params_v) getNextPktInfoToDecParams;
                                                getNextPktInfoToDecParamsV.head = pReq.mems[pCurPktInfo.info.index_mems];
                                                ///todo move_copy
                                                getNextPktInfoToDecParamsV.onePkt = ByteBuffer.allocate(getNextPktInfoToDecParamsV.head.uiLen);
                                                int limit = pCurPktInfo.info.iPos + getNextPktInfoToDecParamsV.head.uiLen;
                                                pReq.buf.position(pCurPktInfo.info.iPos);
                                                pReq.buf.limit(limit);
                                                getNextPktInfoToDecParamsV.onePkt.put(pReq.buf);
                                            }




                                        }
                                        //
                                    }
                                    break;

                                    default:
                                        break;
                                }
                                break;

                            default:
                                break;
                        }

                        //
                        if (!pCurPktInfo.info.bGot_pkt) {
                            pCurPktInfo.bGot_msg = false;
                        }
                    }
                }

                //
//            Log.e("315"  +  "bGot_msg "  +  pCurPktInfo.bGot_msg  +  " bGot_pkt "  +  pCurPktInfo.info.bGot_pkt  );

                //
                if (!pCurPktInfo.bGot_msg) {
                    //
                    int len;
                    len =128;//sizeof(qnmCommProc_mis.MIS_MSGU);
                    if (null == (pMsg = q2GetMsg(pDataQ2))) {
                        pCurPktInfo.bGot_msg = false;
                        //
//                        Log.e("getNextPktInfo_toDec failed, bGot_msg false. 319. iErr  "  +  iErr);
                        //
                        return iErr;
                    }
                    pCurPktInfo.pMIS_MSGU = pMsg;
                    //mymemset( (((byte[]) pMsg) + len), 0, (int) Math.min(256, sizeof(qnmCommProc_mis.MIS_MSGU) - len));
                    //
                    switch (pMsg.uiType) {
                        case CONST_misMsgType_input:
//                            pContent = ((qnmCommProc_mis.MIS_MSG_INPUT) pMsg).data.buf;
                            pContent = ((qnmCommProc_mis.MIS_MSG_INPUT) pMsg).data.buf;

                            //
                            if  (  false  ) {
                                showInfo_open.qyFuncs.showInfo_open((pContent.uiType == CONST_imCommType_transferAudioData ? "Audio:" : "Video:") + " getNodes: " + getQ2Nodes(pDataQ2) + " id: " + pMsg.tmpID + " inputTN: " + ((qnmCommProc_mis.MIS_MSG_INPUT) pMsg).uiTranNo);
                                Log.e((pContent.uiType == CONST_imCommType_transferAudioData ? "Audio:" : "Video:") + " getNodes: " + "getNodes: " + getQ2Nodes(pDataQ2) + " id: " + pMsg.tmpID + " inputTN: " + ((qnmCommProc_mis.MIS_MSG_INPUT) pMsg).uiTranNo);
                            }
                            //
                            switch (pContent.uiType) {
                                case CONST_imCommType_transferAudioData: {

//                                    Log.e("343");

                                    qnmCommProc_mis.TRANSFER_AUDIO_DATA pReq = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent;
                                    pCurPktInfo.bGot_msg = true;


//                                    guiShare.GuiShare.pf_dumpData1.call(pReq.buf.array(), pReq.uiLen, 0, 0, true, false, "xindemp3.mp3");


//                                    int ii;
//                                    for (ii = 0; ii < pReq.usCnt; ii++)
//                                    {
//                                        qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem = pReq.mems[ii];
//                                        String str = "dbg: getNextPktInfo_toDec: " + "pkt" + ii + ".pts is "  +  pMem.uiPts  +  " .len "  +  pMem.uiLen;
//                                        Log.e(str);
//                                        showInfo_open.qyFuncs.showInfo_open(str);
//                                    }

                                    pCurPktInfo.info.clearData();
                                    if (0 == pReq.uiLen) {
                                        pCurPktInfo.bGot_msg = false;
                                        //
//                                        Log.e("getNextPktInfo_toDec failed, bGet_msg false. 356");
                                        //
                                        return iErr;
                                    }
                                    pCurPktInfo.info.bGot_pkt = true;
                                    qyCommProc_open.getNextPktInfo_toDec_params_a getNextPktInfoToDecParamsA = (qyCommProc_open.getNextPktInfo_toDec_params_a) getNextPktInfoToDecParams;
                                    getNextPktInfoToDecParamsA.head = pReq.mems[0];
                                    ///todo move_copy
                                    getNextPktInfoToDecParamsA.onePkt = ByteBuffer.allocate(getNextPktInfoToDecParamsA.head.uiLen);
                                    int limit = pCurPktInfo.info.iPos + getNextPktInfoToDecParamsA.head.uiLen;
                                    pReq.buf.position(pCurPktInfo.info.iPos);
                                    pReq.buf.limit(limit);
                                    getNextPktInfoToDecParamsA.onePkt.put(pReq.buf);
                                    getNextPktInfoToDecParamsA.onePkt.flip();
                                    M_set_pktId_getNewPkt(pCurPktInfo);
//
//                                    //
//                                    pCurPktInfo.bGot_msg = true;
//                                    //
//                                    //mymemset( ( byte[])
////                                    pCurPktInfo.info, 0, sizeof(PKT_info_toDec_info));
//                                    //
//                                    if (0 == pReq.uiLen) {
//                                        pCurPktInfo.bGot_msg = false;
//                                        return iErr;
//                                    }
//                                    //
//                                    pCurPktInfo.info.bGot_pkt = true;
//
//                                    //
//                                    M_set_pktId_getNewPkt(pCurPktInfo);

                                }
                                break;
                                case CONST_imCommType_transferVideoData: {
                                    qnmCommProc_mis.TRANSFER_VIDEO_DATA pReq = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent;
                                    pCurPktInfo.bGot_msg = true;


//                                    guiShare.GuiShare.pf_dumpData1.call(pReq.buf.array(), pReq.uiLen, 0, 0, true, false, CONST_fn_dump_h264_enc);


//                                    int ii;
//                                    for (ii = 0; ii < pReq.usCnt; ii++)
//                                    {
//                                        qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem = pReq.mems[ii];
//                                        String str = "dbg: getNextPktInfo_toDec: " + "pkt" + ii + ".pts is "  +  pMem.uiPts  +  " .len "  +  pMem.uiLen;
//                                        Log.e(str);
//                                        showInfo_open.qyFuncs.showInfo_open(str);
//                                    }

                                    pCurPktInfo.info.clearData();
                                    if (0 == pReq.uiLen) {
                                        pCurPktInfo.bGot_msg = false;
                                        //
//                                        Log.e("getnextPktInfo_toDec failed, bGet_msg false, 410");
                                        //
                                        return iErr;
                                    }
                                    pCurPktInfo.info.bGot_pkt = true;

                                    qyCommProc_open.getNextPktInfo_toDec_params_v getNextPktInfoToDecParamsV = (qyCommProc_open.getNextPktInfo_toDec_params_v) getNextPktInfoToDecParams;
                                    getNextPktInfoToDecParamsV.head = pReq.mems[0];
                                    ///todo move_copy
                                    getNextPktInfoToDecParamsV.onePkt = ByteBuffer.allocate(getNextPktInfoToDecParamsV.head.uiLen);
                                    int limit = pCurPktInfo.info.iPos + getNextPktInfoToDecParamsV.head.uiLen;
                                    pReq.buf.position(pCurPktInfo.info.iPos);
                                    pReq.buf.limit(limit);
                                    getNextPktInfoToDecParamsV.onePkt.put(pReq.buf);
                                    getNextPktInfoToDecParamsV.onePkt.flip();
                                    M_set_pktId_getNewPkt(pCurPktInfo);
                                }
                                break;

                                default:
                                    break;
                            }
                            break;

                        default:
                            break;
                    }
                    //
                    if (!pCurPktInfo.bGot_msg) {
                        //
//                        Log.e("getNextPktInfo_toDec failed, 439");
                        //
                        return iErr;
                    }

                }


                //
                if (!pCurPktInfo.info.bGot_pkt) {
                    pCurPktInfo.bGot_msg = false;
                    //
//                    Log.e("getNextPktInfo_toDec failed, 451");
                    //
                    return iErr;
                }

                //
                if  (  pCurPktInfo.bGot_msg  ==  false  )  {
                    int  ii  =  0;
                    System.out.println(ii);
                }
                if  (  pCurPktInfo.info.bGot_pkt  ==  false  ) {
                    int  ii  =  0;
                    System.out.println(ii);
                }
                if  (  pCurPktInfo.bAudio_forDbg) {
                    qyCommProc_open.getNextPktInfo_toDec_params_a getNextPktInfoToDecParamsA = (qyCommProc_open.getNextPktInfo_toDec_params_a) getNextPktInfoToDecParams;
                    if (getNextPktInfoToDecParamsA.head==null) {
                        int ii  =  0;
                        System.out.println(ii);
                    }
                    if  (  getNextPktInfoToDecParamsA.onePkt==null) {
                        int  ii  =  0;
                        System.out.println(ii);
                    }
                }

                //
                iErr = 0;



                //
//                Log.e("getNextPktInfo_toDec ok");

                //
            return iErr;

        }


        //
        public static int getNextPktInfo_toDec(qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar, qnmCommProc_mis.PKT_info_toDec pCurPktInfoParam, QY_qBuf2 pDataQ2) {
            int iErr = -1;
            long time3 = myGetTickCount(null);
            long time2 = 0;
            long time4 = 0;
            long time5 = 0;
            long time6 = 0;

            qnmCommProc_mis.PKT_info_toDec pCurPktInfo = pCurPktInfoParam;
            {
                qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pCurPktInfo.pMIS_MSGU;
                qnmCommProc_mis.IM_CONTENTU pContent = null;

                if (pCurPktInfo.bGot_msg) {
                    //
                    if (!pCurPktInfo.info.bGot_pkt) {
                        if (!pCurPktInfo.info.bGot_pkt) {
                            pCurPktInfo.bGot_msg = false;
                        }
                    }
                }


                //
                if (!pCurPktInfo.bGot_msg) {
                    //
                    int len;

                    byte[] pHeadData;
                    switch (pDataQ2.cfg.m_qType) {
                        case CONST_qType_decA:
                        {
                            pCurPktInfo.pkt_encA.memHead_a = new qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead();
                            //byte[] data = new byte[pCurPktInfo.pkt_encA.bufSize];
                            byte[] data = tmpVar.m_ctxThreadDecA.data;
                            if (0 > (pCurPktInfo.pkt_encA.len = pDataQ2.q2GetMsg(pCurPktInfo.pkt_encA.memHead_a,data,0,  pCurPktInfo.pkt_encA.bufSize))) {
                                if (0 != iErr) {
                                    pCurPktInfo.bGot_msg = false;
                                }
                                return iErr;
                            }
                            //
                            //Log.d(com.qycx.base.Consts.AUDIO_DOWNLOAD,"4,qBuf2.qs0.uiQNodes "+pDataQ2.qs0.uiQNodes + "  qBuf2.qs1.uiQNodes " + pDataQ2.qs1.uiQNodes);
                            //
                            pCurPktInfo.pkt_encA.buf = ByteBuffer.wrap(data);
                            pCurPktInfo.pkt_encA.memHead_a.uiLen = pCurPktInfo.pkt_encA.len;
                        }
                            break;
                        case CONST_qType_decV:
                        {
                            pCurPktInfo.pkt_encV.memHead_v = new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();
                            //byte[] data = new byte[pCurPktInfo.pkt_encV.bufSize];
                            byte[] data = tmpVar.m_ctxThreadDecV.data;
                            long time1 = myGetTickCount(null);
                            if (0 > (pCurPktInfo.pkt_encV.len = pDataQ2.q2GetMsg(pCurPktInfo.pkt_encV.memHead_v, data, 0,  pCurPktInfo.pkt_encV.bufSize))) {
                                if (0 != iErr) {
                                    pCurPktInfo.bGot_msg = false;
                                }
                                return iErr;
                            }
                            time2 = myGetTickCount(null)- time3;
                            //showInfo_open.qyFuncs.showInfo_open("smpl_v q2GetMsg time is:"+ time2);

                            //Log.d(com.qycx.base.Consts.VIDEO_DOWNLOAD,"4 qBuf2.qs0.uiQNodes "+pDataQ2.qs0.uiQNodes + "  qBuf2.qs1.uiQNodes " + pDataQ2.qs1.uiQNodes);
                            pCurPktInfo.pkt_encV.buf = ByteBuffer.wrap(data);
                            pCurPktInfo.pkt_encV.memHead_v.uiLen = pCurPktInfo.pkt_encV.len;
                        }
                            break;
                        default:
                            String str = "getNextPktInfo failed; unprocessed qType";
                            Log.e(str);
                            showInfo_open.qyFuncs.showInfo_open(0, null,str);
                            if (0 != iErr) {
                                pCurPktInfo.bGot_msg = false;
                            }
                            return iErr;
                    }
                    //
                    pCurPktInfo.bGot_msg = true;
                    pCurPktInfo.info.bGot_pkt = true;
                    //
                    time4 = myGetTickCount(null) - time3 ;
                    M_set_pktId_getNewPkt(pCurPktInfo);
                    time5 = myGetTickCount(null)- time3;
                    //showInfo_open.qyFuncs.showInfo_open("smpl_v M_set_pktId_getNewPkt time is:"+ time5);
                }

                if (!pCurPktInfo.info.bGot_pkt) {
                    if (0 != iErr) {
                        pCurPktInfo.bGot_msg = false;
                    }
                    return iErr;
                }

                iErr = 0;
            }

            time6 = myGetTickCount(null)- time3;
            //showInfo_open.qyFuncs.showInfo_open("smpl_v getNextPktInfo_toDec time2 is:"+  time2 + " time4 " + time4 + " time5:" + time5 + " time6:" + time6);
            return iErr;

        }


        //
        public static void clean_PKT_info_toDec(qnmCommProc_mis.PKT_info_toDec p, String hint) {
            p.info.bGot_pkt = false;
        }


        //
        public static int doDecodeAudio(qmcTransform.QY_TRANSFORM pTransform, qnmCommProc_mis.MIS_MSGU pMsgBuf_unused) {
            int iErr = -1;

            int i;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();// pTransform.pProcInfo;//
            if (null == pProcInfo) return -1;

            ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;//->get_qmc_cfg();


            qnmCommProc_mis.MSGR_ADDR pAddr = pTransform.pAddr_logicalPeer;
            if (null == pAddr) return -1;
            int iTaskId = pTransform.iTaskId;
            //
            qnmCommProc_mis.AV_TRAN_INFO pTranInfo_unused = (qnmCommProc_mis.AV_TRAN_INFO) pTransform.pTranInfo_unused;
            if (null == pTranInfo_unused) return -1;
            //
            taskAv.PROC_TASK_AV pTask = (taskAv.PROC_TASK_AV) pTransform.pTask;
            if (null == pTask) return -1;

            //
            com.qycx.base.ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
            //mymemset((byte[])ctxCaller, 0, sizeof(CTX_caller));

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = getQmcTaskInfoByIndex(ctxCaller, pProcInfo, pTask.iIndex_taskInfo, "doDecodeAudio");
            if (null == pTaskInfo) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;
            if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

            qmcTransform.TRANSFORM_AUDIO_procInfo pTransformAudio = pTransform.audio;

            int index_activeMems_from = pTransform.index_activeMems_from;

            if (index_activeMems_from < 0 || index_activeMems_from >= videoConference.usCntLimit_activeMems_from)
                return -1;
            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_activeMems_from];

            //
            int uiModuleType_toDec = 0;
            switch (pActiveMem_from.avStream.obj.tranInfo.audio.compressor.ucCompressors) {
                default:
                    uiModuleType_toDec = CONST_moduleType_ffmpeg;
                    break;
            }
            //
            uiModuleType_toDec = adjustModuleType_decA(uiModuleType_toDec);

            //
            switch (uiModuleType_toDec) {
                case CONST_moduleType_mediaCodec:
                    return guiShare.GuiShare.pf_doDecodeAudio_MediaCodec.call(pTransform, -1, index_activeMems_from, pMsgBuf_unused);
                default:
                    break;
            }

//            if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
//                //doDecodeAudio_default_useAudioQ2(pTransform, -1, index_activeMems_from, pMsgBuf_unused);
//                doDecodeAudio_default(pTransform, -1, index_activeMems_from, pMsgBuf_unused);
//
//
//            } else {
//                doDecodeAudio_default(pTransform, -1, index_activeMems_from, pMsgBuf_unused);
//            }


            iErr = 0;

            

            //  exit

            return iErr;
        }

    }
}


//

//


package com.qycx.base;

import static com.qycx.base.Consts.*;
import static com.qycx.base.ctxQmcTmpl.Consts.*;
import static com.qycx.base.dec_parseEncVPkt_i.qyFuncs.M_nalType_h265;
import static com.qycx.base.dec_parseEncVPkt_i.qyFuncs.chkToForceKeyFrame;
import static com.qycx.base.imCommType_defs.Consts.*;
import static com.qycx.base.qmCusModuleDefs.Consts.*;
import static com.qycx.base.qmcTaskInfo.Consts.*;
import static com.qycx.base.qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex;
import static com.qycx.base.qmcVideoCapture.Consts.*;
import static com.qycx.base.qnmCommProc_mis.Consts.*;
import static com.qycx.base.qyCommProc_open.Consts.*;
import static com.qycx.base.qyDefs_open.Consts.*;
import static com.qycx.base.taskAvProc.qyFuncs.adjustModuleType_decV;

import com.qycx.base.qdc.DecMediaCodecCommon;
import com.qycx.base.util.Log;

import java.nio.ByteBuffer;

public class qyMcMainMediaProc_decVideo_rtsp {


    public static class Consts {
        public final static String CONST_mutexNamePrefix_decV_inDataCacheQ = ("decV_in");
        public final static String CONST_mutexNamePrefix_decV_tmpDataQ = ("decV_tmp");

    }

    public static class qyFuncs {

        public static void postVPktToRender(qmcTransform.QY_TRANSFORM pTransform, Object pmyDRAW_VIDEO_DATA, boolean pbPktRedirected) {
            int iErr = -1;
            qnmCommProc_mis.myDRAW_VIDEO_DATA pPkt[] = null;//(qnmCommProc_mis.myDRAW_VIDEO_DATA) pmyDRAW_VIDEO_DATA;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//QY_GET_procInfo_isCli(  );
            if (null == pProcInfo) return;

            boolean bPktsRedirected = false;

            //
            if (null != pTransform.pCapDev_rtsp) {
                /**/
            } else {
                //  drawVideoData(  pProcInfo,  pPkt,  1  *  sizeof(  pPkt[0]  ),  &bPktsRedirected,  pTransform.iIndex_player  );
                byte[] buf = pPkt[0].toByteArray2();
                pProcInfo.drawVideoData(pPkt, buf.length, bPktsRedirected, pTransform);
            }
        }

        public static qyCommProc_open.PF_bTransformAudioQuit bTransformAudioQuit = (Object pQY_TRANSFORM, Object p1, Object p2) -> {
            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pQY_TRANSFORM;
            return pTransform.audio.bQuit;
        };


        public static qyCommProc_open.PF_bTransformVideoQuit bTransformVideoQuit = (Object pQY_TRANSFORM, Object p1, Object p2) -> {
            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pQY_TRANSFORM;
            return pTransform.video.bQuit;
        };

        // int  transformGetSpsPps(  void  *  pQY_TRANSFORM,  char  *  pBuf,  unsigned  int  *  puiSize  )
        public static qyCommProc_open.PF_transformGetSpsPps transformGetSpsPps = (int uiTransformType, Object pQY_TRANSFORM, int index_pMems_from, ByteBuffer pBuf, int puiSize) -> {
            int iErr = -1;
            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pQY_TRANSFORM;
            qmcTransform.TRANSFORM_VIDEO_procInfo pTransformVideo = pTransform.video;

            int total = 0;
            int i;

            //
            if (pTransformVideo.tv.vh_stream.usCnt > cntof_VH_stream_mems) return -1;
            //
            for (i = 0; i < pTransformVideo.tv.vh_stream.usCnt; i++) {
                qyCommProc_open.VH_stream_mem mem;
                switch (i) {
                    case 0:
                        mem = pTransformVideo.tv.vh_stream.mem0;
                        break;
                    case 1:
                        mem = pTransformVideo.tv.vh_stream.mem1;
                        break;
                    case 2:
                        mem = pTransformVideo.tv.vh_stream.mem2;
                        break;
                    default:
                        return iErr;
                }
                if (total + mem.usLen > puiSize) return -1;
                pBuf.put(mem.spsPps.array(), 0, mem.usLen);
//                System.arraycopy(mem.spsPps.array(), 0, pBuf,  total, mem.usLen);
                total += mem.usLen;
                //  2014/08/31
            }

            //
            String str;
            str = String.format("transformGetSpsPps: %du, cnt %d", pTransform.video.idInfo.ui64Id, (int) pTransformVideo.tv.vh_stream.usCnt);
            showInfo_open.qyFuncs.showInfo_open(0, null,str);

            //
            pBuf.flip();
            puiSize = (int) total;

            iErr = 0;


            return iErr;
        };


        //
        public static qyCommProc_open.PF_transformGetBih transformGetBih = (Object pQY_TRANSFORM, qyCommProc_open.BITMAPINFOHEADER pBih, Object p2) -> {
            int iErr = -1;
            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pQY_TRANSFORM;
            qmcTransform.TRANSFORM_VIDEO_procInfo pTransformVideo = pTransform.video;

            //memcpy(pBih,pTransformVideo->tv.vh_decompress.bih, sizeof(pBih[0]));
            pBih = pTransformVideo.tv.vh_decompress.bih;

            //
            iErr = 0;
            return iErr;
        };


        //

        public static int dec_parseEncAPkt_i_useAudioQ2(int uiTransformType, Object pParent_transform, qnmCommProc_mis.PKT_info_toDec pPktInfo_toDec_param, DecMediaCodecCommon dm, qyCommProc_open.dec_parseEncVPkt_i_useAudioQ2_params decParseEncVPktIUseAudioQ2Params) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();    //  QY_GET_procInfo_isCli(  );
            //TCHAR tBuf[256] = "";


            //  myDRAW_VIDEO_DATA  *  pPkt  =  pTransform.video.pCurPkt;
            qnmCommProc_mis.PKT_info_toDec pPktInfo_toDec = pPktInfo_toDec_param;
            {
                ;

                //  检查一下。每个包都只能处理一次，否则，容易死循环
//                if (0 == p_old_lPktId_getNewPkt) return -1;
                if (0 == pPktInfo_toDec.lPktId_getNewPkt) {
                    String str= "failed: lPktId_alloc 0";
                    Log.d(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    return -1;
                }
                if (pPktInfo_toDec.lPktId_getNewPkt == dm.m_old_lPktId) {
                    String str ="err: lPkdId_alloc==old";
                    Log.d(str);
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
                //pProcInfo = (MC_VAR_common)pTransform.pProcInfo;
                if (null == pProcInfo) return -1;

                ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;
                //if (!pQmcCfg) return -1;

                //
                if (!pPktInfo_toDec.bGot_msg) {
                    Log.e("dec_parseEncAPkt_i_useAudioQ2 failed: pPktInfo_toDec.bGot_msg false");
                    return -1;
                }
                if (!pPktInfo_toDec.info.bGot_pkt) {
                    Log.e("dec_parseEncAPkt_i_useAudioQ2 failed: pPktInfo_toDec.bGot_pkt false");
                    return -1;
                }


                //  2015/09/11
                qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pPktInfo_toDec.pMIS_MSGU;

                if (pMsg.uiType != CONST_misMsgType_input) {
                    Log.e("dec_parseEncAPkt_i failed, only input msg accepted");
                    return iErr;
                }
                qnmCommProc_mis.IM_CONTENTU pContent = ((qnmCommProc_mis.MIS_MSG_INPUT) pMsg).data.buf;
                if (pContent.uiType != CONST_imCommType_transferAudioData) {
                    Log.e("dec_parseEncAPkt_i failed, pContent.type != transferAudioData");

                    return iErr;
                }

                //
                qnmCommProc_mis.TRANSFER_AUDIO_DATA pReq = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent;
                qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem = decParseEncVPktIUseAudioQ2Params.getNextPktInfoToDecParamsA.head;
//                int i;
//                int iPos;
//
//                //
//                iPos = pPktInfo_toDec.info.iPos;
//                if (pPktInfo_toDec.info.index_mems < 0 || pPktInfo_toDec.info.index_mems >= pReq.usCnt)
//                    return iErr;
//                i = pPktInfo_toDec.info.index_mems;
//
//                int bufSize = bufSize_TRANSFER_AUDIO_dataMemHead_mems;
                //
//                pMem = getMem.qyFuncs.getMem(pReq.mems, bufSize, i);

//                if (iPos + pMem.uiLen > pReq.uiLen) {
//                    return iErr;
//                }
                if  (  pMem  ==  null  ) {
                    Log.e("dec_parseEncAPkt_i failed, pMem == null");
                    ;
                    return  iErr;
                }
                if (0 == pMem.uiLen) {
                    Log.e("dec_parseEncAPkt_i failed, pMem.uiLen is 0");

                    return iErr;
                }
                if (true) {
                    if (0 != pMem.uiPts) {
                        if (0 == pTransform.audio.decInfo.pts.uiPts_lastRecvd) {
                            pTransform.audio.decInfo.pts.uiPts_first = pMem.uiPts;
                            pTransform.audio.decInfo.pts.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);
                        } else {
                            boolean bNeedChkPts  =  true;
                            //
                            if (pProcInfo.bUse100k) {
                                bNeedChkPts=false;
                            }
                            //
                            if  (  bNeedChkPts) {
                                if (pMem.uiPts != pTransform.audio.decInfo.pts.uiPts_lastRecvd + 1) {
                                    String str;
                                    //str = String.format("dec_parseEncAPkt_i: %s: pts err, pkt %s, last %s", pMem.idInfo.ui64Id, pMem.uiPts, pTransform.audio.decInfo.pts.uiPts_lastRecvd);
                                    str = "dec_parseEncAPkt_i: " + pMem.idInfo.ui64Id + " pts err, pkt " + pMem.uiPts + " last " + pTransform.audio.decInfo.pts.uiPts_lastRecvd;
                                    showInfo_open.qyFuncs.showInfo_open(str);
                                    Log.e(str);


                                    //  2015/02/20
                                    return iErr;
                                }
                            }
                            //
                        }
                        //pTransform.video.decInfo.pts.uiPts_lastRecvd  =  pPkt->head.uiPts;
                    }
                }
                //  2015/06/18
                if (0 != pMem.uiPts) {
                    pTransform.audio.decInfo.pts.uiPts_lastRecvd = pMem.uiPts;
                }
                //
                ctxQmcTmpl.QMC_cfg_debugStatusInfo pDbgStatusInfo = pProcInfo.cfg.debugStatusInfo;
                //if (pDbgStatusInfo)
                {
                    if (pDbgStatusInfo.ucbShowPreDecAStatus) {
                        /**/
                    }
                }

//                pReq.buf.position(iPos);
                byte[] encData = decParseEncVPktIUseAudioQ2Params.getNextPktInfoToDecParamsA.onePkt.array();


                //
                decParseEncVPktIUseAudioQ2Params.pInput = encData;
                decParseEncVPktIUseAudioQ2Params.uiInputSize = pMem.uiLen;
                //  2015/01/28
                decParseEncVPktIUseAudioQ2Params.uiSampleTimeInMs = pMem.uiSampleTimeInMs;
                //  2015/02/09
                decParseEncVPktIUseAudioQ2Params.uiPts = pMem.uiPts;

                //  2016/12/15
                pTransform.audio.decInfo.st.last_iSampleTimeInMs_preDec = (int) pMem.uiSampleTimeInMs;

            }

            iErr = 0;


            return iErr;
        }


        public static int dec_parseEncAPkt_i(int uiTransformType, Object pParent_transform, qnmCommProc_mis.PKT_info_toDec pPktInfo_toDec_param,int old_pktId, DecMediaCodecCommon dm, qyCommProc_open.dec_parseEncVPkt_i_useAudioQ2_params param) {
            int iErr = -1;
            if(param==null) return iErr;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();    //  QY_GET_procInfo_isCli(  );
            //TCHAR tBuf[256] = "";


            //  myDRAW_VIDEO_DATA  *  pPkt  =  pTransform.video.pCurPkt;
            qnmCommProc_mis.PKT_info_toDec pPktInfo_toDec = pPktInfo_toDec_param;
            {
                ;

                //  检查一下。每个包都只能处理一次，否则，容易死循环
//                if (0 == old_pktId) return -1;
                if (0 == pPktInfo_toDec.lPktId_getNewPkt) {
                    Log.d(("failed: lPktId_alloc 0"));
                    return -1;
                }
                if (pPktInfo_toDec.lPktId_getNewPkt == old_pktId) {
                    Log.d(("failed: lPkdId_alloc==old"));
                    return -1;
                }
                dm.m_old_lPktId = pPktInfo_toDec.lPktId_getNewPkt;

                //
                if (uiTransformType != CONST_uiTransformType_dec) {
                    //MACRO_qyAssert(0, ("dec_parseEncVPkt failed, transformType err"));
                    return -1;
                }
                qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pParent_transform;
                //pProcInfo = (MC_VAR_common)pTransform.pProcInfo;
                if (null == pProcInfo) return -1;

                ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;
                //if (!pQmcCfg) return -1;

                //
                if (!pPktInfo_toDec.bGot_msg) return -1;
                if (!pPktInfo_toDec.info.bGot_pkt) return -1;


                qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead pMem;

                pMem = pPktInfo_toDec.pkt_encA.memHead_a;
                if (0 == pMem.uiLen) {
                    return iErr;
                }
                if (true) {
                    if (0 != pMem.uiPts) {
                        if (0 == pTransform.audio.decInfo.pts.uiPts_lastRecvd) {
                            pTransform.audio.decInfo.pts.uiPts_first = pMem.uiPts;
                            pTransform.audio.decInfo.pts.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);
                        } else {
                            if (pMem.uiPts != pTransform.audio.decInfo.pts.uiPts_lastRecvd + 1) {
                                String str;
                                str = String.format("dec_parseEncAPkt_i: %s: pts err, pkt %s, last %s", pMem.idInfo.ui64Id, pMem.uiPts, pTransform.audio.decInfo.pts.uiPts_lastRecvd);

                                //  2015/02/20
                                return iErr;
                            }
                            //
                        }
                        //pTransform.video.decInfo.pts.uiPts_lastRecvd  =  pPkt->head.uiPts;
                    }
                }
                //  2015/06/18
                if (0 != pMem.uiPts) {
                    pTransform.audio.decInfo.pts.uiPts_lastRecvd = pMem.uiPts;
                }
                //
                ctxQmcTmpl.QMC_cfg_debugStatusInfo pDbgStatusInfo = pProcInfo.cfg.debugStatusInfo;
                //if (pDbgStatusInfo)
                {
                    if (pDbgStatusInfo.ucbShowPreDecAStatus) {
                    }
                }

                //
                byte[] encData = null;// pReq->buf + iPos;
                encData = pPktInfo_toDec.pkt_encA.buf.array();
                if (pPktInfo_toDec.pkt_encA.len != pMem.uiLen) {
                    int ii = 0;
                }
                param.uiInputSize = pMem.uiLen;
                //
                if (false) {
                    String fn = CONST_fn_dump_pcm_enc;
                    //GuiShare.pf_dumpData1.call(0, 0, 0, 0, false, false, fn);
                    guiShare.GuiShare.pf_dumpData1.call(encData, (int) pMem.uiLen, 0, 0, true, false, fn);
                }


                //
//                ppInput = encData;
                //  2015/01/28
                if (0 != param.uiSampleTimeInMs) {
                    param.uiSampleTimeInMs = pMem.uiSampleTimeInMs;
                }
                //  2015/02/09
                if (0 != param.uiPts) {
                    param.uiPts = pMem.uiPts;
                }

                //  2016/12/15
                pTransform.audio.decInfo.st.last_iSampleTimeInMs_preDec = (int) pMem.uiSampleTimeInMs;

            }

            iErr = 0;


            return iErr;
        }


        //
        public static int dec_parseEncVPkt_i(int uiTransformType, Object pParent_transform, qnmCommProc_mis.PKT_info_toDec pPktInfo_toDec_param, DecMediaCodecCommon dm, qyCommProc_open.dec_parseEncVPkt_i_useVideoQ2_params param) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();    //  QY_GET_procInfo_isCli(  );
            //TCHAR tBuf[256] = "";
            String str;


            //  myDRAW_VIDEO_DATA  *  pPkt  =  pTransform.video.pCurPkt;
            qnmCommProc_mis.PKT_info_toDec pPktInfo_toDec = pPktInfo_toDec_param;
            {
                //  检查一下。每个包都只能处理一次，否则，容易死循环
//                if (0 == dm.m_old_lPktId) return -1;
                if (0 == pPktInfo_toDec.lPktId_getNewPkt) {
                    Log.d(("failed: lPktId_alloc 0"));
                    return -1;
                }
                if (pPktInfo_toDec.lPktId_getNewPkt == dm.m_old_lPktId) {
                    Log.d(("failed: lPkdId_alloc==old"));
                    return -1;
                }
                dm.m_old_lPktId = pPktInfo_toDec.lPktId_getNewPkt;

                //
                if (uiTransformType != CONST_uiTransformType_dec) {
                    //MACRO_qyAssert(0, ("dec_parseEncVPkt failed, transformType err"));
                    return -1;
                }
                qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pParent_transform;
                //pProcInfo = (MC_VAR_common)pTransform.pProcInfo;
                if (null == pProcInfo) return -1;

                ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;
                //if (!pQmcCfg) return -1;

                //
                if (!pPktInfo_toDec.bGot_msg) return -1;
                if (!pPktInfo_toDec.info.bGot_pkt) return -1;


                qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem;

                pMem = pPktInfo_toDec.pkt_encV.memHead_v;
                if (0 == pMem.uiLen) {

                    return iErr;
                }
                if (true) {
                    if (0 != pMem.uiPts) {
                        if (0 == pTransform.video.decInfo.pts.uiPts_lastRecvd) {
                            pTransform.video.decInfo.pts.uiPts_first = pMem.uiPts;
                            pTransform.video.decInfo.pts.dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);
                        } else {
                            if (pMem.uiPts != pTransform.video.decInfo.pts.uiPts_lastRecvd + 1) {
                                str = String.format("dec_parseEncVPkt_i: %s: pts err, pkt %s, last %s", pMem.idInfo.ui64Id, pMem.uiPts, pTransform.video.decInfo.pts.uiPts_lastRecvd);
                                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                                //  2015/02/20
                                return iErr;
                            }
                            //
                        }
                        //pTransform.video.decInfo.pts.uiPts_lastRecvd  =  pPkt->head.uiPts;		
                    }
                }
                //  2015/06/18
                if (0 != pMem.uiPts) {
                    pTransform.video.decInfo.pts.uiPts_lastRecvd = pMem.uiPts;
                }
                //
                ctxQmcTmpl.QMC_cfg_debugStatusInfo pDbgStatusInfo = pProcInfo.cfg.debugStatusInfo;
                //if (pDbgStatusInfo)
                {
                    if (pDbgStatusInfo.ucbShowPreDecAStatus) {
                    }
                }

                //
                byte[] encData = null;// pReq->buf + iPos;
                encData = pPktInfo_toDec.pkt_encV.buf.array();
                if (pPktInfo_toDec.pkt_encV.len != pMem.uiLen) {
                    int ii = 0;
                }


                //
                if (false) {
                    String fn = CONST_fn_dump_h264_enc;
                    //GuiShare.pf_dumpData1.call(0, 0, 0, 0, false, false, fn);
                    guiShare.GuiShare.pf_dumpData1.call(encData, (int) pMem.uiLen, 0, 0, true, false, fn);
                }

                //  2014/12/06

                //
                int iFourcc_enc = pTransform.video.tv.vh_stream.dwFourcc;
                if (iFourcc_enc == CONST_fourcc_HEVC) {

                    //
                    int nalType = M_nalType_h265(encData);

                    //
                    if  (  false  ) {
                        //
                        Log.d("qy", "nalType is " + nalType);
                        switch (nalType) {
                            case NAL_IDR_W_RADL:
                            case NAL_IDR_N_LP:
                            case NAL_VPS:
                                Log.d("qy", "keyFrame");
                                break;
                            default:
                                break;
                        }
                    }

                    //
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
                        //
                        if (!ucbKeyFrame) {
                            //
                            str = ("dec_parseEncVPkt_i failed: "  +  pTransform.video.idInfo.ui64Id  +  " "  +  pTransform.video.tv.uiTranNo_openAvDev  +  " not a keyFrame: need a keyFrame first");
                            str  +=  " pts "  +  pMem.uiPts;
                            Log.d("qy",  str);
                            showInfo_open.qyFuncs.showInfo_open(0, null,str);
                            //
                            chkToForceKeyFrame(null, pTransform);
                            //
                            iErr = CONST_qyRet_needKeyFrameFirst;
                            //
                            return iErr;
                        }
                        //
                        str = ("dec_parseEncVPkt_i get a first keyFrame: "  +  pTransform.video.idInfo.ui64Id  +  " "  +  pTransform.video.tv.uiTranNo_openAvDev  +  "");
                        str  +=  " pts "  +  pMem.uiPts;
                        Log.d("qy",  str);
                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                        //
                        pTransform.video.decInfo.bGot_keyFrame = true;
                    }

                }

                //  2014/
                //chkToForceKeyFrame(null, pTransform);


                //  2014/


                //

                param.pInput = encData;
                param.uiInputSize = pMem.uiLen;
                //  2015/01/28
                if (0 != param.uiSampleTimeInMs) {
                    param.uiSampleTimeInMs = pMem.uiSampleTimeInMs;
                }
                //  2015/02/09
                if (0 != param.uiPts) {
                    param.uiPts = pMem.uiPts;
                }
            }

            iErr = 0;


            return iErr;
        }


        //
        public static int doDecodeVideo(qmcTransform.QY_TRANSFORM pTransform, qnmCommProc_mis.MIS_MSGU pMsgBuf_unused) {
            int iErr = -1;

            int i;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if(pQyMc == null)
            {
                Log.e("pQyMc == null");
                return iErr;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//QY_GET_procInfo_isCli(  );
            if (null == pProcInfo) return -1;

            ctxQmc.QMC_cfg pQmcCfg = pProcInfo.cfg;


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
            ctxCaller.CTX_caller ctxCaller = new ctxCaller.CTX_caller();
            //mymemset((byte[])ctxCaller, 0, sizeof(CTX_caller));

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) getQmcTaskInfoByIndex(ctxCaller, (ctxQmc.CCtxQmc) pProcInfo, pTask.iIndex_taskInfo, "doDecodeVideo");
            if (null == pTaskInfo) return -1;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null) return -1;
            if (pTaskData.uiType != CONST_taskDataType_conf) return -1;
            qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
            qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();


            //
            qmcTransform.TRANSFORM_VIDEO_procInfo pTransformVideo = pTransform.video;
            int index_activeMems_from = -1;// pTransform.index_activeMems_from;
            index_activeMems_from = pTransform.get_index_activeMems_from_decV();

            //
            //pTransform.index_activeMems_from_decV_bak = index_activeMems_from;

            //
            if (index_activeMems_from < 0 || index_activeMems_from >= videoConference.usCntLimit_activeMems_from) {
                String str = "doDecodeVideo failed, index_activeMems_from err";
                Log.d(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                //
                return -1;
            }
            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index_activeMems_from];


            int uiModuleType_toDec = 0;
            switch (pActiveMem_from.avStream.obj.tranInfo.video.compressor.ucCompressors) {
                case CONST_videoCompressors_stream:
                case CONST_videoCompressors_hwAccl:
                case CONST_videoCompressors_ffmpeg:
                    uiModuleType_toDec = CONST_moduleType_mediaSdk_dec;
                    break;
                case CONST_videoCompressors_ipp:
                    uiModuleType_toDec = CONST_moduleType_ipp;
                    break;
                case CONST_videoCompressors_vp8:                            //  2014/07/21
                    uiModuleType_toDec = CONST_moduleType_dec_vp8;
                    break;
                default:
                    break;
            }

            uiModuleType_toDec = adjustModuleType_decV(uiModuleType_toDec);


            switch (uiModuleType_toDec) {
                case CONST_moduleType_mediaCodec:
                    return guiShare.GuiShare.pf_doDecodeVideo_mediaCodec.call(pTransform, index_activeMems_from, pMsgBuf_unused);

                default:
                    break;
            }

            String str;
            str = String.format("doDecodeVideo failed, unsupported moduleType {%s}", uiModuleType_toDec);
            Log.d(str);
            showInfo_open.qyFuncs.showInfo_open(0, null,str);
            if (uiModuleType_toDec != 0) {
                showTaskStatusFunc.qyFuncs.showNotification_open(0, 0, 0, str);
            }

            iErr = 0;

            return iErr;
        }

    }
}

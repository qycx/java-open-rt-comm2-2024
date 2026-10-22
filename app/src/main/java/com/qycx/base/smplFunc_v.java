


package com.qycx.base;

import static com.qycx.base.Consts.CONST_iParamType_decv;
import static com.qycx.base.Consts.CONST_qyRet_needKeyFrameFirst;
import static com.qycx.base.Consts.VIDEO_DOWNLOAD;
import static com.qycx.base.Consts.VIDEO_DOWNLOAD1;
import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.dec_parseEncVPkt_i.qyFuncs.dec_parseEncVPkt_i_useVideoQ2;
import static com.qycx.base.qmcDecAudio.qyFuncs.clean_PKT_info_toDec;
import static com.qycx.base.qmcDecAudio.qyFuncs.getNextPktInfo_toDec;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;

import android.annotation.SuppressLint;

import com.qycx.base.qdc.DecMediaCodecCommon;
import com.qycx.base.qdc.decObj_mediaCodec;
import com.qycx.base.util.Log;

import java.nio.ByteBuffer;

public class smplFunc_v {

    public static class qyFuncs {

        //
        public static qyCommProc_open.PF_smplBitstreamReader_ReadNextFrame_func smplBitstreamReader_ReadNextFrame_func_v=(DecMediaCodecCommon dm, qyCommProc_open.ReadNextFrameFucParams readNextFrameFucParams)-> {
            readNextFrameFucParams.uiPts = 0;
            readNextFrameFucParams.param=new qmcVideoCapture.SMPL_bsRead_param();
            //mymemset((byte[])param, 0, sizeof(SMPL_bsRead_param));

            //				
            return smplBitstreamReader_ReadNextFrame_func_v_new(dm, readNextFrameFucParams);
        };


        // 视频流解析:
        @SuppressLint("DefaultLocale")
        public static int smplBitstreamReader_ReadNextFrame_func_v_new(DecMediaCodecCommon dm, qyCommProc_open.ReadNextFrameFucParams readNextFrameFucParams) {
            int iErr = -1;

            String str;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            int nBytesRead = 0;
            long time = myGetTickCount(null);
            long time1 = 0;
            long time2 = 0;
            long time3 = 0;
            long time4 = 0;
            long time5 = 0;
            long time6 = 0;
            long time7 = 0;
            long time8 = 0;
            long time9 = 0;
            int size = 0;
            int nwait = 0;
            long tick_beforeGet = 0;
            long tick_afterGet  =  0;
            long  lDiffInMs  =  0;
            long tick_beforeParse = 0;
            long tick_afterParse  = 0;


            //
            qyCommProc_open.dec_parseEncVPkt_i_useVideoQ2_params decParseEncVPktIUseVideoQ2Params = new qyCommProc_open.dec_parseEncVPkt_i_useVideoQ2_params();
//            byte[] pInput = null;    //  m_var.inputInfo.pInput;
//            int uiInputSize = 0;  //  m_var.inputInfo.uiInputSize;
//            int uiSampleTimeInMs = 0; //  2015/01/28
//            int uiPts = 0;                //  2015/02/09
            //TCHAR tBuf[256];

            qdcCommProc_open.QOI_common pQdcObjInfo = dm.m_qoi;

            //  QDC_OBJ_hdDec	*	pObj	=	(  QDC_OBJ_hdDec  *  )pQdcObjInfo->pObj;
            //CMyDecPipeline  *  pPipeline  =  (  CMyDecPipeline  *  )pObj->pPipeline;
            //
            if (!pQdcObjInfo.var.ucbTransformFuncsOk) {
                Log.d( ("CMySmpl::ReadNextFrame failed, ucbTransformFuncsOk false"));
                showInfo_open.qyFuncs.showInfo_open(0, null, ("CMySmpl::ReadNextFrame failed, ucbTransformFuncsOk false"));
                return -1;
            }

            //
            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pQdcObjInfo.cfg.v.pParent_transform;
            String str1;
            qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(CONST_iParamType_decv,0);

            for (; ; ) {

                //  2014/07/26
                long dwTickCnt_start = tmpFunc.qyFuncs.mytimeGetTime();

                //
                for (; ; ) {
                    if (pQdcObjInfo.cfg.v.pf_bTransformVideoQuit.call(pQdcObjInfo.cfg.v.pParent_transform, null, null))
                        return -1;
                    //
                    //
                    qyq2.QY_Q2 pDataQ2 = pTransform.video.q2;
                    pDataQ2 = pTransform.v_preTransThread.q2;
                    if (pProcInfo.cfg.b_useVideoQ2_toDecV) {
                        //
                        showInfo_open.qyFuncs.showInfo_open("getNextPktInfo_toDec start");

                        if (0 == getNextPktInfo_toDec( pTransform.video.curPktInfo_toDec, pDataQ2, decParseEncVPktIUseVideoQ2Params.getNextPktInfoToDecParamsV)) {
                            //
                            showInfo_open.qyFuncs.showInfo_open("getNextPktInfo_toDec: break: pts： " + decParseEncVPktIUseVideoQ2Params.getNextPktInfoToDecParamsV.head.uiPts);
                            //
                            break;
                        }
                        showInfo_open.qyFuncs.showInfo_open("getNextPktInfo_toDec: not break");
                    } else {
                        tick_beforeGet = myGetTickCount(null);

                        //showInfo_open.qyFuncs.showInfo_open("smpl_v: getNextPktInfo_toDec start"+ " size: is" + pTransform.v_preTransThread.qBuf2.size());;
                        size = pTransform.v_preTransThread.qBuf2.size();
                        //Log.d(VIDEO_DOWNLOAD1, "smpl_v: getNextPktInfo_toDec start" + time1
                        if (0 == getNextPktInfo_toDec(tmpVar, pTransform.video.curPktInfo_toDec, pTransform.v_preTransThread.qBuf2)) {
                            {
                                tick_afterGet=myGetTickCount(null);

                                long id = ((qmcTransform.QY_TRANSFORM) dm.m_qoi.cfg.v.pParent_transform).video.idInfo.ui64Id;
                               // com.qycx.base.util.Log.d(Consts.VIDEO_DOWNLOAD,"4.5 read video id:"+ id);
                                //time3 = time1 - myGetTickCount(null);
                                //str1 = ("smpl_v: getNextPktInfo_toDec: break: pts： " + " mytime:" + time3 );
                                //Log.d(VIDEO_DOWNLOAD1, str1);
                                //showInfo_open.qyFuncs.showInfo_open(str1);
                                break;
                            }
                        }
                        tick_afterGet = myGetTickCount(null);
                        lDiffInMs=tick_afterGet-tick_beforeGet;
                        if  (lDiffInMs  >  0  ) {

                            //
                            if  (  pProcInfo.cfg.debugStatusInfo.b_print_test) {
                                showInfo_open.qyFuncs.showInfo_open("smpl_v:getNextPktInfo_toDec consts " + lDiffInMs + "ms");
                                //Log.d(VIDEO_DOWNLOAD1, "smpl_v:getNextPktInfo_toDec: not break");
                            }
                        }
                    }


                    //  2014/07/26
                    if (0 != readNextFrameFucParams.uiToInMs) {
                        long dwTickCnt = tmpFunc.qyFuncs.mytimeGetTime();
                        if (dwTickCnt - dwTickCnt_start > readNextFrameFucParams.uiToInMs) {
                            str = String.format("SmplBsReader::LoadNextFrame failed, timeout. toInMs %d", readNextFrameFucParams.uiToInMs);
                            showInfo_open.qyFuncs.showInfo_open(0,null,str);
                            return iErr;
                        }
                    }


                    //
                    int nWaitInMs = 33;
                    //
                    //nWaitInMs = 100;    //  2015/02/06. 是不是放大点能让cpu更悠闲点。但是，不能影响获取包的实时性
                    //
                    int dwRet;
                    qyq2.QY_Q2 pVideoQ2 = (qyq2.QY_Q2) pQdcObjInfo.cfg.v.pVideoQ2;
                    pVideoQ2 = pDataQ2;


                    if (pProcInfo.cfg.b_useVideoQ2_toDecV) {
                        myWaitOne(pVideoQ2.hEvent0, nWaitInMs);
                    } else {
                        myWaitOne(pTransform.v_preTransThread.qBuf2.hEvent0, nWaitInMs);

                    }
                    time3 = myGetTickCount(null) - time;
                    nwait++;
                    //
                    continue;
                }
                //

                //
                int tmpiRet = -1;
                if (pProcInfo.cfg.b_useVideoQ2_toDecV) {
                    //showInfo_open.qyFuncs.showInfo_open("dec_parseEncVPkt_i_useVideoQ2 start");

                    tmpiRet = dec_parseEncVPkt_i_useVideoQ2(
                            pQdcObjInfo.cfg.v.uiTransformType_pParent,
                            pQdcObjInfo.cfg.v.pParent_transform,
                            pTransform.video.curPktInfo_toDec,
                            dm,
                            decParseEncVPktIUseVideoQ2Params,
                            readNextFrameFucParams.param);
                    showInfo_open.qyFuncs.showInfo_open("dec_parseEncVPkt_i_useVideoQ2 end");

                } else {
                    time4 = myGetTickCount(null) - time;
                    ///time2 = myGetTickCount(null);
                   // showInfo_open.qyFuncs.showInfo_open("smpl_v:dec_parseEncVPkt_i_useVideoQ2 start " + time2);
                    //str1 = "smpl_v:dec_parseEncVPkt_i_useVideoQ2 start " + time2;
                    //Log.d(VIDEO_DOWNLOAD1, str1);
                    tick_beforeParse = myGetTickCount(null);
                    tmpiRet = qyMcMainMediaProc_decVideo_rtsp.qyFuncs.dec_parseEncVPkt_i(
                            pQdcObjInfo.cfg.v.uiTransformType_pParent,
                            pQdcObjInfo.cfg.v.pParent_transform,
                            pTransform.video.curPktInfo_toDec,
                            dm,
                            decParseEncVPktIUseVideoQ2Params
                            );
                    tick_afterParse = myGetTickCount(null);
                    //time3 = myGetTickCount(null) - time1;
                    //str1 = "smpl_v:dec_parseEncVPkt_i_useVideoQ2 end" + time3;
                    //Log.d(VIDEO_DOWNLOAD1, str1);
                    //showInfo_open.qyFuncs.showInfo_open(str1);
                    time5 = myGetTickCount(null) - time;
                }

                if (tmpiRet == CONST_qyRet_needKeyFrameFirst) {
                    //
                    showInfo_open.qyFuncs.showInfo_open("smplBitstreamReader_v. needKeyFrameFirst");

                    //
                    clean_PKT_info_toDec(pTransform.video.curPktInfo_toDec, (""));
                    continue;
                }
                if (0 != tmpiRet) {
                    //Log.d( ("doTrans_default failed, dec_parseEncVPkt_i failed"));
                    showInfo_open.qyFuncs.showInfo_open(0, null, ("doTrans_default failed, dec_parseEncVPkt_i failed"));
                    return -1;
                }
                //
                break;
            }

            //
            if (0 == decParseEncVPktIUseVideoQ2Params.uiInputSize) {
                //
                showInfo_open.qyFuncs.showInfo_open(0, null,("CMySmpl::ReadNextFrame failed, 239.uiInputSize 0"));
                return -1;
            }
            if (decParseEncVPktIUseVideoQ2Params.uiInputSize > readNextFrameFucParams.nBytesRead)
            {
                str = String.format("CMySmpl~::ReadNextFrame failed, 244. inputSize %d, dataSize %d:", decParseEncVPktIUseVideoQ2Params.uiInputSize,  readNextFrameFucParams.nBytesRead);
                //Log.d( str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                return -1;
            }

//            System.arraycopy( decParseEncVPktIUseVideoQ2Params.pInput,0, dm.m_tmpDataBuf, 0, (int) decParseEncVPktIUseVideoQ2Params.uiInputSize);
            if (pProcInfo.cfg.b_useVideoQ2_toDecV) {
                System.arraycopy(decParseEncVPktIUseVideoQ2Params.getNextPktInfoToDecParamsV.onePkt.array(), 0, dm.m_tmpDataBuf, 0, (int) decParseEncVPktIUseVideoQ2Params.uiInputSize);
            }else {
                pTransform.video.curPktInfo_toDec.pkt_encV.buf.get(dm.m_tmpDataBuf, 0, pTransform.video.curPktInfo_toDec.pkt_encV.len);
            }
            nBytesRead = decParseEncVPktIUseVideoQ2Params.uiInputSize;


            if (0 == nBytesRead) {
                return -1;
            }

            //
            if  (  true ) {
                //Log.e("tn_msgInput", "smpl_v: tn_msgInput " + pTransform.video.curPktInfo_toDec.pkt_encV.memHead_v.tmp_tn_msgInput + " pts " + pTransform.video.curPktInfo_toDec.pkt_encV.memHead_v.uiPts);
                time6 = myGetTickCount(null) - time;
                //time4 = myGetTickCount(null) - time2;
                //str  =  "smpl_v: tn_msgInput " + pTransform.video.curPktInfo_toDec.pkt_encV.memHead_v.tmp_tn_msgInput + " pts " + pTransform.video.curPktInfo_toDec.pkt_encV.memHead_v.uiPts + " from get:" + time3+ " from dec:" + time4;
                //showInfo_open.qyFuncs.showInfo_open(str);
                //Log.d(VIDEO_DOWNLOAD1, str);
            }


            //  pBS->DataLength += nBytesRead;    
            readNextFrameFucParams.nBytesRead = nBytesRead;
            //  2015/01/28
            readNextFrameFucParams.uiSampleTimeInMs = decParseEncVPktIUseVideoQ2Params.uiSampleTimeInMs;
            readNextFrameFucParams.uiPts = decParseEncVPktIUseVideoQ2Params.uiPts;    //  2015/02/09

            //
            clean_PKT_info_toDec(pTransform.video.curPktInfo_toDec, (""));
            //
            iErr = 0;

            //
            if  (  pProcInfo.cfg.debugStatusInfo.b_print_test) {
                time7 = myGetTickCount(null);
                str = "smpl_v: tn_msgInput" + " nwait:" + nwait +
                        " qsize: " + size + " e_beforeGet " + (tick_beforeGet - time) +
                        " Get costs: " + (tick_afterGet - tick_beforeGet) +
                        "  e_beforeParse:" + (tick_beforeParse - time) + " Parse costs: " +
                        (tick_afterParse - tick_beforeParse) + " totoal : " + (time7 - time);

                showInfo_open.qyFuncs.showInfo_open(str);
            }

            //
            return iErr;
        }

    }
}

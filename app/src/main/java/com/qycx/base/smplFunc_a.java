


package com.qycx.base;

import static com.qycx.base.Consts.CONST_iParamType_deca;
import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.qmcDecAudio.qyFuncs.clean_PKT_info_toDec;
import static com.qycx.base.qmcDecAudio.qyFuncs.getNextPktInfo_toDec;
import static com.qycx.base.qyMcMainMediaProc_decVideo_rtsp.qyFuncs.dec_parseEncAPkt_i;
import static com.qycx.base.qyMcMainMediaProc_decVideo_rtsp.qyFuncs.dec_parseEncAPkt_i_useAudioQ2;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;


import com.qycx.base.qdc.DecMediaCodecCommon;
import com.qycx.base.qdc.decObj_mediaCodec;
import com.qycx.base.util.Log;

public class smplFunc_a {

    public static class qyFuncs {

        public static qyCommProc_open.PF_smplBitstreamReader_ReadNextFrame_func smplBitstreamReader_ReadNextFrame_func_a= (DecMediaCodecCommon dm, qyCommProc_open.ReadNextFrameFucParams readNextFrameFucParams) ->{
            readNextFrameFucParams.uiPts = 0;
            readNextFrameFucParams.param=new qmcVideoCapture.SMPL_bsRead_param();
            return smplBitstreamReader_ReadNextFrame_func_a_new(dm, readNextFrameFucParams);
        };

        public static int smplBitstreamReader_ReadNextFrame_func_a_new(DecMediaCodecCommon dm, qyCommProc_open.ReadNextFrameFucParams readNextFrameFucParams) {
            int iErr = -1;

            long tick_start = myGetTickCount(null);
            //long time1 = 0;
            //long time2 = 0;
            long tick_beforeGet  =  0;
            long tick_afterGet  =  0;
            long tick_beforeParse  =  0;
            long tick_afterParse  =  0;
            int  nWaits  =  0;




            //
            String str;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            int nBytesRead = 0;


            qyCommProc_open.dec_parseEncVPkt_i_useAudioQ2_params decParseEncVPktIUseAudioQ2Params = new qyCommProc_open.dec_parseEncVPkt_i_useAudioQ2_params();
//            byte[] pInput = null;    //  m_var.inputInfo.pInput;
//            int uiInputSize = 0;  //  m_var.inputInfo.uiInputSize;
//            int uiSampleTimeInMs = 0; //  2015/01/28
//            int uiPts = 0;                //  2015/02/09
            //TCHAR tBuf[256];

            qdcCommProc_open.QOI_common pQdcObjInfo = dm.m_qoi;

            if (!pQdcObjInfo.var.ucbTransformFuncsOk) {
                Log.d( ("CMySmpl::ReadNextFrame failed, ucbTransformFuncsOk false"));
                showInfo_open.qyFuncs.showInfo_open(0, null, ("CMySmpl::ReadNextFrame failed, ucbTransformFuncsOk false"));
                return -1;
            }

            //  2016/12/13
            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) pQdcObjInfo.cfg.a.pParent_transform;

            //  2014/07/26
            long dwTickCnt_start = tmpFunc.qyFuncs.mytimeGetTime();

            qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(CONST_iParamType_deca,0);
            //
            for (; ; ) {
                if (pQdcObjInfo.cfg.a.pf_bTransformAudioQuit.call(pQdcObjInfo.cfg.a.pParent_transform, 0, 0))
                    return -1;

                //
                //  2016/12/13
                // fixme to reference video
                if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
                    //
                    pTransform.audio.curPktInfo_toDec.bAudio_forDbg=true;
                    //
                    if (0 == getNextPktInfo_toDec( pTransform.audio.curPktInfo_toDec, pTransform.audio.q2, decParseEncVPktIUseAudioQ2Params.getNextPktInfoToDecParamsA)) {
                        //
                        if (decParseEncVPktIUseAudioQ2Params.getNextPktInfoToDecParamsA.head == null) {
                            int ii = 0;
                            ii = 0;
                            ii++;
                            System.out.println(ii);
                        }
                        if (pTransform.audio.curPktInfo_toDec.bGot_msg == false) {
                            int ii = 0;
                            System.out.println(ii);
                        }

                        //
                        break;
                    }
                } else {

                   tick_beforeGet = myGetTickCount(null);
                   //
                    if (0 == getNextPktInfo_toDec(tmpVar, pTransform.audio.curPktInfo_toDec, pTransform.audio.qBuf2))
                    {
                        long id = ((qmcTransform.QY_TRANSFORM) dm.m_qoi.cfg.a.pParent_transform).audio.idInfo.ui64Id;
                        //com.qycx.base.util.Log.d(Consts.AUDIO_DOWNLOAD,"4.5 read audio id:"+ id);
                        tick_afterGet =  myGetTickCount(null);
                        //
                        break;
                    }

                }


                //  2014/07/26

                if (0 != readNextFrameFucParams.uiToInMs) {
                    long dwTickCnt = tmpFunc.qyFuncs.mytimeGetTime();
                    if (dwTickCnt - dwTickCnt_start > readNextFrameFucParams.uiToInMs) {
                        str = String.format("SmplBsReader::LoadNextFrame failed, timeout. toInMs %d", readNextFrameFucParams.uiToInMs);
                        Log.e(str);
                        showInfo_open.qyFuncs.showInfo_open(0, null, str);
                        return iErr;
                    }
                }


                //

                //
                int nWaitInMs = 40;
                //
                nWaitInMs = 100;    //  2015/02/06. 是不是放大点能让cpu更悠闲点。但是，不能影响获取包的实时性
                //
                int dwRet;
                if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
                    qyq2.QY_Q2 pAudioQ2 = (qyq2.QY_Q2) pQdcObjInfo.cfg.a.pAudioQ2;
                    myWaitOne(pAudioQ2.hEvent0, nWaitInMs);
                } else {
                    myWaitOne(pTransform.audio.qBuf2.hEvent0, nWaitInMs);

                }

                //
                nWaits  ++  ;

                //
                continue;
            }

            if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
                if (0 != dec_parseEncAPkt_i_useAudioQ2(
                        pQdcObjInfo.cfg.a.uiTransformType_pParent,
                        pQdcObjInfo.cfg.a.pParent_transform,
                        pTransform.audio.curPktInfo_toDec,
                        dm,
                        decParseEncVPktIUseAudioQ2Params
                ))
                    {
                    Log.e( ("doTrans_default failed, parsePkt failed"));
                        showInfo_open.qyFuncs.showInfo_open(0, null, ("doTrans_default failed, parsePkt failed"));
                    return -1;
                }
            } else {
                    tick_beforeParse = myGetTickCount(null);
                    //
                if (0 != dec_parseEncAPkt_i(
                        pQdcObjInfo.cfg.a.uiTransformType_pParent,
                        pQdcObjInfo.cfg.a.pParent_transform,
                        pTransform.audio.curPktInfo_toDec,
                        dm.m_old_lPktId,
                        dm,
                        decParseEncVPktIUseAudioQ2Params))
                {
                    Log.e( ("doTrans_default failed, parsePkt failed"));
                    showInfo_open.qyFuncs.showInfo_open(0, null,"doTrans_default failed, parsePkt failed");
                    return -1;
                }
                    tick_afterParse = myGetTickCount(null);

            }
            if (0 == decParseEncVPktIUseAudioQ2Params.uiInputSize) {
                Log.d( ("CMySmpl::ReadNextFrame failed, 187.uiInputSize 0"));
                showInfo_open.qyFuncs.showInfo_open(0, null, ("CMySmpl::ReadNextFrame failed, 188.uiInputSize 0"));
                return -1;
            }
            if (decParseEncVPktIUseAudioQ2Params.uiInputSize > readNextFrameFucParams.nBytesRead)
            {
                str = String.format("CMySmpl~::ReadNextFrame failed, 193. inputSize %d, dataSize %d:", decParseEncVPktIUseAudioQ2Params.uiInputSize,  readNextFrameFucParams.nBytesRead);
                Log.d( str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                return -1;
            }

            //
            if  (  true  )  {
                if  (  pProcInfo.cfg.b_useAudioQ2_toDecA) {
                    str  =  "smpl_a: useQ2: pts "  +  decParseEncVPktIUseAudioQ2Params.getNextPktInfoToDecParamsA.head.uiPts  +  " len "  + decParseEncVPktIUseAudioQ2Params.getNextPktInfoToDecParamsA.head.uiLen;
                    showInfo_open.qyFuncs.showInfo_open(str);
                }
            }


            //  2014/08/05
            //postAPktToSave(pQdcObjInfo.cfg.a.uiTransformType_pParent, pQdcObjInfo->cfg.a.pParent_transform, pQdcObjInfo->cfg.a.iDecParam_index_pMems_from, uiSampleTimeInMs, uiPts, (String)pInput, uiInputSize);
            //
            //  memcpy(  pBS->Data + pBS->DataLength, pInput,  uiInputSize  );
            //mymemcpy(pBsBuf,pInput, uiInputSize);
//            System.arraycopy( decParseEncVPktIUseAudioQ2Params.pInput,0, dm.m_tmpDataBuf, 0, (int) decParseEncVPktIUseAudioQ2Params.uiInputSize);
            if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
                System.arraycopy(decParseEncVPktIUseAudioQ2Params.getNextPktInfoToDecParamsA.onePkt.array(), 0, dm.m_tmpDataBuf, 0, (int) decParseEncVPktIUseAudioQ2Params.uiInputSize);
            }else{
                pTransform.audio.curPktInfo_toDec.pkt_encA.buf.get(dm.m_tmpDataBuf,0,pTransform.audio.curPktInfo_toDec.pkt_encA.len);
            }
            nBytesRead = decParseEncVPktIUseAudioQ2Params.uiInputSize;
            //
            //m_var.inputInfo.bFinished  =  TRUE;

            if (0 == nBytesRead) {
                return -1;
            }

            //  pBS->DataLength += nBytesRead;
			readNextFrameFucParams.nBytesRead = nBytesRead;
            //  2015/01/28
            readNextFrameFucParams.uiSampleTimeInMs = decParseEncVPktIUseAudioQ2Params.uiSampleTimeInMs;
            readNextFrameFucParams.uiPts = decParseEncVPktIUseAudioQ2Params.uiPts;    //  2015/02/09

            //
            //  2016/12/13
            clean_PKT_info_toDec(pTransform.audio.curPktInfo_toDec, (""));


            //
            iErr = 0;

            if  (  pProcInfo.cfg.debugStatusInfo.b_print_test) {
                long tick_end = myGetTickCount(null);
                str = "smpl_a: nWaits " + nWaits + " total  " + (tick_end - tick_start);
                showInfo_open.qyFuncs.showInfo_open(str);
            }

            //
            return iErr;
        }


    }


}

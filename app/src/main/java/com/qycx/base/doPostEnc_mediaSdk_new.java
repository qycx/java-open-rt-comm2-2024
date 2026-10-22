


package com.qycx.base;

import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.dec_parseEncVPkt_i.qyFuncs.M_nalType_h265;
import static com.qycx.base.findOrgMsgAndProcess_is.qyFuncs.canSend;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferVideoData;
import static com.qycx.base.qisChannel.qyFuncs.startChannel;
import static com.qycx.base.qmcSharedObj.Consts.MAX_cnt_preEnc_array;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcVideoFunc.qyFuncs.getSharedObjUsr;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_TRANSFER_VIDEO_dataMemHead_mems;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferVideoData;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.chkToApplyForChannel;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyCommProc_open.Consts.CONST_pktResType_sharedTex;
import static com.qycx.base.qyDefs_open.Consts.NAL_IDR_N_LP;
import static com.qycx.base.qyDefs_open.Consts.NAL_IDR_W_RADL;
import static com.qycx.base.qyDefs_open.Consts.NAL_VPS;
import static com.qycx.base.qyq2.qyFuncs.q2PostMsgAndTrigger;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import com.qycx.base.util.Log;

import java.nio.ByteBuffer;

public class doPostEnc_mediaSdk_new {
    public static class qyFuncs {
        private static int count_video = 0;
        private static int count_not_ok = 1;

        public static boolean bCapBmpQuit(Object pCAP_procInfo_bmpCommon, Object p1, Object p2) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ;
            if (null == pQyMc) return false;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();  //  QY_GET_procInfo_isCli(  );
            if (null == pProcInfo) return false;

            qmcSharedObj.CAP_procInfo_bmpCommon pCapProcInfo = (qmcSharedObj.CAP_procInfo_bmpCommon) pCAP_procInfo_bmpCommon;
            if (null == pCapProcInfo) return false;

            qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, pCapProcInfo.iIndex_sharedObj);
            if (null == pSharedObj) return false;

            if (!bSharedObjActive(pProcInfo, pCapProcInfo.iIndex_sharedObj)) {
                return true;
            }
            return pCapProcInfo.thread.bQuit;
        }


        public static boolean bSharedObjActive(ctxQmc.CCtxQmc pProcInfo, int index) {
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, index);
            if (null == pSharedObj) return false;

            if (pSharedObj.var.bQuit) return false;

            //
            return true;
        }

        public static int doPostEnc_mediaSdk_new(qdcCommProc_open.QOI_common pQDC_OBJ_INFO, byte[] pInput, int srcPos, int inputSize, int iSampleTimeInMs, int uiPts, byte ucbKeyFrame, qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead tmp_head) {
            int iErr = -1;
//            Log.e("doPostEnc_mediaSdk_new 1");

            String str;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) {
                return -1;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) {
                return -1;
            }
//            Log.e("doPostEnc_mediaSdk_new 2");

            qmcSharedObj.CAP_procInfo_bmpCommon pCapProcInfo = (qmcSharedObj.CAP_procInfo_bmpCommon) pQDC_OBJ_INFO.cfg.v.pParent_transform;

            boolean bMsgGot = false;

            qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, pCapProcInfo.iIndex_sharedObj);
            if (null == pSharedObj) {
                if (bMsgGot)
                {
                  //qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA.call(pPkt, (""));
                }
                return iErr;
            }

            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = getSharedObjUsr(pSharedObj, pSharedObj.var.iIndex_curUsr);
            if (null == pSharedObjUsr) {
                if (bMsgGot)
                {
                }
                return iErr;
            }
            if (pSharedObjUsr.uiTranNo_openAvDev_v == 0) {
                str = "doPostEnc_mediaSdk_new failed, sharedObjUsr.uiTranNo_openAvDev_v is 0";
                Log.e(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                if (bMsgGot)
                {
                }
                return iErr;
            }

            //
            pProcInfo.av.localAv.dwLastTickCnt_vCap=myGetTickCount(null);

            //
            boolean  bSend  = true;
            bSend=canSend(pSharedObj);
            if  (  !bSend)  {
                return  iErr;
            }


            //
            int nalType  =  M_nalType_h265(pInput);
            switch (nalType) {
                case NAL_IDR_W_RADL:
                case NAL_IDR_N_LP:
                case NAL_VPS:
                    //
                    //Log.d("qy", "doPostEnc_mediaSdk_new: keyFrame");
                    //
                    break;
                default:
                    //
                    if  (  false  ) {
                        if (pProcInfo.bUse100k) {
                            Log.e("qy", "err: for 100k, not a key frame. skipped");
                            return iErr;
                        }
                    }
                    //
                    break;
            }

            //
            if  (  true  )  {
                //Log.d("qy","doPostEnc: nalType "  +  nalType  +  " inputSize "  +  inputSize  +  "");
            }


            //
            int myuiSampleTimeInMs = 0;
            int myuiPts = 0;  //  2015/01/15

            if (!pCapProcInfo.compressVideo.ucbUseEvt) {
                myuiPts = pCapProcInfo.compressVideo.encInfo.cnt_postEnc;
            } else {  //  2015/10/20
                myuiPts = uiPts;
            }

            pCapProcInfo.compressVideo.encInfo.iSampleTimeInMs_postEnc = iSampleTimeInMs;
            pCapProcInfo.compressVideo.encInfo.iNextIndex_postEnc++;
            if (pCapProcInfo.compressVideo.encInfo.iNextIndex_postEnc >= MAX_cnt_preEnc_array) {
                pCapProcInfo.compressVideo.encInfo.iNextIndex_postEnc = pCapProcInfo.compressVideo.encInfo.iNextIndex_postEnc % MAX_cnt_preEnc_array;
            }
            pCapProcInfo.compressVideo.encInfo.cnt_postEnc++;

            //  2008/11/12,
            if (0 != iSampleTimeInMs) {
                myuiSampleTimeInMs = iSampleTimeInMs;// pSharedObj.iDiffInMs_tStartAudio;	//  2009/05/26
            }

            //
            if  (  !pProcInfo.cfg.b_useQ2_postEncV)  {
                qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem = tmp_head;//new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();
                pMem.memset0();
                //head.copyFrom(pMem);
                pMem.uiLen = inputSize;



                pMem.uiSampleTimeInMs = myuiSampleTimeInMs;
                //  2015/01/15
                pMem.uiPts = myuiPts;
                pMem.ucbKeyFrame = ucbKeyFrame;

                //
                pMem.uiTranNo_openAvDev  =  pSharedObjUsr.uiTranNo_openAvDev_v;
                //
                //head.tmp_pkt=pPkt;
                //
                /*
                if  (  0!=  pCapProcInfo.postThread.qBuf2.q2PostMsgAndTrigger(pMem,pInput,srcPos,inputSize,"doPostEncV")  )  {

                    //
                    Log.e("qy","doPostEncV: q2PostMsgAndTrigger failed");
                }

                 */
                if  (  0!=  pProcInfo.pOutputQBuf2_local_v.q2PostMsgAndTrigger(pMem,pInput,srcPos,inputSize,"doPostEncV")  )  {

                    //
                    Log.e("qy","doPostEncV: q2PostMsgAndTrigger failed");
                }

                //
                qmCommFunc_mis.MIS_CNT pMisCnt  =pProcInfo.pMisCnt;

                //  q2PostMsgAndTrigger(pMsgTask, pTask.photomosaic.pOutputQ2); //zzq改成这样了，需要测试。
                qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, CONST_channelType_media);
                if (null != pChannel) { //

                    chkToApplyForChannel(pProcInfo, pChannel, CONST_channelType_media);
//                                    Log.d(IO_TAG,"3 tmpHandler_findOrgMsgAndProcess_mis_procTaskVideoData");
                    if  (  pChannel.m_b_useQ2_toOutput) {
                        q2PostMsgAndTrigger(null, pChannel.outputQ21, "find_procTaskVideoData.943");
                    }
                    else  {
                        pChannel.outputQBuf2.q2PostMsgAndTrigger(null,null,0, 0,"");

                    }
                }


                //
                return  0;

            }
            else {


                //
                int len = 272;//sizeof(myDRAW_VIDEO_DATA);

                int uiSize_dec = 0;

                //  2011/12/06
                int uiSize_enc = inputSize + 9016;//(uint)System.Runtime.InteropServices.Marshal.OffsetOf<TRANSFER_VIDEO_DATA>("buf")
                int data_len = 930616;//sizeof(TRANSFER_VIDEO_DATA);
                if (uiSize_enc > data_len) {
                    str = "doPostEnc_mediaSdk_new failed, inputSize too big";
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null, str);
                    if (bMsgGot) {
                        //qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA.call(pPkt, (""));
                    }
                    return iErr;
                }

                //
                qnmCommProc_mis.myDRAW_VIDEO_DATA pPkt = new qnmCommProc_mis.myDRAW_VIDEO_DATA();

                pPkt.head.uiLen = uiSize_dec + uiSize_enc;        //  pBmpInfo_input->bmiHeader.biSizeImage;
                pPkt.head.uiSampleTimeInMs = iSampleTimeInMs;
                pPkt.uiSize_dec = uiSize_dec;
                pPkt.usPktResType = CONST_pktResType_sharedTex;

                bMsgGot = true;
                //  注意：这个初始化非常重要，涉及压缩数据有效性


                qnmCommProc_mis.TRANSFER_VIDEO_DATA pReq = new qnmCommProc_mis.TRANSFER_VIDEO_DATA();
                pReq.uiType = CONST_imCommType_transferVideoData;
                pReq.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_v;         //  2010/09/04
                qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

                if (bufSize_transferVideoData <= pReq.uiLen) {
                    Log.e("toShareBmp failed: sizeof(  pReq->buf  ) " + bufSize_transferVideoData + "  <=  pReq->len " + pReq.uiLen);
                    if (bMsgGot) {
                        qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA.call(pPkt, (""));
                    }
                    return iErr;
                }

                if (bufSize_transferVideoData - pReq.uiLen < inputSize) {
                    str = "doPostEnc_mediaSdk failed, buf too small";
                    Log.e(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null, str);
                    if (bMsgGot) {
                        qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA.call(pPkt, (""));
                    }
                    return iErr;
                }

                //
                int buffer_len = inputSize;
                ByteBuffer buffer = ByteBuffer.allocate(buffer_len);
                buffer.put(pInput, srcPos, buffer_len);
                buffer.flip();

                pReq.buf = buffer;
                qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem = getMem.qyFuncs.getMem(pReq.mems, bufSize_TRANSFER_VIDEO_dataMemHead_mems, pReq.usCnt);
                if (null == pMem) {
                    if (bMsgGot) {
                        qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA.call(pPkt, (""));
                    }
                    return iErr;
                }

                pMem.uiLen = inputSize;


                pMem.uiSampleTimeInMs = myuiSampleTimeInMs;
                //  2015/01/15
                pMem.uiPts = myuiPts;
                pMem.ucbKeyFrame = ucbKeyFrame;

                //
                pMem.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_v;


                //
                pReq.uiLen = pMem.uiLen;
                pReq.usCnt++;

                pPkt.memory.m_obj = pReq;

                //


                //
                //
                if (pProcInfo.cfg.debugStatusInfo.b_print_test) {
                    showInfo_open.qyFuncs.showInfo_open(0, "", "doPostEnc: pts " + pMem.uiPts);
                }


                //
                boolean bPktRedirected = false;
//            Log.d(IO_TAG,"1 doPostEnc_mediaSdk_new");

                if (0 == q2PostMsgAndTrigger(pPkt, pCapProcInfo.postThread.q2, "doPostEnc_mediaSdk_new.196")) {
                    bPktRedirected = true;
                }


                if (bPktRedirected) {
                    getNextVPkt.qyFuncs.detachVPkt.call(pPkt, null);
                } else {
                    qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA.call(pPkt, (""));
                }
                bMsgGot = false;


                iErr = 0;


                if (bMsgGot) {
                    qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA.call(pPkt, (""));
                }

            }

            //
            return iErr;
        }


        //
    }
}

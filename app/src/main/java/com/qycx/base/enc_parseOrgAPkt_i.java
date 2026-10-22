


package com.qycx.base;

import static com.qycx.base.qmcSharedObj.Consts.MAX_cnt_preEnc_array;
import static com.qycx.base.qyCommProc_open.Consts.CONST_pktResType_sharedTex;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_encAudio;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_encVideo;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import com.qycx.base.util.Log;


public class enc_parseOrgAPkt_i {

    public static class Consts {
        public final static int CONST_timeInMs_forceKeyFrame = 1800;    //  2015/10/04	//  4000

    }

    public static class qyFuncs {

        public static int enc_parseOrgAPkt_i(int uiTransformType, Object pParent_transform, Object pPkt_myPLAY_AUDIO_DATA, int p_old_lPktId_getNewPkt, byte[] ppInput, int puiSize, boolean pbNeed_forceKeyFrame) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();//QY_GET_procInfo_isCli(  );
            //
            boolean bNeed_forceKeyFrame = false;

            String str ="";
            //  myDRAW_VIDEO_DATA  *  pPkt  =  pTransform->video.pCurPkt;
            myPlayAudioData.myPLAY_AUDIO_DATA pPkt = (myPlayAudioData.myPLAY_AUDIO_DATA) pPkt_myPLAY_AUDIO_DATA;

            //  检查一下。每个包都只能处理一次，否则，容易死循环
//            if (0 == p_old_lPktId_getNewPkt) return -1;
            if (0 == pPkt.lPktId_getNewPkt) {
                str ="enc_parseOrgAPkt_i failed: lPktId_alloc 0";
                Log.e(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                return -1;
            }
            if (pPkt.lPktId_getNewPkt == p_old_lPktId_getNewPkt) {
                str ="enc_parseOrgAPkt_i failed: lPkdId_alloc==old";
                Log.e(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                return -1;
            }
//			p_old_lPktId_getNewPkt = pPkt.lPktId_getNewPkt;
			pPkt.tmp_lPktId_getNewPkt = pPkt.lPktId_getNewPkt;


            if (uiTransformType == CONST_uiTransformType_encAudio) {
                qmcSharedObj.CAP_procInfo_audioCommon pCapAudio = (qmcSharedObj.CAP_procInfo_audioCommon) pParent_transform;
                //
                //
                //  2016/12/15
                int nAvgBytesPerSec = (int) pCapAudio.compressAudio.ah_decompress.wfx.nAvgBytesPerSec;
                if (0 != nAvgBytesPerSec) {
                    pCapAudio.compressAudio.encInfo.st.last_iSampleTimeInMs_preEnc = pPkt.head.uiSampleTimeInMs;
                    long llValInMs = pPkt.uiSize_dec * 1000;
                    pCapAudio.compressAudio.encInfo.st.uiTimeLen_preEnc = (int) (llValInMs / nAvgBytesPerSec);
                }

            }

            if (pPkt.head.uiLen > pPkt.memory.m_pBuf.length
                //||  pPkt->uiSize_dec  +  pPkt->uiOffset_enc  +  pPkt->uiLen_enc  >  pPkt->head.uiLen
            ) {

                Log.d(("doDecVideo: uiSize_dec or uiLen error"));
                return -1;
            }
            byte[] decData =  pPkt.memory.m_pBuf;    //    +  pPkt->uiSize_dec  +  pPkt->uiOffset_enc;
            myPlayAudioData.myPLAY_AUDIO_DATA pPkt_dec = pPkt;

            ppInput =  decData;
            puiSize = pPkt.uiSize_dec;  //  head.uiLen;


            iErr = 0;


            //  2014/08/25
            if (true != pbNeed_forceKeyFrame) {
                pbNeed_forceKeyFrame = bNeed_forceKeyFrame;
            }

            return iErr;
        }


        public static int enc_parseOrgVPkt_i(int uiTransformType, Object pParent_transform, Object pPkt_myDRAW_VIDEO_DATA, int p_old_lPktId_alloc, byte[] ppInput, int puiSize, boolean pbNeed_forceKeyFrame) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();  //  QY_GET_procInfo_isCli(  );
            if (null == pProcInfo) return -1;
            //
            boolean bNeed_forceKeyFrame = false;
            //
            //TCHAR tBuf[128] = "";

            //
            qnmCommProc_mis.myDRAW_VIDEO_DATA pPkt = (qnmCommProc_mis.myDRAW_VIDEO_DATA) pPkt_myDRAW_VIDEO_DATA;

            //

            //  检查一下。每个包都只能处理一次，否则，容易死循环
            if (0 == p_old_lPktId_alloc) return -1;
            if (0 == pPkt.lPktId_alloc) {
                Log.d(("enc_parseOrgVPkt_i failed: lPktId_alloc 0"));
                return -1;
            }
            if (pPkt.lPktId_alloc == p_old_lPktId_alloc) {
                //_sntprintf(tBuf, mycountof(tBuf), ("enc_parseOrgVPkt_i failed: lPkdId_alloc==old %d"), pPkt->lPktId_alloc);
                //showInfo_open(0, 0, tBuf);
                return -1;
            }
            p_old_lPktId_alloc = pPkt.lPktId_alloc;


            if (uiTransformType == CONST_uiTransformType_encVideo) {
                qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp = (qmcSharedObj.CAP_procInfo_bmpCommon) pParent_transform;

                if (pCapBmp.compressVideo.encInfo.iNextIndex_preEnc >= MAX_cnt_preEnc_array) {
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, 0, "", "", ("enc_parseOrgVPkt_i failed, iNextIndex_preEnc too large"));
                    return iErr;
                }
                pCapBmp.compressVideo.encInfo.iSampleTimeInMs_preEnc_array[pCapBmp.compressVideo.encInfo.iNextIndex_preEnc] = (int) pPkt.head.uiSampleTimeInMs;
                pCapBmp.compressVideo.encInfo.iNextIndex_preEnc++;
                if (pCapBmp.compressVideo.encInfo.iNextIndex_preEnc >= MAX_cnt_preEnc_array) {
                    pCapBmp.compressVideo.encInfo.iNextIndex_preEnc = pCapBmp.compressVideo.encInfo.iNextIndex_preEnc % MAX_cnt_preEnc_array;
                }
                pCapBmp.compressVideo.encInfo.cnt_preEnc++;

                //
                if (pCapBmp.compressVideo.encInfo.dwTickCnt_startToForceKeyFrame != 0) {
                    long dwTickCnt = myGetTickCount(null);
                    if (dwTickCnt - pCapBmp.compressVideo.encInfo.dwTickCnt_startToForceKeyFrame < Consts.CONST_timeInMs_forceKeyFrame) {
                        bNeed_forceKeyFrame = true;

                    }
                }
            }

            //
            if (pPkt.head.uiLen > pPkt.memory.m_pBuf.length
                    || pPkt.uiSize_dec + pPkt.uiOffset_enc + pPkt.uiLen_enc > pPkt.head.uiLen) {

                String str = "enc_parseOrgVPkt_i failed: uiSize_dec or uiLen error";
                Log.e(str);
                showInfo_open.qyFuncs.showInfo_open(0, null,str);
                return -1;
            }
            //  2015/10/29
            if (pPkt.usPktResType != CONST_pktResType_sharedTex) {
                if (pPkt.bih.biSizeImage > pPkt.uiSize_dec) {
                    String str = "enc_parseOrgVPkt_i failed, bih.sizeImg > size_dec";
                    Log.d(str);
                    showInfo_open.qyFuncs.showInfo_open(0, null,str);
                    return -1;
                }
            }
            //
            byte[] decData =  pPkt.memory.m_pBuf;   //    +  pPkt->uiSize_dec  +  pPkt->uiOffset_enc;
            qnmCommProc_mis.myDRAW_VIDEO_DATA pPkt_dec = pPkt;

//
            int index_pMems_from = pPkt.decInfo.index_pMems_from;


            ppInput =  decData;
            puiSize = (int) pPkt.bih.biSizeImage;


            iErr = 0;


//  2014/08/25
            if (true != pbNeed_forceKeyFrame) {
                pbNeed_forceKeyFrame = bNeed_forceKeyFrame;
            }

            return iErr;
        }


    }
}

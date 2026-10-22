


package com.qycx.base;

import static com.qycx.base.genericQueue.qyFuncs.isQEmpty;
import static com.qycx.base.genericQueue.qyFuncs.qGetMsg;
import static com.qycx.base.genericQueue.qyFuncs.qPostMsg;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_pkts_decAudio;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_pkts_encVideo;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_myDRAW_VIDEO_DATA_pkts_encVideo;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_myPLAY_AUDIO_DATA_pkts_encAudio;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.M_set_pktId_getNewPkt;
import static com.qycx.base.qyq2.qyFuncs.isQ2Empty;
import static com.qycx.base.qyq2.qyFuncs.q2GetMsg;
import static com.qycx.base.qyq2.qyFuncs.q2GetMsgPkg;


import com.qycx.base.old.qyMcMainMediaProc_encVideo;
import com.qycx.base.util.Log;

public class getNextVPkt {

    public static class qyFuncs {

        public static qyCommProc_open.PF_getNextAPkt getNextAPkt = (myPlayAudioData.myPLAY_AUDIO_DATA pCurPkt, genericQueue.GENERIC_Q pInDataCacheQ, qyq2.QY_Q2 pAudioQ2)-> {
//            int myPLAYAudioData = -1;
            myPlayAudioData.myPLAY_AUDIO_DATA myPLAYAudioData = null;

            //
            //  if  (  pTransformVideo->bQuit  )  return  -1;
            if (null == pCurPkt || null == pInDataCacheQ) {  //  2015/10/11
                return myPLAYAudioData;
            }

            //
            if (null == pCurPkt.memory.m_pBuf) {
                if (isQEmpty(pInDataCacheQ)) {
                    //myPLAY_AUDIO_DATA pkts[CONST_pkts_decAudio];
                    qyMcMainMediaProc_encAudio.TMP_buf_myPLAY_AUDIO_DATA_pkts_encAudio buf_pkts = new qyMcMainMediaProc_encAudio.TMP_buf_myPLAY_AUDIO_DATA_pkts_encAudio();
                    int[] uiLens = new int[CONST_pkts_decAudio];      //
                    int nLens = uiLens.length;// mycountof(uiLens);      //  一次最多处理的包数。
                    Object pMsg = null;
                    int nPkts;
                    int i;

                    for (; ; ) {
                        //  if  (  pTransformVideo->bQuit  )  goto  errLabel;

                        if (isQ2Empty(pAudioQ2)) {
                            return myPLAYAudioData;
                        }

                        nLens = uiLens.length;// mycountof(uiLens);
//                        if (0 != q2GetMsgPkg(pAudioQ2, buf_pkts.buf, bufSize_myPLAY_AUDIO_DATA_pkts_encAudio, uiLens, (int) nLens)) {
//                            Log.d( ("getValidPkt failed: q2GetMsgPkg failed"));
//                            return myPLAYAudioData;
//                        }
//                        q2GetMsg()
                        try //(qyq2.CQyQ2Help q2Help = new qyq2.CQyQ2Help())
                        {
                            genericQueue.GENERIC_Q pQ_toGetMsg  =  pAudioQ2.qs0;
                            if (null == (pMsg = qGetMsg(pQ_toGetMsg))) {
                                Log.d( ("getValidPkt failed: q2GetMsgPkg failed"));
                                showInfo_open.qyFuncs.showInfo_open(0, null, ("getValidPkt failed: q2GetMsgPkg failed"));
                                return myPLAYAudioData;
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        //
                        myPlayAudioData.myPLAY_AUDIO_DATA pkts = (myPlayAudioData.myPLAY_AUDIO_DATA) pMsg;// (myPlayAudioData.myPLAY_AUDIO_DATA) buf_pkts.buf;
//                        guiShare.GuiShare.pf_dumpData1.call(pkts.memory.m_pBuf, (int) pkts.head.uiLen, 0, 0, true, false, "dump_deced_pcm.pcm");

                        pCurPkt = (myPlayAudioData.myPLAY_AUDIO_DATA) pMsg;
//                        myPLAYAudioData =  (myPlayAudioData.myPLAY_AUDIO_DATA) pMsg;

                                //
                        //
                        nPkts = 0;
//                        for (i = 0; i < nLens; i++) {
//                            nPkts += 0;//(int) (uiLens[i] / sizeof(myPlayAudioData.myPLAY_AUDIO_DATA));
////                            if (0 != (uiLens[i] % sizeof(myPlayAudioData.myPLAY_AUDIO_DATA))) {
////                            }
//                        }

                        //
//                        for (i = 0; i < nPkts; i++) {
//                            if (0 != qPostMsg((byte[]) pkts[i], (int) sizeof(myPlayAudioData.myPLAY_AUDIO_DATA), pInDataCacheQ)) {
//                                qyTmpMemory.qyFuncs.clean_myPLAY_AUDIO_DATA((byte[]) pkts[i], ("decVideo_rtsp, qPostMsg failed"));
//                            }
//                        }

                        //
                        break;
                    }
                }
                //
                int size =128;//sizeof(myPlayAudioData.myPLAY_AUDIO_DATA);
//                if (0 != qGetMsg(pInDataCacheQ, pCurPkt.toByteArray(), size)) return myPLAYAudioData;
                //  2015/02/13
                M_set_pktId_getNewPkt(pCurPkt);
            }
            //
//            if (null == pCurPkt.memory.m_pBuf) return myPLAYAudioData;
            myPLAYAudioData = pCurPkt;
            //
//            myPlayAudioData.myPLAY_AUDIO_DATA pPkt = pCurPkt;

            return myPLAYAudioData;
        };


        //
        public static qyCommProc_open.PF_getNextVPkt getNextVPkt=(Object pCurPkt_param, genericQueue.GENERIC_Q pInDataCacheQ, qyq2.QY_Q2 pVideoQ2)-> {
            int iErr = -1;

            qnmCommProc_mis.myDRAW_VIDEO_DATA pCurPkt = (qnmCommProc_mis.myDRAW_VIDEO_DATA) pCurPkt_param;


            //
            //  if  (  pTransformVideo->bQuit  )  return  -1;
            if (null == pCurPkt || null == pInDataCacheQ) {   //  2015/10/11
                return -1;
            }
            //
            if (null == pCurPkt.memory.m_pBuf) {
                if (isQEmpty(pInDataCacheQ)) {
                    //qnmCommProc_mis.myDRAW_VIDEO_DATA pkts[CONST_pkts_decVideo];
                    qyMcMainMediaProc_encVideo.TMP_buf_myDRAW_VIDEO_DATA_pkts_encVideo buf_pkts = new qyMcMainMediaProc_encVideo.TMP_buf_myDRAW_VIDEO_DATA_pkts_encVideo();
                    //unsigned  int uiLens[CONST_pkts_encVideo];      //
                    int[] uiLens = new int[CONST_pkts_encVideo];//[];
                    int nLens = uiLens.length;// mycountof(uiLens);      //  一次最多处理的包数。

                    int nPkts;
                    int i;

                    for (; ; ) {
                        //  if  (  pTransformVideo->bQuit  )  goto  errLabel;

                        if (isQ2Empty(pVideoQ2)) {
                            return iErr;
                        }

                        nLens = uiLens.length;// mycountof(uiLens);
                        if (0 != q2GetMsgPkg(pVideoQ2, buf_pkts.buf, bufSize_myDRAW_VIDEO_DATA_pkts_encVideo, uiLens, (int) nLens)) {
                            Log.d( ("getNextVPkt failed: q2GetMsgPkg failed"));
                            showInfo_open.qyFuncs.showInfo_open(0, null, ("getNextVPkt failed: q2GetMsgPkg failed"));
                            return iErr;
                        }

                        //
                        //myDRAW_VIDEO_DATA pkts = (myDRAW_VIDEO_DATA)buf_pkts.buf;
                        byte[] pStart = buf_pkts.buf.array();
                        int bufSize = bufSize_myDRAW_VIDEO_DATA_pkts_encVideo;

                        nPkts = 0;
                        for (i = 0; i < nLens; i++) {
                            nPkts += 0;//(int) (uiLens[i] / sizeof(myDRAW_VIDEO_DATA));
//                            if (0 != (uiLens[i] % sizeof(myDRAW_VIDEO_DATA))) {
//
//                            }
                        }

//                        for (i = 0; i < nPkts; i++) {
//                            qnmCommProc_mis.myDRAW_VIDEO_DATA tmp_p = getMem.qyFuncs.getMem((qnmCommProc_mis.myDRAW_VIDEO_DATA) pStart, bufSize, i);
//                            if (0 != qPostMsg((byte[]) tmp_p, (int) sizeof(myDRAW_VIDEO_DATA), pInDataCacheQ)) {
//                                qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA.call(tmp_p, ("decVideo_rtsp, qPostMsg failed"));
//                            }
//                        }

                        //
                        break;
                    }
                }
                //
                int size =128;//sizeof(myDRAW_VIDEO_DATA);
                if (0 != qGetMsg(pInDataCacheQ, pCurPkt.toByteArray2(), size)) {
                    Log.e("qy","getNextVPkt failed");
                    return iErr;
                }
                //
            }
            //
            if (null == pCurPkt.memory.m_pBuf) return iErr;

            //
            qnmCommProc_mis.myDRAW_VIDEO_DATA pPkt = pCurPkt;


            //  if  (  pQdcObjInfo->cfg.pCurPkt
            iErr = 0;
            

            return iErr;
        };


        //
        //
        public static qyCommProc_open.PF_parseDecVPkt_o parseDecVPkt_o=(Object pPktParam, byte[] ppBih, byte[] ppOutput, int puiSize)-> {
            int iErr = -1;
            qnmCommProc_mis.myDRAW_VIDEO_DATA pPkt =null;// (qnmCommProc_mis.myDRAW_VIDEO_DATA) pPktParam;

            ppBih = pPkt.bih.toByteArray();
            ppOutput =  pPkt.memory.m_pBuf;
            puiSize = pPkt.uiSize_dec;

            //
            iErr = 0;
            
            return iErr;
        };


        public static qyCommProc_open.PF_detachPkt detachAPkt=(Object pPkt_myPLAY_AUDIO_DATA, Object p1)-> {
            myPlayAudioData.myPLAY_AUDIO_DATA pPkt = (myPlayAudioData.myPLAY_AUDIO_DATA) pPkt_myPLAY_AUDIO_DATA;

            //mymemset((byte[])pPkt->memory, 0, sizeof(QY_MEMORY)  );

            return 0;
        };


        //

        public static qyCommProc_open.PF_detachPkt detachVPkt=(Object pPkt_myDRAW_VIDEO_DATA, Object p1)-> {
            qnmCommProc_mis.myDRAW_VIDEO_DATA pPkt = (qnmCommProc_mis.myDRAW_VIDEO_DATA) pPkt_myDRAW_VIDEO_DATA;
            return 0;
        };


    }
}

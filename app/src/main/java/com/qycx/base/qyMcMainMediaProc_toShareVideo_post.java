


package com.qycx.base;

import static com.qycx.base.Consts.CONST_iParamType_toShareVideo_post;
import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.ctxCaller.Consts.CONST_ctxCallerType_thread;
import static com.qycx.base.ctxCaller.Consts.cntof_ctxCaller_tNameBuf;
import static com.qycx.base.genericQueue.qyFuncs.qGetMsg;
import static com.qycx.base.genericQueue.qyFuncs.qGetMsgPkg;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferVideoData;
import static com.qycx.base.qmcSharedObj.Consts.CONST_sharedObjSubtype_mosaic;
import static com.qycx.base.qmcSharedObj.qyFuncs.getCapBmpBySth;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qmcVideoFunc.qyFuncs.getSharedObjUsr;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_pkts_toShareVideo_post;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_decObj_mediaCodec_tmpDataBuf;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_encV_tmpDataBuf;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_myDRAW_VIDEO_DATA_pkts_toShareVideo_post;
import static com.qycx.base.qyCommProc_open.Consts.CONST_pktResType_sharedTex;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;
import static com.qycx.base.qyq2.qyFuncs.isQ2Empty;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.myWaitOne;

import com.qycx.base.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class qyMcMainMediaProc_toShareVideo_post {

    static class TMP_buf_myDRAW_VIDEO_DATA_pkts_toShareVideo_post {
        public ByteBuffer buf = ByteBuffer.allocate(bufSize_myDRAW_VIDEO_DATA_pkts_toShareVideo_post);
    }

    public static class qyFuncs {
        /*
        public static qmcFunc.PF_threadProc mcThreadProc_toShareVideo_post = (Object lpParameter) -> {
            int iIndex_sharedObj = (int) lpParameter;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (null == pQyMc) {
                return;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return;

            qmcSharedObj.QY_SHARED_OBJ pSharedObj = getSharedObjByIndex(pProcInfo, iIndex_sharedObj);
            if (null == pSharedObj) return;

            qmcSharedObj.CAP_procInfo_bmpCommon pCapProcInfo = getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);
            if (null == pCapProcInfo) return;

            //  2010/12/28
            pCapProcInfo.compressVideo.debugInfo.nStep = 101;

            //  2009/12/24

            //
            ctxMcThread.CTX_MC_thread ctx = new ctxMcThread.CTX_MC_thread();
            ctx.ctxCaller.type = CONST_ctxCallerType_thread;
            ctx.ctxCaller.dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            ctx.ctxCaller.tNameBuf.tNameBuf = "thread_toShareV";

            int i;

            //TMP_buf_myDRAW_VIDEO_DATA_pkts_toShareVideo_post buf_pkts = new TMP_buf_myDRAW_VIDEO_DATA_pkts_toShareVideo_post();
            //int[] uiLens = new int[CONST_pkts_toShareVideo_post];
            //int nLens = uiLens.length;
            int nPkts;

            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            if (null == pMisCnt) return;

            {
                qnmCommProc_mis.MIS_MSGU pMsgBuf = new qnmCommProc_mis.MIS_MSG_TASK();//qnmCommProc_mis.MIS_MSGU();
                qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead tmp_head = new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();
                byte [] tmp_video  =  new byte[bufSize_encV_tmpDataBuf];
                qnmCommProc_mis.TRANSFER_VIDEO_DATA tmp_tvd =new qnmCommProc_mis.TRANSFER_VIDEO_DATA();


                long dwTickCnt;

                boolean tmpbErr;

                pCapProcInfo.postThread.bQuit = false;
                pCapProcInfo.postThread.bRunning = true;

                //qySyncCommProc.CQyMalloc_array mallocObj_tmpPkts = new qySyncCommProc.CQyMalloc_array(CONST_pkts_toShareVideo_post);
                for (; !pCapProcInfo.postThread.bQuit; ) {

                    //  2010/12/28
                    pCapProcInfo.compressVideo.debugInfo.nStep = 105;

                    if  (  pProcInfo.cfg.b_useQ2_postEncV) {
                        myWaitOne(pCapProcInfo.postThread.q2.hEvent0, 5000);
                    }
                    else  {
                          myWaitOne(pCapProcInfo.postThread.qBuf2.hEvent0,5000);
                    }

                    //
                    dwTickCnt = myGetTickCount(null);

                    for (; !pCapProcInfo.postThread.bQuit; ) {

                        qnmCommProc_mis.TRANSFER_VIDEO_DATA tvd_i;
                        //
                        if  (  pProcInfo.cfg.b_useQ2_postEncV) {
                            qnmCommProc_mis.myDRAW_VIDEO_DATA pkts = null;

                            if (isQ2Empty(pCapProcInfo.postThread.q2)) {
                                break;
                            }

                            nPkts = 1;
                            try (qyq2.CQyQ2Help q2Help = new qyq2.CQyQ2Help()) {
                                if (null == (pkts = (qnmCommProc_mis.myDRAW_VIDEO_DATA) qGetMsg(q2Help.getQ_toGetMsg(pCapProcInfo.postThread.q2)))) {
                                    break;
                                }
                            } catch (Exception e) {
                                Log.e(e.getMessage());
                                pCapProcInfo.postThread.bRunning = false;
                                return;
                            }
                            tvd_i  =  (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pkts.memory.m_obj;
                        }
                        else  {
                              if  (  pCapProcInfo.postThread.qBuf2.isQ2Empty())  {
                                  break;
                              }

                              nPkts  =  1;
                              qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead head = tmp_head;//new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();
                              byte [] video  =  tmp_video;//new byte[bufSize_encV_tmpDataBuf];
                              int tmpLen  =  pCapProcInfo.postThread.qBuf2.q2GetMsg(head,video,0,video.length);
                              if  (  tmpLen  <=0  )  {
                                  break;
                              }
                              if  ( tmpLen!=head.uiLen)  {
                                  Log.e("qy","mcThreadProc_toShareVideo_post err: tmpLen != head.uiLen");
                                  break;
                              }
                              //
                              qnmCommProc_mis.TRANSFER_VIDEO_DATA tvd =tmp_tvd;//new qnmCommProc_mis.TRANSFER_VIDEO_DATA();
                              tvd.usCnt  =  1;
                              tvd.buf  = ByteBuffer.wrap(video);
                              tvd.uiLen  =  tmpLen;
                              tvd.mems[0].copyFrom(head);
                              tvd.uiTranNo_openAvDev  =  head.uiTranNo_openAvDev;
                              tvd.uiType  =  CONST_imCommType_transferVideoData;

                              //
                              tvd_i  =  tvd;

                              //pkts  =  head.tmp_pkt;
                              //qnmCommProc_mis.myDRAW_VIDEO_DATA  dbg_pkt  = head.tmp_pkt;;
                            //qnmCommProc_mis.TRANSFER_VIDEO_DATA dbg_tvd  =  (qnmCommProc_mis.TRANSFER_VIDEO_DATA) dbg_pkt.memory.m_obj;


                              //
                            {
                                test.testCls_Droid.testBreak();
                            }
                        }

                        //
                        //Log.d(Consts.VIDEO_UPLOAD,"2.1 qGetMsg from pCapProcInfo.postThread.q2");
                        //
                        qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = getSharedObjUsr(pSharedObj, pSharedObj.var.iIndex_curUsr);
                        if (null == pSharedObjUsr) {
                            continue;
                        }


                        //
                        qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(CONST_iParamType_toShareVideo_post,0);
                        if  ( tmpVar==null)  {
                            continue;
                        }

                        //
                        //showInfo_open.qyFuncs.showInfo_open("befoe toShareVideo_post");

                        //
                        pProcInfo.toShareVideo_post(ctx.ctxCaller, tmpVar,  pCapProcInfo.uiType, pSharedObj.var.iIndex_capBmp, tvd_i, pCapProcInfo.compressVideo, pSharedObj, pMsgBuf);

                        //
                        //showInfo_open.qyFuncs.showInfo_open("after toShareVideo_post");


                        //  2014/09/23
                        boolean bNoDrawForSlave = true;

                        if (bNoDrawForSlave) {
                            if (pSharedObj.var.bSlave) {
                                continue;
                            }
                            if (pSharedObj.var.usSubtype == CONST_sharedObjSubtype_mosaic) {
                                continue;
                            }
                        }

                    }

                    //mallocObj_tmpPkts.Dispose();
                }
            }

            Log.i("mcThreadProc_toShareBmp will quit");
            showInfo_open.qyFuncs.showInfo_open("mcThreadProc_toShareVideo_post will quit");

            //
            pCapProcInfo.compressVideo.debugInfo.nStep = 199;
            pCapProcInfo.postThread.bRunning = false;
        };

         */


    }
}

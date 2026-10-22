package com.qycx.base;

import static com.qycx.base.colorSpaceFunc.qyFuncs.M_sizeImage;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_I420;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_YV12;
import static com.qycx.base.qyCommProc_open.Consts.CONST_pktResType_sharedTex;
import static com.qycx.base.qyTick.qyFuncs.M_GetTgt_video;
import static com.qycx.base.qyq2.qyFuncs.q2GetMsg;
import static com.qycx.base.videoFunc_open.Consts.BI_RGB;

import android.util.Log;

public class ttl_readPkt {
    private static final String TAG = ttl_readPkt.class.getSimpleName();
    public static class qyFuncs_Droid {

        public static int ttl_readPkt(talkDraw.CTalkDraw.TalkTextureLoader ttl) {
            int iErr=-1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            qmcCommFunc_is_open.QY_PLAYER pPlayer = null;
            qmcCommFunc_is_open.PLAY_VIDEO_procInfo pPlay = null;


            //
            boolean bDbg = false;
            if (!ttl.m_bMe) {
                bDbg = true;
            } else {
                //bDbg = true;
            }

            //
            qnmCommProc_mis.PLAYER_ID playerId = ttl.m_drawMem.param.playerId;

            //
            talkDraw.CTalkDraw myDraw = (talkDraw.CTalkDraw) ttl.mMyDraw;


            //
            int dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();

            //
            qyTick.CQyTick tickObj = new qyTick.CQyTick();
            long dwTgt_lastData = M_GetTgt_video(tickObj);

            //
            int nPkts;

            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;


            //
            //  #define		DEFAULT_dwToInMs_playVideo		200
            final int DEFAULT_dwToInMs_playVideo = 33;// 210;
            //#define		DEFAULT_dwToInMs_playVideo			100
            final int CONST_video_skippedInterval = 1;  //  2	//  4
            final int CONST_video_ahead = 0;    //  1	//  2	//  10			//  提前量

            int dwToInMs = DEFAULT_dwToInMs_playVideo;

            long dwTgt_internal;
            int dwTgt_internal_toPlay;
            int dwTickCnt_syncAv_now;

            boolean bPktRedirected = false;

            int uiSampleTimeInMs_audio = 0;
            int dwTickCnt_syncAv_audio = 0;

            //
            //  2015/03/12. 本地视频没有做pts,是错的。不过，现在先暂时伪造一个，以便用pts来改善播放效果
            int tmp_localV_pts_faked = 0;

            //
            String str;
            //byte[] rgb_array = null;
            //byte[] yuv_array = null;

            //

            //
            {
                int i = 0;

                pPlayer = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, playerId.index_player);
                if (pPlayer == null) return -1;
                //pPlayer = pProcInfo.av.localAv.player;
                pPlay = pPlayer.video;

                //  2016/02/18
            }

            switch (pPlayer.video.vh.bih.biCompression) {
                case BI_RGB:
                case CONST_fourcc_YV12:
                case CONST_fourcc_I420: //  2014/07/21
                    break;
                default:

                    return iErr;
            }


            //  2011/12/05
            qySyncCommProc.CQyMalloc mallocObj_tmpRgbBuf = new qySyncCommProc.CQyMalloc();
            {

                for (; !pPlay.bQuit && !myDraw.bQuit; ) {

                    //  2015/09/12
                    dwToInMs = DEFAULT_dwToInMs_playVideo;
                    //if  (  pPlay.m_var.status.fpsInfo.avgTimePerFrameInMs_real  >  10  )  dwToInMs  =  pPlay.m_var.status.fpsInfo.avgTimePerFrameInMs_real;

                    tmpFunc.qyFuncs.myWaitOne(pPlay.displayQ2.hEvent0, (int) dwToInMs);

                    //
                    dwTgt_internal = M_GetTgt_video(tickObj);

                    if (ttl.bDrawModified()) {
                        str = String.format("ttl_readPkt will quit: myDraw.bLayoutModified");
                        Log.d(TAG, str);
                        return iErr;
                    }
                  
                    for (; !pPlay.bQuit && !myDraw.bQuit; ) {
                        //  Log.e(TAG,  "playVideo: msg reaved"  );
                        qnmCommProc_mis.MIS_MSG_procVideo_qmc msg_pkt=new qnmCommProc_mis.MIS_MSG_procVideo_qmc();
                        byte[] buf = msg_pkt.toByteArray();
                        int len_pkt=buf.length;//sizeof(MIS_MSG_procVideo_qmc);
                        {
                            if (0 != q2GetMsg(pPlay.displayQ2,  buf, len_pkt))
                            {
                                dwToInMs = DEFAULT_dwToInMs_playVideo;
                                break;
                            }
                        }
                        qnmCommProc_mis.myDRAW_VIDEO_DATA pPkts[] =null;//  (qnmCommProc_mis.myDRAW_VIDEO_DATA[]) msg_pkt.pkt;
                        //  注意：在上面qGetMsg和下面的attach之间，不要添加代码，否则，内存就失去管理了


                        qySyncCommProc.CQyMalloc_array mallocObj_tmpPkts = new qySyncCommProc.CQyMalloc_array(1);
                        {
                            //CQyMalloc mallocObj_tmpPkts[CONST_pkts_playVideo];


                            boolean tmpbErr = false;   //  注意：这里如果attach出错，要继续attach, 但是最后要continue. 以防止pPkts[i]没有机会释放内存
                            //
                            nPkts = 1;
                            for (int i = 0; i < nPkts; i++) {
                                qnmCommProc_mis.myDRAW_VIDEO_DATA pPkt = pPkts[i];
                                if (pPkt.memory.m_pBuf != null) {
                                    if (0 != mallocObj_tmpPkts.mems[i].attach(pPkt.memory)) {

                                        qyTmpMemory.qyFuncs.clean_myDRAW_VIDEO_DATA.call( pPkt, ("playVideo"));
                                        tmpbErr = true;
                                        continue;
                                    }
                                }
                            }
                            if (tmpbErr) continue;

                            dwTgt_lastData = M_GetTgt_video(tickObj);       //  video data received
                            for (int iIndex = 0; iIndex < nPkts; iIndex++) {
                                qnmCommProc_mis.myDRAW_VIDEO_DATA pPkt = pPkts[iIndex];
                                int pktLen=128;//sizeof(myDRAW_VIDEO_DATA);

                                //  2015/03/12. 对本地视频，伪造一个pts. 等回头在包生成处产生pts才对
                                if (pPlayer.idInfo_recorder.ui64Id == 0) {
                                    if (0 == pPkt.head.uiPts) {
                                        pPkt.head.uiPts = tmp_localV_pts_faked;
                                        tmp_localV_pts_faked++;
                                    }
                                }

                                //  2015/10/04
                                if (pPkt.usPktResType != CONST_pktResType_sharedTex) {
                                    //
                                    if (pPkt.head.uiLen < pPlay.vh.bih.biSizeImage) {

                                        //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("playVideo err: pkt.head.uiLen %d <  sizeImage %d. skipped"), pPkt->head.uiLen, pPlay.vh.bih.biSizeImage);
                                        continue;
                                    }
                                }

                                //  2011/12/05
                                if (pPkt.bih.biWidth != pPlay.vh.bih.biWidth
                                        || pPkt.bih.biHeight != pPlay.vh.bih.biHeight
                                        || 0 == pPkt.bih.biSizeImage) {

                                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("playVideo err: pkt %d,%d !=  player %d,%d. skipped"), (int)pPkt->bih.biWidth, (int)pPkt->bih.biHeight, (int)pPlay.vh.bih.biWidth, (int)pPlay.vh.bih.biHeight);
                                    continue;
                                }

                                //
                                if (pPkt.usPktResType != CONST_pktResType_sharedTex) {
                                    if (pPkt.bih.biCompression != BI_RGB) {

                                        switch (pPkt.bih.biCompression) {
                                            case CONST_fourcc_YV12:
                                                /* */
                                                break;
                                            case CONST_fourcc_I420:
                                                //
                                                int rgbSize = M_sizeImage(32, pPkt.bih.biWidth, pPkt.bih.biHeight);

                                                int yuvSize = M_sizeImage(12, pPkt.bih.biWidth, pPkt.bih.biHeight);

                                                guiShare.GuiShare.pf_i420ToRgb.call( pPkt.memory.m_pBuf, ttl.m_yuvRgbInfo, 32, true, pPkt.bih.biWidth, pPkt.bih.biHeight, false);

                                                //ttl.tmpFillTex( rgb_array, pPkt->bih.biWidth,pPkt->bih.biHeight);
                                                ttl.tmpFillTex(pPkt.bih.biWidth, pPkt.bih.biHeight);


                                                break;
                                            default:

                                                continue;

                                        }
                                        //
                                        //memcpy(pPkt->bih,pPlay.vh.bih, sizeof(pPkt->bih)  );
                                        pPkt.bih = pPlay.vh.bih;
                                        pPkt.uiSize_dec = (int) pPlay.vh.bih.biSizeImage;

                                    } else {
                                    }
                                }

                                //  2015/09/11
                                pPkt.debugInfo.dwTickCnt_playPkt_start = tmpFunc.qyFuncs.myGetTickCount(null);


                                continue;
                            }

                        }
                        continue;
                    }
                }
            }
            if (!ttl.m_bMe) {
                int ii = 0;
            }
            String pName = pPlayer.video.q2.cfg.name;
            {
                str = String.format("%s, %s ends.", new String(pName), pPlayer.idInfo_recorder.ui64Id);
            }

            return 0;

        }

    }
}

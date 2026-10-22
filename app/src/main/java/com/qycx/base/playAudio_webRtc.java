package com.qycx.base;

import static com.qycx.base.Consts.AUDIO_DOWNLOAD;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;

import static com.qycx.base.qyTick.qyFuncs.M_GetTickCount_audio;
import static com.qycx.base.qyq2.qyFuncs.getQ2Nodes;
import static com.qycx.base.qyq2.qyFuncs.q2GetMsg;
import static com.qycx.base.tmpFunc.qyFuncs.MACRO_mysafeFree;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import android.annotation.SuppressLint;
import android.util.Log;

import java.nio.ByteBuffer;

public class playAudio_webRtc {
    private static final String TAG = playAudio_webRtc.class.getSimpleName();

    public static class qyFuncs_Droid {
        public static void M_chk_pts(qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayerAudio, myPlayAudioData.myPLAY_AUDIO_DATA pkt) {


        }

        public static void FillMemory(byte[] Destination, int length, int Fill) {
            //qyFuncs.mymemset((Destination), (Fill), (length));
            for (int i = 0; i < length; i++) {
                Destination[i] = (byte) Fill;
            }
        }


        //
        @SuppressLint("SuspiciousIndentation")
        public static int my_inStream(qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar, qmcCommFunc_is_open.QY_PLAYER pPlayer, byte[] buf, int bufLen) {
            //qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(CONST_iParamType_deca)
            int nRead = 0;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            //qmcCommFunc_is_open.QY_PLAYER pPlayer = (qmcCommFunc_is_open.QY_PLAYER)p0;
            qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio = pPlayer.audio;
            //byte[] buf = p1;
            //int bufLen = (int)p2;

            //
            //TCHAR tBuf[128];
            boolean bDbg = false;
            String str;



            //
            if (pPlayAudio.cache.indexStart != 0) {
                if (0 != pPlayAudio.cache.nLeft){
                    System.arraycopy(pPlayAudio.cache.pBuf, pPlayAudio.cache.indexStart, pPlayAudio.cache.pBuf, 0, pPlayAudio.cache.nLeft);
                }
                    //qyMemoryFunc.qyFuncs.mymemcpy( pPlayAudio.cache.pBuf,  pPlayAudio.cache.pLeft, pPlayAudio.cache.nLeft);
                pPlayAudio.cache.indexStart = 0;
            }


            int nQNodes_left = 0;
            if (pProcInfo.cfg.b_useAudioQ2_toDecA) {
                nQNodes_left = qyq2.qyFuncs.getQ2Nodes(pPlayAudio.q2);
            } else {
                nQNodes_left = pPlayAudio.qBuf2.getQ2Nodes();
            }
            //
            boolean bAdjust = false;

            //  2017/06/21
            long dwTickCnt = myGetTickCount(null);
            int iElapseInMs = (int) (dwTickCnt - pPlayAudio.lowLatency.dwTgt_start1);
            int nIntervalInMs_toAdjust = 5000;
            nIntervalInMs_toAdjust = 4000;
            //
            iElapseInMs = Math.abs(iElapseInMs);
            //
            boolean bNeedReset = false;


            //
            int iElapseInMs_stat = Math.abs((int) (dwTickCnt - pPlayAudio.lowLatency.stat.dwTickCnt_start));
            int nIntervalInMs_stat = 30000;
            if (iElapseInMs_stat > nIntervalInMs_stat) {
                //
                pPlayAudio.lowLatency.stat.iIntervalInMs_xql_mql = (int) Math.abs((int) (pPlayAudio.lowLatency.stat.dwTickCnt_xql - pPlayAudio.lowLatency.stat.dwTickCnt_mql));

                //
                pPlayAudio.lowLatency.stat.dwTickCnt_start = dwTickCnt;
                //
                pPlayAudio.lowLatency.stat.mql = nQNodes_left;
                pPlayAudio.lowLatency.stat.dwTickCnt_mql = dwTickCnt;
                pPlayAudio.lowLatency.stat.xql = nQNodes_left;
                pPlayAudio.lowLatency.stat.dwTickCnt_xql = dwTickCnt;
                //
            } else {
                if (nQNodes_left < pPlayAudio.lowLatency.stat.mql) {
                    pPlayAudio.lowLatency.stat.mql = nQNodes_left;
                    pPlayAudio.lowLatency.stat.dwTickCnt_mql = dwTickCnt;
                }
                if (nQNodes_left > pPlayAudio.lowLatency.stat.xql) {
                    pPlayAudio.lowLatency.stat.xql = nQNodes_left;
                    pPlayAudio.lowLatency.stat.dwTickCnt_xql = dwTickCnt;
                }
            }

            //
            int default_max_mql_ok = 4;
            int default_min_mql_ok = 3;
            int default_max_mql_ok_inMs = 150;
            int default_min_mql_ok_inMs = 100;
            int default_xql_ok_inMs = 2500; //  2000;

            {

                //
                if (pPlayAudio.lowLatency.mql > nQNodes_left) {
                    pPlayAudio.lowLatency.mql = nQNodes_left;
                    //

                    if (pProcInfo.cfg.debugStatusInfo.ucbShowPlayAudioStatus) {
                          //String tBuf = String.format(
                          String tBuf = String.format(("mql set to %d"), pPlayAudio.lowLatency.mql);
                       // _sntprintf(tBuf, mycountof(tBuf), ("mql set to %d"), pPlayAudio.lowLatency.mql);
                        showInfo_open.qyFuncs.showInfo_open(0, "my_inStream", tBuf);
                    }

                }
                if (pPlayAudio.lowLatency.xql < nQNodes_left)
                    pPlayAudio.lowLatency.xql = nQNodes_left;

                //
                if (iElapseInMs > nIntervalInMs_toAdjust) {

                    int max_mql_ok = default_max_mql_ok;
                    int min_mql_ok = default_min_mql_ok;
                    //
                    if (0 != pPlayAudio.lowLatency.avg_pkt.nAvg_pkt_inMs) {
                        int max_mql_ok_inMs = default_max_mql_ok_inMs;
                        int min_mql_ok_inMs = default_min_mql_ok_inMs;
                        //
                        int xql_ok_inMs = default_xql_ok_inMs; //  2000;

                        //
                        int diff_xql_mql = pPlayAudio.lowLatency.xql - pPlayAudio.lowLatency.mql;
                        //
                        int tmp_max_mql_ok = max_mql_ok_inMs / pPlayAudio.lowLatency.avg_pkt.nAvg_pkt_inMs;
                        int tmp_min_mql_ok = min_mql_ok_inMs / pPlayAudio.lowLatency.avg_pkt.nAvg_pkt_inMs;
                        pPlayAudio.lowLatency.xql_ok = xql_ok_inMs / pPlayAudio.lowLatency.avg_pkt.nAvg_pkt_inMs;
                        //
                        if (tmp_max_mql_ok > max_mql_ok) max_mql_ok = tmp_max_mql_ok;
                        if (tmp_min_mql_ok > min_mql_ok) min_mql_ok = tmp_min_mql_ok;
                        //
                        pPlayAudio.lowLatency.max_mql_ok = max_mql_ok;
                        pPlayAudio.lowLatency.min_mql_ok = min_mql_ok;
                        //
                        if (pProcInfo.cfg.debugStatusInfo.ucbShowPlayAudioStatus) {   //  2017/10/13

                        }
                    }
                    //
                    if (pPlayAudio.lowLatency.mql > max_mql_ok) {
                        pPlayAudio.lowLatency.bNeedAdjust = true;

                    }
                    //
                    if (pPlayAudio.lowLatency.bNeedAdjust) {
                        if (pPlayAudio.lowLatency.mql <= min_mql_ok) {
                            pPlayAudio.lowLatency.bNeedAdjust = false;
                        }
                    }
                    //
                    if (pPlayAudio.lowLatency.bNeedAdjust) {
                        if (dwTickCnt - pPlayAudio.lowLatency.dwTickCnt_last_adjust > 200) {
                            bAdjust = true;
                        }
                    }

                    if (!pPlayAudio.lowLatency.bNeedAdjust) {
                        bNeedReset = true;

                    }


                    //
                    if (bAdjust) {
                        //
                        int nSkipped = pPlayAudio.lowLatency.mql - min_mql_ok;
                        if (nSkipped > 0) {
                            //nSkipped = nSkipped / 2;
                            //
                            //if (nSkipped > 1) nSkipped = 1;
                            nSkipped = 1;
                            //
                            int i;
                            for (i = 0; i < nSkipped; i++) {
                                boolean bSkipped = false;
                                //
                                if (pProcInfo.cfg.b_useQ2_toPlayA) {
                                    myPlayAudioData.myPLAY_AUDIO_DATA pkt = new myPlayAudioData.myPLAY_AUDIO_DATA();
                                    int pktLen =128;//sizeof(myPlayAudioData.myPLAY_AUDIO_DATA);
//                                    if (0 == q2GetMsg(pPlayAudio.q2,   pkt.toByteArray(), pktLen))
                                    int q2Nodes = getQ2Nodes(pPlayAudio.q2);
                                    com.qycx.base.util.Log.e("哈哈呵呵", "my_inStream: getQ2Nodes: " + q2Nodes);
                                    if (null == (pkt = (myPlayAudioData.myPLAY_AUDIO_DATA) q2GetMsg(pPlayAudio.q2)))
                                    {
                                        //
                                        M_chk_pts(pPlayAudio, pkt);
                                        //
                                        qyTmpMemory.qyFuncs.clean_myPLAY_AUDIO_DATA.call(pkt, ("PlayAudio, bAdjust, to remove old data"));
                                        //
                                        bSkipped = true;
                                        //
                                    }
                                } else {
                                    int len = (int) (pPlayAudio.cache.uiBufSize - pPlayAudio.cache.nLeft);

                                    QY_qBuf2.HeadData_playA headData = new QY_qBuf2.HeadData_playA();
                                    ByteBuffer buffer = ByteBuffer.wrap(pPlayAudio.cache.pBuf);
                                    buffer.position(pPlayAudio.cache.nLeft);
                                    int remaining = buffer.remaining();
                                    byte[] data = new byte[remaining];

                                    buffer.get(data);
                                    if (0 == pPlayAudio.qBuf2.q2GetMsg(headData, data,0,  len))
                                    {
                                        Log.d(AUDIO_DOWNLOAD,"6 pPlayAudio.qBuf2.qs0 "+ pPlayAudio.qBuf2.qs0.uiQNodes + " pPlayAudio.qBuf2.qs1 "+ pPlayAudio.qBuf2.qs1.uiQNodes);
                                        bSkipped = true;
                                        buffer.put(data,pPlayAudio.cache.nLeft,data.length);
                                    }
                                    data = null;
                                }
                                //
                                if (bSkipped) {
                                    if (!pProcInfo.cfg.debugStatusInfo.ucb_notPrint_my_inStream) {
                                        str = String.format("my_inStream:%s,adjust,remove old,mql %s.max_mql_ok %s.nSkip %s.",
                                                pPlayer.idInfo_recorder.ui64Id, pPlayAudio.lowLatency.mql, pPlayAudio.lowLatency.max_mql_ok, nSkipped);
                                        Log.d(TAG, str);
                                    }
                                }

                            }
                        }
                        //
                        pPlayAudio.lowLatency.dwTickCnt_last_adjust = dwTickCnt;
                    }

                }
            }

            //
            if (bNeedReset) {
                pPlayAudio.lowLatency.dwTgt_start1 = dwTickCnt;
                //
                //
                pPlayAudio.lowLatency.last_mql = pPlayAudio.lowLatency.mql;
                pPlayAudio.lowLatency.last_xql = pPlayAudio.lowLatency.xql;
                //
                pPlayAudio.lowLatency.mql = nQNodes_left;
                pPlayAudio.lowLatency.xql = nQNodes_left;
                //
                //  2017/10/13
                //memset(pPlayAudio->lowLatency.ptsInfo, 0, sizeof(pPlayAudio->lowLatency.ptsInfo)  );
                pPlayAudio.lowLatency.ptsInfo = new qmcCommFunc_is_open.PLAY_AUDIO_procInfo.PLAY_AUDIO_procInfo_lowLatency.PLAY_AUDIO_procInfo_ptsInfo();
                //memset(pPlayAudio->lowLatency.avg_pkt, 0, sizeof(pPlayAudio->lowLatency.avg_pkt)  );
                pPlayAudio.lowLatency.avg_pkt = new qmcCommFunc_is_open.PLAY_AUDIO_procInfo.PLAY_AUDIO_procInfo_lowLatency.PLAY_AUDIO_procInfo_avg_pkt();
                //
                if (pProcInfo.cfg.debugStatusInfo.ucbShowPlayAudioStatus) {
                    str = String.format("my_inStream: %s, need Reset. nIntervalInMs_toAdjust %sms", pPlayer.idInfo_recorder.ui64Id, nIntervalInMs_toAdjust);
                }
            }

            //
            while (pPlayAudio.cache.nLeft < bufLen) {
                //
                int uiPts = 0;        //  2017/10/13
                int uiDataLen = 0;    //  2017/10/13
                //
                if (pProcInfo.cfg.b_useQ2_toPlayA) {
                    myPlayAudioData.myPLAY_AUDIO_DATA pkt = new myPlayAudioData.myPLAY_AUDIO_DATA();
                    int pktLen =128;//sizeof(myPlayAudioData.myPLAY_AUDIO_DATA);
                    int q2Nodes = getQ2Nodes(pPlayAudio.q2);
                    com.qycx.base.util.Log.e("哈哈呵呵", "my_inStream: getQ2Nodes1: " + q2Nodes);
                    if (null == (pkt = (myPlayAudioData.myPLAY_AUDIO_DATA) q2GetMsg(pPlayAudio.q2)))
                        break;
//                    if (0 != q2GetMsg(pPlayAudio.q2,  pkt.toByteArray(), pktLen))
//                    break;
                    if (null == pkt.memory.m_pBuf) break;
                    //
                    uiPts = pkt.head.uiPts;         //  2017/10/13
                    uiDataLen = pkt.uiSize_dec;     //  2017/10/13
                    M_chk_pts(pPlayAudio, pkt); //  2017/10/13
                    //
                    int len = (int) (pPlayAudio.cache.uiBufSize - pPlayAudio.cache.nLeft);
                    len = (int) Math.min(len, pkt.uiSize_dec);
                    //qyMemoryFunc.qyFuncs.mymemcpy( (pPlayAudio.cache.pBuf + pPlayAudio.cache.nLeft), pkt.memory.m_pBuf, (int) len);
                    System.arraycopy(pkt.memory.m_pBuf, 0, pPlayAudio.cache.pBuf, pPlayAudio.cache.nLeft, len);
                    uiDataLen = (int) len;
                    qyTmpMemory.qyFuncs.clean_myPLAY_AUDIO_DATA.call(pkt, ("PlayAudio"));
                } else {
                    QY_qBuf2.HeadData_playA headData=new QY_qBuf2.HeadData_playA();
                    int len = (int) (pPlayAudio.cache.uiBufSize - pPlayAudio.cache.nLeft);
                    //byte[] data = new byte[len];
                    byte[] data = tmpVar.m_ctxThreadPlayA.data;

                    if (0 > (len = pPlayAudio.qBuf2.q2GetMsg(  headData, data, 0,  len))) break;
                    ByteBuffer buffer = ByteBuffer.wrap(pPlayAudio.cache.pBuf);
                    buffer.position(pPlayAudio.cache.nLeft);
                    buffer.put(data, 0, len);

                    if (len != headData.uiSize_dec) {
                        int ii = 0;
                    }
                    //
                    uiPts = headData.uiPts;
                    uiDataLen = len;//headData.uiSize_dec;
                }
                //
                pPlayAudio.cache.nLeft += uiDataLen;// (int)len;
                //
                pPlayAudio.lowLatency.avg_pkt.nBytes_ptks += (int) uiDataLen;
                pPlayAudio.lowLatency.avg_pkt.n_pkts++;
                if (0 != pPlayAudio.ah.wfx.nAvgBytesPerSec) {
                    pPlayAudio.lowLatency.avg_pkt.nAvg_pkt_inMs = (int) (pPlayAudio.lowLatency.avg_pkt.nBytes_ptks * 1000.0f / pPlayAudio.lowLatency.avg_pkt.n_pkts / pPlayAudio.ah.wfx.nAvgBytesPerSec);
                }
                //
                if (pProcInfo.cfg.debugStatusInfo.ucbShowPlayAudioStatus) {

                }
                //
                pPlayAudio.cache.dwTickCnt_lastAudioData = myGetTickCount(null);

            }

            //
            if (0 != pPlayAudio.cache.nLeft) {
                //
                int len = (int) Math.min(bufLen, pPlayAudio.cache.nLeft);
                //qyMemoryFunc.qyFuncs.mymemcpy( buf,  pPlayAudio.cache.pLeft, (int) len);
                System.arraycopy(pPlayAudio.cache.pBuf, pPlayAudio.cache.indexStart, buf, 0, len);

                pPlayAudio.cache.indexStart += len;
                pPlayAudio.cache.nLeft -= (int) len;
                nRead = len;
                return nRead;
            }

            if (dwTickCnt - pPlayAudio.cache.dwTickCnt_lastAudioData > (int) pProcInfo.cfg.usTimeoutInMs_player_a) {
                //
                str = String.format("%s, %s,%s: no data arrives too long, need stop running", pPlayer.idInfo_recorder.ui64Id, pPlayAudio.uiTranNo, pPlayer.video.uiTranNo);

                showInfo_open.qyFuncs.showInfo_open(0, ("my_inStream"), str);

                //  2018/06/16
                pPlayAudio.bNeedStop_webRtc_my_inStream = true;
                //pProcInfo.ctrl.bNeedChkPlayers = true;

                //
                nRead = 0;
                return nRead;
            }

            //
            int wBits = 16;
            FillMemory(buf, bufLen, (wBits == 8) ? 128 : 0);
            nRead = bufLen;

            //  2017/10/14
            if (!pProcInfo.cfg.debugStatusInfo.ucb_notPrint_my_inStream
            ) {
                //
                if  (  pProcInfo.cfg.debugStatusInfo.b_print_test) {
                    str = String.format("%s, fill sicence data because no audio data, %s bytes", pPlayer.idInfo_recorder.ui64Id, nRead);
                    Log.d(TAG, str);
                }

            }

            //


            if (0 == nRead) {
                int ii = 1;
            }

            return nRead;
        }


    }


   public static class qyFuncs {

        public static int startPlayAudio_webRtc(qmcCommFunc_is_open.QY_PLAYER pPlayerParam) {
            int iErr = -1;
            qmcCommFunc_is_open.QY_PLAYER pPlayer = pPlayerParam;
            qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio = pPlayer.audio;


            //
            pPlayAudio.cache.uiBufSize = Math.max((bufSize_transferAudioData + 256) * 2, pPlayAudio.ah.wfx.nAvgBytesPerSec * 2);
            pPlayAudio.cache.pBuf =new byte[pPlayAudio.cache.uiBufSize];// ( byte[])mymalloc((int) pPlayAudio.cache.uiBufSize);
            if (0 == pPlayAudio.cache.uiBufSize || null == pPlayAudio.cache.pBuf) return iErr;

            //
//            pPlayAudio.cache.pLeft = pPlayAudio.cache.pBuf;
            pPlayAudio.cache.indexStart = 0;
            pPlayAudio.cache.nLeft = 0;
            pPlayAudio.cache.dwTickCnt_lastAudioData = myGetTickCount(null);

            //
            pPlayAudio.dwTickCnt_start = M_GetTickCount_audio(null);        //  2010/05/04
            pPlayer.uiSampleTimeInMs_start = 0;
            qmcCommFunc_is_open.PLAY_AUDIO_procInfo_var pm_var = pPlayAudio.m_var;


            //  2018/06/16
            pPlayAudio.bNeedStop_webRtc_my_inStream = false;

            //
            pPlayAudio.bRunning = true;
            com.qycx.base.util.Log.e(AUDIO_DOWNLOAD, "4.85 startPlayAudio_webRtc player true");

            //


            iErr = 0;

            return iErr;
        }


        public static  int stopPlayAudio_webRtc(qmcCommFunc_is_open.QY_PLAYER pPlayerParam) {
            int iErr = -1;
            qmcCommFunc_is_open.QY_PLAYER pPlayer = (qmcCommFunc_is_open.QY_PLAYER) pPlayerParam;
            qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio = pPlayer.audio;


            //
            //voe_play_exit(pPlayAudio->webRtc.pVoePlay);

            //
            pPlayAudio.bRunning = false;
            com.qycx.base.util.Log.e(AUDIO_DOWNLOAD, "4.85 stopPlayAudio_webRtc player false");
            //
            MACRO_mysafeFree( pPlayAudio.cache.pBuf);


            iErr = 0;

            return iErr;


        }

    }

}

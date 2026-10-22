package com.qycx.base;

import static com.qycx.base.Consts.AUDIO_DOWNLOAD;
import static com.qycx.base.ctxQyMc.Consts.CONST_aPlayType_android;
import static com.qycx.base.ctxQyMc.Consts.CONST_aPlayType_dSound;
import static com.qycx.base.ctxQyMc.Consts.CONST_aPlayType_webRtc;
import static com.qycx.base.ctxQyMc.Consts.CONST_vPlayType_android;
import static com.qycx.base.ctxQyMc.Consts.CONST_vPlayType_v;
import static com.qycx.base.playAudio_webRtc.qyFuncs.startPlayAudio_webRtc;
import static com.qycx.base.playAudio_webRtc.qyFuncs.stopPlayAudio_webRtc;
import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.bQThreadExists;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.waitForQThread;
import static com.qycx.base.qyWndContentType.Consts.CONST_qyWndContentType_talker;
import static com.qycx.base.qy_getuiNextTranNo.qyFuncs.getuiNextTranNo;
import static com.qycx.base.tmpFunc.qyFuncs.myEventSet;

import com.qycx.Activity_dlgTalk;
import com.qycx.base.util.Log;

public class doApplyForPlayer {

    public static class qyFuncs {

        public static int myWaitForQThread_infinite(qnmCommProc_mis.QY_qThreadProcInfo_common pQThread, String hint) {
            if (null == hint) hint = "";

            int nTries = 30;    //  这里必须停下来
            int i;

            for (i = 0; i < nTries; i++) {
                if (!bQThreadExists(pQThread)) break;

                pQThread.bQuit = true;
                //
                if (pQThread.q2.hEvent0 != null) myEventSet(pQThread.q2.hEvent0);
                //
                //
                waitForQThread(pQThread, 1000);

            }

            if (i == nTries) {   //  2013/08/02

            }

            return 0;
        }

        public static boolean bPlayAudio_needEnd(qmcCommFunc_is_open.QY_PLAYER pPlayer, qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio) {
            boolean bRet = false;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            //TCHAR tBuf[128];

            //
            long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);

            //
            if (pQyMc.bQuit) {
                return true;
            }


            //
            switch (pPlayAudio.uiPlayType) {
                case 0:
                    break;
                case CONST_aPlayType_webRtc: {
                    int nDiff = 0;
                    //  2018/06/16
                    if (pPlayAudio.bNeedStop_webRtc_my_inStream) {
                        bRet = true;
                        //
                        //_sntprintf(tBuf, mycountof(tBuf), ("bPlayAudio_needEnd: %I64u, bNeedStop is true"), pPlayer->idInfo_recorder.ui64Id);
                        //showInfo_open(0, 0, tBuf);
                    }
                    //
                    nDiff = (int) (dwTickCnt - pPlayAudio.dwLastTickCnt_startPlayer);
                    nDiff = Math.abs(nDiff);
                    if (nDiff < 500) break;
                    //
                    nDiff = (int) (dwTickCnt - pPlayAudio.cache.dwTickCnt_lastAudioData);
                    nDiff = Math.abs(nDiff);
                    if (nDiff > (int) pProcInfo.cfg.usTimeoutInMs_player_a) {
                        pPlayAudio.nTimes_noAudioData_webRtc++;
                    } else {
                        pPlayAudio.nTimes_noAudioData_webRtc = 0;
                    }

                    if (pPlayAudio.nTimes_noAudioData_webRtc > 3) {
                        bRet = true;
                        break;
                    }
                }
                break;
                default:
                    //  pPlayAudio.dwLastTickCnt_startPlayer要在启动时赋值
                    if (!pPlayAudio.bRunning) {
                        if (dwTickCnt - pPlayAudio.dwLastTickCnt_startPlayer > 500)  //  线程没启动, 或已停止
                        {
                            bRet = true;
                            break;
                        }
                    }
                    break;

            }

            //
            return bRet;
        }


        //
        public static boolean bPlayVideo_needEnd(qmcCommFunc_is_open.QY_PLAYER pPlayer, qmcCommFunc_is_open.PLAY_VIDEO_procInfo pPlayVideo) {
            boolean bRet = false;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            //TCHAR tBuf[128];

            //
            long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);

            if (pQyMc.bQuit) {
                return true;
            }


            //
            switch (pPlayVideo.uiPlayType) {
                case 0:
                    break;
                case CONST_vPlayType_v:
                default:
                    //  pPlayAudio.dwLastTickCnt_startPlayer要在启动时赋值
                    if (!pPlayVideo.bRunning) {
                        if (dwTickCnt - pPlayVideo.dwLastTickCnt_startPlayer > 500)  //  线程没启动, 或已停止
                        {
                            bRet = true;
                            break;
                        }
                    }
                    break;

            }

            //
            return bRet;
        }


        public static int chkPlayers(ctxQmc.CCtxQmc pProcInfo) {
            int iErr = -1;
            if (null == pProcInfo) return -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            int i;


            //
            String str;
            str = ("chkPlayers enters.");
            Log.d( str);
            showInfo_open.qyFuncs.showInfo_open(0, null,str);
            //
            if (null == pProcInfo.av.pPlayers) {
                return 0;
            }

            //
            long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);

            //
            qmcCommFunc_is_open.QY_PLAYER pPlayer = new qmcCommFunc_is_open.QY_PLAYER();
            qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio;
            qmcCommFunc_is_open.PLAY_VIDEO_procInfo pPlayVideo;

            //  comment: player是一个数组,在pProcInfo.av.pPlayers[]. pProcInfo是一个全局的结构
            //
            for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                pPlayer = pProcInfo.av.pPlayers[i];
                pPlayAudio = pProcInfo.av.pPlayers[i].audio;
                pPlayVideo = pProcInfo.av.pPlayers[i].video;
                //
                //  comment: 这段是判断哪个player是退出了。如果退出就回收player
                //
                dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);

                //if  (  pPlayer.idInfo_recorder.ui64Id  )
                {
                    if (0 != pPlayAudio.uiPlayType) {
                        if (bPlayAudio_needEnd(pPlayer, pPlayAudio)) {

                            //
                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("chkPlayers: clean playAudio %I64u, %d,%d"), pPlayer.idInfo_recorder.ui64Id, pPlayAudio.uiTranNo, pPlayer.video.uiTranNo);

                            //  回收
                            pPlayAudio.bQuit = true;       //  注意：bQuit不能在mcThread_playX里赋值，只能在主线程里赋值
                            switch (pPlayAudio.uiPlayType) {
                                case CONST_aPlayType_webRtc:
                                    stopPlayAudio_webRtc(pPlayer);
                                    break;
                                case CONST_aPlayType_dSound:
                                    //waitForQThread(  (  qnmCommProc_mis.QY_qThreadProcInfo_common  *  )pPlayAudio,  INFINITE  );
                                    myWaitForQThread_infinite((qnmCommProc_mis.QY_qThreadProcInfo_common) pPlayAudio, ("doApplyForPlayer"));
                                    break;
                                case CONST_aPlayType_android:
                                    guiShare.GuiShare.pf_stopPlayAudio_android.call(pPlayer);
                                    break;
                                default:
                                    //MACRO_qyAssert(0, ("ERROR: unprocessed aplayType"));
                                    break;
                            }
                            //
                            pPlayAudio.uiPlayType = 0;
                        }

                    }
                    if (0 != pPlayVideo.uiPlayType) {
                        if (bPlayVideo_needEnd(pPlayer, pPlayVideo)) {
                            //
                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("chkPlayers: clean playVideo %I64u, %d, %d"), pPlayer.idInfo_recorder.ui64Id, pPlayer.audio.uiTranNo, pPlayVideo.uiTranNo);

                            //  回收
                            pPlayVideo.bQuit = true;       //  注意：bQuit不能在mcThread_playX里赋值，只能在主线程里赋值
                            switch (pPlayVideo.uiPlayType) {
                                case CONST_vPlayType_android:
                                    //myWaitForQThread_infinite((qnmCommProc_mis.QY_qThreadProcInfo_common)pPlayVideo, ("doApplyForPlayer"));
                                    guiShare.GuiShare.pf_stopPlayVideo_android.call(pPlayer);
                                    break;
                                default:
                                    //MACRO_qyAssert(0, ("ERROR: unprocessed vplayType"));
                                    break;
                            }
                            //
                            pPlayVideo.uiPlayType = 0;
                        }
                    }
                    //
                    if (0 == pPlayAudio.uiPlayType && 0 == pPlayVideo.uiPlayType) {
                        pPlayer.idInfo_recorder.ui64Id = 0;                                        //  reset
                        pPlayAudio.uiTranNo = 0;
                        pPlayVideo.uiTranNo = 0;
                        pPlayer.iTaskId = 0;                                                       //  2009/09/17

                    }
                }

                //
                continue;

            }

            str = ("chkPlayers leaves.");
            Log.d( str);
            showInfo_open.qyFuncs.showInfo_open(0, null,str);
            return 0;
        }


        public static int doApplyForPlayer(Object hMainWnd, qnmCommProc_mis.MIS_MSGU pMsg) {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            int i;
            qmcCommFunc_is_open.QY_PLAYER pPlayer = null;
            qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio = null;
            qmcCommFunc_is_open.PLAY_VIDEO_procInfo pPlayVideo = null;


            if (null == pProcInfo) {
                int nThreads_player = 0;
                for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                    pPlayer = pProcInfo.av.pPlayers[i];
                    if (0 != pPlayer.audio.uiPlayType) nThreads_player++;
                    if (0 != pPlayer.video.uiPlayType) nThreads_player++;
                }
                if (bQThreadExists(pProcInfo.av.localAv.player.audio)) nThreads_player++;
                if (bQThreadExists(pProcInfo.av.localAv.player.video)) nThreads_player++;
                if (pProcInfo.status.nThreads_player != nThreads_player)
                    pProcInfo.status.nThreads_player = nThreads_player;

                return iErr;
            }
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            int dwThreadDaemonId;
            long dwTickCnt;

            boolean bDbg = false;
            String str;
            //TCHAR tHintBuf[256] = "";
            qnmCommProc_mis.MIS_MSG_applyForPlayer_qmc applyForPlayer_qmc = (qnmCommProc_mis.MIS_MSG_applyForPlayer_qmc) pMsg;
            // comment: 消息applyForPlayer_qmc里包含idInfo_recorder. audio.uiTranNo,video.uiTranNo来唯一代表一个player.
            if (applyForPlayer_qmc.idInfo_recorder.ui64Id == 0 && 0 == applyForPlayer_qmc.audio.uiTranNo && 0 == applyForPlayer_qmc.video.uiTranNo) {
                //safeTcsnCpy(("idInfo and uiTranNo are 0"), tHintBuf, mycountof(tHintBuf));
                int nThreads_player = 0;
                for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                    pPlayer = pProcInfo.av.pPlayers[i];
                    if (0 != pPlayer.audio.uiPlayType) nThreads_player++;
                    if (0 != pPlayer.video.uiPlayType) nThreads_player++;
                }
                if (bQThreadExists(pProcInfo.av.localAv.player.audio)) nThreads_player++;
                if (bQThreadExists(pProcInfo.av.localAv.player.video)) nThreads_player++;
                if (pProcInfo.status.nThreads_player != nThreads_player)
                    pProcInfo.status.nThreads_player = nThreads_player;

                return iErr;
            }


            //
            chkPlayers(pProcInfo);

            //  comment: player是一个数组,在pProcInfo.av.pPlayers[]. pProcInfo是一个全局的结构
            //
            for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                pPlayer = pProcInfo.av.pPlayers[i];
                pPlayAudio = pProcInfo.av.pPlayers[i].audio;
                pPlayVideo = pProcInfo.av.pPlayers[i].video;
                //
                //  comment: 这段是判断哪个player是退出了。如果退出就回收player
                //
                dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);

                if (applyForPlayer_qmc.iTaskId == pPlayer.iTaskId
                        && applyForPlayer_qmc.idInfo_recorder.ui64Id == pPlayer.idInfo_recorder.ui64Id
                        && applyForPlayer_qmc.audio.uiTranNo == pPlayAudio.uiTranNo
                        && applyForPlayer_qmc.video.uiTranNo == pPlayVideo.uiTranNo) {
                    break;
                }
            }
            if (i == pProcInfo.av.usCnt_players) {

                //  try to find an unused player
                int iStart  =  0;
                iStart  =  1;       //  不要使用0
                for (i = iStart; i < pProcInfo.av.usCnt_players; i++) {
                    pPlayer = pProcInfo.av.pPlayers[i];
                    pPlayAudio = pProcInfo.av.pPlayers[i].audio;
                    pPlayVideo = pProcInfo.av.pPlayers[i].video;
                    if (pPlayer.idInfo_recorder.ui64Id == 0
                            && 0 == pPlayAudio.uiTranNo
                            && 0 == pPlayVideo.uiTranNo) {
                        break;
                    }
                }
                if (i == pProcInfo.av.usCnt_players) {
                    int nThreads_player = 0;
                    for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                        pPlayer = pProcInfo.av.pPlayers[i];
                        if (0 != pPlayer.audio.uiPlayType) nThreads_player++;
                        if (0 != pPlayer.video.uiPlayType) nThreads_player++;
                    }
                    if (bQThreadExists(pProcInfo.av.localAv.player.audio)) nThreads_player++;
                    if (bQThreadExists(pProcInfo.av.localAv.player.video)) nThreads_player++;
                    if (pProcInfo.status.nThreads_player != nThreads_player)
                        pProcInfo.status.nThreads_player = nThreads_player;

                    return iErr;
                }

                pPlayer.idInfo_recorder.ui64Id = applyForPlayer_qmc.idInfo_recorder.ui64Id;
                //
                pPlayAudio.uiTranNo = applyForPlayer_qmc.audio.uiTranNo;
                //memcpy(pPlayAudio.ah,pMsg->applyForPlayer_qmc.audio.ah, sizeof(pPlayAudio.ah)  );
                pPlayAudio.ah = applyForPlayer_qmc.audio.ah;
                //  2009/06/14
                //memcpy(pPlayAudio.playCfg,pMsg->applyForPlayer_qmc.audio.playCfg, sizeof(pPlayAudio.playCfg)  );
                pPlayAudio.playCfg = applyForPlayer_qmc.audio.playCfg;

                //
                pPlayVideo.uiTranNo = applyForPlayer_qmc.video.uiTranNo;
                //memcpy(pPlayVideo.vh,pMsg->applyForPlayer_qmc.video.vh, sizeof(pPlayVideo.vh)  );
                pPlayVideo.vh = applyForPlayer_qmc.video.vh;
                //
                pPlayVideo.usFps = applyForPlayer_qmc.video.usFps;   //  2015/0220
                //
                pPlayer.iTaskId = applyForPlayer_qmc.iTaskId;            //  2009/09/17

                //  2016/02/18
                int pi = pQyMc.globals.lTranNo_player;
                {
                    pPlayer.uiTranNo_player = getuiNextTranNo.call();
                }

                qnmCommProc_mis.PLAYER_ID playerId = new qnmCommProc_mis.PLAYER_ID();
                playerId.index_player = i;
                playerId.uiTranNo_player = pPlayer.uiTranNo_player;
                //
                if (applyForPlayer_qmc.idInfo_recorder.ui64Id != 0) {
                    Activity_dlgTalk hTalk = null;
                    //
                    if (applyForPlayer_qmc.idInfo_logicalPeer.ui64Id == 0) {
                        Log.e("doApplyForPlayer failed, idInfo_logicalPeer is 0");
                        int nThreads_player = 0;
                        for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                            pPlayer = pProcInfo.av.pPlayers[i];
                            if (0 != pPlayer.audio.uiPlayType) nThreads_player++;
                            if (0 != pPlayer.video.uiPlayType) nThreads_player++;
                        }
                        if (bQThreadExists(pProcInfo.av.localAv.player.audio)) nThreads_player++;
                        if (bQThreadExists(pProcInfo.av.localAv.player.video)) nThreads_player++;
                        if (pProcInfo.status.nThreads_player != nThreads_player)
                            pProcInfo.status.nThreads_player = nThreads_player;

                        return iErr;
                    }
                    //
                    if (null == (hTalk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, applyForPlayer_qmc.idInfo_logicalPeer))) {
                        Log.e("doApplyForPlayer failed, findTalker "+applyForPlayer_qmc.idInfo_recorder.ui64Id+" failed");
                        int nThreads_player = 0;
                        for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                            pPlayer = pProcInfo.av.pPlayers[i];
                            if (0 != pPlayer.audio.uiPlayType) nThreads_player++;
                            if (0 != pPlayer.video.uiPlayType) nThreads_player++;
                        }
                        if (bQThreadExists(pProcInfo.av.localAv.player.audio)) nThreads_player++;
                        if (bQThreadExists(pProcInfo.av.localAv.player.video)) nThreads_player++;
                        if (pProcInfo.status.nThreads_player != nThreads_player)
                            pProcInfo.status.nThreads_player = nThreads_player;

                        return iErr;
                    }
                    guiShare.GuiShare.pf_talk_updatePlayerInfo.call(hTalk, pMsg, playerId);

                } else {
                    qmcSharedObj.QY_SHARED_OBJ sharedObj = getSharedObjByIndex(pProcInfo, applyForPlayer_qmc.index_sharedObj);
                    if (sharedObj == null){
                        int nThreads_player = 0;
                        for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                            pPlayer = pProcInfo.av.pPlayers[i];
                            if (0 != pPlayer.audio.uiPlayType) nThreads_player++;
                            if (0 != pPlayer.video.uiPlayType) nThreads_player++;
                        }
                        if (bQThreadExists(pProcInfo.av.localAv.player.audio)) nThreads_player++;
                        if (bQThreadExists(pProcInfo.av.localAv.player.video)) nThreads_player++;
                        if (pProcInfo.status.nThreads_player != nThreads_player)
                            pProcInfo.status.nThreads_player = nThreads_player;

                        return iErr;
                    }
                    qmcSharedObj.SHARED_OBJ_USR sou = sharedObj.usrs[0];
                    if (sou.iWndContentType == CONST_qyWndContentType_talker) {
                        Object hTalk = sou.hWnd_starter;
                        if (hTalk == null){
                            int nThreads_player = 0;
                            for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                                pPlayer = pProcInfo.av.pPlayers[i];
                                if (0 != pPlayer.audio.uiPlayType) nThreads_player++;
                                if (0 != pPlayer.video.uiPlayType) nThreads_player++;
                            }
                            if (bQThreadExists(pProcInfo.av.localAv.player.audio)) nThreads_player++;
                            if (bQThreadExists(pProcInfo.av.localAv.player.video)) nThreads_player++;
                            if (pProcInfo.status.nThreads_player != nThreads_player)
                                pProcInfo.status.nThreads_player = nThreads_player;

                            return iErr;
                        }
                        guiShare.GuiShare.pf_talk_updatePlayerInfo.call(hTalk, pMsg, playerId);
                    }
                }
            }
            //  2009/02/09

            //  2014/1210
            dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
            if (pPlayer==null || dwTickCnt - pPlayer.dwLastTickCnt_doApplyForPlayer < 200) {
                int nThreads_player = 0;
                for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                    pPlayer = pProcInfo.av.pPlayers[i];
                    if (0 != pPlayer.audio.uiPlayType) nThreads_player++;
                    if (0 != pPlayer.video.uiPlayType) nThreads_player++;
                }
                if (bQThreadExists(pProcInfo.av.localAv.player.audio)) nThreads_player++;
                if (bQThreadExists(pProcInfo.av.localAv.player.video)) nThreads_player++;
                if (pProcInfo.status.nThreads_player != nThreads_player)
                    pProcInfo.status.nThreads_player = nThreads_player;

                return iErr;
            }
            pPlayer.dwLastTickCnt_doApplyForPlayer = dwTickCnt;

            //

            if (0 != pPlayAudio.uiTranNo
                    && 0 == pPlayAudio.uiPlayType) {

                //  2013/05/24
                //pPlayAudio.playbackDevDesc[0] = 0;

                //  2017/02/16
                int uiPlayType = pQyMc.uiAPlayType;

                //
                if (pPlayAudio.ah.wfx.nSamplesPerSec == 16000
                        && pPlayAudio.ah.wfx.nChannels == 1
                        && pPlayAudio.ah.wfx.wBitsPerSample == 16) {
                    uiPlayType = CONST_aPlayType_webRtc;
                }

                uiPlayType = CONST_aPlayType_android;

                //
                pPlayAudio.uiPlayType = uiPlayType;
                pPlayAudio.dwLastTickCnt_startPlayer = dwTickCnt;
                pPlayAudio.bQuit = false;
                pPlayAudio.bRunning = false;
                Log.d(AUDIO_DOWNLOAD, "4.8.5 doApplyForPlayer player false");

                //
                if (uiPlayType == CONST_aPlayType_webRtc) {
                    //
                    //showInfo_open(0, ("doApplyForPlayer"), ("before startPlayAudio_webRtc"));
                    //
                    startPlayAudio_webRtc(pPlayer);
                    //
                    //showInfo_open(0, ("doApplyForPlayer"), ("after startPlayAudio_webRtc"));
                    //
                } else if (uiPlayType == CONST_aPlayType_dSound)  //  2017/02/16
                {
                } else if (uiPlayType == CONST_aPlayType_android) {
                    guiShare.GuiShare.pf_startPlayAudio_android.call(pPlayer);
                } else {   //  use wave api. 2010/05/03

                }
            }
            //
            if (0 != pPlayVideo.uiTranNo
                    && 0 == pPlayVideo.uiPlayType) {
                int uiPlayType = CONST_vPlayType_android;

                //
                pPlayVideo.uiPlayType = uiPlayType;
                pPlayVideo.dwLastTickCnt_startPlayer = dwTickCnt;
                pPlayVideo.bQuit = false;
                pPlayVideo.bRunning = false;

                guiShare.GuiShare.pf_startPlayVideo_android.call(pPlayer);

            }


            iErr = 0;

            

            int nThreads_player = 0;
            for (i = 0; i < pProcInfo.av.usCnt_players; i++) {
                pPlayer = pProcInfo.av.pPlayers[i];
                if (0 != pPlayer.audio.uiPlayType) nThreads_player++;
                if (0 != pPlayer.video.uiPlayType) nThreads_player++;
            }
            if (bQThreadExists(pProcInfo.av.localAv.player.audio)) nThreads_player++;
            if (bQThreadExists(pProcInfo.av.localAv.player.video)) nThreads_player++;
            if (pProcInfo.status.nThreads_player != nThreads_player)
                pProcInfo.status.nThreads_player = nThreads_player;


            return iErr;

        }

    }
}

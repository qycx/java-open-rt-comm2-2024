package com.qycx.base;

import static android.media.AudioManager.AUDIO_SESSION_ID_GENERATE;
import static com.qycx.base.Consts.AUDIO_DOWNLOAD;
import static com.qycx.base.Consts.CONST_iParamType_playa;
import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.dlgPolicyIsClient.qyFuncs_Droid.fixPlayCfg;
import static com.qycx.base.playAudio_webRtc.qyFuncs_Droid.my_inStream;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;
import static com.qycx.base.qyTick.qyFuncs.M_GetTickCount_audio;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

import com.qycx.base.util.Log;

public class qmcPlayAudio
{
    public static class qyFuncs_Droid
    {
        public final static int DEFAULT_dwToInMs_playAudio = 200;

        public static  int initAudioPlayDev_android(Object pDynLib_dx, qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio, int dwBufferBytes, qmcSound_dx.myPLAY_SOUND_dx p)
        {
            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            if (null != p.m_playDev_android.m_audioTrack) return -1;

            int SAMPLE_RATE_INHZ = (int)pPlayAudio.ah.wfx.nSamplesPerSec;
            int channelConfig;
            int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
            //
            int iRecorder_audioSessionId = AUDIO_SESSION_ID_GENERATE;
            if (pProcInfo.av.localAv.recordSoundProcInfo.bCapDevConnected)
            {
                iRecorder_audioSessionId = pProcInfo.av.localAv.recordSoundProcInfo.iRecorder_audioSessionId;
            }

            switch (pPlayAudio.ah.wfx.nChannels)
            {
                case 1:
                    channelConfig = AudioFormat.CHANNEL_OUT_MONO;
                    break;
                case 2:
                    channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
                    break;
                default:
                    Log.d("startPlayAudio_android failed, unsuported nChannels");
                    exitAudioPlayDev_android(pDynLib_dx, pPlayAudio,  p);
                    return iErr;
            }
            int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, channelConfig, AUDIO_FORMAT);
            p.m_playDev_android.m_audioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
                new AudioFormat.Builder().setSampleRate(SAMPLE_RATE_INHZ)
                    .setEncoding(AUDIO_FORMAT)
                    .setChannelMask(channelConfig)
                    .build(),
                minBufferSize,
                    AudioTrack.MODE_STREAM,
                iRecorder_audioSessionId);
            AudioTrack audioTrack = (AudioTrack)p.m_playDev_android.m_audioTrack;
            audioTrack.play();

            p.minBufferSize = minBufferSize;

            iErr = 0;
            return iErr;
        }

        public static int exitAudioPlayDev_android(Object pDynLib_dx, qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio, qmcSound_dx.myPLAY_SOUND_dx p)
        {
            if (p.m_playDev_android.m_audioTrack == null) return 0;
            AudioTrack audioTrack = (AudioTrack)p.m_playDev_android.m_audioTrack;

            try
            {
                audioTrack.stop();
            }
            catch (IllegalStateException e)
            {
                e.printStackTrace();
                return -1;
            }

            audioTrack.release();
            //
            p.m_playDev_android.m_audioTrack = null;
            return 0;
        }


        public static int getnWrite_toFillAudio(int nAvgBytesPerSec)
        {
            if (0 == (nAvgBytesPerSec % 100)) return nAvgBytesPerSec / 100 * 4;
            return 0;
        }

        public static int MACRO_getnWrite_toFillSilence(int nAvgBytesPerSec)
        {
            //
            if (0 == (nAvgBytesPerSec % 100)) return nAvgBytesPerSec / 100 * 4;
            //
            if (0 == (nAvgBytesPerSec % 10)) return nAvgBytesPerSec / 10;
            //
            return ((0 == (nAvgBytesPerSec % 5)) ? (nAvgBytesPerSec / 5) : nAvgBytesPerSec);
        }


        //public static  int processOutput(void  pMgr, qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio, myPLAY_SOUND_dx p, boolean bFillWithSilence, byte[] pbInput1, int dwNumBytes)
        public static  int processOutput(Object pMgr, qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio, qmcSound_dx.myPLAY_SOUND_dx p, boolean bFillWithSilence, byte[] pbInput1, qyCommProc_open.ProcessOutputParams processOutputParams)
        {
            int iErr = -1;

            AudioTrack audioTrack = (AudioTrack)p.m_playDev_android.m_audioTrack;

            //
            boolean bDbg = false;
            String str;
            int tickCnt0 = 0;
            int tickCnt1 = 0;

            if (processOutputParams.nLeft < processOutputParams.nWrite)
            {
                //Log.d("processOutput failed, nLeft < nWrite");
                return -1;
            }

            int nWritten = 0;
            int n_toWrite = 0;
            for (; ; )
            {
                if (processOutputParams.nWrite - nWritten < p.minBufferSize)
                {
                    if (0 != nWritten) break;
                    //
                    n_toWrite = processOutputParams.nWrite;
                }
                else
                {
                    n_toWrite = p.minBufferSize;
                }


                //audioTrack.write(pbInput1, offsetInBytes, n_toWrite, WriteMode.NonBlocking);
                int write = audioTrack.write(pbInput1, processOutputParams.offset_pLeft, n_toWrite);
                //
                if  (  false  ) {
                    if (write > 0) {
                        Log.d(Consts.VIDEO_DOWNLOAD, "7 audio play successed.size " + write);
                    } else {
                        Log.d(Consts.VIDEO_DOWNLOAD, "7 audio play failed.size " + write);
                    }
                }

                //
                p.dwMyWriteCursor += (int)n_toWrite;
                //
                processOutputParams.offset_pLeft += n_toWrite;
                processOutputParams.nLeft -= n_toWrite;

                //
                nWritten += n_toWrite;
            }

            //
            processOutputParams.nWritten = nWritten;

            iErr = 0;
        

            return processOutputParams.nWritten;
        }


        public static int fillSilence(qmcCommFunc_is_open.QY_PLAYER pPlayer, int nWrite, byte[] pBuf, int offset_pLeft,  int nLeft, boolean bDbg)
        {

            if (offset_pLeft + nLeft + nWrite > pBuf.length) return -1;
            for (int i = 0; i < nWrite; i++)
            {
                pBuf[offset_pLeft + nLeft + i] = 0;
            }
            nLeft += (int)nWrite;

            return nLeft;
        }

        public static qmcFunc.PF_threadProc mcThreadProc_playAudio = (Object obj)->{


            Log.d( "mcThreadProc_playAudio enter");
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            boolean bDbg = false;
            String str;

            int iErr = -1;

            qmcCommFunc_is_open.QY_PLAYER pPlayer = (qmcCommFunc_is_open.QY_PLAYER) obj;
            qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio = null;

            qmcSound_dx.myPLAY_SOUND_dx myPs=new qmcSound_dx.myPLAY_SOUND_dx();
            Object pMgr = null;
            Object pDynLib = null;

            int dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
            int i;
            int hr;
            int len;
            qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(CONST_iParamType_playa,0);

            long dwTickCnt_start = 0;
            qySyncCommProc.CQyMalloc mallocObj_tmp_pBuf = new qySyncCommProc.CQyMalloc();
            try {
                ;
                byte[] pBuf = null; //todo possible move_copy
                int uiBufSize;

                byte[] tmp_pBuf = null; //todo possible move_copy

                //byte[] pLeft = null;                                 //  2009/04/27
                qyCommProc_open.ProcessOutputParams processOutputParams = new qyCommProc_open.ProcessOutputParams();
//                int offset_pLeft = 0;
//                int nLeft = 0;

                int uiSampleTimeInMs_pLeft = 0;                                       //  2009/05/04
                int dwBytes_cur2Write = 0;
                int nAvgBytesPerSec = 0;
                int dwCurPlayCursor = 0;

                qyTick.CQyTick tickObj = new qyTick.CQyTick();
                long dwTickCnt_lastAudioData = M_GetTickCount_audio(tickObj); ;                        //

                myPlayAudioData.myPLAY_AUDIO_DATA tmp_pkt=new myPlayAudioData.myPLAY_AUDIO_DATA();
                myPlayAudioData.myPLAY_AUDIO_DATA pPkt =tmp_pkt;//  (  myPLAY_AUDIO_DATA  *  )mallocObj_pPkt.mallocf(  iAllocType,  uiBufSize_pPkt  );
                if (null == pPkt){
                    Log.d( ("playAudio pPkt is null"));
                    return ;
                }
                int pktLen;

//                int nWrite = 0;
//                int nWritten = 0;
                int uiTimeInMs_nWrite;
                boolean bNeedCheck_oldAudio = false;
                long dwTickCnt_bNeedCheck_oldAudio = 0;    //  2015/07/03

                int dwToInMs = DEFAULT_dwToInMs_playAudio;
                int iNotifyInterval;

                //qyFuncs.mymemset((byte[])myPs, 0, sizeof(myPLAY_SOUND_dx));
                myPs = new qmcSound_dx.myPLAY_SOUND_dx();

                if (pPlayer == null) {
                    Log.d( ("playAudio pPlayer is null"));
                    return;
                }
                pPlayAudio = pPlayer.audio;

                String pName = pPlayAudio.q2.cfg.name;
                {
                    str = String.format("playAudio %s enters", new String(pName));
                    Log.d( str);
                }

                nAvgBytesPerSec = pPlayer.audio.ah.wfx.nAvgBytesPerSec;
                if (0 == nAvgBytesPerSec)
                {
                    Log.e( ("playAudio failed: nAvgBytesPerSec is 0"));
                    if (myPs.bPlayInited)
                    {
                        exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                        myPs.bPlayInited = false;
                    }
                    pPlayAudio.qBuf2.free();
                    pPlayAudio.bRunning = false;
                    return;
                }

                int maxDataBufLen = 48000 * 2 * 3 / 2;
                int result = pPlayer.audio.qBuf2.alloc(maxDataBufLen);
                Log.d( "playAudio qBuf2 alloc try to alloc " + result);
                if (result != 0) {//todo 这个地方有问题，因为没有地方释放，所以到这里就return了，音频声音出不来，就是因为在这里退出了
                    Log.e( ("playAudio qBuf2 alloc failed"));
                    if (myPs.bPlayInited)
                    {
                        exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                        myPs.bPlayInited = false;
                    }
                    pPlayAudio.qBuf2.free();
                    pPlayAudio.bRunning = false;
                    return;
                }


                //  fix playCfg. 2009/06/14
                fixPlayCfg(pProcInfo,  pPlayAudio.playCfg);
                iNotifyInterval = pPlayAudio.playCfg.usNotifyIntervalInMs_needMoreAudioData;

                //pPlayAudio.bQuit  =  false;
                pPlayAudio.dwTickCnt_start = M_GetTickCount_audio(tickObj);        //  2010/05/04
                //pPlayAudio.bRunning = true; //???????why remove it????

                Log.e(AUDIO_DOWNLOAD ,"mcThreadProc_playAudio 4.85 will start should be true but not player true");
                pPlayer.uiSampleTimeInMs_start = 0;
                //memset(pPlayAudio.m_var, 0, sizeof(pPlayAudio.m_var)  );
                pPlayAudio.m_var = new qmcCommFunc_is_open.PLAY_AUDIO_procInfo_var();

                uiBufSize = Math.max((bufSize_transferAudioData + 256) * 2, pPlayAudio.ah.wfx.nAvgBytesPerSec * 2);
                //pBuf = mallocObj_pBuf.mallocf((int)uiBufSize);
                pBuf = new byte[uiBufSize];
                if (0 == uiBufSize || null == pBuf) {
                    Log.d("playSound: 0 == uiBufSize || null == pBuf)");
                    if (myPs.bPlayInited)
                    {
                        exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                        myPs.bPlayInited = false;
                    }
                    pPlayAudio.qBuf2.free();
                    pPlayAudio.bRunning = false;
                    return;
                }

                //
                tmp_pBuf =new byte[uiBufSize];
                if (tmp_pBuf == null) {
                    Log.i("playSound: 301");
                    if (myPs.bPlayInited)
                    {
                        exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                        myPs.bPlayInited = false;
                    }
                    pPlayAudio.qBuf2.free();
                    pPlayAudio.bRunning = false;
                    return;
                }

                //
                //pLeft = pBuf; nLeft = 0;
                processOutputParams.offset_pLeft = 0;
                processOutputParams.nLeft = 0;

                //  runBuffers_play(  pMgr,  &myPs  );
                //  bPlayStarted  =  true;

                for (; !pPlayAudio.bQuit;)
                {

                    if (0 == dwToInMs || dwToInMs > DEFAULT_dwToInMs_playAudio)
                    {
                        dwToInMs = DEFAULT_dwToInMs_playAudio;
                    }

                    long dwTickCnt0 = M_GetTickCount_audio(tickObj);
                    long dwTickCnt;


                    dwTickCnt = M_GetTickCount_audio(tickObj);

                    bNeedCheck_oldAudio = false;

                    //  if  (  dwEvt  ==  0  )
                    {
                        if (!myPs.bPlayInited)
                        {

                            int dwOffset_notify_unit = pPlayAudio.ah.wfx.nAvgBytesPerSec;

                            if (0 != initAudioPlayDev_android(pDynLib, pPlayAudio, dwOffset_notify_unit * 4,  myPs)) {
                                Log.e( ("playAudio initAudioPlayDev_android init failed"));
                                if (myPs.bPlayInited)
                                {
                                    exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                                    myPs.bPlayInited = false;
                                }
                                pPlayAudio.qBuf2.free();
                                pPlayAudio.bRunning = false;
                                return;
                            }
                            myPs.bPlayInited = true;
                            dwTickCnt_start = tmpFunc.qyFuncs.myGetTickCount(null);

                            //
                            myPs.dwOffset_notify_unit = dwOffset_notify_unit;

                            if (0 == pPlayAudio.ah.wfx.nAvgBytesPerSec) {
                                Log.e( ("playAudio nAvgBytesPerSec"));
                                if (myPs.bPlayInited)
                                {
                                    exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                                    myPs.bPlayInited = false;
                                }
                                pPlayAudio.qBuf2.free();
                                pPlayAudio.bRunning = false;
                                return;
                            }
                            myPs.uiMaxMsgWaiting = 10;

                            if (0 == myPs.uiMaxMsgWaiting)
                            {
                                Log.d("playSound: 348");
                                if (myPs.bPlayInited)
                                {
                                    exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                                    myPs.bPlayInited = false;
                                }
                                pPlayAudio.qBuf2.free();
                                pPlayAudio.bRunning = false;
                                return;
                            }
                        }

                        if (!myPs.bPlayStarted)
                        {
                            //
                            processOutputParams.nWrite = MACRO_getnWrite_toFillSilence((int)myPs.dwOffset_notify_unit);
                            if (-1 == (processOutputParams.nLeft = fillSilence(pPlayer, processOutputParams.nWrite, pBuf, processOutputParams.offset_pLeft,  processOutputParams.nLeft, bDbg)))
                            {
                                Log.e("playSound: fillSilence failed ");
                                if (myPs.bPlayInited)
                                {
                                    exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                                    myPs.bPlayInited = false;
                                }
                                pPlayAudio.qBuf2.free();
                                pPlayAudio.bRunning = false;
                                return;
                            }
                            if (-1 == (processOutputParams.nWritten = processOutput(null, pPlayAudio,  myPs, true, pBuf,  processOutputParams)))
                            {
                                Log.e("playSound: processOutput failed, try to restart");
                                if (myPs.bPlayInited)
                                {
                                    exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                                    myPs.bPlayInited = false;
                                }
                                pPlayAudio.qBuf2.free();
                                pPlayAudio.bRunning = false;
                                return;
                            }
                            uiTimeInMs_nWrite = (int)(processOutputParams.nWritten * 1000 / nAvgBytesPerSec);
                            pPlayAudio.m_var.uiTimeInMs_audio_remains += uiTimeInMs_nWrite;

                            myPs.bPlayStarted = true;
                        }
                    }

                    if (!myPs.bPlayInited || !myPs.bPlayStarted)
                    {

                        dwToInMs = DEFAULT_dwToInMs_playAudio;

                        Log.e("event1, play not inited,  not started, or in silence. skipped");
                        continue;
                    }

                    long dwTickCnt_GetCurrentPosition =qyTick.qyFuncs.M_GetTickCount_audio(tickObj);

                    AudioTrack audioTrack = (AudioTrack)myPs.m_playDev_android.m_audioTrack;
                    dwCurPlayCursor = (int)(dwTickCnt_GetCurrentPosition - dwTickCnt_start) * pPlayAudio.ah.wfx.nAvgBytesPerSec / 1000;


                    int iDiff_dwTickCnt_GetCurrentPosition = 0;
                    pPlayAudio.m_var.dwTickCnt_GetCurrentPosition = dwTickCnt_GetCurrentPosition;

                    if (myPs.dwMyWriteCursor >= dwCurPlayCursor) dwBytes_cur2Write = myPs.dwMyWriteCursor - dwCurPlayCursor;
                    else
                    {
                        //dwBytes_cur2Write = myPs.dwMyWriteCursor + myPs.dwBufferBytes - dwCurPlayCursor;
                        dwBytes_cur2Write = 0;
                    }

                    //  2012/02/19
                    int uiTimeInMs_audio_remains = dwBytes_cur2Write * 1000 / nAvgBytesPerSec;

                    pPlayAudio.m_var.uiTimeInMs_audio_remains = uiTimeInMs_audio_remains;

                    for (; processOutputParams.nLeft < myPs.dwOffset_notify_unit * pPlayAudio.playCfg.usAudioDataAddedToPlayEveryTime / 1000;)
                    {
                        if (processOutputParams.offset_pLeft != 0)
                        {
                            if (0 != processOutputParams.nLeft)
                            {
                                System.arraycopy(pBuf,processOutputParams.offset_pLeft,pBuf,0,processOutputParams.nLeft);
                            }
                            //pLeft = pBuf;
                            processOutputParams.offset_pLeft = 0;
                        }

                        //
                        int iTimeInMs_nLeft = 0;
                        iTimeInMs_nLeft = (int)(processOutputParams.nLeft * 1000.0f / nAvgBytesPerSec);

                        //
                        len = (int)(uiBufSize - processOutputParams.nLeft);
                        if (len <= 0)
                        {
                            Log.e("mcThreadproc_playSound failed: len "+len+" < 0");
                            if (myPs.bPlayInited)
                            {
                                exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                                myPs.bPlayInited = false;
                            }
                            pPlayAudio.qBuf2.free();
                            pPlayAudio.bRunning = false;
                            return;
                        }

                        //
                        int len_suggest = getnWrite_toFillAudio((int)pPlayAudio.ah.wfx.nAvgBytesPerSec);
                        if (len_suggest == 0) {
                            Log.e("len_suggest == 0");
                            if (myPs.bPlayInited)
                            {
                                exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                                myPs.bPlayInited = false;
                            }
                            pPlayAudio.qBuf2.free();
                            pPlayAudio.bRunning = false;
                            return;
                        }
                        //
                        len = Math.min(len, len_suggest);

                        //
                        len = my_inStream(tmpVar, pPlayer, tmp_pBuf, len);
                        if (len == 0)
                        {
                            //
                            str = String.format("%s:doPlayAudio will quit, my_inStream returns 0", pPlayer.idInfo_recorder.ui64Id);
                            Log.d( str);
                            //
                            //pbQuit = true;
                            iErr = 0;
                            if (myPs.bPlayInited)
                            {
                                exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                                myPs.bPlayInited = false;
                            }
                            pPlayAudio.qBuf2.free();
                            pPlayAudio.bRunning = false;
                            return;
                        }
                        System.arraycopy(tmp_pBuf,0, pBuf, processOutputParams.nLeft, len);
//                        System.arraycopy(pPlayer.audio.cache.pBuf,0, pBuf, processOutputParams.nLeft, len);

                        //
                        processOutputParams.nLeft += (int)len;

                        //  2011/11/30
                        dwTickCnt_lastAudioData = dwTickCnt;        //  audio data received

                        //  pPlayer.audio.uiSampleTimeInMs  =  pkt.uiSampleTimeInMs;
                        if (0 != pPkt.head.uiSampleTimeInMs)
                        {
                            uiSampleTimeInMs_pLeft = (int)((pPkt.head.uiSampleTimeInMs >= iTimeInMs_nLeft) ? (pPkt.head.uiSampleTimeInMs - iTimeInMs_nLeft) : 0);
                        }
                    }
                    processOutputParams.nWrite = 0;

                    if (0 != processOutputParams.nLeft)
                    {

                        processOutputParams.nWrite = (int)Math.min(myPs.dwOffset_notify_unit, processOutputParams.nLeft);
                        //
                        if (-1 == (processOutputParams.nWritten = processOutput(null, pPlayAudio,  myPs, true, pBuf,  processOutputParams)))
//                        if (0 != processOutput(pMgr, pPlayAudio,  myPs, false, pBuf,  offset_pLeft,  nLeft, (int)nWrite,  nWritten))
                        {
                            Log.e("playSound: processOutput failed, try to restart");
                            if (myPs.bPlayInited)
                            {
                                exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                                myPs.bPlayInited = false;
                            }
                            pPlayAudio.qBuf2.free();
                            pPlayAudio.bRunning = false;
                            return;
                        }
                        uiTimeInMs_nWrite = (int)(processOutputParams.nWritten * 1000 / nAvgBytesPerSec);
                        pPlayAudio.m_var.uiTimeInMs_audio_remains += uiTimeInMs_nWrite;

                        //offset_pLeft += nWrite;
                        //nLeft -= (int)nWrite;
                    }
                    else
                    {
                        pPlayer.audio.m_var.nTimes_noAudioData++;

                        if (pPlayer.audio.m_var.nTimes_noAudioData > 1)
                        {
                            pPlayer.audio.m_var.bVideoReady = false;                   //  this means to check video again
                        }

                        //  nWrite  =  myPs.dwOffset_notify_unit;
                        processOutputParams.nWrite = MACRO_getnWrite_toFillSilence((int)myPs.dwOffset_notify_unit);
//                        if (0 != fillSilence(pPlayer, nWrite, pBuf, offset_pLeft,  nLeft, bDbg))
                        if (-1 == (processOutputParams.nLeft = fillSilence(pPlayer, processOutputParams.nWrite, pBuf, processOutputParams.offset_pLeft,  processOutputParams.nLeft, bDbg)))
                        {
                            Log.e("playSound: fillSilence failed, try to restart");
                            if (myPs.bPlayInited)
                            {
                                exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                                myPs.bPlayInited = false;
                            }
                            pPlayAudio.qBuf2.free();
                            pPlayAudio.bRunning = false;
                            return;
                        }

                        if (-1 == (processOutputParams.nWritten = processOutput(null, pPlayAudio,  myPs, true, pBuf,  processOutputParams)))
//                        if (0 != processOutput(pMgr, pPlayAudio,  myPs, true, pBuf,  offset_pLeft,  nLeft, (int)nWrite,  nWritten))
                        {
                            Log.e("playSound: processOutput failed, try to restart");
                            if (myPs.bPlayInited)
                            {
                                exitAudioPlayDev_android(pDynLib, pPlayAudio, myPs);
                                myPs.bPlayInited = false;
                            }
                            pPlayAudio.qBuf2.free();
                            pPlayAudio.bRunning = false;
                            return;
                        }
                        uiTimeInMs_nWrite = (int)(processOutputParams.nWritten * 1000 / nAvgBytesPerSec);
                        pPlayAudio.m_var.uiTimeInMs_audio_remains += uiTimeInMs_nWrite;

                        dwToInMs += uiTimeInMs_nWrite;

                    }


                    //  2015/07/05. 不应该在wait后处理，因为这时候可能音频数据已经播放完了，所以remains要是总小于usMinTimeInMsOfAudioDataPlaying,就无法清理队列了，要等填完数据就处理比较好
                    //
                    if (pPlayAudio.m_var.uiTimeInMs_audio_remains >= pPlayAudio.playCfg.usMinTimeInMsOfAudioDataPlaying)
                    {
                        //  2015/07/03
                        if (!bNeedCheck_oldAudio)
                        {
                            //
                            int nQ2 =qyq2.qyFuncs.getQ2Nodes_toGetMsg(pPlayAudio.q2);
                            //
                            if (nQ2 > 4)
                            {  //  2015/07/03
                                long tmp_dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
                                if (tmp_dwTickCnt - dwTickCnt_bNeedCheck_oldAudio > 1000)
                                {
                                    dwTickCnt_bNeedCheck_oldAudio = tmp_dwTickCnt;
                                    //
                                    bNeedCheck_oldAudio = true;
                                }
                            }
                        }
                        //
                        if (bNeedCheck_oldAudio)
                        {
                            if (0 != pPlayAudio.m_var.uiSampleTimeInMs && qyq2.qyFuncs.getQ2Nodes_toGetMsg(pPlayAudio.q2) > 1)
                            {

                            }
                        }

                    }

                    dwToInMs = pPlayAudio.m_var.uiTimeInMs_audio_remains;
                    //
                    int iElapseInMs_fromGetCurrentPosition = 0;
                    //
                    if (0 != processOutputParams.nWrite && 0 != uiSampleTimeInMs_pLeft)
                    {
                        int iTimeInMs_cur2Write = (int)(dwBytes_cur2Write * 1000.0f / nAvgBytesPerSec);
                        pPlayer.audio.m_var.uiSampleTimeInMs = uiSampleTimeInMs_pLeft;
                        pPlayer.audio.m_var.dwTickCnt_uiSampleTimeInMs_internal = (int)(dwTickCnt + iTimeInMs_cur2Write);
                        pPlayer.audio.m_var.dwTickCnt_uiSampleTimeInMs_syncAv = qyTick.qyFuncs.M_GetTickCount_av(tickObj) + iTimeInMs_cur2Write;      //  2012/02/19
                        pPlayer.audio.m_var.dwTickCnt_uiSampleTimeInMs_syncRecv = tmpFunc.qyFuncs.myGetTickCount(null);

                        //
                        uiSampleTimeInMs_pLeft = 0;

                        if (pProcInfo.cfg.debugStatusInfo.ucbShowPlayAudioStatus)
                        {   //  2015/05/24
                            //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("playAudio: %I64u,%d: iSampleTimeInMs: %u, iTimeInMs_cur2Write %d, audio_remains %d"), pPlayer.idInfo_recorder.ui64Id, pPlayer.audio.uiTranNo, pPlayer.audio.m_var.uiSampleTimeInMs, iTimeInMs_cur2Write, pPlayer.audio.m_var.uiTimeInMs_audio_remains);
                        }
                    }


                    continue;
                }

            } catch (Exception e) {
                com.qycx.base.util.Log.e(e.getMessage());
            } finally {
                Log.i("mcThreadProc_playAudio will quit");

                if (myPs.bPlayInited)
                {
                    exitAudioPlayDev_android(pDynLib, pPlayAudio,  myPs);
                    myPs.bPlayInited = false;
                }

                if(pPlayAudio.qBuf2 != null){//zzq
                    pPlayAudio.qBuf2.free();
                }

                pPlayAudio.bRunning = false;
                Log.e(AUDIO_DOWNLOAD ,"mcThreadProc_playAudio 4.85 will quit player false ");
            }



//            String pName = pPlayAudio.q2.cfg.name;
//            {
//                str = String.format("playAudio %s:%s: leaves", new String(pName), pPlayer.idInfo_recorder.ui64Id);
//                Log.d( str);
//            }
        };
    }
}


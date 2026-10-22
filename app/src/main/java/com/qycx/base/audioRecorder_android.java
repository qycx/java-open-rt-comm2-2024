package com.qycx.base;

import static com.qycx.base.Consts.AUDIO_UPLOAD;
import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.CONST_channelType_realTimeMedia;
import static com.qycx.base.ctxQmcTmpl.Consts.CONST_fn_dump_pcm_enc;
import static com.qycx.base.policyIsClientFunc.qyFuncs_Droid.MACRO_getuiBytesRecorded_perBlockAlign;
import static com.qycx.base.qmCommonFunc_mis.Consts.CONST_uiMaxQNodes_outputQ_toMix_audio;
import static com.qycx.base.qmcSound_dx.qyFuncs_Droid.processInput_post;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.widget.Toast;

import com.qycx.base.statuscenter.MeetingStatusCenter;
import com.qycx.base.statuscenter.messagebean.AudioOutStatusEvent;
import com.qycx.base.statuscenter.messagebean.MediaOutStatus;
import com.qycx.base.statuscenter.messagebean.VideoOutStatusEvent;
import com.qycx.base.util.Log;
import com.qycx.base.util.ToastUtils;
import com.qycx.base.util.javatuples.Pair;

import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.bChannelOk;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;

public class audioRecorder_android {
    public static class AudioCapDev_android {
        public AudioRecord m_recorder;
        public qyCommProc_open.WAVEFORMATEX m_wfx_org = new qyCommProc_open.WAVEFORMATEX();
        public byte[] data;
        public int minBufferSize;

        public boolean isMuted = false;

        public AcousticEchoCanceler m_aec;
        public NoiseSuppressor m_ans;
        public AutomaticGainControl m_agc;
        public AudioCapDev_android() {
        }

    }

    public static class qyFuncs_Droid {

        private static int count_audio = 0;

        // 添加一个方法来切换麦克风静音状态
        public static void setMicrophoneOn(boolean bOn ) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo == null) return;
            AudioCapDev_android p = (AudioCapDev_android) pProcInfo.av.localAv.recordSoundProcInfo.u_android.pAudioCapDev;
            if (p != null) {
                p.isMuted= !bOn;
                if (p.isMuted) {
                    //m_recorder.startRecording();
                    //isMuted = false;
                    Log.d("Microphone", "关闭麦克风");
                } else {
                    //m_recorder.stop();
                    //isMuted = true;
                    Log.d("Microphone", "开启麦克风");
                }
            }
        }



        public static void changeMicrophoneStatus(Context context) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo == null) return;
            AudioCapDev_android p = (AudioCapDev_android) pProcInfo.av.localAv.recordSoundProcInfo.u_android.pAudioCapDev;
            if (p != null) {
                //
                boolean bOn  =  p.isMuted;  //  因为要切换
                //
                audioRecorder_android.qyFuncs_Droid.setMicrophoneOn(bOn);
                //
                String message = p.isMuted ? "关闭麦克风" : "开启麦克风";
                //dataLen_pAudioDataBuf = 0;
                ToastUtils.showToast(message);
            }
        }

        public static boolean isMicrophoneMuted() {
            boolean bRet = false;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if  (  pQyMc==null) return false;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if  (  pProcInfo==null) return false;
            AudioCapDev_android p = (AudioCapDev_android) pProcInfo.av.localAv.recordSoundProcInfo.u_android.pAudioCapDev;
            if  (  p==null)return false;
            //
            return p.isMuted;
        }

        @SuppressLint("MissingPermission")
        public static int initAudioCapDev_android(Object p_reserved, String displayName_selected, Object ppCapStuff, qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pCompressor,
                                                  boolean bForConfig, Object hWnd_notify, Object lInstanceData, AudioCapDev_android pDev) {
            int iErr = -1;

            int channelConfig;
            int audioFormat;

            switch (pCompressor.wf_decompress_default.nChannels) {
                case 1:
                    channelConfig = AudioFormat.CHANNEL_IN_MONO;
                    break;
                default:
                    return iErr;
            }
            switch (pCompressor.wf_decompress_default.wBitsPerSample) {
                case 16:
                    audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                    break;
                default:
                    return iErr;
            }

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            Activity mainWnd = (Activity) pQyMc.gui.hMainWnd;
            AudioManager mAudioManager = (AudioManager) mainWnd.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

            int minBufferSize = AudioRecord.getMinBufferSize((int) pCompressor.wf_decompress_default.nSamplesPerSec, channelConfig, audioFormat);
            pDev.m_recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, (int) pCompressor.wf_decompress_default.nSamplesPerSec,
                    channelConfig, audioFormat, minBufferSize);

            pDev.m_wfx_org = pCompressor.wf_decompress_default;
            pDev.minBufferSize = minBufferSize;
            pDev.data = new byte[minBufferSize];

            if (AcousticEchoCanceler.isAvailable()) {
                pDev.m_aec = AcousticEchoCanceler.create(pDev.m_recorder.getAudioSessionId());
                if (pDev.m_aec != null) {
                    pDev.m_aec.setEnabled(true);
                }
            }

            /*
            if(NoiseSuppressor.isAvailable())
            {
                pDev.m_ans = NoiseSuppressor.create(pDev.m_recorder.getAudioSessionId());
                if(pDev.m_ans != null)
                {
                    pDev.m_ans.setEnabled(true);
                }
            }
            if(AutomaticGainControl.isAvailable())
            {
                pDev.m_agc = AutomaticGainControl.create(pDev.m_recorder.getAudioSessionId());
                if(pDev.m_agc!=null)
                {
                    pDev.m_agc.setEnabled(true);
                }
            }

             */


             //
            iErr = 0;

            return iErr;
        }

        public static void exitAudioCapDev_android(Object ppAudioCapDevParam) {
            AudioCapDev_android pDev = (AudioCapDev_android) ppAudioCapDevParam;

            if (pDev != null) {
                if (pDev.m_aec != null) {
                    pDev.m_aec.setEnabled(false);
                    pDev.m_aec.release();
                    pDev.m_aec = null;
                }
                if(pDev.m_ans!=null)
                {
                    pDev.m_ans.setEnabled(false);
                    pDev.m_ans.release();
                    pDev.m_ans = null;
                }
                if (pDev.m_recorder != null) {
                    pDev.m_recorder.release();
                    pDev.m_recorder = null;
                }
                if(pDev.m_agc!=null)
                {
                    pDev.m_agc.setEnabled(false);
                    pDev.m_agc.release();
                    pDev.m_agc = null;
                }
                pDev.data = null;
            }

            ppAudioCapDevParam = null;
        }

        public static Pair<qyCommProc_open.WAVEFORMATEX, Integer> bGetCapturePara_audioCapdev_android(ctxQmc.CCtxQmc pProcInfo, int iIndex_capAudio,
                                                                                                      int uiBufSize_pMsgBuf, Object pAudioCapDevParam, Object pCache) {
            AudioCapDev_android pDev = (AudioCapDev_android) pAudioCapDevParam;
            AudioRecord record = pDev.m_recorder;
            return new Pair<>(pDev.m_wfx_org, record.getAudioSessionId());
        }

        public static int startAudioCapDev_android(Object pAudioCapDevParam) {
            AudioCapDev_android p = (AudioCapDev_android) pAudioCapDevParam;
            if (p == null) return -1;

            //if (!p.isMuted)

            //
            p.m_recorder.startRecording();

            //
            return 0;
        }

        public static int stopAudioCapDev_android(Object pAudioCapDevParam) {
            AudioCapDev_android p = (AudioCapDev_android) pAudioCapDevParam;
            if (p == null) return -1;

            try {
                if (p.m_recorder != null) {
                    p.m_recorder.stop();
                }
            } catch (IllegalStateException e) {
                Log.e(e.getLocalizedMessage());
                e.printStackTrace();
            }

            return 0;
        }

        //放大音量
        public static void adjustAudio(float gain, byte[] audio, int nBytes) {
            int numSamples = nBytes / 2;
            ByteBuffer buffer = ByteBuffer.wrap(audio);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            ShortBuffer shortBuffer = buffer.asShortBuffer();

            for (int i = 0; i < numSamples; i++) {
                short sample = shortBuffer.get(i);
                int adjustedSample = Math.min(Math.max((int) (sample * gain), Short.MIN_VALUE), Short.MAX_VALUE);
                shortBuffer.put(i, (short) adjustedSample);
            }
        }

        public static qmcFunc.PF_threadProc recThreadFunc_android = (Object ignored) -> {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo == null) return;
            qmcSharedObj.CAP_procInfo_recordSound pRecordSoundProcInfo = pProcInfo.av.localAv.recordSoundProcInfo;
            qnmCommProc_mis.QY_qThreadProcInfo_common pRecThread = pRecordSoundProcInfo.recThread;

            AudioCapDev_android pDev = (AudioCapDev_android) pRecordSoundProcInfo.u_android.pAudioCapDev;

            int bufSize_pAudioDataBuf = (int) pProcInfo.cfg.uiBufSize_myPlayAudioData;
            byte[] pAudioDataBuf = new byte[bufSize_pAudioDataBuf];
            //
            int dataLen_pAudioDataBuf;
            dataLen_pAudioDataBuf = 0;
            qnmCommProc_mis.MIS_MSGU pMsgBuf = new qnmCommProc_mis.MIS_MSGU();
            qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pCompressor = pRecordSoundProcInfo.compressAudio.compressor;

            int uiBytesRecorded_perBlockAlign = (int) MACRO_getuiBytesRecorded_perBlockAlign(pCompressor, pDev.m_wfx_org);
            int iCapTimeInMs = 40;
            int nBytes_toRecord = (int) (uiBytesRecorded_perBlockAlign * pDev.m_wfx_org.nBlockAlign);
            int nAvgBytesPerSec = (int) pDev.m_wfx_org.nAvgBytesPerSec;

            if (nBytes_toRecord == 0) {
                pRecThread.bRunning = false;
                return;
            }
            if (nAvgBytesPerSec == 0) {
                pRecThread.bRunning = false;
                return;
            }
            if (iCapTimeInMs <= 10) {
                pRecThread.bRunning = false;
                return;
            }

            pRecThread.bRunning = true;
            pRecordSoundProcInfo.usErrors_processInput = 0;

            while (!pRecThread.bQuit) {
                int read = pDev.m_recorder.read(pDev.data, 0, pDev.minBufferSize);
                if (read > 0) {

                    //
                    if  (  false  ) {
                        String fn = "rec.pcm";//CONST_fn_dump_pcm_enc;
                        //GuiShare.pf_dumpData1.call(0, 0, 0, 0, false, false, fn);
                        guiShare.GuiShare.pf_dumpData1.call(pDev.data, (int) read, 0, 0, true, false, fn);

                    }

                    //
                    int dataLength = read;
                    if (dataLength + dataLen_pAudioDataBuf < bufSize_pAudioDataBuf) {
                        System.arraycopy(pDev.data, 0, pAudioDataBuf, dataLen_pAudioDataBuf, dataLength);
                        dataLen_pAudioDataBuf += dataLength;
                    }
                } else {
                    pRecordSoundProcInfo.usErrors_processInput++;
                }

                while (dataLen_pAudioDataBuf >= nBytes_toRecord) {
                    float fG = 7.0f;
                    adjustAudio(fG, pAudioDataBuf, nBytes_toRecord);
                    //
                    {
                        processInput_post(pProcInfo, nAvgBytesPerSec, pAudioDataBuf, nBytes_toRecord, pMsgBuf);
                    }
                    //if (dataLen_pAudioDataBuf == 0) break;
                    //
                    System.arraycopy(pAudioDataBuf, nBytes_toRecord, pAudioDataBuf, 0, dataLen_pAudioDataBuf - nBytes_toRecord);
                    dataLen_pAudioDataBuf -= nBytes_toRecord;
                }
            }

            pRecThread.bRunning = false;
        };
    }
}

package com.qycx.base.qdc;
//


import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_dec;
import static com.qycx.base.qyCommProc_open.Consts.MIME_TYPE_mp3;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.qycx.MainActivity;
import com.qycx.base.EventWaitHandle;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.myPlayAudioData;
import com.qycx.base.qdcCommProc_open;
import com.qycx.base.qmcTransform;
import com.qycx.base.qyCommProc_open;
import com.qycx.base.qyMcMainMediaReceiver;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

public class decObj_a_mediaCodec_async extends DecMediaCodecCommon {

    private static boolean VERBOSE = true;//false;           // lots of logging

    //
//    public qdcCommProc_open.QOI_common m_qoi;

    public boolean m_bEnd;

    boolean m_bTest = false;

//    public int m_old_lPktId;        //  用来防止getNextPkt出错，得到的是重复包

    //
//    byte[] m_tmpDataBuf = new byte[bufSize_transferAudioData];//[bufSize_transferAudioData];

    private Context context;

//private MediaFormat audioFormat;

    private File pcmFile;

    private FileOutputStream fos = null;

    private MediaCodec decodeCodec = null;


    private LinkedBlockingQueue mOutDataQueue = new LinkedBlockingQueue();
//private Java.Util.IQueue<byte[]> mOutDataQueue = new LinkedBlockingQueue<>();

    private LinkedBlockingQueue mInputDataQueue = new LinkedBlockingQueue();
    //private Queue<Integer> mInputDataQueue = new LinkedBlockingQueue<>();

    MediaFormat m_dec_format = null;


    private Handler mHandler;


    public Tmp_outRunnable outRunnable = null;
    public Tmp_inputRunnable inputRunnable = null;


    //
    public static class Tmp_outRunnable implements Runnable {
        decObj_a_mediaCodec_async mOwner;

        public Tmp_outRunnable(decObj_a_mediaCodec_async owner) {
            mOwner = owner;
        }

        @Override
        public void run() {

            try {
                //Log.e( "outRunnable,当前线程： " + Thread.CurrentThread().Name);

                byte[] pcmData = (byte[]) mOwner.mOutDataQueue.poll();
                if (pcmData == null) {
                    return;
                }
                Log.e( "Handler回调收到,当前数据大小：" + pcmData.length);

                dec_putPcmData(mOwner, pcmData, pcmData.length, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    ;


    public static class Tmp_inputRunnable implements Runnable {
        decObj_a_mediaCodec_async mOwner;

        public Tmp_inputRunnable(decObj_a_mediaCodec_async owner) {
            mOwner = owner;
        }

        @Override
        public void run() {

            try {
                //Log.e( "inputRunnable,当前线程： " + Thread.CurrentThread().Name);

                int index = (int) mOwner.mInputDataQueue.poll();
                if (index == 0) {
                    return;
                }

                ByteBuffer buffer;
                if ((int) Build.VERSION.SDK_INT >= 21) {
                    buffer = mOwner.decodeCodec.getInputBuffer(index);
                } else {
                    buffer = mOwner.decodeCodec.getInputBuffers()[index];
                }
//                long sampleTime = 0;
                int sampleSize = 0;
                qyCommProc_open.DecReadSampleDataParams decReadSampleDataParams = new qyCommProc_open.DecReadSampleDataParams();
                sampleSize = dec_ReadSampleData(mOwner, buffer, 0, decReadSampleDataParams);
                //
                if (sampleSize < 0) {
                    Log.i( "当前音频已经读取完了");
                    mOwner.decodeCodec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                } else {
                    //Log.i( "读取到了音频数据，当前音频数据的数据长度为：" + sampleSize);

                    //
                    mOwner.decodeCodec.queueInputBuffer(index, 0, sampleSize, decReadSampleDataParams.presentationTimeUs, 0);

                    //
                    //mOwner.m_test_extractor.Advance();
                    dec_advance(mOwner);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    ;

    //
    public int dec_init() {
        int iErr = -1;

        //

        //
        if (m_bTest) {

        } else {
            //int width = this.m_iW;
            //int height = this.m_iH;
            MediaFormat format = MediaFormat.createAudioFormat(MIME_TYPE_mp3, 16000, 1);

            m_dec_format = format;
        }

        iErr = 0;
        return iErr;
    }

    public int dec_exit() {
        if (m_bTest) {

        } else {

        }
        return 0;
    }

    static int dec_ReadSampleData(decObj_a_mediaCodec_async dm, ByteBuffer inputBuf, int offset, qyCommProc_open.DecReadSampleDataParams decReadSampleDataParams) {
        int chunkSize = -1;
        int iErr = -1;
        //
        boolean bDbg = false;
        String str;


        //
        if (dm.m_bTest) {

        } else {
            int uiToInMs = 0;

            qyCommProc_open.ReadNextFrameFucParams readNextFrameFucParams = new qyCommProc_open.ReadNextFrameFucParams();
//            int nBytesRead = (int) inputBuf.capacity();
//            int uiSampleTimeInMs = 0;
//            int pold_lPktId = dm.m_old_lPktId;
            {
                readNextFrameFucParams.nBytesRead = (int) dm.m_tmpDataBuf.length;
                //
                if (0 != dm.m_qoi.cfg.a.dec.pf_smplBitstreamReader_ReadNextFrame_func.call(dm,readNextFrameFucParams)) {
                    str = String.format("decA.dec_ReadSmpleData：sampleBitstreamReader failed");
                    //
                    return iErr;
                }
                if (readNextFrameFucParams.nBytesRead == 0) {
                    int ii = 0;
                }
                //inputBuf.Limit((int)nBytesRead);
                inputBuf.clear();
                inputBuf.put(dm.m_tmpDataBuf, 0, (int) readNextFrameFucParams.nBytesRead);


            }
            //
            chunkSize = (int) readNextFrameFucParams.nBytesRead;
            decReadSampleDataParams.presentationTimeUs = readNextFrameFucParams.uiSampleTimeInMs * 1000L;

        }


        return chunkSize;
    }


    //
    static int dec_advance(decObj_a_mediaCodec_async dm) {
        if (dm.m_bTest) {

        } else {

        }

        //
        return 0;
    }

    //
    static int dec_putPcmData(decObj_a_mediaCodec_async dm, byte[] pcm, int pcmLen, int iSampleTimeInMs) {
        int iErr = -1;
        String str;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        myPlayAudioData.myPLAY_AUDIO_DATA pkt = new myPlayAudioData.myPLAY_AUDIO_DATA();
        //qyFuncs.mymemset((System. long) pkt, 0, sizeof(myPlayAudioData.myPLAY_AUDIO_DATA));

        myPlayAudioData.myPLAY_AUDIO_DATA pPkt_dec = pkt;
        //
        //mymemset((byte[])pPkt_dec->head, 0, sizeof(qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead));   //  2015/02/14
        qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) dm.m_qoi.cfg.a.pParent_transform;

        pPkt_dec.head.idInfo.ui64Id = pTransform.audio.idInfo.ui64Id;
        pPkt_dec.head.uiTranNo_openAvDev = pTransform.audio.ta.uiTranNo_openAvDev;

        //
        int nAvgBytes = 16000 * 2 * 1;
        if (nAvgBytes == 0) return iErr;
        pPkt_dec.head.uiSampleTimeInMs = (int) (1000.0f * dm.m_qoi.var.decInfo.nBytes_deced / (float) nAvgBytes);
        dm.m_qoi.var.decInfo.nBytes_deced += pcmLen;// pcm.length;


        pPkt_dec.head.uiLen = (int) pcmLen;// pcm.length;

        //
        if (0 == qyMcMainMediaReceiver.qyFuncs.chkFromPlayerIndex_a(CONST_uiTransformType_dec, pTransform, pPkt_dec, null)) {
            qyMcMainMediaReceiver.qyFuncs.playAudioData(pProcInfo, (int) pPkt_dec.head.uiSampleTimeInMs, pPkt_dec.head.uiPts, pcm, pPkt_dec.head.uiLen, pTransform.iIndex_player);
        }

        //
        iErr = 0;


        return iErr;


    }


    //
    public decObj_a_mediaCodec_async() {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

        m_tmpDataBuf = new byte[bufSize_transferAudioData];//[bufSize_transferAudioData];
        //
        context = mainActivity;

        //
        Log.e( "outRunnable,当前线程： " + Thread.currentThread().getName());

        outRunnable = new Tmp_outRunnable(this);
        inputRunnable = new Tmp_inputRunnable(this);

    }

    public static class Tmp_Handler extends Handler {
        decObj_a_mediaCodec_async m_owner;

        public Tmp_Handler(decObj_a_mediaCodec_async mm, Looper looper) {
            super(looper);
            m_owner = mm;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                m_owner.destory();
            }
        }
    }


    public static class Tmp_MediaCodec_Callback extends MediaCodec.Callback {
        decObj_a_mediaCodec_async mOwner;

        public Tmp_MediaCodec_Callback(decObj_a_mediaCodec_async owner) {
            mOwner = owner;
        }

        @Override
        public void onInputBufferAvailable(MediaCodec codec, int index) {
            mOwner.mInputDataQueue.offer(index);
            mOwner.mHandler.post(mOwner.inputRunnable);

        }

        //
        byte[] m_pcmData = new byte[1024];

        public void onOutputBufferAvailable(MediaCodec codec, int index, MediaCodec.
                BufferInfo info) {
            //Log.e( "OnOutputBufferAvailable,当前线程： " + Thread.CurrentThread().Name);

            //
            //Log.i( "异步回调，onOutputBufferAvailable,当前index:" + index);
            //Log.i( "获取到解码后的数据了，当前解析后的数据长度为：" + info.size);
            //拿到当前装满火腿肠的筐
            ByteBuffer outputBuffer;
            if ((int) Build.VERSION.SDK_INT >= 21) {
                outputBuffer = codec.getOutputBuffer(index);
            } else {
                outputBuffer = codec.getOutputBuffers()[index];
            }
            //将火腿肠放到新的容器里，便于后期装车运走
            //byte[] pcmData = new byte[info.size];
            if (m_pcmData.length < info.size) {
                m_pcmData = new byte[info.size];
            }
            byte[] pcmData = m_pcmData;

            //
            outputBuffer.get(pcmData, 0, info.size);//写入到字节数组中
            outputBuffer.clear();//清空当前筐
            //将装猪的数据放到队列里面，通过handler发送消息在子线程装入数据

            int iSampleInMs = (int) (info.presentationTimeUs / 1000.0f);

            dec_putPcmData(mOwner, pcmData, info.size, iSampleInMs);

            //把筐放回工厂里面
            codec.releaseOutputBuffer(index, false);

            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                mOwner.mHandler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onError(MediaCodec codec, MediaCodec.CodecException e) {
            Log.i( "异步回调，onError" + e.toString());
        }

        @Override
        public void onOutputFormatChanged(MediaCodec codec, MediaFormat format) {
            Log.i( "异步回调，onOutputFormatChanged" + format.toString());

        }
    }

    private int selectTrack(MediaExtractor extractor) {
        // Select the first video track we find, ignore the rest.
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                if (VERBOSE) {
                    //Log.d(TAG, "Extractor selected track " + i + " (" + mime + "): " + format);
                }
                return i;
            }
        }

        return -1;
    }

    @Override
    public void run() {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        Looper.prepare();
        mHandler = new Tmp_Handler(this, Looper.myLooper());


        Log.e( "Decode,当前线程： " + Thread.currentThread().getName());

        try {

            dec_init();

            //
            String str1 = m_dec_format.getString(MediaFormat.KEY_MIME);

            //初始化MiediaCodec
            decodeCodec = MediaCodec.createDecoderByType(this.m_dec_format.getString(MediaFormat.KEY_MIME));
            //
            // 通过回调方式来进行数据的编码，比刚才手动调用方式更合理，回调运行在主线程，记得切换线程
            //
            decodeCodec.setCallback(new Tmp_MediaCodec_Callback(this));

            //
            if (!pProcInfo.cfg.b_no_KeyLowLatency_a) {
                this.m_dec_format.setInteger(MediaFormat.KEY_LOW_LATENCY, 1);
            }


            //先配置callBack，再配置config；数据格式，surface用来渲染解析出来的数据;加密用的对象；标志 encode ：1 decode：0
            decodeCodec.configure(this.m_dec_format, null, null, 0);

            //启动解码
            decodeCodec.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Looper.loop();

        //
        //
        this.m_bEnd = true;

        //
        tmpFunc.qyFuncs.mySetEvent((EventWaitHandle) this.m_qoi.hEvent_notifyToQuit);

        //
        String str = ("a mediaCodec_async.Run ends.");

    }

    public void destory() {

        Log.i( "销毁资源");

        dec_exit();
        //
        if (decodeCodec != null) {
            decodeCodec.stop();
            decodeCodec.release();
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Looper.myLooper().quitSafely();
    }
}



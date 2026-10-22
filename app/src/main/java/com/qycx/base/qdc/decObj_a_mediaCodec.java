

package com.qycx.base.qdc;


import static com.qycx.base.ctxQmcTmpl.Consts.CONST_fn_dump_mp3_dec;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_decObj_a_mediaCodec_tmpDataBuf;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_dec;
import static com.qycx.base.qyCommProc_open.Consts.MIME_TYPE_mp3;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Environment;

import com.qycx.base.Consts;
import com.qycx.base.EventWaitHandle;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.guiShare;
import com.qycx.base.myPlayAudioData;
import com.qycx.base.qmcTransform;
import com.qycx.base.qyCommProc_open;
import com.qycx.base.qyMcMainMediaReceiver;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.showInfo_open;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

//
public class decObj_a_mediaCodec extends DecMediaCodecCommon {

    private static boolean VERBOSE = true;//false;           // lots of logging

    // where to find files (note: requires WRITE_EXTERNAL_STORAGE permission)
    //private static  File FILES_DIR = Environment.GetExternalStorageDirectory();
    private static final File FILES_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    private static int MAX_FRAMES = 100;//10;       // stop extracting after this many
    private static FileOutputStream dump;
    private static boolean dump_mp3 = false;
    //
//    public qdcCommProc_open.QOI_common m_qoi;


    //
    public boolean m_bEnd;
    public qmcTransform.TRANSFORM_AUDIO_procInfo_decInfo m_decInfo = new qmcTransform.TRANSFORM_AUDIO_procInfo_decInfo();


    //
    //
//    public int m_old_lPktId;        //  用来防止getNextPkt出错，得到的是重复包

    //
    public boolean m_bTest;

    //
    public decObj_a_mediaCodec_Wrapper m_wrapper;
    public Thread m_th;

    //
//    byte[] m_tmpDataBuf = new byte[bufSize_transferAudioData];//[bufSize_transferAudioData];

    MediaFormat m_dec_format = null;

    public decObj_a_mediaCodec() {
        //m_tmpDataBuf = new byte[bufSize_transferAudioData];//[bufSize_transferAudioData];

        m_tmpDataBuf = new byte[bufSize_decObj_a_mediaCodec_tmpDataBuf];//[bufSize_transferAudioData];


    }


    /** test entry point */
    public void testExtractMpegFrames(Activity mainActivity) {
        //this.mainActivity = mainActivity;
        decObj_a_mediaCodec_Wrapper.runTest(this);
    }

    /**
     * Wraps extractMpegFrames().  This is necessary because SurfaceTexture will try to use
     * the looper in the current thread if one exists, and the CTS tests create one on the
     * test thread.
     *
     * The wrapper propagates exceptions thrown by the worker thread back to the caller.
     */
    public static class decObj_a_mediaCodec_Wrapper implements Runnable {
        private Throwable mThrowable;
        private decObj_a_mediaCodec mTest;

        public decObj_a_mediaCodec_Wrapper(decObj_a_mediaCodec test) {
            mTest = test;
        }

        @Override
        public void run() {
            Thread.currentThread().getName();
            try {
                mTest.decFrames();

            } catch (Throwable th) {

                //
                //Log.d( "decObj.run, catch throwalbe");
                String str;
                str=( "decObj_a.run, catch throwalbe:" + th.getMessage());
                showInfo_open.qyFuncs.showInfo_open(str);
                Log.e("qy",str);

                //
                mThrowable = th;
            }

            //
            mTest.m_bEnd = true;

            //
            tmpFunc.qyFuncs.mySetEvent((EventWaitHandle) mTest.m_qoi.hEvent_notifyToQuit);

            //
            String str;
            str = String.format("a mediaCodec.wrapper.Run ends.");

            return;
        }

        /** Entry point. */
        public static void runTest(decObj_a_mediaCodec obj) {
            //
            obj.m_wrapper = new decObj_a_mediaCodec_Wrapper(obj);
            obj.m_th = new Thread(obj.m_wrapper, "codec test11");
            obj.m_th.start();

        }
    }

    /**
     * Tests extraction from an MP4 to a series of PNG files.
     * <p>
     * We scale the video to 640x480 for the PNG just to demonstrate that we can scale the
     * video with the GPU.  If the input video has a different aspect ratio, we could preserve
     * it by adjusting the GL viewport to get letterboxing or pillarboxing, but generally if
     * you're extracting frames you don't want black bars.
     */
    private void decFrames() {
        MediaCodec decoder = null;
        MediaExtractor extractor = null;
        int saveWidth = 640;
        //int saveHeight = 368;// 480;
        int saveHeight = 360;// 480;

        String str;


        try {
            dec_init();

            //
            MediaFormat format = this.m_dec_format;

            // Could use width/height from the MediaFormat to get full-size frames.
            //outputSurface = new CodecOutputSurface(saveWidth, saveHeight);

            //  create a MediaCodec decoder, and configure it with the MediaFormat from the
            // extractor.  It's very important to use the format from the extractor because
            // it contains a copy of the CSD-0/CSD-1 codec-specific data chunks.
            String mime = format.getString(MediaFormat.KEY_MIME);
            decoder = MediaCodec.createDecoderByType(mime);
            decoder.configure(format, null, null, 0);
            decoder.start();

            doDec(this, decoder);
        } catch (IOException e) {
            str  =  "decObj_a_mediaCodec.decFrames exception:"  +  e.getMessage();
            showInfo_open.qyFuncs.showInfo_open(str);
            return;
        }
        catch (Exception e) {
            str  =  "decObj_a_mediaCodec.decFrames exception:"  +  e.getMessage();
            showInfo_open.qyFuncs.showInfo_open(str);
            return;
        }
        finally {
            // release everything we grabbed
            if (decoder != null) {
                decoder.stop();
                decoder.release();
                decoder = null;
            }
            dec_exit();
            //
            str  =  "decObj_a_mediaCodec.decFrames finally. ";
            showInfo_open.qyFuncs.showInfo_open(str);
        }
    }


    //
    public int dec_init() {
        int iErr = -1;

        if (m_bTest) {


        } else {
            //int width = this.m_iW;
            //int height = this.m_iH;
            MediaFormat format = MediaFormat.createAudioFormat(MIME_TYPE_mp3, 16000, 1);

            m_dec_format = format;
        }
        if(dump_mp3)
        {
            try {
                dump = new FileOutputStream(FILES_DIR + "/dump_dec_"+System.currentTimeMillis()+".mp3");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        iErr = 0;
        return iErr;
    }

    public int dec_exit() {
        if (dump_mp3 && dump !=null) {
            try {
                dump.close();
                dump = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }


    //
    static int dec_ReadSampleData(decObj_a_mediaCodec dm, ByteBuffer inputBuf, int offset, qyCommProc_open.DecReadSampleDataParams decReadSampleDataParams) {
        int chunkSize = -1;

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
                if (0 != dm.m_qoi.cfg.a.dec.pf_smplBitstreamReader_ReadNextFrame_func.call(dm, readNextFrameFucParams)) {
                    str = String.format("decA.dec_ReadSmpleData：sampleBitstreamReader failed");
                    showInfo_open.qyFuncs.showInfo_open(str);
                    //
                    return -1;
                }
                if (readNextFrameFucParams.nBytesRead == 0) {
                    int ii = 0;
                }
                //inputBuf.Limit((int)nBytesRead);
                inputBuf.clear();
//                guiShare.GuiShare.pf_dumpData1.call(dm.m_tmpDataBuf, readNextFrameFucParams.nBytesRead, 0, 0, true, false, "shurude.mp3");
                inputBuf.put(dm.m_tmpDataBuf, 0, (int) readNextFrameFucParams.nBytesRead);

                //
                if(false)
                {
                    try {
                        //dump.write(dm.m_tmpDataBuf,0,readNextFrameFucParams.nBytesRead);
                        byte[]audio  =  dm.m_tmpDataBuf;
                        int len = readNextFrameFucParams.nBytesRead;
                        guiShare.GuiShare.pf_dumpData1.call( audio, len, 0, 0, true, false, "dec_ReadSmapleData.mp3");
                    } catch (Exception e) {
                        //throw new RuntimeException(e);
                        e.printStackTrace();
                    }
                }
            }

            chunkSize = (int) readNextFrameFucParams.nBytesRead;
            decReadSampleDataParams.presentationTimeUs = readNextFrameFucParams.uiSampleTimeInMs * 1000L;
        }

        return chunkSize;
    }


    //
    static int dec_advance(decObj_a_mediaCodec dm) {
        if (dm.m_bTest) {

        } else {

        }

        //
        return 0;
    }

    //
    static int dec_putPcmData(decObj_a_mediaCodec dm, byte[] pcm, int iSampleTimeInMs) {
        //
        //Log.d(Consts.AUDIO_DOWNLOAD,"4.85 dec_putPcmData enter");
        //
        int iErr = -1;
        String str;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        myPlayAudioData.myPLAY_AUDIO_DATA pkt = new myPlayAudioData.myPLAY_AUDIO_DATA();
        //qyFuncs.mymemset((byte[])pkt, 0, sizeof(myPlayAudioData.myPLAY_AUDIO_DATA));

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
        dm.m_qoi.var.decInfo.nBytes_deced += pcm.length;

        pPkt_dec.head.uiLen = (int) pcm.length;

        //
        if  (  pcm.length  ==  0  )  {
            Log.d("qy","hhh.353");
        }

        //
//        guiShare.GuiShare.pf_dumpData1.call(pcm, (int) pcm.length, 0, 0, true, false, "dump_deced_pcm.pcm");
        if (0 == qyMcMainMediaReceiver.qyFuncs.chkFromPlayerIndex_a(CONST_uiTransformType_dec, pTransform, pPkt_dec, null)) {
            qyMcMainMediaReceiver.qyFuncs.playAudioData(pProcInfo, (int) pPkt_dec.head.uiSampleTimeInMs, pPkt_dec.head.uiPts, pcm, pPkt_dec.head.uiLen, pTransform.iIndex_player);
        }
        else{
            Log.d("qy","decObj_a_mediaCodec.dec_putPcmData: 4.9 chkFromPlayerIndex_a error ");
        }

        //
        iErr = 0;


        
        return iErr;


    }


    /**
     * Selects the video track, if any.
     *
     * @return the track index, or -1 if no video track is found.
     */
    private int selectTrack(MediaExtractor extractor) {
        // Select the first video track we find, ignore the rest.
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                if (VERBOSE) {
                    //Log.d( "Extractor selected track " + i + " (" + mime + "): " + format);
                }
                return i;
            }
        }

        return -1;
    }

    /**
     * Work loop.
     */
    static void doDec(decObj_a_mediaCodec dm, MediaCodec decoder) {
        String str;

        int TIMEOUT_USEC = 10000;
        ByteBuffer[]
                decoderInputBuffers = decoder.getInputBuffers();
        ByteBuffer[] decoderOutputBuffers = decoder.getOutputBuffers();
        //
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        int inputChunk = 0;
        int decodeCount = 0;
        long frameSaveTime = 0;

        boolean outputDone = false;
        boolean inputDone = false;
        while (!outputDone) {
//            if (VERBOSE) Log.d( "loop");

            //  2023/10/03
            if (inputDone) {
                showInfo_open.qyFuncs.showInfo_open("decObj_a_mediaCodec: inputDone, break");
                com.qycx.base.util.Log.e("inputDone, break");
                break;
            }

            // Feed more data to the decoder.
            if (!inputDone) {
                int inputBufIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputBufIndex >= 0) {
                    ByteBuffer inputBuf = decoderInputBuffers[inputBufIndex];
                    // read the sample data into the ByteBuffer.  This neither respects nor
                    // updates inputBuf's position, limit, etc.
                    //int chunkSize = extractor.readSampleData(inputBuf, 0);
                    //
                    int chunkSize = 0;
//                    long presentationTimeUs = 0;
                    qyCommProc_open.DecReadSampleDataParams decReadSampleDataParams = new qyCommProc_open.DecReadSampleDataParams();
                    chunkSize = dec_ReadSampleData(dm, inputBuf, 0, decReadSampleDataParams);
                    //
                    //Log.d(Consts.AUDIO_DOWNLOAD,"4.7 audio decoding...");
                    if (chunkSize < 0) {
                        // End of stream -- send empty frame with EOS flag set.
                        decoder.queueInputBuffer(inputBufIndex, 0, 0, 0L,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;

                        //if (VERBOSE) Log.d( "sent input EOS");
                        showInfo_open.qyFuncs.showInfo_open("decObj_a_mediaCodec: input EOS");

                    } else {
                        decoder.queueInputBuffer(inputBufIndex, 0, chunkSize,
                                decReadSampleDataParams.presentationTimeUs, 0 /*flags*/);
                        if (VERBOSE) {
                            //Log.d( "submitted frame " + inputChunk + " to dec, size=" +                  chunkSize);
                        }
                        inputChunk++;
                        //
                        //extractor.Advance();
                        dec_advance(dm);
                    }
                } else {
                    if (VERBOSE) Log.d( "input buffer not available");
                }
            }

            if (!outputDone) {
                int decoderStatus = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
                if (decoderStatus == (int) MediaCodec.INFO_TRY_AGAIN_LATER) {

                    // no output available yet
                    if (VERBOSE) Log.d( "no output from decoder available");
                } else if (decoderStatus == (int) MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    
                    // not important for us, since we're using Surface
                    if (VERBOSE) Log.d( "decoder output buffers changed");
                } else if (decoderStatus == (int) MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    
                    MediaFormat newFormat = decoder.getOutputFormat();
                    if (VERBOSE)
                        Log.d( "decoder output format changed: " + newFormat);
                    //
                    int sampleRate = newFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    int nChannels = newFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                    //
                    //
                    int ii = 0;
                } else if (decoderStatus < 0) {
                    Log.e( "unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
                    return;
                } else { // decoderStatus >= 0
                    //if (VERBOSE) Log.d( "surface decoder given buffer " + decoderStatus +             " (size=" + info.size + ")");
                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        //if (VERBOSE) Log.d( "output EOS");
                        outputDone = true;
                    }

                    boolean doRender = false;// (info.size != 0);

                    if (info.size != 0) {
                        ByteBuffer outputBuffer;
                        outputBuffer = decoderOutputBuffers[decoderStatus];
                        byte[] chunkPcm;
                        chunkPcm = new byte[info.size];
                        outputBuffer.get(chunkPcm); //todo possible move_copy
                        outputBuffer.clear();
                        //
                        int iSampleInMs = (int) (info.presentationTimeUs / 1000.0f);
                        //
                        //Log.d(Consts.AUDIO_DOWNLOAD,"4.8 audio play prepare");
                        //
                        dec_putPcmData(dm, chunkPcm, iSampleInMs);
                    }
                    // As soon as we call releaseOutputBuffer, the buffer will be forwarded
                    // to SurfaceTexture to convert to a texture.  The API doesn't guarantee
                    // that the texture will be available before the call returns, so we
                    // need to wait for the onFrameAvailable callback to fire.
                    decoder.releaseOutputBuffer(decoderStatus, doRender);
                    if (doRender) {

                    }

                }
            }

            //
            continue;
        }

        int numSaved = (MAX_FRAMES < decodeCount) ? MAX_FRAMES : decodeCount;
        //Log.d( "Saving " + numSaved + " frames took " +    (frameSaveTime / numSaved / 1000) + " us per frame");

        
        return;
    }


}



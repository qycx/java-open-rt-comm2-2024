
package com.qycx.base.qdc;


import static com.qycx.base.Consts.IO_TAG;
import static com.qycx.base.ctxQmcTmpl.Consts.CONST_fn_dump_h264_enc;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_decObj_mediaCodec_tmpDataBuf;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferVideoData;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_HEVC;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_dec;
import static com.qycx.base.qyCommProc_open.Consts.MIME_TYPE_avc;
import static com.qycx.base.qyCommProc_open.Consts.MIME_TYPE_hevc;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.opengl.*;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import com.qycx.MainActivity;
import com.qycx.MyApplication;
import com.qycx.base.EventResetMode;
import com.qycx.base.EventWaitHandle;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.guiShare;
import com.qycx.base.old.chkFromPlayerIndex_v;
import com.qycx.base.qdcCommProc_open;
import com.qycx.base.qmcTransform;
import com.qycx.base.qyCommProc_open;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.showInfo_open;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//
public class decObj_mediaCodec extends DecMediaCodecCommon {

    private static boolean VERBOSE = false;           // lots of logging

    // where to find files (note: requires WRITE_EXTERNAL_STORAGE permission)
    //private static  File FILES_DIR = Environment.GetExternalStorageDirectory();
    private static final File FILES_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private static FileOutputStream dump;
    private static final boolean dump_video = false;

    private static int MAX_FRAMES = 100;//10;       // stop extracting after this many

    //
    public EGLContext m_sharedEglContext;
//    public qdcCommProc_open.QOI_common m_qoi;

    //

    //
    public int m_iW;
    public int m_iH;
    //
    public int m_iFourcc_i;

    //
    public boolean m_bEnd;


    //
    //
//    public int m_old_lPktId;        //  用来防止getNextPkt出错，得到的是重复包


    //
    public String mTag;
    public boolean m_bTest;

    //
    public decObj_mediaCodec_Wrapper m_wrapper;
    public Thread m_th;

    //
//    public byte[] m_tmpDataBuf = new byte[bufSize_transferVideoData];

    public decObj_mediaCodec() {
        //m_tmpDataBuf = new byte[bufSize_transferVideoData];

        m_tmpDataBuf = new byte[bufSize_decObj_mediaCodec_tmpDataBuf];

    }

    /**
     * test entry point
     */
    public void testExtractMpegFrames() {
        //
        decObj_mediaCodec_Wrapper.runTest(this);
    }

    /**
     * Wraps extractMpegFrames().  This is necessary because SurfaceTexture will try to use
     * the looper in the current thread if one exists, and the CTS tests create one on the
     * test thread.
     * <p>
     * The wrapper propagates exceptions thrown by the worker thread back to the caller.
     */
    public static class decObj_mediaCodec_Wrapper implements Runnable {
        private Throwable mThrowable;
        private decObj_mediaCodec mTest;

        public decObj_mediaCodec_Wrapper(decObj_mediaCodec test) {
            mTest = test;
        }

        @Override
        public void run() {
            try {
                mTest.decFrames();

            } catch (IOException e) {
                com.qycx.base.util.Log.e(e.toString());
            }

            //
            mTest.m_bEnd = true;

            tmpFunc.qyFuncs.mySetEvent((EventWaitHandle) mTest.m_qoi.hEvent_notifyToQuit);

        }

        /**
         * Entry point.
         */
        public static void runTest(decObj_mediaCodec obj) {

            obj.m_wrapper = new decObj_mediaCodec_Wrapper(obj);
            obj.m_th = new Thread(obj.m_wrapper, "codec test2");
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
    private void extractMpegFrames() {
        MediaCodec decoder = null;
        CodecOutputSurface outputSurface = null;
        MediaExtractor extractor = null;
        int saveWidth = 640;
        //int saveHeight = 368;// 480;
        int saveHeight = 360;// 480;

        try {
            Context context = MyApplication.getContext();

            AssetManager assets = context.getAssets();

            AssetFileDescriptor afd = null;
            afd = assets.openFd("vid.mp4");

            extractor = new MediaExtractor();
            extractor.setDataSource(afd);
            int trackIndex = selectTrack(extractor);
            if (trackIndex < 0) {
                //throw new RuntimeException("No video track found in " + inputFile);
            }
            extractor.selectTrack(trackIndex);

            MediaFormat format = extractor.getTrackFormat(trackIndex);
            if (VERBOSE) {
                Log.d( "Video size is " + format.getInteger(MediaFormat.KEY_WIDTH) + "x" + format.getInteger(MediaFormat.KEY_HEIGHT));
            }

            //
            //decObj_mediaCodec.m_w = saveWidth;
            //decObj_mediaCodec.m_h = saveHeight;

            // Could use width/height from the MediaFormat to get full-size frames.
            outputSurface = new CodecOutputSurface(this, this.m_sharedEglContext, saveWidth, saveHeight, this.mBackgroundHandler);

            //
            outputSurface.mTextureRender.tmpFrameBuffer.createEnv(saveWidth, saveHeight, "dec.render");

            //  create a MediaCodec decoder, and configure it with the MediaFormat from the
            // extractor.  It's very important to use the format from the extractor because
            // it contains a copy of the CSD-0/CSD-1 codec-specific data chunks.
            String mime = format.getString(MediaFormat.KEY_MIME);
            decoder = MediaCodec.createDecoderByType(mime);
            decoder.configure(format, outputSurface.getSurface(), null, 0);
            decoder.start();

            doExtract(extractor, trackIndex, decoder, outputSurface);
        } catch (IOException e) {
            Log.e(e.getMessage());
            return;
        } finally {
            // release everything we grabbed
            //
            outputSurface.mTextureRender.tmpFrameBuffer.deleteEnv();

            //
            if (outputSurface != null) {
                outputSurface.release();
                outputSurface = null;
            }
            if (decoder != null) {
                decoder.stop();
                decoder.release();
                decoder = null;
            }
            if (extractor != null) {
                extractor.release();
                extractor = null;
            }
        }
    }

    MediaFormat m_dec_format = null;

    //
    // for onFrameAvail
    public HandlerThread mBackgroundThread;
    public Handler mBackgroundHandler;

    //
    public void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("OnFrameAvailBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    public void stopBackgroundThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread.quitSafely();
        }
        if (mBackgroundThread != null) {
            try {
                mBackgroundThread.join();

            } catch (InterruptedException ie) {

            }


        }
        mBackgroundThread = null;
        mBackgroundHandler = null;
    }


    //
    ctxQmc.TMP_tex_info get_tti(ctxQmc.CCtxQmc pProcInfo) {
        int iErr = -1;
        decObj_mediaCodec dm = this;

        if (dm.m_qoi.cfg.v.uiTransformType_pParent == CONST_uiTransformType_dec) {
            qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) dm.m_qoi.cfg.v.pParent_transform;
            if (pTransform.index_activeMems_from < 0 || pTransform.index_activeMems_from >= pProcInfo.av.texes.taskAvTexes.tti_peers.length)
                return null;
            return pProcInfo.av.texes.taskAvTexes.tti_peers[pTransform.index_activeMems_from];

        }

        iErr = 0;


        return null;
    }

    //
    int removeFromTTi(int texId) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) {
            com.qycx.base.util.Log.e("pQyMc == null");
            return -1;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        // TMP_texes texes =  pProcInfo.av.texes;
        ctxQmc.TMP_tex_info tti = null;//  texes.tti_peer;
        tti = get_tti(pProcInfo);
        if (tti == null) return -1;

        if (texId == 0) return -1;

        if (tti.texId == texId) {
            tti.texId = 0;
        }


        return 0;
    }


    //
    private void decFrames() throws IOException {
        MediaCodec decoder = null;
        CodecOutputSurface outputSurface = null;
        //MediaExtractor extractor = null;
        int saveWidth = 640;
        int saveHeight = 360;

        try {

            dec_init();

            //
            saveWidth = this.m_iW;
            saveHeight = this.m_iH;

            //
            MediaFormat format = this.m_dec_format;

            startBackgroundThread();

            // Could use width/height from the MediaFormat to get full-size frames.
            outputSurface = new CodecOutputSurface(this, this.m_sharedEglContext, saveWidth, saveHeight, this.mBackgroundHandler);

            //
            outputSurface.mTextureRender.tmpFrameBuffer.createEnv(saveWidth, saveHeight, "dec.render");

            //  create a MediaCodec decoder, and configure it with the MediaFormat from the
            // extractor.  It's very important to use the format from the extractor because
            // it contains a copy of the CSD-0/CSD-1 codec-specific data chunks.
            String mime = format.getString(MediaFormat.KEY_MIME);
            decoder = MediaCodec.createDecoderByType(mime);
            //
                /* if (!qyFuncs_Droid.isHarmonyOSa())
                 {
                     format.setInteger(MediaFormat.KeyLowLatency, 1);
                 }*/
            //
            decoder.configure(format, outputSurface.getSurface(), null, 0);
            decoder.start();

            //doExtract(extractor, trackIndex, decoder, outputSurface);
            doDec(this, decoder, outputSurface);

            //
            int ii = 0;
        } catch (Exception e ) {
            Log.e("qy","decObj_mediaCodec.decFrames exception: "  + e.getMessage());
        }
            finally
         {

            if (outputSurface != null) {
                //
                removeFromTTi(outputSurface.mTextureRender.tmpFrameBuffer.mTexture[0]);
                //
                outputSurface.mTextureRender.tmpFrameBuffer.deleteEnv();
                Log.e("decFrames delete textureID: " + outputSurface.mTextureRender.tmpFrameBuffer.mTexture[0]);
            }

            //
            if (outputSurface != null) {
                outputSurface.release();
                outputSurface = null;
            }
            if (decoder != null) {
                try {
                    decoder.stop();
                } catch (Exception e) {
                    decoder = null;
                }
                if (decoder != null) {
                    decoder.stop();
                    decoder.release();
                }
            }
            //
            stopBackgroundThread();
            showInfo_open.qyFuncs.showInfo_open("finally dec_exit");
            dec_exit();
        }
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
            if (mime.startsWith("video/")) {
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
    static void doExtract(MediaExtractor extractor, int trackIndex, MediaCodec decoder, CodecOutputSurface outputSurface) {
        int TIMEOUT_USEC = 10000;
        ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        int inputChunk = 0;
        int decodeCount = 0;
        long frameSaveTime = 0;

        boolean outputDone = false;
        boolean inputDone = false;
        while (!outputDone) {
//            if (VERBOSE) Log.d( "loop");

            // Feed more data to the decoder.
            if (!inputDone) {
                int inputBufIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputBufIndex >= 0) {
                    ByteBuffer inputBuf = decoderInputBuffers[inputBufIndex];
                    // read the sample data into the ByteBuffer.  This neither respects nor
                    // updates inputBuf's position, limit, etc.
                    int chunkSize = extractor.readSampleData(inputBuf, 0);
                    if (chunkSize < 0) {
                        // End of stream -- send empty frame with EOS flag set.
                        decoder.queueInputBuffer(inputBufIndex, 0, 0, 0L,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                        //if (VERBOSE) Log.d( "sent input EOS");
                    } else {
                        if (extractor.getSampleTrackIndex() != trackIndex) {
                            //Log.w( "WEIRD: got sample from track " +                            extractor.getSampleTrackIndex() + ", expected " + trackIndex);
                        }
                        long presentationTimeUs = extractor.getSampleTime();
                        decoder.queueInputBuffer(inputBufIndex, 0, chunkSize,
                                presentationTimeUs, 0 /*flags*/);
                        if (VERBOSE) {
                            //Log.d( "submitted frame " + inputChunk + " to dec, size=" +                  chunkSize);
                        }
                        inputChunk++;
                        extractor.advance();
                    }
                } else {
                    if (VERBOSE) Log.d( "input buffer not available");
                }
            }

            if (!outputDone) {
                int decoderStatus = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
                if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
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
                    int width = newFormat.getInteger(MediaFormat.KEY_WIDTH);
                    int height = newFormat.getInteger(MediaFormat.KEY_HEIGHT);
                    //
                    int cropLeft = newFormat.getInteger("crop-left");
                    int cropRight = newFormat.getInteger("crop-right");
                    int cropTop = newFormat.getInteger("crop-top");
                    int cropBottom = newFormat.getInteger("crop-bottom");
                    int cropWidth = cropRight - cropLeft + 1;
                    int cropHeight = cropBottom - cropTop + 1;
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

                    boolean doRender = (info.size != 0);

                    // As soon as we call releaseOutputBuffer, the buffer will be forwarded
                    // to SurfaceTexture to convert to a texture.  The API doesn't guarantee
                    // that the texture will be available before the call returns, so we
                    // need to wait for the onFrameAvailable callback to fire.
                    decoder.releaseOutputBuffer(decoderStatus, doRender);
                    if (doRender) {
                        if (VERBOSE) {
                            Log.d( "awaiting decode of frame " + decodeCount);
                        }
                        //
                        outputSurface.awaitNewImage();

                        outputSurface.drawImage(true);

                        //
                        if (decodeCount < MAX_FRAMES) {

                        }
                        decodeCount++;
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


    //
    //
    public int dec_init() {
        int iErr = -1;

        if (m_bTest) {

        } else {
            int width = this.m_iW;
            int height = this.m_iH;

            //
            String mime = qyCommProc_open.Consts.MIME_TYPE_avc;
            //
            if (m_iFourcc_i == CONST_fourcc_HEVC) {
                mime = MIME_TYPE_hevc;
            }
            MediaFormat format = MediaFormat.createVideoFormat(mime, width, height);

            ByteBuffer bb_extraData;
            int sizeof_spspps = 1024;
            bb_extraData = ByteBuffer.allocateDirect(sizeof_spspps);
            if (bb_extraData == null) return iErr;
            //
            int len = (int) bb_extraData.capacity();
            //
            showInfo_open.qyFuncs.showInfo_open("pf_transformGetSpsPps start");
            if (0 != this.m_qoi.cfg.v.pf_transformGetSpsPps.call(this.m_qoi.cfg.v.uiTransformType_pParent, this.m_qoi.cfg.v.pParent_transform, this.m_qoi.cfg.v.iDecParam_index_pMems_from, bb_extraData, len)) {
                showInfo_open.qyFuncs.showInfo_open("pf_transformGetSpsPps failed");
                return iErr;
            }
            showInfo_open.qyFuncs.showInfo_open("pf_transformGetSpsPps end");

            format.setByteBuffer("csd-0", bb_extraData);
            if(dump_video)
            {
                try {
                    if(dump != null)
                    {
                        dump.close();
                        dump = null;
                    }
                    dump = new FileOutputStream(FILES_DIR+"/dump_dec_"+System.currentTimeMillis()+".hevc");
                    dump.write(bb_extraData.array());
                    Log.d(IO_TAG,"write sps pps vps");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (VERBOSE) {
                Log.d( "Video size is " + format.getInteger(MediaFormat.KEY_WIDTH) + "x" + format.getInteger(MediaFormat.KEY_HEIGHT));
            }
            showInfo_open.qyFuncs.showInfo_open("dec_init");

            //
            m_dec_format = format;

            //
        }

        iErr = 0;


        return iErr;
    }

    public int dec_exit() {
        if(dump!=null && dump_video)
        {
            try {
                dump.close();
                dump = null;
                Log.d(IO_TAG,"write finish");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //
        return 0;
    }

    //
    static int dec_ReadSampleData(decObj_mediaCodec dm, ByteBuffer inputBuf, int offset, qyCommProc_open.DecReadSampleDataParams decReadSampleDataParams) {
        int chunkSize = -1;
        int iErr = -1;
        //
        boolean bDbg = false;
        String str;

        //
        bDbg  =  true;

        //
        long tick_start  =  0;

        //
        if  (  bDbg)  {
            tick_start  =  tmpFunc.qyFuncs.myGetTickCount(null);
        }


        //
        if (dm.m_bTest) {

        } else {
            int uiToInMs = 0;

            qyCommProc_open.ReadNextFrameFucParams readNextFrameFucParams = new qyCommProc_open.ReadNextFrameFucParams();
//            int nBytesRead = (int) inputBuf.capacity();
//            int uiSampleTimeInMs = 0;
//             int pold_lPktId =dm.m_old_lPktId;
            {
                readNextFrameFucParams.nBytesRead = (int) dm.m_tmpDataBuf.length;
                //
                //showInfo_open.qyFuncs.showInfo_open("pf_smplBitstreamReader_ReadNextFrame_func start");
                //
                if (0 != dm.m_qoi.cfg.v.dec.pf_smplBitstreamReader_ReadNextFrame_func.call(dm, readNextFrameFucParams)) {
                    str = "decV.dec_ReadSmpleData：sampleBitstreamReader failed";
                    showInfo_open.qyFuncs.showInfo_open(str);
                    Log.e(str);
                    return iErr;
                }
                if (readNextFrameFucParams.nBytesRead == 0) {
                    int ii = 0;
                }
                //inputBuf.Limit((int)nBytesRead);
                inputBuf.clear();
                inputBuf.put(dm.m_tmpDataBuf, 0, (int) readNextFrameFucParams.nBytesRead);
                if(dump!=null && dump_video)
                {
                    try {
                        dump.write(dm.m_tmpDataBuf,0,readNextFrameFucParams.nBytesRead);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                //
                //showInfo_open.qyFuncs.showInfo_open("pf_smplBitstreamReader_ReadNextFrame_func end");


            }

            //
            chunkSize = (int) readNextFrameFucParams.nBytesRead;
            decReadSampleDataParams.presentationTimeUs = readNextFrameFucParams.uiSampleTimeInMs * 1000L;


        }

        //
        if  (  bDbg  )  {
            ctxQyMc.CCtxQyMc pQyMc  = qyMcMain_global.g.g_pQyMc;
            if (  pQyMc!=null) {
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
                if (pProcInfo.cfg.debugStatusInfo.b_print_test) {
                    long tick_end = tmpFunc.qyFuncs.myGetTickCount(null);
                    str = "dec_ReadSampleData: total " + (tick_end - tick_start);
                    showInfo_open.qyFuncs.showInfo_open(str);
                }
            }

        }


        ///
        return chunkSize;
    }


    //
    static int dec_advance(decObj_mediaCodec dm) {
        if (dm.m_bTest) {

        } else {

        }

        //
        return 0;
    }


    //
    static void doDec(decObj_mediaCodec dm, MediaCodec decoder,
                      CodecOutputSurface outputSurface) {
        int TIMEOUT_USEC = 10000;
        ByteBuffer[]
                decoderInputBuffers = decoder.getInputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        int inputChunk = 0;
        int decodeCount = 0;
        long frameSaveTime = 0;

        //
        Log.d( "dec.doDec enters");
        showInfo_open.qyFuncs.showInfo_open("dec.doDec enters");

        //
        int nTimes_tryAgainLater = 0;

        //
        boolean outputDone = false;
        boolean inputDone = false;
        while (!outputDone) {
//            if (VERBOSE) Log.d( "loop");
            //showInfo_open.qyFuncs.showInfo_open("loop start");


            //  2023/10/03
            if (inputDone) {
                showInfo_open.qyFuncs.showInfo_open("decObj_mediaCodec: inputDone, break");
                Log.i("inputDone, break");
                break;
            }

            //
            if  (  nTimes_tryAgainLater  >  30)  {
                showInfo_open.qyFuncs.showInfo_open("decObj_mediaCodec: nTimes_tryAgainLater too big, break");
                break;
            }


            // Feed more data to the decoder.
            if (!inputDone) {
                int inputBufIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);

                //showInfo_open.qyFuncs.showInfo_open("inputBufIndex -");
                //
                if (inputBufIndex >= 0) {
                    ByteBuffer inputBuf = decoderInputBuffers[inputBufIndex];
                    //
                    //showInfo_open.qyFuncs.showInfo_open("decoderInputBuffers[inputBufIndex]; -");
                    // read the sample data into the ByteBuffer.  This neither respects nor
                    // updates inputBuf's position, limit, etc.
                    //int chunkSize = extractor.readSampleData(inputBuf, 0);
                    int chunkSize = 0;
//                    long presentationTimeUs = 0;
                    qyCommProc_open.DecReadSampleDataParams decReadSampleDataParams = new qyCommProc_open.DecReadSampleDataParams();
                    decReadSampleDataParams.presentationTimeUs = 0;
                    //
                    //showInfo_open.qyFuncs.showInfo_open("dec_ReadSampleData start");
                    //
                    chunkSize = dec_ReadSampleData(dm, inputBuf, 0, decReadSampleDataParams);
                    //
                    //showInfo_open.qyFuncs.showInfo_open("dec_ReadSampleData end");

                    //
                    if (chunkSize < 0) {
                        // End of stream -- send empty frame with EOS flag set.
                        decoder.queueInputBuffer(inputBufIndex, 0, 0, 0L,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                        //if (VERBOSE) Log.d( "sent input EOS");
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
                //
                if (decoderStatus == (int) MediaCodec.INFO_TRY_AGAIN_LATER) nTimes_tryAgainLater++;
                else nTimes_tryAgainLater = 0;

                //
                if (decoderStatus == (int) MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    showInfo_open.qyFuncs.showInfo_open("decoderStatus: MediaCodec.INFO_TRY_AGAIN_LATE");
                    Log.e("看看", "decoderStatus: MediaCodec.INFO_TRY_AGAIN_LATE");
                    if (VERBOSE) Log.d( "no output from decoder available");
                } else if (decoderStatus == (int) MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // not important for us, since we're using Surface
                    showInfo_open.qyFuncs.showInfo_open("decoderStatus: MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED");
                    if (VERBOSE) Log.d( "decoder output buffers changed");
                } else if (decoderStatus == (int) MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = decoder.getOutputFormat();
                    if (VERBOSE)
                        Log.d( "decoder output format changed: " + newFormat);
                    //
                    int width = newFormat.getInteger(MediaFormat.KEY_WIDTH);
                    int height = newFormat.getInteger(MediaFormat.KEY_HEIGHT);
                    //
                    int cropLeft = newFormat.getInteger("crop-left");
                    int cropRight = newFormat.getInteger("crop-right");
                    int cropTop = newFormat.getInteger("crop-top");
                    int cropBottom = newFormat.getInteger("crop-bottom");
                    int cropWidth = cropRight - cropLeft + 1;
                    int cropHeight = cropBottom - cropTop + 1;
                    //
                    int ii = 0;
                } else if (decoderStatus < 0) {
                    showInfo_open.qyFuncs.showInfo_open("decoderStatus: < 0>");
                    Log.e( "unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
                    return;
                } else { // decoderStatus >= 0
                    //if (VERBOSE) Log.d( "surface decoder given buffer " + decoderStatus +             " (size=" + info.size + ")");
                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        showInfo_open.qyFuncs.showInfo_open("decoderStatus: (info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0");
                        //if (VERBOSE) Log.d( "output EOS");
                        outputDone = true;
                    }

                    boolean doRender = (info.size != 0);

                    // As soon as we call releaseOutputBuffer, the buffer will be forwarded
                    // to SurfaceTexture to convert to a texture.  The API doesn't guarantee
                    // that the texture will be available before the call returns, so we
                    // need to wait for the onFrameAvailable callback to fire.
                    decoder.releaseOutputBuffer(decoderStatus, doRender);
                    if (doRender) {
                        //
                        //showInfo_open.qyFuncs.showInfo_open("doRender---");
//                        com.qycx.base.util.Log.e("doRender----");
                        if (VERBOSE) {
                            Log.d( "awaiting decode of frame " + decodeCount);
                        }
                        //
                        outputSurface.awaitNewImage();

                        outputSurface.drawImage(true);

                        //
                        if (decodeCount < MAX_FRAMES) {
                        }
                        decodeCount++;
                        //
                        int iSampleTimeInMs = (int) (info.presentationTimeUs / 1000);
                        chkFromPlayerIndex_v.qyFuncs.chkFromPlayerIndex_v(dm.m_qoi.cfg.v.uiTransformType_pParent, dm.m_qoi.cfg.v.pParent_transform, iSampleTimeInMs);
                    }
                }
            }

            //
//            if  (  nTimes_tryAgainLater  >  2  )  {
//                showInfo_open.qyFuncs.showInfo_open("nTimes_tryAgainLater  >  2 break");
//                break;
//            }

            //
            continue;
        }

        showInfo_open.qyFuncs.showInfo_open("doDec exit");
        Log.d(IO_TAG,"doDec exit");
        int numSaved = (MAX_FRAMES < decodeCount) ? MAX_FRAMES : decodeCount;
        //Log.d( "Saving " + numSaved + " frames took " +    (frameSaveTime / numSaved / 1000) + " us per frame");


        return;
    }


    /**
     * Holds state associated with a Surface used for MediaCodec decoder output.
     * <p>
     * The constructor for this class will prepare GL, create a SurfaceTexture,
     * and then create a Surface for that SurfaceTexture.  The Surface can be passed to
     * MediaCodec.configure() to receive decoder output.  When a frame arrives, we latch the
     * texture with updateTexImage(), then render the texture with GL to a pbuffer.
     * <p>
     * By default, the Surface will be using a BufferQueue in asynchronous mode, so we
     * can potentially drop frames.
     */
    public static class CodecOutputSurface implements SurfaceTexture.OnFrameAvailableListener {
        private static String TAG = "CodecOutputSurface";

        private static boolean VERBOSE = false;           // lots of logging

        //
        public Object m_p0;
        //
        public EGLContext m_sharedEglContext1;
        //
        public int mWidth;
        public int mHeight;
        //
        Handler mHandler;

        //
        public STextureRender mTextureRender;
        public SurfaceTexture mSurfaceTexture;
        public Surface mSurface;

        private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
        private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;

        //private Object mFrameSyncObject = new Object();     // guards mFrameAvailable
        public EventWaitHandle evt_mFrameSyncObject = new EventWaitHandle(false, EventResetMode.AutoReset, "frameSync");

        public boolean mFrameAvailable;

        //
        private ByteBuffer mPixelBuf_unused;                       // used by saveFrame()

        /**
         * Creates a CodecOutputSurface backed by a pbuffer with the specified dimensions.  The
         * new EGL context and surface will be made current.  Creates a Surface that can be passed
         * to MediaCodec.configure().
         */
        public CodecOutputSurface(Object p0, EGLContext sharedEglContext, int width,
                                  int height, Handler handler) {
            //
            this.m_p0 = p0;
            this.m_sharedEglContext1 = sharedEglContext;
            if (width <= 0 || height <= 0) {
                Log.e("width and height must > 0");
                return;
            }
            mWidth = width;
            mHeight = height;
            mHandler = handler;

            //
            eglSetup();
            makeCurrent();
            setup();
        }

        /**
         * Creates interconnected instances of TextureRender, SurfaceTexture, and Surface.
         */
        private void setup() {
            mTextureRender = new STextureRender(m_p0, this.mWidth, this.mHeight);
            mTextureRender.surfaceCreated();

            //if (VERBOSE) Log.d( "textureID=" + mTextureRender.getTextureId());
            mSurfaceTexture = new SurfaceTexture(mTextureRender.getTextureId());

            // This doesn't work if this Object is created on the thread that CTS started for
            // these test cases.
            //
            // The CTS-created thread has a Looper, and the SurfaceTexture constructor will
            // create a Handler that uses it.  The "frame available" message is delivered
            // there, but since we're not a Looper-based thread we'll never see it.  For
            // this to do anything useful, CodecOutputSurface must be created on a thread without
            // a Looper, so that SurfaceTexture uses the main application Looper instead.
            //
            // Java language note: passing "this"  of a constructor is generally unwise,
            // but we should be able to get away with it here.
            if (this.mHandler != null)
                mSurfaceTexture.setOnFrameAvailableListener(this, this.mHandler);
            else mSurfaceTexture.setOnFrameAvailableListener(this);

            //
            mSurface = new Surface(mSurfaceTexture);

            /*
            mPixelBuf = ByteBuffer.allocateDirect(mWidth * mHeight * 4);
            mPixelBuf.order(ByteOrder.LITTLE_ENDIAN);

             */
        }

        /**
         * Prepares EGL.  We want a GLES 2.0 context and a surface that supports pbuffer.
         */
        private void eglSetup() {
            mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
                Log.e("unable to get EGL14 display");
                return;
            }
            int[] version = new int[2];
            if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
                mEGLDisplay = null;
                Log.e("unable to initialize EGL14");
                return;
            }

            // configure EGL for pbuffer and OpenGL ES 2.0, 24-bit RGB.
            int[] attribList = {
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            if (!EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length,
                    numConfigs, 0)) {
                Log.e("unable to find RGB888+recordable ES2 EGL config");
                return;
            }

            // configure context for OpenGL ES 2.0.
            int[] attrib_list = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };
            EGLContext share_context = EGL14.EGL_NO_CONTEXT;
            //
            share_context = this.m_sharedEglContext1;
            //
            mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], share_context,
                    attrib_list, 0);
            checkEglError("eglCreateContext");
            if (mEGLContext == null) {
                Log.e("null context");
                return;
            }

            //  create a pbuffer surface.
            int[] surfaceAttribs = {
                    EGL14.EGL_WIDTH, mWidth,
                    EGL14.EGL_HEIGHT, mHeight,
                    EGL14.EGL_NONE
            };
            mEGLSurface = EGL14.eglCreatePbufferSurface(mEGLDisplay, configs[0], surfaceAttribs, 0);
            checkEglError("eglCreatePbufferSurface");
            if (mEGLSurface == null) {
                Log.e("surface was null");
                return;
            }
        }

        /**
         * Discard all resources held by this class, notably the EGL context.
         */
        public void release() {
            //
            String str;

            str = String.format("decObj_mediaCodec.CodecOutputSurface.release enters");

            //
            if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
                EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(mEGLDisplay);
            }
            mEGLDisplay = EGL14.EGL_NO_DISPLAY;
            mEGLContext = EGL14.EGL_NO_CONTEXT;
            mEGLSurface = EGL14.EGL_NO_SURFACE;

            if (mSurface != null) {
                mSurface.release();
            }

            // this causes a bunch of warnings that appear harmless but might confuse someone:
            //  W BufferQueue: [unnamed-3997-2] cancelBuffer: BufferQueue has been abandoned!
            if (mSurfaceTexture != null) {
                mSurfaceTexture.release();
            }

            //
            if (mTextureRender != null) {
                mTextureRender.release();
            }

            //
            mTextureRender = null;
            mSurface = null;
            mSurfaceTexture = null;

        }

        /**
         * Makes our EGL context and surface current.
         */
        public void makeCurrent() {
            if (!EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
                Log.e("eglMakeCurrent failed");
                return;
            }
        }

        /**
         * Returns the Surface.
         */
        public Surface getSurface() {
            return mSurface;
        }

        /**
         * Latches the next buffer into the texture.  Must be called from the thread that created
         * the CodecOutputSurface Object.  (More specifically, it must be called on the thread
         * with the EGLContext that contains the GL texture Object used by SurfaceTexture.)
         */
        public void awaitNewImage() {
            int TIMEOUT_MS = 2500;
            //TIMEOUT_MS = 250000;

            //
            boolean bDbg = false;

            //synchronized(mFrameSyncObject)
            {
                while (!mFrameAvailable) {
                    // Wait for onFrameAvailable() to signal us.  Use a timeout to avoid
                    // stalling the test if it doesn't arrive.
                    //mFrameSyncObject.wait(TIMEOUT_MS);
                    long dwTickCnt0 = tmpFunc.qyFuncs.myGetTickCount(null);
                    tmpFunc.qyFuncs.myWaitOne(this.evt_mFrameSyncObject, TIMEOUT_MS);
                    //Thread.sleep((int)TIMEOUT_MS);
                    long dwTickCnt1 = tmpFunc.qyFuncs.myGetTickCount(null);
                    int iDiffInMs = (int) (dwTickCnt1 - dwTickCnt0);
                    if (iDiffInMs < 500) {
                        if (!mFrameAvailable) {
                            continue;
                        }
                    }
                    if (!mFrameAvailable) {
                        // TODO: if "spurious wakeup", continue while loop
                        Log.e("frame wait timed ");
                        break;
                    }
                    //
                    continue;
                }

                mFrameAvailable = false;
            }

            // Latch the data.
            mTextureRender.checkGlError("before updateTexImage");
            mSurfaceTexture.updateTexImage();
        }

        /**
         * Draws the data from SurfaceTexture onto the current EGL surface.
         *
         * @param invert if set, render the image with Y inverted (0,0 in top left)
         */
        public void drawImage(boolean invert) {
            mTextureRender.drawFrame(mSurfaceTexture, invert);
        }

        @Override
        public void onFrameAvailable(SurfaceTexture st) {
            myOnFrameAvailable(st);
            return;
        }

        //
        //public abstract void myOnFrameAvailable(SurfaceTexture st);
        public void myOnFrameAvailable(SurfaceTexture st) {
            boolean bDbg = false;
            String str;


            //synchronized(mFrameSyncObject)
            {
                if (mFrameAvailable) {
                    Log.e("mFrameAvailable already set, frame could be dropped");
                    return;
                }
                mFrameAvailable = true;
                //mFrameSyncObject.notifyAll();
                tmpFunc.qyFuncs.mySetEvent(this.evt_mFrameSyncObject);
            }
        }


        /**
         * Saves the current frame to disk as a PNG image.
         */

        public void saveFrame(String filename) throws IOException {
            // glReadPixels gives us a ByteBuffer filled with what is essentially big-endian RGBA
            // data (i.e. a byte of red, followed by a byte of green...).  To use the Bitmap
            // constructor that takes an int[] array with pixel data, we need an int[] filled
            // with little-endian ARGB data.
            //
            // If we implement this as a series of buf.get() calls, we can spend 2.5 seconds just
            // copying data around for a 720p frame.  It's better to do a bulk get() and then
            // rearrange the data in memory.  (For comparison, the PNG compress takes about 500ms
            // for a trivial frame.)
            //
            // So... we set the ByteBuffer to little-endian, which should turn the bulk IntBuffer
            // get() into a straight memcpy on most Android devices.  Our ints will hold ABGR data.
            // Swapping B and R gives us ARGB.  We need about 30ms for the bulk get(), and another
            // 270ms for the color swap.
            //
            // We can avoid the costly B/R swap here if we do it in the fragment shader (see
            // http://stackoverflow.com/questions/21634450/ ).
            //
            // Having said all that... it turns  that the Bitmap#copyPixelsFromBuffer()
            // method wants RGBA pixels, not ARGB, so if we create an empty bitmap and then
            // copy pixel data in we can avoid the swap issue entirely, and just copy straight
            // into the Bitmap from the ByteBuffer.
            //
            // Making this even more interesting is the upside-down nature of GL, which means
            // our output will look upside-down relative to what appears on screen if the
            // typical GL conventions are used.  (For ExtractMpegFrameTest, we avoid the issue
            // by inverting the frame when we render it.)
            //
            // Allocating large buffers is expensive, so we really want mPixelBuf to be
            // allocated ahead of time if possible.  We still get some allocations from the
            // Bitmap / PNG creation.

            /*

            mPixelBuf.rewind();
            GLES20.glReadPixels(0, 0, mWidth, mHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mPixelBuf);

            BufferedOutputStream bos = null;
            OutputStream fos = null;

            try {
                //
                fos = new FileOutputStream(filename, false);
                //bos = new BufferedOutputStream(new FileOutputStream(filename));
                bos = new BufferedOutputStream(fos);
                Bitmap bmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
                mPixelBuf.rewind();
                bmp.copyPixelsFromBuffer(mPixelBuf);
                //bmp.Compress(Bitmap.CompressFormat.Png, 90, bos);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                bmp.recycle();
            } catch (FileNotFoundException e) {
                Log.e(e.getMessage());
                return;
            } finally {
                if (bos != null) bos.close();
                if (fos != null) fos.close();
            }

            if (VERBOSE) {
                Log.d( "Saved " + mWidth + "x" + mHeight + " frame as '" + filename + "'");
            }

             */
        }

        /**
         * Checks for EGL errors.
         */
        public static void checkEglError(String msg) {
            int error;
            if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
                //Log.d("zzzzzccccccccccccccccccccccccccccccccc");

                Log.e(msg + ": EGL error: 0x" + Integer.toHexString(error));
            }
        }

    }


    public static class TMP_frameBuffer {
        //
        private int[] mFrameBuffer = new int[1];
        //
        public int[] mTexture = new int[1];

        //
        public boolean bDone_createEnv;

        //
        public void createEnv(int width, int height, String hint) {
            String str;
            if (hint == null) hint = "";


            // 1. 创建FrameBuffer
            GLES20.glGenFramebuffers(1, mFrameBuffer, 0);

            // 2.1 生成纹理对象
            GLES20.glGenTextures(1, mTexture, 0);
            str = String.format("TMP_frameBuffer.createEnv %sx%s, %s, textureID: %s", width, height, hint, mTexture[0]);
            com.qycx.base.util.Log.e(str);
            // 2.2 绑定纹理对象
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[0]);
            // 2.3 设置纹理对象的相关信息：颜色模式、大小
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                    width, height,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            // 2.4 纹理过滤参数设置
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            // 2.5 解绑当前纹理，避免后续无关的操作影响了纹理内容
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }

        public void bindFrameBufferInfo() {
            // 1. 绑定FrameBuffer到当前的绘制环境上
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
            // 2. 将纹理对象挂载到FrameBuffer上，存储颜色信息
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, mTexture[0], 0);
        }

        public void unbindFrameBufferInfo() {
            // 解绑FrameBuffer
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        }

        public void deleteEnv() {

            GLES20.glDeleteFramebuffers(1, mFrameBuffer, 0);
            GLES20.glDeleteTextures(1, mTexture, 0);
        }
    }

    ;


    //

    /**
     * Code for rendering a texture onto a surface using OpenGL ES 2.0.
     */
    public static class STextureRender {
        private static String TAG = "STextureRender";

        //
        public Object m_p0;
        public int m_iW, m_iH;

        //
        public TMP_frameBuffer tmpFrameBuffer = new TMP_frameBuffer();

        //
        private static int FLOAT_SIZE_BYTES = 4;
        private static int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
        private static int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
        private static int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
        private float[] mTriangleVerticesData = {
                // X, Y, Z, U, V
                -1.0f, -1.0f, 0, 0.0f, 0.0f,
                1.0f, -1.0f, 0, 1.0f, 0.0f,
                -1.0f, 1.0f, 0, 0.0f, 1.0f,
                1.0f, 1.0f, 0, 1.0f, 1.0f,
        };

        private FloatBuffer mTriangleVertices;

        private static String VERTEX_SHADER =
                "uniform mat4 uMVPMatrix;\n" +
                        "uniform mat4 uSTMatrix;\n" +
                        "attribute vec4 aPosition;\n" +
                        "attribute vec4 aTextureCoord;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "void main() {\n" +
                        "    gl_Position = uMVPMatrix * aPosition;\n" +
                        "    vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                        "}\n";

        private static String FRAGMENT_SHADER =
                "#extension GL_OES_EGL_image_external : require\n" +
                        "precision mediump float;\n" +      // highp here doesn't seem to matter
                        "varying vec2 vTextureCoord;\n" +
                        "uniform samplerExternalOES sTexture;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                        "}\n";

        private float[] mMVPMatrix = new float[16];
        private float[] mSTMatrix = new float[16];

        private int mProgram;
        //
        private int mVertexShader = -1;
        private int mFragmentShader = -1;
        //
        private int mTextureID = -12345;
        private int muMVPMatrixHandle;
        private int muSTMatrixHandle;
        private int maPositionHandle;
        private int maTextureHandle;

        public STextureRender(Object p0, int iW, int iH) {
            //
            m_p0 = p0;
            m_iW = iW;
            m_iH = iH;
            //
            mTriangleVertices = ByteBuffer.allocateDirect(
                            mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTriangleVertices.put(mTriangleVerticesData).position(0);

            Matrix.setIdentityM(mSTMatrix, 0);


            //
            return;
        }

        public int getTextureId() {
            return mTextureID;
        }

        public void drawFrame(SurfaceTexture st, boolean invert) {
            String str;

            //
            checkGlError("onDrawFrame start");
            st.getTransformMatrix(mSTMatrix);
            if (invert) {
                mSTMatrix[5] = -mSTMatrix[5];
                mSTMatrix[13] = 1.0f - mSTMatrix[13];
            }


            // 2. 配置FrameBuffer相关的绘制存储信息，并且绑定到当前的绘制环境上
            this.tmpFrameBuffer.bindFrameBufferInfo();
            // 3. 更新视图区域
            GLES20.glViewport(0, 0, m_iW, m_iH);


            // (optional) clear to green so we can see if we're failing to set pixels
            GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glUseProgram(mProgram);
            checkGlError("glUseProgram");

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
            checkGlError("glVertexAttribPointer maPosition");
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            checkGlError("glEnableVertexAttribArray maPositionHandle");

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
            GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
            checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(maTextureHandle);
            checkGlError("glEnableVertexAttribArray maTextureHandle");

            Matrix.setIdentityM(mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            checkGlError("glDrawArrays");

            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

            //
            this.tmpFrameBuffer.unbindFrameBufferInfo();

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) {
                com.qycx.base.util.Log.e("pQyMc ==null");
                return;
            }
            MainActivity mainActivity = (MainActivity) (pQyMc.gui.hMainWnd);
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            decObj_mediaCodec dm = (decObj_mediaCodec) this.m_p0;
            //
            {
                //
                updateTexInfo(pProcInfo, dm, this.tmpFrameBuffer.mTexture[0], m_iW, m_iH, dm.m_qoi.cfg.v.iTaskId);

                int id = this.tmpFrameBuffer.mTexture[0];
                mainActivity.textureIw = m_iW;
                mainActivity.textureIh = m_iH;
                mainActivity.textureId = id;
//                com.qycx.base.util.Log.e("drawFrame：textureId: " + mainActivity.textureId);
            }
        }


        //
        public int updateTexInfo(ctxQmc.CCtxQmc pProcInfo, decObj_mediaCodec dm, int texId, int w, int h, int iTaskId) {
            int iErr = -1;


            if (dm.m_qoi.cfg.v.uiTransformType_pParent == CONST_uiTransformType_dec) {
                qmcTransform.QY_TRANSFORM pTransform = (qmcTransform.QY_TRANSFORM) dm.m_qoi.cfg.v.pParent_transform;
                if (pTransform.index_activeMems_from < 0 || pTransform.index_activeMems_from >= pProcInfo.av.texes.taskAvTexes.tti_peers.length)
                    return iErr;
                ctxQmc.TMP_tex_info tti = pProcInfo.av.texes.taskAvTexes.tti_peers[pTransform.index_activeMems_from];


                //
                if (tti.texId != texId) {
                    tti.texId = texId;
                }
                if (tti.iW != w) {
                    tti.iW = w;
                }
                if (tti.iH != h) {
                    tti.iH = h;
                }
                int ii = 0;


            }


            iErr = 0;
            return iErr;
        }


        /**
         * Initializes GL state.  Call this after the EGL surface has been created and made current.
         */
        public void surfaceCreated() {
            mProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER, this.mVertexShader, this.mFragmentShader);
            if (mProgram == 0) {
                Log.e("failed creating program");
                return;
            }

            maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            checkLocation(maPositionHandle, "aPosition");
            maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
            checkLocation(maTextureHandle, "aTextureCoord");

            muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            checkLocation(muMVPMatrixHandle, "uMVPMatrix");
            muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
            checkLocation(muSTMatrixHandle, "uSTMatrix");

            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);

            mTextureID = textures[0];
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
            checkGlError("glBindTexture mTextureID");

            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
            checkGlError("glTexParameter");
        }

        public void release() {
            if (this.mProgram > 0) {
                GLES20.glDeleteProgram(this.mProgram);
                mProgram = -1;
            }
            if (this.mVertexShader > 0) {
                GLES20.glDeleteShader(this.mVertexShader);
                this.mVertexShader = -1;
            }
            if (this.mFragmentShader > 0) {
                GLES20.glDeleteShader(this.mFragmentShader);
                this.mFragmentShader = -1;
            }
            if (this.mTextureID > 0) {                //
                int[] texes = new int[1];
                texes[0] = mTextureID;
                GLES20.glDeleteTextures(1, texes, 0);
                mTextureID = 0;
            }


            //int []
            return;
        }

        /**
         * Replaces the fragment shader.  Pass in null to reset to default.
         */
        public void changeFragmentShader(String fragmentShader) {
            if (fragmentShader == null) {
                fragmentShader = FRAGMENT_SHADER;
            }
            GLES20.glDeleteProgram(mProgram);
            mProgram = createProgram(VERTEX_SHADER, fragmentShader, this.mVertexShader, this.mFragmentShader);
            if (mProgram == 0) {
                Log.e("failed creating program");
            }
        }

        private int loadShader(int shaderType, String source) {
            int shader = GLES20.glCreateShader(shaderType);
            checkGlError("glCreateShader type=" + shaderType);
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                //Log.e( "Could not compile shader " + shaderType + ":");
                //Log.e( " " + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
            return shader;
        }

        private int createProgram(String vertexSource, String fragmentSource, int vertexShader_o, int pixShader_o) {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
            if (vertexShader == 0) {
                return 0;
            }
            int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
            if (pixelShader == 0) {
                return 0;
            }

            //
            vertexShader_o = vertexShader;
            pixShader_o = pixShader_o;

            //
            int program = GLES20.glCreateProgram();
            if (program == 0) {
                //Log.e( "Could not create program");
            }
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                //Log.e( "Could not link program: ");
                //Log.e( GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }


            //
            return program;
        }

        public void checkGlError(String op) {
            int error;
            while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e( op + ": glError " + error);
                break;
            }
        }

        public static void checkLocation(int location, String label) {
            if (location < 0) {
                Log.e("Unable to locate '" + label + "' in program");
            }
        }
    }


}

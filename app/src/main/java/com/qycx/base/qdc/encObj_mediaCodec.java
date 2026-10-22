package com.qycx.base.qdc;

import static com.qycx.base.Consts.VIDEO_UPLOAD;
import static com.qycx.base.enc_parseOrgAPkt_i.Consts.CONST_timeInMs_forceKeyFrame;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_HEVC;
import static com.qycx.base.qyCommProc_open.Consts.CONST_uiTransformType_encVideo;
import static com.qycx.base.qyCommProc_open.Consts.MAX_spsPpsLen;
import static com.qycx.base.qyCommProc_open.Consts.MIME_TYPE_hevc;
import static com.qycx.base.qyCommonFunc9.Consts.cntof_buf1024;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.tmpFunc.qyFuncs.mySleep;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.view.Surface;

import com.qycx.MyApplication;
import com.qycx.base.EventWaitHandle;
import com.qycx.base.SimpleTexture;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.doPostEnc_mediaSdk_new;
import com.qycx.base.parseSpsPps;
import com.qycx.base.qdcCommProc_open;
import com.qycx.base.qmcSharedObj;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyCommProc_open;
import com.qycx.base.qyCommonFunc9;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qySyncObj;
import com.qycx.base.showInfo_open;
import com.qycx.base.test;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.opengl.EGL14;
import android.opengl.GLES20;

/**
 * Generate an MP4 file using OpenGL ES drawing commands.  Demonstrates the use of MediaMuxer
 * and MediaCodec with Surface input.
 * <p>
 * This uses various features first available in Android "Jellybean" 4.3 (API 18).  There is
 * no equivalent functionality in previous releases.
 * <p>
 * (This was derived from bits and pieces of CTS tests, and is packaged as such, but is not
 * currently part of CTS.)
 */
public class encObj_mediaCodec //extends AndroidTestCase
{

    // where to put the output file (note: /sdcard requires WRITE_EXTERNAL_STORAGE permission)
    private static File OUTPUT_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    // parameters for the encoder
    //private static String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private String mimeType = "video/avc";    // H.264 Advanced Video Coding

    //
    private static int FRAME_RATE = 30;//15;               // 15fps
    private static int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames
    private static int NUM_FRAMES = 15 * 30;               // two seconds of video

    // RGB color values for generated frames
    private static int TEST_R0 = 0;
    private static int TEST_G0 = 136;
    private static int TEST_B0 = 0;
    private static int TEST_R1 = 236;
    private static int TEST_G1 = 50;
    private static int TEST_B1 = 186;

    //
    //
    public EGLContext m_sharedEglContext;
    public qdcCommProc_open.QOI_common m_qoi;
    public int m_index_sharedObj;

    //
    public boolean m_bQuit;


    // size of a frame, in pixels
    public int mWidth = -1;
    public int mHeight = -1;
    // bit rate, in bits per second
    public int mBitRate = -1;

    //
    public int m_iFourcc_o = 0;


    //
    public boolean m_bEnd;

    //
    public ByteBuffer m_tmpBuf_spspps;
    public int m_len_spspps;
    public volatile boolean m_bGot_spspps;


    // encoder / muxer state
    private MediaCodec mEncoder;
    public CodecInputSurface mInputSurface;
    //
    private MediaMuxer mMuxer;
    private int mTrackIndex;
    private boolean mMuxerStarted;

    // allocate one of these up front so we don't need to do it every time
    private MediaCodec.BufferInfo mBufferInfo;

    public boolean m_bTest;

    public encObj_mediaCodec_Wrapper m_wrapper;
    public Thread m_th;

    public SimpleTexture m_simpleTex_o;

    //
    byte[] m_pInput = new byte[100000];
    qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead m_tmp_head=new qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead();


    //
    public encObj_mediaCodec() {
        int ii = 0;

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
    }

    public boolean bQuit() {
        boolean bRet = false;
        String str;

        if (doPostEnc_mediaSdk_new.qyFuncs.bCapBmpQuit(this.m_qoi.cfg.v.pParent_transform, null, null)) {
            bRet = true;
            return bRet;
        }
        if (m_bQuit) {
            bRet = true;
            return bRet;
        }
        return bRet;
    }



    public static class encObj_mediaCodec_Wrapper implements Runnable {
        private Throwable mThrowable;
        private encObj_mediaCodec mTest;

        public encObj_mediaCodec_Wrapper(encObj_mediaCodec test) {
            mTest = test;

        }

        @Override
        public void run() {
            Log.i("encObj_mediaCodec", "encObj_mediaCodec_Wrapper");
            String str;

            try {
                mTest.prepareEncoder();
                mTest.mInputSurface.makeCurrent();

                for (int i = 0; ; i++) {
                    ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
                    if (pQyMc == null) {
                        Log.e("pQyMc==null");
                        break;
                    }
                    //if (i >= NUM_FRAMES) break;
                    if (this.mTest.bQuit()) {
                        break;
                    }


                    // Feed any pending encoder output into the muxer.
                    mTest.drainEncoder(false);

                    // Generate a new frame of input.
                    mTest.generateSurfaceFrame(i);
                    mTest.mInputSurface.setPresentationTime(computePresentationTimeNsec(i));

                    // Submit it to the encoder.  The eglSwapBuffers call will block if the input
                    // is full, which would be bad if it stayed full until we dequeued an output
                    // buffer (which we can't do, since we're stuck here).  So long as we fully drain
                    // the encoder before supplying additional input, the system guarantees that we
                    // can supply another frame without blocking.
//                    Log.d("sending frame " + i + " to encoder");

                    //
                    mTest.mInputSurface.swapBuffers();
                }

                // send end-of-stream to encoder, and drain remaining output
                mTest.drainEncoder(true);
            } catch (Exception e) {
                Log.e(e.getLocalizedMessage());
                e.printStackTrace();
            } finally {
                //
                Log.d("finally encObj.run, before releaseEncoder");

                // release encoder, muxer, and input Surface
                mTest.releaseEncoder();
            }

            mTest.m_bEnd = true;

            tmpFunc.qyFuncs.mySetEvent((EventWaitHandle) mTest.m_qoi.hEvent_notifyToQuit);
        }
    }

    int enc_muxer_new() {
        if (m_bTest) {
            String outputPath = new File(OUTPUT_DIR,
                    "test2." + mWidth + "x" + mHeight + ".mp4").toString();
            Log.d("output file is " + outputPath);

            try {
                mMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException ioe) {
                Log.e("MediaMuxer creation failed   "+ ioe.getMessage());
                return -1;
            }
        } else {

        }

        return 0;
    }

    int enc_muxer_start(MediaFormat newFormat) {
        if (m_bTest) {
            mTrackIndex = mMuxer.addTrack(newFormat);
            mMuxer.start();
        }


        return 0;
    }

    //
    int  m_iLoopCtrl  =  0;

    //
    int enc_muxer_writeSampleData(ByteBuffer encodedData, MediaCodec.BufferInfo mBufferInfo) {
        boolean bDbg = false;
        String str;

//        showInfo_open.qyFuncs.showInfo_open("enc_muxer_writeSampleData");
        //
        boolean keyFrame = (mBufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0;
        if (keyFrame) {
            str = ("enc.get a key frame");
            Log.d("qy",str);
        }

        //
        if (m_bTest) {
            mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
            Log.d("sent " + mBufferInfo.size + " bytes to muxer");
        } else {
            try {

                if (m_pInput.length < mBufferInfo.size) {
                    m_pInput = new byte[mBufferInfo.size];
                }

                byte[] pInput = m_pInput;


                //
                if  (  encodedData.position()  !=  0)  {
                    test.testCls_Droid.testBreak();
                }

                //
                int buffer_len = mBufferInfo.size;
                encodedData.get(pInput, 0, buffer_len);

                //
                int iSampleInMs = (int) (mBufferInfo.presentationTimeUs / 1000.0f);
                int uiPts = 0;

                byte ucbKeyFrame = (byte) (keyFrame ? 1 : 0);

                //
                if  (  true ) {
                    //Log.d("qy", "video st  " + iSampleInMs);
                }

                //
                m_iLoopCtrl  ++  ;
                if  (  0  ==  (  m_iLoopCtrl  %  50)  )  {
                    //showInfo_open.qyFuncs.showInfo_open("before doPostEnc_mediaSdk_new "  +  m_iLoopCtrl);
                }

                //
                doPostEnc_mediaSdk_new.qyFuncs.doPostEnc_mediaSdk_new(this.m_qoi, pInput, 0,  buffer_len, iSampleInMs, uiPts, ucbKeyFrame, m_tmp_head);

            } catch (Exception e) {
                int ii = 0;
                Log.e(e.getMessage());
            }

        }


        //
        return 0;
    }

    int enc_muxer_exit() {
        if (m_bTest) {
            if (mMuxer != null) {
                mMuxer.stop();
                mMuxer.release();
                mMuxer = null;
            }
        }
        //
        if (this.m_simpleTex_o != null) {
            this.m_simpleTex_o.destroy();
            this.m_simpleTex_o = null;
        }

        return 0;
    }

    /**
     * Configures encoder and muxer state, and prepares the input Surface.
     */
    public void prepareEncoder() {
        mBufferInfo = new MediaCodec.BufferInfo();

        //
        if (m_iFourcc_o == CONST_fourcc_HEVC) {
            mimeType = MIME_TYPE_hevc;
        }

        //
        MediaFormat format = MediaFormat.createVideoFormat(mimeType, mWidth, mHeight);

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        //
        boolean b100k =false;
        int use100k_frameRate  =  1;
        int use100k_iFrameInterval  =  1;
        ctxQyMc.CCtxQyMc pQyMc  =  g_pQyMc;
        if  (  pQyMc!=null  )  {
            ctxQmc.CCtxQmc  pProcInfo  =  pQyMc.get_pProcInfo();
            if  (  pProcInfo!=null)  {
                b100k=pProcInfo.bUse100k;
                //
                use100k_frameRate=pProcInfo.cfgInfo100k.use100k_v_frameRate;
                use100k_iFrameInterval=pProcInfo.cfgInfo100k.use100k_v_I_FRAME_INTERVAL;
            }
        }
        if  (b100k) {
            format.setInteger(MediaFormat.KEY_FRAME_RATE, use100k_frameRate);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, use100k_iFrameInterval);
        }
        else {
            format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        }
        //
        Log.d("format: " + format);

        //
        format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);


        //  create a MediaCodec encoder, and configure it with our format.  get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        //
        // If you want to have two EGL contexts -- one for display, one for recording --
        // you will likely want to defer instantiation of CodecInputSurface until after the
        // "display" EGL context is created, then modify the eglCreateContext call to
        // take eglGetCurrentContext() as the share_context argument.
        try {
            mEncoder = MediaCodec.createEncoderByType(mimeType);
        } catch (IOException e) {
            Log.e(e.getMessage());
            return;
        }
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSurface = new CodecInputSurface(mEncoder.createInputSurface(), null, this.m_sharedEglContext);
        mEncoder.start();

        // Output filename.  Ideally this would use Context.getFilesDir() rather than a
        // hard-coded output directory.


        //  create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.

        enc_muxer_new();

        //
        mTrackIndex = -1;
        mMuxerStarted = false;
    }

    /**
     * Releases encoder resources.  May be called after public static / failed initialization.
     */
    private void releaseEncoder() {
        Log.d("releasing encoder objects");
        if (mEncoder != null) {
           try{
               mEncoder.stop();
               mEncoder.release();
           }catch (Exception e)
           {
               Log.e(e.getMessage());
           }
            mEncoder = null;
        }
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }

        enc_muxer_exit();
    }

    /**
     * Extracts all pending data from the encoder.
     * <p>
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     */
    private void drainEncoder(boolean endOfStream) {
        int iErr = -1;
        int TIMEOUT_USEC = 10000;

        if (endOfStream) {
            Log.d("sending EOS to encoder");
            mEncoder.signalEndOfInputStream();
        }

        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
        while (true) {

            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);

            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      //  of while
                } else {
                    Log.d("no output available, spinning to await EOS");
                }
            } else if (encoderStatus == (int) MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == (int) MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted) {
                    Log.e("format changed twice");
                    return;
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();
                Log.d("encoder output format changed: " + newFormat);

                MediaFormat tmpFormat = mEncoder.getOutputFormat();

                if (m_iFourcc_o == CONST_fourcc_HEVC) {
                    ByteBuffer spsBb = tmpFormat.getByteBuffer("csd-0");
                    if (spsBb == null) return;
                    int len = spsBb.capacity();
                    m_tmpBuf_spspps = spsBb;
                    this.m_len_spspps = len;
                    this.m_bGot_spspps = true;
                } else {
                    qyCommProc_open.VH_stream vs = new qyCommProc_open.VH_stream();
                    ByteBuffer spsBb = tmpFormat.getByteBuffer("csd-0");
                    if (spsBb == null) return;
                    int len = spsBb.capacity();
                    vs.mem0.spsPps = spsBb;
                    vs.mem0.usLen = len;

                    ByteBuffer ppsBb = tmpFormat.getByteBuffer("csd-1");
                    if (ppsBb == null) return;
                    len = ppsBb.capacity();
                    vs.mem1.spsPps = ppsBb;
                    vs.mem1.usLen = len;
                    vs.usCnt = 2;

                    if (0 > (len = parseSpsPps.qyFuncs.makeSpsPps(vs, m_tmpBuf_spspps)))
                        return;
                    this.m_len_spspps = len;
                    this.m_bGot_spspps = true;

                    tmpFunc.qyFuncs.mySetEvent((EventWaitHandle) this.m_qoi.hEvent_notifyToQuit);
                }

                enc_muxer_start(newFormat);
                //
                mMuxerStarted = true;
            } else if (encoderStatus < 0) {
                Log.w("unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
            } else {
                if (encoderStatus == 0) {
                    int ii = 0;
                }

                //
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    Log.e("encoderOutputBuffer " + encoderStatus + " was null");
                    return;
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled  and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    Log.d("ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        Log.e("muxer hasn't started");
                        return;
                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                    enc_muxer_writeSampleData(encodedData, mBufferInfo);

                }

                mEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.w("reached end of stream unexpectedly");
                    } else {
                        Log.d("end of stream reached");
                    }
                    break;      //  of while
                }
            }
        }


        iErr = 0;


        //
        {
            //Log.d("drainEncoder leaves");
        }

        //
        return;
    }

    /**
     * Generates a frame of data using GL commands.  We have an 8-frame animation
     * sequence that wraps around.  It looks like this:
     * <pre>
     *   0 1 2 3
     *   7 6 5 4
     * </pre>
     * We draw one of the eight rectangles and leave the rest set to the clear color.
     */
    private void generateSurfaceFrame(int frameIndex) {
        frameIndex %= 8;

        //
        boolean bDbg = false;
        String str;

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            Log.e("pQyMc==null");
            return;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        boolean bTest_draw = false;
        bTest_draw = true;
        if (bTest_draw) {
            //
            long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);

            //
            if  (  pProcInfo.bUse100k) {
                    mySleep(pProcInfo.cfgInfo100k.use100k_enc_sleepInMs);
            }

            //
            test_draw();

            long dwTickCnt_end = tmpFunc.qyFuncs.myGetTickCount(null);

            return;
        }


        //
        int startX, startY;
        if (frameIndex < 4) {
            // (0,0) is bottom-left in GL
            startX = frameIndex * (mWidth / 4);
            startY = mHeight / 2;
        } else {
            startX = (7 - frameIndex) * (mWidth / 4);
            startY = 0;
        }

        GLES20.glClearColor(TEST_R0 / 255.0f, TEST_G0 / 255.0f, TEST_B0 / 255.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        GLES20.glScissor(startX, startY, mWidth / 4, mHeight / 2);
        GLES20.glClearColor(TEST_R1 / 255.0f, TEST_G1 / 255.0f, TEST_B1 / 255.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
    }

    int test_draw() {
        int iErr = -1;

        boolean bDbg = false;
        String str;

        //
        Context context = MyApplication.getContext();

        //
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, this.m_index_sharedObj);
        if (pSharedObj == null) return iErr;

        for (; ; ) {
            if (this.bQuit()) {
                str = String.format("test_draw failed: bCapBmpQuit is true");
                Log.e(str);
                return iErr;
            }
            //
            if (pSharedObj.var.bFrameAvail) break;
            tmpFunc.qyFuncs.myWaitOne(pSharedObj.var.hEvt_notifyFrameAvail, 33);
        }
        //
        pSharedObj.var.bFrameAvail = false;

        //
        int old_lPktId = 0;
        byte[] pInput = null;
        int uiInputSize = 0;
        boolean bNeed_forceKeyFrame = false;
        //
        if (m_qoi.cfg.v.uiTransformType_pParent == CONST_uiTransformType_encVideo) {
            qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp = (qmcSharedObj.CAP_procInfo_bmpCommon) m_qoi.cfg.v.pParent_transform;

            if (pCapBmp.compressVideo.encInfo.dwTickCnt_startToForceKeyFrame != 0) {
                long dwTickCnt = tmpFunc.qyFuncs.myGetTickCount(null);
                if (dwTickCnt - pCapBmp.compressVideo.encInfo.dwTickCnt_startToForceKeyFrame < CONST_timeInMs_forceKeyFrame) {
                    bNeed_forceKeyFrame = true;
                    //
                    //qyFuncs.showInfo_open(  0,  null,  (  "enc_parseOrgVPkt_i: bNeed_forceKeyFrame true"  )  );
                    //
                    if (dwTickCnt - m_qoi.var.encInfo.dwLastTickCnt_insertKeyFrame > 1000) {
                        m_qoi.var.encInfo.dwLastTickCnt_insertKeyFrame = dwTickCnt;
                        //
                        Log.d(("hdEnc_conf: insertKeyFrame"));
                        //
                        //this->InsertKeyFrame();
                        if ((int) Build.VERSION.SDK_INT >= 23) {
                            Bundle params1 = new Bundle();

                            params1.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                            this.mEncoder.setParameters(params1);
                        }
                    }


                }
            }

            //
            if  (  false  ) {
                if (pProcInfo.bUse100k) {   //  对100k，要求每帧都是关键帧
                    if ((int) Build.VERSION.SDK_INT >= 23) {
                        Bundle params1 = new Bundle();

                        params1.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                        this.mEncoder.setParameters(params1);
                    }
                }
            }
        }

        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
            if (0 != syncObj.sync("", pSharedObj.var.hMutex_syncEncFrame,"text_draw.670")) {
                str = String.format("test_draw failed, mutex sync failed");
                Log.e(str);
                return iErr;
            }

            int w = this.mWidth;
            int h = this.mHeight;

            //
            int texId = 0;
            //texId=camera_RenderThread.m_test_textureId;
            texId = pSharedObj.var.m_o_texId;

            //
            if (this.m_simpleTex_o == null) {
                this.m_simpleTex_o = new SimpleTexture(context, texId, false, "enc.881");
            }

            this.m_simpleTex_o.resize(w, h, 0);
            this.m_simpleTex_o.draw(0);

            syncObj.Dispose();
        } catch (Exception e) {
            Log.e(e.getMessage());
            return -1;
        }
        return 0;
    }


    /**
     * Generates the presentation time for frame N, in nanoseconds.
     */
    private static long computePresentationTimeNsec(int frameIndex) {
        long ONE_BILLION = 1000000000;
        return frameIndex * ONE_BILLION / FRAME_RATE;
    }


    /**
     * Holds state associated with a Surface used for MediaCodec encoder input.
     * <p>
     * The constructor takes a Surface obtained from MediaCodec.createInputSurface(), and uses that
     * to create an EGL window surface.  Calls to eglSwapBuffers() cause a frame of data to be sent
     * to the video encoder.
     * <p>
     * This Object owns the Surface -- releasing this will release the Surface too.
     */
    public static class CodecInputSurface {
        public Object m_p0;
        //
        public EGLContext m_sharedEglContext1;

        //
        private static int EGL_RECORDABLE_ANDROID = 0x3142;

        private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
        private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;

        private Surface mSurface;

        /**
         * Creates a CodecInputSurface from a Surface.
         */
        public CodecInputSurface(Surface surface, Object p0, EGLContext sharedEglContext) {
            if (surface == null) {
                Log.e("surface is null");
                return;
            }
            mSurface = surface;

            //
            m_p0 = p0;
            m_sharedEglContext1 = sharedEglContext;

            //
            eglSetup();
        }

        /**
         * Prepares EGL.  We want a GLES 2.0 context and a surface that supports recording.
         */
        private void eglSetup() {
            mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
                Log.e("unable to get EGL14 display");
                return;
            }
            int[] version = new int[2];
            if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
                Log.e("unable to initialize EGL14");
                return;
            }

            // configure EGL for recording and OpenGL ES 2.0.
            int[] attribList = {
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL_RECORDABLE_ANDROID, 1,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length,
                    numConfigs, 0);
            checkEglError("eglCreateContext RGB888+recordable ES2");

            // configure context for OpenGL ES 2.0.
            int[] attrib_list = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };
            //
            EGLContext share_context = EGL14.EGL_NO_CONTEXT;
            //
            share_context = this.m_sharedEglContext1;
            //
            mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], share_context,
                    attrib_list, 0);
            checkEglError("eglCreateContext");

            //  create a window surface, and attach it to the Surface we received.
            int[] surfaceAttribs = {
                    EGL14.EGL_NONE
            };
            mEGLSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, configs[0], mSurface,
                    surfaceAttribs, 0);
            checkEglError("eglCreateWindowSurface");
        }

        /**
         * Discards all resources held by this class, notably the EGL context.  Also releases the
         * Surface that was passed to our constructor.
         */
        public void release() {
            if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                        EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
                EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(mEGLDisplay);
            }

            mSurface.release();

            mEGLDisplay = EGL14.EGL_NO_DISPLAY;
            mEGLContext = EGL14.EGL_NO_CONTEXT;
            mEGLSurface = EGL14.EGL_NO_SURFACE;

            mSurface = null;
        }

        /**
         * Makes our EGL context and surface current.
         */
        public void makeCurrent() {
            EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
            checkEglError("eglMakeCurrent");
        }

        /**
         * Calls eglSwapBuffers.  Use this to "publish" the current frame.
         */
        public boolean swapBuffers() {
            boolean result = EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
            checkEglError("eglSwapBuffers");
            if  (  !result  )  {
                Log.d("qy","eglSwapBuffers failed");
            }
            return result;
        }

        /**
         * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
         */
        public void setPresentationTime(long nsecs) {
            EGLExt.eglPresentationTimeANDROID(mEGLDisplay, mEGLSurface, nsecs);
            checkEglError("eglPresentationTimeANDROID");
        }

        /**
         * Checks for EGL errors.  Throws an exception if one is found.
         */
        private void checkEglError(String msg) {
            int error;
            if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
                //Log.d("zzzzzbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
                Log.e(msg + ": EGL error: 0x" + Integer.toHexString(error));
            }
        }
    }
}





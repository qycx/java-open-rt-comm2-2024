package com.qycx;

import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.qycx.base.SimpleTexture;
import com.qycx.base.camera.MyCamera;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.guiShare;
import com.qycx.base.myrecycleFunc;
import com.qycx.base.qdc.decObj_mediaCodec;
import com.qycx.base.qmcSharedObj;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qySyncObj;
import com.qycx.base.saveTexture;
import com.qycx.base.showInfo_open;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.CameraUtils;
import com.qycx.base.util.Log;
import com.qycx.base.util.ScaledDrawable2d;
import com.qycx.base.util.javatuples.Triplet;
import com.qycx.gleslibrary.Drawable2d;
import com.qycx.gleslibrary.EglCore;
import com.qycx.gleslibrary.GlUtil;
import com.qycx.gleslibrary.Sprite2d;
import com.qycx.gleslibrary.Texture2dProgram;
import com.qycx.gleslibrary.WindowSurface;

import java.io.IOException;

/**
 * Thread that handles all rendering and camera operations.
 */
public class camera_RenderThread extends Thread implements SurfaceTexture.OnFrameAvailableListener {

    //
    private static int DEFAULT_ZOOM_PERCENT = 0;      // 0-100
    private static int DEFAULT_SIZE_PERCENT = 50;     // 0-100
    private static int DEFAULT_ROTATE_PERCENT = 0;    // 0-100

    // Requested values; actual may differ.
    private static int REQ_CAMERA_WIDTH = 480;//1280;
    private static int REQ_CAMERA_HEIGHT = 640;//720;
    private static int REQ_CAMERA_FPS = 30;

    private final static int MSG_SEND_CAMERA_PARAMS0 = 0;
    private final static int MSG_SEND_CAMERA_PARAMS1 = 1;
    private final static int MSG_SEND_RECT_SIZE = 2;
    private final static int MSG_SEND_ZOOM_AREA = 3;
    private final static int MSG_SEND_ROTATE_DEG = 4;


    // Object must be created on render thread to get correct Looper, but is used from
    // UI thread, so we need to declare it volatile to ensure the UI thread sees a fully
    // constructed Object.
    public camera_RenderHandler mHandler;

    // Used to wait for the thread to start.
    private Object mStartLock = new Object();
    private boolean mReady = false;

    //
    private boolean mThreadEnd = false;

    //
    private Handler mMainHandler;
    EGLContext mEglContext_shared;
    int miIndex_sharedObj;

    //
    //boolean m_bOrientation_landscape;
    boolean m_bFront;
    //
    int m_surfaceOrientation;

    //
    boolean m_bUse_camera1 = false;

    //
    private Camera mCamera1;

    //
    public MyCamera myCamera;


    //
    public int mCameraPreviewWidth, mCameraPreviewHeight;

    //
    private EglCore mEglCore;
    private WindowSurface mWindowSurface;
    private int mWindowSurfaceWidth;
    private int mWindowSurfaceHeight;

    // Receives the output from the camera preview.
    private SurfaceTexture mCameraTexture;

    // Orthographic projection matrix.
    private float[] mDisplayProjectionMatrix = new float[16];
    private float[] mDisplayProjectionMatrix_invert = new float[16];


    private Texture2dProgram mTexProgram;
    private static ScaledDrawable2d mRectDrawable = new ScaledDrawable2d(Drawable2d.Prefab.RECTANGLE);
    private Sprite2d mRect = new Sprite2d(mRectDrawable);

    private int mZoomPercent = DEFAULT_ZOOM_PERCENT;
    private int mSizePercent = DEFAULT_SIZE_PERCENT;
    private int mRotatePercent = DEFAULT_ROTATE_PERCENT;
    private float mPosX, mPosY;

    public decObj_mediaCodec.TMP_frameBuffer tmpFrameBuffer_0 = new decObj_mediaCodec.TMP_frameBuffer();        // camera_w*camera_h
    public decObj_mediaCodec.TMP_frameBuffer tmpFrameBuffer_int = new decObj_mediaCodec.TMP_frameBuffer();      // camera_big*camera_big
    public decObj_mediaCodec.TMP_frameBuffer tmpFrameBuffer_rotate = new decObj_mediaCodec.TMP_frameBuffer();  //
    public decObj_mediaCodec.TMP_frameBuffer tmpFrameBuffer_o = new decObj_mediaCodec.TMP_frameBuffer();       // h*w

    //
    public SimpleTexture m_simpleTex_0_int;
    public SimpleTexture m_simpleTex_rotate;
    public SimpleTexture m_simpleTex_o;


    /**
     * Constructor.  Pass in the MainHandler, which allows us to send stuff back to the
     * Activity.
     */
    public camera_RenderThread(Handler mainHandler, EGLContext eglContext_shared, int index_sharedObj, boolean bFront, int surfaceOrientation) {
        mMainHandler = mainHandler;
        mEglContext_shared = eglContext_shared;
        //
        miIndex_sharedObj = index_sharedObj;
        //
        m_bFront = bFront;
        //
        m_surfaceOrientation = surfaceOrientation;

        return;
    }

    /**
     * Thread entry point.
     */
    @Override
    public void run() {
        ctxQyMc.CCtxQyMc pQyMc  =  g_pQyMc;
        if (pQyMc == null) {
            Log.e("qyMcMain_global.g.g_pQyMc == null");
            releaseCamera();
            return;
        }
        ctxQmc.CCtxQmc  pProcInfo  =  pQyMc.get_pProcInfo();
        if  (  pProcInfo==null)  {
            Log.e("pProcInfo == null");
            releaseCamera();
            return;
        }


        //
        Looper.prepare();

        // We need to create the Handler before reporting ready.
        mHandler = new camera_RenderHandler(this);

        // Prepare EGL and open the camera before we start handling messages.
        EGLContext eglContext = null;
        eglContext = this.mEglContext_shared;
        try {
            mEglCore = new EglCore(eglContext, 0);
        } catch (Exception e) {
            Log.e("camera_Render.Run failed, new EglCore exception");
            free();
            return;
        }
        ///
        int reqW = REQ_CAMERA_WIDTH;
        int reqH = REQ_CAMERA_HEIGHT;
        int reqFps = REQ_CAMERA_FPS;

        //
        reqW = 1080;
        reqH = 1920;

        //

        if  (  pProcInfo.bUse100k) {

            reqW = pProcInfo.cfgInfo100k.use100k_v_w;
            reqH = pProcInfo.cfgInfo100k.use100k_v_h;


        }



        //
        openCamera(reqW, reqH, reqFps, m_bFront);

        Looper.loop();
        free();
    }

    private void free()
    {
        Log.d("looper quit");
        if (this.tmpFrameBuffer_0.bDone_createEnv)
        {
            this.tmpFrameBuffer_0.deleteEnv();
            this.tmpFrameBuffer_0.bDone_createEnv = false;
        }
        if (this.tmpFrameBuffer_o.bDone_createEnv)
        {
            this.tmpFrameBuffer_o.deleteEnv();
            this.tmpFrameBuffer_o.bDone_createEnv = false;
        }
//
        if (this.tmpFrameBuffer_rotate.bDone_createEnv)
        {
            this.tmpFrameBuffer_rotate.deleteEnv();
            this.tmpFrameBuffer_rotate.bDone_createEnv = false;
        }

//
        if (this.tmpFrameBuffer_int.bDone_createEnv)
        {
            this.tmpFrameBuffer_int.deleteEnv();
            this.tmpFrameBuffer_int.bDone_createEnv = false;
        }

//
        if (this.m_simpleTex_0_int != null)
        {
            this.m_simpleTex_0_int.destroy(); this.m_simpleTex_0_int = null;
        }
        if (this.m_simpleTex_rotate != null)
        {
            this.m_simpleTex_rotate.destroy(); this.m_simpleTex_rotate = null;
        }
        if (this.m_simpleTex_o != null)
        {
            this.m_simpleTex_o.destroy(); this.m_simpleTex_o = null;
        }

//
        releaseCamera();
        releaseGl();
        if (mEglCore != null)
        {
            mEglCore.release();
        }

//synchronized(mStartLock)
        {
            mReady = false;
        }

//
        mThreadEnd = true;

//
        showInfo_open.qyFuncs.showInfo_open(0, null, "renderThread.run ends");
    }
    public int notifyCameraOpen() {

        //
        if (!this.m_bUse_camera1) {
            mCameraPreviewWidth = myCamera.mVideoSize.getWidth();
            mCameraPreviewHeight = myCamera.mVideoSize.getHeight();
            int thousandFps = 30000;
            //
            sendCameraParams(mMainHandler, mCameraPreviewWidth, mCameraPreviewHeight,
                    thousandFps / 1000.0f);
        }

        //
        mReady = true;

        //
        return 0;
    }

    /**
     * Waits until the render thread is ready to receive messages.
     * <p>
     * Call from the UI thread.
     */
    public int waitUntilReady() {
        //synchronized(mStartLock)
        {
            while (!mReady) {
                //
                if (mThreadEnd) return -1;

                //
                try {
                    //mStartLock.wait();
                    tmpFunc.qyFuncs.mySleep(1);
                } catch (Exception ie) { /* not expected */ }
            }
        }

        return 0;
    }

    /**
     * Shuts everything down.
     */
    public void shutdown() {
        Log.d("shutdown");
        Looper.myLooper().quit();
    }

    /**
     * Returns the render thread's Handler.  This may be called from any thread.
     */
    public camera_RenderHandler getHandler() {
        return mHandler;
    }

    /**
     * Handles the surface-created callback from SurfaceView.  Prepares GLES and the Surface.
     */
    public void surfaceAvailable(SurfaceTexture st, boolean newSurface) {
        //Surface surface = holder.getSurface();
        //mWindowSurface = new WindowSurface(mEglCore, surface, false);
        mWindowSurface = new WindowSurface(mEglCore, st);
        mWindowSurface.makeCurrent();

        //  create and configure the SurfaceTexture, which will receive frames from the
        // camera.  We set the textured rect's program to render from it.
        mTexProgram = new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT);
        int textureId = mTexProgram.createTextureObject();
        mCameraTexture = new SurfaceTexture(textureId);
        mRect.setTexture(textureId);

        mCameraTexture.setDefaultBufferSize(this.mCameraPreviewWidth, this.mCameraPreviewHeight);

        //if (!newSurface)
        {
            // This Surface was established on a previous run, so no surfaceChanged()
            // message is forthcoming.  Finish the surface setup now.
            //
            // We could also just call this unconditionally, and perhaps do an unnecessary
            // bit of reallocating if a surface-changed message arrives.
            mWindowSurfaceWidth = mWindowSurface.getWidth();
            mWindowSurfaceHeight = mWindowSurface.getHeight();
            finishSurfaceSetup();
        }
        mCameraTexture.setOnFrameAvailableListener(this);
    }

    /**
     * Releases most of the GL resources we currently hold (anything allocated by
     * surfaceAvailable()).
     * <p>
     * Does not release EglCore.
     */
    private void releaseGl() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;

        if (pQyMc!=null && !pQyMc.b_isHarmony) {
            GlUtil.checkGlError("releaseGl start");
        }

        //
        if (mWindowSurface != null) {
            mWindowSurface.release();
            mWindowSurface = null;
        }
        if (mTexProgram != null) {
            mTexProgram.release();
            mTexProgram = null;
        }
        if (pQyMc!=null && !pQyMc.b_isHarmony) {
            GlUtil.checkGlError("releaseGl done");
        }

        //
        if (mEglCore != null) {
            mEglCore.makeNothingCurrent();
        }
    }

    /**
     * Handles the surfaceChanged message.
     * <p>
     * We always receive surfaceChanged() after surfaceCreated(), but surfaceAvailable()
     * could also be called with a Surface created on a previous run.  So this may not
     * be called.
     */
    public void surfaceChanged(int width, int height) {
        Log.d("RenderThread surfaceChanged " + width + "x" + height);

        mWindowSurfaceWidth = width;
        mWindowSurfaceHeight = height;
        finishSurfaceSetup();
    }

    /**
     * Handles the surfaceDestroyed message.
     */
    public void surfaceDestroyed() {
        // In practice this never appears to be called -- the activity is always paused
        // before the surface is destroyed.  In theory it could be called though.
        Log.d("RenderThread surfaceDestroyed");
        releaseGl();
    }

    /**
     * Sets up anything that depends on the window size.
     * <p>
     * Open the camera (to set mCameraAspectRatio) before calling here.
     */
    private void finishSurfaceSetup() {
        int width = mWindowSurfaceWidth;
        int height = mWindowSurfaceHeight;
        Log.d("finishSurfaceSetup size=" + width + "x" + height +
                " camera=" + mCameraPreviewWidth + "x" + mCameraPreviewHeight);

        // Use full window.
        GLES20.glViewport(0, 0, width, height);

        // Simple orthographic projection, with (0,0) in lower-left corner.
        Matrix.orthoM(mDisplayProjectionMatrix, 0, 0, width, 0, height, -1, 1);
        //
        Matrix.orthoM(mDisplayProjectionMatrix_invert, 0, 0, width, height, 0, -1, 1);


        // Default position is center of screen.
        mPosX = width / 2.0f;
        mPosY = height / 2.0f;

        updateGeometry();

        // Ready to go, start the camera.
        Log.d("starting camera preview");
        if (m_bUse_camera1) {
            try {
                mCamera1.setPreviewTexture(mCameraTexture);
            } catch (IOException ioe) {
                Log.e(ioe.getMessage());
                return;
            }
            mCamera1.startPreview();
        } else {
            Log.d("not finished, startPreview");
            myCamera.startPreview(mCameraTexture);
        }
    }

    /**
     * Updates the geometry of mRect, based on the size of the window and the current
     * values set by the UI.
     */
    private void updateGeometry() {
        int width = mWindowSurfaceWidth;
        int height = mWindowSurfaceHeight;

        int smallDim = Math.min(width, height);
        // max scale is a bit larger than the screen, so we can show over-size.
        float scaled = smallDim * (mSizePercent / 100.0f) * 1.25f;
        float cameraAspect = (float) mCameraPreviewWidth / mCameraPreviewHeight;
        int newWidth = Math.round(scaled * cameraAspect);
        int newHeight = Math.round(scaled);

        float zoomFactor = 1.0f - (mZoomPercent / 100.0f);
        int rotAngle = Math.round(360 * (mRotatePercent / 100.0f));

        newWidth = width;
        newHeight = height;

        mRect.setScale(newWidth, newHeight);
        mRect.setPosition(mPosX, mPosY);
        mRect.setRotation(rotAngle);
        mRectDrawable.setScale(zoomFactor);

        sendRectSize(mMainHandler, newWidth, newHeight);
        sendZoomArea(mMainHandler, Math.round(mCameraPreviewWidth * zoomFactor),
                Math.round(mCameraPreviewHeight * zoomFactor));
        sendRotateDeg(mMainHandler, rotAngle);
    }

    int cnt;

    // SurfaceTexture.OnFrameAvailableListener; runs on arbitrary thread
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        //
        boolean bDbg = false;
        String str;

        cnt++;

        //
        mHandler.sendFrameAvailable();
    }

    public int m_loopCtrl  =  0;
    /**
     * Handles incoming frame of data from the camera.
     */
    public int frameAvailable() {
        int iErr = -1;

        boolean bDbg = false;
        String str;
        boolean bDumpTex = false;

        //
        mCameraTexture.updateTexImage();

        //
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            Log.e("frameAvailable pQyMc == null");
            return iErr;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, this.miIndex_sharedObj);
        if (pSharedObj == null) return iErr;

        //
        m_loopCtrl  ++  ;
        if ( true )  {
            if  (  (  m_loopCtrl  %  100  )  ==  0  ) {
                Log.d("qy", "camera_render.frameAvail "  +  m_loopCtrl);
            }
        }

        //
        int w = this.mWindowSurfaceWidth;// mCameraPreviewWidth;
        int h = this.mWindowSurfaceHeight;// mCameraPreviewHeight;

        //
        //qyFuncs_Droid.myGLClearError();
        boolean bRtt = true;

        //
        if (!this.tmpFrameBuffer_0.bDone_createEnv) {
            this.tmpFrameBuffer_0.createEnv(w, h, "camera 0");
            this.tmpFrameBuffer_0.bDone_createEnv = true;
        }

        //
        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {

            //
            if (bRtt) {
                this.tmpFrameBuffer_0.bindFrameBufferInfo();
                //
                GLES20.glViewport(0, 0, w, h);

            }

            //
            draw(bRtt);

            //
            if (bRtt) {
                this.tmpFrameBuffer_0.unbindFrameBufferInfo();
            }

            //

            //
            if (bDumpTex) {
                Bitmap bmp = saveTexture.qyFuncs_Droid.texture2Bmp(this.tmpFrameBuffer_0.mTexture[0], w, h);
                guiShare.GuiShare.pf_dumpBmp.call(bmp, "tmpTex_0");
                bmp.recycle();
            }

            //
            if (bRtt) {
                int wh = Math.max(w, h);

                if (!this.tmpFrameBuffer_int.bDone_createEnv) {
                    this.tmpFrameBuffer_int.createEnv(wh, wh, "camera.int");
                    this.tmpFrameBuffer_int.bDone_createEnv = true;
                }

                this.tmpFrameBuffer_int.bindFrameBufferInfo();
                GLES20.glViewport(0, 0, wh, wh);

                //
                Context context = MyApplication.getContext();
                int texId = tmpFrameBuffer_0.mTexture[0];

                //
                //
                if (this.m_simpleTex_0_int == null) {
                    this.m_simpleTex_0_int = new SimpleTexture(context, texId, false, "camera0->int");
                    //
                    this.m_simpleTex_0_int.resize(wh, wh, 0);
                }
                //
                this.m_simpleTex_0_int.draw(0);


                //
                this.tmpFrameBuffer_int.unbindFrameBufferInfo();

                //
                if (bDumpTex) {
                    Bitmap bmp = saveTexture.qyFuncs_Droid.texture2Bmp(this.tmpFrameBuffer_int.mTexture[0], wh, wh);
                    guiShare.GuiShare.pf_dumpBmp.call(bmp, "tmpTex_int");
                    bmp.recycle();
                }

                //////////////////////////////
                //
                if (!this.tmpFrameBuffer_rotate.bDone_createEnv) {
                    this.tmpFrameBuffer_rotate.createEnv(wh, wh, "camera.rotate");
                    this.tmpFrameBuffer_rotate.bDone_createEnv = true;
                }

                this.tmpFrameBuffer_rotate.bindFrameBufferInfo();
                GLES20.glViewport(0, 0, wh, wh);

                //
                texId = tmpFrameBuffer_int.mTexture[0];

                //
                int tex_o_w = 0;
                int tex_o_h = 0;
                //
                {
                    if (this.m_simpleTex_rotate == null) {
                        this.m_simpleTex_rotate = new SimpleTexture(context, texId, false, "camera.rotate");
                    }
                    //
                    int angle_z = 90;
                    //
                    int ii = this.myCamera.mSensorOrientation;

                    Triplet<Integer, Integer, Integer> o_info = myrecycleFunc.qyFuncs_Droid.get_tex_o_info(myCamera.mSensorOrientation, m_surfaceOrientation, mCameraPreviewWidth, mCameraPreviewHeight);
                    angle_z = o_info.getValue0();
                    tex_o_w = o_info.getValue1();
                    tex_o_h = o_info.getValue2();

                    this.m_simpleTex_rotate.resize(wh, wh, angle_z);
                    this.m_simpleTex_rotate.draw(angle_z);

                }

                //
                this.tmpFrameBuffer_rotate.unbindFrameBufferInfo();


                if (bDumpTex) {
                    Bitmap bmp = saveTexture.qyFuncs_Droid.texture2Bmp(this.tmpFrameBuffer_rotate.mTexture[0], wh, wh);
                    guiShare.GuiShare.pf_dumpBmp.call(bmp, "tmpTex_rotate");
                    bmp.recycle();
                }

                if (0 != syncObj.sync("", pSharedObj.var.hMutex_syncEncFrame,"render.frameAvail.833")) {
                    str = String.format("camera.frameAvail: sync failed");
                    Log.e(str);
                    return iErr;
                }

                //
                int o_w = tex_o_w;// mCameraPreviewHeight;
                int o_h = tex_o_h;// mCameraPreviewWidth;

                //
                if (!this.tmpFrameBuffer_o.bDone_createEnv) {
                    this.tmpFrameBuffer_o.createEnv(o_w, o_h, "camera.o");
                    this.tmpFrameBuffer_o.bDone_createEnv = true;
                }

                this.tmpFrameBuffer_o.bindFrameBufferInfo();
                GLES20.glViewport(0, 0, o_w, o_h);

                //
                texId = tmpFrameBuffer_rotate.mTexture[0];

                //
                //

                if (this.m_simpleTex_o == null) {
                    this.m_simpleTex_o = new SimpleTexture(context, texId, false, "camera->o");
                }
                //
                this.m_simpleTex_o.resize(o_w, o_h, 0);
                this.m_simpleTex_o.draw(0);
                //
                this.tmpFrameBuffer_o.unbindFrameBufferInfo();


                if (bDumpTex) {
                    Bitmap bmp = saveTexture.qyFuncs_Droid.texture2Bmp(this.tmpFrameBuffer_o.mTexture[0], o_w, o_h);
                    guiShare.GuiShare.pf_dumpBmp.call(bmp, "tmpTex_o");
                    bmp.recycle();
                }


                //
                //
                if (pSharedObj.var.m_o_texId != this.tmpFrameBuffer_o.mTexture[0]) {
                    pSharedObj.var.m_o_texId = this.tmpFrameBuffer_o.mTexture[0];
                }
                if (pSharedObj.var.m_o_iW != tex_o_w) {
                    pSharedObj.var.m_o_iW = tex_o_w;
                }
                if (pSharedObj.var.m_o_iH != tex_o_h) {
                    pSharedObj.var.m_o_iH = tex_o_h;
                }


                //
            }
            syncObj.Dispose();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.getLocalizedMessage());
            return iErr;
        }

        //
        pSharedObj.var.bFrameAvail = true;
        tmpFunc.qyFuncs.mySetEvent(pSharedObj.var.hEvt_notifyFrameAvail);

        //
        //this.tmpFrameBuffer.deleteEnv();

        //
        iErr = 0;


        return iErr;

    }

    /**
     * Draws the scene and submits the buffer.
     */
    public void draw(boolean bRtt) {
        GlUtil.checkGlError("draw start");

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //
        if (!bRtt) {
            mRect.draw(mTexProgram, mDisplayProjectionMatrix);
        } else {
            //mRect.draw(mTexProgram, mDisplayProjectionMatrix_invert);
            mRect.draw(mTexProgram, mDisplayProjectionMatrix);
        }
        //
        boolean tmp_bRet;
        tmp_bRet  =  mWindowSurface.swapBuffers();
        if  (  !tmp_bRet)  {
            //
            Log.d("qy","camera_RenderThread: swapBuffer failed");
            //
            ctxQyMc.CCtxQyMc  pQyMc = g_pQyMc;
            if  (  pQyMc!=null) {
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
                if  (  pProcInfo!=null)  {
                    pProcInfo.m_n_swapBuffersFailed  ++  ;
                }
            }

        }
        //
        GlUtil.checkGlError("draw done");
    }

    public void setZoom(int percent) {
        mZoomPercent = percent;
        updateGeometry();
    }

    public void setSize(int percent) {
        mSizePercent = percent;
        updateGeometry();
    }

    public void setRotate(int percent) {
        mRotatePercent = percent;
        updateGeometry();
    }

    public void setPosition(int x, int y) {
        mPosX = x;
        mPosY = mWindowSurfaceHeight - y;   // GLES is upside-down
        updateGeometry();
    }

    private void openCamera(int desiredWidth, int desiredHeight, int desiredFps, boolean bFront) {
        if (m_bUse_camera1) {
            Log.d("openCamera1(desiredWidth, desir");
            openCamera1(desiredWidth, desiredHeight, desiredFps);
            //
            this.notifyCameraOpen();
        } else {
            Log.d("openCamera(desiredWidth, desiredHeight");
            try {
                this.myCamera = new MyCamera(this.mHandler);
                //
                this.myCamera.openCamera(desiredWidth, desiredHeight, bFront);
            } catch (Exception e) {
                Log.d("camera_RenderThread.openCamera, exception");
            }
        }
    }

    private void releaseCamera() {
        if (m_bUse_camera1) {
            Log.d(" releaseCamera1();");
            releaseCamera1();
        } else {
            if (this.myCamera != null) {
                Log.d(".closeCamera();");

                myCamera.closeCamera();
                myCamera = null;
            }
        }
    }


    /**
     * Opens a camera, and attempts to establish preview mode at the specified width
     * and height with a fixed frame rate.
     * <p>
     * Sets mCameraPreviewWidth / mCameraPreviewHeight.
     */
    private void openCamera1(int desiredWidth, int desiredHeight, int desiredFps) {
        if (mCamera1 != null) {
            Log.e("camera already initialized");
            return;
        }


        Camera.CameraInfo info = new Camera.CameraInfo();

        // Try to find a front-facing camera (e.g. for videoconferencing).
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCamera1 = Camera.open(i);
                break;
            }
        }
        if (mCamera1 == null) {
            Log.d("No front-facing camera found; opening default");
            mCamera1 = Camera.open();    // opens first back-facing camera
        }
        if (mCamera1 == null) {
            Log.e("Unable to open camera");
            return;
        }

        Camera.Parameters parms = mCamera1.getParameters();

        CameraUtils.choosePreviewSize(parms, desiredWidth, desiredHeight);

        // Try to set the frame rate to a constant value.
        int thousandFps = CameraUtils.chooseFixedPreviewFps(parms, desiredFps * 1000);

        // Give the camera a hint that we're recording video.  This can have a big
        // impact on frame rate.
        parms.setRecordingHint(true);

        mCamera1.setParameters(parms);

        int[] fpsRange = new int[2];
        Camera.Size mCameraPreviewSize = parms.getPreviewSize();
        parms.getPreviewFpsRange(fpsRange);
        String previewFacts = mCameraPreviewSize.width + "x" + mCameraPreviewSize.height;
        if (fpsRange[0] == fpsRange[1]) {
            previewFacts += " @" + (fpsRange[0] / 1000.0) + "fps";
        } else {
            previewFacts += " @[" + (fpsRange[0] / 1000.0) +
                    " - " + (fpsRange[1] / 1000.0) + "] fps";
        }
        Log.i("Camera config: " + previewFacts);

        mCameraPreviewWidth = mCameraPreviewSize.width;
        mCameraPreviewHeight = mCameraPreviewSize.height;
        sendCameraParams(mMainHandler, mCameraPreviewWidth, mCameraPreviewHeight,
                thousandFps / 1000.0f);
    }

    /**
     * Stops camera preview, and releases the camera to the system.
     */
    private void releaseCamera1() {
        if (mCamera1 != null) {
            mCamera1.stopPreview();
            mCamera1.release();
            mCamera1 = null;
            Log.d("releaseCamera -- done");
        }
    }

    //
    public void sendCameraParams(Handler mainHandler, int width, int height, float fps) {
        // The right way to do this is to bundle them up into an Object.  The lazy
        // way is to send two messages.
        mainHandler.sendMessage(mainHandler.obtainMessage(MSG_SEND_CAMERA_PARAMS0, width, height));
        mainHandler.sendMessage(mainHandler.obtainMessage(MSG_SEND_CAMERA_PARAMS1, (int) (fps * 1000), 0));
    }

    /**
     * Sends the updated rect size to the main thread.
     * <p>
     * Call from render thread.
     */
    public void sendRectSize(Handler mainHandler, int width, int height) {
        mainHandler.sendMessage(mainHandler.obtainMessage(MSG_SEND_RECT_SIZE, width, height));
    }

    /**
     * Sends the updated zoom area to the main thread.
     * <p>
     * Call from render thread.
     */
    public void sendZoomArea(Handler mainHandler, int width, int height) {
        mainHandler.sendMessage(mainHandler.obtainMessage(MSG_SEND_ZOOM_AREA, width, height));
    }

    /**
     * Sends the updated zoom area to the main thread.
     * <p>
     * Call from render thread.
     */
    public void sendRotateDeg(Handler mainHandler, int rot) {
        mainHandler.sendMessage(mainHandler.obtainMessage(MSG_SEND_ROTATE_DEG, rot, 0));
    }


    //


    //

    /**
     * Handler for RenderThread.  Used for messages sent from the UI thread to the render thread.
     * <p>
     * The Object is created on the render thread, and the various "send" methods are called
     * from the UI thread.
     */
    public class camera_RenderHandler extends Handler {
        String TAG = "camera_RenderHandler";

        //
        private final static int MSG_SURFACE_AVAILABLE = 0;
        private final static int MSG_SURFACE_CHANGED = 1;
        private final static int MSG_SURFACE_DESTROYED = 2;
        private final static int MSG_SHUTDOWN = 3;
        private final static int MSG_FRAME_AVAILABLE = 4;
        private final static int MSG_ZOOM_VALUE = 5;
        private final static int MSG_SIZE_VALUE = 6;
        private final static int MSG_ROTATE_VALUE = 7;
        private final static int MSG_POSITION = 8;
        private final static int MSG_REDRAW = 9;

        // This shouldn't need to be a weak , since we'll go away when the Looper quits,
        // but no real harm in it.
        private camera_RenderThread mWeakRenderThread;

        /**
         * Call from render thread.
         */
        public camera_RenderHandler(camera_RenderThread rt) {
            mWeakRenderThread = rt;// new WeakReference<RenderThread>(rt);
        }

        /**
         * Sends the "surface available" message.  If the surface was newly created (i.e.
         * this is called from surfaceCreated()), set newSurface to true.  If this is
         * being called during Activity startup for a previously-existing surface, set
         * newSurface to false.
         * <p>
         * The flag tells the caller whether or not it can expect a surfaceChanged() to
         * arrive very soon.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceAvailable(SurfaceTexture holder, boolean newSurface) {
            sendMessage(obtainMessage(MSG_SURFACE_AVAILABLE,
                    newSurface ? 1 : 0, 0, holder));
        }

        /**
         * Sends the "surface changed" message, forwarding what we got from the SurfaceHolder.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceChanged(int format, int width,
                                       int height) {
            // ignore format
            sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height));
        }

        /**
         * Sends the "shutdown" message, which tells the render thread to halt.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceDestroyed() {
            sendMessage(obtainMessage(MSG_SURFACE_DESTROYED));
        }

        /**
         * Sends the "shutdown" message, which tells the render thread to halt.
         * <p>
         * Call from UI thread.
         */
        public void sendShutdown() {
            sendMessage(obtainMessage(MSG_SHUTDOWN));
        }

        /**
         * Sends the "frame available" message.
         * <p>
         * Call from UI thread.
         */
        public void sendFrameAvailable() {
            sendMessage(obtainMessage(MSG_FRAME_AVAILABLE));
        }

        /**
         * Sends the "zoom value" message.  "progress" should be 0-100.
         * <p>
         * Call from UI thread.
         */
        public void sendZoomValue(int progress) {
            sendMessage(obtainMessage(MSG_ZOOM_VALUE, progress, 0));
        }

        /**
         * Sends the "size value" message.  "progress" should be 0-100.
         * <p>
         * Call from UI thread.
         */
        public void sendSizeValue(int progress) {
            sendMessage(obtainMessage(MSG_SIZE_VALUE, progress, 0));
        }

        /**
         * Sends the "rotate value" message.  "progress" should be 0-100.
         * <p>
         * Call from UI thread.
         */
        public void sendRotateValue(int progress) {
            sendMessage(obtainMessage(MSG_ROTATE_VALUE, progress, 0));
        }

        /**
         * Sends the "position" message.  Sets the position of the rect.
         * <p>
         * Call from UI thread.
         */
        public void sendPosition(int x, int y) {
            sendMessage(obtainMessage(MSG_POSITION, x, y));
        }

        /**
         * Sends the "redraw" message.  Forces an immediate redraw.
         * <p>
         * Call from UI thread.
         */
        public void sendRedraw() {
            sendMessage(obtainMessage(MSG_REDRAW));
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            //Log.d("RenderHandler [" + this + "]: what=" + what);

            camera_RenderThread renderThread = mWeakRenderThread;
            if (renderThread == null) {
                Log.w("RenderHandler.handleMessage: weak  is null");
                return;
            }

//            Log.d("camera_RenderThread: msg.what: " + msg.what + " msg.arg1: " + msg.arg1);
            switch (what) {
                case MSG_SURFACE_AVAILABLE:
                    renderThread.surfaceAvailable((SurfaceTexture) msg.obj, msg.arg1 != 0);
                    break;
                case MSG_SURFACE_CHANGED:
                    renderThread.surfaceChanged(msg.arg1, msg.arg2);
                    break;
                case MSG_SURFACE_DESTROYED:
                    renderThread.surfaceDestroyed();
                    break;
                case MSG_SHUTDOWN:
                    renderThread.shutdown();
                    break;
                case MSG_FRAME_AVAILABLE:
                    int ret = renderThread.frameAvailable();
                    if(ret < 0)
                    {
                        sendShutdown();
                    }
                    break;
                case MSG_ZOOM_VALUE:
                    renderThread.setZoom(msg.arg1);
                    break;
                case MSG_SIZE_VALUE:
                    renderThread.setSize(msg.arg1);
                    break;
                case MSG_ROTATE_VALUE:
                    renderThread.setRotate(msg.arg1);
                    break;
                case MSG_POSITION:
                    renderThread.setPosition(msg.arg1, msg.arg2);
                    break;
                case MSG_REDRAW:
                    boolean bRtt = true;
                    //
                    renderThread.draw(bRtt);
                    break;

                case guiShare.Consts.CONST_msg_cameraOpen:
                    renderThread.notifyCameraOpen();
                    break;

                default:
                    Log.e("unknown message " + what);
            }
        }
    }

}


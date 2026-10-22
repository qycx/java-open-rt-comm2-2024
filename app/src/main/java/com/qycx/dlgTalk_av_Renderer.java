package com.qycx;


import android.app.Fragment;
import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.qycx.base.EventResetMode;
import com.qycx.base.EventWaitHandle;
import com.qycx.base.ctxQyMc;
import com.qycx.base.guiShare;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qySyncObj;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.talkDraw;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;
import com.qycx.gleslibrary.EglCore;
import com.qycx.gleslibrary.WindowSurface;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class dlgTalk_av_Renderer extends Thread implements TextureView.SurfaceTextureListener {
    Fragment mOwner;
    int miFgType;

    //public Object mLock = new Object();
    public Semaphore hMutex = new Semaphore(1);
    private SurfaceTexture mSurfaceTexture;
    private EglCore mEglCore;
    private boolean mDone;

    //
    EventWaitHandle mhEvt_toNotifyTexAvail;

    //
    public dlgTalk_av_Renderer(Fragment fg, int iFgType) {
        super("Renderer333");
        mOwner = fg;
        miFgType = iFgType;
        //
        return;
    }

    public int init() {
        int iErr = -1;
        String pName = "notifiTexAvail" + qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
        mhEvt_toNotifyTexAvail = new EventWaitHandle(false, EventResetMode.AutoReset, pName);


        iErr = 0;
        return iErr;
    }

    public int exit() {
        if (null != this.mhEvt_toNotifyTexAvail) {
            this.mhEvt_toNotifyTexAvail.Close();
            this.mhEvt_toNotifyTexAvail = null;
        }

        return 0;
    }

    @Override
    public void run() {
        while (true) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) {
                Log.e("dlgTalk_av_Renderer::run pQyMc == null");
                break;
            }
            SurfaceTexture surfaceTexture = null;

            //synchronized(mLock)
            boolean bReady = true;
            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                if (syncObj.sync("", this.hMutex,"dlgTalk_av_Render.83") != 0) {
                    return;
                }
                if (!mDone && (surfaceTexture = mSurfaceTexture) == null) {
                    bReady = false;
                }
                if (mDone) break;
            } catch (Exception e) {
                Log.e(e.getMessage());
                return;
            }
            if (!bReady) {
                int ms = 100;
                tmpFunc.qyFuncs.myWaitOne(this.mhEvt_toNotifyTexAvail, ms);
                continue;
            }
            EGLContext eglContext_shared = null;
            mEglCore = new EglCore(eglContext_shared, EglCore.FLAG_TRY_GLES3);
            WindowSurface windowSurface = new WindowSurface(mEglCore, surfaceTexture);
            windowSurface.makeCurrent();
            doAnimation(windowSurface);
            windowSurface.release();
            mEglCore.release();

            if (!Fragment_dlgTalk_av2.sReleaseInCallback) {
                //Log.i(TAG, "Releasing SurfaceTexture in renderer thread");
                surfaceTexture.release();
            }
        }
    }

    //


    //
    private void doAnimation(WindowSurface eglSurface) {
        int BLOCK_WIDTH = 80;
        int BLOCK_SPEED = 2;
        float clearColor = 0.0f;
        int xpos = -BLOCK_WIDTH / 2;
        int xdir = BLOCK_SPEED;
        int width = eglSurface.getWidth();
        int height = eglSurface.getHeight();

        String str;

        //
        talkDraw.CTalkDraw myDraw = new talkDraw.CTalkDraw(mOwner, miFgType, eglSurface);

        //
        Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();

        //
        if (0 != myDraw.init()) {
            if ( myDraw!=null)
            {
                myDraw.exit();
            }
            return;
        }
        if (0 != myDraw.onSize(width, height)) {
            if ( myDraw!=null)
            {
                myDraw.exit();
            }
            return;
        }

        //
        while (true) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) {
                Log.e("doAnimation pQyMc == null");
                if ( myDraw!=null)
                {
                    myDraw.exit();
                }
                break;
            }
            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                if (syncObj.sync("", this.hMutex,"Render.doAnimation.163") != 0) {
                    if ( myDraw!=null)
                    {
                        myDraw.exit();
                    }
                    return;
                }
                SurfaceTexture surfaceTexture = mSurfaceTexture;
                if (mDone || surfaceTexture == null) {
                    if ( myDraw!=null)
                    {
                        myDraw.exit();
                    }
                    return;
                }
            } catch (Exception e) {
                Log.e(e.getMessage());
                if ( myDraw!=null)
                {
                    myDraw.exit();
                }
                return;
            }
            //
            if (dlgTalk_av.m_bSurfaceTextureSizeChanged) {
                //
                dlgTalk_av.m_bSurfaceTextureSizeChanged = false;

                Log.d("doAnimation: bSurfaceTextureSizeChanged is true,so needRestart");

                //
                dlgTalk_av.m_bDlgTalk_av_readyFor_talkDrawRestart = false;
                dlgTalk_av.m_bTalkDraw_needRestart = true;
                //
                if (miFgType == guiShare.Consts.CONST_iFgType_dlgTalk_av2) {
                    Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) mOwner;
                    fg.mMainHandler.send_MSG_talkDraw_needRestart();
                }
            }
            if (dlgTalk_av.m_bTalkDraw_needRestart) {
                if (dlgTalk_av.m_bDlgTalk_av_readyFor_talkDrawRestart) {
                    Log.d("doAnimation will stop: m_bDlgTalk_av_readyFor_talkDrawRestart is true");
                    dlgTalk_av.m_bDlgTalk_av_readyFor_talkDrawRestart = false;
                    dlgTalk_av.m_bTalkDraw_needRestart = false;
                    if ( myDraw!=null)
                    {
                        myDraw.exit();
                    }
                    return;
                }
            }

            myDraw.draw();
            boolean tmp_bRet;
            tmp_bRet  =  eglSurface.swapBuffers();
            if  (  !tmp_bRet)  {
                Log.d("qy","doAnimation: swapBuffers failed");
            }
            //
            clearColor += 0.015625f;
            if (clearColor > 1.0f) {
                clearColor = 0.0f;

            }
            xpos += xdir;
            if (xpos <= -BLOCK_WIDTH / 2 || xpos >= width - BLOCK_WIDTH / 2) {
                xdir = -xdir;
            }
        }
    }

    public void halt() {
        Log.d("dlgTalk_av_renderer.halt");
        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
            if (syncObj.sync("", this.hMutex,"render.halt.232") != 0) return;
            mDone = true;
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
    }

    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        //throw new NotImplementedException();
        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
            if (syncObj.sync("", this.hMutex,"render.onSurfaceTexAvail.242") != 0) {
                return;
            }
            mSurfaceTexture = surface;
        } catch (Exception e) {
            Log.e(e.getMessage());
            return;
        }

        tmpFunc.qyFuncs.mySetEvent(this.mhEvt_toNotifyTexAvail);

        //

        return;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
            if (syncObj.sync("", this.hMutex,"onSurfaceTexDestroyed.261") != 0) {
                return Fragment_dlgTalk_av2.sReleaseInCallback;
            }
            mSurfaceTexture = null;
        } catch (Exception e) {
            Log.e(e.getMessage());
            return false;
        }
        return Fragment_dlgTalk_av2.sReleaseInCallback;
    }

    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) this.mOwner.getActivity();
        dlgTalk_av.m_bSurfaceTextureSizeChanged = true;
    }

    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }
}

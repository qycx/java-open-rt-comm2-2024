package com.qycx.base;

import static com.qycx.base.guiShare.Consts.CONST_fz_me;
import static com.qycx.base.guiShare.Consts.CONST_fz_peer;
import static com.qycx.base.isCliGl.qmcD3dFunc.qyFuncs_Droid.get_pt_far_mats;
import static com.qycx.base.isCliGl.qmcD3dFunc.qyFuncs_Droid.projectTouchToWorld;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;

import com.qycx.R;
import com.qycx.base.isCliGl.qisD3d_open;
import com.qycx.base.isCliGl.qmcD3dFunc;
import com.qycx.base.util.Log;
import com.qycx.gleslibrary.EglCore;
import com.qycx.gleslibrary.WindowSurface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class qmcDrawTmpl {

    public static class CQmcDrawTmpl {
        //
        public Fragment mOwner;
        public int miFgType;
        //
        public WindowSurface mWindowSurface;
        //
        public EglCore mEglCore_shared;
        //
        public boolean bQuit;


        //
        static String VERTEX_SHADER =
                "uniform mat4 mMatrix;" +
                        "uniform mat4 vMatrix;" +
                        "uniform mat4 pMatrix;" +
                        "attribute vec4 vPosition;" +
                        "attribute vec2 a_texCoord;" +
                        "varying vec2 v_texCoord;" +
                        "void main(){" +
                        " gl_Position=pMatrix * vMatrix * mMatrix * vPosition;" +
                        " v_texCoord=a_texCoord;" +
                        "}";

        static String FRAGMENT_SHADER =
                "precision mediump float;" +
                        "varying vec2 v_texCoord;" +
                        "uniform sampler2D s_texture;" +
                        " void main(){" +
                        "  gl_FragColor = texture2D(s_texture,v_texCoord);" +
                        "}";


        static float[] VERTEX = {
                //1,1,0,
                //-4.966f,15.19f,-99.898f,
                -3.27258f, 15.18121f, -100.2942f,
                //-1, 1, 0,
                //-50,15,-86,
                -52.47561f, 15.18121f, -85.53316f,
                //-1, -1, 0,
                //-44,-64,-64,
                -45.90787f, -64.36166f, -63.64063f,
                //1, -1, 0,
                //1.6f,-64,-78,
                3.29516f, -64.36166f, -78.40168f,


        };

        //
        public short[] VERTEX_INDEX = {0, 1, 2, 0, 2, 3};

        //
        public float[] TEX_VERTEX = {
                1, 0,
                0, 0,
                0, 1,
                1, 1,
        };

        //
        public FloatBuffer mVertexBuffer;
        //
        public ShortBuffer mVertexIndexBuffer;

        FloatBuffer mTexVertexBuffer;

        //
        public qisD3d_open.QIS_mats mMats=new qisD3d_open.QIS_mats();

        //
        public float[] mMMatrix = new float[16];
        public float[] mVMatrix = new float[16];
        public float[] mPMatrix = new float[16];

        int mProgram;
        public int mPositionHandle;

        public int mMatrixHandle;
        public int vMatrixHandle;
        public int pMatrixHandle;

        public int mTexCoordHandle;
        public int mTexSmaplerHandle;
        //
        public int mTexName_default;
        public int mTexName_default_iW;
        public int mTexName_default_iH;

        //
        public long m_lastFps_tickCnt;
        public int m_lastFps_loop_ctrl;
        public int m_loop_ctrl;


        //


        //
        public static class TextureLoader extends Thread {
            EGLContext mTextureContext_shared;
            EGLConfig mEglConfig;
            EGLDisplay mDisplay;
            //
            Context mAndroidContext;
            //
            public CQmcDrawTmpl mMyDraw;
            //
            public int m_texId_0;
            public int m_texId_1;

            boolean m_bInited;

            //
            public int iTestNo;
            public String m_tag;

            public tmpJniFunc.YUV_RGB_info m_yuvRgbInfo;

            //
            EglCore mEglCore;

            //
            public Bitmap m_bitmap;
            //
            public
            TextureLoader(EglCore eglCore_shared, Context androidContext, CQmcDrawTmpl myDraw)
            {
                mDisplay = eglCore_shared.mEGLDisplay;
                this.mEglConfig = eglCore_shared.mEGLConfig;
                mTextureContext_shared = eglCore_shared.mEGLContext;
                this.mAndroidContext = androidContext;
                mMyDraw = myDraw;
                this.m_yuvRgbInfo = new tmpJniFunc.YUV_RGB_info();
                int ii = 0;
            }

            //
            public int init ()
            {
                int iErr = -1;

                //
                mEglCore = new EglCore(mTextureContext_shared, EglCore.FLAG_TRY_GLES3);

                //
                int[] textureHandle = new int[2];
                GLES20.glGenTextures(2, textureHandle, 0);
                int tmpiRet = GLES20.glGetError();
                if (textureHandle[0] == 0 || textureHandle[1] == 0) {
                    //Log.d(TAG,"TextureLoader.Run failed, recycleGenTextures failed");
                    return iErr;
                }
                m_texId_0 = textureHandle[0];
                m_texId_1 = textureHandle[1];

                this.m_bInited = true;

                iErr = 0;

                return iErr;
            }

            public void exit ()
            {
                if (m_texId_0 != 0 || m_texId_1 != 0) {
                    int[] textureHandle = new int[2];
                    textureHandle[0] = m_texId_0;
                    textureHandle[1] = m_texId_1;
                    GLES20.glDeleteTextures(2, textureHandle, 0);
                }

                //
                if (mEglCore != null) {
                    mEglCore.release();
                }

                //
                m_bInited = false;

                return;
            }

            @Override
            public void run()
            {
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
                TextureLoader tl = this;
                int iTestNo = tl.iTestNo;
                String str;

                //
                if (!m_bInited) {
                    return;
                }

                //qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj();
                {
                    //if (syncObj.sync("", pProcInfo.hMutex_syncQdcUnit) != 0) return iErr;

                    //
                    int[] pbufferAttribs = {EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_TEXTURE_TARGET,
                            EGL14.EGL_NO_TEXTURE, EGL14.EGL_TEXTURE_FORMAT, EGL14.EGL_NO_TEXTURE,
                            EGL14.EGL_NONE};
                    EGLSurface localSurface = EGL14.eglCreatePbufferSurface(mDisplay, mEglConfig, pbufferAttribs, 0);
                    if (localSurface == null || localSurface == EGL14.EGL_NO_SURFACE) {
                        if(m_bitmap!=null)
                        {
                            m_bitmap.recycle();
                            m_bitmap = null;
                        }
                        return;
                    }
                    //boolean tmpbRet = EGL14.eglMakeCurrent(mDisplay, localSurface, localSurface, mTextureContext);
                    boolean tmpbRet = EGL14.eglMakeCurrent(mDisplay, localSurface, localSurface, this.mEglCore.mEGLContext);
                    if (!tmpbRet) {
                        str = GLUtils.getEGLErrorString(EGL14.eglGetError());
                        Log.e(str);
                        if(m_bitmap!=null)
                        {
                            m_bitmap.recycle();
                            m_bitmap = null;
                        }
                        return;
                    }
                    if (tmpbRet) {
                        int ii = iTestNo;
                    }
                }
                
                this.fillTex_run();

                //


                if (m_bitmap != null) {
                    m_bitmap.recycle();
                    m_bitmap = null;
                }

                return;

            }

            public void halt ()
            {
                boolean bDbg = false;
                String str;

                //Thread h = this;
                Object h = this;
                tmpFunc_Droid.qyFuncs_Droid.waitForThread1.call(h, 10000);
                exit();
            }

            int loadTexture ( int resourceId, int texId)
            {
                int iErr = -1;


                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;

                Bitmap bitmap = BitmapFactory.decodeResource(mAndroidContext.getResources(), resourceId, options);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                bitmap.recycle();


                iErr = 0;

                return iErr == 0 ? texId : 0;
            }


            public int loadTexture ( byte[] rgb1, int w, int h, int texId)
            {
                return -1;
            }


            public int loadTexture ( int w, int h, int texId)
            {
                int iErr = -1;
                //Bitmap bitmap = null;


                if (w == 0 || h == 0) return -1;


                //BitmapFactory.Options options = new BitmapFactory.Options();
                //options.InScaled = false;

                try {
                    if (null != m_bitmap) {
                        if (m_bitmap.getWidth() != w || m_bitmap.getHeight() != h) {
                            m_bitmap.recycle();
                            m_bitmap = null;
                        }
                    }
                    if (null == m_bitmap) {
                        m_bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                        //
                        //Log.d(TAG,"loadTexture: createBitmap");
                    }
                } catch (Exception e) {
                    return iErr;
                }
                //
                int ii = 0;
                //
                ByteBuffer b = this.m_yuvRgbInfo.mbbRgb32; // ByteBuffer.wrap(rgb);
                b.rewind();

                m_bitmap.copyPixelsFromBuffer(b);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, m_bitmap, 0);
                int tmpiRet;
                tmpiRet = GLES20.glGetError();
                if (tmpiRet != 0) {
                    ii = 0;
                }
                iErr = 0;
                return texId;
            }


            //
            public int fillTex_run ()
            {
                return 0;
            }

        }

        //
        public CQmcDrawTmpl(Fragment owner, int iFgType, WindowSurface windowSurface) {
            //
            mOwner = owner;
            miFgType = iFgType;
            //
            mWindowSurface = windowSurface;


            //
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(VERTEX);
            mVertexBuffer.position(0);


            //
            mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer()
                    .put(VERTEX_INDEX);
            mVertexIndexBuffer.position(0);
            
            mTexVertexBuffer = ByteBuffer.allocateDirect(TEX_VERTEX.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(TEX_VERTEX);
            mTexVertexBuffer.position(0);

            Matrix.setIdentityM(mMMatrix, 0);
            Matrix.setLookAtM(mVMatrix, 0, 3, 3, 10, 0, 0, 0, 0, 1, 0);

            if(mMats != null)
            {
                qisD3d_open.qyFuncs_Droid.matrix2MY_MATRIX(mMMatrix, this.mMats.matWorld);
                qisD3d_open.qyFuncs_Droid.matrix2MY_MATRIX(mVMatrix, this.mMats.matView);
            }
        }

        static int loadShader(int type, String shaderCode) {
            int shader = GLES20.glCreateShader(type);
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            return shader;
        }

        static Bitmap getBitmap(Activity context, int vectorDrawableId) {
            Bitmap bitmap = null;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                try {
                    Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
                    bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    vectorDrawable.draw(canvas);
                } catch (Exception e) {
                    int ii = 0;
                }
            } else {
                bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
            }
            return bitmap;

        }

        //
        public int init() {            //
            mEglCore_shared = new EglCore(mWindowSurface.mEglCore.mEGLContext, EglCore.FLAG_TRY_GLES3);
       
            mProgram = GLES20.glCreateProgram();
            int vertexShaner = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            GLES20.glAttachShader(mProgram, vertexShaner);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);

            GLES20.glUseProgram(mProgram);

            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
            //
            mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "mMatrix");
            vMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
            pMatrixHandle = GLES20.glGetUniformLocation(mProgram, "pMatrix");
            //
            mTexSmaplerHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");

            //GLES20.glEnableVertexAttribArray(mPositionHandle);
            //GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer_me);
            //
            GLES20.glEnableVertexAttribArray(mTexCoordHandle);
            GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mTexVertexBuffer);

            int[] texNames = new int[1];
            GLES20.glGenTextures(1, texNames, 0);
            mTexName_default = texNames[0];
            //
            Bitmap bitmap = getBitmap(mOwner.getActivity(), R.drawable.bj852x480);
            //
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexName_default);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_REPEAT);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            
            this.mTexName_default_iW = bitmap.getWidth();
            this.mTexName_default_iH = bitmap.getHeight();
            bitmap.recycle();
            return 0;
        }


        //
        public void exit() {
            this.bQuit = true;
            mEglCore_shared.release();
            GLES20.glDeleteTextures(1, new int[]{mTexName_default}, 0);
        }

        public int onSize(int w, int h) {
            if (w == 0 || h == 0) return -1;

            GLES20.glViewport(0, 0, w, h);

            Matrix.perspectiveM(mPMatrix, 0, 45, (float) w / h, 0.1f, 100.0f);

            qisD3d_open.qyFuncs_Droid.matrix2MY_MATRIX(mPMatrix, this.mMats.matProj);

            this.mMats.vp.Width = w;
            this.mMats.vp.Height = h;

            //
            int width = w;
            int height = h;
            //
            float fz = CONST_fz_peer;// = 1f - 0.000001f;
            //
            com.qycx.base.Vector3 v3 = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, 0, 0);
            com.qycx.base.Vector3 v31 = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, width, 0);
            com.qycx.base.Vector3 v32 = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, width, height);
            com.qycx.base.Vector3 v33 = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, 0, height);

            //
            com.qycx.base.Vector4 v4=new Vector4(0,0,0,0);
            get_pt_far_mats(null, 0, 0, this.mMats, fz, v4);


            //
            fz = CONST_fz_me;// 1f - 0.000002f;

            //
            com.qycx.base.Vector3 v3_me = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, width * 2 / 3, height * 2 / 3);
            com.qycx.base.Vector3 v31_me = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, width, height * 2 / 3);
            com.qycx.base.Vector3 v32_me = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, width, height);
            com.qycx.base.Vector3 v33_me = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, width * 2 / 3, height);


            //
            com.qycx.base.Vector3 v3_me_l = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, 0, height * 2 / 3);
            com.qycx.base.Vector3 v31_me_l = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, width / 3, height * 2 / 3);
            com.qycx.base.Vector3 v32_me_l = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, width / 3, height);
            com.qycx.base.Vector3 v33_me_l = projectTouchToWorld(width, height, fz, mVMatrix, mPMatrix, 0, height);


            //
            return 0;
        }


        public void draw() {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            //
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMMatrix, 0);
            GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, mVMatrix, 0);
            GLES20.glUniformMatrix4fv(pMatrixHandle, 1, false, mPMatrix, 0);

            //
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

            //
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexName_default);

            //
            GLES20.glUniform1i(mTexSmaplerHandle, 0);

            //
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                    GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            this.m_loop_ctrl++;
            long curTickCnt = (int) tmpFunc.qyFuncs.myGetTickCount(null);
            int iElapseInMs = Math.abs((int) (this.m_lastFps_tickCnt - curTickCnt));
            if (iElapseInMs >= 1000) {
                float fps = (this.m_loop_ctrl - m_lastFps_loop_ctrl) * 1000.0f / (float) iElapseInMs;
                this.m_lastFps_loop_ctrl = m_loop_ctrl;
                this.m_lastFps_tickCnt = curTickCnt;
            }
        }
    }
}

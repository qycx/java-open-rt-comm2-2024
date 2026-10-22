package com.qycx.base;

import static com.qycx.base.guiShare.Consts.CONST_fz_me;
import static com.qycx.base.guiShare.Consts.CONST_fz_peer;
import static com.qycx.base.guiShare.Consts.CONST_iFgType_dlgTalk_av2;
import static com.qycx.base.qyDefs.Consts.CONST_objType_imGrp;
import static com.qycx.base.qyDefs.Consts.CONST_objType_mosaicStream_resource;
import static com.qycx.base.qyDefs.Consts.CONST_objType_mosaicStream_video;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_chkTalkDraw;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.mySleep;

import android.app.Fragment;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import com.qycx.Activity_dlgTalk;
import com.qycx.Activity_dlgTalk_av;
import com.qycx.MainActivity;
import com.qycx.MyApplication;
import com.qycx.base.isCliGl.t1Frm;
import com.qycx.base.util.Log;
import com.qycx.gleslibrary.EglCore;
import com.qycx.gleslibrary.WindowSurface;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class talkDraw {
    public static class CTalkDraw extends qmcDrawTmpl.CQmcDrawTmpl {
        //
        public dlgTalkProc.TALK_drawLayout m_drawLayout;
        public int tn_modified_drawLayout;

        //
        //
        TalkTextureLoader mTextureLoader_me;
        TalkTextureLoader mTextureLoader_peer;

        //
        public static int me_texId;
        public static SurfaceTexture me_st;


        //
        public static class Talk_tex {
            public int m_textureId_0;
            public int m_textureId_1;
            //
            public int m_cur_textureId;
        }

        ;
        public Talk_tex tex_me, tex_peer;

        public FloatBuffer mVertexBuffer_me;

        //
        public float[] VERTEX_me = {
                //1,1,0,
                //-3.27258f,15.18121f,-100.2942f,                
                1.105982f, -37.78366f, -85.60948f,
                //-1, 1, 0,
                //-52.47561f,15.18121f,-85.53316f,
                -15.27864f, -37.78366f, -80.69443f,
                //-1, -1, 0,
                //-45.90787f,-64.36166f,-63.64063f,
                -13.08971f, -64.29439f, -73.39761f,
                //1, -1, 0,
                //3.29516f,-64.36166f,-78.40168f,
                3.294913f, -64.29439f, -78.31304f,


        };


        //
        public t1Frm.T1_frm tf_me;
        public t1Frm.T1_frm tf_peer;
        public t1Frm.T1_frm centerSurfaceViewPortRect;

        //
        public static class TalkTextureLoader extends TextureLoader {
            //
            public dlgTalkProc.TALK_drawMem m_drawMem;
            //
            public boolean m_bMe;

            public TalkTextureLoader(EglCore eglCore_shared, Context androidContext, CTalkDraw myDraw, boolean bMe) {
                super(eglCore_shared, androidContext, myDraw);
                this.m_bMe = bMe;
            }

            //
            public boolean bDrawModified() {
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
                CTalkDraw mDraw = (CTalkDraw) this.mMyDraw;

                //
                //Fragment_dlgTalk_av fg = (Fragment_dlgTalk_av)mDraw.mOwner;
                Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) mDraw.mOwner.getActivity();
                Activity_dlgTalk hTalk = (Activity_dlgTalk) hTalk_av.mOwner_hTalk.get();
                dlgTalkProc.DLG_TALK_var m_var = hTalk.m_var;

                if (m_bMe) {
                    return (m_drawMem.tn_modified != m_var.m_drawLayout.param.me.tn_modified);
                }

                //                  
                return (m_drawMem.tn_modified != m_var.m_drawLayout.param.peer.tn_modified);
            }

            //
            public int updateDrawMem() {
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
                CTalkDraw mDraw = (CTalkDraw) this.mMyDraw;

                //
                Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) mDraw.mOwner.getActivity();
                Activity_dlgTalk hTalk = hTalk_av.mOwner_hTalk.get();
                dlgTalkProc.DLG_TALK_var m_var = hTalk.m_var;

                if (m_bMe) {
                    if (m_drawMem.tn_modified != m_var.m_drawLayout.param.me.tn_modified) {
                        m_drawMem.tn_modified = m_var.m_drawLayout.param.me.tn_modified;
                        m_drawMem.param = m_var.m_drawLayout.param.me.param;
                    }
                } else {
                    if (m_drawMem.tn_modified != m_var.m_drawLayout.param.peer.tn_modified) {
                        m_drawMem.tn_modified = m_var.m_drawLayout.param.peer.tn_modified;
                        m_drawMem.param = m_var.m_drawLayout.param.peer.param;
                    }
                }
                return 0;
            }

            @Override
            public int fillTex_run() {
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
                CTalkDraw mDraw = (CTalkDraw) this.mMyDraw;

                boolean bDbg = false;
                String str;

                //
                Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) mDraw.mOwner.getActivity();
                Activity_dlgTalk hTalk = hTalk_av.mOwner_hTalk.get();
                dlgTalkProc.DLG_TALK_var m_var = hTalk.m_var;

                //
                //mDraw.tn_modified_drawLayout = m_var.tn_modified_drawLayout;
                //mDraw.m_drawLayout = m_var.m_drawLayout;

                if (!this.m_bMe) {
                    int ii = 0;

                }


                //
                for (; !mDraw.bQuit; ) {
                    //
                    updateDrawMem();

                    //
                    if (m_drawMem.param.playerId.uiTranNo_player == 0) {
                        mySleep(200);
                        continue;
                    }
                    qmcCommFunc_is_open.QY_PLAYER player = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, m_drawMem.param.playerId.index_player);
                    if (player == null) {
                        mySleep(200);
                        continue;
                    }
                    //
                    if (player.video.vh.bih.biWidth == 0 || player.video.vh.bih.biHeight == 0) {
                        //
                        str = String.format("fillTex_run: %s, bih err %sx%s", this.m_tag, player.video.vh.bih.biWidth, player.video.vh.bih.biHeight);
                        Log.d("fillTex_run: " + str);
                        mySleep(200);
                        continue;
                    }
                    //
                    ttl_readPkt.qyFuncs_Droid.ttl_readPkt(this);

                }


                //
                if (m_bMe) {
                    int ii = 0;
                }

                return 0;
            }

            //
            public int tmpFillTex(byte[] rgb, int w, int h) {
                return 0;
            }


            //
            public int tmpFillTex(int w, int h) {
                CTalkDraw myDraw = (CTalkDraw) this.mMyDraw;
                //byte[] rgb_array = rgb;
                boolean bDbg = false;
                if (!m_bMe) {
                    //bDbg = true;
                }

                //
                Talk_tex myTex = null;
                if (m_bMe) {
                    myTex = myDraw.tex_me;
                } else {
                    myTex = myDraw.tex_peer;
                }

                //                  
                if (myTex.m_textureId_0 == 0) {
                    int texId = loadTexture(w, h, m_texId_0);
                    if (texId != 0) {
                        myTex.m_textureId_0 = texId;
                    }
                } else if (myTex.m_textureId_1 == 0) {
                    int texId = loadTexture(w, h, m_texId_1);
                    if (texId != 0) {
                        myTex.m_textureId_1 = texId;
                    }
                }
                return 0;

            }


        }


        //
        public CTalkDraw(Fragment owner, int iFgType, WindowSurface windowSurface) {
            super(owner, iFgType, windowSurface);
            //base(owner, windowSurface);

            int ii = 0;


        }

        @Override
        public int init() {
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) {
                Log.e("CTalkDraw.init: pQyMc is null");
                return iErr;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            if (0 != super.init()) return -1;

            //
            //
            mVertexBuffer_me = ByteBuffer.allocateDirect(VERTEX_me.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(VERTEX_me);
            mVertexBuffer_me.position(0);


            //
            this.tf_me = new t1Frm.T1_frm();

            //            
            qyDefs_open.CAP_RECT capRect = new qyDefs_open.CAP_RECT();
            capRect.iX = 0;
            capRect.iY = this.mWindowSurface.getHeight() * 2 / 3;
            capRect.iW = (int) this.mWindowSurface.getWidth() / 3;
            capRect.iH = (int) this.mWindowSurface.getHeight() / 3;

            if (capRect.iH == 0) return iErr;
            float fAspectRatio_img = capRect.iW / (float) capRect.iH;
            if (0 != tf_me.T1_frm_init(null, capRect, fAspectRatio_img)) {
                return iErr;
            }

            //
            this.tf_peer = new t1Frm.T1_frm();

            //
            //            
            capRect.iX = 0;
            capRect.iY = 0;
            capRect.iW = (int) this.mWindowSurface.getWidth();
            capRect.iH = (int) this.mWindowSurface.getHeight();

            if (capRect.iH == 0) return iErr;
            fAspectRatio_img = capRect.iW / (float) capRect.iH;
            if (0 != tf_peer.T1_frm_init(null, capRect, fAspectRatio_img)) {
                return iErr;
            }

            this.tex_me = new Talk_tex();
            this.tex_peer = new Talk_tex();

            //
            if (pProcInfo.cfg.ucb_USE_Fragment_dlgTalk_av) {
                //
                mTextureLoader_me = new TalkTextureLoader(this.mEglCore_shared, mOwner.getActivity(), this, true);
                if (mTextureLoader_me.init() != 0) {
                    return iErr;
                }
                mTextureLoader_me.iTestNo = 1;
                mTextureLoader_me.m_tag = "tex_me";
                mTextureLoader_me.start();


                mTextureLoader_peer = new TalkTextureLoader(this.mEglCore_shared, mOwner.getActivity(), this, false);
                if (mTextureLoader_peer.init() != 0) {
                    return iErr;
                }
                mTextureLoader_peer.iTestNo = 2;
                mTextureLoader_peer.m_tag = "tex_peer";
                mTextureLoader_peer.start();
            }

            //
            {
                int[] textureHandle = new int[1];
                GLES20.glGenTextures(1, textureHandle, 0);
                int tmpiRet = GLES20.glGetError();
                if (textureHandle[0] == 0) {
                    //Log.d(TAG,"TextureLoader.Run failed, recycleGenTextures failed");
                    return iErr;
                }
                //
                me_texId = textureHandle[0];
            }
            me_st = new SurfaceTexture(me_texId);
            if (me_st == null) return iErr;

            //
            Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();
            if (hTalk_av != null) {
                hTalk_av.save_dlgTalk_share_eglContext(this.mEglCore_shared.mEGLContext, "talkDraw.init");

                //
                switch (miFgType) {
                    case CONST_iFgType_dlgTalk_av2: {
                        //Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2)mOwner;
                        hTalk_av.mOwner_hTalk.get().gui_PostMessage(CONST_qyWm_postComm, CONST_qyWmParam_chkTalkDraw, 0);
                    }

                    break;
                    default:
                        break;
                }
            }

            iErr = 0;


            return iErr;
        }


        @Override
        public void exit() {
            this.bQuit = true;
            //
            if (mTextureLoader_me != null) {
                mTextureLoader_me.halt();
                mTextureLoader_me = null;
            }
            if (mTextureLoader_peer != null) {
                mTextureLoader_peer.halt();
                mTextureLoader_peer = null;
            }
            //
            Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();
            if (hTalk_av != null) {
                hTalk_av.save_dlgTalk_share_eglContext(null, "talkDraw.exit");
            }

            //
            if (me_st != null) {
                me_st.release();
                me_st = null;
            }
            if (me_texId != 0) {
                int[] textureHandle = new int[1];
                textureHandle[0] = me_texId;
                GLES20.glDeleteTextures(1, textureHandle, 0);
                //
                me_texId = 0;
            }

            //
            if (this.tf_me != null) {
                tf_me.T1_frm_exit(null);
                tf_me = null;
            }
            //
            if (this.tf_peer != null) {
                tf_peer.T1_frm_exit(null);
                tf_peer = null;
            }

            //
            super.exit();
            //
            return;
        }


        @Override
        public int onSize(int w, int h) {
            int iErr = -1;

            super.onSize(w, h);


            //
            iErr = 0;

            return iErr;
        }


        //
        public ctxQmc.TMP_tex_info get_tti_peer(ctxQmc.CCtxQmc pProcInfo, Activity_dlgTalk hTalk, ctxQmc.TMP_tex_info tti) {
            dlgTalkProc.DLG_TALK_var m_var = hTalk.get_pm_var();

            //
            ctxQmc.TMP_texes texes = pProcInfo.av.texes;
            if (texes == null) return null;

            ctxQmc.TMP_task_texes ttt = texes.taskAvTexes;
            int i;
            int usCnt_activeMems = ttt.usCnt_activeMems;
            if (usCnt_activeMems > ttt.tti_peers.length) return null;
            if (m_var.addr.uiObjType == CONST_objType_imGrp) {
                for (i = 0; i < usCnt_activeMems; i++) {
                    if (ttt.tti_peers[i].uiObjType == CONST_objType_mosaicStream_video
                            || ttt.tti_peers[i].uiObjType == CONST_objType_mosaicStream_resource) {
                        return ttt.tti_peers[i];
                    }
                }
            } else {
                // 暂时不处理屏幕共享
                for (i = 0; i < usCnt_activeMems; i++) {
                    if (ttt.tti_peers[i].idInfo.ui64Id == m_var.addr.idInfo.ui64Id
                            && ttt.tti_peers[i].uiObjType == 0) {
                        return ttt.tti_peers[i];
                    }
                }

            }

            return null;
        }


        //
        public void myPreRender(Object pCtx) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            int width = this.mWindowSurface.getWidth();
            int height = this.mWindowSurface.getHeight();

            if (height == 0) return;

            Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();
            if (dlgTalk_av == null) return;

            Activity_dlgTalk dlgTalk = (Activity_dlgTalk) dlgTalk_av.mOwner_hTalk.get();
            dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;
            if (!m_var.av.taskInfo.bTaskExists) return;

            handleSharedObjRender(pCtx, pProcInfo, m_var, width, height);
            handleMainRender(pCtx, mainActivity, width, height, m_var);

            return;
        }

        private void handleSharedObjRender(Object pCtx, ctxQmc.CCtxQmc pProcInfo, dlgTalkProc.DLG_TALK_var m_var, int width, int height) {
            if (m_var.av.ucbGot_iIndex_sharedObj_localAv) {
                qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, m_var.av.iIndex_sharedObj_localAv);
                if (pSharedObj == null) return;

                int img_w = pSharedObj.var.m_o_iW;
                int img_h = pSharedObj.var.m_o_iH;

                if (img_h != 0) {
                    float fAspectRatio_img = img_w / (float) img_h;

                    qyDefs_open.CAP_RECT capRect = new qyDefs_open.CAP_RECT();
                    capRect.iX = width * 2 / 3;
                    capRect.iY = 0;
                    capRect.iW = width / 3;
                    capRect.iH = height / 3;

                    if (needsUpdate(this.tf_me.capRect, capRect, fAspectRatio_img, this.tf_me.fAspectRatio_img)) {
                        qyTimestamp.QY_timestamp ts = new qyTimestamp.QY_timestamp();
                        boolean ucbAutoClip = false;
                        float me_z_1 = CONST_fz_me;

                        if (0 != this.tf_me.T1_frm_set(pCtx, ts, capRect, fAspectRatio_img, ucbAutoClip, false, mMats, me_z_1, "", false)) {
                            return;
                        }
                    }
                }
            }
        }

        private void handleMainRender(Object pCtx, MainActivity mainActivity, int width, int height, dlgTalkProc.DLG_TALK_var m_var) {
            int img_w;
            int img_h;

            if (mainActivity.textureId != 0) {
                img_w = mainActivity.textureIw;
                img_h = mainActivity.textureIh;
            } else {
                img_w = this.mTexName_default_iW;
                img_h = this.mTexName_default_iH;
            }

            if (img_h != 0) {
                float fAspectRatio_img = img_w / (float) img_h;

                qyDefs_open.CAP_RECT capRect = new qyDefs_open.CAP_RECT();
                capRect.iX = 0;
                capRect.iY = 0;
                capRect.iW = width;
                capRect.iH = height;

                if (needsUpdate(this.tf_peer.capRect, capRect, fAspectRatio_img, this.tf_peer.fAspectRatio_img)) {
                    qyTimestamp.QY_timestamp ts = new qyTimestamp.QY_timestamp();
                    boolean ucbAutoClip = false;
                    float me_z_1 = CONST_fz_peer;

                    if (0 != this.tf_peer.T1_frm_set(pCtx, ts, capRect, fAspectRatio_img, ucbAutoClip, false, mMats, me_z_1, "", true)) {
                        return;
                    }
                    m_var.av.hDc_av_rect = tf_peer.centerSurfaceViewPortRect;
                }
            }
        }

        private boolean needsUpdate(qyDefs_open.CAP_RECT oldRect, qyDefs_open.CAP_RECT newRect, float newAspectRatio, float oldAspectRatio) {
            return oldRect.iX != newRect.iX || oldRect.iY != newRect.iY || oldRect.iW != newRect.iW || oldRect.iH != newRect.iH || Math.abs(newAspectRatio - oldAspectRatio) > 0.001;
        }

        @Override
        public void draw() {
            boolean bDbg = false;
            String str;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if(pQyMc == null) return;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            if (pQyMc == null) {
                Log.e("pQyMc == null");
                return;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo == null) return;

            Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) this.mOwner.getActivity();
            Activity_dlgTalk dlgTalk = dlgTalk_av.mOwner_hTalk.get();
            dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;

            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                return;
            }

            myPreRender(null);

            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMMatrix, 0);
            GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, mVMatrix, 0);
            GLES20.glUniformMatrix4fv(pMatrixHandle, 1, false, mPMatrix, 0);

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            boolean bShow_peer = true;

            if (bShow_peer) {
                drawTexture(mainActivity.textureId, this.tf_peer.mVertexBuffer, this.tf_peer.mTexVertexBuffer);
            }

            boolean bShow_me = dlgTalk.m_var.av.taskInfo.bTaskExists && dlgTalk.m_var.av.taskInfo.bShow_localVideo;
            if (bShow_me) {
                if (pProcInfo.cfg.ucb_USE_Fragment_dlgTalk_av) {
                    drawTexture(dlgTalk.m_var.av.ucbGot_iIndex_sharedObj_localAv ? getSharedTexture(pProcInfo, dlgTalk.m_var.av.iIndex_sharedObj_localAv) : 0, mVertexBuffer_me, null);
                } else {
                    drawTexture(dlgTalk.m_var.av.ucbGot_iIndex_sharedObj_localAv ? getSharedTexture(pProcInfo, dlgTalk.m_var.av.iIndex_sharedObj_localAv) : 0, this.tf_me.mVertexBuffer, this.tf_me.mTexVertexBuffer);
                }
            }

            updateFpsCounter();

            return;
        }

        private void drawTexture(int textureId, Buffer vertexBuffer, Buffer texVertexBuffer) {
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);
            if (texVertexBuffer != null) {
                GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texVertexBuffer);
            }

            if (textureId != 0) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            } else {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexName_default);
            }

            GLES20.glUniform1i(mTexSmaplerHandle, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
        }

        private int getSharedTexture(ctxQmc.CCtxQmc pProcInfo, int index) {
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, index);
            return (pSharedObj != null) ? pSharedObj.var.m_o_texId : 0;
        }

        private void updateFpsCounter() {
            this.m_loop_ctrl++;
            long curTickCnt = myGetTickCount(null);
            int iElapseInMs = Math.abs((int) (this.m_lastFps_tickCnt - curTickCnt));
            if (iElapseInMs >= 1000) {
                this.m_lastFps_loop_ctrl = m_loop_ctrl;
                this.m_lastFps_tickCnt = curTickCnt;
            }
        }

    }
}

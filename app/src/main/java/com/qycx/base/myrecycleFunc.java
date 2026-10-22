package com.qycx.base;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.view.Surface;

import com.qycx.base.util.javatuples.Pair;
import com.qycx.base.util.javatuples.Triplet;

public class myrecycleFunc {
    public static class qyFuncs_Droid {
        public static int bmp2Texture(Bitmap bmptexture) {
            int[] mTextures = new int[1];
            GLES20.glGenTextures(1, mTextures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmptexture, 0);//通过bmp 图像填充这个纹理对象
            //bmptexture.recycle();
            return mTextures[0];
        }

        //
        public static Triplet<Integer, Integer, Integer> get_tex_o_info(int cameraSensorOrientation, int m_surfaceOrientation, int previewW, int previewH) {
            boolean bGot = false;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) return new Triplet<>(0, 0, 0);
            int angle_z = 0;
            if (pQyMc.fake_bFolder) {
                if (cameraSensorOrientation == 90) {
                    angle_z = 270;
                    //
                    switch (m_surfaceOrientation) {
                        case Surface.ROTATION_90:
                            angle_z = 180;
                            break;
                        case Surface.ROTATION_180:
                            angle_z = 90;
                            break;
                        case Surface.ROTATION_270:
                            angle_z = 0;
                            break;
                        default:
                            break;
                    }

                }

                //
                bGot = true;
            }


            if (!bGot) {
                //
                if (cameraSensorOrientation == 90) {
                    angle_z = 270;
                    //
                    switch (m_surfaceOrientation) {
                        case Surface.ROTATION_90:
                            angle_z = 0;
                            break;
                        case Surface.ROTATION_180:
                            angle_z = 90;
                            break;
                        case Surface.ROTATION_270:
                            angle_z = 180;
                            break;
                        default:
                            break;
                    }

                } else  //  270
                {
                    //
                    angle_z = 90;
                    //
                    switch (m_surfaceOrientation) {
                        case Surface.ROTATION_90:
                            angle_z = 0;
                            break;
                        case Surface.ROTATION_180:
                            angle_z = 270;
                            break;
                        case Surface.ROTATION_270:
                            angle_z = 180;
                            break;
                        default:
                            break;
                    }
                }
            }

            int tex_o_w, tex_o_h;
            switch (angle_z) {
                case 90:
                case 270:
                    tex_o_w = previewH;
                    tex_o_h = previewW;
                    break;
                default:
                    tex_o_w = previewW;
                    tex_o_h = previewH;
                    break;
            }
            return new Triplet<>(angle_z, tex_o_w, tex_o_h);
        }
    }
}

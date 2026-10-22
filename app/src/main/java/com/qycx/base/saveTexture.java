package com.qycx.base;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;

public class saveTexture
{
	public static class qyFuncs_Droid
{
        public static Bitmap texture2Bmp(int texture, int width, int height)
        {
            int[] frame = new int[1];
            GLES20.glGenFramebuffers(1, frame, 0);
            checkrecycleError("glGenFramebuffers");
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frame[0]);
            checkrecycleError("glBindFramebuffer");
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                    GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture,
                    0);
            checkrecycleError("glFramebufferTexture2D");

            ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE, buffer);
            checkrecycleError("glReadPixels");
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            checkrecycleError("glBindFramebuffer");
            GLES20.glDeleteFramebuffers(1, frame, 0);
            checkrecycleError("glDeleteFramebuffer");
            return bitmap;
        }

        public static void checkrecycleError(String op)
        {
            int error;
            while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
            {
            }
        }
    }



   
}




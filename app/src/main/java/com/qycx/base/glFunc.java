package com.qycx.base;

import android.opengl.GLES20;

public class glFunc
{
	public static class qyFuncs_Droid
{
        public static void myGLClearError()
        {
            while (GLES20.glGetError() != GLES20.GL_NO_ERROR) ;
        }

    }
}

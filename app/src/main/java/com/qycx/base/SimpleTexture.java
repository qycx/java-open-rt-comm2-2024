package com.qycx.base;

import static com.qycx.base.guiShare.Consts.CONST_fz_z1;
import static com.qycx.gleslibrary.GlUtil.checkGlError;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.qycx.R;
import com.qycx.base.isCliGl.qisD3d_open;
import com.qycx.base.isCliGl.qmcD3dFunc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class SimpleTexture extends SampleBase {

    private String TAG = "SimpleTexture";

    private int mProgram = -1;
    private int mVertexShader = -1;
    private int mFragmentShader = -1;

    //
    private FloatBuffer mPosition;
    private FloatBuffer mNormal;
    private FloatBuffer mTexCoord;

    //
    public ShortBuffer mVertexIndexBuffer;


    // texture name
    public int mTextureName;
    boolean bCreated_mTextureName;

    // 4x4 matrix
    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjMatrix = new float[16];

    //
    float[] position = {
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
    };

    //
    public short[] VERTEX_INDEX = {0, 1, 2, 3, 4, 5};


    //
    public SimpleTexture(Context context, int texId, boolean b_test, String hint) {
        String str;

        if (hint == null) hint = "";

        str = String.format("SimpleTexture: SimpleTexture() called, texId %s, %s", texId, hint);

        // create shader program
        int shaderType = SHADER_TYPE_TEXTURE;

        mProgram = createProgram(shaderType, mVertexShader, mFragmentShader);

        // same direction (z direction)
        float[] normal = {
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
        };

        // texture coordinate
        float[] texCoord = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        mPosition = createFloatBuffer(position);
        mNormal = createFloatBuffer(normal);
        mTexCoord = createFloatBuffer(texCoord);

        //
        //
        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(VERTEX_INDEX);
        mVertexIndexBuffer.position(0);


        // init matrix value
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.translateM(mViewMatrix, 0, 0.0f, 0.0f, -2.5f);
        Matrix.setIdentityM(mProjMatrix, 0);

        // intializing the texture
        if (texId > 0) {
            mTextureName = texId;
        } else {
            Bitmap texImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.bj852x480);

            initTexture(texImage);
            texImage.recycle();
            //
            bCreated_mTextureName = true;
        }
    }

    private void initTexture(Bitmap image) {
        int[] name = new int[1];
        GLES20.glGenTextures(1, name, 0);

        //
        boolean bDbg = false;
        String str;
  
        if (name[0] > 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, name[0]);

            // setting parameters
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            // use gl utils to load texture by Bitmap
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);

            // texture binding to 0
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

            mTextureName = name[0];
        } else {
            Log.e(TAG, "Fail to bind texture");
        }

    }

    @Override
    public void resize(int width, int height, float angle_z_unused) {

        GLES20.glViewport(0, 0, width, height);

        // perspective projection
        //Android.Opengl.Matrix.perspectiveM(mProjMatrix, 0, 45.0f, (float)width / (float)height, 1.0f, 10.0f);
        Matrix.perspectiveM(mProjMatrix, 0, 45.0f, (float) width / (float) height, 0.1f, 100.0f);

        //
        Matrix.setIdentityM(mModelMatrix, 0);


        //
        int ix = 0;
        int iy = 0;
        int iw = width;
        int ih = height;

        //
        //
        qisD3d_open.myPOINT realPoint_lt=new qisD3d_open.myPOINT(), realPoint_rt=new qisD3d_open.myPOINT(), realPoint_lb=new qisD3d_open.myPOINT(), realPoint_rb=new qisD3d_open.myPOINT();

        //
        realPoint_lt.x = (int) ix;
        realPoint_lt.y = (int) iy;

        realPoint_rt.x = (int) (ix + iw);
        realPoint_rt.y = (int) iy;

        realPoint_lb.x = (int) ix;
        realPoint_lb.y = (int) (iy + ih);

        realPoint_rb.x = (int) (ix + iw);
        realPoint_rb.y = (int) (iy + ih);


        qisD3d_open.QIS_mats mats = new qisD3d_open.QIS_mats();
        qisD3d_open.qyFuncs_Droid.matrix2MY_MATRIX(this.mModelMatrix, mats.matWorld);
        qisD3d_open.qyFuncs_Droid.matrix2MY_MATRIX(this.mProjMatrix, mats.matProj);
        qisD3d_open.qyFuncs_Droid.matrix2MY_MATRIX(this.mViewMatrix, mats.matView);
        mats.vp.Width = iw;
        mats.vp.Height = ih;

        //
        float z_1 = CONST_fz_z1;
        Object pCtx = null;

        //
        com.qycx.base.Vector4 pt_far=new Vector4(0,0,0,0);
        int index = 0;

        //  get_pt_far(  pCtx,  realPoint_rt.x,  realPoint_rt.y,  &pt_far  );
        qmcD3dFunc.qyFuncs_Droid.get_pt_far_mats(pCtx, realPoint_lt.x, realPoint_lt.y, mats, z_1, pt_far);

        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetX(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetY(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetZ(pt_far);
        index++;

        //  get_pt_far(  pCtx,  realPoint_lt.x,  realPoint_lt.y,  &pt_far  );
        qmcD3dFunc.qyFuncs_Droid.get_pt_far_mats(pCtx, realPoint_lb.x, realPoint_lb.y, mats, z_1, pt_far);

        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetX(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetY(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetZ(pt_far);
        index++;

        //  get_pt_far(  pCtx,  realPoint_lb.x,  realPoint_lb.y,  &pt_far  );
        qmcD3dFunc.qyFuncs_Droid.get_pt_far_mats(pCtx, realPoint_rb.x, realPoint_rb.y, mats, z_1, pt_far);

        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetX(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetY(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetZ(pt_far);
        index++;

        //  get_pt_far(  pCtx,  realPoint_rt.x,  realPoint_rt.y,  &pt_far  );
        qmcD3dFunc.qyFuncs_Droid.get_pt_far_mats(pCtx, realPoint_lt.x, realPoint_lt.y, mats, z_1, pt_far);

        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetX(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetY(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetZ(pt_far);
        index++;

        //  get_pt_far(  pCtx,  realPoint_lb.x,  realPoint_lb.y,  &pt_far  );
        qmcD3dFunc.qyFuncs_Droid.get_pt_far_mats(pCtx, realPoint_rb.x, realPoint_rb.y, mats, z_1, pt_far);

        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetX(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetY(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetZ(pt_far);
        index++;

        //  get_pt_far(  pCtx,  realPoint_lb.x,  realPoint_lb.y,  &pt_far  );
        qmcD3dFunc.qyFuncs_Droid.get_pt_far_mats(pCtx, realPoint_rt.x, realPoint_rt.y, mats, z_1, pt_far);

        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetX(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetY(pt_far);
        index++;
        this.position[index] = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetZ(pt_far);
        index++;

        //
        this.mPosition.put(position);
        this.mPosition.position(0);

    }

    @Override

    public void draw(float angle_z) {

        GLES20.glUseProgram(mProgram);

        // screen clear color
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);

        // clear screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        int aPositionLoc = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glVertexAttribPointer(aPositionLoc, 3, GLES20.GL_FLOAT, false, 0, mPosition);

        int aNormalLoc = GLES20.glGetAttribLocation(mProgram, "aNormal");
        GLES20.glVertexAttribPointer(aNormalLoc, 3, GLES20.GL_FLOAT, false, 0, mNormal);

        int aTexCoordLoc = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        GLES20.glVertexAttribPointer(aTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, mTexCoord);

        // light color
        //GLES20.glUniform3f(GLES20.glGetUniformLocation(mProgram, "uLightColor"), 0.8f, 0.8f, 0.8f);
        GLES20.glUniform3f(GLES20.glGetUniformLocation(mProgram, "uLightColor"), 1.0f, 1.0f, 1.0f);

        // triangle material color
        GLES20.glUniform3f(GLES20.glGetUniformLocation(mProgram, "uMaterialDiffuse"), 1.0f, 1.0f, 1.0f);

        // light dir
        GLES20.glUniform3f(GLES20.glGetUniformLocation(mProgram, "uLightDir"), 1.0f, -1.0f, -1.0f);

        // triangle model matrix
        // no transform (identity matrix)
        // y-axis, 1 degree rotation every frames
        //Matrix.rotateM(mModelMatrix, 0, 1.0f, 0.0f, 1.0f, 0.0f);
        //
        Matrix.rotateM(mModelMatrix, 0, angle_z, 0.0f, 0.0f, 1.0f);

        //
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgram, "uModelMatrix"), 1, false, mModelMatrix, 0);

        // view matrix
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgram, "uViewMatrix"), 1, false, mViewMatrix, 0);

        // projection matrix
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgram, "uProjMatrix"), 1, false, mProjMatrix, 0);

        GLES20.glEnableVertexAttribArray(aPositionLoc);
        GLES20.glEnableVertexAttribArray(aNormalLoc);
        GLES20.glEnableVertexAttribArray(aTexCoordLoc);

        // texture 0
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgram, "sTexture0"), 0);

        // binding texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureName);

        // rendering
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        // binding texture to 0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glDisableVertexAttribArray(aPositionLoc);
        GLES20.glDisableVertexAttribArray(aNormalLoc);
        GLES20.glDisableVertexAttribArray(aTexCoordLoc);

        // starting gl command buffer, non-block
        GLES20.glFlush();

    }


    public void test_draw(int w, int h, float angle_z) {
        //test_draw_shaderType_default(w, h, angle_z);

        resize(w, h, 0);
        test_draw_test(0);


        return;
    }


    public void test_draw_test(float angle_z) {

        GLES20.glUseProgram(mProgram);

        // screen clear color
        GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);

        // clear screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        int aPositionLoc = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glVertexAttribPointer(aPositionLoc, 3, GLES20.GL_FLOAT, false, 0, mPosition);


        int aTexCoordLoc = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        GLES20.glVertexAttribPointer(aTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, mTexCoord);


        // triangle model matrix
        // no transform (identity matrix)
        // y-axis, 1 degree rotation every frames
        //Matrix.rotateM(mModelMatrix, 0, 1.0f, 0.0f, 1.0f, 0.0f);
        //
        Matrix.rotateM(mModelMatrix, 0, angle_z, 0.0f, 0.0f, 1.0f);

        //
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgram, "uModelMatrix"), 1, false, mModelMatrix, 0);

        // view matrix
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgram, "uViewMatrix"), 1, false, mViewMatrix, 0);

        // projection matrix
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgram, "uProjMatrix"), 1, false, mProjMatrix, 0);

        GLES20.glEnableVertexAttribArray(aPositionLoc);
        GLES20.glEnableVertexAttribArray(aTexCoordLoc);

        // texture 0
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgram, "sTexture0"), 0);

        // binding texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureName);

        // rendering
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        //


        // binding texture to 0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glDisableVertexAttribArray(aPositionLoc);
        GLES20.glDisableVertexAttribArray(aTexCoordLoc);

        // starting gl command buffer, non-block
        GLES20.glFlush();

    }


    //
    private void test_draw_shaderType_default(int w, int h, float angle_z) {

        GLES20.glUseProgram(mProgram);

        // screen clear color
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        // clear screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        int aPositionLoc = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glVertexAttribPointer(aPositionLoc, 3, GLES20.GL_FLOAT, false, 0, mPosition);

        //
        // light color
        //GLES20.glUniform3f(GLES20.glGetUniformLocation(mProgram, "uLightColor"), 0.8f, 0.8f, 0.8f);
        int i_uColor = GLES20.glGetUniformLocation(mProgram, "uColor");
        checkGlError("kk");
        GLES20.glUniform3f(i_uColor, 0.0f, 1.0f, 0.0f);
        checkGlError("kk");

        GLES20.glEnableVertexAttribArray(aPositionLoc);

        // rendering
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisableVertexAttribArray(aPositionLoc);

        // starting gl command buffer, non-block
        GLES20.glFlush();

    }


    @Override
    public void destroy() {

        if (mProgram > 0) {
            GLES20.glDeleteProgram(mProgram);
            mProgram = -1;
        }
        if (mVertexShader > 0) {
            GLES20.glDeleteShader(mVertexShader);
            mVertexShader = -1;
        }
        if (mFragmentShader > 0) {
            GLES20.glDeleteShader(mFragmentShader);
            mFragmentShader = -1;
        }
        //
        if (bCreated_mTextureName) {
            if (mTextureName > 0) {                //
                int[] texes = new int[1];
                texes[0] = mTextureName;
                GLES20.glDeleteTextures(1, texes, 0);
                mTextureName = 0;
            }
            //
            bCreated_mTextureName = false;
        }


        //
        mPosition = null;

    }

}



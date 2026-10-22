


package com.qycx.base.isCliGl;


import static com.qycx.base.isCliGl.qisD3d_open.qyFuncs_Droid.MY_MATRIX2matrix;

import android.opengl.GLU;

public class qmcD3dFunc {
    public static class qyFuncs_Droid {
        //
        public static int get_pt_far_mats(Object p0_reserved, float x_screen, float y_screen, qisD3d_open.QIS_mats pMats, float z_1, com.qycx.base.Vector4 pPt_far) {
            int iErr = -1;


            //
            //com.qycx.base.Vector4 pt_in;

            //
            //pt_in = new com.qycx.base.Vector4(x_screen, y_screen, z_1,0);

            //

            int width = pMats.vp.Width;
            int height = pMats.vp.Height;

            float x = x_screen;
            float y = y_screen;
            float fz = z_1;
            float[] modelView = new float[16];
            MY_MATRIX2matrix(pMats.matView, modelView);
            float[] projection = new float[16];
            MY_MATRIX2matrix(pMats.matProj, projection);


            //
            int[] view = new int[]{0, 0, width, height};
            float[] touch_position = new float[4];
            int r = GLU.gluUnProject(x, view[3] - y,
                    fz,//1f - 0.000001f,
                    modelView, 0,
                    projection, 0,
                    view, 0,
                    touch_position, 0);

            touch_position[0] /= touch_position[3];
            touch_position[1] /= touch_position[3];
            touch_position[2] /= touch_position[3];
            touch_position[3] /= touch_position[3];

            pPt_far.X = touch_position[0];
            pPt_far.Y = touch_position[1];
            pPt_far.Z = touch_position[2];
            pPt_far.W = touch_position[3];

                    iErr = 0;

            


            return iErr;

        }


        //
        public static com.qycx.base.Vector3 projectTouchToWorld(int width, int height, float fz, float[] modelView, float[] projection,
                                                                  float x, float y) {
            int[] view = new int[]{0, 0, width, height};
            float[] touch_position = new float[4];
            int r = GLU.gluUnProject(x, view[3] - y,
                    fz,//1f - 0.000001f,
                    modelView, 0,
                    projection, 0,
                    view, 0,
                    touch_position, 0);

            touch_position[0] /= touch_position[3];
            touch_position[1] /= touch_position[3];
            touch_position[2] /= touch_position[3];
            touch_position[3] /= touch_position[3];

            com.qycx.base.Vector3 rv = new com.qycx.base.Vector3(touch_position[0], touch_position[1], touch_position[2]);

            return rv;

        }


        //
        public static int get_pt_screen_mats(Object pCtx, com.qycx.base.Vector4 pPt_far, qisD3d_open.QIS_mats pMats, float pfx_screen, float pfy_screen) {
            int iErr = -1;


            return iErr;

        }

        //


    }
}

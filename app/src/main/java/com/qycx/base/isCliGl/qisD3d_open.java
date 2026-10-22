package com.qycx.base.isCliGl;


public class qisD3d_open {
    //
    public static class myPOINT {
        public int x;
        public int y;
    };

    //
    //  2014/10/26
    public static class MY_MATRIX {
        public float m[]=new float[16];//16

    };


    //  2013/10/21
    public static class QIS_mats {
        public MY_MATRIX matWorld = new MY_MATRIX(), matView=new MY_MATRIX(), matProj=new MY_MATRIX();

        //
        public static class QIS_mats_vp {
            public int Width;
            public int Height;
         }
        public QIS_mats_vp vp=new QIS_mats_vp();
        //D3D11_VIEWPORT vp;
    };


    public static class qyFuncs_Droid {
        public static int MY_MATRIX2matrix(MY_MATRIX p, float[] m) {
            if (m.length != 16) return -1;

            int i;
            for (i = 0; i < m.length; i++) {
                m[i] = p.m[i];
            }
            return 0;
        }

        public static int matrix2MY_MATRIX(float[] m, MY_MATRIX p) {
            if (m.length != 16 || p==null) return -1;
            int i;
            for (i = 0; i < m.length; i++) {
                p.m[i] = m[i];
            }

            return 0;
        }

    }

}




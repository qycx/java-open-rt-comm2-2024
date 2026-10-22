


package com.qycx.base.isCliGl;


import static com.qycx.base.guiShare.Consts.CONST_fz_z1;

import android.graphics.Rect;

import com.qycx.base.Vector4;
import com.qycx.base.capImageProc;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.qyDefs_open;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qyTimestamp;
import com.qycx.base.showInfo_open;
import com.qycx.base.tmpFunc_Droid;
import com.qycx.base.util.Log;
import com.qycx.base.util.javatuples.Octet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class t1Frm {
    public static class T1_frm {
        public int n_components;

        //
        //DWORD										dwModifiedTickCnt_layout;	//  
        public qyTimestamp.QY_timestamp ts_layout;                 //  2017/10/17
        //
        public qyDefs_open.CAP_RECT capRect = new qyDefs_open.CAP_RECT();
        public float fAspectRatio_img;         //  
        //
        public int imgW_org, imgH_org;     //  2016/02/13
        //
        public boolean bUseOrtho;                 //  2013/11/12

        //
        public wallMemCommon.CUSTOMVERTEX_org[] points;//[4];

        //  2016/01/05

        public int[] indices;//[6];


        //
        public float[] mVERTEX;
        //
        public FloatBuffer mVertexBuffer;

        //
        public ShortBuffer mVertexIndexBuffer;

        //
        public float[] mTEX_VERTEX;
        //
        public FloatBuffer mTexVertexBuffer;


        //  2016/02/14
        public boolean bInited;
        public Rect centerSurfaceViewPortRect = new Rect(0, 0, 0, 0);

        /// <summary>
        /// //////////
        /// </summary>
        /// <param name="pCtx"></param>
        /// <param name="pCapRect_screen"></param>
        /// <param name="fAspectRatio_img"></param>
        /// <param name="pFrm"></param>
        /// <returns></returns>
        public float[] VERTEX_t1_r = {
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


        public float[] VERTEX_t1_l = {

                -30.19244f, -37.79223f, -76.21281f,
                -43.63957f, -37.79223f, -72.17892f,
                -41.45151f, -64.29143f, -64.88551f,
                -28.00447f, -64.29143f, -68.91977f,


        };

        //
        //
        public float[] TEX_VERTEX = {
                1, 0,
                0, 0,
                0, 1,
                1, 1,
        };

        //
        public int T1_frm_init(Object pCtx, qyDefs_open.CAP_RECT pCapRect_screen, float fAspectRatio_img) {
            int iErr = -1;
            //HRESULT hr;

            T1_frm pFrm = this;

            //  2016/02/14
            if (pFrm.bInited) {
                Log.d(("T1_frm_init11 failed, bInited is true already"));
                return -1;
            }

            //
            int n_components = 1;           //  撓跺醱

            pFrm.n_components = (int) n_components;

            pFrm.mVERTEX = VERTEX_t1_r;

            pFrm.mVertexBuffer = ByteBuffer.allocateDirect(pFrm.mVERTEX.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(pFrm.mVERTEX);
            pFrm.mVertexBuffer.position(0);

            //
            pFrm.mTEX_VERTEX = TEX_VERTEX;

            //
            mTexVertexBuffer = ByteBuffer.allocateDirect(mTEX_VERTEX.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(mTEX_VERTEX);
            mTexVertexBuffer.position(0);

            //
            pFrm.points = new wallMemCommon.CUSTOMVERTEX_org[4];
            for (int i = 0; i < pFrm.points.length; i++) {
                pFrm.points[i] = new wallMemCommon.CUSTOMVERTEX_org();
            }
            //  create index buffer
            //  create vertex buffer
            pFrm.indices = new int[]
                    {
                            0, 1, 2,
                            0, 2, 3,
                    };


            //  2014/10/18
            boolean ucbAutoClip = true;

            //
            float z_1 = CONST_fz_z1;// 1;
            //
            qyTimestamp.QY_timestamp ts_tmp = new qyTimestamp.QY_timestamp();
            pFrm.bInited = true;

            //
            iErr = 0;

            if (0 != iErr) {
                T1_frm_exit(pCtx);
            }

            return iErr;
        }

        public void T1_frm_exit(Object pCtx) {
            T1_frm pFrm = this;

            //
            pFrm.mVertexBuffer = null;
            pFrm.mVERTEX = null;

            //
            pFrm.mTexVertexBuffer = null;
            pFrm.mTEX_VERTEX = null;


            //
            pFrm.bInited = false;


            return;
        }


        public int T1_frm_set(Object pCtx, qyTimestamp.QY_timestamp pTs_layout, qyDefs_open.CAP_RECT pCapRect_screen, float fAspectRatio_img, boolean ucbAutoClip, boolean bUseOrtho, qisD3d_open.QIS_mats pMats, float z_1, String hint, boolean isCenterViewPort) {
            int iErr = -1;

            boolean bDbg = false;
            String str;

            T1_frm pFrm = this;

            //
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            //
            if (null == hint) hint = "";


            //  2014/11/23
            //if (pProcInfo.cfg.debugStatusInfo.ucbPrint_d3d)
            if (true) {
                str = String.format("T1_frm_set called, fAspectRatio_img %f. %s", fAspectRatio_img, hint);
            }

            if (null == pFrm.mVertexBuffer) {
                //
                str = String.format("T1_frm_set11 failed, pVertexBuffer10 is null");

                return -1;
            }
            if (fAspectRatio_img < 0.000001) return iErr;

            float ix = pCapRect_screen.iX;
            float iy = pCapRect_screen.iY;
            float iw = pCapRect_screen.iW;
            float ih = pCapRect_screen.iH;


            //
            float fu = 1.0f;
            float fv = 1.0f;
            float fEdge_l = 0.0f;
            float fEdge_t = 0.0f;

            //
            if (ucbAutoClip && !bUseOrtho) {
                float tmp_w = ih * fAspectRatio_img;
                if (iw > tmp_w) {
                    fv = ih / (iw / fAspectRatio_img);
                } else {
                    fu = iw / (ih * fAspectRatio_img);
                }
            } else {
                float tmp_w_org = ih * fAspectRatio_img;
                float tmp_h_org = ih;
                //
                int iW_i = 0, iH_i = 0;
                int iX_i = 0, iY_i = 0;
                int iW_dst = 0, iH_dst = 0;
                int iX_dst = 0, iY_dst = 0;

                Octet<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> octet = capImageProc.qyFuncs.new_myGetImageInfo(false, false, true, (int) tmp_w_org,
                        (int) tmp_h_org, (int) ix, (int) iy, (int) iw, (int) ih);
                if (null == octet) {
                    return iErr;
                }
                iW_i = octet.getValue0();
                iH_i = octet.getValue1();
                iX_i = octet.getValue2();
                iY_i = octet.getValue3();
                iX_dst = octet.getValue4();
                iY_dst = octet.getValue5();
                iW_dst = octet.getValue6();
                iH_dst = octet.getValue7();

                ix = iX_dst;
                iy = iY_dst;
                iw = iW_dst;
                ih = iH_dst;
            }
            //
            fEdge_l = (1.0f - fu) / 2.0f;
            fEdge_t = (1.0f - fv) / 2.0f;
            if (fEdge_l < 0.0f) fEdge_l = 0;
            if (fEdge_t < 0.0f) fEdge_t = 0;

            //
            qisD3d_open.myPOINT realPoint_lt = new qisD3d_open.myPOINT(), realPoint_rt = new qisD3d_open.myPOINT(), realPoint_lb = new qisD3d_open.myPOINT(), realPoint_rb = new qisD3d_open.myPOINT();

            //
            realPoint_lt.x = (int) ix;
            realPoint_lt.y = (int) iy;

            realPoint_rt.x = (int) (ix + iw);
            realPoint_rt.y = (int) iy;

            realPoint_lb.x = (int) ix;
            realPoint_lb.y = (int) (iy + ih);

            realPoint_rb.x = (int) (ix + iw);
            realPoint_rb.y = (int) (iy + ih);

            if (isCenterViewPort) {

                centerSurfaceViewPortRect.left = realPoint_lt.x;
                centerSurfaceViewPortRect.top = realPoint_lt.y;
                centerSurfaceViewPortRect.right = realPoint_rb.x;
                centerSurfaceViewPortRect.bottom = realPoint_rb.y;

                Log.d("zzstar视窗坐标: " + centerSurfaceViewPortRect.left + " " + centerSurfaceViewPortRect.top + " " + centerSurfaceViewPortRect.right + " " + centerSurfaceViewPortRect.bottom);


            }

            com.qycx.base.Vector4 pt_far = new Vector4(0, 0, 0, 0);

            int index = 0;
            //Log.d("zzstar A");
            //  get_pt_far(  pCtx,  realPoint_rt.x,  realPoint_rt.y,  &pt_far  );
            qmcD3dFunc.qyFuncs_Droid.get_pt_far_mats(pCtx, realPoint_rt.x, realPoint_rt.y, pMats, z_1, pt_far);


            pFrm.points[0].x = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetX(pt_far);
            pFrm.points[0].y = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetY(pt_far);
            pFrm.points[0].z = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetZ(pt_far);
            pFrm.points[index].u = 1.0f - fEdge_l;  //  1.0f;
            pFrm.points[index].v = fEdge_t;           //  0.0f;
            //Log.d("zzstar B");
            index++;
            //  get_pt_far(  pCtx,  realPoint_lt.x,  realPoint_lt.y,  &pt_far  );
            qmcD3dFunc.qyFuncs_Droid.get_pt_far_mats(pCtx, realPoint_lt.x, realPoint_lt.y, pMats, z_1, pt_far);

            pFrm.points[1].x = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetX(pt_far);
            pFrm.points[1].y = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetY(pt_far);
            pFrm.points[1].z = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetZ(pt_far);
            //Log.d("zzstar 453");

            pFrm.points[index].u = fEdge_l; //  0.0f;
            pFrm.points[index].v = fEdge_t;   //  0.0f;

            index++;
            //  get_pt_far(  pCtx,  realPoint_lb.x,  realPoint_lb.y,  &pt_far  );
            qmcD3dFunc.qyFuncs_Droid.get_pt_far_mats(pCtx, realPoint_lb.x, realPoint_lb.y, pMats, z_1, pt_far);

            //Log.d("zzstar 462");


            pFrm.points[2].x = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetX(pt_far);
            pFrm.points[2].y = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetY(pt_far);
            pFrm.points[2].z = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetZ(pt_far);

            pFrm.points[index].u = fEdge_l;         //  0.0f;
            pFrm.points[index].v = 1.0f - fEdge_t;  //  1.0f;
            //Log.d("zzstar 471");

            //
            index++;
            //  get_pt_far(  pCtx,  realPoint_rb.x,  realPoint_rb.y,  &pt_far  );
            qmcD3dFunc.qyFuncs_Droid.get_pt_far_mats(pCtx, realPoint_rb.x, realPoint_rb.y, pMats, z_1, pt_far);

            pFrm.points[3].x = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetX(pt_far);
            pFrm.points[3].y = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetY(pt_far);
            pFrm.points[3].z = tmpFunc_Droid.qyFuncs_Droid.XMVectorGetZ(pt_far);

            pFrm.points[index].u = 1.0f - fEdge_l;  //  1.0f;
            pFrm.points[index].v = 1.0f - fEdge_t;  //  1.0f;


            int i;


            for (i = 0; i < 4; i++) {
                pFrm.mVERTEX[i * 3 + 0] = pFrm.points[i].x;
                pFrm.mVERTEX[i * 3 + 1] = pFrm.points[i].y;
                pFrm.mVERTEX[i * 3 + 2] = pFrm.points[i].z;
                //
                pFrm.mTEX_VERTEX[i * 2 + 0] = pFrm.points[i].u;
                pFrm.mTEX_VERTEX[i * 2 + 1] = pFrm.points[i].v;
            }
            //Log.d("zzstar 523");

            pFrm.mVertexBuffer.put(pFrm.mVERTEX);
            pFrm.mVertexBuffer.position(0);
            //
            pFrm.mTexVertexBuffer.put(pFrm.mTEX_VERTEX);
            pFrm.mTexVertexBuffer.position(0);
            //Log.d("zzstar 530");


            //  2016/01/03
            //pFrm->dwModifiedTickCnt_layout  =  dwModifiedTickCnt_layout;
            pFrm.ts_layout = pTs_layout;
            //
            //memcpy(pFrm->capRect, pCapRect_screen, sizeof(pFrm->capRect)  );
            pFrm.capRect = pCapRect_screen;
            pFrm.fAspectRatio_img = fAspectRatio_img;
            pFrm.bUseOrtho = bUseOrtho;    //  2013/12/25

            //
            //  2016/01/02. force to setTrans
            //memset(pFrm->trans, 0, sizeof(pFrm->trans)  );
            //Log.d("zzstar 545");

            //
            iErr = 0;


            return iErr;
        }


    }

//

}

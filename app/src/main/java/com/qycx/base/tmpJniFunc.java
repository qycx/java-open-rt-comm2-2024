package com.qycx.base;

import static com.qycx.base.colorSpaceFunc.qyFuncs.M_sizeImage;

import android.util.Log;

import java.nio.ByteBuffer;
import com.qycx.tmpjavalibrary.TmpJniUtil;
public class tmpJniFunc {
    private static final String TAG = tmpJniFunc.class.getSimpleName();
    public static class YUV_RGB_info {
        public ByteBuffer mbbYuv;
        public ByteBuffer mbbRgb32;
    }


    //
    public static class qyFuncs_Droid {

        public static guiShare.PF_i420ToRgb i420ToRgb=(byte[] yuvData, Object yriParam, int rgbBits, boolean bBgr, int width, int height, boolean bFlipImg)-> {
            int iErr = -1;
            int tmpiRet = 0;
            String str;

            if (yriParam == null) {
                return -1;
            }
            YUV_RGB_info yri = (YUV_RGB_info) yriParam;

            //
            int rgb32Size = M_sizeImage(32, width, height);
            int iVal;
            if (yri.mbbRgb32 != null) {
                iVal = yri.mbbRgb32.capacity();
            }
            if (yri.mbbRgb32 == null || yri.mbbRgb32.capacity() < rgb32Size) {
                yri.mbbRgb32 = ByteBuffer.allocateDirect(rgb32Size);
                if (yri.mbbRgb32 == null) return iErr;
                //
                str = String.format("i420ToRgb: bbRgb32 alloc %s", rgb32Size);
                Log.d(TAG,str);
            }
            int yuvSize = M_sizeImage(12, width, height);
            if (yri.mbbYuv == null || yri.mbbYuv.capacity() < yuvSize) {
                yri.mbbYuv = ByteBuffer.allocateDirect(yuvSize);
                if (yri.mbbYuv == null) return iErr;
                //
                str = String.format("i420ToRgb: bbYuv alloc %s", yuvSize);
            }

            byte[] yuvBuf =  yri.mbbYuv.array();
            //long[] tmpsrc = new long[1];
            //tmpsrc[0] = yuvData;
            //System.arraycopy(tmpsrc, 0,yuvBuf, yuvSize);
            System.arraycopy( yuvBuf,0, yuvData,0, (int) yuvSize);

            //
            ByteBuffer bbYuv = yri.mbbYuv;
            ByteBuffer bbRgb = yri.mbbRgb32;

            try {
                //tmpiRet = TmpJniUtil.I420ToRgb(yuv, rgb, rgbBits, true, width, height, bFlipImg);
                tmpiRet = TmpJniUtil.i420ToRgb(bbYuv, bbRgb, rgbBits, bBgr, width, height, bFlipImg);
                //Object
                Object kk;

            } catch (Exception e) {
                return iErr;
            }

            iErr = 0;
            


            // 
            return iErr != 0 ? iErr : tmpiRet;

        };


    }
}

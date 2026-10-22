package com.qycx.tmpjavalibrary;

import java.nio.ByteBuffer;

public class TmpJniUtil {
    static {
        System.loadLibrary("tmpJni-lib");
    }

    //
    public static native int hiFromJni(int[]pResult);
    public static native String sayHello(String kk);
    public static native double[] sumAvg(int[] nums);
    public static native int procByteArray(byte[] bb,int bufSize);

    //
    public static native ByteBuffer allocNativeByteBuffer(int size);
    public static native void freeNativeByteBuffer(ByteBuffer bb);


        //
    public static native int  i420ToRgb(ByteBuffer  yuv, ByteBuffer rgb, int rgbBits, boolean bBgr, int  width, int  height, boolean  bFlipImg  );
    public static native int  rgbToI420(  ByteBuffer  rgb,  int rgbBits, boolean bBgr, ByteBuffer  yuv,  int  width,  int  height,  boolean  bFlipImg  );
    public static native int rgb32To24(ByteBuffer rgb32,boolean bBgr_32, ByteBuffer rgb,boolean bBgr_24, int w,int h);

    //
    public static native int rotateI420(ByteBuffer src, ByteBuffer dst, int width_src, int height_src, int degree,boolean bFlipImg);


}

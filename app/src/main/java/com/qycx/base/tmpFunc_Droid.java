package com.qycx.base;

import static com.qycx.base.tmpFunc.qyFuncs.safeTcsnCpy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.qycx.MyApplication;
import com.qycx.tmpjavalibrary.FileUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class tmpFunc_Droid {
    public static class Thread_android extends Thread {
        qmcFunc.PF_threadProc m_pf;
        Object m_param;

        public Thread_android(qmcFunc.PF_threadProc pf, Object param) {
            m_pf = pf;
            m_param = param;
        }

        @Override
        public void run() {
            m_pf.call(m_param);
        }
            }

    public static class qyFuncs_Droid {

//        public static guiShare.PF_myGetTickCount myGetTickCount_android=()-> (int)(System.currentTimeMillis());
        public static guiShare.PF_myGetTickCount myGetTickCount_android=()-> (int)(System.nanoTime() / 1000000);

        public static guiShare.PF_myGetPid myGetPid=()-> Process.myPid();

        public static guiShare.PF_myGetTid myGetTid=()-> Process.myTid();
        
        //
        public static guiShare.PF_startThread1 startThread1 = (qmcFunc.PF_threadProc pf, Object param)-> {
            Thread_android ph = new Thread_android(pf, param);
            ph.start();
            return ph;
        };

        public static guiShare.PF_waitForThread1 waitForThread1=(Object obj_ph, long dwMilliseconds)-> {
            int dwRet = 0;

            Thread ph = (Thread) obj_ph;
            if (null == ph) return;

            if (ph != null) {

                try {
                    ph.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ph = null;
            }
            obj_ph = ph;
        };

        //

        // 
        public static guiShare.PF_dumpData dumpData=(byte[] data, int w, int h, boolean bAppend, boolean bFileName_haveTime, String pureFileName)-> {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            File path;
            Context context = MyApplication.getContext();
            boolean bDbg = true;

            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String str1 = path.getAbsolutePath();

            //
            str1 = str1 + "/test" + w + "X" + h + ".";// + JavaSystem.CurrentTimeMillis();
            if (bFileName_haveTime) {
                Date now =new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_");
                str1 = str1 + sdf.format(now);
            }

            String fileName = str1 + pureFileName;

            //
            FileUtil.saveBytes(data, bAppend, fileName, bDbg);
            //TmpJniUtil.SaveBytes(data, bAppend, fileName);

            //
            //if (data == null)
            {
                File file;
                Uri uri;

                file = new File(fileName);
                uri = Uri.fromFile(file);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            }

            return 0;

        };

        public static guiShare.PF_dumpData1 dumpData1=(byte[] data, int len, int w, int h, boolean bAppend, boolean bFileName_haveTime, String pureFileName)-> {

            byte[] data_array = null;

            if (data != null && len != 0) {
                data_array = new byte[len];
                System.arraycopy( data, 0,data_array, 0, len);
            }

            dumpData.call(data_array, w, h, bAppend, bFileName_haveTime, pureFileName);

            return 0;
        };

        public static guiShare.PF_dumpRgb32 dumpRgb32=(byte[] data, int w, int h, String pureFileName)-> {

            byte[] rgb32 = data;

            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            
            //Java.Nio.Buffer buffer=Java.Nio.Buffer.
            ByteBuffer b = ByteBuffer.wrap(rgb32);

            bitmap.copyPixelsFromBuffer(b);
            
            File path;
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String str = path.getAbsolutePath();

            //
            //String str1 = str + "/rgb32." + w + "X" + h + "." + JavaSystem.CurrentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_");
            Date now =new Date();
            String str1 = str + "/rgb32." + w + "X" + h + "." + sdf.format(now);

            String fileName = str1 + pureFileName + ".bmp";
            FileUtil.saveBitmap(bitmap, fileName);

            File file;
            Uri uri;
            Context context = MyApplication.getContext();

            file = new File(fileName);
            uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return 0;
        };

        public static guiShare.PF_dumpRgb32_1 dumpRgb32_1=(byte[] data, int w, int h, String pureFileName)-> {
            if (data == null) return -1;

            int rgb32Size = w * h * 4;
            byte[] rgb32 = new byte[rgb32Size];
            System.arraycopy( data,0, rgb32, 0, rgb32Size);
            dumpRgb32.call(rgb32, w, h, pureFileName);
            return 0;
        };

        public static guiShare.PF_dumpBmp dumpBmp=(Object bmp, String pureFileName)-> {
            Bitmap bitmap = (Bitmap) bmp;

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            File path;
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String str1 = path.getAbsolutePath();

            SimpleDateFormat sdf=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_");
            Date now = new Date();
            //str1 = str1 + "/tt." + w + "X" + h + "." + JavaSystem.CurrentTimeMillis();
            str1 = str1 + "/test" + w + "X" + h + "." + sdf.format(now);

            String fileName = str1 + pureFileName + ".bmp";
            File file;
            Uri uri;
            Context context = MyApplication.getContext();
            FileUtil.saveBitmap(bitmap, fileName);

            file = new File(fileName);
            uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return 0;
        };

        private static boolean printRandomLog = false;

        //
        public static guiShare.PF_logd logd=(String tag, String str)-> {
            String str1;
//            SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss SSS");

//            Date now =new Date();

            //str1 = dt.toString("yyyy_MM_dd_HH_mm_ss_") + dt.Millisecond + " " + str;
//            str1 = sdf.format(now) + " " + str;

            /*
            if (!tag.contains("r")) {
                Log.e(tag, str1);
            } else {
                if (printRandomLog) {
                    Log.e(tag, str1);
                }
            }
            */

            //
//            Log.e(tag,str1);

            //
            return 0;
        };

        public static float XMVectorGetX(com.qycx.base.Vector4 V) {
            return V.X;
        }

        public static float XMVectorGetY(com.qycx.base.Vector4 V) {
            return V.Y;
        }

        public static float XMVectorGetZ(com.qycx.base.Vector4 V) {
            return V.Z;
        }


        //
        public static int trailDir(String dir, int size) {
            int len;

            if (dir == null) return -1;
            //tTrim(dir);
            String str = new String(dir);
            str.trim();
            safeTcsnCpy(str, dir, size);

            len = (int) tmpFunc.qyFuncs.mylstrlen(dir);
            //
            if (len >= 1 && size >= len + 1) {
                if (dir.getBytes()[len - 1] == '/') return 0;
            }
            //
            if (len + 2 > size) return -1;
            dir.getBytes()[len] = '/';
            dir.getBytes()[len + 1] = (char) 0;
            return 0;
        }

        //
        public static long getFileLen(String fullFilePath) {
            long len = 0;

            if (fullFilePath == null) return 0;

            try {
                File file = new File(fullFilePath);
                len =  file.length();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return len;
        }

        public static int getDirAndFinalName(String svFileName, String dirName, String finalName) {
            //TCHAR pFinalModuleName = NULL;

            if (null == svFileName || 0 == (svFileName.length())) return -1;

            int pos = svFileName.lastIndexOf("/");
            if (pos < 0) return -1;
            pos++;
            String str0 = svFileName.substring(0, pos);
            String str1 = svFileName.substring(pos);
            if (str0 == null || str1 == null) return -1;
            dirName = str0;
            finalName = str1;


            return 0;
        }


    }
}

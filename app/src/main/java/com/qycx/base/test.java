package com.qycx.base;




import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import com.qycx.base.util.Log;
import com.qycx.tmpjavalibrary.TmpJniUtil;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Debug;
import android.os.Environment;

import com.qycx.MainActivity;
import com.qycx.R;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class test {

    //
    public static String tag_100k  =  "100k";

    //
    public class testCls_Droid {


        //
        static qySyncCommProc.CMutexRW mtxRw = new qySyncCommProc.CMutexRW();

        static class Foo {
            public int A;

            public String B;
        }

        //
        static class TMP_tBuf5 {
            public String tBuf;//=new byte[5];
        }

        //
        public static int testBreak()
        {

            return  0;
        }

        public static int testChk(String hint)
        {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;

            ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();

            //
            return  0;

            /*
            //
            int index  =  1;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo  = pProcInfo.getQmcTaskInfoByIndex(null,index,"");
            if  (  pTaskInfo.bUsed)  {
                qmcTaskInfo.QMC_taskData_common pTaskData  =  pTaskInfo.getTaskData();
                if  (  pTaskData.uiType  ==  CONST_taskDataType_conf )  {
                    qmcTaskInfo.QMC_taskData_conf  pTc  =  (  qmcTaskInfo.QMC_taskData_conf) pTaskData;
                    //
                    qnmCommProc_mis.MIS_MSG_TASK  msgTask  =  (qnmCommProc_mis.MIS_MSG_TASK) pTc.msgU;
                    //
                    if  (  msgTask.data.route.idInfo_from.ui64Id  <  1000)  {
                        Log.d("qy","kkkk");
                    }
                    else  {
                          Log.d("qy","testChk ok."  + hint );
                    }


                }
            }



            return  0;

             */
        }


        //
        public static int testPrintTasks(boolean bShow,  String tag,  String hint)
        {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
            String str;

            int index_taskInfo;
            qmcTaskInfo.QMC_TASK_INFO  pTaskInfo;
            for (index_taskInfo = 0; index_taskInfo < pProcInfo.cfg.usMaxCnt_taskInfos; index_taskInfo++) {
                pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(null, index_taskInfo, "procMsgInput_mc_media");
                if (!pTaskInfo.bUsed) continue;
                //
                str  =  hint  +  ": haha: task " + index_taskInfo  +  " active. "  +  "processQ_media.nQNodes "  +  pProcInfo.processQ_media.m_var.uiQNodes;
                Log.d("qy",str);
                if  ( bShow)  {
                    showInfo_open.qyFuncs.showInfo_open(0,0,tag,str);
                }

                //
                qmcTaskInfo.QMC_taskData_common  pTaskData = pTaskInfo.getTaskData();
                if (pTaskData.uiType != CONST_taskDataType_conf) continue;;
                qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();
                //
                int index;
                for (index = 0; index < videoConference.usCntLimit_activeMems_from; index++) {
                    qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[index];
                    //
                    str =  "act"  +  index  +  ":"  +  pActiveMem_from.avStream.idInfo.ui64Id  +  ","  +  pActiveMem_from.avStream.obj.tranInfo.video.uiTranNo_openAvDev;
                    Log.d("qy",str);
                    if  (  bShow)  {
                        showInfo_open.qyFuncs.showInfo_open(0,0,tag,str);
                    }

                }


            }

            return  0;

        }



        //
        public static int testInShow()
        {
            //
            if  (  false  ) {

                ReentrantLock lock = new ReentrantLock(true);
                boolean bRet;
                bRet  =  lock.tryLock();
                lock.unlock();
                lock.lock();
                try {
                    lock.unlock();

                }
                catch ( Exception e)
                {
                    e.printStackTrace();;
                }

                Log.e("qy","testInShow is on");

                //
                ByteBuffer bb = ByteBuffer.allocate(100);

                //
                byte b=1;
                bb.put(b);

                byte[] b1=new byte[10];

                bb.put(b1,2,5);




                //
                Log.d("qy","test");


            }

            return  0;
        }


        //
        public static int test1() {

            //
            if (false) {

                //
                ByteBuffer buffer = ByteBuffer.allocate(10);




            }

            //
            return 0;
        }


        //
        public static guiShare.PF_testInMain testInMain = (ctxQyMc.CCtxQyMc pQyMc) -> {
            if (pQyMc == null) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo == null) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;


            String str;


            //
            test1();


//            str = "";
//            int len = str.length();
//
//            len = sizeof(QY_SHOW_SMALL);
//
//            //
//            DateTime dt;
//            //dt.AddSeconds
//
//            //
//            TMP_tBuf5 tt;
//            str = "1234567890";
//            tt.tBuf, str, 5);
//            String sss = new String(tt.tBuf);

            //
            //qyFuncs_Droid.copyToClipboard("我在测试剪贴板");
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            //
            CameraManager cameraManager = (CameraManager) mainActivity.getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIdList = new String[0];
            try {
                cameraIdList = cameraManager.getCameraIdList();
            } catch (CameraAccessException e) {
                e.printStackTrace();
                return -1;
            }
            if (cameraIdList.length > 0) {
                //cameraIdList.length 是否有摄像头和摄像头个数

            }

            List<Object> persons = new ArrayList<>();

            persons.add("11");
            persons.add("22");
//            persons.add(foo);
//
//            int ii = persons.Count;
//
//            persons.Remove("11");
//            persons.Remove(foo);
//
//            ii = persons.Count;


            //
            int i;

//
//            Object ooo =  null;
//            long p1 =  null;
//            long p2 =  0;
//            if (p1 == 0) {
//                ii = 0;
//            }
//            if (p2 == 0) {
//                ii = 9;
//            }
            int sVal = -1;
            int iVal = (int) sVal;

            //
            //str= Com.Qycx.Tmpffmpeglibrary.TmpFfmpeg.AvCodecInfo();

            //
            //Object obj = this;
            MainActivity wnd = (MainActivity) pQyMc.gui.hMainWnd;

            //
            testTime();


            //
            //testRwLock();


            //
            byte[] byteArrayIn1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
            ByteBuffer buffer = null;//ByteBuffer.wrap(byteArrayIn1);
            byte[] vv = new byte[10];

            buffer.get(vv, 2, 3);

            //
            File path;

            ActivityManager activityManager = (ActivityManager) wnd.getSystemService(Context.ACTIVITY_SERVICE);
            iVal = activityManager.getLargeMemoryClass();
            int jVal = activityManager.getMemoryClass();


            long lVal0 = Debug.getNativeHeapAllocatedSize();

            //
            /* */

            Bitmap bitmap = BitmapFactory.decodeResource(wnd.getResources(), R.drawable.bj);

            int w = bitmap.getWidth(); //80;
            int h = bitmap.getHeight();  // 60;


            int yuvSize = w * h * 3 / 2;
            byte[] yuv = new byte[yuvSize];

            int rgb32Size = w * h * 4;
            byte[] rgb32_org = new byte[rgb32Size];
            byte[] rgb32 = new byte[rgb32Size];


            //
            int bmpBufSize = bitmap.getRowBytes() * bitmap.getHeight();

            //
            ByteBuffer bb_rgb32 = ByteBuffer.allocateDirect(bmpBufSize);
            i = bitmap.getByteCount();
            bitmap.copyPixelsToBuffer(bb_rgb32);
            bb_rgb32.rewind();
            System.arraycopy(bb_rgb32.array(), 0, rgb32_org, 0, rgb32Size);

            //long lVal = JavaSystem.NanoTime;
            //Log.d(TAG,"before rgb32To24");
            long lVal1 = System.currentTimeMillis();


            //long lNano2=JavaSystem.NanoTime；
            long lVal2 = System.currentTimeMillis();

            int iDiffInMs = (int) (lVal2 - lVal1);
            str = String.format("nIntervalInMs of rgb32To24 %sms", iDiffInMs);


            ByteBuffer bb_yuv = ByteBuffer.allocateDirect(yuvSize);
            ByteBuffer bb_yuv_rotate = ByteBuffer.allocateDirect(yuvSize);

            TmpJniUtil.rgbToI420(bb_rgb32, 32, true, bb_yuv, w, h, false);
            //
            TmpJniUtil.rotateI420(bb_yuv, bb_yuv_rotate, w, h, 90, true);
            //
            byte[] pYuv = bb_yuv.array();
            byte[] pYuv_rotate = bb_yuv_rotate.array();
            lVal1 = Debug.getNativeHeapAllocatedSize();
            ByteBuffer kk = ByteBuffer.allocateDirect(10000000);
            lVal2 = Debug.getNativeHeapAllocatedSize();
            long lDiff = lVal2 - lVal1;

            long lVal = Debug.getNativeHeapAllocatedSize();


            int iii = 0;

            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            //
            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM);
            path = new File(path.getAbsolutePath() + "/Camera");
            //
            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);

            //
            qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
            idInfo.ui64Id = 10000;
            Object hWnd = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, idInfo);

            return 0;
        };

        public static int testTime() {
            String str;

            //
//            SimpleDateFormat alldate = new SimpleDateFormat("yyyyMMdd_HHmmss");
//            Java.Util.Date date = new Java.Util.Date();
//            str = alldate.format(date);
//            TimeZoneInfo tzi = TimeZoneInfo.Utc;
//            DateTime dt = new DateTime(date.Time);
//
//            long l = JavaSystem.CurrentTimeMillis();
//            DateTime dt0 = new DateTime(1970, 1, 1);
//            DateTime dt1 = dt0.AddMilliseconds(l);
//
//            dt = DateTime.Now;
//            String str2 = dt.ToLocalTime().toString("yyyyMMdd_HHmmss");
//            String str3 = dt.ToUniversalTime().toString("yyyyMMdd_HHmmss");
//
//
//            //
//            Java.Util.Date d1 = new Java.Util.Date(l);
//            str = alldate.format(d1);

            return 0;
        }


        //
        public static int testRwLock() {
            threadProc_testR[] trs = new threadProc_testR[10];
            threadProc_testW[] tws = new threadProc_testW[1];

            int i;
            for (i = 0; i < trs.length; i++) {
                trs[i] = new threadProc_testR();
                threadProc_testR pR = (threadProc_testR) trs[i];
                pR.m_id = i;
                trs[i].start();
            }
            for (i = 0; i < tws.length; i++) {
                tws[i] = new threadProc_testW();
                threadProc_testW pW = (threadProc_testW) tws[i];
                pW.m_id = i;
                tws[i].start();
            }

            //
            for (i=0; i<100;i++ ) {
                //qyFuncs.mySleep(5000);
                tmpFunc.qyFuncs.mySleep(1000);
            }

            return 0;

        }


        //
        public static class threadProc_testR extends Thread {
            public int m_id;


            @Override
            public void run() {
                int id = (int) m_id;
                String str;
                int i;

                //
                //qyFuncs.mySleep(1000);

                //
                for (i = 0; ; i++) {

                    //
                    try (qySyncCommProc.CQyRWLock rLock = new qySyncCommProc.CQyRWLock()) {

                        //
                        //rLock.m_bDbg=true;

                        //
                        Log.d("tn_msgInput","thread_r"  +  id  +  " before rlock");

                        //
                        if (rLock.rLock(testCls_Droid.mtxRw, 1000, "testR.370") != 0) continue;

                        str = String.format("rlock %s ok", id);
                        Log.d("tn_msgInput",str);

                        tmpFunc.qyFuncs.mySleep(3000);

                    } catch (Exception e) {
                        Log.e(e.getMessage());
                        return;
                    }


                    Log.d("tn_msgInput","r"  +  id  +  " rLock end");

                    tmpFunc.qyFuncs.mySleep(100);

                    //
                    continue;
                }

            }
        }

        //
        public static class threadProc_testW extends Thread {
            public int m_id;


            @Override
            public void run() {
                int id = (int) m_id;
                String str;
                int i;

                for (i = 0; ; i++) {

                    //
                    try (qySyncCommProc.CQyRWLock rwLock = new qySyncCommProc.CQyRWLock()) {
                        String w_hint = "threadProc_testW";

                        Log.d("tn_msgInput","thread_w"  +  m_id  +  "  before wLock");
                        if (rwLock.wLock(mtxRw, 1000, w_hint) != 0) {
                            Log.d("tn_msgInput","thread_w"  +  id  +  "wLock failed");
                            continue;
                        }
                        Log.d("tn_msgInput","thread_w"  +  id  +  "wLock ok");

                        //
                        tmpFunc.qyFuncs.mySleep(3000);

                    } catch (Exception e) {
                        Log.e(e.getMessage());
                        return;
                    }

                    str = String.format("wlock %s ends", id);
                    Log.d("tn_msgInput",str);

                    //
                    tmpFunc.qyFuncs.mySleep(100);
                    continue;
                }

            }

        }


    }
}

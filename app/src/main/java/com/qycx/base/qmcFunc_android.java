package com.qycx.base;

import static com.qycx.base.taskDataFunc_android.qyFuncs_Droid.freeMyThread;
import static com.qycx.base.taskDataFunc_android.qyFuncs_Droid.getMyThread;
import static com.qycx.base.taskDataFunc_android.qyFuncs_Droid.newMyThread;

public class qmcFunc_android
{
    public static class qyFuncs_Droid
    {

        public static  int stopQThread_android(qnmCommProc_mis.QY_qThreadProcInfo_common pThread, String hint)
        {
            //TCHAR tBuf[256];

            if (null == hint) hint = "";

            if (taskDataFunc_android.qyFuncs_Droid.bIndexValid_myThread(pThread.ind_hThread))
            {
                MyThread mt = getMyThread(pThread.ind_hThread);
                if (mt != null)
                {
                    pThread.bQuit = true;
                    tmpFunc.qyFuncs.myEventSet(pThread.q2.hEvent0);
                    try
                    {
                        mt.stop();
                    }
                    catch (Exception e)
                    {

                    }
                }
                pThread.ind_hThread = freeMyThread( pThread.ind_hThread);
            }

            return 0;
        }

        //
        public static int startQThread_android(qmcFunc.PF_threadProc lpStartAddress, Object lpParameter, Object pThreadClass, qnmCommProc_mis.QY_qThreadProcInfo_common pQThread)
        {
            int iErr = -1;
            int dwThreadDaemonId = 0;

            //
            if (null != lpStartAddress)
            {

                int index_o = -1;
                if (-1 == (index_o = newMyThread(null, lpStartAddress, lpParameter,  index_o))) return iErr;
                pQThread.ind_hThread = index_o;
                MyThread mt = getMyThread(pQThread.ind_hThread);
                if (mt == null) return iErr;
                mt.start();
            }

            iErr = 0;

        

            return iErr;
        }

        public static int doGC(String hint)
        {
            return 0;
        }


    }
}
